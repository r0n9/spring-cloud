#!/bin/bash

if [ ! -z $JAVA_HOME ]; then
    echo "Use \$JAVA_HOME"
elif [ -n `which java` ]; then
    echo "Use system java"
    echo `java -version`
else
    echo "java does not exist"
    exit 1
fi

base_path=$(cd `dirname $0`; cd ..; pwd)
bin_path=$base_path/bin
jar_file=$bin_path/xxxxxxxxxxxxxxxxxxxxxxx.jar
conf_path=$base_path/conf
prop_file=$conf_path/application.properties
log_path=$base_path/logs
log_file=$log_path/xxxxxxxxxxxxxxxxxxxxxxx.log
pid_path=$log_path/pid
pid_file=$pid_path/xxxxxxxxxxxxxxxxxxxxxxx.pid
stdout_file=$log_path/std.out

mkdir -p $pid_path >/dev/null 2>&1

check_start() {
	if [ -f $pid_file ]; then
		pid=`head -n1 $pid_file`
		if kill -0 $pid >/dev/null 2>&1; then
			echo "xxxxxxxxxxxxxxxxxxxxxxx service has already started. Stop it first."
			exit 1
		fi
	fi
}

check_stop() {
    if [ -f $pid_file ]; then
		pid=`head -n1 $pid_file`
		if kill -0 $pid >/dev/null 2>&1; then
			echo "xxxxxxxxxxxxxxxxxxxxxxx service is running as process $pid."
		fi
	else
		echo "No pid file detected. Maybe there is no service running."
		exit 1
	fi
}

do_start() {
    java -Dext.resources=$conf_path -Dspring.config.location=$prop_file -Dlogging.file=$log_file -jar $jar_file >$stdout_file 2>&1 &
    echo $! > $pid_file
    echo "xxxxxxxxxxxxxxxxxxxxxxx service started. Process id is `head -n1 $pid_file`."
}

do_stop() {
	kill $pid > /dev/null 2>&1
	echo -n "Now stopping"
	while kill -0 $pid > /dev/null 2>&1;
	do
		echo -n "."
		sleep 1;
	done
	echo ""
	rm -f $pid_file
}

case $1 in
    "start")
        check_start
        do_start
    ;;
    "stop")
        check_stop
        do_stop
    ;;
    "restart")
        check_stop
        do_stop
        check_start
        do_start
    ;;
    *)
        echo "Usage $0 [start|stop|restart]"
        exit 1
    ;;
esac