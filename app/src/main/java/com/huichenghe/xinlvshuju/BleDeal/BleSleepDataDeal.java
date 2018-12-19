package com.huichenghe.xinlvshuju.BleDeal;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.UpdataService.DeviceTypeUtils;
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
 * Created by lixiaoning on 2016/8/29.
 */
public class BleSleepDataDeal
{
    public static final String TAG = BleSleepDataDeal.class.getSimpleName();
    private Context context;

    public BleSleepDataDeal(byte[] sleepData, Context context)
    {
        this.context = context;
        int d = sleepData[0] & 0xff;
        int m = sleepData[1] & 0xff;
        int y = sleepData[2] & 0xff;
        Calendar calendarFromSleepData = Calendar.getInstance(TimeZone.getDefault());
        calendarFromSleepData.set(y + 2000, m - 1, d);
        Date dateOne = calendarFromSleepData.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String curr = format.format(dateOne);
        String dataString = "";
        for (int i = 4; i < 40; i++)        // 遍历字节数组
        {
            byte buffer = sleepData[i];     // 取出一个字节赋值给buffer

            for (int j = 0; j < 4; j++)     // 循环取出两位
            {
                byte a = (byte)((buffer >> (j * 2)) & (byte) 0x03);   // 取出两位
                int ai = a & (byte)0x03;
                dataString = dataString + String.valueOf(ai);
            }
        }
        String s=dataString;
        String userAccount = UserAccountUtil.getAccount(context);
        insertToDatabase(0, userAccount, curr, dataString, "0");
    }

    public BleSleepDataDeal(Context context, String day, String sleepData)
    {
        String userAccount = UserAccountUtil.getAccount(context);
        String data = formateSleepData(sleepData);
        insertToDatabase(1, userAccount, day, data, "1");
    }

    /**
     * 整理数据格式
     * @param sleepData
     * @return
     */
    private String formateSleepData(String sleepData)
    {
        StringBuffer sb = new StringBuffer();
        if(sleepData.contains(","))
        {
            String[] sd = sleepData.split(",");
            for (int i = 0; i < sd.length; i++)
            {
                sb.append(sd[i]);
            }
        }
        return sb.toString();
    }

    private void insertToDatabase(int where, String userAccount, String curr, String dataString, String flag)
    {
        // 先查询数据库有无睡眠数据
        Cursor mCursor = MyDBHelperForDayData.getInstance(context)
                .selectTheSleepData(context, userAccount, curr, DeviceTypeUtils.getDeviceType(context));
        if(mCursor.getCount() != 0) // 等于0代表无数据
        {
            if(dataString != null && dataString.length() > 0)
            {
                if(where == 0)
                {
                    MyDBHelperForDayData.getInstance(context)
                            .updateTheSleepData(context, userAccount, curr,dataString, DeviceTypeUtils.getDeviceType(context), flag);
                }
                else
                {
                    if(checkCanUpdate(mCursor))
                    {
                        MyDBHelperForDayData.getInstance(context)
                                .updateTheSleepData(context, userAccount, curr,dataString, DeviceTypeUtils.getDeviceType(context), flag);
                    }
                }

            }
        }
        else
        {
            if(dataString != null && dataString.length() > 0)
            {
                Log.i(TAG, "插入睡眠数据");
                MyDBHelperForDayData.getInstance(context)
                        .insertTheSleepData(context, userAccount, curr, dataString, DeviceTypeUtils.getDeviceType(context), flag);
            }
        }


    }

    private boolean checkCanUpdate(Cursor mCursor)
    {
        if(mCursor.moveToFirst())
        {
            do {
                String dataSend = mCursor.getString(mCursor.getColumnIndex("dataSendOK"));
                if(dataSend != null && dataSend.equals("0"))
                {
                    return false;
                }
            }while (mCursor.moveToNext());
        }
        return true;
    }


    private void uploadingTheStepAndSleepData(final String day)
    {
        String loginType = UserAccountUtil.getType(context);
        switch (loginType)
        {
            case MyConfingInfo.QQ_TYPE:
            case MyConfingInfo.WEICHART_TYPE:
            case MyConfingInfo.FACEBOOK_TYPE:
                if(NetStatus.isNetWorkConnected(context))
                {
                    checkThirdPartyTask tasks = new checkThirdPartyTask();
                    tasks.setOnLoginBackListener(new OnAllLoginBack()
                    {
                        @Override
                        public void onLoginBack(String re)
                        {
                            new UpdateAttionMovementAndSleepData(context).executeOnExecutor(MyApplication.threadService, day);
                        }
                    });
                    tasks.executeOnExecutor(MyApplication.threadService, UserAccountUtil.getAccount(context), loginType, null, null, null);
                }
                break;
            case MyConfingInfo.NOMAL_TYPE:
                if(NetStatus.isNetWorkConnected(context))
                {
                    LoginOnBackground backLogin = new LoginOnBackground(context);
                    backLogin.setOnLoginBackListener(new OnAllLoginBack()
                    {
                        @Override
                        public void onLoginBack(String re)
                        {
                            new UpdateAttionMovementAndSleepData(context).executeOnExecutor(MyApplication.threadService, day);
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
