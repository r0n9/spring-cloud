#!/bin/bash

base_path=$(cd `dirname $0`; cd ..; pwd)

# maven build
cd $base_path

mvn clean package -DskipTests -Pprod


# stop services
ps -ef|grep java|grep rong|grep cloud-eureka-server|grep -v grep|cut -c 9-15|xargs kill -9
ps -ef|grep java|grep rong|grep cloud-api-gateway|grep -v grep|cut -c 9-15|xargs kill -9
ps -ef|grep java|grep rong|grep client-kds-crawler|grep -v grep|cut -c 9-15|xargs kill -9
ps -ef|grep java|grep rong|grep cloud-eureka-consumergrep -v grep|cut -c 9-15|xargs kill -9


cd $base_path
java -jar cloud-eureka-server/target/cloud-eureka-server-1.0-SNAPSHOT.jar >~/logs/cloud-eureka-server.out 2>&1
sleep 1
java -jar cloud-api-gateway/target/cloud-api-gateway-1.0-SNAPSHOT.jar >~/logs/cloud-api-gateway.out 2>&1
sleep 1
java -jar client-kds-crawler/target/client-kds-crawler-1.0-SNAPSHOT.jar >~/logs/client-kds-crawler.out 2>&1
sleep 1
java -jar cloud-eureka-consumer/target/cloud-eureka-consumer-1.0-SNAPSHOT.jar >~/logs/cloud-eureka-consumer.out 2>&1


