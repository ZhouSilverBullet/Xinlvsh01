package com.huichenghe.xinlvshuju.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.huichenghe.xinlvshuju.CommonUtilss.CommonUtils;
import com.huichenghe.xinlvshuju.R;

/**
 * Created by lixiaoning on 2016/9/29.
 */
public class Custom_hisgram_view_sleep_trend extends ImageView
{
    public static final String TAG = "Custom_hisgram_view";
    private int textSize = 16;
    private int smallSize = 11;
    private float textSizePX, smallSizePX;
    private int hisgramColor, belowTextColor, upTextColor;
    private int[][] histogramData;
    private String[] hisgramTime;
    private float hisgramWidth;
    private float spaceWidth;
    private float end_Touch_Able;
    private int bubbleID;
    private int unitText;
    private int deepColor, lightColor, wakeColor;
    private TouchPointor touchPointor;


    private class TouchPointor
    {
        float x, y;
    }

    public Custom_hisgram_view_sleep_trend(Context context)
    {
        super(context);
    }

    public Custom_hisgram_view_sleep_trend(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initView(attrs);
    }

    public Custom_hisgram_view_sleep_trend(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    public void setData(int[][] data, String[] hisgramTime)
    {
        this.histogramData = data;
        this.hisgramTime = hisgramTime;
        postInvalidate();
    }


    private void initView(AttributeSet attrs)
    {
        touchPointor = new TouchPointor();
        touchPointor.x = 0;
        touchPointor.y = 12;
        TypedArray ty = getContext().obtainStyledAttributes(attrs, R.styleable.Custom_hisgram_view_sleep);
        hisgramColor = ty.getColor(R.styleable.Custom_hisgram_view_sleep_hisgram_sleep_color, Color.WHITE);
        belowTextColor = ty.getColor(R.styleable.Custom_hisgram_view_sleep_hisgram_sleep_bottom_text_color, Color.GRAY);
        upTextColor = ty.getColor(R.styleable.Custom_hisgram_view_sleep_hisgram_sleep_top_text_color, Color.BLACK);
        hisgramWidth = ty.getDimension(R.styleable.Custom_hisgram_view_sleep_hisgram_sleep_width_dp, 10);
        bubbleID = ty.getResourceId(R.styleable.Custom_hisgram_view_sleep_hisgram_sleep_bubble_icon, R.mipmap.bubble_step);
        unitText = ty.getResourceId(R.styleable.Custom_hisgram_view_sleep_hisgram_sleep_unit_text, R.string.step_simple_one);
        spaceWidth = ty.getDimension(R.styleable.Custom_hisgram_view_sleep_space_sleep_width, 10);
        deepColor = ty.getColor(R.styleable.Custom_hisgram_view_sleep_hisgram_sleep_deep_color, getResources().getColor(R.color.deep_sleep));
        lightColor = ty.getColor(R.styleable.Custom_hisgram_view_sleep_hisgram_sleep_light_color, getResources().getColor(R.color.light_sleep));
        wakeColor = ty.getColor(R.styleable.Custom_hisgram_view_sleep_hisgram_sleep_wake_color, getResources().getColor(R.color.sober));
//        textSize = (int) ty.getDimension(R.styleable.Custom_hisgram_view_sleep_big_text_hint_sleep, 16);
        ty.recycle();
        textSizePX = CommonUtils.getTextViewWidth(getContext(), "A", textSize);
        smallSizePX = CommonUtils.getTextViewWidth(getContext(), "A", smallSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int defultWidth = (int)((hisgramWidth + spaceWidth) * 8);
//        int defultHeight;
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int heigthMode = MeasureSpec.getMode(heightMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//        // width mode
//        if(widthMode == MeasureSpec.EXACTLY)
//        {
//            defultWidth = widthSize;
//        }
//        else if(widthMode == MeasureSpec.AT_MOST)
//        {
//            defultWidth = (int)end_Touch_Able;
//        }
//        else
//        {
//            defultWidth = (int)end_Touch_Able;
//        }
//
//        // height modle
//        if(heigthMode == MeasureSpec.EXACTLY)
//        {
//            defultHeight = heightSize;
//        }
//        else if(heigthMode == MeasureSpec.AT_MOST)
//        {
//            defultHeight = heightSize;
//        }
//        else
//        {
//            defultHeight = heightSize;
//        }
//        setMeasuredDimension(defultWidth, heightSize);

    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        subOnDraw(canvas);
    }

    private void subOnDraw(Canvas canvas)
    {
        float width = getMeasuredWidth();
        float height = getMeasuredHeight();
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        if(histogramData != null && histogramData.length > 0)
        {
            int maxValue[] = getMaxValue(histogramData);
            end_Touch_Able = hisgramWidth * 3 + hisgramWidth * histogramData.length + spaceWidth * histogramData.length;
            for (int i = 0; i < histogramData.length; i++)
            {
//                if(histogramData[i] == null)continue;
                //todo bug
                drawHisgram(canvas, paint, width, height, i, histogramData[i], maxValue[0]);
            }
            boolean isDrawedBubble = false;
            for (int i = 0; i < histogramData.length; i++)
            {
                if(histogramData[i] == null)continue;
                int tl = histogramData[i][0] + histogramData[i][1] + histogramData[i][2];
                isDrawedBubble = drawBubble(canvas, paint, width, height, i, tl, maxValue[0]);
                if(isDrawedBubble)break;
            }
            if(!isDrawedBubble)
            {
                drawBubbleDefault(canvas, paint, width, height, maxValue[1], maxValue[0], maxValue[0]);
            }
        }
    }

    private int[] getMaxValue(int[][] histogramData)
    {
        int[] max = {0, 0};
        for (int i = 0; i < histogramData.length; i++)
        {
            int[] d = histogramData[i];
            if(d == null || d.length <= 0)
            {
                continue;
            }
            int sleepData = d[0] + d[1] + d[2];
            if(sleepData > max[0])
            {
                max[0] = sleepData;
                max[1] = i;
            }
        }
        return max;
    }

    private void drawHisgram(Canvas canvas, Paint paint, float width, float height, int i, int[] value, int maxValue)
    {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(hisgramWidth);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.BEVEL);
        paint.setStrokeCap(Paint.Cap.ROUND);
        float subHe = height - textSizePX * 3 + textSizePX/2;
        float subValue = subHe/maxValue;
        float offset = CommonUtils.Dp2Px(getContext(), 5);
        float drawX = hisgramWidth * 3 + (hisgramWidth * i) + spaceWidth * i;
        // 0, 深睡。1，浅睡。2，清醒。
        if(value != null && value[2] > 0)
        {
            paint.setColor(wakeColor);
            float endY = (subHe - subValue * (value[0] + value[1] + value[2]));
            canvas.drawLine(drawX,
                    subHe,
                    drawX,
                    endY < offset ? offset : endY,
                    paint);
        }
        if(value != null && value[1] > 0)
        {
            paint.setColor(lightColor);
            float endY = (subHe - subValue * (value[0] + value[1]));
            canvas.drawLine(drawX,
                    subHe,
                    drawX,
                    endY < offset ? offset : endY,
                    paint);
        }
        if(value != null && value[0] > 0)
        {
            paint.setColor(deepColor);
            float endY = (subHe - subValue * (value[0]));
            canvas.drawLine(drawX,
                    subHe,
                    drawX,
                    endY < offset ? offset : endY,
                    paint);
        }
        String t = hisgramTime[i];
        paint.setColor(belowTextColor);
        paint.setTextSize(smallSizePX);
        paint.setStyle(Paint.Style.FILL);
        float tvWidth = CommonUtils.getTextViewWidth(getContext(), t, textSize);
        float tx = hisgramWidth * 3 + (hisgramWidth * i) + spaceWidth * i - tvWidth/4;
        float ty = height - textSizePX;
//        Log.i(TAG, "当前日期：" + t + "  当前x轴：" + tx + "  当前y轴：" + ty + "  当前下标：" + i);
        canvas.drawText(t, tx, ty, paint);
    }

    private boolean drawBubble(Canvas canvas, Paint paint, float width, float height, int i, int value, int maxValue)
    {
        float subHe = height - textSizePX * 3 + textSizePX/2;
        float subValue = subHe/maxValue;
        float endY = (subHe - subValue * value);
        float offset = CommonUtils.Dp2Px(getContext(), 5);
        float drawX = hisgramWidth * 3 + (hisgramWidth * i) + spaceWidth * i;
        Log.i(TAG, "点击的X坐标：" + touchPointor.x + "--" + end_Touch_Able);
        float xValue = (drawX + hisgramWidth) - touchPointor.x;
        if(Math.abs(xValue) <= hisgramWidth)
        {
            Log.i(TAG, "点击的X坐标：符合条件的-- " + xValue + " -- " + hisgramWidth/2);
            Bitmap bubble = BitmapFactory.decodeResource(getResources(), bubbleID);
            float bubbleWidth = bubble.getWidth();
            float bubbleHeight = bubble.getHeight();
            RectF rec = new RectF();
            float start = drawX - bubbleWidth/2;
            if(start < 0)start = 0;
            rec.left = start;
            rec.top = (endY - bubbleHeight) < 0 ? offset : (endY - bubbleHeight);
            rec.right = start + bubbleWidth;
            rec.bottom = rec.top + bubbleHeight;
            canvas.drawBitmap(bubble, null, rec, paint);
            drawRecBubble(canvas, paint, width, height, i, drawX - bubbleWidth/2, rec.top);
            return true;
        }
        return false;
    }

    private void drawRecBubble(Canvas canvas, Paint paint, float width, float height, int i, float drawX, float drawTop)
    {
        int[] sleepD = histogramData[i];
        drawDeepContent(canvas, paint, deepColor, sleepD[0], drawX, drawTop);
        drawDeepContent(canvas, paint, lightColor, sleepD[1], drawX, drawTop + smallSizePX + smallSizePX/6);
        drawDeepContent(canvas, paint, wakeColor, sleepD[2], drawX, drawTop + smallSizePX * 2 + smallSizePX/6);
    }

    private void drawDeepContent(Canvas canvas, Paint paint, int deepColor, int i, float drawX, float drawTop)
    {
        paint.setColor(deepColor);
        Rect rec = new Rect();
        rec.left = (int)(drawX + smallSizePX/3);
        rec.top = (int) (drawTop + smallSizePX/3);
        rec.right = (int) (drawX + smallSizePX);
        rec.bottom = (int) (drawTop + smallSizePX);
        canvas.drawRect(rec, paint);
        int sleepTime = i;
        int h = sleepTime/60;
        int min = sleepTime % 60;
        StringBuffer times = new StringBuffer();
        if(h > 0)
        {
            times.append(h);
            times.append("h");
        }
        if(min > 0)
        {
            times.append(min);
            times.append("min");
        }
        else
        {
            times.append("0");
            times.append("min");
        }
        paint.setColor(belowTextColor);
        paint.setTextSize(smallSizePX);
        canvas.drawText(times.toString(), drawX + smallSizePX, drawTop + smallSizePX, paint);
    }

    private boolean drawBubbleDefault(Canvas canvas, Paint paint, float width, float height, int i, int value, int maxValue)
    {
        float subHe = height - textSizePX * 3 + textSizePX/2;
        float subValue = subHe/maxValue;
        float endY = (subHe - subValue * value);
        float offset = CommonUtils.Dp2Px(getContext(), 5);
        float drawX = hisgramWidth * 3 + (hisgramWidth * i) + spaceWidth * i;

        Log.i(TAG, "点击的X坐标：" + touchPointor.x + "--" + end_Touch_Able);
        float xValue = (drawX + hisgramWidth) - touchPointor.x;
            Log.i(TAG, "点击的X坐标：符合条件的-- " + xValue + " -- " + hisgramWidth/2);
            Bitmap bubble = BitmapFactory.decodeResource(getResources(), bubbleID);
            float bubbleWidth = bubble.getWidth();
            float bubbleHeight = bubble.getHeight();
            RectF rec = new RectF();
            float start = drawX - bubbleWidth/2;
            rec.left = start;
            rec.top = (endY - bubbleHeight) < 0 ? offset : (endY - bubbleHeight);
            rec.right = start + bubbleWidth;
            rec.bottom = rec.top + bubbleHeight;
            canvas.drawBitmap(bubble, null, rec, paint);
            drawRecBubble(canvas, paint, width, height, i, drawX - bubbleWidth/2, rec.top);

//            paint.setStyle(Paint.Style.FILL);
//            paint.setColor(Color.BLACK);
//            int[] va = histogramData[i];
//            StringBuffer sb = new StringBuffer();
//            sb.append(va[0]);
//            sb.append("h");
//            sb.append(va[1]);
//            sb.append("h");
//            sb.append(va[2]);
//            sb.append("h");
//            String stepValue = sb.toString();
//            paint.setTextSize(textSizePX);
//            float tvWidth = CommonUtils.getTextViewWidth(getContext(), stepValue, textSize);
//            canvas.drawText(stepValue, drawX - tvWidth/3, rec.top + tvWidth/3*2, paint);
//            String unit = getResources().getString(unitText);
//            float tvWidthb = CommonUtils.getTextViewWidth(getContext(), unit, smallSize);
//            paint.setColor(belowTextColor);
//            paint.setTextSize(smallSizePX);
//            canvas.drawText(unit, drawX - tvWidthb/3, rec.top + tvWidth/3*2 + smallSizePX, paint);
            return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
//        float tochX = getX();
//        float tochY = getY();
//        touchPointor.x = tochX;
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                touchPointor.x = event.getRawX();
                if(touchPointor.x <= end_Touch_Able)
                {
                    Log.i(TAG, "X坐标：符合条件的" + touchPointor.x);
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
//                touchPointor.x = event.getRawX();
//                if(touchPointor.x <= end_Touch_Able)
//                {
//                    Log.i(TAG, "X坐标：符合条件的" + touchPointor.x);
//                    postInvalidate();
//                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }
}
