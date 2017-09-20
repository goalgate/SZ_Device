package com.sz_device;

import android.app.Application;
import android.content.Context;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.log.Lg;
import com.squareup.leakcanary.LeakCanary;
import com.sz_device.Tools.DESX;
import com.sz_device.Tools.NetInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zbsz on 2017/8/25.
 */

public class AppInit extends Application {
    protected static AppInit instance;

    private static final String PREFS_NAME = "UserInfo";

    public static AppInit getInstance() {
        return instance;
    }

    public static Context getContext() {
        return getInstance().getApplicationContext();
    }

    SPUtils User_SP;

    @Override
    public void onCreate() {

        super.onCreate();

        Lg.setIsSave(true);

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }

        LeakCanary.install(this);

        instance = this;

        Utils.init(getContext());

        User_SP = SPUtils.getInstance(PREFS_NAME);

        if (User_SP.getBoolean("firstStart", true)) {
            JSONObject jsonKey = new JSONObject();
            try {
                jsonKey.put("daid", new NetInfo().getMacId());
                jsonKey.put("check", DESX.encrypt(new NetInfo().getMacId()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            User_SP.put("firstStart", false);
            User_SP.put("dev_id", new NetInfo().getMacId());
            User_SP.put("jsonKey", DESX.encrypt(jsonKey.toString()));
        }
    }
}
