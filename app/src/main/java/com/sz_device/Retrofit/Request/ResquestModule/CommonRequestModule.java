package com.sz_device.Retrofit.Request.ResquestModule;

import org.simpleframework.xml.Element;

/**
 * Created by zbsz on 2017/9/21.
 */

public class CommonRequestModule implements IRequestModule {
    @Element(name = "key")
    public String key;


    @Element(name = "jsonData" )
    public String jsonData;

    public CommonRequestModule(String key,String jsonData) {
        this.key = key;
        this.jsonData = jsonData;
    }


}

