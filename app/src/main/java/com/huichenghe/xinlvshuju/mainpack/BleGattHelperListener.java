package com.huichenghe.xinlvshuju.mainpack;

import android.bluetooth.BluetoothGatt;

import com.huichenghe.bleControl.Ble.LocalDeviceEntity;

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
