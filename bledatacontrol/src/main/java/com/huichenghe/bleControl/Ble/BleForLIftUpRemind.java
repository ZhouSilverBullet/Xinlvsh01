package com.huichenghe.bleControl.Ble;

import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huichenghe.bleControl.Utils.FormatUtils;

/**
 * Created by lixiaoning on 2016/5/16.
 */
public class BleForLIftUpRemind extends BleBaseDataManage
{
    public static final String TAG = "BleForLIftUpRemind";
    public static final byte toDevice = (byte)0x05;
    public static final byte fromDevice = (byte)0x85;
    private static BleForLIftUpRemind bleForLostRemind;
    private BleForLIftUpRemind(){};
    private boolean isAlreadyBack = false;
    private int count = 0;
    private final int GET_SWITCH_STATUS = 0;
    private final int SETTING_OPEN_OR_CLOSE = 1;
    private DataSendCallback readLostDataListener;
    private String ISOPENORNO = "is open or no";
    public static BleForLIftUpRemind getInstance()
    {
        if(bleForLostRemind == null)
        {
            synchronized (BleForLIftUpRemind.class)
            {
                if(bleForLostRemind == null)
                {
                    bleForLostRemind = new BleForLIftUpRemind();
                }
            }
        }
        return bleForLostRemind;
    }
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case GET_SWITCH_STATUS:
                    if(isAlreadyBack)
                    {
                        closeSend(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            continueSend(this, msg);
                            requestLiftUpData();
                        }
                        else
                        {
                            closeSend(this);
                        }
                    }
                    break;

                case SETTING_OPEN_OR_CLOSE:
                    if(isAlreadyBack)
                    {
                        closeSend(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            continueSendOpen(this, msg);
                            Bundle bundle = msg.getData();
                            boolean op = bundle.getBoolean(ISOPENORNO);
                            openAndClose(op);
                            openAndCloseRotateWrist(op);

                        }
                        else
                        {
                            closeSend(this);
                        }
                    }
                    break;
            }
        }
    };

    private void continueSendOpen(Handler handler, Message msg)
    {
        Message msgIn = handler.obtainMessage();
        msgIn.what = SETTING_OPEN_OR_CLOSE;
        msgIn.arg1 = msg.arg1;
        msgIn.arg2 = msg.arg2;
        msgIn.setData(msg.getData());
        handler.sendMessageDelayed(msgIn, SendLengthHelper.getSendLengthDelay(msg.arg1, msg.arg2));
    }

    private void continueSend(Handler handler, Message mzg)
    {
        Message msg = handler.obtainMessage();
        msg.what = GET_SWITCH_STATUS;
        msg.arg1 = mzg.arg1;
        msg.arg2 = mzg.arg2;
        handler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(mzg.arg1, mzg.arg2));
        count ++;
    }

    private void closeSend(Handler handler)
    {
        handler.removeMessages(GET_SWITCH_STATUS);
        isAlreadyBack = false;
        count = 0;
    }

    public void requestLiftUpData()
    {
        int sendLg = requestLiftUpSwitch();
        Message msg = mHandler.obtainMessage();
        msg.what = GET_SWITCH_STATUS;
        msg.arg1 = sendLg;
        msg.arg2 = 9;
        mHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(sendLg, 9));
    }

    private int requestLiftUpSwitch()
    {
        byte[] bates = new byte[2];
        bates[0] = (byte)0x01;
        bates[1] = (byte)0x06;
        return setMsgToByteDataAndSendToDevice(toDevice, bates, bates.length);
    }

    public void openLiftUp(boolean open)
    {
        int sendLg = openAndClose(open);
        openAndCloseRotateWrist(open);
        Message msg = Message.obtain();
        msg.what = SETTING_OPEN_OR_CLOSE;
        msg.arg1 = sendLg;
        msg.arg2 = 8;
        Bundle bundle = new Bundle();
        bundle.putBoolean(ISOPENORNO, open);
        msg.setData(bundle);
        mHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(sendLg, 8));
    }

    private int openAndClose(boolean open)
    {
        byte[] bytes = new byte[3];
        bytes[0] = (byte)0x00;
        bytes[1] = (byte)0x06;
        if(open)
        {
            bytes[2] = (byte)0x01;
        }
        else
        {
            bytes[2] = (byte)0x00;
        }
        return setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
    }


    private int openAndCloseRotateWrist(boolean open)
    {
        byte[] bytes = new byte[3];
        bytes[0] = (byte)0x00;
        bytes[1] = (byte)0x07;
        if(open)
        {
            bytes[2] = (byte)0x01;
        }
        else
        {
            bytes[2] = (byte)0x00;
        }
        return setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
    }

    public void setLiftUpListener(DataSendCallback lsr)
    {
        readLostDataListener = lsr;
    }
    public void dealLiftUpResqonse(byte[] bufferTmp)
    {
        if(bufferTmp[0] == (byte)0x01)
        {
            if(bufferTmp[1] == (byte)0x06)
            {
                isAlreadyBack = true;
            }
        }
        else if(bufferTmp[0] == (byte)0x00)
        {
            if(bufferTmp[1] == (byte)0x06)
            {
                isAlreadyBack = true;
            }
        }
        readLostDataListener.sendSuccess(bufferTmp);
    }
}
