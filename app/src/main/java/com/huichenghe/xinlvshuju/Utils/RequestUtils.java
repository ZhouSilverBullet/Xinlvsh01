package com.huichenghe.xinlvshuju.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * 请求权限工具类
 * Created by lixiaoning on 15-11-18.
 */
public class RequestUtils
{

    static int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 0;
    static int MY_WRITE_SDCARD = 1;
    /**
     * 请求访问位置权限，扫描设备需要此权限
     */
    public static void requestPermission(Activity a)
    {


        // 检测是否有
        if(ContextCompat.checkSelfPermission(a,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(a,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

            boolean isCan = ActivityCompat.shouldShowRequestPermissionRationale(a, Manifest.permission.READ_CONTACTS);

        }

    }



    public static void requestPermissionSdcard(Activity a)
    {
        if(ContextCompat.checkSelfPermission(a, Manifest.permission.WRITE_SETTINGS)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(a,
                    new String[]{Manifest.permission.WRITE_SETTINGS},
                    MY_WRITE_SDCARD);
        }
    }



}
