package com.sz_device.Tools;


import android.app.ProgressDialog;
import android.content.Context;
import android.view.Gravity;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.sz_device.WYYAddPersonActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;

public class MyObserver<T> implements Observer<T>{
    private Context context;

    private ProgressDialog progressDialog;

    Boolean delay;

    public MyObserver(Context context) {
        this.context = context;
        this.progressDialog = new ProgressDialog(context);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        progressDialog.setMessage("数据上传中，请稍候");
        delay = false;
    }

    public MyObserver(Context context, Boolean delay) {
        this.context = context;
        this.progressDialog = new ProgressDialog(context);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        progressDialog.setMessage("数据上传中，请稍候");
        delay = true;
    }

    @Override
    public void onSubscribe(Disposable d) {
        progressDialog.show();
    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable e) {
        progressDialog.dismiss();
        final AlertView alertView = new AlertView("无法连接服务器", null, null, new String[]{"确定"}, null, context, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {

            }
        });
        if(delay){
            Observable.timer(1,TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            alertView.show();
                        }
                    });
        }else {
            alertView.show();
        }

    }

    @Override
    public void onComplete() {
        progressDialog.dismiss();
    }
}
