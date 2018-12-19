package com.huichenghe.xinlvshuju.UpdataService;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lixiaoning on 2016/12/8.
 */

public class HRVDataHelper extends BaseHelper
{
    public final static String TAG = "updateData";
    private Context context;
    private ArrayList<HRVDataEntity> hrvArray = new ArrayList<>();
    public HRVDataHelper(Context context)
    {
        this.context = context;
    }


    /**
     * 获取当前日期的心率数据
     */
    public void getdateHRVData(String date, String accout, String dataType, SendDataCallback downLoadCallback)
    {
        HashMap<String, String> dataMap =
                getSendMovementDownMap(context, date, dataType, UserMACUtils.getMac(context), DeviceTypeUtils.getDeviceType(context));
        String cookie = LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.COOKIE_FOR_ME);
        new DownDataTask(dataMap, context, downLoadCallback).execute(cookie);
    }

    public void checkAndUpHrvData(String account, String needChecoDay)
    {
        Cursor cursor = MyDBHelperForDayData.getInstance(context)
                .selectFatigueData(context, account, needChecoDay);
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
                    HRVDataEntity en = getHrvDataEntity(needChecoDay, account, cursor);
                    hrvArray.add(en);
                }
                else
                {
                    dataCount --;
                    if(dataCount <= 0)
                    {
                        callback.sendDataSuccess("");
                    }
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        if(hrvArray.size() > 0)
        {
            for (HRVDataEntity en : hrvArray)
            {
                getHrvDataAndSend(en);
            }
        }
    }

    private HRVDataEntity getHrvDataEntity(String needChecoDay, String account, Cursor cursor)
    {
        String hrv = cursor.getString(cursor.getColumnIndex("fatigue"));
        String deviceType = cursor.getString(cursor.getColumnIndex("deviceType"));
        hrv = formateHr(FormatUtils.hexString2ByteArray(hrv));
        Log.i(TAG, "上传的hrv:" + hrv);
        return new HRVDataEntity(account, needChecoDay, hrv, deviceType);
    }

    /**
     * 发送数据
     * @param en
     */
    private void getHrvDataAndSend(HRVDataEntity en)
    {
        HashMap<String, String> map = getSendMovementMap(context, en.getDate(), en.getHrvData(),
                "hrv", UserMACUtils.getMac(context), en.getDeviceType());
        String url = MyConfingInfo.updateTextUrl + "uploadData_MovementData";
        new UpLoadDayDataTask(context, map, sendCallback).execute(getCookie(context), url);
    }

    SendDataCallback sendCallback = new SendDataCallback() {
        @Override
        public void sendDataSuccess(String reslult)
        {
            if(hrvArray.size() > 0)
            {
                boolean isUP = updateAlreadyLoad(reslult);
                updateDB(isUP, hrvArray.get(0));
                hrvArray.remove(0);
            }
            if(hrvArray.size() <= 0)
            {
                callback.sendDataSuccess(reslult);
            }
        }
        @Override
        public void sendDataFailed(String result) {}
        @Override
        public void sendDataTimeOut() {}
    };

    private void updateDB(boolean isUP, HRVDataEntity hrvDataEntity)
    {
        if(isUP)
        {
            MyDBHelperForDayData.getInstance(context)
                    .updateFatigue(context, hrvDataEntity.getAccount(),
                                    hrvDataEntity.getDate(),
                                    hrvDataEntity.getDeviceType(), "1");
        }
    }

}
