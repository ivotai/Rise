package cn.tee3.n2m.ui.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.unicorn.rise.ConstValue;

import java.util.UUID;

import cn.tee3.avd.AVDEngine;
import cn.tee3.avd.MVideo;

public class N2MSetting {
    // updated in archive script
    private static final String Key_serverUrl = "serverurl";
    private static final String Key_uiStype = "uiStype";
    private static final String Key_videoCodec = "videoCodec";
    private static final String Key_videoShow = "videoShow";
    private static final String Key_videoResolution = "videoResolution";
    private static final String Key_dataChannelNet = "dataChannelNet";
    private static final String Key_logLevel = "logLevel";
    private static final String Key_roomId = "roomId";
    private static final String Key_userId = "userId";
    private static final String Key_userName = "userName";
    private static final String Key_autoVideo = "autoVideo";
    private static final String Key_autoAudio = "autoAudio";
    private static final String Key_videoAutoRotation = "videoAutoRotation";
    private static final String Key_multiLive = "multiLive";
    private static final String Key_oemName = "oemName";

    private static final String Def_oemName = "qiniu.com";
    public static final String Key_DATA = "Data";

    ///
    private static N2MSetting instance = null;

    public static N2MSetting getInstance() {
        if (null == instance) {
            instance = new N2MSetting();
        }
        return instance;
    }

    private N2MSetting() {
    }

    SharedPreferences sharedPref;
    Context context;

    public void init(Context context) {
        this.context = context;
        String pkgName = context.getPackageName();
        this.sharedPref = context.getSharedPreferences(pkgName, Activity.MODE_PRIVATE);
    }

    public boolean isOEMTest() {
        return false;
    }

    public String getOEMName() {
        return this.sharedPref.getString(Key_serverUrl, Def_oemName);
    }

    public boolean isAutoAudio() {
        return this.sharedPref.getBoolean(Key_autoAudio, true);
    }

    public boolean isAutoVideo() {
        return this.sharedPref.getBoolean(Key_autoVideo, true);
    }

    public boolean isVideoAutoRotation() {
        return this.sharedPref.getBoolean(Key_videoAutoRotation, false);
    }

    public boolean isMultiLive() {
        return this.sharedPref.getBoolean(Key_multiLive, false);
    }

    public String getServerUrl() {
        return ConstValue.serverUrl;
    }

    public int getUIStype() {
        return this.sharedPref.getInt(Key_uiStype, 0);
    }

    public int getVideoCodec() {
        //支持h264硬编的时候，默认用h264硬编，不支持的时候默认h264软编
        String sh264hw = AVDEngine.instance().getOption(AVDEngine.Option.eo_video_codec_support_h264hw);
        if (sh264hw.equals("true")) {
            return this.sharedPref.getInt(Key_videoCodec, 2);
        } else {
            return this.sharedPref.getInt(Key_videoCodec, 1);
        }
    }

    public int getVideoShow() {
        return this.sharedPref.getInt(Key_videoShow, 0);
    }

    public int getVideoResolution() {
        return this.sharedPref.getInt(Key_videoResolution, 1);
    }

    public int getDataChannelNet() {
        return this.sharedPref.getInt(Key_dataChannelNet, 0);
    }

    public int getLogLevel() {
        return this.sharedPref.getInt(Key_logLevel, 1);
    }

    public String getRoomId() {
        return this.sharedPref.getString(Key_roomId, "r106");//"1380390021-proxy-test");
    }

    public String getRoomToken() {
        // user2
        return "dSC2IIrmjNXONzPvVgXZo0mI0AI83835NIuXQ3iD:KLoVj_CTp7q1uxLE8kdQ0-H5VkQ=:eyJyb29tX25hbWUiOiJxaW5pdTAwMSIsInVzZXJfaWQiOiJ1c2VyMiIsInBlcm0iOiJ1c2VyIiwiZXhwaXJlX2F0IjoxNDcwOTkwMjc3MTQwOTk4MjI1fQ==";
        // user1
        // return "dSC2IIrmjNXONzPvVgXZo0mI0AI83835NIuXQ3iD:HIabkB6d3bnMrB9adR2o2WPaahk=:eyJyb29tX25hbWUiOiJxaW5pdTAwMSIsInVzZXJfaWQiOiJ1c2VyMSIsInBlcm0iOiJ1c2VyIiwiZXhwaXJlX2F0IjoxNDcwOTkwMjIwNzAxNjk4OTM1fQ==";
    }

    public String getUserId() {
        return this.sharedPref.getString(Key_userId, UUID.randomUUID().toString());
    }

    public String getUserName() {
        String userName = this.sharedPref.getString(Key_userName, "");
        if (userName.isEmpty()) {
            userName = android.os.Build.MODEL + ":" + android.os.Build.VERSION.SDK_INT + "_" + android.os.Build.VERSION.RELEASE;
        }
        return userName;
    }

    public String getVideoCodecOption() {
        String videoCodecStr = "";
        //1、3软编；2、4硬编
        switch (getVideoCodec()) {
            case 0:
                videoCodecStr = "VP8";
                break;
            case 1:
                videoCodecStr = "H264";
                break;
            case 2:
                videoCodecStr = "H264";
                break;
            case 3:
                videoCodecStr = "H265";
                break;
            case 4:
                videoCodecStr = "H265";
                break;
        }
        return videoCodecStr;
    }

    public String getVideoCodecHWPriority() {
        return (2 == getVideoCodec() || 4 == getVideoCodec()) ? "true" : "false";
    }

    public String getVideoResolutionOption() {
        int cur = getVideoResolution();
//        String cap = "{\"width\":288,\"height\":352,\"maxFPS\":30}";
//        if (2 == cur) {
//               cap = "{\"width\":720,\"height\":1280,\"maxFPS\":15}";
//        }
//        if (1 == cur) {
//               cap = "{\"width\":480,\"height\":640,\"maxFPS\":20}";
//        }
        String cap = "{\"width\":352,\"height\":288,\"maxFPS\":30}";
        if (2 == cur) {
            cap = "{\"width\":1280,\"height\":720,\"maxFPS\":15}";
        }
        if (1 == cur) {
            cap = "{\"width\":640,\"height\":480,\"maxFPS\":20}";
        }
//        当有反向分辨率时候，如：
//        width = 640,height = 480,fps = 30,rotation = 270
//        width = 480,height = 640,fps = 30,rotation = 270
//        目标355x288，会优先选择了反向分辨率480x640,270,而不是选择355x288,270;
        return cap;
    }

    public String getDataChannelNetOption() {
        return 1 == getDataChannelNet() ? "true" : "false";
    }

    public int getVideoBitrateMin() {
        int cur = getVideoResolution();
        int minbt = 798 * 1000;
        if (0 == cur) {
            minbt = 198 * 1000;
        } else if (2 == cur) {
            minbt = 998 * 1000;
        }
        return minbt;
    }

    public int getVideoBitrateMax() {
        int cur = getVideoResolution();
        int minbt = 998 * 1000;
        if (0 == cur) {
            minbt = 398 * 1000;
        } else if (2 == cur) {
            minbt = 1998 * 1000;
        }
        return minbt;
    }

    MVideo.CameraType currCameraType = MVideo.CameraType.front;

    public MVideo.CameraType getCurrCameraType() {
        return currCameraType;
    }

    public void setCurrCameraType(MVideo.CameraType value) {
        this.currCameraType = value;
    }

    ///
    SharedPreferences.Editor sharedPrefEditor;

    private void createEditor() {
        if (null == this.sharedPrefEditor) {
            this.sharedPrefEditor = this.sharedPref.edit();
        }
    }

    public void saveServerUrl(String value) {
        createEditor();
        this.sharedPrefEditor.putString(Key_serverUrl, value);
    }

    public void saveUIStype(int value) {
        createEditor();
        this.sharedPrefEditor.putInt(Key_uiStype, value);
    }

    public void saveVideoCodec(int value) {
        createEditor();
        this.sharedPrefEditor.putInt(Key_videoCodec, value);
    }

    public void saveVideoShow(int value) {
        createEditor();
        this.sharedPrefEditor.putInt(Key_videoShow, value);
    }

    public void saveVideoResolution(int value) {
        createEditor();
        this.sharedPrefEditor.putInt(Key_videoResolution, value);
    }

    public void saveDataChannelNet(int value) {
        createEditor();
        this.sharedPrefEditor.putInt(Key_dataChannelNet, value);
    }

    public void saveLogLevel(int value) {
        createEditor();
        this.sharedPrefEditor.putInt(Key_logLevel, value);
    }

    public void saveRoomId(String value) {
        createEditor();
        this.sharedPrefEditor.putString(Key_roomId, value);
    }

    public void saveUserName(String value) {
        createEditor();
        this.sharedPrefEditor.putString(Key_userName, value);
    }

    public void saveAutoAudio(boolean value) {
        createEditor();
        this.sharedPrefEditor.putBoolean(Key_autoAudio, value);
    }

    public void saveAutoVideo(boolean value) {
        createEditor();
        this.sharedPrefEditor.putBoolean(Key_autoVideo, value);
    }

    public void saveVideoAutoRotation(boolean value) {
        createEditor();
        this.sharedPrefEditor.putBoolean(Key_videoAutoRotation, value);
    }

    public void saveMultiLive(boolean value) {
        createEditor();
        this.sharedPrefEditor.putBoolean(Key_multiLive, value);
    }

    public void saveCommit() {
        if (null != this.sharedPrefEditor) {
            this.sharedPrefEditor.commit();
        }
    }

    String baseUrl;

    public String getBaseUrl() {
        if (null == this.baseUrl) {
            this.baseUrl = AVDEngine.instance().getOption(AVDEngine.Option.eo_demo_urlbase_liverecord);
            this.baseUrl = "http://" + this.baseUrl;
        }
        return this.baseUrl;
    }
}
