package com.chuwa.cordova.trtc;

/**
 * 从业务服务器获取PrivateMapKey
 */
public interface SyncPrivateMapkeyView {
    /** 获取登录信息成功 */
    void onSyncKeySuccess(PrivateMapKeyInfo privateMapKey);
    /** 获取登录信息失败 */
    void onSyncKeyFailed(int code, String errInfo);
}
