package com.huichenghe.xinlvshuju.UpdataService;

/**
 * Created by lixiaoning on 2016/12/19.
 */

public class OutlineDataEntity
{
    private String date;
    private String outlineData;
    private String deviceType;
    private String account;

    public OutlineDataEntity(String account, String date, String outlineData, String deviceType) {
        this.date = date;
        this.outlineData = outlineData;
        this.deviceType = deviceType;
        this.account = account;
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

    public String getOutlineData() {
        return outlineData;
    }

    public void setOutlineData(String outlineData) {
        this.outlineData = outlineData;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
