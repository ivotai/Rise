package cn.tee3.n2m.ui.fragment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.unicorn.rise.R;

import java.util.List;

import cn.tee3.n2m.ui.adapter.VideosAdapter;

public class SelectVideoView extends LinearLayout {
    public interface OnSelectVideo4Render {
        void OnSelectVideo4Render(List<String> videos);
    }

    Button apply;
    ListView videosList;
    VideosAdapter adapter;

    public SelectVideoView(Context context) {
        this(context, null);
    }

    public SelectVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.select_video, this, true);
        this.apply = (Button) findViewById(R.id.btn_select_video);
        this.apply.setOnClickListener(this.selectvideo4show);
        this.videosList = (ListView) findViewById(R.id.id_videos_list);
    }

    OnSelectVideo4Render onsel;

    private View.OnClickListener selectvideo4show = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!(R.id.btn_select_video == v.getId())) {
                return;
            }
            setVisibility(View.INVISIBLE);

            if (null != onsel) {
                onsel.OnSelectVideo4Render(adapter.getSelectedVideos());
            }
        }
    };

}
