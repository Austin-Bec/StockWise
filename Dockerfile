FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x stockwise/mvnw && cd stockwise && ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["sh", "-c", "cd stockwise && java -Dserver.port=${PORT:-8080} -jar target/stockwise-0.0.1-SNAPSHOT.jar"]
