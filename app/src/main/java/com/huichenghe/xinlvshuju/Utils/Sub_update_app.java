package com.huichenghe.xinlvshuju.Utils;

import android.content.Context;
import android.util.Log;

import com.huichenghe.xinlvshuju.DataEntites.ParamEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Locale;

/**
 * 检查更新app业务
 * Created by DIY on 2015/12/22.
 */
public class Sub_update_app {
    public static final String TAG = Sub_update_app.class.getSimpleName();
    private Context mContext;
    private ParamEntity mParamEntity;
    public Sub_update_app(Context context){
        mContext=context;
    }

    /**
     * 检查更新请求
     */
    public boolean requestCheckUpdate() throws IOException
    {
        String language;
        if(isZh()){
            language="zh-cn";
        }else{
            language="en-us";
        }
        String result = "";
        URL url = null;             // 链接对象
        HttpURLConnection conn = null;     // connecte对象
        //检查更新的URL:http://bracelet.cositea.com:8089/bracelet/androidVersion?language=zh-cn&product=hr64
        //String urlString = WebGlobalConfig.WebRoot+"androidVersion?language="+language;
        String urlString = WebGlobalConfig.WebRoot+"androidVersion?language="+language+"&product=hr64";

        try {
            //　构建对象
            url = new URL(urlString);
            conn = (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        // 设置输入输出流
        conn.setDoOutput(true);
        conn.setDoInput(true);
        // 设置连接超时
        conn.setConnectTimeout(6 * 1000);
        conn.setReadTimeout(6 * 1000);

        // 设置请求方式
        try {
            conn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        conn.setUseCaches(false);
        conn.setInstanceFollowRedirects(true);


        conn.connect();
        int a = conn.getResponseCode();
        InputStreamReader reader = new InputStreamReader(conn.getInputStream(), "UTF-8");

        BufferedReader read = new BufferedReader(reader);
        String c = null;
        while ((c = read.readLine()) != null)
        {
            result = result + c;
        }
        reader.close();
        read.close();
        conn.disconnect();
        boolean isSuccess = false;
        try {

            isSuccess = parserJSON(result);
            mParamEntity=setParamEntity(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return isSuccess;

    }

    /**
     * 解析服务器返回的字符串
     * @param result
     */
    public boolean parserJSON(String result) throws JSONException {
        Log.i(TAG, "登陆返回结果：" + result);
        JSONObject json = new JSONObject(result);
        int code = json.getInt("result");
        //请求发送成功
//检查失败（参数错误）
        return code == 1;
    }

    /**
     * 生成版本信息实体类
     * @param result
     * @return
     * @throws JSONException
     */
    public ParamEntity setParamEntity(String result) throws JSONException{
        Log.i(TAG, "登陆返回结果：" + result);
        JSONObject json = new JSONObject(result);
        JSONObject param = json.getJSONObject("param");
        Log.i(TAG,param.toString());
        ParamEntity mParamEntity=new ParamEntity();
        mParamEntity.setVersion(param.getString("version"));
        mParamEntity.setMinVersion(param.getString("minVersion"));
        mParamEntity.setDescription(param.getString("description"));
        mParamEntity.setUrl(param.getString("url"));
        mParamEntity.setFileSize(param.getInt("fileSize"));

        return mParamEntity;
    }

    //获取版本实体类
    public ParamEntity getParamEntity(){
        return mParamEntity;
    }

    /**
     * 判断是否是中文环境
     * @return boolean
     */
    public boolean isZh() {
        Locale locale = mContext.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }
}
