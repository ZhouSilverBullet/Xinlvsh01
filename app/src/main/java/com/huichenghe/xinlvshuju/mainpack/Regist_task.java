package com.huichenghe.xinlvshuju.mainpack;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.huichenghe.xinlvshuju.http.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lixiaoning on 2016/11/15.
 */
public class Regist_task extends AsyncTask<String, Void, Boolean>
{
    public final String TAG = "Regist_task";
    private Context context;
    private registCallback callback;
    private final String parms = "user.account";

    public Regist_task(Context context, registCallback callback)
    {
        this.context = context;
        this.callback = callback;
    }



    @Override
    protected Boolean doInBackground(String... params)
    {
        String account = params[0];
        String url = params[1];
        Map<String, String> maps = new HashMap<>();
        maps.put(parms, account);
        try {
            HttpUtil.postDataAndImage(context, false, url, maps, null, new HttpUtil.DealResponse()
            {
                @Override
                public boolean dealResponse(int responseCode, InputStream input) throws IOException
                {
                    InputStreamReader inputStreamReader = new InputStreamReader(input);
                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    String readLine = "";
                    String result = "";
                    while ((readLine = reader.readLine()) != null )
                    {
                        result += readLine;
                    }
                    return parseResult(result);
                }
                @Override
                public void setHeader(String url, Object obj)
                {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(Boolean aBoolean)
    {
        super.onPostExecute(aBoolean);
    }

    private boolean parseResult(String result)
    {
        Log.i(TAG, "注册测试的结果：" + result);
        try {
            JSONObject json = new JSONObject(result);
            String code = json.getString("code");
            if(code != null && code.equals("9003") || code != null && code.equals("9005"))
            {
                if(callback != null)
                {
                    callback.registBack(true);
                }
                return true;
            }
            else
            {
                if(callback != null)
                {
                    callback.registBack(false);
                }
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }


    public interface registCallback
    {
        void registBack(boolean re);
    }

}
