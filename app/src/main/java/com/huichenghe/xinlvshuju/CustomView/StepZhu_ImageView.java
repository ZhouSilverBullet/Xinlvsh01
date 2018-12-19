package com.huichenghe.xinlvshuju.CustomView;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.huichenghe.xinlvshuju.CommonUtilss.CommonUtils;
import com.huichenghe.xinlvshuju.Utils.DateUtils;

public class StepZhu_ImageView extends ImageView {
	
	private static final String TAG = "StepZhu_ImageView";
	
	
	public static final float MAX_BK_ZHU_TU = 1.05f;
	
//	final int BootomSpaceColor = ;
	final int ZhuColor = 0xffe17651;
	
	private static final int smallDP = 12;
	private static final int bigDP = 14;
	
	private int DEF_ZHU = 13;
	private float mStepCountMax = 1;
	
//	private int mWidth, mHeight = -clock;
	private stData item = null;
	
	private float mSmallTextPx, mBigTextPx;
	
	private class stData {
		Date theDate;
		int weekOfYear = -1;
		int monthOfYear = -1;
		int stepCount;
	}
	
	public StepZhu_ImageView(Context context) {
		super(context);
		init();
	}
	
	public StepZhu_ImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public StepZhu_ImageView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		mBigTextPx = CommonUtils.Dp2Px(getContext(), bigDP);
		mSmallTextPx = CommonUtils.Dp2Px(getContext(), smallDP);
		
//		// TODO Auto-generated method stub
//		{
//			addDataAsWeek(22, 113, 200);
//		}
//		{
//			addDataAsWeek(phone, 120);
//		}
//		{
//			addDataAsWeek(info, 160);
//		}
//		{
//			addDataAsWeek(sitting, 110);
//		}
//		{
//			addDataAsWeek(heartWarning, 250);
//		}
//		
//		mStepCountMax = 250;
		
	}
	
	public void addData(Date date, int stepCount, int maxStep) {
		item = new stData();
		item.theDate = date;
		item.stepCount = stepCount;
		
		mStepCountMax = maxStep;
		mStepCountMax = mStepCountMax * MAX_BK_ZHU_TU;
	}
	
	public void addDataAsWeek(int weekOfYear, int stepCount, int maxStep) {
		item = new stData();
		item.weekOfYear = weekOfYear;
		item.stepCount = stepCount;
		
		mStepCountMax = maxStep;
		mStepCountMax = mStepCountMax * MAX_BK_ZHU_TU;
	}
	
	public void addDataAsMouth(int monthOfYear, int stepCount, int maxStep) {
		item = new stData();
		item.monthOfYear = monthOfYear;
		item.stepCount = stepCount;
		
		mStepCountMax = maxStep;
		mStepCountMax = mStepCountMax * MAX_BK_ZHU_TU;
	}

	public boolean isHaveData() {
		if (item != null) {
			return true;
		}
		return false;
	}
	
	public void invalidate() {
		if (isHaveData())
			super.postInvalidate();
	}

//	public void initWH(int w, int h) {
//		// TODO Auto-generated method stub
//		mWidth = w;
//		mHeight = h;
//	}
	
	public void onDraw(Canvas canvas) {
		if (isHaveData()) {
			subDraw(canvas);
		}
		super.onDraw(canvas);
	}
	
	private void subDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(1);
		paint.setColor(ZhuColor);
		
		float width = getMeasuredWidth();
		float height = getMeasuredHeight();
		
		float eachStep = (height - mBigTextPx * 4) / mStepCountMax;
		
		// 画柱形
		float _y = height - item.stepCount * eachStep;
		
		canvas.drawRect(10, _y, width-10, height, paint);
		
		// 第几周
		String showDate = "";
		if (null != item.theDate) {
			showDate = getFormatDate(item.theDate);
		} else if (-1 != item.weekOfYear) {
			if(Locale.getDefault().getLanguage().equals("zh")){
				showDate = "第"+item.weekOfYear+"周";
			}else{
				showDate = "Week" + item.weekOfYear;
			}
		} else if (-1 != item.monthOfYear) {
			String mon = DateUtils.getZW(item.monthOfYear);
//			TLog.i("", "月份是：" + mon);
			if(Locale.getDefault().getLanguage().equals("zh")){
				showDate = "" + mon + "月";
			}else{
				switch (Integer.parseInt(mon)) {
				case 1:
					showDate = "January";
					break;
				case 2:
					showDate = "February";
					break;
				case 3:
					showDate = "March";
					break;
//					March, April, may, June, July, August, September, October, November, December
				case 4:
					showDate = "April";
					break;
				case 5:
					showDate = "may";
					break;
				case 6:
					showDate = "June";
					break;
				case 7:
					showDate = "July";
					break;
				case 8:
					showDate = "August";
					break;
				case 9:
					showDate = "September";
					break;
				case 10:
					showDate = "October";
					break;
				case 11:
					showDate = "November";
					break;
				case 12:
					showDate = "December";
					break;
//				showDate = ""+DateUtils.getZW(item.monthOfYear)+getResources().getString(R.string.month);
				}	
			}
		}
		float showDateLen = CommonUtils.getTextViewWidth(getContext(), showDate, bigDP)/2;
		float date_w_x = width/2 - showDateLen;
		float date_w_y = mBigTextPx * 2;
		paint.setColor(Color.BLACK);
		paint.setTextSize(mBigTextPx);
		canvas.drawText(showDate, date_w_x, date_w_y, paint);
		
		// 步数
		String showStep = ""+item.stepCount;
		float showStepLen = CommonUtils.getTextViewWidth(getContext(), showStep, smallDP)/2;
		float step_w_x = width/2 - showStepLen;
		float step_w_y = mBigTextPx * 4;
		paint.setColor(ZhuColor);
		paint.setTextSize(mSmallTextPx);
		canvas.drawText(showStep, step_w_x, step_w_y, paint);
	}
	
	private String getFormatDate(Date date) {
		//return ""+(date.getMonth()+clock)+"/"+(date.getDay()+clock);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return ""+(c.get(Calendar.MONTH)+1)+"-"+(c.get(Calendar.DAY_OF_MONTH));
	}

}
