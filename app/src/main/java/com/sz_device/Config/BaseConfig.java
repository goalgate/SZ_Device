package com.sz_device.Config;

public abstract class BaseConfig {
    public static String IC = "IC";

    public static String ID = "ID";

    public abstract String getServerId();

    public abstract String getProject();

    public abstract String getVer();

    public abstract String getMainActivity();

    public abstract String getAddActivity();

    public abstract void readCard();

    public abstract void stopReadCard();

    public abstract String CardFunction();



}
