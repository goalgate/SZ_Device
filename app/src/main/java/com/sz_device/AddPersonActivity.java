package com.sz_device;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.blankj.utilcode.util.BarUtils;

import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.rxbinding2.view.RxView;
import com.log.Lg;
import com.sz_device.Alerts.Alarm;
import com.sz_device.Bean.ReUploadBean;
import com.sz_device.EventBus.NetworkEvent;
import com.sz_device.EventBus.OpenDoorEvent;
import com.sz_device.Function.Fun_FingerPrint.mvp.presenter.FingerPrintPresenter;
import com.sz_device.Function.Fun_FingerPrint.mvp.view.IFingerPrintView;
import com.sz_device.Retrofit.RetrofitGenerator;
import com.sz_device.Tools.FileUtils;
import com.sz_device.Tools.MyObserver;
import com.sz_device.Tools.User;
import com.sz_device.greendao.DaoSession;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;


/**
 * Created by zbsz on 2017/7/31.
 */

//public class AddPersonActivity extends Activity implements IFingerPrintView {
//
//    SPUtils config = SPUtils.getInstance("config");
//
//    FingerPrintPresenter fpp = FingerPrintPresenter.getInstance();
//
//    boolean commitable;
//
//    DaoSession mdaoSession = AppInit.getInstance().getDaoSession();
//
//    User user;
//
//    String fp_id = "0";
//
//    @BindView(R.id.iv_finger)
//    ImageView img_finger;
//
//    @BindView(R.id.et_finger)
//    TextView tv_finger;
//
//    @BindView(R.id.btn_commit)
//    Button btn_commit;
//
//    @BindView(R.id.et_idcard)
//    EditText et_idcard;
//
//    @BindView(R.id.btn_query)
//    Button query;
//
//    @OnClick(R.id.btn_query)
//    void queryPerson() {
////        if (RegexUtils.isIDCard18(et_idcard.getText().toString())||RegexUtils.isIDCard15(et_idcard.getText().toString())||test(et_idcard.getText().toString().substring(0,1))) {
//        final ProgressDialog progressDialog = new ProgressDialog(AddPersonActivity.this);
//        RetrofitGenerator.getConnectApi().queryPersonInfo("queryPersonInfo", config.getString("key"), et_idcard.getText().toString().toUpperCase())
//                .subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<ResponseBody>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        progressDialog.setMessage("数据上传中，请稍候");
//                        progressDialog.show();
//                    }
//
//                    @Override
//                    public void onNext(ResponseBody responseBody) {
//                        try {
//                            Map<String, String> infoMap = new Gson().fromJson(responseBody.string(),
//                                    new TypeToken<HashMap<String, String>>() {
//                                    }.getType());
//                            if (infoMap.get("result").equals("true")) {
//                                if (infoMap.get("status").equals(String.valueOf(0))) {
//                                    fp_id = infoMap.get("data");
//                                    img_finger.setClickable(false);
//                                    fpp.fpEnroll(fp_id);
//                                    user = new User();
//                                    user.setCardId(et_idcard.getText().toString().toUpperCase());
//                                    user.setName(infoMap.get("name"));
//                                    user.setFingerprintId(fp_id);
//                                    user.setCourIds(infoMap.get("courIds"));
//                                    user.setCourType(infoMap.get("courType"));
//                                    query.setText(infoMap.get("name") + ",欢迎您！");
//                                    query.setClickable(false);
//                                } else {
//                                    new AlertView("您的身份有误，如有疑问请联系客服处理", null, null, new String[]{"确定"}, null, AddPersonActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
//                                        @Override
//                                        public void onItemClick(Object o, int position) {
//
//                                        }
//                                    }).show();
//                                }
//                            } else if (infoMap.get("result").equals("false")) {
//                                new AlertView("系统未能查询到该人员信息，如有疑问请联系客服处理", null, null, new String[]{"确定"}, null, AddPersonActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
//                                    @Override
//                                    public void onItemClick(Object o, int position) {
//
//                                    }
//                                }).show();
//                            } else {
//                                new AlertView(infoMap.get("result"), null, null, new String[]{"确定"}, null, AddPersonActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
//                                    @Override
//                                    public void onItemClick(Object o, int position) {
//
//                                    }
//                                }).show();
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        progressDialog.dismiss();
//                        new AlertView("无法连接服务器", null, null, new String[]{"确定"}, null, AddPersonActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
//                            @Override
//                            public void onItemClick(Object o, int position) {
//
//                            }
//                        }).show();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        progressDialog.dismiss();
//                    }
//                });
////        } else {
////            new AlertView("身份证输入有误，请重试", null, null, new String[]{"确定"}, null, AddPersonActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
////                @Override
////                public void onItemClick(Object o, int position) {
////
////                }
////            }).show();
////        }
//
//
//    }
//
//    @OnClick(R.id.btn_commit)
//    void commit() {
//        if (commitable) {
//            if (user.getFingerprintId() != null) {
//                final ProgressDialog progressDialog = new ProgressDialog(AddPersonActivity.this);
//                JSONObject jsonObject = new JSONObject();
//                try {
//                    jsonObject.put("id", user.getCardId());
//                    jsonObject.put("courIds", user.getCourIds());
//                    jsonObject.put("dataType", "1");
//                    jsonObject.put("name", user.getName());
//                    jsonObject.put("courType", user.getCourType());
//                    jsonObject.put("fingerprintPhoto", user.getFingerprintPhoto());
//                    jsonObject.put("fingerprintId", user.getFingerprintId());
//                    jsonObject.put("fingerprintKey", fpp.fpUpTemlate(user.getFingerprintId()));
//                    jsonObject.put("datetime", TimeUtils.getNowString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                RetrofitGenerator.getConnectApi().withDataRs("fingerLog", config.getString("key"), jsonObject.toString())
//                        .subscribeOn(Schedulers.io())
//                        .unsubscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Observer<String>() {
//                            @Override
//                            public void onSubscribe(@NonNull Disposable d) {
//                                progressDialog.setMessage("数据上传中，请稍候");
//                                progressDialog.show();
//                            }
//
//                            @Override
//                            public void onNext(String s) {
//                                if (s.equals("true")) {
//                                    SPUtils user_sp = SPUtils.getInstance(user.getFingerprintId());
//                                    user_sp.put("courIds", user.getCourIds());
//                                    user_sp.put("name", user.getName());
//                                    user_sp.put("cardId", user.getCardId());
//                                    user_sp.put("courType", user.getCourType());
//                                    ToastUtils.showLong("人员插入成功");
//                                    finish();
////                                    new AlertView("人员插入成功,请选择下一步操作", null, null, new String[]{"继续添加下一个人员信息", "返回主界面"}, null, AddPersonActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
////                                        @Override
////                                        public void onItemClick(Object o, int position) {
////                                            if (position == 0) {
////                                                commitable = false;
////                                                et_idcard.setText(null);
////                                                user = new User();
////                                                tv_finger.setText("先验证人员身份获得指纹编号");
////                                            } else if (position == 1) {
////                                                AddPersonActivity.this.finish();
////                                            }
////                                        }
////                                    }).show();
//                                } else {
//                                    new AlertView("数据插入有错", null, null, new String[]{"确定"}, null, AddPersonActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
//                                        @Override
//                                        public void onItemClick(Object o, int position) {
//
//                                        }
//                                    }).show();
//                                }
//
//
//                            }
//
//                            @Override
//                            public void onError(@NonNull Throwable e) {
//                                e.printStackTrace();
//                                progressDialog.dismiss();
//                                new AlertView("无法连接服务器", null, null, new String[]{"确定"}, null, AddPersonActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
//                                    @Override
//                                    public void onItemClick(Object o, int position) {
//
//                                    }
//                                }).show();
//                            }
//
//                            @Override
//                            public void onComplete() {
//                                progressDialog.dismiss();
//                            }
//                        });
//
//            } else {
//                new AlertView("您的操作有误，请重试", null, null, new String[]{"确定"}, null, AddPersonActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
//                    @Override
//                    public void onItemClick(Object o, int position) {
//
//                    }
//                }).show();
//            }
//        } else {
//            new AlertView("您还有信息未登记，如需退出请按取消", null, null, new String[]{"确定"}, null, AddPersonActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
//                @Override
//                public void onItemClick(Object o, int position) {
//
//                }
//            }).show();
//        }
//    }
//
//    @OnClick(R.id.btn_cancel)
//    void cancel() {
//        fpp.fpRemoveTmpl(String.valueOf(fp_id));
//        finish();
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        BarUtils.hideStatusBar(this);
//        setContentView(R.layout.activity_add_person);
//        ButterKnife.bind(this);
//        RxView.clicks(img_finger).throttleFirst(3, TimeUnit.SECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<Object>() {
//                    @Override
//                    public void accept(@NonNull Object o) throws Exception {
//                        fpp.fpEnroll(fp_id);
//                        img_finger.setClickable(false);
//                    }
//                });
//        img_finger.setClickable(false);
//    }
//
//    @Override
//    public void onSetImg(Bitmap bmp) {
//        img_finger.setImageBitmap(bmp);
//        user.setFingerprintPhoto(FileUtils.bitmapToBase64(bmp));
//    }
//
//    @Override
//    public void onText(String msg) {
//        if (!msg.equals("Canceled")) {
//            tv_finger.setText(msg);
//        }
//        if (msg.endsWith("录入成功")) {
//            commitable = true;
//        }
//        if (msg.endsWith("点我重试")) {
//            img_finger.setClickable(true);
//        }
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        fpp.FingerPrintPresenterSetView(this);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        fpp.fpCancel(true);
//        //fpp.FingerPrintPresenterSetView(null);
//    }
//
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onGetOpenDoorEvent(OpenDoorEvent event) {
//        final JSONObject OpenDoorjson = new JSONObject();
//        try {
//            OpenDoorjson.put("datetime", TimeUtils.getNowString());
//            OpenDoorjson.put("state", "n");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        RetrofitGenerator.getConnectApi().withDataRs("openDoorRecord", config.getString("key"), OpenDoorjson.toString())
//                .subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<String>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(String s) {
//
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        mdaoSession.insert(new ReUploadBean(null, "openDoorRecord", OpenDoorjson.toString()));
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//
//    }
//
//
//    @Override
//    public void onBackPressed() {
//
//    }
//
//    @Override
//    public void onFpSucc(String msg) {
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        commitable = false;
//
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//
//}


public class AddPersonActivity extends Activity implements IFingerPrintView {

    private String TAG = AddPersonActivity.class.getSimpleName();

    SPUtils config = SPUtils.getInstance("config");

    FingerPrintPresenter fpp = FingerPrintPresenter.getInstance();

    boolean commitable;

    DaoSession mdaoSession = AppInit.getInstance().getDaoSession();

    User user;

    String fp_id = "0";

    @BindView(R.id.iv_finger)
    ImageView img_finger;

    @BindView(R.id.et_finger)
    TextView tv_finger;

    @BindView(R.id.btn_commit)
    Button btn_commit;

    @BindView(R.id.et_idcard)
    EditText et_idcard;

    @BindView(R.id.btn_query)
    Button query;

    @OnClick(R.id.btn_query)
    void queryPerson() {
        RetrofitGenerator.getConnectApi().queryPersonInfo("queryPersonInfo", config.getString("key"), et_idcard.getText().toString().toUpperCase())
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
                                    fp_id = infoMap.get("data");
                                    img_finger.setClickable(false);
                                    fpp.fpEnroll(fp_id);
                                    user = new User();
                                    user.setCardId(et_idcard.getText().toString().toUpperCase());
                                    user.setName(infoMap.get("name"));
                                    user.setFingerprintId(fp_id);
                                    user.setCourIds(infoMap.get("courIds"));
                                    user.setCourType(infoMap.get("courType"));
                                    query.setText(infoMap.get("name") + ",欢迎您！");
                                    query.setClickable(false);
                                } else {
                                    Alarm.getInstance(AddPersonActivity.this).messageAlarm("您的身份有误，如有疑问请联系客服处理");
                                }
                            } else if (infoMap.get("result").equals("false")) {
                                Alarm.getInstance(AddPersonActivity.this).messageAlarm("系统未能查询到该人员信息，如有疑问请联系客服处理");
                            } else {
                                Alarm.getInstance(AddPersonActivity.this).messageAlarm(infoMap.get("result"));
                            }
                        } catch (IOException e) {
                            Lg.e(TAG,e.toString());
                        } catch (Exception e){
                            Lg.e(TAG,e.toString());
                        }
                    }
                });
    }

    @OnClick(R.id.btn_commit)
    void commit() {
        if (commitable) {
            if (user.getFingerprintId() != null) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", user.getCardId());
                    jsonObject.put("courIds", user.getCourIds());
                    jsonObject.put("dataType", "1");
                    jsonObject.put("name", user.getName());
                    jsonObject.put("courType", user.getCourType());
                    jsonObject.put("fingerprintPhoto", user.getFingerprintPhoto());
                    jsonObject.put("fingerprintId", user.getFingerprintId());
                    jsonObject.put("fingerprintKey", fpp.fpUpTemlate(user.getFingerprintId()));
                    jsonObject.put("datetime", TimeUtils.getNowString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RetrofitGenerator.getConnectApi().withDataRs("fingerLog", config.getString("key"), jsonObject.toString())
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new MyObserver<String>(this) {
                            @Override
                            public void onNext(String s) {
                                try{
                                    if (s.equals("true")) {
                                        SPUtils user_sp = SPUtils.getInstance(user.getFingerprintId());
                                        user_sp.put("courIds", user.getCourIds());
                                        user_sp.put("name", user.getName());
                                        user_sp.put("cardId", user.getCardId());
                                        user_sp.put("courType", user.getCourType());
                                        fp_id = "0";
                                        ToastUtils.showLong("人员插入成功");
                                        cancel();
                                    } else {
                                        Alarm.getInstance(AddPersonActivity.this).messageAlarm("数据插入有错");
                                    }
                                }catch (Exception e){
                                    Lg.e(TAG,e.toString());
                                }
                            }
                        });
            } else {
                Alarm.getInstance(AddPersonActivity.this).messageAlarm("您的操作有误，请重试");

            }
        } else {
            Alarm.getInstance(AddPersonActivity.this).messageAlarm("您还有信息未登记，如需退出请按取消");
        }
    }

    @OnClick(R.id.btn_cancel)
    void cancel() {
        new AlertView("请选择接下来的操作", null, null, new String[]{"重置并继续录入指纹", "退出至主桌面"}, null, AddPersonActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                if (position == 0) {
                    commitable = false;
                    query.setClickable(true);
                    query.setText("校验人员信息");
                    et_idcard.setHint("请填写身份证信息");
                    et_idcard.setText(null);
                    user = new User();
                    img_finger.setClickable(false);
                    fpp.fpCancel(true);
                    fpp.fpRemoveTmpl(fp_id);
                    tv_finger.setText("先验证人员身份获得指纹编号");
                    img_finger.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.zw_icon));
                } else {
                    fpp.fpCancel(true);
                    fpp.fpRemoveTmpl(fp_id);
                    finish();
                }
            }
        }).show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarUtils.hideStatusBar(this);
        setContentView(R.layout.activity_add_person);
        ButterKnife.bind(this);
        RxView.clicks(img_finger).throttleFirst(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        fpp.fpEnroll(fp_id);
                        img_finger.setClickable(false);
                    }
                });
        img_finger.setClickable(false);
    }

    @Override
    public void onSetImg(Bitmap bmp) {
        img_finger.setImageBitmap(bmp);
        user.setFingerprintPhoto(FileUtils.bitmapToBase64(bmp));
    }

    @Override
    public void onText(String msg) {
        if (!msg.equals("Canceled")) {
            tv_finger.setText(msg);
        }
        if (msg.endsWith("录入成功")) {
            commitable = true;
        }
        if (msg.endsWith("点我重试")) {
            img_finger.setClickable(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        commitable = false;
        fpp.FingerPrintPresenterSetView(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        fpp.fpCancel(true);
        fpp.FingerPrintPresenterSetView(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Alarm.getInstance(this).release();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetOpenDoorEvent(OpenDoorEvent event) {
        final JSONObject OpenDoorjson = new JSONObject();
        try {
            OpenDoorjson.put("datetime", TimeUtils.getNowString());
            OpenDoorjson.put("state", "n");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitGenerator.getConnectApi().withDataRs("openDoorRecord", config.getString("key"), OpenDoorjson.toString())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mdaoSession.insert(new ReUploadBean(null, "openDoorRecord", OpenDoorjson.toString()));

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void onFpSucc(String msg) {

    }

}
