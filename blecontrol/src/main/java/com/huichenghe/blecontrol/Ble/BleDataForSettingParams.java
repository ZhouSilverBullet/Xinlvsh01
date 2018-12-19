package com.huichenghe.blecontrol.Ble;

import android.content.Context;

import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;

import java.util.Calendar;

/**
 * 通过此类设置计步参数，并对手环返回数据进行解析处理
 *
 * Created by lixiaoning on 15-11-26.
 */
public class BleDataForSettingParams extends BleBaseDataManage
{
    private Context mContext;
    public BleDataForSettingParams(Context mContext)
    {
        this.mContext = mContext;
    }


    public static final byte getSettingCmd = (byte)0x04;
    public static final byte fromDevice = (byte)0x84;



    // 设置计步参数
    public void settingTheStepParamsToBracelet()
    {
//        // 获取个人信息
        String hei = getPersionInfo(MyConfingInfo.USER_HEIGHT);     // 身高
        String wei = getPersionInfo(MyConfingInfo.USER_WEIGHT);     // 体重
        String gen = String.valueOf((Integer.parseInt(getPersionInfo(MyConfingInfo.USER_GENDER)) - 1)); // 性别
        String bir = getPersionInfo(MyConfingInfo.USER_BIRTHDAY);


        String birr = bir.substring(0, 4);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int age = year - Integer.parseInt(birr);    // 年龄

        byte[] buffers = new byte[4];
        buffers[0] = (byte)Integer.parseInt(hei);
        buffers[1] = (byte)Integer.parseInt(wei);
        buffers[2] = (byte)Integer.parseInt(gen);
        buffers[3] = (byte)age;


        setMsgToByteDataAndSendToDevice(getSettingCmd, buffers, buffers.length);

    }


    /**
     * 读取基本信息帮助方法
     * @param key
     * @return
     */
    private String getPersionInfo(String key)
    {
        return LocalDataSaveTool.getInstance(mContext).readSp(key);
    }


    public void dealBleResponse(Context context, byte[] data, int length)
    {
        MyToastUitls.showToast(context, R.string.step_pramas, 2);
    }
}
