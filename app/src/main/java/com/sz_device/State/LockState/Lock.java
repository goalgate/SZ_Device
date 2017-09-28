package com.sz_device.State.LockState;


import com.sz_device.Function.Fun_Switching.mvp.presenter.SwitchPresenter;

/**
 * Created by zbsz on 2017/9/28.
 */

public class Lock {

    private LockState lockState;

    private SwitchPresenter sp;

    public Lock(LockState lockState) {

        this.lockState = lockState;

    }

    public LockState getLockState() {
        return lockState;
    }

    public void setLockState(LockState lockState) {
        this.lockState = lockState;
    }

    public void doNext(){
        lockState.onHandle(this);
    }
}
