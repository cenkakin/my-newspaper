package com.github.cenkakin.mynewspaper.repository;

import com.github.cenkakin.mynewspaper.domain.Article;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor;

/**
 * Created by cenkakin
 */
public interface ArticleRepository2 extends ReactiveMongoRepository<Article, String>, ReactiveQuerydslPredicateExecutor<Article> {

}
