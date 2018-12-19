package com.huichenghe.blecontrol.Ble;

import android.content.Context;

/**
 * Created by lixiaoning on 15-12-30.
 */
public class BleLiveHRData extends BleBaseDataManage
{

    public static byte toDevice = (byte)0x0A;
    public void openLiveWatch(Context mcContext)
    {
        setMessageDataByString(toDevice, "clock", true);
    }

    public void closeLiveWatch(Context mcContext)
    {
        setMessageDataByString(toDevice, "0", true);
    }


}
