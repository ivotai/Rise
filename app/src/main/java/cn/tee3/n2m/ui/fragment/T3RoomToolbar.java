package cn.tee3.n2m.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.unicorn.rise.R;

import java.util.List;

import cn.tee3.avd.AVDOutgoing;
import cn.tee3.avd.MAudio;
import cn.tee3.avd.MVideo;
import cn.tee3.avd.Room;
import cn.tee3.avd.RoomInfo;
import cn.tee3.avd.User;
import cn.tee3.n2m.ui.util.N2MSetting;

import static cn.tee3.n2m.ui.util.N2MSetting.getInstance;

public class T3RoomToolbar extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "T3RoomToolbar";

    public interface OnSwitchContent {
        void OnSwitchContent(String name, boolean isShow);
    }

    public interface OnConnectionStatus {
        void OnConnectionStatus(Room.ConnectionStatus status);
    }

    ///
    private ImageButton chats_tb;
    private ImageButton users_tb;
    private ImageButton leave_tb;
    public ImageButton video_tb;
    public ImageButton audio_tb;
    private ImageButton ib_speaker;//扬声器
    private ImageButton ibSwitchVideoModel;//切换模式按钮
    private ImageButton ibMore;
    public RelativeLayout rlMore;
    private ImageButton ibVideoInfo;
    private ImageButton getIbSwitchCamera;
    ///
    public Room room;
    MAudio maudio;
    MVideo mvideo;
    OnSwitchContent onswitch;

    private boolean isSpeaker;
    public boolean isShowMore = false;

    public T3RoomToolbar(Context context) {
        this(context, null);
    }

    public T3RoomToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        Log.i(TAG, "init,context=" + context.toString());
        if (0 == getInstance().getUIStype()) {
            LayoutInflater.from(context).inflate(R.layout.t3room_toolbar_portrait, this, true);
        } else {
            LayoutInflater.from(context).inflate(R.layout.t3room_toolbar_landscape, this, true);
        }
        chats_tb = (ImageButton) findViewById(R.id.chats_tb);
        users_tb = (ImageButton) findViewById(R.id.users_tb);
        leave_tb = (ImageButton) findViewById(R.id.leave_tb);
        video_tb = (ImageButton) findViewById(R.id.video_tb);
        audio_tb = (ImageButton) findViewById(R.id.audio_tb);
        ib_speaker = (ImageButton) findViewById(R.id.ib_speaker);
        ibMore = (ImageButton) findViewById(R.id.ib_more);
        ibSwitchVideoModel = (ImageButton) findViewById(R.id.ib_switch_video_model);
        ibVideoInfo = (ImageButton) findViewById(R.id.ib_info);
        rlMore = (RelativeLayout) findViewById(R.id.rl_more);
        getIbSwitchCamera = (ImageButton) findViewById(R.id.ib_switch_camera);

        ibSwitchVideoModel.setOnClickListener(this);
        chats_tb.setOnClickListener(this);
        users_tb.setOnClickListener(this);
        video_tb.setOnClickListener(this);
        audio_tb.setOnClickListener(this);
        leave_tb.setOnClickListener(this);
        ib_speaker.setOnClickListener(this);
        ibMore.setOnClickListener(this);
        ibVideoInfo.setOnClickListener(this);
        getIbSwitchCamera.setOnClickListener(this);
        rlMore.setVisibility(GONE);//将更多工具栏隐藏

        initRoom();

        //默认先打开扬声器
        this.maudio.setHandFree(true);
        isSpeaker = this.maudio.isHandFree();
        ib_speaker.setBackgroundResource(isSpeaker ? R.drawable.icon_speaker_open : R.drawable.icon_speaker_close);
    }

    private OnConnectionStatus onconnection;

    private void initRoom() {
        if (null != this.maudio && null != this.mvideo) {
            return;
        }

        this.room = Room.obtain(getInstance().getRoomId());
        this.room.setListener(new Room.Listener() {
            @Override
            public void onJoinResult(int result) {
            }

            @Override
            public void onLeaveIndication(int reason, String fromId) {
                Log.i(TAG, "onLeaveIndication,reason=" + reason + ",fromId=" + fromId);
            }

            @Override
            public void onPublicData(byte[] data, int len, String fromId) {
            }

            @Override
            public void onPrivateData(byte[] data, int len, String fromId) {
            }

            @Override
            public void onAppDataNotify(String key, String value) {
            }

            @Override
            public void onRoomStatusNotify(RoomInfo.RoomStatus status) {
            }

            @Override
            public void onConnectionStatus(Room.ConnectionStatus status) {
                Log.i(TAG, "onConnectionStatus,status=" + status);
                if (null != onconnection) {
                    onconnection.OnConnectionStatus(status);
                }
            }
        });

        AVDOutgoing.instance().setListener(new AVDOutgoing.Listener() {
            @Override
            public void onCreateOutgoingUser(int result, String roomId, String userid, String useraddress) {
                Log.i(TAG, "onCreateOutgoingUser,result=" + result + ",roomId=" + roomId + ",userid=" + userid + ",useraddress=" + useraddress);
            }

            @Override
            public void onDestoryOutgoingUser(int result, String roomId, String userid, String useraddress) {
                Log.i(TAG, "onDestoryOutgoingUser,result=" + result + ",roomId=" + roomId + ",userid=" + userid + ",useraddress=" + useraddress);
            }

            @Override
            public void onGetOutgoingUsers(int result, String roomId, List<User> users) {
                Log.i(TAG, "onGetOutgoingUsers,result=" + result + ",roomId=" + roomId + ",users=" + users.size());
            }
        });

        this.maudio = MAudio.getAudio(room);
        this.mvideo = MVideo.getVideo(room);

        if (N2MSetting.getInstance().isAutoAudio()) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    onClick(audio_tb);
                }
            }, 500);
        }
        if (N2MSetting.getInstance().isAutoVideo()) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    onClick(video_tb);
                }
            }, 1000);
        }
    }

    private boolean getBooleanTag(ImageButton btn) {
        boolean checked = false;
        String tag = (String) btn.getTag();
        if (tag.equals("1")) {
            tag = "0";
            checked = false;
        } else {
            tag = "1";
            checked = true;
        }
        btn.setTag(tag);
        return checked;
    }

    VideosFragment videoFragment;

    public void setVideoFragment(VideosFragment avideoFragment) {
        videoFragment = avideoFragment;
    }

    @Override
    public void onClick(View v) {
        if (R.id.leave_tb == v.getId()) {
            alterAndLeaveRoom();
            return;
        }
        boolean checked = false;
        switch (v.getId()) {
            case R.id.chats_tb:
                checked = getBooleanTag(chats_tb);
                if (null != this.onswitch) {
                    this.onswitch.OnSwitchContent("chats", checked);
                }
                if (checked) {
                    users_tb.setTag("0");
                }
                isShowMore = false;
                rlMore.setVisibility(GONE);
                break;
            case R.id.users_tb:
                checked = getBooleanTag(users_tb);
                if (null != this.onswitch) {
                    this.onswitch.OnSwitchContent("users", checked);
                }
                if (checked) {
                    chats_tb.setTag("0");
                }
                break;
            case R.id.video_tb:
                checked = openCloseLocalCamera();
                video_tb.setBackgroundResource(checked ? R.drawable.icon_video_show : R.drawable.icon_video_hide);
                break;
            case R.id.audio_tb:
                Log.i(TAG, "volume: " + maudio.getSpeakerVolume());
                checked = openCloseLocalMicrophone();
                audio_tb.setBackgroundResource(checked ? R.drawable.icon_audio_open : R.drawable.icon_audio_close);
                break;
            case R.id.ib_switch_video_model:
                if (null != this.onswitch) {
                    this.onswitch.OnSwitchContent("switch_video_model", true);
                }
                isShowMore = false;
                rlMore.setVisibility(GONE);
                break;
            case R.id.ib_speaker:
                isSpeaker = !isSpeaker;
                this.maudio.setHandFree(isSpeaker);
                ib_speaker.setBackgroundResource(isSpeaker ? R.drawable.icon_speaker_open : R.drawable.icon_speaker_close);
                isShowMore = false;
                rlMore.setVisibility(GONE);
                break;
            case R.id.ib_more:
                isShowMore = !isShowMore;
                if (isShowMore) {
                    rlMore.setVisibility(VISIBLE);
                } else {
                    rlMore.setVisibility(GONE);
                }
                break;
            case R.id.ib_info:
                videoFragment.showVideoInfo();
                this.room.enableStats(videoFragment.isShowVideoInfo());
                isShowMore = false;
                rlMore.setVisibility(GONE);
                break;
            case R.id.ib_switch_camera:
                List<MVideo.Camera> cameras = mvideo.getPublishedCameras();
                for (int i = 0; i < cameras.size(); i++) {
                    if (mvideo.isSelfDevice(cameras.get(i).getId())) {//判断是否是自己的摄像头设备
                        mvideo.switchToLocalCamera();
                        N2MSetting.getInstance().setCurrCameraType(mvideo.getCurrentCameraType());
                        isShowMore = false;
                        rlMore.setVisibility(GONE);
                    }
                }
                break;
            default:
                break;
        }
    }

    public void setOnSwitchListener(OnSwitchContent l) {
        this.onswitch = l;
    }

    public void setOnConnectionListener(OnConnectionStatus l) {
        this.onconnection = l;
    }

    public void leaveRoom() {
        room.enableStats(false);
        room.leave(0);
    }

    public void alterAndLeaveRoom() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.tip)
                .setIcon(R.drawable.ic_launcher)
                .setMessage(R.string.exitTip)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                leaveRoom();
                Context context = getContext();
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
            }
        }).create().show();
    }

    /**
     * 开启、关闭本地麦克风
     *
     * @return
     */
    private boolean openCloseLocalMicrophone() {
        boolean isOpen = true;
        if (this.maudio.isOpenMicrophone()) {
            this.maudio.closeMicrophone();
            isOpen = false;
        } else {
            this.maudio.openMicrophone();
        }
        return isOpen;
    }

    /**
     * 开启、关闭本地摄像头
     *
     * @return
     */
    private boolean openCloseLocalCamera() {
        room.setOption(Room.Option.ro_video_mixerdata_callback_format, "NV21");
        boolean isOpen = true;
        if (this.mvideo.ispublishedLocalCamera()) {
            this.mvideo.unpublishLocalCamera();
            isOpen = false;
        } else {
            this.mvideo.publishLocalCamera(N2MSetting.getInstance().getCurrCameraType());
        }
        return isOpen;
    }

}
