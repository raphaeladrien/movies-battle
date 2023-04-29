## Build image ##
FROM openjdk:17-slim as build

# Labels
LABEL maintainer="ada.tech"

# Env variables
ENV GRADLE_OPTS -Dorg.gradle.daemon=false
ENV APP_HOME /app

# Workdir creation
WORKDIR $APP_HOME

# Gradle files
COPY gradle gradle
COPY ["gradlew", "build.gradle", "settings.gradle", "$APP_HOME/"]

# Source code
COPY src src
COPY db db

# Build project
RUN bash gradlew clean build -x test

## Charge Management Web container ##
FROM gcr.io/distroless/java17-debian11:nonroot AS moviesbattle

# Labels
LABEL maintainer="ada.tech"

# Env variables
ENV APP_HOME /app

# Workdir creation
WORKDIR $APP_HOME
COPY --from=build /app/build/libs/moviesbattle.jar .
COPY --from=build /app/db ./db

# Container entrypoint
CMD [ "moviesbattle.jar" ]
