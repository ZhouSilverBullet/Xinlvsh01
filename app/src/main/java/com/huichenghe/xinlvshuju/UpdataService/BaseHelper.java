package com.huichenghe.xinlvshuju.UpdataService;

import android.content.Context;

import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by lixiaoning on 2016/12/8.
 */

public class BaseHelper
{
    public SendDataCallback callback;
    public final String movementUrl = MyConfingInfo.updateTextUrl + "uploadData_MovementData";
    public String getCookie(Context context)
    {
        return LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.COOKIE_FOR_ME);
    }

    public void setSendCallback(SendDataCallback callback)
    {
        this.callback = callback;
    }

    public String formateSleepData(String sleepData)
    {
        if(sleepData == null || sleepData.length() <= 0 || sleepData.equals(""))return null;
        StringBuffer sber = new StringBuffer();
        for (int i = 0; i < sleepData.length(); i++)
        {
            String each = sleepData.substring(i, i+1);
            sber.append(each).append(",");
        }
        String temp = sber.toString();
        return temp.substring(0, temp.length()-1);
    }

    public String formateHr(byte[] hr)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < hr.length; i++)
        {
            int hre = hr[i] & 0xff;
            sb.append(String.valueOf(hre)).append(",");
        }
        String hrResult = sb.toString();
        return hrResult.substring(0, hrResult.length() - 1);
    }


    public HashMap<String,String> getSendMovementMap(Context context, String needChecoDay,
                                                     String eachStepData, String dataType, String deviceId,
                                                     String deviceType)
    {
        HashMap<String, String> map = new HashMap<>();
        map.put("deviceType", deviceType);
        map.put("deviceId", deviceId);
        map.put("time", needChecoDay);
        map.put("dataType", dataType);
        map.put("uploadData", eachStepData);
        return map;
    }
    public HashMap<String,String> getSendMovementMapDataJSON(Context context, String needChecoDay,
                                                             JSONObject eachStepData, String dataType, String deviceId,
                                                             String deviceType)
    {
        HashMap<String, String> map = new HashMap<>();
        map.put("deviceType", deviceType);
        map.put("deviceId", deviceId);
        map.put("time", needChecoDay);
        map.put("dataType", dataType);
        return map;
    }

    public HashMap<String,String> getSendMovementDownMap(Context context, String needChecoDay,
                                                     String dataType, String deviceId, String deviceType)
    {
        HashMap<String, String> map = new HashMap<>();
        map.put("deviceType", deviceType);
        map.put("deviceId", deviceId);
        map.put("time", needChecoDay);
        map.put("dataType", dataType);
        return map;
    }


    public boolean updateAlreadyLoad(String result)
    {
        JSONObject json = null;
        try {
            json = new JSONObject(result);
            String code = json.getString("code");
            if(code != null && code.equals("9003"))    // 上传成功
            {
                return true;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

}
