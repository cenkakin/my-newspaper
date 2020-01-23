package com.github.cenkakin.mynewspaper.domain;

import com.github.cenkakin.mynewspaper.request.CreateArticleRequest;
import com.github.cenkakin.mynewspaper.request.UpdateArticleRequest;
import io.github.classgraph.json.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by cenkakin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Article {

    @Id
    private String id;

    @NotBlank
    @Size(max = 250)
    private String header;

    private Boolean deleted;

    @NotBlank
    private String shortDescription;

    @NotBlank
    private String text;

    @NotNull
    private LocalDate publishDate;

    @NotEmpty
    private List<String> authors;

    private List<String> keywords;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant lastModifiedAt;

    @Version
    private Long version;

    private Article(String header, LocalDate publishDate, String shortDescription, String text, List<String> authors, List<String> keywords) {
        this.header = header;
        this.publishDate = publishDate;
        this.shortDescription = shortDescription;
        this.text = text;
        this.authors = authors;
        this.keywords = keywords;
        this.deleted = false;
    }

    private static Function<Set<String>, List<String>> SORT_AND_CAPITALIZE = set -> set.stream().map(String::toUpperCase).sorted().collect(Collectors.toList());

    public Article update(UpdateArticleRequest request) {
        this.header = request.getHeader();
        this.publishDate = request.getPublishDate();
        this.text = request.getText();
        this.shortDescription = request.getShortDescription();
        final List<String> sortedAndCapitalizedAuthors = SORT_AND_CAPITALIZE.apply(request.getAuthors());
        final List<String> sortedAndCapitalizedKeywords = SORT_AND_CAPITALIZE.apply(request.getKeywords());
        this.authors = sortedAndCapitalizedAuthors;
        this.keywords = sortedAndCapitalizedKeywords;
        return this;
    }

    public Article delete() {
        this.deleted = true;
        return this;
    }

    public static Article fromUpsertArticleRequest(CreateArticleRequest request) {
        final List<String> sortedAndCapitalizedAuthors = SORT_AND_CAPITALIZE.apply(request.getAuthors());
        final List<String> sortedAndCapitalizedKeywords = SORT_AND_CAPITALIZE.apply(request.getKeywords());
        return new Article(
                request.getHeader(),
                request.getPublishDate(),
                request.getShortDescription(),
                request.getText(),
                sortedAndCapitalizedAuthors,
                sortedAndCapitalizedKeywords);
    }
}
