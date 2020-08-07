FROM maven:3-jdk-11 as deps
WORKDIR /app

ARG GITHUB_USER
ARG GITHUB_TOKEN
ENV GITHUB_USER=$GITHUB_USER GITHUB_TOKEN=$GITHUB_TOKEN

COPY pom.xml .
COPY settings.xml /app/
RUN mvn -B -Dmaven.repo.local=/app/.m2 dependency:go-offline --settings settings.xml

FROM deps as build

COPY src src
COPY settings.xml /app/
RUN mvn -B -Dmaven.repo.local=/app/.m2 package -DskipTests --settings settings.xml


FROM openjdk:11-jre-slim
WORKDIR /app
VOLUME /tmp
COPY start-dos.sh /app
RUN chmod +x start-dos.sh
ENTRYPOINT [ "/app/start-dos.sh" ]
EXPOSE 8085

COPY --from=build /app/target/cds-dos.war /app