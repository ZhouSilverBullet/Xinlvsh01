package com.huichenghe.bleControl.Ble;

import android.util.Log;

import com.huichenghe.bleControl.Utils.FormatUtils;

/**
 * Created by lixiaoning on 15-11-sosPhone.
 */
public class BleBaseDataManage
{
//    public final int SEND_DATA_FLAG = 0;
//    public boolean sendOkFlag = false;
//    private int sendCount = 0;
//    public Handler sendHandler = new Handler(Looper.getMainLooper())
//    {
//        @Override
//        public void handleMessage(Message msg)
//        {
//            super.handleMessage(msg);
//            if(sendOkFlag)
//            {
//                stopSend(this);
//            }
//            else
//            {
//                if(sendCount < 4)
//                {
//                    continueSend(msg);
//                }
//                else
//                {
//                    stopSend(this);
//                }
//            }
//        }
//    };
//
//    public void stopSend(Handler handler)
//    {
//        handler.removeMessages(SEND_DATA_FLAG);
//        sendOkFlag = false;
//        sendCount = 0;
//    }
//
//
//    public void continueSend(Message msg)
//    {
//        sendCount ++;
//    }

    private byte msg_head = 0x68;
    private byte msg_cmd = 0x00;
    private int msg_data_length = 0;
    private byte[] msg_data;
    private byte msg_check_value = 0x16;


    public byte[] getSendByteArray()
    {
        byte[] sendData = new byte[6 + msg_data_length];
        int count_for_check = 0;

        for (int i = 0; i < sendData.length; i ++)
        {
            if(0 == i)
            {
                sendData[i] = msg_head;
                count_for_check += sendData[i];
            }
            else if(1 == i)
            {
                sendData[i] = msg_cmd;
                count_for_check += sendData[i];
            }
            else if(2 == i)
            {
                sendData[i] = (byte)(msg_data_length & 0xff);
                count_for_check += sendData[i];
            }
            else if(3 == i) // 数据长度的第二个字节
            {
                sendData[i] = (byte)((msg_data_length >> 8) & 0xff);
                count_for_check += sendData[i];
            }
            else if((i >= 4) && (i < (msg_data_length + 4)))
            {
                sendData[i] = msg_data[i - 4];
                count_for_check += sendData[i];
            }
            else if(i == 4 + msg_data_length)
            {
                sendData[i] = (byte)(count_for_check % 256);
            }
            else if(i == 5 + msg_data_length)
            {
                byte msg_tail = 0x16;
                sendData[i] = msg_tail;
            }
        }
        return sendData;
    }


    public int setMsgToByteDataAndSendToDevice(byte cmd, byte[] long2ByteData, int dataLength)
    {
        int length = 10;
        byte[] datas;
        msg_cmd = cmd;
        if(long2ByteData != null)
        {
            msg_data_length = dataLength;
            msg_data = long2ByteData;
        }
       if((true))
       {
            datas = getSendByteArray();
           if(cmd==BleDataForFrame.toDevice){
               Log.i("ssss", "huanhghhh" + FormatUtils.bytesToHexString(datas));
           }
            BleByteDataSendTool.getInstance().judgeTheDataLengthAndAddToSendByteArray(datas);
       }
        return length = datas.length;
    }


    public int setMessageDataByString(byte cmd, String data, boolean isNeedSend)
    {
        byte[] bytes = null;
        msg_cmd = cmd;
        if(data != null)
        {
            if(data.length() == 1)
            {
                data = "0" + data;
            }
            msg_data_length = data.length()/2;
            msg_data = FormatUtils.hexString2ByteArray(data);
        }
        if(isNeedSend)
        {
            bytes = getSendByteArray();
            BleByteDataSendTool.getInstance().judgeTheDataLengthAndAddToSendByteArray(bytes);
        }
        return bytes.length;
    }

    //    public void setDateToHardwareUpdate(byte cmd, byte[] data, int dataLength, boolean isSend)
//    {
//        msg_cmd = cmd;
//        if(data != null)
//        {
//            msg_data = data;
//        }
//
//        if(isSend)
//        {
//            new UpdateHelper().sendToDevice(getSendByteArray());
//        }
//    }
}
