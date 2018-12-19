package com.huichenghe.xinlvshuju;

import android.content.Context;

/**
 * Created by lixiaoning on 2016/7/25.
 */
public class DensityUtils
{
    public static float getLineWidth(Context context)
    {
        float densityWidth = 1.0f;
        float density = context.getResources().getDisplayMetrics().density;
        if(density <= 1.5)
        {
    //            paint.setStrokeWidth(1.5f);
    //            dens = 0.85f;
            densityWidth = 1.5f;
        }
        else if( density > 1.5 && density <= 2.0)
        {
//            paint.setStrokeWidth(2.0f);
//            dens = 1.0f;
            densityWidth = 2.0f;
        }
        else if(density > 2.0 && density <= 3.0)
        {
//            paint.setStrokeWidth(2.5f);				// 心率曲线的宽度
//            dens = 1.4f;
            densityWidth = 2.5f;
        }
        else
        {
//            paint.setStrokeWidth(3.0f);				// 心率曲线的宽度
//            dens = 1.7f;
            densityWidth = 3.0f;
        }
        return densityWidth;
    }
}
