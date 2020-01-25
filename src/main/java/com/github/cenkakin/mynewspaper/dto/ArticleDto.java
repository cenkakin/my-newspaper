package com.github.cenkakin.mynewspaper.dto;

import com.github.cenkakin.mynewspaper.domain.Article;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by cenkakin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleDto {

  private String id;

  private String header;

  private String shortDescription;

  private String text;

  private LocalDate publishDate;

  private List<String> authors;

  private List<String> keywords;

  private Long version;

  public static ArticleDto fromArticle(Article article) {
    return new ArticleDtoBuilder()
        .id(article.getId())
        .header(article.getHeader())
        .shortDescription(article.getShortDescription())
        .text(article.getText())
        .publishDate(article.getPublishDate())
        .authors(article.getAuthors())
        .version(article.getVersion())
        .keywords(article.getKeywords()).build();
  }
}
