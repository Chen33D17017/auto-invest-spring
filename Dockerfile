FROM adoptopenjdk/openjdk15:alpine
EXPOSE 8080
ENV ENV=prod
ARG JAR_FILE=target/auto-Invest.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]