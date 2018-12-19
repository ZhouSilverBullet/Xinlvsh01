package com.huichenghe.xinlvshuju.UpdataService;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lixiaoning on 2016/12/8.
 */

public class SleepDataHelper extends BaseHelper
{
    public final static String TAG = "updateData";
    private Context context;
    public SleepDataHelper(Context context)
    {
        this.context = context;
    }



    /**
     * 获取当前日期的离线数据
     */
    public void getdateSleepData(String date, String accout, String dataType, SendDataCallback downLoadCallback)
    {
        HashMap<String, String> dataMap =
                getSendMovementDownMap(context, date, dataType, UserMACUtils.getMac(context), DeviceTypeUtils.getDeviceType(context));
        String cookie = LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.COOKIE_FOR_ME);
        new DownDataTask(dataMap, context, downLoadCallback).execute(cookie);
    }

    private ArrayList<SleepDataEntity> arrayList = new ArrayList<>();
    /**
     * 查询睡眠数据有无可发送数据
     * @param account
     * @param needChecoDay
     */
    public void checkAndUpSleepData(String account, String needChecoDay)
    {
        Cursor cursor = MyDBHelperForDayData.getInstance(context)
                .selectTheSleepData(context, account, needChecoDay);
        Log.i(TAG, "睡眠数据查询的cursor: 长度：" + cursor.getCount());
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
                if(dataSendOK == null || (dataSendOK != null && dataSendOK.equals("0")) || dataSendOK != null && dataSendOK.equals(""))
                {
                    // 需要发送，获取并发送数据
                    SleepDataEntity en = getDataAndSendSleepDataEntity(needChecoDay, account, cursor);
                    if(en != null)
                    {
                        arrayList.add(en);
                    }
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
        if(arrayList.size() > 0)
        {
            for (int i = 0; i < arrayList.size(); i++)
            {
                SleepDataEntity en = arrayList.get(i);
                getDataAndSendSleepData(en);
            }
        }
        else
        {
            callback.sendDataSuccess("");
            return;
        }
    }

    private SleepDataEntity getDataAndSendSleepDataEntity(String needChecoDay, String account, Cursor cursor)
    {
        String sleepData = cursor.getString(cursor.getColumnIndex("sleepData"));
        String deviceType = cursor.getString(cursor.getColumnIndex("deviceType"));
        Log.i(TAG, "数据库睡眠数据：" + sleepData + "--" + deviceType);
        sleepData = formateSleepData(sleepData);
        if(sleepData == null || sleepData.equals("") || sleepData.length() <= 0)
        {
            return null;
        }
        return new SleepDataEntity(account, needChecoDay, sleepData, deviceType);
    }

    /**
     * 发送睡眠数据
     */
    private void getDataAndSendSleepData(SleepDataEntity en)
    {
        Log.i(TAG, "睡眠数据查询的cursor: 数据：" + en.getData());
        HashMap<String, String> sleepMap = getSendMovementMap(context, en.getDate(), en.getData(),
                "sleepData", UserMACUtils.getMac(context), en.getDeviceType());
        String otherUrl = MyConfingInfo.updateTextUrl + "uploadData_MovementData";
        new UpLoadDayDataTask(context, sleepMap, sendCallback).execute(getCookie(context), otherUrl);
    }

    SendDataCallback sendCallback = new SendDataCallback() {
        @Override
        public void sendDataSuccess(String reslult)
        {
            if(arrayList.size() > 0)
            {
                boolean isUp = updateAlreadyLoad(reslult);
                updateSleepDB(isUp, arrayList.get(0));
                arrayList.remove(0);
            }
            if(arrayList.size() <= 0)
            {
                callback.sendDataSuccess(reslult);
            }
        }
        @Override
        public void sendDataFailed(String result) {}
        @Override
        public void sendDataTimeOut() {}
    };

    private void updateSleepDB(boolean isUp, SleepDataEntity sleepDataEntity)
    {
        if(isUp)
        {
            MyDBHelperForDayData.getInstance(context).updateSleepData(context, sleepDataEntity.getAccount(), sleepDataEntity.getDate(), sleepDataEntity.getDeviceType(), "1");
        }
    }
}
