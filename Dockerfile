FROM maven:3.6.2-jdk-12 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

FROM openjdk:12-alpine
RUN apk --no-cache add ca-certificates
RUN wget -q -O /etc/apk/keys/sgerrand.rsa.pub https://alpine-pkgs.sgerrand.com/sgerrand.rsa.pub

RUN wget https://github.com/sgerrand/alpine-pkg-glibc/releases/download/2.29-r0/glibc-2.29-r0.apk
RUN wget https://github.com/sgerrand/alpine-pkg-glibc/releases/download/2.29-r0/glibc-bin-2.29-r0.apk

RUN apk add glibc-2.29-r0.apk glibc-bin-2.29-r0.apk
COPY --from=build /usr/src/app/target/my-newspaper-0.0.1-SNAPSHOT.jar /usr/app/my-newspaper-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/app/my-newspaper-0.0.1-SNAPSHOT.jar"]