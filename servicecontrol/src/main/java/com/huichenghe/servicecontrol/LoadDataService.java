package com.huichenghe.servicecontrol;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;

public class LoadDataService extends Service
{
    private static LoadDataService mLoadDataService;

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        return new UpBind();
    }

    public class  UpBind extends Binder
    {
        public LoadDataService getService()
        {
            return LoadDataService.this;
        }
    }

    public static LoadDataService getInstance()
    {
        return mLoadDataService;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        mLoadDataService = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return super.onStartCommand(intent, flags, startId);
    }

    public void UpdataHistroyData(SQLiteDatabase db)
    {

    }





}
