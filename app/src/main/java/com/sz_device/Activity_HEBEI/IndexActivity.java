package com.sz_device.Activity_HEBEI;

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
import com.google.gson.reflect.TypeToken;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.log.Lg;
import com.sz_device.Alerts.Alarm;
import com.sz_device.Alerts.Alert_IP;
import com.sz_device.Alerts.Alert_Message;
import com.sz_device.Alerts.Alert_Password;
import com.sz_device.Alerts.Alert_Server;
import com.sz_device.AppInit;
import com.sz_device.Bean.ReUploadBean;
import com.sz_device.Config.BaseConfig;
import com.sz_device.Config.SZ_Config;
import com.sz_device.EventBus.AlarmEvent;
import com.sz_device.EventBus.CloseDoorEvent;
import com.sz_device.EventBus.LockUpEvent;
import com.sz_device.EventBus.NetworkEvent;
import com.sz_device.EventBus.OpenDoorEvent;
import com.sz_device.EventBus.PassEvent;
import com.sz_device.EventBus.TemHumEvent;
import com.sz_device.Function.Func_Switch.mvp.module.SwitchImpl;
import com.sz_device.Function.Func_Switch.mvp.presenter.SwitchPresenter;
import com.sz_device.FunctionActivity;
import com.sz_device.R;
import com.sz_device.Retrofit.RetrofitGenerator;
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

import static com.sz_device.Config.BaseConfig.Hongwai;
import static com.sz_device.Config.BaseConfig.Menci;


/**
 * Created by zbsz on 2017/8/25.
 */


public class IndexActivity extends FunctionActivity implements NormalWindow.OptionTypeListener, SuperWindow.OptionTypeListener {

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

    private SuperWindow superWindow;

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

    Alert_Password alert_password = new Alert_Password(this);

    @OnClick(R.id.lay_setting)
    void option() {
        alert_password.show();
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
                        return Observable.just("等待用户操作...");
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
        alert_password.PasswordViewInit(new Alert_Password.Callback() {
            @Override
            public void normal_call() {
                normalWindow = new NormalWindow(IndexActivity.this);
                normalWindow.setOptionTypeListener(IndexActivity.this);
                normalWindow.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
            }

            @Override
            public void super_call() {
                superWindow = new SuperWindow(IndexActivity.this);
                superWindow.setOptionTypeListener(IndexActivity.this);
                superWindow.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
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
        intent = new Intent(IndexActivity.this, HebeiSwitchService.class);
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
        if (AppInit.getInstrumentConfig().LockMethod().equals(Menci)){
            Alarm.getInstance(this).messageAlarm("门磁打开报警，请检查门磁情况");
        }else if(AppInit.getInstrumentConfig().LockMethod().equals(Hongwai)){
            Alarm.getInstance(this).messageAlarm("开门报警已被触发");
        }
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
        Alarm.getInstance(this).setKnown(false);
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
        tv_info.setText("等待用户操作...");
        //network_state = false;
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Long>bindUntilEvent(ActivityEvent.PAUSE))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        tv_time.setText(formatter.format(new Date(System.currentTimeMillis())));
                    }
                });
        sync();
    }

    @Override
    public void onPause() {
        super.onPause();
        Alarm.getInstance(this).release();
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
    public void onSuperOptionType(Button view, int type) {
        superWindow.dismiss();
        if (type == 1) {
            ActivityUtils.startActivity(getPackageName(), getPackageName() + AppInit.getInstrumentConfig().getAddActivity());
        } else if (type == 2) {
            alert_server.show();
        } else if (type == 3) {
            ViewGroup extView2 = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.inputdevid_form, null);
            final EditText et_devid = (EditText) extView2.findViewById(R.id.devid_input);
            et_devid.setText(config.getString("daid"));
            new AlertView("设备信息同步", null, "取消", new String[]{"确定"}, null, IndexActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
                @Override
                public void onItemClick(Object o, int position) {
                    if (position == 0) {
                        if (TextUtils.isEmpty(et_devid.getText().toString())) {
                            ToastUtils.showLong("您的输入为空请重试");
                        } else {
                            fpp.fpCancel(true);
                            equipment_sync(et_devid.getText().toString());
                        }
                    }
                }
            }).addExtView(extView2).show();
        } else if (type == 4) {
            alert_ip.show();
        } else if (type == 5) {
            ViewGroup deleteView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.delete_person_form, null);
            final EditText et_idcard = (EditText) deleteView.findViewById(R.id.idcard_input);
            final EditText et_finger = (EditText) deleteView.findViewById(R.id.et_finger);
            new AlertView("删除人员指纹信息", null, "取消", new String[]{"确定"}, null, IndexActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
                @Override
                public void onItemClick(Object o, int position) {
                    if (position == 0) {
                        if (TextUtils.isEmpty(et_idcard.getText().toString()) || TextUtils.isEmpty(et_finger.getText().toString())) {
                            ToastUtils.showLong("您的输入为空请重试");
                        } else {
                            deletePerson(et_idcard.getText().toString().toUpperCase(), et_finger.getText().toString());
                        }
                    }
                }
            }).addExtView(deleteView).show();
        }
    }

    @Override
    public void onOptionType(Button view, int type) {
        normalWindow.dismiss();
        if (type == 1) {
            ActivityUtils.startActivity(getPackageName(), getPackageName() + AppInit.getInstrumentConfig().getAddActivity());
        } else if (type == 2) {
            alert_ip.show();
        }
    }

    @Override
    public void onCaremaText(String s) {
        if ("摄像头打开失败，无法拍照".equals(s)) {
            Alarm.getInstance(this).messageAlarm(s);
            //ToastUtils.showLong(s);
        }
    }

    @Override
    public void onSetImg(Bitmap bmp) {

    }

    @Override
    public void onText(String msg) {
        if ("请确认指纹是否已登记".equals(msg)) {
            tv_info.setText("请确认指纹是否已登记,再重试");
        } else if ("松开手指".equals(msg)) {
            tv_info.setText(msg);
        }
    }

    @Override
    public void onFpSucc(final String msg) {
        Alarm.getInstance(this).doorAlarm(new Alarm.doorCallback() {
            @Override
            public void onTextBack(String msg) {
                tv_info.setText(msg);
            }

            @Override
            public void onSucc() {
                Alarm.getInstance(IndexActivity.this).networkAlarm(network_state, new Alarm.networkCallback() {
                    @Override
                    public void onIsKnown() {
                        loadMessage(msg.substring(3, msg.length()));
                    }

                    @Override
                    public void onTextBack(String msg) {
                        Alarm.getInstance(IndexActivity.this).setKnown(true);
                        tv_info.setText(msg);
                    }
                });

            }
        });
    }

    private void loadMessage(String sp) {
        if (SPUtils.getInstance(sp).getString("courType").equals(PersonType.KuGuan)) {
            if (getState(No_one_OperateState.class)) {
                global_Operation.setState(new One_man_OperateState());
                pp.capture();
                cg_User1.setCourIds(SPUtils.getInstance(sp).getString("courIds"));
                cg_User1.setName(SPUtils.getInstance(sp).getString("name"));
                cg_User1.setCardId(SPUtils.getInstance(sp).getString("cardId"));
                cg_User1.setFingerprintId(sp);
                cg_User1.setCourType(SPUtils.getInstance(sp).getString("courType"));
            } else if (getState(Two_man_OperateState.class)) {
                if (!SPUtils.getInstance(sp).getString("cardId").equals(cg_User1.getCardId())) {
                    cg_User2.setCourIds(SPUtils.getInstance(sp).getString("courIds"));
                    cg_User2.setName(SPUtils.getInstance(sp).getString("name"));
                    cg_User2.setCardId(SPUtils.getInstance(sp).getString("cardId"));
                    cg_User2.setFingerprintId(sp);
                    pp.capture();
                    EventBus.getDefault().post(new PassEvent());
                    if(AppInit.getInstrumentConfig().LockMethod().equals(Hongwai)){
                        OpenDoorRecord(true);
                    }
                    iv_lock.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.newui_mj1));
                } else {
                    tv_info.setText("请不要连续输入相同的管理员信息");
                }
            } else if (getState(Door_Open_OperateState.class)) {
                if (AppInit.getInstrumentConfig().LockMethod().equals(Menci)){
                    tv_info.setText("仓库门已解锁");
                }else if(AppInit.getInstrumentConfig().LockMethod().equals(Hongwai)){
                    EventBus.getDefault().post(new CloseDoorEvent());
                    Alarm.getInstance(this).setKnown(false);
                    tv_info.setText("仓库已重新上锁");
                    iv_lock.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.newui_mj));
                    cg_User1 = new User();
                    cg_User2 = new User();
                    global_Operation.setState(no_one_operateState);
                }
            }
        } else if (SPUtils.getInstance(sp).getString("courType").equals(PersonType.XunJian)) {
            if (checkChange != null) {
                checkChange.dispose();
            }
            if (getState(One_man_OperateState.class)) {
                Alarm.getInstance(this).messageAlarm("请注意，该人员为巡检员，无法正常解锁\n如需解锁还请两名仓管员到现场重新操作\n此次巡检记录已保存");
                SwitchPresenter.getInstance().buzz(SwitchImpl.Hex.H2);
            }
            cg_User1.setCourIds(SPUtils.getInstance(sp).getString("courIds"));
            cg_User1.setName(SPUtils.getInstance(sp).getString("name"));
            cg_User1.setCardId(SPUtils.getInstance(sp).getString("cardId"));
            cg_User1.setFingerprintId(sp);
            cg_User1.setCourType(SPUtils.getInstance(sp).getString("courType"));
            checkRecord(String.valueOf(2));
        }else if (SPUtils.getInstance(sp).getString("courType").equals(PersonType.Gongan)) {
            if (checkChange != null) {
                checkChange.dispose();
            }
            if (getState(One_man_OperateState.class)) {
                Alarm.getInstance(this).messageAlarm("请注意，该人员为巡检员，无法正常解锁\n如需解锁还请两名仓管员到现场重新操作\n此次巡检记录已保存");
                SwitchPresenter.getInstance().buzz(SwitchImpl.Hex.H2);
            }
            cg_User1.setCourIds(SPUtils.getInstance(sp).getString("courIds"));
            cg_User1.setName(SPUtils.getInstance(sp).getString("name"));
            cg_User1.setCardId(SPUtils.getInstance(sp).getString("cardId"));
            cg_User1.setFingerprintId(sp);
            cg_User1.setCourType(SPUtils.getInstance(sp).getString("courType"));
            checkRecord(String.valueOf(3));
        } else {
            unknownUser.setName(SPUtils.getInstance(sp).getString("name"));
            unknownUser.setCardId(SPUtils.getInstance(sp).getString("cardId"));
            unknownUser.setFingerprintId(sp);
            pp.capture();
        }
    }

    @Override
    public void onGetPhoto(Bitmap bmp) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.5f, 0.5f);
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        if (unknownUser.getCardId() != null) {
            unknownPeople(bmp);
        }
        if (getState(One_man_OperateState.class)) {
            cg_User1.setPhoto(FileUtils.bitmapToBase64(bmp));
            if (cg_User1.getFingerprintId() != null) {
                //tv_info.setText("管理员" + cg_User1.getName() + "打卡,请继续管理员操作,指纹ID为" + cg_User1.getFingerprintId());
                tv_info.setText(String.format("管理员%s打卡成功,指纹ID:%s\n请继续管理员操作", cg_User1.getName(), cg_User1.getFingerprintId()));
            } else {
                tv_info.setText("管理员" + cg_User1.getName() + "打卡,请继续管理员操作");
            }
            global_Operation.doNext(new Operation.Callback_Operation() {
                @Override
                public void uploadCallback() {
                    pp.setDisplay(surfaceView.getHolder());
                }
            });
            Observable.timer(60, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())
                    .compose(this.<Long>bindUntilEvent(ActivityEvent.PAUSE))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            checkChange = d;
                        }

                        @Override
                        public void onNext(Long aLong) {
                            checkRecord(String.valueOf(1));

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else if (getState(Two_man_OperateState.class)) {
            if (checkChange != null) {
                checkChange.dispose();
            }
            if (cg_User2.getFingerprintId() != null) {
                tv_info.setText(String.format("管理员%s打卡成功,指纹ID:%s\n设备已撤防", cg_User2.getName(), cg_User2.getFingerprintId()));
                //tv_info.setText("管理员" + cg_User2.getName() + "打卡，双人管理成功,指纹ID为" + cg_User2.getFingerprintId());
            } else {
                tv_info.setText("管理员" + cg_User2.getName() + "打卡，双人管理成功");
            }
            cg_User2.setPhoto(FileUtils.bitmapToBase64(bmp));
            global_Operation.doNext(new Operation.Callback_Operation() {
                @Override
                public void uploadCallback() {
                    global_Operation.setState(new Door_Open_OperateState());
                    pp.setDisplay(surfaceView.getHolder());
                }
            });
        }
    }

    @Override
    public void onsetCardInfo(final ICardInfo cardInfo) {
        if (alert_message.Showing()) {
            if (AppInit.getInstrumentConfig().CardFunction().equals(BaseConfig.IC)) {
                alert_message.setICCardText("IC卡号：" + cardInfo.getUid());
            } else {
                alert_message.setICCardText("身份证号：" + cardInfo.cardId());
            }
        } else {
            Alarm.getInstance(this).doorAlarm(new Alarm.doorCallback() {
                @Override
                public void onTextBack(String msg) {
                    tv_info.setText(msg);
                }

                @Override
                public void onSucc() {
                    Alarm.getInstance(IndexActivity.this).networkAlarm(network_state, new Alarm.networkCallback() {
                        @Override
                        public void onIsKnown() {
                            if (AppInit.getInstrumentConfig().CardFunction().equals(BaseConfig.IC)) {
                                iccard_operation(cardInfo);
                            } else {
                                idcard_operation(cardInfo);
                            }

                        }

                        @Override
                        public void onTextBack(String msg) {
                            Alarm.getInstance(IndexActivity.this).setKnown(true);
                            tv_info.setText(msg);
                        }
                    });
                }
            });

        }
    }

    private void idcard_operation(final ICardInfo cardInfo) {
        SPUtils sp = SPUtils.getInstance(cardInfo.cardId());
        if (sp.getString("courType").equals(PersonType.KuGuan)) {
            if (getState(No_one_OperateState.class)) {
                global_Operation.setState(new One_man_OperateState());
                cg_User1.setCourIds(sp.getString("courIds"));
                cg_User1.setName(sp.getString("name"));
                cg_User1.setCardId(cardInfo.cardId());
                pp.capture();
            } else if (getState(Two_man_OperateState.class)) {
                if (!cardInfo.cardId().equals(cg_User1.getCardId())) {
                    cg_User2.setCourIds(sp.getString("courIds"));
                    cg_User2.setName(sp.getString("name"));
                    cg_User2.setCardId(cardInfo.cardId());
                    pp.capture();
                    EventBus.getDefault().post(new PassEvent());
                    iv_lock.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.newui_mj1));
                } else {
                    tv_info.setText("请不要连续输入相同的管理员信息");
                }
            } else if (getState(Door_Open_OperateState.class)) {
                tv_info.setText("仓库门已解锁");
            }
        } else if (sp.getString("courType").equals(PersonType.XunJian)) {
            if (checkChange != null) {
                checkChange.dispose();
            }
            cg_User1.setCourIds(sp.getString("courIds"));
            cg_User1.setName(sp.getString("name"));
            cg_User1.setCardId(cardInfo.cardId());
            checkRecord(String.valueOf(2));
        } else {
            RetrofitGenerator.getConnectApi().queryPersonInfo("queryPersonInfo", config.getString("key"), cardInfo.cardId())
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyObserver<ResponseBody>(this) {
                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                Map<String, String> infoMap = new Gson().fromJson(responseBody.string(),
                                        new TypeToken<HashMap<String, String>>() {
                                        }.getType());
                                if (infoMap.get("result").equals("true")) {
                                    if (infoMap.get("status").equals(String.valueOf(0))) {
                                        if (infoMap.get("courType").equals(PersonType.KuGuan)) {
                                            if (getState(No_one_OperateState.class)) {
                                                global_Operation.setState(new One_man_OperateState());
                                                cg_User1.setCourIds(infoMap.get("courIds"));
                                                cg_User1.setName(infoMap.get("name"));
                                                cg_User1.setCardId(cardInfo.cardId());
                                                pp.capture();
                                            } else if (getState(Two_man_OperateState.class)) {
                                                if (!cardInfo.cardId().equals(cg_User1.getCardId())) {
                                                    cg_User2.setCourIds(infoMap.get("courIds"));
                                                    cg_User2.setName(infoMap.get("name"));
                                                    cg_User2.setCardId(cardInfo.cardId());
                                                    pp.capture();
                                                    EventBus.getDefault().post(new PassEvent());
                                                    iv_lock.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.newui_mj1));
                                                } else {
                                                    tv_info.setText("请不要连续输入相同的管理员信息");
                                                }
                                            } else if (getState(Door_Open_OperateState.class)) {
                                                tv_info.setText("仓库门已解锁");
                                            }
                                        } else if (infoMap.get("courType").equals(PersonType.XunJian)) {
                                            if (checkChange != null) {
                                                checkChange.dispose();
                                            }
                                            cg_User1.setCourIds(infoMap.get("courIds"));
                                            cg_User1.setName(infoMap.get("name"));
                                            cg_User1.setCardId(cardInfo.cardId());
                                            checkRecord(String.valueOf(2));
                                        }
                                    } else {
                                        unknownUser.setName(cardInfo.name());
                                        unknownUser.setCardId(cardInfo.cardId());
                                        pp.capture();
                                    }
                                } else {
                                    unknownUser.setName(cardInfo.name());
                                    unknownUser.setCardId(cardInfo.cardId());
                                    pp.capture();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    });

//            unknownUser.setFingerprintId(sp);

        }
    }

    private void iccard_operation(ICardInfo cardInfo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ickBh", cardInfo.getUid());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RetrofitGenerator.getConnectApi().withDataRr("searchICKBd", config.getString("key"), jsonObject.toString())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<ResponseBody>(this) {
                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody.string().toString());
                            if (jsonObject.getString("result").equals("true")) {
                                JSONObject jsonArray = jsonObject.getJSONObject("data");
                                if (TextUtils.isEmpty(jsonArray.getString("courType")) || Integer.parseInt(jsonArray.getString("courType")) == 2) {
                                    cg_User1.setCourIds(jsonArray.getString("courids"));
                                    cg_User1.setCardId(jsonArray.getString("idcard"));
                                    cg_User1.setName(jsonArray.getString("name"));
                                    checkRecord(jsonArray.getString("courType"));
                                } else if (Integer.parseInt(jsonArray.getString("courType")) == 1) {
                                    if (getState(No_one_OperateState.class)) {
                                        global_Operation.setState(new One_man_OperateState());
                                        pp.capture();
                                        cg_User1.setCourIds(jsonArray.getString("courids"));
                                        cg_User1.setName(jsonArray.getString("name"));
                                        cg_User1.setCardId(jsonArray.getString("idcard"));
                                    } else if (getState(Two_man_OperateState.class)) {
                                        if (!jsonArray.getString("idcard").equals(cg_User1.getCardId())) {
                                            cg_User2.setCourIds(jsonArray.getString("courids"));
                                            cg_User2.setName(jsonArray.getString("name"));
                                            cg_User2.setCardId(jsonArray.getString("idcard"));
                                            pp.capture();
                                            EventBus.getDefault().post(new PassEvent());
                                            iv_lock.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.newui_mj1));
                                        } else {
                                            tv_info.setText("请不要连续输入相同的管理员信息");
                                        }
                                    } else if (getState(Door_Open_OperateState.class)) {
                                        tv_info.setText("仓库门已解锁");
                                    }
                                }
                            } else {
                                tv_info.setText("您的IC卡没有登记备案，请更换IC卡重试");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException exception) {
                            exception.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        tv_info.setText("服务器连接失败，无法辨别IC卡的有效性");
                    }

                });
    }


    @Override
    public void onsetCardImg(Bitmap bmp) {

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
        RetrofitGenerator.getConnectApi().noData("getTime", config.getString("key"))
                .subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                try {
                    String datetime = s;
                    AppInit.getMyManager().setTime(Integer.parseInt(datetime.substring(0, 4)),
                            Integer.parseInt(datetime.substring(5, 7)),
                            Integer.parseInt(datetime.substring(8, 10)),
                            Integer.parseInt(datetime.substring(11, 13)),
                            Integer.parseInt(datetime.substring(14, 16)),
                            Integer.parseInt(datetime.substring(17, 19)));
                } catch (Exception e) {
                    e.printStackTrace();
//                    ToastUtils.showLong(e.toString());
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

    private void deletePerson(String idcard, final String fingerId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", idcard);
            jsonObject.put("fingerprintId", fingerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitGenerator.getConnectApi().withDataRs("deleteFinger", config.getString("key"), jsonObject.toString())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<String>(this, true) {

                    @Override
                    public void onNext(String s) {
                        if (s.equals("true")) {
                            fpp.fpCancel(true);
                            Observable.timer(2, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Long>() {
                                        @Override
                                        public void accept(Long aLong) throws Exception {
                                            fpp.fpRemoveTmpl(fingerId);
                                            SPUtils.getInstance(fingerId).clear();
                                            fpp.fpIdentify();
                                        }
                                    });
                            ToastUtils.showLong("删除成功");
                        } else if (s.equals("false")) {
                            ToastUtils.showLong("删除失败");
                        } else if (s.equals("dataErr")) {
                            ToastUtils.showLong("服务出错");
                        } else if (s.equals("dbErr")) {
                            ToastUtils.showLong("数据库出错");
                        }
                    }

                });
    }

    private void checkRecord(String type) {
        SwitchPresenter.getInstance().OutD9(false);
        final JSONObject checkRecordJson = new JSONObject();
        try {
            checkRecordJson.put("id", cg_User1.getCardId());
            checkRecordJson.put("name", cg_User1.getName());
            checkRecordJson.put("checkType", type);
            checkRecordJson.put("datetime", TimeUtils.getNowString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitGenerator.getConnectApi().withDataRs("checkRecord", config.getString("key"), checkRecordJson.toString())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<String>(this) {

                    @Override
                    public void onNext(String s) {
                        if (s.equals("true")) {
                            if (cg_User1.getFingerprintId() != null) {
                                tv_info.setText("巡检员" + cg_User1.getName() + "巡检成功,指纹ID为" + cg_User1.getFingerprintId());
                            } else {
                                tv_info.setText("巡检员" + cg_User1.getName() + "巡检成功");
                            }
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
                        tv_info.setText("无法连接到服务器");
                        mdaoSession.insert(new ReUploadBean(null, "checkRecord", checkRecordJson.toString()));

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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitGenerator.getConnectApi().withDataRs("saveVisit", config.getString("key"), unknownPeopleJson.toString())
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
                            tv_info.setText("访问人" + unknownUser.getName() + "数据上传成功,指纹号为" + unknownUser.getFingerprintId());
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
                        unknownUser = new User();
                        pp.setDisplay(surfaceView.getHolder());
                        mdaoSession.insert(new ReUploadBean(null, "saveVisit", unknownPeopleJson.toString()));
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        pp.setDisplay(surfaceView.getHolder());
                    }
                });
    }

    private void equipment_sync(final String old_devid) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("oldDaid", old_devid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitGenerator.getConnectApi().withDataRr("searchFinger", config.getString("key"), jsonObject.toString())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<ResponseBody>(this, true) {
                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody.string().toString());
                            if (("true").equals(jsonObject.getString("result"))) {
                                final JSONArray jsonArray = jsonObject.getJSONArray("data");
                                if (null != jsonArray && jsonArray.length() != 0) {
                                    fpp.fpRemoveAll();
                                    Observable.timer(1, TimeUnit.SECONDS)
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Consumer<Long>() {
                                                @Override
                                                public void accept(Long aLong) throws Exception {
                                                    for (int i = 0; i < jsonArray.length(); i++) {
                                                        JSONObject item = jsonArray.getJSONObject(i);
                                                        SPUtils user_sp = SPUtils.getInstance(item.getString("pfpIds"));
                                                        fpp.fpDownTemplate(item.getString("pfpIds"), item.getString("fingerTemp"));
                                                        user_sp.put("courIds", item.getString("personIds"));
                                                        user_sp.put("name", item.getString("name"));
                                                        user_sp.put("cardId", item.getString("idcard"));
                                                        user_sp.put("courType", item.getString("courType"));
                                                    }
                                                    JSONObject jsonKey = new JSONObject();
                                                    try {
                                                        jsonKey.put("daid", old_devid);
                                                        jsonKey.put("check", DESX.encrypt(old_devid));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    config.put("daid", old_devid);
                                                    config.put("key", DESX.encrypt(jsonKey.toString()));
                                                    ToastUtils.showLong("设备数据更新成功");
                                                    config.put("sync29", false);
                                                    fpp.fpIdentify();
                                                }
                                            });
                                } else {
                                    ToastUtils.showLong("该设备号无人员数据");
                                    config.put("sync29", false);
                                    fpp.fpIdentify();

                                }
                            } else {
                                ToastUtils.showLong("设备号有误");
                                config.put("sync29", false);

                                fpp.fpIdentify();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        fpp.fpIdentify();
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
                OpenDoorJson.put("courIds1", cg_User1.getCourIds());
                OpenDoorJson.put("courIds2", cg_User2.getCourIds());
                OpenDoorJson.put("id1", cg_User1.getCardId());
                OpenDoorJson.put("id2", cg_User2.getCardId());
                OpenDoorJson.put("name1", cg_User1.getName());
                OpenDoorJson.put("name2", cg_User2.getName());
                OpenDoorJson.put("photo1", cg_User1.getPhoto());
                OpenDoorJson.put("photo2", cg_User2.getPhoto());
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
        RetrofitGenerator.getConnectApi().withDataRs("openDoorRecord", config.getString("key"), OpenDoorJson.toString())
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

    private void sync() {
        Observable.timer(5, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Long>bindUntilEvent(ActivityEvent.PAUSE))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        if (config.getBoolean("sync29", true) &&
                                AppInit.getInstrumentConfig().getClass().getName().equals(SZ_Config.class.getName())) {
                            fpp.fpCancel(true);
                            equipment_sync(config.getString("daid"));
                        }
                    }
                });
    }

}

