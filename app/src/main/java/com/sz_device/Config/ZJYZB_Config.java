package com.sz_device.Config;

import com.sz_device.Function.Func_ICCard.mvp.presenter.IDCardPresenter;

public class ZJYZB_Config extends BaseConfig {
    @Override
    public String getServerId() {
        return "http://223.4.68.189:8003/";
    }

    @Override
    public String getProject() {
        return "ZJYZB";
    }

    @Override
    public String getVer() {
        return "1.6";
    }

    @Override
    public String getMainActivity() {
        return ".Activity_ZheJiang.ZJYZBActivity";
    }

    @Override
    public String getAddActivity() {
        return ".Activity_ZheJiang.ZJYZBAddActivity";
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
