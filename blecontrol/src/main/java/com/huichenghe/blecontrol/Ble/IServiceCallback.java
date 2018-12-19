package com.huichenghe.blecontrol.Ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.util.List;


public interface IServiceCallback
{

    void onBLEServiceFound(LocalDeviceEntity device, BluetoothGatt gatt, List<BluetoothGattService> list);

    void onBLEDeviceConnected(LocalDeviceEntity device, BluetoothGatt gatt);

    void onBLEDeviceDisConnected(LocalDeviceEntity device, BluetoothGatt gatt);

    void onCharacteristicRead(LocalDeviceEntity device, BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, boolean success);

    void onCharacteristicChanged(LocalDeviceEntity device, BluetoothGatt gatt, final String uuid, final byte[] value);

    void onCharacteristicWrite(LocalDeviceEntity device, BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, boolean success);

    void onDescriptorRead(LocalDeviceEntity device, BluetoothGatt gatt, BluetoothGattDescriptor bd, boolean success);

    void onDescriptorWrite(LocalDeviceEntity device, BluetoothGatt gatt, BluetoothGattDescriptor bd, boolean success);

    void onNoBLEServiceFound();

    void onBLEDeviceConnecError(LocalDeviceEntity device, boolean showToast, boolean fromServer);

    void onReadRemoteRssi(LocalDeviceEntity device, BluetoothGatt gatt, int rssi, boolean success);

    void onReliableWriteCompleted(LocalDeviceEntity device, BluetoothGatt gatt, boolean success);

    void onMTUChange(BluetoothGatt gatt, int mtu, int status);
}
