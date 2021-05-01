package cn.tee3.n2m.ui.fragment;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unicorn.rise.R;

import cn.tee3.avd.MVideo;
import cn.tee3.avd.VideoRenderer;

public class T3VideoView extends FrameLayout {
    private static final String TAG = "T3VideoView";

    GLSurfaceView glView;
    TextView tvUserName;
    TextView tvVideoInfo;
    LinearLayout llInfo;
    ImageView background;
    VideoRenderer mRenderer;
    private boolean isCanMove = true;

    public T3VideoView(Context context) {
        this(context, null);
    }

    public T3VideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public T3VideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    int width;
    int height;

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.t3video_view, this, true);
        glView = (GLSurfaceView) findViewById(R.id.gl_view);
        background = (ImageView) findViewById(R.id.background_img);
        llInfo = (LinearLayout) findViewById(R.id.ll_info);
        tvUserName = (TextView) findViewById(R.id.username_txt);
        tvVideoInfo = (TextView) findViewById(R.id.tv_video_info);

        this.mRenderer = new VideoRenderer(this.glView);
        this.mRenderer.setScalingType(VideoRenderer.ScalingType.Scale_Aspect_Fit);
        this.mRenderer.setAutoRotation(MVideo.isAutoRotation());

        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();

        scaleGestureDetector = new ScaleGestureDetector(getContext(), new simpleScaleGestueListener());
    }

    public void dispose() {
        this.mRenderer.dispose();
        this.glView = null;
    }

    public void setBackground(int bgId) {
        this.background.setBackgroundResource(bgId);
    }

    public boolean hasVideo() {
        return null != getVideoId();
    }

    public String getVideoId() {
        return this.mRenderer.getVideoId();
    }

    public boolean isThisVideo(String deviceId) {
        return deviceId.equalsIgnoreCase(getVideoId());
    }

    public void setVideo(String deviceId, String title) {
        this.tvUserName.setText(title);
        this.getRender().setVideoId(deviceId);
        if (hasVideo()) {
            this.background.setVisibility(View.INVISIBLE);
            this.mRenderer.setFirstFrameCallback(new VideoRenderer.FirstFrameCallback() {
                @Override
                public void onFirstFrameArrived(VideoRenderer render) {
                }
            });
        } else {
            mRenderer.fillBlack();
            this.background.setVisibility(View.VISIBLE);
        }
    }

    public void showVideoInfo(boolean isshowvideoinfo) {
        if (isshowvideoinfo) {
            llInfo.setVisibility(VISIBLE);
        } else {
            llInfo.setVisibility(GONE);
        }
    }

    public void updateVideoInfo(String info) {
        tvVideoInfo.setText(info);
    }

    public void setZOrderOnTop(boolean onTop) {
        this.glView.setZOrderOnTop(onTop);
        this.glView.setZOrderMediaOverlay(true);
    }

    public VideoRenderer getRender() {
        return this.mRenderer;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        this.glView.setVisibility(visibility);
    }

    /***************************平移部分*****************************/
    //缩放比例的偏移量
    float scale = 0.00f;
    // 获取当前触摸的绝对坐标
    int rawX = 0;
    int rawY = 0;
    // 上一次离开时的坐标
    int lastX = 0;
    int lastY = 0;
    // 当次的偏移量
    float offsetX;
    float offsetY;
    //平移的最小偏移量
    private float touchSlop = ViewConfiguration.getTouchSlop();
    private int mode = 0;
    private static final int MODE_DRAG = 1;
    private static final int MODE_ZOOM = 2;
    ScaleGestureDetector scaleGestureDetector = null;
    private ClickCallBack clickCallBack;

    public void setClickCallBack(ClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    public interface ClickCallBack {
        boolean OnClickCallBack(boolean messageStr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获取当前触摸的绝对坐标
        rawX = (int) event.getRawX();
        rawY = (int) event.getRawY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = MODE_DRAG;
                //设置历史坐标
                if (mode == MODE_DRAG) {
                    lastX = rawX;
                    lastY = rawY;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() >= 2) {
                    mode = MODE_ZOOM;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == MODE_DRAG) {
                    // 当次的偏移量
                    offsetX = 0.00f;
                    offsetY = 0.00f;
                    // 两次的偏移量
                    if (Math.abs(rawX - lastX) >= touchSlop) {
                        offsetX = ((float) (rawX - lastX) / width);
                        mRenderer.setOffset(offsetX, offsetY);
                    }
                    if (Math.abs(rawY - lastY) >= touchSlop) {
                        offsetY = ((float) (rawY - lastY) / height);
                        mRenderer.setOffset(offsetX, offsetY);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mode = 0;
                if ((Math.abs(rawX - lastX) < touchSlop) && (Math.abs(rawY - lastY) < touchSlop)) {
                    clickCallBack.OnClickCallBack(true);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = 0;
                break;
            default:
                break;
        }
        if (isCanMove) {
            return scaleGestureDetector.onTouchEvent(event);
        } else {
            return true;
        }
    }
    /***************************平移部分*****************************/


    /***************************缩放部分*****************************/
    private class simpleScaleGestueListener implements ScaleGestureDetector.OnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (mode == MODE_ZOOM) {
                scale = (float) ((detector.getScaleFactor() - 1) / 10.00);
                mRenderer.setScale(scale);
            }
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }
    }

    /***************************缩放部分*****************************/

    public void setVideoViewCanMove(Boolean canMove) {
        this.isCanMove = canMove;
    }
}
