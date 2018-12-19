package com.huichenghe.xinlvshuju.ResideMenu;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huichenghe.xinlvshuju.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;


/**
 *侧拉菜单类
 */
public class ResideMenu extends FrameLayout {

	public static final int DIRECTION_LEFT = 0;
	public static final int DIRECTION_RIGHT = 1;
	private static final int PRESSED_MOVE_HORIZANTAL = 2;
	private static final int PRESSED_DOWN = 3;
	private static final int PRESSED_DONE = 4;
	private static final int PRESSED_MOVE_VERTICAL = 5;

	private ImageView imageViewShadow;		// 阴影
	private ImageView imageViewBackground;	// 侧拉背景
	private LinearLayout layoutLeftMenu;	// 左侧侧拉布局
	private LinearLayout layoutRightMenu;	// 右侧侧拉布局
	private LinearLayout layoutInfo;		//
	private LinearLayout layoutSetting;
	private RelativeLayout leftMenu;		//	左侧侧拉
	private RelativeLayout rightMenu;
	private RelativeLayout scrollViewMenu;
	/** the activity that view attach to 侧拉所依附的activity*/
	private Activity activity;
	/** the decorview of the activity 	 activty大装饰布局*/
	private ViewGroup viewDecor;
	/** the viewgroup of the activity */
	private TouchDisableView viewActivity;
	/** the flag of menu open status 	 标识侧拉菜单开合的的状态*/
	private boolean isOpened;

	private float shadowAdjustScaleX;
	private float shadowAdjustScaleY;
	/** the view which don't want to intercept touch event 不拦截触摸事件的view*/
	private List<View> ignoredViews;
	private List<ResideMenuItem> leftMenuItems;	// 左侧侧拉的条目
	private List<ResideMenuItem> rightMenuItems;// 右侧侧拉的条目
	private DisplayMetrics displayMetrics = new DisplayMetrics();
	private OnMenuListener menuListener;		// 侧拉监听
	private float lastRawX;			// 标识x坐标的位置
	private float lastRawY;
	private boolean isInIgnoredView = false;
	private int scaleDirection = DIRECTION_LEFT;
	private int pressedState = PRESSED_DOWN;
	private List<Integer> disabledSwipeDirection = new ArrayList<Integer>();
	// valid scale factor is between 0.0f and 1.0f.
	private TextView exitButton, loginButton;
	private float mScaleValue = 0.4f;
	private float mScaleValueY = 1.0f;
	private boolean isLocked = false;

	public ResideMenu(Context context) {
		super(context);
		initViews(context);
	}

	private void initViews(Context context)
	{
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.residemenu, this);								// 侧拉布局
		leftMenu = (RelativeLayout) findViewById(R.id.rl_left_menu);				// 左侧侧拉相对布局
		rightMenu = (RelativeLayout) findViewById(R.id.rl_right_menu);			// 右侧侧拉相对布局
		imageViewShadow = (ImageView) findViewById(R.id.iv_shadow);					// 阴影
		layoutLeftMenu = (LinearLayout) findViewById(R.id.layout_left_menu);		//	左侧侧拉item
		layoutRightMenu = (LinearLayout) findViewById(R.id.layout_right_menu);	//	右侧侧拉item
		imageViewBackground = (ImageView) findViewById(R.id.iv_background);		//	侧拉背景
		layoutInfo = (LinearLayout) findViewById(R.id.layout_info);				// 头部用户信息布局
		layoutSetting = (LinearLayout) findViewById(R.id.layout_setting);		// 底部布局
		exitButton = (TextView)findViewById(R.id.exit_button);
		loginButton = (TextView)findViewById(R.id.logOut_button);
	}

	public TextView getExitButton()
	{
		return exitButton;
	}

	public TextView getLoginButton()
	{
		return loginButton;
	}

	/**
	 * use the method to set up the activity which residemenu need to show;
	 * 调用此方法设置侧拉需要显示的Activity
	 * @param activity
	 */
	public void attachToActivity(Activity activity)
	{
		initValue(activity);
		setShadowAdjustScaleXByOrientation();
		setViewPadding();
	}


	/**
	 * 初始化
	 * @param activity
	 */
	private void initValue(Activity activity)
	{
		this.activity = activity;
		leftMenuItems = new ArrayList<ResideMenuItem>();	// 左侧条目集合
		rightMenuItems = new ArrayList<ResideMenuItem>();	// 右侧条目集合
		ignoredViews = new ArrayList<View>();				//
		viewDecor = (ViewGroup) activity.getWindow().getDecorView();	// 通过activity获取window再获取decorView
		View mContent = viewDecor.getChildAt(0);			// 下标为0的子布局
		viewDecor.removeViewAt(0);
		viewActivity = new TouchDisableView(this.activity);				// 自定义ViewGroup
		viewActivity.setContent(mContent);
		addView(viewActivity);								// 添加到当前对象中
		viewDecor.addView(this, 0);
	}

	private void setShadowAdjustScaleXByOrientation() {
		int orientation = getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			shadowAdjustScaleX = 0.034f;
			shadowAdjustScaleY = 0.12f;
		} else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			shadowAdjustScaleX = 0.06f;
			shadowAdjustScaleY = 0.07f;
		}
	}

	public void menuLock()
	{
		isLocked = true;
	}

	public void menuUnLock()
	{
		isLocked = false;
	}


	/**
	 * set the menu background picture;
	 * 设置侧拉的背景
	 * @param imageResrouce
	 */
	public void setBackground(int imageResrouce) {
		imageViewBackground.setImageResource(imageResrouce);
	}

	/**
	 * the visiblity of shadow under the activity view;
	 * 设置阴影是否可见
	 * @param isVisible
	 */
//	public void setShadowVisible(boolean isVisible) {
//		if (isVisible)
//			imageViewShadow.setImageResource(R.drawable.shadow);
//		else
//			imageViewShadow.setVisibility(View.GONE);
//	}

	/**
	 * 添加用户信息视图,即头部
	 * 
	 * @param menuInfo
	 */
	public void addMenuInfo(ResideMenuInfo menuInfo) {
		layoutInfo.addView(menuInfo);// ViepGroup的添加视图方法
	}

	/**
	 *
	 * 添加侧拉条目
	 * add a single items to left menu;
	 * 
	 * @param menuItem
	 */
	@Deprecated
	public void addMenuItem(ResideMenuItem menuItem) {
		this.leftMenuItems.add(menuItem);	// 将侧拉条目添加到list集合
		layoutLeftMenu.addView(menuItem);	// 添加到ViewGroup
	}

	/**
	 * 按不同的放向添加，分为左右两个方向
	 * add a single items;
	 * 
	 * @param menuItem
	 * @param direction
	 */
	public void addMenuItem(ResideMenuItem menuItem, int direction) {
		if (direction == DIRECTION_LEFT) {	// 左侧
			this.leftMenuItems.add(menuItem);
			layoutLeftMenu.addView(menuItem);
		} else {							// 右侧
			this.rightMenuItems.add(menuItem);
			layoutRightMenu.addView(menuItem);
		}
	}

	/**
	 * 设置侧拉条目
	 * set the menu items by array list to left menu;
	 * 
	 * @param menuItems
	 */
	@Deprecated
	public void setMenuItems(List<ResideMenuItem> menuItems) {
		this.leftMenuItems = menuItems;
		rebuildMenu();
	}

	/**
	 * set the menu items by array list;
	 * 
	 * @param menuItems
	 * @param direction
	 */
	public void setMenuItems(List<ResideMenuItem> menuItems, int direction) {
		if (direction == DIRECTION_LEFT)
			this.leftMenuItems = menuItems;
		else
			this.rightMenuItems = menuItems;
		rebuildMenu();
	}


	/**
	 * 重新构建侧拉item
	 */
	private void rebuildMenu() {
		layoutLeftMenu.removeAllViews();
		layoutRightMenu.removeAllViews();
		for (int i = 0; i < leftMenuItems.size(); i++)
			layoutLeftMenu.addView(leftMenuItems.get(i), i);
		for (int i = 0; i < rightMenuItems.size(); i++)
			layoutRightMenu.addView(rightMenuItems.get(i), i);
	}

	/**
	 * 获取左侧所有的item
	 * get the left menu items;
	 * 
	 * @return
	 */
	@Deprecated
	public List<ResideMenuItem> getMenuItems() {
		return leftMenuItems;
	}

	/**
	 * 获取制定方向的item
	 * get the menu items;
	 * 
	 * @return
	 */
	public List<ResideMenuItem> getMenuItems(int direction) {
		if (direction == DIRECTION_LEFT)
			return leftMenuItems;
		else
			return rightMenuItems;
	}

	/**
	 * if you need to do something on the action of closing or opening menu, set
	 * the listener here.
	 * 
	 * @return
	 */
	public void setMenuListener(OnMenuListener menuListener) {
		this.menuListener = menuListener;
	}

	public OnMenuListener getMenuListener() {
		return menuListener;
	}

	/**
	 * we need the call the method before the menu show, because the padding of
	 * activity can't get at the moment of onCreateView();
	 */
	private void setViewPadding() {
		this.setPadding(viewActivity.getPaddingLeft(),
						viewActivity.getPaddingTop(),
						viewActivity.getPaddingRight(),
						viewActivity.getPaddingBottom());
	}

	/**
	 * 打开侧拉
	 * show the reside menu;
	 */
	public void openMenu(int direction)
	{
		setScaleDirection(direction);		// 设置主界面移动到的位置
		isOpened = true;
		AnimatorSet scaleDown_activity = buildScaleDownAnimation(viewActivity,
				mScaleValue, mScaleValueY);	// 构建侧拉时动画
		AnimatorSet scaleDown_shadow = buildScaleDownAnimation(imageViewShadow,
				mScaleValue + shadowAdjustScaleX, mScaleValueY
						+ shadowAdjustScaleY);// 构建侧拉阴影动画
		AnimatorSet alpha_menu = buildMenuAnimation(scrollViewMenu, 1.0f, 1.0f, 1.0f);// 设置侧拉淡入
		scaleDown_activity.addListener(animationListener);
		scaleDown_activity.playTogether(scaleDown_shadow);
		scaleDown_activity.playTogether(alpha_menu);
		scaleDown_activity.start();
	}

	/**
	 * close the reslide menu;
	 */
	public void closeMenu() {

		isOpened = false;
		AnimatorSet scaleUp_activity = buildScaleUpAnimation(viewActivity,
				1.0f, 1.0f);
		AnimatorSet scaleUp_shadow = buildScaleUpAnimation(imageViewShadow,
				1.0f, 1.0f);
		AnimatorSet alpha_menu = buildMenuAnimation(scrollViewMenu, 0.0f, mScaleValueY, mScaleValueY);	// 设置侧拉淡入
		scaleUp_activity.addListener(animationListener);
		scaleUp_activity.playTogether(scaleUp_shadow);
		scaleUp_activity.playTogether(alpha_menu);
		scaleUp_activity.start();
	}

	@Deprecated
	public void setDirectionDisable(int direction) {
		disabledSwipeDirection.add(direction);
	}

	public void setSwipeDirectionDisable(int direction) {
		disabledSwipeDirection.add(direction);
	}

	private boolean isInDisableDirection(int direction) {
		return disabledSwipeDirection.contains(direction);
	}

	/**
	 * 设置主界面整体布局移动到的位置
	 * @param direction
	 */
	private void setScaleDirection(int direction) {

		int screenWidth = getScreenWidth();		// 获取屏幕宽度
		float pivotX;							// x轴
		float pivotY = getScreenHeight() * 0.5f;	// y轴显示在屏幕中间
		float pivotScrollMenuX;
		float pivotScrollMenuY = getScreenHeight() * 0.5f;

		if (direction == DIRECTION_LEFT) {
			scrollViewMenu = leftMenu;					// 左侧侧拉布局
			pivotX = screenWidth * 1.5f;					// 改变侧拉布局宽度属性
			pivotScrollMenuX = screenWidth * -0.5f;
		} else {
			scrollViewMenu = rightMenu;				// 右侧侧拉布局
			pivotX = screenWidth * -0.5f;
			pivotScrollMenuX = screenWidth * 1.5f;
		}

		ViewHelper.setPivotX(viewActivity, pivotX);
//		ViewHelper.setPivotY(viewActivity, pivotY);
//		ViewHelper.setPivotX(scrollViewMenu, pivotScrollMenuX);
//		ViewHelper.setPivotY(scrollViewMenu, pivotScrollMenuY);
		ViewHelper.setPivotX(imageViewShadow, pivotX);
//		ViewHelper.setPivotY(imageViewShadow, pivotY);
		scaleDirection = direction;
	}

	/**
	 * return the flag of menu status;
	 * 
	 * @return
	 */
	public boolean isOpened() {
		return isOpened;
	}

	private OnClickListener viewActivityOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			if (isOpened())
				closeMenu();
		}
	};

	private Animator.AnimatorListener animationListener = new Animator.AnimatorListener() {
		@Override
		public void onAnimationStart(Animator animation) {
			if (isOpened()) {
				scrollViewMenu.setVisibility(VISIBLE);
				if (menuListener != null)
					menuListener.openMenu();
			}
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			// reset the view;
			if (isOpened()) {
				viewActivity.setTouchDisable(true);
				viewActivity.setOnClickListener(viewActivityOnClickListener);
			} else {
				viewActivity.setTouchDisable(false);
				viewActivity.setOnClickListener(null);
				scrollViewMenu.setVisibility(GONE);
				if (menuListener != null)
					menuListener.closeMenu();
			}
		}

		@Override
		public void onAnimationCancel(Animator animation) {

		}

		@Override
		public void onAnimationRepeat(Animator animation) {

		}
	};

	/**
	 * 一种辅助方法来生成缩放动画；
	 * a helper method to build scale down animation;
	 * 
	 * @param target
	 * @param targetScaleX
	 * @param targetScaleY
	 * @return
	 */
	private AnimatorSet buildScaleDownAnimation(View target,
			float targetScaleX, float targetScaleY)
	{
		AnimatorSet scaleDown = new AnimatorSet();
		scaleDown.playTogether(
				ObjectAnimator.ofFloat(target, "scaleX", targetScaleX),
				ObjectAnimator.ofFloat(target, "scaleY", targetScaleY));

		scaleDown.setInterpolator(AnimationUtils.loadInterpolator(activity,
				android.R.anim.decelerate_interpolator));
		scaleDown.setDuration(120);
		return scaleDown;
	}

	/**
	 * 一种辅助方法来建立大规模的动画；
	 * a helper method to build scale up animation;
	 * 
	 * @param target
	 * @param targetScaleX
	 * @param targetScaleY
	 * @return
	 */
	private AnimatorSet buildScaleUpAnimation(View target, float targetScaleX,
			float targetScaleY) {

		AnimatorSet scaleUp = new AnimatorSet();
		scaleUp.playTogether(
				ObjectAnimator.ofFloat(target, "scaleX", targetScaleX),
				ObjectAnimator.ofFloat(target, "scaleY", targetScaleY));

		scaleUp.setDuration(120);
		return scaleUp;
	}

	private AnimatorSet buildMenuAnimation(View target, float alpha, float targetScaleX, float targetScaleY) {

		AnimatorSet alphaAnimation = new AnimatorSet();
		alphaAnimation.playTogether(ObjectAnimator.ofFloat(target, "alpha",
				alpha));
		
//		alphaAnimation.playTogether(
//				ObjectAnimator.ofFloat(target, "scaleX", targetScaleX),
//				ObjectAnimator.ofFloat(target, "scaleY", targetScaleY));

		alphaAnimation.setDuration(120);
		return alphaAnimation;
	}

	/**
	 * 如果有一些视图你不想驻留菜单来拦截他们的触摸事件，你可以使用该方法来设置。
	 * if there ware some view you don't want reside menu to intercept their
	 * touch event,you can use the method to set.
	 * 
	 * @param v
	 */
	public void addIgnoredView(View v) {
		ignoredViews.add(v);
	}

	/**
	 * 从忽略视图列表中删除视图
	 * remove the view from ignored view list;
	 * 
	 * @param v
	 */
	public void removeIgnoredView(View v) {
		ignoredViews.remove(v);
	}

	/**
	 * clear the ignored view list;
	 */
	public void clearIgnoredViewList() {
		ignoredViews.clear();
	}

	/**
	 * if the motion evnent was relative to the view which in ignored view
	 * list,return true;
	 * 
	 * @param ev
	 * @return
	 */
	private boolean isInIgnoredView(MotionEvent ev) {
		Rect rect = new Rect();
		for (View v : ignoredViews) {
			v.getGlobalVisibleRect(rect);
			if (rect.contains((int) ev.getX(), (int) ev.getY()))
				return true;
		}
		return false;
	}

	private void setScaleDirectionByRawX(float currentRawX) {
		if (currentRawX < lastRawX)
			setScaleDirection(DIRECTION_RIGHT);
		else
			setScaleDirection(DIRECTION_LEFT);
	}

	/**
	 *	此方法获取缩放目标
	 * @param currentRawX	当前点击的位置相对于屏幕的x坐标
	 * @return
	 */
	private float getTargetScale(float currentRawX) {
		float scaleFloatX = ((currentRawX - lastRawX) / getScreenWidth()) * 1.0f;
		scaleFloatX = scaleDirection == DIRECTION_RIGHT ? -scaleFloatX
				: scaleFloatX;
		float targetScale = ViewHelper.getScaleX(viewActivity) - scaleFloatX;
		targetScale = targetScale > 1.0f ? 1.0f : targetScale;
		targetScale = targetScale < 0.5f ? 0.5f : targetScale;
		return targetScale;
	}
	
	private float getTargetScaleMenu(float currentRawX) {
		float scaleFloatX = ((currentRawX - lastRawX) / getScreenWidth()) * 1.0f;
		scaleFloatX = scaleDirection == DIRECTION_RIGHT ? scaleFloatX
				: -scaleFloatX;
		float targetScale = ViewHelper.getScaleX(scrollViewMenu) - scaleFloatX;
		targetScale = targetScale > 1.0f ? 1.0f : targetScale;
		targetScale = targetScale < 0.5f ? 0.5f : targetScale;
		return targetScale;
	}
	
	
	private float getTargetScaleY(float currentRawX) {
		float scaleFloatY = ((currentRawX - lastRawX) / getScreenHeight()) * 0.7f;
		float targetScale = ViewHelper.getScaleY(viewActivity) - scaleFloatY;
		targetScale = targetScale > 1.0f ? 1.0f : targetScale;
		targetScale = targetScale < 0.5f ? 0.5f : targetScale;
		return targetScale;
	}
	
	private float getTargetScaleMenuY(float currentRawX) {
		float scaleFloatY = ((currentRawX - lastRawX) / getScreenHeight()) * 0.7f;
		float targetScale = ViewHelper.getScaleY(scrollViewMenu) - scaleFloatY;
		targetScale = targetScale > 1.0f ? 1.0f : targetScale;
		targetScale = targetScale < 0.5f ? 0.5f : targetScale;
		return targetScale;
	}

//	private float getTargetScaleY(float currentRawY)
//		{
//float scaleFloatY = ((currentRawY - lastRawY)/getScreenHeight()) * 5f;
//
//float targetScaleY = ViewHelper.getScaleY(viewActivity) - scaleFloatY;
//targetScaleY = targetScaleY > 1.0f ? 1.0f : targetScaleY;
//targetScaleY = targetScaleY < 0.5f ? 0.5f : targetScaleY;
//
//return targetScaleY;
//}
	private float lastActionDownX, lastActionDownY;
	/**
	 * 处理滑动分发事件
	 * @param ev
	 * @return
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		float currentActivityScaleX = ViewHelper.getScaleX(viewActivity);	// 当前view的缩放比
		if (currentActivityScaleX == 1.0f)					// 1.0代表全屏
			setScaleDirectionByRawX(ev.getRawX());
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:	// 点击屏幕时执行此处
			lastActionDownX = ev.getX();// 点击位置的x坐标
			lastActionDownY = ev.getY();// 点击位置的y坐标
			isInIgnoredView = isInIgnoredView(ev) && !isOpened();
			pressedState = PRESSED_DOWN;
			break;
		case MotionEvent.ACTION_MOVE:	// 手指在屏幕上移动时，执行此处
			if (isInIgnoredView || isInDisableDirection(scaleDirection))
				break;

			if (pressedState != PRESSED_DOWN
					&& pressedState != PRESSED_MOVE_HORIZANTAL)
				break;

			int xOffset = (int) (ev.getX() - lastActionDownX);		// 手指点击后移动的x轴上的距离
			int yOffset = (int) (ev.getY() - lastActionDownY);		// 手指点击后移动的y轴上的距离

			if(isLocked && xOffset > 0)
				break;

			if (pressedState == PRESSED_DOWN) {
				if (yOffset > 25 || yOffset < -25) {				//	垂直滑动
					pressedState = PRESSED_MOVE_VERTICAL;			// 标识为垂直滑动
					break;
				}
				if (xOffset < -50 || xOffset > 50) {				//  水平滑动
					pressedState = PRESSED_MOVE_HORIZANTAL;			// 标识为水平滑动
					ev.setAction(MotionEvent.ACTION_CANCEL);
				}
			} else if (pressedState == PRESSED_MOVE_HORIZANTAL) {
				if (currentActivityScaleX < 0.98)	// 若主界面缩放小于0.95
					scrollViewMenu.setVisibility(VISIBLE);	// 则显示侧拉

				float targetScale = getTargetScale(ev.getRawX());	//获取缩放目标，参数为点击坐标相对于屏幕的坐标
//				float targetScaleY = getTargetScaleY(ev.getRawX());

				ViewHelper.setScaleX(viewActivity, targetScale);
//				ViewHelper.setScaleY(viewActivity, targetScaleY);
//				ViewHelper.setScaleX(scrollViewMenu, getTargetScaleMenu(ev.getRawX()));
//				ViewHelper.setScaleY(scrollViewMenu, getTargetScaleMenuY(ev.getRawX()));
				ViewHelper.setScaleX(imageViewShadow, targetScale
						+ shadowAdjustScaleX);
//				ViewHelper.setScaleY(imageViewShadow, targetScaleY
//						+ shadowAdjustScaleY);
				ViewHelper.setAlpha(scrollViewMenu, (1 - targetScale) * 2.0f);

				lastRawX = ev.getRawX();
//				if(ev.getRawY() == 0)
//				{
//					lastRawY = ev.getRawX();
//				}
//				else
//				{
//					lastRawY = ev.getRawY();
//				}
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:		// 手指离开屏幕，执行此处

			if (isInIgnoredView)
				break;
			if (pressedState != PRESSED_MOVE_HORIZANTAL)
				break;
			pressedState = PRESSED_DONE;
			if (isOpened()) {
				if (currentActivityScaleX > 0.56f)
					closeMenu();
				else
					openMenu(scaleDirection);
			} else {
				if (currentActivityScaleX < 0.98f) {
					openMenu(scaleDirection);
				} else {
					closeMenu();
				}
			}
			break;
		}
		lastRawX = ev.getRawX();
//		lastRawY = ev.getRawX();
		return super.dispatchTouchEvent(ev);
	}



//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev)
//	{
//		if(isLocked)return false;
//		return super.onInterceptTouchEvent(ev);
//	}
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		if(isLocked)return true;
//		return super.onTouchEvent(event);
//	}

	public int getScreenHeight()
	{
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		return displayMetrics.heightPixels;
	}

	private int getDpi()
	{   int dpi = 0;
		Display display = activity.getWindowManager().getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		@SuppressWarnings("rawtypes")
		Class c;
		try {
			c = Class.forName("android.view.Display");
			@SuppressWarnings("unchecked")
			Method method = c.getMethod("getRealMetrics",DisplayMetrics.class);
			method.invoke(display, dm);
			dpi=dm.heightPixels;
		}catch(Exception e){
			e.printStackTrace();
		}
		return dpi;
	}
	/**
	 * 获取屏幕宽度，ps
	 * @return
	 */
	public int getScreenWidth() {
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		return displayMetrics.widthPixels;
	}

	/**
	 * 设置缩放值x
	 * @param scaleValue
	 */
	public void setScaleValue(float scaleValue) {
		this.mScaleValue = scaleValue;
	}

	public void setScaleValueY(float scaleValueY)
	{
		this.mScaleValueY = scaleValueY;
	}





	/**
	 * 侧拉开合状态监听
	 */
	public interface OnMenuListener {

		/**
		 * 此方法是在打开侧拉动画结束后调用
		 * the method will call on the finished time of opening menu's
		 * animation.
		 */
		public void openMenu();

		/**
		 * 此方法是在关闭侧拉动画结束后调用的
		 * the method will call on the finished time of closing menu's animation
		 * .
		 */
		public void closeMenu();
	}

}
