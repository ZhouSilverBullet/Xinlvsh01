package com.huichenghe.xinlvshuju.mainpack;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.huichenghe.xinlvshuju.BleDeal.OutlineDataDealer;
import com.huichenghe.xinlvshuju.DataEntites.OutLineDataEntity;
import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.UpdataService.DeviceTypeUtils;
import com.huichenghe.xinlvshuju.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.http.UserAccountUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * Created by lixiaoning on 2016/7/11.
 */
public class OutlineDataHelper
{
    public static final String TAG = OutlineDataHelper.class.getSimpleName();
    private Context context;
    public OutlineDataHelper(Context context)
    {
        this.context = context;
    }
    public Cursor readDbAndshow(String todayDate)
    {
//			Calendar calendar = Calendar.getInstance();
//			Date d = calendar.getTime();
//			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//			String day = format.format(d);
        String userAccount = UserAccountUtil.getAccount(context);
        MyDBHelperForDayData myHelper = MyDBHelperForDayData.getInstance(context);
        Cursor mCursor = myHelper.selectOutLineData(context, userAccount, todayDate, DeviceTypeUtils.getDeviceType(context));
        Log.i(TAG, "查询结果：" + mCursor.getCount() + todayDate);
       return mCursor;
    }




    /**
     * 遍历Cursor，取出数据并显示出来
     *
     * @param mCursor
     */
    public ArrayList<OutLineDataEntity> getTheDataFromCursor(Cursor mCursor, ArrayList<OutLineDataEntity> dataList)
    {	//startTime, endTime, calorie, stepTotle
        if (mCursor.moveToFirst())        // 游标移动到开头
        {
            do {
                String startTime = mCursor.getString(mCursor.getColumnIndex("startTime"));
                String endTime = mCursor.getString(mCursor.getColumnIndex("endTime"));
                int calorie = mCursor.getInt(mCursor.getColumnIndex("calorie"));
                int stepTotle = mCursor.getInt(mCursor.getColumnIndex("stepTotle"));
                int type = mCursor.getInt(mCursor.getColumnIndex("type"));
                String day = mCursor.getString(mCursor.getColumnIndex("day"));
                Log.i(TAG, "运动类型：" + type);
                String heartRate = mCursor.getString(mCursor.getColumnIndex("heartRate"));
                heartRate = checkHeartRate(heartRate, startTime, endTime);
                String sportName = mCursor.getString(mCursor.getColumnIndex("sportName"));
                OutLineDataEntity entites = new OutLineDataEntity(type, startTime + "=" + endTime, stepTotle, calorie, heartRate, day, sportName);
                if(!dataList.contains(entites))
                {
                    dataList.add(entites);
                }
            } while (mCursor.moveToNext());
            mCursor.close();
            Log.i(TAG, "更新离线数据界面");
            Collections.sort(dataList, new Comparator<OutLineDataEntity>() {
                @Override
                public int compare(OutLineDataEntity lhs, OutLineDataEntity rhs) {
                    String firstTimes = lhs.getTime();
                    String secondTimes = rhs.getTime();

                    if (secondTimes != null && firstTimes != null) {
                        String[] tim = firstTimes.split("=");
                        String end1 = tim[1];
                        String timeStart = end1.substring(11);

                        String[] ti = secondTimes.split("=");
                        String end2 = ti[1];
                        String timeEnd = end2.substring(11);
                        String[] time1 = timeStart.split(":");
                        String[] time2 = timeEnd.split(":");

                        if (Integer.parseInt(time1[0]) == Integer.parseInt(time2[0])) {
                            if (Integer.parseInt(time1[1]) > Integer.parseInt(time2[1])) {
                                return -1;
                            } else if (Integer.parseInt(time1[1]) < Integer.parseInt(time2[1])) {
                                return 1;
                            } else {
                                return 0;
                            }
                        } else {
                            if (Integer.parseInt(time1[0]) > Integer.parseInt(time2[0])) {
                                return -1;
                            } else if (Integer.parseInt(time1[0]) < Integer.parseInt(time2[0])) {
                                return 1;
                            } else {
                                return 0;
                            }
                        }
                    }
                    return 0;
                }
            });
//			itemAdapter.notifyDataSetChanged();
        }
        return dataList;
    }

    /**
     * 校验心率数据
     * @param heartRate
     * @param startTime
     * @param endTime
     */
    private String checkHeartRate(String heartRate, String startTime, String endTime)
    {
        String hrResult = heartRate;
        byte[] dataHR =  FormatUtils.hexStringToByteArray(heartRate);
        int totleHr = 0;
        int hrCount = 0;
        for (int i = 0; i < dataHR.length; i++)
        {
            if((dataHR[i] & 0xff) > 0)
            {
                totleHr += dataHR[i] & 0xff;
                hrCount++;
            }
        }
        int av = 0;
        if(hrCount != 0)
        {
            av = totleHr / hrCount;
        }
        if(av == 0)
        {
            String day = endTime.substring(0, 10);
            String account = UserAccountUtil.getAccount(context);
            hrResult = new OutlineDataDealer().getTheAverageHR(context, account, day, startTime, endTime);
            try {
                MyDBHelperForDayData.getInstance(context).updateOutLineHr(context, account, day, startTime, endTime, hrResult, DeviceTypeUtils.getDeviceType(context));
            }catch (Exception e)
            {
                Log.i(TAG, "没有该条历史数据");
            }

        }
        return hrResult;
    }


    /**
     * 更新主界面离线数据
     */
    private void updateOutLineData()
    {
        String day = null;
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        day = format.format(date);


//		int date = calendar.get(Calendar.DATE);
//		if (date < 10) {
//			dat = "0" + date;
//		}
//		else
//		{
//			dat = String.valueOf(date);
//		}
//		int mon = calendar.get(Calendar.MONTH) + 1;
//		int year = calendar.get(Calendar.YEAR);
//		String day = year + "-" + mon + "-" + dat;
        Log.i(TAG, "MainActivity查询日期：" + day);
        String account = UserAccountUtil.getAccount(context);
        Cursor mCursor = MyDBHelperForDayData.getInstance(context)
                .selectOutLineData(context, account, day, DeviceTypeUtils.getDeviceType(context));

        Log.i(TAG, "MainActivity离线数据" + mCursor.getCount());
        if (mCursor.getCount() != 0) {
            Log.i(TAG, "有离线数据");
//			ergodicCursor(mCursor);        // 遍历cursor,取出数据
//            getTheDataFromCursor(mCursor);
        }
    }

}
