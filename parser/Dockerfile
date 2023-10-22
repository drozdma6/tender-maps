FROM amazoncorretto:17-alpine AS build
WORKDIR /app
COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
COPY src src
RUN ./gradlew bootjar

FROM amazoncorretto:17-alpine
# Create a group and user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
VOLUME /tmp
COPY --from=build /app/build/libs/*.jar app.jar
# Tell docker that all future commands should run as the appuser
USER appuser
CMD ["java", "-jar", "app.jar"]