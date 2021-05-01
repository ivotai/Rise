package cn.tee3.n2m;

import android.app.Application;
import android.util.Log;

import com.unicorn.rise.ConstValue;
import com.unicorn.rise.R;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.tee3.avd.AVDEngine;
import cn.tee3.avd.ErrorCode;
import cn.tee3.avd.MVideo;
import cn.tee3.avd.RoomInfo;
import cn.tee3.n2m.ui.util.AppKey;
import cn.tee3.n2m.ui.util.N2MSetting;
import cn.tee3.n2m.ui.util.ToastUtil;

public class N2MApplication extends Application implements AVDEngine.Listener {
    private static final String TAG = "N2MApplication";
    static N2MApplication myself;

    static public String getTee3Dir() {
        String tee3dir = "/sdcard/cn.tee3/";
        if (isFolderExists(tee3dir)) {
            return tee3dir;
        } else {
            return "/sdcard/";
        }
    }

    static boolean isFolderExists(String strFolder) {
        File file = new File(strFolder);
        if (!file.exists()) {
            if (file.mkdir()) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        N2MSetting.getInstance().init(this);
        Log.i(TAG, "onCreate, begin init AVDEngine ");
        myself = this;

        String tee3dir = getTee3Dir();
        DateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String dumpfile = tee3dir + "nice2meet" + format.format(new Date()) + ".dump";
        AVDEngine.instance().setDumpFile(dumpfile);

        resetLogParams();

        if (N2MSetting.getInstance().isOEMTest()) {
            int ret = AVDEngine.instance().initWithOEM(getApplicationContext(), this, N2MSetting.getInstance().getServerUrl(), N2MSetting.getInstance().getOEMName(), false);
            if (ErrorCode.AVD_OK == ret) {
                onInitResult(ret);
            }
        } else {
            int ret = AVDEngine.instance().init(getApplicationContext(), this, ConstValue.serverUrl, ConstValue.appKey, ConstValue.secretKey);
            if (ErrorCode.AVD_OK != ret) {
                Log.e(TAG, "onCreate, init AVDEngine failed. ret=" + ret);
            }
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        AVDEngine.instance().uninit();
        Log.i(TAG, "onTerminate, after uninit AVDEngine ");
    }

    @Override
    public void onInitResult(int result) {
        Log.i(TAG, "onInitResult result:" + result);
        if (ErrorCode.AVD_OK != result) {
            String err = getString(R.string.avdinitfailed) + result;
            ToastUtil.showLongToast(this, err);
            return;
        }

        AVDEngine.instance().setOption(AVDEngine.Option.eo_camera_mode_frontback, "true");
        AVDEngine.instance().setOption(AVDEngine.Option.eo_video_resolution_16balign, "true");
        // from n2m setting
        AVDEngine.instance().setOption(AVDEngine.Option.eo_camera_capability_default, N2MSetting.getInstance().getVideoResolutionOption());
        AVDEngine.instance().setOption(AVDEngine.Option.eo_video_codec_priority, N2MSetting.getInstance().getVideoCodecOption());
        AVDEngine.instance().setOption(AVDEngine.Option.eo_video_codec_hw_priority, N2MSetting.getInstance().getVideoCodecHWPriority());
        AVDEngine.instance().setOption(AVDEngine.Option.eo_data_channel_tcp_priority, N2MSetting.getInstance().getDataChannelNetOption());
        //AVDEngine.instance().setOption(AVDEngine.Option.eo_mcu_cluster_route_params, "{\"ip_tag\":\"local\",\"idc_code\":\"\"}");
        MVideo.setAutoRotation(N2MSetting.getInstance().isVideoAutoRotation());
        //AVDEngine.instance().setOption(AVDEngine.Option.eo_audio_agc_PlayoutGainMultipleValue, "4.0");
        //AVDEngine.instance().setOption(AVDEngine.Option.eo_video_swapwh_by_rotation, "true");
        //AVDEngine.instance().setOption(AVDEngine.Option.eo_audio_aec_Enable, "false");
        //AVDEngine.instance().setOption(AVDEngine.Option.eo_audio_aec_DAEcho_Enable, "false");
        AVDEngine.instance().setOption(AVDEngine.Option.eo_audio_noiseSuppression_Enable, "false");
        AVDEngine.instance().setOption(AVDEngine.Option.eo_audio_highpassFilter_Enable, "false");
        //AVDEngine.instance().setOption(AVDEngine.Option.eo_audio_autoGainControl_Enable, "false");
    }

    @Override
    public void onUninitResult(int reason) {
        Log.i(TAG, "onUninitResult reason:" + reason);
    }

    @Override
    public void onGetRoomResult(int result, RoomInfo roomInfo) {

        Log.i(TAG, "onScheduleRoomResult,result=" + result + ",roomId=" + roomInfo.toString());
    }

    @Override
    public void onFindRoomsResult(int result, List<RoomInfo> list) {
    }

    @Override
    public void onScheduleRoomResult(int result, String roomId) {
        Log.i(TAG, "onScheduleRoomResult,result=" + result + ",roomId=" + roomId);
    }

    @Override
    public void onCancelRoomResult(int result, String roomId) {
    }

    public void reInitAVDEngine() {
        Log.i(getClass().getName(), "reInitAVDEngine");
        AVDEngine.instance().uninit();
        AVDEngine.instance().init(getApplicationContext(), this, N2MSetting.getInstance().getServerUrl(), AppKey.tee3_app_key, AppKey.tee3_secret_key);
    }

    String logfile;

    public void resetLogParams() {
        AVDEngine.enableLog2SDK(false);

        if (null == logfile) {
            DateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
            logfile = getTee3Dir() + "nice2meet" + format.format(new Date()) + ".log";
        }

        String params = "debug "; //server:121.41.102.126:777
        int nlevel = N2MSetting.getInstance().getLogLevel();
        Log.i(TAG, "resetLogParams: level=" + nlevel);
        if (0 == nlevel) {
            params += "verbose";
        }
        if (1 == nlevel) {
            params += "info";
        }
        if (2 == nlevel) {
            params += "warning";
        }
        AVDEngine.instance().setLogParams(params, logfile);
    }

}
