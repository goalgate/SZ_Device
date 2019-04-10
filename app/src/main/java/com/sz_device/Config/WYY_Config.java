package com.sz_device.Config;

import com.sz_device.Function.Func_ICCard.mvp.presenter.IDCardPresenter;

public class WYY_Config extends BaseConfig{
    @Override
    public String getServerId() {
        return "http://192.168.11.190:8102/";
    }

    @Override
    public String getProject() {
        return "WYY";
    }

    @Override
    public String getVer() {
        return "1.0";
    }

    @Override
    public String getMainActivity() {
        return ".WYYActivity";
    }

    @Override
    public String getAddActivity() {
        return ".WYYAddPersonActivity";
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
}
