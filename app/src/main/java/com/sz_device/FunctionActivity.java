package com.sz_device;

import android.os.Bundle;
import android.view.SurfaceView;

import com.blankj.utilcode.util.BarUtils;

import com.sz_device.Function.Fun_FingerPrint.mvp.presenter.FingerPrintPresenter;
import com.sz_device.Function.Fun_FingerPrint.mvp.view.IFingerPrintView;
import com.sz_device.Function.Func_Camera.mvp.presenter.PhotoPresenter;
import com.sz_device.Function.Func_Camera.mvp.view.IPhotoView;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.RxActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;


/**
 * Created by zbsz on 2017/11/27.
 */

public abstract class FunctionActivity extends RxActivity implements IPhotoView,IFingerPrintView {

    public FingerPrintPresenter fpp = FingerPrintPresenter.getInstance();

    public PhotoPresenter pp = PhotoPresenter.getInstance();

    public SurfaceView surfaceView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarUtils.hideStatusBar(this);
        pp.initCamera();

    }

    @Override
    public void onStart() {
        super.onStart();
        pp.setParameter(surfaceView.getHolder());
    }



    @Override
    public void onRestart() {
        super.onRestart();
        pp.initCamera();

    }

    @Override
    public void onResume() {
        super.onResume();
        pp.PhotoPresenterSetView(this);
        pp.setDisplay(surfaceView.getHolder());
        fpp.FingerPrintPresenterSetView(this);
        Observable.timer(3, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Long>bindUntilEvent(ActivityEvent.PAUSE))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        fpp.fpIdentify();
                    }
                });

    }

    @Override
    public void onPause() {
        super.onPause();
        fpp.fpCancel(true);
        fpp.FingerPrintPresenterSetView(null);
        pp.PhotoPresenterSetView(null);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fpp.fpClose();
        pp.close_Camera();
    }
}
