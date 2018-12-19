package com.huichenghe.xinlvshuju;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;

import com.huichenghe.bleControl.Ble.BleDataForSettingArgs;
import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

/**
 * Created by lixiaoning on 2016/8/19.
 */
public class MyTimeBrocaset extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String unit = LocalDataSaveTool.getInstance(context.getApplicationContext()).readSp(MyConfingInfo.METRICORINCH);
        boolean is24 = DateFormat.is24HourFormat(context);
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_DATE_CHANGED))
        {
            Log.i("", "日期改变" + intent.getAction().toString());
            if(BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice())
            {
                BleDataForSettingArgs.getInstance(context).setArgs(unit, is24);
            }
        }
        if(action.equals(Intent.ACTION_TIME_CHANGED))
        {
            Log.i("", "手机设置时间：");
            if(BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice())
            {
                BleDataForSettingArgs.getInstance(context).setArgs(unit, is24);
            }
        }
    }
}
