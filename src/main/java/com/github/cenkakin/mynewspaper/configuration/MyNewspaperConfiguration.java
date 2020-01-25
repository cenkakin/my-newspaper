package com.github.cenkakin.mynewspaper.configuration;

import com.github.cenkakin.mynewspaper.repository.ArticleRepository;
import com.github.cenkakin.mynewspaper.service.ArticleService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by cenkakin
 */
@Configuration
public class MyNewspaperConfiguration {

  @Bean
  public ArticleService articleService(ArticleRepository articleRepository) {
    return new ArticleService(articleRepository);
  }
}
