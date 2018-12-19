package com.huichenghe.xinlvshuju.CustomView;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.huichenghe.xinlvshuju.R;

/**
 * Created by lixiaoning on 2016/10/18.
 */
public class Custom_circle_loving_care extends ImageView
{
    private int innerIcon;
    private int startColor, endColor;
    private float circleWidth;
    private int degrees;
    private Bitmap icon;
    private Paint paint;
    public Custom_circle_loving_care(Context context)
    {
        super(context);
    }

    public Custom_circle_loving_care(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        intit(context, attrs);
    }

    public Custom_circle_loving_care(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        intit(context, attrs);
    }


    private void intit(Context context, AttributeSet attrs)
    {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Custom_circle_loving_care);
        innerIcon = ta.getResourceId(R.styleable.Custom_circle_loving_care_inner_icon, R.mipmap.attion_defalt_head);
        startColor = ta.getColor(R.styleable.Custom_circle_loving_care_circle_start_color,
                context.getResources().getColor(R.color.broan_color_start));
        endColor = ta.getColor(R.styleable.Custom_circle_loving_care_circle_end_color,
                context.getResources().getColor(R.color.broan_color));
        circleWidth = ta.getDimension(R.styleable.Custom_circle_loving_care_circle_width_attion, 10);
        icon = BitmapFactory.decodeResource(getResources(), innerIcon);
        ta.recycle();
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public void setData(int degree)
    {
        this.degrees = degree;
        postInvalidate();
    }

    public void setInnerIcon(int id)
    {
        this.innerIcon = id;
        postInvalidate();
    }
    public void setInnerIconBigmap(Bitmap bigmap)
    {
        this.icon = bigmap;
        postInvalidate();
    }

    public void runAnima(int degrees)
    {
        ValueAnimator animator = ValueAnimator.ofObject(new FloatEvaluator(), 0, degrees);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float va = (float) animation.getAnimatedValue();
                setData((int) va);
            }
        });
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
        int width = getMeasuredWidth();
        int height= getMeasuredHeight();
        drawCircleIcon(canvas, width, height);
        drawCircleProgress(canvas, width, height);
    }

    private void drawCircleProgress(Canvas canvas, int width, int height)
    {
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.BEVEL);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(circleWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setShader(new SweepGradient(width/2, height/2, new int[]{startColor, endColor}, null));
        RectF recf = new RectF();
        recf.left = 0 + circleWidth/2;
        recf.top = 0 + circleWidth/2;
        recf.right = width - circleWidth/2;
        recf.bottom = height - circleWidth/2;
        canvas.rotate(-90, width/2, height/2);
        canvas.drawArc(recf, 0, degrees, false, paint);
        canvas.rotate(90, width/2, height/2);
        paint.setColor(startColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(null);
        canvas.drawCircle(width/2, 0 + circleWidth/2, circleWidth/2, paint);
    }

    private void drawCircleIcon(Canvas canvas, int width, int height)
    {
        if(icon == null)
        {
            icon = BitmapFactory.decodeResource(getResources(), innerIcon);
        }
        RectF rec = new RectF();
        rec.left = 0 + circleWidth/2 * 3;
        rec.top = 0 + circleWidth/2 * 3;
        rec.right = width - circleWidth/2 * 3;
        rec.bottom = height - circleWidth/2 * 3;
        canvas.drawBitmap(createCircleImage(icon, width), null, rec, paint);
    }

    private Bitmap createCircleImage(Bitmap source, int min)
    {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
        /**
         * 产生一个同样大小的画布
         */
        Canvas canvas = new Canvas(target);
        /**
         * 首先绘制圆形
         */
        canvas.drawCircle(min/2, min/2, min/2, paint);
        /**
         * 使用SRC_IN
         */
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        RectF rec = new RectF();
        rec.left = 0;
        rec.top = 0;
        rec.right = min;
        rec.bottom = min;
        /**
         * 绘制图片
         */
        canvas.drawBitmap(source, null, rec, paint);
        return target;
    }
}
