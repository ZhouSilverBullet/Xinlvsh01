package com.huichenghe.xinlvshuju.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 保存图片到sd卡
 * Created by lixiaoning on 15-11-25.
 */
public class SaveImageUtil
{

    public static void saveImageToSD(Bitmap mBitmap, String path, String name)
    {
        if(mBitmap != null)
        {
            File file = new File(path);
            if(!file.exists())
            {
                file.mkdirs();
            }

            File fileI = new File(path + name);
            if(!fileI.exists())
            {
                try {
                    fileI.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            FileOutputStream outPut = null;
            try {
                outPut = new FileOutputStream(path + name, false);
                BufferedOutputStream bufferOut = new BufferedOutputStream(outPut);
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferOut);
                bufferOut.flush();
                bufferOut.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



    public static Bitmap readImageFormSD(String path)
    {
        File file = new File(path);
        if(file.exists())       // 文件存在再读取
        {
            FileInputStream in = null;
            try {
                in = new FileInputStream(path);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BufferedInputStream into = new BufferedInputStream(in);
            return BitmapFactory.decodeStream(into);
        }
        return null;
    }





}
