package com.sz_device;

import android.os.Bundle;
import android.view.SurfaceView;

import com.blankj.utilcode.util.BarUtils;
import com.sz_device.Function.Fun_FingerPrint.mvp.presenter.FingerPrintPresenter;
import com.sz_device.Function.Fun_FingerPrint.mvp.view.IFingerPrintView;
import com.sz_device.Function.Func_Camera.mvp.presenter.PhotoPresenter;
import com.sz_device.Function.Func_Camera.mvp.view.IPhotoView;
import com.sz_device.Function.Func_ICCard.mvp.presenter.IDCardPresenter;
import com.sz_device.Function.Func_ICCard.mvp.view.IIDCardView;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.RxActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public abstract class HNMBYFunctionActivity extends RxActivity implements IIDCardView,IPhotoView {

    public PhotoPresenter pp = PhotoPresenter.getInstance();

    public IDCardPresenter idp = IDCardPresenter.getInstance();

    public SurfaceView surfaceView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarUtils.hideStatusBar(this);
        pp.initCamera();
        idp.idCardOpen();

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
        idp.IDCardPresenterSetView(this);
        AppInit.getInstrumentConfig().readCard();
    }

    @Override
    public void onPause() {
        super.onPause();
        pp.PhotoPresenterSetView(null);
        idp.IDCardPresenterSetView(null);
        AppInit.getInstrumentConfig().stopReadCard();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pp.close_Camera();
        idp.idCardClose();
    }
}
