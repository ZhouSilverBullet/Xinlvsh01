package com.huichenghe.xinlvshuju.UpdataService;

import android.content.Context;
import android.database.Cursor;

import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lixiaoning on 2016/12/8.
 */

public class EachStepDataHelper extends BaseHelper
{
    public final static String TAG = "updateData";
    private Context context;
    public EachStepDataHelper(Context context)
    {
        this.context = context;
    }



    /**
     * 获取当前日期的分时计步
     */
    public void getStepsData(String date, String accout, String dataType, SendDataCallback downLoadCallback)
    {
        HashMap<String, String> dataMap =
                getSendMovementDownMap(context, date, dataType, UserMACUtils.getMac(context), DeviceTypeUtils.getDeviceType(context));
        String cookie = LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.COOKIE_FOR_ME);
        new DownDataTask(dataMap, context, downLoadCallback).execute(cookie);
    }

    /**
     * 获取当前日期的分时卡路里
     */
    public void getCalorieData(String date, String accout, String dataType, SendDataCallback downLoadCallback)
    {
        HashMap<String, String> dataMap =
                getSendMovementDownMap(context, date, dataType, UserMACUtils.getMac(context), DeviceTypeUtils.getDeviceType(context));
        String cookie = LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.COOKIE_FOR_ME);
        new DownDataTask(dataMap, context, downLoadCallback).execute(cookie);
    }


    private ArrayList<EachStepDataEntity> entitys = new ArrayList<>();

    /**
     * 查询每小时计步
     * @param account
     * @param needChecoDay
     */
    public void checkAndUpEachStepData(String account, String needChecoDay)
    {
        Cursor cursor = MyDBHelperForDayData.getInstance(context)
                .getEachHourData(context, account, needChecoDay);
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
                    EachStepDataEntity en = getEachStepDataEntity(needChecoDay, account, cursor);
                    entitys.add(en);
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
            cursor.close();
            if(entitys.size() > 0)
            {
                for (int i = 0; i < entitys.size(); i++)
                {
                    EachStepDataEntity stepEntity = entitys.get(i);
                    getEachStepDataAndSend(stepEntity.getDate(), stepEntity.getStepData(), stepEntity.getDeviceType(), "stepDay");
                }
            }
        }
    }

    private EachStepDataEntity getEachStepDataEntity(String needChecoDay, String account, Cursor cursor)
    {
        String eachStepData = getAllStepDataToString(needChecoDay, cursor);
        String deviceType = cursor.getString(cursor.getColumnIndex("deviceType"));
        return new EachStepDataEntity(needChecoDay, eachStepData, deviceType, account);
    }

    /**
     * 查询每小时卡路里
     * @param account
     * @param needChecoDay
     */
    public void checkAndUpEachCalorieData(String account, String needChecoDay)
    {
        Cursor cursor = MyDBHelperForDayData.getInstance(context)
                .getEachHourCalorieData(context, account, needChecoDay);
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
                    EachStepDataEntity enty = getEachStepDataEntity(needChecoDay, account, cursor);
                    entitys.add(enty);
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
            cursor.close();
        }
        if(entitys.size() > 0)
        {
            for (int i = 0; i < entitys.size(); i++)
            {
                EachStepDataEntity ens = entitys.get(i);
                getEachStepDataAndSend(ens.getDate(), ens.getStepData(), ens.getDeviceType(), "calorieDay");
            }
        }
    }


    /**
     * 发送分时数据
     * @param needChecoDay
     */
    private void getEachStepDataAndSend(String needChecoDay, String eachStepData, String deviceType, final String sendType)
    {
        SendDataCallback sendCallback = new SendDataCallback() {
            @Override
            public void sendDataSuccess(String reslult)
            {
                if(entitys.size() > 0)
                {
                    boolean isUP = updateAlreadyLoad(reslult);
                    updateDB(isUP, entitys.get(0), sendType);
                    entitys.remove(0);
                }
                if(entitys.size() <= 0)
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
        HashMap<String, String> map = getSendMovementMap(context, needChecoDay, eachStepData, sendType,
                UserMACUtils.getMac(context), deviceType);
        String otherUrl = MyConfingInfo.updateTextUrl + "uploadData_MovementData";
        new UpLoadDayDataTask(context, map, sendCallback).execute(getCookie(context), otherUrl);
    }



    private void updateDB(boolean isUP, EachStepDataEntity eachStepDataEntity, String sendType)
    {
        if(isUP)
        {
            if(sendType.equals("stepDay"))
            {
                MyDBHelperForDayData.getInstance(context).updateEachStepData(context, eachStepDataEntity.getAccount(), eachStepDataEntity.getDate(), "1", eachStepDataEntity.getDeviceType());
            }
            else
            {
                MyDBHelperForDayData.getInstance(context).updateEachCalorieData(context, eachStepDataEntity.getAccount(), eachStepDataEntity.getDate(), eachStepDataEntity.getDeviceType(), "1");
            }
        }
    }

    private String getAllStepDataToString(String needChecoDay, Cursor cursor)
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
        StringBuffer sber = new StringBuffer();
        sber.append((one != null && one.equals("-1")) ? "0" : one).append(",");
        sber.append((two != null && two.equals("-1")) ? "0" : two).append(",");
        sber.append((three != null && three.equals("-1")) ? "0" : three).append(",");
        sber.append((four != null && four.equals("-1")) ? "0" : four).append(",");
        sber.append((five != null && five.equals("-1")) ? "0" : five).append(",");
        sber.append((six != null && six.equals("-1")) ? "0" : six).append(",");
        sber.append((seven != null && seven.equals("-1")) ? "0" : seven).append(",");
        sber.append((eight != null && eight.equals("-1")) ? "0" : eight).append(",");
        sber.append((nine != null && nine.equals("-1")) ? "0" : nine).append(",");
        sber.append((ten != null && ten.equals("-1")) ? "0" : ten).append(",");
        sber.append((one1 != null && one1.equals("-1")) ? "0" : one1).append(",");
        sber.append((two1 != null && two1.equals("-1")) ? "0" : two1).append(",");
        sber.append((three1 != null && three1.equals("-1")) ? "0" : three1).append(",");
        sber.append((four1 != null && four1.equals("-1")) ? "0" : four1).append(",");
        sber.append((five1 != null && five1.equals("-1")) ? "0" : five1).append(",");
        sber.append((six1 != null && six1.equals("-1")) ? "0" : six1).append(",");
        sber.append((seven1 != null && seven1.equals("-1")) ? "0" : seven1).append(",");
        sber.append((eight1 != null && eight1.equals("-1")) ? "0" : eight1).append(",");
        sber.append((nine1 != null && nine1.equals("-1")) ? "0" : nine1).append(",");
        sber.append((ten1 != null && ten1.equals("-1")) ? "0" : ten1).append(",");
        sber.append((one2 != null && one2.equals("-1")) ? "0" : one2).append(",");
        sber.append((two2 != null && two2.equals("-1")) ? "0" : two2).append(",");
        sber.append((three2 != null && three2.equals("-1")) ? "0" : three2).append(",");
        sber.append((four2 != null && four2.equals("-1")) ? "0" : four2);
        return sber.toString();
    }


}
