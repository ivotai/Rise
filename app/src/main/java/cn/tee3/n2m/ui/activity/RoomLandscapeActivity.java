package cn.tee3.n2m.ui.activity;

import android.util.Log;

import com.unicorn.rise.R;


public class RoomLandscapeActivity extends RoomBaseActivity {

    @Override
    protected void initRoomUI() {
        Log.i("RoomLandscapeActivity", "initRoomUI,");
        setContentView(R.layout.activity_landscape);
        super.initRoomUI();
        this.videos.setBackground(R.drawable.bg_landscape);
    }
}

