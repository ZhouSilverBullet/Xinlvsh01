package com.huichenghe.bleControl.Ble;

import android.bluetooth.BluetoothGattCharacteristic;

class BleWriteTypeOperate extends BleWriteOperate {

	private final int mValue;
	
	private final int mFormatType;
	
	private final int mOffset;
	
	public BleWriteTypeOperate(int value, int formatType, int offset,BluetoothGattCharacteristic mCharacteristic) {
		super(mCharacteristic);
		mValue = value;
		mFormatType = formatType;
		mOffset = offset;
	}

	public int getmValue() {
		return mValue;
	}

	public int getmFormatType() {
		return mFormatType;
	}

	public int getmOffset() {
		return mOffset;
	}

}
