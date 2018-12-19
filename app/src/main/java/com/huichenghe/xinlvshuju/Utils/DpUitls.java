package com.huichenghe.xinlvshuju.Utils;

import android.content.Context;

/**像素单位转换类
 * Created by lixiaoning on 15-11-13.
 */
public class DpUitls
{


    /**
     * dp转px
     * @param mContext
     * @return
     */
    public static float DpToPx(Context mContext, float dp)
    {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float pxToDp(Context context, float value)
    {
        float density = context.getResources().getDisplayMetrics().density;
        return value / density + 0.5f;
    }

    /**
     * sp 转px
     * @param context
     * @param value
     * @return
     */
    public static float SpToPx(Context context, float value)
    {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return value * scaledDensity + 0.5f;
    }

    /**
     * px 转 sp
     * @param context
     * @param value
     * @return
     */
    public static float PxToSp(Context context, float value)
    {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return value / scaledDensity + 0.5f;
    }

}
