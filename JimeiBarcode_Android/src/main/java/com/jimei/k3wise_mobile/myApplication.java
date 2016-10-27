package com.jimei.k3wise_mobile;

import android.app.Application;

import com.jimei.k3wise_mobile.Util.CrashHandler;

/**
 * Created by lee on 2016/10/11.
 */

public class myApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext(), this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        System.exit(0);
    }
}
