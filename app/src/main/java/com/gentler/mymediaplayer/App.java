package com.gentler.mymediaplayer;

import android.app.Application;
import android.media.MediaPlayer;

import java.util.HashMap;

/**
 * Created by admin on 2017/11/16.
 */

public class App extends Application {

    private  HashMap<String,MediaPlayer> mMediaHashMap=new HashMap<>(3);
    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance=this;
    }

    public static App getInstance(){
        return mInstance;
    }

    public HashMap<String,MediaPlayer> getMediaHashMap(){
        return mMediaHashMap;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }


}
