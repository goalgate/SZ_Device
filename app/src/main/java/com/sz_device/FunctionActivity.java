package com.sz_device;

import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.Spinner;

import com.blankj.utilcode.util.BarUtils;

import com.blankj.utilcode.util.SPUtils;
import com.sz_device.Bean.FingerprintUser;
import com.sz_device.Bean.ReUploadBean;
import com.sz_device.Config.ShaoXing_Config;
import com.sz_device.Function.Fun_FingerPrint.mvp.presenter.FingerPrintPresenter;
import com.sz_device.Function.Fun_FingerPrint.mvp.view.IFingerPrintView;
import com.sz_device.Function.Func_Camera.mvp.presenter.PhotoPresenter;
import com.sz_device.Function.Func_Camera.mvp.view.IPhotoView;
import com.sz_device.Function.Func_ICCard.mvp.presenter.IDCardPresenter;
import com.sz_device.Function.Func_ICCard.mvp.view.IIDCardView;
import com.sz_device.greendao.DaoSession;
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

public abstract class FunctionActivity extends RxActivity implements IIDCardView,IPhotoView,IFingerPrintView {

    public FingerPrintPresenter fpp = FingerPrintPresenter.getInstance();

    public PhotoPresenter pp = PhotoPresenter.getInstance();

    public IDCardPresenter idp = IDCardPresenter.getInstance();

    public SurfaceView surfaceView;

    DaoSession mdaosession = AppInit.getInstance().getDaoSession();

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
        fpp.FingerPrintPresenterSetView(this);
        AppInit.getInstrumentConfig().readCard();
        Observable.timer(3, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Long>bindUntilEvent(ActivityEvent.PAUSE))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        if(AppInit.getInstrumentConfig().getClass().getName().equals(ShaoXing_Config.class.getName())){
                            if (SPUtils.getInstance("config").getBoolean("firstUse", true)) {
                                try {
                                    mdaosession.deleteAll(ReUploadBean.class);
                                    mdaosession.deleteAll(FingerprintUser.class);
                                    fpp.fpRemoveAll();
                                    SPUtils.getInstance("config").put("firstUse", false);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Thread.sleep(1000);
                                fpp.fpIdentify();
                            }else{
                                fpp.fpIdentify();
                            }
                        }else{
                            fpp.fpIdentify();

                        }

                    }
                });
        idp.IDCardPresenterSetView(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        fpp.fpCancel(true);
        fpp.FingerPrintPresenterSetView(null);
        pp.PhotoPresenterSetView(null);
        idp.IDCardPresenterSetView(null);
        AppInit.getInstrumentConfig().stopReadCard();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fpp.fpClose();
        pp.close_Camera();
        idp.idCardClose();
    }

}
