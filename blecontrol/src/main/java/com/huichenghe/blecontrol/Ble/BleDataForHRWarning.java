package com.huichenghe.blecontrol.Ble;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huichenghe.xinlvshuju.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

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
    private final int GETWARNINGDATA = 0;
    private final int SETWARNINGDATA = 1;
    private final int CLOSEWARNING = 2;
    private int count = 0;
    private int maxHR, minHR;
    private OnWarningDataback databack;
    private DataSendCallback dataSendCallback;




    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case GETWARNINGDATA:
                    Log.i(TAG, "是否返回：" + isBack + "count:" + count);
                    if(isBack)
                    {
                        closeSendData(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            this.sendEmptyMessageDelayed(GETWARNINGDATA, 60);
                            requestTheHRWarningData();
                            count ++;
                        }
                        else
                        {
                            closeSendData(this);
                        }
                    }
                    break;
                case SETWARNINGDATA:
                    if(isBack)
                    {
                        this.removeMessages(SETWARNINGDATA);
                        isBack = false;
                        count = 0;
                    }
                    else
                    {
                        if(count < 4)
                        {
                            this.sendEmptyMessageDelayed(SETWARNINGDATA, 60);
                            setAndOpenTheHRWarningData(maxHR, minHR);
                            count ++;
                        }
                        else
                        {
                            this.removeMessages(SETWARNINGDATA);
                            isBack = false;
                            count = 0;
                        }
                    }
                    break;
                case CLOSEWARNING:
                    if(isBack)
                    {
                        this.removeMessages(CLOSEWARNING);
                        isBack = false;
                        count = 0;
                    }
                    else
                    {
                        if(count < 4)
                        {
                            this.sendEmptyMessageDelayed(CLOSEWARNING, 60);
                            closeTheHRWarning(maxHR, minHR);
                            count ++;
                        }
                        else
                        {
                            this.removeMessages(CLOSEWARNING);
                            isBack = false;
                            count = 0;
                        }
                    }
                    break;
            }
        }
    };

    private void closeSendData(Handler handler)
    {
        handler.removeMessages(GETWARNINGDATA);
        isBack = false;
        hasComm = false;
        count = 0;
        if(dataSendCallback != null)
        {
            dataSendCallback.sendFinish();
        }
    }

    public static synchronized BleDataForHRWarning getInstance()
    {
        if(bleDataForHRWarning == null)
        {
            bleDataForHRWarning = new BleDataForHRWarning();
        }
        return bleDataForHRWarning;
    }

    private BleDataForHRWarning(){};

    public void setDataSendCallback (DataSendCallback dataSendCallback)
    {
        this.dataSendCallback = dataSendCallback;
    }

    // 发送设置并打开心率预警的数据，并handler
    public void sendToGetData(int maxHR, int minHR)
    {
        this.maxHR = maxHR;
        this.minHR = minHR;
        setAndOpenTheHRWarningData(maxHR, minHR);
        mHandler.sendEmptyMessageDelayed(SETWARNINGDATA, 80);
    }
    public void requestWarningData()
    {
        hasComm = true;
        requestTheHRWarningData();
        mHandler.sendEmptyMessageDelayed(GETWARNINGDATA, 80);
    }
    private void requestTheHRWarningData()
    {
        byte type = (byte)0x02;
        byte[] array = new byte[1];
        array[0] = type;
        setMsgToByteDataAndSendToDevice(toDevice, array, array.length);
    }
    private void setAndOpenTheHRWarningData(int maxHR, int minHR)
    {
        byte[] byteArray = new byte[4];
        byteArray[0] = (byte)0x01;
        byteArray[1] = (byte)0x00;
        byteArray[2] = (byte)(maxHR & 0xff);
        byteArray[3] = (byte)(minHR & 0xff);
        setMsgToByteDataAndSendToDevice(toDevice, byteArray, byteArray.length);
    }


    public void closeWarning(int maxHR, int minHR)
    {
        this.maxHR = maxHR;
        this.minHR = minHR;
        closeTheHRWarning(maxHR, minHR);
        mHandler.sendEmptyMessageDelayed(CLOSEWARNING, 80);
    }

    private void closeTheHRWarning(int maxHR, int minHR)
    {
        byte[] byteArray = new byte[4];
        byteArray[0] = (byte)0x01;
        byteArray[1] = (byte)0x01;
        byteArray[2] = (byte)(maxHR & 0xff);
        byteArray[3] = (byte)(minHR & 0xff);
        setMsgToByteDataAndSendToDevice(toDevice, byteArray, byteArray.length);
    }


    public void dealTheHRData(byte[] bufferTmp, Context mcContext)
    {
        Log.i(TAG, "预警-----:" + FormatUtils.bytesToHexString(bufferTmp));
//        0200dc280216  ------01fa16
        if (bufferTmp[0] == (byte)0x02)
        {
            int state = bufferTmp[1] & 0xff;
            int maxHR = bufferTmp[2] & 0xff;
            int minHR = bufferTmp[3] & 0xff;
            JSONObject json = new JSONObject();
            try {
                json.put(MyConfingInfo.HRWARNINGOPENORNO,state);
                json.put(MyConfingInfo.MAXHR, maxHR);
                json.put(MyConfingInfo.MINHR, minHR);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            LocalDataSaveTool.getInstance(mcContext).writeSp(MyConfingInfo.HRWARNING_SETTING_VALUE, json.toString());
//            Intent intent = new Intent();
//            intent.setAction(MyConfingInfo.ACTION_FOR_HR_WARNING);
//            mcContext.sendBroadcast(intent);
            if(databack != null)
            {
                databack.onBack(maxHR, minHR, state);
            }
            if(hasComm)
            {
                isBack = true;
                hasComm = false;
            }
        }
        else if(bufferTmp[0] == (byte)0x01)
        {
            Log.i(TAG, "预警请求");
            isBack = true;
            requestTheHRWarningData();
        }
    }

    public interface OnWarningDataback
    {
        void onBack(int max, int min, int state);
    }

    public void setOnWarningDataback(OnWarningDataback onWarningDataback)
    {
        this.databack = onWarningDataback;
    }


}
