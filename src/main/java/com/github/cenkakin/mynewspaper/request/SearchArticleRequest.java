package com.github.cenkakin.mynewspaper.request;

import lombok.Data;

import java.time.LocalDate;

/**
 * Created by cenkakin
 */
@Data
public class SearchArticleRequest {

    private final String keyword;

    private final String author;

    private final LocalDate fromPublishDate = LocalDate.now().minusDays(10);

    private final LocalDate toPublishDate = LocalDate.now();
}
