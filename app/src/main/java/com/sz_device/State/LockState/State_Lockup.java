package com.sz_device.State.LockState;


import android.util.Log;

import com.sz_device.Function.Func_Switch.mvp.presenter.SwitchPresenter;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by zbsz on 2017/9/28.
 */

public class State_Lockup extends LockState {

    public boolean alarming;

    SwitchPresenter sp;

    public State_Lockup(SwitchPresenter sp) {
        this.sp = sp;
    }


    public State_Lockup() {

    }
    @Override
    public void onHandle(Lock lock) {
        sp.OutD9(true);
        Observable.timer(60, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        sp.OutD9(false);
                        Log.e("信息提示","报警已消除");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        alarming = true;
    }

    @Override
    public boolean isAlarming() {
        return alarming;
    }
}