package com.sz_device.Activity_YZBYUN.MC;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.example.x6.gpioctl.GpioUtils;
import com.sz_device.AppInit;
import com.sz_device.Bean.ReUploadBean;
import com.sz_device.EventBus.AlarmEvent;
import com.sz_device.EventBus.NetworkEvent;
import com.sz_device.EventBus.TemHumEvent;
import com.sz_device.Function.Func_Switch.mvp.presenter.SwitchPresenter;
import com.sz_device.Retrofit.RetrofitGenerator;
import com.sz_device.State.DoorState.Door;
import com.sz_device.State.DoorState.State_Close;
import com.sz_device.State.LockState.Lock;
import com.sz_device.State.LockState.State_Lockup;
import com.sz_device.greendao.DaoSession;
import com.sz_device.greendao.ReUploadBeanDao;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

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

public class MCService extends Service {

    private SPUtils config = SPUtils.getInstance("config");

    DaoSession mdaoSession = AppInit.getInstance().getDaoSession();

    int Last_Value =high;

    private static char group = 'E';
    private static int num = 0;
    private GpioUtils gpioUtils;
    private static int high = 1;
    private static int low = 0;
    private static int io = 0;

    boolean readStop = false;

    boolean readStart = false;

    Door door;

    Lock lock;

    @Override
    public void onCreate() {
        super.onCreate();
        reUpload();

        lock = Lock.getInstance(new State_Lockup());
        door = Door.getInstance(new State_Close());

        Observable.timer(10, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        reboot();
                    }
                });

        Observable.interval(0, 30, TimeUnit.SECONDS).observeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        testNet();
                    }
                });

        IOSet();
    }



    private void IOSet() {

        gpioUtils = GpioUtils.getGpioUtils();
        io = gpioUtils.getGpioPin(group, num);
        gpioUtils.setGpioDirection(io, 1);
        Thread readIOThread = new Thread(() -> {
            if(!readStart){
                if (high == gpioUtils.gpioGetValue(io)) {
                    Log.e("sdds","开机鸣叫");
                    EventBus.getDefault().post(new AlarmEvent());
                    OpenDoor(false);
                }
                readStart = true;
            }
            while (!readStop){
                try {
                    if (low == gpioUtils.gpioGetValue(io)) {
                        Last_Value = low;
                    } else {
                        if(Last_Value!=high){
                            EventBus.getDefault().post(new AlarmEvent());
                            OpenDoor(false);
                        }
                        Last_Value = high;
                    }
                    Thread.sleep(500);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        });
        readIOThread.start();
    }


    private void reUpload() {
        final ReUploadBeanDao reUploadBeanDao = mdaoSession.getReUploadBeanDao();
        List<ReUploadBean> list = reUploadBeanDao.queryBuilder().list();
        for (final ReUploadBean bean : list) {
            if(bean.getMethod().equals("alarm")){
                continue;
            }
            RetrofitGenerator.getGdyzbConnectApi().withDataRs(bean.getMethod(), config.getString("key"), bean.getContent())
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        readStop = true;
        try{
            Thread.sleep(1000);
        }catch (Exception e){
            e.printStackTrace();
        }
        gpioUtils.close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void testNet() {
        RetrofitGenerator.getGdyzbConnectApi().noData("testNet", config.getString("key"))
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
                AppInit.getMyManager().reboot();
                Log.e("txt","关机了");
                }
            };
            t.scheduleAtFixedRate(task, startTime, daySpan);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void OpenDoor(boolean leagl) {
        final JSONObject OpenDoorJson = new JSONObject();
        try {
            OpenDoorJson.put("datetime", TimeUtils.getNowString());
            OpenDoorJson.put("state", "n");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitGenerator.getGdyzbConnectApi().withDataRr("openDoorRecord", config.getString("key"), OpenDoorJson.toString())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mdaoSession.insert(new ReUploadBean(null,"openDoorRecord",OpenDoorJson.toString()));

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
