package com.sz_device.Tools;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.sz_device.AppInit;
import com.ys.myapi.MyManager;


public class WZWManager {

    private static WZWManager wzwManager;

    com.ys.myapi.MyManager manager1;

    com.ys.rkapi.MyManager manager2;

    public static final String STATICIP = "StaticIp";

    public static final String DHCP = "DHCP";

    public static final String ethernet = "ethernet";

    public void unBindAIDLService(Context context) {
        if (Build.DEVICE.startsWith("x3128") || Build.DEVICE.startsWith("rk3288")) {
//            manager2.unBindAIDLService(context);
        } else if (Integer.parseInt(Build.VERSION.INCREMENTAL.substring(Build.VERSION.INCREMENTAL.indexOf(".20") + 1, Build.VERSION.INCREMENTAL.indexOf(".20") + 9)) >= 20190606) {
            manager2.unBindAIDLService(context);
        } else {
            manager1.unBindAIDLService(context);
        }
    }

    public void setTime(int year, int month, int day, int hour, int minute, int second) {
        if (Build.DEVICE.startsWith("x3128")) {
            manager2.setTime(year, month, day, hour, minute, second);
        } else if (Build.DEVICE.startsWith("rk3288")) {
            manager1.setTime(year, month, day, hour, minute);
        } else if (Integer.parseInt(Build.VERSION.INCREMENTAL.substring(Build.VERSION.INCREMENTAL.indexOf(".20") + 1, Build.VERSION.INCREMENTAL.indexOf(".20") + 9)) >= 20190606) {
            manager2.setTime(year, month, day, hour, minute, second);
        } else {
            manager1.setTime(year, month, day, hour, minute);
        }
    }


    public void reboot() {
        if (Build.DEVICE.startsWith("x3128") || Build.DEVICE.startsWith("rk3288")) {
            Intent intent = new Intent("com.xs.reboot");
            AppInit.getContext().sendBroadcast(intent);
        }  else if (AppInit.getMyManager().getAndroidDisplay().startsWith("astar")) {
            Intent intent = new Intent("android.intent.action.MCREBOOT");
            AppInit.getContext().sendBroadcast(intent);
        }else if (Integer.parseInt(Build.VERSION.INCREMENTAL.substring(Build.VERSION.INCREMENTAL.indexOf(".20") + 1, Build.VERSION.INCREMENTAL.indexOf(".20") + 9)) >= 20190606) {
            manager2.reboot();
        } else {
            manager1.reboot();
        }
    }

    public void setStaticEthIPAddress(String IPaddr, String gateWay, String mask, String dns1, String dns2) {
        if (Build.DEVICE.startsWith("x3128")) {
            manager2.setStaticEthIPAddress(IPaddr, mask, gateWay, dns1, dns2);
        } else if (Build.DEVICE.startsWith("rk3288")) {
            manager1.setStaticEthIPAddress(IPaddr, mask, gateWay, dns1, dns2);
        } else if (Integer.parseInt(Build.VERSION.INCREMENTAL.substring(Build.VERSION.INCREMENTAL.indexOf(".20") + 1, Build.VERSION.INCREMENTAL.indexOf(".20") + 9)) >= 20190606) {
            manager2.setStaticEthIPAddress(IPaddr, gateWay, mask, dns1, dns2);
        } else {
            manager1.setStaticEthIPAddress(IPaddr, gateWay, mask, dns1, dns2);
        }
        FileUtils.writeFileSdcard(ethernet, STATICIP);

    }

    public String getAndroidDisplay() {
        if (Build.DEVICE.startsWith("x3128")) {
            return manager2.getAndroidDisplay();
        }else if (Build.DEVICE.startsWith("astar")) {
            return "astar";
        }
        if (Build.DEVICE.startsWith("rk3288")) {
            return manager1.getAndroidDisplay();
        } else if (Integer.parseInt(Build.VERSION.INCREMENTAL.substring(Build.VERSION.INCREMENTAL.indexOf(".20") + 1, Build.VERSION.INCREMENTAL.indexOf(".20") + 9)) >= 20190606) {
            return manager2.getAndroidDisplay();
        }  else {
            return manager1.getAndroidDisplay();
        }
    }

    public void setDhcpIpAddress(Context context) {
        if (Build.DEVICE.startsWith("x3128")) {
            manager2.setDhcpIpAddress(context);
        } else if (Build.DEVICE.startsWith("rk3288")) {
            manager1.setDhcpIpAddress(context);
            FileUtils.writeFileSdcard(ethernet, DHCP);
        } else if (Integer.parseInt(Build.VERSION.INCREMENTAL.substring(Build.VERSION.INCREMENTAL.indexOf(".20") + 1, Build.VERSION.INCREMENTAL.indexOf(".20") + 9)) >= 20190606) {
            manager2.setDhcpIpAddress(context);
        } else {
            manager1.setDhcpIpAddress(context);
        }
    }

    public String getEthMode() {
        if (Build.DEVICE.startsWith("x3128")) {
            return manager2.getEthMode();
        } else if (AppInit.getMyManager().getAndroidDisplay().startsWith("astar")) {
            try {
                return FileUtils.readFileSdcard(ethernet);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "unknown";
        } else if (Build.DEVICE.startsWith("rk3288")) {
            try {
                return FileUtils.readFileSdcard(ethernet);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "unknown";
        } else if (Integer.parseInt(Build.VERSION.INCREMENTAL.substring(Build.VERSION.INCREMENTAL.indexOf(".20") + 1, Build.VERSION.INCREMENTAL.indexOf(".20") + 9)) >= 20190606) {
            return manager2.getEthMode();
        } else {
            return manager1.getEthMode();
        }
    }

    public void bindAIDLService(Context context) {
        if (Build.DEVICE.startsWith("x3128")) {
            manager2.bindAIDLService(context);
        } else if (Build.DEVICE.startsWith("rk3288")) {
            manager1.bindAIDLService(context);
        } else if (Integer.parseInt(Build.VERSION.INCREMENTAL.substring(Build.VERSION.INCREMENTAL.indexOf(".20") + 1, Build.VERSION.INCREMENTAL.indexOf(".20") + 9)) >= 20190606) {
            manager2.bindAIDLService(context);
        } else {
            manager1.bindAIDLService(context);
        }
    }

    public static WZWManager getInstance(Context context) {
        if (wzwManager == null) {
            wzwManager = new WZWManager(context);
        }
        return wzwManager;
    }

    private WZWManager(Context context) {
        if (Build.DEVICE.startsWith("x3128")) {
            manager2 = com.ys.rkapi.MyManager.getInstance(context);
        } else if (Build.DEVICE.startsWith("rk3288")) {
            manager1 = com.ys.myapi.MyManager.getInstance(context);
        } else if (Integer.parseInt(Build.VERSION.INCREMENTAL.substring(Build.VERSION.INCREMENTAL.indexOf(".20") + 1, Build.VERSION.INCREMENTAL.indexOf(".20") + 9)) >= 20190606) {
            manager2 = com.ys.rkapi.MyManager.getInstance(context);
        } else {
            manager1 = com.ys.myapi.MyManager.getInstance(context);
        }
    }

}
