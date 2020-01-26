# My Newspaper

## Libraries

- Spring webflux
- Embedded mongo
- QueryDSL
- Swagger

## How to run

- Use spring boot maven plugin (***You need to install and activate java 12 for this option (see https://www.jenv.be/)***):

```
./mvnw spring-boot:run
```
Go `http://localhost:8080/swagger-ui.html`

- You can pull and run docker image:
 
```
docker pull cenkakin/my-newspaper
docker run -p 8080:8080 -t cenkakin/my-newspaper
```
Go `http://[YOUR_DOCKER_HOST]:8080/swagger-ui.html`


- Use your IDE and run 
 ```
com.github.cenkakin.mynewspaper.MyNewspaperApplication
```
Go `http://localhost:8080/swagger-ui.html`

## Next Steps

- Use standalone mongo db instead of embedded
- Add paging and sorting into search and get methods
- Split article object into article and articleDetail, it would be better to keep textBlob separately to have better performance   
- Create author and keyword services and endpoints to query articles by their ids instead of text search
- Decide conditions of the uniqueness of article (having a composite key for header + authorId list would be a good start)
- Store articles in ElasticSearch like services to have a stronger searching capabilities
- Split command and query functions and use event sourcing
- Integrate CI/CD to automatise builds (spotify docker plugin, circleCI, travisCI etc)
- Enhance custom metrics
- Add monitoring and logging components (for ex: kibana, prometheus, grafana etc) and export application logs and metrics (might setup micrometer, fluent etc?)


![Desired Architecture](desired_architecture.png?raw=true "Desired Architecture")
  