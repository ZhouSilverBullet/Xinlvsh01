package com.huichenghe.xinlvshuju.PreferenceV;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/3/2 0002.
 */

public class SharedPreferencesUtil {

    private static SharedPreferencesUtil sharedPreferencesUtil;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private SharedPreferencesUtil(Context context) {
        sharedPreferences = context.getSharedPreferences("TrineaAndroidCommon", context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static SharedPreferencesUtil getInstance(Context context) {
        if (sharedPreferencesUtil == null) {
            sharedPreferencesUtil = new SharedPreferencesUtil(context);
        }
        return sharedPreferencesUtil;
    }

    //保存
    public void put(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    //获取
    public String get(String key) {
        return sharedPreferences.getString(key, "");
    }


    public void clear() {
        editor.clear();
        editor.commit();
    }
}
