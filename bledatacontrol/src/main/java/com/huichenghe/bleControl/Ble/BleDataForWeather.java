package com.huichenghe.bleControl.Ble;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.huichenghe.bleControl.Utils.FormatUtils;
import com.huichenghe.bleControl.WeatherEntity;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by lixiaoning on 2016/11/22.
 */
public class BleDataForWeather extends BleBaseDataManage
{
    public final static byte fromDevice = (byte)0x8f;
    public final static byte toDevice = (byte)0x0f;
    public final static byte fromDeviceNew = (byte)0xb1;
    public final static byte toDeviceNew = (byte)0x31;
    private static BleDataForWeather bleDataForWeather;
    private final int SEND_WEATHER_DATA_TO_DEVICE = 0;
    private boolean isBack = false;
    private int sendCount = 0;
    private BleDataForWeather(){};

    public static BleDataForWeather getIntance()
    {
        if(bleDataForWeather == null)
        {
            synchronized (BleDataForWeather.class)
            {
                if(bleDataForWeather == null)
                {
                    bleDataForWeather = new BleDataForWeather();
                }
            }
        }
        return bleDataForWeather;
    }

    private Handler weatherHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case SEND_WEATHER_DATA_TO_DEVICE:
                    if(isBack)
                    {
                        closeSend(this, msg);
                    }
                    else
                    {
                        if(sendCount < 4)
                        {
                            sendWeatherDataToDevice((byte)msg.arg1, msg.getData().getString("weatherData"));
                            continueSend(this, msg);
                        }
                        else
                        {
                            closeSend(this, msg);
                        }
                    }
                    break;
            }
        }
    };

    private void continueSend(Handler handler, Message msg)
    {
        Message mzg = handler.obtainMessage();
        mzg.what = msg.what;
        mzg.setData(msg.getData());
        handler.sendMessageDelayed(mzg, 1000);
        sendCount++;
    }

    private void closeSend(Handler handler, Message msg)
    {
        handler.removeMessages(SEND_WEATHER_DATA_TO_DEVICE);
        isBack = false;
        sendCount = 0;
    }

    /**
     *
     * @param byt 操作码
     * @param city 城市
     * @param time 日期时间
     * @param weather 天气整型
     * @param weatherS 天气
     * @param temp 温度范围
     * @param zyx 紫外线
     * @param windFL 风力
     * @param windDrie 风向
     * @param aqi 空气质量
     * @param cuTemp 实时温度
     */
    public void sendWeather(byte byt, String city, String time, int weather, String weatherS, String temp, int zyx, int windFL, int windDrie, String aqi, String cuTemp)
    {
        String dataAll = getAllDataString(byt, city, time, weather, weatherS, temp, zyx, windFL, windDrie, aqi, cuTemp);
        sendWeatherDataToDevice(byt, dataAll);
        Message msg = weatherHandler.obtainMessage();
        msg.what = SEND_WEATHER_DATA_TO_DEVICE;
        msg.arg1 = byt;
        Bundle bundle = new Bundle();
        bundle.putString("weatherData", dataAll);
        msg.setData(bundle);
        weatherHandler.sendMessageDelayed(msg, 1000);
    }

    private String getAllDataString(byte byt, String city, String time, int weather, String weatherS, String temp, int zyx, int windFL, int windDrie, String aqi, String cuTemp)
    {
        byte[] times = getTimeBytes(time);
        byte[] citys = getCityBytes(city);
        byte weath = (byte) weather;
        byte[] temps = getTemp(temp, cuTemp);
        byte ults = (byte)zyx;
        byte winds = (byte)windFL;
        byte dires= (byte)windDrie;
        byte aqis = getAqiBytes(aqi);
        StringBuffer alldata = new StringBuffer();
        if(byt == (byte)0x31)
        alldata.append("01");
        alldata.append("00");
        alldata.append("0" + times.length);
        alldata.append(FormatUtils.bytesToHexString(times));
        alldata.append("01");
        String cityS = "";
        if(citys.length < 16)
        {
            cityS += "0";
        }
        cityS += Integer.toHexString(citys.length);
        alldata.append(cityS);
        alldata.append(FormatUtils.bytesToHexString(citys));
        alldata.append("02");
        alldata.append("01");
        alldata.append(FormatUtils.byteToHexStringNo0X(weath));
        alldata.append("03");
        alldata.append(getWeatherString(weatherS)[0]);
        alldata.append(getWeatherString(weatherS)[1]);
        alldata.append("04");
        alldata.append("0" + temps.length);
        alldata.append(FormatUtils.bytesToHexString(temps));
        alldata.append("05");
        if(ults == 0)
        {
            alldata.append("00");
        }
        else
        {
            alldata.append("01");
            alldata.append(FormatUtils.byteToHexStringNo0X(ults));
        }
        alldata.append("06");
        alldata.append("01");
        alldata.append(FormatUtils.byteToHexStringNo0X(winds));
        alldata.append("07");
        alldata.append("01");
        alldata.append(FormatUtils.byteToHexStringNo0X(dires));
        alldata.append("08");
        if(aqis == 0)
        {
            alldata.append("00");
        }
        else
        {
            alldata.append("01");
            alldata.append(FormatUtils.byteToHexStringNo0X(aqis));
        }
        return alldata.toString();
    }

    private String[] getWeatherString(String weatherS)
    {
        String[] weatherInfo = new String[2];
        String weatherStringLength = "";
        String weatherString = "";
        int weatherLength = 0;
        try {
            byte[] weatherByte = weatherS.getBytes("utf-8");
            if(weatherByte.length > 48)
            {
                if (Locale.getDefault().getCountry().equals("zh"))
                {
                    weatherByte = weatherS.substring(0, 17).getBytes("utf-8");
                }
                else
                {
                    weatherByte = weatherS.substring(0, 49).getBytes("utf-8");
                }
            }
            weatherLength = weatherByte.length;
            weatherString = FormatUtils.bytesToHexString(weatherByte);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(weatherLength < 16)
        {
            weatherStringLength += "0";
        }
        weatherStringLength += Integer.toHexString(weatherLength);
        weatherInfo[0] = weatherStringLength;
        weatherInfo[1] = weatherString;
        return weatherInfo;
    }

    private byte getAqiBytes(String aqi)
    {
        int aqis = Integer.valueOf(aqi);
        byte r = (byte) (aqis & 0xff);
        return r;
    }

    private byte[] getCityBytes(String city)
    {
        if(city.length() > 8)
        {
            city = city.substring(0, 8);
        }
        try {
            return city.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendWeatherDataToDevice(byte byt, String allData)
    {
        setMessageDataByString(byt, allData.toString(), true);
    }


    public void dealWeatherBack(byte[] data)
    {
        isBack = true;
        if(weatherCallbackListener != null)
        {
            weatherCallbackListener.sendSuccess(data);
        }
    }


    private byte[] getTimeBytes(String time)
    {
        if(time.length() > 10)
        {
            byte[] timeData = new byte[4];
            Date date = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                date = format.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calend = Calendar.getInstance(TimeZone.getDefault());
            calend.setTime(date);
            int hour = calend.get(Calendar.HOUR_OF_DAY);
            int day = calend.get(Calendar.DAY_OF_MONTH);
            int month = calend.get(Calendar.MONTH) + 1;
            int year = calend.get(Calendar.YEAR) - 2000;
            timeData[0] = (byte)hour;
            timeData[1] = (byte)day;
            timeData[2] = (byte)month;
            timeData[3] = (byte)year;
            return timeData;
        }
        else
        {
            byte[] timeData = new byte[3];
            Date date = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                date = format.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calend = Calendar.getInstance(TimeZone.getDefault());
            calend.setTime(date);
            int day = calend.get(Calendar.DAY_OF_MONTH);
            int month = calend.get(Calendar.MONTH) + 1;
            int year = calend.get(Calendar.YEAR) - 2000;
            timeData[0] = (byte)day;
            timeData[1] = (byte)month;
            timeData[2] = (byte)year;
            return timeData;
        }

    }

    private byte[] getTemp(String temp, String cutemp)
    {
        byte[] temps = null;
        if(cutemp != null && cutemp.equals(""))
        {
            temps = new byte[2];
            String[] tempss = temp.split("~");
            int oneIndex = tempss[1].indexOf("℃");
            int twoIndex = tempss[0].indexOf("℃");
            temps[0] = (byte)Integer.parseInt(tempss[1].substring(0, oneIndex));
            temps[1] = (byte)Integer.parseInt(tempss[0].substring(0, twoIndex));
        }
        else
        {
            temps = new byte[3];
            String[] tempss = temp.split("~");
            int oneIndex = tempss[1].indexOf("℃");
            int twoIndex = tempss[0].indexOf("℃");
            temps[0] = (byte)Integer.parseInt(tempss[1].substring(0, oneIndex));
            temps[1] = (byte)Integer.parseInt(tempss[0].substring(0, twoIndex));
            temps[2] = (byte)Integer.parseInt(cutemp);
        }
        return temps;
    }

    private byte[] getWeatherBytes(String weather)
    {
        byte[] weath = new byte[0];
        try {
            byte[] wea = weather.getBytes("UTF-8");
            if(wea.length > 72)
            {
                weath = new byte[72];
                System.arraycopy(wea, 0, weath, 0, 72);
            }
            else
            {
                weath = new byte[wea.length];
                System.arraycopy(wea, 0, weath, 0, wea.length);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return weath;
    }

    private DataSendCallback weatherCallbackListener = null;
    public void setWeatherCallbackListener(DataSendCallback weatherCallbackListener) {
        this.weatherCallbackListener = weatherCallbackListener;
    }

    public void sendDatesWeather(byte byt, String city, List<WeatherEntity> datas)
    {
        if(datas.size() <= 0)return;
        StringBuffer dataWeather = new StringBuffer();
        dataWeather.append("02");
        Iterator<WeatherEntity> dates = datas.iterator();
        while (dates.hasNext())
        {
            WeatherEntity enty = dates.next();
            Log.i("weathers", "date:" + enty.getDate());
            Log.i("weathers", "temp:" + enty.getTemp());
            Log.i("weathers", "weather:" + enty.getWeather());
            byte[] ti = getTimeBytes(enty.getDate());
            dataWeather.append(FormatUtils.bytesToHexString(ti));
            String wea = String.valueOf(enty.getWeather());
            if(wea.length() < 2)
            {
                wea = "0" + wea;
            }
            dataWeather.append(wea);
            byte[] tempArray = getTemp(enty.getTemp(), "");

            dataWeather.append(FormatUtils.bytesToHexString(tempArray));
        }
        setMessageDataByString(byt, dataWeather.toString(), true);
    }
}
