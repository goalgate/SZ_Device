package com.sz_device.State.LockState;


import android.support.v7.app.AlertDialog;

import com.sz_device.Function.Fun_Switching.mvp.presenter.SwitchPresenter;

/**
 * Created by zbsz on 2017/9/28.
 */

public class State_Unlock extends LockState {

    public boolean alarming;

    SwitchPresenter sp;

    public State_Unlock(SwitchPresenter sp) {
        this.sp = sp;
    }

    @Override
    public void onHandle(Lock lock) {
        sp.OutD9(false);
        alarming = false;
    }


    @Override
    public boolean isAlarming() {
        return alarming;
    }
}
