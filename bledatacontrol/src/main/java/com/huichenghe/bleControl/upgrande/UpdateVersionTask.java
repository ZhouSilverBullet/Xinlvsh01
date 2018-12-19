package com.huichenghe.bleControl.upgrande;


import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import com.huichenghe.bleControl.Ble.BleDataForUpLoad;
import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.bleControl.Ble.DataSendCallback;
import com.huichenghe.bleControl.Ble.DeviceConfig;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class UpdateVersionTask extends AsyncTask<String, Integer, Boolean>
{
    public final String TAG = UpdateVersionTask.class.getSimpleName();
    private boolean isStartUp = false;
    private boolean isCancle = false;
    private int eachPackege = 0;
    private Context context;
    private UpdateListener updateListener;
    private final int SEND_HANDLER_UPDATE = 0;


    public UpdateVersionTask(Context context, UpdateListener updateListener)
    {
        this.context = context;
        this.updateListener = updateListener;
    }
    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        Intent intent = new Intent(UpdateConfig.CLOSE_THE_REQUEST_FORM_DEVICE);
        intent.putExtra("ok_or_no", true);
        context.sendBroadcast(intent);
    }

    public void setTaskCancel()
    {
        isCancle = true;
    }

    public interface UpdateListener
    {
        void onProgress(int value);
        void onUpdateSuccess();
        void onUpdateFailed();
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
        updateListener.onProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean)
    {
        super.onPostExecute(aBoolean);
        if(aBoolean)
        {
            updateListener.onUpdateSuccess();
////            MyToastUitls.showToastInString(context, context.getString(R.string.update_success), 1);
//              deleteFiles(FILE_OF_MCU);
//              deleteFiles(FILE_OF_BLUETOOTH);
//              removeTheItem();
//              isUpdateing = false;
//              BluetoothLeService.getInstance().close(false);
//              connectTheSaveDevice();
        }
        else
        {
            updateListener.onUpdateFailed();
//            Toast.makeText(context, "升级失败", Toast.LENGTH_SHORT).show();
//            String content = upEntity.getType() + context.getString(R.string.update_failed);
////                Toast.makeText(context, upEntity.getType() + context.getString(R.string.update_failed), Toast.LENGTH_SHORT).show();
//            MyToastUitls.showToastInString(context, content, 1);
//            deleteFiles(FILE_OF_MCU);
//            deleteFiles(FILE_OF_BLUETOOTH);
//            if(BluetoothLeService.getInstance() != null)
//            {
//                BluetoothLeService.getInstance().setBLENotify(null, true, true);
//            }
        }
//        if(updateWindow != null && updateWindow.isShowing())
//        {
//            updateWindow.dismiss();
//        }
        Intent intent = new Intent("close_the_request_form_device");
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



    class UpLoadCallback implements DataSendCallback
    {
        @Override
        public void sendSuccess(byte[] receveData)
        {
            synchronized (UpdateVersionTask.this)
            {
                if(receveData[0] == 0 || receveData[0] == 2)
                {
                    isStartUp = true;
                }
                else
                {
                    eachPackege = receveData[3] & 0xff | ((receveData[4] & 0xff) << 8);
                }
                UpdateVersionTask.this.notify();
            }
        }

        @Override
        public void sendFailed()
        {
            synchronized (UpdateVersionTask.this)
            {
                isStartUp = false;
                UpdateVersionTask.this.notify();
            }
        }

        @Override
        public void sendFinished(){}
    }

    private boolean updateNow(byte[] fileData)
    {
        synchronized (UpdateVersionTask.this)
        {
            BleDataForUpLoad.getUpLoadInstance().setUpLoadCallback(new UpLoadCallback());
            BleDataForUpLoad.getUpLoadInstance().sendStartComm(fileData.length);
            try {
                UpdateVersionTask.this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(isCancle || ((Activity)context).isFinishing() || ((Activity)context).isDestroyed())
            {
                isCancle = false;
                return false;
            }
            if(!isStartUp)return false;
            isStartUp = false;
            boolean isR = false;
            int countt = fileData.length / 200;
            int surplus = fileData.length % 200;
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
                int lastValue = fileData.length - (i * 200);
                byte[] big = new byte[(lastValue > 200) ? 200 : lastValue];
                System.arraycopy(fileData, alreadySend, big, 0, big.length);
                byte[] allData = sendDataBodyToDevice(big, countt, i + 1);
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
                    SystemClock.sleep(10);
                    if(!isR)
                    {
//                        c = -1;
//                        alreadyLength = 0;
                        try {
                            Thread.sleep(60);
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
                try {
                    UpdateVersionTask.this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(isCancle)
                {
                    isCancle = false;
                    return false;
                }
                if(!checkTheCallback(i + 1))
                {
                    i--;
                    continue;
                }
                alreadySend += big.length;
                publishProgress((int) (((float) alreadySend / fileData.length) * 100));
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            BleDataForUpLoad.getUpLoadInstance().finaishUpdate();
            try {
                UpdateVersionTask.this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(isStartUp)
            {
                return true;
            }
            return isR;
        }
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
                }
            });
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
    public byte[] getSendByteArray(byte[] datas)
    {
        byte[] sendData = new byte[6 + datas.length];
        int count_for_check = 0;

        for (int i = 0; i < sendData.length; i ++)
        {
            if(0 == i)
            {
                sendData[i] = msg_head;
                count_for_check += sendData[i];
            }
            else if(1 == i)
            {
                sendData[i] = msg_cmd;
                count_for_check += sendData[i];
            }
            else if(2 == i)
            {
                sendData[i] = (byte)(datas.length & 0xff);
                count_for_check += sendData[i];
            }
            else if(3 == i)
            {
                sendData[i] = (byte)((datas.length >> 8) & 0xff);
                count_for_check += sendData[i];
            }
            else if((i >= 4) && (i < (datas.length + 4)))
            {
                sendData[i] = datas[i - 4];
                count_for_check += sendData[i];
            }
            else if(i == 4 + datas.length)
            {
                sendData[i] = (byte)(count_for_check % 256);
            }
            else if(i == 5 + datas.length)
            {
                sendData[i] = msg_tail;
            }

        }


        return sendData;
    }


    private boolean checkTheCallback(int currentPackage)
    {
        return eachPackege == currentPackage;
    }

}


