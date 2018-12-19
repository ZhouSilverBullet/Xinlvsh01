//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.huichenghe.bleControl.Ble;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class BleZhifu extends BleBaseDataManage {
    private static int mCurrentBattery = -1;
    private DataSendCallback battListerer;
    private static BleZhifu blezhifu;
    private boolean hasComm = false;
    private final int GET_BLE_BATTERY = 0;
    private boolean isSendOk = false;
    private int sendCount = 0;
    public static byte mReceCmd = 52;
    public static byte fromCmd = -76;
    private Handler batteryHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(BleZhifu.this.isSendOk) {
                BleZhifu.this.stopSendData(this);
            } /*else if(BleZhifu.this.sendCount < 4) {
             //   BleZhifu.this.continueSend(this, msg);
             //   BleZhifu.this.getDataFromBr(BleZhifu.this.b);

            } */else {
                BleZhifu.this.stopSendData(this);
            }

        }
    };
    private byte[] b;

    public static BleZhifu getInstance() {
        if(blezhifu == null) {
            Class var0 = BleDataForBattery.class;
            synchronized(BleDataForBattery.class) {
                if(blezhifu == null) {
                    blezhifu = new BleZhifu();
                }
            }
        }

        return blezhifu;
    }

    private void continueSend(Handler handler, Message msges) {
        Message msg = handler.obtainMessage();
        msg.arg1 = msges.arg1;
        msg.arg2 = msges.arg2;
        handler.sendMessageDelayed(msg, (long)SendLengthHelper.getSendLengthDelay(msges.arg1, msges.arg2));
        ++this.sendCount;
    }

    private void stopSendData(Handler handler) {
        handler.removeMessages(0);
        if(!this.isSendOk) {
            this.battListerer.sendFailed();
        }

        this.battListerer.sendFinished();
        this.isSendOk = false;
        this.sendCount = 0;
    }

    private BleZhifu() {
    }

    public void setzhifutype(byte[] byt) {
        this.b = byt;
        boolean sendLength = false;
        int sendLength1;
        if(byt != null) {
            sendLength1 = this.getDataFromBr(byt);
        } else {
            sendLength1 = this.settype();
        }

        Message msg = this.batteryHandler.obtainMessage();
        msg.what = 0;
        msg.arg1 = sendLength1;
        msg.arg2 = 7;
        this.batteryHandler.sendMessageDelayed(msg, (long)SendLengthHelper.getSendLengthDelayForPay(sendLength1, 7));
    }

    public void sendPaymentRawData(byte[] byt) {
        int sendLength1;
        if(byt == null) {
            return;
        }
        this.b = byt;
        sendLength1 = sendPayData(byt);

        Message msg = this.batteryHandler.obtainMessage();
        msg.what = 0;
        msg.arg1 = sendLength1;
        msg.arg2 = 7;
        this.batteryHandler.sendMessageDelayed(msg, (long)SendLengthHelper.getSendLengthDelayForPay(sendLength1, 7));
    }

    private int settype() {
        byte[] data = new byte[]{(byte)1, (byte)1};
        int remindLength = this.setMsgToByteDataAndSendToDevice(mReceCmd, data, data.length);
        return remindLength;
    }

    private int getDataFromBr(byte[] byt) {
        byte[] data = new byte[byt.length + 1];

        int remindLength;
        for(remindLength = 0; remindLength < data.length; ++remindLength) {
            if(remindLength == 0) {
                data[0] = 2;
            } else {
                data[remindLength] = byt[remindLength - 1];
            }
        }

        remindLength = this.setMsgToByteDataAndSendToDevice(mReceCmd, data, data.length);
        return remindLength;
    }

    private int sendPayData(byte[] dat)
    {
        int reminderLength;
        reminderLength = this.setMsgToByteDataAndSendToDevice(mReceCmd, dat, dat.length);
        return reminderLength;
    }

    private int senddata() {
        byte[] data = new byte[]{(byte)1, (byte)1};
        int remindLength = this.setMsgToByteDataAndSendToDevice(mReceCmd, data, data.length);
        return remindLength;
    }

    public void dealReceData(Context mContext, byte[] data) {
        this.isSendOk = true;
        this.battListerer.sendSuccess(data);
    }

    public static int getmCurrentBattery() {
        return mCurrentBattery;
    }

    public void setBatteryListener(DataSendCallback batteryCallback) {
        this.battListerer = batteryCallback;
    }
}
