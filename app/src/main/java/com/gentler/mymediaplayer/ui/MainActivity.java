package com.gentler.mymediaplayer.ui;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.gentler.mymediaplayer.App;
import com.gentler.mymediaplayer.R;
import com.gentler.mymediaplayer.params.MyParams;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {


    private static final String TAG = MainActivity.class.getSimpleName();
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
    public MediaPlayer mMediaPlayer;
    private UIHandler mUIHandler;
    private TimerTask mTimeTask;
    private static final int UPDATE_SEEK_BAR = 1314;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        initListener();
        initTimerTask();
        initData();
    }


    private void initView() {
        mMediaPlayer = new MediaPlayer();
        mUIHandler = new UIHandler(MainActivity.this);
        mTextureView.setSurfaceTextureListener(this);

    }

    private void initListener() {
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                int duration=mp.getDuration();
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

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Log.e(TAG, "progress:" + progress);
                    mMediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initData() {
        try {
            mMediaPlayer.setDataSource(MyParams.URL_BACKUP);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_play)
    public void onClickPlay(View view){
        if(null!=mMediaPlayer){
            mMediaPlayer.start();
        }
    }

    @OnClick(R.id.btn_pause)
    protected void onClickPause(View view){
        if (null!=mMediaPlayer)
        mMediaPlayer.pause();
    }

    @OnClick(R.id.btn_seek)
    public void onClickSeek(View view){
        if (null!=mMediaPlayer){
            mMediaPlayer.seekTo(20000);
        }
    }
    @OnClick(R.id.btn_go_to)
    public void onClickGoTo(View view){
        Intent intent = new Intent(this, VideoDetailActivity.class);
        int pos = mMediaPlayer.getCurrentPosition();
        App.getInstance().getMediaHashMap().put(MyParams.URL_BACKUP,mMediaPlayer);
        intent.putExtra(MyParams.INTENT_VIDEO_POS, pos);
        startActivity(intent);
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
        private WeakReference<MainActivity> mWeakReference;

        public UIHandler(MainActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_SEEK_BAR:
                    MainActivity activity = mWeakReference.get();
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
        Surface surface1=new Surface(surface);
        mMediaPlayer.setSurface(surface1);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null!=mMediaPlayer){
            mMediaPlayer.pause();
//            mMediaPlayer.release();
//            mMediaPlayer=null;
        }

    }
}
