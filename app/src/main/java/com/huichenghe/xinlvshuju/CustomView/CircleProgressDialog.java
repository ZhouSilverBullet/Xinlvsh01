package com.huichenghe.xinlvshuju.CustomView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import com.huichenghe.xinlvshuju.R;

/**
 * Created by lixiaoning on 2016/6/4.
 */
public class CircleProgressDialog
{
    private static CircleProgressDialog circleProgressDialog;
    private CircleProgressDialog(){}
    public static CircleProgressDialog getInstance()
    {
        if(circleProgressDialog == null)
        {
            synchronized (CircleProgressDialog.class)
            {
                if(circleProgressDialog == null)
                {
                    circleProgressDialog = new CircleProgressDialog();
                }
            }
        }
        return circleProgressDialog;
    }


    private Dialog dialog;

    public void showCircleProgressDialog(Context context)
    {
        Activity ac = (Activity)context;
        if(ac.isDestroyed() && ac.isFinishing())return;
        if(dialog == null)
        {
            dialog = new Dialog(context, R.style.mySizeDialog);
        }
        initView(context);
    }

    private void initView(Context context)
    {
        View view =  LayoutInflater.from(context).inflate(R.layout.circle_dialog_layout, null);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogs)
            {
                if(dialog != null)
                {
                    dialog.cancel();
                    dialog = null;
                }
            }
        });
        dialog.show();
    }

    public void closeCircleProgressDialog()
    {
        if(dialog != null)
        {
            if(dialog.isShowing())
            {
                dialog.dismiss();
            }
            dialog = null;
        }
    }

    public Dialog getDialog()
    {
        if(dialog != null)
            return dialog;
            return null;
    }

}
