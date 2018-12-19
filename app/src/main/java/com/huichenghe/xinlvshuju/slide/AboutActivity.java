package com.huichenghe.xinlvshuju.slide;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huichenghe.bleControl.Ble.BleDataForHardVersion;
import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.bleControl.Ble.DataSendCallback;
import com.huichenghe.bleControl.Ble.DeviceConfig;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;
import com.huichenghe.xinlvshuju.Utils.NetStatus;
import com.huichenghe.xinlvshuju.Utils.ProgressUtils;
import com.huichenghe.xinlvshuju.Utils.Sub_update_app;
import com.huichenghe.xinlvshuju.Utils.UpdateAppTask;
import com.huichenghe.xinlvshuju.http.getHardVersionHelper;
import com.huichenghe.xinlvshuju.mainpack.BaseActivity;


public class AboutActivity extends BaseActivity
{
    public static final String TAG = AboutActivity.class.getSimpleName();
    private TextView tvHardVersion;
    private Sub_update_app sub;
    private MyBleListenter recy;
    private LinearLayout mHelp;
    private Handler mHandler = new Handler()
    {
//        @Override
//        public void handleMessage(Message msg)
//        {
//            super.handleMessage(msg);
//            switch (msg.what)
//            {
//                case 0:
//                    updateHardVersion();
//                    break;
//            }
//        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
//        settingTheStatebarAndNavigationbar();
        init();
        recy = new MyBleListenter();
        registRece();

    }

    private void registRece()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(DeviceConfig.DEVICE_CONNECTE_AND_NOTIFY_SUCESSFUL);
        filter.addAction(MyConfingInfo.ON_DEVICE_STATE_CHANGE);
        registerReceiver(recy, filter);
    }

    private void updateHardVersion()
    {
            if(BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice())
            {
                String v = LocalDataSaveTool.getInstance(AboutActivity.this.getApplicationContext()).readSp(MyConfingInfo.HARD_VERSION);
                if(v != null && v.equals(""))
                {
                    BleDataForHardVersion.getInstance().setDataSendCallback(new DataSendCallback()
                    {
                        @Override
                        public void sendSuccess(byte[] receveData)
                        {
                            Log.i(TAG, "收到的数据：" + FormatUtils.bytesToHexString(receveData));
                            byte hard = receveData[0];
                            byte bluetooth;
                            byte soft;
                            byte[] hardVerson = new byte[receveData.length - 4];
                            System.arraycopy(receveData, 0, hardVerson, 0, receveData.length - 4);
                            hardVerson = revresionBytes(hardVerson);
                            bluetooth = receveData[receveData.length - 4];
                            soft = receveData[receveData.length - 3];
                            final String versionString = FormatUtils.bytesToHexString(hardVerson) + "/"
                                    + parseTheHexString(bluetooth) + "/"
                                    + parseTheHexString(soft);


                            LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.HARD_VERSION, versionString);

                            Log.i(TAG, "设备版本号:" + versionString);
                            AboutActivity.this.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    tvHardVersion.setText(versionString.replace('/', '.'));
                                    if(NetStatus.isNetWorkConnected(getApplicationContext()))
                                    {
                                        mHandler.postDelayed(runnable, 1000);
                                        getNewHardView(versionString);
                                    }
                                }
                            });
                        }
                        @Override
                        public void sendFailed(){}
                        @Override
                        public void sendFinished(){}
                    });
                    BleDataForHardVersion.getInstance().requestHardVersion();
                }
                else
                {
//                    String vs = parseVersionFormate(v);
                    tvHardVersion.setText(v.replace('/', '.'));
                    if(NetStatus.isNetWorkConnected(getApplicationContext()))
                    {
                        mHandler.postDelayed(runnable, 1000);
                        getNewHardView(v);
                    }
                }
            }
            else
            {
                tvHardVersion.setText(getString(R.string.disconnected));
            }
    }

    private byte[] revresionBytes(byte[] hardVerson)
    {
        byte[] reV = new byte[hardVerson.length];
        for (int i = 0; i < hardVerson.length; i++)
        {
            reV[i] = hardVerson[hardVerson.length - i - 1];
        }
        return reV;
    }

    private static String parseTheHexString(byte hard)
    {
        byte a = (byte)(hard >> 4);
        byte b = (byte)(hard & 0x0f);
        String a1 = Integer.toHexString(a);
        String b1 = Integer.toHexString(b);
        return a1 + "" + b1;
    }

    private String parseVersionFormate(String v)
    {
        String[] allV = v.split("\\/");
        if(allV[0].length() >= 4)
        {
            String hardA = allV[0].substring(0, 2);
            String hardB = allV[0].substring(2);
            hardA = Integer.toHexString(Integer.parseInt(hardA));
            hardB = Integer.toHexString(Integer.parseInt(hardB));
            StringBuffer ha = new StringBuffer();
            ha.append(hardA.length() < 2 ? "0" + hardA : hardA);
            ha.append(hardB.length() < 2 ? "0" + hardB : hardB);
//            tvHardVersion.setText(ha.toString() + "/" + allV[1] + allV[2]);
            return ha.toString() + "/" + allV[1] + "/" + allV[2];
        }

        int ver = Integer.parseInt(allV[0]);
        String vers = String.valueOf(Integer.toHexString(ver));
        String hard = (vers.length() < 2) ? (0 + vers) : vers;
        return hard + "/" + allV[1] + "/" + allV[2];
    }

    private void init()
    {
        mHelp = (LinearLayout)findViewById(R.id.help_layout);
        TextView checkHardVersion = (TextView) findViewById(R.id.check_hard_version);
        TextView checkAppVersion = (TextView) findViewById(R.id.check_app_version);
        TextView tvAppVersion = (TextView) findViewById(R.id.app_version_tv);
        tvHardVersion = (TextView)findViewById(R.id.hard_version_tv);
        mHelp.setOnClickListener(listener);
        checkHardVersion.setOnClickListener(listener);
        checkAppVersion.setOnClickListener(listener);
        tvAppVersion.setText(getAppVersion());
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_about_activity);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);           // 设置为Actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// 显示返回键
        toolbar.setNavigationIcon(R.mipmap.back_icon_new);
        toolbar.setNavigationOnClickListener(new NoDoubleClickListener()
        {
            @Override
            public void onNoDoubleClick(View v)
            {
                AboutActivity.this.onBackPressed();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        updateHardVersion();
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(recy);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ProgressUtils.getInstance().showProgressDialog(AboutActivity.this, getString(R.string.check_new_version));
        }
    };


    private getHardVersionHelper helper;
    NoDoubleClickListener listener = new NoDoubleClickListener()
    {
        @Override
        public void onNoDoubleClick(View v)
        {
            switch (v.getId())
            {
                case R.id.check_app_version:
                    mHandler.postDelayed(runnable, 1000);
                    UpdateAppTask mUpdateAppTask= new UpdateAppTask(AboutActivity.this,sub);
                    mUpdateAppTask.setOnDataBack(new UpdateAppTask.OnDataBack() {
                        @Override
                        public void onBack() {
                            mHandler.removeCallbacks(runnable);
                        }
                    });
                    mUpdateAppTask.execute((Void) null);
                    break;
                case R.id.check_hard_version:
                    if(NetStatus.isNetWorkConnected(AboutActivity.this.getApplicationContext()))
                    {
                        if(BluetoothLeService.getInstance().isConnectedDevice())
                        {
                            String hardVersion = LocalDataSaveTool.getInstance(AboutActivity.this.getApplicationContext())
                                    .readSp(MyConfingInfo.HARD_VERSION);
                            if(hardVersion != null && !hardVersion.equals(""))
                            {
                                mHandler.postDelayed(runnable, 1000);
                                getNewHardView(hardVersion);
                            }
                            else
                            {
                                updateHardVersion();
                                String hv = LocalDataSaveTool.getInstance(AboutActivity.this.getApplicationContext()).readSp(MyConfingInfo.HARD_VERSION);
                                getNewHardView(hv);
                            }
                        }
                        else
                        {
                            MyToastUitls.showToast(AboutActivity.this, R.string.not_connecte, 1);
                        }
                    }
                    else
                    {
                        MyToastUitls.showToast(AboutActivity.this, R.string.net_wrong, 1);
                    }
                    break;
                case R.id.help_layout:
                    launchActivity(mHelp, HelpActivity.class);
                    break;
            }

        }
    };

    private void getNewHardView(String hardVersion)
    {
        if(hardVersion != null && !hardVersion.equals(""))
        {
            if(helper == null)
            {
                helper = new getHardVersionHelper();
            }
            helper.setOnRequstBack(new getHardVersionHelper.OnRequesBack()
            {
                @Override
                public void onDataBack()
                {
                    mHandler.removeCallbacks(runnable);
                }
            });
            helper.initHardVersionHelper(AboutActivity.this, hardVersion, true);
            helper.getNewVersionFromNet();
        }
        else
        {
            MyToastUitls.showToast(getApplicationContext(), R.string.not_connecte, 1);
        }
    }

    private void launchActivity(ViewGroup aboutA, Class<?> cl) {

        ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(
                aboutA,    // 点击的控件
                aboutA.getWidth()/4,            // 起始坐标x
                aboutA.getHeight() / 2,    // 起始坐标y
                0,            // 拉伸开始的区域的大小
                0);        // 拉伸开始的区域的大小

        startNewActivityOptions(options, cl);
    }

    private void startNewActivityOptions(ActivityOptionsCompat options, Class<?> cl) {
        Intent intent = new Intent(this, cl);
        intent.putExtra(MyConfingInfo.WHERE_ARE_YOU_FROM, MyConfingInfo.FROM_THE_MAINACTIVITY);
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    /**
     * 设置沉浸式状态栏和底部虚拟键全屏
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void settingTheStatebarAndNavigationbar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }


    /**
     * 获取app软件版本
     * @return
     */
    private String getAppVersion()
    {
        PackageInfo mPackageInfo;
        try {
            mPackageInfo = AboutActivity.this
                    .getPackageManager()
                    .getPackageInfo(AboutActivity.this.getPackageName(), 0);

            return mPackageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }




    class MyBleListenter extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent.getAction().equals(DeviceConfig.DEVICE_CONNECTE_AND_NOTIFY_SUCESSFUL))
            {
                Log.i(TAG, "蓝牙连接成功!");
                updateHardVersion();
            }
            else if(intent.getAction().equals(MyConfingInfo.ON_DEVICE_STATE_CHANGE))
            {
                clearHeardVersion();
            }

        }
    }

    private void clearHeardVersion()
    {
        tvHardVersion.setText(R.string.disconnected);
    }

}
