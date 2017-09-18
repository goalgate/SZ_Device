package com.sz_device.Retrofit.Request;



import com.sz_device.Retrofit.Request.ResquestModule.IRequestModule;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;


/**
 * 用户角色请求Envelope
 * Created by SmileXie on 16/7/15.
 */
@Root(name = "soapenv:Envelope")
@NamespaceList({
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi"),
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema", prefix = "xsd"),
        @Namespace(reference = "http://schemas.xmlsoap.org/soap/encoding/", prefix = "enc"),
        @Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope/", prefix = "soapenv"),
        @Namespace(reference = "http://inf.sz.da.cbdi.cn/", prefix = "inf")
})
public class RequestEnvelope {
    @Element(name = "soapenv:Body", required = false)
    public RequestBody body;


    public static RequestEnvelope GetRequestEnvelope(IRequestModule module) {
        RequestEnvelope requestEnvelope = new RequestEnvelope();
        RequestBody body = new RequestBody(module);
        requestEnvelope.body = body;
        return requestEnvelope;
    }

}