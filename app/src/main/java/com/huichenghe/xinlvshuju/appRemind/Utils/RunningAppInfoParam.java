package com.huichenghe.xinlvshuju.appRemind.Utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.appRemind.AppInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/10/25 0025.
 */

public class RunningAppInfoParam {

    private Context m_context;
    private static final String LogTag = "hello";

    public RunningAppInfoParam(Context context) {
        m_context = context;
    }

    public List<ApplicationInfo> getInstallAppInfo() {
        PackageManager mypm = m_context.getPackageManager();
        List<ApplicationInfo> appInfoList = mypm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);

        Collections.sort(appInfoList, new ApplicationInfo.DisplayNameComparator(mypm));// 排序

        for (ApplicationInfo app : appInfoList) {
            //Log.v(LogTag, "RunningAppInfoParam  getInstallAppInfo app label = " + (String)app.loadLabel(umpm));
            //Log.v(LogTag, "RunningAppInfoParam  getInstallAppInfo app packageName = " + app.packageName);
        }
        return appInfoList;
    }



    //获取第三方应用信息
    public ArrayList<AppInfo> getThirdAppInfo() {
        List<ApplicationInfo> appList = getInstallAppInfo();
        List<ApplicationInfo> thirdAppList = new ArrayList<>();
        List<ApplicationInfo> sysAppList = new ArrayList<>();
        thirdAppList.clear();
        for (ApplicationInfo app : appList) {
            //非系统程序
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0)
            {
                thirdAppList.add(app);
            }
            //本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了
            else if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)
            {
                sysAppList.add(app);
            }else if((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0){//系统应用
                sysAppList.add(app);
            }
        }
        PackageManager mypm = m_context.getPackageManager();
        ArrayList<AppInfo> thirdAppNameList = new ArrayList<>();
        for(int i = 0; i < thirdAppList.size() + sysAppList.size(); i++)
        {
            ApplicationInfo app;
            if(i < thirdAppList.size())
            {
                app = thirdAppList.get(i);
            }
            else
            {
                app = sysAppList.get(i - thirdAppList.size());
            }
            String label = (String) app.loadLabel(mypm);
            Drawable loadIcon = app.loadIcon(mypm);
            String packageName = app.packageName;
            Cursor mCursor = MyDBHelperForDayData.getInstance(m_context.getApplicationContext()).selectInfoData(m_context.getApplicationContext());
            Boolean isChecked = !parseCursorAndCompare(mCursor, packageName);
            AppInfo appInfo = new AppInfo(loadIcon, isChecked, packageName, label);
            Log.e(LogTag, "RunningAppInfoParam getThirdAppInfo app label = " + label);
            thirdAppNameList.add(appInfo);
        }
        return thirdAppNameList;
    }


    private boolean parseCursorAndCompare(Cursor cursor, String com)
    {
        if(com != null && com.equals(""))
        {
            return false;
        }
        if(cursor.getCount() > 0)
        {
            if(cursor.moveToFirst())
            {
                do {
                    String appName = cursor.getString(cursor.getColumnIndex("appName"));
                    if(com.equals(appName))
                    {
                        return false;
                    }
                }
                while (cursor.moveToNext());
            }
        }
        return true;
    }

    //获取系统应用信息
    public ArrayList<String> getSystemAppInfo() {
        List<ApplicationInfo> appList = getInstallAppInfo();
        List<ApplicationInfo> sysAppList = new ArrayList<ApplicationInfo>();
        sysAppList.clear();
        for (ApplicationInfo app : appList) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                sysAppList.add(app);
            }
        }
        PackageManager mypm = m_context.getPackageManager();
        ArrayList<String> sysAppNameList = new ArrayList<String>();
        for (ApplicationInfo app : sysAppList) {
            Log.v(LogTag, "RunningAppInfoParam getThirdAppInfo app label = " + (String) app.loadLabel(mypm));
            sysAppNameList.add((String) app.loadLabel(mypm));
        }

        return sysAppNameList;

    }

}
