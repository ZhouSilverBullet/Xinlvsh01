package com.huichenghe.xinlvshuju.slide.AttionModle;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.http.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by lixiaoning on 2016/6/6.
 */
public class lovingCareAddHelper extends AsyncTask<String, Void, Boolean>
{
    public static final String TAG = lovingCareAddHelper.class.getSimpleName();
    private Context context;
    private String content = "attention_addAttention";
    private String account = "account";
    private String birth = "birth";
    private String mark = "mark";
    private String code = "";
    private AddAcctionCallback addAcctionCallback;

    public lovingCareAddHelper(Context context)
    {
        this.context = context;
    }
    @Override
    protected Boolean doInBackground(String... params)
    {
        String urlS = MyConfingInfo.WebRoot + content;
        HashMap<String, String> ma = new HashMap<>();
        ma.put(account, params[0]);
        ma.put(birth, params[1]);
        ma.put(mark, params[2]);
        String cookie = LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.COOKIE_FOR_ME);
        try {
            HttpUtil.postDataAndImageHaveCookie(false, urlS, ma, null, cookie, new HttpUtil.DealResponse()
            {
                @Override
                public boolean dealResponse(int responseCode, InputStream input) throws IOException
                {
                    code = dealTheResponse(input);
                    return false;
                }
                @Override
                public void setHeader(String url, Object obj) {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code != null;
    }


    @Override
    protected void onPostExecute(Boolean aBoolean)
    {
        super.onPostExecute(aBoolean);
        if(aBoolean)
        {
            if(addAcctionCallback != null)
            {
                addAcctionCallback.onSuccess(code);
            }
        }
        else
        {
            if(addAcctionCallback != null)
            {
                addAcctionCallback.onFailed();
            }
        }
    }

    private String dealTheResponse(InputStream input) throws IOException
    {
        InputStreamReader reader = new InputStreamReader(input);
        BufferedReader read = new BufferedReader(reader);
        String result = "";
        String buff = "";
        String aa = "";
        while ((buff = read.readLine()) != null)
        {
            result = result + buff;
        }
        Log.i(TAG, "添加关注结果：" + result);
        try {
            JSONObject josn = new JSONObject(result);
            aa = josn.getString("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return aa;
    }

    public void setOnAddAcctionCallback(AddAcctionCallback addAcctionCallback)
    {
        this.addAcctionCallback = addAcctionCallback;
    }



}
