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
        return "1.2";
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
