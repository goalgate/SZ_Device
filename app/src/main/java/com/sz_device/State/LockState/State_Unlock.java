package com.sz_device.State.LockState;


import com.sz_device.Function.Fun_Switching.mvp.presenter.SwitchPresenter;

/**
 * Created by zbsz on 2017/9/28.
 */

public class State_Unlock extends LockState {

    SwitchPresenter sp;

    public State_Unlock(SwitchPresenter sp) {
        this.sp = sp;
    }

    @Override
    public void onHandle(Lock lock) {
        sp.OutD9(false);

    }
}
