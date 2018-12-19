package com.huichenghe.bleControl.Ble;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import java.io.UnsupportedEncodingException;

public class BleDataForCustomRemind extends BleBaseDataManage
{
    public static final String TAG = BleDataForCustomRemind.class.getSimpleName();
    public static final byte toDevice = (byte)0x09;
    public static final byte fromDevice = (byte)0x89;
    public static final byte fromDeviceNull = (byte)0xC9;
    private final int GET_CUSTOM_EREMIND = 0;
    private final int SETTINGS_CUSTOM_REMIND = 1;
    private final int DELETE_CUSTOM_REMIND = 2;
    private final String REMIND_NUMBER = "remind number";
    private boolean isRemindDataBack = false;
    private int count = 0;
    private boolean isSettingsOk = false;
    private int settingCount = 0;
    private DeleteCallback callback;
    private DataSendCallback requesCallback;
    private static BleDataForCustomRemind bleDataForCustomRemind;
    private int numberes = 0;
    private Handler customRemindHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case GET_CUSTOM_EREMIND:
                    if(numberes < 8)
                    {
                        if(isRemindDataBack)
                        {
                            continueSendNext(this, msg);
                            numberes = numberes + 1;
                            if(numberes >= 8)return;
                            getCustomRemind(numberes);
                        }
                        else
                        {
                            if(count < 4)
                            {
                                continueSendThis(this, msg);
                                getCustomRemind(numberes);
                            }
                            else
                            {
                                continueSendNext(this, msg);
                                numberes = numberes + 1;
                                getCustomRemind(numberes);
                            }
                        }
                    }
                    else
                    {
                        closeSendData(this);
                        if(requesCallback != null)
                        {
                            requesCallback.sendFinished();
                        }
                    }
                    break;
                case SETTINGS_CUSTOM_REMIND:
                    if(isSettingsOk)
                    {
                        stopSendSettingsData(this);
                    }
                    else
                    {
                        if(settingCount < 4)
                        {
                            continueSendSettinsData(this, msg);
                            byte[] data = msg.getData().getByteArray("settings");
                            setMsgToByteDataAndSendToDevice(toDevice, data, data.length);
                        }
                        else
                        {
                            stopSendSettingsData(this);
                        }
                    }
                    break;
                case DELETE_CUSTOM_REMIND:
                    if(isSettingsOk)
                    {
                        stopDelete(this);
                    }
                    else
                    {
                        if(settingCount < 4)
                        {
                            continueSendDelete(this, msg);
                            deleteCustomRemind(msg.getData().getByte("number"));
                        }
                        else
                        {
                            stopDelete(this);
                        }
                    }
                    break;
            }
        }
    };

    private void continueSendDelete(Handler handler, Message msg)
    {
        Message mzg = handler.obtainMessage();
        mzg.what = msg.what;
        mzg.arg1 = msg.arg1;
        mzg.arg2 = msg.arg2;
        mzg.setData(msg.getData());
        handler.sendMessageDelayed(mzg, SendLengthHelper.getSendLengthDelay(msg.arg1, msg.arg2));
    }

    private void stopDelete(Handler handler)
    {
        handler.removeMessages(DELETE_CUSTOM_REMIND);
        isSettingsOk = false;
        settingCount = 0;
    }

    private void continueSendSettinsData(Handler handler, Message msg)
    {
        Message mzg = handler.obtainMessage();
        mzg.what = SETTINGS_CUSTOM_REMIND;
        mzg.arg1 = msg.arg1;
        mzg.arg2 = msg.arg2;
        mzg.setData(msg.getData());
        handler.sendMessageDelayed(mzg, SendLengthHelper.getSendLengthDelay(msg.arg1, msg.arg2));
        settingCount ++;
    }

    private void stopSendSettingsData(Handler handler)
    {
        handler.removeMessages(SETTINGS_CUSTOM_REMIND);
        if(!isSettingsOk)
        {
            requesCallback.sendFailed();
        }
        requesCallback.sendFinished();
        isSettingsOk = false;
        settingCount = 0;
    }

    private void closeSendData(Handler handler)
    {
        handler.removeMessages(GET_CUSTOM_EREMIND);
        isRemindDataBack = false;
        count = 0;
        numberes = 0;
    }

    private void continueSendThis(Handler handler, Message mzg)
    {
        Message msg = handler.obtainMessage();
        msg.what = GET_CUSTOM_EREMIND;
        msg.arg1 = mzg.arg1;
        msg.arg2 = mzg.arg2;
        handler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(mzg.arg1, mzg.arg2));
        count ++;
    }

    private void continueSendNext(Handler handler, Message mzg)
    {
        Message msg = handler.obtainMessage();
        msg.what = GET_CUSTOM_EREMIND;
        msg.arg1 = mzg.arg1;
        msg.arg2 = mzg.arg2;
        handler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(mzg.arg1, mzg.arg2));
        isRemindDataBack = false;
        count = 0;
    }

    public static BleDataForCustomRemind getCustomRemindDataInstance()
    {
        if(bleDataForCustomRemind == null)
        {
            synchronized (BleDataForCustomRemind.class)
            {
                if(bleDataForCustomRemind == null)
                {
                    bleDataForCustomRemind = new BleDataForCustomRemind();
                }
            }
        }
        return bleDataForCustomRemind;
    }

    //单利模式
    private BleDataForCustomRemind(){};


    public void deletePx(byte number)
    {
        int deleLg = deleteCustomRemind(number);
        Message msg = customRemindHandler.obtainMessage();
        msg.what = DELETE_CUSTOM_REMIND;
        msg.arg1 = deleLg;
        msg.arg2 = 6;
        Bundle bundle = new Bundle();
        bundle.putByte("number", number);
        msg.setData(bundle);
        customRemindHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(deleLg, 6));
    }
    private int deleteCustomRemind(byte number)
    {
        byte[] bytes = new byte[2];
        bytes[0] = (byte)0x02;
        bytes[1] =  number;
        return setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
    }
    public void dealTheDeleteCallback(byte[] buffer)
    {
        callback.deleteCallback(buffer[0]);
    }

    public void closeSendData()
    {
        closeSendData(customRemindHandler);
    }

    public void getTheCustomRemind(int num)
    {
        int sendLg = getCustomRemind(num);
        Message msg = customRemindHandler.obtainMessage();
        msg.what = GET_CUSTOM_EREMIND;
        msg.arg1 = sendLg;
        msg.arg2 = 25;
        Bundle bundle = new Bundle();
        bundle.putInt(REMIND_NUMBER, num);
        msg.setData(bundle);
        customRemindHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(sendLg, 25));
    }

    public void setCustomRingSettings(byte[] data)
    {
        int sendLg = setMsgToByteDataAndSendToDevice(toDevice, data, data.length);
        Message msg = customRemindHandler.obtainMessage();
        msg.what = SETTINGS_CUSTOM_REMIND;
        msg.arg1 = sendLg;
        msg.arg2 = 10;
        Bundle bu = new Bundle();
        bu.putByteArray("settings", data);
        msg.setData(bu);
        customRemindHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(sendLg, 10));
    }


    private int getCustomRemind(int number)
    {
        byte[] bytes = new byte[2];
        bytes[0] = (byte)0x00;
        bytes[1] = (byte)(number & 0xff);
        return setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
    }
    byte[] hasData = new byte[8];

    public void dealTheValidData(byte cmd, Context mContext, byte[] dataValid)
    {
        if(cmd == fromDevice && dataValid.length <= 4)
        {
            isSettingsOk = true;
        }
        else
        {
            isRemindDataBack = true;
        }
        requesCallback.sendSuccess(dataValid);
    }



    public void setOnDeleteListener(DeleteCallback callback)
    {
        this.callback = callback;
    }

    public interface DeleteCallback
    {
        void deleteCallback(byte a);
    }

    public void setOnRequesCallback(DataSendCallback requesCallback)
    {
        this.requesCallback = requesCallback;
    }

}
