package com.huichenghe.bleControl.Ble;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huichenghe.bleControl.Utils.FormatUtils;

/**
 * Created by lixiaoning on 2016/5/9.
 */
public class BleForFindDevice extends BleBaseDataManage
{
    public final String TAG = BleForFindDevice.class.getSimpleName();

    public static byte fromDevice = (byte)0x93;
    public static byte toDevice = (byte)0x13;
    private DataSendCallback onsendOk;
    private static BleForFindDevice mBleForFindDevice;
    private final int FIND_DEVICE_INFO = 0;
    private Boolean isSendOk = false;
    private Boolean isSendStart = false;
    private int sendConut = 0;
    private BleForFindDevice(){};
    public static BleForFindDevice getBleForFindDeviceInstance()
    {
        if(mBleForFindDevice == null)
        {
            synchronized (BleForFindDevice.class)
            {
                if(mBleForFindDevice == null)
                {
                    mBleForFindDevice = new BleForFindDevice();
                }
            }
        }
        return mBleForFindDevice;
    }

    private Handler findHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case FIND_DEVICE_INFO:
                    if(isSendOk)
                    {
                        stopFind(this);
                    }
                    else
                    {
                        if(sendConut < 4)
                        {
                            continueFind(this, msg);
                            sendToStartFindDevice(msg.getData().getByte("comm"));
                        }
                        else
                        {
                            stopFind(this);
                        }
                    }
                    break;
            }
        }
    };



    private void continueFind(Handler handler, Message msg)
    {
        Message mzg = handler.obtainMessage();
        mzg.what = FIND_DEVICE_INFO;
        mzg.arg1 = msg.arg1;
        mzg.arg2 = msg.arg2;
        mzg.setData(msg.getData());
        handler.sendMessageDelayed(mzg, SendLengthHelper.getSendLengthDelay(msg.arg1, msg.arg2));
        sendConut ++;
    }

    private void stopFind(Handler handler)
    {
        handler.removeMessages(FIND_DEVICE_INFO);
        if(!isSendOk)
        {
            onsendOk.sendFailed();
        }
        onsendOk.sendFinished();
        isSendOk = false;
        sendConut = 0;
    }

    public void findConnectedDevice(byte comm)
    {
        isSendStart = true;
        int sendLg = sendToStartFindDevice(comm);
        Message msg = findHandler.obtainMessage();
        msg.what = FIND_DEVICE_INFO;
        msg.arg1 = sendLg;
        msg.arg2 = 20;
        Bundle bu = new Bundle();
        bu.putByte("comm", comm);
        msg.setData(bu);
        findHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(sendLg, 20));
    }

    private int sendToStartFindDevice(byte comm)
    {
        byte[] data = new byte[1];
        data[0] = comm;
        return setMsgToByteDataAndSendToDevice(toDevice, data, data.length);
    }


    public void dealTheResponseData(byte[] backData)
    {
        if(isSendStart)
        {
            isSendStart = false;
            //        68 93 0100 00 fc16
            isSendOk = true;
            if(backData[0] == (byte)0x00)
            {
                if(onsendOk != null)
                {
                    onsendOk.sendSuccess(backData);
                }
            }
            if(backData[1] == (byte)0x00)
            {
            }
        }
    }


    public void setListener(DataSendCallback listener)
    {
        this.onsendOk = listener;
    }


}
