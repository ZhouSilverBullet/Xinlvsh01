package com.huichenghe.bleControl.Ble;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huichenghe.bleControl.Utils.DateUtils;
import com.huichenghe.bleControl.Utils.FormatUtils;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by lixiaoning on 15-11-sosPhone.
 */
public class BleDataforSyn extends BleBaseDataManage
{
    public static byte back_cmd = (byte)0xA0;
    private static byte send_cmd = (byte)0x20;
    private boolean hasComm = false;
    private static BleDataforSyn bleDataforSyn;
    private final int SYNC_TIME = 0;
    private boolean isBack = false;
    private int count = 0;
    private DataSendCallback dataSendCallback;
    public void setDataSendCallback(DataSendCallback dataSendCallback)
    {
        this.dataSendCallback = dataSendCallback;
    }

    public static BleDataforSyn getSynInstance()
    {
        if(bleDataforSyn == null)
        {
            bleDataforSyn = new BleDataforSyn();
        }
        return bleDataforSyn;
    }

    private Handler synHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case SYNC_TIME:
                    if(isBack)
                    {
                        closeSycn(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            cotinueSycn(this, msg.arg1, msg.arg2);
                            sysnTheTime();
                        }
                        else
                        {
                            closeSycn(this);
                        }
                    }
                    break;
            }
        }
    };

    private void cotinueSycn(Handler handler, int sendLength, int receLength)
    {
        Message msg = handler.obtainMessage();
        msg.what = SYNC_TIME;
        msg.arg1 = sendLength;
        msg.arg2 = receLength;
        handler.sendEmptyMessageDelayed(SYNC_TIME, SendLengthHelper.getSendLengthDelay(sendLength, receLength));
        count ++;
    }

    private void closeSycn(Handler handler)
    {
        handler.removeMessages(SYNC_TIME);
        if(dataSendCallback != null)
        {
            if(!isBack)
            {
                dataSendCallback.sendFailed();
            }
            dataSendCallback.sendFinished();
        }
        isBack = false;
        count = 0;
    }
    private BleDataforSyn(){}
    /**
     * 同步时间
     */
    public int syncCurrentTime()
    {
        int delay = sysnTheTime();
        hasComm = true;
        Message msg = synHandler.obtainMessage();
        msg.what = SYNC_TIME;
        msg.arg1 = delay;
        msg.arg2 = 6;
        synHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(delay, 6));
        return delay;
    }
    private int sysnTheTime()
    {
        Calendar calendar = Calendar.getInstance();
        int off = calendar.get(Calendar.DST_OFFSET)/1000;
        int current = DateUtils.currentTimeSeconds();
        TimeZone tz = TimeZone.getDefault();
        current += tz.getRawOffset()/1000 + off;
        byte[] ret = FormatUtils.int2Byte_LH(current);
        return setMsgToByteDataAndSendToDevice(send_cmd, ret, ret.length);
    }


    public void dealTheResult()
    {
        if(hasComm)
        {
            isBack = true;
            if(dataSendCallback != null)
            {
                dataSendCallback.sendSuccess(null);
            }
            else
            {
                Log.e("", "回调为空");
            }
            hasComm = false;
        }
    }




}
