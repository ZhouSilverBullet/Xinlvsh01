package com.huichenghe.xinlvshuju.CustomView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by lixiaoning on 2016/11/2.
 */
public class Custom_recycler_view extends RecyclerView
{
    public Custom_recycler_view(Context context) {
        super(context);
    }

    public Custom_recycler_view(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Custom_recycler_view(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }




    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


}
