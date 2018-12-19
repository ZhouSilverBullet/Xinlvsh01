package com.huichenghe.blecontrol.Ble;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.huichenghe.xinlvshuju.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

/**
 * Created by lixiaoning on 16-clock-sitting.
 */
public class BleDataForFactoryReset extends BleBaseDataManage
{
    private static final String TAG = BleDataForFactoryReset.class.getSimpleName();
    public static final byte toDevice = (byte)0x11;
    public static final byte fromDevice = (byte)0x91;


    public void factoryReset()
    {
        byte[] bytes = new byte[1];
        bytes[0] = (byte)0x01;
        setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
    }


    public void dealTheResult(Context mContext, byte[] bufferTmp)
    {
        byte dataResult = bufferTmp[1];
        String result = "";
        if(bufferTmp[0] == (byte)0x01)
        {
            if(dataResult == (byte)0x00)
            {
                mContext.sendBroadcast(new Intent(MyConfingInfo.FACTORY_RESET_SUCCESS));
//                result = mContext.getString(R.string.factory_reset_seccess);
            }
            else if(dataResult == (byte)0x01)
            {
                factoryReset();
//                result = mContext.getString(R.string.factory_reset_failed);
            }

            Log.i(TAG, "恢复出厂设置结果:" + FormatUtils.bytesToHexString(bufferTmp));


        }


    }
}
