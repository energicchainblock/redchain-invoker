#!/bin/bash
cd `dirname $0`
LIB_DIR=`pwd`

boot_jar='chain-restful.jar'
PIDS=`ps -ef | grep java | grep "$LIB_DIR" |grep $boot_jar|awk '{print $2}'`
if [ -n "$PIDS" ]; then
    echo "start fail! The $boot_jar already started!"
    exit 1
fi


LIB_JARS=`ls $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`
cd ..
nohup java -Djava.net.preferIPv4Stack=true -server -Xms1g -Xmx1g -XX:PermSize=128m  -jar $boot_jar nohup.out 2>&1 &
echo "start "$boot_jar" success!"