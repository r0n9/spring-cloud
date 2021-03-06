﻿#!/bin/bash

base_path=$(cd `dirname $0`; cd ..; pwd)

cd $base_path

scp client-web-crawler/target/*.jar rong@s4.fanrong.vip:/home/rong/cloud/client-web-crawler
scp client-web-crawler/target/classes/application.properties rong@s4.fanrong.vip:/home/rong/cloud/client-web-crawler/config

# 停止client-web-crawler
ssh rong@s4.fanrong.vip 'ps -ef|grep java|grep rong|grep client-web-crawler|grep -v grep|cut -c 9-15|xargs kill -9'

# 启动client-web-crawler
ssh rong@s4.fanrong.vip 'cd /home/rong/cloud/client-web-crawler; nohup java -jar client-web-crawler-1.0-SNAPSHOT.jar > ~/logs/client-web-crawler.out 2>&1 &'



#scp client-web-crawler/target/*.jar rong@s5.fanrong.vip:/home/rong/cloud/client-web-crawler
#scp client-web-crawler/target/classes/application.properties rong@s5.fanrong.vip:/home/rong/cloud/client-web-crawler/config

# 停止client-web-crawler
#ssh rong@s5.fanrong.vip 'ps -ef|grep java|grep rong|grep client-web-crawler|grep -v grep|cut -c 9-15|xargs kill -9'

# 启动client-web-crawler
#ssh rong@s5.fanrong.vip 'cd /home/rong/cloud/client-web-crawler; nohup java -jar client-web-crawler-1.0-SNAPSHOT.jar > ~/logs/client-web-crawler.out 2>&1 &'
