package com.huichenghe.xinlvshuju.DataEntites;

/**
 * Created by lixiaoning on 2016/10/25.
 */
public class Attion_sleep_data_entity
{
    private int deepData;
    private int lightData;
    private int wakeData;
    private String date;

    public Attion_sleep_data_entity(int deepData, int lightData, int wakeData, String date) {
        this.deepData = deepData;
        this.lightData = lightData;
        this.wakeData = wakeData;
        this.date = date;
    }

    public int getDeepData() {
        return deepData;
    }

    public int getLightData() {
        return lightData;
    }

    public int getWakeData() {
        return wakeData;
    }

    public String getDate() {
        return date;
    }

    public void setDeepData(int deepData) {
        this.deepData = deepData;
    }

    public void setLightData(int lightData) {
        this.lightData = lightData;
    }

    public void setWakeData(int wakeData) {
        this.wakeData = wakeData;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
