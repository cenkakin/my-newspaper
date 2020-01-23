package com.github.cenkakin.mynewspaper.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

/**
 * Created by cenkakin
 */
@Data
public final class CreateArticleRequest {

    @NotBlank
    @Size(max = 250)
    private String header;

    @NotBlank
    private String shortDescription;

    @NotBlank
    private String text;

    @NotNull
    private LocalDate publishDate;

    @NotEmpty
    private Set<String> authors;

    private Set<String> keywords;
}
