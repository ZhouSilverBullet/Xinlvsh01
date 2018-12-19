package com.huichenghe.xinlvshuju.UpdataService;

import android.content.Context;
import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import com.huichenghe.xinlvshuju.DataEntites.OutLineDataEntity;
import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lixiaoning on 2016/12/8.
 */

public class OutLineDataHelper extends BaseHelper
{
    private Context context;
    public OutLineDataHelper(Context context)
    {
        this.context = context;
    }
    private JSONObject metaJson;
    private JSONObject metaWatchJson;
    public final static String TAG = "updateData";


    public void getdateHRVData(String date, String accout, String dataType, SendDataCallback downLoadCallback)
    {
        HashMap<String, String> dataMap =
                getSendMovementDownMap(context, date, dataType, UserMACUtils.getMac(context), DeviceTypeUtils.getDeviceType(context));
        String cookie = LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.COOKIE_FOR_ME);
        new DownDataTask(dataMap, context, downLoadCallback).execute(cookie);
    }

    private ArrayList<OutlineDataEntity> outlineDataEntities = new ArrayList<>();

    public void checkOutLineDat(String account, String needSenday)
    {
        Cursor cursor = MyDBHelperForDayData.getInstance(context)
                .selectOutLineData(context, account, needSenday);
        if(cursor.getCount() <= 0)
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
                    Object[] oneJsonData = getOutlineData(needSenday, cursor);
                    if((deviceType != null && deviceType.equals("001")) || (deviceType != null && deviceType.equals("")))
                    {
                        if(metaJson == null)
                            metaJson = new JSONObject();
                        try {
                            metaJson.put((String)oneJsonData[0], (JSONObject)oneJsonData[1]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        if(metaWatchJson == null)
                            metaWatchJson = new JSONObject();
                        try {
                            metaWatchJson.put((String)oneJsonData[0], (JSONObject)oneJsonData[1]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        if(metaJson != null)
        {
            String data = metaJson.toString();
            outlineDataEntities.add(new OutlineDataEntity(account, needSenday, data, "001"));
        }
        if(metaWatchJson != null)
        {
            String data = metaWatchJson.toString();
            outlineDataEntities.add(new OutlineDataEntity(account, needSenday, data, "002"));
        }
        metaJson = null;
        metaWatchJson = null;
        if(outlineDataEntities.size() > 0)
        {
            for (OutlineDataEntity en : outlineDataEntities)
            {
                sendDataToService(en);
            }
        }
        else
        {
            callback.sendDataSuccess("");
        }

    }

    private void sendDataToService(OutlineDataEntity entity)
    {
        String url = MyConfingInfo.updateTextUrl + "uploadData_MovementData";
        HashMap<String, String> map = getSendMovementMap(context, entity.getDate(), entity.getOutlineData(),
                "offlineData", UserMACUtils.getMac(context), entity.getDeviceType());
        new UpLoadDayDataTask(context, map, sendDataCallback).execute(getCookie(context), url);
    }



    SendDataCallback sendDataCallback = new SendDataCallback() {
        @Override
        public void sendDataSuccess(String reslult)
        {
            if(outlineDataEntities.size() > 0)
            {
                boolean isUP = updateAlreadyLoad(reslult);
                updateDB(isUP, outlineDataEntities.get(0));
                outlineDataEntities.remove(0);
            }
            if(outlineDataEntities.size() <= 0)
            {
                callback.sendDataSuccess(reslult);
            }
        }
        @Override
        public void sendDataFailed(String result){}
        @Override
        public void sendDataTimeOut(){}
    };

    private void updateDB(boolean isUP, OutlineDataEntity entity)
    {
        if(isUP)
        {
            String data = entity.getOutlineData();
            parseJsonAndUpDB(data, entity);
        }


    }

    private void parseJsonAndUpDB(String data, OutlineDataEntity entity)
    {
        try {
            JSONObject json = new JSONObject(data);
            Iterator<String> it = json.keys();
            while (it.hasNext())
            {
                String key = it.next();
                String[] times = key.split("-");
                times = parseTimeToFormate(times);
                MyDBHelperForDayData.getInstance(context).updateDataSendOK(context, entity.getAccount(), times[0], times[1], entity.getDeviceType(), "1");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private Object[] getOutlineData(String needSenday, Cursor cursor)
    {
        String startTime = cursor.getString(cursor.getColumnIndex("startTime"));
        String endTime = cursor.getString(cursor.getColumnIndex("endTime"));
        String type = cursor.getString(cursor.getColumnIndex("type"));
        String calorie = cursor.getString(cursor.getColumnIndex("calorie"));
        String stepTotle = cursor.getString(cursor.getColumnIndex("stepTotle"));
        String heartRate = cursor.getString(cursor.getColumnIndex("heartRate"));
        heartRate = formateHr(FormatUtils.hexStringToByteArray(heartRate));
        String movementName = cursor.getString(cursor.getColumnIndex("sportName"));
        if(type != null && Integer.parseInt(type) == 6)
        {
            type = movementName;
        }
        String movementTime = formatStartAndEndTime(startTime, endTime);
        JSONObject jsonString = formateOutLineDataToJSON(movementTime, heartRate, stepTotle, calorie, type);
        return new Object[]{movementTime, jsonString};
    }

    private JSONObject formateOutLineDataToJSON(String movementTime, String heartRate,
                                            String stepTotle, String calorie, String type)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("heart", heartRate);
            json.put("calorie", calorie);
            json.put("step", stepTotle);
            json.put("movementType", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private String formatStartAndEndTime(String startTime, String endTime)
    {
        StringBuffer sb = new StringBuffer();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date start = format.parse(startTime);
            Date end = format.parse(endTime);
            Long startMill = start.getTime()/1000;
            Long endMill = end.getTime()/1000;
            sb.append(startMill).append("-").append(endMill);
        } catch (ParseException e){
            e.printStackTrace();
        }
        return sb.toString();
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

    public void deleteData(String deleteOffline, OutLineDataEntity entity, SendDataCallback callback)
    {
        String url = MyConfingInfo.updateTextUrl + "uploadData_MovementData";
        String[] times = entity.getTime().split("=");
        String data = formatStartAndEndTime(times[0], times[1]);
        HashMap<String, String> map = getSendMovementMap(context, entity.getTime().substring(0, 10), data,
                deleteOffline, UserMACUtils.getMac(context), DeviceTypeUtils.getDeviceType(context));
        new UpLoadDayDataTask(context, map, callback).execute(getCookie(context), url);
    }

}
