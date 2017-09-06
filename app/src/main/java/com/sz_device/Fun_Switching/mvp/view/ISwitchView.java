package com.sz_device.Fun_Switching.mvp.view;

/**
 * Created by zbsz on 2017/8/23.
 */

public interface ISwitchView {

    void onSwitchingText(String value);

    void onTemHum(int temperature, int humidity);

}
