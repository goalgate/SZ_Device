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
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.downPersonFingerprintInfo;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.downPersonInfo;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.getFingerPrint;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.openDoorRecord;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.queryPersonInfo;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.registerPerson;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.stateRecord;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.testNet;


@Root(name = "soapenv:Body", strict = false)
public class RequestBody {

    @Element(name = "inf:testNet", required = false)
    public IRequestModule testNetModule;

    @Element(name = "inf:checkOnline", required = false)
    public IRequestModule checkOnlineModule;

    @Element(name = "inf:stateRecord", required = false)
    public IRequestModule stateRecordModule;

    @Element(name = "inf:openDoorRecord", required = false)
    public IRequestModule openDoorRecordModule;

    @Element(name = "inf:closeDoorRecord", required = false)
    public IRequestModule closeDoorRecordModule;

    @Element(name = "inf:getFingerprintId", required = false)
    public IRequestModule getFingerprintIdModule;

    @Element(name = "inf:registerPerson", required = false)
    public IRequestModule registerPersonModule;

    @Element(name = "inf:queryPersonInfo", required = false)
    public IRequestModule queryPersonInfoModule;

    @Element(name = "inf:checkRecord", required = false)
    public IRequestModule checkRecordModule;

    @Element(name = "inf:alarmRecord", required = false)
    public IRequestModule alarmRecordModule;

    @Element(name = "inf:alarmCease", required = false)
    public IRequestModule alarmCeaseModule;

    @Element(name = "inf:downPersonInfo", required = false)
    public IRequestModule downPersonInfoModule;

    @Element(name = "inf:downPersonFingerprintInfo", required = false)
    public IRequestModule downPersonFingerprintInfoModule;

    public RequestBody(IRequestModule module) {
        switch (module.getMethod()) {
            case testNet:
                testNetModule = module;
                break;
            case checkOnline:
                checkOnlineModule = module;
                break;
            case stateRecord:
                stateRecordModule = module;
                break;
            case openDoorRecord:
                openDoorRecordModule = module;
                break;
            case closeDoorRecord:
                closeDoorRecordModule = module;
                break;
            case getFingerPrint:
                getFingerprintIdModule = module;
                break;
            case queryPersonInfo:
                queryPersonInfoModule = module;
                break;
            case registerPerson:
                registerPersonModule = module;
                break;
            case checkRecord:
                checkRecordModule = module;
                break;
            case alarmRecord:
                alarmRecordModule = module;
                break;
            case alarmCease:
                alarmCeaseModule = module;
                break;
            case downPersonInfo:
                downPersonInfoModule = module;
                break;
            case downPersonFingerprintInfo:
                downPersonFingerprintInfoModule = module;
                break;
            default:
                ToastUtils.showLong("接口方法错误");


        }

    }
}