package com.sz_device;

import android.app.Application;
import android.content.Context;

import com.blankj.utilcode.util.Utils;
import com.log.Lg;
import com.squareup.leakcanary.LeakCanary;
import com.ys.myapi.MyManager;


/**
 * Created by zbsz on 2017/8/25.
 */

public class AppInit extends Application {
    protected static AppInit instance;

    public static AppInit getInstance() {
        return instance;
    }

    protected static MyManager manager;

    public static MyManager getMyManager() {
        return manager;
    }

    public static Context getContext() {
        return getInstance().getApplicationContext();
    }

    @Override
    public void onCreate() {

        super.onCreate();

        Lg.setIsSave(true);

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }

        LeakCanary.install(this);

        instance = this;

        manager = MyManager.getInstance(this);

        Utils.init(getContext());

    }



}

