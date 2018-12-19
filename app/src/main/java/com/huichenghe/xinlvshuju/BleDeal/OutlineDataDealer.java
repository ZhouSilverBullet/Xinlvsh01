package com.huichenghe.xinlvshuju.BleDeal;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.huichenghe.bleControl.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.DataEntites.OutLineDataEntity;
import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.UpdataService.DeviceTypeUtils;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.http.UserAccountUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by lixiaoning on 2016/9/1.
 */
public class OutlineDataDealer
{
    public final String TAG = OutlineDataDealer.class.getSimpleName();

    public OutlineDataDealer(){}
    public OutlineDataDealer(Context mContext, byte[] data)
    {
        //        this.outLineData = data;
//      15665c56 16675c56 01 04000000 2c000000 a816
        TimeZone tz = TimeZone.getDefault();        // 获取默认时区
        int beginT = FormatUtils.byte2Int(data, 0) - tz.getRawOffset()/1000;
        int endT = FormatUtils.byte2Int(data, 4) - tz.getRawOffset()/1000;
        long b = (long)beginT;
        long e = (long)endT;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date d = new Date(e * 1000);
        String day = format.format(d);
        Log.i(TAG, "开始时间------：" + day + "int :" + beginT + endT);
        String[] times = splitTime(b * 1000, e * 1000);
        // 开始时间
        String beginTime = times[0];
        // 结束时间
        String endTime = times[1];
        byte pak = data[8];
        if(1 == pak)        // 表示心率总数据
        {
            boolean isHas = false;
            int calorie = FormatUtils.byte2Int(data, 9);
            int stepTotle = FormatUtils.byte2Int(data, 13);
            calorie = Math.abs(calorie);
            stepTotle = Math.abs(stepTotle);
            String type = String.valueOf(OutLineDataEntity.TYPE_UNKNOW);
            // 获取心率
            String outlineHr = getTheAverageHR(mContext, UserAccountUtil.getAccount(mContext), day, beginTime, endTime);
            insterOutlineData(mContext, day, beginTime, endTime, type, calorie, stepTotle, outlineHr, "", "0");
        }
        else if(2 == pak){}
        else if(3 == pak){}
        else if(4 == pak)
        {
            dealHasMovementTypeOutLineData(mContext, data, day, beginTime, endTime);
        }
    }

    public OutlineDataDealer(Context context, String result)
    {
        try {
            JSONObject json = new JSONObject(result);
            String code = json.getString("code");
            if(code != null && code.equals("9003"))
            {
                String data = json.getString("data");
                JSONObject innerJson = new JSONObject(data);
                parseRemoteData(context, innerJson.getString("offlineData"), innerJson.getString("time"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseRemoteData(Context context, String offline, String day) throws JSONException
    {
        if(offline != null && offline.equals(""))return;
        JSONObject jsonw = new JSONObject(offline);
        Iterator<String> keySet = jsonw.keys();
        while (keySet.hasNext())
        {
            String key = keySet.next();
            String value = jsonw.getString(key);
            JSONObject jsonOtherData = new JSONObject(value);
            String heartRate = jsonOtherData.getString("heart");
            int calorie = jsonOtherData.getInt("calorie");
            int step = jsonOtherData.getInt("step");
            String movementType = jsonOtherData.getString("movementType");
            String[] times = parseTime(key);
            heartRate = formateHr(heartRate);
            int moveType;
            try
            {
                moveType = Integer.parseInt(movementType);
            }catch (NumberFormatException e)
            {
                moveType = 6;
            }
            Log.i("updateData", "远程离线数据：" + times[0] + "--" + heartRate);
            insterOutlineData(context, day, times[0], times[1], String.valueOf(moveType), calorie, step, heartRate, movementType, "1");
        }
    }

    private String formateHr(String heartRate)
    {
        StringBuffer res = new StringBuffer();
        if(heartRate.contains(","))
        {
            String[] hr = heartRate.split(",");
            for (int i = 0; i < hr.length; i++)
            {
                String each = Integer.toHexString(Integer.parseInt(hr[i]));
                if(each.length() == 1)
                {
                    each = "0" + each;
                }
                res.append(each);
            }
        }
        else
        {
            String each = Integer.toHexString(Integer.parseInt(heartRate));
            if(each.length() == 1)
            {
                each = "0" + each;
            }
            res.append(each);
        }
        return res.toString();
    }

    private String[] parseTime(String key)
    {
        String[] re = new String[2];
        if(key.contains("-"))
        {
            String[] keys = key.split("-");
            long date01 = Long.parseLong(keys[0]);
            long date02 = Long.parseLong(keys[1]);
            SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            re[0] = formate.format(new Date(date01 * 1000));
            re[1] = formate.format(new Date(date02 * 1000));
        }
        return re;
    }

    /**
     * 处理手表带运动类型的离线数据
     * @param context
     * @param data
     * @param day
     * @param beginTime
     * @param endTime
     */
    private void dealHasMovementTypeOutLineData(Context context, byte[] data, String day, String beginTime, String endTime)
    {
//        0a752d58 54772d58 04 01 04000000 00000000 7916
        int type = data[9] & 0xff;
        int cal = Math.abs(FormatUtils.byte2Int(data, 10));
        int step = Math.abs(FormatUtils.byte2Int(data, 14));
        String types = String.valueOf(type - 1);
        String outlineHr = getTheAverageHR(context, UserAccountUtil.getAccount(context), day, beginTime, endTime);
        insterOutlineData(context, day, beginTime, endTime, types, cal, step, outlineHr, "", "0");
//        String userAccount = UserAccountUtil.getAccount(context.getApplicationContext());
//        Cursor cursor = MyDBHelperForDayData.getInstance(context).selectOutLineData(context, userAccount, day, DeviceTypeUtils.getDeviceType(context));
//        boolean isHas = false;
//        if(cursor.getCount() > 0)
//        {
//            isHas = judgeTime(beginTime, endTime, cursor);
//        }
//
//        if(!isHas)
//        {
//
//            Log.i(TAG, "查询数据库有数据无重复数据，可以添加" + beginTime + "--" + endTime);
//            // 获取平均心率
//            String outlineHr = getTheAverageHR(context, userAccount, day, beginTime, endTime);
//            // 将数据存入数据库
//            MyDBHelperForDayData dbHelperForDayData = MyDBHelperForDayData.getInstance(context);
//            dbHelperForDayData.insertOutLineData(context, userAccount, types, day, beginTime, endTime, cal, step, outlineHr, "", DeviceTypeUtils.getDeviceType(context));
//            context.sendBroadcast(new Intent(MyConfingInfo.NOTIFY_MAINACTIVITY_TO_UPDATE_OUTLINE_DATA));
//        }
    }

    /**
     * 保存离线数据
     * @param mContext
     * @param day
     * @param beginTime
     * @param endTime
     * @param type
     */
    private void insterOutlineData(Context mContext, String day, String beginTime, String endTime, String type,
                                   int kacl, int steps, String outlineHr, String moveName, String flag)
    {
        boolean isHas = false;
        String userAccount = UserAccountUtil.getAccount(mContext);
        Cursor cursor = MyDBHelperForDayData.getInstance(mContext).selectOutLineData(mContext, userAccount, day,
                                                                                     DeviceTypeUtils.getDeviceType(mContext));
        if(cursor.getCount() != 0)
        {
            isHas = judgeTime(beginTime, endTime, cursor);
        }
        if(!isHas)
        {
            // 将数据存入数据库
            MyDBHelperForDayData dbHelperForDayData = MyDBHelperForDayData.getInstance(mContext);
            dbHelperForDayData.insertOutLineData(mContext, userAccount, type, day, beginTime, endTime, kacl,
                                                 steps, outlineHr, moveName, DeviceTypeUtils.getDeviceType(mContext), flag);
            mContext.sendBroadcast(new Intent(MyConfingInfo.NOTIFY_MAINACTIVITY_TO_UPDATE_OUTLINE_DATA));
        }
        else
        {
            String typeDB = "-1";
            if(cursor.moveToFirst())
            {
                do {
                    typeDB = cursor.getString(cursor.getColumnIndex("type"));
                }while (cursor.moveToNext());
            }
            if(type != null && !type.equals(typeDB) && type.length() < typeDB.length())
            {
                MyDBHelperForDayData.getInstance(mContext)
                        .updateOutLineData(mContext, userAccount, day, beginTime,
                                           endTime, "type", Integer.parseInt(type),
                                           "sportName", moveName, DeviceTypeUtils.getDeviceType(mContext), "1");
            }
            Log.i(TAG, "重复离线数据，无需保存");
        }
    }



    public void saveOutlineData(OutLineDataEntity outLineDataEntity, Context mContext)
    {
        String userAccount = UserAccountUtil.getAccount(mContext);
        // 先查询数据库，判断是否是同一条数据重复上传，是则忽略，不是则添加
        String beginTime = outLineDataEntity.getTime().split("=")[0];
        String endTime = outLineDataEntity.getTime().split("=")[1];
        int kcals = outLineDataEntity.getCalorie();
        int stepss = outLineDataEntity.getStepCount();
        String hr = outLineDataEntity.getHeartReat();
        insterOutlineData(mContext, outLineDataEntity.getDay(), beginTime, endTime,
                          String.valueOf(outLineDataEntity.getType()), kcals, stepss, hr, outLineDataEntity.getSportName(), "0");
//        Cursor cursor = MyDBHelperForDayData.getInstance(mContext).selectOutLineData(mContext, userAccount, outLineDataEntity.getDay(), DeviceTypeUtils.getDeviceType(mContext));
//        boolean isHas = false;
//        if(cursor.getCount() != 0)
//        {
//            isHas = judgeTime(beginTime, endTime, cursor);
//            Log.i(TAG, "查询数据库数据" + isHas);
//        }
//        if(!isHas)
//        {
////            String type = String.valueOf(OutLineDataEntity.TYPE_UNKNOW);
//            Log.i(TAG, "查询数据库有数据无重复数据，可以添加" + beginTime + "--" + endTime);
//            // 获取平均心率
////            String outlineHr = getTheAverageHR(mContext, userAccount, outLineDataEntity.getDay(), beginTime, endTime);
//            // 将数据存入数据库
//            MyDBHelperForDayData dbHelperForDayData = MyDBHelperForDayData.getInstance(mContext);
//            dbHelperForDayData.insertOutLineData(mContext,
//                    userAccount, String.valueOf(outLineDataEntity.getType()), outLineDataEntity.getDay(),
//                    beginTime, endTime, outLineDataEntity.getCalorie(),
//                    outLineDataEntity.getStepCount(), outLineDataEntity.getHeartReat(), outLineDataEntity.getSportName(), DeviceTypeUtils.getDeviceType(mContext));
////            mContext.sendBroadcast(new Intent(MyConfingInfo.NOTIFY_MAINACTIVITY_TO_UPDATE_OUTLINE_DATA));
//        }
    }


    public String[] splitTime(long begin, long end)
    {
        String[] times = new String[2];
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String start = format.format(new Date(begin));
        String over = format.format(new Date(end));
        times[0] = start;
        times[1] = over;
        return times;
    }

    /**
     * 比较开始时间和结束时间，正常情况下不可能相同
     * @param beginTime
     * @param endTime
     */
    private boolean judgeTime(String beginTime, String endTime, Cursor cursor)
    {
        boolean isHas = false;
        if(cursor.moveToFirst())
        {
            do {
                String start =  cursor.getString(cursor.getColumnIndex("startTime"));
                String end = cursor.getString(cursor.getColumnIndex("endTime"));
                Log.i(TAG, "数据库离线数据：" + start + "--" + end);
                if(beginTime.equals(start) && endTime.equals(end))
                {
                    isHas = true;
                }
            }while (cursor.moveToNext());
        }
        return isHas;
    }

    /**
     * 根据开始和结束时间截取心率数据
     * @param mContext
     * @param userAccount
     * @param day
     * @param beginTime
     * @param endTime
     * @return
     */
    public String getTheAverageHR(Context mContext, String userAccount, String day, String beginTime, String endTime)
    {

//        yyyy-MM-dd HH:mm
        String[] columns = new String[]{"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
                "one1", "two1", "three1", "four1", "five1", "six1", "seven1", "eight1", "nine1", "ten1",
                "one2", "two2", "three2", "four2"};
        String beginDay = beginTime.substring(0, 10);
        String endDay = endTime.substring(0, 10);
        if(beginDay != null && endDay != null)
        {
            if(beginDay.equals(endDay))     // 同一天
            {
                String[] sHourAndMinute = beginTime.substring(11).split(":");
                String[] eHourAndMinute = endTime.substring(11).split(":");
                int startHour = Integer.valueOf(sHourAndMinute[0]);
                int endHour = Integer.valueOf(eHourAndMinute[0]);
                int startMinute = Integer.valueOf(sHourAndMinute[1]);
                int endMinute = Integer.valueOf(eHourAndMinute[1]);
                Log.i(TAG, "截取当前离线运动全部格式：开始时间：" + beginTime + "结束时间：" + endTime);
                String splitData = "";
                for (int i = startHour; i < (endHour - startHour) + startHour + 1; i++)
                {
                    Log.i(TAG, "截取当前离线运动：开始时间：" + startHour + "结束时间：" + endHour + columns[i]);
                    Cursor mCursor = MyDBHelperForDayData.getInstance(mContext).selecteOneColumnHr(mContext, userAccount, day, columns[i], DeviceTypeUtils.getDeviceType(mContext));
                    splitData += parseTheHRCursor(mCursor, columns[i]);
                }
                Log.i(TAG, "截取当前离线运动对应的心率数据：" + splitData);
                byte[] outHrData = FormatUtils.hexString2ByteArray(splitData);
                endMinute = outHrData.length - (60 - endMinute);
                int re = 1;
                if(endMinute - startMinute <= 0)
                {
                    re = 1;
                }
                else
                {
                    re = endMinute - startMinute;
                }

                byte[] realData = new byte[re];
                for (int i = startMinute; i < endMinute; i++)
                {
                    realData[i - startMinute] = outHrData[i];
                }
                return FormatUtils.bytesToHexString(realData);
            }
            else                            // 非同一天
            {

                long startMill = parseFormatToMill(beginTime);
                long endMill = parseFormatToMill(endTime);
                int totalTime = (int)((endMill - startMill) / 1000/ 60 / 60);
                Log.i(TAG, "开始毫秒:" + startMill + "结束毫秒" + endMill + "--" + "开始具体时间:" + beginTime + "结束具体时间:" + endTime);
                Log.i(TAG, "测试时间时长:" + (endMill - startMill) + "转换后的时长:" + totalTime);
                String[] sHourAndMinute = beginTime.substring(11).split(":");
                String[] eHourAndMinute = endTime.substring(11).split(":");

                int startDayHour = Integer.valueOf(sHourAndMinute[0]);
                int endDayHour = Integer.valueOf(eHourAndMinute[0]);
                int startMinute = Integer.valueOf(sHourAndMinute[1]);
                int endMinute = Integer.valueOf(eHourAndMinute[1]);


                Log.i(TAG, "开始的小时:" + startDayHour + "结束时间的小时:" + endDayHour);

                int eachDay = totalTime / (24 + startDayHour);

                Log.i(TAG, "所有时间小时" + totalTime + "--" + eachDay);

                String splitData = "";
                for (int i = eachDay; i > -1 ; i--)
                {
                    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                    calendar.set(Integer.valueOf(beginTime.substring(0, 4)),
                            Integer.valueOf(beginTime.substring(5, 7)) - 1,
                            Integer.valueOf(beginTime.substring(8, 10)) - i);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String dayes = format.format(calendar.getTime());
                    Log.i(TAG, "查询前一天的日期" + dayes);
                    if(i == eachDay)
                    {
                        for (int j = startDayHour; j < 24; j++)
                        {
                            Log.i(TAG, "开始小时" + startDayHour);
                            Cursor mCursor = MyDBHelperForDayData.getInstance(mContext).selecteOneColumnHr(mContext, userAccount, dayes, columns[j], DeviceTypeUtils.getDeviceType(mContext));
                            String resultS = parseTheHRCursor(mCursor, columns[j]);
                            if(resultS != null && resultS.equals(""))
                            {
                                resultS = MyConfingInfo.DISABLE_DATA;
                            }
                            splitData += resultS;
                        }
                    }
                    else
                    {
                        for (int s = 0; s < 24; s++)
                        {
                            Cursor mCursor = MyDBHelperForDayData.getInstance(mContext).selecteOneColumnHr(mContext, userAccount, dayes, columns[s], DeviceTypeUtils.getDeviceType(mContext));
                            String res = parseTheHRCursor(mCursor, columns[s]);
                            if(res != null && res.equals(""))
                            {
                                res = MyConfingInfo.DISABLE_DATA;
                            }
                            splitData += res;
                        }
                    }
                }

                for (int d = 0; d <= endDayHour;d++)
                {
                    Cursor mCursor = MyDBHelperForDayData.getInstance(mContext).selecteOneColumnHr(mContext, userAccount, day, columns[d], DeviceTypeUtils.getDeviceType(mContext));
                    String resS = parseTheHRCursor(mCursor, columns[d]);
                    if(resS != null && resS.equals(""))
                    {
                        resS = MyConfingInfo.DISABLE_DATA;
                    }
                    splitData += resS;
                }
                Log.i(TAG, "跨天离线数据心率:" + splitData);

                byte[] dataes = FormatUtils.hexString2ByteArray(splitData);
                byte[] da = new byte[dataes.length - (60 - endMinute) - startMinute];
                System.arraycopy(dataes, startMinute, da, 0, dataes.length - (60 - endMinute) - startMinute);

                return FormatUtils.bytesToHexString(da);
            }
        }
        return null;
    }


    private long parseFormatToMill(String beginTime)
    {
        TimeZone tz = TimeZone.getDefault();
        int offset = tz.getRawOffset();
        Date date = null;
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            date = format.parse(beginTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        return calendar.getTimeInMillis();
    }

    private String parseTheHRCursor(Cursor mCursor, String column)
    {
        String data = "";
        if(mCursor.getCount() != 0)
        {
            if(mCursor.moveToFirst())
            {
                do {
                    data =  mCursor.getString(mCursor.getColumnIndex(column));
                }while (mCursor.moveToNext());
                mCursor.close();
            }
        }

        return (data != null && !data.equals("")) ? data : "";
    }
}
