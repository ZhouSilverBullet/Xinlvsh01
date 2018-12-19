package com.huichenghe.blecontrol.Ble;

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
    private final int READSWITCH = 0;
    private final int OPENORCLOSE = 1;
    private static BleForQQWeiChartFacebook bleForQQWeiChartFacebook;
    private boolean isBack = false;
    private int count = 0;
    private BleForQQWeiChartFacebook()
    {

    }
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
                case READSWITCH:
                if(isBack)
                {
                    closeSend(this);
                }
                else
                {
                    if(count < 4)
                    {
                        continueSend(this);
                        readSwitchFromDevice();
                    }
                    else
                    {
                        closeSend(this);
                    }
                }
                break;
                case OPENORCLOSE:
                    if(isBack)
                    {
                        closeSendOpenOrClose(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            continueSendOpenOrClose(this, (byte)msg.arg1, (byte)msg.arg2);
                            openQQWeiChartFacebook((byte)msg.arg1, (byte)msg.arg2);
                        }
                        else
                        {
                            closeSendOpenOrClose(this);
                        }
                    }
                    break;
            }
        }
    };

    private void continueSendOpenOrClose(Handler handler, byte arg1, byte arg2)
    {
        Message msg = Message.obtain();
        msg.what = OPENORCLOSE;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        handler.sendMessageDelayed(msg, 60);
        count ++;
    }

    private void closeSendOpenOrClose(Handler handler)
    {
        handler.removeMessages(OPENORCLOSE);
        isBack = false;
        count = 0;
    }

    private void continueSend(Handler handler)
    {
        handler.sendEmptyMessageDelayed(READSWITCH, 60);
        count ++;
    }
    private void closeSend(Handler handler)
    {
        handler.removeMessages(READSWITCH);
        isBack = false;
        count = 0;
    }

    public void readSwitch()
    {
        readSwitchFromDevice();
        QQHandler.sendEmptyMessageDelayed(READSWITCH, 80);
    }

    private void readSwitchFromDevice()
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
        setMsgToByteDataAndSendToDevice(readToDevice, bytes, bytes.length);
    }

    public void dealTheResuponse(byte[] bufferTmp)
    {
//        onDataBack.onDataBack(bufferTmp);
        if(bufferTmp[1] == (byte)0x02)
        {
            isBack = true;
            onDataBack.onDataBack(bufferTmp);
        }
    }
    public void setOnDeviceDataBack(OnDeviceDataBack back)
    {
        this.onDataBack = back;
    }
    private OnDeviceDataBack onDataBack;


    public void openRemind(byte num, byte swich)
    {
        openQQWeiChartFacebook(num, swich);
        Message msg = Message.obtain();
        msg.what = OPENORCLOSE;
        msg.arg1 = num;
        msg.arg2 = swich;
        QQHandler.sendMessageDelayed(msg, 80);
    }

    /**
     * 开关提醒
     */
    public void openQQWeiChartFacebook(byte code, byte witch)
    {
        byte[] bytes = new byte[3];
        bytes[0] = (byte)0x00;
        bytes[1] = code;
        bytes[2] = witch;
        setMsgToByteDataAndSendToDevice(readToDevice, bytes, bytes.length);
    }

    public void dealOpenOrCloseRequese(byte[] bufferTmp)
    {
//        Log.i(TAG, "开关数据：" + FormatUtils.bytesToHexString(bufferTmp));
        isBack = true;
    }

    public interface OnDeviceDataBack
    {
        void onDataBack(byte[] buffer);
    }
}
