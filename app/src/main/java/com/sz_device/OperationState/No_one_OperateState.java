package com.sz_device.OperationState;

import com.sz_device.Retrofit.Request.ResquestModule.IRequestModule;
import com.sz_device.Tools.UnUploadPackageDao;

import io.reactivex.Observable;

/**
 * Created by zbsz on 2017/9/26.
 */

public class No_one_OperateState extends OperationState {


    @Override
    public void onHandle(Operation op) {

    }

    @Override
    public void setMessage(UnUploadPackageDao unUploadPackageDao, IRequestModule module, Boolean network_state) {

    }
}
