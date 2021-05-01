package cn.tee3.n2m.ui.activity;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.unicorn.rise.R;

import java.util.Timer;
import java.util.TimerTask;

import cn.tee3.avd.MVideo;
import cn.tee3.avd.NetworkStats;
import cn.tee3.avd.Room;
import cn.tee3.n2m.ui.fragment.ChatsFragment;
import cn.tee3.n2m.ui.fragment.T3RoomToolbar;
import cn.tee3.n2m.ui.fragment.UsersFragment;
import cn.tee3.n2m.ui.fragment.VideosFragment;
import cn.tee3.n2m.ui.util.N2MSetting;
import cn.tee3.n2m.ui.util.ScreenUtil;

public class RoomBaseActivity extends FragmentActivity {
    private static final String TAG = "RoomBaseActivity";
    protected static T3RoomToolbar toolbar;
    protected FrameLayout contents;
    protected ChatsFragment chats;
    protected UsersFragment users;
    protected VideosFragment videos;
    private TextView clock;
    private TextView roomid;
    private ProgressDialog progressDialog;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDAT_WALL_TIME_TIMER_TASK:
                    updateWallTime();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate,");
        super.onCreate(savedInstanceState);
        // 隐藏标题和设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initRoomUI();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    protected void initRoomUI() {
        Log.i(TAG, "initRoomUI,");
        this.videos = (VideosFragment) findViewById(R.id.fs_video_fragment);
        this.contents = (FrameLayout) findViewById(R.id.id_upcontents);
        this.toolbar = (T3RoomToolbar) findViewById(R.id.id_t3roomtoolbar);
        this.toolbar.setOnSwitchListener(this.tb_switchcontent);
        this.toolbar.setOnConnectionListener(this.tb_onconnection);
        this.toolbar.setVideoFragment(videos);
        this.videos.setT3VideoView(toolbar);
        this.videos.setOnClickListener(this.bg_OnClickListener);
        this.clock = (TextView) findViewById(R.id.id_inroomtimer);
        this.roomid = (TextView) findViewById(R.id.id_roomid);

        ScreenUtil.init(this);
        Room room = Room.obtain(N2MSetting.getInstance().getRoomId());
        this.roomid.setText("房间号" + room.getRoomId());

        this.roomTimer = new Timer(true);
        this.roomTimerTask = new RoomTimerTask();
        this.roomTimer.schedule(this.roomTimerTask, 1000, 1000);
    }

    private T3RoomToolbar.OnSwitchContent tb_switchcontent = new T3RoomToolbar.OnSwitchContent() {
        @Override
        public void OnSwitchContent(String name, boolean isShow) {
            if (!isShow) {
                contents.setVisibility(View.INVISIBLE);
                if (null != users) {
                    users.monitorAudioLevel(false);
                }
                return;
            }
            FragmentManager fm = getFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            if (name.equalsIgnoreCase("chats")) {
                if (null == chats) {
                    chats = new ChatsFragment();
                }
                transaction.replace(R.id.id_upcontents, chats);
                transaction.commit();
                contents.setVisibility(View.VISIBLE);
            } else if (name.equalsIgnoreCase("users")) {
                if (null == users) {
                    users = new UsersFragment();
                }
                transaction.replace(R.id.id_upcontents, users);
                users.monitorAudioLevel(true);
                transaction.commit();
                contents.setVisibility(View.VISIBLE);
            } else if (name.equals("switch_video_model")) {
                videos.switchDisplayMode();
            }
        }
    };
    private T3RoomToolbar.OnConnectionStatus tb_onconnection = new T3RoomToolbar.OnConnectionStatus() {
        @Override
        public void OnConnectionStatus(Room.ConnectionStatus status) {
            if (Room.ConnectionStatus.connecting == status) {
                showProgressDialog(getString(R.string.tip), getString(R.string.connectingTip));
            } else if (Room.ConnectionStatus.connected == status) {
                hideProgressDialog();
            } else if (Room.ConnectionStatus.connectFailed == status) {
                hideProgressDialog();
                connectFailedLeaveRoom();
            }
        }
    };

    private void showProgressDialog(String title, String message) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(RoomBaseActivity.this, title, message, true, false);
            progressDialog.setIcon(R.drawable.ic_launcher);
        } else if (progressDialog.isShowing()) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
        }
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void connectFailedLeaveRoom() {
        Log.i(TAG, "connectFailedLeaveRoom,");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.tip)
                .setIcon(R.drawable.ic_launcher)
                .setMessage(R.string.exit4connectFailedTip)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        toolbar.leaveRoom();
                        finish();
                    }
                }).create().show();
    }

    // background video: show or hide toolbar, select video for show
    private View.OnClickListener bg_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showorhideToolbar();
        }
    };

    /**
     * 隐藏显示工具栏
     */
    public static void showorhideToolbar() {
        if (View.VISIBLE == toolbar.getVisibility()) {
            toolbar.setVisibility(View.GONE);
            toolbar.isShowMore = false;
            toolbar.rlMore.setVisibility(View.GONE);
        } else {
            toolbar.setVisibility(View.VISIBLE);
        }
        toolbar.invalidate();
    }

    private static final int UPDAT_WALL_TIME_TIMER_TASK = 0x100 + 1;
    Timer roomTimer;
    RoomTimerTask roomTimerTask;
    private long second = 0;

    private class RoomTimerTask extends TimerTask {
        public void run() {
            ++second;
            mHandler.sendEmptyMessage(UPDAT_WALL_TIME_TIMER_TASK);
        }
    }

    private void updateWallTime() {
        String formatTime;
        String hs, ms, ss;
        long h, m, s;
        h = second / 3600;
        m = (second % 3600) / 60;
        s = (second % 3600) % 60;
        if (h < 10) {
            hs = "0" + h;
        } else {
            hs = "" + h;
        }

        if (m < 10) {
            ms = "0" + m;
        } else {
            ms = "" + m;
        }

        if (s < 10) {
            ss = "0" + s;
        } else {
            ss = "" + s;
        }

        String lostPercentStr = "";
        if (videos.isShowVideoInfo()) {
            //获取房间的流量统计信息
            NetworkStats.RoomStats roomStats = toolbar.room.getRoomStats();
            if (roomStats != null) {
                int lostPercent = roomStats.getLostpercent();
                if (lostPercent > 0) {
                    lostPercentStr = "(丢包率:" + lostPercent + ")";
                }
            }
        }

        if (h > 0) {
            formatTime = "幸会 " + hs + ":" + ms + ":" + ss + lostPercentStr;
        } else {
            formatTime = "幸会 " + ms + ":" + ss + lostPercentStr;
        }
        this.clock.setText(formatTime);
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        this.videos.dispose();
        this.roomTimer.cancel();
        Room room = Room.obtain(N2MSetting.getInstance().getRoomId());
        Room.destoryRoom(room);
        //退出房间后，将摄像头设置为前置
        N2MSetting.getInstance().setCurrCameraType(MVideo.CameraType.front);
    }

    @Override
    public void onBackPressed() {
        this.toolbar.alterAndLeaveRoom();
    }
}
