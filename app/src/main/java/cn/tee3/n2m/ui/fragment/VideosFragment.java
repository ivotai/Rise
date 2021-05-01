package cn.tee3.n2m.ui.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.unicorn.rise.R;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cn.tee3.avd.Device;
import cn.tee3.avd.MAudio;
import cn.tee3.avd.MScreen;
import cn.tee3.avd.MVideo;
import cn.tee3.avd.NetworkStats;
import cn.tee3.avd.Room;
import cn.tee3.avd.VideoRenderer;
import cn.tee3.n2m.ui.activity.RoomBaseActivity;
import cn.tee3.n2m.ui.util.N2MSetting;
import cn.tee3.n2m.ui.util.ScreenUtil;

/**
 * 视频模块
 * Created by shengf on 2017/6/31.
 */
public class VideosFragment extends FrameLayout {
    private static final String TAG = "VideosFragment";
    private T3VideoView mvideoViewbg;
    private T3VideoView mvideoView1;
    private T3VideoView mvideoView2;
    private T3VideoView mvideoView3;
    private List<T3VideoView> mvideoViews = new LinkedList<>();
    private MVideo mvideo;
    private MAudio mAudio;
    private MScreen mscreen;
    private Room room;
    private View view_hide_tools;

    public VideosFragment(Context context) {
        this(context, null);
    }

    public VideosFragment(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideosFragment(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    boolean isshowvideoinfo = false;

    public void showVideoInfo() {
        isshowvideoinfo = !isshowvideoinfo;
        for (final T3VideoView item : this.mvideoViews) {
            item.showVideoInfo(isshowvideoinfo);
        }
    }

    public boolean isShowVideoInfo() {
        return isshowvideoinfo;
    }

    private Handler mHandler = new Handler();

    private Runnable run_showVideoInfo = new Runnable() {
        public void run() {
            if (!isshowvideoinfo) {
                mHandler.postDelayed(this, 1000);
                return;
            }
            for (final T3VideoView item : mvideoViews) {
                if (!item.hasVideo()) {
                    continue;
                }
                NetworkStats.MediaStats stats = room.getMediaStats(item.getVideoId());
                if (null == stats) {
                    continue;
                }
                int bpsSent = stats.getBps_sent() / 1000;
                int rameRate = stats.getFrame_rate();
                int frameWidth = stats.getFrame_width();
                int frameHeight = stats.getFrame_height();
                int bpsReceived = stats.getBps_received() / 1000;

                String bpsSentStr = (bpsSent > 0) ? bpsSent + "" : "--";
                String rameRateStr = (rameRate > 0) ? rameRate + "" : "--";
                String frameWidthStr = (frameWidth > 0) ? frameWidth + "" : "--";
                String frameHeightStr = (frameHeight > 0) ? frameHeight + "" : "--";
                String bpsReceivedStr = (bpsReceived > 0) ? bpsReceived + "" : "--";

                String infoStr = "";
                if (mvideo.isSelfDevice(item.getVideoId())) {
                    infoStr = bpsSentStr + "Kbps\t\t" + rameRateStr + "fps\n" + frameWidthStr + "x" + frameHeightStr;
                } else {
                    infoStr = bpsReceivedStr + "Kbps\t\t" + rameRateStr + "fps\n" + frameWidthStr + "x" + frameHeightStr;
                }
                item.updateVideoInfo(infoStr);
            }
            mHandler.postDelayed(this, 1000);
        }
    };

    private void init(Context context) {
        if (0 == mScreenWidth) {
            WindowManager manager = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            mScreenWidth = manager.getDefaultDisplay().getWidth();
            mScreenHeight = manager.getDefaultDisplay().getHeight();
            Log.i(TAG, "VideosFragment2 init screen: " + mScreenWidth + " x " + mScreenHeight);
        }
        LayoutInflater.from(context).inflate(R.layout.fragment_video2, this, true);
        this.mRootView = (RelativeLayout) findViewById(R.id.fs_video_fragment2);
        this.mvideoViewbg = (T3VideoView) findViewById(R.id.video_bg);
        this.mvideoView1 = (T3VideoView) findViewById(R.id.video_view1);
        this.mvideoView2 = (T3VideoView) findViewById(R.id.video_view2);
        this.mvideoView3 = (T3VideoView) findViewById(R.id.video_view3);
        view_hide_tools = findViewById(R.id.view_hide_tools);
        this.mvideoViews.add(this.mvideoViewbg);
        this.mvideoViews.add(this.mvideoView1);
        this.mvideoViews.add(this.mvideoView2);
        this.mvideoViews.add(this.mvideoView3);

        //此处用于小屏的布局拖动、大小屏切换，如有需要小屏也要缩放，此处注释即可
        mvideoView1.setOnTouchListener(touchListener);
        mvideoView2.setOnTouchListener(touchListener);
        mvideoView3.setOnTouchListener(touchListener);

//        //设置工具栏的隐藏、显示
        //模式一下
        mvideoViewbg.setClickCallBack(new T3VideoView.ClickCallBack() {
            @Override
            public boolean OnClickCallBack(boolean messageStr) {
                RoomBaseActivity.showorhideToolbar();
                return false;
            }
        });
        //模式二下
        view_hide_tools.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RoomBaseActivity.showorhideToolbar();
            }
        });

        VideoRenderer.ScalingType scaleType = VideoRenderer.ScalingType.Scale_Aspect_Full;
        if (1 == N2MSetting.getInstance().getVideoShow()) {
            scaleType = VideoRenderer.ScalingType.Scale_Aspect_Fit;
        }
        for (T3VideoView item : this.mvideoViews) {
            item.getRender().setScalingType(scaleType);
            if (item != this.mvideoViewbg) {
                item.setZOrderOnTop(true);
            }
        }
        initMVideo();
        this.mHandler.postDelayed(run_showVideoInfo, 0);
    }

    // upper video: move upper video, switch upper and background video
    private int lastX, lastY;
    private int inX, inY;
    private Rect paddingRect;
    private static final int TOUCH_SLOP = 10;
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            if (mCurrentWindowMode == 1) {//只有模式一支持支持小视屏视图的移动
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = x;
                        lastY = y;
                        int[] p = new int[2];
                        v.getLocationOnScreen(p);
                        inX = x - p[0];
                        inY = y - p[1];
                        break;
                    case MotionEvent.ACTION_MOVE:
                        final int diff = Math.max(Math.abs(lastX - x), Math.abs(lastY - y));
                        if (diff < TOUCH_SLOP)
                            break;
                        moveSmallVideoView(x, y, v);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (Math.max(Math.abs(lastX - x), Math.abs(lastY - y)) <= 5) {
                            switchVideoRender((T3VideoView) v);
                        }
                        break;
                }
            }
            return true;
        }
    };

    /**
     * 移动小视图
     *
     * @param x
     * @param y
     * @param v
     */
    private void moveSmallVideoView(int x, int y, View v) {
        if (paddingRect == null) {
            paddingRect = new Rect(ScreenUtil.dip2px(10), ScreenUtil.dip2px(20), ScreenUtil.dip2px(10),
                    ScreenUtil.dip2px(70));
        }
        int destX, destY;
        if (x - inX <= paddingRect.left) {
            destX = paddingRect.left;
        } else if (x - inX + v.getWidth() >= ScreenUtil.screenWidth - paddingRect.right) {
            destX = ScreenUtil.screenWidth - v.getWidth() - paddingRect.right;
        } else {
            destX = x - inX;
        }
        if (y - inY <= paddingRect.top) {
            destY = paddingRect.top;
        } else if (y - inY + v.getHeight() >= ScreenUtil.screenHeight - paddingRect.bottom) {
            destY = ScreenUtil.screenHeight - v.getHeight() - paddingRect.bottom;
        } else {
            destY = y - inY;
        }
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();
        params.leftMargin = destX;
        params.topMargin = destY;
        v.setLayoutParams(params);
    }

    public void setBackground(int bgId) {
        this.mvideoViewbg.setBackground(bgId);
    }

    T3RoomToolbar t3RoomToolbar;

    public void setT3VideoView(T3RoomToolbar t3RoomToolbar) {
        this.t3RoomToolbar = t3RoomToolbar;
    }

    private void initMVideo() {
        if (null != this.mvideo && null != this.mscreen && null != mAudio) {
            return;
        }
        room = Room.obtain(N2MSetting.getInstance().getRoomId());
        this.mvideo = MVideo.getVideo(room);
        mvideo.setListener(new MVideo.Listener() {
            @Override
            public void onCameraStatusNotify(Device.DeviceStatus status, String fromId) {
                Log.i(TAG, "onCameraStatusNotify: fromId=" + fromId + ",status=" + status);
            }

            @Override
            public void onCameraDataNotify(int level, String description, String fromId) {
            }

            @Override
            public void onUnsubscribeResult(int result, String fromId) {
            }

            @Override
            public void onPublishLocalResult(int result, String fromId) {
            }

            @Override
            public void onUnpublishLocalResult(int result, String fromId) {
            }

            @Override
            public void onPublishCameraNotify(MVideo.Camera from) {
                Log.i(TAG, "onPublishCameraNotify: fromId=" + from.getId());
                //远端开启本地摄像头时，设置图标变换
                if (mvideo.isSelfDevice(from.getId())) {
                    t3RoomToolbar.video_tb.setBackgroundResource(R.drawable.icon_video_show);
                }
                subscribeVideo(from);
            }

            @Override
            public void onUnpublishCameraNotify(MVideo.Camera from) {
                Log.i(TAG, "onUnpublishCameraNotify: fromId=" + from.getId());
                //远端关闭本地摄像头时，设置图标变换
                if (mvideo.isSelfDevice(from.getId())) {
                    t3RoomToolbar.video_tb.setBackgroundResource(R.drawable.icon_video_hide);
                }
                unsubscribeVideoAndReshow(from);
            }

            @Override
            public void onSubscribeResult(int result, String fromId) {
                Log.i(TAG, "onSubscribeResult: fromId=" + fromId);
                attachRender(fromId);
            }

        });
        mscreen = MScreen.getScreen(room);
        mscreen.setListener(new MScreen.Listener() {
            @Override
            public void onScreenStatusNotify(Device.DeviceStatus status, String fromId) {
            }

            @Override
            public void onScreenDataNotify(int level, String description, String fromId) {
                Log.i(TAG, "onScreenDataNotify,level=" + level + ",description=" + description + ",fromId=" + fromId);
            }

            @Override
            public void onPublishScreenNotify(MScreen.ScreenWindow from) {
                subscribeVideo(from);
            }

            @Override
            public void onUnpublishScreenNotify(MScreen.ScreenWindow from) {
                unsubscribeVideoAndReshow(from);
            }

            @Override
            public void onSubscribeResult(int result, String fromId) {
                attachRender(fromId);
            }

            @Override
            public void onUnsubscribeResult(int result, String fromId) {
            }
        });
        mAudio = MAudio.getAudio(room);
        mAudio.setListener(new MAudio.Listener() {
            @Override
            public void onMicrophoneStatusNotify(Device.DeviceStatus status, String fromUserId) {

            }

            @Override
            public void onAudioLevelMonitorNotify(MAudio.AudioInfo info) {

            }

            @Override
            public void onOpenMicrophoneResult(int result) {
                Log.i(TAG, "onOpenMicrophoneResult: result=" + result);
                //远端开启本地麦克风时，设置图标变换
                t3RoomToolbar.audio_tb.setBackgroundResource(R.drawable.icon_audio_open);
            }

            @Override
            public void onCloseMicrophoneResult(int result) {
                Log.i(TAG, "onCloseMicrophoneResult: result=" + result);
                //远端开启本地麦克风时，设置图标变换
                t3RoomToolbar.audio_tb.setBackgroundResource(R.drawable.icon_audio_close);
            }
        });

        defaultSelectVideo4Show();
    }

    private void defaultSelectVideo4Show() {
        if (null == mvideo || null == mvideo.getRoom() || !mvideo.getRoom().isWorking()) {
            return;
        }
        List<MScreen.ScreenWindow> shows1 = mscreen.getPublishedScreens();
        List<MVideo.Camera> shows2 = mvideo.getPublishedCameras();
        List<Device> shows = new LinkedList<>();
        shows.addAll(shows1);
        shows.addAll(shows2);
        int needSub = 4;
        List<String> displayedVideos = getShowVideos();
        if (!displayedVideos.isEmpty()) {
            Iterator<Device> iter = shows.iterator();
            while (iter.hasNext()) {
                Device item = iter.next();
                if (displayedVideos.contains(item.getId())) {
                    iter.remove();
                }
            }
            needSub -= displayedVideos.size();
        }
        while (shows.size() > needSub) {
            shows.remove(shows.size() - 1);
        }
        if (!shows.isEmpty()) {
            for (Device item : shows) {
                subscribeVideo(item);
            }
        }
    }

    private List<String> getShowVideos() {
        List<String> shows = new LinkedList<>();
        for (T3VideoView item : this.mvideoViews) {
            if (item.hasVideo()) {
                shows.add(item.getVideoId());
            }
        }
        return shows;
    }

    private void subscribeVideo(Device video) {
        if (!hasFreeRender()) {
            return;
        }
        if (mvideo.isSelfDevice(video.getId())) {
            attachRender(video.getId());
        } else {
            if (video instanceof MScreen.ScreenWindow) {
                mscreen.subscribe(video.getId());
            } else {
                mvideo.subscribe(video.getId());
            }
        }
    }

    private void unsubscribeVideoAndReshow(Device video) {
        unsubscribeVideo(video);
        defaultSelectVideo4Show();
    }

    private void unsubscribeVideo(Device video) {
        detachRender(video.getId());
        if (mvideo.isSelfDevice(video.getId())) {
            return;
        }
        if (video instanceof MScreen.ScreenWindow) {
            mscreen.unsubscribe(video.getId());
        } else {
            mvideo.unsubscribe(video.getId());
        }
    }

    private void attachRender(String videoId) {
        T3VideoView render = getFreeRender();
        if (null != render) {
            render.setVideo(videoId, this.mvideo.getOwnerName(videoId));
            Log.i(TAG, "attachRender render=" + render);
            // layout video view
            VideoRenderer avdrender = render.getRender();
            mvideo.attachRender(videoId, avdrender);
            layoutVideoViews(mCurrentWindowMode);
        } else {
            Log.w(TAG, "attachRender no render. videoId=" + videoId);
        }
    }

    private void detachRender(String videoId) {
        T3VideoView render = getRenderByVideoId(videoId);
        if (null != render) {
            detachVideoView(render);
            if (render == this.mvideoViewbg) { // switch up to bg
                if (mvideoView1.hasVideo()) {
                    switchVideoRender(mvideoView1);
                    return;
                }
                if (mvideoView2.hasVideo()) {
                    switchVideoRender(mvideoView2);
                    return;
                }
                if (mvideoView3.hasVideo()) {
                    switchVideoRender(mvideoView3);
                    return;
                }
            }
        }
    }

    private void detachVideoView(T3VideoView render) {
        if (!render.hasVideo()) {
            return;
        }
        Log.i(TAG, "detachVideoView render=" + render);
        mvideo.detachRender(render.getRender());
        render.getRender().setCallbacks(null);
        render.setVideo(null, null);
        // layout video view
        layoutVideoViews(mCurrentWindowMode);
    }

    private T3VideoView getFreeRender() {
        for (T3VideoView item : this.mvideoViews) {
            if (!item.hasVideo()) {
                return item;
            }
        }
        return null;
    }

    private boolean hasFreeRender() {
        if (null == getFreeRender()) {
            Log.i(TAG, "hasFreeRender no free Render.");
            return false;
        }
        return true;
    }

    private T3VideoView getRenderByVideoId(String videoId) {
        for (T3VideoView item : this.mvideoViews) {
            if (item.isThisVideo(videoId)) {
                return item;
            }
        }
        return null;
    }

    public void dispose() {
        for (T3VideoView item : this.mvideoViews) {
            item.dispose();
        }
        this.mvideoViews.clear();
    }

    private RelativeLayout mRootView;
    private int mCurrentWindowMode = 1;
    private int mScreenWidth = 0;
    private int mScreenHeight = 0;
    private int mControlBarHeight = 200;
    private int mStatusBarHeight = 0;
    private int mTitleBarHeight = 0;

    private void addViewIntoRootView(View v, int index) {
        if (index >= 0) {
            if (v.getParent() == null) {
                mRootView.addView(v, index);
            }
        } else {
            if (v.getParent() == null) {
                mRootView.addView(v);
            }
        }
    }

    private String toString(RelativeLayout.LayoutParams l) {
        return new String("lo:" + l.leftMargin + " " + l.topMargin + " " + l.width + "x" + l.height);
    }

    /**
     * 布局模式（一大三小；二、三分屏；四分屏）
     *
     * @param currentWindowMode
     */
    private void layoutVideoViews(int currentWindowMode) {
        int videoCount = getShowVideos().size();
        int idx = 0;
        if (currentWindowMode == 1 || videoCount == 1) {//一大三小模式,或者模式二下单个视频
            setView_1Big3small(videoCount, idx);
        } else if (currentWindowMode == 2) {//均分模式
            view_hide_tools.setVisibility(VISIBLE);
            if (videoCount <= 3 && videoCount > 0) {//1--3位用户（1个视频满屏；2、3个视频均分）
                setView_SplitScreen2or3(videoCount, idx);
            } else {//4个及以上用户（田字划分）
                setView_SplitScreen4(idx);
            }
        }

        int addIndex = 0;
        for (T3VideoView item : this.mvideoViews) {
            if (!item.hasVideo()) {
                item.setVisibility(View.GONE);
                removeView(item);
            } else {
                item.setVisibility(View.VISIBLE);
                addViewIntoRootView(item, addIndex++);
            }
        }
    }

    /**
     * 一大三小视图，分屏模式下单个视图
     *
     * @param videoCount
     * @param idx
     */
    private void setView_1Big3small(int videoCount, int idx) {
        int videoWidth = 0;
        int videoHeight = 0;
        view_hide_tools.setVisibility(GONE);

        if (mScreenHeight >= mScreenWidth) {//竖屏
            videoWidth = mScreenWidth / 3;
            videoHeight = videoWidth;
        } else {
            videoWidth = mScreenWidth / 5;
            videoHeight = videoWidth;
        }

        int top = mScreenHeight - mControlBarHeight - videoHeight - mStatusBarHeight;
        int leftMargin = 0;
        if (videoCount >= 4) {
            leftMargin = 0;
        } else if (videoCount >= 3) {
            leftMargin = videoWidth >> 1;
        } else {
            leftMargin = videoWidth;
        }
        Log.i(TAG, "layoutVideoViews: videoCount=" + videoCount + ",leftMargin=" + leftMargin);
        for (final T3VideoView item : this.mvideoViews) {
            if (!item.hasVideo()) {
                continue;
            }
            RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) item.getLayoutParams();
            if (this.mvideoViewbg == item) {
                l.width = mScreenWidth;
                l.height = mScreenHeight;
                l.setMargins(0, mTitleBarHeight, 0, 0);
                item.setLayoutParams(l);
            } else {
                l.width = videoWidth;
                l.height = videoHeight;
                if (mScreenHeight > mScreenWidth) {
                    l.setMargins(leftMargin, top, 0, 0);
                    leftMargin += videoWidth;
                } else {
                    l.setMargins(leftMargin * 2, top, 0, 0);
                    leftMargin += videoWidth;
                }
                item.setLayoutParams(l);
            }
            Log.i(TAG, "layoutVideoViews " + idx + " :" + toString(l));
            ++idx;
        }
    }

    /**
     * 2、3分屏（需考虑横竖屏）
     *
     * @param videoCount
     * @param idx
     */
    private void setView_SplitScreen2or3(int videoCount, int idx) {
        int w = 0;
        int h = 0;
        if (mScreenHeight > mScreenWidth) {//竖屏
            w = mScreenWidth;
            h = mScreenHeight / videoCount;
            for (T3VideoView item : this.mvideoViews) {
                if (!item.hasVideo()) {
                    continue;
                }
                RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) item.getLayoutParams();
                l.width = w;
                l.height = h;
                int top = h * (idx);
                l.setMargins(0, top, 0, 0);
                item.setLayoutParams(l);
                ++idx;
            }
        } else {//横屏
            w = mScreenWidth / videoCount;
            h = mScreenHeight;
            for (T3VideoView item : this.mvideoViews) {
                if (!item.hasVideo()) {
                    continue;
                }
                RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) item.getLayoutParams();
                l.width = w;
                l.height = h;
                int left = w * (idx);
                l.setMargins(left, 0, 0, 0);
                item.setLayoutParams(l);
                ++idx;
            }
        }
    }

    /**
     * 四分屏
     *
     * @param idx
     */
    private void setView_SplitScreen4(int idx) {
        int w = mScreenWidth / 2;
        int h = mScreenHeight / 2;
        for (T3VideoView item : this.mvideoViews) {
            if (!item.hasVideo()) {
                continue;
            }
            RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) item.getLayoutParams();
            l.width = w;
            l.height = h;
            int left = 0;
            int top = 0;
            if (idx == 1 || idx == 3) {//第二、三个视频距离左边的间距
                left = w;
            }
            if (idx == 2 || idx == 3) {//第三、四个视频距离右边的间距
                top = h;
            }
            l.setMargins(left, top, 0, 0);
            item.setLayoutParams(l);
            ++idx;
        }
    }

    /**
     * 切换模式
     */
    public void switchDisplayMode() {
        if (mCurrentWindowMode == 1) {
            mCurrentWindowMode = 2;
        } else {
            mCurrentWindowMode = 1;
        }
        mvideoViewbg.setVideoViewCanMove(mCurrentWindowMode == 1 ? true : false);
        layoutVideoViews(mCurrentWindowMode);
    }

    /**
     * 切换大小图
     *
     * @param item
     */
    private void switchVideoRender(T3VideoView item) {
        if (null == item) {
            return;
        }
        String videobg = item.getVideoId();
        String videoup = mvideoViewbg.getVideoId();
        detachVideoView(item);
        detachVideoView(mvideoViewbg);

        if (null == videobg) {
            return;
        }
        attachRender(videobg);

        if (null == videoup) {
            return;
        }
        attachRender(videoup);
    }
}
