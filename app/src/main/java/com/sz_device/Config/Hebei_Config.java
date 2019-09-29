package com.sz_device.Config;

import com.sz_device.Function.Func_ICCard.mvp.presenter.IDCardPresenter;

public class Hebei_Config extends BaseConfig{

    @Override
    public String getProject() {
        return "HeBei";
    }

    @Override
    public String getServerId() {
        return "http://121.28.252.22:8001/";
    }

    @Override
    public String getVer() {
        return "1.5";
    }

    @Override
    public String getMainActivity() {
        return ".Activity_HEBEI.IndexActivity";
    }

    @Override
    public String getAddActivity() {
        return ".Activity_HEBEI.AddPersonActivity";
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
    public String getServiceName() {
        return "数据采集器";
    }

    @Override
    public String getModel() {
        return "CBDI-P-IC";
    }

    @Override
    public String LockMethod() {
        return Hongwai;
    }
}
