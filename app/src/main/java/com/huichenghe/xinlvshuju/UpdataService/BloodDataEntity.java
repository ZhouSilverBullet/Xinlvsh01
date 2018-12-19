package com.huichenghe.xinlvshuju.UpdataService;

/**
 * Created by lixiaoning on 2016/12/19.
 */

public class BloodDataEntity
{
    private String account;
    private String date;
    private String bloodData;
    private String deviceType;

    public BloodDataEntity(String account, String date, String bloodData, String deviceType) {
        this.account = account;
        this.date = date;
        this.bloodData = bloodData;
        this.deviceType = deviceType;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBloodData() {
        return bloodData;
    }

    public void setBloodData(String bloodData) {
        this.bloodData = bloodData;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
