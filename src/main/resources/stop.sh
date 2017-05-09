#!/bin/bash

#bin=`dirname "$0"`
#. "$bin/env.sh"

#cd $APP_HOME

echo "Current JVM Process:"
jps
echo ""

MAIN_CLASS=io.sugo.user.group.UserGroupBootstrap


PID=`jps -ml|grep $MAIN_CLASS|awk '{print $1;}'`

if [ "$PID" = "" ] ;then
	echo "$MAIN_CLASS is not started"
	exit 0;
else
	echo "kill $PID"
	kill $PID
	for ((i=1;i<=1000;i+=1))
	do
		p=`jps -ml|grep $MAIN_CLASS|awk '{print $1;}'`
		if [ "$p" == "" ];then 
			echo "$PID has exit"
			exit 0
		else 
			echo "waitting $PID to exit"
			sleep 3
		fi
	done
fi
exit 2




