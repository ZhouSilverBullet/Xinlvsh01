package com.huichenghe.xinlvshuju.http;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.bleControl.Ble.DeviceConfig;
import com.huichenghe.xinlvshuju.Utils.FormatUtils;

/**
 * Created by lixiaoning on 16-clock-sosPhone.
 */
public class UpdateHelper
{
    public static String TAG = UpdateHelper.class.getCanonicalName();


    public void sendToDevice(byte[] sendByteArray)
    {
        judgeTheDataLengthAndAddToSendByteArray(sendByteArray);
    }




    /**
     * 此方法判断data的字节长度，并且将包含此data的Task对象添加到集合中
     * @param value
     */
    public void judgeTheDataLengthAndAddToSendByteArray(byte[] value)
    {
        int count = (value.length + 19) / 20;       // +19 的目的是保证，数据不足20的情况下count为1
        if(count == 1)                              // == 1代表value的长度小于 20
        {
            writeUpdateValue(value);
        }
        else                                        // 若大于20， count有可能为2,info,sitting....
        {
            int alreadyUsed = 0;                    // 计数变量，表示已发出的数据
            for(int i = 0; i < count; i ++)         // 遍历数据，count个数就是发送的次数
            {
                int lastValue = value.length - alreadyUsed;         // 剩余的数据
                int length = (lastValue > 20) ? 20 : lastValue;     // 剩余的数据大于20，则长度定为20，小于等于20，则长度为本身的长度
                byte[] bytes = new byte[length];    // 创建一个byte数组，长度为发送数据的长度
                for(int j = 0; j < length; j ++)
                {
                    bytes[j] = value[alreadyUsed + j];
                }
                alreadyUsed += length;

                writeUpdateValue(bytes);
                try
                {
                    Thread.sleep(40);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

            }

        }

        Log.i(TAG, "固件升级写数据: --- " + FormatUtils.bytesToHexString(value));


    }

    private void writeUpdateValue(byte[] value)
    {
        BluetoothLeService serviceMain = BluetoothLeService.getInstance();
        if(serviceMain == null)
        {
            Log.i(TAG, "writeDeylayValue  e1");
            return;
        }
        // 拿到gatt
        BluetoothGatt gatt = serviceMain.getBluetoothGatt();
        if(gatt == null)
        {
            Log.i(TAG, "writeDelayValue   e2");
            return;
        }

        BluetoothGattService service = gatt.getService(DeviceConfig.MAIN_SERVICE_UUID);
        if(service == null)
        {
            Log.i(TAG, "writeDelayValue   e3");
            return;
        }

        BluetoothGattCharacteristic chara = service.getCharacteristic(DeviceConfig.UUID_CHARACTERISTIC_WR);

        BluetoothLeService.getInstance().writeDelayValue(value, chara, new BluetoothLeService.WriteCallBack()
        {
            @Override
            public void onWrite(boolean result)
            {
                Log.i(TAG, "固件升级数据传输结果:" + result);
            }
        });






    }



}
