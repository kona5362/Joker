package com.kona.joker;

import android.app.Application;

/**
 * Created by kona on 2018/1/25.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ExceptionCrashHandler.getmInstance().init(this);
    }
}
