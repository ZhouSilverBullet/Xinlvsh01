package com.huichenghe.bleControl.Ble;

import android.content.Context;

import java.util.TimeZone;

/**
 * Created by lixiaoning on 15-11-27.
 */
class BleDataForOutLineData extends BleBaseDataManage
{
    public static byte formDevice = (byte)0xA2;
    public static byte toDevice = (byte)0x22;


    public void dealOutLineMovementData(Context mContext, byte[] data, int dataLength)
    {
        TimeZone tz = TimeZone.getDefault();






    }



}
