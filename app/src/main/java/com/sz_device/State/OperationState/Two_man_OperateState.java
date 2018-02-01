package com.sz_device.State.OperationState;


/**
 * Created by zbsz on 2017/9/26.
 */

public class Two_man_OperateState extends OperationState {

    @Override
    public void onHandle(Operation op,Operation.Callback_Operation callback) {
        /*op.setState(new No_one_OperateState());*/

        callback.uploadCallback();



    }

}
