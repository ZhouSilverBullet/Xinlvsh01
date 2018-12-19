/*
 * Bluegiga’s Bluetooth Smart Android SW for Bluegiga BLE modules
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
package com.huichenghe.bleControl.Ble;

import java.io.Serializable;


// Device - it's wrapper for BLE device object
public class LocalDeviceEntity implements Serializable{

    private static final long serialVersionUID = 6349262396991397672L;
    private int rssi;
    private String mName = "";
    private String mAddress;
    private byte[] mScanRecord;


//  private String mWrapName;
//  Pattern mPhoneNumPattern = Pattern.compile("clock([\\d]{10})|((\\+[0-sosPhone]{phone,sitting})?\\(?[0-sosPhone]+\\)?-?)?[0-sosPhone]{sos,brocast}");
//  Pattern mPhoneNumPattern = Pattern.compile("clock([\\d]{10})|((\\+[0-sosPhone]{phone,sitting})?\\(?[0-sosPhone]+\\)?-?)?[0-sosPhone]{sos,brocast}");
  
    public LocalDeviceEntity() {

    }

    public LocalDeviceEntity(String name, String addr, int rssi, byte[] scanRecord) {
        mName = name;
        mAddress = addr;
        this.rssi = rssi;
        mScanRecord = scanRecord;
//        if(mScanRecord == null){
//            mWrapName = mName;
//        }else{
//            String adverst = new String(scanRecord);
//            if(TextUtils.isEmpty(adverst)){
//                mWrapName = mName;
//            }else{
//                Matcher m = mPhoneNumPattern.matcher(adverst);
//                String number = null;
//                while (m.find()) {
//                    if(!TextUtils.isEmpty(m.group())){
//                        number = m.group();
//                        Log.i("found userinfo from scanrecord:" + number);
//                        break;
//                    }
//                }
//                if(TextUtils.isEmpty(number)){
//                    mWrapName = mName;
//                }else{
//                    StringBuilder sb = new StringBuilder(mName);
//                    sb.append("-");
//                    sb.append(number);
//                    mWrapName = sb.toString();
//                }
//            }
//        }
        setDefaultConfig();
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String getName() {
        return mName;
    }
    

    public String getAddress() {
        return mAddress;
    }
    
    public byte[] getmScanRecord() {
		return mScanRecord;
	}

//    public String getWrapName(){
//        return mWrapName;
//    }
//    public boolean isHasConnected8Others(){
//    	if(mScanRecord == null){
//            return false;
//        }else{
//            String adverst = new String(mScanRecord);
//            if(TextUtils.isEmpty(adverst)){
//                return false;
//            }else{
//                Matcher m = mPhoneNumPattern.matcher(adverst);
//                String number = null;
//                while (m.find()) {
//                    if(!TextUtils.isEmpty(m.group())){
//                        number = m.group();
//                        Log.i("found userinfo from scanrecord:" + number);
//                        break;
//                    }
//                }
//                if(TextUtils.isEmpty(number)){
//                	return false;
//                }else{
//                	return true;
//                }
//            }
//        }
//    }


	@Override
    public boolean equals(Object other)
    {
        if(other == null){
            return false;
        }
        if(this == other){
            return true;
        }
           
       if(!(other instanceof LocalDeviceEntity)){
           return false;
       }else{
           final LocalDeviceEntity u = (LocalDeviceEntity)other;
           if(mAddress.equals(u.getAddress())){
               return true;
           }else{
               return false;
           }
           
       }
           
    }

    private void setDefaultConfig() {
    }
    
    @Override
    public String toString() {
        return mName;
    }
}
