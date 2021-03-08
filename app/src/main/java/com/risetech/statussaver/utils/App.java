package com.risetech.statussaver.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.google.android.gms.ads.MobileAds;
import com.risetech.statussaver.ads.AdManager;

public class App extends android.app.Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
        MobileAds.initialize(context);
        AdManager.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
