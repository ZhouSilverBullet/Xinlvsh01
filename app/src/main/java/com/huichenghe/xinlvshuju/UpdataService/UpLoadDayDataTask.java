package com.huichenghe.xinlvshuju.UpdataService;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.huichenghe.xinlvshuju.http.HttpUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by lixiaoning on 2016/12/7.
 */

public class UpLoadDayDataTask extends AsyncTask<String, Void, Boolean>
{
    public static final String TAG = "updateData";
    private Context context;
    private Map<String, String> args;
    private SendDataCallback sendDataCallback;

    public UpLoadDayDataTask(Context context, Map<String, String> map, SendDataCallback callback)
    {
        Log.i(TAG, "UpLoadDayDataTask创建异步任务构造方法");
        this.context = context;
        this.sendDataCallback = callback;
        this.args = map;
    }
    private String parseResult(InputStream input)
    {
        InputStreamReader in = new InputStreamReader(input);
        BufferedReader read = new BufferedReader(in);
        StringBuffer sult = new StringBuffer();
        String line = "";
        try {
            while ((line = read.readLine()) != null)
            {
                sult.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sult.toString();
    }
    @Override
    protected Boolean doInBackground(String... strings)
    {
        Log.i(TAG, "UpLoadDayDataTask线程开始上传数据:");
        try {
//            Log.i(TAG, "线程上传数据登录成功postDataAndImageHaveCookie:");
            HttpUtil.postDataAndImageHaveCookie(false, strings[1], args, null,  strings[0], new HttpUtil.DealResponse() {
                @Override
                public boolean dealResponse(int responseCode, InputStream input) throws IOException
                {
                    String result = parseResult(input);
                    Log.i(TAG, "线程上传数据成功UpLoadDayDataTask:" + result);
                    if(sendDataCallback != null)
                    sendDataCallback.sendDataSuccess(result);
                    return false;
                }
                @Override
                public void setHeader(String url, Object obj){}
            });
        } catch (IOException e) {
            sendDataCallback.sendDataFailed("上传失败" + e.toString());
            e.printStackTrace();
        }
        return null;
    }
}
