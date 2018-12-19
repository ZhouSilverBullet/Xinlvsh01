package com.huichenghe.blecontrol.Ble;

/**
 * Created by lixiaoning on 2016/5/9.
 */
public class BleForFindDevice extends BleBaseDataManage
{
    public static byte fromDevice = (byte)0x93;
    public static byte toDevice = (byte)0x13;
    private onSendToDeviceOk onsendOk;
    private static BleForFindDevice mBleForFindDevice;
    private BleForFindDevice(){};
    public static synchronized BleForFindDevice getBleForFindDeviceInstance()
    {
        if(mBleForFindDevice == null)
        {
            mBleForFindDevice = new BleForFindDevice();
        }
        return mBleForFindDevice;
    }

    public void sendToStartFindDevice()
    {
        byte[] data = new byte[1];
        data[0] = (byte)0x00;
        setMsgToByteDataAndSendToDevice(toDevice, data, data.length);
    }
    public void dealTheResponseData(byte[] backData)
    {
        byte data = backData[0];
        if(data == (byte)0x00)
        {
            onsendOk.sendOk(0);
        }
    }
    public void closeDeviceShock()
    {
        byte[] data = new byte[1];
        data[0] = (byte)0x01;
        setMsgToByteDataAndSendToDevice(toDevice, data, data.length);
    }

    public void setListener(onSendToDeviceOk listener)
    {
        this.onsendOk = listener;
    }

    public interface onSendToDeviceOk
    {
        void sendOk(int count);
    }

}
