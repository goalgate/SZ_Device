package com.sz_device.State.DoorState;

import com.sz_device.EventBus.OpenDoorEvent;
import com.sz_device.State.LockState.Lock;
import com.sz_device.State.LockState.State_Lockup;
import com.sz_device.State.LockState.State_Unlock;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by zbsz on 2017/9/27.
 */

public class State_Open extends DoorState {
//
//    Lock lock;
    doorStateCallback callback;
//    public State_Open(Lock lock) {
//        this.lock = lock;
//    }

    public State_Open(doorStateCallback callback){
        this.callback = callback;
    }

    @Override
    public void onHandle(Door door) {
        callback.onback();
//        if (lock.getLockState().getClass().getName().equals(State_Lockup.class.getName())) {
//            EventBus.getDefault().post(new OpenDoorEvent(false));
//            lock.doNext();
//        } else if (lock.getLockState().getClass().getName().equals(State_Unlock.class.getName())) {
//            EventBus.getDefault().post(new OpenDoorEvent(true));
//        }
    }
}
