package com.github.cenkakin.mynewspaper.repository;

import com.github.cenkakin.mynewspaper.domain.Article;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.function.Supplier;

@AllArgsConstructor
public class ArticleRepository {

    private static final Supplier<Query> INITIALIZE_NOT_DELETED_ARTICLE_QUERY =
            () -> Query.query(Criteria.where("deleted").is(false));

    private static final Function<Integer, Query> INITIALIZE_ARTICLES_QUERY = (limit) -> {
        Query query = Query.query(Criteria.where("deleted").is(false));
        query.limit(limit);
        Sort publishDate = Sort.by(Sort.Direction.DESC, "id");
        query.with(publishDate);
        return query;
    };

    private final ReactiveMongoTemplate template;

    public Mono<Article> insert(Article article) {
        return template.insert(article);
    }

    public Mono<Article> findById(String id) {
        Query query = INITIALIZE_NOT_DELETED_ARTICLE_QUERY.get();
        query.addCriteria(Criteria.where("id").is(id));
        return template.findOne(query, Article.class);
    }

    public Mono<Article> update(Article article) {
        return template.save(article);
    }

    public Flux<Article> findAll(Integer limit, Integer offset) {
        Query query = INITIALIZE_ARTICLES_QUERY.apply(limit);
        query.skip(offset);
        return template.find(query, Article.class);
    }

    public Flux<Article> find(Integer limit, Integer offset) {
        return null;
    }
}