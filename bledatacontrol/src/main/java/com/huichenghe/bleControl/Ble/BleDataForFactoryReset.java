package com.huichenghe.bleControl.Ble;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.ServiceWorkerClient;


public class BleDataForFactoryReset extends BleBaseDataManage
{
    private static final String TAG = BleDataForFactoryReset.class.getSimpleName();
    public static final byte toDevice = (byte)0x11;
    public static final byte fromDevice = (byte)0x91;
    private final int FACTORY_RESET = 0;
    private boolean isSendOk = false;
    private int sendCount = 0;
    private static BleDataForFactoryReset bleDataForFactoryReset = null;
    public static BleDataForFactoryReset getBleDataInstance()
    {
        if(bleDataForFactoryReset == null)
        {
            synchronized (BleDataForFactoryReset.class)
            {
                if(bleDataForFactoryReset == null)
                {
                    bleDataForFactoryReset = new BleDataForFactoryReset();
                }
            }
        }
        return bleDataForFactoryReset;
    }

    private BleDataForFactoryReset(){};


    private Handler factoryHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case FACTORY_RESET:
                    if(isSendOk)
                    {
                        stopSend(this);
                    }
                    else
                    {
                        if(sendCount < 4)
                        {
                            continueSend(this, msg);
                            factoryReset();
                        }
                        else
                        {
                            stopSend(this);
                        }
                    }


                    break;
            }
        }
    };

    private void continueSend(Handler handler, Message msg)
    {
        Message mzg = handler.obtainMessage();
        mzg.what = FACTORY_RESET;
        mzg.arg1 = msg.arg1;
        mzg.arg2 = msg.arg2;
        handler.sendMessageDelayed(mzg, SendLengthHelper.getSendLengthDelay(msg.arg1, msg.arg2));
        sendCount ++;
    }

    private void stopSend(Handler handler)
    {
        handler.removeMessages(FACTORY_RESET);
        if(resetCallback != null)
        {
            if(!isSendOk)
            {
                 resetCallback.sendFailed();
            }
            resetCallback.sendFinished();
        }
        isSendOk = false;
        sendCount = 0;
    }


    public void settingFactoryReset()
    {
        int sendLg = factoryReset();
        Message msg = factoryHandler.obtainMessage();
        msg.what = FACTORY_RESET;
        msg.arg1 = sendLg;
        msg.arg2 = 10;
        factoryHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(sendLg, 10));
    }

    private int factoryReset()
    {
        byte[] bytes = new byte[1];
        bytes[0] = (byte)0x01;
        return setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
    }


    public void dealTheResult(Context mContext, byte[] bufferTmp)
    {
//        0100 fc16
        isSendOk = true;
        byte dataResult = bufferTmp[1];
        String result = "";
        if(bufferTmp[0] == (byte)0x01)
        {
            if(dataResult == (byte)0x00)
            {
                if(resetCallback != null)
                {
                    resetCallback.sendSuccess(bufferTmp);
                }
            }
            else if(dataResult == (byte)0x01)
            {
                factoryReset();
//                result = mContext.getString(R.string.factory_reset_failed);
            }
        }
    }

    private DataSendCallback resetCallback;
    public void setFactoryResetListener(DataSendCallback resetCallback)
    {
        this.resetCallback = resetCallback;
    }
}
