package com.huichenghe.xinlvshuju.BleDeal;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.huichenghe.bleControl.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.UpdataService.DeviceTypeUtils;
import com.huichenghe.xinlvshuju.http.UserAccountUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by lixiaoning on 2016/10/16.
 */
public class BloodDataDealer
{
    public final String TAG = "BloodDataDealer";
//    00 c6a50458 7c 50 42 63 32 8516
    // 血压数据：00 46 f8 ed 58  64(收缩压) 3c（舒张压） 4c（心率）61 （spo2）64 50 16
    public BloodDataDealer(Context contexts, byte[] bloodData)
    {
        int va = (bloodData.length - 3)/9;
        Log.i(TAG, "血压数据长度：" + va);
        for (int i = 0; i < va; i++)
        {
            int start = i * 9 + 1;
            parseBloodData(start, bloodData, contexts);
        }
    }

    public BloodDataDealer(Context context, String jsonObj) throws JSONException
    {
        JSONObject jsonObject = new JSONObject(jsonObj);
        Iterator<String> it = jsonObject.keys();
        while (it.hasNext())
        {
            String bloodKey = it.next();
            String bloodValue = jsonObject.getString(bloodKey);
            JSONObject value = new JSONObject(bloodValue);
            int Systolic = value.getInt("Systolic");
            int Diastolic = value.getInt("Diastolic");
            int HeartRate = value.getInt("HeartRate");
            int SPO2 = value.getInt("SPO2");
            int HRV = value.getInt("HRV");
            SaveData(context, UserAccountUtil.getAccount(context), bloodKey, Systolic, Diastolic, HeartRate, SPO2, HRV, "1");
        }
    }

    private void parseBloodData(int start, byte[] bloodData, Context context)
    {
        TimeZone tz = TimeZone.getDefault();
        int offset = tz.getRawOffset()/1000;
        long date = FormatUtils.byte2Int(bloodData, start) - offset;
        int highPre = bloodData[start + 4] & 0xff;
        int lowPre = bloodData[start + 5] & 0xff;
        int hr = bloodData[start + 6] & 0xff;
        int sp02 = bloodData[start + 7] & 0xff;
        int hrv = bloodData[start + 8] & 0xff;
        String dateS = FormatDate(date * 1000);
        Log.i(TAG, "血压数据血压日期：" + dateS + "高压：" + highPre + "低压：" + lowPre);
        String account = UserAccountUtil.getAccount(context);
        SaveData(context, account, dateS, highPre, lowPre, hr, sp02, hrv, "0");
    }

    private void SaveData(Context context, String account, String dateS, int highPre, int lowPre, int hr, int sp02, int hrv, String flag)
    {
        Cursor cursor = MyDBHelperForDayData.getInstance(context)
                .selectBloodData(context, account, dateS.substring(0, 10), dateS.substring(11), DeviceTypeUtils.getDeviceType(context));
        if(cursor.getCount() > 0)
        {
            Log.i(TAG, "重复血压数据到数据库");
            return;
        }
        else
        {
            MyDBHelperForDayData.getInstance(context)
                    .insertBloodData(account, dateS.substring(0, 10), dateS.substring(11), highPre, lowPre, hr, sp02, hrv, DeviceTypeUtils.getDeviceType(context), flag);
            Log.i(TAG, "插入血压数据到数据库");
        }
    }
    private String FormatDate(long date)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return format.format(new Date(date));
    }
}
