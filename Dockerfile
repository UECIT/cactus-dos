FROM maven:3-jdk-11 as deps
WORKDIR /app

COPY pom.xml .
RUN mvn -B -Dmaven.repo.local=/app/.m2 dependency:go-offline

FROM deps as build

COPY src src
RUN mvn -B -Dmaven.repo.local=/app/.m2 package

FROM openjdk:11-jre-slim
WORKDIR /app
VOLUME /tmp
COPY start-dos.sh /app
RUN chmod +x start-dos.sh
ENTRYPOINT [ "/app/start-dos.sh" ]
EXPOSE 8085

COPY --from=build /app/target/cds-dos.war /app