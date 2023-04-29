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

## Movie battle image ##
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

## Documentation Image ##
FROM node:alpine3.11 AS doc

# Labels
LABEL maintainer="tblx"

# Env variables
ENV APP_HOME /docs

# Install dependencies
RUN npm install -g yamlinc http-server

# Workdir creation
WORKDIR $APP_HOME
# Copy with a dot to force docker to copy only the contents
# and not the api-docs dir
COPY api-docs .

# Compile yaml files
RUN yamlinc moviebattle.yaml && mv moviebattle.inc.yaml swagger-ui/moviebattle.inc.yaml

# Container entrypoint
CMD [ "/bin/sh", "-c", "http-server /docs/swagger-ui -p 3333"]
