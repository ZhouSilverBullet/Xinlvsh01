package com.huichenghe.xinlvshuju.UpdataService;

import android.content.Context;
import android.util.Log;

import com.huichenghe.bleControl.Ble.DeviceConfig;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;

/**
 * Created by lixiaoning on 2016/12/9.
 */

public class UserMACUtils
{
    public static String getMac(Context context)
    {
        String mac = LocalDataSaveTool.getInstance(context).readSp(DeviceConfig.DEVICE_ADDRESS);
        Log.i("updateData", "缓存的mac:" + mac);
        mac = formateMAC(mac);
        if(mac != null && mac.equals(""))
        {
            mac = "111111111111";
        }
        Log.i("updateData", "上传数据的mac:" + mac);
        return mac;
    }

    private static String formateMAC(String mac)
    {
        StringBuffer re = new StringBuffer();
        if(mac.contains(":"))
        {
            String[] ma = mac.split(":");
            for (int i = 0; i < ma.length; i++)
            {
                re.append(ma[i].toLowerCase());
            }
        }
        return re.toString();
    }

}
