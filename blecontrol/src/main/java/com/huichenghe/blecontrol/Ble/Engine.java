/*
 * Bluegiga's Bluetooth Smart Android SW for Bluegiga BLE modules
 * Contact: support@bluegiga.com.
 *
 * This is free software distributed under the terms of the MIT license reproduced below.
 *
 * Copyright (c) 2013, Bluegiga Technologies
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files ("Software")
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF 
 * ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A  PARTICULAR PURPOSE.
 */
package com.huichenghe.blecontrol.Ble;

import android.bluetooth.BluetoothGatt;
import android.content.Context;

import java.util.HashMap;
import java.util.Vector;


// Engine - contains data accessible by each part of application
// It links data models from xml resources with real Bluetooth classes  
public class Engine {

    private static Engine instance = null;
    private Vector<LocalDeviceEntity> mNearbyList;
    private Vector<LocalDeviceEntity> mFindedDeviceList = new Vector<LocalDeviceEntity>();
    private HashMap<BluetoothGatt, LocalDeviceEntity> mDeviceMap = new HashMap<BluetoothGatt, LocalDeviceEntity>();
    private static final Object locker = new Object();

    public static Engine getInstance() {
        if (instance == null) {
            synchronized (locker) {
                if (instance == null) {
                    instance = new Engine();
                }
            }
        }
        return instance;
    }

    // Initializes class members, it must be first called
    public void init(Context context) {
    	if (mNearbyList == null) {
    		mNearbyList = new Vector<LocalDeviceEntity>();
    	}
    }

    
    public Vector<LocalDeviceEntity> getFindedDeviceList() {
        return mFindedDeviceList;
    }
    
    public void addFindedDeviceList(LocalDeviceEntity dev) {
    	mFindedDeviceList.add(dev);
    }

    // Gets all mNearbyList
    public Vector<LocalDeviceEntity> getNearbyDeviceList() {
        return mNearbyList;
    }

    public boolean addGatt2DeviceMap(BluetoothGatt gatt, LocalDeviceEntity device){
        if(mDeviceMap.containsKey(gatt) || mDeviceMap.containsValue(device)){
            return false;
        }
        mDeviceMap.put(gatt, device);
        return true;
    }
    public boolean removeGattFromDeviceMap(BluetoothGatt gatt){
        if(mDeviceMap.containsKey(gatt)){
            mDeviceMap.remove(gatt);
            return true;
        }
        return false;
    }
    
    public synchronized LocalDeviceEntity getDeviceFromGatt(BluetoothGatt gatt){
        return mDeviceMap.get(gatt);
    }
    
 // Clears all data lists
    public void close() {
        if (mFindedDeviceList != null) {
        	mFindedDeviceList.clear();
        }
        if (mNearbyList != null) {
            mNearbyList.clear();
        }
        if (mDeviceMap != null) {
            mDeviceMap.clear();
        }
    }
    
    
    
////    public LocalDeviceEntity getLocalDeviceByAddress(String address) {
////        Iterator<LocalDeviceEntity> e = mNearbyList.iterator();
////
////        LocalDeviceEntity elem;
////        while (e.hasNext()) {
////            elem = e.next();
////            if (elem.getAddress().equals(address)) {
////                return elem;
////            }
////        }
////        return null;
////    }
//
//    // Gets device for given device gatt object
////    public LocalDeviceEntity getLocalDevice(BluetoothDevice device) {
////        Iterator<LocalDeviceEntity> e = mNearbyList.iterator();
////        
////        LocalDeviceEntity elem;
////        while (e.hasNext()) {
////            elem = e.next();
////            if (device != null && elem.getAddress().equals(device.getAddress())) {
////                return elem;
////            }
////        }
////        return null;
////    }

}
