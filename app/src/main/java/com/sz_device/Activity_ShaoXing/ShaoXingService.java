package com.sz_device.Activity_ShaoXing;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.google.gson.JsonSyntaxException;
import com.log.Lg;
import com.sz_device.AppInit;
import com.sz_device.Bean.ReUploadBean;
import com.sz_device.EventBus.AlarmEvent;
import com.sz_device.EventBus.LockUpEvent;
import com.sz_device.EventBus.NetworkEvent;
import com.sz_device.EventBus.OpenDoorEvent;
import com.sz_device.EventBus.PassEvent;
import com.sz_device.EventBus.TemHumEvent;
import com.sz_device.Function.Fun_FingerPrint.mvp.presenter.FingerPrintPresenter;
import com.sz_device.Function.Func_Switch.mvp.module.SwitchImpl;
import com.sz_device.Function.Func_Switch.mvp.presenter.SwitchPresenter;
import com.sz_device.Function.Func_Switch.mvp.view.ISwitchView;
import com.sz_device.Retrofit.RetrofitGenerator;
import com.sz_device.State.DoorState.Door;
import com.sz_device.State.DoorState.DoorState;
import com.sz_device.State.DoorState.State_Close;
import com.sz_device.State.DoorState.State_Open;
import com.sz_device.State.LockState.Lock;
import com.sz_device.State.LockState.State_Lockup;
import com.sz_device.State.LockState.State_Unlock;
import com.sz_device.greendao.DaoSession;
import com.sz_device.greendao.ReUploadBeanDao;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class ShaoXingService extends Service implements ISwitchView {

    SwitchPresenter sp = SwitchPresenter.getInstance();

    private SPUtils config = SPUtils.getInstance("config");

    DaoSession mdaoSession = AppInit.getInstance().getDaoSession();

    String Last_Value;

    int last_mTemperature = 0;

    int last_mHumidity = 0;

    String THSwitchValue;

    Disposable rx_delay;

    Disposable unlock_noOpen;

    Door door;

    State_Open door_open;

    Lock lock;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        sp.SwitchPresenterSetView(this);
        sp.switch_Open();
        lock = Lock.getInstance(new State_Lockup(sp));
        door = Door.getInstance(new State_Close());
        reUpload();
        Observable.timer(10, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        reboot();
                    }
                });

        Observable.interval(0, 5, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        sp.readHum();
                    }
                });
        Observable.interval(0, 30, TimeUnit.SECONDS).observeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        testNet();
                    }
                });
        Observable.interval(10, 600, TimeUnit.SECONDS).observeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        StateRecord();
                    }
                });

        door_open = new State_Open(new DoorState.doorStateCallback() {
            @Override
            public void onback() {
                if (getLockState(State_Lockup.class)) {
                    EventBus.getDefault().post(new OpenDoorEvent(false));
                    lock.doNext();
                } else if (getLockState(State_Unlock.class)) {
                    EventBus.getDefault().post(new OpenDoorEvent(true));
                }
            }
        });
    }


    private void reUpload() {
        final ReUploadBeanDao reUploadBeanDao = mdaoSession.getReUploadBeanDao();
        List<ReUploadBean> list = reUploadBeanDao.queryBuilder().list();
        for (final ReUploadBean bean : list) {
            RetrofitGenerator.getShaoXingApi().withDataRs(bean.getMethod(), config.getString("key"), bean.getContent())
                    .subscribeOn(Schedulers.single())
                    .unsubscribeOn(Schedulers.single())
                    .observeOn(Schedulers.single())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull String s) {
                            Log.e("信息提示", bean.getMethod());
                            reUploadBeanDao.delete(bean);


                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.e("信息提示error", bean.getMethod());

                        }

                        @Override
                        public void onComplete() {

                        }
                    });

        }
    }
//        }).start();


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetPassEvent(PassEvent event) {
        lock.setLockState(new State_Unlock(sp));
        lock.doNext();
        Observable.timer(120, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        unlock_noOpen = d;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        lock.setLockState(new State_Lockup(sp));
                        sp.buzz(SwitchImpl.Hex.H2);
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
            if (value.equals("AAAAAA000000000000") || value.equals("AAAAAA000001000000")) {
                Last_Value = value;
                if (value.equals("AAAAAA000000000000")) {
                    door.setDoorState(door_open);
                    door.doNext();
                    alarmRecord();
                }
            }
        } else {
            if (value.equals("AAAAAA000000000000") || value.equals("AAAAAA000001000000")) {
                if (!value.equals(Last_Value)) {
                    Last_Value = value;
                    if (Last_Value.equals("AAAAAA000000000000")) {
                        if (getDoorState(State_Close.class)) {
                            door.setDoorState(door_open);
                            door.doNext();
                            if (getLockState(State_Lockup.class)) {
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
                        //door.setDoorState(new State_Close());
                        if (getLockState(State_Unlock.class)) {
                            final String closeDoorTime = TimeUtils.getNowString();
                            Observable.timer(10, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())
                                    .subscribe(new Observer<Long>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {
                                            rx_delay = d;
                                        }

                                        @Override
                                        public void onNext(Long aLong) {
                                            lock.setLockState(new State_Lockup(sp));
                                            door.setDoorState(new State_Close());
                                            sp.buzz(SwitchImpl.Hex.H2);
                                            if (unlock_noOpen != null) {
                                                unlock_noOpen.dispose();
                                            }
                                            CloseDoorRecord(closeDoorTime);
                                            EventBus.getDefault().post(new LockUpEvent());
                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });
                        } else {
                            CloseDoorRecord(TimeUtils.getNowString());
                            door.setDoorState(new State_Close());
                        }
                    }
                }
            } else {
                if (value.startsWith("BBBBBB") && value.endsWith("C1EF")) {
                    THSwitchValue = value;
                }
            }
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


    private void CloseDoorRecord(String time) {
        final JSONObject CloseDoorRecordJson = new JSONObject();
        try {
            CloseDoorRecordJson.put("datetime", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitGenerator.getShaoXingApi().withDataRs("closeDoorRecord", config.getString("key"), CloseDoorRecordJson.toString())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mdaoSession.insert(new ReUploadBean(null, "closeDoorRecord", CloseDoorRecordJson.toString()));
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void alarmRecord() {
        EventBus.getDefault().post(new AlarmEvent());
        final JSONObject alarmRecordJson = new JSONObject();
        try {
            alarmRecordJson.put("datetime", TimeUtils.getNowString());
            alarmRecordJson.put("alarmType", String.valueOf(1));
            alarmRecordJson.put("alarmValue", String.valueOf(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RetrofitGenerator.getShaoXingApi().withDataRs("alarmRecord", config.getString("key"), alarmRecordJson.toString())
                .subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull String s) {

            }

            @Override
            public void onError(@NonNull Throwable e) {
                mdaoSession.insert(new ReUploadBean(null, "alarmRecord", alarmRecordJson.toString()));
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void StateRecord() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("datetime", TimeUtils.getNowString());
            jsonObject.put("switching", THSwitchValue);
            jsonObject.put("temperature", last_mTemperature);
            jsonObject.put("humidity", last_mHumidity);
            if (getDoorState(State_Open.class)) {
                jsonObject.put("state", "0");
            } else {
                jsonObject.put("state", "1");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitGenerator.getShaoXingApi().withDataRs("stateRecord", config.getString("key"), jsonObject.toString())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void testNet() {
        RetrofitGenerator.getShaoXingApi().noData("testNet", config.getString("key"))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        if (s.equals("true")) {
                            EventBus.getDefault().post(new NetworkEvent(true));
                        } else {
                            EventBus.getDefault().post(new NetworkEvent(false));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        EventBus.getDefault().post(new NetworkEvent(false));

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void reboot() {
        long daySpan = 24 * 60 * 60 * 1000 * 1;
        // 规定的每天时间，某时刻运行
        int randomTime = new Random().nextInt(50) + 10;
        String pattern = "yyyy-MM-dd '03:" + randomTime + ":00'";
//        String pattern = "yyyy-MM-dd '10:11:00'";

        final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Log.e("rebootTime", pattern);
        // 首次运行时间
        try {
            Date startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sdf.format(new Date()));
            if (System.currentTimeMillis() > startTime.getTime()) {
                startTime = new Date(startTime.getTime() + daySpan);
            } else if (startTime.getHours() == new Date().getHours()) {
                startTime = new Date(startTime.getTime() + daySpan);
            }
            Log.e("startTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTime));
            Timer t = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    // 要执行的代码
                    Lg.d("message", "equipment");
                    FingerPrintPresenter.getInstance().fpCancel(true);
//                    FingerPrintPresenter.getInstance().fpRemoveAll();
                    equipment_sync(config.getString("daid"));
                }
            };
            t.scheduleAtFixedRate(task, startTime, daySpan);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void equipment_sync(String old_devid) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("oldDaid", old_devid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitGenerator.getShaoXingApi().withDataRr("searchFinger", config.getString("key"), jsonObject.toString())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            JSONArray jsonArray = new JSONArray(responseBody.string().toString());
                            if (null != jsonArray && jsonArray.length() != 0) {
                                FingerPrintPresenter.getInstance().fpRemoveAll();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject item = jsonArray.getJSONObject(i);
                                    SPUtils user_sp = SPUtils.getInstance(item.getString("pfpIds"));
                                    user_sp.put("courIds", item.getString("courIds"));
                                    user_sp.put("name", item.getString("name"));
                                    user_sp.put("cardId", item.getString("cardId"));
                                    user_sp.put("courType", item.getString("courType"));
                                    FingerPrintPresenter.getInstance().fpDownTemplate(item.getString("pfpIds"), item.getString("fingerTemp"));

                                    SPUtils user_id = SPUtils.getInstance(item.getString("cardId"));
                                    user_id.put("courIds", item.getString("courIds"));
                                    user_id.put("name", item.getString("name"));
                                    user_id.put("fingerprintId", item.getString("pfpIds"));
                                    user_id.put("courType", item.getString("courType"));
                                }
//                                Observable.timer(1, TimeUnit.SECONDS)
//                                        .observeOn(AndroidSchedulers.mainThread())
//                                        .subscribe(new Consumer<Long>() {
//                                            @Override
//                                            public void accept(Long aLong) throws Exception {
////                                                for (int i = 0; i < jsonArray.length(); i++) {
////                                                    JSONObject item = jsonArray.getJSONObject(i);
////                                                    SPUtils user_sp = SPUtils.getInstance(item.getString("pfpIds"));
////                                                    FingerPrintPresenter.getInstance().fpDownTemplate(item.getString("pfpIds"), item.getString("fingerTemp"));
////                                                    user_sp.put("courIds", item.getString("courIds"));
////                                                    user_sp.put("name", item.getString("name"));
////                                                    user_sp.put("cardId", item.getString("cardId"));
////                                                    user_sp.put("courType", item.getString("courType"));
////
////                                                    SPUtils user_id = SPUtils.getInstance(item.getString("cardId"));
////                                                    user_id.put("courIds", item.getString("courIds"));
////                                                    user_id.put("name", item.getString("name"));
////                                                    user_id.put("fingerprintId", item.getString("pfpIds"));
////                                                    user_id.put("courType", item.getString("courType"));
////                                                }
//                                                AppInit.getMyManager().reboot();
//
//                                            }
//                                        });
                            }else{
//                                AppInit.getMyManager().reboot();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }  catch (IOException e) {
                            e.printStackTrace();
                        }catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }finally {
                            AppInit.getMyManager().reboot();

                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        AppInit.getMyManager().reboot();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
