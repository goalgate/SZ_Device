package com.sz_device;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.rxbinding2.view.RxView;
import com.sz_device.EventBus.NetworkEvent;
import com.sz_device.EventBus.OpenDoorEvent;
import com.sz_device.Fun_FingerPrint.mvp.presenter.FingerPrintPresenter;
import com.sz_device.Fun_FingerPrint.mvp.view.IFingerPrintView;
import com.sz_device.Retrofit.Request.RequestEnvelope;
import com.sz_device.Retrofit.Request.ResquestModule.CommonRequestModule;
import com.sz_device.Retrofit.Request.ResquestModule.OnlyPutKeyModule;
import com.sz_device.Retrofit.Response.ResponseEnvelope;
import com.sz_device.Retrofit.RetrofitGenerator;
import com.sz_device.Tools.FileUtils;
import com.sz_device.Tools.MyObserver;
import com.sz_device.Tools.UnUploadPackage;
import com.sz_device.Tools.UnUploadPackageDao;
import com.sz_device.Tools.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
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

import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.getFingerPrint;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.openDoorRecord;
import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.registerPerson;

/**
 * Created by zbsz on 2017/7/31.
 */

public class AddPersonActivity extends Activity implements IFingerPrintView {
    private static final String PREFS_NAME = "UserInfo";

    Uri photoUri = null;

    User registerUser = new User();

    SPUtils user;

    boolean network_state = false;

    String TAG = "AddPersonActivity";

    UnUploadPackageDao unUploadPackageDao;

    FingerPrintPresenter fpp = FingerPrintPresenter.getInstance();

    @BindView(R.id.iv_finger)
    ImageView img_finger;

    @BindView(R.id.iv_camera_view)
    ImageView img_camera;

    @BindView(R.id.et_finger)
    TextView tv_finger;

    @BindView(R.id.btn_commit)
    Button btn_commit;

    @BindView(R.id.et_person_name)
    TextView tv_person_name;

    @BindView(R.id.et_id_card)
    TextView tv_id_card;

    @OnClick(R.id.iv_camera_view)
    void capture() {
        ToastUtils.showLong("正在打开照相机，请稍等");
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoUri = FileUtils.getOutputMediaFileUri(FileUtils.MEDIA_TYPE_IMAGE);
        photoIntent.putExtra(MediaStore.EXTRA_FULL_SCREEN, true);
        photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        photoIntent.putExtra("camerasensortype", 1);// 调用前置摄像头
        photoIntent.putExtra("autofocus", true); // 自动对焦
        this.startActivityForResult(photoIntent, 100);

    }

    @OnClick(R.id.btn_commit)
    void commit() {
        if (StringUtils.isEmpty(tv_person_name.getText().toString()) || (StringUtils.isEmpty(tv_id_card.getText().toString()))
                || photoUri == null) {
            ToastUtils.showLong("信息不全，无法上传数据");
        } else {
            if (network_state) {
                user = SPUtils.getInstance(registerUser.getFingerprintId());
                user.put("name", tv_person_name.getText().toString());
                user.put("id", tv_id_card.getText().toString());
                user.put("fp_id", registerUser.getFingerprintId());
                RegisterPerson();
            } else {
                ToastUtils.showLong("无法连接服务器，请稍后重试");
                fpp.fpRemoveTmpl(registerUser.getFingerprintId());
            }

        }
    }

    @OnClick(R.id.btn_cancel)
    void cancel() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarUtils.hideStatusBar(this);
        setContentView(R.layout.activity_add_person);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        btn_commit.setClickable(false);
        RxView.clicks(img_finger).throttleFirst(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        if (!btn_commit.isClickable()) {
                            OnlyPutKeyModule getFingerPrintM = new OnlyPutKeyModule(getFingerPrint,SPUtils.getInstance(PREFS_NAME).getString("jsonKey"));
                            RetrofitGenerator.getCommonApi().commonFunction(RequestEnvelope.GetRequestEnvelope(getFingerPrintM))
                                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<ResponseEnvelope>() {
                                        @Override
                                        public void onSubscribe(@NonNull Disposable d) {

                                        }

                                        @Override
                                        public void onNext(@NonNull ResponseEnvelope responseEnvelope) {
                                            Map<String, String> infoMap = new Gson().fromJson(responseEnvelope.body.getFingerprintIdResponse.info,
                                                    new TypeToken<HashMap<String, String>>() {
                                                    }.getType());
                                            if (infoMap.get("result").equals("true")) {
                                                img_finger.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.zw_icon));
                                                fpp.fpCancel(true);
                                                registerUser.setFingerprintId(infoMap.get("fingerprintId"));
                                                fpp.fpEnroll(registerUser.getFingerprintId());
                                            } else if (infoMap.get("result").equals("false")) {

                                            }
                                        }

                                        @Override
                                        public void onError(@NonNull Throwable e) {
                                            ToastUtils.showLong("服务器出错");
                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });
                        }
                    }
                });
    }

    private void RegisterPerson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", tv_id_card.getText().toString());
            jsonObject.put("name", tv_person_name.getText().toString());
            jsonObject.put("photo", registerUser.getPhoto());
            jsonObject.put("fingerprintPhoto", registerUser.getFingerprintPhoto());
            jsonObject.put("fingerprintId", registerUser.getFingerprintId());
            jsonObject.put("fingerprintKey", fpp.fpUpTemlate(registerUser.getFingerprintId()));
            jsonObject.put("datetime", TimeUtils.getNowString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CommonRequestModule registerPersonM = new CommonRequestModule(
                registerPerson, SPUtils.getInstance(PREFS_NAME).getString("jsonKey"), jsonObject.toString()
        );
        RetrofitGenerator.getCommonApi().commonFunction(RequestEnvelope.GetRequestEnvelope(registerPersonM)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseEnvelope>() {
                    @Override
                    public void accept(@NonNull ResponseEnvelope responseEnvelope) throws Exception {
                        Map<String, String> infoMap = new Gson().fromJson(responseEnvelope.body.registerPersonResponse.info,
                                new TypeToken<HashMap<String, String>>() {
                                }.getType());
                        if (infoMap.get("result").equals("true")) {
                            ToastUtils.showLong("上传成功");
                            finish();
                        } else if (infoMap.get("result").equals("false")) {
                            ToastUtils.showLong("上传失败");
                        } else if (infoMap.get("result").equals("checkErr")) {
                            ToastUtils.showLong("上传失败");
                        }
                    }
                });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetNetworkEvent(NetworkEvent event) {
        network_state = event.getNetwork_state();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetOpenDoorEvent(OpenDoorEvent event) {
        OpenDoorRecord(event.getLegal());

    }

    private void OpenDoorRecord(boolean leagl) {
        JSONObject openDoorRecordJson = new JSONObject();
        try {
            openDoorRecordJson.put("datetime", TimeUtils.getNowString());
            openDoorRecordJson.put("state", "n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(network_state){
            CommonRequestModule openDoorRecordM = new CommonRequestModule( openDoorRecord,SPUtils.getInstance(PREFS_NAME).getString("jsonKey"), openDoorRecordJson.toString());
            RetrofitGenerator.getCommonApi().commonFunction(RequestEnvelope.GetRequestEnvelope(openDoorRecordM))
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyObserver(unUploadPackageDao,openDoorRecordM));
        }else{
            UnUploadPackage un = new UnUploadPackage();
            un.setMethod(openDoorRecord);
            un.setJsonData(openDoorRecordJson.toString());
            un.setUpload(false);
            unUploadPackageDao.insert(un);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            photoUri = null;
            ToastUtils.showLong("没有照片，请重新拍照");
        } else {
            if (requestCode == 100) {
                try {
                    // 图片解析成Bitmap对象
                    Bitmap bitmap = BitmapFactory
                            .decodeStream(this.getContentResolver().openInputStream(photoUri));
                    Matrix matrix = new Matrix();
                    matrix.postRotate(180);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                            matrix, true);
                    img_camera.setImageBitmap(bitmap); // 将剪裁后照片显示出来
                    registerUser.setPhoto(FileUtils.bitmapToBase64(bitmap));
                } catch (FileNotFoundException e) {
                    Log.e("SZ_Device信息提示", "文件不存在" + e.toString());
                }
            }
        }
    }

    @Override
    public void onSetImg(Bitmap bmp) {
        img_finger.setImageBitmap(bmp);
        registerUser.setFingerprintPhoto(FileUtils.bitmapToBase64(bmp));
    }

    @Override
    public void onText(String msg) {
        if (!msg.equals("Canceled")) {
            tv_finger.setText(msg);
        }
        if (msg.endsWith("录入成功")) {
            btn_commit.setClickable(true);
        }
        if (msg.equals("指纹模板已存在")) {
            fpp.fpRemoveTmpl(registerUser.getFingerprintId());
            fpp.fpEnroll(registerUser.getFingerprintId());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        fpp.FingerPrintPresenterSetView(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        fpp.fpCancel(true);
        fpp.FingerPrintPresenterSetView(null);
        Log.e(TAG, "onPause");
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}


