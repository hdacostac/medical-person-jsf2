FROM registry.cegedim.com/chs/swf-java:8

ENV HTTP_PORT 9191

ENV ENV_NAME test2

ENV DB0_SERVER db-t1mpchs.cegedim
ENV DB0_PORT 1521
ENV DB0_SID t1mpchs.cegedim.fr
ENV DB0_USER MESPATIENTS2
ENV DB0_PASS pIgS7Ks#

ENV CONFIG_FILES_DIR /home/app/config

ENV PROXY_OPTS ""
ENV REGION_OPTS "-Duser.timezone=UTC -Duser.language=fr -Duser.region=FR"
ENV JAVA_OPTS "-Xms512m -Xmx512m -Xss256k -Djava.net.preferIPv4Stack=true -Dfile.encoding=UTF-8 $PROXY_OPTS $REGION_OPTS -server"

ENV BUILD_URL ""
ENV BUILD_NUMBER ""

# RUN apk add --no-cache bash
# RUN addgroup app && adduser app app
# RUN addgroup app
RUN adduser --disabled-password --gecos "" app
# RUN usermod -aG app app

USER app
WORKDIR /home/app

RUN mkdir -p $CONFIG_FILES_DIR

COPY target/web-example.jar /tmp
COPY scripts/entrypoint.sh /tmp
COPY src/main/resources/docker/application.properties /tmp

RUN cp -r /tmp/web-example.jar /home/app/
RUN cp -r /tmp/entrypoint.sh /home/app/
RUN cp -r /tmp/application.properties /home/app/

RUN sed -i 's/\r$//' entrypoint.sh

RUN ["chmod", "+x", "entrypoint.sh"]

EXPOSE ${HTTP_PORT}

ENTRYPOINT ["./entrypoint.sh"]
