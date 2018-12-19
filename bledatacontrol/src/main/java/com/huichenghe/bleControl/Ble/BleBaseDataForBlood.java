package com.huichenghe.bleControl.Ble;

import java.math.BigDecimal;

public class BleBaseDataForBlood extends BleBaseDataManage
{
    public static final String TAG = BleBaseDataForBlood.class.getSimpleName();
    public static final byte mNotify_cmd = (byte)0xaa;
    public static final byte toDevice = (byte)0x2a;
//    private byte[] outLineData;
    private DataSendCallback bloodDataListener;

    private static BleBaseDataForBlood bleBaseDataForBloodMovement;
    private BleBaseDataForBlood(){};
    public static BleBaseDataForBlood getBloodInstance()
    {
        if(bleBaseDataForBloodMovement == null)
        {
            synchronized (BleBaseDataForBlood.class)
            {
                if (bleBaseDataForBloodMovement == null)
                {
                    bleBaseDataForBloodMovement = new BleBaseDataForBlood();
                }
            }
        }
        return bleBaseDataForBloodMovement;
    }

    public void setOnBloodDataListener(DataSendCallback outLineDataListener)
    {
        this.bloodDataListener = outLineDataListener;
    }


    public void dealTheData(byte[] data)
    {
        bloodDataListener.sendSuccess(data);
    }
//todo 请求
public void requstOutlineData(int hig,int low) {
    byte[] send = new byte[3];
    send[0] = (byte) 0x05;
    send[1] = (byte) hig;
    send[2] = (byte) low;
    setMsgToByteDataAndSendToDevice(toDevice, send, send.length);
}

    /**
     * 告诉设备不接收原始数据
     */
    public void requestdevice() {
        byte[] send = {0x02, 0x00};
        setMsgToByteDataAndSendToDevice(toDevice, send, send.length);
    }
    public void requstOutlineData()
    {

        byte[] back = new byte[1];
        back[0] = (byte)0x00;
        setMsgToByteDataAndSendToDevice(toDevice, back, back.length);
    }

    private int getTheHalfUp(float averageHr)
    {
        BigDecimal bigDecimal = new BigDecimal(averageHr);
        bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP);
        return bigDecimal.intValue();
    }

    /**
     * 发送用户输入的血压值
     * @param firstData
     * @param secondData
     */
    public void sendStandardBloodData(int firstData, int secondData)
    {
        byte[] send = new byte[3];
        send[0] = (byte)0x05;
        send[1] = (byte)firstData;
        send[2] = (byte)secondData;
        setMsgToByteDataAndSendToDevice(toDevice, send, send.length);
    }
    //发送是否接受血压数据应答
    public void sendStandardBloodDataYD(int  true_or_flase){
        byte [] send =new byte[2];
        send[0]=(byte)0x01;
        send[1]=(byte)true_or_flase ;
        setMsgToByteDataAndSendToDevice(toDevice,send,send.length);
    }
    //  设备向APP查询,询问APP是否准备好接收血压原始数据：
    public  void  sendStandardBloodDataFA(int t_or_f){
        byte [] send =new byte[2];
        send[0]=(byte)0x02;
        send[1]=(byte)t_or_f ;
        setMsgToByteDataAndSendToDevice(toDevice,send,send.length);
    }

    public void sendStandardBloodDataTT(int firstData,int secondData ){
        byte [] send =new byte[3];
        send[0] = (byte)0x04;
        send[1] = (byte)firstData;
        send[2] = (byte)secondData;
    }

}
