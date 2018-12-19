package com.huichenghe.xinlvshuju.Utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.Iterator;
import java.util.List;

/**
 * Created by lixiaoning on 2016/6/22.
 */
public class PackageNameUtils
{
    public static final String TAG = PackageNameUtils.class.getSimpleName();
    public void getPackageName(Context context)
    {
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningList = manager.getRunningAppProcesses();
        Iterator<ActivityManager.RunningAppProcessInfo> iterators = runningList.iterator();
        while (iterators.hasNext())
        {
            ActivityManager.RunningAppProcessInfo processInfo = iterators.next();
            String na = processInfo.processName;
            Log.i(TAG, "运行软件processName：" + na);
            String[] pakList = processInfo.pkgList;
            for (String s : pakList)
            {
                Log.i(TAG, "运行软件包名：" + s);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void getTask(Context context)
    {
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> taskInfos = manager.getAppTasks();
        Iterator<ActivityManager.AppTask> taskIterator = taskInfos.iterator();
        while (taskIterator.hasNext())
        {
            ActivityManager.AppTask task = taskIterator.next();
            ActivityManager.RecentTaskInfo info = task.getTaskInfo();
//            info.
        }



        List<ActivityManager.RunningAppProcessInfo> runningList = manager.getRunningAppProcesses();
        Iterator<ActivityManager.RunningAppProcessInfo> iterators = runningList.iterator();
        while (iterators.hasNext())
        {
            ActivityManager.RunningAppProcessInfo processInfo = iterators.next();
            String na = processInfo.processName;
            Log.i(TAG, "运行软件processName：" + na);
            String[] pakList = processInfo.pkgList;
            for (String s : pakList)
            {
                Log.i(TAG, "运行软件包名：" + s);
            }
        }
    }

    public void getPackageNameM(Context con)
    {
        PackageManager pm =  con.getPackageManager();
        List<PackageInfo> data = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (PackageInfo info :data)
        {
            Log.i(TAG, "运行软件包名：" + info.packageName + "--" + info.applicationInfo.className);
        }
    }


}
