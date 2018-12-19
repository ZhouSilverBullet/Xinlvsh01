package com.huichenghe.xinlvshuju;

import android.os.Environment;

import java.io.File;

/**
 * Created by lixiaoning on 2016/6/23.
 */
public class SDPathUtils
{
    public static String getSdcardPath()
    {
        String pathRoot = "";
        File envirFile = Environment.getExternalStorageDirectory();
        if(envirFile.canRead())
        {
            pathRoot = envirFile.getAbsolutePath();
        }
        else
        {
            pathRoot = "/storage/emulated/0";
        }
        return pathRoot;
    }
}
