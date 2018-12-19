package com.huichenghe.xinlvshuju;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.huichenghe.xinlvshuju.zhy.utils.ScreenUtils;
import com.nineoldandroids.view.ViewHelper;

/**
 * 测滑菜单，继承HorizontalScrollView
 * @author lixiaoning
 *
 */
public class SlidingMenu extends HorizontalScrollView
{

	public static final String TAG = "SlidingMenu";
	private GestureDetector gestureDetector;
	private OnTouchListener mGestureListener;

	private static final int PRESSED_DOWN = 0;	// 标识手指已按下
	private static final int PRESSED_MOVE_HORIZANTAL = 1;// 标识手指水平滑动
	private static final int PRESSED_MOVE_VERTICAL = 2;  // 标识手指垂直滑动
	private static final int PRESSED_UP = 3;			 // 标识手指松开


	int pressendState = PRESSED_UP;
	// 是否存在滑动事件
	private boolean isHasDragger;
	// 纪录滑动的初始位置
	private float mDragX, mDragY;
	// 发出事件的最短距离.应该类似于容差值，即，用户滑动大于这个距离才开始处理滑动事件
	private int mTouchSlop;

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
	private int mMenuWidth;
	private int mHalfMenuWidth;

	private boolean isOpen = false;

	private boolean once;

	private ViewGroup mMenu;
	private ViewGroup mContent;



	public SlidingMenu(Context context)
	{
		this(context, null, 0);
		gestureDetector = new GestureDetector(new HScrollDetector());

		setFadingEdgeLength(0);
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
	}

	public SlidingMenu(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);	// 默认调用此构造方法,在构造方法内部调用三参数的构造方法
		gestureDetector = new GestureDetector(new HScrollDetector());
		setFadingEdgeLength(0);
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
	}

	public SlidingMenu(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		gestureDetector = new GestureDetector(new HScrollDetector());
		setFadingEdgeLength(0);
		mScreenWidth = ScreenUtils.getScreenWidth(context);// 调用静态方法获取手机屏幕宽度
						// 从上下文检索自定义属性集
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
								 R.styleable.SlidingMenu, defStyle, 0);
		int n = a.getIndexCount();				// 获取自定义属性的个数
		for (int i = 0; i < n; i++)				// 遍历
		{
			int attr = a.getIndex(i);
			switch (attr)
			{
			case R.styleable.SlidingMenu_rightPadding:
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


	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		return super.onInterceptTouchEvent(ev) && gestureDetector.onTouchEvent(ev);
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
			mMenu = (ViewGroup) wrapper.getChildAt(0);		// 子布局的第一个元素
			mContent = (ViewGroup) wrapper.getChildAt(1);	// 子布局的第二个元素

			mMenuWidth = mScreenWidth - mMenuRightPadding;
			mHalfMenuWidth = mMenuWidth / 2;
			mMenu.getLayoutParams().width = mMenuWidth;		// 设置侧拉菜单宽度
			mContent.getLayoutParams().width = mScreenWidth;// 设置内容视图宽度

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
			this.scrollTo(mMenuWidth, 0);
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
//		Log.i(TAG, "侧拉菜单元素:" + count);
		int action = ev.getAction();
			switch (action)
			{
//				case MotionEvent.ACTION_DOWN:
//					// 记录手指按下的位置
//					mDragX = ev.getX();
//					mDragY = ev.getY();
//					//初始化左右滑动事件为false
//					isHasDragger = false;
//					break;
//				case MotionEvent.ACTION_MOVE:
//					//如果左右滑动事件为true  直接返回false 不拦截事件
//					if (isHasDragger) {
//						return true;
//					}
//					// 获取当前手指位置
//					float endY = ev.getY();
//					float endX = ev.getX();
//					//获取X,Y滑动距离的绝对值
//					float distanceX = Math.abs(endX - mDragX);
//					float distanceY = Math.abs(endY - mDragY);
//					// 如果X轴位移大于Y轴距离，那么将事件交给其他控件
//					if (distanceY > mTouchSlop && distanceY > distanceX) {
//						isHasDragger = true;
//						return true;
//					}
//					break;
				// Up时，进行判断，如果显示区域大于菜单宽度一半则完全显示，否则隐藏
				case MotionEvent.ACTION_UP:// 手指松开的位置

					int scrollX = getScrollX();
					Log.e(TAG, "走了这里.....scrollX: " + scrollX +"   " + "scrollY: " + getScrollY()
					+ "   " + getScaleX());

					if(scrollX > mMenuWidth/10 && isOpen)
					{
						// 关闭侧拉菜单
						this.smoothScrollTo(mMenuWidth, 0);
						isOpen = false;
						return true;// 返回true会立即执行
					}
					else if(scrollX < mMenuWidth - (mMenuWidth/10) && !isOpen)
					{
						// 打开侧拉菜单
						this.smoothScrollTo(0, 0);
						isOpen = true;
						return true;// 返回true会立即执行
					}

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
		if (isOpen)
		{
			this.smoothScrollTo(mMenuWidth, 0);
			isOpen = false;
		}
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

			super.onScrollChanged(l, t, oldl, oldt);
			float scale = l * 1.0f / mMenuWidth;
			float leftScale = 1 - 0.3f * scale;
			float rightScale = 0.8f + scale * 0.2f;

			ViewHelper.setScaleX(mMenu, leftScale);
			ViewHelper.setScaleY(mMenu, leftScale);
			ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
			ViewHelper.setTranslationX(mMenu, mMenuWidth * scale * 0.7f);

			ViewHelper.setPivotX(mContent, 0);
			ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
			ViewHelper.setScaleX(mContent, rightScale);
			ViewHelper.setScaleY(mContent, rightScale);


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
