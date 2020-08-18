package com.sz_device;

import android.os.Bundle;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.sz_device.Config.HNMBY_Config;
import com.sz_device.Config.Hebei_Config;
import com.sz_device.Config.LN_Config;
import com.sz_device.Config.ShaoXing_Config;
import com.sz_device.Config.ZJYZB_Config;
import com.sz_device.Function.Fun_FingerPrint.mvp.presenter.FingerPrintPresenter;
import com.sz_device.Tools.AssetsUtils;
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

        if (AppInit.getInstrumentConfig().getClass().getName().equals(HNMBY_Config.class.getName())) {
//            if (config.getBoolean("firstStart", true)) {
//                AssetsUtils.getInstance(AppInit.getContext()).copyAssetsToSD("wltlib", "wltlib");
//                ActivityUtils.startActivity(getPackageName(), getPackageName() + ".StartActivity");
//                SplashActivity.this.finish();
//            } else {
//                ActivityUtils.startActivity(getPackageName(), getPackageName() + AppInit.getInstrumentConfig().getMainActivity());
//                SplashActivity.this.finish();
//            }
            if (config.getBoolean("firstStart", true)) {
                JSONObject jsonKey = new JSONObject();
                try {
//                    jsonKey.put("daid", "130214-057232-081228");
//                    jsonKey.put("check", DESX.encrypt("130214-057232-081228"));
                    jsonKey.put("daid", new NetInfo().getMacId());
                    jsonKey.put("check", DESX.encrypt(new NetInfo().getMacId()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                config.put("firstStart", false);
//                config.put("daid", "130214-057232-081228");
                config.put("daid", new NetInfo().getMacId());
                config.put("key", DESX.encrypt(jsonKey.toString()));
                config.put("ServerId", AppInit.getInstrumentConfig().getServerId());
                AssetsUtils.getInstance(AppInit.getContext()).copyAssetsToSD("wltlib", "wltlib");
            }
            ActivityUtils.startActivity(getPackageName(), getPackageName() + AppInit.getInstrumentConfig().getMainActivity());
            SplashActivity.this.finish();

        } else {
            Observable.timer(3, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(SplashActivity.this.<Long>bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull Long aLong) {
                            if (AppInit.getInstrumentConfig().getClass().getName().equals(Hebei_Config.class.getName())) {
                                if (config.getString("ServerId").equals("http://124.172.232.89:8050/daServer/")) {
                                    config.put("ServerId", "http://121.28.252.22:8001/");
                                }
                            } else if (AppInit.getInstrumentConfig().getClass().getName().equals(ShaoXing_Config.class.getName())) {
                                if (config.getString("ServerId").equals(new ZJYZB_Config().getServerId())) {
                                    config.put("ServerId", new ShaoXing_Config().getServerId());
                                }else{
                                    if (config.getString("ServerId").equals("http://14.23.69.2:1162/")) {
                                        config.put("ServerId", "http://220.191.224.57:8162/");
                                    }
                                }
                            } else if (config.getString("ServerId").contains("192.168.11")) {
                                config.put("ServerId", AppInit.getInstrumentConfig().getServerId());
                            } else if (config.getString("ServerId").equals("http://jdwp.szxhdz.com/")) {
                                config.put("ServerId", AppInit.getInstrumentConfig().getServerId());
                            } else if (AppInit.getInstrumentConfig().getClass().getName().equals(ZJYZB_Config.class.getName())) {
                                if (config.getString("ServerId").equals("http://113.140.1.136:7117/cjy/s/")) {
                                    config.put("ServerId", "http://223.4.68.189:8003/");
                                }
                            }
                            fpp.fpInit();
                            fpp.fpOpen();
                            if (config.getBoolean("firstStart", true)) {
                                JSONObject jsonKey = new JSONObject();
                                try {
//                                    jsonKey.put("daid", "048031-154100-122165");
//                                    jsonKey.put("check", DESX.encrypt("048031-154100-122165"));
                                    jsonKey.put("daid", new NetInfo().getMacId());
                                    jsonKey.put("check", DESX.encrypt(new NetInfo().getMacId()));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                config.put("firstStart", false);
//                                config.put("daid","048031-154100-122165");
                                config.put("daid", new NetInfo().getMacId());
                                config.put("key", DESX.encrypt(jsonKey.toString()));
                                config.put("ServerId", AppInit.getInstrumentConfig().getServerId());
                                AssetsUtils.getInstance(AppInit.getContext()).copyAssetsToSD("wltlib", "wltlib");
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
                                            ActivityUtils.startActivity(getPackageName(), getPackageName() + AppInit.getInstrumentConfig().getMainActivity());
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
}
