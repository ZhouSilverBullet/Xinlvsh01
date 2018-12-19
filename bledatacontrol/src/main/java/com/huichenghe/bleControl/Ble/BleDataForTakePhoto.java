package com.huichenghe.bleControl.Ble;

import android.os.Bundle;
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
    public static BleDataForTakePhoto getInstance()
    {
        if(bleDataForTakePhoto == null)
        {
            synchronized (BleDataForTakePhoto.class)
            {
                if(bleDataForTakePhoto == null)
                {
                    bleDataForTakePhoto = new BleDataForTakePhoto();
                }
            }
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
                            continueSendStartMessage(this, msg);
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

    private void continueSendStartMessage(Handler handler, Message msges)
    {
        Message msg = Message.obtain();
        msg.what = START_END_MESSAGE;
        msg.arg1 = msges.arg1;
        Bundle bundle = msges.getData();
        msg.setData(bundle);
        handler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(bundle.getInt("send_length"), bundle.getInt("rece_length")));
        count ++;
    }

    private void closeSendStartMessage(Handler handler)
    {
        handler.removeMessages(START_END_MESSAGE);
        if(onDeviceCallback != null)
        {
            if(!isAlreadyBack)
            {
                onDeviceCallback.sendFailed();
            }
            onDeviceCallback.sendFinished();
        }
        isAlreadyBack = false;
        count = 0;
        hasComm = false;
    }

    public void openTakePhoto(byte swich)
    {
        hasComm = true;
        int sendLg = sendTakePhoto(swich);
        Message msg = Message.obtain();
        msg.what = START_END_MESSAGE;
        msg.arg1 = swich;
        Bundle bundle = new Bundle();
        bundle.putInt("send_length", sendLg);
        bundle.putInt("rece_length", 7);
        msg.setData(bundle);
        takePhotoHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(sendLg, 7));
    }

    private int sendTakePhoto(byte swich)
    {
        byte[] bytes = new byte[1];
        bytes[0] = swich;
        return setMsgToByteDataAndSendToDevice(startToDevice, bytes, bytes.length);
    }

    public void dealOpenResponse(byte[] bufferTmp)
    {
        if(hasComm)
        {
            isAlreadyBack = true;
            hasComm = false;
            if(onDeviceCallback != null)
            {
                onDeviceCallback.sendSuccess(bufferTmp);
            }
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
    public void backMessageToDevice()
    {
        sendToDeviceMessage();
        if(onDeviceCallback != null)
        {
            onDeviceCallback.sendSuccess(null);
        }
    }


    private DataSendCallback onDeviceCallback;
    public void setOnDeviceTakePhotoOpen(DataSendCallback cameraCallback)
    {
        this.onDeviceCallback = cameraCallback;
    }
}
