package com.gentler.mymediaplayer.ui;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Button;
import android.widget.SeekBar;

import com.gentler.mymediaplayer.App;
import com.gentler.mymediaplayer.R;
import com.gentler.mymediaplayer.params.MyParams;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/11/16.
 */

public class VideoDetailActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    @BindView(R.id.btn_go_to)
    Button mBtnGoTo;
    @BindView(R.id.btn_pause)
    Button mBtnPause;
    @BindView(R.id.texture_view)
    TextureView mTextureView;
    @BindView(R.id.btn_seek)
    Button mBtnSeek;
    @BindView(R.id.seek_bar)
    SeekBar mSeekBar;
    private MediaPlayer mMediaPlayer;
    private UIHandler mUIHandler;
    private TimerTask mTimeTask;
    private static final int UPDATE_SEEK_BAR = 1314;
    private MediaPlayer nextMedia;
    private int currPos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        ButterKnife.bind(this);
        initView();
//        initListener();
        initTimerTask();
        initData();
    }

    private void initView() {
        Intent intent=getIntent();
        if (intent.hasExtra(MyParams.INTENT_VIDEO_POS)){
            currPos = intent.getIntExtra(MyParams.INTENT_VIDEO_POS,0);
        }
        mMediaPlayer = App.getInstance().getMediaHashMap().get(MyParams.URL_BACKUP);
        int duration=mMediaPlayer.getDuration();
        mSeekBar.setMax(duration);
        mSeekBar.setProgress(currPos);
        mUIHandler = new UIHandler(VideoDetailActivity.this);
        mTextureView.setSurfaceTextureListener(this);
    }

    private void initListener() {
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                int duration = mp.getDuration();
                mSeekBar.setMax(duration);

                mMediaPlayer.start();
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });

        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {

            }
        });
    }

    private void initData() {
        nextMedia = App.getInstance().getMediaHashMap().get(MyParams.URL_BACKUP);
    }



    private void initTimerTask() {
        Timer timer = new Timer();
        mTimeTask = getTimerTask();
        timer.schedule(mTimeTask, 300, 300);
    }


    private TimerTask getTimerTask() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                mUIHandler.sendEmptyMessageDelayed(UPDATE_SEEK_BAR, 500);
            }
        };
        return timerTask;
    }

    private static class UIHandler extends Handler {
        private WeakReference<VideoDetailActivity> mWeakReference;

        public UIHandler(VideoDetailActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_SEEK_BAR:
                    VideoDetailActivity activity = mWeakReference.get();
                    if (null != activity) {
                        int currPos = activity.mMediaPlayer.getCurrentPosition();
                        activity.mSeekBar.setProgress(currPos);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Surface surface1 = new Surface(surface);
        mMediaPlayer.setSurface(surface1);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (null != mMediaPlayer) {
            mMediaPlayer.pause();
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


}
