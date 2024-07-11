#!/bin/bash
BUILD_JAR=$(ls /home/ubuntu/jenkins/build/libs/*.jar)     # jar가 위치하는 곳
JAR_NAME=$(basename $BUILD_JAR)
echo "> build 파일명: $JAR_NAME" >> /home/ubuntu/deploy.log

echo "> build 파일 복사" >> /home/ubuntu/deploy.log
DEPLOY_PATH=/home/ubuntu/
cp $BUILD_JAR $DEPLOY_PATH