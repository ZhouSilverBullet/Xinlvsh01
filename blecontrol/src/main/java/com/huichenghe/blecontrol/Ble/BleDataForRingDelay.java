package com.huichenghe.blecontrol.Ble;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

/**
 * Created by lixiaoning on 16-clock-sitting.
 */
public class BleDataForRingDelay extends BleBaseDataManage
{
    public static final String TAG = BleDataForRingDelay.class.getSimpleName();
    private static final byte toDevice = (byte)0x12;
    public static final byte fromDevice = (byte)0x92;
    private static BleDataForRingDelay mBleDataDelay;
    private onDelayDataRecever mRecever;
    private final int SEND_TO_GET_RING_DELAY = 0;
    private final int SETTING_DELAY_DATA = 1;
    private int count = 0;
    private boolean isBack = false;
    private BleDataForRingDelay(){};

    public interface onDelayDataRecever
    {
        void onRecever(int delayTime);
    }
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
                            continueSendData(this);
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
                            continueSetting(this, msg.arg1);
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

    private void continueSetting(Handler handler, int arg1)
    {
        Message msg = Message.obtain();
        msg.what = SETTING_DELAY_DATA;
        msg.arg1 = arg1;
        handler.sendMessageDelayed(msg, 60);
    }

    private void closeSetting(Handler handler)
    {
        handler.removeMessages(SETTING_DELAY_DATA);
        isBack = false;
        count = 0;
    }

    private void continueSendData(Handler handler)
    {
        handler.sendEmptyMessageDelayed(SEND_TO_GET_RING_DELAY, 60);
        count ++;
    }

    private void closeSendData(Handler handler)
    {
        handler.removeMessages(SEND_TO_GET_RING_DELAY);
        isBack = false;
        count = 0;
    }

    public void addListener(onDelayDataRecever re)
    {
        this.mRecever = re;
    }

    public static synchronized BleDataForRingDelay getDelayInstance()
    {
        if(mBleDataDelay == null)
        {
            mBleDataDelay = new BleDataForRingDelay();
        }
        return mBleDataDelay;
    }


    public void getDelayData()
    {
        sendToGetTheDealyData();
        ringHandler.sendEmptyMessageDelayed(SEND_TO_GET_RING_DELAY, 80);
    }


    private void sendToGetTheDealyData()
    {
        byte[] data = new byte[]{0x02};
        setMsgToByteDataAndSendToDevice(toDevice, data, data.length);
    }


    public void dealTheDelayData(Context mContexts, byte[] bufferTmp)
    {
//        02060416
//        Log.i(TAG, "电话延时数据：" + FormatUtils.bytesToHexString(bufferTmp));
        Intent data = null;
        byte type = bufferTmp[0];
        if(type == (byte)0x02)
        {
            isBack = true;
            byte delayTime = bufferTmp[1];
//            data = new Intent();
//            data.setAction(MyConfingInfo.BROADCAST_DELAY_DATA);
//            data.putExtra(MyConfingInfo.DELAY_DATA, time);
//            mContexts.sendBroadcast(data);
            if(mRecever != null)
            {
//                Log.i(TAG, "电话延时数据mRecever：" + FormatUtils.bytesToHexString(bufferTmp));
                mRecever.onRecever(delayTime);
            }
        }
        else if(type == (byte)0x01)
        {
//            sendToGetTheDealyData();
            isBack = true;
        }
    }


    public void settingDelayData(int second)
    {
        settingTheRingDelay(second);
        Message msg = Message.obtain();
        msg.what = SETTING_DELAY_DATA;
        msg.arg1 = second;
        ringHandler.sendMessageDelayed(msg, 80);
    }
    private void settingTheRingDelay(int second)
    {

        byte[] bytes = new byte[2];
        bytes[0] = (byte)0x01;
        bytes[1] = (byte)(second & 0xff);
        setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
    }

}
