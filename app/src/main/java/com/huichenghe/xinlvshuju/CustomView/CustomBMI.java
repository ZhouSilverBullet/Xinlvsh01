package com.huichenghe.xinlvshuju.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.huichenghe.xinlvshuju.R;

/**
 * Created by lixiaoning on 2016/5/7.
 */
public class CustomBMI extends LinearLayout
{
    private ImageView progressType;
    private CustomRuler progressPointer;
    public CustomBMI(Context context) {
        super(context);
    }
    public CustomBMI(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }
    public CustomBMI(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    private void initView(Context mContext)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_bmi_layout, null);
        progressType = (ImageView)view.findViewById(R.id.progress_type);
        progressPointer = (CustomRuler)view.findViewById(R.id.progress_pointer);
    }

    public void setImageView(int res)
    {
        progressType.setImageResource(res);
    }

    public void setPointer(int pointer)
    {

    }



}
