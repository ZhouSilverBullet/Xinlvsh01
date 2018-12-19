package com.huichenghe.xinlvshuju.CustomView;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.huichenghe.xinlvshuju.CommonUtilss.CommonUtils;
import com.huichenghe.xinlvshuju.R;

/**
 * Created by lixiaoning on 2016/9/28.
 */
public class Custom_movement_progress extends ImageView
{
    private float progressWidth;
    private int circle_background;
    private int progress_backgrund;
    private int pointerId;
    private int valueDegree = 0;

    public Custom_movement_progress(Context context)
    {
        super(context);
    }

    public Custom_movement_progress(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        intit(attrs);
    }

    public Custom_movement_progress(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        intit(attrs);
    }

    private void intit(AttributeSet attrs)
    {
        TypedArray ty = getContext().obtainStyledAttributes(attrs, R.styleable.Custom_movement_progress);
        progressWidth = ty.getDimension(R.styleable.Custom_movement_progress_progress_width_movement, 16);
        circle_background = ty.getColor(R.styleable.Custom_movement_progress_circle_background_movement, 0X2F385C);
        progress_backgrund = ty.getColor(R.styleable.Custom_movement_progress_progress_background_movement, 0X15EBF5);
        pointerId = ty.getResourceId(R.styleable.Custom_movement_progress_movement_pointer_icon, R.mipmap.pointer_active);
        ty.recycle();
    }

    public void setProgress(int value)
    {
        this.valueDegree = value;
        postInvalidate();
    }

    public void runAnimate(int value)
    {
        ValueAnimator animator = ValueAnimator.ofObject(new FloatEvaluator(), 0, value);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float v = (float) animation.getAnimatedValue();
                setProgress((int)v);
            }
        });
        animator.setDuration(400);
        animator.start();
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
        float off = CommonUtils.Dp2Px(getContext(), 15);
        drawCircleBackground(paint, canvas, width, height, off);
        if(valueDegree != 0)
        {
            drawProgress(paint, canvas, width, height, off);
        }

    }


    private void drawProgress(Paint paint, Canvas canvas, float width, float height, float off)
    {
        paint.setColor(progress_backgrund);
        RectF rectF = new RectF();
        rectF.left = 0 + progressWidth/2 + off;
        rectF.top = 0 + progressWidth/2 + off;
        rectF.right = width - progressWidth/2 - off;
        rectF.bottom = height - progressWidth/2 - off;
        canvas.drawArc(rectF, 45, valueDegree, false, paint);

//        float pointerX = (float)(width/2 + Math.cos(Math.toRadians(valueDegree)) * (width/2 - progressWidth/2 - off));
//        float pointerY = (float)(width/2 + Math.sin(Math.toRadians(valueDegree)) * (width/2 - progressWidth/2 - off));


        float pointerX = (float)(width/2 + ((Math.cos(Math.toRadians(valueDegree + 45))) * (width/2 - progressWidth/2 - off)));
        float pointerY = (float)(width/2 + Math.sin(Math.toRadians(valueDegree + 45)) * (width/2 - progressWidth/2 - off));
        Bitmap icon = BitmapFactory.decodeResource(getResources(), pointerId);
        float wi = icon.getWidth();
        float he = icon.getHeight();
        RectF re = new RectF();
        re.left = pointerX - wi/3;
        re.top = pointerY - he/3;
        re.right = pointerX + wi/3;
        re.bottom = pointerY + he/3;
        canvas.drawBitmap(icon, null, re, paint);
    }

    private void drawCircleBackground(Paint paint, Canvas canvas, float width, float height, float off)
    {
        canvas.rotate(90, width/2, height/2);
        paint.setColor(circle_background);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(progressWidth);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.BEVEL);
        paint.setStrokeCap(Paint.Cap.ROUND);
        RectF rectF = new RectF();
        rectF.left = 0 + progressWidth/2 + off;
        rectF.top = 0 + progressWidth/2 + off;
        rectF.right = width - progressWidth/2 - off;
        rectF.bottom = height - progressWidth/2 - off;
        canvas.drawArc(rectF, 45, 270, false, paint);
    }
}
