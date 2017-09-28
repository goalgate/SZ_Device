package com.sz_device.State.OperationState;

import com.sz_device.Retrofit.Request.ResquestModule.IRequestModule;
import com.sz_device.Tools.UnUploadPackageDao;

/**
 * Created by zbsz on 2017/9/26.
 */

public abstract class OperationState {


    public abstract void onHandle(Operation op);

    public abstract void setMessage(UnUploadPackageDao unUploadPackageDao, IRequestModule module, Boolean network_state);


}
