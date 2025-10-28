# ビルド用と実行用を分けてイメージを軽量化する

# ------------
# Build Stage
# ------------
FROM gradle:8.10-jdk21-alpine AS builder
WORKDIR /app

COPY build.gradle settings.gradle gradle* ./
RUN gradle dependencies --no-daemon || true

COPY src ./src
RUN gradle clean bootJar --no-daemon

# ----------
# Run Stage
# ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache tzdata
ENV TZ=Asia/Tokyo

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]