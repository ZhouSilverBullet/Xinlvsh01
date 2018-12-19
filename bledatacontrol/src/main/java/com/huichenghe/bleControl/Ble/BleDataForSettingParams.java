package com.huichenghe.bleControl.Ble;

import android.content.Context;


import java.util.Calendar;

public class BleDataForSettingParams extends BleBaseDataManage
{
    private Context mContext;
    public BleDataForSettingParams(Context mContext)
    {
        this.mContext = mContext;
    }


    public static final byte getSettingCmd = (byte)0x04;
    public static final byte fromDevice = (byte)0x84;



    public void settingTheStepParamsToBracelet(String hei, String wei, String gen, String bir)
    {
        if(bir == null || bir != null && bir.equals(""))return;
        if(hei == null || hei != null && hei.equals(""))return;
        if(wei == null || wei != null && wei.equals(""))return;
        if(gen == null || gen != null && gen.equals(""))return;
        String birr = bir.substring(0, 4);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int age = year - Integer.parseInt(birr);

        byte[] buffers = new byte[4];
        buffers[0] = (byte)Integer.parseInt(hei);
        buffers[1] = (byte)Integer.parseInt(wei);
        buffers[2] = (byte)Integer.parseInt(gen);
        buffers[3] = (byte)age;
        setMsgToByteDataAndSendToDevice(getSettingCmd, buffers, buffers.length);
    }





    public void dealBleResponse(Context context, byte[] data, int length)
    {
//        MyToastUitls.showToast(context, R.string.step_pramas, 2);
    }
}
