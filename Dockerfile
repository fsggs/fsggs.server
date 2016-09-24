FROM java:8

MAINTAINER Dimitriy Kalugin

VOLUME /tmp
WORKDIR .

ADD fsggs.server-0.0.4-all.jar .

CMD ["java", "-jar", "fsggs.server-0.0.4-all.jar"]

EXPOSE 32500
