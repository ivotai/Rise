package cn.tee3.n2m.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Toast;

import com.unicorn.rise.R;

import java.util.List;

import cn.tee3.avd.VideoDevice;

public class VideosAdapter extends BaseAdapter {
    Context context;
    List<VideoDevice> allVideos;
    List<String> selectedVideos;

    public VideosAdapter(Context context, List<VideoDevice> videos, List<String> shows) {
        this.context = context;
        this.allVideos = videos;
        this.selectedVideos = shows;
    }

    public List<String> getSelectedVideos() {
        return this.selectedVideos;
    }

    @Override
    public int getCount() {
        return allVideos.size();
    }

    @Override
    public Object getItem(int position) {
        return allVideos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_video, null);
        }
        initView(convertView, position);
        return convertView;
    }

    private void initView(View view, final int position) {
        final CheckBox cb = (CheckBox) view.findViewById(R.id.cbUser);
        final VideoDevice video = allVideos.get(position);
        cb.setText(video.getDescription());
        cb.setChecked(selectedVideos.contains(video.getId()));
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb.isChecked()) {
                    if (selectedVideos.size() > 2) {
                        cb.setChecked(false);
                        Toast.makeText(context, "最多加入两路视频", Toast.LENGTH_SHORT).show();
                    }
                    selectedVideos.add(video.getId());
                } else {
                    selectedVideos.remove(video.getId());
                }
                notifyDataSetChanged();
            }
        });
    }
}
