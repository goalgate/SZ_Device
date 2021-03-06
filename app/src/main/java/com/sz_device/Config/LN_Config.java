package com.sz_device.Config;

import com.sz_device.Function.Func_ICCard.mvp.presenter.IDCardPresenter;

public class LN_Config extends BaseConfig {
    @Override
    public String getProject() {
        return "LN";
    }

    @Override
    public String getServerId() {
            return "http://124.172.232.89:8050/daServer/";
    }

    @Override
    public String getVer() {
        return "1.5";
    }

    @Override
    public String getMainActivity() {
        return ".IndexActivity";
    }

    @Override
    public String getAddActivity() {
        return ".AddPersonActivity";
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
