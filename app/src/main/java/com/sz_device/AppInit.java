package com.sz_device;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.blankj.utilcode.util.Utils;
import com.log.Lg;
import com.squareup.leakcanary.LeakCanary;
import com.sz_device.Config.BaseConfig;

import com.sz_device.Config.HNMBY_Config;
import com.sz_device.Config.JMZH_Config;
import com.sz_device.Config.LN_Config;
import com.sz_device.Config.SZ_Config;
import com.sz_device.Config.WYY_Config;
import com.sz_device.Config.ZJYZB_Config;
import com.sz_device.greendao.DaoMaster;
import com.sz_device.greendao.DaoSession;
import com.ys.myapi.MyManager;


/**
 * Created by zbsz on 2017/8/25.
 */

public class AppInit extends Application {

    private DaoMaster.DevOpenHelper mHelper;

    private SQLiteDatabase db;

    private DaoMaster mDaoMaster;

    private static BaseConfig config;

    private DaoSession mDaoSession;

    protected static AppInit instance;

    public static AppInit getInstance() {
        return instance;
    }

    public static BaseConfig getInstrumentConfig(){
        return config;
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

        config = new HNMBY_Config();

        manager = MyManager.getInstance(this);

        manager.bindAIDLService(this);

        Utils.init(getContext());

        setDatabase();
    }

    private void setDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(this, "reUpload-db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

}

