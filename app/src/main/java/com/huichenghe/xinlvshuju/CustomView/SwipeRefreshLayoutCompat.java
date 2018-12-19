package com.huichenghe.xinlvshuju.CustomView;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Created by DIY on 2016/sitting/18.
 */
public class SwipeRefreshLayoutCompat extends SwipeRefreshLayout
{
    // 是否存在滑动事件
    private boolean isHasDragger;
    // 纪录滑动的初始位置
    private float mDragX, mDragY;
    // 发出事件的最短距离.应该类似于容差值，即，用户滑动大于这个距离才开始处理滑动事件
    private int mTouchSlop;



    public SwipeRefreshLayoutCompat(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }





    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        int action = event.getAction();
        switch (action)
        {
                case MotionEvent.ACTION_DOWN:
                    // 记录手指按下的位置
                    mDragX = event.getX();
                    mDragY = event.getY();
                    //初始化左右滑动事件为false
                    isHasDragger = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    //如果左右滑动事件为true  直接返回false 不拦截事件
                    if (isHasDragger) {
                        return true;
                    }
                    // 获取当前手指位置
                    float endY = event.getY();
                    float endX = event.getX();
                    //获取X,Y滑动距离的绝对值
                    float distanceX = Math.abs(endX - mDragX);
                    float distanceY = Math.abs(endY - mDragY);
                    // 如果X轴位移大于Y轴距离，那么将事件交给其他控件
                    if (distanceY > mTouchSlop && distanceY > distanceX) {
                        isHasDragger = true;
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    //初始化左右滑动事件为false
                    isHasDragger = false;
                    break;
            }
            return super.onInterceptTouchEvent(event);

        }
//            case MotionEvent.ACTION_DOWN:
//                mDragX = event.getX();        // 纪录手指滑动的位置
//                mDragY = event.getY();
//                isHasDragger = false;       // 初始化是否有左右滑动为false
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if (isHasDragger)            // 如果左右滑动事件为true，不拦截交给下一层处理
//                {
//                    return true;
//                }
//                float moveX = event.getX();
//                float moveY = event.getY();
//                float distanceX = Math.abs(moveX - mDragX);
//                float distanceY = Math.abs(moveY - mDragY);
//                if(distanceY > mTouchSlop && distanceY > distanceX)
//                {
//                    isHasDragger = true;
//                    return true;
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                isHasDragger = false;
//                break;
//        }
//        return super.onInterceptTouchEvent(event);
//    }
}
