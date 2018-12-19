package com.huichenghe.xinlvshuju.CustomView;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * 自定义SeekBar，
 * Created by lixiaoning on 15-11-11.
 */
public class MyTargetProgress extends SeekBar
{
    private MyTargetProgress myTargetProgress;
    public MyTargetProgress(Context context)
    {
        this(context, null);

    }

    public MyTargetProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyTargetProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyTargetProgress(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


}
