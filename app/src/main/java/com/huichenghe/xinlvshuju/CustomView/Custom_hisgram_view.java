package com.huichenghe.xinlvshuju.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
public class Custom_hisgram_view extends ImageView
{
    public static final String TAG = "Custom_hisgram_view";
    private int textSize = 16;
    private int smallSize = 12;
    private float textSizePX, smallSizePX;
    private int hisgramColor, belowTextColor, upTextColor;
    private int[] hisgramData;
    private String[] hisgramTime;
    private float hisgramWidth;
    private float spaceWidth;
    private float end_Touch_Able;
    private int bubbleID;
    private int unitText;
    private TouchPointor touchPointor;


    private class TouchPointor
    {
        float x, y;
    }

    public Custom_hisgram_view(Context context)
    {
        super(context);
    }

    public Custom_hisgram_view(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initView(attrs);
    }

    public Custom_hisgram_view(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    public void setData(int[] data, String[] hisgramTime)
    {
        this.hisgramData = data;
        this.hisgramTime = hisgramTime;
        postInvalidate();
    }


    private void initView(AttributeSet attrs)
    {
        touchPointor = new TouchPointor();
        touchPointor.x = 0;
        touchPointor.y = 12;
        TypedArray ty = getContext().obtainStyledAttributes(attrs, R.styleable.Custom_hisgram_view);
        hisgramColor = ty.getColor(R.styleable.Custom_hisgram_view_hisgram_color, Color.WHITE);
        belowTextColor = ty.getColor(R.styleable.Custom_hisgram_view_hisgram_bottom_text_color, Color.GRAY);
        upTextColor = ty.getColor(R.styleable.Custom_hisgram_view_hisgram_top_text_color, Color.BLACK);
        hisgramWidth = ty.getDimension(R.styleable.Custom_hisgram_view_hisgram_width_dp, 10);
        bubbleID = ty.getResourceId(R.styleable.Custom_hisgram_view_hisgram_bubble_icon, R.mipmap.bubble_step);
        unitText = ty.getResourceId(R.styleable.Custom_hisgram_view_hisgram_unit_text, R.string.step_simple_one);
        spaceWidth = ty.getDimension(R.styleable.Custom_hisgram_view_space_width, 10);
        smallSize = (int) ty.getDimension(R.styleable.Custom_hisgram_view_hint_text_size, 12);
        textSize = (int) ty.getDimension(R.styleable.Custom_hisgram_view_hint_big_text_size, 16);
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

        if(hisgramData != null && hisgramData.length > 0)
        {
            int maxValue[] = getMaxValue(hisgramData);
            end_Touch_Able = hisgramWidth * 3 + hisgramWidth * hisgramData.length + spaceWidth * hisgramData.length;
            for (int i = 0; i < hisgramData.length; i++)
            {
                drawHisgram(canvas, paint, width, height, i, hisgramData[i], maxValue[0]);
            }
            boolean isDrawedBubble = false;
            for (int i = 0; i < hisgramData.length; i++)
            {
                isDrawedBubble = drawBubble(canvas, paint, width, height, i, hisgramData[i], maxValue[0]);
                if(isDrawedBubble)break;
            }
            if(!isDrawedBubble)
            {
                drawBubbleDefault(canvas, paint, width, height, maxValue[1], maxValue[0], maxValue[0]);
            }
        }
    }

    private int[] getMaxValue(int[] hisgramData)
    {
        int[] max = {0, 0};
        for (int i = 0; i < hisgramData.length; i++)
        {
            if(hisgramData[i] > max[0])
            {
                max[0] = hisgramData[i];
                max[1] = i;
            }
        }
        return max;
    }

    private void drawHisgram(Canvas canvas, Paint paint, float width, float height, int i, int value, int maxValue)
    {
        paint.setColor(hisgramColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(hisgramWidth);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.BEVEL);
        paint.setStrokeCap(Paint.Cap.ROUND);
        float subHe = height - textSizePX * 3 + textSizePX/2;
        float subValue = subHe/maxValue;
        float endY = (subHe - subValue * value);
        float offset = CommonUtils.Dp2Px(getContext(), 5);
        float drawX = hisgramWidth * 3 + (hisgramWidth * i) + spaceWidth * i;
        canvas.drawLine(drawX,
                        subHe,
                        drawX,
                        endY < offset ? offset : endY,
                        paint);
        if(hisgramTime == null || hisgramTime[i] == null || hisgramTime[i].equals(""))
        {
            return;
        }
        if(hisgramTime[i].contains(":"))
        {
            if(i % 4 == 0)
            {
                paint.setColor(belowTextColor);
                paint.setTextSize(textSizePX);
                paint.setStyle(Paint.Style.FILL);
                String t = hisgramTime[i];
                float tx = hisgramWidth + hisgramWidth/3 + (hisgramWidth * i) + spaceWidth * i;
                float ty = height - textSizePX;
                canvas.drawText(t, tx, ty, paint);
            }
        }
        else
        {
            paint.setColor(belowTextColor);
            paint.setTextSize(textSizePX);
            paint.setStyle(Paint.Style.FILL);
            String t = hisgramTime[i];
            float tvWidth = CommonUtils.getTextViewWidth(getContext(), t, textSize);
            float tx = hisgramWidth * 3 + (hisgramWidth * i) + spaceWidth * i - tvWidth/3;
            float ty = height - textSizePX;
            canvas.drawText(t, tx, ty, paint);
        }

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
            float bubbleWidth = (float)bubble.getWidth();
            float bubbleHeight = (float)bubble.getHeight();
            System.out.println("cecece"+bubbleHeight);
            RectF rec = new RectF();
            rec.left = drawX - bubbleWidth/2;
            rec.top = (endY - bubbleHeight) < 0 ? offset : (endY - bubbleHeight);
            rec.right = drawX + bubbleWidth/2;
            rec.bottom = rec.top + bubbleHeight;
            canvas.drawBitmap(bubble, null, rec, paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.BLACK);
            String stepValue = String.valueOf(hisgramData[i]);
            paint.setTextSize(textSizePX);
            float tvWidth = CommonUtils.getTextViewWidth(getContext(), stepValue, textSize);
            canvas.drawText(stepValue, drawX - tvWidth/3, rec.top + tvWidth/3*2, paint);
            String unit = getResources().getString(unitText);
            float tvWidthb = CommonUtils.getTextViewWidth(getContext(), unit, smallSize);
            paint.setColor(belowTextColor);
            paint.setTextSize(smallSizePX);
            canvas.drawText(unit, drawX - tvWidthb/3, rec.top + tvWidth/3*2 + smallSizePX, paint);
            return true;
        }
        return false;
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
            rec.left = drawX - bubbleWidth/2;
            rec.top = (endY - bubbleHeight) < 0 ? offset : (endY - bubbleHeight);
            rec.right = drawX + bubbleWidth/2;
            rec.bottom = rec.top + bubbleHeight;
            canvas.drawBitmap(bubble, null, rec, paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.BLACK);
            String stepValue = String.valueOf(hisgramData[i]);
            paint.setTextSize(textSizePX);
            float tvWidth = CommonUtils.getTextViewWidth(getContext(), stepValue, textSize);
            canvas.drawText(stepValue, drawX - tvWidth/3, rec.top + tvWidth/3*2, paint);
            String unit = getResources().getString(unitText);
            float tvWidthb = CommonUtils.getTextViewWidth(getContext(), unit, smallSize);
            paint.setColor(belowTextColor);
            paint.setTextSize(smallSizePX);
            canvas.drawText(unit, drawX - tvWidthb/3, rec.top + tvWidth/3*2 + smallSizePX, paint);
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
