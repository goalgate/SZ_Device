package com.sz_device;

import android.app.Application;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.drv.card.ICardInfo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.log.Lg;
import com.sz_device.Alerts.Alarm;
import com.sz_device.Alerts.Alert_IP;
import com.sz_device.Alerts.Alert_Message;
import com.sz_device.Alerts.Alert_Password;
import com.sz_device.Alerts.Alert_Server;
import com.sz_device.Bean.ReUploadBean;
import com.sz_device.Config.BaseConfig;
import com.sz_device.Config.SZ_Config;
import com.sz_device.EventBus.AlarmEvent;
import com.sz_device.EventBus.LockUpEvent;
import com.sz_device.EventBus.NetworkEvent;
import com.sz_device.EventBus.OpenDoorEvent;
import com.sz_device.EventBus.PassEvent;
import com.sz_device.EventBus.TemHumEvent;
import com.sz_device.Function.Func_Switch.mvp.module.SwitchImpl;
import com.sz_device.Function.Func_Switch.mvp.presenter.SwitchPresenter;
import com.sz_device.Retrofit.RetrofitGenerator;
import com.sz_device.Service.HNMBYService;
import com.sz_device.Service.SwitchService;
import com.sz_device.State.OperationState.Door_Open_OperateState;
import com.sz_device.State.OperationState.No_one_OperateState;
import com.sz_device.State.OperationState.One_man_OperateState;
import com.sz_device.State.OperationState.Operation;
import com.sz_device.State.OperationState.Two_man_OperateState;
import com.sz_device.Tools.DESX;
import com.sz_device.Tools.FileUtils;
import com.sz_device.Tools.MyObserver;
import com.sz_device.Tools.PersonType;
import com.sz_device.Tools.ServerConnectionUtil;
import com.sz_device.Tools.User;
import com.sz_device.UI.NormalWindow;
import com.sz_device.UI.SuperWindow;
import com.sz_device.greendao.DaoSession;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class HNMBYActivity extends HNMBYFunctionActivity implements NormalWindow.OptionTypeListener {

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private SPUtils config = SPUtils.getInstance("config");

    DaoSession mdaoSession = AppInit.getInstance().getDaoSession();

    Intent intent;

    Disposable checkChange;

    Disposable disposableTips;

    User cg_User1 = new User();

    User cg_User2 = new User();

    User unknownUser = new User();

    private NormalWindow normalWindow;

    Bitmap headphoto;

    No_one_OperateState no_one_operateState = new No_one_OperateState();

    @BindView(R.id.tv_info)
    TextView tv_info;

    @BindView(R.id.iv_network)
    ImageView iv_network;

    @BindView(R.id.iv_setting)
    ImageView iv_setting;

    @BindView(R.id.tv_time)
    TextView tv_time;

    @BindView(R.id.iv_lock)
    ImageView iv_lock;

    @BindView(R.id.tv_temperature)
    TextView tv_temperature;

    @BindView(R.id.tv_humidity)
    TextView tv_humidity;

    Operation global_Operation;

    Alert_Message alert_message = new Alert_Message(this);

    Alert_Server alert_server = new Alert_Server(this);

    Alert_IP alert_ip = new Alert_IP(this);

    @OnClick(R.id.lay_setting)
    void option() {
        normalWindow = new NormalWindow(HNMBYActivity.this);
        normalWindow.setOptionTypeListener(HNMBYActivity.this);
        normalWindow.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
    }

    @OnClick(R.id.lay_network)
    void showMessage() {
        alert_message.showMessage();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_adapter);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        autoUpdate();
        Lg.e("key", config.getString("key"));
        openService();
        network_state = false;
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        disposableTips = RxTextView.textChanges(tv_info)
                .debounce(60, TimeUnit.SECONDS)
                .switchMap(new Function<CharSequence, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(@NonNull CharSequence charSequence) throws Exception {
                        return Observable.just(config.getString("daid") + "\n等待用户操作...");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        tv_info.setText(s);
                    }
                });
        global_Operation = new Operation(no_one_operateState);
        setGestures();
        alert_ip.IpviewInit();
        alert_server.serverInit(new Alert_Server.Server_Callback() {
            @Override
            public void setNetworkBmp() {
                iv_network.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.newui_wifi));
            }
        });
        alert_message.messageInit();
        syncTime();
    }

    private void autoUpdate() {
        new ServerConnectionUtil().download("http://124.172.232.89:8050/daServer/updateADA.do?ver=" + AppUtils.getAppVersionName() + "&daid=" + config.getString("daid") + "&url=" + config.getString("ServerId"), new ServerConnectionUtil.Callback() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    if (response.equals("true")) {
                        AppUtils.installApp(new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Download" + File.separator + "app-release.apk"), "application/vnd.android.package-archive");
                    }
                }
            }
        });
    }

    @BindView(R.id.gestures_overlay)
    GestureOverlayView gestures;
    GestureLibrary mGestureLib;

    private void setGestures() {
        gestures.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);
        gestures.setGestureVisible(false);
        gestures.addOnGesturePerformedListener(new GestureOverlayView.OnGesturePerformedListener() {
            @Override
            public void onGesturePerformed(GestureOverlayView overlay,
                                           Gesture gesture) {
                ArrayList<Prediction> predictions = mGestureLib.recognize(gesture);
                if (predictions.size() > 0) {
                    Prediction prediction = (Prediction) predictions.get(0);
                    // 匹配的手势
                    if (prediction.score > 1.0) { // 越匹配score的值越大，最大为10
                        if (prediction.name.equals("setting")) {
                            NetworkUtils.openWirelessSettings();
                        }
                    }
                }
            }
        });
        if (mGestureLib == null) {
            mGestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
            mGestureLib.load();
        }
    }


    void openService() {
        intent = new Intent(HNMBYActivity.this, HNMBYService.class);
        startService(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTemHumEvent(TemHumEvent event) {
        tv_temperature.setText(event.getTem() + "℃");
        tv_humidity.setText(event.getHum() + "%");
    }

    boolean network_state;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetNetworkEvent(NetworkEvent event) {
        if (event.getNetwork_state()) {
            iv_network.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.newui_wifi));
            network_state = true;
        } else {
            iv_network.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.newui_wifi1));
            network_state = false;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetAlarmEvent(AlarmEvent event) {
//        Alarm.getInstance(this).messageAlarm(门磁打开报警，请检查门磁情况);
        tv_info.setText("门磁打开报警,请检查门磁情况");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetOpenDoorEvent(OpenDoorEvent event) {
        OpenDoorRecord(event.getLegal());
        if (checkChange != null) {
            checkChange.dispose();
        }
        if (!getState(Door_Open_OperateState.class)) {
            global_Operation.setState(no_one_operateState);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetLockUpEvent(LockUpEvent event) {
//        Alarm.getInstance(this).setKnown(false);
        tv_info.setText("仓库已重新上锁");
        iv_lock.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.newui_mj));
        cg_User1 = new User();
        cg_User2 = new User();
        global_Operation.setState(no_one_operateState);
    }

    @Override
    public void onResume() {
        super.onResume();
        cg_User1 = new User();
        cg_User2 = new User();
        global_Operation.setState(no_one_operateState);
        tv_info.setText(config.getString("daid") + "\n等待用户操作...");
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Long>bindUntilEvent(ActivityEvent.PAUSE))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        tv_time.setText(formatter.format(new Date(System.currentTimeMillis())));
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        //stopService(intent);
        AppInit.getMyManager().unBindAIDLService(AppInit.getContext());
        disposableTips.dispose();
    }


    @Override
    public void onOptionType(Button view, int type) {
        normalWindow.dismiss();
        if (type == 1) {
            alert_server.show();
        } else if (type == 2) {
            alert_ip.show();
        }
    }

    @Override
    public void onCaremaText(String s) {
        if ("摄像头打开失败，无法拍照".equals(s)) {
//            Alarm.getInstance(this).messageAlarm(s);
            tv_info.setText(s);
        }
    }


    @Override
    public void onGetPhoto(Bitmap bmp) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.5f, 0.5f);
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        if (unknownUser.getCardId() != null) {
            unknownPeople(bmp);
            return;
        } else if (xunjian) {
            cg_User1.setPhoto(FileUtils.bitmapToBase64(bmp));
            checkRecord(String.valueOf(2));
            return;
        } else if (gongan) {
            cg_User1.setPhoto(FileUtils.bitmapToBase64(bmp));
            checkRecord(String.valueOf(3));
            return;
        }
        if (getState(No_one_OperateState.class)) {
            cg_User1.setPhoto(FileUtils.bitmapToBase64(bmp));
            if (AppInit.getInstrumentConfig().face()) {
                faceRecognition(cg_User1, bmp);
            } else {
                Noface(cg_User1);
            }
        } else if (getState(One_man_OperateState.class)) {
            cg_User2.setPhoto(FileUtils.bitmapToBase64(bmp));
            if (AppInit.getInstrumentConfig().face()) {
                faceRecognition(cg_User2, bmp);
            } else {
                Noface(cg_User2);
            }
        }

    }

    @Override
    public void onsetCardInfo(ICardInfo cardInfo) {
        if (alert_message.Showing()) {
            if (AppInit.getInstrumentConfig().CardFunction().equals(BaseConfig.IC)) {
                alert_message.setICCardText("IC卡号：" + cardInfo.getUid());
            } else {
                alert_message.setICCardText("身份证号：" + cardInfo.cardId());
            }
        } else {
            try {
                idcard_operation(cardInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    boolean xunjian = false;
    boolean gongan = false;

    private void idcard_operation(final ICardInfo cardInfo) {
        SPUtils sp = SPUtils.getInstance("personData");
        xunjian = false;
        gongan = false;
        if (sp.getString(cardInfo.cardId()).equals(PersonType.KuGuan)) {
            if (getState(No_one_OperateState.class)) {
                cg_User1.setName(cardInfo.name());
                cg_User1.setCardId(cardInfo.cardId());
                pp.screenshots();
            } else if (getState(One_man_OperateState.class)) {
                if (!cardInfo.cardId().equals(cg_User1.getCardId())) {
                    cg_User2.setName(cardInfo.name());
                    cg_User2.setCardId(cardInfo.cardId());
                    pp.screenshots();
                } else {
                    tv_info.setText("请不要连续输入相同的管理员信息");
                    return;
                }
            } else if (getState(Door_Open_OperateState.class)) {
                tv_info.setText("仓库门已解锁");
            }
        } else if (sp.getString(cardInfo.cardId()).equals(PersonType.XunJian)) {
            if (checkChange != null) {
                checkChange.dispose();
            }
            if (getState(One_man_OperateState.class)) {
                if (!cardInfo.cardId().equals(cg_User1.getCardId())) {
                    cg_User2.setName(cardInfo.name());
                    cg_User2.setCardId(cardInfo.cardId());
                    pp.screenshots();
                } else {
                    tv_info.setText("请不要连续输入相同的管理员信息");
                    return;
                }
            } else {
                cg_User1.setName(cardInfo.name());
                cg_User1.setCardId(cardInfo.cardId());
                pp.screenshots();
                xunjian = true;
            }

        } else if (sp.getString(cardInfo.cardId()).equals(PersonType.Gongan)) {
            if (checkChange != null) {
                checkChange.dispose();
            }
            cg_User1.setName(cardInfo.name());
            cg_User1.setCardId(cardInfo.cardId());
            pp.screenshots();
            gongan = true;

        } else {
            RetrofitGenerator.getHnmbyApi().queryPersonInfo("queryPersion", config.getString("key"), cardInfo.cardId())
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyObserver<ResponseBody>(this) {
                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                String s = responseBody.string().toString();
                                if (s.equals("false")) {
                                    unknownUser.setName(cardInfo.name());
                                    unknownUser.setCardId(cardInfo.cardId());
                                    pp.screenshots();
                                } else if (s.startsWith("true")) {
                                    String type = s.substring(5, s.length());
                                    SPUtils.getInstance("personData").put(cardInfo.cardId(), type);
                                    if (type.equals(PersonType.KuGuan)) {
                                        if (getState(No_one_OperateState.class)) {
                                            cg_User1.setName(cardInfo.name());
                                            cg_User1.setCardId(cardInfo.cardId());
                                            pp.screenshots();
                                        } else if (getState(One_man_OperateState.class)) {
                                            if (!cardInfo.cardId().equals(cg_User1.getCardId())) {
                                                cg_User2.setName(cardInfo.name());
                                                cg_User2.setCardId(cardInfo.cardId());
                                                pp.screenshots();
                                            } else {
                                                tv_info.setText("请不要连续输入相同的管理员信息");
                                                return;
                                            }
                                        } else if (getState(Door_Open_OperateState.class)) {
                                            tv_info.setText("仓库门已解锁");
                                        }
                                    } else if (type.equals(PersonType.XunJian)) {
                                        if (checkChange != null) {
                                            checkChange.dispose();
                                        }
//                                        cg_User1.setName(cardInfo.name());
//                                        cg_User1.setCardId(cardInfo.cardId());
//                                        pp.screenshots();
//                                        xunjian = true;
                                        if (getState(One_man_OperateState.class)) {
                                            if (!cardInfo.cardId().equals(cg_User1.getCardId())) {
                                                cg_User2.setName(cardInfo.name());
                                                cg_User2.setCardId(cardInfo.cardId());
                                                pp.screenshots();
                                            } else {
                                                tv_info.setText("请不要连续输入相同的管理员信息");
                                                return;
                                            }
                                        } else {
                                            cg_User1.setName(cardInfo.name());
                                            cg_User1.setCardId(cardInfo.cardId());
                                            pp.screenshots();
                                            xunjian = true;
                                        }
                                    } else if (type.equals(PersonType.Gongan)) {
                                        if (checkChange != null) {
                                            checkChange.dispose();
                                        }
                                        cg_User1.setName(cardInfo.name());
                                        cg_User1.setCardId(cardInfo.cardId());
                                        pp.screenshots();
                                        gongan = true;

                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                tv_info.setText("Exception");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            unknownUser.setName(cardInfo.name());
                            unknownUser.setCardId(cardInfo.cardId());
                            pp.screenshots();
                        }
                    });
        }
    }

    private void Noface(final User user) {
        if (getState(no_one_operateState.getClass())) {
            global_Operation.setState(new One_man_OperateState());
            tv_info.setText("管理员" + user.getName() + "打卡,请继续管理员操作");
            Observable.timer(60, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())
                    .compose(HNMBYActivity.this.<Long>bindUntilEvent(ActivityEvent.PAUSE))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            checkChange = d;
                        }

                        @Override
                        public void onNext(Long aLong) {
                            checkRecord(String.valueOf(2));

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else if (getState(One_man_OperateState.class)) {
            if (checkChange != null) {
                checkChange.dispose();
            }
            global_Operation.setState(new Two_man_OperateState());
            tv_info.setText("管理员" + user.getName() + "打卡成功,请开启仓库门");
            global_Operation.setState(new Door_Open_OperateState());
            EventBus.getDefault().post(new PassEvent());
            iv_lock.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.newui_mj1));
        }
    }

    private void faceRecognition(final User user, Bitmap bitmap) {
        JSONObject faceJson = new JSONObject();
        try {
            faceJson.put("psonCardId", user.getCardId());
            faceJson.put("image1", FileUtils.bitmapToBase64(headphoto));
            faceJson.put("image2", FileUtils.bitmapToBase64(bitmap));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitGenerator.getHnmbyApi().withDataRs("faceRecognition", config.getString("key"), faceJson.toString())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<String>(this) {
                    @Override
                    public void onNext(String s) {
                        if (s.startsWith("true") && (int) Double.parseDouble(s.substring(5, s.length())) > 60) {
                            if (getState(No_one_OperateState.class)) {
                                global_Operation.setState(new One_man_OperateState());
                                user.setFaceRecognition(s.substring(5, s.length()));
                                tv_info.setText("管理员" + user.getName() + "打卡,请继续管理员操作");
                                Observable.timer(60, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())
                                        .compose(HNMBYActivity.this.<Long>bindUntilEvent(ActivityEvent.PAUSE))
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Observer<Long>() {
                                            @Override
                                            public void onSubscribe(Disposable d) {
                                                checkChange = d;
                                            }

                                            @Override
                                            public void onNext(Long aLong) {
                                                checkRecord(String.valueOf(2));

                                            }

                                            @Override
                                            public void onError(Throwable e) {

                                            }

                                            @Override
                                            public void onComplete() {

                                            }
                                        });
                            } else if (getState(One_man_OperateState.class)) {
                                if (checkChange != null) {
                                    checkChange.dispose();
                                }
                                global_Operation.setState(new Two_man_OperateState());
                                user.setFaceRecognition(s.substring(5, s.length()));
                                tv_info.setText("管理员" + user.getName() + "打卡,等待两张现场照片比对结果");
                                samePsonFaceRecognition();
                            }

                        } else {
                            tv_info.setText("管理员" + user.getName() + "打卡,人脸比对数据不通过");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        tv_info.setText("无法连接服务器,机器已重置,请重新录入仓管员信息");
                        global_Operation.setState(new No_one_OperateState());
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();

                    }
                });
    }

    String faceRecognition3;

    private void samePsonFaceRecognition() {
        JSONObject faceJson = new JSONObject();
        try {
            faceJson.put("image1", cg_User1.getPhoto());
            faceJson.put("image2", cg_User1.getPhoto());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitGenerator.getHnmbyApi().withDataRs("samePsonFaceRecognition", config.getString("key"), faceJson.toString())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<String>(this) {
                    @Override
                    public void onNext(String s) {
                        if (s.startsWith("true") && (int) Double.parseDouble(s.substring(5, s.length())) > 30) {
                            faceRecognition3 = s.substring(5, s.length());
                            global_Operation.setState(new Door_Open_OperateState());
                            EventBus.getDefault().post(new PassEvent());
                            iv_lock.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.newui_mj1));
                            tv_info.setText("双人管理成功,请开启仓库门");
                        } else {
                            global_Operation.setState(new No_one_OperateState());
                            tv_info.setText("现场比对不通过,机器已重置,请重新录入仓管员信息");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        tv_info.setText("无法连接服务器,机器已重置,请重新录入仓管员信息");
                        global_Operation.setState(new No_one_OperateState());
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                    }
                });
    }

    @Override
    public void onsetCardImg(Bitmap bmp) {
        headphoto = bmp;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private Boolean getState(Class stateClass) {
        if (global_Operation.getState().getClass().getName().equals(stateClass.getName())) {
            return true;
        } else {
            return false;
        }
    }

    private void syncTime() {
        RetrofitGenerator.getHnmbyApi().withDataRr("getTime", config.getString("key"), null)
                .subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io()).subscribe(new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String datetime = responseBody.string();
                    AppInit.getMyManager().setTime(Integer.parseInt(datetime.substring(0, 4)),
                            Integer.parseInt(datetime.substring(5, 7)),
                            Integer.parseInt(datetime.substring(8, 10)),
                            Integer.parseInt(datetime.substring(11, 13)),
                            Integer.parseInt(datetime.substring(14, 16)),
                            Integer.parseInt(datetime.substring(17, 19)));
                } catch (Exception e) {
                    Lg.e("Exception", e.toString());
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void checkRecord(String type) {
        SwitchPresenter.getInstance().OutD9(false);
        final JSONObject checkRecordJson = new JSONObject();
        try {
            checkRecordJson.put("id", cg_User1.getCardId());
            checkRecordJson.put("name", cg_User1.getName());
            checkRecordJson.put("photos", cg_User1.getPhoto());
            checkRecordJson.put("checkType", type);
            checkRecordJson.put("datetime", TimeUtils.getNowString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitGenerator.getHnmbyApi().withDataRs("saveVisit", config.getString("key"), checkRecordJson.toString())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<String>(this) {

                    @Override
                    public void onNext(String s) {
                        if (s.equals("true")) {
                            tv_info.setText("巡检员" + cg_User1.getName() + "巡检成功");
                        } else if (s.equals("false")) {
                            tv_info.setText("巡检失败");
                        } else if (s.equals("dataErr")) {
                            tv_info.setText("上传巡检数据失败");
                        } else if (s.equals("dataErr")) {
                            tv_info.setText("数据库操作有错");
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        tv_info.setText("无法连接服务器,请检查网络,离线数据已保存");
                        mdaoSession.insert(new ReUploadBean(null, "saveVisit", checkRecordJson.toString()));

                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        cg_User1 = new User();
                        cg_User2 = new User();
                        if (!getState(Two_man_OperateState.class) || !getState(Door_Open_OperateState.class)) {
                            global_Operation.setState(new No_one_OperateState());
                        }
                    }
                });
    }

    private void unknownPeople(Bitmap bmp) {
        final JSONObject unknownPeopleJson = new JSONObject();
        try {
            unknownPeopleJson.put("visitIdcard", unknownUser.getCardId());
            unknownPeopleJson.put("visitName", unknownUser.getName());
            unknownPeopleJson.put("photos", FileUtils.bitmapToBase64(bmp));
            unknownPeopleJson.put("photoSfz", FileUtils.bitmapToBase64(headphoto));
            unknownPeopleJson.put("datetime", TimeUtils.getNowString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitGenerator.getHnmbyApi().withDataRs("persionRecord", config.getString("key"), unknownPeopleJson.toString())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<String>(this) {

                    @Override
                    public void onNext(String s) {
                        if (!getState(Two_man_OperateState.class) || !getState(Door_Open_OperateState.class)) {
                            global_Operation.setState(new No_one_OperateState());
                        }
                        if (s.equals("true")) {
                            tv_info.setText("访问人" + unknownUser.getName() + "数据上传成功");
                        } else if (s.equals("false")) {
                            tv_info.setText("访问人上传失败");
                        } else if (s.equals("dataErr")) {
                            tv_info.setText("上传访问人数据失败");
                        } else if (s.equals("dbErr")) {
                            tv_info.setText("数据库操作有错");
                        }
                        unknownUser = new User();

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        tv_info.setText("无法连接服务器,请检查网络,离线数据已保存");
                        unknownUser = new User();
                        mdaoSession.insert(new ReUploadBean(null, "persionRecord", unknownPeopleJson.toString()));

                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                    }
                });
    }

    private void OpenDoorRecord(boolean leagl) {
        final JSONObject OpenDoorJson = new JSONObject();
        if (leagl) {
            try {
//                OpenDoorJson.put("courIds1", cg_User1.getCourIds());
//                OpenDoorJson.put("courIds2", cg_User2.getCourIds());
                OpenDoorJson.put("id1", cg_User1.getCardId());
                OpenDoorJson.put("id2", cg_User2.getCardId());
                OpenDoorJson.put("name1", cg_User1.getName());
                OpenDoorJson.put("name2", cg_User2.getName());
                OpenDoorJson.put("photo1", cg_User1.getPhoto());
                OpenDoorJson.put("photo2", cg_User2.getPhoto());
                OpenDoorJson.put("faceRecognition1", cg_User1.getFaceRecognition());
                OpenDoorJson.put("faceRecognition2", cg_User2.getFaceRecognition());
                OpenDoorJson.put("faceRecognition3", faceRecognition3);
                OpenDoorJson.put("datetime", TimeUtils.getNowString());
                OpenDoorJson.put("state", "y");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            try {
                OpenDoorJson.put("datetime", TimeUtils.getNowString());
                OpenDoorJson.put("state", "n");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        RetrofitGenerator.getHnmbyApi().withDataRs("openDoorRecord", config.getString("key"), OpenDoorJson.toString())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<String>(this) {
                    @Override
                    public void onNext(String s) {
                        if (s.equals("true")) {
                            try {
                                if (OpenDoorJson.getString("state").equals("y")) {
                                    tv_info.setText("正常开门数据上传成功");
                                } else {
                                    tv_info.setText("非法开门数据上传成功");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else if (s.equals("false")) {
                            tv_info.setText("开门数据上传失败");
                        } else if (s.equals("dataErr")) {
                            tv_info.setText("上传的json数据有错");
                        } else if (s.equals("dbErr")) {
                            tv_info.setText("数据库操作有错");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        tv_info.setText("无法连接服务器,请检查网络,离线数据已保存");
                        mdaoSession.insert(new ReUploadBean(null, "openDoorRecord", OpenDoorJson.toString()));
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        cg_User1 = new User();
                        cg_User2 = new User();
                    }
                });
    }


}
