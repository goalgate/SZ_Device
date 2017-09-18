package com.sz_device.Retrofit.Request.ResquestModule;

import org.simpleframework.xml.Element;

/**
 * Created by zbsz on 2017/7/13.
 */

public class TestNetModule implements IRequestModule {

    @Element(name = "key", required = false)
    public String key;

    public TestNetModule(String key) {
        this.key = key;
    }
}
