# docker build -t etoro-api .
# docker run -p 8088:8088 -e LOGIN=mkjiau -e PASSWORD=ooooooooo -it --rm --name my-etoro-api etoro-api

FROM openjdk:11-jdk-slim AS builder
RUN apt update && apt-get install libfontconfig -y
WORKDIR /artifact/app
COPY . .
RUN ./gradlew build
ENV OPENSSL_CONF=/etc/ssl/
EXPOSE 8088
# ENTRYPOINT ["java","-jar","/artifact/app/build/libs/*.jar"]
ENTRYPOINT ["java","-jar","/artifact/app/build/libs/etoro-api-0.1.2.jar"]
