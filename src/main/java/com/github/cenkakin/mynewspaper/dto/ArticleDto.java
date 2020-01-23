package com.github.cenkakin.mynewspaper.dto;

import com.github.cenkakin.mynewspaper.domain.Article;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by cenkakin
 */
@Data
@Builder
public final class ArticleDto {

    private final String id;

    private final String header;

    private final String shortDescription;

    private final String text;

    private final LocalDate publishDate;

    private final List<String> authors;

    private final List<String> keywords;

    private final Long version;

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
