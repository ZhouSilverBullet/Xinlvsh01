package com.huichenghe.bleControl.Ble;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.huichenghe.bleControl.Utils.FormatUtils;

public class BleNotifyParse {
    private static final String TAG = "BleNotifyParse";
    private final String mNotifyLock = "mNotifyLock";
    public static BleNotifyParse mBleNotifyParse;
    private static Context mContext;
    private static String MESSAGE_FOR_HANDLER = "byte array for handler";

    private static Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray(MESSAGE_FOR_HANDLER);

            switch (msg.what) {
                case 0:
                    BleBaseDataForOutlineMovement.getOutlineInstance().dealTheData(mContext, data, data.length);
                    break;
            }
        }
    };

    private BleNotifyParse() {
    }

    public static BleNotifyParse getInstance() {
        if (mBleNotifyParse == null) {
            mBleNotifyParse = new BleNotifyParse();
        }
        return mBleNotifyParse;
    }

    public void doParse(Context mContext, byte[] notifyData) {
        Log.i(TAG, "writeDelay receveData: " + FormatUtils.bytesToHexString(notifyData));
        this.mContext = mContext;
        synchronized (mNotifyLock) {
            excuteParse(mContext, notifyData);
        }
    }

    private static final int BUFFER_MAX_LEN = 8 * 1024;
    private byte[] buffer = new byte[BUFFER_MAX_LEN];
    private byte[] bufferTmp = new byte[1024];
    private int bufferFront = 0;
    private int bufferRear = 0;

    private void excuteParse(Context mContext, final byte[] notifyData) {
        for (int i = 0; i < notifyData.length; i++) {
            int pos = bufferFront;
            buffer[pos] = notifyData[i];
            bufferFront = (bufferFront + 1) % BUFFER_MAX_LEN;
            if(bufferFront == bufferRear)
            {
                bufferRear = (bufferRear + 1) % BUFFER_MAX_LEN;
            }
        }

        boolean isFindNotifyBegin = false;
        int notifyIndex = 0;
        int msgLen = 0;
        for (int read = bufferRear; read != bufferFront;) {
            if (buffer[read] == (byte) 0x68 && !isFindNotifyBegin) {
                isFindNotifyBegin = true;
                notifyIndex = 0;
                msgLen = 6;
            }
            if (isFindNotifyBegin) {
                bufferTmp[notifyIndex] = buffer[read];
                notifyIndex++;
            }
            if (notifyIndex == 4) {
                msgLen = bytes2Char(bufferTmp, 2) + 4 + 2;
                if (msgLen > 1000) {
                    isFindNotifyBegin = false;
                    bufferRear = (bufferRear + 1) % BUFFER_MAX_LEN;
                    read = bufferRear;
                    notifyIndex = 0;
                    continue;
                }
            }
            if (isFindNotifyBegin && notifyIndex == msgLen) {
                if (buffer[read] == 0x16) {   // 处理数据
                    bufferRear = (read + 1) % BUFFER_MAX_LEN;
                    read = bufferRear;
                    isFindNotifyBegin = false;
                    excuteParse(mContext, bufferTmp, notifyIndex);
                    notifyIndex = 0;
                    continue;
                } else {
                    isFindNotifyBegin = false;
                    bufferRear = (bufferRear + 1) % BUFFER_MAX_LEN;
                    read = bufferRear;
                    notifyIndex = 0;
                    continue;
                }
            }
            read = (read + 1) % BUFFER_MAX_LEN;
        }
    }

    private boolean checkData(byte[] bufferTmp, byte[] totalData) {
        byte addd = 0;
        int a = totalData.length - 1;
        int b = totalData.length - 2;
        for (int i = 0; i < totalData.length; i++) {
            totalData[i] = bufferTmp[i];
            if (i != b && i != a) {
                addd += bufferTmp[i];
            }
        }
        byte add = (byte) (addd % 0xff);
        byte endFram = totalData[totalData.length - 2];
        if (add == endFram) {
            return true;
        } else {
            return false;
        }
    }

    private void excuteParse(final Context mContext, byte[] bufferTmp, int notifyIndex) {
        byte head = bufferTmp[0];
        byte cmd = (byte) (bufferTmp[1] & 0xff);
        int dataLen = bytes2Char(bufferTmp, 2);
        if (dataLen > 1000 || bufferTmp.length < dataLen) {
            return;
        }
        byte[] theData = new byte[dataLen + 2];
        byte[] totalData = new byte[dataLen + 6];
        if (!checkData(bufferTmp, totalData)) {
            return;
        }
        for (int i = 0; i < theData.length; i++) {
            theData[i] = bufferTmp[i + 4];
        }
        bufferTmp = theData;
        if (cmd == BleDataForBattery.fromCmd) {
            BleDataForBattery.getInstance().dealReceData(mContext, bufferTmp, dataLen);
        } else if (cmd == BleDataForHardVersion.fromDevice) {
            BleDataForHardVersion.getInstance().dealReceData(mContext, bufferTmp, dataLen);
        } else if (cmd == BleDataForSettingParams.fromDevice) {
//                new BleDataForSettingParams(mContext).dealBleResponse(MainActivity.this, bufferTmp, bufferTmp.length);
//            ((Activity)mContext).runOnUiThread(new Runnable()
//            {
//                @Override
//                public void run()
//                {
//                    Toast.makeText(mContext, mContext.getString(R.string.step_pramas), Toast.LENGTH_SHORT).show();
//                }
//            });
        } else if (cmd == BleDataForCustomRemind.fromDevice) {
            BleDataForCustomRemind.getCustomRemindDataInstance().dealTheValidData(cmd, mContext, bufferTmp);
        } else if (cmd == BleDataForHRWarning.fromDevice) {
            BleDataForHRWarning.getInstance().dealTheHRData(bufferTmp, mContext);
        } else if (cmd == BleDataForRingDelay.fromDevice) {
            BleDataForRingDelay.getDelayInstance().dealTheDelayData(mContext, bufferTmp);
        } else if (cmd == BleDataForFactoryReset.fromDevice) {
            BleDataForFactoryReset.getBleDataInstance().dealTheResult(mContext, bufferTmp);
        }
        else if (cmd == BleZhifu.fromCmd) {
            BleZhifu.getInstance().dealReceData(mContext, bufferTmp);
        }
        else if (cmd == BleDataForUpLoad.fromDevice) {
//             String result = FormatUtils.bytesToHexString(bufferTmp);
//             Intent intent = new Intent();
//             intent.setAction("broadcast for update hardware");
//             intent.putExtra("intent for update hardware result", result);
//             mContext.sendBroadcast(intent);
            BleDataForUpLoad.getUpLoadInstance().dealUpLoadBackData(cmd, bufferTmp);
        } else if (cmd == BleForPhoneAndSmsRemind.phoneRemindFromDevice) {
            BleForPhoneAndSmsRemind.getInstance().dealTheResponse(true);
        } else if (cmd == BleForPhoneAndSmsRemind.formDevice) {
            if ((bufferTmp[0] == (byte) 0x00 && bufferTmp[1] == (byte) 0x03)
                    || (bufferTmp[0] == (byte) 0x00 && bufferTmp[1] == (byte) 0x02)
                    || (bufferTmp[0] == (byte) 0x01 && bufferTmp[1] == (byte) 0x03)) {
                BleForPhoneAndSmsRemind.getInstance().dealOpenResponse(bufferTmp);
            } else if (bufferTmp[1] == (byte) 0x02 && bufferTmp[0] == (byte) 0x01) {
                BleForQQWeiChartFacebook.getInstance().dealTheResuponse(bufferTmp);
            } else if (bufferTmp[1] == (byte) 0x0a || bufferTmp[1] == (byte) 0x0b
                    || bufferTmp[1] == (byte) 0x0c || bufferTmp[1] == (byte) 0x0d
                    || bufferTmp[1] == (byte) 0x09 || bufferTmp[1] == (byte) 0x0e) {
                BleForQQWeiChartFacebook.getInstance().dealOpenOrCloseRequese(bufferTmp);
            } else if (bufferTmp[1] == (byte) 0x01) {
                BleForLostRemind.getInstance().dealTheLostResqonse(bufferTmp);
            } else if (bufferTmp[1] == (byte) 0x06) {
                BleForLIftUpRemind.getInstance().dealLiftUpResqonse(bufferTmp);
            } else if (bufferTmp[0] == (byte) 0x02) {
                BleForQQWeiChartFacebook.getInstance().dealOpenOrCloseRequese(bufferTmp);
            }
//            }
//            {
//                if(bufferTmp[1] == (byte)0x03)
//                {
//                    BleForPhoneAndSmsRemind.getInstance().dealOpenResponse(bufferTmp);
//                }
//                else if(bufferTmp[1] == (byte)0x02)
//                {
//                    BleForQQWeiChartFacebook.getInstance().dealOpenOrCloseRequese(bufferTmp);
//                }
//                else if(bufferTmp[1] == (byte)0x01)
//                {
//                    BleForLostRemind.getInstance().dealTheLostResqonse(bufferTmp);
//                }
//                else if(bufferTmp[1] == (byte)0x05)
//                {
//                    BleForLIftUpRemind.getInstance().dealLiftUpResqonse(bufferTmp);
//                }
//            }
        } else if (cmd == BleForPhoneAndSmsRemind.smsRemindFromDevice) {
            if (bufferTmp[0] == (byte) 0x00) {
                BleForPhoneAndSmsRemind.getInstance().dealSmsDataBack(bufferTmp);
            } else {
                BleDataForQQAndOtherRemine.getIntance().dealRemindResponse(bufferTmp);
            }
        } else if (cmd == BleForFindDevice.fromDevice) {
            BleForFindDevice.getBleForFindDeviceInstance().dealTheResponseData(bufferTmp);
        } else if (cmd == (byte) (BleDataForDayData.fromDevice)) {
            if (bufferTmp.length < 34) return;
            byte pagType = bufferTmp[3];
            if (pagType == (byte) 0x00 && bufferTmp.length == 34) {
                BleDataForDayData.getDayDataInstance(mContext).dealDayData(mContext, bufferTmp);
            } else if (pagType == (byte) 0x00) {
                BleDataForDayData.getDayDataInstance(mContext).juestResponse(bufferTmp);
            } else if (pagType == (byte) 0x01 && bufferTmp.length >= 198) {
                BleDataForEachHourData.getEachHourDataInstance().dealTheEachData(bufferTmp);
            } else if (pagType == (byte) 0x02 && bufferTmp.length > 40) {
                BleDataForSleepData.getInstance(mContext).dealTheSleepData(bufferTmp);
            } else if (pagType == (byte) 0x03 && bufferTmp.length > 180) {
                BleDataForDayHeartReatData.getHRDataInstance(mContext).dealTheHeartRateData(bufferTmp);
            }
        } else if (cmd == BleBaseDataForOutlineMovement.mNotify_cmd) {
            BleBaseDataForOutlineMovement.getOutlineInstance().requstOutlineData(bufferTmp);
            new DealTheOutLineData().execute(bufferTmp);
        } else if (cmd == BleDataforSyn.back_cmd) {
            BleDataforSyn.getSynInstance().dealTheResult();
        } else if (cmd == BleDataForCustomRemind.fromDeviceNull) {
            BleDataForCustomRemind.getCustomRemindDataInstance().dealTheValidData(cmd, mContext, bufferTmp);
        } else if (cmd == BleForGetFatigueData.fromDevice) {
            BleForGetFatigueData.getInstance(mContext).dealTheResbonseData(bufferTmp);
        } else if (cmd == BleForGetFatigueData.exceptionDevice) {
            BleForGetFatigueData.getInstance(mContext).dealException();
        } else if (cmd == BleForSitRemind.fromDevice) {
            BleForSitRemind.getInstance().dealTheResponseData(bufferTmp);
        } else if (cmd == BleForSitRemind.excepteionDevice) {
            BleForSitRemind.getInstance().dealSitException(bufferTmp);
        } else if (cmd == BleDataForSettingArgs.fromDevice) {
            BleDataForSettingArgs.getInstance(mContext).dealTheBack(bufferTmp);
        } else if (cmd == BleDataForTakePhoto.startFromDevice) {
            BleDataForTakePhoto.getInstance().dealOpenResponse(bufferTmp);
        } else if (cmd == BleDataForTakePhoto.takeFromDevice) {
            BleDataForTakePhoto.getInstance().backMessageToDevice();
        } else if (cmd == DeviceExceptionDeal.fromDevice) {
            DeviceExceptionDeal.getExceptionInstance(mContext).dealExceptionInfo(bufferTmp);
        } else if (cmd == DeviceExceptionDeal.testFromDevice) {
            Log.i(TAG, "huang1234: " + FormatUtils.bytesToHexString(bufferTmp));
            DeviceExceptionDeal.getExceptionInstance(mContext).dealTextData(bufferTmp);
        } else if (cmd == BleDataForOnLineMovement.fromDevice) {
            BleDataForOnLineMovement.getBleDataForOutlineInstance().dealOnlineHRMonitor(bufferTmp);
        }
        //todo 詢問血压
        else if (cmd == BleBaseDataForBlood.mNotify_cmd) {
           // Log.i(TAG, "writeDelay receveData: " + FormatUtils.bytesToHexString(notifyData));
            Log.i(TAG, "writeDelay receveData: "+ bufferTmp[0]);
            if (bufferTmp[0] == 4) {
                SharedPreferences preferences = mContext.getSharedPreferences("user", Context.MODE_PRIVATE);
                String strlow = preferences.getString("short_ed", "");
                String strhig = preferences.getString("height_ed", "");
                if (strlow.length() > 1 && strhig.length() > 1) {
                    int int_s = Integer.parseInt(strlow);
                    int int_h = Integer.parseInt(strhig);
                    BleBaseDataForBlood.getBloodInstance().requstOutlineData(int_h, int_s);
                }
                //   BleBaseDataForBlood.getBloodInstance().requstOutlineData();
                BleBaseDataForBlood.getBloodInstance().dealTheData(bufferTmp);
            }else if(bufferTmp[0] == 2){
                BleBaseDataForBlood.getBloodInstance().requestdevice();
            }
                BleBaseDataForBlood.getBloodInstance().requstOutlineData();
                BleBaseDataForBlood.getBloodInstance().dealTheData(bufferTmp);


        } else if (cmd == BleDataForPhoneComm.fromDevice) {
            BleDataForPhoneComm.getInstance().dealDeviceComm(bufferTmp);
        } else if (cmd == BleReadDeviceMenuState.fromDevice) {
            BleReadDeviceMenuState.getInstance().sendSuccess(bufferTmp);
        } else if (cmd == BleDataForWeather.fromDevice || cmd == BleDataForWeather.fromDeviceNew) {
            BleDataForWeather.getIntance().dealWeatherBack(bufferTmp);
        } else if (cmd == BleDataForFrame.fromDevice) {
            Log.i(TAG, "writeDelay receveData222: " + FormatUtils.bytesToHexString(bufferTmp));
            BleDataForFrame.getInstance().dealReceData(bufferTmp);
        } else if (cmd == BleDataForTarget.fromDevice) {
            BleDataForTarget.getInstance().dealComm(bufferTmp);
        } else if (cmd == BleDataForFrame.fromDevice3) {
            BleDataForFrame.getInstance().dealReceB3(bufferTmp);
        }
    }


    private class DealTheOutLineData extends AsyncTask<byte[], Void, Boolean> {
        private byte[] dataByte = null;

        @Override
        protected Boolean doInBackground(byte[]... params) {
            dataByte = params[0];
            BleDataForDayHeartReatData.getHRDataInstance(mContext).requestHeartReatDataAll();
            int ta = 0;
            while (ta < 30) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ta++;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                Bundle bundle = new Bundle();
                bundle.putByteArray(MESSAGE_FOR_HANDLER, dataByte);
                Message message = new Message();
                message.what = 0;
                message.setData(bundle);
                mHandler.sendMessageDelayed(message, 1500);
            }
        }
    }

    int bytes2Char(byte[] data, int offset) {
        int va = data[offset] & 0xFF;
        int vb = (data[offset + 1] << 8) & 0xFFFF;
        return va + vb;
    }


}
