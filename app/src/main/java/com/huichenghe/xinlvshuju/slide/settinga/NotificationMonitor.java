package com.huichenghe.xinlvshuju.slide.settinga;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

/**
 * Created by lixiaoning on 2016/8/11.
 */
public class NotificationMonitor extends NotificationListenerService
{
    private final String RECEVER_NOTIFICATION = "recever_notification_from_listenerservice";
    private final String NOTIFICATION_TITLE = "notification_title";
    private final String NOTIFICATION_TEXT = "notification_text";
    private final String NOTIFICATION_PAK = "notification_pak";
    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn)
    {
//        super.onNotificationPosted(sbn);
        switchNotification(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn)
    {
        super.onNotificationRemoved(sbn);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void switchNotification(StatusBarNotification sbn)
    {
        String pak = sbn.getPackageName();
        String weiChart = "com.tencent.mm";
        String QQ = "com.tencent.mobileqq";
        String Facebook = "com.facebook.katana";
        String message = "com.facebook.orca";
        String WhatsApp = "com.whatsapp";
        String Twitter = "com.twitter.android";
        String Skype = "com.skype.rover";
        String sk = "com.skype.raider";
        String weiChartTwo = "com.tencent.vt05";
        if(pak != null)
        {
            sendMessageBroadcast(sbn, pak);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void sendMessageBroadcast(StatusBarNotification sbn, String pak)
    {
        Bundle bun = sbn.getNotification().extras;
        if(bun != null)
        {
            CharSequence charTitle = bun.getCharSequence(Notification.EXTRA_TITLE);
            CharSequence charText = bun.getCharSequence(Notification.EXTRA_TEXT);
            if(charTitle != null && charText != null)
            {
                Intent intent = new Intent(RECEVER_NOTIFICATION);
                intent.putExtra(NOTIFICATION_TITLE, charTitle.toString());
                intent.putExtra(NOTIFICATION_TEXT, charText.toString());
                intent.putExtra(NOTIFICATION_PAK, pak);
                this.sendOrderedBroadcast(intent, "private.recever.message.info");
            }
        }
    }
}
