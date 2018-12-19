package com.huichenghe.xinlvshuju.BleDeal;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.huichenghe.bleControl.Utils.FormatUtils;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by lixiaoning on 2016/8/31.
 */
public class DayDataDealer
{
    public final String TAG = "DayDataDealer";
    private Context mContext;

    public DayDataDealer(Context context, String data)
    {
        this.mContext = context;
        String date;
        int step, calorie, mileage, activityTime, activityCalor, sitTime, sitCalor;
        try {
            JSONObject json = new JSONObject(data);
            String code = json.getString("code");
            if(code != null && code.equals("9003"))
            {
                JSONObject jsonObj = json.getJSONObject("data");
                date = jsonObj.getString("time");
                step = jsonObj.getInt("step");
                calorie = jsonObj.getInt("calorie");
                mileage = jsonObj.getInt("mileage");
                activityTime = jsonObj.getInt("activityTime");
                activityCalor = jsonObj.getInt("activityCalor");
                sitTime = jsonObj.getInt("sitTime");
                sitCalor = jsonObj.getInt("sitCalor");
                saveIntoDatabase(1, date, step, calorie, mileage, activityTime, activityCalor, sitTime, sitCalor, "1");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public DayDataDealer(Context mContext, byte[] bufferTmp)
    {
        this.mContext = mContext;
        int day = bufferTmp[0];         // 日
        int month = bufferTmp[1];       // 月
        int year = bufferTmp[2];        // 年
        String dataDate = formatTheDate(year + 2000, month, day);
        int stepAll = FormatUtils.byte2Int(bufferTmp, 4);
        int calorie = FormatUtils.byte2Int(bufferTmp, 8);
        int mileage = FormatUtils.byte2Int(bufferTmp, 12);
        int movementTime = FormatUtils.byte2Int(bufferTmp, 16);
        int moveCalorie = FormatUtils.byte2Int(bufferTmp, 20);
        int sitTime = FormatUtils.byte2Int(bufferTmp, 24);
        int sitCalorie = FormatUtils.byte2Int(bufferTmp, 28);
        String dateC = getCurrentDate();
        saveIntoDatabase(0, dataDate, stepAll, calorie, mileage, movementTime, moveCalorie, sitTime, sitCalorie, "0");
    }


    public DayDataDealer(Context context, String key, String value)
    {
        saveIntoDatabase(key, value);
    }

    private void saveIntoDatabase(String key, String value)
    {
        String userAccount = UserAccountUtil.getAccount(mContext);
        MyDBHelperForDayData helper = MyDBHelperForDayData.getInstance(mContext);
        Cursor mCursor = helper.selecteDayData(mContext, userAccount, key, DeviceTypeUtils.getDeviceType(mContext));
        if(mCursor.getCount() != 0)
        {
            helper.updateDayDataOnStep(mContext, DeviceTypeUtils.getDeviceType(mContext), userAccount, key, value, "1");
        }
        else
        {
            helper.insertDayDataOnlySteps(mContext, userAccount, key, DeviceTypeUtils.getDeviceType(mContext), value, "1");
        }

    }

    private void saveIntoDatabase(int where, String dataDate, int stepAll, int calorie,
                                  int mileage, int movementTime, int moveCalorie,
                                  int sitTime, int sitCalorie, String flag)
    {
        String userAccount = UserAccountUtil.getAccount(mContext);
        MyDBHelperForDayData helper = MyDBHelperForDayData.getInstance(mContext);
        Cursor mCursor = helper.selecteDayData(mContext, userAccount, dataDate, DeviceTypeUtils.getDeviceType(mContext));
        if(mCursor.getCount() != 0)
        {
            if(where == 0)
            {
                helper.updateDayDataToday(mContext, DeviceTypeUtils.getDeviceType(mContext), userAccount, dataDate, stepAll,
                        calorie, mileage, String.valueOf(movementTime), moveCalorie, String.valueOf(sitTime), sitCalorie, flag);
            }
            else
            {
                if(checkCanUpdate(mCursor))
                {
                    helper.updateDayDataToday(mContext, DeviceTypeUtils.getDeviceType(mContext), userAccount, dataDate, stepAll,
                            calorie, mileage, String.valueOf(movementTime), moveCalorie, String.valueOf(sitTime), sitCalorie, flag);
                }
            }

        }
        else
        {
            helper.insert(mContext, DeviceTypeUtils.getDeviceType(mContext), userAccount, dataDate, stepAll, calorie, mileage,
                    String.valueOf(movementTime), moveCalorie, String.valueOf(sitTime), sitCalorie, flag);
        }
        // 发送广播通知更新界面
        mContext.sendBroadcast(new Intent(MyConfingInfo.NOTIFY_MAINACTIVITY_TO_UPDATE_DAY_DATA));
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

    private String formatTheDate(int year, int month, int day)
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.set(year, month - 1, day);
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(date);
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
                        public void onLoginBack(String re)
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
                        public void onLoginBack(String re)
                        {
                            new UpdateAttionMovementAndSleepData(mContext).executeOnExecutor(MyApplication.threadService, day);
                        }
                    });
                    backLogin.executeOnExecutor(MyApplication.threadService);
                }
                break;
        }


    }


    private String getFormetDay(int year, int month, int day)
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.set(year, month - 1, day);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
    }


    public String getCurrentDate()
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        Date dateNow = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(dateNow);
    }
}
