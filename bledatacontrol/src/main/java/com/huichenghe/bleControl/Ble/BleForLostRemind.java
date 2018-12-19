package com.huichenghe.bleControl.Ble;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by lixiaoning on 2016/5/16.
 */
public class BleForLostRemind extends BleBaseDataManage
{
    public static final byte toDevice = (byte)0x05;
    public static final byte fromDevice = (byte)0x85;
    private static BleForLostRemind bleForLostRemind;
    private BleForLostRemind(){};
    private boolean isAlreadyBack = false;
    private int count = 0;
    private final int GET_SWITCH_STATUS = 0;
    private final int SETTING_OPEN_OR_CLOSE = 1;
    private DataSendCallback readLostDataListener;
    private String ISOPENORNO = "is open or no";
    public static BleForLostRemind getInstance()
    {
        if(bleForLostRemind == null)
        {
            synchronized (BleForLostRemind.class)
            {
                if(bleForLostRemind == null)
                {
                    bleForLostRemind = new BleForLostRemind();
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
                            requestLostSwitch();
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

    public void requestAndHandler()
    {
        int sendLg = requestLostSwitch();
        Message msg = mHandler.obtainMessage();
        msg.what = GET_SWITCH_STATUS;
        msg.arg1 = sendLg;
        msg.arg2 = 9;
        mHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(sendLg, 9));
    }

    /**
     * 读取
     */
    private int requestLostSwitch()
    {
        byte[] bates = new byte[2];
        bates[0] = (byte)0x01;
        bates[1] = (byte)0x01;
        return setMsgToByteDataAndSendToDevice(toDevice, bates, bates.length);
    }

    public void openAndHandler(boolean open)
    {
        int sendLg = openAndClose(open);
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
        bytes[1] = (byte)0x01;
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

    public void setLostListener(DataSendCallback lsr)
    {
        readLostDataListener = lsr;
    }
    public void dealTheLostResqonse(byte[] bufferTmp)
    {
        if(bufferTmp[0] == (byte)0x01)
        {
            if(bufferTmp[1] == (byte)0x01)
            {
                isAlreadyBack = true;
            }
        }
        else if(bufferTmp[0] == (byte)0x00)
        {
            if(bufferTmp[1] == (byte)0x01)
            {
                isAlreadyBack = true;
            }
        }
        readLostDataListener.sendSuccess(bufferTmp);
    }


}
