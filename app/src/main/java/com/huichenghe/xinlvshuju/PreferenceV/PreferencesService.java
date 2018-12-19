package com.huichenghe.xinlvshuju.PreferenceV;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/14 0014.
 */

public class PreferencesService {
    private Context context;

    public  PreferencesService(Context context){
        this.context = context;
    }

    /**
     * 保存设置偏好

     */
    public void save(String short_ed_s, String height_ed_s) {
        //设置文件是以xml来进行保存的
        SharedPreferences preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("short_ed", short_ed_s);
        editor.putString("height_ed", height_ed_s);
        editor.commit();
    }
    public void baocun(String s1, String s2) {
        //设置文件是以xml来进行保存的
        SharedPreferences preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("s1", s1);
        editor.putString("s2", s2);
        editor.commit();
    }



    public Map<String, String> getPreferences(){
        Map<String, String> params = new HashMap<String, String>();
        SharedPreferences preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        params.put("short_ed", preferences.getString("short_ed", ""));
        params.put("height_ed", preferences.getString("height_ed", ""));
        return params;
    }
    public Map<String, String> getzhi(){
        Map<String, String> params = new HashMap<String, String>();
        SharedPreferences preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        params.put("s1", preferences.getString("s1", ""));
        params.put("s2", preferences.getString("s2", ""));
        return params;
    }
}
