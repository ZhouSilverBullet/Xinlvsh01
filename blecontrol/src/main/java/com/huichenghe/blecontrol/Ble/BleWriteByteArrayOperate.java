package com.huichenghe.blecontrol.Ble;

import android.bluetooth.BluetoothGattCharacteristic;

class BleWriteByteArrayOperate extends BleWriteOperate{
    private final byte[] mValue;
    
    public BleWriteByteArrayOperate(byte[] mValue, BluetoothGattCharacteristic mCharacteristic) {
        super(mCharacteristic);
        this.mValue = mValue;
    }
    

    public byte[] getmValue() {
        return mValue;
    }

    
}
