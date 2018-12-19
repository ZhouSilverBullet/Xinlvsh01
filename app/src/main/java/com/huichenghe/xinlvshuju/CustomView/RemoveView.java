package com.huichenghe.xinlvshuju.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.DpUitls;

/**
 *
 * Created by lixiaoning on 16-clock-12.
 */
public class RemoveView extends HorizontalScrollView
{
    public static final String TAG = RemoveView.class.getSimpleName();
    /**
     * 屏幕宽度
     */
    private int mScreenWidth;
    /**
     * dp
     */
    private int mMenuRightPadding;
    /**
     * 菜单的宽度
     */
    private int mainWidth;
    private int mHalfWidth;

    private boolean isOpen = false;

    private boolean once;

    private Context context;



    public RemoveView(Context context)
    {
        this(context, null, 0);
        this.context = context;


        setFadingEdgeLength(0);
    }

    public RemoveView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);	// 默认调用此构造方法,在构造方法内部调用三参数的构造方法
        setFadingEdgeLength(0);
    }

    public void setWidth(int width)
    {
        mScreenWidth = width;
    }



    public RemoveView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        setFadingEdgeLength(0);
        mScreenWidth = getScreenWidth(context) - (int)DpUitls.DpToPx(context, 86);// 调用静态方法获取手机屏幕宽度
        // 从上下文检索自定义属性集
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.RemoveView, defStyle, 0);
        int n = a.getIndexCount();				// 获取自定义属性的个数
        for (int i = 0; i < n; i++)				// 遍历
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.RemoveView_rightwidth:
                    // 默认50
                    mMenuRightPadding = a.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension
                                    (TypedValue.COMPLEX_UNIT_DIP, 50f,
                                            getResources().getDisplayMetrics()));// 默认为10DP
                    break;
            }
        }
        a.recycle();


    }

    private int getScreenWidth(Context mContext)
    {
        WindowManager manager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);

        return metrics.widthPixels;

    }



    /**
     * 尺寸设置
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        /**
         * 显示的设置一个宽度
         */
        if (!once)
        {	// 获取第一个子布局,事实上也就只有一个布局
            LinearLayout wrapper = (LinearLayout) getChildAt(0);
            ViewGroup mainPager = (ViewGroup) wrapper.getChildAt(0);
            ViewGroup deletePager = (ViewGroup) wrapper.getChildAt(1);

//            mMenuWidth = mScreenWidth - mMenuRightPadding;
//            mHalfMenuWidth = mMenuWidth / phone;
            deletePager.getLayoutParams().width = mMenuRightPadding;		// 设置侧拉菜单宽度
            mainPager.getLayoutParams().width = mScreenWidth;// 设置内容视图宽度

        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    /**
     * 布局改变
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);
        if (changed)
        {
            // 打开菜单
            this.scrollTo(mainWidth - mMenuRightPadding, 0);
            once = true;
        }
    }


    private float lastActionDownX,lastActionDownY;
    boolean DoIt = false;
    boolean isok = false;

    private float moveStartX, moveStartY;
    private float moveOffsetX = 0;
    private int touchState = 0;



    /**
     * 此方法是对触摸事件的处理
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        int count = this.getChildCount();
        Log.i(TAG, "侧拉菜单元素:" + count);
        int action = ev.getAction();
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:






                break;

            case MotionEvent.ACTION_MOVE:






                break;


            // Up时，进行判断，如果显示区域大于菜单宽度一半则完全显示，否则隐藏
            case MotionEvent.ACTION_UP:// 手指松开的位置
//                toggle();
                break;

//                int scrollX = getScrollX();
//                Log.e(TAG, "走了这里.....scrollX: " + scrollX +"   " + "scrollY: " + getScrollY()
//                        + "   " + getScaleX());
//
//                if(scrollX > mMenuWidth/10 && isOpen)
//                {
//                    // 关闭侧拉菜单
//                    this.smoothScrollTo(mMenuWidth, 0);
//                    isOpen = false;
//                    return true;// 返回true会立即执行
//                }
//                else if(scrollX < mMenuWidth - (mMenuWidth/10) && !isOpen)
//                {
//                    // 打开侧拉菜单
//                    this.smoothScrollTo(0, 0);
//                    isOpen = true;
//                    return true;// 返回true会立即执行
//                }

        }

        return super.onTouchEvent(ev);


    }

//	private void doSubTouch(MotionEvent ev)
//	{
//		View v = this.getChildAt(0);
//	}


    /**
     * 打开菜单
     */
    public void openMenu()
    {
        if (isOpen)
            return;
        this.smoothScrollTo(0, 0);
        isOpen = true;
    }

    /**
     * 关闭菜单
     */
    public void closeMenu()
    {
            this.smoothScrollTo(mMenuRightPadding, 0);
            isOpen = false;
    }

    /**
     * 切换菜单打开关闭状态
     */
    public void toggle()
    {
        if (isOpen)
        {
            closeMenu();
        } else
        {
            openMenu();
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {
//        float scale = l * clock.0f / mMenuWidth;
//        float leftScale = clock - 0.3f * scale;
//        float rightScale = 0.8f + scale * 0.2f;
//
//        ViewHelper.setScaleX(mMenu, leftScale);
//        ViewHelper.setScaleY(mMenu, leftScale);
//        ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (clock - scale));
//        ViewHelper.setTranslationX(mMenu, mMenuWidth * scale * 0.7f);
//
//        ViewHelper.setPivotX(mContent, 0);
//        ViewHelper.setPivotY(mContent, mContent.getHeight() / phone);
//        ViewHelper.setScaleX(mContent, rightScale);
//        ViewHelper.setScaleY(mContent, rightScale);
        super.onScrollChanged(l, t, oldl, oldt);
//


    }



    class HScrollDetector extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            return Math.abs(distanceX) > Math.abs(distanceY);


        }
    }
}
