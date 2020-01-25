package com.github.cenkakin.mynewspaper.repository;

import com.github.cenkakin.mynewspaper.domain.Article;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by cenkakin
 */
public interface ArticleRepository extends ReactiveMongoRepository<Article, String>, ReactiveQuerydslPredicateExecutor<Article> {

  Mono<Article> findByIdAndDeletedFalse(String id);

  Flux<Article> findAllByDeletedFalse();
}
