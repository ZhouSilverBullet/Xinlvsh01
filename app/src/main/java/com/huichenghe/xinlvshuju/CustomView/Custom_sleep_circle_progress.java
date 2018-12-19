package com.huichenghe.xinlvshuju.CustomView;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.huichenghe.xinlvshuju.CommonUtilss.CommonUtils;
import com.huichenghe.xinlvshuju.R;

/**
 * Created by lixiaoning on 2016/9/27.
 */
public class Custom_sleep_circle_progress extends ImageView
{
    private float smallTextSizeDP = 24;
    private float bigTextSizeDP = 64;
    private float smallTextSizePX;
    private float bigTextSizePX;
    private int sleep_pointer;
    private int start_progress_color;
    private int end_progress_color;
    private int circle_background_color, smallTextColor;
    private float progressWidth;
    private String complet = "0%", sleepTimeH = String.valueOf(0), sleepTimeM = String.valueOf(0);
    private int degreeSleep;

    public Custom_sleep_circle_progress(Context context)
    {
        super(context);
    }

    public Custom_sleep_circle_progress(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        inti(attrs);
    }

    public Custom_sleep_circle_progress(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        inti(attrs);
    }

    private void inti(AttributeSet attrs)
    {
        TypedArray ty = getContext().obtainStyledAttributes(attrs, R.styleable.Custom_sleep_circle_progress);
        sleep_pointer = ty.getResourceId(R.styleable.Custom_sleep_circle_progress_pointer_icon, R.mipmap.pointer_sleep);
        smallTextSizeDP = ty.getDimension(R.styleable.Custom_sleep_circle_progress_small_text_t_size, 24);
        bigTextSizeDP = ty.getDimension(R.styleable.Custom_sleep_circle_progress_big_text_t_size, 64);
        start_progress_color = ty.getInt(R.styleable.Custom_sleep_circle_progress_start_progress_color, 0X30DEF7);
        end_progress_color = ty.getInt(R.styleable.Custom_sleep_circle_progress_end_progress_color, 0X3D72E2);
        circle_background_color = ty.getInt(R.styleable.Custom_sleep_circle_progress_circle_background, 0XEAEDF2);
        progressWidth = ty.getDimension(R.styleable.Custom_sleep_circle_progress_circle_progress_width, 20);
        smallTextColor = ty.getColor(R.styleable.Custom_sleep_circle_progress_small_text_color, 0X737373);
        ty.recycle();
        smallTextSizePX = CommonUtils.getTextViewWidth(getContext(), "A", smallTextSizeDP);
        bigTextSizePX = CommonUtils.getTextViewWidth(getContext(), "A", bigTextSizeDP);
    }

    public void setSleepValue(String comp, String sleepTimeH, String sleepTimeM, int degree)
    {
        this.complet = comp;
        this.sleepTimeH = sleepTimeH;
        this.sleepTimeM = sleepTimeM;
        this.degreeSleep = degree;
        postInvalidate();
    }

    public void runAnimate(final String comp, final String sleepTimeH, final String sleepTimeM, final int degree)
    {
        ValueAnimator animator = ValueAnimator.ofObject(new FloatEvaluator(), 0, degree);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float anValue = (float) animation.getAnimatedValue();
                setSleepValue(comp, sleepTimeH, sleepTimeM, (int)anValue);
            }
        });
        animator.setDuration(600);
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        subOnDraw(canvas);
        super.onDraw(canvas);
    }

    private void subOnDraw(Canvas canvas)
    {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        drawSleepText(paint, canvas, width, height);
        drawCircleBackground(paint, canvas, width, height);
        drawProgressCircle(paint, canvas, width, height);
        if(degreeSleep > 20 && degreeSleep < 340)
        {
            drawStartCircle(paint, canvas, width, height);
        }
    }

    private void drawStartCircle(Paint paint, Canvas canvas, int width, int height)
    {
        float off = CommonUtils.Dp2Px(getContext(), 15);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(start_progress_color);
        canvas.rotate(90, width/2, height/2);
        canvas.drawCircle(width/2, 0 + off + progressWidth/2, progressWidth/2, paint);
    }

    private void drawProgressCircle(Paint paint, Canvas canvas, int width, int height)
    {
        float off = CommonUtils.Dp2Px(getContext(), 15);
        canvas.rotate(-90, width/2, height/2);
        paint.setStrokeWidth(progressWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.BEVEL);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setShader(new SweepGradient(width/2, height/2, new int[]{start_progress_color, end_progress_color}, null));
        RectF ref = new RectF();
        ref.left = 0 + progressWidth/2 + off;
        ref.top = 0 + progressWidth/2 + off;
        ref.right = width - progressWidth/2 - off;
        ref.bottom = height - progressWidth/2 - off;
        canvas.drawArc(ref, 0, degreeSleep, false, paint);


        float pointerX = (float)(width/2 + ((Math.cos(Math.toRadians(degreeSleep))) * (width/2 - progressWidth/2 - off)));
        float pointerY = (float)(width/2 + Math.sin(Math.toRadians(degreeSleep)) * (width/2 - progressWidth/2 - off));
        Bitmap sleepPointer = BitmapFactory.decodeResource(getResources(), sleep_pointer);
        int pointWidth = sleepPointer.getWidth();
        int pointHeight = sleepPointer.getHeight();
        RectF refs = new RectF();
        refs.left = pointerX - pointWidth/2;
        refs.top = pointerY - pointHeight/2;
        refs.right = pointerX + pointWidth/2;
        refs.bottom = pointerY + pointHeight/2;
        canvas.drawBitmap(sleepPointer, null, refs, paint);
        paint.setShader(null);

    }

    private void drawSleepText(Paint paint, Canvas canvas, int width, int height)
    {
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(bigTextSizePX);
        float bigTextWidth = CommonUtils.getTextViewWidth(getContext(), sleepTimeH + "h" + sleepTimeM + "min", bigTextSizeDP);
        float firstX = width/2 - bigTextWidth/5;
        canvas.drawText(sleepTimeH, firstX, height/2 + bigTextSizePX/3, paint);       // 小时
        paint.setTextSize(smallTextSizePX);
        float firstTextVidth = CommonUtils.getTextViewWidth(getContext(), sleepTimeH, bigTextSizeDP);
        float secondX = firstX + firstTextVidth/3*2;
        paint.setColor(smallTextColor);
        canvas.drawText("h", secondX, height/2 + bigTextSizePX/3, paint);              // 单位
        paint.setTextSize(bigTextSizePX);
        paint.setColor(Color.BLACK);
        float thirdX = secondX + smallTextSizePX / 3 * 2;
        canvas.drawText(sleepTimeM, thirdX, height/2 + bigTextSizePX/3, paint);       // 分钟
        float minTextWidth = CommonUtils.getTextViewWidth(getContext(), sleepTimeM, bigTextSizeDP);
        float fourthTextWidth = thirdX + minTextWidth/3*2;
        paint.setTextSize(smallTextSizePX);
        paint.setColor(smallTextColor);
        canvas.drawText("min", fourthTextWidth, height/2 + bigTextSizePX/3, paint);   // 单位
        float comTextWidth = CommonUtils.getTextViewWidth(getContext(), complet, smallTextSizeDP);
        canvas.drawText(complet, width/2 - comTextWidth/3, height/2 + bigTextSizePX/3 *4, paint);
    }

    private void drawCircleBackground(Paint paint, Canvas canvas, int width, int height)
    {
        float offset = CommonUtils.Dp2Px(getContext(), 15);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(progressWidth);
        paint.setColor(circle_background_color);
        RectF retf = new RectF();
        retf.left = 0 + progressWidth/2 + offset;
        retf.top = progressWidth/2 + offset;
        retf.right = width - progressWidth/2 - offset;
        retf.bottom = width - progressWidth/2 - offset;
        canvas.drawArc(retf, 0, 360, false, paint);
    }
}
