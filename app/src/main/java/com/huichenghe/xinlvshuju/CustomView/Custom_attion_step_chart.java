package com.huichenghe.xinlvshuju.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.huichenghe.xinlvshuju.CommonUtilss.CommonUtils;
import com.huichenghe.xinlvshuju.DataEntites.Attion_step_Entity;
import com.huichenghe.xinlvshuju.R;

import java.util.ArrayList;

/**
 * Created by lixiaoning on 2016/10/21.
 */
public class Custom_attion_step_chart extends ImageView
{
    private int smallTextSize = 12;
    private int bigTextSize = 16;
    private float smallTextSizePx, bigTextSizePx;
    private int chartColor, lineColor;
    private int iconId, blueBubble;
    private int axisColor;
    private float lineWidth;
    private ArrayList<Attion_step_Entity> steps;
    private int width, height;
    private Paint mPaint;
    private Path mPath = new Path();
    private PathEffect pathEffect;
    private TouchPointor touchPointor;

    public Custom_attion_step_chart(Context context)
    {
        super(context);
    }

    public Custom_attion_step_chart(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        intit(context, attrs);
    }

    public Custom_attion_step_chart(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        intit(context, attrs);
    }


    private void intit(Context context, AttributeSet attrs)
    {
        smallTextSizePx = CommonUtils.getTextViewWidth(context, "A", smallTextSize);
        bigTextSizePx = CommonUtils.getTextViewWidth(context, "A", bigTextSize);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Custom_attion_step_chart);
        chartColor = ta.getColor(R.styleable.Custom_attion_step_chart_chart_color, getResources().getColor(R.color.step_histogram));
        lineColor = ta.getColor(R.styleable.Custom_attion_step_chart_line_color, getResources().getColor(R.color.grey_color_dark));
        iconId = ta.getResourceId(R.styleable.Custom_attion_step_chart_poniter_icon, R.mipmap.small_blood_pointer);
        axisColor = ta.getColor(R.styleable.Custom_attion_step_chart_x_axis_color_only_show, getResources().getColor(R.color.grey_color_dark));
        lineWidth = ta.getDimension(R.styleable.Custom_attion_step_chart_line_width, 1);
        blueBubble = ta.getResourceId(R.styleable.Custom_attion_step_chart_blue_bubble_icon, R.mipmap.blue_bubble);
        ta.recycle();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        touchPointor = new TouchPointor();
    }

    public void setStepData(ArrayList<Attion_step_Entity> entitys)
    {
        this.steps = entitys;
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
        int[] max = getMaxData();
        drawBottomLine(canvas);
        if(steps != null && steps.size() > 0)
        {
            boolean isDrawBubble = false;
            drawShaderChart(canvas, width, height, max[0]);
            for (int i = 0; i < steps.size(); i++)
            {
                isDrawBubble = drawBubble(i, canvas, width, height, steps.get(i).getStep(), max, false);
                if(isDrawBubble)break;
            }
            if(!isDrawBubble)
            {
                drawBubble(-1, canvas, width, height, steps.get(max[1]).getStep(), max, true);
            }
        }
    }

    private boolean drawBubble(int i, Canvas canvas, int width, int height, int step, int[] max, boolean showMax)
    {
        float subWid = width - smallTextSizePx * 4;
        float subHei = height - smallTextSizePx * 5;
        float eachWidth = subWid / 6;
        float eachHeight = subHei / max[0];
        float drawX = i * eachWidth + smallTextSizePx * 2;
        float drawY = subHei - (eachHeight * step);
        float value = drawX - touchPointor.x;
        Bitmap b = BitmapFactory.decodeResource(getResources(), iconId);
        if((Math.abs(value) < b.getWidth()) && (i != -1) && touchPointor.x > 0)
        {
            Bitmap mBubble = BitmapFactory.decodeResource(getResources(), blueBubble);
            int bubbleWidth = mBubble.getWidth();
            int bubbleHeight = mBubble.getHeight();
            int bubbleTop = (int) ((drawY - bubbleHeight/2) < 0 ? 0 : drawY - bubbleHeight/2);
            int bubbleBottom = (drawY - bubbleHeight/2) < 0 ? bubbleHeight : (int) (drawY + bubbleHeight / 2);
            RectF recf = new RectF();
            recf.left = drawX - bubbleWidth/2;
            recf.top =  bubbleTop;
            recf.right = drawX + bubbleWidth/2;
            recf.bottom = bubbleBottom;
            canvas.drawBitmap(mBubble, null, recf, mPaint);
            float tvWidth = CommonUtils.getTextViewWidth(getContext(), String.valueOf(step), bigTextSize);
            mPaint.setColor(Color.WHITE);
            mPaint.setTextSize(bigTextSizePx);
            canvas.drawText(String.valueOf(step), drawX - tvWidth/3, bubbleTop + smallTextSizePx * 2, mPaint);
            mPaint.setTextSize(smallTextSizePx);
            canvas.drawText("steps", drawX - tvWidth/3, bubbleTop + smallTextSizePx * 3, mPaint);
            return true;
        }
        if(showMax)
        {
            float drawXx = max[1] * eachWidth + smallTextSizePx * 2;
            Bitmap mBubble = BitmapFactory.decodeResource(getResources(), blueBubble);
            int bubbleWidth = mBubble.getWidth();
            int bubbleHeight = mBubble.getHeight();
            int bubbleTop = (int) ((drawY - bubbleHeight/2) < 0 ? 0 : drawY - bubbleHeight/2);
            int bubbleBottom = (drawY - bubbleHeight/2) < 0 ? bubbleHeight : (int) (drawY + bubbleHeight / 2);
            RectF recf = new RectF();
            recf.left = drawXx - bubbleWidth/2;
            recf.top =  bubbleTop;
            recf.right = drawXx + bubbleWidth/2;
            recf.bottom = bubbleBottom;
            canvas.drawBitmap(mBubble, null, recf, mPaint);
            float tvWidth = CommonUtils.getTextViewWidth(getContext(), String.valueOf(step), bigTextSize);
            mPaint.setColor(Color.WHITE);
            mPaint.setTextSize(bigTextSizePx);
            canvas.drawText(String.valueOf(step), drawXx - tvWidth/3, bubbleTop + smallTextSizePx * 2, mPaint);
            mPaint.setTextSize(smallTextSizePx);
            canvas.drawText("steps", drawXx - tvWidth/3, bubbleTop + smallTextSizePx * 3, mPaint);
            return false;
        }
        return false;
    }

    private void drawShaderChart(Canvas canvas, int width, int height, int max)
    {
        mPaint.setStyle(Paint.Style.FILL);
        LinearGradient gradient = new LinearGradient(
                0,
                0,
                0,
                getHeight(),
                new int[]{chartColor, chartColor},
                null,
                LinearGradient.TileMode.CLAMP);
        mPaint.setShader(gradient);
        mPaint.setColor(lineColor);
        pathEffect = new CornerPathEffect(0);
        float subHei = height - smallTextSizePx * 5;
        float subWid = width - smallTextSizePx * 4;
        float eachWidth = subWid / 6;
        float eachHeight = subHei / max;
        mPath.moveTo(smallTextSizePx * 2, subHei + smallTextSizePx * 2);
        for (int i = 0; i < steps.size(); i++)
        {
            Attion_step_Entity en = steps.get(i);
            int ste = en.getStep();
            String data = en.getDate();
//            mPath.lineTo();
            mPath.lineTo(i * eachWidth + smallTextSizePx * 2, subHei - (eachHeight * ste) + smallTextSizePx * 2);
            if(i == steps.size() - 1)
            {
                mPath.lineTo(i * eachWidth + smallTextSizePx * 2, subHei + smallTextSizePx * 2);
            }
        }
        mPaint.setPathEffect(pathEffect);
        mPath.close();
        canvas.drawPath(mPath, mPaint);
        Bitmap b = BitmapFactory.decodeResource(getResources(), iconId);
        int bWidth = b.getWidth();
        int bHeight = b.getHeight();
        for (int i = 0; i < steps.size(); i++)
        {
            Attion_step_Entity en = steps.get(i);
            int ste = en.getStep();
            String date = en.getDate();
            float x = i * eachWidth + smallTextSizePx * 2;
            float y = subHei - (eachHeight * ste)  + smallTextSizePx * 2;
            RectF recf = new RectF();
            recf.left = x - bWidth/2;
            recf.top = y - bHeight/2;
            recf.right = x + bWidth/2;
            recf.bottom = y + bHeight/2;
            canvas.drawBitmap(b, null, recf, mPaint);
            mPaint.setShader(null);
            mPaint.setTextSize(smallTextSizePx);
            float tvWidth = CommonUtils.getTextViewWidth(getContext(), date, smallTextSize);
            canvas.drawText(date, x - tvWidth/3, height - smallTextSizePx * 2, mPaint);
        }
    }

    private int[] getMaxData()
    {
        int max[] = new int[2];
        for (int i = 0; i < steps.size(); i ++)
        {
            int s = steps.get(i).getStep();
            if(max[0] < s)
            {
                max[0] = s;
                max[1] = i;
            }
        }
        return max;
    }

    private void drawBottomLine(Canvas canvas) 
    {
        mPaint.setColor(lineColor);
        mPaint.setStrokeWidth(lineWidth);
        canvas.drawLine(0 + smallTextSizePx * 2, height - smallTextSizePx * 3, width - smallTextSizePx * 2, height - smallTextSizePx * 3, mPaint);
        canvas.drawLine(0 + smallTextSizePx * 2, height - smallTextSizePx * 3, 0 + smallTextSizePx * 2, 0, mPaint);
    }

    private class TouchPointor
    {
        float x, y;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                touchPointor.x = event.getRawX();
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
