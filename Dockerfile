# Use the official JDK 21 image as the base image
FROM openjdk:21-jdk

# Copy the JAR file from the target directory to the container
COPY ./target/*.jar /app/app.jar

# Expose port 8089
EXPOSE 8080

# Run the application
ENTRYPOINT exec java $JAVA_OPTIONS -jar /app/app.jar