package com.huichenghe.xinlvshuju;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by ${lixiaoning} on 16-info-11.
 */
public class TimeService extends Service
{

    private OnDateChangeListener dateListeners;
    private static final String TAG = TimeService.class.getSimpleName();

    private MyTimeBrocaset broadcast;
    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return new MYBinder();
    }



    class MYBinder extends Binder
    {
        public TimeService getService()
        {
            return TimeService.this;
        }
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        broadcast = new MyTimeBrocaset();
        registerReceiverFor();
        Log.i(TAG, "时间服务onCreate");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(broadcast);
    }

    private void registerReceiverFor()
    {

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        registerReceiver(broadcast, filter);
        Log.i(TAG, "注册广播onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }



    class MyTimeBrocaset extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if(action.equals(Intent.ACTION_DATE_CHANGED))
            {

                Log.i(TAG, "日期改变" + intent.getAction().toString());
                dateListeners.onDateChange();
            }
            if(action.equals(Intent.ACTION_TIME_CHANGED))
            {
                dateListeners.onTimeChange();
            }
        }
    }

   public void setOnDateChangeListener(OnDateChangeListener dateListener)
   {
       this.dateListeners = dateListener;
   }


    public interface OnDateChangeListener
    {
        void onDateChange();
        void onTimeChange();
    }

}
