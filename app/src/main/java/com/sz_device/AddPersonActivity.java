package com.sz_device;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;

import com.blankj.utilcode.util.ToastUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.sz_device.Fun_FingerPrint.mvp.presenter.FingerPrintPresenter;
import com.sz_device.Fun_FingerPrint.mvp.view.IFingerPrintView;
import com.sz_device.Tools.AppActivitys;
import com.sz_device.Tools.FileUtils;

import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by zbsz on 2017/7/31.
 */

public class AddPersonActivity extends Activity implements IFingerPrintView {

    Uri photoUri = null;

    String TAG = "AddPersonActivity";

    FingerPrintPresenter fpp = FingerPrintPresenter.getInstance();

    @BindView(R.id.iv_finger)
    ImageView img_finger;


    @BindView(R.id.iv_camera_view)
    ImageView img_camera;


    @OnClick(R.id.iv_camera_view) void capture(){
        ToastUtils.showLong("正在打开照相机，请稍等");
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoUri = FileUtils.getOutputMediaFileUri(FileUtils.MEDIA_TYPE_IMAGE);
        photoIntent.putExtra(MediaStore.EXTRA_FULL_SCREEN, true);
        photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        photoIntent.putExtra("camerasensortype", 1); // 调用前置摄像头
        photoIntent.putExtra("autofocus", true); // 自动对焦
        this.startActivityForResult(photoIntent, 100);
    }

    @OnClick(R.id.btn_commit) void commit(){
        ActivityUtils.startActivity(getPackageName(),getPackageName()+".IndexActivity");

    }

    @OnClick(R.id.btn_cancel) void cancel(){
        ActivityUtils.startActivity(getPackageName(),getPackageName()+".IndexActivity");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarUtils.hideStatusBar(this);
        setContentView(R.layout.activity_add_person);
        ButterKnife.bind(this);
        AppActivitys.getInstance().addActivity(this);
        RxView.clicks(img_finger).throttleFirst(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                               @Override
                               public void accept(@NonNull Object o) throws Exception {
                                   img_finger.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.zw_icon));
                                   fpp.fpCancel(true);
                                   fpp.fpEnroll("1");
                               }
                });
        //网络获取id

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fpp.fpEnroll("1");
        if (resultCode != Activity.RESULT_OK) {
            photoUri = null;
            ToastUtils.showLong("没有照片，请重新拍照");
        } else {
            if (requestCode == 100) {
                try {
                    // 图片解析成Bitmap对象
                    Bitmap bitmap = BitmapFactory
                            .decodeStream(this.getContentResolver().openInputStream(photoUri));
                    img_camera.setImageBitmap(bitmap); // 将剪裁后照片显示出来
                } catch (FileNotFoundException e) {
                    Log.e("SZ_Device信息提示", "文件不存在" + e.toString());
                }
            }
        }
    }

    @Override
    public void onSetImg(Bitmap bmp) {
        img_finger.setImageBitmap(bmp);
    }

    @Override
    public void onText(String msg) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        fpp.FingerPrintPresenterSetView(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onPause() {
        super.onPause();
        fpp.fpCancel(true);
        Log.e(TAG,"onPause");
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppActivitys.getInstance().exit();
    }
}


