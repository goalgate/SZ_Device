package com.sz_device.State.LockState;


import com.sz_device.Function.Fun_Switching.mvp.presenter.SwitchPresenter;

/**
 * Created by zbsz on 2017/9/28.
 */

public class State_Lockup extends LockState {

    SwitchPresenter sp;

    public State_Lockup(SwitchPresenter sp) {
        this.sp = sp;
    }
    @Override
    public void onHandle(Lock lock) {
        sp.OutD9(true);
    }
}
