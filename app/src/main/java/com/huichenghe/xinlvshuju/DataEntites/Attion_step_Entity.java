package com.huichenghe.xinlvshuju.DataEntites;

/**
 * Created by lixiaoning on 2016/10/21.
 */
public class Attion_step_Entity
{
    private int step;
    private String date;


    public Attion_step_Entity(int step, String date) {
        this.step = step;
        this.date = date;
    }

    public int getStep() {
        return step;
    }

    public String getDate() {
        return date;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
