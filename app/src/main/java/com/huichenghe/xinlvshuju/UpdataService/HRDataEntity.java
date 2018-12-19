package com.huichenghe.xinlvshuju.UpdataService;

/**
 * Created by lixiaoning on 2016/12/19.
 */

public class HRDataEntity
{
    private String account;
    private String date;
    private String HRData;
    private String deviceType;

    public HRDataEntity(String date, String HRData, String deviceType) {
        this.date = date;
        this.HRData = HRData;
        this.deviceType = deviceType;
    }

    public HRDataEntity(String account, String date, String HRData, String deviceType) {
        this.account = account;
        this.date = date;
        this.HRData = HRData;
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

    public String getHRData() {
        return HRData;
    }

    public void setHRData(String HRData) {
        this.HRData = HRData;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
