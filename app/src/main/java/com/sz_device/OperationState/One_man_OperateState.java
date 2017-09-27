package com.sz_device.OperationState;

import com.sz_device.Retrofit.Request.ResquestModule.IRequestModule;
import com.sz_device.Tools.UnUploadPackageDao;

/**
 * Created by zbsz on 2017/9/26.
 */

public class One_man_OperateState extends OperationState {
    IRequestModule checkModule;

    UnUploadPackageDao unUploadPackageDao;

    Boolean network_state;


    @Override
    public void onHandle(Operation op) {
        op.setState(new Two_man_OperateState());
    }

    @Override
    public void setMessage(UnUploadPackageDao unUploadPackageDao, IRequestModule module, Boolean network_state) {
        this.unUploadPackageDao = unUploadPackageDao;
        this.checkModule = module;
        this.network_state = network_state;
    }
}
