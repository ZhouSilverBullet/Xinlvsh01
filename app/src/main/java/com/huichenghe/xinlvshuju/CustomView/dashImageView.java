package com.huichenghe.xinlvshuju.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.huichenghe.xinlvshuju.R;

/**
 * Created by lixiaoning on 15-12-31.
 */
public class dashImageView extends View
{
    private Paint paint = null;
    private Path path = null;
    private DashPathEffect effect = null;
    public dashImageView(Context context)
    {
        this(context, null);
    }

    public dashImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.dashImageView);

        int lineColor = a.getColor(R.styleable.dashImageView_lineColor, getResources().getColor(R.color.black_color_transparent));
        a.recycle();
        this.paint = new Paint();
        this.path = new Path();
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setColor(lineColor);
        this.paint.setAntiAlias(true);
        this.paint.setStrokeWidth(dip2px(context, 2.0f));
        float[] arrayOfFloat = new float[4];
        arrayOfFloat[0] = dip2px(context, 2.0f);
        arrayOfFloat[1] = dip2px(context, 2.0f);
        arrayOfFloat[2] = dip2px(context, 2.0f);
        arrayOfFloat[3] = dip2px(context, 2.0f);

        this.effect = new DashPathEffect(arrayOfFloat, dip2px(context, 1.0f));
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        this.path.moveTo(0.0f, 0.0f);
        this.path.lineTo(getMeasuredWidth(), 0.0f);
        this.paint.setPathEffect(this.effect);
        canvas.drawPath(this.path, this.paint);






//        Paint paint = new Paint();
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setColor(getResources().getColor(R.color.black_color_transparent));
//        Path path = new Path();
//        path.moveTo(0, 10);
//        path.lineTo(480, 10);
//
//        PathEffect effect = new DashPathEffect(new float[]{heartWarning,heartWarning,heartWarning,heartWarning,heartWarning,heartWarning,heartWarning}, clock);
//        paint.setPathEffect(effect);
//        canvas.drawPath(path, paint);




    }



    private int dip2px(Context mContext, float dpValue)
    {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }
}
