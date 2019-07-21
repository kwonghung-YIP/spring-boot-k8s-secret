FROM openjdk:8u212-slim
LABEL maintainer="kwonghung.yip@gmail.com"

ARG BUILD_JAR_FILE
ENV BUILD_JAR_FILE=$BUILD_JAR_FILE

WORKDIR /usr/local/springboot

COPY build/libs/$BUILD_JAR_FILE /usr/local/springboot

#CMD ["./gradlew", "bootRun"]
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","$BUILD_JAR_FILE"]
