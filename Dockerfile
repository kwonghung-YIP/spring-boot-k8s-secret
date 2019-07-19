FROM openjdk:8u212-slim
LABEL maintainer="kwonghung.yip@gmail.com"

COPY build/libs/spring-boot-k8s-secret-0.0.1-SNAPSHOT.jar .

#CMD ["./gradlew", "bootRun"]
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","build/libs/spring-boot-k8s-secret-0.0.1-SNAPSHOT.jar"]
