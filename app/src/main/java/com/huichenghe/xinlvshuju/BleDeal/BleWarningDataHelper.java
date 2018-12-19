package com.huichenghe.xinlvshuju.BleDeal;

import android.content.Context;

import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lixiaoning on 2016/8/30.
 */
public class BleWarningDataHelper
{
    public BleWarningDataHelper(byte[] bufferTmp, Context mContext)
    {
        int state = bufferTmp[1] & 0xff;
        int maxHR = bufferTmp[2] & 0xff;
        int minHR = bufferTmp[3] & 0xff;
        JSONObject json = new JSONObject();
        try {
            json.put(MyConfingInfo.HR_WARNING_OPEN_OR_NO,state);
            json.put(MyConfingInfo.MAX_HR, maxHR);
            json.put(MyConfingInfo.MIN_HR, minHR);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LocalDataSaveTool.getInstance(mContext).writeSp(MyConfingInfo.HRWARNING_SETTING_VALUE, json.toString());
    }
}
