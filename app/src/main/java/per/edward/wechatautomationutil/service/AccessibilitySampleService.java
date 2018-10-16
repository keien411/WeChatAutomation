package per.edward.wechatautomationutil.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import per.edward.wechatautomationutil.utils.Constant;
import per.edward.wechatautomationutil.utils.LogUtil;

/**
 * Created by Edward on 2018-01-30.
 */
@TargetApi(18)
public class AccessibilitySampleService extends AccessibilityService {
    private final int TEMP = 1000;

    private int process = 0;//进行到哪一步的标时

    private AccessibilityNodeInfo accessibilityNodeInfo;


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        LogUtil.e("服务onServiceConnected!");
        process = 0;
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final SharedPreferences sharedPreferences = getSharedPreferences(Constant.WECHAT_STORAGE, Activity.MODE_MULTI_PROCESS);
        final int action = sharedPreferences.getInt(Constant.ACTION, 0);

        int eventType = event.getEventType();
        LogUtil.e(eventType + "             " + Integer.toHexString(eventType) + "         " + event.getClassName());

        accessibilityNodeInfo = getRootInActiveWindow();
//        accessibilityNodeInfo = AccessibilityWindowInfo.getRoot();
        switch (eventType) {
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                if (event.getClassName().equals("android.widget.ListView")) {
//                    clickCircleOfFriendsBtn();//点击发送朋友圈按钮
                }

                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:

                if (event.getClassName().equals("com.tencent.mm.ui.LauncherUI")) {//第一次启动app
                    switch (action){
                        case 0:
                            process = Constant.NAME;
                            jumpToMy();//跳进我的界面
                            break;
                        case 1:
                            jumpToMy();//跳进我的界面
                            break;
                        case 2:
                            jumpToFind();//进入发现页面
                            break;
                    }
                }

                if (event.getClassName().equals("android.widget.FrameLayout")) {//进入朋友圈页面

                    switch (action){
                        case 0:
                            break;
                        case 1:
                            break;
                        case 2:
                            if (process == Constant.CYCLE){
                                jumpToCircleOfFriends();//进入朋友圈页面
                            }
                            break;
                    }
                }

                if (event.getClassName().equals("com.tencent.mm.plugin.sns.ui.SnsUploadUI")) {
                    if (process == Constant.CYCLE){
                        String content = sharedPreferences.getString(Constant.CONTENT, "");
                        inputContentFinish(content);//写入要发送的朋友圈内容
                    }
                }

                if (event.getClassName().equals("com.tencent.mm.plugin.gallery.ui.AlbumPreviewUI")) {

                    if (accessibilityNodeInfo == null){
                        accessibilityNodeInfo = event.getSource();
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (sharedPreferences != null) {

                                switch (action){
                                    case 0:
                                        break;
                                    case 1:

                                        if (process == Constant.HEADPIC){
                                            int index2 = sharedPreferences.getInt(Constant.INDEXBYPHOTO, 0);
                                            choosePicture(index2, 1,action);
                                        }

                                        break;
                                    case 2:

                                        if (process == Constant.CYCLE){
                                            int index = sharedPreferences.getInt(Constant.INDEX, 0);
                                            int count = sharedPreferences.getInt(Constant.COUNT, 0);
                                            choosePicture(index, count,action);
                                        }

                                        if (process == Constant.SUCCESS){
                                            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);//返回
                                        }

                                        break;
                                }

//                                LogUtil.e("AlbumPreviewUI 2"+accessibilityNodeInfo);
                            }
                        }
                    }, TEMP);
                }

                if (event.getClassName().equals("com.tencent.mm.plugin.setting.ui.setting.SettingsPersonalInfoUI")){
                    switch (action){
                        case 0:
                            if (process == Constant.NAME){
                                jumpToPersionName();//跳进昵称界面
                            }
                            break;
                        case 1:
                            if (process == Constant.HEADPIC){
                                jumpToPersionPic();//进入头像
                            }
                            break;
                        case 2:
                            if (process == Constant.CYCLE){
                                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);//返回
                            }
                            break;
                    }
                }

                if (event.getClassName().equals("com.tencent.mm.plugin.setting.ui.setting.SettingsModifyNameUI")){
                    if (process == Constant.NAME){
                        String content2 = sharedPreferences.getString(Constant.CONTENTBYNAME, "");
                        inputContentFinishByName(content2);//修改的名称
                    }

                }

                if (event.getClassName().equals("com.tencent.mm.ui.tools.CropImageNewUI")){
                    Log.d("TAGGOD", "onAccessibilityEvent: 进入剪切 process   "+process);
                    if (process == Constant.HEADPIC){
                        Log.d("TAGGOD", "onAccessibilityEvent: 进入剪切");
                        finishPersionPic();//剪切头像
                    }
                }



                break;

                case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:

                    switch (action){
                        case 0:

                            if (process == Constant.NAME){
                                jumpToPersionMession();//跳进我的信息界面

                            }


                            break;
                        case 1:

                            if (process == Constant.HEADPIC){
                                jumpToPersionMession();//跳进我的信息界面
                            }

                            break;
                        case 2:
                            if (process == Constant.CYCLE){
                                jumpToCircleOfFriends();
                                jumpToTakePhoto();
                            }

                            break;
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
     * 跳进我的界面
     */
    private void jumpToMy() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<AccessibilityNodeInfo> list = accessibilityNodeInfo.findAccessibilityNodeInfosByText("我");
                LogUtil.e("jumpToMy 1 "+list.size());
                if (list != null && list.size() != 0) {
                    LogUtil.e("jumpToMy 2");
                    AccessibilityNodeInfo tempInfo = list.get(0);
                    if (tempInfo != null && tempInfo.getParent() != null) {
                        tempInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            }
        }, TEMP);
    }

    /**
     * 跳进个人信息界面
     */
    private void jumpToPersionMession() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (accessibilityNodeInfo == null){
                    return;
                }
                List<AccessibilityNodeInfo> list = accessibilityNodeInfo.findAccessibilityNodeInfosByText("微信号：");
                LogUtil.e("jumpToPersionMession 1 "+list.size());
                if (list != null && list.size() != 0) {
                    LogUtil.e("jumpToPersionMession 2");
                    AccessibilityNodeInfo tempInfo = list.get(0);
                    if (tempInfo != null && tempInfo.getParent() != null) {
                        tempInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            }
        }, TEMP);
    }

    /**
     * 跳进昵称界面
     */
    private void jumpToPersionName() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (accessibilityNodeInfo == null){
                    return;
                }
                List<AccessibilityNodeInfo> list = accessibilityNodeInfo.findAccessibilityNodeInfosByText("昵称");
                LogUtil.e("jumpToPersionMession 1 "+list.size());
                if (list != null && list.size() != 0) {
                    LogUtil.e("jumpToPersionMession 2");
                    AccessibilityNodeInfo tempInfo = list.get(0);
                    if (tempInfo != null && tempInfo.getParent() != null) {
                        tempInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);

                    }
                }
            }
        }, TEMP);
    }

    /**
     * 跳进头像界面
     */
    private void jumpToPersionPic() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (accessibilityNodeInfo == null){
                    return;
                }
                List<AccessibilityNodeInfo> list = accessibilityNodeInfo.findAccessibilityNodeInfosByText("头像");
                LogUtil.e("jumpToPersionPic 1 "+list.size());
                if (list != null && list.size() != 0) {
                    LogUtil.e("jumpToPersionPic 2");
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

            //粘贴的效果
//            clipboard.setPrimaryClip(clip);
//            tempInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
//            tempInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);


            //直接设置文本 （全选 =》 粘贴的效果）
            Bundle arguments = new Bundle();
            arguments.putCharSequence(
                    AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    contentStr);

            tempInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

            return true;
        }
        return false;
    }

    private boolean sendMsg() {
        List<AccessibilityNodeInfo> list = accessibilityNodeInfo.findAccessibilityNodeInfosByText("发表");//微信6.6.6版本修改为发表
        if (performClickBtn(list)) {
            process = Constant.SUCCESS;
            return true;
        }
        return false;
    }

    private boolean saveMsg() {
        List<AccessibilityNodeInfo> list = accessibilityNodeInfo.findAccessibilityNodeInfosByText("保存");//微信6.6.6版本修改为发表
        if (performClickBtn(list)) {

            SharedPreferences sharedPreferences = getSharedPreferences(Constant.WECHAT_STORAGE, Activity.MODE_MULTI_PROCESS);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(Constant.ACTION, 1);

            if (editor.commit()) {
                process = Constant.HEADPIC;
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            } else {
                Toast.makeText(getBaseContext(), "保存失败", Toast.LENGTH_LONG).show();
            }


            return true;
        }
        return false;
    }

    //剪切头像
    private boolean finishPersionPic(){
        List<AccessibilityNodeInfo> list = accessibilityNodeInfo.findAccessibilityNodeInfosByText("使用");//微信6.6.6版本修改为发表
        if (performClickBtn(list)) {
            Log.d("TAGGOD", "onAccessibilityEvent: 进入剪切1");
            SharedPreferences sharedPreferences = getSharedPreferences(Constant.WECHAT_STORAGE, Activity.MODE_MULTI_PROCESS);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(Constant.ACTION, 2);

            if (editor.commit()) {
                Log.d("TAGGOD", "onAccessibilityEvent: 进入剪切2");
                process = Constant.CYCLE;
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            } else {
                Toast.makeText(getBaseContext(), "保存失败", Toast.LENGTH_LONG).show();
            }

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
     * 修改昵称
     *
     * @param contentStr
     */
    private void inputContentFinishByName(final String contentStr) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (accessibilityNodeInfo == null) {
                    return;
                }
                List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText("好名字可以让你的朋友更容易记住你。");
                if (nodeInfoList == null ||
                        nodeInfoList.size() == 0 ||
                        nodeInfoList.get(0) == null ||
                        nodeInfoList.get(0).getParent() == null ) {
                    return;
                }
                AccessibilityNodeInfo tempInfo = nodeInfoList.get(0).getParent().getChild(0);//微信6.6.6
                if (pasteContent(tempInfo, contentStr)) {
                    saveMsg();
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
    private void choosePicture(final int startPicIndex, final int picCount, final int action) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtil.e("choosePicture 1 =》 "+ startPicIndex +" " +picCount+" "+accessibilityNodeInfo);
                if (accessibilityNodeInfo == null) {
                    return;
                }
                List<AccessibilityNodeInfo> accessibilityNodeInfoList = new ArrayList<>();


                switch (action){
                    case 1:
                        accessibilityNodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText("拍摄照片");
                        break;
                    case 2:
                        accessibilityNodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText("预览");
                        break;
                }
                LogUtil.e("choosePicture 2  "+accessibilityNodeInfoList.size()+" aaaaaa  "+accessibilityNodeInfoList);

//                List<AccessibilityNodeInfo> accessibilityNodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/dpf");



                switch (action){
                    case 1:
                        if (accessibilityNodeInfoList == null ||
                                accessibilityNodeInfoList.size() == 0 ) {
                            monitor();
                            return;
                        }
                        AccessibilityNodeInfo tempInfo2 = accessibilityNodeInfoList.get(0).getParent().getParent();
                        LogUtil.e("choosePicture 3 "+tempInfo2.getChildCount());

                        LogUtil.e("choosePicture 4 "+(startPicIndex+1)+"  "+(startPicIndex + picCount));
                        for (int i = startPicIndex+1; i < startPicIndex+1 + picCount; i++) {
                            AccessibilityNodeInfo childNodeInfo = tempInfo2.getChild(i);
                            LogUtil.e("choosePicture 4"+childNodeInfo);
                            if (childNodeInfo != null) {
                                LogUtil.e("choosePicture 5 "+childNodeInfo.getChildCount());
                                if (childNodeInfo.isEnabled() && childNodeInfo.isClickable()) {

                                    childNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);//选中图片
                                }
                            }
                        }

                        break;
                    case 2:
                        if (accessibilityNodeInfoList == null ||
                                accessibilityNodeInfoList.size() == 0 ||
                                accessibilityNodeInfoList.get(0).getParent() == null ||
                                accessibilityNodeInfoList.get(0).getParent().getChildCount() == 0) {
                            monitor();
                            return;
                        }
                        AccessibilityNodeInfo tempInfo = accessibilityNodeInfoList.get(0).getParent().getChild(3);
                        LogUtil.e("choosePicture 3"+tempInfo);
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
                        break;
                }





            }
        }, TEMP);
    }

    public void monitor() {
//        SystemClock.sleep(TEMP);
//        performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
        SystemClock.sleep(TEMP);
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
        againWeixin();
    }

    /**
     * 点击系统菜单键，再次进入微信
     */
    private void againWeixin() {
        SystemClock.sleep(TEMP);
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        LogUtil.e("againWeixin 1 "+nodeInfo);
        LogUtil.e("againWeixin 3 "+getWindows());
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> infos = nodeInfo.findAccessibilityNodeInfosByViewId("com.android.systemui:id/recents_view");
            LogUtil.e("againWeixin 2 "+infos);
            if (infos != null && infos.size() > 0) {
                AccessibilityNodeInfo clearBtn = infos.get(0).getChild(0);
                clearBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
            nodeInfo.recycle();
        }
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
