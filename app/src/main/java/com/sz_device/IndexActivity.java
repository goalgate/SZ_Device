package com.sz_device;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sz_device.EventBus.LegalEvent;
import com.sz_device.EventBus.NetworkEvent;
import com.sz_device.EventBus.OpenDoorEvent;
import com.sz_device.EventBus.TemHumEvent;
import com.sz_device.Fun_Camera.mvp.presenter.PhotoPresenter;
import com.sz_device.Fun_Camera.mvp.view.IPhotoView;
import com.sz_device.Fun_FingerPrint.mvp.presenter.FingerPrintPresenter;
import com.sz_device.Fun_FingerPrint.mvp.view.IFingerPrintView;
import com.sz_device.Retrofit.Request.RequestEnvelope;
import com.sz_device.Retrofit.Request.ResquestModule.CheckOnlineModule;
import com.sz_device.Retrofit.Request.ResquestModule.QueryPersonInfoModule;
import com.sz_device.Retrofit.Request.ResquestModule.TestNetModule;
import com.sz_device.Retrofit.Response.ResponseEnvelope;
import com.sz_device.Retrofit.RetrofitGenerator;
import com.sz_device.Service.SwitchService;
import com.sz_device.Tools.AppActivitys;
import com.sz_device.UI.AddPersonWindow;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

/**
 * Created by zbsz on 2017/8/25.
 */

public class IndexActivity extends Activity implements IFingerPrintView,IPhotoView, AddPersonWindow.OptionTypeListener {

    private static final String PREFS_NAME = "UserInfo";

    boolean network_state;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    String TAG = "IndexActivity";

    Intent intent;

    FingerPrintPresenter fpp = FingerPrintPresenter.getInstance();

    PhotoPresenter pp = PhotoPresenter.getInstance();

    int successCount = 0;

    String last_men;

    private AddPersonWindow popUpWindow;

    @BindView(R.id.img_captured)
    ImageView captured;

    @BindView(R.id.tv_info)
    TextView tv_info;

    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;


    @BindView(R.id.network_state)
    TextView tv_network_state;

    @BindView(R.id.tv_time)
    TextView tv_time;

    @BindView(R.id.tv_temperature)
    TextView tv_temperature;

    @BindView(R.id.tv_humidity)
    TextView tv_humidity;

    @OnClick(R.id.iv_person_add)
    void addPerson() {
        popUpWindow = new AddPersonWindow(this);
        popUpWindow.setOptionTypeListener(this);
        popUpWindow.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        ButterKnife.bind(this);
        AppActivitys.getInstance().addActivity(this);
        EventBus.getDefault().register(this);
        openService();

        fpp.fpInit(this);
        fpp.fpOpen();
        Observable.interval(0, 1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(@NonNull Long aLong) throws Exception {
                tv_time.setText(formatter.format(new Date(System.currentTimeMillis())));
            }
        });

        Observable.interval(1, 1, TimeUnit.HOURS).observeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        if(network_state){
                            RetrofitGenerator.getCheckOnlineApi().CheckOnline(RequestEnvelope.GetRequestEnvelope(new CheckOnlineModule(SPUtils.getInstance(PREFS_NAME).getString("jsonKey")))).enqueue(new Callback<ResponseEnvelope>() {
                                @Override
                                public void onResponse(Call<ResponseEnvelope> call, Response<ResponseEnvelope> response) {
                                }

                                @Override
                                public void onFailure(Call<ResponseEnvelope> call, Throwable t) {
                                }
                            });
                        }
                    }
                });

    }



    void openService() {
        intent = new Intent(IndexActivity.this, SwitchService.class);
        startService(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTemHumEvent(TemHumEvent event) {
        tv_temperature.setText(event.getTem() + "度");
        tv_humidity.setText(event.getHum() + "%");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetNetworkEvent(NetworkEvent event) {
        tv_network_state.setText(event.getMsg());
        network_state = event.getNetwork_state();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetOpenDoorEvent(OpenDoorEvent event) {
        OpenDoorRecord(event.getLegal());
    }

    @Override
    protected void onResume() {
        super.onResume();
        fpp.FingerPrintPresenterSetView(this);
        pp.PhotoPresenterSetView(this);
        fpp.fpIdentify();
        pp.initCamera();
        pp.setHolderAndDisplay(surfaceView.getHolder(),true);
        successCount = 0;
        Log.e(TAG,"onResume0");
    }



    @Override
    protected void onPause() {
        super.onPause();
        fpp.fpCancel(true);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        stopService(intent);
    }

    @Override
    public void onOptionType(Button view, int type) {
        popUpWindow.dismiss();
        if (type == 1) {
            ActivityUtils.startActivity(getPackageName(), getPackageName() + ".AddPersonActivity");
        } else {
            ToastUtils.showLong("该权限下无法删除人员信息");
        }
    }

    @Override
    public void onCaremaText(String s) {
        pp.setDisplay(surfaceView.getHolder());
    }

    @Override
    public void onGetPhoto(Bitmap bmp) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.5f,0.5f);
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),bmp.getHeight(),matrix,true);
        captured.setImageBitmap(bmp);
        Observable.timer(1,TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        captured.setImageBitmap(null);
                    }
                });
    }

    @Override
    public void onSetImg(Bitmap bmp) {

    }

    @Override
    public void onText(String msg) {
        if (successCount < 2) {
            if (msg.substring(0, 3).equals("TAG")) {
                QueryPersonInfo(msg.substring(3, 4));
                if(SPUtils.getInstance(msg.substring(3, 4)).getString("type").equals(String.valueOf(1))){
                    pp.capture();
                    if (successCount == 0) {
                        last_men = SPUtils.getInstance(msg.substring(3, 4)).getString("name");
                        successCount++;
                        tv_info.setText("管理员" + last_men + "打卡，请继续输入管理员信息");
                        Observable.timer(60, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<Long>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(Long aLong) {
                                        successCount = 0;

                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    } else if (successCount == 1) {
                        if (!last_men.equals(SPUtils.getInstance(msg.substring(3, 4)).getString("name"))) {
                            tv_info.setText("管理员" + SPUtils.getInstance(msg.substring(3, 4)).getString("name") + "打卡，双人管理成功");
                            EventBus.getDefault().post(new LegalEvent(true));
                            successCount++;
                            last_men = null;
                        } else if (last_men.equals(SPUtils.getInstance(msg.substring(3, 4)).getString("name")) && tv_info.getText().toString().equals("松开手指")) {
                            tv_info.setText("请不要连续输入相同的管理员信息");
                        }
                    }
                }else if(SPUtils.getInstance(msg.substring(3, 4)).getString("type").equals(String.valueOf(2))){
                    successCount = 0;
                }else {
                    tv_info.setText("该人员的身份合法性尚未通过");
                }
            } else if ("请确认指纹是否已登记".equals(msg)) {
                tv_info.setText("请确认指纹是否已登记,再重试");
            } else if ("松开手指".equals(msg)) {
                tv_info.setText(msg);
            }
        }
    }




    private void OpenDoorRecord(boolean leagl){

    }

    private void QueryPersonInfo(String sp){
        final String spname =sp;
        if(SPUtils.getInstance(sp).getBoolean("check",false)){
            RetrofitGenerator.getQueryPersonInfoApi().
                    QueryPersonInfo(RequestEnvelope.GetRequestEnvelope(new QueryPersonInfoModule(
                            SPUtils.getInstance(PREFS_NAME).getString("jsonKey"),SPUtils.getInstance(sp).getString("id")
                    ))).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseEnvelope>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(ResponseEnvelope responseEnvelope) {
                    Map<String, String> infoMap = new Gson().fromJson(responseEnvelope.body.registerPersonResponse.info,
                            new TypeToken<HashMap<String, String>>() {
                            }.getType());
                    if (infoMap.get("result").equals("true")) {
                        SPUtils.getInstance(spname).put("check",true);
                        SPUtils.getInstance(spname).put("type",infoMap.get("info"));
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





    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}