package com.sz_device.Config;

import com.sz_device.Function.Func_ICCard.mvp.presenter.IDCardPresenter;

public class SZ_Config extends BaseConfig{
    @Override
    public String getServerId() {
        return "http://jdwp.szxhdz.com/";
    }

    @Override
    public String getProject() {
        return "SZ";
    }

    @Override
    public String getVer() {
        return "2.3";
    }

    @Override
    public String getActivity() {
        return ".New_IndexActivity";
    }

    @Override
    public void readCard() {
        IDCardPresenter.getInstance().readCard();
    }

    @Override
    public void stopReadCard() {
        IDCardPresenter.getInstance().stopReadCard();
    }
}

