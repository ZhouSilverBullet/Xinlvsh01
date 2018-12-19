package com.huichenghe.bleControl.Ble;

import android.content.Context;
import android.database.Cursor;
import android.icu.util.IslamicCalendar;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by lixiaoning on 15-12-sos.
 */
public class BleDataForSleepData extends BleBaseDataManage
{
    public static final String TAG = BleDataForSleepData.class.getSimpleName();
    private Context mContext;
    public static final byte toDevice = (byte)0x26;
    public static final byte fromDevice = (byte)0xa6;
    private DataSendCallback callback;
    private static volatile BleDataForSleepData instance = null;
    private boolean isBack = false;
    private boolean isComm = false;
    private int count = 0;
    private final int GET_SLEEP_DATA = 0;
    private Handler sleepHandler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case GET_SLEEP_DATA:
                    if(isBack)
                    {
                        closeSendData(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            continueSendData(this, msg.arg1, msg.arg2);
                            getTodaySleepdata();
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

    private void continueSendData(Handler handler, int length, int reLength)
    {
        Message msg = handler.obtainMessage();
        msg.what = GET_SLEEP_DATA;
        msg.arg1 = length;
        msg.arg2 = reLength;
        handler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(length, reLength));
        count ++;
    }

    private void closeSendData(Handler handler)
    {
        handler.removeMessages(GET_SLEEP_DATA);

        if(callback != null)
        {
            if(!isBack)
            {
                callback.sendFailed();
            }
            callback.sendFinished();
        }
        else
        {
        }
        isBack = false;
        count = 0;
    }

    public static BleDataForSleepData getInstance(Context context)
    {
        if(instance == null)
        {
            synchronized (BleDataForSleepData.class)
            {
                if(instance == null)
                {
                    instance = new BleDataForSleepData(context);
                }
            }
        }
        return instance;
    }

    private BleDataForSleepData(Context mContext)
    {
        this.mContext = mContext;
    }

    private int[] getTodayDate()
    {
        int[] dates = new int[3];
        Calendar calendarCurrent = Calendar.getInstance(Locale.getDefault());
        dates[0] = calendarCurrent.get(Calendar.DAY_OF_MONTH);
        dates[1] = calendarCurrent.get(Calendar.MONTH) + 1;
        dates[2] = calendarCurrent.get(Calendar.YEAR) - 2000;
        return dates;
    }

    public void getSleepingData()
    {
        isComm = true;
        int datalength = getTodaySleepdata();
        Message msg = sleepHandler.obtainMessage();
        msg.what = GET_SLEEP_DATA;
        msg.arg1 = datalength;
        msg.arg2 = 46;
        sleepHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(datalength, 46));
    }

    private int getTodaySleepdata()
    {
        int[] dates = getTodayDate();
        byte[] bytes = new byte[4];
        bytes[0] = (byte)(dates[0] & 0xff);
        bytes[1] = (byte)(dates[1] & 0xff);
        bytes[2] = (byte)(dates[2] & 0xff);
        bytes[3] = (byte)0x02;
        return setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
    }

    public void getTodaySleepdataAndCallback()
    {
        int[] dates = getTodayDate();
        byte[] bytes = new byte[4];
        bytes[0] = (byte)(dates[0] & 0xff);
        bytes[1] = (byte)(dates[1] & 0xff);
        bytes[2] = (byte)(dates[2] & 0xff);
        bytes[3] = (byte)0x02;

        setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
    }


    public void setOnSleepDataRecever(DataSendCallback callback)
    {
        this.callback = callback;
    }

    public void dealTheSleepData(byte[] bufferTmp)
    {
        if(isComm)
        {
            isBack = true;
            isComm = false;
            if(callback == null)
            {
                Log.e(TAG, "bleDataForSleepData callback 为空");
            }
            else
            {
                callback.sendSuccess(bufferTmp);
            }
        }

        int d = bufferTmp[0] & 0xff;
        int m = bufferTmp[1] & 0xff;
        int y = bufferTmp[2] & 0xff;
        Calendar calendarFromSleepData = Calendar.getInstance(TimeZone.getDefault());
        calendarFromSleepData.set(y + 2000, m - 1, d);
        Date dateOne = calendarFromSleepData.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String curr = format.format(dateOne);
        Calendar calendarsLocal = Calendar.getInstance(TimeZone.getDefault());
        Date dates = calendarsLocal.getTime();
        SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateSleep = formats.format(dates);

        Log.i(TAG, "时间对比：" + curr + "--" + dateSleep);
        if(curr != null && !curr.equals("") && dateSleep != null && !dateSleep.equals(""))
        {
            if(!curr.equals(dateSleep))
            {
                byte[] backData = new byte[4];
                for (int i = 0; i < backData.length; i++)
                {
                    backData[i] = bufferTmp[i];
                }
                setMsgToByteDataAndSendToDevice(toDevice, backData, backData.length);
                if(callback == null)
                {
                    Log.e(TAG, "bleDataForSleepData callback 为空");
                }
                else
                {
                    callback.sendSuccess(bufferTmp);
                }
            }
        }
    }
}
