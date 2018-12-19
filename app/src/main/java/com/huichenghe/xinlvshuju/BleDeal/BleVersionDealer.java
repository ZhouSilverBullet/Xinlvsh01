package com.huichenghe.xinlvshuju.BleDeal;

import android.content.Context;

import com.huichenghe.xinlvshuju.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.Utils.NetStatus;
import com.huichenghe.xinlvshuju.http.getHardVersionHelper;

/**
 * Created by lixiaoning on 2016/8/30.
 */
public class BleVersionDealer
{
    public final String TAG = "BleVersionDealer";
    private getHardVersionHelper helper;
    private Context mContext;
    public BleVersionDealer(byte[] bufferTmp, Context mContext)
    {
        this.mContext = mContext;
        int hard;
        String harda2 = "";
        String harda = "";
        byte bluetooth, soft;
        byte[] hardVerson = new byte[bufferTmp.length - 4];
        System.arraycopy(bufferTmp, 0, hardVerson, 0, bufferTmp.length - 4);
        hardVerson = revresionBytes(hardVerson);
//            Log.i(TAG, "版本数据handverson：" + FormatUtils.bytesToHexString(hardVerson));
        bluetooth = bufferTmp[bufferTmp.length - 4];
        soft = bufferTmp[bufferTmp.length - 3];

        String versionString = FormatUtils.bytesToHexString(hardVerson) + "/"
                + parseTheHexString(bluetooth) + "/"
                + parseTheHexString(soft);
        /**
         * 写入sp
         */
        LocalDataSaveTool.getInstance(mContext.getApplicationContext()).writeSp(MyConfingInfo.HARD_VERSION, versionString);

//        String hardV = LocalDataSaveTool.getInstance(MainActivity.this.getApplicationContext()).readSp(MyConfingInfo.HARD_VERSION);
//				hardV = "17/01/02";
        if(versionString != null && !versionString.equals(""))
        {
            if(NetStatus.isNetWorkConnected(mContext.getApplicationContext()))
            {
                connectNetToGetUpdate(versionString);
            }
            else
            {
//						Toast.makeText(MainActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private byte[] revresionBytes(byte[] hardVerson)
    {
        byte[] reV = new byte[hardVerson.length];
        for (int i = 0; i < hardVerson.length; i++)
        {
            reV[i] = hardVerson[hardVerson.length - i - 1];
        }
        return reV;
    }

    private void connectNetToGetUpdate(String hardV)
    {
        if(helper == null)
        {
            helper = new getHardVersionHelper();
        }
//				new getHardVersionHelper(MainActivity.this, hardV, true);
        helper.initHardVersionHelper(mContext, hardV, true);
        helper.getNewVersionFromNet();
    }

    private static String parseTheHexString(byte hard)
    {
        byte a = (byte)(hard >> 4);
        byte b = (byte)(hard & 0x0f);
        String a1 = Integer.toHexString(a);
        String b1 = Integer.toHexString(b);
        return a1 + "" + b1;
    }
}
