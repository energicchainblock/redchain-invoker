#!/bin/sh  
  
jarName="chain-restful"  
pidPath="/var/run/$jarName-tpid"  
  
rm -f $pidPath  
  
nohup java -jar ./$jarName.jar -server -Xms1024m -Xmx2048m -Xss256k > ./run.log 2>&1 &  
  
echo $! > $pidPath  