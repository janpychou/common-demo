#!/bin/bash

APP_HOME=`pwd`
#APP_HOME=/web/stork/agent
export APP_HOME
cd $APP_HOME
echo $APP_HOME

#export JAVA_HOME=/usr/local/jdk

CLASSPATH=${CLASSPATH}:$APP_HOME/:$APP_HOME/lib/*

for f in $APP_HOME/*.jar; do
  CLASSPATH=${CLASSPATH}:$f;
done

#for f in $APP_HOME/lib/*.jar; do
#  CLASSPATH=${CLASSPATH}:$f;
#done

export CLASSPATH

echo "monitor home : ${APP_HOME}"

#debug option
#JVM_ARGS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8765,suspend=y "

JVM_ARGS="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$APP_HOME/dump_file/ -XX:ErrorFile=$APP_HOME/dump_file/ -XX:OnOutOfMemoryError=\"kill -9 %p\" $JVM_ARGS "
JVM_ARGS="-Xmx4096M -Xshare:off -Xms512M -XX:+UseParallelGC -XX:ParallelGCThreads=4 -XX:+UseParallelOldGC -Duser.timezone=GMT+08 -Dfile.encoding=UTF-8 $JVM_ARGS "

echo "JAVA_HOME:$JAVA_HOME"
echo "  JAVA_HOME cannot be null"
MAIN_CLASS=io.sugo.user.group.UserGroupBootstrap
cmd="$JAVA_HOME/bin/java $JVM_ARGS -cp $CLASSPATH $MAIN_CLASS $@ > /dev/null 2>&1 &"
#cmd="java $JVM_ARGS -cp $CLASSPATH $MAIN_CLASS $@ &"
echo $cmd
eval $cmd
