package com.huichenghe.xinlvshuju.BleDeal;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.UpdataService.DeviceTypeUtils;
import com.huichenghe.xinlvshuju.http.UserAccountUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by lixiaoning on 2016/10/16.
 */
public class LoadingBloodData extends AsyncTask<String, Void, Boolean>
{
    private ArrayList<BloodDataEntity> bloodList;
    private BloodCallback callback;
    private Context context;
    public LoadingBloodData(Context context, BloodCallback callback)
    {
        this.context = context;
        this.callback = callback;
        bloodList = new ArrayList<>();
    }
    @Override
    protected Boolean doInBackground(String... params)
    {
        String date = params[0];
        String acc = UserAccountUtil.getAccount(context);
        Cursor cursor = MyDBHelperForDayData.getInstance(context).selectBloodData(context, acc, date, DeviceTypeUtils.getDeviceType(context));
        if(cursor == null)return false;
        if(cursor.getCount() > 0)
        {
            if(cursor.moveToFirst())
            {
                do {
                    String time = cursor.getString(cursor.getColumnIndex("time"));
                    int highPre = cursor.getInt(cursor.getColumnIndex("highPre"));
                    int lowPre = cursor.getInt(cursor.getColumnIndex("lowPre"));
                    int hr = cursor.getInt(cursor.getColumnIndex("heartRate"));
                    int spo2 = cursor.getInt(cursor.getColumnIndex("spo2"));
                    int hrv = cursor.getInt(cursor.getColumnIndex("hrv"));
                    BloodDataEntity en = new BloodDataEntity(hrv, date, time, highPre, lowPre, hr, spo2);
                    if(!bloodList.contains(en))
                    {
                        bloodList.add(en);
                    }
                }while (cursor.moveToNext());
            }
        }

        Collections.sort(bloodList, new Comparator<BloodDataEntity>() {
            @Override
            public int compare(BloodDataEntity lhs, BloodDataEntity rhs)
            {
                String[] timeOne = lhs.getTime().split(":");
                String[] timeTwo = rhs.getTime().split(":");
                int timeOnes = Integer.parseInt(timeOne[0]) * 60 + Integer.parseInt(timeOne[1]);
                int timeTwos = Integer.parseInt(timeTwo[0]) * 60 + Integer.parseInt(timeTwo[1]);
                if(timeOnes > timeTwos)
                {
                    return 1;
                }
                else if(timeOnes < timeTwos)
                {
                    return -1;
                }
                else
                {
                    return 0;
                }
            }
        });

        return true;
    }


    @Override
    protected void onPostExecute(Boolean aBoolean)
    {
        super.onPostExecute(aBoolean);
        callback.onDataCallback(bloodList);
    }
}
