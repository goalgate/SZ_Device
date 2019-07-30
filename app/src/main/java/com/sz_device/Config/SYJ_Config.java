package com.sz_device.Config;

import com.sz_device.Function.Func_ICCard.mvp.presenter.IDCardPresenter;

public class SYJ_Config extends BaseConfig {
    @Override
    public String getProject() {
        return "SYJ";
    }

    @Override
    public String getServerId() {
        return "http://192.168.11.124:8111/";
    }

    @Override
    public String getVer() {
        return "1.0";
    }

    @Override
    public String getMainActivity() {
        return ".TestActivity.MainActivity";
    }

    @Override
    public String getAddActivity() {
        return ".TestActivity.AddPersonActivity";
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
    public String getServiceName() {
        return "数据采集器";
    }

    @Override
    public String getModel() {
        return "CBDI-P-IC";
    }
}
