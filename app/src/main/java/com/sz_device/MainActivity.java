package com.sz_device;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.drv.m121.CardInfo;
import com.drv.m121.ICardState;
import com.drv.m121.M121Com;
import com.log.Lg;
import com.sz_device.Fun_Switching.mvp.module.ISwitching;
import com.sz_device.Fun_Switching.mvp.presenter.SwitchPresenter;
import com.sz_device.Fun_Switching.mvp.view.ISwitchView;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class MainActivity extends Activity implements ISwitchView {

    boolean statusD8 = true;

    int count;

    boolean statusD9 = true;

    String Last_Value = "AAAAAA000000000000";

    SwitchPresenter sp = SwitchPresenter.getInstance();

    @BindView(R.id.tInfo)
    TextView tInfo;

    @BindView(R.id.tInfo1)
    TextView tInfo1;

    @OnClick(R.id.btn1)
    void outD8() {
        sp.OutD8(statusD8);
        if (statusD8) {
            tInfo1.setText("第一路输出：闭合");
            statusD8 = !statusD8;
        } else {
            tInfo1.setText("第一路输出：断开");
            statusD8 = !statusD8;
        }
    }

    @OnClick(R.id.btn2)
    void outD9() {
        sp.OutD9(statusD9);
        if (statusD9) {
            tInfo1.setText("第二路输出：闭合");
            statusD9 = !statusD9;
        } else {
            tInfo1.setText("第二路输出：断开");
            statusD9 = !statusD9;
        }
    }

    @OnClick(R.id.btn3)
    void temp() {
        sp.readHum();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        sp.SwitchPresenterSetView(this);
        sp.switch_Open();

    }

    @Override
    public void onSwitchingText(String value) {
       if(!value.equals(Last_Value)){
           Last_Value = value;

           if(Last_Value.endsWith(String.valueOf(1))){
               //tInfo1.setText("第一路输入由开变到关");
               sp.OutD9(false);
           }else{
               count++;
               tInfo1.setText(String.valueOf(count));
               sp.OutD9(true);
           }
       }
        tInfo.setText(value);
    }

    @Override
    public void onTemHum(int temperature, int humidity) {
        tInfo1.setText("温度：" + temperature + "  湿度：" + humidity);
    }

    @Override
    protected void onResume() {
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onResume();
    }
}
