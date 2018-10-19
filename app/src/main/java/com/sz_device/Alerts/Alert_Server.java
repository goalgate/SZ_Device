package com.sz_device.Alerts;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.sz_device.R;
import com.sz_device.Retrofit.InterfaceApi.ConnectApi;
import com.sz_device.Retrofit.RetrofitGenerator;
import com.sz_device.Tools.DAInfo;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Alert_Server {


    private Context context;

    private SPUtils config = SPUtils.getInstance("config");

    String url;
    private AlertView inputServerView;
    private EditText etName;
    private ImageView QRview;

    public Alert_Server(Context context) {
        this.context = context;
    }

    public void serverInit(final Server_Callback callback) {
        ViewGroup extView1 = (ViewGroup) LayoutInflater.from(this.context).inflate(R.layout.inputserver_form, null);
        etName = (EditText) extView1.findViewById(R.id.server_input);
        QRview = (ImageView) extView1.findViewById(R.id.QRimage);
        inputServerView = new AlertView("服务器设置", null, "取消", new String[]{"确定"}, null, this.context, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                if (position == 0) {
                    if (!etName.getText().toString().replaceAll(" ", "").endsWith("/")) {
                        url = etName.getText().toString() + "/";
                    } else {
                        url = etName.getText().toString();
                    }
                    RetrofitGenerator.getConnectApi(url).noData("testNet", config.getString("key"))
                            .subscribeOn(Schedulers.io())
                            .unsubscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<String>() {
                                @Override
                                public void onSubscribe(@NonNull Disposable d) {

                                }

                                @Override
                                public void onNext(String s) {
                                    if (s.equals("true")) {
                                        config.put("ServerId", url);
                                        ToastUtils.showLong("连接服务器成功");
                                        callback.setNetworkBmp();
                                        //iv_network.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wifi));
                                    } else {
                                        ToastUtils.showLong("连接服务器失败");
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
//                    OkHttpClient client = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS)
//                            .writeTimeout(30, TimeUnit.SECONDS)
//                            .readTimeout(30, TimeUnit.SECONDS)
//                            .build();
//                    new Retrofit.Builder()
//                            .addConverterFactory(GsonConverterFactory.create())
//                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                            .baseUrl(url).client(client).build().create(ConnectApi.class)
//                            .noData("testNet", config.getString("key"))
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
//                                    if (s.equals("true")) {
//                                        config.put("ServerId", url);
//                                        ToastUtils.showLong("连接服务器成功");
//                                        callback.setNetworkBmp();
//                                        //iv_network.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wifi));
//                                    } else {
//                                        ToastUtils.showLong("连接服务器失败");
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
            di.setName("数据采集器");
            di.setModel("CBDI-P-IC");
            di.setSoftwareVer(AppUtils.getAppVersionName());
            di.setProject("SZ");
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

    ;
}
