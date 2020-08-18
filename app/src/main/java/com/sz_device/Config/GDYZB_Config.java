package com.sz_device.Config;

import com.sz_device.Function.Func_ICCard.mvp.presenter.IDCardPresenter;

public class GDYZB_Config extends BaseConfig {
    @Override
    public String getServerId() {
        return "http://192.168.11.21:8231/";
    }



    @Override
    public String getProject() {
        return "GDYZB";
    }

    @Override
    public String getVer() {
        return "1.0";
    }

    @Override
    public String getMainActivity() {
        return ".Activity_GDYZB.MainActivity";
    }

    @Override
    public String getAddActivity() {
        return ".Activity_GDYZB.AddPersonActivity";
    }

    @Override
    public void readCard() {
        IDCardPresenter.getInstance().readCard();
    }

    @Override
    public void stopReadCard() {
        IDCardPresenter.getInstance().stopReadCard();
    }

    @Override
    public String CardFunction() {
        return IC;
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
        return "CBDI-P-IC";
    }

    @Override
    public String LockMethod() {
        return Menci;
    }
}
