package com.huichenghe.xinlvshuju.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.huichenghe.xinlvshuju.SDPathUtils;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by lixiaoning on 2016/6/3.
 */
public class IconTaskForNomal extends AsyncTask<String, Void, Boolean>
{
    public static final String TAG = IconTaskForNomal.class.getSimpleName();
    private Bitmap bitmap = null;
    private Context context;
    public IconTaskForNomal(Context context)
    {
        this.context = context;
    }
    @Override
    protected Boolean doInBackground(String... params)
    {
        Log.i(TAG, "获取亲情关注头像2" + params[0]);
        HttpURLConnection conn;
        try {
            URL url = new URL(params[0]);
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(6 * 1000);
            conn.setRequestProperty("Cookie", LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.COOKIE_FOR_ME));
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setReadTimeout(6 * 1000);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.connect();
            bitmap = BitmapFactory.decodeStream(conn.getInputStream());
//            Log.i(TAG, "获取亲情关注头像2" + bitmap);
            if(bitmap == null)
            {
                return false;
            }
//            String path = MyConfingInfo.SDCARD_DATA_ROOT_DIR + File.separator + params[1];
//            Log.i(TAG, "获取亲情关注头像" + path);
//            Log.i(TAG, "获取亲情关注头像" + params[1]);
            String names = params[1];
            String[] name = names.split("\\.");
//            Log.i(TAG, "获取亲情关注头像" + name);
            String pa = SDPathUtils.getSdcardPath() + MyConfingInfo.SDCARD_DATA_ROOT_DIR + File.separator + name[0] + ".jpg";
//            Log.i(TAG, "获取亲情关注头像" + params[1]);
            File file = new File(pa);
            if(!file.exists())file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);
            outputStream.flush();
            outputStream.close();
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

        }
        return bitmap != null;
    }


    @Override
    protected void onPostExecute(Boolean aBoolean)
    {
        super.onPostExecute(aBoolean);

        if(aBoolean)
        {
            if(bitmapBack != null)
            {
                bitmapBack.onBitmapBack(bitmap);
            }
        }
        else
        {
            bitmapBack.onBitmapError();
        }
    }

    private onBitmapBack bitmapBack;
    public void setOnBitmapBack(onBitmapBack onback)
    {
        this.bitmapBack = onback;
    }



}
