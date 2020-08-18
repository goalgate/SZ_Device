package com.sz_device.Config;

import com.sz_device.Function.Func_ICCard.mvp.presenter.IDCardPresenter;

public class YZBALARM_Config extends BaseConfig {

    public String getServerId() {
        return "http://yzbyun.wxhxp.cn:81/";
    }


    @Override
    public String getProject() {
        return "YUNALARM";
    }

    @Override
    public String getVer() {
        return "1.0";
    }

    @Override
    public String getMainActivity() {
        return ".Activity_YZBYUN.IndexActivity";
    }

    @Override
    public String getAddActivity() {
        return "";
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
