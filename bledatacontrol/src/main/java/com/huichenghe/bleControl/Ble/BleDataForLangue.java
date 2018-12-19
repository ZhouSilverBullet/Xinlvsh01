package com.huichenghe.bleControl.Ble;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * 语言
 */
public class BleDataForLangue extends BleBaseDataManage {
    private static int mCurrentBattery = -1;
    private DataSendCallback battListerer;
    private static BleDataForLangue bleflangue;
    private boolean hasComm = false;
    private final int SET_BLE_LANGUE = 0;
    private boolean isSendOk = false;
    private int sendCount = 0;

    public static byte mReceCmd = 0x02;
    public static byte fromCmd = (byte) 0x82;

    public static BleDataForLangue getInstance() {
        if (bleflangue == null) {
            synchronized (BleDataForBattery.class) {
                if (bleflangue == null) {
                    bleflangue = new BleDataForLangue();
                }
            }
        }
        return bleflangue;
    }

    private Handler batteryHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isSendOk) {
                stopSendData(this);
            } else {
                if (sendCount < 4) {
                    continueSend(this, msg);
                    getBatteryFromBr();
                } else {
                    stopSendData(this);
                }
            }
        }
    };

    private void continueSend(Handler handler, Message msges) {
        Message msg = handler.obtainMessage();
        msg.arg1 = msges.arg1;
        msg.arg2 = msges.arg2;
        handler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(msges.arg1, msges.arg2));
        sendCount++;
    }

    private void stopSendData(Handler handler) {
        handler.removeMessages(SET_BLE_LANGUE);
        if (!isSendOk) {
            battListerer.sendFailed();
        }
        battListerer.sendFinished();
        isSendOk = false;
        sendCount = 0;
    }


    private BleDataForLangue() {

//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        setMessageDataByString(mReceCmd, null, true);
    }

    private int languetype = 0;

    public void getBatteryPx(int i) {
        languetype=i;
        getBatteryFromBr();
//        Message msg = batteryHandler.obtainMessage();
//        msg.what = SET_BLE_LANGUE;
//        msg.arg1 = 9;
//        msg.arg2 = 7;
//        getBatteryFromBr();
//        //batteryHandler.sendMessageDelayed(msg, 100);
    }


    private void getBatteryFromBr() {
        byte[] send = new byte[3];
        send[0] = (byte) 0x01;
        send[1] = (byte) 0x06;
        send[2] = (byte) languetype;
        setMsgToByteDataAndSendToDevice(mReceCmd, send, send.length);
        //   return setMessageDataByString(mReceCmd, null, true);
    }


    /**
     * 处理解析后的data
     *
     * @param mContext
     * @param data
     * @param dataLength
     */
    public void dealReceData(Context mContext, byte[] data, int dataLength) {
//        mCurrentBattery = (data[0] & 0xFF);
//        Log.i("", "dealReceData :" + mCurrentBattery);
        isSendOk = true;
        battListerer.sendSuccess(data);
    }

    public static int getmCurrentBattery() {
        return mCurrentBattery;
    }


    public void setBatteryListener(DataSendCallback batteryCallback) {
        this.battListerer = batteryCallback;
    }

}
