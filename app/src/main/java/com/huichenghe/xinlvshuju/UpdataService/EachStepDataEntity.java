package com.huichenghe.xinlvshuju.UpdataService;

/**
 * Created by lixiaoning on 2016/12/19.
 */

public class EachStepDataEntity
{
    private String date;
    private String stepData;
    private String deviceType;
    private String account;

    public EachStepDataEntity(String date, String stepData, String deviceType) {
        this.date = date;
        this.stepData = stepData;
        this.deviceType = deviceType;
    }

    public EachStepDataEntity(String date, String stepData, String deviceType, String account) {
        this.date = date;
        this.stepData = stepData;
        this.deviceType = deviceType;
        this.account = account;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStepData() {
        return stepData;
    }

    public void setStepData(String stepData) {
        this.stepData = stepData;
    }
}
