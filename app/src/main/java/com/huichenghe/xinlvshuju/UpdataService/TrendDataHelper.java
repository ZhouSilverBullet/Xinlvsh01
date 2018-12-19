package com.huichenghe.xinlvshuju.UpdataService;

import android.content.Context;

import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

import java.util.HashMap;

/**
 * Created by lixiaoning on 2016/12/22.
 */

public class TrendDataHelper extends BaseHelper
{
    private Context context;
    public TrendDataHelper(Context context)
    {
        this.context = context;
    }

    public void getDateTrend(String date, String account, String dataType, SendDataCallback callback)
    {
        HashMap<String, String> map = getSendMovementDownMap(context, date, dataType, UserMACUtils.getMac(context), DeviceTypeUtils.getDeviceType(context));
        String cookie = LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.COOKIE_FOR_ME);
        new DownDataTask(map, context, callback).execute(cookie);
    }

}
