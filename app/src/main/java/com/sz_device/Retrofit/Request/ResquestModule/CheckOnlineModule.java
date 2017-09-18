package com.sz_device.Retrofit.Request.ResquestModule;

import org.simpleframework.xml.Element;

/**
 * Created by zbsz on 2017/7/3.
 */

public class CheckOnlineModule implements IRequestModule {

    @Element(name = "key", required = false)
    public String key;

    public CheckOnlineModule(String key) {
        this.key = key;
    }
}
