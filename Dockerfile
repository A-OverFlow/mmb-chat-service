# 1단계: 빌드 스테이지
FROM gradle:8.6-jdk17 AS builder

WORKDIR /home/gradle/app

# 빌드 캐시 최적화를 위해 필요한 파일만 먼저 복사
COPY settings.gradle.kts .
COPY build.gradle.kts .

# (필요하다면 gradle 디렉토리도 미리 복사해줘야 함)
# COPY gradle gradle

# 의존성만 사전 다운로드
RUN gradle dependencies --no-daemon || true

# 전체 소스 복사 (src, Dockerfile 등 포함)
COPY . .

# (!!!) 소스 복사한 다음에 빌드해야 제대로 JAR 생긴다
RUN gradle clean bootJar --no-daemon

# 2단계: 실행 스테이지
FROM amazoncorretto:17-alpine

# JAR 파일 복사
COPY --from=builder /home/gradle/app/build/libs/ms-msa-playground.jar /app/ms-msa-playground.jar

EXPOSE 8080 5005

ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "/app/ms-msa-playground.jar"]
