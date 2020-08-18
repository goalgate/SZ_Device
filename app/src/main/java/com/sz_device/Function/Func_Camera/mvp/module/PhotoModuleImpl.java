package com.sz_device.Function.Func_Camera.mvp.module;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.blankj.utilcode.util.ScreenUtils;
import com.sz_device.AppInit;


import java.io.IOException;

/**
 * Created by zbsz on 2017/5/19.
 */

public class PhotoModuleImpl implements IPhotoModule {
    final static String ApplicationName = "PhotoModule_";
    static Camera camera;
    Bitmap bm;
    IOnSetListener callback;

    @Override
    public void setDisplay(SurfaceHolder sHolder) {
        try {
            if (camera != null) {
                camera.setPreviewDisplay(sHolder);
                camera.startPreview();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setParameter(SurfaceHolder sHolder) {
        sHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {

            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (camera != null) {
                    Camera.Parameters parameters = camera.getParameters();
                    // 设置预览照片时每秒显示多少帧的最小值和最大值

                    parameters.setPreviewSize(ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight());

                    parameters.setPreviewFpsRange(45, 50);
                    // 设置图片格式
                    parameters.setPictureFormat(ImageFormat.JPEG);
                    // 设置JPG照片的质量
                    parameters.set("jpeg-quality", 85);
                    // 通过SurfaceView显示取景画面
                    setDisplay(holder);

                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // 如果camera不为null ,释放摄像头
                if (camera != null) {
                    /*        if (isPreview) */
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                    Log.e(ApplicationName, "摄像头被释放");
                    /*                    isPreview = false;*/
                }
            }

        });
    }

    @Override
    public void capture(IOnSetListener listener) {
        this.callback = listener;
        /*        hasDetected = false;*/
        /*      if (isPreview) {*/
        camera.takePicture(new Camera.ShutterCallback() {
            public void onShutter() {
                // 按下快门瞬间会执行此处代码
            }
        }, new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera c) {
                // 此处代码可以决定是否需要保存原始照片信息
            }
        }, myJpegCallback);
        /*      }*/
    }

    Camera.PictureCallback myJpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, final Camera camera) {
            camera.stopPreview();
            bm = BitmapFactory.decodeByteArray(data, 0, data.length);
            callback.onBtnText("拍照成功");
            callback.onGetPhoto(bm);
        }
    };

    @Override
    public void initCamera() {
//        if (AppInit.getMyManager().getAndroidDisplay().startsWith("rk3288")) {
//            safeCameraOpen(1);
//        } else {
//            safeCameraOpen(0);
//        }
        safeCameraOpen(0);

    }

    private void safeCameraOpen(int id) {
        try {
            if (AppInit.getMyManager().getAndroidDisplay().startsWith("rk3288")) {
                releaseCameraAndPreview();
                camera = Camera.open(1);
            } else {
                releaseCameraAndPreview();
                camera = Camera.open();
            }

        } catch (Exception e) {
            Toast.makeText(AppInit.getContext(), "无法获取摄像头权限", Toast.LENGTH_LONG);
            e.printStackTrace();
        }

    }

    private void releaseCameraAndPreview() {
        if (camera != null) {
            camera.release();
            camera = null;
        }

    }

    @Override
    public void closeCamera() {
        releaseCameraAndPreview();
    }

    @Override
    public void getOneShut(IOnSetListener iOnSetListener) {

    }
}

