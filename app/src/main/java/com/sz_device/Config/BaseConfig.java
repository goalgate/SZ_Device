package com.sz_device.Config;

public abstract class BaseConfig {
    public abstract String getServerId();

    public abstract String getProject();

    public abstract String getVer();

    public abstract String getActivity();

    public abstract void readCard();

    public abstract void stopReadCard();

}
