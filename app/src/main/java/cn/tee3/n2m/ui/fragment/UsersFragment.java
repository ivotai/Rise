package cn.tee3.n2m.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.unicorn.rise.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tee3.avd.Device;
import cn.tee3.avd.MAudio;
import cn.tee3.avd.MUserManager;
import cn.tee3.avd.MVideo;
import cn.tee3.avd.Room;
import cn.tee3.avd.User;
import cn.tee3.n2m.ui.adapter.UsersAdapter;
import cn.tee3.n2m.ui.util.N2MSetting;

public class UsersFragment extends Fragment {
    private static final String TAG = "UsersFragment";
    RelativeLayout layout;
    TextView usersNum;
    ListView usersList;
    ListView lvrecList;

    MUserManager musers;
    MAudio maudio;
    MVideo mvideo;
    UsersAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        initMUsers();
        if (null == this.layout) {
            this.layout = (RelativeLayout) inflater.inflate(R.layout.fragment_users, null);
            this.usersNum = (TextView) this.layout.findViewById(R.id.id_users_num);
            this.usersList = (ListView) this.layout.findViewById(R.id.id_users_list);
            this.lvrecList = (ListView) this.layout.findViewById(R.id.id_lvrec_list);
            this.adapter = new UsersAdapter(
                    getActivity(), this.musers.getParticipants(0, 20));
            this.usersList.setAdapter(adapter);
            this.adapter.setOnUserRemoteCmdListener(this.onremotecmd);
        } else {
            ViewGroup vg = (ViewGroup) this.layout.getParent();
            if (vg != null) {
                vg.removeAllViewsInLayout();
            }
        }
        refreshUsersList();
        monitorAudioLevel(false);
        return this.layout;
    }

    private boolean testPreview = false;
    Room room;

    private void initMUsers() {
        if (null != this.musers && null != this.maudio) {
            return;
        }

        room = Room.obtain(N2MSetting.getInstance().getRoomId());
        this.musers = MUserManager.getUserManager(room);
        musers.setListener(new MUserManager.Listener() {
            @Override
            public void onUserJoinNotify(User user) {
                refreshUsersList();
            }

            @Override
            public void onUserLeaveNotify(User user) {
                refreshUsersList();
            }

            @Override
            public void onUserUpdateNotify(User user) {
            }

            @Override
            public void onUserStatusNotify(int status, String fromId) {
                refreshUsersList();
            }

            @Override
            public void onUserDataNotify(String userData, String fromId) {
            }
        });

        maudio = MAudio.getAudio(room);
        maudio.setListener(new MAudio.Listener() {
            @Override
            public void onMicrophoneStatusNotify(Device.DeviceStatus status, String fromUserId) {
                Log.i(TAG, "onMicrophoneStatusNotify, status=" + status + ",fromUserId=" + fromUserId);
                if (testPreview && maudio.isSelfUser(fromUserId) && status == Device.DeviceStatus.published) {
                    maudio.muteMicrophone();
                    Log.i(TAG, "onMicrophoneStatusNotify, muteMicrophone");
                }
            }

            @Override
            public void onAudioLevelMonitorNotify(MAudio.AudioInfo info) {
                refreshAudioLevel(info);
            }

            @Override
            public void onOpenMicrophoneResult(int result) {
            }

            @Override
            public void onCloseMicrophoneResult(int result) {
            }
        });
        mvideo = MVideo.getVideo(room);
    }

    private boolean isRefreshingList = false;

    private void refreshUsersList() {
        if (isRefreshingList) {
            return;
        }
        isRefreshingList = true;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                String susersNum = "共" + (musers.getParticipantsCount() + 1) + "人";
                usersNum.setText(susersNum);
                //获取用户列表，注意该用户列表不包含本用户
                List<User> users = musers.getParticipants(0, musers.getParticipantsCount());
                //添加自己
                users.add(0, musers.getSelfUser());
                adapter.refreshUserList(users);
                usersList.smoothScrollToPosition(adapter.getCount() - 1);
                isRefreshingList = false;
            }
        }, 500);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void monitorAudioLevel(boolean ismonitor) {
        if (null == this.adapter || null == this.maudio) {
            return;
        }
        if (ismonitor) {
            if (this.adapter.getCount() < 20 && !maudio.ismonitorAudioLevel()) {
                maudio.monitorAudioLevel();
            }
        } else {
            if (maudio.ismonitorAudioLevel()) {
                maudio.unmonitorAudioLevel();
            }
        }
    }

    private boolean isRefreshingLevel = false;
    Map<String, Integer> audioActives = new HashMap();

    private void refreshAudioLevel(MAudio.AudioInfo info) {
        if (isRefreshingLevel) {
            return;
        }
        this.audioActives = info.getActiveStreams();
        if (info.getInputLevel() > 0) { // add myself in level map
            this.audioActives.put(     // first item is myself in userBeans
                    this.musers.getSelfUserId(), info.getInputLevel());
        }
        if (this.audioActives.isEmpty()) {
            return;
        }
        isRefreshingLevel = true;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                adapter.refreshAudioLevel(audioActives);
                isRefreshingLevel = false;
            }
        }, 300);
    }

    UsersAdapter.OnUserRemoteCmd onremotecmd = new UsersAdapter.OnUserRemoteCmd() {
        @Override
        public void OnUserRemoteCmd(String type, int status, String userId) {
            if (null == type || null == userId || 0 == status) {
                return;
            }
            if (musers.isSelfUser(userId)) {
                return;
            }
            if ("audio" == type) {
                if (1 == status) {
                    maudio.remotecmdOpenMicrophone(userId);
                } else if (2 == status) {
                    maudio.remotecmdCloseMicrophone(userId);
                }
            } else if ("video" == type) {
                List<MVideo.Camera> items = mvideo.getRemoteCameras(userId);
                if (null == items || items.isEmpty()) {
                    return;
                }
                if (1 == status) {
                    mvideo.remotecmdPublishCamera(items.get(0));
                } else if (2 == status) {
                    String deviceId = "";
                    for (MVideo.Camera item : items) {
                        if (item.getStatus() == Device.DeviceStatus.published || item.getStatus() == Device.DeviceStatus.muted) {
                            deviceId = item.getId();
                            break;
                        }
                    }
                    mvideo.remotecmdUnpublishCamera(deviceId);
                }
            }
        }
    };

}
