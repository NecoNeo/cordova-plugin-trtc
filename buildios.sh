#! /bin/bash

# for ios build use

# App Id
appId="app_id"

echo ${appId}

echo -e "prepare...\n"
cordova platform rm ios
cordova plugin add cordova-plugin-trtc --variable APP_ID="${appId}"
cordova platform add ios
echo -e "BUILD START...\n"
cordova build ios
echo -e "TASK END\n"
