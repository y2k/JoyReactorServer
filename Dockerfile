# ###############################
# Build stage
# ###############################

FROM openjdk:10.0.2-jdk-slim

WORKDIR /build-app
COPY . /build-app
RUN ./gradlew --no-daemon installDist

# ###############################
# Deploy stage
# ###############################

FROM openjdk:10.0.2-jre-slim

EXPOSE 4567

WORKDIR /app
COPY --from=0 /build-app/build/install/parseserver .

CMD ["bin/parseserver"]