package com.sz_device.Retrofit.Request;




import com.sz_device.Fun_Switching.mvp.presenter.SwitchPresenter;
import com.sz_device.Retrofit.Request.ResquestModule.AlarmRecordModule;
import com.sz_device.Retrofit.Request.ResquestModule.CheckOnlineModule;
import com.sz_device.Retrofit.Request.ResquestModule.CheckRecordModule;
import com.sz_device.Retrofit.Request.ResquestModule.CloseDoorRecordModule;
import com.sz_device.Retrofit.Request.ResquestModule.GetFingerprintIdModule;
import com.sz_device.Retrofit.Request.ResquestModule.IRequestModule;
import com.sz_device.Retrofit.Request.ResquestModule.OpenDoorRecordModule;
import com.sz_device.Retrofit.Request.ResquestModule.QueryPersonInfoModule;
import com.sz_device.Retrofit.Request.ResquestModule.RegisterPersonModule;
import com.sz_device.Retrofit.Request.ResquestModule.StateRecordModule;
import com.sz_device.Retrofit.Request.ResquestModule.TestNetModule;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * 用户角色返回body
 * Created by SmileXie on 16/7/15.
 */
@Root(name = "soapenv:Body", strict = false)
public class RequestBody {

    @Element(name = "inf:testNet", required = false)
    public TestNetModule testNetModule;

    @Element(name = "inf:checkOnline", required = false)
    public CheckOnlineModule checkOnlineModule;

    @Element(name = "inf:stateRecord", required = false)
    public StateRecordModule stateRecordModule;

    @Element(name = "inf:openDoorRecord", required = false)
    public OpenDoorRecordModule openDoorRecord;

    @Element(name = "inf:closeDoorRecord", required = false)
    public CloseDoorRecordModule closeDoorRecord;

    @Element(name = "inf:getFingerprintId", required = false)
    public GetFingerprintIdModule getFingerprintId;

    @Element(name = "inf:registerPerson", required = false)
    public RegisterPersonModule registerPersonModule;

    @Element(name = "inf:queryPersonInfo", required = false)
    public QueryPersonInfoModule queryPersonInfoModule;

    @Element(name = "inf:checkRecord", required = false)
    public CheckRecordModule checkRecordModule;

    @Element(name = "inf:alarmRecord", required = false)
    public AlarmRecordModule alarmRecordModule;

    public RequestBody(IRequestModule module) {
        if (module.getClass().getName().equals(TestNetModule.class.getName())) {
            testNetModule = (TestNetModule) module;
        }else if (module.getClass().getName().equals(CheckOnlineModule.class.getName())) {
            checkOnlineModule = (CheckOnlineModule) module;
        }else if (module.getClass().getName().equals(StateRecordModule.class.getName())) {
            stateRecordModule = (StateRecordModule) module;
        }else if (module.getClass().getName().equals(OpenDoorRecordModule.class.getName())) {
            openDoorRecord = (OpenDoorRecordModule) module;
        }else if (module.getClass().getName().equals(CloseDoorRecordModule.class.getName())) {
            closeDoorRecord = (CloseDoorRecordModule) module;
        }else if (module.getClass().getName().equals(GetFingerprintIdModule.class.getName())) {
            getFingerprintId = (GetFingerprintIdModule) module;
        }else if (module.getClass().getName().equals(RegisterPersonModule.class.getName())) {
            registerPersonModule = (RegisterPersonModule) module;
        }else if (module.getClass().getName().equals(QueryPersonInfoModule.class.getName())) {
            queryPersonInfoModule = (QueryPersonInfoModule) module;
        }else if (module.getClass().getName().equals(CheckRecordModule.class.getName())) {
            checkRecordModule = (CheckRecordModule) module;
        }else if (module.getClass().getName().equals(AlarmRecordModule.class.getName())) {
            alarmRecordModule = (AlarmRecordModule) module;
        }
    }
}