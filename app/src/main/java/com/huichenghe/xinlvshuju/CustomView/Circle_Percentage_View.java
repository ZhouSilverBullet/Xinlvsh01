package com.huichenghe.xinlvshuju.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.huichenghe.xinlvshuju.R;

import java.util.ArrayList;

/**
 * Created by lixiaoning on 2016/10/10.
 */
public class Circle_Percentage_View extends ImageView
{
    private int oneColor, twoColor, threeColor, fourColor, fiveColor, backgroundColor;
    private float circleWidth;
    private float oneEach, twoEach, threeEach, fourEach, fiveEach;
    private Paint paint;

    public Circle_Percentage_View(Context context)
    {
        super(context);
    }

    public Circle_Percentage_View(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initView(context, attrs);
    }

    public Circle_Percentage_View(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs)
    {
        paint = new Paint();
        TypedArray ty = context.obtainStyledAttributes(attrs, R.styleable.Circle_Percentage_View);
        oneColor = ty.getColor(R.styleable.Circle_Percentage_View_one_color, getResources().getColor(R.color.circle_one_color));
        twoColor = ty.getColor(R.styleable.Circle_Percentage_View_two_color, getResources().getColor(R.color.circle_two_color));
        threeColor = ty.getColor(R.styleable.Circle_Percentage_View_three_color, getResources().getColor(R.color.circle_three_color));
        fourColor = ty.getColor(R.styleable.Circle_Percentage_View_four_color, getResources().getColor(R.color.circle_four_color));
        fiveColor = ty.getColor(R.styleable.Circle_Percentage_View_five_color, getResources().getColor(R.color.circle_five_color));
        backgroundColor = ty.getColor(R.styleable.Circle_Percentage_View_hisgram_background, getResources().getColor(R.color.gray_color));
        circleWidth = ty.getDimension(R.styleable.Circle_Percentage_View_hisgram_width, 16);
        ty.recycle();
    }

    public void setEachDegree(float oneEach, float twoEach, float threeEach, float fourEach, float fiveEach)
    {
        this.oneEach = oneEach;
        this.twoEach = twoEach;
        this.threeEach = threeEach;
        this.fourEach = fourEach;
        this.fiveEach = fiveEach;
        postInvalidate();
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
        int heigth = getMeasuredHeight();
        paint.setAntiAlias(true);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        drawBackgroundCircle(canvas, width, heigth);
        drawEachCircleDegree(canvas, width, heigth);
    }

    private void drawEachCircleDegree(Canvas canvas, int width, int heigth)
    {
        canvas.rotate(-90, width/2, heigth/2);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.BEVEL);
        paint.setStrokeCap(Paint.Cap.ROUND);
        RectF ref = new RectF();
        ref.left = 0 + circleWidth;
        ref.top = 0 + circleWidth;
        ref.right = width - circleWidth;
        ref.bottom = heigth - circleWidth;
        // 第一个角度是开始位置，第二个是旋转的度数

//        paint.setColor(fiveColor);
//        canvas.drawArc(ref, 270, 90, false, paint);
//        paint.setColor(fourColor);
//        canvas.drawArc(ref, 180, 90, false, paint);
//        paint.setColor(threeColor);
//        canvas.drawArc(ref, 90, 90, false, paint);
//        paint.setColor(twoColor);
//        canvas.drawArc(ref, 60, 30, false, paint);
//        paint.setColor(oneColor);
//        canvas.drawArc(ref, 0, 60, false, paint);


        int poColor = getPointerColor();

        paint.setColor(fiveColor);
        canvas.drawArc(ref, (oneEach + twoEach + threeEach + fourEach) * 360, fiveEach * 360, false, paint);
        paint.setColor(fourColor);
        canvas.drawArc(ref, (oneEach + twoEach + threeEach) * 360, fourEach * 360, false, paint);
        paint.setColor(threeColor);
        canvas.drawArc(ref, (oneEach + twoEach) * 360, threeEach * 360, false, paint);
        paint.setColor(twoColor);
        canvas.drawArc(ref, oneEach * 360, twoEach * 360, false, paint);
        paint.setColor(oneColor);
        canvas.drawArc(ref, 0, oneEach * 360, false, paint);
        drawCirclePointer(canvas, width, heigth, poColor);
    }

    private int getPointerColor()
    {
        ArrayList<Float> de = new ArrayList<>();
        ArrayList<Integer> deColor = new ArrayList<>();
        de.add(fiveEach);
        de.add(fourEach);
        de.add(threeEach);
        de.add(twoEach);
        de.add(oneEach);
        deColor.add(fiveColor);
        deColor.add(fourColor);
        deColor.add(threeColor);
        deColor.add(twoColor);
        deColor.add(oneColor);
        int color = deColor.get(4);
        for (int i = 0; i < de.size(); i++)
        {
            if(de.get(i) > 0)
            {
                color = deColor.get(i);
                break;
            }
        }
        return color;
    }

    private void drawCirclePointer(Canvas canvas, int width, int heigth, int colors)
    {
        paint.setColor(colors);
        paint.setStyle(Paint.Style.FILL);
        canvas.rotate(90, width/2, heigth/2);
        canvas.drawCircle(width/2 - circleWidth/4, circleWidth, circleWidth/2, paint);
    }

    private void drawBackgroundCircle(Canvas canvas, int width, int heigth)
    {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(circleWidth);
        paint.setColor(backgroundColor);
        RectF rec = new RectF();
        rec.left = 0 + circleWidth;
        rec.top = 0 + circleWidth;
        rec.right = width - circleWidth;
        rec.bottom = heigth - circleWidth;
        canvas.drawArc(rec, 0, 360, false, paint);
    }
}
