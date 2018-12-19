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
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.huichenghe.xinlvshuju.CommonUtilss.CommonUtils;
import com.huichenghe.xinlvshuju.R;

/**
 * Created by lixiaoning on 2016/9/26.
 */
public class StepProgressView extends ImageView
{
    private float smallTextDP = 24;
    private float bigTextDP = 72;
    private float smallTextPX, bigTextPX;
    private int textColor;
    private int progressBackground, progressColor;
    private int solidColor;
    private int pointerId, active_step;
    private float widthProgress;
    private int progressValue;
    private int complete = 0;
    private ValueAnimator animator;
    private String compString = "完成度 100%";
    public StepProgressView(Context context)
    {
        super(context);
    }

    public StepProgressView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initArgs(attrs);
    }

    public StepProgressView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initArgs(attrs);
    }

    public void setProgress(int comp)
    {
        this.complete = comp;
        postInvalidate();
    }

    public void updateTextValue(int pro, String comps)
    {
        this.progressValue = pro;
        this.compString = comps;
        postInvalidate();
    }


    public void cancleAnimate()
    {
        if(animator != null && animator.isRunning())
        {
            animator.cancel();
        }
    }
    public void runAnimate(float comp)
    {
        animator = ValueAnimator.ofObject(new FloatEvaluator(), 0, comp);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float val = (float) animation.getAnimatedValue();
                setProgress((int)val);
            }
        });
        animator.setDuration(400);
        animator.start();

    }

    public void initArgs(AttributeSet set)
    {
        smallTextPX = CommonUtils.getTextViewWidth(getContext(), "A", smallTextDP);
        bigTextPX = CommonUtils.getTextViewWidth(getContext(), "A", bigTextDP);
        TypedArray ty = getContext().obtainStyledAttributes(set, R.styleable.StepProgressView);
        textColor = ty.getColor(R.styleable.StepProgressView_text_color, Color.WHITE);
        progressBackground = ty.getColor(R.styleable.StepProgressView_progress_background, 0x30385D);
        solidColor = ty.getColor(R.styleable.StepProgressView_solid_color, 0x5E6784);
        progressColor = ty.getColor(R.styleable.StepProgressView_progress_color, 0x58FED6);
        pointerId = ty.getResourceId(R.styleable.StepProgressView_posint_img, R.mipmap.pointer_active);
        active_step = ty.getResourceId(R.styleable.StepProgressView_active_step_img, R.mipmap.active_steps);
        widthProgress = ty.getDimension(R.styleable.StepProgressView_progress_width, 20);
        ty.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        subDraw(canvas);
        super.onDraw(canvas);
    }

    private void subDraw(Canvas canvas)
    {
        float width = getMeasuredWidth();
        float height = getMeasuredHeight();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        drawSolidBackground(paint, canvas, width, height);
        drawCirclePregressBackground(paint, canvas, width, height);
        drawValue(paint, canvas, width, height);
        drawCircleProgress(paint, canvas, width, height);
    }

    private void drawValue(Paint paint, Canvas canvas, float width, float height)
    {
        paint.setColor(textColor);
        Bitmap stepB = BitmapFactory.decodeResource(getResources(), active_step);
        int w =  stepB.getWidth();
        int h = stepB.getHeight();
        RectF recf = new RectF();
        recf.left = width/8 * 3;
        recf.top = width/8 * 2 + smallTextPX/2;
        recf.right = width/8 * 3 + w;
        recf.bottom = width/8 * 2 + h + smallTextPX/2;
        canvas.drawBitmap(stepB, null, recf, paint);
        paint.setTextSize(smallTextPX);
        paint.setStyle(Paint.Style.FILL);
        float textWidth = CommonUtils.getTextViewWidth(getContext(), "Steps", smallTextDP);
        canvas.drawText("Steps", width/8 * 3 + w + textWidth/5, height/8 * 2 + smallTextPX/2*3, paint);
        float comTextWidth = CommonUtils.getTextViewWidth(getContext(), compString, smallTextDP);
        canvas.drawText(compString, width/2 - comTextWidth/3, (height/2 + smallTextPX * 3) + smallTextPX/2, paint);
        paint.setTextSize(bigTextPX);
        float bigTextWidth = CommonUtils.getTextViewWidth(getContext(), String.valueOf(progressValue), bigTextDP);
        canvas.drawText(String.valueOf(progressValue), width/2 - bigTextWidth/3, height/2 + bigTextPX/2, paint);
    }

    private void drawCircleProgress(Paint paint, Canvas canvas, float width, float height)
    {
        paint.setColor(progressColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(widthProgress);
        paint.setDither(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.BEVEL);
        canvas.rotate(-90, width/2, height/2);
        RectF ref = new RectF();
        ref.left = 0 + widthProgress/2 + CommonUtils.Dp2Px(getContext(), 15);
        ref.top = 0 + widthProgress/2 + CommonUtils.Dp2Px(getContext(), 15);
        ref.right = width - widthProgress/2 - CommonUtils.Dp2Px(getContext(), 15);
        ref.bottom = height - widthProgress/2 - CommonUtils.Dp2Px(getContext(), 15);
        canvas.drawArc(ref, 0, complete, false, paint);
        float pointerX = (float)(width/2 + (Math.cos(Math.toRadians(complete)) * (width/2 - widthProgress/2 - CommonUtils.Dp2Px(getContext(), 15))));
        float pointerY = (float)(width/2 + (Math.sin(Math.toRadians(complete)) * (width/2 - widthProgress/2 - CommonUtils.Dp2Px(getContext(), 15))));
        Bitmap pointer = BitmapFactory.decodeResource(getResources(), pointerId);
        int poWidth = pointer.getWidth();
        int poHeight = pointer.getHeight();
        RectF pointRec = new RectF();
        pointRec.left = pointerX - poWidth/2;
        pointRec.top = pointerY - poHeight/2;
        pointRec.right = pointerX + poWidth/2;
        pointRec.bottom = pointerY + poHeight/2;
        canvas.drawBitmap(pointer, null, pointRec, paint);



    }

    private void drawSolidBackground(Paint paint, Canvas canvas, float width, float height)
    {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(solidColor);
        canvas.drawCircle(width/2, height/2, width/2 - widthProgress + 1 - CommonUtils.Dp2Px(getContext(), 15), paint);
    }

    private void drawCirclePregressBackground(Paint paint, Canvas canvas, float width, float height)
    {
        RectF ref = new RectF();
        ref.left = 0 + widthProgress/2 + CommonUtils.Dp2Px(getContext(), 15);
        ref.top = 0 + widthProgress/2 + CommonUtils.Dp2Px(getContext(), 15);
        ref.right = width - widthProgress/2 - CommonUtils.Dp2Px(getContext(), 15);
        ref.bottom = height - widthProgress/2 - CommonUtils.Dp2Px(getContext(), 15);
        paint.setColor(progressBackground);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(widthProgress);
        canvas.drawArc(ref, 0, 360, false, paint);
    }
}
