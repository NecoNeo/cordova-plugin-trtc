package com.chuwa.cordova.trtc;

// import org.apache.cordova.CordovaInterface;

// import android.content.Intent;
// import android.net.Uri;
// import android.content.SharedPreferences;

import android.app.Activity;
import android.content.Intent;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
// import org.json.JSONObject;


public class Trtc extends CordovaPlugin {

    static final String ACTION_JOIN_CHANNEL = "joinChannel";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Activity activity = this.cordova.getActivity();

        // if (ACTION_JOIN_CHANNEL.equals(action)) {
        //     Intent intent = new Intent(activity, ChatRoomActivity.class);
        //     cordova.startActivityForResult(this, intent, 666);
        // }

        return false;
    }

}
