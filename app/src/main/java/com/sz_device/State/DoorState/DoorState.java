package com.sz_device.State.DoorState;

/**
 * Created by zbsz on 2017/9/27.
 */

public abstract class DoorState {

    public interface doorStateCallback{
        void onback();
    }
    public abstract void onHandle(Door door);


}
