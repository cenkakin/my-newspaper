package com.github.cenkakin.mynewspaper.controller;

import com.github.cenkakin.mynewspaper.domain.Article;
import com.github.cenkakin.mynewspaper.dto.ArticleDto;
import com.github.cenkakin.mynewspaper.repository.ArticleRepository;
import com.github.cenkakin.mynewspaper.request.CreateArticleRequest;
import com.github.cenkakin.mynewspaper.request.UpdateArticleRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootTest
class ArticleControllerE2ETest {

  private WebTestClient webClient;

  @Autowired
  private ArticleRepository articleRepository;

  @BeforeEach
  void setUp(ApplicationContext context) {
    webClient = WebTestClient.bindToApplicationContext(context).build();
    articleRepository.deleteAll().block();
  }

  @Test
  void shouldReturn400WhenHeaderNotProvided() {
    CreateArticleRequest request = new CreateArticleRequest(null,
        "Is it a worldwide threat?",
        "We should be careful...",
        LocalDate.parse("2020-01-01"),
        Set.of("Cenk Akin", "Pulitzer"),
        Set.of("health")
    );

    webClient.post()
        .uri("/api/v1/articles")
        .body(BodyInserters.fromValue(request))
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody(String.class);
  }

  @Test
  void shouldReturn400WhenShortDescriptionNotProvided() {
    CreateArticleRequest request = new CreateArticleRequest("Corona Virus!",
        null,
        "We should be careful...",
        LocalDate.parse("2020-01-01"),
        Set.of("Cenk Akin", "Pulitzer"),
        Set.of("health")
    );

    webClient.post()
        .uri("/api/v1/articles")
        .body(BodyInserters.fromValue(request))
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody(String.class);
  }

  @Test
  void shouldReturn400WhenTextNotProvided() {
    CreateArticleRequest request = new CreateArticleRequest("Corona Virus!",
        "Is it a worldwide threat?",
        null,
        LocalDate.parse("2020-01-01"),
        Set.of("Cenk Akin", "Pulitzer"),
        Set.of("health")
    );

    webClient.post()
        .uri("/api/v1/articles")
        .body(BodyInserters.fromValue(request))
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody(String.class);
  }

  @Test
  void shouldReturn400WhenPublishDateNotProvided() {
    CreateArticleRequest request = new CreateArticleRequest("Corona Virus!",
        "Is it a worldwide threat?",
        "We should be careful...",
        null,
        Set.of("Cenk Akin", "Pulitzer"),
        Set.of("health")
    );

    webClient.post()
        .uri("/api/v1/articles")
        .body(BodyInserters.fromValue(request))
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody(String.class);
  }

  @Test
  void shouldReturn400WhenAuthorsNotProvided() {
    CreateArticleRequest request = new CreateArticleRequest("Corona Virus!",
        "Is it a worldwide threat?",
        "We should be careful...",
        LocalDate.parse("2020-01-01"),
        null,
        Set.of("health")
    );

    webClient.post()
        .uri("/api/v1/articles")
        .body(BodyInserters.fromValue(request))
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody(String.class);
  }

  @Test
  void shouldReturn200AndArticleWhenCreateNewArticle() {
    CreateArticleRequest request = new CreateArticleRequest("Corona Virus!",
        "Is it a worldwide threat?",
        "We should be careful...",
        LocalDate.parse("2020-01-01"),
        Set.of("Cenk Akin", "Pulitzer"),
        Set.of("health")
    );

    webClient.post()
        .uri("/api/v1/articles")
        .body(BodyInserters.fromValue(request))
        .exchange()
        .expectStatus()
        .isCreated()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectHeader()
        .exists("location")
        .expectBody(ArticleDto.class)
        .value(result -> {
          Assertions.assertEquals(result.getVersion(), 0L);
          Assertions.assertEquals(result.getHeader(), request.getHeader());
          Assertions.assertEquals(result.getPublishDate(), request.getPublishDate());
          Assertions.assertEquals(result.getShortDescription(), request.getShortDescription());
          Assertions.assertEquals(result.getText(), request.getText());
          Assertions.assertEquals(result.getKeywords(), List.of("Health"));
          Assertions.assertEquals(result.getAuthors(), List.of("Cenk Akin", "Pulitzer"));
          Assertions.assertNotNull(result.getId());
        });
  }

  @Test
  void shouldGetArticleWhenItExists() {
    //given
    CreateArticleRequest request = new CreateArticleRequest("Corona Virus!",
        "Is it a worldwide threat?",
        "We should be careful...",
        LocalDate.parse("2020-01-01"),
        Set.of("Cenk Akin", "Pulitzer"),
        Set.of("health")
    );
    Article entity = Article.fromCreateArticleRequest(request);
    String articleId = articleRepository.save(entity).block().getId();

    //when - then
    webClient.get()
        .uri("/api/v1/articles/" + articleId)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody(ArticleDto.class)
        .value(result -> {
          Assertions.assertEquals(result.getVersion(), 0L);
          Assertions.assertEquals(result.getHeader(), entity.getHeader());
          Assertions.assertEquals(result.getPublishDate(), entity.getPublishDate());
          Assertions.assertEquals(result.getShortDescription(), entity.getShortDescription());
          Assertions.assertEquals(result.getText(), entity.getText());
          Assertions.assertEquals(result.getKeywords(), List.of("Health"));
          Assertions.assertEquals(result.getAuthors(), List.of("Cenk Akin", "Pulitzer"));
          Assertions.assertNotNull(entity.getId());
        });
  }

  @Test
  void shouldReturnNotFoundWhenArticleIsAlreadyDeleted() {
    //given
    Article entity = generateArticleForTest();
    Article createdArticle = articleRepository.save(entity).block();
    String articleId = createdArticle.getId();
    articleRepository.save(createdArticle.delete()).block();

    //when - then
    webClient.get()
        .uri("/api/v1/articles/" + articleId)
        .exchange()
        .expectStatus()
        .isNotFound();
  }


  @Test
  void shouldReturnNotFoundWhenArticleDoesNotExist() {
    //given
    String articleId = "1234";

    //when - then
    webClient.get()
        .uri("/api/v1/articles/" + articleId)
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void shouldReturnNotFoundWhenUpdatingArticleDoesNotExist() {
    //given
    String articleId = "1234";
    UpdateArticleRequest updateRequest = new UpdateArticleRequest("Corona Virus!",
        "Updating",
        "Updating",
        LocalDate.parse("2020-01-01"),
        Set.of("Cenk Akin", "Pulitzer"),
        Set.of("health"),
        1L
    );

    //when - then
    webClient.put()
        .uri("/api/v1/articles/" + articleId)
        .body(BodyInserters.fromValue(updateRequest))
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void shouldReturnBadRequestWhenUpdatingArticleRequestOutdated() {
    //given
    Article entity = generateArticleForTest();
    Article createdArticle = articleRepository.save(entity).block();
    createdArticle.setHeader("First Update");
    String articleId = articleRepository.save(createdArticle).block().getId();

    UpdateArticleRequest updateRequest = new UpdateArticleRequest("Second Update with wrong version",
        "Updating",
        "Updating",
        LocalDate.parse("2020-01-01"),
        Set.of("Cenk Akin", "Pulitzer"),
        Set.of("health"),
        1L
    );

    //when - then
    webClient.put()
        .uri("/api/v1/articles/" + articleId)
        .body(BodyInserters.fromValue(updateRequest))
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void shouldReturnNotFoundWhenUpdatingArticleRequestIsAlreadyDeleted() {
    //given
    Article entity = generateArticleForTest();
    Article createdArticle = articleRepository.save(entity).block();
    String articleId = createdArticle.getId();
    articleRepository.save(createdArticle.delete()).block();

    UpdateArticleRequest updateRequest = new UpdateArticleRequest("Trying to update the deleted article",
        "Updating",
        "Updating",
        LocalDate.parse("2020-01-01"),
        Set.of("Cenk Akin", "Pulitzer"),
        Set.of("health"),
        1L
    );

    //when - then
    webClient.put()
        .uri("/api/v1/articles/" + articleId)
        .body(BodyInserters.fromValue(updateRequest))
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void shouldGetEmptyResultWith200WhenNoArticleExist() {
    //when - then
    webClient.get()
        .uri("/api/v1/articles")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(List.class)
        .value(l -> Assertions.assertTrue(l.isEmpty()));
  }

  @Test
  void shouldGetArticles() {
    Flux.fromStream(Stream.generate(this::generateArticleForTest))
        .take(5L)
        .flatMap(articleRepository::save)
        .collectList()
        .block();

    //when - then
    webClient.get()
        .uri("/api/v1/articles")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(ArticleDto.class)
        .hasSize(5);
  }

  @Test
  void shouldReturn200WhenDeleteArticle() {
    Article entity = generateArticleForTest();
    Article createdArticle = articleRepository.save(entity).block();

    //beforeDelete
    String articleId = createdArticle.getId();

    webClient.get()
        .uri("/api/v1/articles/" + articleId)
        .exchange()
        .expectStatus()
        .isOk();

    //when - then
    webClient.delete()
        .uri("/api/v1/articles/" + articleId)
        .exchange()
        .expectStatus()
        .isOk();

    //afterDelete
    webClient.get()
        .uri("/api/v1/articles/" + articleId)
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void shouldReturnOneResultWhenSearchWithGivenKeywordAndAuthor() {
    //given
    articleRepository.saveAll(generateArticlesForTest()).subscribe();
    URI uri = UriComponentsBuilder.fromPath("/api/v1/articles:search")
        .queryParam("keyword", "keyword1")
        .queryParam("author", "author3")
        .build().toUri();

    //when - then
    webClient.get()
        .uri(uri)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(ArticleDto.class)
        .hasSize(1);
  }

  @Test
  void shouldReturnOneResultWhenSearchWithGivenKeywordAndAuthorAndFromPublishDate() {
    //given
    articleRepository.saveAll(generateArticlesForTest()).subscribe();
    URI uri = UriComponentsBuilder.fromPath("/api/v1/articles:search")
        .queryParam("keyword", "keyword1")
        .queryParam("author", "author1")
        .queryParam("fromPublishDate", "2019-12-12")
        .build().toUri();

    //when - then
    webClient.get()
        .uri(uri)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(ArticleDto.class)
        .hasSize(1);
  }

  @Test
  void shouldReturnTwoResultsWhenSearchWithAuthorAndFromPublishDateAndToPublishDate() {
    //given
    articleRepository.saveAll(generateArticlesForTest()).subscribe();
    URI uri = UriComponentsBuilder.fromPath("/api/v1/articles:search")
        .queryParam("author", "author1")
        .queryParam("fromPublishDate", "2019-01-12")
        .queryParam("toPublishDate", "2020-12-12")
        .build().toUri();

    //when - then
    webClient.get()
        .uri(uri)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(ArticleDto.class)
        .hasSize(2);
  }

  @Test
  void shouldReturnTwoResultsWhenSearchWithPossibleAllFields() {
    //given
    articleRepository.saveAll(generateArticlesForTest()).subscribe();
    URI uri = UriComponentsBuilder.fromPath("/api/v1/articles:search")
        .queryParam("author", "author1")
        .queryParam("keyword", "keyword1")
        .queryParam("fromPublishDate", "2019-01-12")
        .queryParam("toPublishDate", "2020-12-12")
        .build().toUri();

    //when - then
    webClient.get()
        .uri(uri)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(ArticleDto.class)
        .hasSize(2);
  }


//
//  @Test
//  void shouldCreateCorrectQueryWithAllFields() {
//    //given
//    SearchArticleRequest request = new SearchArticleRequest("health", "Pulitzer", LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-25"));
//    ArgumentCaptor<Predicate> predicateArgumentCaptor = ArgumentCaptor.forClass(Predicate.class);
//
//    //when
//    articleService.searchArticles(request);
//    verify(articleRepository).findAll(predicateArgumentCaptor.capture());
//    Predicate query = predicateArgumentCaptor.getValue();
//
//    //then
//    assertEquals("article.deleted = false && eqIc(any(article.authors),Pulitzer) && eqIc(any(article.keywords),health) " +
//            "&& article.publishDate > 2018-12-31 && article.publishDate < 2020-01-26",
//        query.toString());
//  }

  private Article generateArticleForTest() {
    return generateArticleForTest(
        "Corona Virus!" + UUID.randomUUID().toString(),
        "Is it a worldwide threat?" + UUID.randomUUID().toString(),
        "We should be careful..." + UUID.randomUUID().toString(),
        Set.of("Cenk Akin", "Pulitzer", UUID.randomUUID().toString()),
        Set.of("health", UUID.randomUUID().toString()),
        LocalDate.parse("2020-01-01"));
  }

  private List<Article> generateArticlesForTest() {
    Article article1 = generateArticleForTest("header1", "shortDescription1", "text1", Set.of("author1", "author2"),
        Set.of("keyword1", "keyword2"), LocalDate.parse("2019-09-01"));
    Article article2 = generateArticleForTest("header2", "shortDescription2", "text2", Set.of("author1", "author3"),
        Set.of("keyword1", "keyword3"), LocalDate.parse("2020-01-01"));
    return List.of(article1, article2);
  }

  private Article generateArticleForTest(String header, String shortDescription,
                                         String text, Set<String> authors, Set<String> keywords,
                                         LocalDate publishDate) {
    CreateArticleRequest request = new CreateArticleRequest(header, shortDescription, text, publishDate, authors, keywords);
    return Article.fromCreateArticleRequest(request);
  }
}