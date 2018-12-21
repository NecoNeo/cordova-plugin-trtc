package com.chuwa.cordova.trtc;

/**
 * 帐号被踢回调接口
 */
public interface OfflineView {
    /** 帐号被踢下线 */
    void onOffline(int errCode, String strMsg);
}
