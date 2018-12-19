package com.huichenghe.bleControl;

/**
 * Created by lixiaoning on 2017/1/8.
 */

public class WeatherEntity
{
    private String date;
    private int weather;
    private String temp;

    public WeatherEntity(String date, int weather, String temp) {
        this.date = date;
        this.weather = weather;
        this.temp = temp;
    }

    public String getDate() {
        return date;
    }

    public int getWeather() {
        return weather;
    }

    public String getTemp() {
        return temp;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setWeather(int weather) {
        this.weather = weather;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}
