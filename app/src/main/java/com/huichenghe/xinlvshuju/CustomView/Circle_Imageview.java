package com.huichenghe.xinlvshuju.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.huichenghe.xinlvshuju.R;

/**
 * Created by lixiaoning on 2016/11/10.
 */
public class Circle_Imageview extends ImageView
{
    private int color;
    private float radius;
    private Paint mPaint;
    public Circle_Imageview(Context context) {
        super(context);
    }

    public Circle_Imageview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    public Circle_Imageview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs)
    {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Circle_Imageview);
        color = ta.getColor(R.styleable.Circle_Imageview_circle_color, getResources().getColor(R.color.White_forme));
        radius = ta.getDimension(R.styleable.Circle_Imageview_circle_radius, 10);
        ta.recycle();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        subOnDraw(canvas);
    }

    private void subOnDraw(Canvas canvas)
    {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        drawCircle(canvas, width, height);
    }

    private void drawCircle(Canvas canvas, int width, int height)
    {
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width/2, width/2, width/2, mPaint);
    }
}
