FROM openjdk:17
add target/*.jar rest-demo.jar
ENTRYPOINT ["java", "-jar", "rest-demo.jar"]

