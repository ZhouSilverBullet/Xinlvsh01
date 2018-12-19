package com.huichenghe.bleControl.Ble;

import android.bluetooth.BluetoothGatt;

/**
 * Created by lixiaoning on 2016/8/19.
 */
public interface BleGattHelperListener
{
    void onDeviceStateChangeUI(LocalDeviceEntity device,
                               BluetoothGatt gatt,
                               String uuid,
                               byte[] value);

    void onDeviceConnectedChangeUI(LocalDeviceEntity device,
                                   boolean showToast, final
                                   boolean fromServer);
}
