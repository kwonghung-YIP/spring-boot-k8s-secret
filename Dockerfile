FROM openjdk:8u212-slim
LABEL maintainer="kwonghung.yip@gmail.com"

ARG BUILD_JAR_FILE
ENV BUILD_JAR_FILE=$BUILD_JAR_FILE

COPY build/libs/$BUILD_JAR_FILE .

#CMD ["./gradlew", "bootRun"]
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","build/libs/$BUILD_JAR_FILE"]
