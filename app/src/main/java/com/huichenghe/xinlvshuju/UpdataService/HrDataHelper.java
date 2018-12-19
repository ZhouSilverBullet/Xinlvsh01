package com.huichenghe.xinlvshuju.UpdataService;

import android.content.Context;
import android.database.Cursor;

import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lixiaoning on 2016/12/8.
 */

public class HrDataHelper extends BaseHelper
{
    public final static String TAG = "updateData";
    private Context context;
    public HrDataHelper(Context context)
    {
        this.context = context;
    }



    /**
     * 获取当前日期的心率数据
     */
    public void getdateHRData(String date, String accout, String dataType, SendDataCallback downLoadCallback)
    {
        HashMap<String, String> dataMap =
                getSendMovementDownMap(context, date, dataType, UserMACUtils.getMac(context), DeviceTypeUtils.getDeviceType(context));
        String cookie = LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.COOKIE_FOR_ME);
        new DownDataTask(dataMap, context, downLoadCallback).execute(cookie);
    }


    private ArrayList<HRDataEntity> arrayData = new ArrayList<>();
    /**
     * 查询全天心率
     * @param account
     * @param needChecoDay
     */
    public void checkAndUpHrData(String account, String needChecoDay)
    {
        Cursor cursor = MyDBHelperForDayData.getInstance(context)
                .selectHrAccount(context, account, needChecoDay);
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
                    HRDataEntity en = getHrDataEntity(needChecoDay, account, cursor);
                    arrayData.add(en);
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
        if(arrayData.size() > 0)
        {
            for (HRDataEntity en : arrayData)
            {
                getHrDataAndSend(en);
            }
        }
    }

    private HRDataEntity getHrDataEntity(String needChecoDay, String account, Cursor cursor)
    {
        String deviceType = cursor.getString(cursor.getColumnIndex("deviceType"));
        String hrData = getAllHrData(cursor);
//        Log.i(TAG, "上传当天的心率数据转换前：" + hrData);
//        Log.i(TAG, "上传当天的心率数据转换前长度：" + hrData.length()/2);
        byte[] hr = FormatUtils.hexStringToByteArray(hrData);
//        Log.i(TAG, "上传当天的心率数据转换为byte的长度：" + hr.length);
        hrData = formateHr(hr);
//        Log.i(TAG, "上传当天的心率数据转换为string的长度：" + hrData.split(",").length);
//        Log.i(TAG, "上传当天的心率数据：" + hrData);
        return new HRDataEntity(account, needChecoDay, hrData, deviceType);
    }

    /**
     * 从cursor中取出心率数据
     */
    private void getHrDataAndSend(HRDataEntity en)
    {
        HashMap<String, String> map = getSendMovementMap(context, en.getDate(), en.getHRData(),
                "heartRate", UserMACUtils.getMac(context), en.getDeviceType());
        String url = MyConfingInfo.updateTextUrl + "uploadData_MovementData";
        new UpLoadDayDataTask(context, map, sendCallback).execute(getCookie(context), url);
    }

    SendDataCallback sendCallback = new SendDataCallback() {
        @Override
        public void sendDataSuccess(String reslult)
        {
            if(arrayData.size() > 0)
            {
                boolean isUP = updateAlreadyLoad(reslult);
                updateDB(isUP, arrayData.get(0));
                arrayData.remove(0);
            }
            if(arrayData.size() <= 0)
            {
                callback.sendDataSuccess(reslult);
            }

        }

        @Override
        public void sendDataFailed(String result) {

        }

        @Override
        public void sendDataTimeOut() {

        }
    };

    private void updateDB(boolean isUP, HRDataEntity hrDataEntity)
    {
        if(isUP)
        {
            MyDBHelperForDayData.getInstance(context).updateHRSend(context, hrDataEntity.getAccount(), hrDataEntity.getDate(), hrDataEntity.getDeviceType(), "1");
        }
    }

    /**
     * 拼接心率数据
     * @param cursor
     * @return
     */
    private String getAllHrData(Cursor cursor)
    {
        String one = cursor.getString(cursor.getColumnIndex("one"));
        String two = cursor.getString(cursor.getColumnIndex("two"));
        String three = cursor.getString(cursor.getColumnIndex("three"));
        String four = cursor.getString(cursor.getColumnIndex("four"));
        String five = cursor.getString(cursor.getColumnIndex("five"));
        String six = cursor.getString(cursor.getColumnIndex("six"));
        String seven = cursor.getString(cursor.getColumnIndex("seven"));
        String eight = cursor.getString(cursor.getColumnIndex("eight"));
        String nine = cursor.getString(cursor.getColumnIndex("nine"));
        String ten = cursor.getString(cursor.getColumnIndex("ten"));
        String one1 = cursor.getString(cursor.getColumnIndex("one1"));
        String two1 = cursor.getString(cursor.getColumnIndex("two1"));
        String three1 = cursor.getString(cursor.getColumnIndex("three1"));
        String four1 = cursor.getString(cursor.getColumnIndex("four1"));
        String five1 = cursor.getString(cursor.getColumnIndex("five1"));
        String six1 = cursor.getString(cursor.getColumnIndex("six1"));
        String seven1 = cursor.getString(cursor.getColumnIndex("seven1"));
        String eight1 = cursor.getString(cursor.getColumnIndex("eight1"));
        String nine1 = cursor.getString(cursor.getColumnIndex("nine1"));
        String ten1 = cursor.getString(cursor.getColumnIndex("ten1"));
        String one2 = cursor.getString(cursor.getColumnIndex("one2"));
        String two2 = cursor.getString(cursor.getColumnIndex("two2"));
        String three2 = cursor.getString(cursor.getColumnIndex("three2"));
        String four2 = cursor.getString(cursor.getColumnIndex("four2"));
        StringBuffer hr = new StringBuffer();
        if(one != null)
            hr.append(one);
        if(two != null)
            hr.append(two);
        if(three != null)
            hr.append(three);
        if(four != null)
            hr.append(four);
        if(five != null)
            hr.append(five);
        if(six != null)
            hr.append(six);
        if(seven != null)
            hr.append(seven);
        if(eight != null)
            hr.append(eight);
        if(nine != null)
            hr.append(nine);
        if(ten != null)
            hr.append(ten);
        if(one1 != null)
            hr.append(one1);
        if(two1 != null)
            hr.append(two1);
        if(three1 != null)
            hr.append(three1);
        if(four1 != null)
            hr.append(four1);
        if(five1 != null)
            hr.append(five1);
        if(six1 != null)
            hr.append(six1);
        if(seven1 != null)
            hr.append(seven1);
        if(eight1 != null)
            hr.append(eight1);
        if(nine1 != null)
            hr.append(nine1);
        if(ten1 != null)
            hr.append(ten1);
        if(one2 != null)
            hr.append(one2);
        if(two2 != null)
            hr.append(two2);
        if(three2 != null)
            hr.append(three2);
        if(four2 != null)
            hr.append(four2);
        return hr.toString();
    }
}
