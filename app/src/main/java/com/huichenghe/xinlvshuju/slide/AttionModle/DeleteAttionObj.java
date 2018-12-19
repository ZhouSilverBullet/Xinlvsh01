package com.huichenghe.xinlvshuju.slide.AttionModle;

import android.os.AsyncTask;
import android.util.Log;

import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.http.HttpUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by lixiaoning on 2016/6/13.
 */
public class DeleteAttionObj extends AsyncTask<String, Void, Boolean>
{
    private final String TAG = DeleteAttionObj.class.getSimpleName();
    private final String passiveUserId = "passiveUserId";
    @Override
    protected Boolean doInBackground(String... params)
    {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(passiveUserId, params[0]);
        String url = MyConfingInfo.WebRoot + "attention_deleteAttention";
        try {
            HttpUtil.postDataAndImageHaveCookie(false, url, hashMap, null, params[1], new HttpUtil.DealResponse()
            {
                @Override
                public boolean dealResponse(int responseCode, InputStream input) throws IOException
                {
                    parseDeleteCallback(input);
                    return false;
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

    private void parseDeleteCallback(InputStream input) throws IOException
    {
        InputStreamReader reader = new InputStreamReader(input);
        BufferedReader read = new BufferedReader(reader);
        String buff = "";
        String result = "";
        while ((buff = read.readLine()) != null)
        {
            result = result + buff;
        }
        Log.i(TAG, "删除关注对象结果"  + result);
    }
}
