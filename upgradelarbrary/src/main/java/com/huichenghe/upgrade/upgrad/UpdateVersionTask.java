package com.huichenghe.upgrade.upgrad;

/**
 * Created by lixiaoning on 2016/9/9.
 */

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * 升级
 */
public class UpdateVersionTask extends AsyncTask<String, Integer, Boolean>
{
    public final String TAG = UpdateVersionTask.class.getSimpleName();


    private Context context;
    public UpdateVersionTask(Context context)
    {
        this.context = context;
    }
    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        Intent intent = new Intent(UpdateConfig.CLOSE_THE_REQUEST_FORM_DEVICE);
        intent.putExtra("ok_or_no", true);
        context.sendBroadcast(intent);
    }


    @Override
    protected Boolean doInBackground(String... params)
    {
//            if(BluetoothLeService.getInstance() != null)
//            {
//               BluetoothLeService.getInstance().setBLENotify(null, true, false);
//            }

        byte[] fileData = readTheFileFormSD(params[0]);
        if(fileData == null || fileData.length <= 0)
        {
            return false;
        }
        Log.i(TAG, "开始升级,文件长度:" + fileData.length);
        if(!updateNow(fileData))
        {
            return false;
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(final Integer... values)
    {
        super.onProgressUpdate(values);
        // 在此处更新进度条
    }

    @Override
    protected void onPostExecute(Boolean aBoolean)
    {
        super.onPostExecute(aBoolean);
        isUpdateing = false;
        if(aBoolean)
        {
//                Toast.makeText(context, upEntity.getType() + context.getString(R.string.update_success), Toast.LENGTH_SHORT).show();
            MyToastUitls.showToastInString(context, upEntity.getType() + context.getString(R.string.update_success), 1);
            deleteFiles(FILE_OF_MCU);
            deleteFiles(FILE_OF_BLUETOOTH);
            removeTheItem();
//                isUpdateing = false;
//                BluetoothLeService.getInstance().close(false);
//                connectTheSaveDevice();
        }
        else
        {
            String content = upEntity.getType() + context.getString(R.string.update_failed);
//                Toast.makeText(context, upEntity.getType() + context.getString(R.string.update_failed), Toast.LENGTH_SHORT).show();
            MyToastUitls.showToastInString(context, content, 1);
            deleteFiles(FILE_OF_MCU);
            deleteFiles(FILE_OF_BLUETOOTH);
            if(BluetoothLeService.getInstance() != null)
            {
                BluetoothLeService.getInstance().setBLENotify(null, true, true);
            }
        }

        if(updateWindow != null && updateWindow.isShowing())
        {
            updateWindow.dismiss();
        }

        Intent intent = new Intent(MyConfingInfo.CLOSE_THE_REQUEST_FORM_DEVICE);
        intent.putExtra("ok_or_no", false);
        context.sendBroadcast(intent);


    }

    private byte[] readTheFileFormSD(String filePath)
    {
        File file = new File(filePath);
        if(!file.exists())return null;
        byte[] fileBytePool = new byte[6 * 1024 * 1024];
        byte[] fileBytes = null;

        FileInputStream inputStream = null;
        byte[] tempbytes = new byte[500];
        int byteRead = 0;
        int len = 0;
        try {
            inputStream = new FileInputStream(file);
            while ((byteRead = inputStream.read(tempbytes)) != -1)
            {
                System.arraycopy(tempbytes, 0, fileBytePool, len, byteRead);
                len += byteRead;
            }
            fileBytes = new byte[len];
            System.arraycopy(fileBytePool, 0, fileBytes, 0, len);
//                byte checkSun = IntegerUtil.getCheckSun(fileBytes);
//                fileBytes[len] = checkSun;
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileBytes;
    }





    synchronized private boolean updateNow(byte[] fileData)
    {
        BleDataForUpLoad ble = new BleDataForUpLoad();
        ble.sendToDeviceToStartUpLoad(fileData.length);
        Log.i(TAG, "升级数据比较:" + canContinue + "--" + (byte)0x00);
        int startConut = 0;
        while (!isOk && startConut < 20)
        {
            Log.i(TAG, "升级开始的消息" + isOk);
            try {
                Thread.sleep(1000);
                startConut ++;
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        if(!isOk)
        {
            ble.sendToDeviceToStartUpLoad(fileData.length);
            startConut = 0;
        }
        while (!isOk && startConut < 10)
        {
            try {
                Thread.sleep(1000);
                startConut ++;
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        if(!isOk)return false;
        int times = 0;
        Log.i(TAG, "升级开始的消息" + isOk);

        boolean isR = false;
        int countt = fileData.length / 200;  // 计算发送次数
        int surplus = fileData.length % 200; // 剩余数据长度
        int alreadySend = 0;
        if(surplus > 0)
        {
            countt = countt + 1;
        }
        int j = 0;
        int jCount = 0;
        for(int i = 0; i < countt; i++)
        {
            if(j == i)
            {
                jCount++;
                if(jCount >= 4)
                {
                    return false;
                }
            }
            else
            {
                jCount = 0;
                j = i;
            }

            if(isCancle)
            {
                return false;
            }
            int lastValue = fileData.length - (i * 200);
            byte[] big = new byte[(lastValue > 200) ? 200 : lastValue];
            System.arraycopy(fileData, alreadySend, big, 0, big.length);
            byte[] allData = sendDataBodyToDevice(big, countt, i + 1);
//                Log.i(TAG, "每个包的数据:第" + (i + 1) + "个包:" + FormatUtils.bytesToHexString(allData) + countt);
            int c = 0;
            int second = allData.length / 20;
            second += (allData.length % 20) > 0 ? 1 : 0;
            int alreadyLength = 0;
            int k = 0;
            int kCount = 0;
            while (c < second)
            {
                if(k == c)
                {
                    kCount++;
                    if(kCount >= 4)
                    {
                        return false;
                    }
                }
                else
                {
                    kCount = 0;
                    k = c;
                }
                int seLastes = allData.length - alreadyLength;
                byte[] data = new byte[(seLastes > 20) ? 20 : seLastes];
                System.arraycopy(allData, alreadyLength, data, 0, data.length);
                isR = setDataToDevice(data);
                if(!isR)
                {
//                        c = -1;
//                        alreadyLength = 0;
                    try {
                        Thread.sleep(4);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                else
                {
                    alreadyLength += data.length;
                    c++;
                }
            }
            Log.i(TAG, "升级校验:第几包递减调用前后aa:"  + i);
            if(!checkTheCallback(i + 1))
            {
                Log.i(TAG, "升级校验:第几包:"  + i);
                i--;
                Log.i(TAG, "升级校验:第几包递减后:"  + i);
                continue;
            }
            alreadySend += big.length;
//            onProgressUpdate((int) (((float) alreadySend / fileData.length) * 100));
            publishProgress((int) (((float) alreadySend / fileData.length) * 100));
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ble.overTheUpdate();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int timees = 0;
        while (!isOverUpdate && timees < 10)
        {
            if(timees == 3)
            {
                ble.overTheUpdate();
            }

            if(timees == 6)
            {
                ble.overTheUpdate();
            }
            if(timees == 9)
            {
                ble.overTheUpdate();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timees ++;
        }
        return isR;
    }

    private boolean setDataToDevice(byte[] data)
    {
        final boolean[] a = new boolean[1];
        if(data != null)
        {
            BluetoothLeService serviceMain = BluetoothLeService.getInstance();
            if(serviceMain == null)
            {
                Log.i(TAG, "writeDeylayValue  e1");
                return false;
            }
            // 拿到gatt
            BluetoothGatt gatt = serviceMain.getBluetoothGatt();
            if(gatt == null)
            {
                Log.i(TAG, "writeDelayValue   e2");
                return false;
            }
            BluetoothGattService service = gatt.getService(DeviceConfig.MAIN_SERVICE_UUID);
            if(service == null)
            {
                Log.i(TAG, "writeDelayValue   e3");
                return false;
            }
            BluetoothGattCharacteristic chara = service.getCharacteristic(DeviceConfig.UUID_CHARACTERISTIC_WR);
            BluetoothLeService.getInstance().writeDelayValue(data, chara, new BluetoothLeService.WriteCallBack()
            {
                @Override
                public void onWrite(boolean result)
                {
                    a[0] = result;
//                            Log.i(TAG, "固件升级数据传输结果:" + result);
                }
            });
        }
        try {
            Thread.sleep(4);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        return a[0];
    }
    public byte[] sendDataBodyToDevice(byte[] big, int count, int each)
    {
        byte[] byteData = new byte[big.length + 5];
        byteData[0] = (byte)0x01;
        byteData[1] = (byte)(count & 0xff);
        byteData[2] = (byte)(count >> 8 & 0xff);
        byteData[3] = (byte)(each & 0xff);
        byteData[4] = (byte)(each >> 8 & 0xff);
        System.arraycopy(big, 0, byteData, 5, big.length);
        return getSendByteArray(byteData);
    }


    byte msg_cmd = BleDataForUpLoad.toDevice;
    byte msg_head = (byte)0x68;
    byte msg_tail = 0x16;
    /**
     * 此方法将以上所有项，整合到一个byte数组，以待发送
     * @return
     */
    public byte[] getSendByteArray(byte[] datas)
    {
        byte[] sendData = new byte[6 + datas.length];    // 创建一个长度为整条数据总长的byte数组
        int count_for_check = 0;                            // 校验码的计数变量

        for (int i = 0; i < sendData.length; i ++)
        {
            if(0 == i)      // 包头，数据的第一个字节
            {
                sendData[i] = msg_head;
                count_for_check += sendData[i];
            }
            else if(1 == i) // 功能码，数据的第二个字节
            {
                sendData[i] = msg_cmd;
                count_for_check += sendData[i];
            }
            else if(2 == i) // 数据长度的第一个字节，整条数据的第三个字节
            {
                sendData[i] = (byte)(datas.length & 0xff);       // int为32位，取最后八位
                count_for_check += sendData[i];
            }
            else if(3 == i) // 数据长度的第二个字节
            {
                sendData[i] = (byte)((datas.length >> 8) & 0xff);// int向右移动8位，取右边第二个八位
                count_for_check += sendData[i];
            }
            else if((i >= 4) && (i < (datas.length + 4)))        // 数据域,范围是从第5个字节开始，到数据长度加上前边的四个字节的位置
            {
                sendData[i] = datas[i - 4];
                count_for_check += sendData[i];
            }
            else if(i == 4 + datas.length)                       // 校验码，整条数据的倒数第二个字节
            {
                sendData[i] = (byte)(count_for_check % 256);        // 各个字节相加，对256取余
            }
            else if(i == 5 + datas.length)
            {
                sendData[i] = msg_tail;
            }

        }


        Log.i("", "整合后的数据getSendByteArray" + FormatUtils.bytesToHexString(sendData));
        return sendData;
    }





    private void connectTheSaveDevice()
    {
        ArrayList<DeviceSaveData> allDevice = (ArrayList<DeviceSaveData>) LocalDataBaseUtils.getInstance(context).getDeviceListFromDB();
        if(allDevice.size() != 0)
        {
            DeviceSaveData deviceSaveData = allDevice.get(allDevice.size() - 1);
            LocalDeviceEntity entity = new LocalDeviceEntity(deviceSaveData.getDeviceName(), deviceSaveData.getDeviceAddress(), -70, new byte[10]);
            BluetoothLeService.getInstance().connect(entity);
        }

    }

    private void deleteFiles(String file_of_mcu)
    {
        String pa = SDPathUtils.getSdcardPath() + MyConfingInfo.SDCARD_DATA_ROOT_DIR_UPDATE + File.separator + file_of_mcu;
        File file = new File(pa);
        if(file.exists())
        {
            file.delete();
        }
    }

}

    private boolean checkTheCallback(int currentPackage)
    {
        int times = 0;
        while (totalPackege == 0 || eachPackege == 0)
        {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            times ++;
            if(times == 10)
            {
                break;
            }
        }

        int c = 0;
        while (eachPackege != currentPackage && c < 50)
        {
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            c ++;
            Log.i(TAG, "等待手环返回:eachPackege:" + eachPackege + "currentPackage:" + currentPackage + "-" + c);
        }

        return eachPackege == currentPackage;
    }
