#!/bin/bash

#bin=`dirname "$0"`
#. "$bin/env.sh"
#APP_HOME=`pwd`
APP_HOME=/web/stork/monitor

echo "Current JVM Process:" 
/usr/local/jdk/bin/jps 
echo "" 

MAIN_CLASS=io.sugo.user.group.UserGroupBootstrap


PID=`/usr/local/jdk/bin/jps -ml|grep $MAIN_CLASS|awk '{print $1;}'`

if [ "$PID" = "" ] ;then
	echo `date +'%Y-%m-%d %H:%M:%S'` 'start stork-monitor...' >> $APP_HOME/check.log
	$APP_HOME/start.sh >> $APP_HOME/check.log
else
	echo `date +'%Y-%m-%d %H:%M:%S'` "process $PID is running" >> $APP_HOME/check.log
fi
exit 2
