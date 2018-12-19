package com.huichenghe.blecontrol.Ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import java.util.List;

/**
 * Created by lixiaoning on 2016/8/19.
 */
public class BleGattHelper implements IServiceCallback
{
    private Context context;
    private BleGattHelperListener gattHelperListener;
    private IBleDataReceves iBleDataReceves;
    public BleGattHelper(Context context, BleGattHelperListener listener, IBleDataReceves receverCallback)
    {
        this.context = context;
        this.gattHelperListener = listener;
        this.iBleDataReceves = receverCallback;
    }
    @Override
    public void onBLEServiceFound(LocalDeviceEntity device,
                                   BluetoothGatt gatt,
                                   List<BluetoothGattService> list)
    {

    }
    @Override
    public void onBLEDeviceConnected(LocalDeviceEntity device,
                                      BluetoothGatt gatt)
    {

    }

    @Override
    public void onBLEDeviceDisConnected(LocalDeviceEntity device,
                                         BluetoothGatt gatt)
    {

    }

    @Override
    public void onCharacteristicRead(LocalDeviceEntity device,
                                      BluetoothGatt gatt,
                                      BluetoothGattCharacteristic characteristic,
                                      boolean success)
    {

    }

    @Override
    public void onCharacteristicChanged(LocalDeviceEntity device,
                                         BluetoothGatt gatt,
                                         String uuid,
                                         byte[] value)
    {
        if(uuid.equals(DeviceConfig.HEARTRATE_FOR_TIRED_NOTIFY.toString()))
        {
            gattHelperListener.onDeviceStateChangeUI(device, gatt, uuid, value);
//					Log.i(TAG, "stepFragment对象：" + fragmentList.size());
        }
        else if(uuid.equals(DeviceConfig.UUID_CHARACTERISTIC_NOTIFY.toString()))
        {
//			Log.i(TAG, "上传的所有数据:" + FormatUtils.bytesToHexString(value));
//			Log.i(TAG, "添加到线程后返回的数据，进行解析：："
//					+ FormatUtils.bytesToHexString(value) + "++长度++" + value.length);
            BleNotifyParse.getInstance(iBleDataReceves).doParse(context, value);
        }
    }


    @Override
    public void onCharacteristicWrite(LocalDeviceEntity device,
                                       BluetoothGatt gatt,
                                       BluetoothGattCharacteristic characteristic,
                                       boolean success)
    {

    }

    @Override
    public void onDescriptorRead(LocalDeviceEntity device,
                                  BluetoothGatt gatt,
                                  BluetoothGattDescriptor bd,
                                  boolean success)
    {

    }

    @Override
    public void onDescriptorWrite(LocalDeviceEntity device,
                                   BluetoothGatt gatt,
                                   BluetoothGattDescriptor bd,
                                   boolean success)
    {

    }

    @Override
    public void onNoBLEServiceFound() {

    }

    @Override
    public void onBLEDeviceConnecError(LocalDeviceEntity device,
                                        boolean showToast,
                                        boolean fromServer)
    {
        gattHelperListener.onDeviceConnectedChangeUI(device, showToast, fromServer);
    }

    @Override
    public void onReadRemoteRssi(LocalDeviceEntity device,
                                  BluetoothGatt gatt, int rssi,
                                  boolean success) {

    }

    @Override
    public void onReliableWriteCompleted(LocalDeviceEntity device,
                                          BluetoothGatt gatt,
                                          boolean success) {

    }

    @Override
    public void onMTUChange(BluetoothGatt gatt,
                             int mtu, int status)
    { }
}
