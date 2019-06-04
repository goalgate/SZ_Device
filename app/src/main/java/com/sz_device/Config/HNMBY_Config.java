package com.sz_device.Config;

import com.sz_device.Function.Func_ICCard.mvp.presenter.IDCardPresenter;

public class HNMBY_Config extends BaseConfig {
    @Override
    public String getServerId() {
        return "http://192.168.11.125:8102/";
    }

    @Override
    public String getProject() {
        return "HNMBY";
    }

    @Override
    public String getVer() {
        return "1.0";
    }

    @Override
    public String getMainActivity() {
        return ".HNMBYActivity";
    }

    @Override
    public String getAddActivity() {
        return null;
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
    public String getModel() {
        return "CBDI-DA-01";
    }

    @Override
    public String getServiceName() {
        return "防爆数据采集器";
    }
}
