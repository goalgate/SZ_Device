package com.sz_device.EventBus;

/**
 * Created by zbsz on 2017/9/12.
 */

public class OpenDoorEvent {
    boolean legal;

    public boolean getLegal(){
        return  legal;
    }

    public OpenDoorEvent(boolean legal){
        this.legal = legal;
    }
}
