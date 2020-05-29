FROM openjdk:8-jdk-alpine
USER root
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
COPY build/libs/*.jar /opt/app.jar
WORKDIR /opt
ENTRYPOINT exec java -jar app.jar