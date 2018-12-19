package com.huichenghe.blecontrol.Ble;

import android.os.Handler;
import android.os.Message;

/**
 * Created by lixiaoning on 2016/5/19.
 */
public class BleDataForTakePhoto extends BleBaseDataManage
{
    public final static byte startToDevice = (byte)0x0D;
    public final static byte startFromDevice = (byte)0x8D;
    public final static byte takeToDevice = (byte)0x0E;
    public final static byte takeFromDevice = (byte)0x8E;
    private final int START_END_MESSAGE = 0;
    private final int TAKE_PHOTO_MESSAGE = 1;
    private boolean isAlreadyBack = false;
    private boolean hasComm = false;
    private int count = 0;
    private static BleDataForTakePhoto bleDataForTakePhoto;
    private BleDataForTakePhoto()
    {

    }
    public static synchronized BleDataForTakePhoto getInstance()
    {
        if(bleDataForTakePhoto == null)
        {
            bleDataForTakePhoto = new BleDataForTakePhoto();
        }
        return bleDataForTakePhoto;
    }
    private Handler takePhotoHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case START_END_MESSAGE:
                    if(isAlreadyBack)
                    {
                        closeSendStartMessage(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            continueSendStartMessage(this, msg.arg1);
                            sendTakePhoto((byte)msg.arg1);
                        }
                        else
                        {
                            closeSendStartMessage(this);
                        }
                    }
                    break;
                case TAKE_PHOTO_MESSAGE:
                    if(isAlreadyBack)
                    {
                        closeSendTakeMessage(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            continueSendTakeMessage(this);
                            backMessage();
                        }
                        else
                        {
                            closeSendTakeMessage(this);
                        }
                    }
                    break;
            }
        }
    };

    private void continueSendTakeMessage(Handler handler)
    {
        handler.sendEmptyMessage(TAKE_PHOTO_MESSAGE);
        count ++;
    }

    private void closeSendTakeMessage(Handler handler)
    {
        handler.removeMessages(TAKE_PHOTO_MESSAGE);
        isAlreadyBack = false;
        count = 0;
    }

    private void continueSendStartMessage(Handler handler, int arg1)
    {
        Message msg = Message.obtain();
        msg.what = START_END_MESSAGE;
        msg.arg1 = arg1;
        handler.sendMessageDelayed(msg, 60);
        count ++;
    }

    private void closeSendStartMessage(Handler handler)
    {
        handler.removeMessages(START_END_MESSAGE);
        isAlreadyBack = false;
        count = 0;
        hasComm = false;
    }

    public void openTakePhoto(byte swich)
    {
        hasComm = true;
        sendTakePhoto(swich);
        Message msg = Message.obtain();
        msg.what = START_END_MESSAGE;
        msg.arg1 = swich;
        takePhotoHandler.sendMessageDelayed(msg, 80);
    }

    private void sendTakePhoto(byte swich)
    {
        byte[] bytes = new byte[1];
        bytes[0] = swich;
        setMsgToByteDataAndSendToDevice(startToDevice, bytes, bytes.length);
    }

    public void dealOpenResponse(byte[] bufferTmp)
    {
//        Log.i("", "返回的数据" + FormatUtils.bytesToHexString(bufferTmp));
        if(hasComm)
        {
            isAlreadyBack = true;
            hasComm = false;
        }
        if(bufferTmp[0] == (byte)0x00)
        {
//            Log.i("", "点击打开相机onDeviceTakePhotoOpen");
            if(onDeviceTakePhotoOpen != null)
            onDeviceTakePhotoOpen.onOpen();
        }
    }


    private void sendToDeviceMessage()
    {
        backMessage();
    }

    private void backMessage()
    {

        setMsgToByteDataAndSendToDevice(takeToDevice, new byte[0], 0);
    }
    /**
     * 收到手环的拍照指令
     */
    public void backMessageToDevice()
    {
        // 返回数据
        sendToDeviceMessage();
        // 回调方法通知执行快门拍照
        if(onDeviceTakePhotoOpen != null)
        {
            onDeviceTakePhotoOpen.onMessageRecever();
        }
    }

    public interface OnDeviceTakePhotoOpen
    {
        void onOpen();
        void onMessageRecever();
    }

    private OnDeviceTakePhotoOpen onDeviceTakePhotoOpen;
    public void setOnDeviceTakePhotoOpen(OnDeviceTakePhotoOpen onOpen)
    {
        this.onDeviceTakePhotoOpen = onOpen;
    }
}
