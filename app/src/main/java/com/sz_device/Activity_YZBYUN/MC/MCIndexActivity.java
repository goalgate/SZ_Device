package com.sz_device.Activity_YZBYUN.MC;

import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.log.Lg;
import com.sz_device.Activity_YZBYUN.YunService;
import com.sz_device.Alerts.Alarm;
import com.sz_device.Alerts.Alert_IP;
import com.sz_device.Alerts.Alert_Message;
import com.sz_device.Alerts.Alert_Password;
import com.sz_device.Alerts.Alert_Server;
import com.sz_device.AppInit;
import com.sz_device.Bean.ReUploadBean;
import com.sz_device.EventBus.AlarmEvent;
import com.sz_device.EventBus.NetworkEvent;
import com.sz_device.Function.Func_Camera.mvp.presenter.PhotoPresenter;
import com.sz_device.Function.Func_Switch.mvp.presenter.SwitchPresenter;
import com.sz_device.R;
import com.sz_device.State.OperationState.No_one_OperateState;
import com.sz_device.State.OperationState.Operation;
import com.sz_device.Tools.MediaHelper;
import com.sz_device.Tools.ServerConnectionUtil;
import com.sz_device.UI.AlarmRecordAdapter;
import com.sz_device.UI.NormalWindow;
import com.sz_device.greendao.DaoSession;
import com.sz_device.greendao.ReUploadBeanDao;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.RxActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

public class MCIndexActivity extends RxActivity implements NormalWindow.OptionTypeListener{

    DaoSession mdaoSession = AppInit.getInstance().getDaoSession();

    int alarmCount = 5;

    AlarmRecordAdapter alarmRecordAdapter;

    List<ReUploadBean> showList= new ArrayList<>();

    Disposable disposable_alarmCease;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private SPUtils config = SPUtils.getInstance("config");

    Intent intent;

    Disposable disposableTips;

    private NormalWindow normalWindow;

    No_one_OperateState no_one_operateState = new No_one_OperateState();

    @BindView(R.id.tv_info)
    TextView tv_info;

    @BindView(R.id.iv_network)
    ImageView iv_network;

    @BindView(R.id.tv_time)
    TextView tv_time;

    @BindView(R.id.id_recyclerview)
    RecyclerView recyclerView;

    Operation global_Operation;

    Alert_Message alert_message = new Alert_Message(this);

    Alert_Server alert_server = new Alert_Server(this);

    Alert_Password alert_password = new Alert_Password(this);

    @OnClick(R.id.lay_setting)
    void option() {
        alert_password.show();
    }

    @OnClick(R.id.lay_network)
    void showMessage() {
        alert_message.showMessage();
    }

    @OnClick(R.id.lay_lock)
    void delete() {
        mdaoSession.deleteAll(ReUploadBean.class);
        alarmDataGet();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mc_adapter);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        autoUpdate();

        MediaHelper.mediaOpen();
        Lg.e("key", config.getString("key"));
        Log.d("width", String.valueOf(ScreenUtils.getScreenWidth()));
        Log.d("height", String.valueOf(ScreenUtils.getScreenHeight()));
        openService();
        network_state = false;
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
        MyAlarmInit();
        alert_server.serverInit(new Alert_Server.Server_Callback() {
            @Override
            public void setNetworkBmp() {
                iv_network.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.newui_wifi));
            }
        });
        alert_password.PasswordViewInit(new Alert_Password.Callback() {
            @Override
            public void normal_call() {
            }

            @Override
            public void super_call() {
                normalWindow = new NormalWindow(MCIndexActivity.this);
                normalWindow.setOptionTypeListener(MCIndexActivity.this);
                normalWindow.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content), Gravity.CENTER, 0, 0);

            }
        });
        alert_message.messageInit();

        recycleViewInit();
        alarmDataGet();
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
                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                            startActivity(intent);
//                            NetworkUtils.openWirelessSettings();
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
        intent = new Intent(MCIndexActivity.this, MCService.class);
        startService(intent);
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


    AlertView alert_myAlarm;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetAlarmEvent(AlarmEvent event) {
        mdaoSession.insert(new ReUploadBean(null,"alarm",formatter.format(new Date(System.currentTimeMillis()))));
        alarmDataGet();
        if(!alert_myAlarm.isShowing()){
            alert_myAlarm.show();
            Observable.interval(0, 2, TimeUnit.SECONDS)
                    .take(alarmCount + 1)
                    .map((aLong)->{
                        return alarmCount - aLong;
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            disposable_alarmCease=d;

                        }

                        @Override
                        public void onNext(@NonNull Long aLong) {
                            MediaHelper.play(MediaHelper.Text.alarm);
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



    private void MyAlarmInit(){
        ViewGroup alarmView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.alarm_text, null);
        TextView alarmText = (TextView) alarmView.findViewById(R.id.alarmText);
        alarmText.setText("周界红外报警");
        alert_myAlarm = new AlertView("", null, null, new String[]{"确定"}, null, this, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                disposable_alarmCease.dispose();
            }
        });
        alert_myAlarm.addExtView(alarmView);
    }



    @Override
    public void onResume() {
        super.onResume();
        global_Operation.setState(no_one_operateState);
        tv_info.setText("等待用户操作...");
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
        Alarm.getInstance(this).release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaHelper.mediaRealese();
        EventBus.getDefault().unregister(this);
        disposableTips.dispose();
    }



    @Override
    public void onOptionType(Button view, int type) {
        normalWindow.dismiss();
        if (type == 1) {
            alert_server.show();
        } else if (type == 2) {
            NetworkUtils.openWirelessSettings();
        }
    }

    @Override
    public void onBackPressed() {

    }


    private void alarmDataGet(){
        showList.clear();
        QueryBuilder queryBuilder = mdaoSession.queryBuilder(ReUploadBean.class);
        queryBuilder.orderDesc(ReUploadBeanDao.Properties.Id);
        List<ReUploadBean> list = queryBuilder.list();
        for (ReUploadBean bean :list){
            if(bean.getMethod().equals("alarm")){
                showList.add(bean);
            }
        }
        alarmRecordAdapter.notifyDataSetChanged();
    }

    private void recycleViewInit() {
        alarmRecordAdapter = new AlarmRecordAdapter(this, showList);
        recyclerView.setAdapter(alarmRecordAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }


}



