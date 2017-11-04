# ###############################
# Build stage
# ###############################

FROM openjdk:8u131-jdk-alpine

WORKDIR /build-app
COPY . /build-app
RUN ./gradlew --no-daemon installDist

# ###############################
# Deploy stage
# ###############################

FROM openjdk:8u131-jre-alpine

EXPOSE 4567

WORKDIR /app
COPY --from=0 /build-app/build/install/parseserver .

CMD ["bin/parseserver"]