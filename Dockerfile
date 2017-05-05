FROM openjdk:8-jdk-alpine

EXPOSE 4567

COPY . /usr/src/myapp
WORKDIR /usr/src/myapp
RUN ./gradlew --no-daemon installDist

CMD ["build/install/parseserver/bin/parseserver"]