package com.sz_device.Retrofit.Request.ResquestModule;

import org.simpleframework.xml.Element;

/**
 * Created by zbsz on 2017/9/18.
 */

public class AlarmRecordModule implements IRequestModule{

    @Element(name = "key")
    public String key;


    @Element(name = "jsonData" )
    public String jsonData;

    public AlarmRecordModule(String key,String jsonData) {
        this.key = key;
        this.jsonData = jsonData;
    }

}
