#!/bin/sh

TomcatId=$(ps -ef|grep tomcat|grep -v grep|grep -v ffmpeg|awk '{print $2}')
# tomcat启动程序(这里注意tomcat实际安装的路径)  
StartTomcat=/home/cwh/software/tomcat/bin/startup.sh  
  
# 日志输出  
TomcatMonitorLog=/home/cwh/software/tomcat/logs/TomcatMonitor.log
  
Monitor()  
{  
  while true
  do
    if [ -z "$TomcatId" ];then # 这里判断TOMCAT进程是否存在
      echo "[error]tomcat die, restarting"  
      $StartTomcat  
    else  
      echo "[info]tomcat id is $TomcatId"  
    fi  
    sleep 30s  
    echo "------------------------------"  
  done
}  
Monitor>>$TomcatMonitorLog  
