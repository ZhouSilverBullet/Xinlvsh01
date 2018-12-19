package com.huichenghe.blecontrol.Ble;

import android.content.Context;
import android.util.Log;

import com.huichenghe.xinlvshuju.SDPathUtils;
import com.huichenghe.xinlvshuju.SaveSDHelper;
import com.huichenghe.xinlvshuju.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by lixiaoning on 2016/7/27.
 */
public class DeviceExceptionDeal extends BleBaseDataManage
{
    public static final String TAG = DeviceExceptionDeal.class.getSimpleName();
    public static final byte fromDevice = (byte)0xa7;
    public static final byte toDevice = (byte)0x27;
    public static final byte testFromDevice = (byte)0xa8;
    public static final byte testToDevice = (byte)0x28;
    private SaveSDHelper saveHelper = null;
    private int framByte = 0;

    private DeviceExceptionDeal(Context context)
    {
        this.context = context;
        saveHelper = new SaveSDHelper(SDPathUtils.getSdcardPath() + MyConfingInfo.SDCARD_DATA_ROOT_DIR);
    };
    private static DeviceExceptionDeal deviceExceptionDeal;
    private Context context;
    public static synchronized DeviceExceptionDeal getExceptionInstance(Context context)
    {
        if(deviceExceptionDeal == null)
        {
            deviceExceptionDeal = new DeviceExceptionDeal(context);
        }
        return deviceExceptionDeal;
    }

    public void dealExceptionInfo(byte[] bufferTmp)
    {
        Log.i(TAG, "异常数据：" + FormatUtils.bytesToHexString(bufferTmp));
        byte[] bytes = new byte[2];
        bytes[0] = 0x01;
        bytes[1] = 0x00;
        setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
        saveExceptionData(bufferTmp);
    }

    private void saveExceptionData(byte[] bufferTmp)
    {
        String data = FormatUtils.bytesToHexString(bufferTmp);
        String add = LocalDataSaveTool.getInstance(context).readSp(DeviceConfig.DEVICE_ADDRESS);
        String name = LocalDataSaveTool.getInstance(context).readSp(DeviceConfig.DEVICE_NAME);
        Calendar calend = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss:SS");
        String allData = "异常时间：" + format.format(calend.getTime()) + "\u3000" + "设备地址：" + add + "\u3000" + "设备名：" + name + "\u3000" + "异常数据数据域：" + data.substring(0, data.length() - 4) + "\r\n";
        String dirce = SDPathUtils.getSdcardPath() + MyConfingInfo.SDCARD_DATA_ROOT_DIR + File.separator + "exceptionData";
        File file = new File(dirce);
        if(!file.exists())
        {
            file.mkdirs();
        }
        File filsedata = new File(dirce + File.separator + "excepionLive.txt");
        if(filsedata.exists() && filsedata.length() > 1024 * 1024)
        {
            filsedata.delete();
        }
        if(!filsedata.exists())
        {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileOutputStream out = null;
        OutputStreamWriter write = null;
        try {
            out = new FileOutputStream(filsedata, true);
            write = new OutputStreamWriter(out);
            write.write(allData);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                out.flush();
                write.flush();
                out.close();
                write.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public void dealTextData(byte[] bufferTmp)
    {
//        Flag: 0X01（g-sensor); 0x02(heart);
        Log.i(TAG, "测试数据：" + FormatUtils.bytesToHexString(bufferTmp));
        if(bufferTmp[0] == (byte)0x01)
        {
//            01
//            00ff 9002 e002
//            10ff 9002 d002
//            00ff a002 d002
//            10ff 9002 c002
//            10ff 9002 c002
//            0000 5116
            int checkData = bufferTmp[bufferTmp.length - 4] | bufferTmp[bufferTmp.length - 3];
            if(checkData == framByte)
            {
                return;
            }
            framByte = checkData;
            String target = SDPathUtils.getSdcardPath() + MyConfingInfo.SDCARD_DATA_ROOT_DIR + File.separator + "g-sensor.txt";
            for (int i = 1; i < bufferTmp.length - 4; i += 6)
            {
                int a = bufferTmp[i] | (bufferTmp[i + 1] << 8);
                int b = bufferTmp[i + 2] | (bufferTmp[i + 3] << 8);
                int c = bufferTmp[i + 4] | (bufferTmp[i + 5] << 8);
                String data = "data: " + a + " " + b + " " + c + "\r\n";
                saveHelper.saveStringData(target, data);
            }
            responseData(bufferTmp[0], bufferTmp[bufferTmp.length - 4], bufferTmp[bufferTmp.length - 3]);
        }
        else if(bufferTmp[0] == (byte)0x02)
        {
            String target = SDPathUtils.getSdcardPath() + MyConfingInfo.SDCARD_DATA_ROOT_DIR + File.separator + "g-heart.txt";
            for (int i = 1; i < bufferTmp.length - 4; i += 6)
            {
                int a = bufferTmp[i] | (bufferTmp[i + 1] << 8);
                int b = bufferTmp[i + 2] | (bufferTmp[i + 3] << 8);
                int c = bufferTmp[i + 4] | (bufferTmp[i + 5] << 8);
                String data = "data: " + a + " " + b + " " + c + "\r\n";
                saveHelper.saveStringData(target, data);
            }
        }
    }

    private void responseData(byte data1, byte b, byte data)
    {
        byte[] resData = new byte[4];
        resData[0] = data1;
        resData[1] = (byte)0x00;
        resData[2] = b;
        resData[3] = data;
        setMsgToByteDataAndSendToDevice(testToDevice, resData, resData.length);
    }
}
