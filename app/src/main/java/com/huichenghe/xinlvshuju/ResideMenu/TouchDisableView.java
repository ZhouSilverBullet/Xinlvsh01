package com.huichenghe.xinlvshuju.ResideMenu;



import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 主界面的最上层view
 */
class TouchDisableView extends ViewGroup {

    private View mContent;      // 声明本地视图

    //	private int mMode;
    private boolean mTouchDisabled = false;

    public TouchDisableView(Context context) {
        this(context, null);    // 一个参数的构造方法，调用两个参数的构造方法
    }

    public TouchDisableView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * 设置视图
     * @param v 视图对象
     */
    public void setContent(View v) {
        if (mContent != null) { // 如果不为空，移除视图
            this.removeView(mContent);
        }
        mContent = v;           // 传入的视图，复制到本地
        addView(mContent);      // 添加到试图组
    }

    /**
     * 获取当前视图
     * @return
     */
    public View getContent() {
        return mContent;
    }

    /**
     * 复写onMeasure方法，决定视图组的大小
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = getDefaultSize(0, widthMeasureSpec);       // 获取视图组的默认宽度
        int height = getDefaultSize(0, heightMeasureSpec);     // 获取视图组的默认高度
        setMeasuredDimension(width, height);                   // 设置宽高
        final int contentWidth = getChildMeasureSpec(widthMeasureSpec, 0, width);
        final int contentHeight = getChildMeasureSpec(heightMeasureSpec, 0, height);
        Log.i("", "onMeasure--width:" + width + " " + "onMeasure--height:" + height
                    + "onMeasure--contentWidth:" + contentWidth + " " + "onMeasure--contentHeight:" + contentHeight);
        mContent.measure(contentWidth, contentHeight);
    }

    /**
     * 设置视图组的布局及可视化范围
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = r - l;
        final int height = b - t;

        Log.i("", "onLayout--width" + width + " " + "onLayout--height" + height);
        mContent.layout(0, 0, width, height);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mTouchDisabled;
    }

    void setTouchDisable(boolean disableTouch) {
        mTouchDisabled = disableTouch;
    }

    boolean isTouchDisabled() {
        return mTouchDisabled;
    }
}
