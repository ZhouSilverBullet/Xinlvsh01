package com.huichenghe.blecontrol.Ble;

import android.content.Context;

/**
 * Created by lixiaoning on 15-11-sosPhone.
 */
public class BleDataForBattery extends BleBaseDataManage
{
    private static int mCurrentBattery = -1;
    private BatteryListener battListerer;
    private static BleDataForBattery bleBAttery;
    private boolean hasComm = false;

    public static byte mReceCmd = 0x03;
    public static byte fromCmd = (byte)0x83;

    public static BleDataForBattery getInstance()
    {
        if(bleBAttery == null)
        {
            bleBAttery = new BleDataForBattery();
        }
        return bleBAttery;
    }

    private BleDataForBattery()
    {

//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        setMessageDataByString(mReceCmd, null, true);
    }

    public void getBatteryFromBr()
    {
        setMessageDataByString(mReceCmd, null, true);
    }


    /**
     * 处理解析后的data
     * @param mContext
     * @param data
     * @param dataLength
     */
    public void dealReceData(Context mContext, byte[] data, int dataLength)
    {
        mCurrentBattery = (data[0] & 0xFF);
//        Log.i("", "dealReceData :" + mCurrentBattery);
        battListerer.listenerBatter(mCurrentBattery);
    }

    public static int getmCurrentBattery()
    {
        return mCurrentBattery;
    }




    public interface BatteryListener
    {
        void listenerBatter(int battery);
    }
    public void setBatteryListener(BatteryListener listerer)
    {
        this.battListerer = listerer;
    }

}
