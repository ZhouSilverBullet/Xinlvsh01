package com.huichenghe.xinlvshuju.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;


/**
 * Created by DIY on 2015/12/23.
 */
public class ProgressUtils
{
    private static ProgressUtils progressUtils;
    public ProgressDialog progressDialog;
    private ProgressUtils() {}
    private Activity activity;
    public static ProgressUtils getInstance()
    {
        if(progressUtils == null)
        {
            synchronized (ProgressUtils.class)
            {
                if(progressUtils == null)
                {
                    progressUtils = new ProgressUtils();
                }
            }
        }
        return progressUtils;
    }

    /**
     * 显示进度条
     *
     * @param
     */
    public void showProgressDialog(final Activity activity, String msg)
    {
        this.activity = activity;
        if((activity != null && activity.isFinishing()) || (activity != null && activity.isDestroyed()))
        {
            return;
        }
        if(progressDialog == null)
        {
            progressDialog = new ProgressDialog(activity);
        }
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(progressDialog!=null)
                {
                    progressDialog.cancel();
                    progressDialog=null;
                }
            }
        });
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    public void closeProgressDialog()
    {
        if(activity != null && !activity.isDestroyed() || activity != null && !activity.isFinishing())
        {
            if(progressDialog!=null)
            {
                try {
                    progressDialog.cancel();
                    progressDialog=null;
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        }
    }


    public boolean isShowing()
    {
        return progressDialog != null && progressDialog.isShowing();
    }

}
