package com.huichenghe.xinlvshuju.mainpack;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by lixiaoning on 2016/7/14.
 */
public class BaseActivity extends AppCompatActivity
{
    @Override
    public Resources getResources()
    {
        Resources resources = super.getResources();
        try {

            Configuration configuration = new Configuration();
            configuration.setToDefaults();
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return resources;
    }



    public boolean canDoContinue()
    {
        return !isDestroyed() || !isFinishing();
    }
}
