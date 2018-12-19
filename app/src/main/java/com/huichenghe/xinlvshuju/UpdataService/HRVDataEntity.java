package com.huichenghe.xinlvshuju.UpdataService;

/**
 * Created by lixiaoning on 2016/12/19.
 */

public class HRVDataEntity
{
    private String account;
    private String date;
    private String hrvData;
    private String deviceType;

    public HRVDataEntity(String date, String hrvData, String deviceType) {
        this.date = date;
        this.hrvData = hrvData;
        this.deviceType = deviceType;
    }

    public HRVDataEntity(String account, String date, String hrvData, String deviceType) {
        this.account = account;
        this.date = date;
        this.hrvData = hrvData;
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

    public String getHrvData() {
        return hrvData;
    }

    public void setHrvData(String hrvData) {
        this.hrvData = hrvData;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
