package com.sz_device.Retrofit.Request.ResquestModule;

import org.simpleframework.xml.Element;

/**
 * Created by zbsz on 2017/9/14.
 */

public class QueryPersonInfoModule implements IRequestModule {

    @Element(name = "key")
    public String key;


    @Element(name = "id" )
    public String id;

    public QueryPersonInfoModule(String key,String id) {
        this.key = key;
        this.id = id;
    }
}
