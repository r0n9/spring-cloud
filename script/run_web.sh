#!/bin/bash

base_path=$(cd `dirname $0`; cd ..; pwd)

cd $base_path

scp cloud-eureka-consumer/target/*.jar rong@s5.fanrong.vip:/home/rong/cloud/web
ssh rong@s5.fanrong.vip 'ps -ef|grep java|grep rong|grep cloud-eureka-consumer|grep -v grep|cut -c 9-15|xargs kill -9'
ssh rong@s5.fanrong.vip 'cd /home/rong/cloud/web; nohup java -jar cloud-eureka-consumer-1.0-SNAPSHOT.jar &'

scp cloud-eureka-consumer/target/*.jar rong@or233.cn:/home/rong/cloud/web
ssh rong@or233.cn 'ps -ef|grep java|grep rong|grep cloud-eureka-consumer|grep -v grep|cut -c 9-15|xargs kill -9'
ssh rong@or233.cn 'cd /home/rong/cloud/web; nohup java -jar cloud-eureka-consumer-1.0-SNAPSHOT.jar &'
