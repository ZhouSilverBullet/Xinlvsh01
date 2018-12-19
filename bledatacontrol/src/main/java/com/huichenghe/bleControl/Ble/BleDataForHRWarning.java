package com.huichenghe.bleControl.Ble;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huichenghe.bleControl.Utils.FormatUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lixiaoning on 15-12-30.
 */
public class BleDataForHRWarning extends BleBaseDataManage
{
    private static final String TAG = BleDataForHRWarning.class.getSimpleName();
    public static final byte toDevice = (byte)0x10;
    public static final byte fromDevice = (byte)0x90;
    private static BleDataForHRWarning bleDataForHRWarning;
    private boolean isBack = false;
    private boolean hasComm = false;
    private boolean isCloseOrOpenBack = false;
    private int closeOrOpenCount = 0;
    private final int GET_WARNING_DATA = 0;
    private final int CLOSE_OR_OPEN_WARNING = 2;
    private int count = 0;
    private int maxHR, minHR;
    private DataSendCallback dataSendCallback;


    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case GET_WARNING_DATA:
                    if(isBack)
                    {
                        closeSendData(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            continueSend(this, msg);
                            requestTheHRWarningData();
                        }
                        else
                        {
                            closeSendData(this);
                        }
                    }
                    break;
//                case SET_WARNING_DATA:
//                    if(isBack)
//                    {
//                        this.removeMessages(SET_WARNING_DATA);
//                        isBack = false;
//                        count = 0;
//                    }
//                    else
//                    {
//                        if(count < 4)
//                        {
//                            this.sendEmptyMessageDelayed(SET_WARNING_DATA, 60);
//                            setAndOpenTheHRWarningData(maxHR, minHR);
//                            count ++;
//                        }
//                        else
//                        {
//                            this.removeMessages(SET_WARNING_DATA);
//                            isBack = false;
//                            count = 0;
//                        }
//                    }
//                    break;
                case CLOSE_OR_OPEN_WARNING:
                    if(isCloseOrOpenBack)
                    {
                        closeSendDataClose(this);
                    }
                    else
                    {
                        if(closeOrOpenCount < 4)
                        {
                            conitueSendCloseData(this, msg);
                            closeOrOpenWarning(maxHR, minHR, msg.getData().getByte("closeOrOpen"));
                        }
                        else
                        {
                            closeSendDataClose(this);
                        }
                    }
                    break;
            }
        }
    };

    private void closeSendDataClose(Handler handler)
    {
        handler.removeMessages(CLOSE_OR_OPEN_WARNING);
        isCloseOrOpenBack = false;
        closeOrOpenCount = 0;
    }

    private void continueSend(Handler handler, Message msg)
    {
        Message mzg = handler.obtainMessage();
        mzg.what = GET_WARNING_DATA;
        mzg.arg1 = msg.arg1;
        mzg.arg2 = msg.arg2;
        handler.sendMessageDelayed(mzg, SendLengthHelper.getSendLengthDelay(msg.arg1, msg.arg2));
        count ++;
    }

    private void conitueSendCloseData(Handler handler, Message msges)
    {
        Message msg = handler.obtainMessage();
        msg.what = CLOSE_OR_OPEN_WARNING;
        msg.arg1 = msges.arg1;
        msg.arg2 = msges.arg2;
        msg.setData(msges.getData());
        handler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(msges.arg1, msges.arg2));
        closeOrOpenCount ++;
    }

    private void closeSendData(Handler handler)
    {
        handler.removeMessages(GET_WARNING_DATA);
        if(dataSendCallback != null)
        {
            if(!isBack)
            {
                dataSendCallback.sendFailed();
            }
            dataSendCallback.sendFinished();
        }
        isBack = false;
        hasComm = false;
        count = 0;
    }

    public static BleDataForHRWarning getInstance()
    {
        if(bleDataForHRWarning == null)
        {
            synchronized (BleDataForHRWarning.class)
            {
                if(bleDataForHRWarning == null)
                {
                    bleDataForHRWarning = new BleDataForHRWarning();
                }
            }
        }
        return bleDataForHRWarning;
    }

    private BleDataForHRWarning(){};

    public void setDataSendCallback (DataSendCallback dataSendCallback)
    {
        this.dataSendCallback = dataSendCallback;
    }

//    public void sendToGetData(int maxHR, int minHR)
//    {
//        this.maxHR = maxHR;
//        this.minHR = minHR;
//        setAndOpenTheHRWarningData(maxHR, minHR);
//        mHandler.sendEmptyMessageDelayed(SET_WARNING_DATA, 80);
//    }
//    private void setAndOpenTheHRWarningData(int maxHR, int minHR)
//    {
//        byte[] byteArray = new byte[4];
//        byteArray[0] = (byte)0x01;
//        byteArray[1] = (byte)0x00;
//        byteArray[2] = (byte)(maxHR & 0xff);
//        byteArray[3] = (byte)(minHR & 0xff);
//        setMsgToByteDataAndSendToDevice(toDevice, byteArray, byteArray.length);
//    }
    public void requestWarningData()
    {
        hasComm = true;
        int sendLg = requestTheHRWarningData();
        Message msg = mHandler.obtainMessage();
        msg.what = GET_WARNING_DATA;
        msg.arg1 = sendLg;
        msg.arg2 = 10;
        mHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(sendLg, 10));
    }

    private int requestTheHRWarningData()
    {
        byte type = (byte)0x02;
        byte[] array = new byte[1];
        array[0] = type;
        return setMsgToByteDataAndSendToDevice(toDevice, array, array.length);
    }



    public void closeOrOpenWarning(int maxHR, int minHR, byte open)
    {
        this.maxHR = maxHR;
        this.minHR = minHR;
        int closeLg = settingsHRWarning(maxHR, minHR, open);
        Message msg = mHandler.obtainMessage();
        msg.what = CLOSE_OR_OPEN_WARNING;
        msg.arg1 = closeLg;
        msg.arg2 = 10;
        Bundle bundle = new Bundle();
        bundle.putByte("openOrNo", open);
        msg.setData(bundle);
        mHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(closeLg, 10));
    }

    private int settingsHRWarning(int maxHR, int minHR, byte open)
    {
        byte[] byteArray = new byte[4];
        byteArray[0] = (byte)0x01;
        byteArray[1] = open;
        byteArray[2] = (byte)(maxHR & 0xff);
        byteArray[3] = (byte)(minHR & 0xff);
        return setMsgToByteDataAndSendToDevice(toDevice, byteArray, byteArray.length);
    }


    public void dealTheHRData(byte[] bufferTmp, Context mcContext)
    {
        if (bufferTmp[0] == (byte)0x02)
        {
//            Intent intent = new Intent();
//            intent.setAction(MyConfingInfo.ACTION_FOR_HR_WARNING);
//            mcContext.sendBroadcast(intent);
            if(hasComm)
            {
                isBack = true;
                hasComm = false;
                dataSendCallback.sendSuccess(bufferTmp);
            }
        }
        else if(bufferTmp[0] == (byte)0x01)
        {
            isCloseOrOpenBack = true;
            requestWarningData();
        }
    }



}
