#!/bin/bash

base_path=$(cd `dirname $0`; cd ..; pwd)

# Git pull
cd $base_path
git pull

# maven build
mvn clean package -DskipTests -Pprod


# stop services
ps -ef|grep java|grep rong|grep cloud-eureka-server|grep -v grep|cut -c 9-15|xargs kill -9
ps -ef|grep java|grep rong|grep cloud-api-gateway|grep -v grep|cut -c 9-15|xargs kill -9
ps -ef|grep java|grep rong|grep client-kds-crawler|grep -v grep|cut -c 9-15|xargs kill -9
ps -ef|grep java|grep rong|grep cloud-eureka-consumergrep -v grep|cut -c 9-15|xargs kill -9

# start services
java -jar $base_path/cloud-eureka-server/target/cloud-eureka-server-0.0.1-SNAPSHOT.jar >~/logs/cloud-eureka-server.out 2>&1 &
sleep 1

java -jar $base_pathcloud-api-gateway/target/cloud-api-gateway-0.0.1-SNAPSHOT.jar >~/logs/cloud-api-gateway.out 2>&1 &
sleep 1

java -jar client-kds-crawler/target/client-kds-crawler-0.0.1-SNAPSHOT.jar >~/logs/client-kds-crawler.out 2>&1 &
sleep 1

java -jar cloud-eureka-consumer/target/cloud-eureka-consumer-0.0.1-SNAPSHOT.jar >~/logs/cloud-eureka-consumer.out 2>&1 &

