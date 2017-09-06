package com.sz_device.Tools;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zbsz on 2017/5/16.
 */

public class AppActivitys {
    private List<Activity> activityList = new LinkedList<Activity>();

    private static AppActivitys instance;

    private AppActivitys() {

    }

    //init
    public static AppActivitys getInstance() {
        if (null == instance) {
            instance = new AppActivitys();
        }
        return instance;
    }

    public void addActivity(Activity activity) {
        if (!activityList.contains(activity)) {
            activityList.add(activity);
        }
    }

    //finish all
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
