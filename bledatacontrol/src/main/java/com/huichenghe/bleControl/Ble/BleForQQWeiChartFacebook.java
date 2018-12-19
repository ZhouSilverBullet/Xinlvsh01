package com.huichenghe.bleControl.Ble;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by lixiaoning on 2016/5/18.
 */
public class BleForQQWeiChartFacebook extends BleBaseDataManage
{
    public static final String TAG = BleForQQWeiChartFacebook.class.getSimpleName();
    public final static byte readToDevice = (byte)0x05;
    public final static byte readFromDevice = (byte)0x85;
    private final int READ_SWITCH = 0;
    private final int OPEN_OR_CLOSE = 1;
    private final int CHECK_INFO_TYPE = 2;
    private static BleForQQWeiChartFacebook bleForQQWeiChartFacebook;
    private boolean isBack = false;
    private int count = 0;
    private BleForQQWeiChartFacebook(){}
    public static synchronized BleForQQWeiChartFacebook getInstance()
    {
        if(bleForQQWeiChartFacebook == null)
        {
            bleForQQWeiChartFacebook = new BleForQQWeiChartFacebook();
        }
        return bleForQQWeiChartFacebook;
    }

    private Handler QQHandler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case READ_SWITCH:
                if(isBack)
                {
                    closeSend(this);
                }
                else
                {
                    if(count < 4)
                    {
                        continueSend(this, msg);
                        readSwitchFromDevice();
                    }
                    else
                    {
                        closeSend(this);
                    }
                }
                break;
                case OPEN_OR_CLOSE:
                    if(isBack)
                    {
                        closeSendOpenOrClose(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            continueSendOpenOrClose(this, msg);
                            openQQWeiChartFacebook((byte)msg.arg1, (byte)msg.arg2);
                        }
                        else
                        {
                            closeSendOpenOrClose(this);
                        }
                    }
                    break;
                case CHECK_INFO_TYPE:
                    if(isBack)
                    {
                        closeCheck(this, msg);
                    }
                    else
                    {
                        if(count < 3)
                        {
                            checkInfoType();
                            conitnueCheck(this, msg);
                        }
                        else
                        {
                            closeCheck(this, msg);
                        }
                    }

                    break;
            }
        }
    };

    private void conitnueCheck(Handler handler, Message msg)
    {
        handler.sendEmptyMessageDelayed(CHECK_INFO_TYPE, 500);
        count++;
    }

    private void closeCheck(Handler handler, Message msg)
    {
        handler.removeMessages(CHECK_INFO_TYPE);
        isBack = false;
        count = 0;
    }

    private void continueSendOpenOrClose(Handler handler, Message mzg)
    {
        Message msg = Message.obtain();
        msg.what = OPEN_OR_CLOSE;
        msg.arg1 = mzg.arg1;
        msg.arg2 = mzg.arg2;
        Bundle bundle = mzg.getData();
        msg.setData(bundle);
        handler.sendMessageDelayed(msg,
                SendLengthHelper.getSendLengthDelay(bundle.getInt("send_length"), bundle.getInt("rece_length")));
        count ++;
    }

    private void closeSendOpenOrClose(Handler handler)
    {
        handler.removeMessages(OPEN_OR_CLOSE);
        isBack = false;
        count = 0;
    }

    private void continueSend(Handler handler, Message msg)
    {
        Message mzg = handler.obtainMessage();
        mzg.what = READ_SWITCH;
        mzg.arg1 = msg.arg1;
        mzg.arg2 = msg.arg2;
        handler.sendMessageDelayed(mzg, SendLengthHelper.getSendLengthDelay(msg.arg1, msg.arg2));
        count ++;
    }
    private void closeSend(Handler handler)
    {
        handler.removeMessages(READ_SWITCH);
        isBack = false;
        count = 0;
    }


    public void checkInfoType()
    {
        checkData();
        Message msg = QQHandler.obtainMessage();
        msg.what = CHECK_INFO_TYPE;
        QQHandler.sendMessageDelayed(msg, 500);
    }
    private void checkData()
    {
        byte[] data = new byte[2];
        data[0] = (byte)0x02;
        data[1] = (byte)0x0f;
        setMsgToByteDataAndSendToDevice(readToDevice, data, data.length);
    }

    public void readSwitch()
    {
        int readLg = readSwitchFromDevice();
        Message msg = QQHandler.obtainMessage();
        msg.what = READ_SWITCH;
        msg.arg1 = readLg;
        msg.arg2 = 21;
        QQHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(readLg, 21));
    }

    private int readSwitchFromDevice()
    {
        byte[] bytes = new byte[8];
        bytes[0] = (byte)0x01;
        bytes[1] = (byte)0x02;
        bytes[2] = (byte)0x09;
        bytes[3] = (byte)0x0a;
        bytes[4] = (byte)0x0b;
        bytes[5] = (byte)0x0c;
        bytes[6] = (byte)0x0d;
        bytes[7] = (byte)0x0e;
        return setMsgToByteDataAndSendToDevice(readToDevice, bytes, bytes.length);
    }


    public void dealTheResuponse(byte[] bufferTmp)
    {
        if(bufferTmp[1] == (byte)0x02)
        {
            isBack = true;
            onDataBack.sendSuccess(bufferTmp);
        }
    }
    private DataSendCallback onDataBack;
    private DataSendCallback onCheckBack;
    public void setOnDeviceDataBack(DataSendCallback back)
    {
        this.onDataBack = back;
    }

    public void setOnDeviceCheckBack(DataSendCallback backs)
    {
        this.onCheckBack = backs;
    }



    public void openRemind(byte num, byte swich)
    {
        int sendLg = openQQWeiChartFacebook(num, swich);
        Message msg = Message.obtain();
        msg.what = OPEN_OR_CLOSE;
        msg.arg1 = num;
        msg.arg2 = swich;
        Bundle bundle = new Bundle();
        bundle.putInt("send_length", sendLg);
        bundle.putInt("rece_length", 8);
        msg.setData(bundle);
        QQHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(sendLg, 8));
    }

    public int openQQWeiChartFacebook(byte code, byte witch)
    {
        byte[] bytes = new byte[3];
        bytes[0] = (byte)0x00;
        bytes[1] = code;
        bytes[2] = witch;
        return setMsgToByteDataAndSendToDevice(readToDevice, bytes, bytes.length);
    }

    public void dealOpenOrCloseRequese(byte[] bufferTmp)
    {
        isBack = true;
        if(bufferTmp[0] == (byte)0x02 && bufferTmp[1] == (byte)0x0f)
        {
            if(onCheckBack != null)
            {
                onCheckBack.sendSuccess(bufferTmp);
            }


        }
    }

}
