package com.huichenghe.xinlvshuju.slide.AttionModle;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.UpdataService.DeviceTypeUtils;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.expand_activity.MovementDetail.SleepDataHelper;
import com.huichenghe.xinlvshuju.http.HttpUtil;
import com.huichenghe.xinlvshuju.http.UserAccountUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by lixiaoning on 2016/6/12.
 */
public class UpdateAttionMovementAndSleepData extends AsyncTask<String, Void, Boolean>
{
    public final String TAG = UpdateAttionMovementAndSleepData.class.getSimpleName();
    private Context context;
    private final String attionDay = "activity.day";
    private final String attionStep = "activity.step";
    private final String sleepDeep = "activity.sleepDept";
    private final String sleepLight = "activity.sleepLight";
    private final String sleepAweak = "activity.sleepAweek";
    private final String sleepStates = "activity.sleepStatus";
    private final String finash = "activity.finishStatus";
    public UpdateAttionMovementAndSleepData(Context context)
    {
        this.context = context;
    }
    @Override
    protected Boolean doInBackground(String... params)
    {
        String today = params[0];
        String yestoday = getYestoday(today);
        String account = UserAccountUtil.getAccount(context);
        Cursor cursor = MyDBHelperForDayData.getInstance(context).selecteDayData(context, account, today, DeviceTypeUtils.getDeviceType(context));
        int step = parseCursor(cursor);
        int movementResult = getMovementResult(step);
        String[] sleepData = new SleepDataHelper(context).loadSleepData(yestoday, today, UserAccountUtil.getAccount(context));
        int[] allSleep = getSleepEachData(sleepData[2]);
        String sleetState = getSleepState(allSleep);
        String cookie = LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.COOKIE_FOR_ME);
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(attionDay, today);
        dataMap.put(attionStep, String.valueOf(step));
        dataMap.put(sleepDeep, String.valueOf(allSleep[1]));
        dataMap.put(sleepLight, String.valueOf(allSleep[0]));
        dataMap.put(sleepAweak, String .valueOf(allSleep[2]));
        dataMap.put(sleepStates, sleetState);
        dataMap.put(finash, String.valueOf(movementResult));
        String url = MyConfingInfo.WebRoot + "uploadData_dayActivity";
        Log.i(TAG, "上传数据的cookie" + cookie);
        try {
            HttpUtil.postDataAndImageHaveCookie(false, url, dataMap, null, cookie, new HttpUtil.DealResponse()
            {
                @Override
                public boolean dealResponse(int responseCode, InputStream input) throws IOException
                {
                    parseInputStream(input);
                    return false;
                }
                @Override
                public void setHeader(String url, Object obj)
                {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void parseInputStream(InputStream input) throws IOException
    {
        InputStreamReader read = new InputStreamReader(input);
        BufferedReader reader = new BufferedReader(read);
        String buff = "";
        String result = "";
        while ((buff = reader.readLine()) != null)
        {
            result = result + buff;
        }
        Log.i(TAG, "提交运动情况和睡眠数据" + result);
    }


    private int getMovementResult(int step)
    {
        int movementState = 0;
        String value = LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.TARGET_SETTING_VALUE);
        if(value != null && !value.equals(""))
        {
            if(step > Integer.parseInt(value))
            {
                movementState = 1;
            }
            else
            {
                movementState = 2;
            }
        }
//        Integer.parseInt(value);
        return movementState;
    }

    private String getSleepState(int[] allSleep)
    {
        String sleepState = null;
        int sleepTime = allSleep[0] + allSleep[1];
        if( sleepTime <= 6 * 60)
        {
            sleepState = "C";
        }
        else if (sleepTime > 6 * 60 && sleepTime < 9 * 60)
        {
            sleepState = "B";
        }
        else
        {
            sleepState = "A";
        }
        return sleepState;
    }

    private int[] getSleepEachData(String param)
    {
        int[] allSleepData = {0, 0, 0};
        if(param != null && !param.equals(""))
        {
            allSleepData = parseSleepData(param);
        }
        return allSleepData;
    }

    private int[] parseSleepData(String param)
    {
        int[] sleepData = new int[3];
        int lightSleep = 0;
        int deepSleep = 0;
        int aweakSleep = 0;
        for (int i = 0; i < param.length(); i++)
        {
            String state = param.substring(i, i+1);
            switch (state)
            {
                case "1":
                    lightSleep ++;
                    break;
                case "2":
                    deepSleep ++;
                    break;
                case "3":
                case "0":
                    aweakSleep ++;
                    break;
            }
        }
        sleepData[0] = lightSleep * 10;
        sleepData[1] = deepSleep * 10;
        sleepData[2] = aweakSleep * 10;
        return sleepData;
    }

    private int parseCursor(Cursor cursor)
    {
        int stepAll = 0;
        if(cursor.getCount() > 0)
        {
            if(cursor.moveToFirst())
            {
                do {
                    stepAll = cursor.getInt(cursor.getColumnIndex("stepAll"));


                }while (cursor.moveToNext());
            }
        }
        return stepAll;
    }

    private String getTodayDate()
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(calendar.getTime());
    }

    private String getYestoday(String beforDay)
    {
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = null;
        try {
            date = format.parse(beforDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(date == null)return null;
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, day - 1);
        return format.format(calendar.getTime());
    }
}
