package com.huichenghe.blecontrol.Ble;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


import com.huichenghe.blecontrol.Utils.FormatUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 处理app收到手环的数据，此类为单利
 * Created by lixiaoning on 15-11-sosPhone.
 */
public class BleNotifyParse {
    private static final String TAG = "BleNotifyParse";
    private final String mNotifyLock = "mNotifyLock";     // lock对象
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
                    new BleBaseDataForOutlineMovement().dealTheData(mContext, data, data.length);
                    break;
            }
        }
    };

    //    private static class MyHandler extends Handler
//    {
//
//        private WeakReference<BleNotifyParse> weekP = null;
//
//        public MyHandler(BleNotifyParse bleNotifyParse)
//        {
//            weekP = new WeakReference<>(bleNotifyParse);
//
//
//        }
//        @Override
//        public void handleMessage(Message msg)
//        {
//            BleNotifyParse parsess = weekP.get();
//            if(parsess != null)
//            {
//                Bundle bundle = msg.getData();
//                byte[] data = bundle.getByteArray(MESSAGE_FOR_HANDLER);
//
//                switch (msg.what)
//                {
//                    case 0:
//                        new BleBaseDataForOutlineMovement().dealTheData(mContext, data, data.length);
//                        break;
//                }
//
//            }
//        }
//    }
//    private  MyHandler mHandler = new MyHandler(this);
    private BleNotifyParse()
    {
    }

    public static BleNotifyParse getInstance() {
        if (mBleNotifyParse == null) {
            mBleNotifyParse = new BleNotifyParse();
        }
        return mBleNotifyParse;
    }
    /**
     * 添加同步锁
     *
     * @param mContext
     * @param notifyData
     */
    public void doParse(Context mContext, byte[] notifyData)
    {
        this.mContext = mContext;
        synchronized (mNotifyLock)
        {
            excuteParse(mContext, notifyData);
        }
    }
    private static final int BUFFER_MAX_LEN = 8 * 1024; // 最大长度
    private byte[] buffer = new byte[BUFFER_MAX_LEN];   // 母数组
    private byte[] bufferTmp = new byte[1024];          // 单条数据的数组
    private int bufferLength;       // 未处理的数据的长度
    private int bufferReadB;        // 已处理到的位置,处理过的数据不在bufferLength里边，

    /**
     * 此处理方式，为一个链式数组，新数据在数据尾部添加，读取数据从第一个开始读，下标超过最大值则轮换为0，再一次轮询
     * 从母数组中取出数据第一条数据
     *
     * @param mContext
     * @param notifyData
     */
    private void excuteParse(Context mContext, final byte[] notifyData)
    {
        Log.i(TAG, "writeDelayValue receveValue done:"
                + FormatUtils.bytesToHexString(notifyData) + "++长度++" + notifyData.length);
        saveToFile(notifyData);  // 保存到sd卡
        /**
         * 遍历当前数据，并添加到母数组
         */
        for (int i = 0; i < notifyData.length; i++)     // 遍历当前数据，
        {
            int pos = bufferReadB + bufferLength + i;   // pos为添加到母数组的起始位置
            pos %= BUFFER_MAX_LEN;                       // 下标超出母数组范围则循环到0，重新计数
            buffer[pos] = notifyData[i];                  // 取出数据放入母数组
        }
        bufferLength += notifyData.length;              // bufferLength为未处理的数据的长度
        boolean isFindNotifyBegin = false;               // 标识当前数据是否为包头
        int notifyIndex = 0;                              // 标识一条数据开始的位置
        int msgLen = 0;                                   // 一条数据的长度
        Log.i(TAG, "buffer11Tmp : bufferReadB = " + bufferReadB + "; bufferLength = " + bufferLength);
        Log.i(TAG, "buffer11Tmp : buffer = " + FormatUtils.bytesToHexString(buffer));
        /**
         * 从母数组取数据并处理
         * 开始下标为读取到的位置，结束位置为数据的长度，即处理未处理的数据
         */
        for (int read = bufferReadB; read < bufferReadB + bufferLength; read++)
        {
            int pos = read % BUFFER_MAX_LEN;
            if (buffer[pos] == 0x68 && !isFindNotifyBegin)   // 判断此条数据的包头
            {
                isFindNotifyBegin = true;
                notifyIndex = 0;                            // 标识此条数据开始位置
                msgLen = 6;                                 // 此变量为数据长度
            }
            if (isFindNotifyBegin) {
                bufferTmp[notifyIndex] = buffer[pos];
                notifyIndex++;
            }
            /**
             * 解析数据长度
             */
            if (notifyIndex == 4)
            {
                msgLen = bytes2Char(bufferTmp, 2) + 4 + 2;  // 解析数据域长度
                if (msgLen > 1000)                           // 长度大于1000，数据错误
                {   // 重置标识
                    isFindNotifyBegin = false;
                    read = read - notifyIndex + 1;
                    notifyIndex = 0;
                }
            }
            if (isFindNotifyBegin && notifyIndex == msgLen)  // 数据结尾
            {
                if (buffer[pos] == 0x16) {   // 处理数据
                    excuteParse(mContext, bufferTmp, notifyIndex);
                    bufferLength -= (read - bufferReadB);   // 当前数据结束的位置，减去开始的位置，即当前数据长度，处理数据后总长去除此条数据
                    bufferReadB = pos + 1;                  // +1是下一条数据开始的下标
                    isFindNotifyBegin = false;
                    notifyIndex = 0;
                } else {   // 如果到了数据结尾却没有尾帧，则重置read值，重新遍历
                    isFindNotifyBegin = false;
                    read = read - notifyIndex + 1;
                }
            }
        }
    }

    private boolean checkData(byte[] bufferTmp, byte[] totalData)
    {
//        Log.i(TAG, "校验数据：" + FormatUtils.bytesToHexString(bufferTmp));
//        68a000000816
        byte addd = 0;
        int a = totalData.length - 1;
        int b = totalData.length - 2;
        for (int i = 0; i < totalData.length; i++)
        {
            totalData[i] = bufferTmp[i];
            if(i != b && i != a)
            {
                addd += bufferTmp[i];
            }
        }
        byte add = (byte) (addd % 0xff);
        byte endFram = totalData[totalData.length - 2];
//        Log.i(TAG, "校验数据对比：" + add + "--" + endFram + "--" + true);
        if(add == endFram)
        {
            return true;
        }
        else
        {
//            Log.i(TAG, "校验数据对比：" + add + "--" + endFram + "--" + false);
            return false;
        }
    }

    /**
     * 重载方法处理一条完整的数据
     * @param mContext
     * @param bufferTmp
     * @param notifyIndex
     */
    private void excuteParse(final Context mContext, byte[] bufferTmp, int notifyIndex)
    {
        Log.i(TAG, "excuteParse当前解析的数据是：" + FormatUtils.bytesToHexString(bufferTmp));
//        68a000000816
        byte head = bufferTmp[0];       // 包头
        byte cmd = (byte) (bufferTmp[1] & 0xff);        // 功能码
        int dataLen = bytes2Char(bufferTmp, 2);  // 数据长度
//        Log.i(TAG, "包头: " + (head & 0xff) + "\r\n" + "校验码：" + (cmd & 0xff)+ "\r\n" + "数据域长度：" + dataLen);
        if(dataLen > 1000 || bufferTmp.length < dataLen)    // 数据错误，直接结束
        {
            return;
        }
        byte[] theData = new byte[dataLen + 2];
        byte[] totalData = new byte[dataLen + 6];
//        System.arraycopy(bufferTmp, 0, totalData, 0, totalData.length);
        if(!checkData(bufferTmp, totalData))
        {
            return;
        }
        for (int i = 0; i < theData.length; i ++)
        {
            theData[i] = bufferTmp[i + 4];       // 从下标5开始取数据，到数据长度结束的位置，即取出的是当前数据的数据域
        }
        bufferTmp = theData;
//        Log.i(TAG,  "控制码:" + (int)(cmd & 0xff));
//        Log.i("", "解析后的数据域：" + FormatUtils.bytesToHexString(theData));
        // 校验数据

            // 接收的手环电量
        if(cmd == BleDataForBattery.fromCmd)
        {
            BleDataForBattery.getInstance().dealReceData(mContext, bufferTmp, dataLen);
        }
        else if(cmd == BleDataForHardVersion.fromDevice)    //接收到的版本号
        {
            BleDataForHardVersion.getInstance().dealReceData(mContext, bufferTmp, dataLen);
        }
        else if(cmd == BleDataForSettingParams.fromDevice)
        {
//                new BleDataForSettingParams(mContext).dealBleResponse(MainActivity.this, bufferTmp, bufferTmp.length);
            ((Activity)mContext).runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(mContext, mContext.getString(R.string.step_pramas), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if(cmd == BleDataForCustomRemind.fromDevice)    // 收到有数据的自定义提醒
        {
            BleDataForCustomRemind.getCustomRemindDataInstance().dealTheValidData(mContext, bufferTmp);
//                68 89 0700 00 00 04 01 0d34 2e 6c16
//                new BleDataForCustomRemind().dealTheValidData(mContext, bufferTmp);
                // 发送广播通知RemindActivity解析并显示数据
//                Intent intent = new Intent();
//                intent.setAction(MyConfingInfo.CUSTOM_REMIND_VALID);
//                intent.putExtra(MyConfingInfo.CUSTOM_REMIND_VALID_DATA, bufferTmp);
//                mContext.sendBroadcast(intent);
//                String re = FormatUtils.bytesToHexString(bufferTmp);
//                Log.i(TAG, "自定义提醒返回数据:" + re);
//                if(re != null && re.equals("f116"))
//                {
//                    new BleDataForCustomRemind().dealTheDeleteCallback(bufferTmp);
//                }
         }
         else if(cmd == BleDataForHRWarning.fromDevice)
         {
            BleDataForHRWarning.getInstance().dealTheHRData(bufferTmp, mContext);
         }
         else if(cmd == BleDataForRingDelay.fromDevice)
         {
            BleDataForRingDelay.getDelayInstance().dealTheDelayData(mContext, bufferTmp);
         }
         else if(cmd == BleDataForFactoryReset.fromDevice)
         {
            new BleDataForFactoryReset().dealTheResult(mContext, bufferTmp);
         }
         else if(cmd == BleDataForUpLoad.fromDevice)
         {
             String result = FormatUtils.bytesToHexString(bufferTmp);
             Intent intent = new Intent();
             intent.setAction(MyConfingInfo.BROADCAST_FOR_UPDATA_HARDWARE);
             intent.putExtra(MyConfingInfo.INTENT_FOR_UPDATA_HARDWARE_RESULT, result);
             mContext.sendBroadcast(intent);
         }
         else if(cmd == BleForPhoneAndSmsRemind.phoneRemindFromDevice)
         {
             BleForPhoneAndSmsRemind.getInstance().dealTheResponse(true);
             Log.i(TAG, "手环提醒成功");
         }
        else if(cmd == BleForPhoneAndSmsRemind.formDevice)
        {
//            68850200 0002f116
            if(bufferTmp[0] == (byte)0x01)
            {
                if(bufferTmp[1] == (byte)0x03)
                {
                    BleForPhoneAndSmsRemind.getInstance().dealOpenResponse(bufferTmp);
                }
                else if(bufferTmp[1] == (byte)0x02 && bufferTmp[0] == (byte)0x01)
                {
                    BleForQQWeiChartFacebook.getInstance().dealTheResuponse(bufferTmp);
                }
                else if(bufferTmp[1] == (byte)0x01)
                {
                    BleForLostRemind.getInstance().dealTheLostResqonse(bufferTmp);
                }
            }
            else if(bufferTmp[0] == (byte)0x00)
            {
                if(bufferTmp[1] == (byte)0x02)
                {
                    BleForPhoneAndSmsRemind.getInstance().dealOpenResponse(bufferTmp);
                }
                else
                {
                    BleForQQWeiChartFacebook.getInstance().dealOpenOrCloseRequese(bufferTmp);
                }
            }
        }
        else if(cmd == BleForPhoneAndSmsRemind.smsRemindFromDevice)
        {
            if(bufferTmp[0] == (byte)0x00)
            {
                BleForPhoneAndSmsRemind.getInstance().dealSmsDataBack(bufferTmp);
            }
            else
            {
                BleDataForQQAndOtherRemine.getIntance().dealRemindResponse(bufferTmp);
            }
        }
         else if(cmd == BleForFindDevice.fromDevice)
         {
             Log.i(TAG, "找手环返回数据" + FormatUtils.bytesToHexString(bufferTmp));
             BleForFindDevice.getBleForFindDeviceInstance().dealTheResponseData(bufferTmp);
         }
         else if(cmd == (byte)(BleDataForDayData.fromDevice))        // 接收到手环的天数据,此为主页上的数据
         {
                if(bufferTmp.length < 34)return;
                Log.i(TAG, "此条数据:" + FormatUtils.bytesToHexString(bufferTmp));
                // 判断包类型，通过数据域第四个字节判断
                byte pagType = bufferTmp[3];
                if(pagType == (byte)0x00 && bufferTmp.length == 34)
                {
                    new BleDataForDayData(mContext).dealDayData(mContext, bufferTmp);
                }
                else if(pagType == (byte)0x00)
                {
                    new BleDataForDayData(mContext).juestResponse(bufferTmp);
                }
                else if(pagType == (byte)0x01 && bufferTmp.length >= 198)
                {
                    new BleDataForEachHourData(mContext).dealTheEachData(bufferTmp);
                }
                else if(pagType == (byte)0x02 && bufferTmp.length > 40)
                {
                    BleDataForSleepData.getInstance(mContext).dealTheSleepData(bufferTmp);
                }
                else if(pagType == (byte)0x03 && bufferTmp.length > 180)
                {
                    new BleDataForDayHeartReatData(mContext).dealTheHeartRateData(bufferTmp);
                }
         }
         else if(cmd == BleBaseDataForOutlineMovement.mNotify_cmd)   // 离线数据
         {
             new BleBaseDataForOutlineMovement().requstOutlineData(bufferTmp);  // 回复离线数据
             new DealTheOutLineData().execute(bufferTmp);
         }
         else if(cmd == BleDataforSyn.back_cmd)
         {
             BleDataforSyn.getSynInstance().dealTheResult();
             Log.i(TAG, "同步时间结果:" + FormatUtils.bytesToHexString(bufferTmp));
          }
          else if(cmd == BleDataForCustomRemind.fromDeviceNull)
          {
             BleDataForCustomRemind.getCustomRemindDataInstance().dealTheValidData(mContext, bufferTmp);
          }
          else if(cmd == BleForGetFatigueData.fromDevice)
          {
            BleForGetFatigueData.getInstance(mContext).dealTheResbonseData(bufferTmp);
          }
        else if(cmd == BleForGetFatigueData.exceptionDevice)
        {
            BleForGetFatigueData.getInstance(mContext).dealException();
        }
        else if(cmd == BleForSitRemind.fromDevice)
        {
            BleForSitRemind.getInstance().dealTheResponseData(bufferTmp);
        }
        else if(cmd == BleForSitRemind.excepteionDevice)
        {
            BleForSitRemind.getInstance().dealSitException(bufferTmp);
        }
        else if(cmd == BleDataForSettingArgs.fromDevice)
        {
            BleDataForSettingArgs.getInstance(mContext).dealTheBack(bufferTmp);
        }
        else if(cmd == BleDataForTakePhoto.startFromDevice)
        {
            BleDataForTakePhoto.getInstance().dealOpenResponse(bufferTmp);
        }
        else if(cmd == BleDataForTakePhoto.takeFromDevice)
        {
            BleDataForTakePhoto.getInstance().backMessageToDevice();
        }
        else if(cmd == DeviceExceptionDeal.fromDevice)
        {
            DeviceExceptionDeal.getExceptionInstance(mContext).dealExceptionInfo(bufferTmp);
        }
        else if(cmd == DeviceExceptionDeal.testFromDevice)
        {
            DeviceExceptionDeal.getExceptionInstance(mContext).dealTextData(bufferTmp);

        }
    }



    private class DealTheOutLineData extends AsyncTask<byte[], Void, Boolean>
    {
        private byte[] dataByte = null;
        @Override
        protected Boolean doInBackground(byte[]... params)
        {
            dataByte = params[0];
            new BleDataForDayHeartReatData(mContext).requestHeartReatDataAll();
            int ta = 0;
            while (ta < 30)
            {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ta ++;
                Log.i(TAG, "执行时间等待:获取离线数据中的while循环:" + System.currentTimeMillis());
            }
            return true;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            super.onPostExecute(aBoolean);
            if(aBoolean)
            {
                Bundle bundle = new Bundle();
                bundle.putByteArray(MESSAGE_FOR_HANDLER, dataByte);
                Message message = new Message();
                message.what = 0;
                message.setData(bundle);
                mHandler.sendMessageDelayed(message, 1500);
            }
        }
    }


    /**
     * 两个byte转化为int
     * @param data
     * @param offset
     * @return
     */
    int bytes2Char(byte[] data, int offset)
    {
        int va = data[offset] & 0xFF;               // 第三个数据，取最后八位
        int vb = (data[offset + 1] << 8) & 0xFFFF;  // 第四个数据，向左移动八位，取十六位
        return  va + vb;
    }

    /**
     * 将app收的到的数据保存到sd卡
     * @param notifyData
     */
    private void saveToFile(byte[] notifyData)
    {
        FileOutputStream out;
        String filePath = SDPathUtils.getSdcardPath() + MyConfingInfo.SDCARD_DATA_ROOT_DIR;
        File fileP = new File(filePath);
//        Log.i(TAG, "当前文件保存大小" + fileP.length());
        if(!fileP.exists())fileP.mkdirs();
        File file = new File(filePath + File.separator + "liveData.txt");
        if(file.length() > 1024 * 1024)
        {
            boolean s = file.delete();
        }
        if(!file.exists())
        {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String useAccount = UserAccountUtil.getAccount(mContext);
        try {
            out = new FileOutputStream(file, true);     // 创建输出流对象，true代表追加
            String str = "手环数据："  + useAccount + " \t时间" + DateUtils.getNowTimeToMill() + "\t数据:"
                    + FormatUtils.bytesToHexString(notifyData) + "\r\n";
            out.write(str.getBytes("UTF-8"));
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
