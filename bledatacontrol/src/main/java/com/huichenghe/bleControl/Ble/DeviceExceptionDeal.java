package com.huichenghe.bleControl.Ble;

import android.content.Context;

/**
 * Created by lixiaoning on 2016/7/27.
 */
public class DeviceExceptionDeal extends BleBaseDataManage
{
    public static final String TAG = DeviceExceptionDeal.class.getSimpleName();
    public static final byte fromDevice = (byte)0xa7;
    public static final byte toDevice = (byte)0x27;
    public static final byte testFromDevice = (byte)0xa8;
    public static final byte testToDevice = (byte)0x28;

    private DeviceExceptionDeal(Context context)
    {
        this.context = context;
    };
    private static DeviceExceptionDeal deviceExceptionDeal;
    private Context context;
    public static DeviceExceptionDeal getExceptionInstance(Context context)
    {
        if(deviceExceptionDeal == null)
        {
            synchronized (DeviceExceptionDeal.class)
            {
                if(deviceExceptionDeal == null)
                {
                    deviceExceptionDeal = new DeviceExceptionDeal(context);
                }
            }
        }
        return deviceExceptionDeal;
    }

    public void dealExceptionInfo(byte[] bufferTmp)
    {
        byte[] bytes = new byte[2];
        bytes[0] = 0x01;
        bytes[1] = 0x00;
        setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
        byte[] temp = new byte[bufferTmp.length + 1];

        System.arraycopy(bufferTmp, 0, temp, 1, bufferTmp.length);
        temp[0] = (byte)0x00;
        dataSendCallback.sendSuccess(temp);
    }


    public void dealTextData(byte[] bufferTmp)
    {
        byte[] temp = new byte[bufferTmp.length + 1];
        System.arraycopy(bufferTmp, 0, temp, 1, bufferTmp.length);
        temp[0] = (byte)0x01;

        dataSendCallback.sendSuccess(bufferTmp);
        if(bufferTmp[0] == (byte)0x01)
        {
            responseData(bufferTmp[0], bufferTmp[bufferTmp.length - 4], bufferTmp[bufferTmp.length - 3]);
        }
        else if(bufferTmp[0] == (byte)0x02)
        {
        }
    }



    private void responseData(byte data1, byte b, byte data)
    {
        byte[] resData = new byte[4];
        resData[0] = data1;
        resData[1] = (byte)0x00;
        resData[2] = b;
        resData[3] = data;
        setMsgToByteDataAndSendToDevice(testToDevice, resData, resData.length);
    }


    private DataSendCallback dataSendCallback;
    public void setOnExceptionData(DataSendCallback dataSendCallback)
    {
        this.dataSendCallback = dataSendCallback;
    }


}
