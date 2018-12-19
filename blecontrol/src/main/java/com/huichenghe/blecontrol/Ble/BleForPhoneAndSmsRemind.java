package com.huichenghe.blecontrol.Ble;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huichenghe.xinlvshuju.Utils.FormatUtils;

import java.io.UnsupportedEncodingException;

/**
 * 打开电话和短信提醒的管理类
 * Created by lixiaoning on 15-12-phone.
 */
public class BleForPhoneAndSmsRemind extends BleBaseDataManage
{
    private static String TAG = BleForPhoneAndSmsRemind.class.getSimpleName();
    public static final byte toDevice = (byte)0x05;
    public static final byte formDevice = (byte)0x85;

    public static final byte startPhoneRemindToDevice = (byte)0x01;
    public static final byte phoneRemindFromDevice = (byte)0x81;
    public static final byte startSmsRemindToDevice = (byte)0x0B;
    public static final byte smsRemindFromDevice = (byte)0x8B;
    private final int SMSOPENMSG = 0;
    private final int SMSREMINDMSG = 1;
    private final int PHONE_OPEN_MSG = 2;
    private final int PHEONREMINDMSG = 3;
    private final int PHONE_READ_STATE = 4;
    private boolean isReadStatus = false;
    private int readCount = 0;
    private final String CONTACTNAME = "contact name";
    private final String CONTENE = "content";
    private final String PHONECONTACTNAME = "phone contact name";
    private final String PHONENUMBER = "phone number";
    private final String PHONEARGS = "phone args";
    private static BleForPhoneAndSmsRemind bleForPhoneAndSmsRemind;
    public onPhoneHasRecever mOnPhoneHasRecever;
    private byte[] datas = new byte[0];
    private boolean isOpenOk = false;
    private boolean isAlreadyBack = false;
    private int count = 0;
    private Handler phoneHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case SMSOPENMSG:
                    if(isOpenOk)
                    {
                        closeTheHandler(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            continueSendHandler(this);
                            openThePhoneAndSmsRemind((byte)msg.arg1, (byte)msg.arg2);
                        }
                        else
                        {
                            closeTheHandler(this);
                        }
                    }
                break;
                case SMSREMINDMSG:
                    if(isAlreadyBack)
                    {
                        closeTheSmsRemindHandler(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            Bundle bun = msg.getData();
                            String contactName = bun.getString(CONTACTNAME);
                            byte content = bun.getByte(CONTENE);
                            startSmsRemind(contactName, content);
                            continueSendSmsHandler(this, contactName, content);
                        }
                        else
                        {
                            closeTheSmsRemindHandler(this);
                        }
                    }
                    break;
                case PHONE_OPEN_MSG:
                    Log.i(TAG, "isOpen:" + isOpenOk);
                    if(isOpenOk)
                    {
                        closeThePhoenHandler(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            continueSendPhoneHandler(this, msg.arg1, msg.arg2);
                            openThePhoneAndSmsRemind((byte)msg.arg1, (byte)msg.arg2);
                        }
                        else
                        {
                            closeThePhoenHandler(this);
                        }
                    }
                    break;
                case PHEONREMINDMSG:
                    if(isAlreadyBack)
                    {
                        closeThePhoneRemindHandler(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            Bundle bundle = msg.getData();
                            String num = bundle.getString(PHONENUMBER);
                            String conName = bundle.getString(PHONECONTACTNAME);
                            byte ba = bundle.getByte(PHONEARGS);
                            startPhoneRemind(num, conName, ba);
                            continueSendPhoneRemindHandler(this, num, conName, ba);
                        }
                        else
                        {
                            closeThePhoneRemindHandler(this);
                        }
                    }
                    break;
                case PHONE_READ_STATE:
                    if(isReadStatus)
                    {
                        closeReadPhoneStatus(this);
                    }
                    else
                    {
                        if(readCount < 4)
                        {
                            readPhoneRemind();
                            continueReadPhoneStatus(this);
                        }
                        else
                        {
                            closeReadPhoneStatus(this);
                        }
                    }
                    break;
            }
        }
    };

    private void continueReadPhoneStatus(Handler handler)
    {
        handler.sendEmptyMessageDelayed(PHONE_READ_STATE, 80);
        readCount ++;
    }

    private void closeReadPhoneStatus(Handler handler)
    {
        handler.removeMessages(PHONE_READ_STATE);
        isReadStatus = false;
        readCount = 0;
    }

    private void continueSendHandler(Handler handler)
    {
        handler.sendEmptyMessageDelayed(SMSOPENMSG, 60);
        count ++;
    }

    private void continueSendSmsHandler(Handler handler, String contactName, byte content)
    {
        Message msg = Message.obtain();
        msg.what = SMSREMINDMSG;
        Bundle bundle = new Bundle();
        bundle.putString(CONTACTNAME, contactName);
        bundle.putByte(CONTENE, content);
        msg.setData(bundle);
        handler.sendMessageDelayed(msg, 60);
        count ++;
    }
    private void continueSendPhoneHandler(Handler handler, int arg1, int arg2)
    {
        Message msg = handler.obtainMessage();
        msg.what = PHONE_OPEN_MSG;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        handler.sendMessageDelayed(msg, 60);
        count ++;
    }
    private void continueSendPhoneRemindHandler(Handler handler, String num, String conName, byte ba)
    {
        Message msg = Message.obtain();
        msg.what = PHEONREMINDMSG;
        Bundle bundle = new Bundle();
        bundle.putString(PHONENUMBER, num);
        bundle.putString(PHONECONTACTNAME, conName);
        bundle.putByte(PHONEARGS, ba);
        msg.setData(bundle);
        handler.sendMessageDelayed(msg, 60);
        count ++;
    }

    private void closeTheHandler(Handler handler)
    {
        handler.removeMessages(SMSOPENMSG);
        isOpenOk = false;
        count = 0;
    }
    private void closeTheSmsRemindHandler(Handler handler)
    {
        handler.removeMessages(SMSREMINDMSG);
        isAlreadyBack = false;
        count = 0;
    }

    private void closeThePhoenHandler(Handler handler)
    {
        handler.removeMessages(PHONE_OPEN_MSG);
        isOpenOk = false;
        count = 0;
    }


    private void closeThePhoneRemindHandler(Handler handler)
    {
        handler.removeMessages(PHEONREMINDMSG);
        isAlreadyBack = false;
        count = 0;
    }

    public void addCallback(onPhoneHasRecever phoneHasRecever)
    {
        this.mOnPhoneHasRecever = phoneHasRecever;
    }

    public static BleForPhoneAndSmsRemind getInstance()
    {
        if(bleForPhoneAndSmsRemind == null)
        {
            bleForPhoneAndSmsRemind = new BleForPhoneAndSmsRemind();
        }
        return bleForPhoneAndSmsRemind;
    }

    private BleForPhoneAndSmsRemind(){}


    public void openSmsRemind(byte ba, byte sw)
    {
        openThePhoneAndSmsRemind(ba, sw);
        Message msg = Message.obtain();
        msg.what = SMSOPENMSG;
        msg.arg1 = ba;
        phoneHandler.sendMessageDelayed(msg, 80);
    }


    public void openPhoneRemine(byte ba, byte sw)
    {
        openThePhoneAndSmsRemind(ba, sw);
        Message msg = phoneHandler.obtainMessage();
        msg.what = PHONE_OPEN_MSG;
        msg.arg1 = ba;
        msg.arg2 = sw;
        phoneHandler.sendMessageDelayed(msg, 80);
    }

    /**
     * 手机请求打开手环来电提醒功能
     */
    private void openThePhoneAndSmsRemind(byte ba, byte sw)
    {
        byte[] data = new byte[3];
        data[0] = (byte)0x00;
        data[1] = ba;
        data[2] = sw;
        setMsgToByteDataAndSendToDevice(toDevice, data, data.length);
    }


    public void startSMSRemine(String contactName, byte content)
    {
        startSmsRemind(contactName, content);
        Message msg = Message.obtain();
        msg.what = SMSREMINDMSG;
        Bundle bun = new Bundle();
        bun.putString(CONTACTNAME, contactName);
        bun.putByte(CONTENE, content);
        msg.setData(bun);
        phoneHandler.sendMessageDelayed(msg, 80);
    }

    private void startSmsRemind(String contactName, byte content)
    {
        Log.i(TAG, "短信名字：" + contactName);
        if(contactName != null && contactName.equals(""))
            return;
        byte[] data;
        compareTheLength(contactName);
        if(datas == null)return;
        if(datas.length == 36)
        {
            data = new byte[datas.length + 1];
        }
        else
        {
            data = new byte[datas.length + 2];
        }
        System.arraycopy(datas, 0, data, 1, datas.length);
        Log.i(TAG, "短信名字转byte：" + FormatUtils.bytesToHexString(data));
        setMsgToByteDataAndSendToDevice(startSmsRemindToDevice, data, data.length);
    }

    private void compareTheLength(String contactName)
    {
        try {

            if(contactName.getBytes("utf-8").length > 36)
            {
                contactName = contactName.substring(0, contactName.length() - 1);
                compareTheLength(contactName);
            }
            else
            {
                datas = contactName.getBytes("utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public void closeTheRemind()
    {
        setMsgToByteDataAndSendToDevice(startPhoneRemindToDevice, new byte[]{0x01}, 1);
    }


    public void readPhoneRemindStatus()
    {
        phoneHandler.sendEmptyMessageDelayed(PHONE_READ_STATE, 80);
        readPhoneRemind();
    }

    private void readPhoneRemind()
    {
        byte[] readData  = new byte[2];
        readData[0] = 0x01;
        readData[1] = 0x03;
        setMsgToByteDataAndSendToDevice(toDevice, readData, readData.length);
    }

    /**
     * 发送提醒数据给手环
     * @param contactName
     */
    public void startPhoneAndSmsRemind(String smsBody, String contactName, byte ba)
    {
//        if(phoneNumber.startsWith("+86"))
//        {   // 去掉 +86
//            phoneNumber = phoneNumber.substring(info);
//        }
//        // 字符串转化为字符数组
//        char[] chars = phoneNumber.toCharArray();
//        byte[] pnum = new byte[chars.length];
//        for (int i = 0; i < chars.length; i++)
//        {
//            pnum[i] = (byte)(chars[i] & 0xff);
//        }

        if(contactName != null && !contactName.equals(""))
        {   // 如果有联系人名字
            if(contactName.getBytes().length < 16)
            {
                byte[] bytesName = null;
                byte[] body = null;
                try {
                    bytesName = contactName.getBytes("UTF-8");  // 以utf-8编码转化为byte数组
                    if(smsBody.length() > 48)
                    {
                        smsBody.substring(0, 47);
                    }
                    body = smsBody.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                byte[] allData = new byte[16 + body.length];
                for (int i = 0; i < allData.length; i ++)
                {
                    if(i >= 0 && i < bytesName.length)
                    {
                        allData[i + 1] = bytesName[i];
                    }
                    if(i > 15)
                    {
                        allData[i] = body[i - 16];
                    }
                }
//                for (int i = 16; i < allData.length; i ++)
//                {
//                    allData[i] = bytes[i - 16];
//                }
//                pnum = allData;

                setMsgToByteDataAndSendToDevice(startSmsRemindToDevice, allData, allData.length);

            }
            else
            {
            }


        }
//        else
//        {
//            // 只有电话号码
//            byte[] phoneData = new byte[16];
//            for (int i = 0; i < phoneData.length && i < pnum.length; i ++)
//            {
//                phoneData[i + clock] = pnum[i];
//            }
//                pnum = phoneData;
//        }




    }


    public void beginPhoneRemind(String phoneNumber, String contactName, byte ba)
    {
        startPhoneRemind(phoneNumber, contactName, ba);
        Message msg = Message.obtain();
        msg.what = PHEONREMINDMSG;
        Bundle bundle = new Bundle();
        bundle.putString(PHONENUMBER, phoneNumber);
        bundle.putString(PHONECONTACTNAME, contactName);
        bundle.putByte(PHONEARGS, ba);
        msg.setData(bundle);
        phoneHandler.sendMessageDelayed(msg, 80);
    }


    /**
     * 发送提醒数据给手环
     * @param contactName
     */
    private void startPhoneRemind(String phoneNumber, String contactName, byte ba)
    {
        if(phoneNumber.startsWith("+86"))
        {   // 去掉 +86
            phoneNumber = phoneNumber.substring(3);
        }
        // 字符串转化为字符数组
        char[] chars = phoneNumber.toCharArray();
        byte[] pnum = new byte[chars.length];
        for (int i = 0; i < chars.length; i++)
        {
            pnum[i] = (byte)(chars[i] & 0xff);
        }

        if(contactName != null && !contactName.equals(""))
        {   // 如果有联系人名字
            if(contactName.getBytes().length < 32)
            {
                byte[] bytesName = null;
                try {
                    bytesName = contactName.getBytes("UTF-8");  // 以utf-8编码转化为byte数组
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                byte[] allData = new byte[16 + bytesName.length];
                for (int i = 0; i < allData.length; i ++)
                {
                    if(i >= 0 && i < pnum.length)
                    {
                        allData[i + 1] = pnum[i];
                    }
                    if(i > 15)
                    {
                        allData[i] = bytesName[i - 16];
                    }
                }
//                for (int i = 16; i < allData.length; i ++)
//                {
//                    allData[i] = bytes[i - 16];
//                }
//                pnum = allData;

                setMsgToByteDataAndSendToDevice(startPhoneRemindToDevice, allData, allData.length);

            }
            else
            {
                return;
            }
        }
        else
        {
            // 只有电话号码
            byte[] phoneData = new byte[16];
            for (int i = 0; i < phoneData.length && i < pnum.length; i ++)
            {
                phoneData[i + 1] = pnum[i];
            }
                pnum = phoneData;
            setMsgToByteDataAndSendToDevice(startPhoneRemindToDevice, pnum, pnum.length);
        }




    }

    public void dealTheResponse(boolean b)
    {
        isAlreadyBack = true;
        if(mOnPhoneHasRecever != null)
        mOnPhoneHasRecever.onReceverLisener();
    }

    public void dealOpenResponse(byte[] bufferTmp)
    {
//        68850300 01 0301 f516
//        68 85 0200 00 03 f216
//        68 85 0200 0002 f116
        if(bufferTmp[0] == (byte)0x00)
        {
            Log.i(TAG, "isOpen:" + bufferTmp);
            isOpenOk = true;
        }
        else if(bufferTmp[0] == (byte)0x01)
        {
            isReadStatus = true;
            readListener.onReadData(bufferTmp);
        }
    }

    public void dealSmsDataBack(byte[] bufferTmp)
    {
        if(bufferTmp[0] == (byte)0x00)
        {
            isAlreadyBack = true;
        }
    }


    public interface onPhoneHasRecever
    {
        void onReceverLisener();
    }

    private OnReadDataListener readListener;
    public void setOnDataListener(OnReadDataListener onReadDataListener)
    {
        this.readListener = onReadDataListener;
    }
    public interface OnReadDataListener
    {
        void onReadData(byte[] data);
    }

}
