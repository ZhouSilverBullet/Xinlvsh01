package com.huichenghe.xinlvshuju.appRemind.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2016/10/26 0026.
 */

public class Utils {


    public static void saveData(Context context, Boolean isCheck,String packageName) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(packageName, isCheck);
        editor.commit();
    }

    public static Boolean loadData(Context context,String packageName) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
//        Toast.makeText(context, sp.getString("content", "").toString(), Toast.LENGTH_SHORT).show();
        return sp.getBoolean(packageName, false);
    }

    public static void savaRemindTag(Context context, Boolean isCheck,String msgRemind) {

        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(msgRemind, isCheck);
        editor.commit();


    }
}
