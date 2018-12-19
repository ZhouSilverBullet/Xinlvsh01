package com.huichenghe.xinlvshuju.UpdataService;

/**
 * Created by lixiaoning on 2016/12/19.
 */

public class DayDataEntity
{
    private String account;
    private String date;
    private String stepAll;
    private String calorie;
    private String mileage;
    private String movementTime;
    private String moveCalorie;
    private String sitTime;
    private String sitCalorie;
    private String deviceType;
    private String stepTarget;
    private String sleepTarget;

    public DayDataEntity(String account, String date, String stepAll, String calorie, String mileage, String movementTime, String moveCalorie, String sitTime, String sitCalorie, String stepTarget, String sleepTarget, String deviceType) {
        this.account = account;
        this.date = date;
        this.stepAll = stepAll;
        this.calorie = calorie;
        this.mileage = mileage;
        this.movementTime = movementTime;
        this.moveCalorie = moveCalorie;
        this.sitTime = sitTime;
        this.sitCalorie = sitCalorie;
        this.deviceType = deviceType;
        this.stepTarget = stepTarget;
        this.sleepTarget = sleepTarget;
    }

    public DayDataEntity(String date, String stepAll, String calorie, String mileage, String movementTime,
                         String moveCalorie, String sitTime, String sitCalorie, String stepTarget, String sleepTarget, String deviceType) {
        this.date = date;
        this.stepAll = stepAll;
        this.calorie = calorie;
        this.mileage = mileage;
        this.movementTime = movementTime;
        this.moveCalorie = moveCalorie;
        this.sitTime = sitTime;
        this.sitCalorie = sitCalorie;
        this.deviceType = deviceType;
        this.stepTarget = stepTarget;
        this.sleepTarget = sleepTarget;
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

    public String getStepAll() {
        return stepAll;
    }

    public String getCalorie() {
        return calorie;
    }

    public String getMileage() {
        return mileage;
    }

    public String getMovementTime() {
        return movementTime;
    }

    public String getMoveCalorie() {
        return moveCalorie;
    }

    public String getSitTime() {
        return sitTime;
    }

    public String getSitCalorie() {
        return sitCalorie;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getStepTarget() {
        return stepTarget;
    }

    public String getSleepTarget() {
        return sleepTarget;
    }

    public void setStepAll(String stepAll) {
        this.stepAll = stepAll;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public void setMovementTime(String movementTime) {
        this.movementTime = movementTime;
    }

    public void setMoveCalorie(String moveCalorie) {
        this.moveCalorie = moveCalorie;
    }

    public void setSitTime(String sitTime) {
        this.sitTime = sitTime;
    }

    public void setSitCalorie(String sitCalorie) {
        this.sitCalorie = sitCalorie;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public void setStepTarget(String stepTarget) {
        this.stepTarget = stepTarget;
    }

    public void setSleepTarget(String sleepTarget) {
        this.sleepTarget = sleepTarget;
    }
}
