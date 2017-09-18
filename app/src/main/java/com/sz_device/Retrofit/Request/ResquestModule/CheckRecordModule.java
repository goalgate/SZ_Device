package com.sz_device.Retrofit.Request.ResquestModule;

import org.simpleframework.xml.Element;

/**
 * Created by zbsz on 2017/9/15.
 */

public class CheckRecordModule implements IRequestModule {

    @Element(name = "key")
    public String key;


    @Element(name = "jsonData" )
    public String jsonData;

    public CheckRecordModule(String key,String jsonData) {
        this.key = key;
        this.jsonData = jsonData;
    }
}
