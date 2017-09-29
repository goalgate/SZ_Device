package com.sz_device.State.OperationState;

import com.sz_device.Retrofit.Request.RequestEnvelope;
import com.sz_device.Retrofit.Request.ResquestModule.IRequestModule;
import com.sz_device.Retrofit.RetrofitGenerator;
import com.sz_device.Tools.MyObserver;
import com.sz_device.Tools.SaveObserver;
import com.sz_device.Tools.UnUploadPackage;
import com.sz_device.Tools.UnUploadPackageDao;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zbsz on 2017/9/26.
 */

public class Check_OperateState extends OperationState {

    IRequestModule checkModule;

    UnUploadPackageDao unUploadPackageDao;

    Boolean network_state;

    @Override
    public void setMessage(UnUploadPackageDao unUploadPackageDao, IRequestModule module, Boolean network_state){
        this.unUploadPackageDao = unUploadPackageDao;
        this.checkModule = module;
        this.network_state = network_state;
    }

    @Override
    public void onHandle(Operation op) {
        if (network_state) {
            RetrofitGenerator.getCommonApi().commonFunction(RequestEnvelope.GetRequestEnvelope(checkModule))
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SaveObserver(unUploadPackageDao,checkModule));
        } else {

            UnUploadPackage un = new UnUploadPackage();
            un.setMethod(checkModule.getMethod());
            un.setJsonData(checkModule.getJSON());
            un.setUpload(false);
            unUploadPackageDao.insert(un);
        }


        op.setState(new No_one_OperateState());
    }
}
