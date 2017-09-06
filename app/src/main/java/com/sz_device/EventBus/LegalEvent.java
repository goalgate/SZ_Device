package com.sz_device.EventBus;

/**
 * Created by zbsz on 2017/8/29.
 */

public class LegalEvent {
    boolean legal;

    public boolean getLegal(){
        return  legal;
    }

    public LegalEvent(boolean legal){
        this.legal = legal;
    }
}
