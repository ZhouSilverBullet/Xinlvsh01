/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huichenghe.bleControl.Ble;

import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.huichenghe.bleControl.Utils.FormatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

//import com.huichenghe.xinlvsh01.ForgroundService;
//import com.huichenghe.xinlvsh01.slide.settinga.NotificationSendHelper;

public class BluetoothLeService extends Service{
    private final static String TAGBLE = BluetoothLeService.class.getSimpleName();

    public static final long DEFAULT_DELAY_TIME = 120;
    private Notification.Builder notificatains;
    private static final boolean NeedAutoPair = false;
    private static final boolean NeedReadRssi = false;
    private BoundReceiver mReceiver;
    private LocalDeviceEntity deviceEn;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private final int REFRESH_NOTIFICATION = 0;
    private boolean mOptConnected = true;
    private List<IServiceCallback> mServiceCallbacks = new ArrayList<IServiceCallback>();
    private Vector<BleWriteOperate> mPendingWriteOperates = new Vector<BleWriteOperate>();
    private final Object mPendingLock = new Object();
    private Handler blueHandler = new Handler(Looper.getMainLooper()){};
    
    public static void printCharacteristicProperty(BluetoothGattCharacteristic characteristic){
        if(characteristic == null){
            Log.e(TAGBLE, "checkCharacteristicProperty fail");
            return;
        }
        int property = characteristic.getProperties();
    }
    
    
    public synchronized void writeCharacteristic(final BluetoothGattCharacteristic characteristic, final WriteCallBack callback)
    {
    	if(isRunOnUIThread()){
    		if(mBluetoothGatt != null){
    			if(characteristic != null){
    				boolean result2 = mBluetoothGatt.writeCharacteristic(characteristic);
    				if(callback != null){
    					callback.onWrite(true);
    				}
    			}else
                {
    				Log.e(TAGBLE, "characteristic is null");
    				if(callback != null){
    					callback.onWrite(false);
    				}
    			}
    		}else{
    			Log.e(TAGBLE, "mBluetoothGatt is null");
    			if(callback != null){
    				callback.onWrite(false);
    			}
    		}
    	}else{
    		mHandler.post(new Runnable() {
    			
    			@Override
    			public void run() {
    				writeCharacteristic(characteristic, callback);
    			}
    		});
    	}
    }
    
    private synchronized void writePendingValue(final byte[] value, final BluetoothGattCharacteristic characteristic)
    {
    	if(isRunOnUIThread()){
    		if(mBluetoothGatt != null){
                if(characteristic != null){
                    synchronized (mPendingLock) {
                        if(mPendingWriteOperates.isEmpty()){
                            BleWriteByteArrayOperate operate = new BleWriteByteArrayOperate(value, characteristic);
                            mPendingWriteOperates.add(operate);
                            characteristic.setValue(value);
                            operate.setmOperateState(OperateState.RUNNING);
                            Log.w(TAGBLE, String.format("writePendingValue[%d] +++dowrite, by user empty pengdinglist", mPendingWriteOperates.size()));
                            mBluetoothGatt.writeCharacteristic(characteristic);
//                            Log.e(String.format("writePendingValue[%d] +++fail", mPendingWriteOperates.size()));
//                            operate.setmOperateState(OperateState.RUNNED_FAIL);
//                            mPendingWriteOperates.remove(operate);
                        }else{
                            int length = mPendingWriteOperates.size();
                            boolean haveRunningOpeate = false;
                            for (int i = 0; i < length; i++) {
                            	BleWriteOperate o = mPendingWriteOperates.get(i);
                                if(o.getmOperateState() == OperateState.RUNNING){
                                    haveRunningOpeate = true;
                                    break;
                                }
                            }
                            BleWriteByteArrayOperate now = new BleWriteByteArrayOperate(value, characteristic);
                            mPendingWriteOperates.add(now);
                            now.setmOperateState(OperateState.PENDING);
                            if(haveRunningOpeate){
                            	Log.i(TAGBLE, String.format("writePendingValue[%d] +++have running operate, add operate to pendinglist", mPendingWriteOperates.size()));
                            }else{
                            	BleWriteOperate operate = mPendingWriteOperates.get(0);
                                if(operate.getmCharacteristic() != null){
                                	if(operate instanceof BleWriteByteArrayOperate){
                                		operate.getmCharacteristic().setValue(((BleWriteByteArrayOperate)operate).getmValue());
                                		Log.w(TAGBLE, String.format("writePendingValue[%d] +++dowrite, by user", mPendingWriteOperates.size()));
                                		operate.setmOperateState(OperateState.RUNNING);
                                		mBluetoothGatt.writeCharacteristic(operate.getmCharacteristic());
                                	}else if(operate instanceof BleWriteTypeOperate){
                                		BleWriteTypeOperate writeTypeOpe = (BleWriteTypeOperate)operate;
                                		operate.getmCharacteristic().setValue(writeTypeOpe.getmValue(), writeTypeOpe.getmFormatType(), writeTypeOpe.getmOffset());
                                		Log.w(TAGBLE, String.format("writePendingValue[%d] +++dowrite, by user", mPendingWriteOperates.size()));
                                		operate.setmOperateState(OperateState.RUNNING);
                                		mBluetoothGatt.writeCharacteristic(operate.getmCharacteristic());
                                		
                                	}
//                                    if(result & result2){
//                                    	
//                                    }else{
//                                        Log.e("writePendingValue +++fail2");
//                                        operate.setmOperateState(OperateState.RUNNED_FAIL);
//                                        mPendingWriteOperates.remove(operate);
//                                    }
                                }else{
                                    Log.e(TAGBLE, "writePendingValue +++fail3");
                                    operate.setmOperateState(OperateState.RUNNED_FAIL);
                                    mPendingWriteOperates.remove(operate);
                                }
                            }
                        }
                    }
                }else{
                    Log.e(TAGBLE, "characteristic is null");
                }
            }else{
                Log.e(TAGBLE, "mBluetoothGatt is null");
            }
    	}else{
    		mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					writePendingValue(value, characteristic);
				}
			});
    	}
        
    }
    
    public synchronized void writePendingValue
            (final int value, final int formatType, final int offset, final BluetoothGattCharacteristic characteristic)
    {
    	if(isRunOnUIThread()){
    		if(mBluetoothGatt != null){
                if(characteristic != null){
                    synchronized (mPendingLock) {
                        
                        if(mPendingWriteOperates.isEmpty()){
                            BleWriteTypeOperate operate = new BleWriteTypeOperate(value,formatType, offset, characteristic);
                            mPendingWriteOperates.add(operate);
                            characteristic.setValue(value, formatType, offset);
                            operate.setmOperateState(OperateState.RUNNING);
                            Log.w(TAGBLE, String.format("writePendingValue[%d] +++dowrite, by user empty pengdinglist", mPendingWriteOperates.size()));
                            mBluetoothGatt.writeCharacteristic(characteristic);
//                            Log.e(String.format("writePendingValue[%d] +++fail", mPendingWriteOperates.size()));
//                            operate.setmOperateState(OperateState.RUNNED_FAIL);
//                            mPendingWriteOperates.remove(operate);
                        }else{
                            int length = mPendingWriteOperates.size();
                            boolean haveRunningOpeate = false;
                            for (int i = 0; i < length; i++) {
                            	BleWriteOperate o = mPendingWriteOperates.get(i);
                                if(o.getmOperateState() == OperateState.RUNNING){
                                    haveRunningOpeate = true;
                                    break;
                                }
                            }
                            BleWriteTypeOperate now = new BleWriteTypeOperate(value,formatType, offset, characteristic);
                            mPendingWriteOperates.add(now);
                            now.setmOperateState(OperateState.PENDING);
                            if(haveRunningOpeate){
                            	Log.i(TAGBLE, String.format("writePendingValue[%d] +++have running operate, add operate to pendinglist", mPendingWriteOperates.size()));
                            }else{
                            	BleWriteOperate operate = mPendingWriteOperates.get(0);
                                if(operate.getmCharacteristic() != null){
                                	if(operate instanceof BleWriteByteArrayOperate){
                                		operate.getmCharacteristic().setValue(((BleWriteByteArrayOperate)operate).getmValue());
                                		Log.w(TAGBLE, String.format("writePendingValue[%d] +++dowrite, by user", mPendingWriteOperates.size()));
                                		operate.setmOperateState(OperateState.RUNNING);
                                		mBluetoothGatt.writeCharacteristic(operate.getmCharacteristic());
                                	}else if(operate instanceof BleWriteTypeOperate){
                                		BleWriteTypeOperate writeTypeOpe = (BleWriteTypeOperate)operate;
                                		operate.getmCharacteristic().setValue(writeTypeOpe.getmValue(), writeTypeOpe.getmFormatType(), writeTypeOpe.getmOffset());
                                		Log.w(TAGBLE, String.format("writePendingValue[%d] +++dowrite, by user", mPendingWriteOperates.size()));
                                		operate.setmOperateState(OperateState.RUNNING);
                                		mBluetoothGatt.writeCharacteristic(operate.getmCharacteristic());
                                		
                                	}
//                                    if(result & result2){
//                                    	
//                                    }else{
//                                        Log.e("writePendingValue +++fail2");
//                                        operate.setmOperateState(OperateState.RUNNED_FAIL);
//                                        mPendingWriteOperates.remove(operate);
//                                    }
                                }else{
                                    Log.e(TAGBLE, "writePendingValue +++fail3");
                                    operate.setmOperateState(OperateState.RUNNED_FAIL);
                                    mPendingWriteOperates.remove(operate);
                                }
                            }
                        }
                    }
                }else{
                    Log.e(TAGBLE, "characteristic is null");
                }
            }else{
                Log.e(TAGBLE, "mBluetoothGatt is null");
            }
    	}else{
    		mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					writePendingValue(value, formatType, offset, characteristic);
				}
			});
    	}
        
    }
    
    public interface WriteCallBack{
        void onWrite(boolean result);
    }


    /**
     * 向手环手表发送数据
     * @param value
     * @param characteristic
     * @param callback
     */
    public void writeDelayValue(final byte[] value, final BluetoothGattCharacteristic characteristic, final WriteCallBack callback)
    {
//        if(isRunOnUIThread()) {
            if(mBluetoothGatt != null){
        if(characteristic != null){
            characteristic.setValue(value);
            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            boolean already = mBluetoothGatt.writeCharacteristic(characteristic);
            Log.i(TAGBLE, "发送:"+ FormatUtils.bytesToHexString(value) + "长度:" + value.length + "成功：" + already);
            Log.i(TAGBLE, "writeDelayValue setValue done:"+ FormatUtils.bytesToHexString(value) + "长度:" + value.length + "成功：" + already);
            if(callback != null){
                callback.onWrite(already);
            }
        }else{
            Log.e(TAGBLE, "writeDelayValue() characteristic is null");
            if(callback != null){
                callback.onWrite(false);
            }
        }
    }else{
        Log.e(TAGBLE, "writeDelayValue() setValue fail:"+FormatUtils.bytesToHexString(value));
        if(callback != null){
            callback.onWrite(false);
        }
    }
            
//        }else{
//        	Log.i("writeDelayValue()---begin---phone");
//            mHandler.post(new Runnable() {
//                
//                @Override
//                public void run() {
//                    writeDelayValue(value, characteristic, callback);
//                }
//            });
//        }
    }
    
    public void writeDelayValue(final int value, final int formatType, final int offset, final BluetoothGattCharacteristic characteristic, final WriteCallBack callback)
    {
//        if(isRunOnUIThread()){
            if(mBluetoothGatt != null){
                if(characteristic != null){
                    boolean result = characteristic.setValue(value, formatType, offset);
                    boolean result2 = mBluetoothGatt.writeCharacteristic(characteristic);
                    Log.i(TAGBLE, "writeDelayValue2 setValue done value:" + value);
                    if(callback != null){
                        callback.onWrite(true);
                    }
                }else{
                    Log.e(TAGBLE, "characteristic is null");
                    if(callback != null){
                        callback.onWrite(false);
                    }
                }
            }else{
                Log.e(TAGBLE, "writeDelayValue2 setValue fail value:" + value);
                if(callback != null){
                    callback.onWrite(false);
                }
            }
            
//        }else{
//            mHandler.post(new Runnable() {
//                
//                @Override
//                public void run() {
//                    writeDelayValue(value, formatType, offset, characteristic, callback);
//                }
//            });
//        }
    }
    
    private void WriteValue(final BluetoothGattCharacteristic characteristic, final WriteCallBack callback)
    {
    	if(isRunOnUIThread()){
    		if(mBluetoothGatt != null){
    			if(characteristic != null){
    				boolean result2 = mBluetoothGatt.writeCharacteristic(characteristic);
    				if(callback != null){
    					callback.onWrite(true);
    				}
    			}else{
    				Log.e(TAGBLE, "characteristic is null");
    				if(callback != null){
    					callback.onWrite(false);
    				}
    			}
    		}else{
    			Log.e(TAGBLE, "mBluetoothGatt is null");
    			if(callback != null){
    				callback.onWrite(false);
    			}
    		}
    		
    	}else{
    		mHandler.post(new Runnable() {
    			
    			@Override
    			public void run() {
    				WriteValue(characteristic, callback);
    			}
    		});
    	}
    }
    
    public boolean isConnectedDevice(){
        if(mBluetoothGatt == null){
            return false;
        }else{
            LocalDeviceEntity device = Engine.getInstance().getDeviceFromGatt(mBluetoothGatt);
                return device != null && isDeviceConnected(device);
        }
        
    }
    
    public List<BluetoothDevice> getConnectedDevices(){
        if(mBluetoothManager == null){
            return null;
        }else{
            return mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT_SERVER);
        }
    }
    
    public boolean isDeviceConnected(LocalDeviceEntity device){
        List<BluetoothDevice> devices = getConnectedDevices();
        if(device == null || devices == null || devices.isEmpty()){
            return false;
        }
        int size = devices.size();
        for (int i = 0; i < size; i++) {
            BluetoothDevice bluetoothDevice = devices.get(i);
            if(device.getAddress().equals(bluetoothDevice.getAddress())){
                return true;
            }
        }
        return false;
    }
    
    private boolean isConnectingDevice = false;
    public boolean isConnectingDevice(){
        return isConnectingDevice;
    }
    
    private boolean isDiscoveringService = false;
    
    public boolean isDiscoveringService(){
        return isDiscoveringService;
    }
    
    private synchronized void readDeviceRssi(){
        mHandler.postDelayed(new Runnable() {
            
            @Override
            public void run() {
                if(isServiceRunning && isConnectedDevice()){
                	Log.d(TAGBLE, "readDeviceRssi");
                    mBluetoothGatt.readRemoteRssi();
                    readDeviceRssi();
                }else{
                    Log.e(TAGBLE, "readDeviceRssi fail, service stop");
                }
            }
        }, 1000);
    }
    
    private synchronized void readDevicePower(final UUID serviceuuid, final UUID batteryuuid){
        if(isRunOnUIThread()){
            if(isServiceRunning && isConnectedDevice()){
                BluetoothGattService batteryservice = mBluetoothGatt.getService(serviceuuid);
                if(batteryservice != null){
                    BluetoothGattCharacteristic characteristic = batteryservice.getCharacteristic(batteryuuid);
                    if(characteristic != null){
                        Log.i(TAGBLE, "<<start read device power>>");
                        readCharacteristic(characteristic);
                    }else{
                        Log.e(TAGBLE, "readDevicePower fail, characteristic null");
                    }
                }else{
                    Log.e(TAGBLE, "readDevicePower fail, battery service null");
                }
            }else{
                Log.e(TAGBLE, "readDevicePower fail, mBluetoothGatt null");
            }
            
        }else{
            mHandler.post(new Runnable() {
                
                @Override
                public void run() {
                    readDevicePower(serviceuuid, batteryuuid);
                }
            });
        }
    }
    
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback()
    {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
        {
        	boolean success = (status == BluetoothGatt.GATT_SUCCESS);
            Log.w(TAGBLE, "onConnectionStateChange success=" + success + " NewStates=" + newState + "status" + status);
            LocalDeviceEntity device = Engine.getInstance().getDeviceFromGatt(gatt);
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                if (newState == BluetoothProfile.STATE_CONNECTED)
                {
                    Log.w(TAGBLE, "Connected to GATT server.");
                    isConnectingDevice = false;
                    List<BluetoothDevice> devicesList = mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT_SERVER);
                    if(devicesList != null && !devicesList.isEmpty())
                    {
                        for (int i = 0; i < devicesList.size(); i++)
                        {
                            Log.i(TAGBLE, "@@11connected device:"+devicesList.get(i).getName());
                        }
                    }
                    if (!mServiceCallbacks.isEmpty())
                    {
                        int size = mServiceCallbacks.size();
                        for (int i = 0; i < size; i++)
                        {
                            IServiceCallback callback = mServiceCallbacks.get(i);
                            callback.onBLEDeviceConnected(device, gatt);
                        }
                    }
                    if(NeedReadRssi){
                    	readDeviceRssi();
                    }
                    // Attempts to discover services after successful
                    // connection.

                    final BluetoothGatt finalGatt = gatt;
                    mHandler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            boolean result = finalGatt.discoverServices();//bluetoothAdapter.cancelDiscovery();可能会对此方法有影响,导致找不到服务
                            isDiscoveringService = result;              //true
                            Log.w(TAGBLE, "Attempting to start service discovery:" + result);
                        }
                    }, 300);
                }
                else if (newState == BluetoothProfile.STATE_DISCONNECTED)
                {
//                    if(gatt.connect()){//是否可以做为可连接设备？？？
//                    Log.w("Disconnected from GATT server. reconnect it");
                        //TODO 参照BLEEventBus
//                        List<BluetoothDevice> devicesList = mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT_SERVER);
//                        if(devicesList != null && !devicesList.isEmpty()){
//                            for (int i = 0; i < devicesList.size(); i++) {
//                                Log.i("after reconnect @@22connected device:"+devicesList.get(i).getName());
//                            }
//                        }
//                    }else{
//                        if(gatt != null){
//                            Engine.getInstance().removeGattFromDeviceMap(gatt);
//                            gatt.disconnect();
//                            gatt.close();
//                            gatt = null;
//                        }
//                    Log.w("Disconnected from GATT server.");
                        isConnectingDevice = true;
                        isDiscoveringService = false;
                        
                        if (mOptConnected) {
	                        if (!mServiceCallbacks.isEmpty()) {
	                            int size = mServiceCallbacks.size();
	                            for (int i = 0; i < size; i++) {
	                                IServiceCallback callback = mServiceCallbacks.get(i);
	                                callback.onBLEDeviceDisConnected(device, gatt);
	                            }
	                        }
	                        
	                        Log.w(TAGBLE, "Disconnected from GATT server. reconnect it, mOptConnected="+mOptConnected);
	                        connect(device);
                        }
//                    }
                }else if(newState == BluetoothProfile.STATE_CONNECTING){
                    isConnectingDevice = true;
                    Log.w(TAGBLE, "connecting to device");
                }
            }
            else
            {
                Log.w(TAGBLE, "connect device error" + gatt.getDevice().getAddress());
                isConnectingDevice = false;
                isDiscoveringService = false;
                if(gatt != null)
                {
                    synchronized (Engine.getInstance()) {
                        Engine.getInstance().removeGattFromDeviceMap(gatt);
                        mPendingWriteOperates.clear();
                        gatt.disconnect();
                        gatt.close();
                        gatt = null;
                        Log.w(TAGBLE, "close gatt");
                    }
                }
                if (!mServiceCallbacks.isEmpty()) {
                    int size = mServiceCallbacks.size();
                    for (int i = 0; i < size; i++) {
                        IServiceCallback callback = mServiceCallbacks.get(i);
                        callback.onBLEDeviceConnecError(device,				//设备 
                        								true,				//是否显示toast
                        								true);				//是否是主动断开
                    }
                }
                connectTheSaveDevice(true);
            }
        }


        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status)
        {
            super.onMtuChanged(gatt, mtu, status);
            if(status == BluetoothGatt.GATT_SUCCESS)
            {
                for (int i = 0; i < mServiceCallbacks.size(); i++)
                {
                    mServiceCallbacks.get(i).onMTUChange(gatt, mtu, status);
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            LocalDeviceEntity device = Engine.getInstance().getDeviceFromGatt(gatt);
            isDiscoveringService = false;
            boolean success = (status == BluetoothGatt.GATT_SUCCESS);
            Log.i(TAGBLE, "onServicesDiscovered success: " + success);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if(gatt.getServices() != null ){
                    notifyAndSendBrocast(gatt.getServices(), gatt, device);
                    if (!mServiceCallbacks.isEmpty()) {
                        int size = mServiceCallbacks.size();
                        for (int i = 0; i < size; i++) {
                            IServiceCallback callback = mServiceCallbacks.get(i);
                            callback.onBLEServiceFound(device, gatt, gatt.getServices());
                        }
//                        blueHandler.sendEmptyMessageDelayed(REFRESH_NOTIFICATION, 30 * 1000);
                    }
                }else{
                    if (!mServiceCallbacks.isEmpty()) {
                        int size = mServiceCallbacks.size();
                        for (int i = 0; i < size; i++) {
                            IServiceCallback callback = mServiceCallbacks.get(i);
                            callback.onNoBLEServiceFound();
                        }
                    } 
                }
            } else {
                Log.i(TAGBLE, "onServicesDiscovered success: 失败" + status);
                if (!mServiceCallbacks.isEmpty()) {
                    int size = mServiceCallbacks.size();
                    for (int i = 0; i < size; i++) {
                        IServiceCallback callback = mServiceCallbacks.get(i);
                        callback.onNoBLEServiceFound();
                    }
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic,
                int status) {
        	boolean success = (status == BluetoothGatt.GATT_SUCCESS);
        	byte[] value = characteristic.getValue();
            Log.i(TAGBLE, "onCharacteristicRead success: " + success + " value:" + FormatUtils.bytesToHexString(value));
            LocalDeviceEntity device = Engine.getInstance().getDeviceFromGatt(gatt);
            if (!mServiceCallbacks.isEmpty()) {
            	int size = mServiceCallbacks.size();
            	for (int i = 0; i < size; i++) {
            		IServiceCallback callback = mServiceCallbacks.get(i);
            		callback.onCharacteristicRead(device, gatt, characteristic, success);
            	}
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic) {
        	final byte[] value = characteristic.getValue();
        	final String uuid = characteristic.getUuid().toString();
            Log.i(TAGBLE, "onCharacteristicChanged receive the notify value:" + FormatUtils.bytesToHexString(value));
            LocalDeviceEntity device = Engine.getInstance().getDeviceFromGatt(gatt);
            if (!mServiceCallbacks.isEmpty()) {
                int size = mServiceCallbacks.size();
                for (int i = 0; i < size; i++) {
                    IServiceCallback callback = mServiceCallbacks.get(i);
                    callback.onCharacteristicChanged(device, gatt, uuid, value);
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            LocalDeviceEntity device = Engine.getInstance().getDeviceFromGatt(gatt);
            synchronized (mPendingLock) {
                if(!mPendingWriteOperates.isEmpty()){
	                    int length = mPendingWriteOperates.size();
//	                    boolean haveRunningOpeate = false;
//	                    int index = -clock;
	                    for (int i = 0; i < length; i++) {
	                        BleWriteOperate o = mPendingWriteOperates.get(i);
	                        if(o.getmOperateState() == OperateState.RUNNING){
//	                            haveRunningOpeate = true;
//	                            index = i;
	                            mPendingWriteOperates.remove(o);
	                            Log.w(TAGBLE, String.format("onCharacteristicWrite[%d] done", mPendingWriteOperates.size()));
	                            break;
	                        }
	                    }
//	                    if(haveRunningOpeate && index != -clock){
//	                        BleWriteOperate operate = mPendingWriteOperates.get(index);
//	                        operate.setmOperateState(status == BluetoothGatt.GATT_SUCCESS ? OperateState.RUNNED_SUCCESS : OperateState.RUNNED_FAIL);
//	                    }
                    
                }else{
                    Log.e(TAGBLE, "onCharacteristicWrite +++mPendingWriteOperates is empry");
                }
            }
            
            byte[] value = characteristic.getValue();
            boolean success = (status == BluetoothGatt.GATT_SUCCESS);
            Log.w(TAGBLE, "onCharacteristicWrite success: " + success + " value:" + FormatUtils.bytesToHexString(value));
            if (!mServiceCallbacks.isEmpty()) {
            	int size = mServiceCallbacks.size();
            	
            	for (int i = 0; i < size; i++) {
            		IServiceCallback callback = mServiceCallbacks.get(i);
            		callback.onCharacteristicWrite(device, gatt, characteristic, success);
            	}
            }
            autoRunNext();
        }

        private void autoRunNext() {
        	if(!mPendingWriteOperates.isEmpty()){
                final BleWriteOperate operate = mPendingWriteOperates.get(0);
                Log.d(TAGBLE, "autoRunNext operate:" + operate.getmOperateState());
                if(operate.isPending()){
                	Log.i(TAGBLE, String.format("autoRunNext[%d] auto run next", mPendingWriteOperates.size()));
                	mHandler.post(new Runnable() {
                		
                		@Override
                		public void run() {
                			synchronized (mPendingLock) {
                				if(operate.getmCharacteristic() != null){
                					if(operate instanceof BleWriteByteArrayOperate){
                						operate.getmCharacteristic().setValue(((BleWriteByteArrayOperate)operate).getmValue());
                                		Log.w(TAGBLE, String.format("autoRunNext[%d] +++dowrite, auto", mPendingWriteOperates.size()));
                                		operate.setmOperateState(OperateState.RUNNING);
                                		mBluetoothGatt.writeCharacteristic(operate.getmCharacteristic());
                                	}else if(operate instanceof BleWriteTypeOperate){
                                		BleWriteTypeOperate writeTypeOpe = (BleWriteTypeOperate)operate;
                                		operate.getmCharacteristic().setValue(writeTypeOpe.getmValue(), writeTypeOpe.getmFormatType(), writeTypeOpe.getmOffset());
                                		Log.w(TAGBLE, String.format("autoRunNext[%d] +++dowrite, auto", mPendingWriteOperates.size()));
                                		operate.setmOperateState(OperateState.RUNNING);
                                		mBluetoothGatt.writeCharacteristic(operate.getmCharacteristic());
                                		
                                	}
//                					Log.w(String.format("autoRunNext[%d] +++dowrite, auto", mPendingWriteOperates.size()));
//                					boolean result2 = mBluetoothGatt.writeCharacteristic(operate.getmCharacteristic());
//                					if(result & result2){
//                						
//                					}else{
//                						Log.i(String.format("autoRunNext[%d] +++fail2", mPendingWriteOperates.size()));
//                						operate.setmOperateState(OperateState.RUNNED_FAIL);
//                						mPendingWriteOperates.remove(operate);
//                					}
                				}else{
                					Log.i(TAGBLE, String.format("autoRunNext[%d] +++fail3", mPendingWriteOperates.size()));
                					operate.setmOperateState(OperateState.RUNNED_FAIL);
                					mPendingWriteOperates.remove(operate);
                				}
                			}
                		}
                	});
                }else if(operate.isRunning()){
                	Log.i(TAGBLE, String.format("autoRunNext[%d] +++but next opearte is running", mPendingWriteOperates.size()));
                }else if(operate.hasHandled()){
                	Log.i(TAGBLE, String.format("autoRunNext[%d] +++but next opearte have handled", mPendingWriteOperates.size()));
                	mPendingWriteOperates.remove(operate);
                	autoRunNext();
                }else{
                	Log.e(TAGBLE, String.format("autoRunNext[%d] +++ignore this one auto run next", mPendingWriteOperates.size()));
                	mPendingWriteOperates.remove(operate);
                	autoRunNext();
                }
            }else{
            	Log.e(TAGBLE, String.format("autoRunNext[%d] +++pending list empty, no next", mPendingWriteOperates.size()));
            }
			
		}

		@Override
        public void onDescriptorRead(BluetoothGatt gatt,
                BluetoothGattDescriptor bd,
                int status) {
			boolean success = (status == BluetoothGatt.GATT_SUCCESS);
            Log.w(TAGBLE, "onDescriptorRead success: " + success);
            LocalDeviceEntity device = Engine.getInstance().getDeviceFromGatt(gatt);
            if (!mServiceCallbacks.isEmpty()) {
            	int size = mServiceCallbacks.size();
            	for (int i = 0; i < size; i++) {
            		IServiceCallback callback = mServiceCallbacks.get(i);
            		callback.onDescriptorRead(device, gatt, bd, success);
            	}
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                BluetoothGattDescriptor bd,
                int status) {
        	boolean success = (status == BluetoothGatt.GATT_SUCCESS);
            Log.w(TAGBLE, "onDescriptorWrite success: " + success);
            LocalDeviceEntity device = Engine.getInstance().getDeviceFromGatt(gatt);
            if (!mServiceCallbacks.isEmpty()) {
            	int size = mServiceCallbacks.size();
            	for (int i = 0; i < size; i++) {
            		IServiceCallback callback = mServiceCallbacks.get(i);
            		callback.onDescriptorWrite(device, gatt, bd, success);
            	}
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status)
        {
        	boolean success = (status == BluetoothGatt.GATT_SUCCESS);
            Log.d(TAGBLE, "onReadRemoteRssi success: " + success);
            LocalDeviceEntity device = Engine.getInstance().getDeviceFromGatt(gatt);
            if (!mServiceCallbacks.isEmpty()) {
            	int size = mServiceCallbacks.size();
            	for (int i = 0; i < size; i++) {
            		IServiceCallback callback = mServiceCallbacks.get(i);
            		callback.onReadRemoteRssi(device, gatt, rssi, success);
            	}
            }
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status)
        {
        	boolean success = (status == BluetoothGatt.GATT_SUCCESS);
            Log.w(TAGBLE, "onReliableWriteCompleted success: " + success);
            LocalDeviceEntity device = Engine.getInstance().getDeviceFromGatt(gatt);
            if (!mServiceCallbacks.isEmpty()) {
            	int size = mServiceCallbacks.size();
            	for (int i = 0; i < size; i++)
                {
            		IServiceCallback callback = mServiceCallbacks.get(i);
            		callback.onReliableWriteCompleted(device, gatt, success);
            	}
            }
        }

    };

    private void notifyAndSendBrocast(final List<BluetoothGattService> list, final BluetoothGatt gatt, final LocalDeviceEntity device)
    {
          if (list != null)
          {
                if(BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice())
                {
                    blueHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            setBLENotify(gatt, true, true);
                        }
                    });
                }
          }

          Intent intent = new Intent();
          intent.putExtra("DEVICE_OK_INFO", device);
          intent.setAction(DeviceConfig.DEVICE_CONNECTE_AND_NOTIFY_SUCESSFUL);
          this.getApplicationContext().sendBroadcast(intent);

    }

    public void addCallback(IServiceCallback callback)
    {
        if(!mServiceCallbacks.contains(callback)){
            mServiceCallbacks.add(callback);
        }
    }
    
    public void removeCallback(IServiceCallback callback)
    {
        if(mServiceCallbacks.contains(callback)){
            mServiceCallbacks.remove(callback);
        }
    }

    public void removeAllCallback()
    {
        mServiceCallbacks.clear();
    }


    public class LocalBinder extends Binder
    {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     * 
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter
        // through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAGBLE, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAGBLE, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        isConnectingDevice = false;
        isDiscoveringService = false;
        return true;
    }


    public LocalDeviceEntity getCurrentDevice()
    {
        return deviceEn;
    }

    public synchronized boolean connect(LocalDeviceEntity device)
    {
        sendStartConnectBracost();
        this.deviceEn = device;
//    	Exception debug = new Exception();
//    	debug.printStackTrace();
    	mOptConnected = true;
    	
        if (mBluetoothAdapter == null || device == null || TextUtils.isEmpty(device.getAddress()))
        {
            Log.w(TAGBLE, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        /*
         * // Previously connected device. Try to reconnect. if
         * (mBluetoothDeviceAddress != null &&
         * address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
         * Log.d(TAG,
         * "Trying to use an existing mBluetoothGatt for connection."); if
         * (mBluetoothGatt.connect()) { mConnectionState = STATE_CONNECTING;
         * return true; } else { return false; } }
         */
        BluetoothDevice d = mBluetoothAdapter.getRemoteDevice(device.getAddress());
        if (device == null) {
            Log.w(TAGBLE, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the
        // autoConnect
        // parameter to false.
        if (mBluetoothGatt != null)
        {
            Log.w(TAGBLE, "close last BluetoothGatt");
            synchronized (Engine.getInstance()) {
                Engine.getInstance().removeGattFromDeviceMap(mBluetoothGatt);
                mPendingWriteOperates.clear();
                mBluetoothGatt.disconnect();
                mBluetoothGatt.close();
                mBluetoothGatt = null;
            }
        }
        if(NeedAutoPair){
            boolean result = BluetoothAutoPair.pair(d, "1234");
            Log.w(TAGBLE, "auto pair:" + result);
        }
        boolean hasBouded = (d.getBondState() == BluetoothDevice.BOND_BONDED);
        Log.i(TAGBLE, " +++++connect+++++" + device.getName() + " have bond:" + hasBouded);
        isConnectingDevice = true;
        isDiscoveringService = false;
        if(!hasBouded && NeedAutoPair){
            Log.w(TAGBLE, "device have not bound,wait for bounded");
            if(mReceiver != null){
                mReceiver.setTargetDevice(device);
            }
        }else{
            if(mReceiver != null){
                mReceiver.setTargetDevice(device);
            }
            synchronized (Engine.getInstance()) {
                mBluetoothGatt = d.connectGatt(this, false, mGattCallback);
                Engine.getInstance().addGatt2DeviceMap(mBluetoothGatt, device);
                Log.i(TAGBLE, "进行连接Ble设备" + d.getAddress());
            }
        }
//         mBluetoothGatt.connect();
        return true;
    }

    private void sendStartConnectBracost()
    {
        Intent intent = new Intent();
        intent.setAction(DeviceConfig.DEVICE_CONNECTING_AUTO);
        sendBroadcast(intent);
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The
     * disconnection result is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public synchronized void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null)
        {
            Log.w(TAGBLE, "disconnect fail, BluetoothAdapter not initialized");
            return;
        }
        Log.i(TAGBLE, "disconnect an existing connection or cancel a pending connection  mOptConnected = false");
        mOptConnected = false;
        mBluetoothGatt.disconnect();
    }

    public void setBLENotify(BluetoothGatt gatt, boolean isOpenFFF0, boolean isOpen2a37)
    {
//        Log.e(TAGBLE,  "setBLENotify  -bdoChecked; isOpen= "+isOpen);
//        Log.e(TAGBLE,  "setBLENotify  -bdoChecked; mBLENotifyOpened= "+mBLENotifyOpened);
        if (gatt == null)
        {
            BluetoothLeService serviceMain = getInstance();
            if (serviceMain == null)
            {
                Log.e(TAGBLE,  "writeDelayValue  e1");
                return;
            }

            gatt = serviceMain.getBluetoothGatt();
            if (gatt == null)
            {
                Log.e(TAGBLE,  "writeDelayValue  e2");
                return;
            }
        }
        Log.e(TAGBLE,  "setBLENotify  -bdoChecked; BluetoothAdapter.getDefaultAdapter().isEnabled()= "
                +BluetoothAdapter.getDefaultAdapter().isEnabled());
//        mBLENotifyOpened = isOpen;

        if (BluetoothAdapter.getDefaultAdapter().isEnabled())
        {
                BluetoothGattService main = gatt
                        .getService(DeviceConfig.MAIN_SERVICE_UUID);

                Log.e(TAGBLE,  "onBLEServiceFound doChecked main="+main);
                if (main != null) {
                    try {
                        BluetoothGattCharacteristic characteristic = main
                                .getCharacteristic(DeviceConfig.UUID_CHARACTERISTIC_NOTIFY);
                        BluetoothLeService.printCharacteristicProperty(characteristic);
                        boolean isTrue = BluetoothLeService.getInstance().setCharacteristicNotification(characteristic, isOpenFFF0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            try
            {
                Thread.sleep(300);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
                BluetoothGattService hrate = gatt
                        .getService(DeviceConfig.HEARTRATE_SERVICE_UUID);

                Log.e(TAGBLE,  "onBLEServiceFound doChecked hrate="+hrate);
                if(null != hrate)
                {
                    BluetoothGattCharacteristic characteristic =
                            hrate.getCharacteristic(DeviceConfig.HEARTRATE_FOR_TIRED_NOTIFY);
                    boolean isHrOk = BluetoothLeService.getInstance().setCharacteristicNotification(characteristic, isOpen2a37);
                }

            try
            {
                Thread.sleep(300);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        } else {
            Log.e(TAGBLE, "have  found service, but bt have disabled  doChecked");
        }
    }

    public void setHrNotify(BluetoothGatt gatt, boolean isOpen)
    {
        if (gatt == null)
        {
            BluetoothLeService serviceMain = getInstance();
            if (serviceMain == null)
            {
                Log.e(TAGBLE,  "writeDelayValue  e1");
                return;
            }

            gatt = serviceMain.getBluetoothGatt();
            if (gatt == null)
            {
                Log.e(TAGBLE,  "writeDelayValue  e2");
                return;
            }
        }

        BluetoothGattService hrate = gatt
                .getService(DeviceConfig.HEARTRATE_SERVICE_UUID);

        Log.e(TAGBLE,  "onBLEServiceFound doChecked hrate="+hrate);
        if(null != hrate)
        {
            BluetoothGattCharacteristic characteristic =
                    hrate.getCharacteristic(DeviceConfig.HEARTRATE_FOR_TIRED_NOTIFY);
            boolean isHrOk = BluetoothLeService.getInstance().setCharacteristicNotification(characteristic, isOpen);
            Log.e(TAGBLE, "onBLEServiceFound characteristic=" + characteristic.getUuid().toString());
        }
        try
        {
            Thread.sleep(300);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }



    private void connectTheSaveDevice(boolean isConnectWhere)
    {

        this.sendBroadcast(new Intent("recever_need_connect_save_device"));
        /**
         *  if(!BluetoothAdapter.getDefaultAdapter().isEnabled())return;
         if(BluetoothLeService.getInstance() != null && !BluetoothLeService.getInstance().isConnectedDevice())
         {
         String deviceName = LocalDataSaveTool.getInstance(BluetoothLeService.this.getApplicationContext()).readSp(DeviceConfig.DEVICE_NAME);
         String deviceAddress = LocalDataSaveTool.getInstance(BluetoothLeService.this.getApplicationContext()).readSp(DeviceConfig.DEVICE_ADDRESS);
         if(deviceName != null && !deviceName.equals("") && deviceAddress != null && !deviceAddress.equals(""))
         {
         BluetoothLeService.getInstance().connect(new LocalDeviceEntity(deviceName, deviceAddress, -50, new byte[0]));
         }
         */

//			if(isConnectWhere)
//			{
//				BluetoothLeService.getInstance().connect(BluetoothLeService.getInstance().getCurrentDevice());
//				Log.i(TAG, "设备是否为空:" + BluetoothLeService.getInstance().getCurrentDevice());
//			}
//			else
//			{
//				Log.i(TAG, "设备为空");
//				ArrayList<DeviceSaveData> allDevice = (ArrayList<DeviceSaveData>)LocalDataBaseUtils.getInstance(MainActivity.this).getDeviceListFromDB();
//				if(allDevice.size() != 0 && BluetoothLeService.getInstance() != null)
//				{
//					DeviceSaveData deviceSaveData = allDevice.get(allDevice.size() - 1);
//					LocalDeviceEntity entity = new LocalDeviceEntity(deviceSaveData.getDeviceName(), deviceSaveData.getDeviceAddress(), -70, new byte[10]);
//					BluetoothLeService.getInstance().connect(entity);
//				}
//			}
//        }
    }
    /**
     * After using a given BLE device, the app must call this method to ensure
     * resources are released properly.
     */
    public synchronized void close(boolean showToast) {
        if (mBluetoothGatt == null) {
            return;
        }
        Log.w(TAGBLE, "close device");
        isConnectingDevice = false;
        isDiscoveringService = false;
        LocalDeviceEntity device = Engine.getInstance().getDeviceFromGatt(mBluetoothGatt);
        synchronized (Engine.getInstance()) {
            Log.i(TAGBLE, "close mBluetoothGatt");
            Engine.getInstance().removeGattFromDeviceMap(mBluetoothGatt);
            mPendingWriteOperates.clear();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        if (!mServiceCallbacks.isEmpty()) {
            int size = mServiceCallbacks.size();
            for (int i = 0; i < size; i++) {
                IServiceCallback callback = mServiceCallbacks.get(i);
                callback.onBLEDeviceConnecError(device, 
                								showToast,// 显示提示信息
                								false);   // 主动断开
            }
        }
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read
     * result is reported asynchronously through the
     * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     * 
     * @param characteristic The characteristic to read from.
     */
    private boolean readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAGBLE, "BluetoothAdapter not initialized");
            return false;
        }
        return mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification. False otherwise.
     */
    public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
            boolean enabled)
    {
        if (mBluetoothAdapter == null || mBluetoothGatt == null)
        {
            Log.w(TAGBLE, "BluetoothAdapter not initialized");
            return false;
        }
        boolean isNotify = mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
        if(descriptors != null && !descriptors.isEmpty()){
            for (int i = 0; i < descriptors.size(); i++) {
                BluetoothGattDescriptor lastDescriptor = descriptors.get(i);
                if(enabled){
                	lastDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                }else{
                	lastDescriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                }
                mBluetoothGatt.writeDescriptor(lastDescriptor);
            }
        }
        return isNotify;

    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This
     * should be invoked only after {@code BluetoothGatt#discoverServices()}
     * completes successfully.
     * 
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null)
            return null;

        return mBluetoothGatt.getServices();
    }
    
    public BluetoothGatt getBluetoothGatt() {
		return mBluetoothGatt;
	}

	//--------------------------------------SERVICE--------------------------------------------------
    boolean isServiceRunning;
    private static BluetoothLeService sInBluetoothLeService;
    private long mUIThreadId;

    
    private boolean isRunOnUIThread() {
        return mUIThreadId == Thread.currentThread().getId();
    }

    public synchronized static BluetoothLeService getInstance() {
        return sInBluetoothLeService;
    }

    Handler mHandler;
    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.i(TAGBLE, "<< BLE Service onCreate >>");
        Engine.getInstance().init(this.getApplicationContext());
        mHandler = new Handler();
        mUIThreadId = Thread.currentThread().getId();
        sInBluetoothLeService = this;
        isServiceRunning = true;
        initialize();
        if(NeedAutoPair){
            mReceiver = new BoundReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            registerReceiver(mReceiver, filter);
        }
        registDayDataRecever();
        startServiceAsForgroundService();
//        setForground();
    }

//    private ForgroundServiceConnection forgrondConnection;
//    private void setForground()
//    {
//        Intent intentFor = new Intent(this, ForgroundService.class);
//        startService(intentFor);
//        startForeground(459, new Notification());
//        if(forgrondConnection == null)
//        {
//            forgrondConnection = new ForgroundServiceConnection();
//        }
//        BluetoothLeService.this.bindService(intentFor, forgrondConnection, Service.BIND_AUTO_CREATE);
//    }


//    class ForgroundServiceConnection implements ServiceConnection
//    {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service)
//        {
//            BluetoothLeService.this.startForeground(459, new Notification());
//            Service forgroundService = ((ForgroundService.MyBinder)service).getService();
//            forgroundService.startForeground(459, new Notification());
//            forgroundService.stopForeground(true);
//            BluetoothLeService.this.unbindService(forgrondConnection);
//            forgrondConnection = null;
//        }
//        @Override
//        public void onServiceDisconnected(ComponentName name) {}
//    }



    private RefreshRecever recever;
    private void registDayDataRecever()
    {
        recever = new RefreshRecever();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(recever, intentFilter);
    }

    private void startServiceAsForgroundService()
    {
//        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        if(notificatains == null)
//        {
//            notificatains = new Notification.Builder(this);
//        }
//        Intent intent = new Intent(BluetoothLeService.this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, 0);
//        notificatains.setContentIntent(pendingIntent);
//        notificatains.setSmallIcon(R.mipmap.movement_icon);
//        notificatains.setAutoCancel(true);
//        notificatains.setContentTitle(getString(R.string.app_name));
//        notificatains.setShowWhen(true);
//        notificatains.setWhen(System.currentTimeMillis());
//        notificatains.build();
//        startForeground(serviceID, notificatains.getNotification());
        this.getApplicationContext().sendBroadcast(new Intent("recever_notification_show"));
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Engine.getInstance().close();
        mPendingWriteOperates.clear();
        if(NeedAutoPair){
            unregisterReceiver(mReceiver);
        }
        sInBluetoothLeService = null;
        isServiceRunning = false;
        Log.i(TAGBLE, "<< BLE Service onDestory >>" + sInBluetoothLeService);
        unregisterReceiver(recever);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (intent != null) {
            Log.i(TAGBLE, "BLE Service onStartCommand");
            isServiceRunning = true;
        }



        connectTheSaveDevice(true);
        return START_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAGBLE, "BLE Service onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAGBLE, "BLE Service onUnBind");
        // After using a given device, you should make sure that
        // BluetoothGatt.close() is called
        // such that resources are cleaned up properly. In this particular
        // example, close() is
        // invoked when the UI is disconnected from the Service.
//        close(true);
        return super.onUnbind(intent);
    }
    
    public boolean isServiceRunning(){
        return isServiceRunning;
    }
    
    private final IBinder mBinder = new LocalBinder();
    
//    public static void startService(Context context){
//        if(sInBluetoothLeService == null){
//            Intent intent = new Intent(context, BluetoothLeService.class);
//            context.startService(intent);
//        }else{
//            Log.w("service have started");
//        }
//    }
//    
//    public static void stopService(Context context){
//        if(sInBluetoothLeService != null){
//            Intent intent = new Intent(context, BluetoothLeService.class);
//            context.stopService(intent);
//        }else{
//            Log.w("service have stoped");
//        }
//    }
    
    private class BoundReceiver extends BroadcastReceiver {    
        private LocalDeviceEntity mTargetDevice;
        public void setTargetDevice(LocalDeviceEntity device){
            mTargetDevice = device;
        }
        
        public void onReceive(Context context, Intent intent) {    
            String action = intent.getAction();    
            Bundle b = intent.getExtras();    
            Object[] lstName = b.keySet().toArray();    
    
            BluetoothDevice device = null;
            if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);    
                switch (device.getBondState()) {    
                case BluetoothDevice.BOND_BONDING:    
                    break;
                case BluetoothDevice.BOND_BONDED:    
                    boolean needReConn = (mBluetoothGatt == null && isConnectingDevice && mTargetDevice != null && device != null && mTargetDevice.getAddress().equals(device.getAddress()));
                    if(needReConn){
                        connect(mTargetDevice);
                    }
                    break;    
                case BluetoothDevice.BOND_NONE:    
                default:
                    break;    
                }    
            } 
        }  
    }

//    private int stepNow = 0;
    private class RefreshRecever extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
//            if(intent.getAction().equals(MyConfingInfo.NOTIFY_MAINACTIVITY_TO_UPDATE_DAY_DATA))
//            {
//                blueHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        stepNow = getCurrentDayData();
//                        notificatains.setContentText("今天：" + stepNow + "步");
//                        notificatains.setWhen(System.currentTimeMillis());
//                        notificatains.setShowWhen(true);
//                        notificatains.build();
//                        nm.notify(serviceID, notificatains.getNotification());
//                    }
//                });
//            }
//            else
            if(intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED))
            {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                switch (state)
                {
                    case BluetoothAdapter.STATE_ON:
                        connectTheSaveDevice(true);
                        Log.i("", "BluetoothAdapter.STATE_ON");
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Log.i("", "BluetoothAdapter.STATE_OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.i("", "BluetoothAdapter.STATE_TURNING_OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.i("", "BluetoothAdapter.STATE_TURNING_ON");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.i("", "BluetoothAdapter.STATE_CONNECTED");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.i("", "BluetoothAdapter.STATE_CONNECTING");
                        break;
                    case BluetoothAdapter.STATE_DISCONNECTED:
                        Log.i("", "BluetoothAdapter.STATE_DISCONNECTED");
                        break;
                    case BluetoothAdapter.STATE_DISCONNECTING:
                        Log.i("", "BluetoothAdapter.STATE_DISCONNECTING");
                        break;
                }
            }
        }
    }
//
//    private int getCurrentDayData()
//    {
//        int allSteps = 0;
//        String account = UserAccountUtil.getAccount(BluetoothLeService.this);
//        MyDBHelperForDayData helper = MyDBHelperForDayData.getInstance(BluetoothLeService.this);
//        Cursor mCursor = helper.selecteDayData(BluetoothLeService.this, account, getTodayDate());
//        if(mCursor.getCount() > 0)
//        {
//            if(mCursor.moveToFirst())
//            {
//                while (mCursor.moveToFirst())
//                {
//                    allSteps = mCursor.getInt(mCursor.getColumnIndex("stepAll"));
//                }
//            }
//        }
//        return allSteps;
//    }
//    private String getTodayDate()
//    {
//        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        return format.format(calendar.getTime());
//    }

}
