package com.huichenghe.xinlvshuju;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by lixiaoning on 2016/8/25.
 */
public class ForgroundService extends Service
{

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
//        startForeground(459, new Notification());
//        stopForeground(true);
//        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class MyBinder extends Binder
    {
        public ForgroundService getService()
        {
            return ForgroundService.this;
        }
    }
}
