package com.huichenghe.xinlvshuju.http;

import android.content.Context;
import android.util.Log;

import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lixiaoning on 2016/6/1.
 */
public class UserAccountUtil
{
    public static final String TAG = UserAccountUtil.class.getSimpleName();
    public static String getAccount(Context context)
    {
        String userAccount = LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.USER_ACCOUNT);
        Log.i(TAG, "保存的账号地址" + userAccount);
        String acc = "";
        if(userAccount != null && !userAccount.equals(""))
        {
            JSONObject json = null;
            try {
                json = new JSONObject(userAccount);
                acc = json.getString(MyConfingInfo.ACCOUNT);
                if(acc.contains(";"))
                {
                    acc = acc.split(";")[0];
                }
            } catch (JSONException e) {
                e.printStackTrace();
                acc = "";
            }
        }
        return acc;
    }

    public static String getType(Context context)
    {
        String type = null;
        String userAccont = LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.USER_ACCOUNT);
        if(userAccont != null && !userAccont.equals(""))
        {
            try {
                JSONObject json = new JSONObject(userAccont);
                type = json.getString(MyConfingInfo.TYPE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return type;
    };






}
