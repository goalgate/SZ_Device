package com.sz_device.Config;

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
}
