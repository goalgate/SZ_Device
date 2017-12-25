package com.sz_device.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sz_device.AppInit;

import com.sz_device.EventBus.LockUpEvent;

import com.sz_device.Function.Func_Switch.mvp.module.SwitchImpl;
import com.sz_device.Function.Func_Switch.mvp.presenter.SwitchPresenter;
import com.sz_device.Function.Func_Switch.mvp.view.ISwitchView;
import com.sz_device.State.DoorState.Door;

import com.sz_device.State.DoorState.State_Close;
import com.sz_device.State.DoorState.State_Open;

import com.sz_device.EventBus.NetworkEvent;
import com.sz_device.EventBus.PassEvent;
import com.sz_device.EventBus.TemHumEvent;

import com.sz_device.State.LockState.Lock;
import com.sz_device.State.LockState.State_Lockup;
import com.sz_device.State.LockState.State_Unlock;
import com.sz_device.Retrofit.Request.RequestEnvelope;
import com.sz_device.Retrofit.Request.ResquestModule.CommonRequestModule;
import com.sz_device.Retrofit.Request.ResquestModule.OnlyPutKeyModule;
import com.sz_device.Retrofit.Response.ResponseEnvelope;
import com.sz_device.Retrofit.RetrofitGenerator;
import com.sz_device.Tools.DaoSession;
import com.sz_device.Tools.MyObserver;
import com.sz_device.Tools.SaveObserver;
import com.sz_device.Tools.UnUploadPackage;
import com.sz_device.Tools.UnUploadPackageDao;
import com.sz_device.Tools.UploadValue;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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

    SwitchPresenter sp = SwitchPresenter.getInstance();

    String Last_Value;

    boolean network_state = false;

    int last_mTemperature = 0;

    int last_mHumidity = 0;

    String THSwitchValue;

    Disposable rx_delay;

    Disposable unlock_noOpen;

    UnUploadPackageDao unUploadPackageDao;

    UploadValue isUploading = new UploadValue();

    QueryBuilder qb;

    Door door;

    Lock lock;
    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        sp.SwitchPresenterSetView(this);
        sp.switch_Open();

        Log.e("Message","ServiceStart");

        DaoSession daoSession = AppInit.getInstance().getDaoSession();
        unUploadPackageDao = daoSession.getUnUploadPackageDao();
        qb = unUploadPackageDao.queryBuilder();

        lock = new Lock(new State_Lockup(sp));
        door = new Door(new State_Close(lock));

        Observable.interval(0, 5, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(@NonNull Long aLong) throws Exception {
                sp.readHum();
            }
        });

        Observable.interval(0, 30, TimeUnit.SECONDS).observeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        if (NetworkUtils.isConnected()) {
                            RetrofitGenerator.getCommonApi().commonFunction(RequestEnvelope
                                    .GetRequestEnvelope(new OnlyPutKeyModule(testNet)))
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
                                            if(!network_state){
                                                qb.where(UnUploadPackageDao.Properties.Upload.eq(false));
                                                if (!isUploading.getIsUploading() && qb.list().size() > 0) {
                                                    isUploading.setIsUploading(true);
                                                    reUpload(qb.list());
                                                }
                                            }
                                            network_state = true;
                                            EventBus.getDefault().post(new NetworkEvent(true));
                                        } else {
                                            network_state = false;
                                            EventBus.getDefault().post(new NetworkEvent(false));
                                        }
                                    }
                                }

                                @Override
                                public void onError(@NonNull Throwable e) {
                                    network_state = false;
                                    EventBus.getDefault().post(new NetworkEvent(false));
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                        } else {
                            network_state = false;
                            EventBus.getDefault().post(new NetworkEvent(false));
                        }
                    }
                });

        Observable.interval(10, 30, TimeUnit.SECONDS).observeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        StateRecord();
                    }
                });

        Observable.interval(1, 1, TimeUnit.HOURS).observeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        if (network_state) {
                            RetrofitGenerator.getCommonApi().commonFunction(RequestEnvelope.GetRequestEnvelope(new OnlyPutKeyModule(checkOnline)))
                                    .subscribeOn(Schedulers.io()).subscribe(new MyObserver());
                        }
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetPassEvent(PassEvent event) {
        lock.setLockState(new State_Unlock(sp));
        if(lock.isAlarming()){
            AlarmCease();
        }
        lock.doNext();

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
            if (value.startsWith("AAAAAA")) {
                Last_Value = value;
                if (value.equals("AAAAAA000000000000") /*&& legal == false*/) {
                    door.setDoorState(new State_Open(lock));
                    door.doNext();
                    alarmRecord();
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
                        if(getDoorState(State_Close.class)){
                            door.setDoorState(new State_Open(lock));
                            door.doNext();
                            if (getLockState(State_Lockup.class)){
                                alarmRecord();
                            }
                        }


                        if (unlock_noOpen != null) {
                            unlock_noOpen.dispose();
                        }
                        if (rx_delay != null) {
                            rx_delay.dispose();
                        }
                    } else if (Last_Value.equals("AAAAAA000001000000")) {
                        final String closeDoorTime = TimeUtils.getNowString();
                        Observable.timer(20, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())
                                .subscribe(new Observer<Long>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        rx_delay = d;
                                    }

                                    @Override
                                    public void onNext(Long aLong) {
                                        lock.setLockState(new State_Lockup(sp));
                                        door.setDoorState(new State_Close(lock));
                                        CloseDoorRecord(closeDoorTime);
                                        sp.buzz(SwitchImpl.Hex.H2);
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

                if (getLockState(State_Unlock.class)&& value.equals("AAAAAA000001000000")) {
                    Observable.timer(120, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())
                            .subscribe(new Observer<Long>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                    unlock_noOpen = d;
                                }

                                @Override
                                public void onNext(Long aLong) {
                                    lock.setLockState(new State_Lockup(sp));
                                    EventBus.getDefault().post(new LockUpEvent());
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

    private void AlarmCease() {
        final JSONObject AlarmCeaseJson = new JSONObject();
        try {
            AlarmCeaseJson.put("datetime", TimeUtils.getNowString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (network_state) {
            CommonRequestModule alarmCeaseM = new CommonRequestModule(alarmCease, AlarmCeaseJson.toString());
            RetrofitGenerator.getCommonApi().commonFunction(RequestEnvelope.GetRequestEnvelope(alarmCeaseM))
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SaveObserver(unUploadPackageDao, alarmCeaseM));
        } else {
            UnUploadPackage un = new UnUploadPackage();
            un.setMethod(alarmCease);
            un.setJsonData(AlarmCeaseJson.toString());
            un.setUpload(false);
            unUploadPackageDao.insert(un);
        }

    }

    private void CloseDoorRecord(String time) {
        JSONObject CloseDoorRecordJson = new JSONObject();
        try {
            CloseDoorRecordJson.put("datetime", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (network_state) {
            CommonRequestModule closeDoorRecordM = new CommonRequestModule(closeDoorRecord, CloseDoorRecordJson.toString());
            RetrofitGenerator.getCommonApi().commonFunction(RequestEnvelope.GetRequestEnvelope(closeDoorRecordM))
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SaveObserver(unUploadPackageDao, closeDoorRecordM));
        } else {
            UnUploadPackage un = new UnUploadPackage();
            un.setMethod(closeDoorRecord);
            un.setJsonData(CloseDoorRecordJson.toString());
            un.setUpload(false);
            unUploadPackageDao.insert(un);
        }
    }

    private void alarmRecord() {
        JSONObject alarmRecordJson = new JSONObject();
        try {
            alarmRecordJson.put("datetime", TimeUtils.getNowString());
            alarmRecordJson.put("alarmType", String.valueOf(1));
            alarmRecordJson.put("alarmValue", String.valueOf(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (network_state) {
            CommonRequestModule alarmRecordM = new CommonRequestModule(alarmRecord, alarmRecordJson.toString());
            RetrofitGenerator.getCommonApi().commonFunction(RequestEnvelope.GetRequestEnvelope(alarmRecordM))
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SaveObserver(unUploadPackageDao, alarmRecordM));
        } else {
            UnUploadPackage un = new UnUploadPackage();
            un.setMethod(alarmRecord);
            un.setJsonData(alarmRecordJson.toString());
            un.setUpload(false);
            unUploadPackageDao.insert(un);
        }

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
        if (network_state) {
            RetrofitGenerator.getCommonApi().commonFunction(RequestEnvelope.GetRequestEnvelope(new CommonRequestModule(stateRecord, jsonObject.toString())))
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyObserver());
        }

    }

    @Override
    public void onTemHum(int temperature, int humidity) {
        EventBus.getDefault().post(new TemHumEvent(temperature, humidity));
        if ((Math.abs(temperature - last_mTemperature) > 5 || Math.abs(temperature - last_mTemperature) > 10)) {
            StateRecord();
        }
        last_mTemperature = temperature;
        last_mHumidity = humidity;
    }

    private void reUpload(List<UnUploadPackage> list) {

        for (final UnUploadPackage unUploadPackage : list) {
            RetrofitGenerator.getCommonApi()
                    .commonFunction(RequestEnvelope.GetRequestEnvelope(
                            new CommonRequestModule(unUploadPackage.getMethod(), unUploadPackage.getJsonData())))
                    .subscribeOn(Schedulers.io()).subscribe(new Observer<ResponseEnvelope>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {

                }

                @Override
                public void onNext(@NonNull ResponseEnvelope responseEnvelope) {
                    unUploadPackageDao.delete(unUploadPackage);
                }

                @Override
                public void onError(@NonNull Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });
        }
        isUploading.setIsUploading(false);
    }

    private Boolean getDoorState(Class stateClass) {
        if (door.getDoorState().getClass().getName().equals(stateClass.getName())) {
            return true;
        } else {
            return false;
        }
    }
    private Boolean getLockState(Class stateClass) {
        if (lock.getLockState().getClass().getName().equals(stateClass.getName())) {
            return true;
        } else {
            return false;
        }
    }
}
