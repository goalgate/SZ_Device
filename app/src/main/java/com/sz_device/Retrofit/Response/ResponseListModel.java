package com.sz_device.Retrofit.Response;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by zbsz on 2017/8/15.
 */

@Root
public class ResponseListModel {

    @ElementList(entry = "return" , inline=true)
    public List<String> Listinfo;
}
