package cn.tee3.n2m.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.unicorn.rise.R;

import cn.tee3.avd.AVDEngine;
import cn.tee3.avd.MVideo;
import cn.tee3.n2m.ui.fragment.SettingsDialog;
import cn.tee3.n2m.ui.util.N2MSetting;

/**
 * 高级设置
 * Created by shengf on 2017/8/2.
 */

public class AdvancedSettingsActivity extends Activity implements View.OnClickListener {
    private RelativeLayout rlVideoCodec;
    private RelativeLayout rlDatachannelNet;
    private TextView tvVideoCodec;
    private TextView tvDatachannelNet;
    private ImageView ivAutoVideo;
    private ImageView ivAutoAudio;
    private ImageView ivVideooptionScalefull;
    private ImageView ivVideooptionAutorotation;
    private ImageView ivAudiooptionAudioAec;
    private ImageView ivAudiooptionAudioAecDae;
    private ImageView ivAudiooptionAudioAgc;

    private boolean isAutoVideo;
    private boolean isAutoAudio;
    private boolean isVideoFullScreen;
    private boolean isVideoAutoRotation;
    private boolean isAudioAec;
    private boolean isAudioAecDae;
    private boolean isAudioAgc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advanced_setting_layout);
        createView();
        initView();
    }

    private void createView() {
        rlVideoCodec = (RelativeLayout) findViewById(R.id.rl_video_codec);
        rlDatachannelNet = (RelativeLayout) findViewById(R.id.rl_datachannel_net);
        tvVideoCodec = (TextView) findViewById(R.id.tv_video_codec);
        tvDatachannelNet = (TextView) findViewById(R.id.tv_datachannel_net);
        ivAutoVideo = (ImageView) findViewById(R.id.iv_auto_video);
        ivAutoAudio = (ImageView) findViewById(R.id.iv_auto_audio);
        ivVideooptionScalefull = (ImageView) findViewById(R.id.iv_videooption_scalefull);
        ivVideooptionAutorotation = (ImageView) findViewById(R.id.iv_videooption_autorotation);
        ivAudiooptionAudioAec = (ImageView) findViewById(R.id.iv_audiooption_audio_aec);
        ivAudiooptionAudioAecDae = (ImageView) findViewById(R.id.iv_audiooption_audio_aec_dae);
        ivAudiooptionAudioAgc = (ImageView) findViewById(R.id.iv_audiooption_audio_agc);

        rlVideoCodec.setOnClickListener(this);
        rlDatachannelNet.setOnClickListener(this);
        ivAutoVideo.setOnClickListener(this);
        ivAutoAudio.setOnClickListener(this);
        ivVideooptionScalefull.setOnClickListener(this);
        ivVideooptionAutorotation.setOnClickListener(this);
        ivAudiooptionAudioAec.setOnClickListener(this);
        ivAudiooptionAudioAecDae.setOnClickListener(this);
        ivAudiooptionAudioAgc.setOnClickListener(this);
    }

    private void initView() {
        //视频编码
        int videoCodec = N2MSetting.getInstance().getVideoCodec();
        if (0 == videoCodec) {
            tvVideoCodec.setText(R.string.videocodec_vp8);
        } else if (1 == videoCodec) {
            tvVideoCodec.setText(R.string.videocodec_h264_sw);
        } else if (2 == videoCodec) {
            tvVideoCodec.setText(R.string.videocodec_h264_hw);
        } else if (3 == videoCodec) {
            tvVideoCodec.setText(R.string.videocodec_h265_sw);
        } else if (4 == videoCodec) {
            tvVideoCodec.setText(R.string.videocodec_h265_hw);
        }
        //数据通道
        int dataChannel = N2MSetting.getInstance().getDataChannelNet();
        if (0 == dataChannel) {
            tvDatachannelNet.setText(R.string.datachannel_net_udp);
        } else {
            tvDatachannelNet.setText(R.string.datachannel_net_tcp);
        }

        isAutoVideo = N2MSetting.getInstance().isAutoVideo();
        setSwitchImg(ivAutoVideo, isAutoVideo);
        isAutoAudio = N2MSetting.getInstance().isAutoAudio();
        setSwitchImg(ivAutoAudio, isAutoAudio);
        int temp = N2MSetting.getInstance().getVideoShow();
        isVideoFullScreen = (0 == temp);
        setSwitchImg(ivVideooptionScalefull, isVideoFullScreen);

        //横屏状态下禁用自动旋转
        if (1 == N2MSetting.getInstance().getUIStype()) {
            ivVideooptionAutorotation.setClickable(false);
            isVideoAutoRotation = false;
        } else {
            ivVideooptionAutorotation.setClickable(true);
            isVideoAutoRotation = N2MSetting.getInstance().isVideoAutoRotation();
        }
        setSwitchImg(ivVideooptionAutorotation, isVideoAutoRotation);

        String svalue;
        svalue = AVDEngine.instance().getOption(AVDEngine.Option.eo_audio_aec_Enable);
        isAudioAec = (svalue.equals("true"));
        setSwitchImg(ivAudiooptionAudioAec, isAudioAec);
        svalue = AVDEngine.instance().getOption(AVDEngine.Option.eo_audio_aec_DAEcho_Enable);
        isAudioAecDae = (svalue.equals("true"));
        setSwitchImg(ivAudiooptionAudioAecDae, isAudioAecDae);
        svalue = AVDEngine.instance().getOption(AVDEngine.Option.eo_audio_autoGainControl_Enable);
        isAudioAgc = (svalue.equals("true"));
        setSwitchImg(ivAudiooptionAudioAgc, isAudioAgc);
    }

    /**
     * 设置开关图片
     *
     * @param imageView 相应布局
     * @param isOpen    开关是否打开
     */
    private void setSwitchImg(ImageView imageView, boolean isOpen) {
        if (isOpen) {
            imageView.setBackgroundResource(R.drawable.switch_open);
        } else {
            imageView.setBackgroundResource(R.drawable.switch_close);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_video_codec:
                SettingsDialog.videoCodecDialog(this, new SettingsDialog.MCallBack() {
                    @Override
                    public boolean OnCallBackStr(String messageStr) {
                        tvVideoCodec.setText(messageStr);
                        return false;
                    }
                });
                break;
            case R.id.rl_datachannel_net:
                SettingsDialog.dataChannelDialog(this, new SettingsDialog.MCallBack() {
                    @Override
                    public boolean OnCallBackStr(String messageStr) {
                        tvDatachannelNet.setText(messageStr);
                        return false;
                    }
                });
                break;
            case R.id.iv_auto_video:
                isAutoVideo = !isAutoVideo;
                setSwitchImg(ivAutoVideo, isAutoVideo);
                break;
            case R.id.iv_auto_audio:
                isAutoAudio = !isAutoAudio;
                setSwitchImg(ivAutoAudio, isAutoAudio);
                break;
            case R.id.iv_videooption_scalefull:
                isVideoFullScreen = !isVideoFullScreen;
                setSwitchImg(ivVideooptionScalefull, isVideoFullScreen);
                break;
            case R.id.iv_videooption_autorotation:
                isVideoAutoRotation = !isVideoAutoRotation;
                setSwitchImg(ivVideooptionAutorotation, isVideoAutoRotation);
                break;
            case R.id.iv_audiooption_audio_aec:
                isAudioAec = !isAudioAec;
                setSwitchImg(ivAudiooptionAudioAec, isAudioAec);
                break;
            case R.id.iv_audiooption_audio_aec_dae:
                isAudioAecDae = !isAudioAecDae;
                setSwitchImg(ivAudiooptionAudioAecDae, isAudioAecDae);
                break;
            case R.id.iv_audiooption_audio_agc:
                isAudioAgc = !isAudioAgc;
                setSwitchImg(ivAudiooptionAudioAgc, isAudioAgc);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        N2MSetting.getInstance().saveAutoVideo(isAutoVideo);
        N2MSetting.getInstance().saveAutoAudio(isAutoAudio);
        if (isVideoFullScreen) {
            N2MSetting.getInstance().saveVideoShow(0);
        } else {
            N2MSetting.getInstance().saveVideoShow(1);
        }
        N2MSetting.getInstance().saveVideoAutoRotation(isVideoAutoRotation);
        String svalue;
        svalue = isAudioAec ? "true" : "false";
        AVDEngine.instance().setOption(AVDEngine.Option.eo_audio_aec_Enable, svalue);
        svalue = isAudioAecDae ? "true" : "false";
        AVDEngine.instance().setOption(AVDEngine.Option.eo_audio_aec_DAEcho_Enable, svalue);
        svalue = isAudioAgc ? "true" : "false";
        AVDEngine.instance().setOption(AVDEngine.Option.eo_audio_autoGainControl_Enable, svalue);
        N2MSetting.getInstance().saveCommit();
        MVideo.setAutoRotation(isVideoAutoRotation);
    }
}
