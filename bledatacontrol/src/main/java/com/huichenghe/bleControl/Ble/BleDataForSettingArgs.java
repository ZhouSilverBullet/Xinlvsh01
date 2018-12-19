package com.huichenghe.bleControl.Ble;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;

import com.huichenghe.bleControl.CountryUtils;
import com.huichenghe.bleControl.Utils.FormatUtils;

/**
 * Created by lixiaoning on 2016/5/17.
 */
public class BleDataForSettingArgs extends BleBaseDataManage
{
    private final byte toDevice = (byte)0x02;
    public static final byte fromDevice = (byte)0x82;
    private final int SETTING_PARAMTER = 0;
    private final int SET_HEART_MONITOR = 1;
    private final int SET_FATIGUE = 2;
    private final int READ_HEART_AND_FATIGUE = 3;
    private boolean isAlreadyBack = false;
    private boolean hasComm = false;
    private int count = 0;
    private Context context;
    private DataSendCallback dataSendCallback;
    private BleDataForSettingArgs(Context context)
    {
        this.context = context;
    };
    private static BleDataForSettingArgs bleDataForSettingArgs;
    public static synchronized BleDataForSettingArgs getInstance(Context context)
    {
        if(bleDataForSettingArgs == null)
        {
            bleDataForSettingArgs = new BleDataForSettingArgs(context);
        }
        return bleDataForSettingArgs;
    }

    public void setDataSendCallback(DataSendCallback dataSendCallback)
    {
        this.dataSendCallback = dataSendCallback;
    }

    private Handler settingHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case SETTING_PARAMTER:
                    if(isAlreadyBack)
                    {
                        closeSend(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            couninueSend(this, msg);
                            Bundle bundle = msg.getData();
                            settingParamter(bundle.getString("unit"), bundle.getBoolean("is24"));
                            settingDateOrder(CountryUtils.getMonthAndDayFormate(), (byte)0x05);
                            settingDateOrder(CountryUtils.getLanguageFormate(), (byte)0x06);
                        }
                        else
                        {
                            closeSend(this);
                        }
                    }
                    break;
                case SET_HEART_MONITOR:
                    if(isAlreadyBack)
                    {
                        closeSendHeartMonitor(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            continueSendHeartMonitor(this, msg);
                            setHeartReatMonnitor((byte)msg.arg1);
                        }
                        else
                        {
                            closeSendHeartMonitor(this);
                        }
                    }
                    break;
                case SET_FATIGUE:
                    if(isAlreadyBack)
                    {
                        closeFatigue(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            continueSendFatigue(this, msg);
                            setFatigue((byte)msg.arg1);
                        }
                        else
                        {
                            closeFatigue(this);
                        }
                    }
                    break;
                case READ_HEART_AND_FATIGUE:
                    if(isAlreadyBack)
                    {
                        closeRead(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            conitnueRead(this, msg);
                            readFromDeviceHeartAndFatigue();
                        }
                        else
                        {
                            closeRead(this);
                        }
                    }
                    break;
            }
        }
    };

    private void conitnueRead(Handler handler, Message mzg)
    {
        Message msg = handler.obtainMessage();
        msg.what = READ_HEART_AND_FATIGUE;
        msg.arg1 = mzg.arg1;
        msg.arg2 = mzg.arg2;
        handler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(mzg.arg1, mzg.arg2));
        count ++;
    }

    private void closeRead(Handler handler)
    {
        handler.removeMessages(READ_HEART_AND_FATIGUE);
        if(!isAlreadyBack)
        {
            listener.sendFailed();
        }
        listener.sendFinished();
        isAlreadyBack = false;
        count = 0;
    }

    private void continueSendFatigue(Handler handler, Message mzg)
    {
        Message msg = Message.obtain();
        msg.what = SET_FATIGUE;
        msg.arg1 = mzg.arg1;
        msg.arg2 = mzg.arg2;
        handler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(mzg.arg2, 10));
        count ++;
    }

    private void closeFatigue(Handler handler)
    {
        handler.removeMessages(SET_FATIGUE);
        isAlreadyBack = false;
        count = 0;
    }

    private void continueSendHeartMonitor(Handler handler, Message mzg)
    {
        Message msg = new Message();
        msg.what = SET_HEART_MONITOR;
        msg.arg1 = mzg.arg1;
        msg.arg2 = mzg.arg2;
        handler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(mzg.arg2, 10));
        count ++;
    }

    private void closeSendHeartMonitor(Handler handler)
    {
        handler.removeMessages(SET_HEART_MONITOR);
        isAlreadyBack = false;
        count = 0;
    }

    private void couninueSend(Handler handler, Message msges)
    {
        Message msg = handler.obtainMessage();
        msg.what = SETTING_PARAMTER;
        msg.arg1 = msges.arg1;
        msg.arg2 = msges.arg2;
        msg.setData(msges.getData());
        handler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(msges.arg1, msges.arg2));
        count ++;
    }

    private void closeSend(Handler handler)
    {
        handler.removeMessages(SETTING_PARAMTER);
        isAlreadyBack = false;
        hasComm = false;
        count = 0;
        if(dataSendCallback != null)
        {
            dataSendCallback.sendFinished();
        }
    }

    public void setArgs(String unit, boolean is24)
    {
        hasComm = true;
        int lenth = settingParamter(unit, is24);
        settingDateOrder(CountryUtils.getMonthAndDayFormate(), (byte)0x05);
        settingDateOrder(CountryUtils.getLanguageFormate(), (byte)0x06);
        Message msg = settingHandler.obtainMessage();
        msg.what = SETTING_PARAMTER;
        msg.arg1 = lenth;
        msg.arg2 = 9;
        Bundle bundle = new Bundle();
        bundle.putString("unit", unit);
        bundle.putBoolean("is24", is24);
        msg.setData(bundle);
        settingHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(lenth, 9));
    }

    /**
     * 设置12/24小时制和公英制
     */
    private int settingParamter(String unit, boolean is24)
    {
        byte[] bytes = new byte[5];
        bytes[0] = (byte)0x01;
        bytes[1] = (byte)0x00;
        if(is24)
        {
            bytes[2] = (byte)0x01;
        }
        else
        {
            bytes[2] = (byte)0x00;
        }
        bytes[3] = (byte)0x01;
        if(unit != null && unit.equals("inch"))
        {
            bytes[4] = (byte)0x01;
        }
        else
        {
            bytes[4] = (byte)0x00;
        }
        return setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
    }

    private int settingDateOrder(boolean isMonthDay, byte comm)
    {
        byte[] datas = new byte[3];
        datas[0] = (byte)0x01;
        datas[1] = comm;
        if(isMonthDay)
        {
            datas[2] = (byte)0x00;
        }
        else
        {
            datas[2] = (byte)0x01;
        }
        return setMsgToByteDataAndSendToDevice(toDevice, datas, datas.length);
    }

    public void setHeartReatArgs(byte time)
    {
        int sendLen = setHeartReatMonnitor(time);
        Message msg = new Message();
        msg.what = SET_HEART_MONITOR;
        msg.arg1 = time;
        msg.arg2 = sendLen;
        settingHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(sendLen, 9));
    }


    private int setHeartReatMonnitor(byte time)
    {
        byte[] heData = new byte[3];
        heData[0] = (byte)0x01;
        heData[1] = (byte)0x02;
        heData[2] = time;
        return setMsgToByteDataAndSendToDevice(toDevice, heData, heData.length);
    }


    public void setFatigueSwich(byte open)
    {
        int sendLg = setFatigue(open);
        Message msg = new Message();
        msg.what = SET_FATIGUE;
        msg.arg1 = open;
        msg.arg2 = sendLg;
        settingHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(sendLg, 9));
    }


    private int setFatigue(byte open)
    {
        byte[] heData = new byte[3];
        heData[0] = (byte)0x01;
        heData[1] = (byte)0x04;
        heData[2] = open;
        return setMsgToByteDataAndSendToDevice(toDevice, heData, heData.length);
    }




    public void dealTheBack(byte[] bufferTmp)
    {
//        010001ef16
        Log.i("", "设置参数返回的：" + FormatUtils.bytesToHexString(bufferTmp));
        if(bufferTmp[0] == (byte)0x01)
        {
            if(bufferTmp[1] == (byte)0x02)
            {
                isAlreadyBack = true;
            }
            else if(bufferTmp[1] == (byte)0x00 || bufferTmp[1] == (byte)0x01)
            {
                if(hasComm)
                {
                    isAlreadyBack = true;
                    hasComm = false;
                }
            }
            else if(bufferTmp[1] == (byte)0x04)
            {
                isAlreadyBack = true;
            }
        }
        else if(bufferTmp[0] == (byte)0x00)
        {
//            0002050301fa16
            isAlreadyBack = true;
            listener.sendSuccess(bufferTmp);
        }
    }

    public void readHeartAndFatigue()
    {
        int lenglg = readFromDeviceHeartAndFatigue();
        Message msg = settingHandler.obtainMessage();
        msg.what = READ_HEART_AND_FATIGUE;
        msg.arg1 = lenglg;
        msg.arg2 = 11;
        settingHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(lenglg, 11));
    }


    private int readFromDeviceHeartAndFatigue()
    {
        byte[] readData = new byte[3];
        readData[0] = (byte)0x00;
        readData[1] = (byte)0x02;
        readData[2] = (byte)0x04;
        return setMsgToByteDataAndSendToDevice(toDevice, readData, readData.length);
    }

    private DataSendCallback listener;
    public void setOnArgsBackListener(DataSendCallback dataSendCallback)
    {
        this.listener = dataSendCallback;
    }
}
