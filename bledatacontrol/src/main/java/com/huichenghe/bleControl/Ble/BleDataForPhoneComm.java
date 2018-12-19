package com.huichenghe.bleControl.Ble;

/**
 * Created by lixiaoning on 2016/11/16.
 */
public class BleDataForPhoneComm extends BleBaseDataManage
{
    public final static byte toDevice = (byte)0x2b;
    public final static byte fromDevice = (byte)0xab;
    private DataSendCallback mDataSendCallback;
    private static BleDataForPhoneComm bleDataForPhoneComm;
    private BleDataForPhoneComm(){}

    public static BleDataForPhoneComm getInstance()
    {
        if(bleDataForPhoneComm == null)
        {
            synchronized (BleDataForPhoneComm.class)
            {
                if(bleDataForPhoneComm == null)
                {
                    bleDataForPhoneComm = new BleDataForPhoneComm();
                }
            }
        }
        return bleDataForPhoneComm;
    }

    public void dealDeviceComm(byte[] bufferTmp)
    {
        if(mDataSendCallback != null)
        {
            mDataSendCallback.sendSuccess(bufferTmp);
        }
    }


    public void setDeviceCommListener(DataSendCallback callback)
    {
        this.mDataSendCallback = callback;
    }


    public void respondFlag2()
    {
        byte[] data = new byte[1];
        data[0] = (byte)0x02;
        setMsgToByteDataAndSendToDevice(toDevice, data, data.length);
    }
}
