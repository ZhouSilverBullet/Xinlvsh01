package com.huichenghe.bleControl.Ble;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.huichenghe.bleControl.Utils.FormatUtils;

/**
 * Created by lixiaoning on 15-11-10.
 */
public class BleDataForHardVersion extends BleBaseDataManage
{
    public final static byte send_cmd = (byte)0x07;
    public final static byte fromDevice = (byte)0x87;
    private static String versionString = null;
    private final int GET_HARD_VERSION = 0;
    private boolean isBack = false;
    private boolean hasComm = false;
    private int count = 0;
    private static BleDataForHardVersion bleDataInstance;
    private DataSendCallback dataSendCallback;


    private Handler verHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case GET_HARD_VERSION:
                    if(isBack)
                    {
                        closeSendData(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            coutinueSendData(this, msg);
                            getHardVersion();
                        }
                        else
                        {
                            closeSendData(this);
                        }
                    }
                    break;
            }
        }
    };

    private void coutinueSendData(Handler handler, Message msges)
    {
        Message msg = handler.obtainMessage();
        msg.what = GET_HARD_VERSION;
        msg.arg1 = msges.arg1;
        msg.arg2 = msges.arg2;
        handler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(msges.arg1, msges.arg2));
        count ++;
    }

    private void closeSendData(Handler handler)
    {
        handler.removeMessages(GET_HARD_VERSION);
        isBack = false;
        hasComm = false;
        count = 0;
        dataSendCallback.sendFinished();
    }

    private BleDataForHardVersion(){ }
    public static BleDataForHardVersion getInstance()
    {
        if(bleDataInstance == null)
        {
            synchronized (BleDataForHardVersion.class)
            {
                if(bleDataInstance == null)
                {
                    bleDataInstance = new BleDataForHardVersion();
                }
            }
        }
        return bleDataInstance;
    }

    public void setDataSendCallback(DataSendCallback dataSendCallback)
    {
        this.dataSendCallback = dataSendCallback;
    }


    public void requestHardVersion()
    {
        hasComm = true;
        int length = getHardVersion();
        Message msg = verHandler.obtainMessage();
        msg.what = GET_HARD_VERSION;
        msg.arg1 = length;
        msg.arg2 = 9;
        verHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(length, 9));
    }

    private int getHardVersion()
    {
        return setMessageDataByString(send_cmd, null, true);
    }


    public void dealReceData(Context mContext, byte[] bufferTmp, int dataLen)
    {
        if(hasComm)
        {
            isBack = true;
            hasComm = false;
            dataSendCallback.sendSuccess(bufferTmp);
        }
    }
    public static String getVersionString()
    {
        return versionString;
    }

}
