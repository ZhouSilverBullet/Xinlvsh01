package com.huichenghe.xinlvshuju.mainpack;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.baidu.location.service.LocationService;
import com.huichenghe.bleControl.Ble.DeviceConfig;
import com.huichenghe.bleControl.Ble.LocalDeviceEntity;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lixiaoning on 15-11-18.
 */
public class MyApplication extends Application
{
    public LocationService mLocationService;
    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }


    private AppRecever appRecever;
    public static ExecutorService threadService = null;
    static
    {
        threadService = Executors.newFixedThreadPool(GetThreadCount());
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
        registeBro();
        instance = this;
        mLocationService = new LocationService(getApplicationContext());
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED);
//        ActivityCompat.requestPermissions(MainActivity,);
//        LogHelper.getLogcatHelper(this).startLog();
    }



    private static int GetThreadCount()
    {
        int cpu = Runtime.getRuntime().availableProcessors();
        int cou = cpu * 2 + 2;
//        Log.i(TAG, "线程数量" + cou + "核心数：" + cpu);
        return cou;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        threadService.shutdown();
        unregisterReceiver(appRecever);
    }
    private void registeBro()
    {
        appRecever = new AppRecever();
        IntentFilter inte = new IntentFilter();
        inte.addAction(DeviceConfig.DEVICE_CONNECTE_AND_NOTIFY_SUCESSFUL);
        registerReceiver(appRecever, inte);
    }

    class AppRecever extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            switch (intent.getAction())
            {
                case DeviceConfig.DEVICE_CONNECTE_AND_NOTIFY_SUCESSFUL:
                    LocalDeviceEntity entity = (LocalDeviceEntity) intent.getSerializableExtra("DEVICE_OK_INFO");
                    if(entity == null)return;
                    saveDevice(entity.getName(), entity.getAddress());
                    break;
            }
        }
    }

    private void saveDevice(String name, String address)
    {
        LocalDataSaveTool.getInstance(this).writeSp(DeviceConfig.DEVICE_NAME, name);
        LocalDataSaveTool.getInstance(this).writeSp(DeviceConfig.DEVICE_ADDRESS, address);
        LocalDataSaveTool.getInstance(this).writeSp(DeviceConfig.DEVICE_ADDRESS_COPY, address);
    }
}
