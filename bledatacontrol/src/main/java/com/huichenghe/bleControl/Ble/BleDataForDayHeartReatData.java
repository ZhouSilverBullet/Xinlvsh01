package com.huichenghe.bleControl.Ble;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.huichenghe.bleControl.Utils.FormatUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BleDataForDayHeartReatData extends BleBaseDataManage
{

    private static final String TAG = BleDataForDayHeartReatData.class.getSimpleName();
    private Context mContext;
    public static final byte fromDevice = (byte)0xa6;
    public static final byte toDevice = (byte)0x26;
    private final int GET_HEART_WRATE_DATA = 0;
    private final String PACKAGE = "package_int";
    private final String DATA_PACKAGE = "data_pak_send";
    private boolean isSendOk = false;
    private int sendTimes = 0;
    private int packageCount = 0;
    private DataSendCallback hrCallback;
    private static BleDataForDayHeartReatData bleDataForDayHeartReatData;
    private String stringDateFormat;
    private Handler hrHandler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case GET_HEART_WRATE_DATA:
                    if(isSendOk)
                    {
                        stopSendData(this);
                    }
                    else
                    {
                        if(sendTimes < 4)
                        {
                            continueSendData(this, msg);
                            Bundle bundle = msg.getData();
                            byte[] datas = bundle.getByteArray(DATA_PACKAGE);
                            setMsgToByteDataAndSendToDevice(toDevice, datas, datas.length);
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




    private void continueSendData(Handler handler, Message msg)
    {
        Message msges = handler.obtainMessage();
        msges.what = GET_HEART_WRATE_DATA;
        msges.arg1 = msg.arg1;
        msges.arg2 = msg.arg2;
        msges.setData(msg.getData());
        handler.sendMessageDelayed(msges, com.huichenghe.bleControl.Ble.SendLengthHelper.getSendLengthDelay(msg.arg1, msg.arg2));
        sendTimes ++;
    }

    private void stopSendData(Handler handler)
    {
        handler.removeMessages(GET_HEART_WRATE_DATA);
        isSendOk = false;
        sendTimes = 0;
        synchronized (BleDataForDayHeartReatData.this)
        {
            BleDataForDayHeartReatData.this.notify();
        }
        if(packageCount == 1)
        {
            hrCallback.sendFinished();
        }
    }

    public static BleDataForDayHeartReatData getHRDataInstance(Context context)
    {
        if(bleDataForDayHeartReatData == null)
        {
            synchronized (BleDataForDayHeartReatData.class)
            {
                if(bleDataForDayHeartReatData == null)
                {
                    bleDataForDayHeartReatData = new BleDataForDayHeartReatData(context);
                }
            }
        }
        return bleDataForDayHeartReatData;
    }

    private BleDataForDayHeartReatData(Context mContext)
    {
        this.mContext = mContext;
    }

    public void setOnHrDataRecever(DataSendCallback hrCallback)
    {
        this.hrCallback = hrCallback;
    }

    public void requestHeartReatDataAll()
    {
        byte[] bytes = new byte[6];
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR) - 2000;
        byte packageData = (byte)0x08;
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int mi = calendar.get(Calendar.MINUTE);

        int count = hour/3;
        int counts = hour%3;
        if(counts != 0 || mi != 0)
        {
            count = count + 1;
        }
        bytes[0] = (byte)(day & 0xff);
        bytes[1] = (byte)(month & 0xff);
        bytes[2] = (byte)(year & 0xff);
        bytes[3] = (byte)0x03;
        bytes[4] = (byte)packageData;
        synchronized (BleDataForDayHeartReatData.this)
        {
            for (int i = count; i >= 1; i--)
            {
                packageCount = i;
                bytes[5] =(byte)(i & 0xff);
                int length = setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
                Message msg = hrHandler.obtainMessage();
                msg.what = GET_HEART_WRATE_DATA;
                msg.arg1 = length;
                msg.arg2 = 300;
                Bundle bundle = new Bundle();
                bundle.putByteArray(DATA_PACKAGE, bytes);
                msg.setData(bundle);
                hrHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(length, 300));
                try {
                    BleDataForDayHeartReatData.this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void dealTheHeartRateData(byte[] hr)
    {
        if(hr[4] == 0x08 && hr[5] == packageCount)
        {
            isSendOk = true;
        }
        if(hrCallback == null)
        {
        }
        else
        {
            hrCallback.sendSuccess(hr);
        }
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        String today = format.format(calendar.getTime());
        int dayCurrent = calendar.get(Calendar.DATE);
        int monthCurrent = calendar.get(Calendar.MONTH) + 1;
        int yearCurrent = calendar.get(Calendar.YEAR);

//        MyDBHelperForDayData.getInstance(mContext).insertHrAccount(mContext, userAccount, today);
        int day = (hr[0] & 0xff);
        int month = (hr[1] & 0xff);
        int year = (hr[2] & 0xff) + 2000;
        Calendar calendarData = Calendar.getInstance();
        calendarData.set(year, (month - 1), day);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dataDay = format1.format(calendarData.getTime());
        if(day != dayCurrent || month != monthCurrent || year != yearCurrent)
        {
            Log.i(TAG, "心率日期对比：" + day + "--" + dayCurrent + "--" + month + "--" + monthCurrent + "--" + year + "--" + yearCurrent);

            byte[] responseData = new byte[6];
            for (int i = 0; i < responseData.length; i++)
            {
                responseData[i] = hr[i];
            }
            setMsgToByteDataAndSendToDevice(toDevice, responseData, responseData.length);
        }

    }

}
