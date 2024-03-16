# 런타임 이미지로 OpenJDK 17 지정
FROM openjdk:17

# 애플리케이션을 실행할 작업 디렉토리를 생성
WORKDIR /app

# 빌드 이미지에서 생성된 JAR 파일을 런타임 이미지로 복사
COPY --from=build /app/build/libs/*.jar /app/app.jar

# 애플리케이션 실행
EXPOSE 8081
ENTRYPOINT ["java"]
CMD ["-jar","app.jar"]