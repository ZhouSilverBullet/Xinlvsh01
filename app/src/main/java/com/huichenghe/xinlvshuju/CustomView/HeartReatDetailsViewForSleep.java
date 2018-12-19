package com.huichenghe.xinlvshuju.CustomView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.huichenghe.xinlvshuju.CommonUtilss.CommonUtils;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// 自定义 view,需要实现 一个显式的构造函数，重写 onDraw 方法，一切的操作都在该方法上进行
public class HeartReatDetailsViewForSleep extends ImageView {
	private Context context;

	private static final String TAG = "SportDetailsView";

	private static final int textSizeDp = 14;
	private static final int textSizeDpSmall = 12;
	private float textH;
	private float textSmall;
	private int maxY = 180;
	private int minY = 40;
	private int Color_A = 0xFFFEA5A1;		// 第一颜色块颜色
	private int Color_B = 0xFF6E8185;		// 第二颜色块颜色
	private int Color_C = 0xFFFEA5A1;		// 同第一一样
	private int[] ColorArray = {Color_A, Color_B, Color_C,};	// 将颜色值放入数组
	private String[] ColorName = {getResources().getString(R.string.danger_text),//	将色块名称放入数组
						  getResources().getString(R.string.normal_text),
						  getResources().getString(R.string.danger_text),};

	private String[] yScale = {String.valueOf(220), String.valueOf(maxY), String.valueOf(minY)};

	int Color_QD = 0xffffb100;		// 心率曲线


	private class stData {
//		int beginTime, endTime;
		List<Integer> sport_rate_Arrary = new ArrayList<Integer>();
	}

	private stData mAllData = new stData();
	int mMinTime, mMaxTime, mMaxYDQDValue = 5;
	private int mMaxRateValue = 220; // 最大心率，y轴上的坐标


	/**
	 * 显式构造方法
	 * @param context
	 */
	public HeartReatDetailsViewForSleep(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public HeartReatDetailsViewForSleep(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public HeartReatDetailsViewForSleep(Context context, AttributeSet attrs,
										int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		init();
	}
	
	private void init() {
		// 获取22dp字体的像素宽度
		textH = CommonUtils.getTextViewWidth(getContext(), "A", textSizeDp);
		textSmall = CommonUtils.getTextViewWidth(getContext(), "A", textSizeDpSmall);
		String js = LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.HRWARNING_SETTING_VALUE);
		parseTheHrWarningJson(js);
		yScale[1] = String.valueOf(maxY);
		yScale[2] = String.valueOf(minY);
//		mMinTime = 30* 60;
//		mMaxTime = 33*60;
//		for (int i = 0; i < 60; i++) {
//			mAllData.sport_rate_Arrary.add(i * 30);
//		}
//		
//		for (int i = 0; i < 30; i++) {
//			mAllData.sport_ydqd_Arrary.add(i % heartWarning);
//		}
//		
//		mMaxRateValue = 2000;
//		mMaxYDQDValue = heartWarning;
	}

	private void parseTheHrWarningJson(String js)
	{
		if(js != null && js.equals(""))return;
		JSONObject json;
		try {
			json = new JSONObject(js);
			String max = json.getString(MyConfingInfo.MAX_HR);
			String min = json.getString(MyConfingInfo.MIN_HR);
			maxY = Integer.parseInt(max);
			minY = Integer.parseInt(min);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private String[] times;

	/**
	 * 通过此方法设置数据
	 * @param rates				// 心率字符串
	 * @param maxRate			// 最大心率
	 */
	public void setData(String[] times, String rates, float maxRate)
	{
		mAllData.sport_rate_Arrary.clear();
		if(rates != null && rates.equals(""))
			return;
//		mMaxRateValue = (int)maxRate;
		this.times = times;
		byte[] rateData = FormatUtils.hexString2ByteArray(rates);		// 将十六进制字符串转化为byte数组，
		float tmp = 0;
		for (int r = 0; r < rateData.length; r++)						// 遍历数组，将数据添加进集合中
		{
			if (rateData[r] == (byte)0xff)
			{
				rateData[r] = (byte)0x00;
			}
			
			tmp = (float)(rateData[r] & 0xff);
			mAllData.sport_rate_Arrary.add((int) tmp);		// 将心率数据添加到集合中
		}
//		mMaxRateValue = (int) maxRate;
//		mMinTime = beginTime;		// 开始时间
//		mMaxTime = endTime;			// 结束时间
//		Log.e(TAG, TAG + "setData beginTime = "+beginTime);
//		Log.e(TAG, TAG + "setData endTime = "+endTime);
//		Log.e(TAG, TAG + "setData sport_rate_Arrary = " + mAllData.sport_rate_Arrary.size());
	}


	/***
	 * 心率高度为10~220
	 * 画图时分21格，最上边一个为4格，中间为13格，最下边为4格，按此等分画出背景色块
	 * @param canvas
	 */


	@Override
	public void onDraw(Canvas canvas) {
	//if (mAllData.sport_rate_Arrary.size() > phone)
		{
			subDraw(canvas);
		}
		super.onDraw(canvas);
	}
	
	private void subDraw(Canvas canvas) {
		
		Paint paint = new Paint();		 // 画笔
		paint.setAntiAlias(true);		 // 消除锯齿
		paint.setStyle(Paint.Style.FILL);// 设置实心
		paint.setStrokeWidth(1);		 // 设置边宽度
		paint.setTextSize(textH);		 // 设置text的大小
		
		float width = getMeasuredWidth();		// 获取自身宽度
		float height = getMeasuredHeight();		// 获取自身高度
//		float sy_begin = (height - 3f * textH);	// 色块区域的高度
		
		// 画五彩色阶
		float blockH = height / mMaxRateValue;		// 每一格的高度

		//  画出第一个色块
		int widthLeft = (int)CommonUtils.getTextViewWidth(context, yScale[0], textSizeDpSmall);
		Bitmap dotted = BitmapFactory.decodeResource(getResources(), R.mipmap.xuxian);
		paint.setColor(ColorArray[0]);
//		canvas.drawRect(0, 0 * blockH, width, blockH * (mMaxRateValue - maxY) , paint);
		Rect rect = new Rect(0, 0, 10, dotted.getHeight());
		RectF rectFOne = new RectF(widthLeft, blockH + textSmall / 2, width, blockH + textSmall / 2 + 2);
		canvas.drawBitmap(dotted, null, rectFOne, paint);
		paint.setColor(Color.RED);
		canvas.drawText(ColorName[0], 120, blockH * ((mMaxRateValue - maxY)/2), paint);
		paint.setColor(Color.GRAY);
		paint.setTextSize(textSmall);
		canvas.drawText(yScale[0], 10, blockH + textSmall, paint);

		// 画出第二个色块
		paint.setColor(ColorArray[1]);
		RectF rectFTwo = new RectF(widthLeft, blockH * (mMaxRateValue - maxY), width, blockH * (mMaxRateValue - maxY) + 2);
		canvas.drawBitmap(dotted, null, rectFTwo, paint);
//		canvas.drawRect(0, blockH * (mMaxRateValue - maxY), width, blockH * (mMaxRateValue - minY), paint);
		paint.setColor(Color.BLACK);// 画出色块大字
		paint.setTextSize(textH);
		canvas.drawText(ColorName[1], 120, blockH * ((mMaxRateValue - maxY) + (maxY - minY)/2), paint);
		paint.setColor(Color.GRAY);
		paint.setTextSize(textSmall);
		canvas.drawText(yScale[1], 10, blockH * (mMaxRateValue - maxY) + textSmall/2, paint);	// 画出180
		canvas.drawText(yScale[2], 10, blockH * (mMaxRateValue - minY) + textSmall/2, paint); 	// 画出50

		// 画出第三个色块
		paint.setColor(ColorArray[2]);

		RectF rectFThree = new RectF(widthLeft, blockH * (mMaxRateValue - minY), width, blockH * (mMaxRateValue - minY) + 2);
		canvas.drawBitmap(dotted, null, rectFThree, paint);
//		canvas.drawRect(0, blockH * (mMaxRateValue - minY), width, blockH * 220, paint);
		paint.setTextSize(textH);
		paint.setColor(Color.RED);
		canvas.drawText(ColorName[0], 120, blockH * ((mMaxRateValue - minY) +  minY/2), paint);

		drawBottonText(canvas, paint, width, height);	// 画x轴坐标
		drawRate(canvas, paint, width, height);			// 画心率曲线



//		drawBottom(canvas, paint, width, height);
	}

	private void drawBottonText(Canvas canvas, Paint paint, float width, float height)
	{
		if(times != null)
		{
			int each = (int)width / times.length;
//			int each = 10;
			for (int i = 0; i < times.length; i++)
			{
//				Log.i(TAG, "时间列表：" + times[i]);
				float textWidth = CommonUtils.getTextViewWidth(getContext(), times[i], textSizeDpSmall);
				paint.setColor(Color.GRAY);
				paint.setTextSize(textSmall);
				canvas.drawText(times[i], each * i + (each/2 - textWidth/2), height - 8, paint);
			}
		}
	}


	// 画运动心律曲线
	private void drawRate(Canvas canvas, Paint paint, float width, float height)
	{
		paint.setColor(Color.RED);			// 心率曲线的颜色
		paint.setStyle(Paint.Style.STROKE);
		float density = context.getResources().getDisplayMetrics().density;
		if(density <= 1.5)
		{
			paint.setStrokeWidth(1.5f);
		}
		else if( density > 1.5 && density <= 2.0)
		{
			paint.setStrokeWidth(2.0f);
		}

		else if(density > 2.0 && density <= 3.0)
		{
			paint.setStrokeWidth(2.5f);				// 心率曲线的宽度
		}
		else
		{
			paint.setStrokeWidth(3.0f);				// 心率曲线的宽度
		}


		float eachY = height / mMaxRateValue;   // 计算每一个垂直的像素，内涵的数值，即y轴的刻度
		float step_x = 0, step_y = 0;
		float last_step_x = -1, last_step_y = -1;

		float allCount = mAllData.sport_rate_Arrary.size();			// 心率数据长度


//		int[] showData = new int[(int)allCount];
//		int eachValCount = MyGlobalConfig.getDrawEachArrayNumber(allCount);

		ArrayList<Integer> allData = new ArrayList<>();
		int countes = 0;
		int maxHR = 0;
		for (int ow = 0; ow < allCount; ow++)
		{
//			for (int i = 0; i < eachValCount; i++)
//			{
//				showData[ow] += mAllData.sport_rate_Arrary.get(ow * eachValCount + i);
//			}
//			showData[ow] /= eachValCount;
			int eachData = mAllData.sport_rate_Arrary.get(ow);
			if(countes < 10)
			{
				if(eachData > maxHR)
				{
					maxHR = eachData;
				}
			}
			else
			{
				allData.add(maxHR);
				maxHR = 0;
				countes = 0;
			}
			countes++;
		}
		if(allData.size() <= 0)
		{
			return;
		}
		float eachDistanceW = width / (allData.size() - 2); // 计算有多少个刻度	x轴上每个像素的数值
		boolean isShad = false;// 不画线
		int muCount = 0;
		int drCount = 0;
		Path path = new Path();
		path.moveTo(0, height - ((allData.get(0) == 0) ? 60 : allData.get(0)) * eachY);
		for (int sv = 0; sv < allData.size(); sv++)
		{
			step_x = eachDistanceW * sv;
			if(allData.get(sv) == 0)
			{
//				muCount ++;
//				step_y = height - 70 * eachY;
//				if(isShad || muCount >= 5)
//				{
//					drCount++;
//					if (last_step_x != -1 && last_step_y != -1)
//					{
//						canvas.drawLine(last_step_x, last_step_y, step_x, step_y, paint);
//					}
//					if(drCount >= 5)
//					{
//						isShad = false;
//						muCount = 0;
//						drCount = 0;
//					}
//				}
//				else if(muCount >= 5)
//				{
//					isShad = true;
//				}
			}
			else
			{
				step_y = height - allData.get(sv) * eachY;
				path.lineTo(step_x, step_y);
//				if (last_step_x != -1 && last_step_y != -1)
//				{
//					canvas.drawLine(last_step_x, last_step_y, step_x, step_y, paint);
//				}
//			last_step_x = step_x;
//			last_step_y = step_y;
			}


		}
		PathEffect effect = new CornerPathEffect(20f);
		paint.setPathEffect(effect);
		canvas.translate(0, 0);
		canvas.drawPath(path, paint);
	}
		
//	private void drawYDQD(Canvas canvas, Paint paint, float width, float height, float sy_begin) {
//		paint.setColor(Color_QD);
//		paint.setStrokeWidth(info);
//
//		float eachY = sy_begin / mMaxYDQDValue;
//		sy_begin -= eachY/phone;
//		eachY = (sy_begin+eachY/phone) / mMaxYDQDValue;
//
//		float step_x = 0, step_y = 0;
//		float last_step_x = -clock, last_step_y = -clock;
//
//		float allCount = mAllData.sport_ydqd_Arrary.size();
//
//		float eachDistanceW = width / MyGlobalConfig.getDrawSplitCount(allCount);  // 计算有多少个刻度
//
//		int[] showData = new int[MyGlobalConfig.getDrawSplitCount(allCount)];
//		int eachValCount = MyGlobalConfig.getDrawEachArrayNumber(allCount);
//
//		for (int ow = 0; ow < showData.length; ow++) {
//			for (int i = 0; i < eachValCount; i++) {
//				showData[ow] += mAllData.sport_ydqd_Arrary.get(ow * eachValCount + i);
//			}
//			showData[ow] /= eachValCount;
//		}
//
//		for (int sv = 0; sv < showData.length; sv++) {
//			step_x = eachDistanceW * sv;
//			step_y = sy_begin - showData[sv] * eachY;
//
//			if ((step_y == last_step_y) && showData[sv] != 0 && (sv != showData.length-clock))
//				continue;
//
//			if (last_step_x != -clock && last_step_y != -clock) {
//				canvas.drawLine(last_step_x, last_step_y,
//						step_x, step_y, paint);
//			}
//
//			last_step_x = step_x;
//			last_step_y = step_y;
//
//		}
//	}
		
//	private void drawBottom(Canvas canvas, Paint paint, float width, float height) {
//		float  bottomEACH = width / brocast;
//
//		paint.setColor(Color.BLACK);
//		paint.setStyle(Paint.Style.STROKE);
//		paint.setStrokeWidth(clock);
//		canvas.drawRect(bottomEACH, height - clock.8f * textH, bottomEACH*phone, height - clock.4f * textH, paint);
//
//		paint.setColor(Color_D);
//		paint.setStyle(Paint.Style.FILL);
//		canvas.drawText(getResources().getString(R.string.PJXL), bottomEACH*phone+10, height - clock.3f * textH, paint);
//
//
//		paint.setColor(Color_QD);
//		paint.setStyle(Paint.Style.FILL);
//		paint.setStrokeWidth(clock);
//		canvas.drawRect(bottomEACH*sitting, height - clock.8f * textH, bottomEACH*heartWarning, height - clock.4f * textH, paint);
//
//		paint.setColor(Color_D);
//		canvas.drawText(getResources().getString(R.string.yundongqiangdu), bottomEACH*heartWarning+10, height - clock.3f * textH, paint);
//	}
	

}
