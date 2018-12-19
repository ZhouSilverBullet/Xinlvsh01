package com.huichenghe.xinlvshuju.UpdataService;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lixiaoning on 2016/12/8.
 */

public class DayDataHelper extends BaseHelper
{
    public final static String TAG = "updateData";
    private Context context;
    public DayDataHelper(Context context)
    {
        this.context = context;
    }

    /**
     * 获取当前日期的全天数据
     */
    public void getDayData(String date, String accout, String dataType, SendDataCallback downLoadCallback)
    {
        HashMap<String, String> dataMap =
                getSendMovementDownMap(context, date, dataType, UserMACUtils.getMac(context), DeviceTypeUtils.getDeviceType(context));
        String cookie = LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.COOKIE_FOR_ME);
        new DownDataTask(dataMap, context, downLoadCallback).execute(cookie);
    }

    private ArrayList<DayDataEntity> entitys = new ArrayList<>();

    /**
     * 查询全天数据
     * @param account
     * @param needChecoDay
     */
    public void checkAndUpDayData(String account, String needChecoDay)
    {
        Cursor cursor = MyDBHelperForDayData.getInstance(context)
                .selecteDayData(context, account, needChecoDay);
        if(cursor.getCount() <= 0)
        {
            callback.sendDataSuccess("");
            return;
        }
        int dataCount = cursor.getCount();
        if(cursor.moveToFirst())
        {
            do {
                String dataSendOK = cursor.getString(cursor.getColumnIndex("dataSendOK"));
                if(dataSendOK == null || dataSendOK.equals("0") ||  dataSendOK.equals(""))
                {
                    // 需要发送，获取并发送数据
                    DayDataEntity entity = getDataAndSend(needChecoDay, account, cursor);
                    entitys.add(entity);
                }
                else
                {
                    dataCount--;
                    if(dataCount <= 0)
                    {
                        callback.sendDataSuccess("");
                    }
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        if(entitys.size() > 0)
        {
            for (int i = 0; i < entitys.size(); i++)
            {
                int q=entitys.size();
                DayDataEntity en = entitys.get(i);
                doUpdateDayData(en);
            }
        }
    }

    /**
     * 获取全天各项数据
     * @param date
     * @param cursor
     */
    private DayDataEntity getDataAndSend(String date, String account, Cursor cursor)
    {
        String stepAll = cursor.getString(cursor.getColumnIndex("stepAll"));
        String calorie = cursor.getString(cursor.getColumnIndex("calorie"));
        String mileage = cursor.getString(cursor.getColumnIndex("mileage"));
        String movementTime = cursor.getString(cursor.getColumnIndex("movementTime"));
        String moveCalorie = cursor.getString(cursor.getColumnIndex("moveCalorie"));
        String sitTime = cursor.getString(cursor.getColumnIndex("sitTime"));
        String sitCalorie = cursor.getString(cursor.getColumnIndex("sitCalorie"));
        String deviceType = cursor.getString(cursor.getColumnIndex("deviceType"));
        String stepTarget = "8000";
        String sleepTarget = "8";
        Log.i(TAG, "查询的数据--步数：" + stepAll + "--卡路里：" + calorie);
        return new DayDataEntity(account, date, stepAll, calorie, mileage, movementTime,
                moveCalorie, sitTime, sitCalorie, stepTarget, sleepTarget, deviceType);
    }

    /**
     * 发送结果回调
     */
    SendDataCallback sendCallback = new SendDataCallback()
    {
        @Override
        public void sendDataSuccess(String reslult)
        {
            if(entitys.size() > 0)
            {
                boolean isup = updateAlreadyLoad(reslult);
                updateDatabase(isup, entitys.get(0));
                entitys.remove(0);
                if(entitys.size() <= 0)
                {
                    callback.sendDataSuccess(reslult);
                    Log.i(TAG, "上传天数据结果：" + reslult);
                }
            }
        }
        @Override
        public void sendDataFailed(String result) {
            callback.sendDataFailed(result);
        }
        @Override
        public void sendDataTimeOut()
        {
            callback.sendDataTimeOut();
        }
    };

    private void updateDatabase(boolean isup, DayDataEntity entity)
    {
        if(isup)
        {
            MyDBHelperForDayData.getInstance(context).updateSendFlag(context, entity.getDeviceType(), entity.getAccount(), entity.getDate(), "1");
        }
    }

//    private boolean updateAlreadyLoad(DayDataEntity entity, String result)
//    {
//        JSONObject json = null;
//        try {
//            json = new JSONObject(result);
//            String code = json.getString("code");
//            if(code != null && code.equals("9003"))    // 上传成功
//            {
//                return true;
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }


    /**
     * 组织并发送数据
     */
    private void doUpdateDayData(DayDataEntity entity)
    {
        Map<String, String> map = getSendMap(entity.getDate(), entity.getStepAll(), entity.getCalorie(), entity.getMileage(),
                entity.getMovementTime(), entity.getMoveCalorie(), entity.getSitTime(), entity.getSitCalorie(),
                entity.getStepTarget(), entity.getSleepTarget(), entity.getDeviceType());
        String url = MyConfingInfo.updateTextUrl + "uploadData_AllData";
        Log.i(TAG, "UpLoadDayDataTask创建异步任务");
        new UpLoadDayDataTask(context, map, sendCallback).execute(getCookie(context), url);
    }

    /**
     * 将数据存入map
     * @param date
     * @param stepAll
     * @param calorie
     * @param mileage
     * @param movementTime
     * @param moveCalorie
     * @param sitTime
     * @param sitCalorie
     * @param stepTarget
     * @param sleepTarget
     * @param deviceType
     * @return
     */
    private Map<String,String> getSendMap(String date, String stepAll, String calorie,
                                          String mileage, String movementTime,
                                          String moveCalorie, String sitTime,
                                          String sitCalorie, String stepTarget,
                                          String sleepTarget, String deviceType)
    {
        HashMap<String, String> dayData = new HashMap<>();
        dayData.put("deviceType", deviceType);
        dayData.put("deviceId", UserMACUtils.getMac(context));
        dayData.put("time", date);
        dayData.put("allData.step", stepAll);
        dayData.put("allData.calorie", calorie);
        dayData.put("allData.mileage", mileage);
        dayData.put("allData.activityTimes", movementTime);
        dayData.put("allData.activityCalor", moveCalorie);
        dayData.put("allData.sitTimes", sitTime);
        dayData.put("allData.sitCalor", sitCalorie);
        dayData.put("allData.stepTarget", stepTarget);
        dayData.put("allData.sleepTarget", sleepTarget);
        return dayData;
    }
}
