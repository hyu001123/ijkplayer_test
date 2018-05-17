package com.example.administrator.ijkplayer_test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.ijkplayer_test.parser.BiliXMLDanmakuParser;
import com.example.administrator.ijkplayer_test.utils.FileUtil;
import com.example.administrator.ijkplayer_test.utils.LogUtil;
import com.example.administrator.ijkplayer_test.utils.MarqueTextView;
import com.example.administrator.ijkplayer_test.utils.NavUtils;
import com.example.administrator.ijkplayer_test.utils.TimeUtil;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import tv.danmaku.ijk.media.example.widget.media.IjkVideoView;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class ViewPlayer extends FrameLayout implements View.OnClickListener {

    private static final int MSG_RELAOD = 10088;
    private static final int MSG_PLAYSEEKBAR_UPDATE = 3;
    private static final int SHAREDIALOG_IS_SHOW = 1;
    private static final int MSG_START_DANMAKU = 2;
    private AppCompatActivity mActivity = (AppCompatActivity) getContext();
    private IjkVideoView videoView;
    private ImageView play_circle;
    private SeekBar playSeekBar;
    private ImageView mFullScreen;
    private boolean isViewing;
    public ImageView thumbView;
    private TableLayout hubView;
    private LinearLayout bottomBar;
    private FrameLayout viewPlayerBox;
    private ImageView ivPlayer;
    private ImageView fullView;
    private TextView tvCurTime;
    private TextView tvEndTime;

    private static final int MAX_PLAYSEEKBAR = 1000;
    private ProgressBar loadingView;
    private boolean networkConnected;
    private int durationTime;
    private int curTime;

    private Handler mhandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_PLAYSEEKBAR_UPDATE) {
                // durationTime= videoView.getDuration();
                if(!isTVLive) curTime = videoView.getCurrentPosition();
                if (!isFullScreen) {
                    playSeekBar.setProgress(curTime);
                } else {
                    danmuSeekbar.setProgress(curTime);
                }
                tvCurTime.setText(TimeUtil.IntToHMS(curTime));
                sendEmptyMessageDelayed(MSG_PLAYSEEKBAR_UPDATE, 1000);
            } else if (msg.what == SHAREDIALOG_IS_SHOW) {
                if (sharedialog.DialogOver()) {
                    hideBottomUIMenu();
                    playStart();
                    mhandle.removeMessages(SHAREDIALOG_IS_SHOW);
                    mhandle.postDelayed(hideAllViewRunnable, 1000);
                } else sendEmptyMessageDelayed(SHAREDIALOG_IS_SHOW, 1000);
            }
        }
    };
    private FrameLayout windowTopBar;
    private int targetTime;
    private boolean isFullScreen;
    private TextView recoverScreen;
    private SeekBar danmuSeekbar;
    private TextView tvOpenDanmu;
    private ImageView danmuCtl;
    private TextView tvMediaQuality;
    private TextView tvTag;
    private LinearLayout fullscreenTopBar;
    private int mInitHeight;
    private int mWidthPixels;
    private MarqueTextView tvMarquee;
    private long exitTime = 0;
    private ImageView fullscreenBack;
    private ImageView windowBack;
    private GestureDetector mGestureDetector;
    private TextView tvVolume;
    private TextView tvBrightness;
    private FrameLayout guesterLayout;
    private TextView tvForward;
    private TextView tvRewind;
    //手势控制滑动视频目标位置
    private long mTime;
    private boolean isRenderingStart;
    private ProgressBar batteryProBar;
    private TextView sysTime;
    private ImageView screenShot;
    private BatteryReceiver mBatteryReceiver;
    private NetConnectReceiver mNetConnectReceiver;
    private ScreenReceiver mScreenReceiver;
    private boolean isScreenLocked;
    private boolean isNetConnected;
    private String path;
    private int oldUiVisibility;
    private ShareDialog sharedialog;
    private Message msgShareDialog;
    private boolean isEnableDanmaku;
    private IDanmakuView danmakuView;
    private ImageView danmakuCrl;
    private TextView danmakuOpenEdit;
    private LinearLayout danmaku_layout;
    private ImageView danmakuCancel;
    private ImageView danmakuSend;
    private BaseDanmakuParser mParser;
    private DanmakuContext mContext;
    private EditText danmakuEdit;
    private long sclTime;
    private int interruptPosition;
    private boolean isTVLive;

    public ViewPlayer(@NonNull Context context) {
        super(context);
    }

    public ViewPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ViewPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void initView(Context context) {
        View.inflate(context, R.layout.layout_player_view, this);
        viewPlayerBox = (FrameLayout) findViewById(R.id.fl_video_box);
        videoView = (IjkVideoView) findViewById(R.id.video_view1);
        play_circle = (ImageView) findViewById(R.id.iv_play_circle);
        playSeekBar = (SeekBar) findViewById(R.id.player_seek);
        mFullScreen = (ImageView) findViewById(R.id.iv_fullscreen);
        thumbView = (ImageView) findViewById(R.id.iv_thumb);
        hubView = (TableLayout) findViewById(R.id.hubView);
        bottomBar = (LinearLayout) findViewById(R.id.ll_bottom_bar);
        ivPlayer = (ImageView) findViewById(R.id.iv_play);
        tvCurTime = (TextView) findViewById(R.id.tv_cur_time);
        tvEndTime = (TextView) findViewById(R.id.tv_end_time);
        videoView.setHudView(hubView);
        fullView = (ImageView) findViewById(R.id.iv_fullscreen);
        loadingView = (ProgressBar) findViewById(R.id.pb_loading);
        windowTopBar = (FrameLayout) findViewById(R.id.window_top_bar);
        recoverScreen = (TextView) findViewById(R.id.tv_recover_screen);
        danmuSeekbar = (SeekBar) findViewById(R.id.danmaku_player_seek);
        tvOpenDanmu = (TextView) findViewById(R.id.tv_open_edit_danmaku);
        danmuCtl = (ImageView) findViewById(R.id.iv_danmaku_control);
        tvTag = (TextView) findViewById(R.id.tv_separator);
        tvMediaQuality = (TextView) findViewById(R.id.iv_media_quality);
        fullscreenTopBar = (LinearLayout) findViewById(R.id.fullscreen_top_bar);
        tvMarquee = (MarqueTextView) findViewById(R.id.tv_marquee);
        fullscreenBack = (ImageView) findViewById(R.id.iv_back);
        windowBack = (ImageView) findViewById(R.id.iv_back_window);
        tvVolume = (TextView) findViewById(R.id.tv_volume);
        tvBrightness = (TextView) findViewById(R.id.tv_brightness);
        guesterLayout = (FrameLayout) findViewById(R.id.fl_touch_layout);
        tvForward = (TextView) findViewById(R.id.tv_fast_forward);
        tvRewind = (TextView) findViewById(R.id.tv_fast_rewind);
        //path = "http://flv2.bn.netease.com/videolib3/1611/28/GbgsL3639/SD/movie_index.m3u8";
       // path="http://js.js.js.js:80/PLTV/3/224/3221226924/index.m3u8";
        //tvMarquee.setSelected(true);
        play_circle.setVisibility(View.VISIBLE);

        play_circle.setOnClickListener(this);
        ivPlayer.setOnClickListener(this);
        fullView.setOnClickListener(this);
        fullscreenBack.setOnClickListener(this);
        windowBack.setOnClickListener(this);

    }

    private void initMedia() {
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        videoView.setRender(IjkVideoView.RENDER_TEXTURE_VIEW);
        viewPlayerBox.setClickable(true);
        viewPlayerBox.setOnTouchListener(viewPlayerTouchListener);
        //视频监听
        videoView.setOnInfoListener(videoInfoListener);
        //playseekbar监听
        playSeekBar.setOnSeekBarChangeListener(playSeekBarChangeListener);
        danmuSeekbar.setOnSeekBarChangeListener(playSeekBarChangeListener);
        mGestureDetector = new GestureDetector(mActivity, videoGustureListener);

        initReceiver();
    }

    public ViewPlayer init() {
        initMedia();
        return this;
    }

    public ViewPlayer setSourcePath(String str){
        videoView.setVideoPath(str);
        isTVLive=true;
        return this;
    }

    public ViewPlayer setTitle(String title){
        tvMarquee.setText(title);
        return this;
    }
    public void playStart() {
        play_circle.setVisibility(View.GONE);
        play_circle.setSelected(true);
        videoView.start();
        //danmakuView.start();
        ivPlayer.setSelected(true);
        mhandle.sendEmptyMessage(MSG_PLAYSEEKBAR_UPDATE);
    }

    private void playPause() {
        play_circle.setVisibility(View.VISIBLE);
        play_circle.setSelected(false);
        videoView.pause();
        ivPlayer.setSelected(false);
//        danmakuView.pause();
        mhandle.removeMessages(MSG_PLAYSEEKBAR_UPDATE);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mInitHeight == 0) {
            mInitHeight = getHeight();
            mWidthPixels = getResources().getDisplayMetrics().widthPixels;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_play_circle:
            case R.id.iv_play:
                if (!videoView.isPlaying()) {
                    play_circle.setVisibility(View.GONE);
                    thumbView.setVisibility(View.GONE);
                    if(!isTVLive) durationTime = videoView.getDuration();
                    playSeekBar.setMax(durationTime);
                    danmuSeekbar.setMax(durationTime);
                    String info = TimeUtil.IntToHMS(durationTime);
                    tvEndTime.setText(info);
                    playStart();
                    if(isTVLive) tvLiveViewHide();
                    mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                } else {
                    playPause();
                }
                break;
            case R.id.iv_fullscreen:
                if (mActivity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    isFullScreen = false;
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                } else {
                    isFullScreen = true;
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                break;
            case R.id.iv_back:
                isFullScreen = false;
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case R.id.iv_back_window:
                _exit();
                break;
            case R.id.iv_screenshot:
                doShotPhoto();
                break;
            case R.id.iv_danmaku_control:
                if (danmakuView.isShown()) {
                    danmakuCrl.setSelected(false);
                    danmakuView.hide();
                } else {
                    danmakuCrl.setSelected(true);
                    danmakuView.show();
                }
                mhandle.postDelayed(hideAllViewRunnable, 1000);
                break;
            case R.id.tv_open_edit_danmaku:
                playPause();
                danmaku_layout.setVisibility(View.VISIBLE);
                NavUtils.setEditOnFocus(mActivity, danmakuEdit);
                break;
            case R.id.iv_cancel_send:
                playStart();
                danmaku_layout.clearFocus();
                NavUtils.closeSoftKeyboard(mActivity);
                danmaku_layout.setVisibility(View.GONE);
                danmakuEdit.setText("");
                mhandle.postDelayed(hideAllViewRunnable, 1000);
                break;
            case R.id.iv_do_send:
                playStart();
                danmaku_layout.clearFocus();
                addDanmaku(false, danmakuEdit.getText().toString(), true);
                NavUtils.closeSoftKeyboard(mActivity);
                danmakuEdit.setText("");
                danmaku_layout.setVisibility(View.GONE);
                mhandle.postDelayed(hideAllViewRunnable, 1000);
                break;
            default:
                break;
        }
    }

    /**
     * 电视直播时需要隐藏禁止相关控件
     */
    private void tvLiveViewHide() {
        danmakuCrl.setVisibility(View.VISIBLE);
        tvCurTime.setVisibility(View.INVISIBLE);
        tvEndTime.setVisibility(View.INVISIBLE);
        tvTag.setVisibility(View.INVISIBLE);
        if(isFullScreen){
            danmuSeekbar.setVisibility(View.GONE);
        }else{
            playSeekBar.setVisibility(View.INVISIBLE);
        }
    }

    private void doShotPhoto() {
        playPause();
        Bitmap bitmap = videoView.getShotScreen();
        sharedialog = ShareDialog.newInstance("");
        sharedialog.setPhoto(bitmap)
                .show(mActivity.getSupportFragmentManager(), "tag");
        mhandle.sendEmptyMessage(SHAREDIALOG_IS_SHOW);
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    private void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = mActivity.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = mActivity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    public boolean sharedSuccess() {
        if (sharedialog != null) {
            return sharedialog.DialogOver();
        }
        return false;
    }


    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        bottomBarViewReset(isFullScreen);
        if(isTVLive)tvLiveViewHide();
        if (Build.VERSION.SDK_INT > 14) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                View decorView = mActivity.getWindow().getDecorView();
                // 保存旧的配置
                oldUiVisibility = decorView.getSystemUiVisibility();
                layoutParams.height=mWidthPixels;
                mActivity.getSupportActionBar().hide();
                // 沉浸式使用这些Flag
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                );
                mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                View decorView = mActivity.getWindow().getDecorView();
                // 还原
                decorView.setSystemUiVisibility(oldUiVisibility);
                mActivity.getSupportActionBar().show();
                layoutParams.height=mInitHeight;
                mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
            setLayoutParams(layoutParams);
        }
        mhandle.postDelayed(hideAllViewRunnable, 1000);
    }


    private void bottomBarViewReset(boolean isFullScreen) {
        //recoverScreen.setVisibility(isFullScreen? View.VISIBLE:View.GONE);
        fullView.setSelected(isFullScreen);
        tvTag.setVisibility(isFullScreen ? View.VISIBLE : View.GONE);
        bottomBar.setVisibility(View.VISIBLE);
        danmuSeekbar.setVisibility(isFullScreen ? View.VISIBLE : View.GONE);
        tvOpenDanmu.setVisibility(isFullScreen ? View.VISIBLE : View.GONE);
        danmuCtl.setVisibility(isFullScreen ? View.VISIBLE : View.GONE);
        tvMediaQuality.setVisibility(isFullScreen ? View.VISIBLE : View.GONE);
        fullscreenTopBar.setVisibility(isFullScreen ? View.VISIBLE : View.GONE);
        windowTopBar.setVisibility(isFullScreen ? View.GONE : View.VISIBLE);
        playSeekBar.setVisibility(isFullScreen ? View.GONE : View.VISIBLE);
    }



    private boolean isLandscape;
    private OnGestureListener videoGustureListener = new SimpleOnGestureListener() {
        public boolean isDownTouch;
        private boolean isVolume;

        @Override
        public boolean onDown(MotionEvent e) {
            isDownTouch = true;
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            LogUtil.logUtilMyself("info", "x dis=" + (e2.getX() - e1.getX()));
            LogUtil.logUtilMyself("info", "distanceX=" + distanceX);
            float oldX = e1.getX();
            float oldY = e1.getY();
            if (isDownTouch) {
                isLandscape = Math.abs(e2.getX() - oldX) > Math.abs(e2.getY() - oldY);
                isVolume = oldX < getResources().getDisplayMetrics().widthPixels * 0.5f;
                isDownTouch = false;
            }
            float percent = (e2.getY() - oldY) / videoView.getHeight();
            float progress = (e2.getX() - oldX) / videoView.getWidth();
            if (isLandscape) {
                /**
                 * 直播时需要禁止此功能
                 */
                if(!isTVLive) setForward(progress);
            } else {
                if (isVolume) {
                    setVolume(percent);
                } else {
                    setBrightness(percent);
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (videoView.isPlaying()) {
                playPause();
            } else {
                playStart();
            }
            return true;
        }
    };

    private void setForward(float progress) {
        long sclTimeMax = Math.min(100 * 1000, durationTime / 2);
        long curTime = videoView.getCurrentPosition();
        mTime = curTime + (long) (sclTimeMax * progress);
        sclTime = Math.abs((mTime - curTime) / 1000);
        if (mTime <= 0) {
            mTime = 0;
            sclTime = curTime / 1000;
        } else if (mTime > durationTime) {
            mTime = durationTime;
            sclTime = (durationTime - curTime) / 1000;
        }
        String desc;
        if (mTime > curTime) {
            desc = TimeUtil.IntToHMS((int) mTime) + "/" + TimeUtil.IntToHMS(durationTime) + "\n" + "+" + String.valueOf((int) sclTime);
        } else {
            desc = TimeUtil.IntToHMS((int) mTime) + "/" + TimeUtil.IntToHMS(durationTime) + "\n" + "-" + String.valueOf((int) sclTime);
        }
        tvForward.setText(desc);
        if (guesterLayout.getVisibility() == View.GONE) {
            guesterLayout.setVisibility(View.VISIBLE);
        }
        if (tvForward.getVisibility() == View.GONE) {
            tvBrightness.setVisibility(View.GONE);
            tvVolume.setVisibility(View.GONE);
            tvForward.setVisibility(View.VISIBLE);
        }
        mhandle.removeCallbacks(hideAllViewRunnable);
        mhandle.postDelayed(hideAllViewRunnable, 1000);
    }

    private void setBrightness(float percent) {
        float mBrightness = mActivity.getWindow().getAttributes().screenBrightness;
        if (mBrightness < 0.01f) {
            mBrightness = 0.01f;
        }
        float targetBrightness = mBrightness + percent * 0.1f;
        if (targetBrightness > 1.0f) {
            targetBrightness = 1.0f;
        } else if (targetBrightness < 0.1f) {
            targetBrightness = 0.1f;
        }
        WindowManager.LayoutParams atr = mActivity.getWindow().getAttributes();
        atr.screenBrightness = targetBrightness;
        mActivity.getWindow().setAttributes(atr);
        tvBrightness.setText("" + (int) (targetBrightness * 100) + "%");
        if (guesterLayout.getVisibility() == View.GONE) {
            guesterLayout.setVisibility(View.VISIBLE);
        }
        if (tvBrightness.getVisibility() == View.GONE) {
            tvForward.setVisibility(View.GONE);
            tvVolume.setVisibility(View.GONE);
            tvBrightness.setVisibility(View.VISIBLE);
        }
        mhandle.removeCallbacks(hideAllViewRunnable);
        mhandle.postDelayed(hideAllViewRunnable, 1000);
    }

    private void setVolume(float percent) {
        AudioManager mAndioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        float mCurVolume = mAndioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float MAX_VOLUME = mAndioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float targetVolume = mCurVolume + percent * MAX_VOLUME / 6;
        if (targetVolume > MAX_VOLUME) {
            targetVolume = MAX_VOLUME;
        } else if (targetVolume < 0) {
            targetVolume = 0;
        }
        mAndioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) targetVolume, 0);
        tvVolume.setText("" + (int) (targetVolume * 100 / MAX_VOLUME) + "%");
        if (guesterLayout.getVisibility() == View.GONE) {
            guesterLayout.setVisibility(View.VISIBLE);
        }
        if (tvVolume.getVisibility() == View.GONE) {
            tvForward.setVisibility(View.GONE);
            tvBrightness.setVisibility(View.GONE);
            tvVolume.setVisibility(View.VISIBLE);
        }
        mhandle.removeCallbacks(hideAllViewRunnable);
        mhandle.postDelayed(hideAllViewRunnable, 1000);
    }


    private Runnable hideAllViewRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isFullScreen) {
                bottomBar.setVisibility(View.GONE);
                windowTopBar.setVisibility(View.GONE);
            } else {
                fullscreenTopBar.setVisibility(View.GONE);
                bottomBar.setVisibility(View.GONE);
                danmuSeekbar.setVisibility(View.GONE);
            }
            guesterLayout.setVisibility(View.GONE);
        }
    };

    private OnTouchListener viewPlayerTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            viewPlayerBox.onInterceptTouchEvent(event);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (isFullScreen) {
                        if (!bottomBar.isShown()) {
                            bottomBarViewReset(isFullScreen);
                            if(isTVLive)tvLiveViewHide();
                            fullscreenTopBar.setVisibility(View.VISIBLE);
                        } else {
                            bottomBar.setVisibility(View.GONE);
                            danmuSeekbar.setVisibility(View.GONE);
                            fullscreenTopBar.setVisibility(View.GONE);
                        }
                    } else {
                        danmuSeekbar.setVisibility(View.GONE);
                        if (!bottomBar.isShown()) {
                            bottomBar.setVisibility(View.VISIBLE);
                            windowTopBar.setVisibility(View.VISIBLE);
                        } else {
                            bottomBar.setVisibility(View.GONE);
                            windowTopBar.setVisibility(View.GONE);
                        }
                    }
                    break;
            }
            if (mGestureDetector.onTouchEvent(event)) {
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                /**
                 * 电视直播需禁止此方法
                 */
                if(!isTVLive) endGesture();
            }
            return false;
        }
    };

    private void endGesture() {
        if (mTime == 0 && isLandscape) {
            videoView.release(false);
            videoView.setRender(IjkVideoView.RENDER_TEXTURE_VIEW);
            tvEndTime.setText(videoView.getDuration());
            playStart();
        } else if (Math.abs(sclTime) > 1 && isLandscape) {
            videoView.seekTo((int) mTime);
            sclTime = -1;
            mhandle.postDelayed(hideAllViewRunnable, 1000);
        }
    }

    private IMediaPlayer.OnInfoListener videoInfoListener = new IMediaPlayer.OnInfoListener() {

        @Override
        public boolean onInfo(IMediaPlayer mp, int status, int extra) {
            setPlaySeekBar(status);
            LogUtil.logUtilMyself("tag", "status=" + status);
            LogUtil.logUtilMyself("tag", "extra=" + extra);
            return false;
        }
    };

    private SeekBar.OnSeekBarChangeListener playSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        private int curPosition;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mhandle.removeMessages(MSG_PLAYSEEKBAR_UPDATE);
            LogUtil.logUtilMyself("tag", "progress=" + progress);
            LogUtil.logUtilMyself("tag", "fromUser=" + fromUser);
            if (fromUser) {
                // targetTime = progress * durationTime / 1000;
                targetTime = progress;
                tvCurTime.setText(TimeUtil.IntToHMS(targetTime));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            showControlBar();
            curPosition = videoView.getCurrentPosition();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (targetTime == 0) {
                videoView.release(false);
                videoView.setRender(IjkVideoView.RENDER_TEXTURE_VIEW);
                tvEndTime.setText(TimeUtil.IntToHMS(videoView.getDuration()));
                playStart();
            } else {
                videoView.seekTo(targetTime);
                playStart();
            }
            seekBar.setProgress(targetTime);
            if (targetTime != 0) {
                int bufferValue = videoView.getBufferPercentage();
                seekBar.setSecondaryProgress(bufferValue * 10);
            }
            targetTime = -1;
            showControlBar();
            mhandle.postDelayed(hideAllViewRunnable, 1000);
        }
    };

    private void showControlBar() {
        fullscreenTopBar.setVisibility(isFullScreen ? View.VISIBLE : View.GONE);
        windowTopBar.setVisibility(isFullScreen ? View.GONE : View.VISIBLE);
        bottomBar.setVisibility(View.VISIBLE);
        danmuSeekbar.setVisibility(isFullScreen ? View.VISIBLE : View.GONE);
    }

    private void setPlaySeekBar(int status) {
        switch (status) {
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                networkConnected = true;
                loadingView.setVisibility(View.VISIBLE);
                mhandle.sendEmptyMessage(MSG_RELAOD);
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                loadingView.setVisibility(View.GONE);
                break;
            case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                isRenderingStart = true;
                loadingView.setVisibility(View.GONE);
                mhandle.sendEmptyMessage(MSG_PLAYSEEKBAR_UPDATE);
                break;
        }
    }




    public int onDestroy() {
        mActivity.unregisterReceiver(mBatteryReceiver);
        mActivity.unregisterReceiver(mNetConnectReceiver);
        mActivity.unregisterReceiver(mScreenReceiver);
        int a = videoView.getCurrentPosition();
        videoView.release(true);
        danmakuView.hideAndPauseDrawTask();
        return a;
    }

    public void onResume(){
        if(interruptPosition!=-1){
            videoView.seekTo(interruptPosition);
        }
    }

    public void onPause() {
        interruptPosition= videoView.getCurrentPosition();
        mhandle.removeMessages(SHAREDIALOG_IS_SHOW);
        playPause();
    }

    public boolean onBackPressed() {
        if (isFullScreen) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return false;
        } else {
            _exit();
            return true;
        }
    }

    private void _exit() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(mActivity, "再按一次退出", Toast.LENGTH_LONG).show();
            exitTime = System.currentTimeMillis();
        } else {
            mActivity.unregisterReceiver(mBatteryReceiver);
            mActivity.unregisterReceiver(mNetConnectReceiver);
            mActivity.unregisterReceiver(mScreenReceiver);
            mActivity.finish();
        }
    }






    private void initReceiver() {
        batteryProBar = (ProgressBar) findViewById(R.id.pb_battery);
        sysTime = (TextView) findViewById(R.id.tv_system_time);
        screenShot = (ImageView) findViewById(R.id.iv_screenshot);
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
        sysTime.setText(sf.format(System.currentTimeMillis()));
        mBatteryReceiver = new BatteryReceiver();
        mNetConnectReceiver = new NetConnectReceiver();
        mScreenReceiver = new ScreenReceiver();
        mActivity.registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        mActivity.registerReceiver(mNetConnectReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        mActivity.registerReceiver(mScreenReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        screenShot.setClickable(true);
        screenShot.setOnClickListener(this);
        createSaveDir(mActivity.getCacheDir().getPath() + File.separator + "ijkPlayerScreenShot");
    }

    private void createSaveDir(String path) {
        FileUtil.CreateFileDir(path);
    }

    class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                int level = intent.getIntExtra("level", 0);
                int scale = intent.getIntExtra("scale", 100);
                int status = intent.getIntExtra("status", BatteryManager.BATTERY_HEALTH_UNKNOWN);
                int curPower = level * 100 / scale;
                if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                    batteryProBar.setProgress(curPower);
                    batteryProBar.setSecondaryProgress(0);
                    batteryProBar.setBackgroundResource(R.mipmap.ic_battery_charging);
                } else if (curPower < 20) {
                    batteryProBar.setProgress(0);
                    batteryProBar.setSecondaryProgress(curPower);
                    batteryProBar.setBackgroundResource(R.mipmap.ic_battery_red);
                } else {
                    batteryProBar.setProgress(curPower);
                    batteryProBar.setSecondaryProgress(0);
                    batteryProBar.setBackgroundResource(R.mipmap.ic_battery);
                }
            }
        }
    }

    class NetConnectReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                isNetConnected = true;
            }
        }
    }

    /**
     * 锁屏状态接收
     */
    class ScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                isScreenLocked = true;
            }
        }
    }

    public ViewPlayer enableDanmaku(boolean istrue) {
        if(istrue){
            isEnableDanmaku = true;
            initDanmaku();
        }else{
            isEnableDanmaku = false;
            initDanmaku();
            danmakuView.hideAndPauseDrawTask();
        }
        return this;
    }

    private void initDanmaku() {
        danmakuView = (IDanmakuView) findViewById(R.id.dm_danmaku);
        danmakuCrl = (ImageView) findViewById(R.id.iv_danmaku_control);
        danmakuOpenEdit = (TextView) findViewById(R.id.tv_open_edit_danmaku);
        danmaku_layout = (LinearLayout) findViewById(R.id.ll_edit_danmaku);
        danmakuCancel = (ImageView) findViewById(R.id.iv_cancel_send);
        danmakuSend = (ImageView) findViewById(R.id.iv_do_send);
        danmakuEdit = (EditText) findViewById(R.id.et_danmaku_content);

        int navigationBarHeight = NavUtils.getNavigationHeight(mActivity);
        if (navigationBarHeight > 0) {
            // 对于有虚拟键的设备需要将弹幕编辑布局右偏移防止被覆盖
            danmaku_layout.setPadding(0, 0, navigationBarHeight, 0);
        }


        danmakuCrl.setSelected(true);
        danmakuCrl.setOnClickListener(this);
        danmakuOpenEdit.setOnClickListener(this);
        danmakuCancel.setOnClickListener(this);
        danmakuSend.setOnClickListener(this);
        loadDanmaku();
    }

    private void loadDanmaku() {
        mContext = DanmakuContext.create();
        if (isEnableDanmaku) {

            //设置最大显示行数
            HashMap<Integer, Integer> maxLinePair = new HashMap<>();
            maxLinePair.put(BaseDanmaku.TYPE_SCROLL_LR, 5);
            //设置是否禁止重叠
            HashMap<Integer, Boolean> overlappingEnable = new HashMap<>();
            overlappingEnable.put(BaseDanmaku.TYPE_SCROLL_LR, true);
            overlappingEnable.put(BaseDanmaku.TYPE_FIX_BOTTOM, true);
            mContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3)
                    .setDuplicateMergingEnabled(false)
                    .setScrollSpeedFactor(1.2f)
                    .setScaleTextSize(1.2f)
                    .setMaximumLines(maxLinePair)
                    .setOverlapping(overlappingEnable);
        }
        if (danmakuView != null) {
            mParser = createParser(this.getResources().openRawResource(R.raw.bili));
            danmakuView.setCallback(new DrawHandler.Callback() {
                @Override
                public void prepared() {
                    danmakuView.start();
                }

                @Override
                public void updateTimer(DanmakuTimer timer) {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {

                }

                @Override
                public void drawingFinished() {

                }
            });
        }
        danmakuView.prepare(mParser, mContext);
        //danmakuView.showFPS(true);
        danmakuView.enableDanmakuDrawingCache(true);
    }

    private BaseDanmakuParser createParser(InputStream inputStream) {
        if (inputStream == null) {
            return new BaseDanmakuParser() {
                @Override
                protected IDanmakus parse() {
                    return new Danmakus();
                }
            };
        }
        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);
        try {
            loader.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BiliXMLDanmakuParser parser = new BiliXMLDanmakuParser();
        IDataSource<?> datasource = loader.getDataSource();
        parser.load(datasource);
        return parser;
    }

    public void addDanmaku(boolean isLive, String msg, boolean isUser) {
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_LR);
        if (danmaku == null || danmakuView == null) {
            return;
        }
        danmaku.text = msg;
        danmaku.padding = 5;
        danmaku.priority = 1;
        danmaku.isLive = isLive;
        danmaku.setTime(danmakuView.getCurrentTime() + 1200);
        danmaku.textSize = 18f * (mParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = Color.WHITE;
        danmaku.textShadowColor = Color.parseColor("#333333");
        if (isUser) {
            danmaku.borderColor = Color.YELLOW;
            danmakuView.addDanmaku(danmaku);
        }
    }
}
