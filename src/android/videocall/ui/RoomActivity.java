package com.chuwa.cordova.trtc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.core.ILiveLog;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.data.ILiveMessage;
import com.tencent.ilivesdk.data.msg.ILiveCustomMessage;
import com.tencent.ilivesdk.data.msg.ILiveTextMessage;
import com.tencent.ilivesdk.listener.ILiveMessageListener;
import com.tencent.ilivesdk.view.AVRootView;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

/**
 * Created by tencent on 2018/5/21.
 */
public class RoomActivity extends Activity implements ILiveMessageListener, View.OnClickListener,
        RoomView, TipsView, SyncPrivateMapkeyView {
    private final static String TAG = "RoomActivity";
    private AVRootView avRootView;
    private TextView tvRoomName, tvRoomId;
    private ImageView ivSwitch, ivBeauty, ivMic, ivLog;
    private EditText etMsg;
    private MsgListView lvChatMsg;
    private ChatMsgAdapter msgAdapter;
    private boolean bFirstBackPress = true;
    private ArrayList<ILiveMessage> chatMsg = new ArrayList<>();

    private boolean bFrontCamera = true, bBeautyEnable = true, bMicEnable = true, bLogEnable = false, bChatEnable = false;

    private FakeR fakeR;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fakeR = new FakeR(this);

        initView();

        OKHelper.getInstance().addPrivateMapKeyView(this);
        SDKHelper.getInstance().addRoomView(this);
        SDKHelper.getInstance().addTipsView(this);

        SDKHelper.getInstance().setTrtcRenderView(avRootView);
        OKHelper.getInstance().getPrivateMapKey(ILiveLoginManager.getInstance().getMyUserId(),
                UserInfo.getInstance().getCurRoomId());
        MessageObservable.getInstance().addObserver(this);

        msgAdapter = new ChatMsgAdapter(this, chatMsg);
        lvChatMsg.setAdapter(msgAdapter);

        initRoleDialog();
        initFeedBackDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SDKHelper.getInstance().setTipsInfoColor(getResources().getColor(fakeR.getId("color", "colorAccent")));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageObservable.getInstance().deleteObserver(this);

        OKHelper.getInstance().removePrivateMapKeyView(this);
        SDKHelper.getInstance().onDestoryRoom();
        SDKHelper.getInstance().removeRoomView(this);
        SDKHelper.getInstance().removeTipsView(this);
    }

    @Override
    public void onBackPressed() {
        if (bChatEnable){
            changeChatStatus(false);
            return;
        }
        if (bFirstBackPress) {
            bFirstBackPress = false;
            SDKHelper.getInstance().exitTrtcRoom();
        }else{
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == fakeR.getId("id", "ll_switch")){
            bFrontCamera = !bFrontCamera;
            SDKHelper.getInstance().switchCamera(bFrontCamera);
            ivSwitch.setImageResource(bFrontCamera ? fakeR.getId("mipmap", "camera") : fakeR.getId("mipmap", "camera2"));
        }else if (v.getId() == fakeR.getId("id", "ll_voice")){
            bMicEnable = !bMicEnable;
            SDKHelper.getInstance().enableMic(bMicEnable);
            ivMic.setImageResource(bMicEnable ? fakeR.getId("mipmap", "mic") : fakeR.getId("mipmap", "mic2"));
        }else if (v.getId() == fakeR.getId("id", "ll_log")){
            changeLogStatus(!bLogEnable);
        }else if (v.getId() == fakeR.getId("id", "ll_beauty")){
            bBeautyEnable = !bBeautyEnable;
            SDKHelper.getInstance().enableBeauty(bBeautyEnable);
            ivBeauty.setImageResource(bBeautyEnable ? fakeR.getId("mipmap", "beauty") : fakeR.getId("mipmap", "beauty2"));
        }else if (v.getId() == fakeR.getId("id", "ll_role")){
            if (null != roleDialog)
                roleDialog.show();
        }else if (v.getId() == fakeR.getId("id", "ll_chat")){
            changeChatStatus(!bChatEnable);
        }else if (v.getId() == fakeR.getId("id", "ll_feedback")){
            if (null != feedDialog) {
                feedDialog.clearCheck();
                if (null != inputDlg && inputDlg.isShowing()){
                    inputDlg.dismiss();
                    inputDlg = null;
                }
                feedDialog.show();
            }
        }
    }

    @Override
    public void onSyncKeySuccess(PrivateMapKeyInfo privateMapKey) {
        SDKHelper.getInstance().enterTrtcRoom(privateMapKey.getRoomId(), privateMapKey.getPrivateMapKey());
    }

    @Override
    public void onSyncKeyFailed(int code, String errInfo) {
        DlgMgr.showMsg(getContext(), "onSyncKeyFailed->: " + code + "|" + errInfo);
        finish();
    }

    @Override
    public void onRoomEnter() {
        DlgMgr.showToast(getContext(), "加入房间成功");
        Rect rectangle= new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);
        SDKHelper.getInstance().setFixStatusHeigth(rectangle.top);
    }

    @Override
    public void onEnterRoomFailed(String module, int errCode, String errMsg) {
        DlgMgr.showMsg(getContext(), "enterRoom->failed: " + module + "|" + errCode + "|" + errMsg);
    }

    @Override
    public void onRoomExit() {
        finish();
    }

    @Override
    public void onRoomDisconnected(String module, int errCode, String errMsg) {
        DlgMgr.showMsg(getContext(), "onRoomDisconnected->: " + module + "|" + errCode + "|" + errMsg);
        finish();
    }

    @Override
    public void onRoomException(int eventId, int errCode, String errMsg) {
        DlgMgr.showToast(getContext(), "event:"+eventId+"|"+errCode+"|"+errMsg);
    }

    @Override
    public void onChangeRoleSuccess() {
        if (!bLogEnable) {  // 切换角色后，打开信息展示
            changeLogStatus(true);
        }
    }

    @Override
    public void onChangeRoleFailed(String module, int errCode, String errMsg) {
        DlgMgr.showMsg(getContext(), "change failed:" + module + "|" + errCode + "|" + errMsg);
    }

    @Override
    public void onFeedBackResult(String module, int errCode, String errMsg) {
        if (ILiveConstants.NO_ERR == errCode) {
            DlgMgr.showToast(getContext(), getResources().getString(fakeR.getId("string", "str_feedback_ret")));
        }else{
            DlgMgr.showMsg(getContext(), "upload failed:" + module + "|" + errCode + "|" + errMsg);
        }
    }

    @Override
    public void onTipsInfo(String tips) {
        ((TextView) findViewById(fakeR.getId("id", "tv_status"))).setText(tips);
    }

    @Override
    public void onSendMessageSuccess(ILiveTextMessage message) {
        chatMsg.add(message);
        notifyChatUpdate();
        changeChatStatus(false);
    }

    @Override
    public void onSendMessageFailed(String module, int errCode, String errMsg) {
        DlgMgr.showToast(getContext(), "SendMsg: " + module + "|" + errCode + "|" + errMsg);
        changeChatStatus(false);
    }

    public void onNewMessage(ILiveMessage message) {
        ILiveLog.ki(TAG, "onNewMessage", new ILiveLog.LogExts().put("msgType", message.getMsgType()).put("conversationType",
                message.getConversationType()).put("sender", message.getSender()));
        switch (message.getMsgType()) {
            case ILiveMessage.ILIVE_MSG_TYPE_TEXT:
                // 文本消息
                chatMsg.add(message);
                notifyChatUpdate();
                break;
            case ILiveMessage.ILIVE_MSG_TYPE_CUSTOM:
                // 自定义消息
                ILiveCustomMessage customMessage = (ILiveCustomMessage) message;
                String strExt = new String(customMessage.getExts());
                if (strExt.equals(Constants.EXT_TEXT)) {
                    try {
                        JSONTokener jsonTokener = new JSONTokener(customMessage.getDesc());
                        JSONObject msgJson = (JSONObject) jsonTokener.nextValue();
                        ILiveTextMessage textMessage = new ILiveTextMessage(new String(customMessage.getData()));
                        textMessage.setSender(msgJson.getString(BussinessConstants.JSON_NICKNAME));
                        chatMsg.add(textMessage);
                        notifyChatUpdate();
                    } catch (Exception e) {
                    }
                }
                break;
            default:
                ILiveLog.w(TAG, "onNewMessage-> message type: " + message.getMsgType());
                break;
        }
    }

    private Context getContext() {
        return this;
    }


    private LinearLayout initClickableLayout(int resId) {
        LinearLayout layout = (LinearLayout) findViewById(resId);
        layout.setOnClickListener(this);
        return layout;
    }

    private void changeChatStatus(boolean enable) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        bChatEnable = enable;
        if (bChatEnable) {
            etMsg.setVisibility(View.VISIBLE);
            etMsg.requestFocus();
            //打开软键盘
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            //关闭软键盘
            imm.hideSoftInputFromWindow(etMsg.getWindowToken(), 0);
            etMsg.clearFocus();
            etMsg.setVisibility(View.GONE);
        }
    }

    private void initView() {
        setContentView(fakeR.getId("layout", "room_activity"));
        avRootView = (AVRootView) findViewById(fakeR.getId("id", "av_root_view"));
        tvRoomName = (TextView) findViewById(fakeR.getId("id", "tv_room_name"));
        tvRoomId = (TextView) findViewById(fakeR.getId("id", "tv_room_id"));
        initClickableLayout(fakeR.getId("id", "ll_chat"));
        initClickableLayout(fakeR.getId("id", "ll_switch"));
        initClickableLayout(fakeR.getId("id", "ll_beauty"));
        initClickableLayout(fakeR.getId("id", "ll_voice"));
        initClickableLayout(fakeR.getId("id", "ll_log"));
        initClickableLayout(fakeR.getId("id", "ll_role"));
        initClickableLayout(fakeR.getId("id", "ll_feedback"));
        ivSwitch = (ImageView) findViewById(fakeR.getId("id", "iv_switch"));
        ivBeauty = (ImageView) findViewById(fakeR.getId("id", "iv_beauty"));
        ivMic = (ImageView) findViewById(fakeR.getId("id", "iv_mic"));
        ivLog = (ImageView) findViewById(fakeR.getId("id", "iv_log"));
        lvChatMsg = (MsgListView) findViewById(fakeR.getId("id", "lv_chat_msg"));
        etMsg = (EditText) findViewById(fakeR.getId("id", "et_msg"));
        etMsg.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    //处理事件
                    if (TextUtils.isEmpty(v.getText().toString())) {
                        return false;
                    }
                    SDKHelper.getInstance().sendGroupMessage(v.getText().toString());
                    v.setText("");
                }
                return false;
            }
        });

        tvRoomId.setText("" + UserInfo.getInstance().getCurRoomId());
    }

    // 角色对话框
    private RadioGroupDialog roleDialog;

    private void initRoleDialog() {
        final String[] roles = getResources().getStringArray(fakeR.getId("array", "roleinfos"));
        final String[] values = getResources().getStringArray(fakeR.getId("array", "roles"));
        roleDialog = new RadioGroupDialog(this, roles);
        roleDialog.setTitle(fakeR.getId("string", "str_set_role"));
        roleDialog.setSelected(2);
        roleDialog.setOnItemClickListener(new RadioGroupDialog.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                SDKHelper.getInstance().changeRole(values[position]);
            }
        });
    }

    // 反馈对话框
    private RadioGroupDialog feedDialog;

    private void initFeedBackDialog() {
        final String[] problems = getResources().getStringArray(fakeR.getId("array", "feedback"));
        feedDialog = new RadioGroupDialog(this, problems);
        feedDialog.setTitle(fakeR.getId("string", "str_set_problem"));
        feedDialog.setOnItemClickListener(new RadioGroupDialog.onItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                feedDialog.dismiss();
                if (position == 3) {
                    showInputDialog();
                } else {
                    SDKHelper.getInstance().uploadProblem(problems[position]);
                }
            }
        });
    }

    private AlertDialog inputDlg;
    private void showInputDialog() {
        if (null != inputDlg && inputDlg.isShowing()){
            inputDlg.dismiss();
            inputDlg = null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), fakeR.getId("style", "full_dlg"));
        builder.setTitle(fakeR.getId("string", "str_problem_other"));

        final EditText input = new EditText(getContext());
        input.setSingleLine(false);
        input.setTextColor(Color.BLACK);
        builder.setView(input);

        builder.setPositiveButton("提交", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SDKHelper.getInstance().uploadProblem(input.getText().toString());
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        inputDlg = builder.create();
        inputDlg.setCanceledOnTouchOutside(true);
        inputDlg.show();
    }

    private void changeLogStatus(boolean enable){
        bLogEnable = enable;
        findViewById(fakeR.getId("id", "tv_status")).setVisibility(bLogEnable ? View.VISIBLE : View.GONE);
        ivLog.setImageResource(bLogEnable ? fakeR.getId("mipmap", "log2") : fakeR.getId("mipmap", "log"));

        SDKHelper.getInstance().enableTipsInfo(bLogEnable);
    }

    private void notifyChatUpdate(){
        msgAdapter.notifyDataSetChanged();
    }
}
