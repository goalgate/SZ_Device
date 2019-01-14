package com.sz_device;

import android.os.Bundle;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.sz_device.Function.Fun_FingerPrint.mvp.presenter.FingerPrintPresenter;
import com.sz_device.Tools.DESX;
import com.sz_device.Tools.NetInfo;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.RxActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by zbsz on 2017/12/8.
 */

public class SplashActivity extends RxActivity {
    public FingerPrintPresenter fpp = FingerPrintPresenter.getInstance();
    private SPUtils config = SPUtils.getInstance("config");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Observable.timer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(SplashActivity.this.<Long>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        fpp.fpInit();
                        fpp.fpOpen();
                        if (config.getBoolean("firstStart", true)) {
                            JSONObject jsonKey = new JSONObject();
                            try {
                                jsonKey.put("daid", new NetInfo().getMacId());
                                jsonKey.put("check", DESX.encrypt(new NetInfo().getMacId()));
                                //jsonKey.put("daid", "054231-123179-163237");
                                //jsonKey.put("check", DESX.encrypt("054231-123179-163237"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            config.put("firstStart", false);
                            config.put("daid", new NetInfo().getMacId());
                             //config.put("daid", "054231-123179-163237");
                            config.put("key", DESX.encrypt(jsonKey.toString()));
                            // config.put("ServerId","http://jdwp.szxhdz.com/");
                            config.put("ServerId", AppInit.getInstrumentConfig().getServerId());
                            //config.put("ServerId","http://124.172.232.89:8050/gdda/");
                        }
                        Observable.timer(3, TimeUnit.SECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .compose(SplashActivity.this.<Long>bindUntilEvent(ActivityEvent.DESTROY))
                                .subscribe(new Observer<Long>() {
                                    @Override
                                    public void onSubscribe(@NonNull Disposable d) {

                                    }

                                    @Override
                                    public void onNext(@NonNull Long aLong) {
                                        ActivityUtils.startActivity(getPackageName(), getPackageName() + ".IndexActivity");
                                        SplashActivity.this.finish();
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {


                                    }
                                });

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {


                    }
                });

    }
}
