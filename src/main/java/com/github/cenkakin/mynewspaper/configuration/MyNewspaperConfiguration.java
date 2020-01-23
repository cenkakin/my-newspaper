package com.github.cenkakin.mynewspaper.configuration;

import com.github.cenkakin.mynewspaper.repository.ArticleRepository;
import com.github.cenkakin.mynewspaper.service.ArticleService;
import com.mongodb.reactivestreams.client.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

/**
 * Created by cenkakin
 */
@Configuration
public class MyNewspaperConfiguration {

    @Bean
    public ArticleRepository articleRepository(MongoClient mongoClient) {
        var template = new ReactiveMongoTemplate(mongoClient, "article");
        return new ArticleRepository(template);
    }

    @Bean
    public ArticleService articleService(ArticleRepository articleRepository) {
        return new ArticleService(articleRepository);
    }
}
