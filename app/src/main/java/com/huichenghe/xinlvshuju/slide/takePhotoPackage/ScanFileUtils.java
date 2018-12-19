package com.huichenghe.xinlvshuju.slide.takePhotoPackage;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

/**
 * Created by lixiaoning on 2016/6/17.
 */
public class ScanFileUtils implements MediaScannerConnection.MediaScannerConnectionClient
{
    public static final String TAG = ScanFileUtils.class.getSimpleName();
    private Context context;
    String scanPhth = null;
    private final String SCAN_TYPE = "image/png";
    private MediaScannerConnection connection;

    public ScanFileUtils(Context context)
    {
        this.context = context;
//        File file = new File(scanFilePath);
//        String[] photoList = file.list();
//        for (String s : photoList)
//        {
//            Log.i(TAG, "camera中的目录：" + s);
//        }
//        scanPhth = scanFilePath + photoList[photoList.length - 1];
//        Log.i(TAG, "camera中的第一个：" + scanPhth);
    }

    public void startScan(String scanContent)
    {
        this.scanPhth = scanContent;
        if(connection != null)
        {
            connection.disconnect();
        }
        connection = new MediaScannerConnection(context, this);
        connection.connect();
    }
    @Override
    public void onMediaScannerConnected()
    {
        connection.scanFile(scanPhth, SCAN_TYPE);
    }

    @Override
    public void onScanCompleted(String path, Uri uri)
    {
        Log.i(TAG, "camera路径" + path + "uri" + uri.getPath());
        try {
            if(uri != null)
            {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(uri);
                context.startActivity(intent);
            }
        }finally {
            connection.disconnect();
            connection = null;
        }


    }


    public void closeConnection()
    {
        if(connection != null && connection.isConnected())
        {
            connection.disconnect();
            connection = null;
        }
    }
}
