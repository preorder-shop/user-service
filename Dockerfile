FROM openjdk:17-slim

WORKDIR /app

COPY ./build/libs/*.jar ./app.jar

EXPOSE 8081
ENTRYPOINT ["java"]
CMD ["-jar","app.jar"]