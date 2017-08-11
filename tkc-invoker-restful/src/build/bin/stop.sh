#!/bin/sh
jarName="chain-restful"  
pid=`cat /var/run/$jarName-tpid | awk '{print $1}'`  
pid=`ps -aef | grep $pid | awk '{print $2}' |grep $pid`  
if [ ${pid} ]; then  
kill -9 $pid  
fi  