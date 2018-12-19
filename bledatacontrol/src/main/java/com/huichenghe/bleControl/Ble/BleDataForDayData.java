package com.huichenghe.bleControl.Ble;

import android.content.Context;
import android.content.Intent;
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

public class BleDataForDayData extends BleBaseDataManage
{
    public static final String TAG = BleDataForDayData.class.getSimpleName();
    private Context context;
    public static byte fromDevice = (byte)0xa6;
    public static byte toDevice = (byte)0x26;
    private static BleDataForDayData bleDataForDayData;
    private boolean isSendOk = false;
    private boolean hasComm = false;
    private int sendCount = 0;
    private final int GET_DAY_DATA_HANDLER = 0;
    private DataSendCallback dayCallback;

    private Handler dayHandler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case GET_DAY_DATA_HANDLER:
                    if(isSendOk)
                    {
                        stopSendData(this);
                    }
                    else
                    {
                        if(sendCount < 4)
                        {
                            continueSend(this, msg);
                            requestDayDate();
                        }
                        else
                        {
                            stopSendData(this);
                        }
                    }
                    break;
            }
        }
    };

    private void continueSend(Handler handler, Message msges)
    {
        Message msg = handler.obtainMessage();
        msg.what = GET_DAY_DATA_HANDLER;
        msg.arg1 = msges.arg1;
        msg.arg2 = msges.arg2;
        handler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(msges.arg1, msges.arg2));
        sendCount ++;
    }

    private void stopSendData(Handler handler)
    {
        handler.removeMessages(GET_DAY_DATA_HANDLER);
        if(!isSendOk)
        {   if(dayCallback != null)
            {
                dayCallback.sendFailed();
            }
        }
        if(dayCallback != null)
        dayCallback.sendFinished();
        isSendOk = false;
        sendCount = 0;
    }


    private BleDataForDayData(Context context)
    {
        this.context = context;
    }

    public static BleDataForDayData getDayDataInstance(Context context)
    {
        if(bleDataForDayData == null)
        {
            synchronized (BleDataForDayData.class)
            {
                if(bleDataForDayData == null)
                {
                    bleDataForDayData = new BleDataForDayData(context);
                }
            }
        }
        return bleDataForDayData;
    }



    public void getDayData()
    {
        hasComm = true;
        int length = requestDayDate();
        Message msg = dayHandler.obtainMessage();
        msg.what = GET_DAY_DATA_HANDLER;
        msg.arg1 = length;
        msg.arg2 = 38;
        dayHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(length, 38));
    }
    private int requestDayDate()
    {
        byte[] reqData = new byte[4];
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        int year = cal.get(Calendar.YEAR);// 获取年份
        int month = cal.get(Calendar.MONTH) + 1;// 获取月份
        int day = cal.get(Calendar.DATE);// 获取日
        reqData[0] = (byte)day;
        reqData[1] = (byte)month;
        reqData[2] = (byte)(year - 2000);
        reqData[3] = (byte)0x00;

        return setMsgToByteDataAndSendToDevice(toDevice, reqData, reqData.length);


    }

    public void juestResponse(byte[] data)
    {
        byte[] responseData = new byte[4];
        System.arraycopy(data, 0, responseData, 0, responseData.length);
        setMsgToByteDataAndSendToDevice(toDevice, responseData, responseData.length);
    }

    public void dealDayData(Context mContext, byte[] data)
    {
        if(hasComm)
        {
            isSendOk = true;
            hasComm = false;
            if(dayCallback != null)
            {
                dayCallback.sendSuccess(data);
            }
            else
            {
            }
        }
        int day = data[0];
        int month = data[1];
        int year = data[2];

        String dataDate = formatTheDate(year + 2000, month, day);
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        Date dateNow = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateC = format.format(dateNow);
        if(dataDate != null && dateC != null && !dataDate.equals(dateC))
        {
            byte[] bytess = new byte[4];
            bytess[0] = (byte)day;
            bytess[1] = (byte)month;
            bytess[2] = (byte)year;
            bytess[3] = (byte)0x00;
            setMsgToByteDataAndSendToDevice(toDevice, bytess, bytess.length);
            if(dayCallback != null)
            {
                dayCallback.sendSuccess(data);
            }
            else
            {
            }
        }
    }

    public void setOnDayDataListener(DataSendCallback sendCallback)
    {
        this.dayCallback = sendCallback;
    }

    private String formatTheDate(int year, int month, int day)
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.set(year, month - 1, day);
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(date);
    }
}
