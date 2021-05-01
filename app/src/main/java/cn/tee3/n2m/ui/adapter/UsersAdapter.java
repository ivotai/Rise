package cn.tee3.n2m.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.unicorn.rise.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tee3.avd.User;

public class UsersAdapter extends BaseAdapter {
    public interface OnUserRemoteCmd {
        void OnUserRemoteCmd(String type, int status, String userId);
    }

    Context context;
    List<User> userBeans;
    Map<String, Integer> actives = new HashMap();

    public UsersAdapter(Context context, List<User> messageBeans) {
        this.context = context;
        this.userBeans = messageBeans;
    }

    @Override
    public int getCount() {
        return userBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return userBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        User item = userBeans.get(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.list_item_user, null);
        if (convertView.getTag() == null) {
            holder = new ViewHolder();
            holder.userName = (TextView) convertView.findViewById(R.id.id_user_name);
            holder.audioLevel = (ImageView) convertView.findViewById(R.id.id_user_audio_level);
            holder.audioStatus = (ImageButton) convertView.findViewById(R.id.id_user_audio);
            holder.videoStatus = (ImageButton) convertView.findViewById(R.id.id_user_video);
            holder.screenStatus = (ImageView) convertView.findViewById(R.id.id_user_screen);
            holder.audioStatus.setOnClickListener(this.statusclick);
            holder.videoStatus.setOnClickListener(this.statusclick);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (null != holder) {
            holder.userName.setText(item.getUserName());
            showButton(holder.audioStatus, item.getStatus().isAudioOn(), R.drawable.icon_audio_status, item.getStatus().hasMicrophone(), R.drawable.icon_audio_status_close, item.getUserId());
            showButton(holder.videoStatus, item.getStatus().isVideoOn(), R.drawable.icon_video_status, item.getStatus().hasCamera(), R.drawable.icon_video_status_close, item.getUserId());
            holder.screenStatus.setVisibility(item.getStatus().isScreenOn() ? View.VISIBLE : View.INVISIBLE);
            if (item.getStatus().isAudioOn() && this.actives.containsKey(item.getUserId())) {
                holder.audioLevel.setBackgroundResource(level2ResourceId(this.actives.get(item.getUserId())));
                holder.audioLevel.setVisibility(View.VISIBLE);
            } else {
                holder.audioLevel.setVisibility(View.INVISIBLE);
            }
        }
        return convertView;
    }

    private void showButton(ImageButton btn, boolean a, int imga, boolean b, int imgb, String userId) {
        int imgid = 0;
        btn.setTag(R.id.tag_userId, userId);
        if (a) {
            imgid = imga;
            btn.setTag(R.id.tag_status, 2);
        } else if (b) {
            imgid = imgb;
            btn.setTag(R.id.tag_status, 1);
        }
        if (0 == imgid) {
            btn.setVisibility(View.INVISIBLE);
            btn.setTag(R.id.tag_status, 0);
        } else {
            btn.setVisibility(View.VISIBLE);
            btn.setBackgroundResource(imgid);
        }
    }

    public void refreshUserList(List<User> messageBeans) {
        this.userBeans = messageBeans;
        notifyDataSetChanged();
    }

    public void refreshAudioLevel(Map<String, Integer> actives) {
        if (this.getCount() > 20) {
            return;
        }
        this.actives = actives;
        notifyDataSetChanged();
    }

    private int level2ResourceId(int level) {
        return (level < 4) ? R.drawable.icon_audio_level_1
                : (level < 7) ? R.drawable.icon_audio_level_2
                : R.drawable.icon_audio_level_3;
    }

    class ViewHolder {
        TextView userName;
        ImageView audioLevel;
        ImageButton audioStatus;
        ImageButton videoStatus;
        ImageView screenStatus;
    }

    View.OnClickListener statusclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String type = null;
            if (R.id.id_user_audio == v.getId()) {
                type = "audio";
            } else if (R.id.id_user_video == v.getId()) {
                type = "video";
            }
            String userId = (String) v.getTag(R.id.tag_userId);
            int status = (int) v.getTag(R.id.tag_status);
            if (null != onremotecmd) {
                onremotecmd.OnUserRemoteCmd(type, status, userId);
            }
        }
    };
    OnUserRemoteCmd onremotecmd;

    public void setOnUserRemoteCmdListener(OnUserRemoteCmd l) {
        this.onremotecmd = l;
    }
}
