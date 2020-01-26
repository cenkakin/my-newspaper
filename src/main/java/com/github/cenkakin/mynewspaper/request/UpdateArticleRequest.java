package com.github.cenkakin.mynewspaper.request;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

/**
 * Created by cenkakin
 */
@Value
public final class UpdateArticleRequest {

  @NotBlank
  @Size(max = 250)
  private final String header;

  @NotBlank
  private final String shortDescription;

  @NotBlank
  private final String text;

  @NotNull
  private final LocalDate publishDate;

  @NotEmpty
  private final Set<String> authors;

  private final Set<String> keywords;

  @NotNull
  private final Long version;
}
