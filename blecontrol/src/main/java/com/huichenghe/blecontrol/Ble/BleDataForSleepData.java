package com.huichenghe.blecontrol.Ble;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.Utils.NetStatus;
import com.huichenghe.xinlvshuju.http.LoginOnBackground;
import com.huichenghe.xinlvshuju.http.OnAllLoginBack;
import com.huichenghe.xinlvshuju.http.UserAccountUtil;
import com.huichenghe.xinlvshuju.http.checkThirdPartyTask;
import com.huichenghe.xinlvshuju.mainpack.MyApplication;
import com.huichenghe.xinlvshuju.slide.AttionModle.UpdateAttionMovementAndSleepData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 获取和处理以及响应手环数据
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
                            continueSendData(this, msg.arg1);
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

    private void continueSendData(Handler handler, int length)
    {
//        6826040009081002b516
        Message msg = handler.obtainMessage();
        msg.what = GET_SLEEP_DATA;
        msg.arg1 = length;
        handler.sendMessageDelayed(msg, length * 24);
        count ++;
    }

    private void closeSendData(Handler handler)
    {
        handler.removeMessages(GET_SLEEP_DATA);
        isBack = false;
        count = 0;
        if(callback != null)
        {
            callback.sendFinish();
        }
        else
        {
            throw new NullPointerException("callback 为空");
        }
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
        int datalength = getTodaySleepdata();
        Message msg = sleepHandler.obtainMessage();
        msg.what = GET_SLEEP_DATA;
        msg.arg1 = datalength;
        sleepHandler.sendMessageDelayed(msg, datalength * 24);
    }

    /**
     * 获取睡眠数据
     */
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

    /**
     * 获取睡眠数据,带有回调函数
     */
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

    /**
     * 处理手环返回的数据
     * @param bufferTmp
     */
    public void dealTheSleepData(byte[] bufferTmp)
    {
        isBack = true;
        if(callback == null)
        {
            throw new NullPointerException("callback 为空");
            return;
        }
        callback.sendSuccess(bufferTmp);
//        070c0f02ffffffffffffffffffffffffffffffffff3f000000030000c0ffffffffffffffffffffff4016
//        Log.i(TAG, "睡眠数据：" + FormatUtils.bytesToHexString(bufferTmp));
        int d = bufferTmp[0] & 0xff;
        int m = bufferTmp[1] & 0xff;
        int y = bufferTmp[2] & 0xff;
        Calendar calendarFromSleepData = Calendar.getInstance(TimeZone.getDefault());
        calendarFromSleepData.set(y + 2000, m - 1, d);
        Date dateOne = calendarFromSleepData.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String curr = format.format(dateOne);
        /**
         * 每2位表示十分钟的睡眠质量， 0：活动，clock：浅睡，phone：深睡，info：未检测
         * 模拟的睡眠数据
         * ff ff ff ff ff ff ff ff ff ff
         * ff ff ff ff ff ff ff 3f 00 00
         * 00 03 00 00 c0 ff ff ff ff ff
         * ff ff ff ff ff ff 4016
         */
        String dataString = "";
//        int[] dataS = new int[36 * sitting];
//        int index = 0;
        for (int i = 4; i < 40; i++)        // 遍历字节数组
        {
            byte buffer = bufferTmp[i];     // 取出一个字节赋值给buffer

            for (int j = 0; j < 4; j++)     // 循环取出两位
            {
                byte a = (byte)((buffer >> (j * 2)) & (byte) 0x03);   // 取出两位
                int ai = a & (byte)0x03;
                dataString = dataString + String.valueOf(ai);
//                index++;
            }
        }
//        Log.i(TAG, "睡眠数据===:" + dataString);
        String userAccount = UserAccountUtil.getAccount(mContext);
        Calendar calendarsLocal = Calendar.getInstance(TimeZone.getDefault());
        Date dates = calendarsLocal.getTime();
        SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateSleep = formats.format(dates);


//        int day = calendar.get(Calendar.DATE);
//        int month = calendar.get(Calendar.MONTH) + clock;
//        int year = calendar.get(Calendar.YEAR) - 2000;
//        String dateSleep = (year + 2000) + "-" + month + "-" + day;

        // 先查询数据库有无睡眠数据
        Cursor mCursor = MyDBHelperForDayData.getInstance(mContext).selectTheSleepData(mContext, userAccount, curr);
        if(mCursor.getCount() != 0) // 等于0代表无数据
        {
            if(dataString != null && dataString.length() > 0)
            {
                MyDBHelperForDayData.getInstance(mContext).updateTheSleepData(mContext, userAccount, curr,dataString);
            }
        }
        else
        {
            if(dataString != null && dataString.length() > 0)
            {
                MyDBHelperForDayData.getInstance(mContext).insertTheSleepData(mContext, userAccount, curr, dataString);
            }
        }

        Log.i(TAG, "时间对比：" + curr + "--" + dateSleep);
        // 判断是否是当天数据，不是则返回响应数据
        if(curr != null && !curr.equals("") && dateSleep != null && !dateSleep.equals(""))
        {
            if(!curr.equals(dateSleep))
            {
                uploadingTheStepAndSleepData(formatTheDate(y, m, d));
                byte[] backData = new byte[4];
                for (int i = 0; i < backData.length; i++)
                {
                    backData[i] = bufferTmp[i];
                }
                setMsgToByteDataAndSendToDevice(toDevice, backData, backData.length);
            }
        }
        if(callback != null)
        callback.onSleepDataCallback();
    }


    private void uploadingTheStepAndSleepData(final String day)
    {
        String loginType = UserAccountUtil.getType(mContext);
        switch (loginType)
        {
            case MyConfingInfo.QQ_TYPE:
            case MyConfingInfo.WEICHART_TYPE:
            case MyConfingInfo.FACEBOOK_TYPE:
                if(NetStatus.isNetWorkConnected(mContext))
                {
                    checkThirdPartyTask tasks = new checkThirdPartyTask();
                    tasks.setOnLoginBackListener(new OnAllLoginBack()
                    {
                        @Override
                        public void onLoginBack()
                        {
                            new UpdateAttionMovementAndSleepData(mContext).executeOnExecutor(MyApplication.threadService, day);
                        }
                    });
                    tasks.executeOnExecutor(MyApplication.threadService, UserAccountUtil.getAccount(mContext), loginType, null, null, null);
                }
                break;
            case MyConfingInfo.NOMAL_TYPE:
                if(NetStatus.isNetWorkConnected(mContext))
                {
                    LoginOnBackground backLogin = new LoginOnBackground(mContext);
                    backLogin.setOnLoginBackListener(new OnAllLoginBack()
                    {
                        @Override
                        public void onLoginBack()
                        {
                            new UpdateAttionMovementAndSleepData(mContext).executeOnExecutor(MyApplication.threadService, day);
                        }
                    });
                    backLogin.executeOnExecutor(MyApplication.threadService);
                }
                break;
        }
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
