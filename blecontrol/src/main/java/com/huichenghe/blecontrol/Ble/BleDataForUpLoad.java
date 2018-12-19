package com.huichenghe.blecontrol.Ble;

/**
 * Created by lixiaoning on 16-clock-sos.
 */
public class BleDataForUpLoad extends BleBaseDataManage
{
    public static final String TAG = BleDataForUpLoad.class.getSimpleName();

    public static final byte toDevice = (byte)0x08;
    public static final byte fromDevice = (byte)0x88;


    public void sendToDeviceToStartUpLoad(int length)
    {
        byte[] data = new byte[5];
        data[0] = (byte)0x00;
        data[1] = (byte) (length);
        data[2] = (byte)(length >> 8);
        data[3] = (byte)(length >> 16);
        data[4] = (byte)(length >> 24);

        setMsgToByteDataAndSendToDevice(toDevice, data, data.length);
    }





    public void overTheUpdate()
    {
        byte[] overDate = new byte[1];
        overDate[0] = (byte)0x02;
        setMsgToByteDataAndSendToDevice(toDevice, overDate, overDate.length);
    }



    public void sendDataUpdate(byte[] data)
    {
        setMsgToByteDataAndSendToDevice(toDevice, data, data.length);
    }
}
