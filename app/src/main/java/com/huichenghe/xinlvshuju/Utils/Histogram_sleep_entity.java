package com.huichenghe.xinlvshuju.Utils;

/**
 * 柱状图数据封装类
 * Created by lixiaoning on 15-11-14.
 */
public class Histogram_sleep_entity {
    private String time;
    private int totalCount;
    private int MaxCount;
    private int deepCount;
    private int wakeCount;
    private int lightCount;

    public Histogram_sleep_entity(int lightCount) {
        this.lightCount = lightCount;
    }

    public int getLightCount() {
        return lightCount;
    }

    public Histogram_sleep_entity(String time, int deepCount, int lightCount, int wakeCount, int MaxCount)
    {
        this.time = time;
        this.lightCount = lightCount;
        this.MaxCount = MaxCount;
        this.deepCount = deepCount;
        this.wakeCount = wakeCount;
    }

    public int getWakeCount() {
        return wakeCount;
    }

    public String getTime() {
        return time;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getMaxCount() {
        return MaxCount;
    }

    public int getDeepCount() {
        return deepCount;
    }
}
