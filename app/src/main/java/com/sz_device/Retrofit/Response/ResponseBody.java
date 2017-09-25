package com.sz_device.Retrofit.Response;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * 用户角色返回body
 * Created by SmileXie on 16/7/15.
 */
@Root(name = "Body")
public class ResponseBody {

    @Element(name = "testNetResponse", required = false)
    public ResponseModel testNetResponse;

    @Element(name = "checkOnlineResponse", required = false)
    public ResponseModel checkOnlineResponse;

    @Element(name = "stateRecordResponse", required = false)
    public ResponseModel stateRecordResponse;

    @Element(name = "openDoorRecordResponse", required = false)
    public ResponseModel openDoorRecordResponse;

    @Element(name = "closeDoorRecordResponse", required = false)
    public ResponseModel closeDoorRecordResponse;

    @Element(name = "getFingerprintIdResponse", required = false)
    public ResponseModel getFingerprintIdResponse;

    @Element(name = "registerPersonResponse", required = false)
    public ResponseModel registerPersonResponse;

    @Element(name = "queryPersonInfoResponse", required = false)
    public ResponseModel queryPersonInfoResponse;

    @Element(name = "checkRecordResponse", required = false)
    public ResponseModel checkRecordResponse;

    @Element(name = "alarmRecordResponse", required = false)
    public ResponseModel alarmRecordResponse;

    @Element(name = "alarmCeaseResponse", required = false)
    public ResponseModel alarmCeaseResponse;

    @Element(name = "downPersonFingerprintInfoResponse", required = false)
    public ResponseListModel downPersonFingerprintInfoResponse;

    @Element(name = "downPersonInfoResponse", required = false)
    public ResponseListModel downPersonInfoResponse;
}
