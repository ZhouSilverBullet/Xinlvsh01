package com.huichenghe.xinlvshuju.BleDeal;

import android.content.Context;
import android.database.Cursor;

import com.huichenghe.bleControl.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.UpdataService.DeviceTypeUtils;
import com.huichenghe.xinlvshuju.http.UserAccountUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by lixiaoning on 2016/8/31.
 * //        计步数据
 //        ffffffff ffffffff ffffffff ffffffff ffffffff ffffffff
 //        ffffffff ffffffff ffffffff 00000000 cc000000 ffffffff
 //        ffffffff 10000000 30000000 00000000 18000000 0d000000
 //        00000000 33000000 ffffffff ffffffff ffffffff ffffffff

 //        热量数据
 //        ffffffff ffffffff ffffffff ffffffff ffffffff ffffffff
 //        ffffffff ffffffff ffffffff 00000000 42000000 ffffffff
 //        ffffffff 40000000 33000000 2a000000 37000000 36000000
 //        37000000 35000000 ffffffff ffffffff ffffffff ffffffff
 */
public class BleEachDataDealer
{

    public BleEachDataDealer(Context context, String data, int type)
    {
        try {
            JSONObject json = new JSONObject(data);
            String time = json.getString("time");
            if(type == 0)
            {
                String stepDay = json.getString("stepDay");
                int[] steps = changeStringToArray(stepDay);
                if(steps == null)return;
                saveStepsToDatabase(1, context, UserAccountUtil.getAccount(context), time, steps, "1");
            }
            else
            {
                String calorieDay = json.getString("calorieDay");
                int[] calories = changeStringToArray(calorieDay);
                if(calories == null)return;
                saveCalorieDatabase(1, context, UserAccountUtil.getAccount(context), time, calories, "1");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int[] changeStringToArray(String stepDay)
    {
        if(stepDay != null && stepDay.contains(","))
        {
            String[] eachData = stepDay.split(",");
            int[] each = new int[eachData.length];
            for (int i = 0; i < eachData.length; i++)
            {
                each[i] = Integer.valueOf(eachData[i]);
            }
            return each;
        }
        return null;
    }


    public BleEachDataDealer(Context mContext, byte[] eachData)
    {
        int[] stepArray = new int[24];
        int[] calorieArray = new int[24];
        int dayNow = eachData[0] & 0xff;
        int monthNow = eachData[1] & 0xff;
        int yearNow = (eachData[2] & 0xff) + 2000;
        String dayS = formatTheDataDate(yearNow, monthNow, dayNow);
        stepArray[0] = FormatUtils.byte2Int(eachData, 4);
        stepArray[1] = FormatUtils.byte2Int(eachData, 8);
        stepArray[2] = FormatUtils.byte2Int(eachData, 12);
        stepArray[3] = FormatUtils.byte2Int(eachData, 16);
        stepArray[4] = FormatUtils.byte2Int(eachData, 20);
        stepArray[5] = FormatUtils.byte2Int(eachData, 24);
        stepArray[6] = FormatUtils.byte2Int(eachData, 28);
        stepArray[7] = FormatUtils.byte2Int(eachData, 32);
        stepArray[8] = FormatUtils.byte2Int(eachData, 36);
        stepArray[9] = FormatUtils.byte2Int(eachData, 40);
        stepArray[10] = FormatUtils.byte2Int(eachData, 44);
        stepArray[11] = FormatUtils.byte2Int(eachData, 48);
        stepArray[12] = FormatUtils.byte2Int(eachData, 52);
        stepArray[13] = FormatUtils.byte2Int(eachData, 56);
        stepArray[14] = FormatUtils.byte2Int(eachData, 60);
        stepArray[15] = FormatUtils.byte2Int(eachData, 64);
        stepArray[16] = FormatUtils.byte2Int(eachData, 68);
        stepArray[17] = FormatUtils.byte2Int(eachData, 72);
        stepArray[18] = FormatUtils.byte2Int(eachData, 76);
        stepArray[19] = FormatUtils.byte2Int(eachData, 80);
        stepArray[20] = FormatUtils.byte2Int(eachData, 84);
        stepArray[21] = FormatUtils.byte2Int(eachData, 88);
        stepArray[22] = FormatUtils.byte2Int(eachData, 92);
        stepArray[23] = FormatUtils.byte2Int(eachData, 96);

        calorieArray[0] = FormatUtils.byte2Int(eachData, 100);
        calorieArray[1] = FormatUtils.byte2Int(eachData, 104);
        calorieArray[2] = FormatUtils.byte2Int(eachData, 108);
        calorieArray[3] = FormatUtils.byte2Int(eachData, 112);
        calorieArray[4] = FormatUtils.byte2Int(eachData, 116);
        calorieArray[5] = FormatUtils.byte2Int(eachData, 120);
        calorieArray[6] = FormatUtils.byte2Int(eachData, 124);
        calorieArray[7] = FormatUtils.byte2Int(eachData, 128);
        calorieArray[8] = FormatUtils.byte2Int(eachData, 132);
        calorieArray[9] = FormatUtils.byte2Int(eachData, 136);
        calorieArray[10] = FormatUtils.byte2Int(eachData, 140);
        calorieArray[11] = FormatUtils.byte2Int(eachData, 144);
        calorieArray[12] = FormatUtils.byte2Int(eachData, 148);
        calorieArray[13] = FormatUtils.byte2Int(eachData, 152);
        calorieArray[14] = FormatUtils.byte2Int(eachData, 156);
        calorieArray[15] = FormatUtils.byte2Int(eachData, 160);
        calorieArray[16] = FormatUtils.byte2Int(eachData, 164);
        calorieArray[17] = FormatUtils.byte2Int(eachData, 168);
        calorieArray[18] = FormatUtils.byte2Int(eachData, 172);
        calorieArray[19] = FormatUtils.byte2Int(eachData, 176);
        calorieArray[20] = FormatUtils.byte2Int(eachData, 180);
        calorieArray[21] = FormatUtils.byte2Int(eachData, 184);
        calorieArray[22] = FormatUtils.byte2Int(eachData, 188);
        calorieArray[23] = FormatUtils.byte2Int(eachData, 192);
        String userAccount = UserAccountUtil.getAccount(mContext);
        saveStepsToDatabase(0, mContext, userAccount, dayS, stepArray, "0");
        saveCalorieDatabase(0, mContext, userAccount, dayS, calorieArray, "0");
    }

    /**
     * 保存卡路里数据
     * @param mContext
     * @param userAccount
     * @param dayS
     * @param calorieArray
     */
    private void saveCalorieDatabase(int where, Context mContext, String userAccount, String dayS, int[] calorieArray, String flag)
    {
        Cursor cursor = MyDBHelperForDayData.getInstance(mContext).getEachHourCalorieData(mContext, userAccount, dayS, DeviceTypeUtils.getDeviceType(mContext));
        if(cursor.getCount() == 0)
        {
            MyDBHelperForDayData.getInstance(mContext)
                    .insertEachHourCalorieData(mContext, userAccount, dayS, calorieArray[0], calorieArray[1], calorieArray[2], calorieArray[3],
                            calorieArray[4], calorieArray[5], calorieArray[6], calorieArray[7], calorieArray[8], calorieArray[9], calorieArray[10],
                            calorieArray[11], calorieArray[12], calorieArray[13], calorieArray[14], calorieArray[15], calorieArray[16], calorieArray[17],
                            calorieArray[18], calorieArray[19], calorieArray[20], calorieArray[21], calorieArray[22], calorieArray[23],
                            DeviceTypeUtils.getDeviceType(mContext), flag);
        }
        else    // 有数据则更新数据
        {
            if(where == 0)
            {
                MyDBHelperForDayData.getInstance(mContext).updateEachHourCalorieData
                        (mContext, userAccount, dayS, calorieArray[0], calorieArray[1], calorieArray[2], calorieArray[3],
                                calorieArray[4], calorieArray[5], calorieArray[6], calorieArray[7], calorieArray[8], calorieArray[9], calorieArray[10],
                                calorieArray[11], calorieArray[12], calorieArray[13], calorieArray[14], calorieArray[15], calorieArray[16], calorieArray[17],
                                calorieArray[18], calorieArray[19], calorieArray[20], calorieArray[21], calorieArray[22], calorieArray[23],
                                DeviceTypeUtils.getDeviceType(mContext), flag);
            }
            else
            {
                if(checkCanUpdate(cursor))
                {
                    MyDBHelperForDayData.getInstance(mContext).updateEachHourCalorieData
                            (mContext, userAccount, dayS, calorieArray[0], calorieArray[1], calorieArray[2], calorieArray[3],
                                    calorieArray[4], calorieArray[5], calorieArray[6], calorieArray[7], calorieArray[8], calorieArray[9], calorieArray[10],
                                    calorieArray[11], calorieArray[12], calorieArray[13], calorieArray[14], calorieArray[15], calorieArray[16], calorieArray[17],
                                    calorieArray[18], calorieArray[19], calorieArray[20], calorieArray[21], calorieArray[22], calorieArray[23],
                                    DeviceTypeUtils.getDeviceType(mContext), flag);
                }
            }

        }
    }


    private void saveStepsToDatabase(int where, Context mContext, String userAccount, String dayS, int[] steps, String flag)
    {
        Cursor mCursor = MyDBHelperForDayData.getInstance(mContext).getEachHourData(mContext, userAccount, dayS, DeviceTypeUtils.getDeviceType(mContext));
        if(mCursor.getCount() == 0)
        {
            MyDBHelperForDayData.getInstance(mContext)
                    .insertEachHourData(mContext, userAccount, dayS, steps[0], steps[1], steps[2], steps[3], steps[4], steps[5],
                            steps[6], steps[7], steps[8], steps[9], steps[10], steps[11], steps[12], steps[13], steps[14],
                            steps[15], steps[16], steps[17], steps[18], steps[19], steps[20], steps[21], steps[22], steps[23],
                            DeviceTypeUtils.getDeviceType(mContext), flag);
        }
        else
        {
            if(where == 0)
            {
                MyDBHelperForDayData.getInstance(mContext).updateEachHourData(mContext, userAccount, dayS, steps[0], steps[1], steps[2],
                        steps[3], steps[4], steps[5], steps[6], steps[7], steps[8], steps[9], steps[10], steps[11], steps[12], steps[13],
                        steps[14], steps[15], steps[16], steps[17], steps[18], steps[19], steps[20], steps[21], steps[22], steps[23],
                        DeviceTypeUtils.getDeviceType(mContext), flag);
            }
            else
            {
                if(checkCanUpdate(mCursor))
                {
                    MyDBHelperForDayData.getInstance(mContext).updateEachHourData(mContext, userAccount, dayS, steps[0], steps[1], steps[2],
                            steps[3], steps[4], steps[5], steps[6], steps[7], steps[8], steps[9], steps[10], steps[11], steps[12], steps[13],
                            steps[14], steps[15], steps[16], steps[17], steps[18], steps[19], steps[20], steps[21], steps[22], steps[23],
                            DeviceTypeUtils.getDeviceType(mContext), flag);
                }
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

    private String formatTheDataDate(int yearNow, int monthNow, int dayNow)
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.set(yearNow, monthNow - 1, dayNow);
        Date dateF = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(dateF);
    }

}
