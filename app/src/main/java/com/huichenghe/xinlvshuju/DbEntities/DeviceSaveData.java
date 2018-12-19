package com.huichenghe.xinlvshuju.DbEntities;

import android.content.Context;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

import java.util.Date;

/**
 * // 建议加上注解， 混淆后表名不受影响
 * Created by lixiaoning on 15-11-sitting.
 * 类名即表名
 */
@Table(name = "DeviceSaveData")
public class DeviceSaveData extends EntityBase {
    @Column(column = "deviceName")
    public String deviceName;   // 设备名字段

    @Column(column = "deviceAddress")
    public String deviceAddress;// 设备地址字段


    public DeviceSaveData(Context c, Date date) {
        super(c, date);
    }

    public DeviceSaveData() {
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

}
