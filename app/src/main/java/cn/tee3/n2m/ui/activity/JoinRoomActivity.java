package cn.tee3.n2m.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.unicorn.rise.R;

import cn.tee3.avd.AVDEngine;
import cn.tee3.avd.Room;
import cn.tee3.avd.User;
import cn.tee3.n2m.N2MApplication;
import cn.tee3.n2m.ui.util.N2MSetting;
import cn.tee3.n2m.ui.util.TextViewUtil;
import cn.tee3.n2m.ui.util.ToastUtil;

public class JoinRoomActivity extends FragmentActivity implements View.OnClickListener, Room.JoinResultListener, ActivityCompat.OnRequestPermissionsResultCallback {
    EditText roomId;
    EditText userName;
    EditText password;
    Button btnJoin;
    ImageButton btnSetting;
    JoinRoomActivity self;

    private long touchTime = 0;
    private long waitTime = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joinroom);
        this.roomId = (EditText) findViewById(R.id.id_roomId);
        this.userName = (EditText) findViewById(R.id.id_userName);
        this.password = (EditText) findViewById(R.id.id_password);
        this.btnJoin = (Button) findViewById(R.id.id_joinroom);
        this.btnJoin.setOnClickListener(this);
        this.btnSetting = (ImageButton) findViewById(R.id.id_setting);
        this.btnSetting.setOnClickListener(this);
        this.roomId.setText(N2MSetting.getInstance().getRoomId());
        this.userName.setText(N2MSetting.getInstance().getUserName());
        this.password.setText("admin");
        this.self = this;
    }

    public static final int RESULT_CODE_STARTAUDIO = 1;
    public static final int RESULT_CODE_STARTCAMERA = 2;

    protected boolean checkPermission(String permission, final Activity activity, int reqcode) {
        int checkResult = ContextCompat.checkSelfPermission(activity, permission);
        if (PackageManager.PERMISSION_GRANTED != checkResult) {
            Log.w("N2m", "checkPermission, no permission:" + permission);
            if (null != activity) {
                String[] perms = {permission};
                ActivityCompat.requestPermissions(activity, perms, reqcode);
            }
            return false;
        } else {
            Log.i("N2m", "checkPermission, has permission:" + permission);
        }
        return true;
    }

    public boolean checkAudioPermission(final Activity activity) {
        // "android 6.0以上版本有效； 6.0以下版本无效，需要做真实的录制才能录制成功
        return checkPermission(android.Manifest.permission.RECORD_AUDIO, activity, RESULT_CODE_STARTAUDIO);
    }

    public boolean checkVideoPermission(final Activity activity) {
        return checkPermission(android.Manifest.permission.CAMERA, activity, RESULT_CODE_STARTCAMERA);
    }

//    @Override
//    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
//        switch (permsRequestCode) {
//            case RESULT_CODE_STARTAUDIO:
//                boolean albumAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                if (!albumAccepted) {
//                    ToastUtil.showLongToast(this, "请开启应用录音权限");
//                }
//                break;
//            case RESULT_CODE_STARTCAMERA:
//                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                if (!cameraAccepted) {
//                    ToastUtil.showLongToast(this, "请开启应用拍照权限");
//                }
//                break;
//            default:
//                break;
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_setting:
                Intent intent = new Intent();
                intent.setClass(JoinRoomActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.id_joinroom:
                if (!AVDEngine.instance().isWorking()) {
                    Log.i(getClass().getName(), "Join Room, AVDEngine is not working, reInitAVDEngine");
                    ToastUtil.showToast(this, R.string.engineNeedInit);
                    ((N2MApplication) getApplication()).reInitAVDEngine();
                    return;
                }
                if (!TextViewUtil.isNullOrEmpty(roomId)) {
                    ToastUtil.showToast(this, R.string.noMeetNum);
                    return;
                }
                if (!TextViewUtil.isNullOrEmpty(userName)) {
                    ToastUtil.showToast(this, R.string.noUser);
                    return;
                }
                joinConference();
                break;
            default:
                break;
        }
    }

    private String currentRoomId;

    private void joinConference() {
        if (N2MSetting.getInstance().isMultiLive()) { // only supported 640x480
            N2MSetting.getInstance().saveVideoResolution(1);
            AVDEngine.instance().setOption(AVDEngine.Option.eo_camera_capability_default, N2MSetting.getInstance().getVideoResolutionOption());
        }
        AVDEngine.instance().setOption(AVDEngine.Option.eo_audio_autoGainControl_Enable, "false");

        this.currentRoomId = roomId.getText().toString();
        Room room = Room.obtain(this.currentRoomId);
        room.setOption(Room.Option.ro_audio_option_codec, "opus");
        if (null == room) {
            Log.w(getClass().getName(), "joinConference room is null. RoomId=" + currentRoomId);
            ToastUtil.showToast(this, R.string.errNum);
            return;
        }
        room.setOption(Room.Option.ro_media_use_dtls, "false");
        User user = new User(N2MSetting.getInstance().getUserId(), userName.getText().toString(), "");
        final String password = this.password.getText().toString();
        if (N2MSetting.getInstance().isOEMTest()) {
            room.join(user, password, this, N2MSetting.getInstance().getRoomToken());
        } else {
            room.join(user, password, this);
        }
    }

    @Override
    public void onJoinResult(int result) {
        if (0 != result) {
            Log.w(getClass().getName(), "join result=" + result);
            String err = getString(R.string.joinError) + result;
            ToastUtil.showLongToast(this, err);
            return;
        }
        N2MSetting.getInstance().saveRoomId(this.currentRoomId);
        N2MSetting.getInstance().saveUserName(this.userName.getText().toString());
        N2MSetting.getInstance().saveCommit();

        Log.i(getClass().getName(), "join result = " + result);
        Intent intent = new Intent();
        if (0 == N2MSetting.getInstance().getUIStype()) {
            intent.setClass(JoinRoomActivity.this, RoomPortraitActivity.class);
        } else {
            intent.setClass(JoinRoomActivity.this, RoomLandscapeActivity.class);
        }
        startActivity(intent);
    }

    /**
     * 退出app
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && KeyEvent.KEYCODE_BACK == keyCode) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - touchTime) >= waitTime) {
                Toast.makeText(this, "再按一次 退出程序", Toast.LENGTH_SHORT).show();
                touchTime = currentTime;
            } else {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
