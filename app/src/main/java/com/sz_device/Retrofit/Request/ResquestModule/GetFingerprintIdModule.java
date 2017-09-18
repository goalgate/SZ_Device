package com.sz_device.Retrofit.Request.ResquestModule;

import org.simpleframework.xml.Element;

/**
 * Created by zbsz on 2017/6/29.
 */

public class GetFingerprintIdModule implements IRequestModule {

    @Element(name = "key", required = false)
    public String key;

    public GetFingerprintIdModule(String key) {
        this.key = key;
    }
}
