package com.sz_device.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sz_device.AppInit;
import com.sz_device.IndexActivity;
import com.sz_device.Service.SwitchService;

/**
 * Created by zbsz on 2017/7/28.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {// boot;
            Intent intent2 = new Intent(context, IndexActivity.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);
        }
    }


}
