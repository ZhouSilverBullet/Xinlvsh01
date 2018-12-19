package com.huichenghe.blecontrol.Ble;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huichenghe.xinlvshuju.DataEntites.CustomRemindEntity;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.FormatUtils;

import java.io.UnsupportedEncodingException;

/**
 * 自定义提醒类，发送数据和处理返回的数据
 * Created by lixiaoning on 15-12-info.
 */
public class BleDataForCustomRemind extends BleBaseDataManage
{
    public static final String TAG = BleDataForCustomRemind.class.getSimpleName();
    public static final byte toDevice = (byte)0x09;
    public static final byte fromDevice = (byte)0x89;
    public static final byte fromDeviceNull = (byte)0xC9;
    private final int GETCUSTOMEREMIND = 0;
    private final String REMINDNUMBER = "remind number";
    private boolean isRemindDataBack = false;
    private int count = 0;
    private DeleteCallback callback;
    private RequesCallback requesCallback;
    private static BleDataForCustomRemind bleDataForCustomRemind;
    private int numberes = 0;
    private Handler customRemindHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
//            numberes = msg.getData().getInt(REMINDNUMBER);
            switch (msg.what)
            {
                case GETCUSTOMEREMIND:
                    Log.i(TAG, "是否返回数据：" + isRemindDataBack + "-编号：" + numberes);
                    if(numberes < 8)
                    {
                        if(isRemindDataBack)
                        {
                            continueSendNext(this);
                            numberes = numberes + 1;
                            getCustomRemind(numberes);
                        }
                        else
                        {
                            if(count < 4)
                            {
                                continueSendThis(this);
                                getCustomRemind(numberes);
                            }
                            else
                            {
                                continueSendNext(this);
                                numberes = numberes + 1;
                                getCustomRemind(numberes);
                            }
                        }
                    }
                    else
                    {
                        closeSendData(this);
                        requesCallback.requestOver();
                    }
                    break;
            }
        }
    };

    private void closeSendData(Handler handler)
    {
        handler.removeMessages(GETCUSTOMEREMIND);
        isRemindDataBack = false;
        count = 0;
        numberes = 0;
    }

    private void continueSendThis(Handler handler)
    {
        handler.sendEmptyMessageDelayed(GETCUSTOMEREMIND, 60);
        count ++;
    }

    private void continueSendNext(Handler handler)
    {
        handler.sendEmptyMessageDelayed(GETCUSTOMEREMIND, 60);
        isRemindDataBack = false;
        count = 0;
    }

    public static synchronized BleDataForCustomRemind getCustomRemindDataInstance()
    {
        if(bleDataForCustomRemind == null)
        {
            bleDataForCustomRemind = new BleDataForCustomRemind();
        }
        return bleDataForCustomRemind;
    }

    //单利模式
    private BleDataForCustomRemind(){};
    public void deleteCustomRemind(byte number)
    {
        byte[] bytes = new byte[2];
        bytes[0] = (byte)0x02;
        bytes[1] =  number;
        setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
    }
    public void dealTheDeleteCallback(byte[] buffer)
    {
        callback.deleteCallback(buffer[0]);
    }

    public void closeSendData()
    {
        closeSendData(customRemindHandler);
    }

    public void getTheCustomRemind(int num)
    {
        getCustomRemind(num);
        Message msg = getBundle(num);
        customRemindHandler.sendMessageDelayed(msg, 80);
    }

    private Message getBundle(int num)
    {
        Message msg = Message.obtain();
        msg.what = GETCUSTOMEREMIND;
        Bundle bundle = new Bundle();
        bundle.putInt(REMINDNUMBER, num);
        msg.setData(bundle);
        return msg;
    }

    /**
     * 向手环发送指令获取自定义提醒内容
     */
    private void getCustomRemind(int number)
    {
        byte[] bytes = new byte[2];
        bytes[0] = (byte)0x00;
        bytes[1] = (byte)(number & 0xff);
        setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
    }

    CustomRemindEntity entity;
    byte[] hasData = new byte[8];   // 储存提醒编号

    /**
     * 解析设备返回数据
     * @param mContext
     * @param dataValid
     */
    public void dealTheValidData(Context mContext, byte[] dataValid)
    {
//        00 00 04 01 0d34 2e 6c16
//        00 01 02 01 1337 61 a716
        Log.i(TAG, "有效的自定义提醒数据：" + FormatUtils.bytesToHexString(dataValid));
        isRemindDataBack = true;
        if(dataValid.length > 5)    // 长度大于4是有效数据
        {
            if(dataValid[0] == (byte)0x00)  // 第一个字节00代表是读取自定义提醒返回的数据
            {
                boolean isCustom = false;
                entity = new CustomRemindEntity();
                entity.setNumber(dataValid[1]);   // 设置此对象的编号

                byte type = dataValid[2];         // 提醒类型，决定图片的选择

                entity.setType(type);
                if(type == (byte)0x06)
                {
                    isCustom = true;
                }
                int count = dataValid[3] & 0xff;
                entity.setCount(count);   // 提醒时间的个数
                String timeS = "";
                for (int i = 4; i < 4 + count * 2; i += 2)
                {
                    int hour = dataValid[i] & 0xff;
                    int minute = dataValid[i + 1] & 0xff;
                    String a = ((hour < 10) ? ("0" + hour) : String.valueOf(hour)) + ":" + ((minute < 10) ? ("0" + minute) : String.valueOf(minute));
                    timeS = timeS + ((timeS.length() == 0) ? "" : "/") + a;
                }

                entity.setTimes(timeS);
                int ind = 3 + 2 * count + 1;
                byte week = dataValid[ind];
                String weekS = FormatWeeks(mContext, week);          // 构建星期显示格式
                if(weekS != null && weekS.equals(""))
                {
                    weekS = mContext.getString(R.string.only_once);
                }
                entity.setRepeats(weekS);
                entity.setWeekMetadata(week);

                if(isCustom)
                {
                    String cusName = null;
                    int start = ind + 1;
                    int end = dataValid.length - 2;

                    byte[] customByte = new byte[end - start];
//                  314e d638 3153 314e 00
//                    Log.i(TAG, "自定义开始:" + start + "自定义结束:" + end + "数组长度:" + customByte.length);

                    for (int i = start; i < dataValid.length - 2; i += 2)
                    {
                        customByte[i - start] = dataValid[i + 1];
                        customByte[i - start + 1]
                                = dataValid[i];
                    }
                    try {
                        cusName = new String(customByte, "Unicode");
                        cusName = cusName.trim();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
//                    Log.i(TAG, "自定义名称:" + cusName + "自定义byte:" + FormatUtils.bytesToHexString(customByte) + "数组长度" + customByte.length);
                    entity.setCustomName(cusName);
                }
                requesCallback.request(entity);
            }
        }
        else
        {
            requesCallback.request(null);
        }
    }

    private String FormatWeeks(Context mContext, byte week)
    {
        String ret = "";
        if(week == (byte)0x7f)
        {
            ret = mContext.getString(R.string.everyday);
        }
        else
        {
            String[] weekName = new String[]
                    {

//                    <string name="sunday">日</string>
//                    <string name="monday">一</string>
//                    <string name="tuesday">二</string>
//                    <string name="wednesday">三</string>
//                    <string name="thursday">四</string>
//                    <string name="friday">五</string>
//                    <string name="saturday">六</string>
                            mContext.getString(R.string.sunday),
                            mContext.getString(R.string.monday),
                            mContext.getString(R.string.tuesday),
                            mContext.getString(R.string.wednesday),
                            mContext.getString(R.string.thursday),
                            mContext.getString(R.string.friday),
                            mContext.getString(R.string.saturday),
                    };

            for (int i = 0; i < 7; i ++)
            {
                if((week >> i & (byte)0x01) == (byte)0x01)  // week依次右移，并做and运算，若结果为0x01,则代表有对应的天
                {
                    if(!ret.equals(""))
                    {
                        ret += "/";
                    }
                    ret += weekName[i];
                    Log.i(TAG, "重复周:" + weekName[i]);
                }

            }
        }


        return ret;
    }

    public void setOnDeleteListener(DeleteCallback callback)
    {
        this.callback = callback;
    }

    public interface DeleteCallback
    {
        void deleteCallback(byte a);
    }

    public interface RequesCallback
    {
        void request(CustomRemindEntity entity);
        void requestOver();
    }
    public void setOnRequesCallback(RequesCallback requesCallback)
    {
        this.requesCallback = requesCallback;
    }

}
