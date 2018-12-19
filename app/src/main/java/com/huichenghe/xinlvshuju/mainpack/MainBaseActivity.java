package com.huichenghe.xinlvshuju.mainpack;

import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;
import com.huichenghe.xinlvshuju.Utils.NetStatus;
import com.huichenghe.xinlvshuju.Utils.Sub_update_app;
import com.huichenghe.xinlvshuju.Utils.UpdateAppTask;
import com.huichenghe.xinlvshuju.http.UserAccountUtil;

import cn.sharesdk.framework.ShareSDK;

/**
 * Created by DIY on 2016/sitting/16.
 */
public class MainBaseActivity extends AppCompatActivity
{
    @Override
    public Resources getResources()
    {
        Resources res = super.getResources();
        Configuration configuration = new Configuration();
        configuration.setToDefaults();
        res.updateConfiguration(configuration, res.getDisplayMetrics());
        return res;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        checkAppUpgrade();
        InitBluetooth();
        ShareSDK.initSDK(this.getApplicationContext());//初始化shareSDK
        setDefaultTarget();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareSDK.stopSDK();
    }

    /**
     * 在用户没有设置的情况下，设置默认值
     */
    private void setDefaultTarget()
    {
        if(LocalDataSaveTool.getInstance(this.getApplicationContext()).readSp(MyConfingInfo.TARGET_SETTING_VALUE).equals(""))
        {
            LocalDataSaveTool.getInstance(this.getApplicationContext()).writeSp(MyConfingInfo.TARGET_SETTING_VALUE, String.valueOf(10000));
        }
        if(LocalDataSaveTool.getInstance(this.getApplicationContext()).readSp(MyConfingInfo.TARGET_SETTING_VALUE_SLEEP).equals(""))
        {
            LocalDataSaveTool.getInstance(this.getApplicationContext()).writeSp(MyConfingInfo.TARGET_SETTING_VALUE_SLEEP, String.valueOf(8));
        }
    }

    /**
     * 初始化蓝牙相关内容
     */
    private void InitBluetooth()
    {
        // 检查手机是否支持蓝牙4.0
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            MyToastUitls.showToast(this.getApplicationContext(), R.string.ble_not_support, 1);
            finish();
            return;
        }
        // 检查手机是否支持蓝牙
        if (BluetoothAdapter.getDefaultAdapter() == null) {
            MyToastUitls.showToast(this.getApplicationContext(), R.string.ble_not_support, 1);
            finish();
        }
    }
    private void checkAppUpgrade()
    {
        if(NetStatus.isNetWorkConnected(getApplicationContext()))
        {
            String accout = UserAccountUtil.getAccount(MainBaseActivity.this.getApplicationContext());
            if(accout != null && !accout.equals(""))
            {
                UpdateAppTask task=new UpdateAppTask(this, new Sub_update_app(this));
                task.executeOnExecutor(MyApplication.threadService);
            }
        }
    }



}
