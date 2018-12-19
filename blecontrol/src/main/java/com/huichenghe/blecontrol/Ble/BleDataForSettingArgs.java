package com.huichenghe.blecontrol.Ble;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;

import com.huichenghe.xinlvshuju.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

/**
 * Created by lixiaoning on 2016/5/17.
 */
public class BleDataForSettingArgs extends BleBaseDataManage
{
    private final byte toDevice = (byte)0x02;
    public static final byte fromDevice = (byte)0x82;
    private final int SETTINGPARAMTER = 0;
    private final int SETHEARTMONITOR = 1;
    private final int SETFATIGUE = 2;
    private final int READHEARTANDFATIGUE = 3;
    private boolean isAlreadyBack = false;
    private boolean hasComm = false;
    private int count = 0;
    private Context context;
    private DataSendCallback dataSendCallback;
    private BleDataForSettingArgs(Context context)
    {
        this.context = context;
    };
    private static BleDataForSettingArgs bleDataForSettingArgs;
    public static synchronized BleDataForSettingArgs getInstance(Context context)
    {
        if(bleDataForSettingArgs == null)
        {
            bleDataForSettingArgs = new BleDataForSettingArgs(context);
        }
        return bleDataForSettingArgs;
    }

    public void setDataSendCallback(DataSendCallback dataSendCallback)
    {
        this.dataSendCallback = dataSendCallback;
    }

    private Handler settingHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case SETTINGPARAMTER:
                    if(isAlreadyBack)
                    {
                        closeSend(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            couninueSend(this, msg.arg1, msg.arg2);
                            settingParamter();
                        }
                        else
                        {
                            closeSend(this);
                        }
                    }
                    break;
                case SETHEARTMONITOR:
                    if(isAlreadyBack)
                    {
                        closeSendHeartMonitor(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            continueSendHeartMonitor(this, (byte)msg.what);
                            setHeartReatMonnitor((byte)msg.what);
                        }
                        else
                        {
                            closeSendHeartMonitor(this);
                        }
                    }
                    break;
                case SETFATIGUE:
                    if(isAlreadyBack)
                    {
                        closeFatigue(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            continueSendFatigue(this, (byte)msg.arg1);
                            setFatigue((byte)msg.arg1);
                        }
                        else
                        {
                            closeFatigue(this);
                        }
                    }
                    break;
                case READHEARTANDFATIGUE:
                    if(isAlreadyBack)
                    {
                        closeRead(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            conitnueRead(this);
                            readFromDeviceHeartAndFatigue();
                        }
                        else
                        {
                            closeRead(this);
                        }
                    }
                    break;
            }
        }
    };

    private void conitnueRead(Handler handler)
    {
        handler.sendEmptyMessageDelayed(READHEARTANDFATIGUE, 60);
        count ++;
    }

    private void closeRead(Handler handler)
    {
        handler.removeMessages(READHEARTANDFATIGUE);
        isAlreadyBack = false;
        count = 0;
    }

    private void continueSendFatigue(Handler handler, byte arg1)
    {
        Message msg = Message.obtain();
        msg.what = SETFATIGUE;
        msg.arg1 = arg1;
        handler.sendMessageDelayed(msg, 60);
        count ++;
    }

    private void closeFatigue(Handler handler)
    {
        handler.removeMessages(SETFATIGUE);
        isAlreadyBack = false;
        count = 0;
    }

    private void continueSendHeartMonitor(Handler handler, byte what)
    {
        Message msg = new Message();
        msg.what = SETHEARTMONITOR;
        msg.arg1 = what;
        handler.sendMessageDelayed(msg, 60);
        count ++;
    }

    private void closeSendHeartMonitor(Handler handler)
    {
        handler.removeMessages(SETHEARTMONITOR);
        isAlreadyBack = false;
        count = 0;
    }

    private void couninueSend(Handler handler, int sendLeng, int receleng)
    {
        Message msg = handler.obtainMessage();
        msg.what = SETTINGPARAMTER;
        msg.arg1 = sendLeng;
        msg.arg2 = receleng;
        handler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(sendLeng, receleng));
        count ++;
    }

    private void closeSend(Handler handler)
    {
        handler.removeMessages(SETTINGPARAMTER);
        isAlreadyBack = false;
        hasComm = false;
        count = 0;
        if(dataSendCallback != null)
        {
            dataSendCallback.sendFinish();
        }
    }

    public void setArgs()
    {
        hasComm = true;
        int lenth = settingParamter();
        Message msg = settingHandler.obtainMessage();
        msg.what = SETTINGPARAMTER;
        msg.arg1 = lenth;
        msg.arg2 = 9;
        settingHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(lenth, 9));
    }

    /**
     * 设置12/24小时制和公英制
     */
    private int settingParamter()
    {
        byte[] bytes = new byte[5];
        boolean is24 = DateFormat.is24HourFormat(context);
        String unit = LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.METRICORINCH);
        bytes[0] = (byte)0x01;
        bytes[1] = (byte)0x00;
        if(is24)
        {
            bytes[2] = (byte)0x01;
        }
        else
        {
            bytes[2] = (byte)0x00;
        }
        bytes[3] = (byte)0x01;
        if(unit != null && unit.equals(MyConfingInfo.INCH))
        {
            bytes[4] = (byte)0x01;
        }
        else
        {
            bytes[4] = (byte)0x00;
        }
        return setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
    }

    public void setHeartReatArgs(byte time)
    {
        int sendLen = setHeartReatMonnitor(time);
        Message msg = new Message();
        msg.what = SETHEARTMONITOR;
        msg.arg1 = time;
        settingHandler.sendMessageDelayed(msg, 80);
    }


    /**
     * 设置心率监测间隔
     * @param time
     */
    private int setHeartReatMonnitor(byte time)
    {
        byte[] heData = new byte[3];
        heData[0] = (byte)0x01;
        heData[1] = (byte)0x02;
        heData[2] = time;
        return setMsgToByteDataAndSendToDevice(toDevice, heData, heData.length);
    }


    public void setFatigueSwich(byte open)
    {
        setFatigue(open);
        Message msg = new Message();
        msg.what = SETFATIGUE;
        msg.arg1 = open;
        settingHandler.sendMessageDelayed(msg, 80);
    }


    /**
     * 设置疲劳度开关
     */
    private void setFatigue(byte open)
    {
        byte[] heData = new byte[3];
        heData[0] = (byte)0x01;
        heData[1] = (byte)0x04;
        heData[2] = open;
        setMsgToByteDataAndSendToDevice(toDevice, heData, heData.length);
    }




    public void dealTheBack(byte[] bufferTmp)
    {
//        010001ef16
        Log.i("", "设置参数返回的：" + FormatUtils.bytesToHexString(bufferTmp));
        if(bufferTmp[0] == (byte)0x01)
        {
            if(bufferTmp[1] == (byte)0x02)
            {
                isAlreadyBack = true;
            }
            else if(bufferTmp[1] == (byte)0x00 || bufferTmp[1] == (byte)0x01)
            {
                if(hasComm)
                {
                    isAlreadyBack = true;
                    hasComm = false;
                }
            }
            else if(bufferTmp[1] == (byte)0x04)
            {
                isAlreadyBack = true;
            }
        }
        else if(bufferTmp[0] == (byte)0x00)
        {
//            0002050301fa16
            isAlreadyBack = true;
            listener.onBack(bufferTmp);
        }
    }

    public void readHeartAndFatigue()
    {
        readFromDeviceHeartAndFatigue();
        settingHandler.sendEmptyMessageDelayed(READHEARTANDFATIGUE, 80);
    }


    /**
     * 读取心率监测频率和疲劳值
     */
    private void readFromDeviceHeartAndFatigue()
    {
        byte[] readData = new byte[3];
        readData[0] = (byte)0x00;
        readData[1] = (byte)0x02;
        readData[2] = (byte)0x04;
        setMsgToByteDataAndSendToDevice(toDevice, readData, readData.length);
    }

    private OnArgsBackListener listener;
    public void setOnArgsBackListener(OnArgsBackListener list)
    {
        this.listener = list;
    }
    public interface OnArgsBackListener
    {
        void onBack(byte[] butterData);
    }
}
