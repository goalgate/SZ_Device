package com.sz_device.Function.Func_Switch.mvp.presenter;

import com.sz_device.AppInit;
import com.sz_device.Function.Func_Switch.mvp.module.ISwitching;
import com.sz_device.Function.Func_Switch.mvp.module.SwitchImpl;
import com.sz_device.Function.Func_Switch.mvp.module.SwitchImpl4;
import com.sz_device.Function.Func_Switch.mvp.view.ISwitchView;

/**
 * Created by zbsz on 2017/8/23.
 */

public class SwitchPresenter {

    private ISwitchView view;

    ISwitching switchingModule;

    private SwitchPresenter(){
        if (AppInit.getMyManager().getAndroidDisplay().startsWith("x3128")){
            this.switchingModule = new SwitchImpl4();
        }else {
            this.switchingModule = new SwitchImpl();
        }

    }

    private static SwitchPresenter instance = null;

    public static SwitchPresenter getInstance(){
        if (instance == null)
            instance = new SwitchPresenter();
        return instance;
    }

    public void SwitchPresenterSetView(ISwitchView view) {
        this.view = view;
    }



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

    public void buzz(SwitchImpl.Hex hex){
        switchingModule.onBuzz(hex);
    }

    public void buzzOff(){
        switchingModule.onBuzzOff();
    }
}
