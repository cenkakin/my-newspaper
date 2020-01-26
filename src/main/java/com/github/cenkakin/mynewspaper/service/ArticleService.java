package com.github.cenkakin.mynewspaper.service;

import com.github.cenkakin.mynewspaper.domain.Article;
import com.github.cenkakin.mynewspaper.domain.QArticle;
import com.github.cenkakin.mynewspaper.exception.ArticleNotFoundException;
import com.github.cenkakin.mynewspaper.exception.OutdatedUpdateArticleException;
import com.github.cenkakin.mynewspaper.repository.ArticleRepository;
import com.github.cenkakin.mynewspaper.request.CreateArticleRequest;
import com.github.cenkakin.mynewspaper.request.SearchArticleRequest;
import com.github.cenkakin.mynewspaper.request.UpdateArticleRequest;
import com.github.cenkakin.mynewspaper.util.OptionalBooleanExpressionBuilder;
import com.querydsl.core.types.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by cenkakin
 */
@Slf4j
@AllArgsConstructor
public class ArticleService {

  private final ArticleRepository articleRepository;

  public Mono<Article> createArticle(CreateArticleRequest createArticleRequest) {
    return articleRepository.insert(Article.fromCreateArticleRequest(createArticleRequest))
        .doOnNext(article -> log.info("Article with id: {} is created", article.getId()));
  }

  public Mono<Article> getArticle(String id) {
    return articleRepository.findByIdAndDeletedFalse(id)
        .switchIfEmpty(Mono.error(new ArticleNotFoundException(id)));
  }

  public Mono<Article> updateArticle(String id, UpdateArticleRequest updateArticleRequest) {
    return getArticle(id)
        .flatMap(articleInDb -> {
          if (articleInDb.getVersion() >= updateArticleRequest.getVersion()) {
            return Mono.error(new OutdatedUpdateArticleException(articleInDb.getVersion()));
          }
          Article toBeUpdated = articleInDb.update(updateArticleRequest);
          return articleRepository.save(toBeUpdated);
        })
        .doOnNext(article ->
            log.info("Article with id: {} is updated to version {}", article.getId(), article.getVersion()));
  }

  public Mono<Void> deleteArticle(String id) {
    return getArticle(id)
        .map(Article::delete)
        .flatMap(articleRepository::save)
        .doOnNext(article -> log.info("Article with id: {} is deleted", article.getId()))
        .then();
  }

  public Flux<Article> getArticles() {
    return articleRepository.findAllByDeletedFalse();
  }

  public Flux<Article> searchArticles(SearchArticleRequest request) {
    Predicate searchQuery = createSearchQuery(request);
    return articleRepository.findAll(searchQuery);
  }

  private Predicate createSearchQuery(SearchArticleRequest request) {
    QArticle articleQuery = QArticle.article;
    return new OptionalBooleanExpressionBuilder(articleQuery.deleted.isFalse())
        .notNullAnd(a -> articleQuery.authors.any().equalsIgnoreCase(a), request.getAuthor())
        .notNullAnd(k -> articleQuery.keywords.any().equalsIgnoreCase(k), request.getKeyword())
        .notNullAnd(fpd -> articleQuery.publishDate.after(fpd.minusDays(1)), request.getFromPublishDate())
        .notNullAnd(tpd -> articleQuery.publishDate.before(tpd.plusDays(1)), request.getToPublishDate())
        .build();
  }
}
