package com.huichenghe.bleControl.Ble;

import android.bluetooth.BluetoothGattCharacteristic;

public class BleWriteOperate {
    private final BluetoothGattCharacteristic mCharacteristic;
    
    private OperateState mOperateState = OperateState.INIT;

    public BleWriteOperate(BluetoothGattCharacteristic mCharacteristic) {
        super();
        this.mCharacteristic = mCharacteristic;
        mOperateState = OperateState.INIT;
    }
    
    public boolean hasHandled(){
        return mOperateState == OperateState.RUNNED_SUCCESS || mOperateState == OperateState.RUNNED_FAIL;
    }
    
    public boolean isRunning(){
        return mOperateState == OperateState.RUNNING;
    }
    
    public boolean isPending(){
        return mOperateState == OperateState.PENDING;
    }

    public OperateState getmOperateState() {
        return mOperateState;
    }

    public void setmOperateState(OperateState mOperateState) {
        this.mOperateState = mOperateState;
    }

    public BluetoothGattCharacteristic getmCharacteristic() {
        return mCharacteristic;
    }
    
    
}
