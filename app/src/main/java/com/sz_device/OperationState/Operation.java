package com.sz_device.OperationState;

import android.graphics.Path;

import com.squareup.haha.perflib.ThreadObj;
import com.sz_device.Retrofit.Request.ResquestModule.IRequestModule;
import com.sz_device.Tools.UnUploadPackageDao;

/**
 * Created by zbsz on 2017/9/26.
 */

public class Operation {

    private OperationState state;

    public Operation(OperationState state){
        this.state = state;
    }

    public OperationState getState() {
        return state;
    }

    public void setState(OperationState state) {
        this.state = state;
    }


    public void setMessage(UnUploadPackageDao unUploadPackageDao, IRequestModule module, Boolean network_state){
        state.setMessage(unUploadPackageDao,module, network_state);
    }


    public void doNext(){
        state.onHandle(this);
    }
}
