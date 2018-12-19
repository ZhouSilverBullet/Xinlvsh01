package com.huichenghe.xinlvshuju.mainpack;

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
import com.huichenghe.xinlvshuju.R;

/**
 * Created by lixiaoning on 2016/7/13.
 */
public class BloodPressureView extends ImageView
{
    private int FIRST_LINE_COLOR = getResources().getColor(R.color.blood_first_line_color) ;
    private int SECOND_LINE_COLOR = getResources().getColor(R.color.blood_second_line_color);
    private int X_ASIX_COLOR = getResources().getColor(R.color.black_color_transparent);
    private int firstLineColor = FIRST_LINE_COLOR;
    private int secondLineColor = SECOND_LINE_COLOR;
    private int xAsixColor = X_ASIX_COLOR;
    private int yAsixColor = X_ASIX_COLOR;
    private int dividLineColor = X_ASIX_COLOR;
    private int textColor = X_ASIX_COLOR;
    private int textOther = X_ASIX_COLOR;
    private String expand_text_id = null, sysetolic_text_id = null;
    private float bigTextSize = 16, smallTextSize = 10;
    private Paint paint;
    private float height, width;
    private PathEffect eachEffect;
    private Path path = new Path();
    private float bigTextHeight, smallTextHeight;
    private float bigTextSizeInPx, smallTextSizeInPx;
    private int xCount = 140;
    private String[] xString = {"200", "180", "160", "140", "120", "100", "80", "60"};
    private String[] yString = {"0:00", "4:00", "8:00", "12:00", "16:00", "20:00", "23:00"};
    private int[] valueFirst;
    private int[] valueSecond;
    private String[] valueTime;
    private int[] times;
    private int offsetHeight = 60;
    private float lineStoke = 2;
    private float ridus = 5;
    private float widthSpace;
    private int smallIcon, bigIcon;

    public BloodPressureView(Context context)
    {
        super(context);
        intit();
    }
    public BloodPressureView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
        intit();
    }
    public BloodPressureView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BloodPressureView, defStyleAttr, 0);
        firstLineColor = typedArray.getColor(R.styleable.BloodPressureView_first_line_color, FIRST_LINE_COLOR);
        secondLineColor = typedArray.getColor(R.styleable.BloodPressureView_second_line_color, SECOND_LINE_COLOR);
        xAsixColor = typedArray.getColor(R.styleable.BloodPressureView_x_axis_color, X_ASIX_COLOR);
        yAsixColor = typedArray.getColor(R.styleable.BloodPressureView_y_axis_color, X_ASIX_COLOR);
        dividLineColor = typedArray.getColor(R.styleable.BloodPressureView_divide_line_color, X_ASIX_COLOR);
        textColor = typedArray.getColor(R.styleable.BloodPressureView_text_axis, X_ASIX_COLOR);
        textOther = typedArray.getColor(R.styleable.BloodPressureView_other_axis, X_ASIX_COLOR);
        expand_text_id = typedArray.getString(R.styleable.BloodPressureView_expand_text);
        sysetolic_text_id = typedArray.getString(R.styleable.BloodPressureView_sysitolic_text);
        bigTextSize = typedArray.getInt(R.styleable.BloodPressureView_big_text_size, 24);
        smallTextSize = typedArray.getInt(R.styleable.BloodPressureView_small_text_size, 16);
        lineStoke = typedArray.getInt(R.styleable.BloodPressureView_line_stoke, 2);
        ridus = typedArray.getDimension(R.styleable.BloodPressureView_pointe_radus, 4);
        smallIcon = typedArray.getResourceId(R.styleable.BloodPressureView_pointer_small_icon, R.mipmap.small_blood_pointer);
        bigIcon = typedArray.getResourceId(R.styleable.BloodPressureView_pointer_big_icon, R.mipmap.blood_big_pointer);
        widthSpace = typedArray.getDimension(R.styleable.BloodPressureView_width_space, 10);
        typedArray.recycle();
        intit();
    }

    private void intit()
    {
//        bigTextSizeInPx = DpUitls.SpToPx(getContext(), bigTextSize);
//        smallTextSizeInPx = DpUitls.SpToPx(getContext(), smallTextSize);
        bigTextSizeInPx = CommonUtils.getTextViewWidth(getContext(), "A", bigTextSize);
        smallTextSizeInPx = CommonUtils.getTextViewWidth(getContext(), "A", smallTextSize);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        lineStoke = compareDensity((int) lineStoke);
        ridus = compareDensity((int) ridus);
    }

    private float compareDensity(int lineStoke)
    {
        float density = getContext().getResources().getDisplayMetrics().density;
        if(density <= 1.5)
        {
            return lineStoke * 1.1f;
        }
        else if(density <= 2.5 && density > 1.5)
        {
            return lineStoke * 1.5f;
        }
        else if(density <= 3.5 && density > 2.5)
        {
            return lineStoke * 2f;
        }
        else if(density > 3.5)
        {
            return  lineStoke * 2.3f;
        }
        return lineStoke;
    }


    public void setBigTextSize(int size)
    {
        this.bigTextSize = size;
        bigTextSizeInPx = CommonUtils.getTextViewWidth(getContext(), "A", bigTextSize);
    }

    public void setSmallTextSize(int size)
    {
        this.smallTextSize = size;
        smallTextSizeInPx = CommonUtils.getTextViewWidth(getContext(), "A", smallTextSize);
    }

    public void setData(int[] upData, int[] unData, String[] valTime, int[] times)
    {
        this.valueFirst = upData;
        this.valueSecond = unData;
        this.valueTime = valTime;
        this.times = times;
        postInvalidate();
    }

    public float getSpaceWidth()
    {
        return widthSpace;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        subDraw(canvas);
    }

    private void subDraw(Canvas canvas)
    {
        height = getMeasuredHeight();
        width = getMeasuredWidth();
//        drawLegend(canvas);
        drawXiax(canvas);
        drawYAsix(canvas);
        if(valueFirst != null)
            drawFirstValue(canvas, secondLineColor);
        if(valueSecond != null)
            drawDashValue(canvas, firstLineColor);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return super.onTouchEvent(event);
    }

    private void drawDashValue(Canvas canvas, int col)
    {
        drawLineOrDashValue(canvas, eachEffect, valueSecond, col);
    }

    private void drawFirstValue(Canvas canvas, int col)
    {
        drawLineOrDashValue(canvas, eachEffect, valueFirst, col);
    }

    private void drawLineOrDashValue(Canvas canvas,  PathEffect effect, int[] values, int color)
    {
        int subX = (int)CommonUtils.getTextViewWidth(getContext(), "200", smallTextSize);
        int subValue = (int)CommonUtils.getTextViewWidth(getContext(), "8:00", smallTextSize);
//        eachEffect = new DashPathEffect(new float[]{10f, 3f}, 0);
        float subHeight = height - smallTextSizeInPx * 3;
        float yStart = height - smallTextSizeInPx/2*3 ;
        float eachHeight = subHeight/140;
        float xStart = subX/2 *3;
        float subWidth = width - xStart - smallTextSizeInPx/2;
        float eachwidth = subWidth/8;
        paint.setColor(color);
        paint.setStrokeWidth(lineStoke);
        paint.setStyle(Paint.Style.FILL);
        LinearGradient gradient = new LinearGradient(
                0,
                0,
                0,
                getHeight(),
                new int[]{color, getResources().getColor(R.color.Transparent_color)},
                null,
                LinearGradient.TileMode.CLAMP);
        paint.setShader(gradient);
        path.moveTo(xStart, yStart);
        for (int i = 0; i < values.length; i++)
        {
            if(i == 0)
            {
                path.lineTo(xStart + widthSpace * i, yStart);
            }
            path.lineTo(xStart + widthSpace * i, yStart - (eachHeight * (values[i] - 60)));
            if(i == values.length - 1)
            {
                path.lineTo(xStart + widthSpace * i, yStart);
            }
        }
        paint.setPathEffect(effect);
        path.close();
        canvas.drawPath(path, paint);
        path.reset();
        paint.setPathEffect(null);
        paint.setShader(null);
        Bitmap smallPointer = BitmapFactory.decodeResource(getResources(), smallIcon);
        int smallPointerWidth = smallPointer.getWidth();
        int smallPointerHeight = smallPointer.getHeight();
//        paint.setShader(new SweepGradient(width/2, height/2, new int[]{start_progress_color, end_progress_color}, null));
        for (int i = 0; i < values.length; i++)
        {
//            LinearGradient gradient = new LinearGradient();
//            path.lineTo(xStart + subValue/2 + eachwidth * i, yStart + (subHeight - (values[i]- 40) * eachHeight));
//            drawCircle(canvas, xStart + subValue/2 + eachwidth * i, yStart + (subHeight - (values[i]- 40) * eachHeight), (int) ridus);
//            paint.setShader(new SweepGradient());
//            paint.setShader();
//            paint.setShader(new RadialGradient());
            float x = xStart + widthSpace * i;
            float y = yStart - (eachHeight * (values[i] - 60));
            RectF rectF = new RectF();
            rectF.left = x - smallPointerWidth/2;
            rectF.top = y - smallPointerWidth/2;
            rectF.right = x + smallPointerWidth/2;
            rectF.bottom = y + smallPointerHeight/2;
            canvas.drawBitmap(smallPointer, null, rectF, paint);
        }
    }

    private void drawYAsix(Canvas canvas)
    {
        float subHeight = height - smallTextSizeInPx * 3;
        float eachCount = subHeight/7;
        int subX = (int)CommonUtils.getTextViewWidth(getContext(), "200", smallTextSize);
        float startX = smallTextSizeInPx/2*3;
        float subWidth = (width - startX);
        canvas.drawLine(subX/2*3, height - eachCount * 10, subX/2 * 3, height - smallTextSizeInPx/2*3, paint);
        paint.setStyle(Paint.Style.FILL);
        if(times != null && times.length > 0)
        {
            for (int i = 0; i < times.length; i++)
            {
                float eachWidth = subWidth/times.length;
                int tw = (int)CommonUtils.getTextViewWidth(getContext(), String.valueOf(times[i] + 1), smallTextSize);
                canvas.drawText(String.valueOf(times[i] + 1), subX/2*3 + widthSpace * i - tw/3, height - smallTextSizeInPx/2,  paint);
            }
        }

//        eachEffect = new CornerPathEffect(0);
//        paint.setColor(Color.BLACK);
//        paint.setStyle(Paint.Style.FILL);
//        path.moveTo(100, height - eachCount * (10 - 0));
//        path.lineTo(100, height - eachCount * (10 - 0));
//        path.lineTo(100, height - eachCount * (10 - 8));
//        paint.setPathEffect(eachEffect);
//        canvas.drawPath(path, paint);
//        path.reset();
    }

    // 画图例
    private void drawLegend(Canvas canvas)
    {
        drawfistLine(canvas);
        drawFirstText(canvas);
        drawSecondDashLine(canvas);
        drawSecondText(canvas);
    }

    public void setStokeWidth(int stokeWidth)
    {
        this.lineStoke = stokeWidth;
    }

    private void drawXiax(Canvas canvas)
    {
        float subHeight = height - smallTextSizeInPx * 3;
        float eachCount = subHeight/7;
        paint.setTextSize(smallTextSizeInPx);
        int subX = (int)CommonUtils.getTextViewWidth(getContext(), "200", smallTextSize);
        eachEffect = new CornerPathEffect(0);
        for (int i = 0; i < xString.length; i++)
        {
            paint.setColor(xAsixColor);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawText(xString[i], subX/2, height - eachCount * (7 - i) - smallTextSizeInPx/2*3, paint);
            paint.setColor(getResources().getColor(R.color.gray_color));
            path.moveTo(subX/2 * 3, height - eachCount * (7 - i) - smallTextSizeInPx/2*3);
            path.lineTo(subX/2 * 3, height - eachCount * (7 - i) - smallTextSizeInPx/2*3);
            path.lineTo(width - subX/2, height - eachCount * (7 - i) - smallTextSizeInPx/2*3);
            paint.setPathEffect(eachEffect);
            if(i >= 7)
            {
                paint.setColor(Color.BLACK);
            }
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(path, paint);
            path.reset();
        }
    }

    private void drawfistLine(Canvas canvas)
    {
//        paint.setStrokeWidth(lineStoke);
//        paint.setColor(firstLineColor);
//        eachEffect = new CornerPathEffect(0);
//        path.moveTo((width/9) * 2, height / 9);
//        path.lineTo((width/9) * 2, height / 9);
//        path.lineTo((width/9) * 3, height / 9);
//        paint.setPathEffect(eachEffect);
//        canvas.translate(0, 0);
//        canvas.drawPath(path, paint);
//        path.reset();
        paint.setColor(firstLineColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle((float)((width/9) * 2.5), height / 9, ridus, paint);

    }

    private void drawSecondDashLine(Canvas canvas)
    {
//        paint.setStrokeWidth(lineStoke);
//        paint.setColor(secondLineColor);
//        eachEffect = new DashPathEffect(new float[]{10f, 3f}, 0);
//        path.moveTo((width / 9) * 5, height / 9);
//        path.lineTo((width/9) * 5, height / 9);
//        path.lineTo((width/9) * 6, height / 9);
//        paint.setPathEffect(eachEffect);
//        canvas.translate(0, 0);
//        canvas.drawPath(path, paint);
//        path.reset();
        paint.setColor(secondLineColor);
        canvas.drawCircle((float)((width / 9) * 5.5), height/9, ridus, paint);
    }
    private void drawFirstText(Canvas canvas)
    {
        paint.setColor(textColor);
        paint.setTextSize(bigTextSizeInPx);
        paint.setStrokeWidth(0);
        paint.setPathEffect(null);
        canvas.drawText(expand_text_id, width/9 * 3, height/9 + bigTextSizeInPx/3, paint);
    }
    private void drawSecondText(Canvas canvas)
    {
        paint.setColor(textColor);
        paint.setTextSize(bigTextSizeInPx);
        paint.setStrokeWidth(0);
        paint.setPathEffect(null);
        canvas.drawText(sysetolic_text_id, width/9 * 6, height/9 + bigTextSizeInPx/3, paint);
//        canvas.drawText("你好", 0, 0, paint);
    }


    private void drawCircle(Canvas canvas, float x, float y, int radis)
    {

        paint.setStrokeWidth(2);
        canvas.drawCircle(x, y, radis, paint);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y, radis - 1, paint);
        paint.setColor(firstLineColor);
        paint.setStyle(Paint.Style.STROKE);
    }
}
