package com.sz_device.Config;

import com.sz_device.Function.Func_ICCard.mvp.presenter.IDCardPresenter;

public class ZJYZB_Config extends BaseConfig {
    @Override
    public String getServerId() {
        return "http://192.168.11.21:9000/cjy/s/";
    }

    @Override
    public String getProject() {
        return "ZJYZB";
    }

    @Override
    public String getVer() {
        return "1.0";
    }

    @Override
    public String getMainActivity() {
        return ".New_IndexActivity";
    }

    @Override
    public String getAddActivity() {
        return ".ZJYZBAddActivity";
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