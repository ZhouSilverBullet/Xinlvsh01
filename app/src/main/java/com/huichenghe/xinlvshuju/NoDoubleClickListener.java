package com.huichenghe.xinlvshuju;

import android.view.View;

import java.util.Calendar;

/**
 * Created by lixiaoning on 2016/7/16.
 */
public abstract class  NoDoubleClickListener implements View.OnClickListener
{
    private int MIN_TIME_INTERVAL = 600;
    private long lastTime = 0;

    @Override
    public void onClick(View v)
    {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if(Math.abs(currentTime - lastTime) > MIN_TIME_INTERVAL)
        {
            lastTime = currentTime;
            onNoDoubleClick(v);
        }
    }
    public abstract void onNoDoubleClick(View view);
}
