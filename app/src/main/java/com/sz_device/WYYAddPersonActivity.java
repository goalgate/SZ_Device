package com.sz_device;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.drv.card.ICardInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.rxbinding2.view.RxView;
import com.sz_device.Alerts.Alarm;
import com.sz_device.Bean.ReUploadBean;
import com.sz_device.EventBus.OpenDoorEvent;
import com.sz_device.Function.Fun_FingerPrint.mvp.presenter.FingerPrintPresenter;
import com.sz_device.Function.Fun_FingerPrint.mvp.view.IFingerPrintView;
import com.sz_device.Function.Func_ICCard.mvp.presenter.IDCardPresenter;
import com.sz_device.Function.Func_ICCard.mvp.view.IIDCardView;
import com.sz_device.Retrofit.RetrofitGenerator;
import com.sz_device.Tools.FileUtils;
import com.sz_device.Tools.MyObserver;
import com.sz_device.Tools.User;
import com.sz_device.greendao.DaoSession;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

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

//public class WYYAddPersonActivity extends Activity implements IFingerPrintView {
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
//        RetrofitGenerator.getWyyConnectApi().queryPersonInfo("queryPersonInfo", config.getString("key"), et_idcard.getText().toString().toUpperCase())
//                .subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new MyObserver<ResponseBody>(this) {
//                    @Override
//                    public void onNext(ResponseBody responseBody) {
//                        try {
//                            Map<String, String> infoMap = new Gson().fromJson(responseBody.string(),
//                                    new TypeToken<HashMap<String, String>>() {
//                                    }.getType());
//                            if (infoMap.size() > 0) {
//                                if (infoMap.get("status").equals(String.valueOf(0))) {
//                                    img_finger.setClickable(false);
//                                    fp_id = String.valueOf(fpp.fpGetEmptyID());
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
//                                    new AlertView("您的身份有误，如有疑问请联系客服处理", null, null, new String[]{"确定"}, null, WYYAddPersonActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
//                                        @Override
//                                        public void onItemClick(Object o, int position) {
//
//                                        }
//                                    }).show();
//                                }
//                            } else {
//                                new AlertView("系统未能查询到该人员信息，如有疑问请联系客服处理", null, null, new String[]{"确定"}, null, WYYAddPersonActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
//                                    @Override
//                                    public void onItemClick(Object o, int position) {
//
//                                    }
//                                }).show();
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        } catch (NullPointerException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//    }
//
//    @OnClick(R.id.btn_commit)
//    void commit() {
//        if (commitable) {
//            if (user.getFingerprintId() != null) {
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
//                RetrofitGenerator.getWyyConnectApi().withDataRs("fingerLog", config.getString("key"), jsonObject.toString())
//                        .subscribeOn(Schedulers.io())
//                        .unsubscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new MyObserver<String>(this) {
//                            @Override
//                            public void onNext(String s) {
//                                if (s.equals("true")) {
//                                    SPUtils user_sp = SPUtils.getInstance(user.getFingerprintId());
//                                    user_sp.put("courIds", user.getCourIds());
//                                    user_sp.put("name", user.getName());
//                                    user_sp.put("cardId", user.getCardId());
//                                    user_sp.put("courType", user.getCourType());
//                                    ToastUtils.showLong("人员插入成功");
//                                    cancel();
//                                } else {
//                                    new AlertView("数据插入有错", null, null, new String[]{"确定"}, null, WYYAddPersonActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
//                                        @Override
//                                        public void onItemClick(Object o, int position) {
//
//                                        }
//                                    }).show();
//                                }
//                            }
//
//                        });
//
//            } else {
//                new AlertView("您的操作有误，请重试", null, null, new String[]{"确定"}, null, WYYAddPersonActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
//                    @Override
//                    public void onItemClick(Object o, int position) {
//
//                    }
//                }).show();
//            }
//        } else {
//            new AlertView("您还有信息未登记，如需退出请按取消", null, null, new String[]{"确定"}, null, WYYAddPersonActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
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
//        new AlertView("请选择接下来的操作", null, null, new String[]{"重置", "退出"}, null, WYYAddPersonActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
//            @Override
//            public void onItemClick(Object o, int position) {
//                if (position == 0) {
//                    fp_id = "0";
//                    commitable = false;
//                    query.setClickable(true);
//                    query.setText("校验人员信息");
//                    et_idcard.setHint("请填写身份证信息");
//                    user = new User();
//                    img_finger.setClickable(false);
//                    tv_finger.setText("先验证人员身份获得指纹编号");
//                    img_finger.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.zw_icon));
//                } else {
//                    finish();
//                }
//            }
//        }).show();
//
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
//    protected void onResume() {
//        super.onResume();
//        commitable = false;
//        fpp.FingerPrintPresenterSetView(this);
//
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        fpp.fpCancel(true);
//        fpp.FingerPrintPresenterSetView(null);
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
//        RetrofitGenerator.getWyyConnectApi().withDataRs("openDoorRecord", config.getString("key"), OpenDoorjson.toString())
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
//    @Override
//    public void onFpSucc(String msg) {
//
//    }
//
//}

public class WYYAddPersonActivity extends Activity implements IFingerPrintView, IIDCardView {

    SPUtils config = SPUtils.getInstance("config");

    FingerPrintPresenter fpp = FingerPrintPresenter.getInstance();

    boolean commitable;

    DaoSession mdaoSession = AppInit.getInstance().getDaoSession();

    User user;

    String fp_id = "0";

    @BindView(R.id.iv_finger)
    ImageView img_finger;

    @BindView(R.id.tv_finger)
    TextView tv_finger;

    @BindView(R.id.btn_commit)
    Button btn_commit;

    @BindView(R.id.tv_person_name)
    TextView tv_person_name;

    @BindView(R.id.tv_id_card)
    TextView tv_id_card;

    @BindView(R.id.iv_head_photo)
    ImageView iv_head_photo;

    @OnClick(R.id.btn_commit)
    void commit() {
        if (commitable) {
            if (user.getFingerprintId() != null) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", user.getCardId());
                    jsonObject.put("courIds", user.getCourIds());
                    jsonObject.put("name", user.getName());
                    jsonObject.put("courType", user.getCourType());
                    jsonObject.put("fingerprintPhoto", user.getFingerprintPhoto());
                    jsonObject.put("fingerprintId", user.getFingerprintId());
                    jsonObject.put("fingerprintKey", fpp.fpUpTemlate(user.getFingerprintId()));
                    jsonObject.put("datetime", TimeUtils.getNowString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RetrofitGenerator.getWyyConnectApi().withDataRs("fingerLog", config.getString("key"), jsonObject.toString())
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new MyObserver<String>(this) {
                            @Override
                            public void onNext(String s) {
                                if (s.equals("true")) {
                                    SPUtils user_sp = SPUtils.getInstance(user.getFingerprintId());
                                    user_sp.put("courIds", user.getCourIds());
                                    user_sp.put("name", user.getName());
                                    user_sp.put("cardId", user.getCardId());
                                    user_sp.put("courType", user.getCourType());
                                    SPUtils user_id = SPUtils.getInstance(user.getCardId());
                                    user_id.put("courIds", user.getCourIds());
                                    user_id.put("name", user.getName());
                                    user_id.put("fingerprintId", user.getFingerprintId());
                                    user_id.put("courType", user.getCourType());
                                    fp_id = "0";
                                    ToastUtils.showLong("人员插入成功");
                                    cancel();
                                } else {
                                    Alarm.getInstance(WYYAddPersonActivity.this).messageAlarm("数据插入有错");
                                }
                            }
                        });
            } else {
                Alarm.getInstance(WYYAddPersonActivity.this).messageAlarm("您的操作有误，请重试");
            }
        } else {
            Alarm.getInstance(WYYAddPersonActivity.this).messageAlarm("您还有信息未登记，如需退出请按取消");
        }
    }

    @OnClick(R.id.btn_cancel)
    void cancel() {
        new AlertView("请选择接下来的操作", null, null, new String[]{"重置", "退出"}, null, WYYAddPersonActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                if (position == 0) {
                    commitable = false;
                    user = new User();
                    AppInit.getInstrumentConfig().readCard();
                    tv_id_card.setHint(R.string.cardHint);
                    tv_id_card.setText(null);
                    tv_person_name.setHint(R.string.nameHint);
                    tv_person_name.setText(null);
                    iv_head_photo.setImageBitmap(null);
                    img_finger.setClickable(false);
                    fpp.fpCancel(true);
                    fpp.fpRemoveTmpl(fp_id);
                    tv_finger.setText("请刷身份证以获得指纹编号");
                    img_finger.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.zw_icon));
                    //fpp.fpRemoveAll();
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
        setContentView(R.layout.activty_new_add_person);
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
    protected void onResume() {
        super.onResume();
        commitable = false;
        AppInit.getInstrumentConfig().readCard();
        fpp.FingerPrintPresenterSetView(this);
        IDCardPresenter.getInstance().IDCardPresenterSetView(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppInit.getInstrumentConfig().stopReadCard();
        fpp.fpCancel(true);
        fpp.FingerPrintPresenterSetView(null);
        IDCardPresenter.getInstance().IDCardPresenterSetView(null);
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
        RetrofitGenerator.getWyyConnectApi().withDataRs("openDoorRecord", config.getString("key"), OpenDoorjson.toString())
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
    public void onFpSucc(String msg) {

    }

    @Override
    public void onsetCardImg(Bitmap bmp) {
        iv_head_photo.setImageBitmap(bmp);
    }

    @Override
    public void onsetCardInfo(ICardInfo cardInfo) {
        queryPerson(cardInfo);
        tv_person_name.setText(cardInfo.name());
        tv_id_card.setText(cardInfo.cardId());
    }

    void queryPerson(final ICardInfo cardInfo) {
        RetrofitGenerator.getWyyConnectApi().queryPersonInfo("queryPersonInfo", config.getString("key"), cardInfo.cardId())
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
                            if (infoMap.size() > 0) {
                                if (infoMap.get("status").equals(String.valueOf(0))) {
                                    img_finger.setClickable(false);
                                    fp_id = String.valueOf(fpp.fpGetEmptyID());
                                    fpp.fpEnroll(fp_id);
                                    user = new User();
                                    user.setCardId(cardInfo.cardId());
                                    user.setName(infoMap.get("name"));
                                    user.setFingerprintId(fp_id);
                                    user.setCourIds(infoMap.get("courIds"));
                                    user.setCourType(infoMap.get("courType"));
                                    AppInit.getInstrumentConfig().stopReadCard();
                                } else {
                                    Alarm.getInstance(WYYAddPersonActivity.this).messageAlarm("您的身份有误，如有疑问请联系客服处理");
                                }
                            } else {
                                Alarm.getInstance(WYYAddPersonActivity.this).messageAlarm("系统未能查询到该人员信息，如有疑问请联系客服处理");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
