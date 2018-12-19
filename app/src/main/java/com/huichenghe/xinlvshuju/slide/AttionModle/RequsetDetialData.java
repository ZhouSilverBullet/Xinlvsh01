package com.huichenghe.xinlvshuju.slide.AttionModle;

import android.content.Context;
import android.os.AsyncTask;

import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.http.HttpUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by lixiaoning on 2016/6/6.
 */
public class RequsetDetialData extends AsyncTask<String, Void, Boolean>
{
    private static final String TAG = RequsetDetialData.class.getSimpleName();
    private Context context;
    private AddAcctionCallback addAcctionCallback;
    String rusult = "";

    public RequsetDetialData(Context context)
    {
        this.context = context;
    }
    public void setAddAcctionCallback(AddAcctionCallback addAcctionCallback)
    {
        this.addAcctionCallback = addAcctionCallback;
    }
    @Override
    protected Boolean doInBackground(String... params)
    {
        String url = MyConfingInfo.WebRoot + "attention_friendHealth";
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", params[0]);
        map.put("timeType", "1");
        String cookie = LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.COOKIE_FOR_ME);
        try {
            HttpUtil.postDataAndImageHaveCookie(false, url, map, null, cookie, new HttpUtil.DealResponse() {
                @Override
                public boolean dealResponse(int responseCode, InputStream input) throws IOException
                {
                    rusult = dealTheResponse(input);
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
        return rusult != null && !rusult.equals("");
    }

    private String dealTheResponse(InputStream input) throws IOException
    {
        InputStreamReader read = new InputStreamReader(input);
        BufferedReader reader = new BufferedReader(read);
        String buff = "";
        String result = "";
        while ((buff = reader.readLine()) != null)
        {
            result = result + buff;
        }
//        Log.i(TAG, "详细情亲关注数据" + result);
        return result;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean)
    {
        super.onPostExecute(aBoolean);
        if(aBoolean)
        {
            if(addAcctionCallback != null)
            {
                addAcctionCallback.onSuccess(rusult);
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


}
