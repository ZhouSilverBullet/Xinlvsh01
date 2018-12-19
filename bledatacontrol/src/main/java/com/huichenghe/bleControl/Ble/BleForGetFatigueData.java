package com.huichenghe.bleControl.Ble;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by lixiaoning on 2016/5/12.
 */
public class BleForGetFatigueData extends BleBaseDataManage
{
    private final String TAG = BleForGetFatigueData.class.getSimpleName();
    private final byte toDevice = (byte)0x25;
    public static final byte fromDevice = (byte)0xa5;
    public static final byte exceptionDevice = (byte)0xe5;
    private Context context;
    private int year, month, day;
    private final int GET_FATIGUE_DATA = 0;
    private boolean isBack = false;
    private int count = 0;
    private String currentDate = null;
    private boolean hasComm = false;
    private DataSendCallback dataSendCallback;
    private onDeviceException exception;
    private BleForGetFatigueData(Context context)
    {
        this.context = context;
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        year = calendar.get(Calendar.YEAR) - 2000;
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        currentDate = format.format(calendar.getTime());
    };
    private static BleForGetFatigueData bleForGetFatigueData;

    private Handler mHandler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case GET_FATIGUE_DATA:
                if(isBack)
                {
                    closeSendData(this);
                }
                else
                {
                    if(count < 4)
                    {
                        continueSendGetData(this, msg);
                        GetFatigueData();
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

    private void continueSendGetData(Handler handler, Message msg)
    {
        Message mzg = handler.obtainMessage();
        mzg.what = GET_FATIGUE_DATA;
        mzg.arg1 = msg.arg1;
        mzg.arg2 = msg.arg2;
        handler.sendMessageDelayed(mzg, SendLengthHelper.getSendLengthDelay(msg.arg1, msg.arg2));
        count ++ ;
    }

    private void closeSendData(Handler handler)
    {
        handler.removeMessages(GET_FATIGUE_DATA);
        if(dataSendCallback != null)
        {
            if(!isBack)
            {
                dataSendCallback.sendFailed();
            }
            dataSendCallback.sendFinished();
        }
        hasComm = false;
        isBack = false;
        count = 0;
    }

    public static BleForGetFatigueData getInstance(Context context)
    {
        if(bleForGetFatigueData == null)
        {
            synchronized (BleForGetFatigueData.class)
            {
                if(bleForGetFatigueData == null)
                {
                    bleForGetFatigueData = new BleForGetFatigueData(context);
                }
            }
        }
        return bleForGetFatigueData;
    }

    public void setDataSendCallback(DataSendCallback dataSendCallback)
    {
        this.dataSendCallback = dataSendCallback;
    }

    public  void getFatigueDayData()
    {
        hasComm = true;
        int sendLg = GetFatigueData();
        Message msg = mHandler.obtainMessage();
        msg.what = GET_FATIGUE_DATA;
        msg.arg1 = sendLg;
        msg.arg2 = 33;
        mHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(sendLg, 33));
    }

    private int GetFatigueData()
    {
        byte[] by = new byte[3];
        by[0] = (byte)day;
        by[1] = (byte)month;
        by[2] = (byte)year;
        return setMsgToByteDataAndSendToDevice(toDevice, by, by.length);
    }


    public void dealTheResbonseData(byte[] bufferTmp)
    {
//        68 a5 1b00 060910 ffffffffffffffffffffffffff
        if(hasComm)
        {
            isBack = true;
            hasComm = false;
            if(dataSendCallback != null)
            {
                dataSendCallback.sendSuccess(bufferTmp);
            }
            else
            {
                Log.e(TAG, "BleForGetFatigueData 为空");
            }
        }
        int day = bufferTmp[0] & 0xff;
        int month = (bufferTmp[1] & 0xff) - 1;
        int year = (bufferTmp[2] & 0xff) + 2000;
        Calendar calend = Calendar.getInstance(TimeZone.getDefault());
        calend.set(year, month, day);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = format.format(calend.getTime());
        if(date != null && !date.equals(currentDate))
        {
            Log.i(TAG, "是否是当天数据" + date + "--" + currentDate);
            backToDevice(bufferTmp);
            if(dataSendCallback != null)
            {
                dataSendCallback.sendSuccess(bufferTmp);
            }
            else
            {
                Log.e(TAG, "BleForGetFatigueData 为空");
            }
        }
    }


    private void backToDevice(byte[] bufferTmp)
    {
        byte[] backData = new byte[3];
        System.arraycopy(bufferTmp, 0, backData, 0, 3);
        setMsgToByteDataAndSendToDevice(toDevice, backData, backData.length);
    }

    public void dealException()
    {
        if(hasComm)
        {
            isBack = true;
            hasComm = false;
        }
        if(exception != null)
        exception.onException();
    }

    public void setOnDeviceException(onDeviceException exception)
    {
        this.exception = exception;
    }
    public interface onDeviceException
    {
        void onException();
    }
}
