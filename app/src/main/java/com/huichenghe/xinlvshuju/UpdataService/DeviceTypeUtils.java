package com.huichenghe.xinlvshuju.UpdataService;

import android.content.Context;

import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

/**
 * Created by lixiaoning on 2016/12/7.
 */

public class DeviceTypeUtils
{
    public static String getDeviceType(Context context)
    {
        String result = "";
        String dType = LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.USER_DEVICE_TYPE);
        if(dType != null && dType.equals(MyConfingInfo.DEVICE_HR))
        {
            result = "001";
        }
        else if(dType != null && dType.equals(MyConfingInfo.DEVICE_BLOOD))
        {
            result = "002";
        }
        else
        {
            result = "001";
        }

        return result;
    }


}
