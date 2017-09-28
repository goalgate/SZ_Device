package com.sz_device.Tools;


import android.text.TextUtils;

import com.sz_device.AppInit;

import com.sz_device.Retrofit.Request.ResquestModule.CommonRequestModule;
import com.sz_device.Retrofit.Request.ResquestModule.IRequestModule;
import com.sz_device.Retrofit.Response.ResponseEnvelope;

import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

import static com.sz_device.Retrofit.InterfaceApi.InterfaceCode.alarmCease;

/**
 * Created by zbsz on 2017/9/19.
 */

public class MyObserver implements Observer<ResponseEnvelope> {

    private UnUploadPackageDao unUploadPackageDao;

    private IRequestModule Module;

    public MyObserver() {

    }

    public MyObserver(UnUploadPackageDao unUploadPackageDao ,IRequestModule module) {
        this.unUploadPackageDao = unUploadPackageDao;
        this.Module = module;
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(@NonNull Throwable e) {
        if (Module!=null){
            UnUploadPackage un = new UnUploadPackage();
            un.setMethod(Module.getMethod());
            un.setJsonData(Module.getJSON());
            un.setUpload(false);
            unUploadPackageDao.insert(un);
        }
    }

    @Override
    public void onNext(@NonNull ResponseEnvelope responseEnvelope) {

    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }
}
