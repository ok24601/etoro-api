FROM markhobson/maven-chrome:jdk-11
RUN apt update && apt install -y xvfb/stable google-chrome-stable

WORKDIR /artifact/app
COPY . .
RUN ./gradlew build

EXPOSE 8088
CMD xvfb-run -l -a -e /dev/stdout java -jar /artifact/app/build/libs/etoro-api-0.1.3.jar