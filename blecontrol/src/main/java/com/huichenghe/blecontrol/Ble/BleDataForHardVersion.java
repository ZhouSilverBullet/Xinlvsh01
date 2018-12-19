package com.huichenghe.blecontrol.Ble;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.huichenghe.xinlvshuju.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

/**
 * 获取手环版本号
 * Created by lixiaoning on 15-11-10.
 */
public class BleDataForHardVersion extends BleBaseDataManage
{
    public final static byte send_cmd = (byte)0x07;
    public final static byte fromDevice = (byte)0x87;

    private static String versionString = null;     // 储存版本字符串的变量
    private final int GET_HARD_VERSION = 0;
    private boolean isBack = false;
    private boolean hasComm = false;
    private int count = 0;
    private static BleDataForHardVersion bleDataInstance;
    private DataSendCallback dataSendCallback;


    private Handler verHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case GET_HARD_VERSION:
                    if(isBack)
                    {
                        closeSendData(this);
                    }
                    else
                    {
                        if(count < 4)
                        {
                            coutinueSendData(this);
                            getHardVersion();
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

    private void coutinueSendData(Handler handler)
    {
        handler.sendEmptyMessageDelayed(GET_HARD_VERSION, 60);
        count ++;
    }

    private void closeSendData(Handler handler)
    {
        handler.removeMessages(GET_HARD_VERSION);
        isBack = false;
        hasComm = false;
        count = 0;
        dataSendCallback.sendFinish();
    }

    private BleDataForHardVersion(){ }
    public synchronized static BleDataForHardVersion getInstance()
    {
        if(bleDataInstance == null)
        {
            bleDataInstance = new BleDataForHardVersion();
        }
        return bleDataInstance;
    }

    public void setDataSendCallback(DataSendCallback dataSendCallback)
    {
        this.dataSendCallback = dataSendCallback;
    }


    public void requestHardVersion()
    {
        hasComm = true;
        getHardVersion();
        verHandler.sendEmptyMessageDelayed(GET_HARD_VERSION, 100);
    }

    public void getHardVersion()
    {
        setMessageDataByString(send_cmd, null, true);
    }


    /**
     * 处理手环接收到的数据域数据
     * @param mContext  上下文
     * @param bufferTmp byte数组
     * @param dataLen   数据长度
     */
    public void dealReceData(Context mContext, byte[] bufferTmp, int dataLen)
    {
        if(hasComm)
        {
            isBack = true;
            hasComm = false;
        }
        dataSendCallback.sendSuccess(FormatUtils.bytesToHexString(bufferTmp));
        byte hard = bufferTmp[0];
        byte bluetooth = bufferTmp[1];
        byte soft = bufferTmp[2];

        versionString = hard + "/"
                      + parseTheHexString(bluetooth) + "/"
                      + parseTheHexString(soft);
        /**
         * 写入sp
         */
        LocalDataSaveTool.getInstance(mContext).writeSp(MyConfingInfo.HARD_VERSION, versionString);
    }
    public static String getVersionString()
    {
        return versionString;
    }
    /**
     * 按协议解析版本号
     * @param hard
     */
    private static String parseTheHexString(byte hard)
    {
        byte a = (byte)(hard >> 4);
        byte b = (byte)(hard & 0x0f);
        String a1 = Integer.toHexString(a);
        String b1 = Integer.toHexString(b);

        return a1 + "" + b1;
    }
}
