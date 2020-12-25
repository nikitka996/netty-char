FROM openjdk:12-jdk
EXPOSE 8080
COPY netty-chat.jar netty-chat.jar
ENTRYPOINT ["java", "-jar", "netty-chat.jar"]
