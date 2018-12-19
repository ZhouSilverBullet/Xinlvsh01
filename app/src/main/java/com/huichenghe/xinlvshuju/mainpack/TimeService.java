package com.huichenghe.xinlvshuju.mainpack;

import android.app.Service;
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
        registerReceiverFor();
        Log.i(TAG, "时间服务onCreate");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    private void registerReceiverFor()
    {

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        Log.i(TAG, "注册广播onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
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
