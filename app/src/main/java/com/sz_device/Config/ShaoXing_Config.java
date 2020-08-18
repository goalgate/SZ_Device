package com.sz_device.Config;

import com.sz_device.Function.Func_ICCard.mvp.presenter.IDCardPresenter;

public class ShaoXing_Config extends BaseConfig {

    @Override
    public String getServerId() {
//        return "http://223.4.68.189:8003/";
//        return "http://192.168.12.163:9091/";
        return "http://220.191.224.57:8162/";
//        return "http://192.168.1.233:8162/";
//        return "http://124.114.153.91:8162/";
    }

    @Override
    public String getProject() {
        return "ShaoXing";
    }

    @Override
    public String getVer() {
        return "1.9";
    }

    @Override
    public String getMainActivity() {
        return ".Activity_ShaoXing.ShaoXingActivity";
    }

    @Override
    public String getAddActivity() {
        return ".Activity_ShaoXing.ShaoXingAddActivity";
    }

    @Override
    public void readCard() {
        IDCardPresenter.getInstance().readIDCard();
    }

    @Override
    public void stopReadCard() {
        IDCardPresenter.getInstance().stopReadIDCard();
    }

    @Override
    public String CardFunction() {
        return ID;
    }

    @Override
    public boolean face() {
        return false;
    }

    @Override
    public boolean TemHum() {
        return true;
    }

    @Override
    public String getServiceName() {
        return "数据采集器";
    }

    @Override
    public String getModel() {
        return "CBDI-P-ID";
    }

    @Override
    public String LockMethod() {
        return Menci;
    }
}
