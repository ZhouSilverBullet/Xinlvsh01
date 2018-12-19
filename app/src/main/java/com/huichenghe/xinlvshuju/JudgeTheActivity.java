package com.huichenghe.xinlvshuju;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import java.util.List;

/**
 * 获取是否是在栈顶
 * Created by lixiaoning on 2016/6/6.
 */
public class JudgeTheActivity
{
    public static final String TAG = JudgeTheActivity.class.getSimpleName();
    public static boolean getTaskForgrand(Context context, String pakage, String activityName)
    {
        ActivityManager.RunningTaskInfo first = null;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(1);
        if(tasks != null && !tasks.isEmpty())
        {
            first = tasks.get(0);
        }
        if(first != null)
        {
            ComponentName componentName = first.topActivity;
//            Log.i(TAG, "包名：jun" + componentName.getPackageName() + "--" + componentName.getClassName());
            if(componentName.getPackageName().equals(pakage) &&
                    componentName.getClassName().equals(activityName))
            {
                return true;
            }
        }
        return false;
    }
}
