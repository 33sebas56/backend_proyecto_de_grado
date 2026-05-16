# Build stage
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy Maven files first to improve Docker cache usage
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw ./

RUN chmod +x mvnw
RUN mvn -B -DskipTests dependency:go-offline

# Copy source code and build the Spring Boot jar
COPY src ./src
RUN mvn -B -DskipTests clean package

# Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

ENV JAVA_OPTS=""

COPY --from=build /app/target/convenios-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 10000

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.address=0.0.0.0 -Dserver.port=${PORT:-10000} -jar /app/app.jar"]