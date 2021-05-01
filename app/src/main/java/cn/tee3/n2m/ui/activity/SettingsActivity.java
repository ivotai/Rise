package cn.tee3.n2m.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.unicorn.rise.R;

import cn.tee3.avd.AVDEngine;
import cn.tee3.avd.MVideo;
import cn.tee3.n2m.N2MApplication;
import cn.tee3.n2m.ui.fragment.SettingsDialog;
import cn.tee3.n2m.ui.util.N2MSetting;

/**
 * 设置
 * Created by shengf on 2017/7/31.
 */

public class SettingsActivity extends Activity implements View.OnClickListener {
    private TextView tvSDKversion;
    private EditText etServerUrl;
    private RelativeLayout rlUIStyle;
    private RelativeLayout rlVideoResolution;
    private RelativeLayout rlLogLevel;
    private RelativeLayout rlAdvancedSetting;
    private TextView tvUIStyle;
    private TextView tvVideoResolution;
    private TextView tvLogLevel;

    private String serverUrlStr = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        createView();
        initView();
    }

    private void createView() {
        tvSDKversion = (TextView) findViewById(R.id.tv_sdkversion);
        etServerUrl = (EditText) findViewById(R.id.et_serverurl);
        rlUIStyle = (RelativeLayout) findViewById(R.id.rl_ui_style);
        rlVideoResolution = (RelativeLayout) findViewById(R.id.rl_video_resolution);
        rlLogLevel = (RelativeLayout) findViewById(R.id.rl_log_level);
        rlAdvancedSetting = (RelativeLayout) findViewById(R.id.rl_advanced_setting);
        tvUIStyle = (TextView) findViewById(R.id.tv_ui_style);
        tvVideoResolution = (TextView) findViewById(R.id.tv_video_resolution);
        tvLogLevel = (TextView) findViewById(R.id.tv_log_level);

        rlUIStyle.setOnClickListener(this);
        rlVideoResolution.setOnClickListener(this);
        rlLogLevel.setOnClickListener(this);
        rlAdvancedSetting.setOnClickListener(this);
    }

    private void initView() {
        serverUrlStr = N2MSetting.getInstance().getServerUrl();
        tvSDKversion.setText("(" + AVDEngine.instance().getVersion() + ")");
        etServerUrl.setText(serverUrlStr);
        //界面风格
        if (0 == N2MSetting.getInstance().getUIStype()) {
            tvUIStyle.setText(R.string.uistyle_portrait);
        } else {
            tvUIStyle.setText(R.string.uistyle_landscape);
        }
        //视频分辨率
        int videoResolution = N2MSetting.getInstance().getVideoResolution();
        if (0 == videoResolution) {
            tvVideoResolution.setText(R.string.videoresolution_320);
        } else if (1 == videoResolution) {
            tvVideoResolution.setText(R.string.videoresolution_640);
        } else {
            tvVideoResolution.setText(R.string.videoresolution_1280);
        }
        //日志等级
        int logLevel = N2MSetting.getInstance().getLogLevel();
        if (0 == logLevel) {
            tvLogLevel.setText(R.string.loglevel_verbose);
        } else if (1 == logLevel) {
            tvLogLevel.setText(R.string.loglevel_info);
        } else {
            tvLogLevel.setText(R.string.loglevel_warning);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_ui_style:
                SettingsDialog.selectUIStyleDialog(this, new SettingsDialog.MCallBack() {
                    @Override
                    public boolean OnCallBackStr(String messageStr) {
                        tvUIStyle.setText(messageStr);
                        return false;
                    }
                });
                break;
            case R.id.rl_video_resolution:
                SettingsDialog.videoResolutionDialog(this, new SettingsDialog.MCallBack() {
                    @Override
                    public boolean OnCallBackStr(String messageStr) {
                        tvVideoResolution.setText(messageStr);
                        return false;
                    }
                });
                break;
            case R.id.rl_log_level:
                SettingsDialog.logLevelDialog(this, new SettingsDialog.MCallBack() {
                    @Override
                    public boolean OnCallBackStr(String messageStr) {
                        tvLogLevel.setText(messageStr);
                        return false;
                    }
                });
                break;
            case R.id.rl_advanced_setting:
                Intent intent = new Intent(SettingsActivity.this, AdvancedSettingsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        boolean changedUrl = false;
        if (!serverUrlStr.equals(etServerUrl.getText().toString())) {
            N2MSetting.getInstance().saveServerUrl(etServerUrl.getText().toString());
            changedUrl = true;
        }
        //横屏状态下禁用自动旋转
        if (1 == N2MSetting.getInstance().getUIStype()) {
            N2MSetting.getInstance().saveVideoAutoRotation(false);
            MVideo.setAutoRotation(false);
        }

        N2MSetting.getInstance().saveCommit();

        ((N2MApplication) getApplication()).resetLogParams();

        if (changedUrl) {
            ((N2MApplication) getApplication()).reInitAVDEngine();
        } else {
            AVDEngine.instance().setOption(AVDEngine.Option.eo_camera_capability_default, N2MSetting.getInstance().getVideoResolutionOption());
            AVDEngine.instance().setOption(AVDEngine.Option.eo_video_codec_priority, N2MSetting.getInstance().getVideoCodecOption());
            AVDEngine.instance().setOption(AVDEngine.Option.eo_video_codec_hw_priority, N2MSetting.getInstance().getVideoCodecHWPriority());
            AVDEngine.instance().setOption(AVDEngine.Option.eo_data_channel_tcp_priority, N2MSetting.getInstance().getDataChannelNetOption());
        }
    }
}