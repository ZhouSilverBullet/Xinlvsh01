package com.huichenghe.blecontrol.Ble;

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
    private onLoseDataListener readListener;
    private String ISOPENORNO = "is open or no";
    public synchronized static BleForLostRemind getInstance()
    {
        if(bleForLostRemind == null)
        {
            bleForLostRemind = new BleForLostRemind();
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
                            continueSend(this);
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
                            continueSend(this);
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

    private void continueSend(Handler handler)
    {
        handler.sendEmptyMessageDelayed(GET_SWITCH_STATUS, 60);
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
        requestLostSwitch();
        mHandler.sendEmptyMessageDelayed(GET_SWITCH_STATUS, 80);
    }

    /**
     * 读取
     */
    private void requestLostSwitch()
    {
        byte[] bates = new byte[2];
        bates[0] = (byte)0x01;
        bates[1] = (byte)0x01;
        setMsgToByteDataAndSendToDevice(toDevice, bates, bates.length);
    }

    public void openAndHandler(boolean open)
    {
        openAndClose(open);
        Message msg = Message.obtain();
        msg.what = SETTING_OPEN_OR_CLOSE;
        Bundle bundle = new Bundle();
        bundle.putBoolean(ISOPENORNO, open);
        msg.setData(bundle);
        mHandler.sendMessageDelayed(msg, 80);
    }

    /**
     * 设置开关
     * @param open
     */
    private void openAndClose(boolean open)
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
        setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
    }



    public interface onLoseDataListener
    {
        void onDataListener(boolean isOpen);
    }

    public void setLostListener(onLoseDataListener lsr)
    {
        readListener = lsr;
    }

    public void dealTheLostResqonse(byte[] bufferTmp)
    {
//        Log.i("", "防丢返回的数据" + FormatUtils.bytesToHexString(bufferTmp));
//        防丢返回的数据010101f316
        if(bufferTmp[0] == (byte)0x01)  // 读取
        {
            if(bufferTmp[1] == (byte)0x01)  // 防丢
            {
                isAlreadyBack = true;
                if(bufferTmp[2] == (byte)0x00)
                {
                    if(readListener != null)
                    {
                        readListener.onDataListener(false);
                    }
                }
                else
                {
                    if(readListener != null)
                    {
                        readListener.onDataListener(true);
                    }
                }

            }
        }
        else if(bufferTmp[0] == (byte)0x00)
        {
            if(bufferTmp[1] == (byte)0x01)
            {
                isAlreadyBack = true;
            }
        }




    }






}
