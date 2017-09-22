package com.sz_device.Retrofit.Request.ResquestModule;

import org.simpleframework.xml.Element;

/**
 * Created by zbsz on 2017/9/21.
 */

public class CommonRequestModule implements IRequestModule {

    public int method;


    @Element(name = "key")
    public String key;


    @Element(name = "jsonData" )
    public String jsonData;

    public CommonRequestModule(int method,String key,String jsonData) {
        this.method = method;
        this.key = key;
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

