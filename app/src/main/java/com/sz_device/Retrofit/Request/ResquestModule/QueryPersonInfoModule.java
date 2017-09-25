package com.sz_device.Retrofit.Request.ResquestModule;

import android.os.Message;

import com.blankj.utilcode.util.SPUtils;

import org.json.JSONArray;
import org.simpleframework.xml.Element;

import java.lang.reflect.Method;

import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.queryPersonInfo;

/**
 * Created by zbsz on 2017/9/14.
 */

public class QueryPersonInfoModule implements IRequestModule {
    private static final String PREFS_NAME = "UserInfo";

    @Element(name = "key")
    public String key;


    @Element(name = "id" )
    public String id;

    public QueryPersonInfoModule( String id) {
        this.key = SPUtils.getInstance(PREFS_NAME).getString("jsonKey");
        this.id = id;
    }

    @Override
    public int getMethod() {
        return queryPersonInfo;
    }

    @Override
    public String getJSON() {
        return id;
    }
}
