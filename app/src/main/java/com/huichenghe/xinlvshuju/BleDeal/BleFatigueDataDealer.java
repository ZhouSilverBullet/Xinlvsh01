package com.huichenghe.xinlvshuju.BleDeal;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.UpdataService.DeviceTypeUtils;
import com.huichenghe.xinlvshuju.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.http.UserAccountUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by lixiaoning on 2016/8/30.
 */
public class BleFatigueDataDealer
{
    public BleFatigueDataDealer(byte[] bufferTmp, Context context)
    {
        int day = bufferTmp[0] & 0xff;
        int month = (bufferTmp[1] & 0xff) - 1;
        int year = (bufferTmp[2] & 0xff) + 2000;
        Calendar calend = Calendar.getInstance(TimeZone.getDefault());
        calend.set(year, month, day);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = format.format(calend.getTime());
        byte[] bytes = new byte[24];
        System.arraycopy(bufferTmp, 3, bytes, 0, 24);
        String userAccount = UserAccountUtil.getAccount(context);
        saveHRVToDataBase(0, context, userAccount, date, bytes, "0");
    }




    public BleFatigueDataDealer(Context context, String serviceData) throws JSONException
    {
        Log.i("updateData", "服务器返回的hrv数据" + serviceData);
//        {"time":"2016-12-14","hrv":"255,255,255,255,255,255,255,255,37,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255"}
        JSONObject json = new JSONObject(serviceData);
        String date = json.getString("time");
        String hrv = json.getString("hrv");
        if(hrv != null && hrv.equals(""))return;
        hrv = changeStringToHexString(hrv);
        saveHRVToDataBase(1, context, UserAccountUtil.getAccount(context), date, FormatUtils.hexString2ByteArray(hrv), "1");
    }

    private void saveHRVToDataBase(int where, Context context, String userAccount, String date, byte[] bytes, String flag)
    {
        MyDBHelperForDayData db = MyDBHelperForDayData.getInstance(context);
        Cursor cu = db.selectFatigueData(context, userAccount, date, DeviceTypeUtils.getDeviceType(context));
        if(cu.getCount() > 0)
        {
            if(where == 0)
            {
                db.updateFatigueData(context, userAccount, date, FormatUtils.bytesToHexString(bytes), DeviceTypeUtils.getDeviceType(context), flag);
            }
            else
            {
                if(checkCanUpdate(cu))
                {
                    db.updateFatigueData(context, userAccount, date, FormatUtils.bytesToHexString(bytes), DeviceTypeUtils.getDeviceType(context), flag);
                }
            }
        }
        else
        {
            db.insertFatigueData(context, userAccount, date, FormatUtils.bytesToHexString(bytes), DeviceTypeUtils.getDeviceType(context), flag);
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

    private String changeStringToHexString(String heartRate)
    {
        String[] hrData = heartRate.split(",");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < hrData.length; i++)
        {
            String a = Integer.toHexString(Integer.parseInt(hrData[i]));
            if(a.length() == 1)
            {
                a = "0" + a;
            }
            sb.append(a);
        }
        return sb.toString();
    }


}
