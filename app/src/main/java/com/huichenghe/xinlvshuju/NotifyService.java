package com.huichenghe.xinlvshuju;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.huichenghe.bleControl.Ble.BluetoothLeService;

import java.util.Set;

public class NotifyService extends NotificationListenerService
{
    public final static String TAG = NotifyService.class.getSimpleName();
    private byte[] dataContent;
    private final int GET_NOTIFYTION = 0;
    private Handler serviceHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case GET_NOTIFYTION:
//                    StatusBarNotification[] notification = getActiveNotifications();
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        Log.d(TAG, "启动通知");
        super.onCreate();
//        serviceHandler.sendMessageDelayed(serviceHandler.obtainMessage(GET_NOTIFYTION), 60);
        Notification.Builder notificatains = new Notification.Builder(this);
//        notificatains.setSmallIcon(R.mipmap.movement_icon);
//        notificatains.setAutoCancel(false);
//        notificatains.setContentTitle(getString(R.string.app_name));
//        notificatains.setContentText("今天："+ 100 + "步");
//        notificatains.setShowWhen(true);
//        notificatains.setWhen(System.currentTimeMillis());
        notificatains.setPriority(Notification.PRIORITY_MAX);
        notificatains.build();
        startForeground(1, notificatains.getNotification());

    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "销毁通知");
        super.onDestroy();
//        startService(new Intent(this, NotifyService.class));
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return super.onBind(intent);
    }


    public static boolean isServiceRunning(Context context)
    {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(context);
        for (String s : packageNames)
        {
            Log.i(TAG, "运行的监听服务：" + s + "当前包名：" + context.getPackageName());
        }
        if(packageNames.contains(context.getPackageName()))
        {
            return true;
        }
        return false;
    }



    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn)
    {
//        super.onNotificationPosted(sbn);
        Log.i(TAG, "获取到通知：的包名" + sbn.getPackageName());
        if(BluetoothLeService.getInstance() == null)
        {
            return;
        }

//        Notification notification = sbn.getNotification();
//        Bundle bundle = notification.extras;
//        String text = (String)bundle.getCharSequence(Notification.EXTRA_TEXT);
//        String title = (String)bundle.getCharSequence(Notification.EXTRA_TITLE);
//        String bigTEXT = (String)bundle.getCharSequence(Notification.EXTRA_BIG_TEXT);
//        String infoTExt = (String)bundle.getCharSequence(Notification.EXTRA_INFO_TEXT);
//        String subTExt = (String)bundle.getCharSequence(Notification.EXTRA_SUB_TEXT);
//        String summary = (String)bundle.getCharSequence(Notification.EXTRA_SUMMARY_TEXT);
//        Log.i(TAG, "获取到通知title：" + title);
//        Log.i(TAG, "获取到通知text：" + text);
//        Log.i(TAG, "获取到通知bigTEXT：" + bigTEXT);
//        Log.i(TAG, "获取到通知infoTExt：" + infoTExt);
//        Log.i(TAG, "获取到通知subTExt：" + subTExt);
//        Log.i(TAG, "获取到通知summary：" + summary);

        Notification notification = sbn.getNotification();
        Bundle bundle = notification.extras;
        String packName = sbn.getPackageName();
//        comparePackageNameAndSend(packName, bundle);
    }



    private void getContentFromWeiChart(Bundle bundle)
    {
        String title = (String)bundle.getCharSequence(Notification.EXTRA_TITLE);
        String text = (String)bundle.getCharSequence(Notification.EXTRA_TEXT);
        Log.d(TAG, "获取到通知：" + text + "title:" + title);
        if(text != null && text.equals(""))
        {
            return;
        }
//        getCanSendByte(text);
    }





    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
//        super.onNotificationRemoved(sbn);
    }


}
