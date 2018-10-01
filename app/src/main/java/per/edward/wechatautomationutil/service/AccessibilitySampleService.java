package per.edward.wechatautomationutil.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.util.List;

import per.edward.wechatautomationutil.utils.Constant;
import per.edward.wechatautomationutil.utils.LogUtil;

/**
 * Created by Edward on 2018-01-30.
 */
@TargetApi(18)
public class AccessibilitySampleService extends AccessibilityService {
    private final int TEMP = 2000;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        LogUtil.e("服务onServiceConnected!");
        flag = false;
    }

    private AccessibilityNodeInfo accessibilityNodeInfo;

    /**
     * 是否已经发送过朋友圈，true已经发送，false还未发送
     */
    public static boolean flag = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final SharedPreferences sharedPreferences = getSharedPreferences(Constant.WECHAT_STORAGE, Activity.MODE_MULTI_PROCESS);
        int eventType = event.getEventType();
        LogUtil.e(eventType + "             " + Integer.toHexString(eventType) + "         " + event.getClassName());

        accessibilityNodeInfo = getRootInActiveWindow();
//        accessibilityNodeInfo = AccessibilityWindowInfo.getRoot();
        switch (eventType) {
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                if (!flag && event.getClassName().equals("android.widget.ListView")) {
//                    clickCircleOfFriendsBtn();//点击发送朋友圈按钮
                }

                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:

                if (event.getClassName().equals("com.tencent.mm.ui.LauncherUI")) {//第一次启动app
                    flag = false;
                    jumpToFind();//进入发现页面
                }

                if (event.getClassName().equals("android.widget.FrameLayout")) {//进入发现页面
                    flag = false;
                    jumpToCircleOfFriends();//进入朋友圈页面
                }

                if (!flag && event.getClassName().equals("com.tencent.mm.plugin.sns.ui.SnsUploadUI")) {
                    String content = sharedPreferences.getString(Constant.CONTENT, "");
                    inputContentFinish(content);//写入要发送的朋友圈内容
                }

                if (!flag && event.getClassName().equals("com.tencent.mm.plugin.gallery.ui.AlbumPreviewUI")) {
                    LogUtil.e("AlbumPreviewUI 1 "+accessibilityNodeInfo);
                    LogUtil.e("window 1 "+getWindows().size());
                    LogUtil.e("getSource 1 "+event.getSource());

                    if (accessibilityNodeInfo == null){
                        accessibilityNodeInfo = event.getSource();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (sharedPreferences != null) {
                                int index = sharedPreferences.getInt(Constant.INDEX, 0);
                                int count = sharedPreferences.getInt(Constant.COUNT, 0);
                                choosePicture(index, count);
                                LogUtil.e("AlbumPreviewUI 2"+accessibilityNodeInfo);
                            }
                        }
                    }, TEMP);
                }

                break;

                case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                    if (!flag){
                        jumpToCircleOfFriends();
                        jumpToTakePhoto();
                    }

                    break;
        }
    }

    /**
     * 跳进朋友圈
     */
    private void jumpToCircleOfFriends() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (accessibilityNodeInfo == null){
                    return;
                }
                List<AccessibilityNodeInfo> list = accessibilityNodeInfo.findAccessibilityNodeInfosByText("朋友圈");
                LogUtil.e("jumpToCircleOfFriends1 "+list.size());
                if (list != null && list.size() != 0) {
                    LogUtil.e("jumpToCircleOfFriends2");
                    AccessibilityNodeInfo tempInfo = list.get(0);
                    if (tempInfo != null && tempInfo.getParent() != null) {
                        tempInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            }
        }, TEMP);
    }

    /**
     * 跳进发现
     */
    private void jumpToFind() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<AccessibilityNodeInfo> list = accessibilityNodeInfo.findAccessibilityNodeInfosByText("发现");
                LogUtil.e("jumpToFind1 "+list.size());
                if (list != null && list.size() != 0) {
                    LogUtil.e("jumpToFind2");
                    AccessibilityNodeInfo tempInfo = list.get(0);
                    if (tempInfo != null && tempInfo.getParent() != null) {
                        tempInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            }
        }, TEMP);
    }

    /**
     * 跳进拍照分享
     */
    private void jumpToTakePhoto() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (accessibilityNodeInfo == null){
                    return;
                }
                List<AccessibilityNodeInfo> list = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/j1");
//                List<AccessibilityNodeInfo> list2 = accessibilityNodeInfo.findAccessibilityNodeInfosByText("拍照分享");
                LogUtil.e("jumpToTakePhoto 1 "+list.size());
                if (list != null && list.size() != 0) {
                    AccessibilityNodeInfo tempInfo = list.get(0);
                    LogUtil.e("jumpToTakePhoto 2"+ tempInfo);
                    if (tempInfo != null && tempInfo.getParent() != null) {
                        tempInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        openAlbum();
                    }
                }
            }
        }, TEMP);
    }

    /**
     * 粘贴文本
     *
     * @param tempInfo
     * @param contentStr
     * @return true 粘贴成功，false 失败
     */
    private boolean pasteContent(AccessibilityNodeInfo tempInfo, String contentStr) {
        if (tempInfo == null) {
            return false;
        }
        if (tempInfo.isEnabled() && tempInfo.isClickable() && tempInfo.isFocusable()) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("text", contentStr);
            if (clipboard == null) {
                return false;
            }
            clipboard.setPrimaryClip(clip);
            tempInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            tempInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
            return true;
        }
        return false;
    }

    private boolean sendMsg() {
        List<AccessibilityNodeInfo> list = accessibilityNodeInfo.findAccessibilityNodeInfosByText("发表");//微信6.6.6版本修改为发表
        if (performClickBtn(list)) {
            flag = true;//标记为已发送
            return true;
        }
        return false;
    }

    /**
     * 写入朋友圈内容
     *
     * @param contentStr
     */
    private void inputContentFinish(final String contentStr) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (accessibilityNodeInfo == null) {
                    return;
                }
                List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText("添加照片按钮");
                if (nodeInfoList == null ||
                        nodeInfoList.size() == 0 ||
                        nodeInfoList.get(0) == null ||
                        nodeInfoList.get(0).getParent() == null ||
                        nodeInfoList.get(0).getParent().getParent() == null ||
                        nodeInfoList.get(0).getParent().getParent().getParent() == null ||
                        nodeInfoList.get(0).getParent().getParent().getParent().getChildCount() == 0) {
                    return;
                }
                AccessibilityNodeInfo tempInfo = nodeInfoList.get(0).getParent().getParent().getParent().getChild(1);//微信6.6.6
                if (pasteContent(tempInfo, contentStr)) {
                    sendMsg();
                }
            }
        }, TEMP);
    }

    /**
     * @param accessibilityNodeInfoList
     * @return
     */
    private boolean performClickBtn(List<AccessibilityNodeInfo> accessibilityNodeInfoList) {
        if (accessibilityNodeInfoList != null && accessibilityNodeInfoList.size() != 0) {
            for (int i = 0; i < accessibilityNodeInfoList.size(); i++) {
                AccessibilityNodeInfo accessibilityNodeInfo = accessibilityNodeInfoList.get(i);
                if (accessibilityNodeInfo != null) {
                    if (accessibilityNodeInfo.isClickable() && accessibilityNodeInfo.isEnabled()) {
                        accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 选择图片
     *
     * @param startPicIndex 从第startPicIndex张开始选
     * @param picCount      总共选picCount张
     */
    private void choosePicture(final int startPicIndex, final int picCount) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtil.e("choosePicture 1 =》 "+ startPicIndex +" " +picCount+" "+accessibilityNodeInfo);
                if (accessibilityNodeInfo == null) {
                    return;
                }
                LogUtil.e("choosePicture 2");
                List<AccessibilityNodeInfo> accessibilityNodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText("预览");
//                List<AccessibilityNodeInfo> accessibilityNodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/dpf");

                if (accessibilityNodeInfoList == null ||
                        accessibilityNodeInfoList.size() == 0 ||
                        accessibilityNodeInfoList.get(0).getParent() == null ||
                        accessibilityNodeInfoList.get(0).getParent().getChildCount() == 0) {
                    return;
                }
                LogUtil.e("choosePicture 3");
                AccessibilityNodeInfo tempInfo = accessibilityNodeInfoList.get(0).getParent().getChild(3);

                for (int j = startPicIndex; j < startPicIndex + picCount; j++) {
                    AccessibilityNodeInfo childNodeInfo = tempInfo.getChild(j);
                    if (childNodeInfo != null) {
                        for (int k = 0; k < childNodeInfo.getChildCount(); k++) {
                            if (childNodeInfo.getChild(k).isEnabled() && childNodeInfo.getChild(k).isClickable()) {
                                childNodeInfo.getChild(k).performAction(AccessibilityNodeInfo.ACTION_CLICK);//选中图片
                            }
                        }
                    }
                }


                LogUtil.e("choosePicture 4");
                List<AccessibilityNodeInfo> finishList = accessibilityNodeInfo.findAccessibilityNodeInfosByText("完成(" + picCount + "/9)");//点击确定
                performClickBtn(finishList);
            }
        }, TEMP);
    }


    /**
     * 点击发送朋友圈按钮
     */
    private void clickCircleOfFriendsBtn() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (accessibilityNodeInfo == null) {
                    return;
                }

                List<AccessibilityNodeInfo> accessibilityNodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText("更多功能按钮");
                performClickBtn(accessibilityNodeInfoList);
                openAlbum();
            }
        }, TEMP);
    }


    /**
     * 打开相册
     */
    private void openAlbum() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (accessibilityNodeInfo == null) {
                    return;
                }

                List<AccessibilityNodeInfo> accessibilityNodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText("从相册选择");
                traverseNode(accessibilityNodeInfoList);
            }
        }, TEMP);
    }

    private boolean traverseNode(List<AccessibilityNodeInfo> accessibilityNodeInfoList) {
        if (accessibilityNodeInfoList != null && accessibilityNodeInfoList.size() != 0) {
            AccessibilityNodeInfo accessibilityNodeInfo = accessibilityNodeInfoList.get(0).getParent();
            if (accessibilityNodeInfo != null && accessibilityNodeInfo.getChildCount() != 0) {
                accessibilityNodeInfo = accessibilityNodeInfo.getChild(0);
                if (accessibilityNodeInfo != null) {
                    accessibilityNodeInfo = accessibilityNodeInfo.getParent();
                    if (accessibilityNodeInfo != null) {
                        accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);//点击从相册中选择
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Override
    public void onInterrupt() {

    }


    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.  The
     * service should clean up any resources it holds (threads, registered
     * receivers, etc) at this point.  Upon return, there will be no more calls
     * in to this Service object and it is effectively dead.  Do not call this method directly.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.e("服务被杀死!");
    }
}
