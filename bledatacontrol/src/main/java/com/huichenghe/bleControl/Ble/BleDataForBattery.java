package com.huichenghe.bleControl.Ble;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class BleDataForBattery extends BleBaseDataManage
{
    private static int mCurrentBattery = -1;
    private DataSendCallback battListerer;
    private static BleDataForBattery bleBAttery;
    private boolean hasComm = false;
    private final int GET_BLE_BATTERY = 0;
    private boolean isSendOk = false;
    private int sendCount = 0;

    public static byte mReceCmd = 0x03;
    public static byte fromCmd = (byte)0x83;

    public static BleDataForBattery getInstance()
    {
        if(bleBAttery == null)
        {
            synchronized (BleDataForBattery.class)
            {
                if(bleBAttery == null)
                {
                    bleBAttery = new BleDataForBattery();
                }
            }
        }
        return bleBAttery;
    }

    private Handler batteryHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if(isSendOk)
            {
                stopSendData(this);
            }
            else
            {
                if(sendCount < 4)
                {
                    continueSend(this, msg);
                    getBatteryFromBr();
                }
                else
                {
                    stopSendData(this);
                }
            }
        }
    };

    private void continueSend(Handler handler, Message msges)
    {
        Message msg = handler.obtainMessage();
        msg.arg1 = msges.arg1;
        msg.arg2 = msges.arg2;
        handler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(msges.arg1, msges.arg2));
        sendCount ++;
    }

    private void stopSendData(Handler handler)
    {
        handler.removeMessages(GET_BLE_BATTERY);
        if(!isSendOk)
        {
            battListerer.sendFailed();
        }
        battListerer.sendFinished();
        isSendOk = false;
        sendCount = 0;
    }


    private BleDataForBattery()
    {

//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        setMessageDataByString(mReceCmd, null, true);
    }


    public void getBatteryPx()
    {
        int sendLength = getBatteryFromBr();
        Message msg = batteryHandler.obtainMessage();
        msg.what = GET_BLE_BATTERY;
        msg.arg1 = sendLength;
        msg.arg2 = 7;
        batteryHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(sendLength, 7));
    }


    private int getBatteryFromBr()
    {
        return setMessageDataByString(mReceCmd, null, true);
    }


    public void dealReceData(Context mContext, byte[] data, int dataLength)
    {
        isSendOk = true;
        battListerer.sendSuccess(data);
    }

    public static int getmCurrentBattery()
    {
        return mCurrentBattery;
    }




    public void setBatteryListener(DataSendCallback batteryCallback)
    {
        this.battListerer = batteryCallback;
    }

}
