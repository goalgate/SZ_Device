package com.sz_device.Retrofit.Request.ResquestModule;

import org.simpleframework.xml.Element;

/**
 * Created by zbsz on 2017/9/21.
 */

public class OnlyPutKeyModule implements IRequestModule {

    public int method;

    @Element(name = "key", required = false)
    public String key;

    public OnlyPutKeyModule(int method,String key) {
        this.method = method;
        this.key = key;
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
