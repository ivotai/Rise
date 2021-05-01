package cn.tee3.n2m.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;

import com.unicorn.rise.R;

import cn.tee3.avd.AVDEngine;
import cn.tee3.n2m.ui.util.N2MSetting;

/**
 * 自定义弹窗
 * Created by shengf on 2017/8/1.
 */

public class SettingsDialog {
    private static boolean isShowing;

    /**
     * 界面风格弹窗
     * N2MSetting.getInstance().getUIStype()   0：为竖屏；1为横屏
     *
     * @param context
     */
    public static void selectUIStyleDialog(final Context context, final MCallBack callBack) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setCancelable(true);
        alertDialog.setView(new EditText(context));
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.select_dialog_layout);
        TextView tvTitle = (TextView) window.findViewById(R.id.tv_title);
        RadioGroup rgSelect = (RadioGroup) window.findViewById(R.id.rg_select);
        RadioButton rbOne = (RadioButton) window.findViewById(R.id.rb_one);
        RadioButton rbTwo = (RadioButton) window.findViewById(R.id.rb_two);

        tvTitle.setText(R.string.uistyle);
        rbOne.setText(R.string.uistyle_portrait);
        rbTwo.setText(R.string.uistyle_landscape);

        if (0 == N2MSetting.getInstance().getUIStype()) {
            rgSelect.check(R.id.rb_one);
        } else {
            rgSelect.check(R.id.rb_two);
        }
        rgSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_one:
                        N2MSetting.getInstance().saveUIStype(0);
                        callBack.OnCallBackStr(context.getString(R.string.uistyle_portrait));
                        break;
                    case R.id.rb_two:
                        N2MSetting.getInstance().saveUIStype(1);
                        callBack.OnCallBackStr(context.getString(R.string.uistyle_landscape));
                        break;
                    default:
                        break;
                }
                N2MSetting.getInstance().saveCommit();
                alertDialog.dismiss();
                isShowing = false;
            }
        });
    }

    /**
     * 视频编码弹窗
     * N2MSetting.getInstance().getVideoCodec()  0:vp8;  1:h264软编;  2:h264硬编
     *
     * @param context
     */
    public static void videoCodecDialog(final Context context, final MCallBack callBack) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setCancelable(true);
        alertDialog.setView(new EditText(context));
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.select_dialog_layout);
        TextView tvTitle = (TextView) window.findViewById(R.id.tv_title);
        RadioGroup rgSelect = (RadioGroup) window.findViewById(R.id.rg_select);
        RadioButton rbOne = (RadioButton) window.findViewById(R.id.rb_one);
        RadioButton rbTwo = (RadioButton) window.findViewById(R.id.rb_two);
        RadioButton rbThree = (RadioButton) window.findViewById(R.id.rb_three);
        RadioButton rbFour = (RadioButton) window.findViewById(R.id.rb_four);
        RadioButton rbFive = (RadioButton) window.findViewById(R.id.rb_five);

        tvTitle.setText(R.string.videocodec);
        rbOne.setText(R.string.videocodec_vp8);
        rbTwo.setText(R.string.videocodec_h264_sw);
        rbThree.setText(R.string.videocodec_h264_hw);
        rbFour.setText(R.string.videocodec_h265_sw);
        rbFive.setText(R.string.videocodec_h265_hw);

        rbFour.setVisibility(View.VISIBLE);
        //判断当前设备是否支持h264的硬件编解码
        String sh264hw = AVDEngine.instance().getOption(AVDEngine.Option.eo_video_codec_support_h264hw);
        if (sh264hw.equals("true")) {
            rbThree.setVisibility(View.VISIBLE);
        } else {
            rbThree.setVisibility(View.GONE);
        }
        //判断当前设备是否支持h265的硬件编解码
        String sh265hw = AVDEngine.instance().getOption(AVDEngine.Option.eo_video_codec_support_h265hw);
        if (sh265hw.equals("true")) {
            rbFive.setVisibility(View.VISIBLE);
        } else {
            rbFive.setVisibility(View.GONE);
        }

        int videoCodec = N2MSetting.getInstance().getVideoCodec();
        if (0 == videoCodec) {
            rgSelect.check(R.id.rb_one);
        } else if (1 == videoCodec) {
            rgSelect.check(R.id.rb_two);
        } else if (2 == videoCodec) {
            rgSelect.check(R.id.rb_three);
        } else if (3 == videoCodec) {
            rgSelect.check(R.id.rb_four);
        } else if (4 == videoCodec) {
            rgSelect.check(R.id.rb_five);
        }

        rgSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_one:
                        N2MSetting.getInstance().saveVideoCodec(0);
                        callBack.OnCallBackStr(context.getString(R.string.videocodec_vp8));
                        break;
                    case R.id.rb_two:
                        N2MSetting.getInstance().saveVideoCodec(1);
                        callBack.OnCallBackStr(context.getString(R.string.videocodec_h264_sw));
                        break;
                    case R.id.rb_three:
                        N2MSetting.getInstance().saveVideoCodec(2);
                        callBack.OnCallBackStr(context.getString(R.string.videocodec_h264_hw));
                        break;
                    case R.id.rb_four:
                        N2MSetting.getInstance().saveVideoCodec(3);
                        callBack.OnCallBackStr(context.getString(R.string.videocodec_h265_sw));
                        break;
                    case R.id.rb_five:
                        N2MSetting.getInstance().saveVideoCodec(4);
                        callBack.OnCallBackStr(context.getString(R.string.videocodec_h265_hw));
                        break;
                    default:
                        break;
                }
                N2MSetting.getInstance().saveCommit();
                alertDialog.dismiss();
                isShowing = false;
            }
        });
    }

    /**
     * 视频分辨率弹窗
     * N2MSetting.getInstance().getVideoResolution()  0:CIF-352;  1:标清-640;  2:高清720P
     *
     * @param context
     */
    public static void videoResolutionDialog(final Context context, final MCallBack callBack) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setCancelable(true);
        alertDialog.setView(new EditText(context));
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.select_dialog_layout);
        TextView tvTitle = (TextView) window.findViewById(R.id.tv_title);
        RadioGroup rgSelect = (RadioGroup) window.findViewById(R.id.rg_select);
        RadioButton rbOne = (RadioButton) window.findViewById(R.id.rb_one);
        RadioButton rbTwo = (RadioButton) window.findViewById(R.id.rb_two);
        RadioButton rbThree = (RadioButton) window.findViewById(R.id.rb_three);

        tvTitle.setText(R.string.videoresolution);
        rbOne.setText(R.string.videoresolution_320);
        rbTwo.setText(R.string.videoresolution_640);
        rbThree.setText(R.string.videoresolution_1280);

        rbThree.setVisibility(View.VISIBLE);

        int videoResolution = N2MSetting.getInstance().getVideoResolution();
        if (0 == videoResolution) {
            rgSelect.check(R.id.rb_one);
        } else if (1 == videoResolution) {
            rgSelect.check(R.id.rb_two);
        } else {
            rgSelect.check(R.id.rb_three);
        }

        rgSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_one:
                        N2MSetting.getInstance().saveVideoResolution(0);
                        callBack.OnCallBackStr(context.getString(R.string.videoresolution_320));
                        break;
                    case R.id.rb_two:
                        N2MSetting.getInstance().saveVideoResolution(1);
                        callBack.OnCallBackStr(context.getString(R.string.videoresolution_640));
                        break;
                    case R.id.rb_three:
                        N2MSetting.getInstance().saveVideoResolution(2);
                        callBack.OnCallBackStr(context.getString(R.string.videoresolution_1280));
                        break;
                    default:
                        break;
                }
                N2MSetting.getInstance().saveCommit();
                alertDialog.dismiss();
                isShowing = false;
            }
        });
    }

    /**
     * 数据通道弹窗
     * N2MSetting.getInstance().getDataChannelNet()  0:UDP;  1:TCP;
     *
     * @param context
     */
    public static void dataChannelDialog(final Context context, final MCallBack callBack) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setCancelable(true);
        alertDialog.setView(new EditText(context));
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.select_dialog_layout);
        TextView tvTitle = (TextView) window.findViewById(R.id.tv_title);
        RadioGroup rgSelect = (RadioGroup) window.findViewById(R.id.rg_select);
        RadioButton rbOne = (RadioButton) window.findViewById(R.id.rb_one);
        RadioButton rbTwo = (RadioButton) window.findViewById(R.id.rb_two);

        tvTitle.setText(R.string.datachannel_net);
        rbOne.setText(R.string.datachannel_net_udp);
        rbTwo.setText(R.string.datachannel_net_tcp);

        int dataChannel = N2MSetting.getInstance().getDataChannelNet();
        if (0 == dataChannel) {
            rgSelect.check(R.id.rb_one);
        } else {
            rgSelect.check(R.id.rb_two);
        }

        rgSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_one:
                        N2MSetting.getInstance().saveDataChannelNet(0);
                        callBack.OnCallBackStr(context.getString(R.string.datachannel_net_udp));
                        break;
                    case R.id.rb_two:
                        N2MSetting.getInstance().saveDataChannelNet(1);
                        callBack.OnCallBackStr(context.getString(R.string.datachannel_net_tcp));
                        break;
                    default:
                        break;
                }
                N2MSetting.getInstance().saveCommit();
                alertDialog.dismiss();
                isShowing = false;
            }
        });
    }

    /**
     * 日志等级弹窗
     * N2MSetting.getInstance().getLogLevel()  0:详细;  1:正常;  2:错误
     *
     * @param context
     */
    public static void logLevelDialog(final Context context, final MCallBack callBack) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setCancelable(true);
        alertDialog.setView(new EditText(context));
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.select_dialog_layout);
        TextView tvTitle = (TextView) window.findViewById(R.id.tv_title);
        RadioGroup rgSelect = (RadioGroup) window.findViewById(R.id.rg_select);
        RadioButton rbOne = (RadioButton) window.findViewById(R.id.rb_one);
        RadioButton rbTwo = (RadioButton) window.findViewById(R.id.rb_two);
        RadioButton rbThree = (RadioButton) window.findViewById(R.id.rb_three);

        tvTitle.setText(R.string.loglevel);
        rbOne.setText(R.string.loglevel_verbose);
        rbTwo.setText(R.string.loglevel_info);
        rbThree.setText(R.string.loglevel_warning);

        rbThree.setVisibility(View.VISIBLE);

        int logLevel = N2MSetting.getInstance().getLogLevel();
        if (0 == logLevel) {
            rgSelect.check(R.id.rb_one);
        } else if (1 == logLevel) {
            rgSelect.check(R.id.rb_two);
        } else {
            rgSelect.check(R.id.rb_three);
        }

        rgSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_one:
                        N2MSetting.getInstance().saveLogLevel(0);
                        callBack.OnCallBackStr(context.getString(R.string.loglevel_verbose));
                        break;
                    case R.id.rb_two:
                        N2MSetting.getInstance().saveLogLevel(1);
                        callBack.OnCallBackStr(context.getString(R.string.loglevel_info));
                        break;
                    case R.id.rb_three:
                        N2MSetting.getInstance().saveLogLevel(2);
                        callBack.OnCallBackStr(context.getString(R.string.loglevel_warning));
                        break;
                    default:
                        break;
                }
                N2MSetting.getInstance().saveCommit();
                alertDialog.dismiss();
                isShowing = false;
            }
        });
    }

    public interface MCallBack {
        boolean OnCallBackStr(String messageStr);
    }
}
