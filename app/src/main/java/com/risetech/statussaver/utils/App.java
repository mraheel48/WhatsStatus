package com.risetech.statussaver.utils;

import android.content.Context;
import com.risetech.statussaver.ads.AdManager;

public class App extends android.app.Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        AdManager.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
