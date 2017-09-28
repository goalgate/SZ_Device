package com.sz_device.Function.Fun_Switching.mvp.presenter;


import com.sz_device.Function.Fun_Switching.mvp.module.ISwitching;
import com.sz_device.Function.Fun_Switching.mvp.module.SwitchImpl;
import com.sz_device.Function.Fun_Switching.mvp.view.ISwitchView;

/**
 * Created by zbsz on 2017/8/23.
 */

public class SwitchPresenter {

    private ISwitchView view;

    private SwitchPresenter(){}

    private static SwitchPresenter instance = null;

    public static SwitchPresenter getInstance(){
        if (instance == null)
            instance = new SwitchPresenter();
        return instance;
    }

    public void SwitchPresenterSetView(ISwitchView view) {
        this.view = view;
    }

    ISwitching switchingModule = new SwitchImpl();

    public void switch_Open(){
        switchingModule.onOpen(new ISwitching.ISwitchingListener() {
            @Override
            public void onSwitchingText(String value) {
                if(view != null){
                    view.onSwitchingText(value);
                }
            }

            @Override
            public void onTemHum(int temperature, int humidity) {
                if(view != null){
                    view.onTemHum(temperature,humidity);
                }
            }
        });
    }

    public void readHum(){
        switchingModule.onReadHum();
    }

    public void OutD8(boolean isOn){
        switchingModule.onOutD8(isOn);
    }

    public void OutD9(boolean isOn){
        switchingModule.onOutD9(isOn);
    }


}
