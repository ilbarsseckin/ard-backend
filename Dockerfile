# ─── STAGE 1: Build ───────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-24 AS builder

WORKDIR /app

# Bağımlılıkları önce kopyala → layer cache
COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn clean package -DskipTests -q

# ─── STAGE 2: Runtime ─────────────────────────────────────────────
FROM eclipse-temurin:24-jre-alpine

RUN addgroup -S spring && adduser -S spring -G spring
USER spring

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

# Sentry, R2, iyzico vb. env'den gelir — hardcode yok
EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
