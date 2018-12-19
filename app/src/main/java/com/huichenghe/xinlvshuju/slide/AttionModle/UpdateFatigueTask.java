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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by lixiaoning on 2016/6/12.
 */
public class UpdateFatigueTask extends AsyncTask<Integer, Void, Boolean>
{
    public final String TAG = UpdateFatigueTask.class.getSimpleName();
    private Context context;
    private final String fatigue = "fatigue";
    private final String monitorTime = "monitorTime";
    public UpdateFatigueTask(Context context)
    {
        this.context = context;
    }
    @Override
    protected Boolean doInBackground(final Integer... params)
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String date = format.format(calendar.getTime());
        HashMap<String, String> hashData = new HashMap<>();
        hashData.put(fatigue, String.valueOf(params[0]));
        hashData.put(monitorTime, date);
        String url = MyConfingInfo.WebRoot + "uploadData_index";
        String cookie = LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.COOKIE_FOR_ME);
        try {
            HttpUtil.postDataAndImageHaveCookie(false, url, hashData, null, cookie, new HttpUtil.DealResponse() {
                @Override
                public boolean dealResponse(int responseCode, InputStream input) throws IOException
                {
                    parseTheUpdateFatigueResult(input);
                    return false;
                }
                @Override
                public void setHeader(String url, Object obj) {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void parseTheUpdateFatigueResult(InputStream input) throws IOException
    {
        InputStreamReader read = new InputStreamReader(input);
        BufferedReader reader = new BufferedReader(read);
        String buff = "";
        String result = "";
        while ((buff = reader.readLine()) != null)
        {
            result = result + buff;
        }
//        Log.i(TAG, "上传疲劳值结果" + result);
    }
}
