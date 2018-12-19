package com.huichenghe.xinlvshuju;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by lixiaoning on 2016/8/23.
 */
public class SaveSDHelper
{
    public SaveSDHelper(String path)
    {
        File file = new File(path);
        if(!file.exists())
        {
            file.mkdirs();
        }
    }
    public void saveStringData(String target, String data)
    {
        File file = new File(target);
        if(file.exists())
        {
            if(file.length() > 1024 * 1024 * 100)
            {
                file.delete();
            }
        }
        if(!file.exists())
        {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileWriter writers = null;
//        FileOutputStream outputStream = null;
//        OutputStreamWriter writer = null;
        try {
//            outputStream = new FileOutputStream(file, true);
            writers = new FileWriter(file, true);
            long time=System.currentTimeMillis();
            Log.i("hahaha", "ssssss: ");
            writers.write(data);
//            writer = new OutputStreamWriter(outputStream, "utf-8");
//            writer.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
//                outputStream.flush();
//                outputStream.close();
//                writer.flush();
//                writer.close();
                writers.flush();
                writers.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
