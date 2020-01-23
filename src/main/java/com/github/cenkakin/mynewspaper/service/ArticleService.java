package com.github.cenkakin.mynewspaper.service;

import com.github.cenkakin.mynewspaper.domain.Article;
import com.github.cenkakin.mynewspaper.exception.ArticleNotFoundException;
import com.github.cenkakin.mynewspaper.exception.OutdatedUpdateArticleException;
import com.github.cenkakin.mynewspaper.repository.ArticleRepository;
import com.github.cenkakin.mynewspaper.request.CreateArticleRequest;
import com.github.cenkakin.mynewspaper.request.SearchArticleRequest;
import com.github.cenkakin.mynewspaper.request.UpdateArticleRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Optional;

/**
 * Created by cenkakin
 */
@Slf4j
@AllArgsConstructor
public class ArticleService {

    private static final int MAX_LIMIT = 10;
    private static final int DEFAULT_OFFSET = 0;

    private final ArticleRepository articleRepository;

    public Mono<Article> createArticle(CreateArticleRequest createArticleRequest) {
        return articleRepository.insert(Article.fromUpsertArticleRequest(createArticleRequest));
    }

    public Mono<Article> getArticle(String id) {
        return articleRepository.findById(id)
                .switchIfEmpty(Mono.error(new ArticleNotFoundException(id)));
    }

    public Mono<Article> updateArticle(String id, @Valid UpdateArticleRequest updateArticleRequest) {
        return getArticle(id)
                .flatMap(articleInDb -> {
                    if (articleInDb.getVersion() >= updateArticleRequest.getVersion()) {
                        return Mono.error(new OutdatedUpdateArticleException(articleInDb.getVersion()));
                    }
                    Article toBeUpdated = articleInDb.update(updateArticleRequest);
                    return articleRepository.update(toBeUpdated);
                });

    }

    public Mono<Void> deleteArticle(String id) {
        return getArticle(id)
                .flatMap(article -> {
                    Article toBeDeleted = article.delete();
                    return articleRepository.update(toBeDeleted);
                }).then();
    }

    public Flux<Article> getArticles(Optional<Integer> optionalLimit, Optional<Integer> optionalOffset) {
        final Integer limit = optionalLimit.orElse(MAX_LIMIT);
        final Integer offset = optionalOffset.orElse(DEFAULT_OFFSET);
        return articleRepository.findAll(limit, offset);
    }

    public Flux<Article> searchArticles(SearchArticleRequest request, Optional<Integer> optionalLimit,
                                        Optional<Integer> optionalOffset) {
        final Integer limit = optionalLimit.orElse(MAX_LIMIT);
        final Integer offset = optionalOffset.orElse(DEFAULT_OFFSET);
        return articleRepository.findAll(limit, offset);
    }
}
