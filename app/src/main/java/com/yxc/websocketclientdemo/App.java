package com.yxc.websocketclientdemo;

import android.app.Application;

import com.yxc.websocketclientdemo.util.SPUtils;

public class App extends Application {

    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        SPUtils.getDefault().init(this, SPUtils.KEY_SP_NAME);
    }

    public static App getInstance(){
        return mInstance;
    }

}
