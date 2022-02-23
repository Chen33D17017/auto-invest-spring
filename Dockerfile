FROM arm32v7/openjdk:11
EXPOSE 8080
ENV ENV=prod
ARG JAR_FILE=app.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
