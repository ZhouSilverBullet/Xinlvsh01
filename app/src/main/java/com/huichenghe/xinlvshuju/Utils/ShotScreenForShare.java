package com.huichenghe.xinlvshuju.Utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;

import com.huichenghe.xinlvshuju.SDPathUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 截屏帮助类
 * Created by lixiaoning on 15-12-sos.
 */
public class ShotScreenForShare
{
//    private Activity mActivity;
    private static final String TAG = ShotScreenForShare.class.getSimpleName();
    private static ShotScreenForShare shotScreenForShare;
    private ShortScreenListener listener;
    public static ShotScreenForShare getInstance()
    {
        if(shotScreenForShare == null)
        {
            shotScreenForShare = new ShotScreenForShare();
        }
        return shotScreenForShare;
    }

    private ShotScreenForShare()
    {
//        this.mActivity = activity;
    }


    private Bitmap takeScreenShot(Activity activity)
    {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        Bitmap b = view.getDrawingCache();
        int bWidth = b.getWidth();
        int bHeight = b.getHeight();
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();
        int stateBarHeight = height - bHeight;
        int hei = height - stateBarHeight;
        Bitmap bitmap = Bitmap.createBitmap(b, 0, 0, width, hei);
        view.destroyDrawingCache();
        return bitmap;
    }


    /**
     * 保存图片
     * @param bitmap
     * @param fileName
     */
    private void savePic(Bitmap bitmap, final Activity ac, String fileName)
    {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        if(listener != null)
//        {
//            listener.onShortOverListener();
//        }
//        else
//        {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        while (listener == null)
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        listener.onShortOverListener();

    }


    public void takeshotScreen(final Activity a)
    {
        final Bitmap bitmap = takeScreenShot(a);
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                String pathSD = SDPathUtils.getSdcardPath() + File.separator + "mistepshareIcon.png";
//                Log.i(TAG, "保存截图路径:" + pathSD);
                savePic(bitmap, a, pathSD);
            }
        }).start();


    }


    public interface ShortScreenListener
    {
        void onShortOverListener();
    }

    public  void setOnShotListener(ShortScreenListener sh)
    {
        this.listener = sh;
    }


}
