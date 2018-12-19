package com.huichenghe.blecontrol.Ble;

import android.content.Context;

import java.util.TimeZone;

/**
 * 离线数据获取和处理类
 * Created by lixiaoning on 15-11-27.
 */
class BleDataForOutLineData extends BleBaseDataManage
{
    public static byte formDevice = (byte)0xA2;
    public static byte toDevice = (byte)0x22;


    /**
     * 处理上传的离线数据
     */
    public void dealOutLineMovementData(Context mContext, byte[] data, int dataLength)
    {
        TimeZone tz = TimeZone.getDefault();   // 获取默认时区






    }



}
