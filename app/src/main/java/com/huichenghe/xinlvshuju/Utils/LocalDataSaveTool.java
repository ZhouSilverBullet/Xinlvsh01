package com.huichenghe.xinlvshuju.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * shareprense,帮助类
 * Created by lixiaoning on 15-11-10.
 */
public class LocalDataSaveTool
{
    private static LocalDataSaveTool mLocalDataSaveTool;
    private Context mContext;
    private String spName = "LoaclData";
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private LocalDataSaveTool(Context mContext)
    {
        sp = mContext.getSharedPreferences(spName, 0);
        editor = sp.edit();
    }

    public static LocalDataSaveTool getInstance(Context mContext)
    {
        if(mLocalDataSaveTool == null)
        {
            mLocalDataSaveTool = new LocalDataSaveTool(mContext);
        }
        return mLocalDataSaveTool;
    }

    public void writeSp(String key, String value)
    {
        if(editor == null)return;
        editor.putString(key, value);
        editor.commit();
    }



    public String readSp(String key)
    {
        String mData = null;
        if(sp != null)
        {
            mData = sp.getString(key, "");
        }


        return mData;
    }
}
