package com.github.cenkakin.mynewspaper.request;

import lombok.Value;

import java.time.LocalDate;

/**
 * Created by cenkakin
 */
@Value
public class SearchArticleRequest {

  private final String keyword;

  private final String author;

  private final LocalDate fromPublishDate;

  private final LocalDate toPublishDate;
}
