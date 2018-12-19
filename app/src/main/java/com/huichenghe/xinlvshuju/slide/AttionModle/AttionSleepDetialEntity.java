package com.huichenghe.xinlvshuju.slide.AttionModle;

/**
 * Created by lixiaoning on 2016/6/7.
 */
public class AttionSleepDetialEntity
{
    private String day;
    private int deepSleep;
    private int lightSleep;
    private int aweekSleep;
    private int totalData;

    public AttionSleepDetialEntity(String day, int deepSleep, int lightSleep, int aweekSleep, int maxData) {
        this.day = day;
        this.deepSleep = deepSleep;
        this.lightSleep = lightSleep;
        this.aweekSleep = aweekSleep;
        this.totalData = maxData;
    }

    public int getTotalData() {
        return totalData;
    }

    public void setTotalData(int totalData) {
        this.totalData = totalData;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setDeepSleep(int deepSleep) {
        this.deepSleep = deepSleep;
    }

    public void setLightSleep(int lightSleep) {
        this.lightSleep = lightSleep;
    }

    public void setAweekSleep(int aweekSleep) {
        this.aweekSleep = aweekSleep;
    }

    public int getDeepSleep() {
        return deepSleep;
    }

    public int getLightSleep() {
        return lightSleep;
    }

    public int getAweekSleep() {
        return aweekSleep;
    }
}
