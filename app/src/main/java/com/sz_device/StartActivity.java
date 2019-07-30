package com.sz_device;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.sz_device.Tools.DESX;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zbsz on 2017/12/8.
 */

public class StartActivity extends Activity {

    private String regEx = "^\\d{4}$";

    private SPUtils config = SPUtils.getInstance("config");

    Pattern pattern = Pattern.compile(regEx);

    @BindView(R.id.dev_prefix)
    TextView dev_prefix;

    @BindView(R.id.devid_input)
    EditText dev_suffix;

    @OnClick(R.id.next)
    void next() {
        if (pattern.matcher(dev_suffix.getText().toString()).matches()) {
            config.put("firstStart", false);
            config.put("ServerId", AppInit.getInstrumentConfig().getServerId());
            config.put("daid", dev_prefix.getText().toString() + dev_suffix.getText().toString());

            JSONObject jsonKey = new JSONObject();
            try {
                jsonKey.put("daid",AppInit.getInstrumentConfig().getServerId());
                jsonKey.put("check", DESX.encrypt(AppInit.getInstrumentConfig().getServerId()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            config.put("key", DESX.encrypt(jsonKey.toString()));
            ActivityUtils.startActivity(getPackageName(),getPackageName()+ AppInit.getInstrumentConfig().getMainActivity());
            StartActivity.this.finish();
            ToastUtils.showLong("设备ID设置成功");
        } else {
            ToastUtils.showLong("设备ID输入错误，请重试");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_form);
        ButterKnife.bind(this);
    }


}
