package com.sz_device.Retrofit.Request.ResquestModule;

import org.simpleframework.xml.Element;

/**
 * Created by zbsz on 2017/9/21.
 */

public class OnlyPutKeyModule implements IRequestModule {

    @Element(name = "key", required = false)
    public String key;

    public OnlyPutKeyModule(String key) {
        this.key = key;
    }
}
