package com.huichenghe.blecontrol.Ble;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.huichenghe.xinlvshuju.Utils.FormatUtils;


/**
 * Created by lixiaoning on 2016/5/18.
 */
public class BleDataForQQAndOtherRemine extends BleBaseDataManage
{
    public final static String TAG = BleDataForQQAndOtherRemine.class.getSimpleName();
    public final static byte toDevice = (byte)0x0B;
    public final static byte fromDevice = (byte)0x8B;
    public final static byte qq = (byte)0x02;
    public final static byte weichart = (byte)0x01;
    public final static byte facebook = (byte)0x03;
    public final static byte skype = (byte)0x04;
    public final static byte twitter = (byte)0x05;
    public final static byte whatsapp = (byte)0x06;
    private final int SEND_QQ_MESSANE = 0;
    private final int SEND_WEICHART_MESSAGE = 1;
    private final int SEND_FACEBOOK_MESSAGE = 2;
    private final String SEND_NUMBER = "send number";
    private final String SEND_CONTENT = "send content";
    private boolean isSendOk = false;
    private int count = 0;

    private static BleDataForQQAndOtherRemine bleDataForQQAndOtherRemine;
    private BleDataForQQAndOtherRemine()
    {
    }

    public static synchronized BleDataForQQAndOtherRemine getIntance()
    {
        if(bleDataForQQAndOtherRemine == null)
        {
            bleDataForQQAndOtherRemine = new BleDataForQQAndOtherRemine();
        }
        return bleDataForQQAndOtherRemine;
    }

    private Handler remindHandler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case SEND_QQ_MESSANE:
                    if(isSendOk)
                    {
                        closeSendQQ(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            Bundle bun = msg.getData();
                            byte num =  bun.getByte(SEND_NUMBER);
                            byte[] content =  bun.getByteArray(SEND_CONTENT);
                            countinueSendQQ(this, num, content, msg.arg1);
                            startRemindToDevice(num, content);
                        }
                        else
                        {
                            closeSendQQ(this);
                        }
                    }
                    break;
                case SEND_WEICHART_MESSAGE:
                    Log.i(TAG, "微信isSendOk：" + isSendOk + "count:" + count);
                    if(isSendOk)
                    {
                        closeSendWeiChart(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            Bundle bun = msg.getData();
                            byte numWei =  bun.getByte(SEND_NUMBER);
                            byte[] contentWei =  bun.getByteArray(SEND_CONTENT);
//                            Log.i(TAG, "微信内容：" + FormatUtils.bytesToHexString(contentWei));
                            countinueSendWeiChart(this, numWei, contentWei, msg.arg1);
                            startRemindToDevice(numWei, contentWei);
                        }
                        else
                        {
                            closeSendWeiChart(this);
                        }
                    }
                    break;
                case SEND_FACEBOOK_MESSAGE:
                    if(isSendOk)
                    {
                        closeSendFacebook(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            Bundle bun = msg.getData();
                            byte num =  bun.getByte(SEND_NUMBER);
                            byte[] content =  bun.getByteArray(SEND_CONTENT);
                            countinueSendFacebook(this, num, content, msg.arg1);
                            startRemindToDevice(num, content);
                        }
                        else
                        {
                            closeSendFacebook(this);
                        }
                    }
                    break;
            }

        }
    };

    private void countinueSendFacebook(Handler handler, byte num, byte[] name, int delayTime)
    {
        Message msg = Message.obtain();
        msg.what = SEND_FACEBOOK_MESSAGE;
        Bundle bu = new Bundle();
        bu.putByte(SEND_NUMBER, num);
        bu.putByteArray(SEND_CONTENT, name);
        msg.setData(bu);
        msg.arg1 = delayTime;
        handler.sendMessageDelayed(msg, delayTime);
        count ++;
    }

    private void closeSendFacebook(Handler handler)
    {
        handler.removeMessages(SEND_FACEBOOK_MESSAGE);
        isSendOk = false;
        count = 0;
    }

    private void countinueSendWeiChart(Handler handler, byte num, byte[] name, int delayTime)
    {
        Log.i(TAG, "微信内容：countinueSendWeiChart:" + FormatUtils.bytesToHexString(name));
        Message msg = Message.obtain();
        msg.what = SEND_WEICHART_MESSAGE;
        Bundle bu = new Bundle();
        bu.putByte(SEND_NUMBER, num);
        bu.putByteArray(SEND_CONTENT, name);
        msg.setData(bu);
        msg.arg1 = delayTime;
        handler.sendMessageDelayed(msg, delayTime);
        count ++;
    }

    private void closeSendWeiChart(Handler handler)
    {
        handler.removeMessages(SEND_WEICHART_MESSAGE);
        isSendOk = false;
        count = 0;
    }

    private void countinueSendQQ(Handler handler, byte num, byte[] name, int delayTime)
    {
        Message msg = Message.obtain();
        msg.what = SEND_QQ_MESSANE;
        Bundle bu = new Bundle();
        bu.putByte(SEND_NUMBER, num);
        bu.putByteArray(SEND_CONTENT, name);
        msg.setData(bu);
        msg.what = delayTime;
        handler.sendMessageDelayed(msg, delayTime);
        count ++;
    }

    private void closeSendQQ(Handler handler)
    {
        handler.removeMessages(SEND_QQ_MESSANE);
        isSendOk = false;
        count = 0;
    }

    public void sendMessageToDevice(byte num, byte[] msg)
    {
        int remindLength = startRemindToDevice(num, msg);
        int delayTime = remindLength * 24 + getRandom();
        Message msgs = remindHandler.obtainMessage();
        Bundle bun = new Bundle();
        bun.putByte(SEND_NUMBER, num);
        bun.putByteArray(SEND_CONTENT, msg);
        msgs.setData(bun);
        msgs.arg1 = delayTime;
        if(num == weichart)
        {
            msgs.what = SEND_WEICHART_MESSAGE;
        }
        else if(num == qq)
        {
            msgs.what = SEND_QQ_MESSANE;
        }
        else if(num == facebook)
        {
            msgs.what = SEND_FACEBOOK_MESSAGE;
        }
        else
        {
            msgs.what = SEND_WEICHART_MESSAGE;
        }
        remindHandler.sendMessageDelayed(msgs, delayTime);
    }

    private int getRandom()
    {
        return (int)(24 + Math.random() * (100 - 24));
    }
    /**
     * 发送数据给设备
     * @param num
     * @param msg
     */
    private int startRemindToDevice(byte num, byte[] msg)
    {
        byte[] data = new byte[msg.length + 1];
        System.arraycopy(msg, 0, data, 1, msg.length);
        data[0] = num;
        int remindLength = setMsgToByteDataAndSendToDevice(toDevice, data, data.length);
        return remindLength;
    }
    public void dealRemindResponse(byte[] bufferTmp)
    {
        isSendOk = true;
    }
}
