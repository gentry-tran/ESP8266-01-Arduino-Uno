FROM eclipse-temurin:11
COPY target/java-server-1.0.0-SNAPSHOT.jar /grpc-java-server/
COPY /grpc-java-server/classes/* .
EXPOSE 8080
ENTRYPOINT [ "java","-jar","java-server-1.0.0-SNAPSHOT.jar" ]