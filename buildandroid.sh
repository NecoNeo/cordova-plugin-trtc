#! /bin/bash

# for android build use

# App Id
appId="app_id"

echo ${appId}

echo -e "prepare...\n"
cordova platform rm android
cordova plugin add cordova-plugin-trtc --variable APP_ID="${appId}"
cordova platform add android
echo -e "BUILD START...\n"
cordova build android
echo -e "TASK END\n"

read -p "task end"
