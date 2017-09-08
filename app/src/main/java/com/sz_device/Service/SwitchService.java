package com.sz_device.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.sz_device.EventBus.LegalEvent;
import com.sz_device.EventBus.TemHumEvent;
import com.sz_device.Fun_Switching.mvp.presenter.SwitchPresenter;
import com.sz_device.Fun_Switching.mvp.view.ISwitchView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zbsz on 2017/8/28.
 */

public class SwitchService extends Service implements ISwitchView{

    SwitchPresenter sp = SwitchPresenter.getInstance();

    String Last_Value ;

    boolean legal = false;

    Disposable rx_delay;

    Disposable unlock_noOpen;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        sp.SwitchPresenterSetView(this);
        sp.switch_Open();
        Observable.interval(0, 5, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(@NonNull Long aLong) throws Exception {
            sp.readHum();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTemHumEvent(LegalEvent event) {
        legal = event.getLegal();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSwitchingText(String value) {
        if(Last_Value == null||Last_Value.equals("")){
            Last_Value = value;
            if(value.equals("AAAAAA000000000100")&&legal == false){
                sp.OutD9(true);
            }
        }else{
            if(!value.equals(Last_Value)){
                Last_Value = value;
                if (Last_Value.equals("AAAAAA000000000100")) {
                    if (legal == false) {
                        sp.OutD9(true);
                    }
                    if (unlock_noOpen != null) {
                        unlock_noOpen.dispose();
                    }
                    if (rx_delay != null) {
                        rx_delay.dispose();
                    }
                }else if(Last_Value.equals("AAAAAA000000000101")) {
                    Observable.timer(20, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())
                            .subscribe(new Observer<Long>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                    rx_delay = d;
                                }

                                @Override
                                public void onNext(Long aLong) {
                                    legal = false;
                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                }
            }
            if(legal==true){
                sp.OutD9(false);
            }
            if(legal == true && value.equals("AAAAAA000000000101")){
                Observable.timer(120,TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())
                        .subscribe(new Observer<Long>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                unlock_noOpen = d;
                            }

                            @Override
                            public void onNext(Long aLong) {
                                legal = false;
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                            }
                        });
            }
        }
    }

    @Override
    public void onTemHum(int temperature, int humidity) {
        EventBus.getDefault().post(new TemHumEvent(temperature,humidity));
    }
}
