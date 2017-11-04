# ###############################
# Build stage
# ###############################

FROM openjdk:8u131-jdk-alpine

COPY . /usr/src/myapp
WORKDIR /usr/src/myapp
RUN ./gradlew --no-daemon installDist

# ###############################
# Deploy stage
# ###############################

FROM openjdk:8u131-jre-alpine

EXPOSE 4567

WORKDIR /app
COPY build/install/parseserver .

CMD ["bin/parseserver"]