package com.huichenghe.xinlvshuju.LocationS;

import java.util.ArrayList;

/**
 * Created by lixiaoning on 2016/11/24.
 */
public class WeatherUtils {
    private static ArrayList<String> maps = new ArrayList<>();
    private static ArrayList<String> zyxMap = new ArrayList<>();
    private static String[] flArray = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
            "13", "14", "15", "16", "17", "18", "19", "20"};

    /**
     * 晴、
     * 多云、
     * 阴
     * 阵雨、雷阵雨。雷阵雨伴有冰雹
     * 雨夹雪
     * 小雨
     * 中雨
     * 大雨 暴雨 大暴雨 特大暴雨
     * 阵雪 小雪
     * 中雪
     * 大雪 暴雪
     * 雾
     * 冻雨
     * 小到中雨 中到大雨 大到暴雨 暴雨到大暴雨 大暴雨到特大暴雨
     * 小到中雪 中到大雪 大到暴雪
     * 浮尘
     * 扬沙
     * 沙尘暴 强沙尘暴 特强沙尘暴轻雾
     * 浓雾强浓雾
     * 轻微霾 轻度霾 中度霾 重度霾 特强霾
     * 霰
     * 飑线
     */

    public static int compare(String weathers) {
        int result = 0;
        if (weathers != null && weathers.equals("")) return result;
        maps.add("晴");
        maps.add("多云");
        maps.add("阴");
        maps.add("阵雨");
        maps.add("小雨");
        maps.add("小到中雨");
        maps.add("中雨");
        maps.add("中到大雨");
        maps.add("大雨");
        maps.add("暴雨");
        maps.add("大暴雨");
        maps.add("特大暴雨");
        maps.add("冻雨");
        maps.add("雷阵雨");
        maps.add("雷阵雨伴有冰雹");
        maps.add("雷雨");
        maps.add("雨带雪");
        maps.add("阵雪");
        maps.add("小雪");
        maps.add("小到中雪");
        maps.add("中雪");
        maps.add("中到大雪");
        maps.add("大雪");
        maps.add("暴雪");
        maps.add("浮尘");
        maps.add("沙尘暴");
        maps.add("扬沙");
        maps.add("霾");
        maps.add("雾");
        maps.add("霰");
        maps.add("飑线");

        for (int i = 0; i < maps.size(); i++) {
            String eachWeather = maps.get(i);
            if (eachWeather.equals(weathers)) {
                result = i;
                break;
            }
            if (eachWeather.contains(weathers)) {
                result = i;
                break;
            }
            if (weathers.contains("转")) {
                String[] ea = weathers.split("转");
                weathers = ea[ea.length - 1];
                if (eachWeather.contains(weathers)) {
                    result = i;
                }
            }
        }
        return result;
    }

    public static int comparecode(String weathers) {
        int result = 0;
        if (weathers != null && weathers.equals("")) return result;
        maps.add("100");
        maps.add("101");
        maps.add("104");
        maps.add("300");
        maps.add("305");
        maps.add("小到中雨");
        maps.add("306");
        maps.add("中到大雨");
        maps.add("307");
        maps.add("310");
        maps.add("311");
        maps.add("312");
        maps.add("313");
        maps.add("302");
        maps.add("304");
        maps.add("雷雨");
        maps.add("404");
        maps.add("407");
        maps.add("400");
        maps.add("小到中雪");
        maps.add("401");
        maps.add("中到大雪");
        maps.add("402");
        maps.add("403");
        maps.add("504");
        maps.add("507");
        maps.add("503");
        maps.add("502");
        maps.add("501");
        maps.add("霰");
        maps.add("飑线");

        for (int i = 0; i < maps.size(); i++) {
            String eachWeather = maps.get(i);
            if (eachWeather.equals(weathers)) {
                result = i;
                break;
            }
        }
        return result;
    }

    public static int getXYX(String zyx) {
        int resul = 0;
        zyxMap.add("弱、较弱、低");
        zyxMap.add("中等、较强");
        zyxMap.add("高等、强");
        for (int i = 0; i < zyxMap.size(); i++) {
            String z = zyxMap.get(i);
            if (z.equals(zyx)) {
                resul = i;
                break;
            }
            if (z.contains(zyx)) {
                resul = i;
                break;
            }
        }
        return resul;
    }


    public static int getWind(String wind) {
        int flRes = 1;
        for (int i = 0; i < flArray.length; i++) {
            String ff = flArray[i];
            if (ff.contains(wind)) {
                flRes = i;
                break;
            }
        }
        return flRes;
    }

    /**
     * 通过和风天气得到风力
     *
     * @param wind
     * @return
     */
    public static int getWindli(String wind) {
        int windli = 3;
        if (wind.equals("平静")) {
            windli = 0;
        }
        if (wind.equals("有风")) {
            windli = 1;
        }
        if (wind.equals("微风")) {
            windli = 3;
        }
        if (wind.equals("和风")) {
            windli = 4;
        }
        if (wind.equals("强风")) {
            windli = 6;
        }
        if (wind.equals("劲风")) {
            windli = 6;
        }
        if (wind.equals("疾风")) {
            windli = 7;
        }
        if (wind.equals("大风")) {
            windli = 8;
        }
        if (wind.equals("清风") || wind.equals("烈风")) {
            windli = 9;
        }
        if (wind.equals("风暴") || wind.equals("狂爆风")) {
            windli = 10;
        }
        if (wind.equals("飓风")) {
            windli = 12;
        }
        if (wind.equals("龙卷风")) {
            windli = 13;
        }
        if (wind.equals("热带风暴")) {
            windli = 14;
        }
        return windli;

    }

    /**
     * 0：东
     * 1：南
     * 2：西
     * 3：北
     * 4：东南
     * 5：东北
     * 6：西南
     * 7：西北
     * 8：微风
     *
     * @param dir
     * @return
     */
    public static int getDriect(String dir) {
        int result = 0;
        String[] dirArray = new String[]{"微风", "东", "南", "西", "北", "东南", "东北", "西南", "西北"};
        for (int i = 0; i < dirArray.length; i++) {
            String eaDir = dirArray[i];
            if (eaDir.equals(dir)) {
                result = i;
                break;
            }

            if (eaDir.contains("dir")) {
                result = i;
                break;
            }
        }
        return result;
    }
}
