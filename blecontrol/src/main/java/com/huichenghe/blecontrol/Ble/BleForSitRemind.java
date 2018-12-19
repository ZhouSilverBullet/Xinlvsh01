package com.huichenghe.blecontrol.Ble;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huichenghe.xinlvshuju.DataEntites.sitRemindEntity;
import com.huichenghe.xinlvshuju.Utils.FormatUtils;

/**
 * Created by lixiaoning on 2016/5/16.
 */
public class BleForSitRemind extends BleBaseDataManage
{
    public static final byte toDevice = (byte)0x14;
    public static final byte fromDevice = (byte)0x94;
    public static final byte excepteionDevice = (byte)0xd4;
    private static BleForSitRemind bleForSitRemind;
    private onSitDataRecever dataRecever;
    private final int GETSITDATA = 0;
    private final int SETTINGSITDATA = 1;
    private final int DELETEDATA = 2;
    private final String SITENTITY = "sit entity";
    private boolean isSitDataResqonse = false;
    private int count = 0;
    private BleForSitRemind(){};
    public synchronized static BleForSitRemind getInstance()
    {
        if(bleForSitRemind == null)
        {
            bleForSitRemind = new BleForSitRemind();
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
                case GETSITDATA:
                    if(isSitDataResqonse)
                    {
                        closeSendData(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            continueSendData(this);
                            requsetDeviceSitData();
                        }
                        else
                        {
                            closeSendData(this);
                        }
                    }
                    break;

                case SETTINGSITDATA:
                    if(isSitDataResqonse)
                    {
                        closeSendData(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            continueSendData(this);
                            sitRemindEntity entity = (sitRemindEntity)msg.getData().getSerializable(SITENTITY);
                            setAndOpenSitData(entity.getBeginTime(), entity.getEndTime(), entity.getDuration(), entity.getOpenOrno(), entity.getNumber());
                        }
                        else
                        {
                            closeSendData(this);
                        }
                    }
                    break;
                case DELETEDATA:
                    if(isSitDataResqonse)
                    {
                        closeSendData(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            continueSendData(this);
                            deleteItem(msg.arg1);
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

    private void continueSendData(Handler handler)
    {
        handler.sendEmptyMessageDelayed(GETSITDATA, 60);
        count ++;
    }

    private void closeSendData(Handler handler)
    {
        handler.removeMessages(GETSITDATA);
        isSitDataResqonse = false;
        count = 0;
    }

    public void sendToGetSitData()
    {
        requsetDeviceSitData();
        sitHandler.sendEmptyMessageDelayed(GETSITDATA, 80);
    }


    /**
     * 获取手环久坐提醒列表及开关状态
     */
    private void requsetDeviceSitData()
    {
        byte[] bytes = new byte[2];
        bytes[0] = (byte)0x00;
        bytes[1] = (byte)0xff;
        setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
    }

    public void dealTheResponseData(byte[] bufferTmp)
    {

//        68 d4 0100 04 4116

        Log.i("", "久坐提醒数据:" + FormatUtils.bytesToHexString(bufferTmp));
        if(bufferTmp[0] == (byte)0x00)
        {
            isSitDataResqonse = true;
            if(bufferTmp.length < 4)
            {
                dataRecever.onRecever(null);
            }
            else
            {
//                00 00010009030e3c
//                   03011f0c1f133c ff16
                int count = (bufferTmp.length - 3)/7;
                for (int i = 0; i < count; i++)
                {
                    int number = bufferTmp[1 + i * 7] & 0xff;
                    int open = bufferTmp[2 + i * 7] & 0xff;
                    int beginM = bufferTmp[3 + i * 7] & 0xff;
                    int beginH = bufferTmp[4 + i * 7] & 0xff;
                    int endM = bufferTmp[5 + i * 7] & 0xff;
                    int endH = bufferTmp[6 + i * 7] & 0xff;
                    int duration = bufferTmp[7 + i * 7] & 0xff;
                    String beginTime = beginH + ":" + ((beginM < 10) ? ("0" + beginM) : beginM);
                    String endTime = endH + ":" + ((endM < 10) ? ("0" + endM) :endM);
                    dataRecever.onRecever(new sitRemindEntity(number, open, beginTime, endTime, duration));
                }
            }
        }
        else if(bufferTmp[0] == (byte)0x01)
        {
            isSitDataResqonse = true;
        }
        else if(bufferTmp[0] == (byte)0x02)
        {
            isSitDataResqonse = true;
        }


    }

    public void setOnSitDataRecever(onSitDataRecever recever)
    {
        this.dataRecever = recever;
    }

    public void setSitData(sitRemindEntity en)
    {
        setAndOpenSitData(en.getBeginTime(), en.getEndTime(), en.getDuration(), en.getOpenOrno(), en.getNumber());
        Message msg = new Message();
        msg.what = SETTINGSITDATA;
        Bundle bundle = new Bundle();
        bundle.putSerializable(SITENTITY, en);
        msg.setData(bundle);
        sitHandler.sendMessageDelayed(msg, 80);
    }


    /**
     * 设置和打开久坐提醒
     */
    public void setAndOpenSitData(String begin, String end, int duration, int check, int num)
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
        setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
    }

    /**
     * 删除久坐提醒
     * @param number
     */
    public void deleteThisItem(int number)
    {
        deleteItem(number);
        Message msg = Message.obtain();
        msg.what = DELETEDATA;
        msg.arg1 = number;
        sitHandler.sendMessageDelayed(msg, 80);
    }

    /**
     * 删除久坐提醒, 具体实现
     * @param number
     */
    private void deleteItem(int number)
    {
        byte[] deleteData = new byte[2];
        deleteData[0] = (byte)0x02;
        deleteData[1] = (byte)number;
        setMsgToByteDataAndSendToDevice(toDevice, deleteData, deleteData.length);
    }

    public void dealSitException(byte[] bufferTmp)
    {
        Log.i("", "异常数据：" + FormatUtils.bytesToHexString(bufferTmp));
        isSitDataResqonse = true;
        if(dataRecever != null)
        dataRecever.onException();
    }

    public interface onSitDataRecever
    {
        void onRecever(sitRemindEntity entity);
        void onException();
    }
}
