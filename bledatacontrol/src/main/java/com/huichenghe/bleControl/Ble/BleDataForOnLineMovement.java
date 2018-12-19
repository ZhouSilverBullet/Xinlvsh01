package com.huichenghe.bleControl.Ble;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huichenghe.bleControl.Utils.FormatUtils;

/**
 * Created by lixiaoning on 2016/10/9.
 */
public class BleDataForOnLineMovement extends BleBaseDataManage
{
    public final String TAG = BleDataForOnLineMovement.class.getSimpleName();
    public static final byte toDevice = (byte)0x06;
    public static final byte fromDevice = (byte)0x86;
    private boolean isReceverSwitch = false;
    private int sendCount = 0;
    private boolean hasSendOneSwitch = false;
    private boolean isReceverData = false;
    private boolean hasSendOneData = false;
    private int sendDataCount = 0;
    private final int SEND_DATA_COMM = 0;
    private final int SEND_DATA_COMM_SWITCH = 1;
    private static BleDataForOnLineMovement onLineMovement;
    private BleDataForOnLineMovement(){};
    public static BleDataForOnLineMovement getBleDataForOutlineInstance()
    {
        if(onLineMovement == null)
        {
            synchronized (BleDataForOnLineMovement.class)
            {
                if(onLineMovement == null)
                {
                    onLineMovement = new BleDataForOnLineMovement();
                }
            }
        }
        return onLineMovement;
    }

    private Handler onLineHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case SEND_DATA_COMM:
                    if(isReceverData)
                    {
                        closeSend(SEND_DATA_COMM, this, msg);
                    }
                    else
                    {
                        if(sendDataCount < 4)
                        {
                            onLineHRComm((byte)msg.arg2);
                            continueSend(SEND_DATA_COMM, this, msg);
                            sendDataCount++;
                        }
                        else
                        {
                            closeSend(SEND_DATA_COMM, this, msg);
                            sendDataCount = 0;
                            isReceverData = false;
                        }
                    }
                    break;
                case SEND_DATA_COMM_SWITCH:
                    if(isReceverSwitch)
                    {
                        closeSend(SEND_DATA_COMM_SWITCH, this, msg);
                        isReceverSwitch = false;
                        sendCount = 0;
                    }
                    else
                    {
                        if(sendCount < 4)
                        {
                            onLineHRComm((byte)msg.arg2);
                            continueSend(SEND_DATA_COMM_SWITCH, this, msg);
                            sendCount++;
                        }
                        else
                        {
                            closeSend(SEND_DATA_COMM_SWITCH, this, msg);
                            isReceverSwitch = false;
                            sendCount = 0;
                        }
                    }
                    break;
            }
        }
    };

    private void continueSend(int send_data_comm, Handler handler, Message msg)
    {
        Message mzg = handler.obtainMessage();
        mzg.what = send_data_comm;
        mzg.arg1 = msg.arg1;
        mzg.arg2 = msg.arg2;
        handler.sendMessageDelayed(mzg, SendLengthHelper.getSendLengthDelay(msg.arg1, 20));
    }

    private void closeSend(int send_data_comm, Handler handler, Message msg)
    {
        handler.removeMessages(send_data_comm);
        sendCallback.sendFinished();
//        if(!rec)
//        {
//            sendCallback.sendFailed();
//        }
    }

    public void sendHRDataToDevice(byte comm)
    {
        Message msg = onLineHandler.obtainMessage();
        int len = onLineHRComm(comm);
        msg.arg1 = len;
        msg.arg2 = comm;
        if(comm == 0)
        {
            hasSendOneData = true;
            msg.what = SEND_DATA_COMM;
        }
        else
        {
            hasSendOneSwitch = true;
            msg.what = SEND_DATA_COMM_SWITCH;
        }
        onLineHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(len, 200));
    }

    private int onLineHRComm(byte comm)
    {
        byte[] bytes = new byte[]{comm};
        return setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
    }

    public void dealOnlineHRMonitor(byte[] bufferTmp)
    {

        byte comment = bufferTmp[0];
        if(comment == 0)
        {
            if(hasSendOneData)
            {
                isReceverData = true;
                hasSendOneData = false;
                sendCallback.sendSuccess(bufferTmp);
            }
        }
        else
        {
            if(hasSendOneSwitch)
            {
                isReceverSwitch = true;
                hasSendOneSwitch = false;
                sendCallback.sendSuccess(bufferTmp);
            }
        }
    }
    private DataSendCallback sendCallback;
    public void setOnSendRecever(DataSendCallback callback)
    {
        this.sendCallback = callback;
    }
}
