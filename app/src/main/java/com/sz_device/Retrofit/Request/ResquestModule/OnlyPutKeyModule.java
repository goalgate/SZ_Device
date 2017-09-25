package com.sz_device.Retrofit.Request.ResquestModule;

import com.blankj.utilcode.util.SPUtils;

import org.simpleframework.xml.Element;

/**
 * Created by zbsz on 2017/9/21.
 */

public class OnlyPutKeyModule implements IRequestModule {
    private static final String PREFS_NAME = "UserInfo";
    public int method;

    @Element(name = "key", required = false)
    public String key;

    public OnlyPutKeyModule(int method) {
        this.method = method;
        this.key = SPUtils.getInstance(PREFS_NAME).getString("jsonKey");
    }

    @Override
    public String getJSON() {
        return null;
    }

    @Override
    public int getMethod() {
        return method;
    }
}
