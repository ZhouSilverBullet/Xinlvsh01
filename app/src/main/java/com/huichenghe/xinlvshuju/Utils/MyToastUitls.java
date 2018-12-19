package com.huichenghe.xinlvshuju.Utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by lixiaoning on 15-11-heartWarning.
 */
public class MyToastUitls
{
    private static Toast toast = null;
    public static void showToast(Context mContext, int msg, int length)
    {
        if(toast == null)
        {
            toast = Toast.makeText(mContext, msg, length == 1 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
        }
        else
        {
            toast.setText(msg);
            toast.setDuration(length == 1 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
        }
        if(length != 1)
        {
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        }
        toast.show();
    }


    public static void showToastInString (Context mContext, String msg, int length)
    {
        if(toast == null)
        {
            toast = Toast.makeText(mContext, msg, length == 1 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
        }
        else
        {
            toast.setText(msg);
            toast.setDuration(length == 1 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
        }
        toast.show();
    }


}
