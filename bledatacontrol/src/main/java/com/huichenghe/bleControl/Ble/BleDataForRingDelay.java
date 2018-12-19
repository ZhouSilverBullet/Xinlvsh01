package com.huichenghe.bleControl.Ble;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.sax.EndElementListener;

/**
 * Created by lixiaoning on 16-clock-sitting.
 */
public class BleDataForRingDelay extends BleBaseDataManage
{
    public static final String TAG = BleDataForRingDelay.class.getSimpleName();
    private static final byte toDevice = (byte)0x12;
    public static final byte fromDevice = (byte)0x92;
    private static BleDataForRingDelay mBleDataDelay;
    private DataSendCallback mRecever;
    private final int SEND_TO_GET_RING_DELAY = 0;
    private final int SETTING_DELAY_DATA = 1;
    private int count = 0;
    private boolean isBack = false;
    private BleDataForRingDelay(){};

    private Handler ringHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case SEND_TO_GET_RING_DELAY:
                    if(isBack)
                    {
                        closeSendData(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            continueSendData(this, msg);
                            sendToGetTheDealyData();
                        }
                        else
                        {
                            closeSendData(this);
                        }
                    }
                    break;
                case SETTING_DELAY_DATA:
                    if(isBack)
                    {
                        closeSetting(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            continueSetting(this, msg);
                            settingTheRingDelay(msg.arg1);
                        }
                        else
                        {
                            closeSetting(this);
                        }
                    }
                    break;
            }
        }
    };

    private void continueSetting(Handler handler, Message mzg)
    {
        Message msg = Message.obtain();
        msg.what = SETTING_DELAY_DATA;
        msg.arg1 = mzg.arg1;
        Bundle bundle = mzg.getData();
        msg.setData(bundle);
        handler.sendMessageDelayed(msg,
                SendLengthHelper.getSendLengthDelay(bundle.getInt("send_length"), bundle.getInt("rece_length")));
    }

    private void closeSetting(Handler handler)
    {
        handler.removeMessages(SETTING_DELAY_DATA);
        isBack = false;
        count = 0;
    }

    private void continueSendData(Handler handler, Message mzg)
    {
        Message msg = handler.obtainMessage();
        msg.what = SEND_TO_GET_RING_DELAY;
        msg.arg1 = mzg.arg1;
        msg.arg2 = mzg.arg2;
        handler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(mzg.arg1, mzg.arg2));
        count ++;
    }

    private void closeSendData(Handler handler)
    {
        handler.removeMessages(SEND_TO_GET_RING_DELAY);
        if(mRecever != null)
        {
            if(!isBack)
            {
                mRecever.sendFailed();
            }
            mRecever.sendFinished();
        }
        isBack = false;
        count = 0;
    }

    public void addListener(DataSendCallback re)
    {
        this.mRecever = re;
    }

    public static BleDataForRingDelay getDelayInstance()
    {
        if(mBleDataDelay == null)
        {
            synchronized (BleDataForRingDelay.class)
            {
                if(mBleDataDelay == null)
                {
                    mBleDataDelay = new BleDataForRingDelay();
                }
            }
        }
        return mBleDataDelay;
    }


    public void getDelayData()
    {
        int sendLg = sendToGetTheDealyData();
        Message msg = ringHandler.obtainMessage();
        msg.what = SEND_TO_GET_RING_DELAY;
        msg.arg1 = sendLg;
        msg.arg2 = 8;
        ringHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(sendLg, 8));
    }


    private int sendToGetTheDealyData()
    {
        byte[] data = new byte[]{0x02};
        return setMsgToByteDataAndSendToDevice(toDevice, data, data.length);
    }


    public void dealTheDelayData(Context mContexts, byte[] bufferTmp)
    {
        isBack = true;
        if(bufferTmp[0] == (byte)0x02)
        {
            if(mRecever != null)
            {
                mRecever.sendSuccess(bufferTmp);
            }
        }
    }


    public void settingDelayData(int second)
    {
        int sendLg = settingTheRingDelay(second);
        Message msg = ringHandler.obtainMessage();
        msg.what = SETTING_DELAY_DATA;
        msg.arg1 = second;
        Bundle bundle = new Bundle();
        bundle.putInt("send_length", sendLg);
        bundle.putInt("rece_length", 10);
        ringHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(sendLg, 10));
    }
    private int settingTheRingDelay(int second)
    {

        byte[] bytes = new byte[2];
        bytes[0] = (byte)0x01;
        bytes[1] = (byte)(second & 0xff);
        return setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
    }

}
