package com.huichenghe.xinlvshuju.zhy.utils;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by DIY on 2016/sitting/16.
 */
public class BaseActivity extends AppCompatActivity
{
    @Override
    public Resources getResources()
    {
        Resources res = super.getResources();
        Configuration configuration = new Configuration();
        configuration.setToDefaults();
        res.updateConfiguration(configuration, res.getDisplayMetrics());
        return res;
    }
}
