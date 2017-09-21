package com.sz_device.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sz_device.EventBus.LegalEvent;
import com.sz_device.EventBus.NetworkEvent;
import com.sz_device.EventBus.OpenDoorEvent;
import com.sz_device.EventBus.TemHumEvent;
import com.sz_device.Fun_Switching.mvp.presenter.SwitchPresenter;
import com.sz_device.Fun_Switching.mvp.view.ISwitchView;
import com.sz_device.Retrofit.Request.RequestEnvelope;
import com.sz_device.Retrofit.Request.ResquestModule.CommonRequestModule;
import com.sz_device.Retrofit.Request.ResquestModule.OnlyPutKeyModule;
import com.sz_device.Retrofit.Response.ResponseEnvelope;
import com.sz_device.Retrofit.RetrofitGenerator;
import com.sz_device.Tools.MyObserver;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.alarmCease;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.alarmRecord;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.checkOnline;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.closeDoorRecord;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.stateRecord;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.testNet;

/**
 * Created by zbsz on 2017/8/28.
 */

public class SwitchService extends Service implements ISwitchView {

    private static final String PREFS_NAME = "UserInfo";

    SwitchPresenter sp = SwitchPresenter.getInstance();

    String Last_Value;

    boolean legal = false;

    boolean network_state;

    int last_mTemperature = 0;

    int last_mHumidity = 0;

    String THSwitchValue;

    Disposable rx_delay;

    Disposable unlock_noOpen;

    boolean first_open = true;

    boolean alarming = false;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        sp.SwitchPresenterSetView(this);
        sp.switch_Open();
        Observable.interval(0, 5, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(@NonNull Long aLong) throws Exception {
                sp.readHum();
            }
        });

        Observable.interval(0, 5, TimeUnit.SECONDS).observeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        if (NetworkUtils.isConnected()) {
                            RetrofitGenerator.getCommonApi().commonFunction(RequestEnvelope.GetRequestEnvelope(testNet,new OnlyPutKeyModule(SPUtils.getInstance(PREFS_NAME).getString("jsonKey"))))
                                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseEnvelope>() {
                                @Override
                                public void onSubscribe(@NonNull Disposable d) {

                                }

                                @Override
                                public void onNext(@NonNull ResponseEnvelope responseEnvelope) {
                                    if (responseEnvelope != null) {
                                        Map<String, String> infoMap = new Gson().fromJson(responseEnvelope.body.testNetResponse.info,
                                                new TypeToken<HashMap<String, String>>() {
                                                }.getType());
                                        if (infoMap.get("result").equals("true")) {
                                            network_state = true;
                                            EventBus.getDefault().post(new NetworkEvent(true, "服务器连接正常"));
                                        } else {
                                            network_state = false;
                                            EventBus.getDefault().post(new NetworkEvent(false, "设备出错"));
                                        }
                                    }
                                }

                                @Override
                                public void onError(@NonNull Throwable e) {
                                    network_state = false;
                                    EventBus.getDefault().post(new NetworkEvent(false, "服务器连接出错"));
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                        } else {
                            network_state = false;
                            EventBus.getDefault().post(new NetworkEvent(false, "请检查网络是否已连接"));
                        }
                    }
                });

        Observable.interval(10, 30, TimeUnit.SECONDS).observeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        if (network_state) {
                            StateRecord();
                        }

                    }
                });
        Observable.interval(1, 1, TimeUnit.HOURS).observeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        if(network_state){
                            RetrofitGenerator.getCommonApi().commonFunction(RequestEnvelope.GetRequestEnvelope(checkOnline,new OnlyPutKeyModule(SPUtils.getInstance(PREFS_NAME).getString("jsonKey"))))
                               .subscribeOn(Schedulers.io()).subscribe(new MyObserver());
                        }
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetLegalEvent(LegalEvent event) {
        legal = event.getLegal();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSwitchingText(String value) {
        if ((Last_Value == null || Last_Value.equals(""))) {
            if(value.startsWith("AAAAAA")){
                Last_Value = value;
                if (value.equals("AAAAAA000000000000") && legal == false) {
                    sp.OutD9(true);
                    if(network_state){
                        alarmRecord();
                    }
                    alarming = true;
                }
            }

        } else {
            if (value.startsWith("BBBBBB") && value.endsWith("C1EF")) {
                THSwitchValue = value;
            }
            if (value.startsWith("AAAAAA")) {
                if (!value.equals(Last_Value)) {
                    Last_Value = value;
                    if (Last_Value.equals("AAAAAA000000000000")) {
                        if (legal == false) {
                            sp.OutD9(true);
                            if(first_open && network_state){
                                alarmRecord();
                                alarming = true;
                            }
                            if(first_open && network_state){
                                EventBus.getDefault().post(new OpenDoorEvent(false));
                                first_open = false;
                            }
                        } else {
                            if(first_open && network_state){
                                EventBus.getDefault().post(new OpenDoorEvent(true));
                                first_open = false;
                            }
                        }

                        if (unlock_noOpen != null) {
                            unlock_noOpen.dispose();
                        }
                        if (rx_delay != null) {
                            rx_delay.dispose();
                        }
                    } else if (Last_Value.equals("AAAAAA000000000001")) {
                        final String closeDoorTime = TimeUtils.getNowString();
                        Observable.timer(20, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())
                                .subscribe(new Observer<Long>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        rx_delay = d;
                                    }

                                    @Override
                                    public void onNext(Long aLong) {
                                        legal = false;
                                        if (network_state) {
                                            CloseDoorRecord(closeDoorTime);
                                        }
                                        first_open = true;
                                     }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    }
                }
                if (legal == true) {
                    sp.OutD9(false);
                    if(network_state&&alarming){
                        AlarmCease();
                        alarming = false;
                    }

                }
                if (legal == true && value.equals("AAAAAA000000000001")) {
                    Observable.timer(120, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())
                            .subscribe(new Observer<Long>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                    unlock_noOpen = d;
                                }

                                @Override
                                public void onNext(Long aLong) {
                                    legal = false;
                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onComplete() {
                                }
                            });
                }
            }
        }
    }

    private void AlarmCease(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("datetime",TimeUtils.getNowString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitGenerator.getCommonApi().commonFunction(RequestEnvelope.GetRequestEnvelope(alarmCease,
                new CommonRequestModule(SPUtils.getInstance(PREFS_NAME).getString("jsonKey"), jsonObject.toString())
        )).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new MyObserver());
    }

    private void CloseDoorRecord(String time) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("datetime", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitGenerator.getCommonApi().commonFunction(RequestEnvelope.GetRequestEnvelope(closeDoorRecord,new CommonRequestModule(SPUtils.getInstance(PREFS_NAME).getString("jsonKey"), jsonObject.toString())))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver());
    }

    private void alarmRecord(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("datetime", TimeUtils.getNowString());
            jsonObject.put("alarmType", String.valueOf(1));
            jsonObject.put("alarmValue",String.valueOf(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitGenerator.getCommonApi().commonFunction(RequestEnvelope.GetRequestEnvelope(
           alarmRecord,new CommonRequestModule(SPUtils.getInstance(PREFS_NAME).getString("jsonKey"),jsonObject.toString())
        )) .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new MyObserver());
    }

    private void StateRecord() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("datetime", TimeUtils.getNowString());
            jsonObject.put("switching", THSwitchValue);
            jsonObject.put("temperature", last_mTemperature);
            jsonObject.put("humidity", last_mHumidity);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RetrofitGenerator.getCommonApi().commonFunction(RequestEnvelope.GetRequestEnvelope(stateRecord,new CommonRequestModule(SPUtils.getInstance(PREFS_NAME).getString("jsonKey"),jsonObject.toString())))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver());
    }

    @Override
    public void onTemHum(int temperature, int humidity) {
        EventBus.getDefault().post(new TemHumEvent(temperature, humidity));
        if ((Math.abs(temperature - last_mTemperature) > 5 || Math.abs(temperature - last_mTemperature) > 10) && network_state) {
            StateRecord();
        }
        last_mTemperature = temperature;
        last_mHumidity = humidity;
    }
}
