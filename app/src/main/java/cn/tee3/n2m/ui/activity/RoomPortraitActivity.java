package cn.tee3.n2m.ui.activity;

import android.util.Log;

import com.unicorn.rise.R;

public class RoomPortraitActivity extends RoomBaseActivity {

    @Override
    protected void initRoomUI() {
        Log.i("RoomPortraitActivity", "initRoomUI,");
        setContentView(R.layout.activity_portrait);
        super.initRoomUI();
        this.videos.setBackground(R.drawable.bg_portrait);
    }
}
