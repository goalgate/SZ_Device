package com.sz_device.Retrofit.Request.ResquestModule;

import com.blankj.utilcode.util.SPUtils;

import org.simpleframework.xml.Element;

/**
 * Created by zbsz on 2017/9/21.
 */

public class CommonRequestModule implements IRequestModule {

    private static final String PREFS_NAME = "UserInfo";

    public int method;


    @Element(name = "key")
    public String key;


    @Element(name = "jsonData" )
    public String jsonData;

    public CommonRequestModule(int method,String jsonData) {
        this.method = method;
        this.key = SPUtils.getInstance(PREFS_NAME).getString("jsonKey");
        this.jsonData = jsonData;
    }

    @Override
    public int getMethod() {
        return method;
    }

    @Override
    public String getJSON() {
        return jsonData;
    }
}

