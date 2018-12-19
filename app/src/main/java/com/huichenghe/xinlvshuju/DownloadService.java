package com.huichenghe.xinlvshuju;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.MalformedInputException;

/**
 * Created by DIY on 2015/12/22.
 */
public class DownloadService extends Service {
    private final static int DOWNLOAD_COMPLETE = -2;

    private final static int DOWNLOAD_FAIL = -1;

    //自定义通知栏类
    private UpdateNotification myNotification;

    private String filePathString; //下载文件绝对路径(包括文件名)

    //通知栏跳转Intent
    private Intent updateIntent = null;
    private PendingIntent updatePendingIntent = null;

    private DownFileThread downFileThread;  //自定义文件下载线程

    private Handler updateHandler = new  Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case DOWNLOAD_COMPLETE:
                    //点击安装PendingIntent
                    /*Uri uri = Uri.fromFile(downFileThread.getApkFile());
                    Intent installIntent = new Intent(Intent.ACTION_VIEW);
                    installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
                    updatePendingIntent = PendingIntent.getActivity(DownloadService.this, 0, installIntent, 0);
                    myNotification.changeContentIntent(updatePendingIntent);
                    myNotification.notification.defaults=Notification.DEFAULT_SOUND;//铃声提醒
                    myNotification.changeNotificationText("下载完成，请点击安装！");*/
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(downFileThread.getApkFile()),
                            "application/vnd.android.package-archive");
                    startActivity(intent);
                    myNotification.removeNotification();
                    //停止服务
                    stopSelf();
                    break;
                case DOWNLOAD_FAIL:
                    //下载失败
                    myNotification.changeNotificationText(getString(R.string.file_download_failed));
                    stopSelf();
                    break;
                default:  //下载中
                    Log.i("service", "default" + msg.what);

                    myNotification.changeProgressStatus(msg.what);
            }
        }
    };

    public DownloadService() {
        Log.i("service","DownloadServices1");

    }



    @Override
    public void onDestroy() {
        Log.i("service","onDestroy");
        if(downFileThread!=null)
            downFileThread.interuptThread();
        stopSelf();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.i("service","onStartCommand");
        String downUrl=intent.getStringExtra("url");
        long size=intent.getIntExtra("size",0);
        updateIntent = new Intent();
        PendingIntent   updatePendingIntent = PendingIntent.getActivity(this,0,updateIntent,0);
        myNotification = new UpdateNotification(this, updatePendingIntent, 1);
        //显示notification
        myNotification.showCustomizeNotification(R.mipmap.ic_luncher, getString(R.string.app_name), R.layout.remoteviews2);

        filePathString = SDPathUtils.getSdcardPath() + "/xinlvshouhuan.apk";
        //开启下载
        downFileThread = new DownFileThread(updateHandler, downUrl, filePathString,size);
        new Thread(downFileThread).start();
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    /**
     * Created by DIY on 2015/12/22.
     */
    public static class DownFileThread implements Runnable {
        public final static String TAG = "DownFileThread";
        Handler mHandler; //传入的Handler,用于像Activity或service通知下载进度
        String urlStr;  //下载URL
        File apkFile;   //文件保存路径
        boolean isFinished; //下载是否完成
        boolean interupted = false;  //是否强制停止下载线程
        long fileSize;

        public DownFileThread(Handler handler, String urlStr, String filePath, long fileSize) {
            Log.i(TAG, urlStr);
            this.mHandler = handler;
            this.urlStr = urlStr;
            apkFile = new File(filePath);
            isFinished = false;
            this.fileSize = fileSize;
        }

        public File getApkFile() {
            if (isFinished)
                return apkFile;
            else
                return null;
        }

        /**
         * 强行终止文件下载
         */
        public void interuptThread() {
            interupted = true;
        }

        @Override
        public void run() {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                java.net.URL url = null;
                HttpURLConnection conn = null;
                InputStream iStream = null;
                int requestCode = 0;

                try {
                    url = new java.net.URL(urlStr);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(6000);
                    conn.setReadTimeout(60000);
                    requestCode = conn.getResponseCode();
                    iStream = conn.getInputStream();
                } catch (MalformedInputException e) {
                    Log.i(TAG, "MalformedURLException");
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.i(TAG, "获得输入流失败");
                    e.printStackTrace();
                }

                    FileOutputStream fos = null;
                    Log.i("tag", "" + requestCode);
                    try {
                        fos = new FileOutputStream(apkFile);
                    } catch (FileNotFoundException e) {
                        Log.i(TAG, "获得输出流失败：new FileOutputStream(apkFile);");
                        e.printStackTrace();
                    }


                    BufferedInputStream bis = new BufferedInputStream(iStream);
                    byte[] buffer = new byte[1024];
                    int len;
                    long length = fileSize;            // 获取文件总长度
                    double rate = (double) 100 / length;  //最大进度转化为100
                    int total = 0;
                    int times = 0;//设置更新频率，频繁操作ＵＩ线程会导致系统奔溃
                    try {
                        Log.i("threadStatus", "开始下载");
                        while (false == interupted && ((len = bis.read(buffer)) != -1)) {
                            fos.write(buffer, 0, len);
                            // 获取已经读取长度
                            total += len;
                            int p = (int) (total * rate);
                            Log.i("num", rate + "," + total + "," + p);
                            if (times >= 512 || p == 100) {/*
                        这是防止频繁地更新通知，而导致系统变慢甚至崩溃。*/
                                Log.i("time", "time");
                                times = 0;
                                Message msg = Message.obtain();
                                msg.what = p;
                                mHandler.sendMessage(msg);
                            }
                            times++;
                        }
                        fos.close();
                        bis.close();
                        iStream.close();
                        if (total == length) {
                            isFinished = true;
                            mHandler.sendEmptyMessage(DOWNLOAD_COMPLETE);
                            Log.i(TAG, "下载完成结束");
                        }
                    } catch (IOException e) {
                        Log.i(TAG, "异常中途结束");
                        mHandler.sendEmptyMessage(DOWNLOAD_FAIL);
                        e.printStackTrace();
                    }

            } else {
                Log.i(TAG, "外部存储卡不存在，下载失败！");
                mHandler.sendEmptyMessage(DOWNLOAD_FAIL);
            }
        }
    }

    /**
     * Created by DIY on 2015/12/23.
     */
    public static class UpdateNotification {
        Context mContext;   //Activity或Service上下文
        Notification notification;  //notification
        NotificationManager nm;
        String titleStr;   //通知标题
        String contentStr; //通知内容
        PendingIntent contentIntent; //点击通知后的动作
        int notificationID;   //通知的唯一标示ID
        int iconID;         //通知栏图标
        long when = System.currentTimeMillis();
        RemoteViews remoteView=null;  //自定义的通知栏视图
        /**
         *
         * @param context Activity或Service上下文
         * @param contentIntent  点击通知后的动作
         * @param id    通知的唯一标示ID
         */
        public UpdateNotification(Context context,PendingIntent contentIntent,int id) {
            // TODO Auto-generated constructor stub
            mContext=context;
            notificationID=id;
            this.contentIntent=contentIntent;
            this.nm=(NotificationManager)mContext.getSystemService(NOTIFICATION_SERVICE);
        }

        /**
         * 显示自定义通知
         * @param icoId 自定义视图中的图片ID
         * @param titleStr 通知栏标题
         * @param layoutId 自定义布局文件ID
         */
        public void showCustomizeNotification(int icoId,String titleStr,int layoutId) {
            this.titleStr=titleStr;
            /*notification=new Notification(icoId, titleStr, when);
            notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.contentIntent=this.contentIntent;*/

            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                notification = new Notification.Builder(mContext)
                        .setSmallIcon(R.mipmap.ic_luncher)
                        .setWhen(when)
                        .setTicker(titleStr)
                        .setAutoCancel(false)
                        .setOnlyAlertOnce(true)
                        .setContentIntent(contentIntent)
                        .build();
            }else {
                notification = new Notification.Builder(mContext)
                        .setSmallIcon(R.mipmap.ic_lunerwhile)
                        .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_luncher))
                        .setWhen(when)
                        .setTicker(titleStr)
                        .setAutoCancel(false)
                        .setOnlyAlertOnce(true)
                        .setContentIntent(contentIntent)
                        .setColor(Color.MAGENTA)
                        .build();
            }

            // clock、创建一个自定义的消息布局 view.xml
            // phone、在程序代码中使用RemoteViews的方法来定义image和text。然后把RemoteViews对象传到contentView字段
            if(remoteView==null)
            {
                remoteView = new RemoteViews(mContext.getPackageName(),layoutId);
                remoteView.setImageViewResource(R.id.tv_appicon, R.mipmap.ic_luncher);
                remoteView.setTextViewText(R.id.tv_title_notification, titleStr);
                remoteView.setTextViewText(R.id.tv_tip, mContext.getString(R.string.start_download));
                remoteView.setProgressBar(R.id.progressBar2, 100, 0, false);
                notification.contentView = remoteView;
            }
            nm.notify(notificationID, notification);
        }
        /**
         * 更改自定义布局文件中的进度条的值
         * @param p 进度值(0~100)
         */
        public void changeProgressStatus(int p)
        {
            if(notification.contentView!=null)
            {
                if(p==DOWNLOAD_FAIL)
                    notification.contentView.setTextViewText(R.id.tv_tip , mContext.getString(R.string.download_failed));
                else if(p==100)
                    notification.contentView.setTextViewText(R.id.tv_tip , mContext.getString(R.string.download_complete));
                else
                    notification.contentView.setTextViewText(R.id.tv_tip , mContext.getString(R.string.download_progress) + "("+p+")%");
                notification.contentView.setProgressBar(R.id.progressBar2, 100, p, false);
            }
            nm.notify(notificationID, notification);
        }
        public void changeContentIntent(PendingIntent intent)
        {
            this.contentIntent=intent;
            notification.contentIntent=intent;
        }

        /**
         * 改变通知栏的通知内容
         * @param content
         */
        public void changeNotificationText(String content)
        {
            nm.notify(notificationID, notification);
        }

        /**
         * 移除通知
         */
        public void removeNotification()
        {
            // 取消的只是当前Context的Notification
            nm.cancel(notificationID);
        }

    }
}
