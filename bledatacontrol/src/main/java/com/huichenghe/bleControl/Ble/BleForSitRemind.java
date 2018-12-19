package com.huichenghe.bleControl.Ble;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huichenghe.bleControl.Entity.sitRemindEntity;

/**
 * Created by lixiaoning on 2016/5/16.
 */
public class BleForSitRemind extends BleBaseDataManage
{
    public static final byte toDevice = (byte)0x14;
    public static final byte fromDevice = (byte)0x94;
    public static final byte excepteionDevice = (byte)0xd4;
    private static BleForSitRemind bleForSitRemind;
    private DataSendCallback dataRecever;
    private final int GET_SIT_DATA = 0;
    private final int SETTING_SIT_DATA = 1;
    private final int DELETE_DATA = 2;
    private final String SIT_ENTITY = "sit entity";
    private boolean isSitDataResqonse = false;
    private int count = 0;
    private BleForSitRemind(){};
    public static BleForSitRemind getInstance()
    {
        if(bleForSitRemind == null)
        {
            synchronized (BleForSitRemind.class)
            {
                if(bleForSitRemind == null)
                {
                    bleForSitRemind = new BleForSitRemind();
                }
            }
        }
        return bleForSitRemind;
    }

    private Handler sitHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case GET_SIT_DATA:
                    if(isSitDataResqonse)
                    {
                        closeSendData(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            continueSendData(this, msg);
                            requsetDeviceSitData();
                        }
                        else
                        {
                            closeSendData(this);
                        }
                    }
                    break;

                case SETTING_SIT_DATA:
                    if(isSitDataResqonse)
                    {
                        closeSendData(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            continueSendSettingData(this, msg);
                            sitRemindEntity entity = (sitRemindEntity)msg.getData().getSerializable(SIT_ENTITY);
                            setAndOpenSitData(entity.getBeginTime(), entity.getEndTime(), entity.getDuration(), entity.getOpenOrno(), entity.getNumber());
                        }
                        else
                        {
                            closeSendData(this);
                        }
                    }
                    break;
                case DELETE_DATA:
                    if(isSitDataResqonse)
                    {
                        closeSendData(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            continueSendDeleteData(this, msg);
                            deleteItem(msg.getData().getInt("delete_number"));
                        }
                        else
                        {
                            closeSendData(this);
                        }
                    }
                    break;
            }
        }
    };

    private void continueSendDeleteData(Handler handler, Message msg)
    {
        Message mzg = handler.obtainMessage();
        mzg.what = DELETE_DATA;
        mzg.arg1 = msg.arg1;
        mzg.arg2 = msg.arg2;
        mzg.setData(msg.getData());
        handler.sendMessageDelayed(mzg, SendLengthHelper.getSendLengthDelay(msg.arg1, msg.arg2));
    }

    private void continueSendSettingData(Handler handler, Message msg)
    {
        Message mzg = handler.obtainMessage();
        mzg.what = SETTING_SIT_DATA;
        mzg.arg1 = msg.arg1;
        mzg.arg2 = msg.arg2;
        mzg.setData(msg.getData());
        handler.sendMessageDelayed(mzg, 800);
        count ++;
    }

    private void continueSendData(Handler handler, Message mzg)
    {
        Message msg = handler.obtainMessage();
        msg.what = GET_SIT_DATA;
        msg.arg1 = mzg.arg1;
        msg.arg2 = mzg.arg2;
        handler.sendEmptyMessageDelayed(GET_SIT_DATA, 800);
        count ++;
    }

    private void closeSendData(Handler handler)
    {
        handler.removeMessages(GET_SIT_DATA);
        isSitDataResqonse = false;
        count = 0;
    }

    public void sendToGetSitData()
    {
        int sendLg = requsetDeviceSitData();
        Message msg = sitHandler.obtainMessage();
        msg.what = GET_SIT_DATA;
        msg.arg1 = sendLg;
        msg.arg2 = 14;
        sitHandler.sendMessageDelayed(msg, 800);
    }


    private int requsetDeviceSitData()
    {
        byte[] bytes = new byte[2];
        bytes[0] = (byte)0x00;
        bytes[1] = (byte)0xff;
        return setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
    }

    public void dealTheResponseData(byte[] bufferTmp)
    {
            isSitDataResqonse = true;
            dataRecever.sendSuccess(bufferTmp);
    }

    public void setOnSitDataRecever(DataSendCallback recever)
    {
        this.dataRecever = recever;
    }

    public void setSitData(sitRemindEntity en)
    {
        int sendLg = setAndOpenSitData(en.getBeginTime(), en.getEndTime(), en.getDuration(), en.getOpenOrno(), en.getNumber());
        Message msg = new Message();
        msg.what = SETTING_SIT_DATA;
        msg.arg1 = sendLg;
        msg.arg2 = 8;
        Bundle bundle = new Bundle();
        bundle.putSerializable(SIT_ENTITY, en);
        msg.setData(bundle);
        sitHandler.sendMessageDelayed(msg, 800);
    }


    private int setAndOpenSitData(String begin, String end, int duration, int check, int num)
    {
        String[] beg = begin.split(":");
        String[] ends = end.split(":");
        int benginH = Integer.parseInt(beg[0]);
        int beginM = Integer.parseInt(beg[1]);
        int endH = Integer.parseInt(ends[0]);
        int endM = Integer.parseInt(ends[1]);
        byte[] bytes = new byte[8];
        bytes[0] = (byte)0x01;
        bytes[1] = (byte)num;
        bytes[2] = (byte)check;
        bytes[3] = (byte)beginM;
        bytes[4] = (byte)benginH;
        bytes[5] = (byte)endM;
        bytes[6] = (byte)endH;
        bytes[7] = (byte)duration;
        return setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
    }

    public void deleteThisItem(int number)
    {
        int sendLg = deleteItem(number);
        Message msg = Message.obtain();
        msg.what = DELETE_DATA;
        msg.arg1 = sendLg;
        msg.arg2 = 8;
        Bundle bundle = new Bundle();
        bundle.putInt("delete_number", number);
        msg.setData(bundle);
        sitHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(sendLg, 8));
    }

    private int deleteItem(int number)
    {
        byte[] deleteData = new byte[2];
        deleteData[0] = (byte)0x02;
        deleteData[1] = (byte)number;
        return setMsgToByteDataAndSendToDevice(toDevice, deleteData, deleteData.length);
    }

    public void dealSitException(byte[] bufferTmp)
    {
            isSitDataResqonse = true;
            if(dataRecever != null)
                dataRecever.sendSuccess(bufferTmp);
    }

}
