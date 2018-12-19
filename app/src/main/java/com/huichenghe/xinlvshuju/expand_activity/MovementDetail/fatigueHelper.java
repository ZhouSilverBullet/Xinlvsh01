package com.huichenghe.xinlvshuju.expand_activity.MovementDetail;

import android.content.Context;

import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by lixiaoning on 2016/7/25.
 */
public class fatigueHelper
{


    public int getUserAger(Context context)
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int year = calendar.get(Calendar.YEAR);
        String birthday = LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.USER_BIRTHDAY);
        if(birthday != null && birthday.equals(""))
        {
            return 0;
        }
        // 2018-09-11
        int bir = Integer.valueOf(birthday.substring(0, 4));
        return  year - bir;
    }


    public int[] getFatigueClass(int age)
    {
        int[] fatigueClass = null;
        if(age < 25)
        {
            fatigueClass = new int[]{50, 40, 30};
        }
        else if(age >= 25 && age < 35)
        {
            fatigueClass = new int[]{45, 35, 20};
        }
        else if(age >= 35 && age < 45)
        {
            fatigueClass = new int[]{40, 25, 15};
        }
        else if(age >= 45 && age < 55)
        {
            fatigueClass = new int[]{35, 20, 10};
        }
        else if(age >= 55 && age < 65)
        {
            fatigueClass = new int[]{30, 15, 5};
        }
        else if(age >= 65)
        {
            fatigueClass = new int[]{25, 15, 5};
        }
        return fatigueClass;
    }
    // 0 健康， 1 正常， 2 危险， 3 高危
    public int getFagigue(int age, int fatigue)
    {
        int result = 0;
        if(age < 25)
        {
            result = getResult(fatigue, 50, 40, 30);
        }
        else if(age >= 25 && age < 35)
        {
            result = getResult(fatigue, 45, 35, 20);
        }
        else if(age >= 35 && age < 45)
        {
            result = getResult(fatigue, 40, 25, 15);
        }
        else if(age >= 45 && age < 55)
        {
            result = getResult(fatigue, 35, 20, 10);
        }
        else if(age >= 55 && age < 65)
        {
            result = getResult(fatigue, 30, 15, 5);
        }
        else if(age >= 65)
        {
            result = getResult(fatigue, 25, 15, 5);
        }
        return result;
    }

    private int getResult(int fatigue, int i, int i1, int i2)
    {
        int result = 0;
        if(fatigue >= i)
        {
            result = 0;
        }
        else if(fatigue < i && fatigue >= i1)
        {
            result = 1;
        }
        else if(fatigue < i1 && fatigue >= i2)
        {
            result = 2;
        }
        else
        {
            result = 3;
        }
        return result;
    }


}
