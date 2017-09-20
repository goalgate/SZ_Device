package com.sz_device;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.sz_device.EventBus.LegalEvent;
import com.sz_device.EventBus.NetworkEvent;
import com.sz_device.EventBus.OpenDoorEvent;
import com.sz_device.EventBus.TemHumEvent;
import com.sz_device.Fun_Camera.mvp.presenter.PhotoPresenter;
import com.sz_device.Fun_Camera.mvp.view.IPhotoView;
import com.sz_device.Fun_FingerPrint.mvp.presenter.FingerPrintPresenter;
import com.sz_device.Fun_FingerPrint.mvp.view.IFingerPrintView;
import com.sz_device.Retrofit.InterfaceApi.TestNetApi;
import com.sz_device.Retrofit.Request.RequestEnvelope;
import com.sz_device.Retrofit.Request.ResquestModule.CheckOnlineModule;
import com.sz_device.Retrofit.Request.ResquestModule.CheckRecordModule;
import com.sz_device.Retrofit.Request.ResquestModule.OpenDoorRecordModule;
import com.sz_device.Retrofit.Request.ResquestModule.QueryPersonInfoModule;
import com.sz_device.Retrofit.Request.ResquestModule.TestNetModule;
import com.sz_device.Retrofit.Response.ResponseEnvelope;
import com.sz_device.Retrofit.RetrofitGenerator;
import com.sz_device.Service.SwitchService;
import com.sz_device.Tools.FileUtils;
import com.sz_device.Tools.MyObserver;
import com.sz_device.Tools.User;
import com.sz_device.UI.AddPersonWindow;
import com.sz_device.UI.OptionWindow;


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
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by zbsz on 2017/8/25.
 */

public class IndexActivity extends Activity implements IFingerPrintView,IPhotoView, AddPersonWindow.OptionTypeListener,OptionWindow.OptionListener {

    private static final String PREFS_NAME = "UserInfo";

    public static final String Server_URL = "http://((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?):\\d{4}/";

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    String TAG = "IndexActivity";

    Intent intent;

    User checkUser ;

    User cg_User1 = new User();

    User cg_User2 = new User();

    int option;

    boolean network_state;

    FingerPrintPresenter fpp = FingerPrintPresenter.getInstance();

    PhotoPresenter pp = PhotoPresenter.getInstance();

    int fingerprintCount = 0;

    int photoCount = 0;

    private AddPersonWindow personWindow;

    private OptionWindow optionWindow;

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

    private AlertView inputServerView;
    private String url;
    private TextView dev_name;
    private EditText etName;

    @OnClick(R.id.iv_person_add)
    void addPerson() {
        personWindow = new AddPersonWindow(this);
        personWindow.setOptionTypeListener(this);
        personWindow.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
    }

    @OnClick(R.id.iv_option)
    void setOption(){
        optionWindow = new OptionWindow(this);
        optionWindow.setOptionListener(this);
        optionWindow.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        ButterKnife.bind(this);
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
        RxTextView.textChanges(tv_info)
                .debounce(10, TimeUnit.SECONDS)
                .switchMap(new Function<CharSequence, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(@NonNull CharSequence charSequence) throws Exception {
                        return Observable.just("等待用户操作");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        tv_info.setText(s);
                    }
                });

        inputServerView = new AlertView("服务器设置", null, "取消", new String[]{"确定"}, null, this, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                if (position == 0) {

                    Pattern pattern = Pattern.compile(Server_URL);

                    url = etName.getText().toString().replaceAll(" ", "");

                    if (!(url.endsWith("/"))) {
                        url = url + "/";
                    }
                    if (!(url.startsWith("http://"))) {
                        url = "http://" + url;
                    }
                    if (pattern.matcher(url).matches()) {
                        new RetrofitGenerator().createSer(TestNetApi.class, url).testNet(RequestEnvelope.GetRequestEnvelope(new TestNetModule(SPUtils.getInstance("UserInfo").getString("jsonKey"))))
                                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<ResponseEnvelope>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        ToastUtils.showLong("测试连接中请稍后");
                                    }

                                    @Override
                                    public void onNext(ResponseEnvelope responseEnvelope) {
                                        if (responseEnvelope != null) {
                                            Map<String, String> infoMap = new Gson().fromJson(responseEnvelope.body.testNetResponse.info,
                                                    new TypeToken<HashMap<String, String>>() {
                                                    }.getType());
                                            if (infoMap.get("result").equals("true")) {
                                                SPUtils.getInstance(PREFS_NAME).put("server", url);
                                                ToastUtils.showLong("服务器设置成功");
                                            } else {
                                                ToastUtils.showLong("设备出错");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        ToastUtils.showLong("连接服务器失败");
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    } else {
                        ToastUtils.showLong("请输入正确的服务器地址");
                    }
                }
            }
        });
        ViewGroup extView1 = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.inputserver_form, null);
        dev_name = (TextView) extView1.findViewById(R.id.dev_id);
        etName = (EditText) extView1.findViewById(R.id.server_input);
        inputServerView.addExtView(extView1);

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
        if(network_state){
            OpenDoorRecord(event.getLegal());
        }
        cg_User1 = new User();
        cg_User2 = new User();

    }

    @Override
    protected void onResume() {
        super.onResume();
        fpp.FingerPrintPresenterSetView(this);
        pp.PhotoPresenterSetView(this);
        fpp.fpIdentify();
        pp.initCamera();
        pp.setHolderAndDisplay(surfaceView.getHolder(),true);
        fingerprintCount = 0;
        photoCount = 0;
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
        fpp.fpCancel(true);
        fpp.FingerPrintPresenterSetView(null);
        fpp.fpClose();
        EventBus.getDefault().unregister(this);
        stopService(intent);
    }

    @Override
    public void onOptionType(Button view, int type) {
        personWindow.dismiss();
        if (type == 1) {
            ActivityUtils.startActivity(getPackageName(), getPackageName() + ".AddPersonActivity");
        } else {
            ToastUtils.showLong("该权限下无法删除人员信息");
        }
    }

    @Override
    public void onOption(Button view, int type) {
        optionWindow.dismiss();
        if (type == 1) {
            dev_name.setText(SPUtils.getInstance(PREFS_NAME).getString("dev_id"));
            etName.setText(SPUtils.getInstance(PREFS_NAME).getString("server"));
            inputServerView.show();
        } else {

        }

    }

    @Override
    public void onCaremaText(String s) {
        pp.setDisplay(surfaceView.getHolder());
    }

    @Override
    public void onGetPhoto(Bitmap bmp) {
        if(option == 1){
            photoCount++;
            if(photoCount == 1){
                cg_User1.setPhoto(FileUtils.bitmapToBase64(bmp));
            }else if(photoCount == 2){
                cg_User2.setPhoto(FileUtils.bitmapToBase64(bmp));
            }
        }else if(option == 2){
            if(network_state){
                CheckRecord(bmp);
            }
        }
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
        if (fingerprintCount < 2) {
            if (msg.substring(0, 3).equals("TAG")) {
                QueryPersonInfo(msg.substring(3, msg.length()));
                if(SPUtils.getInstance(msg.substring(3, msg.length())).getString("type").equals(String.valueOf(1))){
                    option = 1;

                    if (fingerprintCount == 0) {
                        pp.capture();
                        fingerprintCount++;
                        cg_User1.setName(SPUtils.getInstance(msg.substring(3, msg.length())).getString("name"));
                        cg_User1.setId(SPUtils.getInstance(msg.substring(3, msg.length())).getString("id"));
                        tv_info.setText("管理员" + cg_User1.getName() + "打卡，请继续输入管理员信息");
                        Observable.timer(60, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<Long>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(Long aLong) {
                                        fingerprintCount = 0;
                                        cg_User1 = new User();
                                        cg_User2 = new User();
                                        photoCount = 0;
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    } else if (fingerprintCount == 1) {
                        if (!cg_User1.getName().equals(SPUtils.getInstance(msg.substring(3, msg.length())).getString("name"))) {
                            pp.capture();
                            fingerprintCount++;
                            tv_info.setText("管理员" + SPUtils.getInstance(msg.substring(3, msg.length())).getString("name") + "打卡，双人管理成功");
                            EventBus.getDefault().post(new LegalEvent(true));
                            cg_User2.setName(SPUtils.getInstance(msg.substring(3, msg.length())).getString("name"));
                            cg_User2.setId(SPUtils.getInstance(msg.substring(3, msg.length())).getString("id"));
                        } else if (cg_User1.getName().equals(SPUtils.getInstance(msg.substring(3, msg.length())).getString("name")) && tv_info.getText().toString().equals("松开手指")) {
                            tv_info.setText("请不要连续输入相同的管理员信息");
                        }
                    }
                }else if(SPUtils.getInstance(msg.substring(3, msg.length())).getString("type").equals(String.valueOf(2))||SPUtils.getInstance(msg.substring(3, msg.length())).getString("type").equals(String.valueOf(3))){
                    fingerprintCount = 0;
                    option = 2;
                    pp.capture();
                    checkUser = new User();
                    checkUser.setId(SPUtils.getInstance(msg.substring(3, msg.length())).getString("id"));
                    checkUser.setName(SPUtils.getInstance(msg.substring(3, msg.length())).getString("name"));
                    checkUser.setType(SPUtils.getInstance(msg.substring(3, msg.length())).getString("type"));
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
        JSONObject jsonObject = new JSONObject();
        if (leagl){
            try {
                jsonObject.put("id1",cg_User1.getId());
                jsonObject.put("name1",cg_User1.getName());
                jsonObject.put("id2", cg_User2.getId());
                jsonObject.put("name2", cg_User2.getName());
                jsonObject.put("photo1", cg_User1.getPhoto());
                jsonObject.put("photo2", cg_User2.getPhoto());
                jsonObject.put("datetime",TimeUtils.getNowString());
                jsonObject.put("state","y");
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            try {
                jsonObject.put("id1",cg_User1.getId());
                jsonObject.put("name1",cg_User1.getName());
                jsonObject.put("id2", cg_User2.getId());
                jsonObject.put("name2", cg_User2.getName());
                jsonObject.put("photo1", cg_User1.getPhoto());
                jsonObject.put("photo2", cg_User2.getPhoto());
                jsonObject.put("datetime",TimeUtils.getNowString());
                jsonObject.put("state","n");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        RetrofitGenerator.getOpenDoorRecordApi().openDoorRecord(RequestEnvelope.GetRequestEnvelope(
                new OpenDoorRecordModule(SPUtils.getInstance(PREFS_NAME).getString("jsonKey"),jsonObject.toString())
        )).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new MyObserver());

    }


    private void CheckRecord(Bitmap bmp){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id",checkUser.getId());
            jsonObject.put("name",checkUser.getName());
            jsonObject.put("photo", FileUtils.bitmapToBase64(bmp));
            jsonObject.put("personType",checkUser.getType());
            jsonObject.put("datetime",TimeUtils.getNowString());
        }catch (Exception e){
            e.printStackTrace();
        }
        RetrofitGenerator.getCheckRecordApi().checkRecord(RequestEnvelope.GetRequestEnvelope(
                new CheckRecordModule(SPUtils.getInstance(PREFS_NAME).getString("jsonKey"),jsonObject.toString())
        )).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseEnvelope>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseEnvelope responseEnvelope) {
                Map<String, String> infoMap = new Gson().fromJson(responseEnvelope.body.checkRecordResponse.info,
                        new TypeToken<HashMap<String, String>>() {
                        }.getType());
                if (infoMap.get("result").equals("true")) {
                    ToastUtils.showLong("上传成功");
                }else{
                    ToastUtils.showLong("上传失败");
                }
            }

            @Override
            public void onError(Throwable e) {
                ToastUtils.showLong("上传失败");
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void QueryPersonInfo(String sp){
        final String spname =sp;
        if(SPUtils.getInstance(sp).getBoolean("need_check",true)){
            RetrofitGenerator.getQueryPersonInfoApi().
                    QueryPersonInfo(RequestEnvelope.GetRequestEnvelope(new QueryPersonInfoModule(
                            SPUtils.getInstance(PREFS_NAME).getString("jsonKey"),SPUtils.getInstance(sp).getString("id")
                    ))).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseEnvelope>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(ResponseEnvelope responseEnvelope) {
                    Map<String, String> infoMap = new Gson().fromJson(responseEnvelope.body.queryPersonInfoResponse.info,
                            new TypeToken<HashMap<String, String>>() {
                            }.getType());
                    if (infoMap.get("result").equals("true")) {
                        SPUtils.getInstance(spname).put("need_check",false);
                        SPUtils.getInstance(spname).put("type",infoMap.get("info"));
                        tv_info.setText("该人员的身份合法性已在机器验证");
                    }
                }

                @Override
                public void onError(Throwable e) {
                    tv_info.setText("服务器出错，无法验证合法性");

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