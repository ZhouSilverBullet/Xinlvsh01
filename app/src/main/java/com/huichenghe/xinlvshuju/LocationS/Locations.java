package com.huichenghe.xinlvshuju.LocationS;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.location.service.LocationService;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.mainpack.MyApplication;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.blueware.agent.android.BlueWare.getApplicationContext;

/**
 * Created by lixiaoning on 2016/11/21.
 */
public class Locations {
    public final String TAG = "Locations";
    private LocationService mLocationService;
    private LocationBack callback;
    private Context context;
    private String cityId;
    private String WSDL;
    private static final String targetNameSpace = "http://weatherapi.market.xiaomi.com/";
    private static final String getSupportProvince = "getRegionProvince";

    public Locations(Context context, LocationBack back) {
        this.context = context;
        this.callback = back;
        mLocationService = MyApplication.getInstance().mLocationService;
    }

    public void registListener() {
        LocationClientOption mOption = new LocationClientOption();
        mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        mOption.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        mOption.setScanSpan(1000 * 60 * 60);   //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        mOption.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        mOption.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
        mOption.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
        mOption.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        mOption.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        mOption.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        mOption.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        mOption.setIsNeedAltitude(false);//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        mLocationService.setLocationOption(mOption);
        mLocationService.registerListener(listener);
    }

    public void startGps() {
        mLocationService.start();
    }


    public void stopGps() {
        mLocationService.stop();
    }

    public void unregisterListener() {
        mLocationService.unregisterListener(listener);
    }

    BDLocationListener listener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //维度
            double doulat = location.getLatitude();
            //经度
            double doulon = location.getLongitude();
            //getWeatherforforeign(doulat, doulon);
            Log.i(TAG, "定位结果" + location.toString());
            stopGps();
            if (location != null && location.getLocType() != location.TypeServerError) {
                String conS = "市";
                String currnetCity = location.getCity();

                Calendar calenda = Calendar.getInstance(Locale.getDefault());
                SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String datetoday = formate.format(calenda.getTime());//今天时间
                String datefrompre = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.TIME);//xmi中的日期
                boolean istoday = false;//控制一天只能请求一次数据
                if (datetoday.equals(datefrompre)) {
                    istoday = true;
                }
                if (currnetCity == null && !istoday) {
                    LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.IS_CHINA, "no_china");
                    getWeatherforforeign(doulat, doulon);
                } else if(currnetCity!=null) {
                    LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.IS_CHINA, "china");
                    getWeatherforforeign(doulat, doulon);
                    if (currnetCity.contains(conS)) {
                        int end = currnetCity.indexOf(conS);
                        if (end != -1) {
                            currnetCity = currnetCity.substring(0, end);
                            cityId = LocalCity.getCityIdByName(currnetCity);
                            Log.i(TAG, "当前定位：" + currnetCity + "   cityId" + cityId);
//                        getWeatherFromCityId(cityId);
                            new weatherThread().start();
                        }
                    }
                }
            }

            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            Log.i("TAG", sb.toString());
        }
    };

    class  weatherThread extends Thread {
        @Override
        public void run() {
            super.run();
            if (cityId == null || cityId != null && cityId.equals("")) {
                return;
            }
              getWeather(cityId);
//            WSDL = MyConfingInfo.weatherRoot + cityId + ".html";
//            new NetAsyncTask().execute();
//            Map<String, String> urlMap = new HashMap<>();
//            urlMap.put("cityId", cityId);

//            try {
//                HttpUtil.get(context, WSDL, new HttpUtil.DealResponse()
//                {
//                    @Override
//                    public boolean dealResponse(int responseCode, InputStream input) throws IOException
//                    {
//                        InputStreamReader re = new InputStreamReader(input);
//                        BufferedReader read = new BufferedReader(re);
//                        StringBuffer result = new StringBuffer();
//                        String line = "";
//                        while ((line = read.readLine()) != null)
//                        {
//                            result.append(line);
//                        }
//                        parseWeatherResult(result.toString());
//                        return false;
//                    }
//                    @Override
//                    public void setHeader(String url, Object obj){}
//                });
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    };

    private void getWeatherforforeign(double doulat, double doulon) {
        //https://api.heweather.com/v5/weather?city=yourcity&key=yourkey
        //经度,纬度&key=39c9cf54657c47deab625aa80b58a567&lang=zh-cn
        String urlbase = "https://api.heweather.com/v5/weather?city=";
        String urlkey = "&key=39c9cf54657c47deab625aa80b58a567&lang=";
        Locale.getDefault().getLanguage();
        String strlangue = Locale.getDefault().toString();
        if (strlangue.equals("th_TH")) {
            urlkey = urlkey+"th";
        } else if (strlangue.contains("zh_CN")) {
            urlkey = urlkey+"zh-cn";
        }else{
            urlkey = urlkey+"en";
        }
        String url = urlbase + doulon + "," + doulat + urlkey;
        HttpUtils httpUtils = new HttpUtils(5000);
        httpUtils.send(HttpRequest.HttpMethod.GET,     // 连接类型
                url,        // ur
                new RequestCallBack<String>()        // 通过回调回传结果
                {
                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        arg0.printStackTrace();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        String weatherData = arg0.result;
                        System.out.println("555555"+weatherData);
                        try {
                            JSONObject json = new JSONObject(weatherData);
                            JSONArray jsonArray = json.getJSONArray("HeWeather5");
                            JSONObject jsondata = jsonArray.getJSONObject(0);
                            JSONObject jsonbase = jsondata.getJSONObject("basic");
                            String city = jsonbase.getString("city");//城市

                            String  forcastfor3 = jsondata.getString("daily_forecast");

                            JSONObject jsomnow = jsondata.getJSONObject("now");//实时天气
                            String tmpnow = jsomnow.getString("tmp");
                            LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.USERCITY, city);
                            LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.FORCAST, forcastfor3);
                            LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.NOWTEMP, tmpnow);
                            Calendar calenda = Calendar.getInstance(Locale.getDefault());
                            SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            String datetoday = formate.format(calenda.getTime());//今天时间
                            LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.TIME, datetoday);
                            JSONObject jsonaqi = jsondata.getJSONObject("aqi");
                            JSONObject jsoncity = jsonaqi.getJSONObject("city");
                            String aqi = jsoncity.getString("aqi");
                            LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.AQI, aqi);//aqi和风可能不返回
                        } catch (JSONException e) {
                            LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.AQI, "0");
                        }
                        if (callback != null) {
                            callback.result(weatherData);
                        }
                    }
                });
    }

    private void getWeather(String cityId) {
        String urls = "http://weatherapi.market.xiaomi.com/wtr-v2/weather?cityId=";
        String urlEnd = ".html";
        HttpUtils httpUtils = new HttpUtils(5000);            // http连接工具类
        httpUtils.send(HttpRequest.HttpMethod.GET,                // 连接类型
                urls + cityId,        // ur
                new RequestCallBack<String>()        // 通过回调回传结果
                {
                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        arg0.printStackTrace();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        String weatherData = arg0.result;
//                        Log.i(TAG, "当前城市的天气情况：" + weatherData);
                        if (callback != null) {
                            callback.result(weatherData);
                        }
//                        parseWeatherResult(weatherData);

                    }
                });
    }


    class NetAsyncTask extends AsyncTask<Object, Object, String> {

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("success")) {

            }
            super.onPostExecute(result);
        }

        @Override
        protected String doInBackground(Object... params) {
            // 根据命名空间和方法得到SoapObject对象
            SoapObject soapObject = new SoapObject(null,
                    getSupportProvince);
            // 通过SOAP1.1协议得到envelop对象
            SoapSerializationEnvelope envelop = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            // 将soapObject对象设置为envelop对象，传出消息

            envelop.dotNet = true;
            envelop.setOutputSoapObject(soapObject);
            // 或者envelop.bodyOut = soapObject;
            HttpTransportSE httpSE = new HttpTransportSE(WSDL);
            // 开始调用远程方法
            try {
                httpSE.call(null + getSupportProvince, envelop);
                // 得到远程方法返回的SOAP对象
                SoapObject resultObj = (SoapObject) envelop.getResponse();
                // 得到服务器传回的数
                String re = resultObj.toString();
                Log.i(TAG, "天气返回的数据：" + re);
//                int count = resultObj.getPropertyCount();
//                for (int i = 0; i < count; i++) {
//                    Map<String,String> listItem = new HashMap<String, String>();
//                    listItem.put("province", resultObj.getProperty(i).toString());
//                    listItems.add(listItem);
//                }
            } catch (IOException e) {
                e.printStackTrace();
                return "IOException";
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return "XmlPullParserException";
            }
            return "success";
        }
    }

}
