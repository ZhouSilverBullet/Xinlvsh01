package com.huichenghe.xinlvshuju.BleDeal;

/**
 * Created by lixiaoning on 2016/10/16.
 */
public class BloodDataEntity
{
    private String date;
    private String time;
    private int highPre;
    private int lowPre;
    private int heartRate;
    private int spo2;
    private int hrv;

    public BloodDataEntity(int hrv, String date, String time, int highPre, int lowPre, int heartRate, int spo2) {
        this.hrv = hrv;
        this.date = date;
        this.time = time;
        this.highPre = highPre;
        this.lowPre = lowPre;
        this.heartRate = heartRate;
        this.spo2 = spo2;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }
        if(o == this)
        {
            return true;
        }
        if(!(o instanceof BloodDataEntity))
        {
            return false;
        }
        else
        {
            String time = ((BloodDataEntity) o).getTime();
            String ti = this.getTime();
            if(time.equals(ti))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getHighPre() {
        return highPre;
    }

    public int getLowPre() {
        return lowPre;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public int getSpo2() {
        return spo2;
    }

    public int getHrv() {
        return hrv;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setHighPre(int highPre) {
        this.highPre = highPre;
    }

    public void setLowPre(int lowPre) {
        this.lowPre = lowPre;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public void setSpo2(int spo2) {
        this.spo2 = spo2;
    }

    public void setHrv(int hrv) {
        this.hrv = hrv;
    }
}
