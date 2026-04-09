FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY stockwise/ .

RUN chmod +x mvnw && ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar target/stockwise-0.0.1-SNAPSHOT.jar"]