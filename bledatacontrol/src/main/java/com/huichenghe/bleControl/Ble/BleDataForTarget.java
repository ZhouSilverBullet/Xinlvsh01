package com.huichenghe.bleControl.Ble;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.huichenghe.bleControl.Utils.FormatUtils;

/**
 * Created by lixiaoning on 2016/12/23.
 */

public class BleDataForTarget extends BleBaseDataManage
{
    public final static byte toDevice = (byte)0x2d;
    public final static byte fromDevice = (byte)0xad;
    private static BleDataForTarget mBleDataForTarget;
    private final int SEND_TARGET_TO_DEVICE = 0;
    private int sendCount;
    private boolean isSendOK = false;
    private boolean sendActive = false;
    private DataSendCallback sendCallback;
    private BleDataForTarget(){};

    public static BleDataForTarget getInstance()
    {
        if(mBleDataForTarget == null)
        {
            synchronized (BleDataForTarget.class)
            {
                if(mBleDataForTarget == null)
                {
                    mBleDataForTarget = new BleDataForTarget();
                }
            }
        }
        return mBleDataForTarget;
    }


    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case SEND_TARGET_TO_DEVICE:
                    if(isSendOK)
                    {
                        closeSend(this);
                    }
                    else
                    {
                        if(sendCount < 4)
                        {
                            continueSend(this, msg);
                            Bundle bun = msg.getData();
                            sendTargetTo(bun.getInt(stepTargets), bun.getInt(sleepTargets),
                                         bun.getInt(sleepHours), bun.getInt(sleepMinutes));
                        }
                        else
                        {
                            closeSend(this);
                        }
                    }
                    break;
            }
        }
    };

    private void continueSend(Handler handler, Message msg)
    {
        Message mzg = handler.obtainMessage();
        mzg.what = msg.what;
        mzg.setData(msg.getData());
        handler.sendMessageDelayed(mzg, 300);
        sendCount++;
    }

    private void closeSend(Handler handler)
    {
        handler.removeMessages(SEND_TARGET_TO_DEVICE);
        sendCount = 0;
        isSendOK = false;
    }

    private final String stepTargets = "stepTarget";
    private final String sleepTargets = "sleepTarget";
    private final String sleepHours = "sleepHour";
    private final String sleepMinutes = "sleepMinute";

    public void sendTargetToDevice(int stepTarget, int sleepTimes, int sleepHour, int sleepMinute)
    {
        sendActive = true;
        sendTargetTo(stepTarget, sleepTimes, sleepHour, sleepMinute);
        Message msg = handler.obtainMessage();
        msg.what = SEND_TARGET_TO_DEVICE;
        Bundle bundle = new Bundle();
        bundle.putInt(stepTargets, stepTarget);
        bundle.putInt(sleepTargets, sleepTimes);
        bundle.putInt(sleepHours, sleepHour);
        bundle.putInt(sleepMinutes, sleepMinute);
        msg.setData(bundle);
        handler.sendMessageDelayed(msg, 300);
    }


    public void sendTargetTo(int stepTarget, int sleepTimes, int sleepHour, int sleepMinute)
    {
        byte[] datas = new byte[11];
        datas[0] = (byte)0x02;
        byte[] steps = FormatUtils.int2Byte_HL_(stepTarget);
        System.arraycopy(steps, 0, datas, 1, steps.length);
        byte[] sleeps = FormatUtils.int2Byte_HL_(sleepTimes);
        System.arraycopy(sleeps, 0, datas, 5, sleeps.length);
        datas[9] = (byte)sleepMinute;
        datas[10] = (byte)sleepHour;
        setMsgToByteDataAndSendToDevice(toDevice, datas, datas.length);
    }

    public void dealComm(byte[] buffTmp)
    {
        if(buffTmp[0] == 1)
        {
            if(sendActive)
            {
                sendActive = false;
                isSendOK = true;
            }
            else if(sendCount <= 0)
            {
                sendCallback.sendSuccess(null);
            }
        }
    }
    public void setSendCallback(DataSendCallback callback)
    {
        this.sendCallback = callback;
    }
}
