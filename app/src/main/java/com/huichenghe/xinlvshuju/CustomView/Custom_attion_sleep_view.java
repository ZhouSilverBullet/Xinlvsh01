package com.huichenghe.xinlvshuju.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.huichenghe.xinlvshuju.CommonUtilss.CommonUtils;
import com.huichenghe.xinlvshuju.DataEntites.Attion_sleep_data_entity;
import com.huichenghe.xinlvshuju.R;

import java.util.ArrayList;

/**
 * Created by lixiaoning on 2016/10/25.
 */
public class Custom_attion_sleep_view extends ImageView
{
    private int smallTextSizeDp = 12;
    private int bigTextSizeDp = 16;
    private float smallTextSizePx, bigTextSizePx;
    private int deepColor, lightColor, wakeColor, tvColor;
    private Paint mPaint;
    private int width, height;
    private ArrayList<Attion_sleep_data_entity> sleeps;
    private float chartWidth;
    private TouchPointer touchPointer;
    private int bubbleId;



    class TouchPointer
    {
        float x, y;
    }
    public Custom_attion_sleep_view(Context context)
    {
        super(context);
    }

    public Custom_attion_sleep_view(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initView(context, attrs);
    }

    public Custom_attion_sleep_view(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }


    private void initView(Context context, AttributeSet attrs)
    {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Custom_attion_sleep_view);
        deepColor = ta.getColor(R.styleable.Custom_attion_sleep_view_attion_deep_color,
                getResources().getColor(R.color.deep_sleep));
        lightColor = ta.getColor(R.styleable.Custom_attion_sleep_view_attion_light_color,
                getResources().getColor(R.color.light_sleep));
        wakeColor = ta.getColor(R.styleable.Custom_attion_sleep_view_attion_wake_color,
                getResources().getColor(R.color.sober));
        tvColor = ta.getColor(R.styleable.Custom_attion_sleep_view_attion_text_color,
                getResources().getColor(R.color.grey_color_dark));
        chartWidth = ta.getDimension(R.styleable.Custom_attion_sleep_view_attion_chart_width, 10);
        bubbleId = ta.getResourceId(R.styleable.Custom_attion_sleep_view_attion_rec_bubble, R.mipmap.rec_bubble);
        ta.recycle();
        smallTextSizePx = CommonUtils.getTextViewWidth(getContext(), "A", smallTextSizeDp);
        bigTextSizePx = CommonUtils.getTextViewWidth(getContext(), "A", bigTextSizeDp);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        touchPointer = new TouchPointer();
    }

    public void setSleepData(ArrayList<Attion_sleep_data_entity> sleepData)
    {
        this.sleeps = sleepData;
        postInvalidate();
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        subOnDraw(canvas);
    }

    private void subOnDraw(Canvas canvas)
    {
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        drawBottomLine(canvas, width, height);
        if(sleeps != null && sleeps.size() > 0)
        {
            int[] maxData = getMaxSleepData();
            drawRectf(canvas, width, height, maxData[0]);
            boolean isBubble = false;
            for (int i = 0; i < sleeps.size(); i++)
            {
                if(touchPointer.x > 0)
                {
                    isBubble = drawRectBubble(i, canvas, width, height, maxData[0], false);
                    if(isBubble)break;
                }
            }
            if(!isBubble)
            {
                drawRectBubble(maxData[1], canvas, width, height, maxData[0], true);
            }
        }
    }

    private boolean drawRectBubble(int i, Canvas canvas, int width, int height, int maxData, boolean showMax)
    {
        float subWidth = width - smallTextSizePx * 2;
        float eachW = subWidth/7;
        float subHeight = height - smallTextSizePx * 5;
        float eachH = subHeight/maxData;
        float drawX = eachW * i + smallTextSizePx + eachW/4 + chartWidth * 2;
        float value = drawX - touchPointer.x;
        if(Math.abs(value) < chartWidth * 4)
        {
            Bitmap bubble = BitmapFactory.decodeResource(getResources(), bubbleId);
            int bubbleWidth = bubble.getWidth();
            int bubbleHeight = bubble.getHeight();
            Attion_sleep_data_entity entity = sleeps.get(i);
            int deep = entity.getDeepData();
            int maxEach = deep;
            int light = entity.getLightData();
            int wake = entity.getWakeData();
            if(deep < light)
            {
                maxEach = light;
            }
            if(light < wake)
            {
                maxEach = wake;
            }
            float bottom = subHeight - (eachH * maxEach) + smallTextSizePx * 2;
            float top = bottom - bubbleHeight;
            top = top < 0 ? 0 : top;
            RectF recf = new RectF();
            recf.left = drawX - bubbleWidth/2;
            recf.top = top;
            recf.right = drawX + bubbleWidth/2;
            recf.bottom = (top == 0) ? bubbleHeight : bottom;
            canvas.drawBitmap(bubble, null, recf, mPaint);
            drawDeepText(deep, deepColor, canvas, drawX - bubbleWidth/2, top, bubbleHeight, bubbleWidth);
            drawDeepText(light, lightColor, canvas, drawX - bubbleWidth/2, top + smallTextSizePx, bubbleHeight, bubbleWidth);
            drawDeepText(wake, wakeColor, canvas, drawX - bubbleWidth/2, top + smallTextSizePx * 2, bubbleHeight, bubbleWidth);
            return true;
        }
        if(showMax)
        {
            Bitmap bubble = BitmapFactory.decodeResource(getResources(), bubbleId);
            int bubbleWidth = bubble.getWidth();
            int bubbleHeight = bubble.getHeight();
            Attion_sleep_data_entity entity = sleeps.get(i);
            int deep = entity.getDeepData();
            int maxEach = deep;
            int light = entity.getLightData();
            int wake = entity.getWakeData();
            if(deep < light)
            {
                maxEach = light;
            }
            if(light < wake)
            {
                maxEach = wake;
            }
            float bottom = subHeight - (eachH * maxEach) + smallTextSizePx * 2;
            float top = bottom - bubbleHeight;
            top = top < 0 ? 0 : top;
            RectF recf = new RectF();
            recf.left = drawX - bubbleWidth/2;
            recf.top = top;
            recf.right = drawX + bubbleWidth/2;
            recf.bottom = (top == 0) ? bubbleHeight : bottom;
            canvas.drawBitmap(bubble, null, recf, mPaint);
            drawDeepText(deep, deepColor, canvas, drawX - bubbleWidth/2, top, bubbleHeight, bubbleWidth);
            drawDeepText(light, lightColor, canvas, drawX - bubbleWidth/2, top + smallTextSizePx, bubbleHeight, bubbleWidth);
            drawDeepText(wake, wakeColor, canvas, drawX - bubbleWidth/2, top + smallTextSizePx * 2, bubbleHeight, bubbleWidth);
            return true;
        }
        return false;
    }

    private void drawDeepText(int deep, int color, Canvas canvas, float left, float top, int bubbleHeight, int bubbleWidth)
    {
        RectF recF = new RectF();
        recF.left = left + smallTextSizePx/2;
        recF.top = top + smallTextSizePx/2;
        recF.right = left + smallTextSizePx ;
        recF.bottom = top + smallTextSizePx;
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(recF, mPaint);
        mPaint.setColor(tvColor);
        mPaint.setTextSize(smallTextSizePx);
        StringBuffer tvTime = new StringBuffer();
        int h = deep/60;
        int min = deep%60;
        if(h > 0)
        {
            tvTime.append(String.valueOf(h));
            tvTime.append("h");
        }
        if(min > 0)
        {
            tvTime.append(String.valueOf(min));
            tvTime.append("min");
        }
        canvas.drawText(tvTime.toString(), left + smallTextSizePx, top + smallTextSizePx, mPaint);
    }


    private void drawRectf(Canvas canvas, int width, int height, int maxData)
    {
        float subWidth = width - smallTextSizePx * 2;
        float eachW = subWidth/7;
        float subHeight = height - smallTextSizePx * 5;
        float eachH = subHeight/maxData;
        for (int i = 0; i < sleeps.size(); i++)
        {
            Attion_sleep_data_entity sleepEn = sleeps.get(i);
            int deep = sleepEn.getDeepData();
            int light = sleepEn.getLightData();
            int wake = sleepEn.getWakeData();
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(deepColor);
            float startX = eachW * i + smallTextSizePx + eachW/4;
            RectF recf = new RectF();
            recf.left = startX;
            recf.top = subHeight - (eachH * deep) + smallTextSizePx * 2;
            recf.right = startX + chartWidth;
            recf.bottom = height - smallTextSizePx * 3;
            canvas.drawRect(recf, mPaint);

            startX = startX + chartWidth + chartWidth/2;
            mPaint.setColor(lightColor);
            RectF recfL = new RectF();
            recfL.left = startX;
            recfL.top = subHeight - (eachH * light) + smallTextSizePx * 2;
            recfL.right = startX + chartWidth;
            recfL.bottom = height - smallTextSizePx * 3;
            canvas.drawRect(recfL, mPaint);

            startX = startX + chartWidth + chartWidth/2;
            mPaint.setColor(wakeColor);
            RectF recfW = new RectF();
            recfW.left = startX;
            recfW.top = subHeight - (eachH * wake) + smallTextSizePx * 2;
            recfW.right = startX + chartWidth;
            recfW.bottom = height - smallTextSizePx * 3;
            canvas.drawRect(recfW, mPaint);

            mPaint.setTextSize(bigTextSizePx);
            mPaint.setColor(tvColor);
            canvas.drawText(sleepEn.getDate(), eachW * i + smallTextSizePx + eachW/4, height - smallTextSizePx *1, mPaint);
        }
    }

    private int[] getMaxSleepData()
    {
        int max[] = new int[2];
        for (int i = 0; i < sleeps.size(); i++)
        {
            Attion_sleep_data_entity en = sleeps.get(i);
            int deepD = en.getDeepData();
            int lightD = en.getLightData();
            int wakeD = en.getWakeData();
            int all = 0;
            if(deepD < lightD)
            {
                all = lightD;
            }
            if(lightD < wakeD)
            {
                all = wakeD;
            }

            if(max[0] < all)
            {
                max[0] = all;
                max[1] = i;
            }
        }
        return max;
    }


    private void drawBottomLine(Canvas canvas, int width, int height)
    {
        mPaint.setColor(tvColor);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(smallTextSizePx, height - smallTextSizePx * 3, width - smallTextSizePx, height - smallTextSizePx * 3, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                touchPointer.x = event.getRawX();
                postInvalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return true;
    }
}
