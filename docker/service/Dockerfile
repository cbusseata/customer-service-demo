FROM maven:3.9.6-amazoncorretto-17

ARG SPRING_PROFILE=default
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILE}

RUN echo "Spring profile: ${SPRING_PROFILES_ACTIVE}"

# Replace with copying src over
COPY src ./src/
COPY pom.xml pom.xml

# JAR the application
RUN mvn clean package

#ENTRYPOINT ["java","-jar","target/demo-0.0.1-SNAPSHOT.jar"]
ENTRYPOINT exec java -jar -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} target/demo-0.0.1-SNAPSHOT.jar
