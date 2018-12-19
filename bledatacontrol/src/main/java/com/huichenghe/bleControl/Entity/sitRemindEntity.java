package com.huichenghe.bleControl.Entity;

import java.io.Serializable;

public class sitRemindEntity implements Serializable
{
    private int number;
    private int openOrno;
    private String beginTime;
    private String endTime;
    private int duration;

    public sitRemindEntity(int number, int openOrno, String beginTime, String endTime, int duration) {
        this.number = number;
        this.openOrno = openOrno;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.duration = duration;
    }

    public int getNumber() {
        return number;
    }

    public int getOpenOrno() {
        return openOrno;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setOpenOrno(int openOrno) {
        this.openOrno = openOrno;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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
        if(!(o instanceof sitRemindEntity))
        {
            return false;
        }
        else
        {
            int nu = ((sitRemindEntity) o).getNumber();
            if(nu == this.number)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }
}
