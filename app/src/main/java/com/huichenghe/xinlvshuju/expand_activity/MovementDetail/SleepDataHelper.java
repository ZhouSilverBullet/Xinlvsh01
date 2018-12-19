package com.huichenghe.xinlvshuju.expand_activity.MovementDetail;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.UpdataService.DeviceTypeUtils;

/**
 * Created by lixiaoning on 2016/8/8.
 */
public class SleepDataHelper
{
    private Context context;
    public SleepDataHelper(Context context)
    {
        this.context = context;
    }
    public static final String TAG = SleepDataHelper.class.getSimpleName();
    public String[] loadSleepData(String yestoday, String today, String userAccount)
    {
        String yesterdayData = "";
        String[] backData = new String[3];
        // 昨天睡眠数据
        if(yestoday != null && !yestoday.equals(""))
        {
            Cursor cursor = MyDBHelperForDayData.getInstance(context)
                    .selectTheSleepData(context, userAccount, yestoday, DeviceTypeUtils.getDeviceType(context));
            yesterdayData = parseCursor(cursor);
        }
        // 今天睡眠数据
        Cursor tCursor1 = MyDBHelperForDayData.getInstance(context)
                .selectTheSleepData(context, userAccount, today, DeviceTypeUtils.getDeviceType(context));
        String todayData = parseCursor(tCursor1);
//        Log.i(TAG, "当天睡眠数据:" + todayData);
        // 截取昨天的数据, 从数据末尾截取12个字符，代表两个小时
        String y = "";
        if(yesterdayData.length() > 1)
        {
            String res = yesterdayData.replaceAll("\\d{12}$", "");
            y = yesterdayData.replaceAll(res, "");
        }
        // 截取今天的数据， 从数据开头截取60个字符，代表10小时
        String t = "";
        if(todayData.length() > 0)
        {
            t = todayData.substring(0, 60);
        }
        String allData = y + t;
        int start = 0;
        String first = allData.replaceAll("^[0, 3]+", "");
        start = allData.length() - first.length();
        String second = allData.replaceAll("[0, 3]+$", "");
        int end = allData.length() - second.length();
        final String finalString = first.replaceAll("[0, 3]+$", "");
        Log.i(TAG, "全部数据最终数据：" + finalString );
        Log.i(TAG, "全部数据开始位置：" + start);
        Log.i(TAG, "全部数据结束位置：" + end);
//        String ridStart = first.replaceAll("^[info]+", "");                // 去掉开头的3，
//        String alw = ridStart.replaceAll("^[0]+", "");
//        String ridEnd = alw.replaceAll("[0]+$", "");                    // 去掉结尾的0；
//        ridEnd = ridEnd.replaceAll("[info]+$", "");                        // 去掉结尾的3；      // 这是最终的数据
//        ridEnd = ridEnd.replaceAll("[0]+$", "");
//        String onlyRidEnd = dataSs.replaceAll("[0]+$", "");
//        onlyRidEnd = onlyRidEnd.replaceAll("[info]+$", "");
        int sleepTimeStart = start * 10;
        int sleepTimeEnd = end * 10;
        // 构造开始时间
        int invalidHour = sleepTimeStart / 60;
        int invalidMinute = sleepTimeStart % 60;
        int startHour;
        if(y != null && !y.equals(""))
        {
            startHour = 22 + invalidHour;
            if(startHour >= 24)
            {
                startHour = startHour - 24;
            }
        }
        else
        {
            startHour = invalidHour;
        }
        String minu = String.valueOf(invalidMinute);
        if(invalidMinute < 10)
        {
            minu = "0" + invalidMinute;
        }
        final String startH = startHour + ":" + minu;


        int inHour = sleepTimeEnd / 60;
        int inMinute = sleepTimeEnd % 60;
        if(y != null && !y.equals("") && t != null && t.equals(""))
        {
            inHour = 24 - inHour;
            if(inHour >= 24)return null;
            if(inMinute > 0)
            {
                inHour = inHour - 1;
                inMinute = 60 - inMinute;
            }
        }
        else
        {
            // 构造结束时间

            Log.i(TAG, "睡眠结束时间" + sleepTimeEnd + "-" + inHour + "--" + inMinute);
            if(inMinute != 0)
            {
                inHour = 10 - inHour -1;
                inMinute = 60 - inMinute;
            }
            else
            {
                inHour = 10 - inHour;
            }
            if(inHour < 0)
            {
                inHour = 0;
            }
        }
        final String endH = inHour + ":" + ((inMinute < 10) ? "0" + inMinute : String.valueOf(inMinute));



//        Log.i(TAG, "过滤后的睡眠数据：" + finalString);
//        ((Detail_activity)mContext).runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                histrogramSleep.setSleepData(startH, endH, finalString);
//                histrogramSleep.invalidate();
        // 更新显示, 获取深睡和浅睡时间
//                updateTheTextAboutDeepAndLightAndCompletion(finalString);
//            }
//        });
        backData[0] = startH;
        backData[1] = endH;
        backData[2] = finalString;
        return backData;
    }

    /**
     * 解析查询数据库返回的cursor
     * @param cursor
     * @return
     */
    private String parseCursor(Cursor cursor)
    {
        String data = "";
        if(cursor.getCount() != 0)
        {
            if(cursor.moveToFirst())
            {
                do {
                    data = cursor.getString(cursor.getColumnIndex("sleepData"));
                }while (cursor.moveToNext());
            }
        }
        return  data;
    }


}
