package com.huichenghe.xinlvshuju.UpdataService;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

/**
 * Created by lixiaoning on 2016/12/9.
 */

public class BloodDataHelper extends BaseHelper
{
    public final static String TAG = "updateData";
    private JSONObject mateJson;
    private JSONObject mateWatchJson;
    private Context context;
    private final String dataType = "bloodPressure";
    public BloodDataHelper(Context context)
    {
        this.context = context;
    }

    /**
     * 获取当前日期的血压数据
     */
    public void getDayBloodData(String date, String accout, String dataType, SendDataCallback downLoadCallback)
    {
        HashMap<String, String> dataMap =
                getSendMovementDownMap(context, date, dataType, UserMACUtils.getMac(context), DeviceTypeUtils.getDeviceType(context));
        String cookie = LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.COOKIE_FOR_ME);
        new DownDataTask(dataMap, context, downLoadCallback).execute(cookie);
    }
    private ArrayList<BloodDataEntity> entities = new ArrayList<>();

    /**
     * 查询数据库判断有无可发送数据
     * @param account
     * @param needDay
     */
    public void checkBloodData(String account, String needDay)
    {
        Cursor cursor = MyDBHelperForDayData.getInstance(context)
                .selectBloodData(context, account, needDay);
        if(cursor == null || cursor.getCount() <= 0)
        {
            callback.sendDataSuccess("");
            return;
        }
        if(cursor.moveToFirst())
        {
            do {
                String dataSendOK = cursor.getString(cursor.getColumnIndex("dataSendOK"));
                if(dataSendOK == null || (dataSendOK != null && dataSendOK.equals("0")) || dataSendOK != null && dataSendOK.equals(""))
                {
                    String deviceType = cursor.getString(cursor.getColumnIndex("deviceType"));
                    // 需要发送，获取并发送数据
                    Object[] objArray = getBloodDataAndSend(needDay, cursor);
                    if((deviceType != null && deviceType.equals("001")) || deviceType != null && deviceType.equals(""))
                    {
                        if(mateJson == null)
                            mateJson = new JSONObject();
                        try {
                            mateJson.put((String)objArray[0], objArray[1]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        if(mateWatchJson == null)
                            mateWatchJson = new JSONObject();
                        try {
                            mateWatchJson.put((String)objArray[0], objArray[1]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        if(mateJson != null)
        {
            String data = mateJson.toString();
            entities.add(new BloodDataEntity(account, needDay, data, "001"));
            mateJson = null;
            Log.i(TAG, "手环血压：" + data);
            HashMap<String, String> map = getSendMovementMap(context, needDay, data, dataType, UserMACUtils.getMac(context), "001");
            new UpLoadDayDataTask(context, map, callback).execute(getCookie(context), movementUrl);
        }
        if(mateWatchJson != null)
        {
            String data = mateWatchJson.toString();
            entities.add(new BloodDataEntity(account, needDay, data, "002"));
            mateWatchJson = null;
            Log.i(TAG, "手表血压：" + data);

        }
        if(entities.size() > 0)
        {
            for (BloodDataEntity en : entities)
            {
                sendBloodData(en);
            }
        }
        else
        {
            callback.sendDataSuccess("");
        }
    }

    /**
     * 发送数据
     * @param en
     */
    private void sendBloodData(BloodDataEntity en)
    {
        HashMap<String, String> map = getSendMovementMap(context, en.getDate(), en.getBloodData(),
                dataType, UserMACUtils.getMac(context), en.getDeviceType());
        new UpLoadDayDataTask(context, map, sendDataCallback).execute(getCookie(context), movementUrl);
    }

    SendDataCallback sendDataCallback = new SendDataCallback()
    {
        @Override
        public void sendDataSuccess(String reslult) {
            if(entities.size() > 0)
            {
                boolean isUP = updateAlreadyLoad(reslult);
                updateDB(isUP, entities.get(0));
                entities.remove(0);
            }
            if(entities.size() <= 0)
            {
                callback.sendDataSuccess(reslult);
            }
        }
        @Override
        public void sendDataFailed(String result) {}
        @Override
        public void sendDataTimeOut() {}
    };

    private void updateDB(boolean isUP, BloodDataEntity entity)
    {
        if(isUP)
        {
            String data = entity.getBloodData();
            try {
                JSONObject json = new JSONObject(data);
                Iterator<String> it = json.keys();
                while (it.hasNext())
                {
                    String key = it.next();
                    String date = key.substring(0, 10);
                    String time = key.substring(11);
                    MyDBHelperForDayData.getInstance(context).updateBloodDB(context, entity.getAccount(), date, time, entity.getDeviceType(), "1");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取数据
     * @param needDay
     * @param cursor
     */
    private Object[] getBloodDataAndSend(String needDay, Cursor cursor)
    {
        String date = cursor.getString(cursor.getColumnIndex("date"));
        String time = cursor.getString(cursor.getColumnIndex("time"));
        String highPre = cursor.getString(cursor.getColumnIndex("highPre"));
        String lowPre = cursor.getString(cursor.getColumnIndex("lowPre"));
        String heartRate = cursor.getString(cursor.getColumnIndex("heartRate"));
        String spo2 = cursor.getString(cursor.getColumnIndex("spo2"));
        String hrv = cursor.getString(cursor.getColumnIndex("hrv"));
        String longTime = date + " " + time;
        JSONObject job = FormateToJson(highPre, lowPre, heartRate, spo2, hrv);
        return new Object[]{longTime, job};
    }

    private JSONObject FormateToJson(String highPre, String lowPre, String heartRate, String spo2, String hrv)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("Systolic", highPre);
            json.put("Diastolic", lowPre);
            json.put("HeartRate", heartRate);
            json.put("SPO2", spo2);
            json.put("HRV", hrv);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }


    private String[] parseTimeToFormate(String[] times)
    {
        String[] dates = new String[times.length];
        for (int i = 0; i < dates.length; i++)
        {
            int time = Integer.parseInt(times[i]);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String re = format.format(new Date(time * 1000));
            dates[i] = re;
        }
        return dates;
    }

}
