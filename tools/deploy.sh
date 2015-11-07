#!/bin/bash
cd /root/mpserver/maopao_server
git pull
activator clean compile stage
PROCESS=`ps -ef|grep play|grep -v grep|grep -v PPID|awk '{ print $2}'`
for i in $PROCESS
do
  echo "Kill the $1 process [ $i ]"
  kill -9 $i
done
pidFile="/root/mpserver/maopao_server/target/universal/stage/RUNNING_PID"
if [ -f "$myFile" ]; then
  rm $pidFile
fi
activator start
