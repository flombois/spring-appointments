FROM eclipse-temurin:17-jre-ubi9-minimal@sha256:2419c9c7116601aee0c939888e2eed78e235d38f5f5e9e9f1d1d18d043df55eb
ADD ./target/spring-appointments.jar .
USER 1001
ENTRYPOINT java -jar spring-appointments.jar
EXPOSE 8080