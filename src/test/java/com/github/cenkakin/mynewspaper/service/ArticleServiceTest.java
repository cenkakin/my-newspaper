package com.github.cenkakin.mynewspaper.service;

import com.github.cenkakin.mynewspaper.domain.Article;
import com.github.cenkakin.mynewspaper.exception.ArticleNotFoundException;
import com.github.cenkakin.mynewspaper.exception.OutdatedUpdateArticleException;
import com.github.cenkakin.mynewspaper.repository.ArticleRepository;
import com.github.cenkakin.mynewspaper.request.CreateArticleRequest;
import com.github.cenkakin.mynewspaper.request.SearchArticleRequest;
import com.github.cenkakin.mynewspaper.request.UpdateArticleRequest;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ArticleServiceTest {

  private final ArticleRepository articleRepository = mock(ArticleRepository.class);

  private final ArticleService articleService = new ArticleService(articleRepository);

  @Test
  void shouldCreateArticleFromCreateArticleRequest() {
    //given
    CreateArticleRequest request = new CreateArticleRequest("Corona Virus!",
        "Is it a worldwide threat?",
        "We should be careful...",
        LocalDate.parse("2020-01-01"),
        Set.of("Cenk Akin"),
        Set.of("health")
    );

    Article mockArticle = Article.fromCreateArticleRequest(request);
    String randomId = UUID.randomUUID().toString();
    mockArticle.setId(randomId);

    when(articleRepository.insert(Article.fromCreateArticleRequest(request)))
        .thenReturn(Mono.just(mockArticle));

    //when
    Mono<Article> articleCreation = articleService.createArticle(request);

    //then
    StepVerifier.create(articleCreation)
        .expectNext(mockArticle)
        .expectComplete()
        .verify();
  }

  @Test
  void shouldThrowNotFoundExceptionWhenArticleWithIdNotExists() {
    //given
    String givenId = "1234";

    when(articleRepository.findByIdAndDeletedFalse(givenId))
        .thenReturn(Mono.empty());

    //when
    Mono<Article> articleRetrieve = articleService.getArticle(givenId);

    //then
    StepVerifier.create(articleRetrieve)
        .expectErrorMatches(ex -> ex instanceof ArticleNotFoundException &&
            ex.getMessage().equals("Article id is not found! id: 1234"))
        .verify();
  }

  @Test
  void shouldReturnArticleWithId() {
    //given
    String givenId = "1234";
    Article mockArticleInDb = new Article();
    mockArticleInDb.setId("1234");

    when(articleRepository.findByIdAndDeletedFalse(givenId))
        .thenReturn(Mono.just(mockArticleInDb));

    //when
    Mono<Article> articleRetrieve = articleService.getArticle(givenId);

    //then
    StepVerifier.create(articleRetrieve)
        .expectNext(mockArticleInDb)
        .expectComplete()
        .verify();
  }

  @Test
  void shouldThrowNotFoundExceptionWhenUpdatingArticleWithIdNotExists() {
    //given
    String givenId = "1234";

    UpdateArticleRequest request = new UpdateArticleRequest("Corona Virus!",
        "Is it a worldwide threat?",
        "We should be careful...",
        LocalDate.parse("2020-01-01"),
        Set.of("Cenk Akin"),
        Set.of("health"),
        1L
    );

    when(articleRepository.findByIdAndDeletedFalse(givenId))
        .thenReturn(Mono.empty());

    //when
    Mono<Article> articleUpdate = articleService.updateArticle("1234", request);

    //then
    StepVerifier.create(articleUpdate)
        .expectErrorMatches(ex -> ex instanceof ArticleNotFoundException &&
            ex.getMessage().equals("Article id is not found! id: 1234"))
        .verify();
  }

  @Test
  void shouldThrowOutdatedUpdateArticleExceptionWhenArticleInDbHasEqualVersionNumber() {
    //given
    String givenId = "1234";

    Article mockArticleInDb = new Article();
    mockArticleInDb.setId("1234");
    mockArticleInDb.setVersion(1L);

    UpdateArticleRequest request = new UpdateArticleRequest("Corona Virus!",
        "Is it a worldwide threat?",
        "We should be careful...",
        LocalDate.parse("2020-01-01"),
        Set.of("Cenk Akin"),
        Set.of("health"),
        1L
    );

    when(articleRepository.findByIdAndDeletedFalse(givenId))
        .thenReturn(Mono.just(mockArticleInDb));

    //when
    Mono<Article> articleUpdate = articleService.updateArticle("1234", request);

    //then
    StepVerifier.create(articleUpdate)
        .expectErrorMatches(ex -> ex instanceof OutdatedUpdateArticleException &&
            ex.getMessage().equals("You need to increase version number or article has already been changed! Current version: 1"))
        .verify();
  }

  @Test
  void shouldThrowOutdatedUpdateArticleExceptionWhenArticleInDbHasBiggerVersionNumber() {
    //given
    String givenId = "1234";

    Article mockArticleInDb = new Article();
    mockArticleInDb.setId("1234");
    mockArticleInDb.setVersion(2L);

    UpdateArticleRequest request = new UpdateArticleRequest("Corona Virus!",
        "Is it a worldwide threat?",
        "We should be careful...",
        LocalDate.parse("2020-01-01"),
        Set.of("Cenk Akin"),
        Set.of("health"),
        1L
    );

    when(articleRepository.findByIdAndDeletedFalse(givenId))
        .thenReturn(Mono.just(mockArticleInDb));

    //when
    Mono<Article> articleUpdate = articleService.updateArticle("1234", request);

    //then
    StepVerifier.create(articleUpdate)
        .expectErrorMatches(ex -> ex instanceof OutdatedUpdateArticleException &&
            ex.getMessage().equals("You need to increase version number or article has already been changed! Current version: 2"))
        .verify();
  }

  @Test
  void shouldUpdateArticleSuccessfullyWhenArticleExistAndHasSmallerVersion() {
    //given
    String givenId = "1234";

    Article mockArticleInDb = new Article();
    mockArticleInDb.setId("1234");
    mockArticleInDb.setVersion(1L);

    UpdateArticleRequest request = new UpdateArticleRequest("Corona Virus!",
        "Is it a worldwide threat?",
        "We should be careful...",
        LocalDate.parse("2020-01-01"),
        Set.of("Cenk Akin"),
        Set.of("health"),
        2L
    );

    when(articleRepository.findByIdAndDeletedFalse(givenId))
        .thenReturn(Mono.just(mockArticleInDb));

    Article updatedArticle = mockArticleInDb.update(request);
    when(articleRepository.save(updatedArticle))
        .thenReturn(Mono.just(updatedArticle));

    //when
    Mono<Article> articleUpdate = articleService.updateArticle("1234", request);

    //then
    StepVerifier.create(articleUpdate)
        .expectNext(updatedArticle)
        .expectComplete()
        .verify();
  }

  @Test
  void shouldThrowNotFoundExceptionWhenDeletingArticleWithIdNotExists() {
    //given
    String givenId = "1234";

    when(articleRepository.findByIdAndDeletedFalse(givenId))
        .thenReturn(Mono.empty());

    //when
    Mono<Void> articleDeletion = articleService.deleteArticle(givenId);

    //then
    StepVerifier.create(articleDeletion)
        .expectErrorMatches(ex -> ex instanceof ArticleNotFoundException &&
            ex.getMessage().equals("Article id is not found! id: 1234"))
        .verify();
  }

  @Test
  void shouldNotThrowAnyExceptionWhenDeletingArticleIsSuccessful() {
    //given
    String givenId = "1234";
    Article mockArticleInDb = new Article();
    mockArticleInDb.setId("1234");

    when(articleRepository.findByIdAndDeletedFalse(givenId))
        .thenReturn(Mono.just(mockArticleInDb));

    when(articleRepository.save(mockArticleInDb.delete()))
        .thenReturn(Mono.just(mockArticleInDb.delete()));

    //when
    Mono<Void> articleDeletion = articleService.deleteArticle(givenId);

    //then
    StepVerifier.create(articleDeletion)
        .expectComplete()
        .verify();
  }

  @Test
  void shouldCreateCorrectQueryWithAllNull() {
    //given
    SearchArticleRequest request = new SearchArticleRequest(null, null, null, null);
    ArgumentCaptor<Predicate> predicateArgumentCaptor = ArgumentCaptor.forClass(Predicate.class);

    //when
    articleService.searchArticles(request);
    verify(articleRepository).findAll(predicateArgumentCaptor.capture());
    Predicate query = predicateArgumentCaptor.getValue();

    //then
    assertEquals("article.deleted = false",
        query.toString());
  }


  @Test
  void shouldCreateCorrectQueryWithOnlyKeywordAndAuthor() {
    //given
    SearchArticleRequest request = new SearchArticleRequest("health", "Pulitzer", null, null);
    ArgumentCaptor<Predicate> predicateArgumentCaptor = ArgumentCaptor.forClass(Predicate.class);

    //when
    articleService.searchArticles(request);
    verify(articleRepository).findAll(predicateArgumentCaptor.capture());
    Predicate query = predicateArgumentCaptor.getValue();

    //then
    assertEquals("article.deleted = false && eqIc(any(article.authors),Pulitzer) && eqIc(any(article.keywords),health)",
        query.toString());
  }

  @Test
  void shouldCreateCorrectQueryWithOnlyKeywordAndAuthorAndFromPublishDate() {
    //given
    SearchArticleRequest request = new SearchArticleRequest("health", "Pulitzer", LocalDate.parse("2019-01-01"), null);
    ArgumentCaptor<Predicate> predicateArgumentCaptor = ArgumentCaptor.forClass(Predicate.class);

    //when
    articleService.searchArticles(request);
    verify(articleRepository).findAll(predicateArgumentCaptor.capture());
    Predicate query = predicateArgumentCaptor.getValue();

    //then
    assertEquals("article.deleted = false && eqIc(any(article.authors),Pulitzer) && eqIc(any(article.keywords),health) && article.publishDate > 2018-12-31",
        query.toString());
  }

  @Test
  void shouldCreateCorrectQueryWithOnlyAuthorAndFromPublishDateAndToPublishDate() {
    //given
    SearchArticleRequest request = new SearchArticleRequest(null, "Pulitzer", LocalDate.parse("2019-01-01"), null);
    ArgumentCaptor<Predicate> predicateArgumentCaptor = ArgumentCaptor.forClass(Predicate.class);

    //when
    articleService.searchArticles(request);
    verify(articleRepository).findAll(predicateArgumentCaptor.capture());
    Predicate query = predicateArgumentCaptor.getValue();

    //then
    assertEquals("article.deleted = false && eqIc(any(article.authors),Pulitzer) && article.publishDate > 2018-12-31",
        query.toString());
  }

  @Test
  void shouldCreateCorrectQueryWithAllFields() {
    //given
    SearchArticleRequest request = new SearchArticleRequest("health", "Pulitzer", LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-25"));
    ArgumentCaptor<Predicate> predicateArgumentCaptor = ArgumentCaptor.forClass(Predicate.class);

    //when
    articleService.searchArticles(request);
    verify(articleRepository).findAll(predicateArgumentCaptor.capture());
    Predicate query = predicateArgumentCaptor.getValue();

    //then
    assertEquals("article.deleted = false && eqIc(any(article.authors),Pulitzer) && eqIc(any(article.keywords),health) " +
            "&& article.publishDate > 2018-12-31 && article.publishDate < 2020-01-26",
        query.toString());
  }

}