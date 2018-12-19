package com.huichenghe.blecontrol.Ble;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.huichenghe.blecontrol.Utils.FormatUtils;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 处理离线数据的帮助类
 * Created by lixiaoning on 15-11-17.
 */
public class BleBaseDataForOutlineMovement extends BleBaseDataManage
{
    public static final String TAG = BleBaseDataForOutlineMovement.class.getSimpleName();
    public static final byte mNotify_cmd = (byte)0xa2;
    public static final byte toDevice = (byte)0x22;
//    private byte[] outLineData;


    //68 a2 1100 8b436d38 9d436d38 01 0000000000000000 1416
    public void dealTheData(Context mContext, byte[] data, int length)
    {
//        this.outLineData = data;
//      15665c56 16675c56 01 04000000 2c000000 a816
        Log.i(TAG, "解析手环端上传的离线数据" + FormatUtils.bytesToHexString(data));
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
            Log.i(TAG, "离线数据卡路里：" + calorie + "计步：" + stepTotle);

            String userAccount = UserAccountUtil.getAccount(mContext);
            // 先查询数据库，判断是否是同一条数据重复上传，是则忽略，不是则添加
            Cursor cursor = MyDBHelperForDayData.getInstance(mContext).selectOutLineData(mContext, userAccount, day);
            if(cursor.getCount() != 0)
            {
                isHas = judgeTime(beginTime, endTime, cursor);
                Log.i(TAG, "查询数据库数据" + isHas);
            }
            if(!isHas)
            {
                String type = String.valueOf(OutLineDataEntity.TYPE_UNKNOW);
                Log.i(TAG, "查询数据库有数据无重复数据，可以添加" + beginTime + "--" + endTime);
                // 获取平均心率
                String outlineHr = getTheAverageHR(mContext, userAccount, day, beginTime, endTime);
                // 将数据存入数据库
                MyDBHelperForDayData dbHelperForDayData = MyDBHelperForDayData.getInstance(mContext);
                dbHelperForDayData.insertOutLineData(mContext, userAccount, type, day, beginTime, endTime, calorie, stepTotle, outlineHr, "");
                mContext.sendBroadcast(new Intent(MyConfingInfo.NOTIFY_MAINACTIVITY_TO_UPDATE_OUTLINE_DATA));
            }
        }else if(2 == pak)
        {

        }else if(3 == pak)
        {

        }


        // 发送响应数据
//        byte[] back = new byte[sosPhone];
//        for (int i = 0; i < back.length; i ++)
//        {
//            back[i] = data[i];
//        }
//
//
//        setMsgToByteDataAndSendToDevice(toDevice, back, back.length, true);

    }

    public void requstOutlineData(byte[] data)
    {
        // 发送响应数据
        byte[] back = new byte[9];
//        for (int i = 0; i < back.length; i ++)
//        {
//            back[i] = data[i];
//        }
        System.arraycopy(data, 0, back, 0, 9);
        setMsgToByteDataAndSendToDevice(toDevice, back, back.length);
    }

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
                    Cursor mCursor = MyDBHelperForDayData.getInstance(mContext).selecteOneColumnHr(mContext, userAccount, day, columns[i]);
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
                            Cursor mCursor = MyDBHelperForDayData.getInstance(mContext).selecteOneColumnHr(mContext, userAccount, dayes, columns[j]);
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
                            Cursor mCursor = MyDBHelperForDayData.getInstance(mContext).selecteOneColumnHr(mContext, userAccount, dayes, columns[s]);
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
                    Cursor mCursor = MyDBHelperForDayData.getInstance(mContext).selecteOneColumnHr(mContext, userAccount, day, columns[d]);
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

    private int getTheHalfUp(float averageHr)
    {
        BigDecimal bigDecimal = new BigDecimal(averageHr);
        bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP);
        return bigDecimal.intValue();
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


    public String[] splitTime(long begin, long end)
    {
        String[] times = new String[2];
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(begin);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String start = format.format(new Date(begin));
        String over = format.format(new Date(end));
        times[0] = start;
        times[1] = over;
        return times;
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        int minute = calendar.get(Calendar.MINUTE);
//        return hour + ":" + minute;
    }






}
