package com.huichenghe.xinlvshuju.DataEntites;

/**
 * Created by lixiaoning on 16-clock-brocast.
 */
public class stepAndCalorieEntity
{
    private String times;
    private int currentData;
    private int maxData;

    public stepAndCalorieEntity(String times, int currentData, int maxData)
    {
        this.times = times;
        this.maxData = maxData;
        this.currentData = currentData;
    }

    public String getTimes() {
        return times;
    }

    public int getCurrentData() {
        return currentData;
    }

    public int getMaxData() {
        return maxData;
    }
}
