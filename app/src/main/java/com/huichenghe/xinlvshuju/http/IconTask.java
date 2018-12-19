package com.huichenghe.xinlvshuju.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.huichenghe.xinlvshuju.SDPathUtils;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 获取第三方头像
 */
public class IconTask extends AsyncTask<String, Void, Boolean> {
    Bitmap bt = null;

    @Override
    protected Boolean doInBackground(String... params) {
//            try {
//                URL url = new URL(params[0]);
//                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
//                conn.setRequestMethod("GET");
//                conn.setDoInput(true);
//                conn.setDoOutput(true);
//                conn.setConnectTimeout(6 * 1000);
//                conn.setUseCaches(false);
//                conn.setReadTimeout(6 * 1000);
//                conn.setInstanceFollowRedirects(true);
//                conn.connect();
//                bt = BitmapFactory.decodeStream(conn.getInputStream());
//                conn.disconnect();
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        FileOutputStream outputStream = null;
        try {
            URL url = new URL(params[0]);
            bt = BitmapFactory.decodeStream(url.openStream());
            String iconPath = SDPathUtils.getSdcardPath() + MyConfingInfo.IMAGEVIEW_SAVE_PATH;
            outputStream = new FileOutputStream(iconPath);
            bt.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bt != null;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean) {
//            takePortrait.setImageBitmap(bt);
            if(bitmapBack != null)
            bitmapBack.onBitmapBack(bt);
        }
    }

    private onBitmapBack bitmapBack;
    public void setOnBitmapBack(onBitmapBack back)
    {
        this.bitmapBack = back;
    }




}
