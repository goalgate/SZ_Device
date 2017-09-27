package com.sz_device;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.sz_device.EventBus.LegalEvent;
import com.sz_device.EventBus.NetworkEvent;
import com.sz_device.EventBus.OpenDoorEvent;
import com.sz_device.EventBus.TemHumEvent;
import com.sz_device.Fun_Camera.mvp.presenter.PhotoPresenter;
import com.sz_device.Fun_Camera.mvp.view.IPhotoView;
import com.sz_device.Fun_FingerPrint.mvp.presenter.FingerPrintPresenter;
import com.sz_device.Fun_FingerPrint.mvp.view.IFingerPrintView;
import com.sz_device.OperationState.Check_OperateState;
import com.sz_device.OperationState.No_one_OperateState;
import com.sz_device.OperationState.One_man_OperateState;
import com.sz_device.OperationState.Operation;
import com.sz_device.OperationState.Two_man_OperateState;
import com.sz_device.Retrofit.InterfaceApi.CommonApi;
import com.sz_device.Retrofit.Request.RequestEnvelope;
import com.sz_device.Retrofit.Request.ResquestModule.CommonRequestModule;
import com.sz_device.Retrofit.Request.ResquestModule.OnlyPutKeyModule;
import com.sz_device.Retrofit.Request.ResquestModule.QueryPersonInfoModule;
import com.sz_device.Retrofit.Response.ResponseEnvelope;
import com.sz_device.Retrofit.RetrofitGenerator;
import com.sz_device.Service.SwitchService;
import com.sz_device.Tools.DaoSession;
import com.sz_device.Tools.FileUtils;
import com.sz_device.Tools.MyObserver;
import com.sz_device.Tools.UnUploadPackage;
import com.sz_device.Tools.UnUploadPackageDao;
import com.sz_device.Tools.User;
import com.sz_device.UI.AddPersonWindow;
import com.sz_device.UI.OptionWindow;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

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

import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.checkRecord;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.downPersonInfo;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.openDoorRecord;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.testNet;

/**
 * Created by zbsz on 2017/8/25.
 */

public class IndexActivity extends Activity implements IFingerPrintView, IPhotoView, AddPersonWindow.OptionTypeListener, OptionWindow.OptionListener {

    private static final String PREFS_NAME = "UserInfo";

    public static final String Server_URL = "http://((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?):\\d{4}/";

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    String TAG = "IndexActivity";

    Intent intent;

    User cg_User1 = new User();

    User cg_User2 = new User();

    boolean network_state;

    FingerPrintPresenter fpp = FingerPrintPresenter.getInstance();

    PhotoPresenter pp = PhotoPresenter.getInstance();

    private AddPersonWindow personWindow;

    private OptionWindow optionWindow;

    No_one_OperateState no_one_operateState = new No_one_OperateState();

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

    UnUploadPackageDao unUploadPackageDao;

    Operation global_Operation;

    String global_user_fpid;

    @OnClick(R.id.iv_person_add)
    void addPerson() {
        personWindow = new AddPersonWindow(this);
        personWindow.setOptionTypeListener(this);
        personWindow.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
    }

    @OnClick(R.id.iv_option)
    void setOption() {
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

        DaoSession daoSession = AppInit.getInstance().getDaoSession();
        unUploadPackageDao = daoSession.getUnUploadPackageDao();
        openService();

        fpp.fpInit(this);
        fpp.fpOpen();

        Observable.interval(0, 1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(@NonNull Long aLong) throws Exception {
                tv_time.setText(formatter.format(new Date(System.currentTimeMillis())));
            }
        });

        RxTextView.textChanges(tv_info)
                .debounce(60, TimeUnit.SECONDS)
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

        ViewGroup extView1 = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.inputserver_form, null);
        dev_name = (TextView) extView1.findViewById(R.id.dev_id);
        etName = (EditText) extView1.findViewById(R.id.server_input);
        inputServerView = new AlertView("服务器设置", null, "取消", new String[]{"确定"}, null, this, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                inputServerSetting(position);
            }
        });
        inputServerView.addExtView(extView1);

        global_Operation = new Operation(new No_one_OperateState());
    }

    private void inputServerSetting(int position) {
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
                new RetrofitGenerator().createSer(CommonApi.class, url).commonFunction(RequestEnvelope.GetRequestEnvelope(new OnlyPutKeyModule(testNet)))
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
        cg_User1 = new User();
        cg_User2 = new User();
        global_Operation.setState(no_one_operateState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fpp.FingerPrintPresenterSetView(this);
        pp.PhotoPresenterSetView(this);
        fpp.fpIdentify();
        pp.initCamera();
        pp.setHolderAndDisplay(surfaceView.getHolder(), true);
        global_Operation.setState(no_one_operateState);
        Log.e(TAG, "onResume0");
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

    SPUtils data;
    @Override
    public void onOption(Button view, int type) {
        optionWindow.dismiss();
        if (type == 1) {
            dev_name.setText(SPUtils.getInstance(PREFS_NAME).getString("dev_id"));
            etName.setText(SPUtils.getInstance(PREFS_NAME).getString("server"));
            inputServerView.show();
        } else {

            RetrofitGenerator.getCommonApi().commonFunction(RequestEnvelope.GetRequestEnvelope(
                    new OnlyPutKeyModule(downPersonInfo)))
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseEnvelope>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull ResponseEnvelope responseEnvelope) {
                            List<String> dataList = responseEnvelope.body.downPersonInfoResponse.Listinfo;
                            String result = dataList.get(0);
                            Map<String, String> infoMap = new Gson().fromJson(result,
                                    new TypeToken<HashMap<String, String>>() {
                                    }.getType());
                            if (infoMap.get("result").equals("checkErr")) {
                                ToastUtils.showLong("设备出错");
                                return;
                            } else if (infoMap.get("result").equals("noData")) {
                                ToastUtils.showLong("找不到相应的数据");
                                return;
                            } else if (infoMap.get("result").equals("true")) {
                                dataList.remove(0);
                                for (String siminfo : dataList) {
                                    Map<String, String> simInfoMap = new Gson().fromJson(siminfo,
                                            new TypeToken<HashMap<String, String>>() {
                                            }.getType());
                                    data = SPUtils.getInstance(simInfoMap.get("fp_id"));
                                    data.put("id", simInfoMap.get("id"));
                                    data.put("name", simInfoMap.get("name"));
                                    data.put("type", simInfoMap.get("personType"));
                                }
                                ToastUtils.showLong("人员信息更新成功");
                            }
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

    @Override
    public void onCaremaText(String s) {
        pp.setDisplay(surfaceView.getHolder());

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
    public void onFpSucc(String msg) {
        loadMessage(msg.substring(3, msg.length()));

    }

    private void OpenDoorRecord(boolean leagl) {
        final JSONObject openDoorRecordJson = new JSONObject();
        if (leagl) {
            try {
                openDoorRecordJson.put("id1", cg_User1.getId());
                openDoorRecordJson.put("name1", cg_User1.getName());
                openDoorRecordJson.put("id2", cg_User2.getId());
                openDoorRecordJson.put("name2", cg_User2.getName());
                openDoorRecordJson.put("photo1", cg_User1.getPhoto());
                openDoorRecordJson.put("photo2", cg_User2.getPhoto());
                openDoorRecordJson.put("datetime", TimeUtils.getNowString());
                openDoorRecordJson.put("state", "y");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                openDoorRecordJson.put("id1", cg_User1.getId());
                openDoorRecordJson.put("name1", cg_User1.getName());
                openDoorRecordJson.put("id2", cg_User2.getId());
                openDoorRecordJson.put("name2", cg_User2.getName());
                openDoorRecordJson.put("photo1", cg_User1.getPhoto());
                openDoorRecordJson.put("photo2", cg_User2.getPhoto());
                openDoorRecordJson.put("datetime", TimeUtils.getNowString());
                openDoorRecordJson.put("state", "n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (network_state) {
            CommonRequestModule openDoorRecordM = new CommonRequestModule(openDoorRecord, openDoorRecordJson.toString());
            RetrofitGenerator.getCommonApi().commonFunction(RequestEnvelope.GetRequestEnvelope(openDoorRecordM))
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyObserver(unUploadPackageDao, openDoorRecordM));
        } else {
            UnUploadPackage un = new UnUploadPackage();
            un.setMethod(openDoorRecord);
            un.setJsonData(openDoorRecordJson.toString());
            un.setUpload(false);
            unUploadPackageDao.insert(un);
        }
    }


 /*   private void CheckRecord() {
        final JSONObject checkRecordJson = new JSONObject();
        try {
            checkRecordJson.put("id", SPUtils.getInstance(global_user_fpid).getString("id"));
            checkRecordJson.put("name", SPUtils.getInstance(global_user_fpid).getString("name"));
            checkRecordJson.put("photo", FileUtils.bitmapToBase64(bmp));
            checkRecordJson.put("personType", SPUtils.getInstance(global_user_fpid).getString("type"));
            checkRecordJson.put("datetime", TimeUtils.getNowString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (network_state) {
            CommonRequestModule checkRecordM = new CommonRequestModule(checkRecord,checkRecordJson.toString());
            RetrofitGenerator.getCommonApi().commonFunction(RequestEnvelope.GetRequestEnvelope(checkRecordM))
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseEnvelope>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            tv_info.setText("联网中，请稍后");
                        }

                        @Override
                        public void onNext(ResponseEnvelope responseEnvelope) {
                            Map<String, String> infoMap = new Gson().fromJson(responseEnvelope.body.checkRecordResponse.info,
                                    new TypeToken<HashMap<String, String>>() {
                                    }.getType());
                            if (infoMap.get("result").equals("true")) {
                                ToastUtils.showLong("上传成功");
                            } else {
                                ToastUtils.showLong("上传失败");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            UnUploadPackage un = new UnUploadPackage();
                            un.setMethod(checkRecord);
                            un.setJsonData(checkRecordJson.toString());
                            un.setUpload(false);
                            unUploadPackageDao.insert(un);
                            ToastUtils.showLong("上传失败");

                        }

                        @Override
                        public void onComplete() {


                        }
                    });
        } else {
            ToastUtils.showLong("巡检员"+checkUser.getName()+"巡检成功");
            UnUploadPackage un = new UnUploadPackage();
            un.setMethod(checkRecord);
            un.setJsonData(checkRecordJson.toString());
            un.setUpload(false);
            unUploadPackageDao.insert(un);
        }

    }*/

  /*  private void QueryPersonInfo(final String sp) {
        final String spname = sp;

        if (network_state) {
            RetrofitGenerator.getCommonApi().commonFunction(RequestEnvelope.GetRequestEnvelope(new QueryPersonInfoModule(SPUtils.getInstance(sp).getString("id"))))
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseEnvelope>() {
                @Override
                public void onSubscribe(Disposable d) {
                    tv_info.setText("联网中，请稍后");
                }

                @Override
                public void onNext(ResponseEnvelope responseEnvelope) {
                    Map<String, String> infoMap = new Gson().fromJson(responseEnvelope.body.queryPersonInfoResponse.info,
                            new TypeToken<HashMap<String, String>>() {
                            }.getType());
                    if (infoMap.get("result").equals("true")) {
                        SPUtils.getInstance(spname).put("type", infoMap.get("info"));
                        loadMessage(sp);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    loadMessage(sp);

                }

                @Override
                public void onComplete() {

                }
            });
        } else {
            loadMessage(sp);
        }

    }*/

    private void loadMessage(String sp) {
        if (SPUtils.getInstance(sp).getString("type").equals(String.valueOf(1))) {
            if (getState(No_one_OperateState.class)) {
                global_Operation.setState(new One_man_OperateState());
                pp.capture();
                cg_User1.setName(SPUtils.getInstance(sp).getString("name"));
                cg_User1.setId(SPUtils.getInstance(sp).getString("id"));
                Observable.timer(60, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Long>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Long aLong) {
                                global_Operation.setState(new No_one_OperateState());
                                cg_User1 = new User();
                                cg_User2 = new User();

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            } else if (getState(Two_man_OperateState.class)) {
                if (!cg_User1.getName().equals(SPUtils.getInstance(sp).getString("name"))) {
                    pp.capture();
                    EventBus.getDefault().post(new LegalEvent(true));
                    cg_User2.setName(SPUtils.getInstance(sp).getString("name"));
                    cg_User2.setId(SPUtils.getInstance(sp).getString("id"));
                } else if (cg_User1.getName().equals(SPUtils.getInstance(sp).getString("name"))) {
                    tv_info.setText("请不要连续输入相同的管理员信息");
                }
            }
        } else if (SPUtils.getInstance(sp).getString("type").equals(String.valueOf(2)) || SPUtils.getInstance(sp).getString("type").equals(String.valueOf(3))) {
            global_user_fpid = sp;
            global_Operation.setState(new Check_OperateState());
            pp.capture();

        } else {
            tv_info.setText("该人员的身份合法性尚未通过");
        }


    }

    @Override
    public void onGetPhoto(Bitmap bmp) {
        if (getState(Check_OperateState.class)) {
            JSONObject checkRecordJson = new JSONObject();
            try {
                checkRecordJson.put("id", SPUtils.getInstance(global_user_fpid).getString("id"));
                checkRecordJson.put("name", SPUtils.getInstance(global_user_fpid).getString("name"));
                checkRecordJson.put("photo", FileUtils.bitmapToBase64(bmp));
                checkRecordJson.put("personType", SPUtils.getInstance(global_user_fpid).getString("type"));
                checkRecordJson.put("datetime", TimeUtils.getNowString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            CommonRequestModule checkRecordM = new CommonRequestModule(checkRecord, checkRecordJson.toString());
            global_Operation.setMessage(unUploadPackageDao, checkRecordM, network_state);
            global_Operation.doNext();
        } else if (getState(One_man_OperateState.class)) {
            cg_User1.setPhoto(FileUtils.bitmapToBase64(bmp));
            tv_info.setText("管理员" + cg_User1.getName() + "打卡,请继续管理员操作");
            global_Operation.doNext();
        } else if (getState(Two_man_OperateState.class)) {
            cg_User2.setPhoto(FileUtils.bitmapToBase64(bmp));
            tv_info.setText("管理员" + cg_User2.getName() + "打卡，双人管理成功");
            global_Operation.doNext();
        }
        Matrix matrix = new Matrix();
        matrix.postScale(0.5f, 0.5f);
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        captured.setImageBitmap(bmp);
        Observable.timer(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        captured.setImageBitmap(null);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private Boolean getState(Class statcClass) {
        if (global_Operation.getState().getClass().getName().equals(statcClass.getName())) {
            return true;
        } else {
            return false;
        }
    }

}