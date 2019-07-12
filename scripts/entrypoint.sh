#!/bin/bash

cd ${CONFIG_FILES_DIR}

echo "==> Configure the environment..."
echo "====> Download properties to determine the application.properties file to use with Spring Boot..."
git clone https://sa_swf_tools:V5%40%26Mm7q%406%2AP%2A9BpFp5t@swf-git.cegedim.com/scm/swfar/swf-env-properties.git .
cp ${CONFIG_FILES_DIR}/mlm/${ENV_NAME}/web-example-test.properties /home/app/application.properties

cd /home/app

JAVA_OPTS=${JAVA_OPTS}

HTTP_PORT=${HTTP_PORT}
DB0_SERVER=${DB0_SERVER}
DB0_PORT=${DB0_PORT}
DB0_SID=${DB0_SID}
DB0_USER=${DB0_USER}
DB0_PASS=${DB0_PASS}

echo JAVA_OPTS=$JAVA_OPTS

java -Dspring.config.location=file:/home/app/ $JAVA_OPTS -jar web-example.jar