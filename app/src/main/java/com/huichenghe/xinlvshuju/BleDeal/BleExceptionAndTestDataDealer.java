package com.huichenghe.xinlvshuju.BleDeal;

import android.content.Context;

import com.huichenghe.bleControl.Ble.DeviceConfig;
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
 * Created by lixiaoning on 2016/9/2.
 */
public class BleExceptionAndTestDataDealer {
    public static final String TAG = BleExceptionAndTestDataDealer.class.getSimpleName();
    private int framByte = 0;
    private SaveSDHelper saveHelper = null;
    private Context context;

    public BleExceptionAndTestDataDealer(Context context) {
        this.context = context;
    }


    public void saveExceptionData(byte[] bufferTmp) {
        String data = FormatUtils.bytesToHexString(bufferTmp);
        String add = LocalDataSaveTool.getInstance(context).readSp(DeviceConfig.DEVICE_ADDRESS);
        String name = LocalDataSaveTool.getInstance(context).readSp(DeviceConfig.DEVICE_NAME);
        Calendar calend = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss:SS");
        String allData = "异常时间：" + format.format(calend.getTime()) + "\u3000" + "设备地址：" + add + "\u3000" + "设备名：" + name + "\u3000" + "异常数据数据域：" + data.substring(0, data.length() - 4) + "\r\n";
        String dirce = SDPathUtils.getSdcardPath() + MyConfingInfo.SDCARD_DATA_ROOT_DIR + File.separator + "exceptionData";
        File file = new File(dirce);
        if (!file.exists()) {
            file.mkdirs();
        }
        File filsedata = new File(dirce + File.separator + "excepionLive.txt");
        if (filsedata.exists() && filsedata.length() > 1024 * 1024) {
            filsedata.delete();
        }
        if (!filsedata.exists()) {
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
        } finally {
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

    public void dealTextData(byte[] bufferTmp) {
        if (saveHelper == null) {
            saveHelper = new SaveSDHelper(SDPathUtils.getSdcardPath() + MyConfingInfo.SDCARD_DATA_ROOT_DIR);
        }

//        Flag: 0X01（g-sensor); 0x02(heart);


        if (bufferTmp[0] == (byte) 0x01) {
//            01 00ff 9002 e002 10ff 9002 d002 00ff a002 d002 10ff 9002 c002 10ff 9002 c002 0000 5116

            int checkData = bufferTmp[bufferTmp.length - 4] | bufferTmp[bufferTmp.length - 3];
            if (checkData == framByte) {
                return;
            }
            byte[] temp = new byte[bufferTmp.length - 1];
            //    01
            // e003 5000 3000
            // e003 4000 3000
            // 0004 4000 3000
            // f003 4000 5000
            // e003 50004 000
            // 4d3c  db16
            System.arraycopy(bufferTmp, 1, temp, 0, temp.length);
            framByte = checkData;
            byte[] bb = new byte[2];
            String target = SDPathUtils.getSdcardPath() + MyConfingInfo.SDCARD_DATA_ROOT_DIR + File.separator + "g-sensor.txt";
            for (int i = 0; i < temp.length - 4; i += 6) {
                int a = (temp[i] | (temp[i + 1] << 8));
                int b = (temp[i + 2] | (temp[i + 3] << 8));
                int c = (temp[i + 4] | (temp[i + 5] << 8));
                bb[0] = temp[temp.length - 3];
                bb[1] = temp[temp.length - 4];
                String d = com.huichenghe.bleControl.Utils.FormatUtils.bytesToHexString(bb);
//                int hight = temp[temp.length - 3];
//                int low = temp[temp.length - 4];
//
//                if (hight < 0) {
//                    hight = 128 - hight;
//                }
//                if (low < 0) {
//                    low = 128 - low;
//                }
//                hight = hight * 256;
//                Integer.parseInt(temp[temp.length - 3],16);
//                int d = hight+low;
//
//                Log.i(TAG, "niubi1111hight: " +hight );
//                Log.i(TAG, "niubi1111low: " +low );
//                Log.i(TAG, "niubi1111: " +d );

                String data = "data: " + a + " " + b + " " + c + " " + d + "\r\n";

                saveHelper.saveStringData(target, data);
            }
        } else if (bufferTmp[0] == (byte) 0x02) {
            String target = SDPathUtils.getSdcardPath() + MyConfingInfo.SDCARD_DATA_ROOT_DIR + File.separator + "g-heart.txt";
            for (int i = 1; i < bufferTmp.length - 4; i += 6) {
                int a = bufferTmp[i] | (bufferTmp[i + 1] << 8);
                int b = bufferTmp[i + 2] | (bufferTmp[i + 3] << 8);
                int c = bufferTmp[i + 4] | (bufferTmp[i + 5] << 8);
                String data = "data: " + a + " " + b + " " + c + "\r\n";
                saveHelper.saveStringData(target, data);
            }
        }
    }
}
