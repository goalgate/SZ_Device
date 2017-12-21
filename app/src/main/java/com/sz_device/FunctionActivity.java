package com.sz_device;

import android.os.Bundle;
import android.view.SurfaceView;

import com.blankj.utilcode.util.BarUtils;
import com.sz_device.Function.Fun_Camera.mvp.presenter.PhotoPresenter;
import com.sz_device.Function.Fun_Camera.mvp.view.IPhotoView;
import com.sz_device.Function.Fun_FingerPrint.mvp.presenter.FingerPrintPresenter;
import com.sz_device.Function.Fun_FingerPrint.mvp.view.IFingerPrintView;
import com.trello.rxlifecycle2.components.RxActivity;


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
        fpp.fpInit();
        fpp.fpOpen();
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
        fpp.fpIdentify();
    }

    @Override
    public void onPause() {
        super.onPause();
        fpp.fpCancel(true);
        pp.PhotoPresenterSetView(null);
        fpp.FingerPrintPresenterSetView(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fpp.fpClose();
    }
}
