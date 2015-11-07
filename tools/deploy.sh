#!/bin/bash
cd /root/mpserver/maopao_server
git pull
activator clean compile stage
play_pid=`cat /root/mpserver/maopao_server/target/universal/stage/RUNNING_PID`
kill -9 $play_pid
rm /root/mpserver/maopao_server/target/universal/stage/RUNNING_PID
activator start
