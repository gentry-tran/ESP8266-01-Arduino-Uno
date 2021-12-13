FROM eclipse-temurin:11
COPY target/java-server-1.0.0-SNAPSHOT-jar-with-dependencies.jar /grpc-java-server/
WORKDIR /grpc-java-server
EXPOSE 8080
ENTRYPOINT [ "java","-jar","java-server-1.0.0-SNAPSHOT-jar-with-dependencies.jar" ]