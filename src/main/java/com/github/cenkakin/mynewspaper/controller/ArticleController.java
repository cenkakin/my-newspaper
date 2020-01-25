package com.github.cenkakin.mynewspaper.controller;

import com.github.cenkakin.mynewspaper.dto.ArticleDto;
import com.github.cenkakin.mynewspaper.exception.ArticleNotFoundException;
import com.github.cenkakin.mynewspaper.exception.BadRequestException;
import com.github.cenkakin.mynewspaper.exception.OutdatedUpdateArticleException;
import com.github.cenkakin.mynewspaper.request.CreateArticleRequest;
import com.github.cenkakin.mynewspaper.request.SearchArticleRequest;
import com.github.cenkakin.mynewspaper.request.UpdateArticleRequest;
import com.github.cenkakin.mynewspaper.service.ArticleService;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.ConversionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

/**
 * Created by cenkakin
 */
@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class ArticleController {

  private final ArticleService articleService;

  @GetMapping("articles/{id}")
  public Mono<ResponseEntity<ArticleDto>> getArticle(@PathVariable String id) {
    return articleService.getArticle(id)
        .map(ArticleDto::fromArticle)
        .map(aDto -> ResponseEntity.ok().body(aDto))
        .onErrorMap(ArticleNotFoundException.class, e -> new ResponseStatusException(HttpStatus.NOT_FOUND))
        .onErrorMap(OutdatedUpdateArticleException.class, e -> new BadRequestException(e.getMessage()));
  }

  @GetMapping("/articles")
  public Flux<ArticleDto> getArticles() {
    return articleService.getArticles()
        .map(ArticleDto::fromArticle);
  }

  @GetMapping("articles:search")
  public Flux<ArticleDto> searchArticles(@Valid SearchArticleRequest searchArticleRequest) {
    return articleService.searchArticles(searchArticleRequest)
        .map(ArticleDto::fromArticle);
  }

  @PostMapping("articles")
  public Mono<ResponseEntity<ArticleDto>> createArticle(@Valid @RequestBody CreateArticleRequest createArticleRequest) {
    return articleService.createArticle(createArticleRequest)
        .map(ArticleDto::fromArticle)
        .map(aDto -> ResponseEntity.created(URI.create("/api/articles/" + aDto.getId())).body(aDto));
  }

  @PutMapping("articles/{id}")
  public Mono<ResponseEntity<ArticleDto>> updateArticle(@PathVariable String id,
                                                        @Valid @RequestBody UpdateArticleRequest updateArticleRequest) {
    return articleService.updateArticle(id, updateArticleRequest)
        .map(ArticleDto::fromArticle)
        .map(aDto -> ResponseEntity.ok().body(aDto))
        .onErrorMap(ArticleNotFoundException.class, e -> new ResponseStatusException(HttpStatus.NOT_FOUND))
        .onErrorMap(OutdatedUpdateArticleException.class, e -> new BadRequestException(e.getMessage()));
  }

  @DeleteMapping("articles/{id}")
  public Mono<ResponseEntity<Object>> deleteArticle(@PathVariable String id) {
    return articleService.deleteArticle(id)
        .map(aDto -> ResponseEntity.ok().build())
        .onErrorMap(ArticleNotFoundException.class, e -> new ResponseStatusException(HttpStatus.NOT_FOUND))
        .onErrorMap(OutdatedUpdateArticleException.class, e -> new BadRequestException(e.getMessage()));
  }

  @ExceptionHandler
  public ResponseEntity<String> handle(ConversionException ex) {
    return ResponseEntity.badRequest().body(ex.getCause().getMessage());
  }
}
