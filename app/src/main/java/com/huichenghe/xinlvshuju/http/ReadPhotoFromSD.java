package com.huichenghe.xinlvshuju.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by lixiaoning on 2016/6/3.
 */
public class ReadPhotoFromSD extends AsyncTask<File, Void, Boolean>
{
    Bitmap bitmap = null;
    @Override
    protected Boolean doInBackground(File... params)
    {
        File file = params[0];
        try {
            FileInputStream inputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            bitmap = BitmapFactory.decodeStream(bufferedInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
            if(bitmapBack != null)
            {
                bitmapBack.onBitmapError();
            }
        }
    }
    private onBitmapBack bitmapBack;
    public void setOnBitmapBack(onBitmapBack bitmapBack)
    {
        this.bitmapBack = bitmapBack;
    }




}
