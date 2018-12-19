package com.huichenghe.bleControl.Ble;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by lixiaoning on 16-clock-sos.
 */
public class BleDataForUpLoad extends BleBaseDataManage
{
    public static final String TAG = BleDataForUpLoad.class.getSimpleName();
    public static final byte toDevice = (byte)0x08;
    public static final byte fromDevice = (byte)0x88;
    private final int START_UPGRAND_COMM = 0;
    private final int FINISH_UPGRAND_COMM = 1;
    private boolean isSendOk = false;
    private int sendCount = 0;
    private boolean hasRecever = false;
    private BleDataForUpLoad(){};
    private DataSendCallback dataSendCallback;
    private static BleDataForUpLoad bleDataForUpLoad;
    public static BleDataForUpLoad getUpLoadInstance()
    {
        if(bleDataForUpLoad == null)
        {
            synchronized (BleDataForUpLoad.class)
            {
                if(bleDataForUpLoad == null)
                {
                    bleDataForUpLoad = new BleDataForUpLoad();
                }
            }
        }
        return bleDataForUpLoad;
    }

    private Handler updateHandler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case START_UPGRAND_COMM:
                    if(isSendOk)
                    {
                        stopSend(this);
                    }
                    else
                    {
                        if(sendCount < 5)
                        {
                            continueStart(this, msg);
                        }
                        else
                        {
                            stopSend(this);
                        }
                    }
                    break;
                case FINISH_UPGRAND_COMM:
                    if(isSendOk)
                    {
                        stopFinish(this);
                    }
                    else
                    {
                        if(sendCount < 4)
                        {
                            continueFinish(this, msg);
                            overTheUpdate();
                        }
                        else
                        {
                            stopSend(this);
                        }
                    }
                    break;
            }
        }
    };

    private void continueFinish(Handler handler, Message msg)
    {
        handler.sendEmptyMessageDelayed(FINISH_UPGRAND_COMM, 2000);
        sendCount ++;
    }

    private void stopFinish(Handler handler)
    {
        handler.removeMessages(FINISH_UPGRAND_COMM);
        if(dataSendCallback != null)
        {
            if(!isSendOk)
            {
                dataSendCallback.sendFailed();
            }
            dataSendCallback.sendFinished();
        }
        isSendOk = false;
        sendCount = 0;
    }

    private void continueStart(Handler handler, Message msg)
    {
        Message mzg = handler.obtainMessage();
        mzg.what = START_UPGRAND_COMM;
        handler.sendMessageDelayed(mzg, 3000);
        sendCount ++;
    }

    private void stopSend(Handler handler)
    {
        handler.removeMessages(START_UPGRAND_COMM);
        if(dataSendCallback != null)
        {
            if(!isSendOk)
            {
                dataSendCallback.sendFailed();
            }
            dataSendCallback.sendFinished();
        }
        isSendOk = false;
        sendCount = 0;
    }


    public void sendStartComm(int length)
    {
        hasRecever = true;
        int leng = sendToDeviceToStartUpLoad(length);
        Message msg = updateHandler.obtainMessage();
        msg.what = START_UPGRAND_COMM;
        updateHandler.sendMessageDelayed(msg, 3000);
    }

    private int sendToDeviceToStartUpLoad(int length)
    {
        byte[] data = new byte[5];
        data[0] = (byte)0x00;
        data[1] = (byte) (length);
        data[2] = (byte)(length >> 8);
        data[3] = (byte)(length >> 16);
        data[4] = (byte)(length >> 24);
        return setMsgToByteDataAndSendToDevice(toDevice, data, data.length);
    }



    public void finaishUpdate()
    {
        hasRecever = true;
        int lg = overTheUpdate();
        updateHandler.sendEmptyMessageDelayed(FINISH_UPGRAND_COMM, 2000);
    }


    public int overTheUpdate()
    {
        byte[] overDate = new byte[1];
        overDate[0] = (byte)0x02;
        return setMsgToByteDataAndSendToDevice(toDevice, overDate, overDate.length);
    }



    public void sendDataUpdate(byte[] data)
    {
        setMsgToByteDataAndSendToDevice(toDevice, data, data.length);
    }

    public void dealUpLoadBackData(byte cmd, byte[] bufferTmp)
    {
        if(bufferTmp[0] == (byte)0x00 || bufferTmp[0] == (byte)0x02)
        {
            isSendOk = true;
            if(hasRecever)
            {
                hasRecever = false;
                dataSendCallback.sendSuccess(bufferTmp);
            }
        }
        if(bufferTmp[0] == (byte)0x01)
        {
            dataSendCallback.sendSuccess(bufferTmp);
        }
    }


    public void setUpLoadCallback(DataSendCallback dataSendCallback)
    {
        this.dataSendCallback = dataSendCallback;
    }

}
