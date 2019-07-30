package com.sz_device.Alerts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.blankj.utilcode.util.ToastUtils;
import com.sz_device.Function.Fun_FingerPrint.mvp.presenter.FingerPrintPresenter;
import com.sz_device.R;
import com.sz_device.UI.PasswordInputView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;


public class Alert_Password {
    private Context context;

    public Alert_Password(Context context) {
        this.context = context;
    }

    private AlertView passwordAlert;
    private PasswordInputView passwordInputView;

    public void PasswordViewInit(final Callback callback) {
        ViewGroup passwordView = (ViewGroup) LayoutInflater.from(this.context).inflate(R.layout.inputpassword_form, null);
        passwordInputView = (PasswordInputView) passwordView.findViewById(R.id.passwordInputView);
        passwordAlert = new AlertView("通知:", "请输入密码以进入设置界面", "取消", new String[]{"确定"}, null, this.context, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                if (position == 0) {
                    if (passwordInputView.getText().toString().equals("123654")) {
                        callback.normal_call();
                    } else if (passwordInputView.getText().toString().equals("665901")) {
                        callback.super_call();
                    } else if (passwordInputView.getText().toString().equals("453987")) {
                        FingerPrintPresenter.getInstance().fpCancel(true);
                        Observable.timer(1, TimeUnit.SECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) throws Exception {
                                        FingerPrintPresenter.getInstance().fpRemoveAll();
                                        Observable.timer(1, TimeUnit.SECONDS)
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Consumer<Long>() {
                                                    @Override
                                                    public void accept(Long aLong) throws Exception {
                                                        ToastUtils.showLong("全部指纹已清除");
                                                        FingerPrintPresenter.getInstance().fpIdentify();
                                                    }
                                                });
                                    }
                                });

                    } else {
                        ToastUtils.showLong("密码错误，请重试");
                    }
                }

            }
        });
        passwordAlert.addExtView(passwordView);
    }

    public void show() {
        passwordInputView.setText(null);
        passwordAlert.show();
    }

    public interface Callback {
        void normal_call();

        void super_call();
    }
}
