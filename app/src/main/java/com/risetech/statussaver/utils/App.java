package com.risetech.statussaver.utils;

import android.content.Context;

import com.risetech.statussaver.ads.AdManger;

public class App extends android.app.Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
        AdManger.init(this);
    }
}
