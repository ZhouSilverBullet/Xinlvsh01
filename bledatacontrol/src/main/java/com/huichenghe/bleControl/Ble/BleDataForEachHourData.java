package com.huichenghe.bleControl.Ble;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.huichenghe.bleControl.Utils.FormatUtils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class BleDataForEachHourData extends BleBaseDataManage
{
    private static final String TAG = BleDataForEachHourData.class.getSimpleName();
    public static final byte fromDevice = (byte)0xa6;
    public static final byte toDevice = (byte)0x26;
    public static BleDataForEachHourData bleDataForEachHourData;
    private final int GET_EACH_HOUR_DATA = 0;
    private boolean isSendOk = false;
    private boolean isComm = false;
    private int sendCount = 0;
    private DataSendCallback sendCallback;



//    public final String patten = "yyyy-MM-dd";

    private BleDataForEachHourData(){ }

    public static BleDataForEachHourData getEachHourDataInstance()
    {
        if(bleDataForEachHourData == null)
        {
            synchronized (BleDataForEachHourData.class)
            {
                if(bleDataForEachHourData == null)
                {
                    bleDataForEachHourData = new BleDataForEachHourData();
                }
            }
        }
        return bleDataForEachHourData;
    }
    private Handler eachHandler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case GET_EACH_HOUR_DATA:
                    if(isSendOk)
                    {
                        stopSend(this);
                    }
                    else
                    {
                        if(sendCount < 4)
                        {
                            continueSendData(this, msg);
                            getTheEcchHourData();
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

    private void continueSendData(Handler handler, Message msg)
    {
        Message msges = handler.obtainMessage();
        msges.what = GET_EACH_HOUR_DATA;
        msges.arg1 = msg.arg1;
        msges.arg2 = msg.arg2;
        handler.sendMessageDelayed(msges, SendLengthHelper.getSendLengthDelay(msg.arg1, msg.arg2));
        sendCount++;
    }

    private void stopSend(Handler handler)
    {
        handler.removeMessages(GET_EACH_HOUR_DATA);
        if(!isSendOk)
        {
            sendCallback.sendFailed();
        }
        sendCallback.sendFinished();
        isSendOk = false;
        sendCount = 0;
    }


    public void getEachData()
    {
        isComm = true;
        int sendLength = getTheEcchHourData();
        Message msg = eachHandler.obtainMessage();
        msg.what = GET_EACH_HOUR_DATA;
        msg.arg1 = sendLength;
        msg.arg2 = 300;
        int recLg = SendLengthHelper.getSendLengthDelay(sendLength, 300);
        eachHandler.sendMessageDelayed(msg, recLg);
    }

    private int getTheEcchHourData()
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR) - 2000;
        byte[] bytes = new byte[4];
        bytes[0] = (byte)(day & 0xff);
        bytes[1] = (byte)(month & 0xff);
        bytes[2] = (byte)(year & 0xff);
        bytes[3] = (byte)0x01;
        return setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
    }


    public void dealTheEachData(byte[] eachData)
    {
        if(isComm)
        {
            isSendOk = true;
            isComm = false;
            if(sendCallback != null)
            {
                sendCallback.sendSuccess(eachData);
            }
            else
            {
            }
        }

        Log.i(TAG, "每小时数据：" + FormatUtils.bytesToHexString(eachData));
        int dayNow = eachData[0] & 0xff;
        int monthNow = eachData[1] & 0xff;
        int yearNow = (eachData[2] & 0xff) + 2000;
        String dayS = formatTheDataDate(yearNow, monthNow, dayNow);
        Date dates = Calendar.getInstance(TimeZone.getDefault()).getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateCurrent = format.format(dates);
        if(dateCurrent != null && dayS != null && !dateCurrent.equals(dayS))
        {
            byte[] backData = new byte[4];
            for (int i = 0; i < backData.length; i ++)
            {
                backData[i] = eachData[i];
            }
            setMsgToByteDataAndSendToDevice(toDevice, backData, backData.length);
            if(sendCallback != null)
            {
                sendCallback.sendSuccess(eachData);
            }
            else
            {
            }
        }
    }

    private String formatTheDataDate(int yearNow, int monthNow, int dayNow)
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.set(yearNow, monthNow - 1, dayNow);
        Date dateF = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(dateF);
    }



    public void setOnBleDataReceListener(DataSendCallback dataCallback)
    {
        this.sendCallback = dataCallback;
    }


}
