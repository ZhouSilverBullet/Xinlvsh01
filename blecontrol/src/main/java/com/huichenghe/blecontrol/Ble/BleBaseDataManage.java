package com.huichenghe.blecontrol.Ble;

import android.util.Log;

import com.huichenghe.blecontrol.Utils.FormatUtils;

/**
 * 此类为ble数据处理的基类，负责整合数据，完成格式的整理
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










    // 一个完整的数据包格式
    //  68       xx(1b)       xxxx(2b)       xx(由实际长度而定)         xx(1b)    xx(1b)
    // 包头      功能码        数据长度         数据域                  校验码     尾帧


    private byte msg_head = 0x68;
    private byte msg_cmd = 0x00;
    private int msg_data_length = 0;
    private byte[] msg_data;
    private byte msg_check_value = 0x16;


    /**
     * 此方法将以上所有项，整合到一个byte数组，以待发送
     * @return
     */
    public byte[] getSendByteArray()
    {
        byte[] sendData = new byte[6 + msg_data_length];    // 创建一个长度为整条数据总长的byte数组
        int count_for_check = 0;                            // 校验码的计数变量

        for (int i = 0; i < sendData.length; i ++)
        {
            if(0 == i)      // 包头，数据的第一个字节
            {
                sendData[i] = msg_head;
                count_for_check += sendData[i];
            }
            else if(1 == i) // 功能码，数据的第二个字节
            {
                sendData[i] = msg_cmd;
                count_for_check += sendData[i];
            }
            else if(2 == i) // 数据长度的第一个字节，整条数据的第三个字节
            {
                sendData[i] = (byte)(msg_data_length & 0xff);       // int为32位，取最后八位
                count_for_check += sendData[i];
            }
            else if(3 == i) // 数据长度的第二个字节
            {
                sendData[i] = (byte)((msg_data_length >> 8) & 0xff);// int向右移动8位，取右边第二个八位
                count_for_check += sendData[i];
            }
            else if((i >= 4) && (i < (msg_data_length + 4)))        // 数据域,范围是从第5个字节开始，到数据长度加上前边的四个字节的位置
            {
                sendData[i] = msg_data[i - 4];
                count_for_check += sendData[i];
            }
            else if(i == 4 + msg_data_length)                       // 校验码，整条数据的倒数第二个字节
            {
                sendData[i] = (byte)(count_for_check % 256);        // 各个字节相加，对256取余
            }
            else if(i == 5 + msg_data_length)
            {
                byte msg_tail = 0x16;
                sendData[i] = msg_tail;
            }
        }
        Log.i("", "整合后的数据getSendByteArray" + FormatUtils.bytesToHexString(sendData));
        return sendData;
    }


    /**
     * 设置功能码，数据域，和数据长度，并发送to设备
     * @param cmd
     * @param long2ByteData
     * @param dataLength
     */
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
        // 若为true,则发送数据
       if((true))
       {
            // 先整合数据，然后添加到线程
            datas = getSendByteArray();
            BleByteDataSendTool.getInstance().judgeTheDataLengthAndAddToSendByteArray(datas);
       }
        return length = datas.length;
    }


    /**
     * 设置功能码，数据域，和数据长度，并发送to设备
     * @param cmd
     * @param data      十六进制字符串
     * @param isNeedSend
     */
    public void setMessageDataByString(byte cmd, String data, boolean isNeedSend)
    {
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
            BleByteDataSendTool.getInstance().judgeTheDataLengthAndAddToSendByteArray(getSendByteArray());
        }
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
