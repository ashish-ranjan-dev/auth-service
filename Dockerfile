FROM openjdk:17
EXPOSE 8080
ADD ./build/libs/auth-service-1.0-SNAPSHOT.jar auth-service-1.0-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","auth-service-1.0-SNAPSHOT.jar"]
