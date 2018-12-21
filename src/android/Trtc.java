package com.chuwa.cordova.trtc;


// import android.net.Uri;
// import android.content.SharedPreferences;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.tencent.ilivesdk.ILiveSDK;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
// import org.json.JSONObject;


public class Trtc extends CordovaPlugin {

    static final String ACTION_SHOW_CREATE_ACTIVITY = "showCreateActivity"; // for test
    static final String ACTION_JOIN_CHANNEL = "joinChannel";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Activity activity = this.cordova.getActivity();

        if (ACTION_SHOW_CREATE_ACTIVITY.equals(action)) {
            Intent intent = new Intent(activity, CreateActivity.class);
            cordova.startActivityForResult(this, intent, 666);
        }

        return false;
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        Log.v("TRTCPlugin", ILiveSDK.getInstance().getVersion());
        super.initialize(cordova, webView);
    }


}
