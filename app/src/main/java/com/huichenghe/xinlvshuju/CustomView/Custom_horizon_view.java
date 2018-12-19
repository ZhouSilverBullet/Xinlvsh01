package com.huichenghe.xinlvshuju.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by lixiaoning on 2016/11/1.
 */
public class Custom_horizon_view extends HorizontalScrollView
{


    public Custom_horizon_view(Context context) {
        super(context);
    }

    public Custom_horizon_view(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Custom_horizon_view(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
//        switch (ev.getAction())
//        {
//            case MotionEvent.ACTION_DOWN:
//                getParent().requestDisallowInterceptTouchEvent(false);
//                return true;
//            case MotionEvent.ACTION_MOVE:
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                getParent().requestDisallowInterceptTouchEvent(false);
//                break;
//        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }



}
