package com.sz_device.Retrofit.Request;

import com.blankj.utilcode.util.ToastUtils;
import com.sz_device.Retrofit.Request.ResquestModule.CommonRequestModule;
import com.sz_device.Retrofit.Request.ResquestModule.IRequestModule;
import com.sz_device.Retrofit.Request.ResquestModule.OnlyPutKeyModule;
import com.sz_device.Retrofit.Request.ResquestModule.QueryPersonInfoModule;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.alarmCease;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.alarmRecord;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.checkOnline;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.checkRecord;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.closeDoorRecord;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.getFingerPrint;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.openDoorRecord;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.queryPersonInfo;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.registerPerson;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.stateRecord;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.testNet;


@Root(name = "soapenv:Body", strict = false)
public class RequestBody {

    @Element(name = "inf:testNet", required = false)
    public OnlyPutKeyModule testNetModule;

    @Element(name = "inf:checkOnline", required = false)
    public OnlyPutKeyModule checkOnlineModule;

    @Element(name = "inf:stateRecord", required = false)
    public CommonRequestModule stateRecordModule;

    @Element(name = "inf:openDoorRecord", required = false)
    public CommonRequestModule openDoorRecordModule;

    @Element(name = "inf:closeDoorRecord", required = false)
    public CommonRequestModule closeDoorRecordModule;

    @Element(name = "inf:getFingerprintId", required = false)
    public OnlyPutKeyModule getFingerprintIdModule;

    @Element(name = "inf:registerPerson", required = false)
    public CommonRequestModule registerPersonModule;

    @Element(name = "inf:queryPersonInfo", required = false)
    public QueryPersonInfoModule queryPersonInfoModule;

    @Element(name = "inf:checkRecord", required = false)
    public CommonRequestModule checkRecordModule;

    @Element(name = "inf:alarmRecord", required = false)
    public CommonRequestModule alarmRecordModule;

    @Element(name = "inf:alarmCease", required = false)
    public CommonRequestModule alarmCeaseModule;

    public RequestBody(int method, IRequestModule module) {
        switch (method) {
            case testNet:
                testNetModule = (OnlyPutKeyModule) module;
                break;
            case checkOnline:
                checkOnlineModule = (OnlyPutKeyModule) module;
                break;
            case stateRecord:
                stateRecordModule = (CommonRequestModule) module;
                break;
            case openDoorRecord:
                openDoorRecordModule = (CommonRequestModule) module;
                break;
            case closeDoorRecord:
                closeDoorRecordModule = (CommonRequestModule) module;
                break;
            case getFingerPrint:
                getFingerprintIdModule = (OnlyPutKeyModule) module;
                break;
            case queryPersonInfo:
                queryPersonInfoModule = (QueryPersonInfoModule) module;
                break;
            case registerPerson:
                registerPersonModule = (CommonRequestModule) module;
                break;
            case checkRecord:
                checkRecordModule = (CommonRequestModule) module;
                break;
            case alarmRecord:
                alarmRecordModule = (CommonRequestModule) module;
                break;
            case alarmCease:
                alarmCeaseModule = (CommonRequestModule) module;
                break;
            default:
                ToastUtils.showLong("接口方法错误");

        }

    }
}