package com.sz_device.Tools;

import com.sz_device.Retrofit.Request.ResquestModule.IRequestModule;
import com.sz_device.Retrofit.Response.ResponseEnvelope;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by zbsz on 2017/9/29.
 */

public class SaveObserver implements Observer<ResponseEnvelope> {

    private UnUploadPackageDao unUploadPackageDao;

    private IRequestModule Module;

    public SaveObserver() {

    }

    public SaveObserver(UnUploadPackageDao unUploadPackageDao ,IRequestModule module) {
        this.unUploadPackageDao = unUploadPackageDao;
        this.Module = module;
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(@NonNull Throwable e) {
        UnUploadPackage un = new UnUploadPackage();
        un.setMethod(Module.getMethod());
        un.setJsonData(Module.getJSON());
        un.setUpload(false);
        unUploadPackageDao.insert(un);

    }

    @Override
    public void onNext(@NonNull ResponseEnvelope responseEnvelope) {

    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }
}
