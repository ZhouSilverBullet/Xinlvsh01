package com.huichenghe.xinlvshuju.slide;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;
import com.huichenghe.xinlvshuju.Utils.WebGlobalConfig;
import com.huichenghe.xinlvshuju.http.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;

/**
 * 此类进行登陆业务
 * Created by lixiaoning on 15-11-24.
 */
public class Sub_login
{
    public static final String TAG = Sub_login.class.getSimpleName();
    private Context mContext;
    public Sub_login(Context mContext)
    {
        this.mContext = mContext;
    }
    private final String LOGIN_CONNETION = "user_login?account=";
    String pwd = "&password=";
    private String imageFileName = null;

    /**
     * 进行登陆请求
     */
    public boolean requestLogin(String mAccount, String mPassword) throws IOException
    {
        String password = WebGlobalConfig.get32MD5Str(mPassword);
        String result = "";
        URL url = null;             // 链接对象
        HttpURLConnection conn = null;     // connecte对象
        String urlString = MyConfingInfo.WebRoot + LOGIN_CONNETION + mAccount + pwd + password;
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
        conn.setConnectTimeout(60 * 1000);
        conn.setReadTimeout(60 * 1000);
        // 设置请求方式
        try {
            conn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        conn.setUseCaches(false);
        conn.setInstanceFollowRedirects(true);
        conn.connect();
        InputStreamReader reader = new InputStreamReader(conn.getInputStream(), "UTF-8");
        String cookies = conn.getHeaderField("Set-Cookie");
        String cookie = null;
        if(cookies != null)
        {
            cookie = cookies.substring(0, cookies.indexOf(";"));
        }
//        Log.i(TAG, "当前登录的cookie:" + cookies);
        LocalDataSaveTool.getInstance(mContext.getApplicationContext()).writeSp(MyConfingInfo.COOKIE_FOR_ME, cookie);
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
        if(result != null && !result.equals(""))
        {
            try {
                isSuccess = parserJSON(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            if(mContext instanceof Activity)
            {
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyToastUitls.showToast(mContext, R.string.login_outtime_failed, 1);
                    }
                });
            }
        }
        return isSuccess;
    }


    public Bitmap getImageIcon()
    {
        Bitmap mBitmap = null;
        if(imageFileName != null)
        {
            try {
                mBitmap = requestPersionIcon(imageFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mBitmap;
    }


    public void ThirdLogin(String actionUrl, HashMap<String, String> data)
    {

        try {

            HttpUtil.postDataAndImage(mContext, false, actionUrl, data, null, new HttpUtil.DealResponse()
            {
                @Override
                public boolean dealResponse(int responseCode, InputStream input) throws IOException
                {
                    Log.i(TAG, "第三方系统响应吗：" + responseCode);
                    InputStreamReader reader = new InputStreamReader(input);
                    BufferedReader br = new BufferedReader(reader);
                    String result = "";
                    String line = "";
                    while ((line = br.readLine()) != null)
                    {
                        result = result + line;
                    }

                    Log.i(TAG, "第三方登录后返回的数据：" + result);
                    onThirdLoginBack.onThirdLoginBack(result);
                    return false;
                }
                @Override
                public void setHeader(String url, Object obj) {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private OnThirdLoginBack onThirdLoginBack;
    public interface OnThirdLoginBack
    {
        void onThirdLoginBack(String requesCode);
    }
    public void setOnThirdLoginBack(OnThirdLoginBack back)
    {
        this.onThirdLoginBack = back;
    }
    private Bitmap requestPersionIcon(String name) throws IOException {
        URL rul = new URL(MyConfingInfo.WebRequeIcon + "?filename=" + name);
        HttpURLConnection conne = null;
            conne = (HttpURLConnection)rul.openConnection();
            // 设置连接超时
            conne.setReadTimeout(6 * 1000);
            // 设置自动重定向
            conne.setInstanceFollowRedirects(true);
            // 设置请求方式
        try {
            conne.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
            String cookie = LocalDataSaveTool.getInstance(mContext.getApplicationContext()).readSp(MyConfingInfo.COOKIE_FOR_ME);
            conne.setRequestProperty("Cookie", cookie);
            conne.setDoInput(true);
            conne.setDoOutput(true);
            conne.setConnectTimeout(6 * 1000);  // 设置连接超时
            conne.setUseCaches(false);          // 不使用缓存
            conne.connect();
            InputStream in = conne.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            in.close();
            conne.disconnect();
        return  bitmap;
    }


    /**
     * 解析服务器返回的字符串
     * @param result
     */
    public boolean parserJSON(String result) throws JSONException
    {
        Log.i(TAG, "登陆返回信息:" + result);
        JSONObject json = new JSONObject(result);
        String code = json.getString("code");
        String msg = json.getString("msg");
        String data = json.getString("data");
        Log.i(TAG, "code:" + code);
        // 代表登陆成功
        if(code.equals("9003"))
        {
            Log.i(TAG, "code:clock" + code);
            JSONObject jsonObject = new JSONObject(data);
            String nick = jsonObject.getString("nick");
            String email = jsonObject.getString("email");
            String birthdate = jsonObject.getString("birthdate");
            String gender = jsonObject.getString("gender");
            String height = jsonObject.getString("height");
            String weight = jsonObject.getString("weight");
            String header = jsonObject.getString("header");
            /**
             * 写入缓存
             */
            saveSp(MyConfingInfo.USER_NICK, nick);
            saveSp(MyConfingInfo.USER_EMAIL, email);
            saveSp(MyConfingInfo.USER_BIRTHDAY, birthdate);
            saveSp(MyConfingInfo.USER_GENDER, gender);
            saveSp(MyConfingInfo.USER_HEIGHT, height);
            saveSp(MyConfingInfo.USER_WEIGHT, weight);

            imageFileName = header;
            return true;    // 登陆成功返回true

        // 登陆超时
        }else if(code.equals("9001"))
        {
            Log.i(TAG, "code:phone" + code);
            if(mContext instanceof Activity)
            {
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, mContext.getString(R.string.login_outtime), Toast.LENGTH_SHORT).show();
                    }
                });
            }


        // 账号秘密错误
        }else if(code.equals("9002"))
        {
            Log.i(TAG, "code:info" + code);
            if(mContext instanceof Activity)
            {
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyToastUitls.showToast(mContext, R.string.login_wrong, 1);
                    }
                });
            }

        }
        else if(code.equals("9004"))
        {
            if(mContext instanceof Activity)
            {
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyToastUitls.showToast(mContext, R.string.login_wrong, 1);
                    }
                });
            }

        }
        return false;
    }

    /**
     * 对象存入sp的简化
     * @param key
     * @param value
     */
    private void saveSp(String key, String value)
    {
        LocalDataSaveTool.getInstance(mContext.getApplicationContext()).writeSp(key, value);
    }



}
