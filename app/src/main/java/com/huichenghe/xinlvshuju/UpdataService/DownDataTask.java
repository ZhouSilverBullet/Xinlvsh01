package com.huichenghe.xinlvshuju.UpdataService;

import android.content.Context;
import android.os.AsyncTask;

import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.http.HttpUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * 下载数据任务
 * Created by lixiaoning on 2016/12/12.
 */

public class DownDataTask extends AsyncTask<String, Void, Boolean>
{
    private HashMap<String, String> dataMap;
    private Context context;
    private SendDataCallback downCallback;
    private final String downUrl = MyConfingInfo.updateTextUrl + "download_allData";
    public DownDataTask(HashMap<String, String> data, Context context, SendDataCallback downCallback)
    {
        this.dataMap = data;
        this.context = context;
        this.downCallback = downCallback;
    }
    private static DownDataTask downDataTask;
    public static DownDataTask getInstance(HashMap<String, String> data, Context context, SendDataCallback downCallback)
    {
        if(downDataTask == null)
        {
            synchronized (DownDataTask.class)
            {
                if(downDataTask == null)
                {
                    downDataTask = new DownDataTask(data, context, downCallback);
                }
            }
        }
        return downDataTask;
    }

    @Override
    protected Boolean doInBackground(String... strings)
    {
        try {
            HttpUtil.postDataAndImageHaveCookie(false, downUrl, dataMap, null, strings[0], new HttpUtil.DealResponse() {
                @Override
                public boolean dealResponse(int responseCode, InputStream input) throws IOException
                {
                    String relult = parseInput(input);
                    downCallback.sendDataSuccess(relult);
                    return false;
                }

                @Override
                public void setHeader(String url, Object obj){}
            });
        } catch (IOException e) {
            e.printStackTrace();
            downCallback.sendDataFailed(e.toString());
        }
        return null;
    }

    /**
     * 读取返回数据
     * @param input
     * @return
     */
    private String parseInput(InputStream input) throws IOException
    {
        InputStreamReader read = new InputStreamReader(input);
        BufferedReader reader = new BufferedReader(read);
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = reader.readLine()) != null)
        {
            result.append(line);
        }
        return result.toString();
    }
}
