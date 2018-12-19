package com.huichenghe.bleControl.Ble;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import com.huichenghe.bleControl.Utils.FormatUtils;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class BleBaseDataForOutlineMovement extends BleBaseDataManage
{
    public static final String TAG = BleBaseDataForOutlineMovement.class.getSimpleName();
    public static final byte mNotify_cmd = (byte)0xa2;
    public static final byte toDevice = (byte)0x22;
//    private byte[] outLineData;
    private DataSendCallback outlineDataListener;

    private static BleBaseDataForOutlineMovement bleBaseDataForOutlineMovement;
    private BleBaseDataForOutlineMovement(){};
    public static BleBaseDataForOutlineMovement getOutlineInstance()
    {
        if(bleBaseDataForOutlineMovement == null)
        {
            synchronized (BleBaseDataForOutlineMovement.class)
            {
                if (bleBaseDataForOutlineMovement == null)
                {
                    bleBaseDataForOutlineMovement = new BleBaseDataForOutlineMovement();
                }
            }
        }
        return bleBaseDataForOutlineMovement;
    }

    public void setOnOutLineDataListener(DataSendCallback outLineDataListener)
    {
        this.outlineDataListener = outLineDataListener;
    }


    //68 a2 1100 8b436d38 9d436d38 01 0000000000000000 1416
    public void dealTheData(Context mContext, byte[] data, int length)
    {
        outlineDataListener.sendSuccess(data);
    }

    public void requstOutlineData(byte[] data)
    {
        byte[] back = new byte[9];
//        for (int i = 0; i < back.length; i ++)
//        {
//            back[i] = data[i];
//        }
        System.arraycopy(data, 0, back, 0, 9);
        setMsgToByteDataAndSendToDevice(toDevice, back, back.length);
    }





    private int getTheHalfUp(float averageHr)
    {
        BigDecimal bigDecimal = new BigDecimal(averageHr);
        bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP);
        return bigDecimal.intValue();
    }














}
