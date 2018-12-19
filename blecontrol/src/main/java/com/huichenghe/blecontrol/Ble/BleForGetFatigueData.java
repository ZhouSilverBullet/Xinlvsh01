package com.huichenghe.blecontrol.Ble;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.http.UserAccountUtil;

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
    private final int GETFATIGUEDATA = 0;
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

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            Log.i(TAG, "疲劳值isback:" + isBack);
            switch (msg.what)
            {
                case GETFATIGUEDATA:
                if(isBack)
                {
                    closeSendData(this);
                }
                else
                {
                    if(count < 4)
                    {
                        this.sendEmptyMessageDelayed(GETFATIGUEDATA, 60);
                        GetFatigueData();
                        count ++ ;
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

    private void closeSendData(Handler handler)
    {
        hasComm = false;
        handler.removeMessages(GETFATIGUEDATA);
        isBack = false;
        count = 0;
        if(dataSendCallback != null)
        {
            dataSendCallback.sendFinish();
        }
    }

    public static synchronized BleForGetFatigueData getInstance(Context context)
    {
        if(bleForGetFatigueData == null)
        {
            bleForGetFatigueData = new BleForGetFatigueData(context);
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
        GetFatigueData();
        mHandler.sendEmptyMessageDelayed(GETFATIGUEDATA, 80);
    }

    public void GetFatigueData()
    {
        byte[] by = new byte[3];
        by[0] = (byte)day;
        by[1] = (byte)month;
        by[2] = (byte)year;
        setMsgToByteDataAndSendToDevice(toDevice, by, by.length);
    }


    public void dealTheResbonseData(byte[] bufferTmp)
    {
        if(hasComm)
        {
            isBack = true;
            hasComm = false;
        }
        if(dataSendCallback != null)
        {
            dataSendCallback.sendSuccess(FormatUtils.bytesToHexString(bufferTmp));
        }
        int day = bufferTmp[0] & 0xff;
        int month = (bufferTmp[1] & 0xff) - 1;
        int year = (bufferTmp[2] & 0xff) + 2000;
        Calendar calend = Calendar.getInstance(TimeZone.getDefault());
        calend.set(year, month, day);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = format.format(calend.getTime());
        byte[] bytes = new byte[24];
        System.arraycopy(bufferTmp, 3, bytes, 0, 24);
        String userAccount = UserAccountUtil.getAccount(context);
        MyDBHelperForDayData db = MyDBHelperForDayData.getInstance(context);
        Cursor cu = db.selectFatigueData(context, userAccount, date);
//        Log.i(TAG, "数据库有无数据：" + cu.getCount());
        if(cu.getCount() > 0)
        {
            db.updateFatigueData(context, userAccount, date, FormatUtils.bytesToHexString(bytes));
        }
        else
        {
            db.insertFatigueData(context, userAccount, date, FormatUtils.bytesToHexString(bytes));
        }
        // 不是当天数据，回复设备代表已收到数据
        if(date != null && !date.equals(currentDate))
        {
            Log.i(TAG, "是否是当天数据" + date + "--" + currentDate);
            backToDevice(bufferTmp);
        }
    }


    /**
     * 返回非当天数据
     * @param bufferTmp
     */
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
