package com.huichenghe.xinlvshuju.zhy.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.huichenghe.bleControl.Ble.DeviceConfig;
import com.huichenghe.xinlvshuju.mainpack.BaseActivity;
import com.huichenghe.xinlvshuju.mainpack.MainActivity;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;
import com.huichenghe.xinlvshuju.Utils.NetStatus;
import com.huichenghe.xinlvshuju.http.LoginOnBackground;
import com.huichenghe.xinlvshuju.http.checkThirdPartyTask;
import com.huichenghe.xinlvshuju.slide.Device_List_Activity;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 欢迎页面
 */
public class WelcomeActivity extends BaseActivity
{
    public static final String TAG = WelcomeActivity.class.getSimpleName();
    private String userAccount;
    private checkThirdPartyTask thirdPartyTask;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        /** 设置隐藏底部导航键 */
//        setStatusBarTransparent();
//        userAccount = LocalDataSaveTool.getInstance(WelcomeActivity.this.getApplicationContext()).readSp(MyConfingInfo.USER_ACCOUNT);
//        Log.i(TAG, "缓存中的账号：" + userAccount);
//        paseTheUserAccount(userAccount);
        doNextStep();
    }

    private void paseTheUserAccount(String userAccount)
    {
        if(userAccount != null && userAccount.equals(""))
        {
//            String birthday = LocalDataSaveTool.getInstance(WelcomeActivity.this).readSp(MyConfingInfo.USER_BIRTHDAY);
//            if(birthday != null && birthday.isEmpty())
//            {
//                startActy(WelcomeActivity.this, nextToLoginActivity.class);
//            }
//            else
//            {
//                startActy(WelcomeActivity.this, MainActivity.class);
//            }
        }
        else
        {
            JSONObject json = null;
            String type = null;
            try {
                json = new JSONObject(userAccount);
                type = json.getString(MyConfingInfo.TYPE);
                String accountId = json.getString(MyConfingInfo.ACCOUNT);
                if(type != null && !type.equals(""))
                {
                   doLogin(type, accountId);
                }
                else
                {
//                    startActy(WelcomeActivity.this, nextToLoginActivity.class);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param type 1是微信，2是qq， 3是facebook， 4是普通账号
     * @param accountId
     */
    private void doLogin(String type, String accountId)
    {
        if(NetStatus.isNetWorkConnected(WelcomeActivity.this))
        {
            switch (type)
            {
                case MyConfingInfo.QQ_TYPE:
                case MyConfingInfo.WEICHART_TYPE:
                case MyConfingInfo.FACEBOOK_TYPE:
                    thirdPartyTask = new checkThirdPartyTask(WelcomeActivity.this);
                    thirdPartyTask.execute(accountId.split(";")[0], type, null, null, null);
                    break;
                case MyConfingInfo.NOMAL_TYPE:
                    new LoginOnBackground(WelcomeActivity.this).execute((Void) null);
                    break;
            }
        }
        else
        {
            MyToastUitls.showToast(WelcomeActivity.this, R.string.net_wrong, 1);
//            startActy(WelcomeActivity.this, MainActivity.class);
        }
    }


    /**
     * 设置状态栏透明
     */
    private void setStatusBarTransparent()
    {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void set() {
        /** 设置状态栏透明*/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    Runnable run = new Runnable() {
        @Override
        public void run() {
            doNextStep();
        }
    };

    private void doNextStep()
    {

        String userAcc = LocalDataSaveTool.getInstance(WelcomeActivity.this.getApplicationContext()).readSp(MyConfingInfo.USER_ACCOUNT);
        Log.i(TAG, "登录账户字符" + userAcc);
        if(userAcc != null && userAcc.isEmpty())
        {
            startActy(WelcomeActivity.this, nextToLoginActivity.class);
        }
        else
        {
    //                try {
    //                    JSONObject json = new JSONObject(userAcc);
    //                    String type = json.getString(MyConfingInfo.TYPE);
    //                    if(type != null && type.equals(""))
    //                    {
    //                        startActy(WelcomeActivity.this, nextToLoginActivity.class);
    //                        return;
    //                } catch (JSONException e) {
    //                    e.printStackTrace();
    //                    startActy(WelcomeActivity.this, nextToLoginActivity.class);
    //                    return;
    //                }
            String type = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.USER_DEVICE_TYPE);
            if(type != null && type.equals(""))
            {
                startActy(getApplicationContext(), Device_List_Activity.class);
                LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(DeviceConfig.DEVICE_ADDRESS, "");
                LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(DeviceConfig.DEVICE_NAME, "");
            }
            else
            {
                startActy(WelcomeActivity.this, MainActivity.class);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void startActy(Context mContext, Class<?> clazz)
    {
        Intent mIntent = new Intent(mContext, clazz);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mIntent);
        WelcomeActivity.this.finish();
    }

}
