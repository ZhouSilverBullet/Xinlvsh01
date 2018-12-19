package com.huichenghe.bleControl.Ble;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huichenghe.bleControl.Utils.FormatUtils;

/**
 * Created by Administrator on 2016/11/14 0014.
 */

public class BleReadDeviceMenuState extends BleBaseDataManage
{

    public final static byte toDevice = (byte)0x2c;
    public final static byte fromDevice = (byte)0xac;

    private static final int OPEN_OR_CLOSE = 1;
    private static BleReadDeviceMenuState bleReadDeviceMenuState;
    private int count;

    private BleReadDeviceMenuState() {

    }

    public static synchronized BleReadDeviceMenuState getInstance() {
        if (bleReadDeviceMenuState == null) {
            bleReadDeviceMenuState = new BleReadDeviceMenuState();
        }
        return bleReadDeviceMenuState;
    }

    public void sendUpdateSwitchData(byte code) {

        byte[] swich = new byte[5];
        swich[0] = 0x01;
        swich[1] = code;
        swich[2] = 0x00;
        swich[3] = 0x00;
        swich[4] = 0x00;
        geistate((byte) 0x2c, swich);
    }

    public void sendUpdateSwitchData32(int allData) {
        byte[] swich = new byte[5];
        swich[0] = 0x01;
        byte[] d = FormatUtils.int2Byte_HL_(allData);
        System.arraycopy(d, 0, swich, 1, d.length);
        geistate((byte) 0x2c, swich);
    }

    private boolean isBack;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isBack) {
                closeSendOpenOrClose(this);
            } else {
                if (count < 4) {
                    continueSendOpenOrClose(this, msg);
                    sendData((byte) msg.arg1, (byte) msg.arg2);
                } else {
                    closeSendOpenOrClose(this);
                }
            }
        }
    };

    private void closeSendOpenOrClose(Handler handler) {
        handler.removeMessages(OPEN_OR_CLOSE);
        isBack = false;
        count = 0;
    }

    public void geistate(byte code, byte content) {
        int sendLg = sendData(code, content);
        Message msg = Message.obtain();
        msg.what = OPEN_OR_CLOSE;
        msg.arg1 = code;
        msg.arg2 = content;
        Bundle bundle = new Bundle();
        bundle.putInt("send_length", sendLg);
        bundle.putInt("rece_length", 8);
        msg.setData(bundle);
        mHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(sendLg, 8));
    }

    public void geistate(byte code, byte[] content) {
        int sendLg = sendData(code, content);
        Message msg = Message.obtain();
        msg.what = OPEN_OR_CLOSE;
        msg.arg1 = code;
        msg.arg2 = content[0];
        Bundle bundle = new Bundle();
        bundle.putInt("send_length", sendLg);
        bundle.putInt("rece_length", 8);
        msg.setData(bundle);
        mHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(sendLg, 8));
    }

    public int sendData(byte controlCode, byte content) {
        byte[] sendData = new byte[1];
        sendData[0] = content;
        int len = setMsgToByteDataAndSendToDevice(controlCode, sendData, sendData.length);
        return len;

    }

    public int sendData(byte controlCode, byte[] content) {
        int len = setMsgToByteDataAndSendToDevice(controlCode, content, content.length);
        return len;

    }

    private void continueSendOpenOrClose(Handler handler, Message mzg) {
        Message msg = Message.obtain();
        msg.what = OPEN_OR_CLOSE;
        msg.arg1 = mzg.arg1;
        msg.arg2 = mzg.arg2;
        Bundle bundle = mzg.getData();
        msg.setData(bundle);
        handler.sendMessageDelayed(msg,
                SendLengthHelper.getSendLengthDelay(bundle.getInt("send_length"), bundle.getInt("rece_length")));
        count++;
    }



    public void sendSuccess(byte[] receveData)
    {
        isBack = true;
        if(receveData[0] == 1)return;
        devicemenuCallback.onGEtCharArray(receveData);
    }


    public String formattingH(int a) {
        String i = String.valueOf(a);
        switch (a) {
            case 10:
                i = "a";
                break;
            case 11:
                i = "b";
                break;
            case 12:
                i = "c";
                break;
            case 13:
                i = "d";
                break;
            case 14:
                i = "e";
                break;
            case 15:
                i = "f";
                break;
        }
        return i;
    }

    public void setResultlistener(DevicemenuCallback resultlistener) {
        devicemenuCallback = resultlistener;
    }
    DevicemenuCallback devicemenuCallback;
    public interface DevicemenuCallback {
        void onGEtCharArray(byte[] data);
    }
}
