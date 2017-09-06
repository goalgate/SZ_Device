package com.sz_device.Fun_Switching.mvp.module;

/**
 * Created by zbsz on 2017/8/23.
 */

public interface ISwitching {

    void onOpen(ISwitchingListener listener);

    void onReadHum();

    void onOutD8(boolean status);

    void onOutD9(boolean status);

    interface ISwitchingListener{

        void onSwitchingText(String value);

        void onTemHum(int temperature, int humidity);

    }

}
