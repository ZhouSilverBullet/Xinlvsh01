package com.huichenghe.xinlvshuju.UpdataService;

/**
 * Created by lixiaoning on 2016/12/19.
 */

public class SleepDataEntity
{

    private String account;
    private String date;
    private String data;
    private String deviceType;

    public SleepDataEntity(String date, String data, String deviceType) {
        this.date = date;
        this.data = data;
        this.deviceType = deviceType;
    }

    public SleepDataEntity(String account, String date, String data, String deviceType) {
        this.account = account;
        this.date = date;
        this.data = data;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
