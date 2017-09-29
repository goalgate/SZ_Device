package com.sz_device.Function.Fun_Camera.mvp.module;

import android.graphics.Bitmap;
import android.view.SurfaceHolder;

/**
 * Created by zbsz on 2017/5/19.
 */

public interface IPhotoModule {

    void initCamera();

    void setHolderAndDisplay(SurfaceHolder surfaceHolder, boolean display);

    void setDisplay(SurfaceHolder surfaceHolder);

    void capture(IOnSetListener iOnSetListener);//拍照按钮点击事件

    interface IOnSetListener {
        void onBtnText(String msg);//按完按钮后的回调接口

        void onGetPhoto(Bitmap bmp);
    }
}