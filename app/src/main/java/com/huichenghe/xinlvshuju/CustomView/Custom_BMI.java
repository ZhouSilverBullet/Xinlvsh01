package com.huichenghe.xinlvshuju.CustomView;

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
import android.widget.ImageView;

import com.huichenghe.xinlvshuju.CommonUtilss.CommonUtils;
import com.huichenghe.xinlvshuju.R;

/**
 * Created by lixiaoning on 2016/9/24.
 */
public class Custom_BMI extends ImageView
{
    private float circleWidth = 14;
    private float oneTextSize = 24;
    private float twoTextSize = 32;
    private float threeTextSize = 120;
    private float oneTextSizePx, twoTextSizePx, threeTextSizePx, circleWidthPx;
    private int lessColor, nomaleColor, muchColor;
    private int[] colors;
    private int width, height;
    private String BMI = "BMI";
    private String bmiValue = "55";
    private String bmiState = "很重";
    private int bitAddress = R.mipmap.persion_two;
    private int textChangeColor;
    public Custom_BMI(Context context)
    {
        super(context);
    }

    public Custom_BMI(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        intit(context, attrs);
    }

    public Custom_BMI(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        intit(context, attrs);
    }

    public void setTextValue(String BMI, String bmiValue, String bmiState, int bimp, int color)
    {
        this.bmiValue = bmiValue;
        this.bmiState = bmiState;
        this.BMI = BMI;
        this.bitAddress = bimp;
        this.textChangeColor = color;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private void intit(Context context, AttributeSet attrs)
    {
        oneTextSizePx = CommonUtils.getTextViewWidth(getContext(), "A", oneTextSize);
        twoTextSizePx = CommonUtils.getTextViewWidth(getContext(), "A", twoTextSize);
        threeTextSizePx = CommonUtils.getTextViewWidth(getContext(), "A", threeTextSize);
        circleWidthPx = CommonUtils.Dp2Px(getContext(), circleWidth);
        TypedArray ty = context.obtainStyledAttributes(attrs, R.styleable.Custom_BMI);
        lessColor = ty.getColor(R.styleable.Custom_BMI_less_color, 0x03E7F0);
        nomaleColor = ty.getColor(R.styleable.Custom_BMI_nomale_color, 0xF9386C);
        muchColor = ty.getColor(R.styleable.Custom_BMI_much_color, 0xFBA853);
        ty.recycle();
        colors = new int[]{nomaleColor, nomaleColor, muchColor, lessColor, lessColor, muchColor, nomaleColor, nomaleColor};
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        subDraw(canvas);
        super.onDraw(canvas);
    }

    private void subDraw(Canvas canvas)
    {
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        if(BMI != null && !BMI.equals(""))
        {
            drawBMI(paint, canvas);
        }
        if(bmiValue != null && !bmiValue.equals(""))
        {
            float textVidth = CommonUtils.getTextViewWidth(getContext(), bmiValue, threeTextSize);
            float x = width/2 - textVidth/5;
            float y = height/2 + threeTextSizePx/3;
            paint.setTextSize(threeTextSizePx);
            paint.setColor(getResources().getColor(textChangeColor));
            drawTextValue(paint, canvas, bmiValue, x, y);
        }
        if(bmiState != null && !bmiState.equals(""))
        {
            float textW = CommonUtils.getTextViewWidth(getContext(), bmiState, twoTextSize);
            float x = width/2 - textW / 3;
            float y = height - twoTextSizePx;
            paint.setTextSize(twoTextSizePx);
            paint.setColor(getResources().getColor(textChangeColor));
            drawTextValue(paint, canvas, bmiState, x, y);
        }
        drawBitmap(canvas, paint);
        drawArc(paint, canvas);



    }

    private void drawBitmap(Canvas canvas, Paint paint)
    {
        float textThreeWidth = CommonUtils.getTextViewWidth(getContext(), bmiValue, threeTextSize);
        Bitmap b = BitmapFactory.decodeResource(getResources(), bitAddress);
        canvas.drawBitmap(b, width/2 - textThreeWidth/2, height/2 - threeTextSizePx/2, paint);
    }

    private void drawTextValue(Paint paint, Canvas canvas, String bmiValue, float x, float y)
    {
        canvas.drawText(bmiValue, x, y, paint);
    }

    private void drawBMI(Paint paint, Canvas canvas)
    {
        float textVidth = CommonUtils.getTextViewWidth(getContext(), BMI, oneTextSize);
        paint.setColor(Color.BLACK);
        paint.setTextSize(oneTextSizePx);
        canvas.drawText(BMI, width/2 - textVidth/4, (height/2 - height/10 * 3), paint);
    }

    private void drawArc(Paint paint, Canvas canvas)
    {
        int min = Math.min(width, height);
        RectF ref = new RectF();
        ref.left = 0 + circleWidthPx;
        ref.top = 0 + circleWidthPx;
        ref.right = width - circleWidthPx;
        ref.bottom = height - circleWidthPx;
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.BEVEL);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(circleWidthPx);
        canvas.rotate(90, width/2, height/2);
        paint.setShader(new SweepGradient(width/2, height/2, colors, null));
        canvas.drawArc(ref, 45, 270, false, paint);

    }
}
