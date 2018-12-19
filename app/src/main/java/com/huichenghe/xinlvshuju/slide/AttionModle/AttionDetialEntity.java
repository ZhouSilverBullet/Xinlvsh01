package com.huichenghe.xinlvshuju.slide.AttionModle;

/**
 * Created by lixiaoning on 2016/6/7.
 */
public class AttionDetialEntity
{
    private String day;
    private int step;
    private int sleepDeep;
    private int sleepLight;
    private int sleepAweek;

    public AttionDetialEntity(String day, int step, int sleepDeep, int sleepLight, int sleepAweek) {
        this.day = day;
        this.sleepAweek = sleepAweek;
        this.sleepLight = sleepLight;
        this.sleepDeep = sleepDeep;
        this.step = step;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public void setSleepDeep(int sleepDeep) {
        this.sleepDeep = sleepDeep;
    }

    public void setSleepLight(int sleepLight) {
        this.sleepLight = sleepLight;
    }

    public void setSleepAweek(int sleepAweek) {
        this.sleepAweek = sleepAweek;
    }

    public String getDay() {
        return day;
    }

    public int getSleepAweek() {
        return sleepAweek;
    }

    public int getSleepLight() {
        return sleepLight;
    }

    public int getSleepDeep() {
        return sleepDeep;
    }

    public int getStep() {
        return step;
    }


    @Override
    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }
        if(this == o)
        {
            return true;
        }
        if(o instanceof AttionDetialEntity)
        {
            AttionDetialEntity en = (AttionDetialEntity)o;
            if(en.getDay().equals(this.getDay()))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
}
