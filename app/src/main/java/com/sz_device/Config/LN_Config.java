package com.sz_device.Config;

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
        return "1.1";
    }
}
