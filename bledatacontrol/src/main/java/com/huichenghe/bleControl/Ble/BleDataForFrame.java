package com.huichenghe.bleControl.Ble;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huichenghe.bleControl.Utils.FormatUtils;

/**
 * Created by lixiaoning on 15-11-sosPhone.
 */
public class BleDataForFrame extends BleBaseDataManage {
    private static int mCurrentBattery = -1;
    private DataSendCallback battListerer;
    private DataSendCallback battListerer3;
    private static BleDataForFrame bleBAttery;
    private boolean hasComm = false;
    private final int GET_BLE_FRAME = 0;
    private final int GET_BLE_PARAMS = 1;
    private final int GET_BLE_WEATHER = 2;
    // 0x32对应的两个表示01,02返回的结果
    private boolean isSendOk = false;
    private boolean isSendOk1 = false;
    private int sendCount = 0;

    public static byte toDevice = (byte) 0x32;
    public static byte fromDevice = (byte) 0xb2;
    public static byte toDevice3 = (byte) 0x33;
    public static byte fromDevice3 = (byte) 0xb3;

    public static BleDataForFrame getInstance() {
        if (bleBAttery == null) {
            synchronized (BleDataForFrame.class) {
                if (bleBAttery == null) {
                    bleBAttery = new BleDataForFrame();
                }
            }
        }
        return bleBAttery;
    }

    private Handler batteryHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_BLE_FRAME:
                    if (isSendOk) {
                        stopSendData(this, GET_BLE_FRAME);
                    } else {
                        if (sendCount < 4) {
                            continueSend(this, msg);
                            getCheckFrameFromBr();
                        } else {
                            stopSendData(this, GET_BLE_FRAME);
                        }
                    }
                    break;
                case GET_BLE_PARAMS:
                    if (isSendOk1) {
                        stopSendData(this, GET_BLE_PARAMS);
                    } else {
                        if (sendCount < 4) {
                            continueSend(this, msg);
                            getSupportParamFromBr();
                        } else {
                            stopSendData(this, GET_BLE_PARAMS);
                        }
                    }
                    break;

            }

        }
    };

    private void continueSend(Handler handler, Message msges) {
        Message msg = handler.obtainMessage();
        msg.what = msges.what;
        msg.arg1 = msges.arg1;
        msg.arg2 = msges.arg2;
        handler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(msges.arg1, msges.arg2));
        sendCount++;
    }

    private void stopSendData(Handler handler, int comm) {
        handler.removeMessages(comm);
        if (!isSendOk||!isSendOk1) {
            battListerer.sendFailed();
        }
        battListerer.sendFinished();
        isSendOk = false;
        isSendOk1 = false;
        sendCount = 0;
    }


    private BleDataForFrame() {
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        setMessageDataByString(mReceCmd, null, true);
    }

    /**
     * 是否支持某功能
     */
    public void getCheckFrame() {
        int sendLength = getCheckFrameFromBr();
        Message msg = batteryHandler.obtainMessage();
        msg.what = GET_BLE_FRAME;
        msg.arg1 = sendLength;
        msg.arg2 = 7;
        batteryHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(sendLength, 20));
    }

    public void getSupportParam() {
        int sendLength = getSupportParamFromBr();
        Message msg = batteryHandler.obtainMessage();
        msg.what = GET_BLE_PARAMS;
        msg.arg1 = sendLength;
        msg.arg2 = 20;
        batteryHandler.sendMessageDelayed(msg, SendLengthHelper.getSendLengthDelay(sendLength, 20));
    }

    /**
     * 查询设备是否支持某功能（心率连续监测，闹铃，天气）
     *
     * @return
     */
    private int getCheckFrameFromBr() {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) 0x01;
        bytes[1] = (byte) 0x03;
        bytes[2] = (byte) 0x04;
        bytes[3] = (byte) 0x05;
        return setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
    }

    /**
     * app 查询设备能支持的参数
     *
     * @return
     */
    private int getSupportParamFromBr() {
        byte[] bytes = new byte[3];
        bytes[0] = (byte) 0x02;
        bytes[1] = (byte) 0x01;
        bytes[2] = (byte) 0x02;
        return setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
    }

//    04-18 09:46:42.825: I/System.out(27318): 010301040105003016
//    04-18 09:46:42.843: I/System.out(27318): 02018c02641416
//    04-18 09:46:43.012: I/System.out(27318): 02018c02641416

    public void dealReceData(byte[] data) {
//        mCurrentBattery = (data[0] & 0xFF);
//        Log.i("", "dealReceData :" + mCurrentBattery);
        System.out.println( "333333333333"+ FormatUtils.bytesToHexString(data));
        if (data[0] == 1)
            isSendOk = true;
        if (data[0] == 2)
            isSendOk1 = true;
        battListerer.sendSuccess(data);
    }


    public void dealReceB3(byte[] data) {
//        01 30 02 64 b616
        byte[] copyData = new byte[data.length - 2];
        byte[] reqData = new byte[(data.length - 2)];
        System.arraycopy(data, 0, copyData, 0, data.length - 2);
        for (int i = 0; i < copyData.length; i += 2) {
            reqData[i] = copyData[i];
        }
        Log.i("", "dealReceData :" + FormatUtils.bytesToHexString(reqData));
        setMsgToByteDataAndSendToDevice(toDevice3, reqData, reqData.length);
        if (battListerer != null) {
            battListerer3.sendSuccess(data);
        }
    }

    public static int getmCurrentBattery() {
        return mCurrentBattery;
    }

    public void setCheckFrameListener(DataSendCallback batteryCallback) {
        this.battListerer = batteryCallback;
    }

    public void setCheckFrameListener3(DataSendCallback batteryCallback) {
        this.battListerer3 = batteryCallback;
    }

}
