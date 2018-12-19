package com.huichenghe.xinlvshuju.CustomView;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huichenghe.xinlvshuju.Adapter.OutlineDetailWindowAdapter;
import com.huichenghe.xinlvshuju.DataEntites.EntityForOutline;
import com.huichenghe.xinlvshuju.DataEntites.OutLineDataEntity;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by lixiaoning on 2016/10/10.
 */
public class Movement_deatail_window
{
    public final String TAG = "Movement_deatail_window";
    private PopupWindow detialWindow;
    private OutlineDetailWindowAdapter outlineDetailWindowAdapter;
    private static Movement_deatail_window movement_deatail_windows;


    public static Movement_deatail_window getPopWindowInstance()
    {
        if(movement_deatail_windows == null)
        {
            synchronized (Movement_deatail_window.class)
            {
                if(movement_deatail_windows == null)
                {
                    movement_deatail_windows = new Movement_deatail_window();
                }
            }
        }
        return movement_deatail_windows;
    }


    private Movement_deatail_window()
    {
    }
    public void showTheOutlinDetialWindow(OutLineDataEntity obj, Context context) {

        View view = getTheXMLLayout(obj, context);
        ImageView imageView = (ImageView)view.findViewById(R.id.back_to_mainactivity);
        imageView.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                if(detialWindow != null)
                {
                    detialWindow.dismiss();
                }
            }
        });
        if(detialWindow == null)
        {
            detialWindow =
                    new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        }
        detialWindow.setFocusable(true);
        detialWindow.setAnimationStyle(R.style.mypopupwindow_anim_style_center);
        detialWindow.setBackgroundDrawable(new BitmapDrawable());
        detialWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        detialWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss()
            {
                detialWindow = null;
            }
        });
    }

    private View getTheXMLLayout(OutLineDataEntity obj, Context context)
    {
        ArrayList<EntityForOutline> RecyclerData = new ArrayList<EntityForOutline>();
        int age = getUserAge(context);
        int maxHRCompare = 220 - age;
        String[] outlineType = new String[]{context.getString(R.string.walk), context.getString(R.string.running),
                context.getString(R.string.climing), context.getString(R.string.ball),
                context.getString(R.string.muscle), context.getString(R.string.aerobic)};
        View v = LayoutInflater.from(context).inflate(R.layout.popwindow_for_outline_detail, null);
        TextView title = (TextView)v.findViewById(R.id.detail_title);
        TextView totalTimes = (TextView)v.findViewById(R.id.total_times_for_outline);
        TextView averageHR = (TextView)v.findViewById(R.id.average_hr_for_outline);
        TextView calorie = (TextView)v.findViewById(R.id.calorie_for_outline);
        TextView outlineStrenth = (TextView)v.findViewById(R.id.outline_strenth);
        Circle_Percentage_View circle_percentage_view = (Circle_Percentage_View) v.findViewById(R.id.each_movement_percentage);
        int Type = obj.getType();
        String typeString;
        if(Type == OutLineDataEntity.TYPE_CUSTOM)
        {
            typeString = obj.getSportName();
        }
        else
        {
            typeString = outlineType[Type];
        }
        String[] tatTimes = getTheOutlineTotalTimes(obj.getTime());
        int tatCalorie = obj.getCalorie();
        String ourlineHR = obj.getHeartReat();
        Log.i(TAG, "离线心率数据:" + ourlineHR);
        if(ourlineHR != null && ourlineHR.equals(""))
        {
            ourlineHR = "00";
        }
        int allHR = 0;
        int count = 0;
        int average = 0;
        int[] minutes = new int[5];
        if(ourlineHR != null && !ourlineHR.equals("null"))
        {
            if(ourlineHR != null && !ourlineHR.equals(""))
            {
                byte[] outlineHrData = FormatUtils.hexString2ByteArray(ourlineHR);
                for (int i = 0; i < outlineHrData.length; i++)
                {
                    if(outlineHrData[i] == (byte)0xff || outlineHrData[i] == (byte)0x00)
                    {
                        outlineHrData[i] = 0;
                    }
                    int hr = (outlineHrData[i] & 0xff);
                    if(hr > 0)
                    {
                        allHR += hr;
                        count ++;
                    }
                    if(hr >= (int)(maxHRCompare * 0.9))
                    {
                        minutes[0] ++;
                    }
                    else if(hr >= (int)(maxHRCompare * 0.8))
                    {
                        minutes[1] ++;
                    }
                    else if(hr >= (int)(maxHRCompare * 0.7))
                    {
                        minutes[2] ++;
                    }
                    else if(hr >= (maxHRCompare * 0.6))
                    {
                        minutes[3] ++;
                    }
                    else if(hr < (maxHRCompare * 0.6))
                    {
                        minutes[4] ++;
                    }
                }
                if(allHR != 0 && count != 0)
                {
                    float averageHRF = (float)allHR / (float)count;
//                    Log.i(TAG, "心率数据时长:" + averageHRF);
                    average = getHalfUp(averageHRF);
                }
            }
        }
        String intencity = getTheExerciseIntensity(minutes, context);
        title.setText(typeString);
        totalTimes.setText(tatTimes[0]);
        averageHR.setText(String.valueOf(Math.abs(average)));
        calorie.setText(String.valueOf(tatCalorie));
        outlineStrenth.setText(intencity);
        String aaText = context.getString(R.string.minute_has_prefix);
        int heartOne = (int)(maxHRCompare * 0.6);
        int heartTwo = (int)(maxHRCompare * 0.7);
        int heartThree = (int)(maxHRCompare * 0.8);
        int heartFour = (int)(maxHRCompare * 0.9);
        int allMinute = minutes[0] + minutes[1] + minutes[2] + minutes[3] + minutes[4];
        if(tatTimes[1] != null)
        {
            int offset = Integer.parseInt(tatTimes[1]) - allMinute;
            minutes[4] += offset;
            allMinute += offset;
        }
        else
        {
            minutes[4] = 0;
        }
        RecyclerData.add(new EntityForOutline(0, (allMinute == 0) ? "0" : getComp(minutes[4], allMinute), String.valueOf(minutes[4]), "≤" + heartOne, context.getString(R.string.warm_up_for_outline)));
        RecyclerData.add(new EntityForOutline(1, (allMinute == 0) ? "0" : getComp(minutes[3], allMinute), String.valueOf(minutes[3]), heartOne + "-" + heartTwo, context.getString(R.string.fat_for_outline)));
        RecyclerData.add(new EntityForOutline(2, (allMinute == 0) ? "0" : getComp(minutes[2], allMinute), String.valueOf(minutes[2]), heartTwo + "-" + heartThree, context.getString(R.string.has_02_for_outline)));
        RecyclerData.add(new EntityForOutline(3, (allMinute == 0) ? "0" : getComp(minutes[1], allMinute), String.valueOf(minutes[1]), heartThree + "-" + heartFour, context.getString(R.string.not_02_for_outline)));
        RecyclerData.add(new EntityForOutline(4, (allMinute == 0) ? "0" : getComp(minutes[0], allMinute), String.valueOf(minutes[0]), "≥" + heartFour, context.getString(R.string.limit)));
        circle_percentage_view.setEachDegree((allMinute == 0) ? 0 : (float)minutes[4]/(float)allMinute,
                (allMinute == 0) ? 0 : (float)minutes[3]/(float)allMinute,
                (allMinute == 0) ? 0 : (float)minutes[2]/(float)allMinute,
                (allMinute == 0) ? 0 : (float)minutes[1]/(float)allMinute,
                (allMinute == 0) ? 0 : (float)minutes[0]/(float)allMinute);
        RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.recycler_for_xinlv_state);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        View headView = LayoutInflater.from(context).inflate(R.layout.head_view_movement, null);
        outlineDetailWindowAdapter = new OutlineDetailWindowAdapter(context, RecyclerData, headView);
        recyclerView.setAdapter(outlineDetailWindowAdapter);
        return v;
    }

    private String getComp(int minute, int count)
    {
        float com = (float)minute/count * 100;
        return String.valueOf(Math.round(com));
    }

    private String getTheExerciseIntensity(int[] minutess, Context context)
    {
        String result = context.getString(R.string.very_low_grade);
        String[] Intensity = new String[]{context.getString(R.string.very_height), context.getString(R.string.nomal_height),
                context.getString(R.string.medium), context.getString(R.string.low_grade), context.getString(R.string.very_low_grade)};
        for (int i = 0; i < minutess.length; i ++)
        {
//            Log.i(TAG, "数据长度:" + minutess.length);
            if(i == 0 )
            {
                if(minutess[0] >= 5)
                {
//                    Log.i(TAG, "心率数据：===" + minutess[i]);
                    result = Intensity[i];
                    break;
                }
                else
                {
                    continue;
                }
            }
            if(i == 1)
            {
                if(minutess[1] >= 10)
                {
//                    Log.i(TAG, "心率数据：=== minutess[1]" + minutess[i]);
                    result = Intensity[1];
                    break;
                }
                else
                {
                    continue;
                }
            }
            if(i == 2)
            {
                if(minutess[2] >= 40)
                {
                    result = Intensity[2];
                    break;
                }
                else
                {
                    continue;
                }
            }
            if(i == 3)
            {
                if(minutess[3] >= 80)
                {
                    result = Intensity[3];
                    break;
                }
                else
                {
                    continue;
                }
            }
//            if(i == 4)
//            {
//                result = Intensity[4];
//                break;
//            }
        }
        return result;

    }

    private int getHalfUp(float averageHRF)
    {
        BigDecimal bigDecimal = new BigDecimal(averageHRF);
        bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP);
        return bigDecimal.intValue();
    }

    private String[] getTheOutlineTotalTimes(String time)
    {
//        Log.i(TAG, "测试时间" + time);2016-40-50 16:89=2016-03-40 34:88
        String[] tTimes = new String[2];
        tTimes[0] = "";

        if(time != null && !time.equals("") && (time != null && !time.equals("2016-03-05 00:00=2016-03-05 00:00")))
        {
            int timeDifference = 0;
            String[] times = time.split("=");
            String startTime = times[0];
            String endTime = times[1];
            int dayDiff = gettheDayDiff(startTime, endTime);
            if(dayDiff == 0)
            {
                String[] startTimes = startTime.substring(11).split(":");
                String[] endTimes = endTime.substring(11).split(":");
                int startMinute = Integer.parseInt(startTimes[0]) * 60 + Integer.parseInt(startTimes[1]);
                int endMinute = Integer.parseInt(endTimes[0]) * 60 + Integer.parseInt(endTimes[1]);
                timeDifference = endMinute - startMinute;
            }
            else
            {
                String[] startTimes = startTime.substring(11).split(":");
                String[] endTimes = endTime.substring(11).split(":");
                int startMinute = 1440 - (Integer.parseInt(startTimes[0]) * 60 + Integer.parseInt(startTimes[1]));
                int firstMinute = startMinute + ((dayDiff - 1) * 24 * 60);
                int secondMinute = Integer.parseInt(endTimes[0]) * 60 + Integer.parseInt(endTimes[1]);
                timeDifference = firstMinute + secondMinute;
            }
            if(timeDifference == 0)
            {
                timeDifference = 1;
            }
            tTimes[1] = String.valueOf(timeDifference);
            int totalHour = timeDifference/60;
            int totalMinute = timeDifference%60;
            if(totalHour > 0)
            {
                tTimes[0] += totalHour + "h";
            }
            if(totalMinute > 0)
            {
                tTimes[0] += totalMinute + "min";
            }
            if(totalMinute <= 0)
            {
                tTimes[0] = 1 + "min";
            }
        }
        else
        {
            tTimes[0] = "0min";
        }
        return tTimes;
    }

    private int getUserAge(Context context)
    {
        String userBirthday = LocalDataSaveTool.getInstance(context.getApplicationContext()).readSp(MyConfingInfo.USER_BIRTHDAY);
//        Log.i(TAG, "用户生日：" + userBirthday);
        int yearBrithday = Integer.parseInt(userBirthday.substring(0, 4));
        Calendar calendar = Calendar.getInstance();
        int yearCurrent = calendar.get(Calendar.YEAR);
        return yearCurrent - yearBrithday;
    }

    private int gettheDayDiff(String startTime, String endTime)
    {
        Calendar startCalendar = Calendar.getInstance(TimeZone.getDefault());
        startCalendar.set(Integer.parseInt(startTime.substring(0, 4)),
                Integer.parseInt(startTime.substring(5, 7)),
                Integer.parseInt(startTime.substring(8, 10)));
        Calendar endCalendar = Calendar.getInstance(TimeZone.getDefault());
        endCalendar.set(Integer.parseInt(endTime.substring(0, 4)),
                Integer.parseInt(endTime.substring(5, 7)),
                Integer.parseInt(endTime.substring(8, 10)));
//		long startIn = startCalendar.getTime().getTime();
//		long endIn = endCalendar.getTime().getTime();
        long endTimes = endCalendar.getTime().getTime();
        long startTimes = startCalendar.getTime().getTime();
        long result = endTimes - startTimes;
        int ss = (int)(result / (1000 * 60 * 60 * 24));
//        Log.i(TAG, "离线数据时间::::" + endTimes + "/" + startTimes + "/" + result + "/" + ss);
        return ss;
    }
}
