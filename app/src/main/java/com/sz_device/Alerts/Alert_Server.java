package com.sz_device.Alerts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.sz_device.AppInit;
import com.sz_device.Config.HNMBY_Config;
import com.sz_device.Config.WYY_Config;
import com.sz_device.Function.Func_Camera.mvp.presenter.PhotoPresenter;
import com.sz_device.R;
import com.sz_device.Retrofit.InterfaceApi.ConnectApi;
import com.sz_device.Retrofit.RetrofitGenerator;
import com.sz_device.Tools.DAInfo;

import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Alert_Server {

    private Context context;

    int count = 5;

    private SPUtils config = SPUtils.getInstance("config");

    String url;
    private AlertView inputServerView;
    private EditText etName;
    private ImageView QRview;
    private Button connect;

    public Alert_Server(Context context) {
        this.context = context;
    }

    public void serverInit(final Server_Callback callback) {
        ViewGroup extView1 = (ViewGroup) LayoutInflater.from(this.context).inflate(R.layout.inputserver_form, null);
        etName = (EditText) extView1.findViewById(R.id.server_input);
        QRview = (ImageView) extView1.findViewById(R.id.QRimage);
        connect = (Button) extView1.findViewById(R.id.connect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etName.getText().toString().replaceAll(" ", "").endsWith("/")) {
                    url = etName.getText().toString() + "/";
                } else {
                    url = etName.getText().toString();
                }
                if(AppInit.getInstrumentConfig().getClass().getName().equals(WYY_Config.class.getName())){
                    new RetrofitGenerator().getWyyConnectApi(url).noData("testNet", config.getString("key"))
                            .subscribeOn(Schedulers.io())
                            .unsubscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<String>() {
                                @Override
                                public void onSubscribe(@NonNull Disposable d) {

                                }

                                @Override
                                public void onNext(String s) {
                                    try {
                                        if (s.equals("true")) {
                                            config.put("ServerId", url);
                                            ToastUtils.showLong("连接服务器成功,请点击确定立即启用");
                                            callback.setNetworkBmp();
                                            //iv_network.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wifi));
                                        } else {
                                            ToastUtils.showLong("连接服务器失败");
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(@NonNull Throwable e) {
                                    ToastUtils.showLong("服务器连接失败");
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                }else if(AppInit.getInstrumentConfig().getClass().getName().equals(HNMBY_Config.class.getName())){
                    new RetrofitGenerator().getHnmbyApi(url).noData("testNet", config.getString("key"))
                            .subscribeOn(Schedulers.io())
                            .unsubscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<String>() {
                                @Override
                                public void onSubscribe(@NonNull Disposable d) {

                                }

                                @Override
                                public void onNext(String s) {
                                    try {
                                        if (s.equals("true")) {
                                            config.put("ServerId", url);
                                            ToastUtils.showLong("连接服务器成功,请点击确定立即启用");
                                            callback.setNetworkBmp();
                                            //iv_network.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wifi));
                                        } else {
                                            ToastUtils.showLong("连接服务器失败");
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(@NonNull Throwable e) {
                                    ToastUtils.showLong("服务器连接失败");
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                }else {
                    new RetrofitGenerator().getConnectApi(url).noData1("cjy_updata","testNet", config.getString("key"))
                            .subscribeOn(Schedulers.io())
                            .unsubscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<String>() {
                                @Override
                                public void onSubscribe(@NonNull Disposable d) {

                                }

                                @Override
                                public void onNext(String s) {
                                    try {
                                        if (s.equals("true")) {
                                            config.put("ServerId", url);
                                            ToastUtils.showLong("连接服务器成功,请点击确定立即启用");
                                            callback.setNetworkBmp();
                                            //iv_network.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wifi));
                                        } else {
                                            ToastUtils.showLong("连接服务器失败");
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void onError(@NonNull Throwable e) {
                                    ToastUtils.showLong("服务器连接失败");
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                }
//                new RetrofitGenerator().getCommonApi(url).noData("testNet", config.getString("key"))
//                            .subscribeOn(Schedulers.io())
//                            .unsubscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(new Observer<String>() {
//                                @Override
//                                public void onSubscribe(@NonNull Disposable d) {
//
//                                }
//
//                                @Override
//                                public void onNext(String s) {
//                                    try {
//                                        if (s.equals("true")) {
//                                            config.put("ServerId", url);
//                                            ToastUtils.showLong("连接服务器成功,请点击确定立即启用");
//                                            callback.setNetworkBmp();
//                                            //iv_network.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wifi));
//                                        } else {
//                                            ToastUtils.showLong("连接服务器失败");
//                                        }
//                                    }catch (Exception e){
//                                        e.printStackTrace();
//                                    }
//                                }
//
//                                @Override
//                                public void onError(@NonNull Throwable e) {
//                                    ToastUtils.showLong("服务器连接失败");
//                                }
//
//                                @Override
//                                public void onComplete() {
//
//                                }
//                            });

            }
        });
        inputServerView = new AlertView("服务器设置", null, "取消", new String[]{"确定"}, null, this.context, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                if (position == 0) {
                    Observable.interval(0, 1, TimeUnit.SECONDS)
                            .take(count + 1)
                            .map(new Function<Long, Long>() {
                                @Override
                                public Long apply(@NonNull Long aLong) throws Exception {
                                    return count - aLong;
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Long>() {
                                @Override
                                public void onSubscribe(@NonNull Disposable d) {

                                }

                                @Override
                                public void onNext(@NonNull Long aLong) {
                                    ToastUtils.showLong(aLong + "秒后重新开机保存设置");
                                }

                                @Override
                                public void onError(@NonNull Throwable e) {

                                }

                                @Override
                                public void onComplete() {
                                    PhotoPresenter.getInstance().close_Camera();
                                    AppInit.getMyManager().reboot();
                                }
                            });
                }
            }
        });
        inputServerView.addExtView(extView1);
    }



    public void show() {
        Bitmap mBitmap = null;
        etName.setText(config.getString("ServerId"));
        DAInfo di = new DAInfo();
        try {
            di.setId(config.getString("daid"));
            di.setName(AppInit.getInstrumentConfig().getServiceName());
            di.setModel(AppInit.getInstrumentConfig().getModel());
            di.setSoftwareVer(AppUtils.getAppVersionName());
            di.setProject(AppInit.getInstrumentConfig().getProject());
            mBitmap = di.daInfoBmp();
        } catch (Exception ex) {

        }
        if (mBitmap != null) {
            QRview.setImageBitmap(mBitmap);
        }
        inputServerView.show();
    }

    public interface Server_Callback {
        void setNetworkBmp();
    }


}
