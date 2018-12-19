package com.huichenghe.xinlvshuju.CustomView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.huichenghe.xinlvshuju.CommonUtilss.CommonUtils;
import com.huichenghe.xinlvshuju.R;

/**
 * Created by lixiaoning on 2016/5/7.
 */
public class CustomRuler extends ImageView
{
    private Context mContext;
    private int textSize = 24;
    private float size;
    private int mot = 5;
    private float lastWidth;
    private int persionId = R.mipmap.persion_one;
    private int porinterID = 1;
    private float eachHeight;
    private String[] textContent = {getResources().getString(R.string.thin),
                                      getResources().getString(R.string.zhengchang),
                                      getResources().getString(R.string.over_weight),
                                      getResources().getString(R.string.Obesity)};
    public CustomRuler(Context context) {
        super(context);
        this.mContext = context;
        inti();
    }
    public CustomRuler(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.mContext = context;
        inti();
    }
    public CustomRuler(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        inti();
    }
    private void inti()
    {
        size = CommonUtils.getTextViewWidth(mContext, "A", textSize);
        lastWidth = CommonUtils.getTextViewWidth(mContext, textContent[0], textSize);
    }
    @Override
    protected void onDraw(Canvas canvas)
    {
        sub_onDraw(canvas);
        super.onDraw(canvas);
    }
    private void sub_onDraw(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(mContext.getResources().getColor(R.color.grey_color_dark));
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(size);
        float height = getMeasuredHeight();
        float width = getMeasuredWidth();
        eachHeight = height / mot;

        drawImageView(canvas, paint, height, width);
        // 画左边字体
        for (int i = 0; i < textContent.length; i++)
        {
            canvas.drawText(textContent[i], width / 8 * 5, eachHeight * (i + 1), paint);
        }
        // 画右侧部分
        drawRight(paint, canvas, height, width);
        drawPointer(paint, canvas, height, width);
    }

    /**
     * 画标识点
     * @param paint
     * @param canvas
     * @param height
     * @param width
     */
    private void drawPointer(Paint paint, Canvas canvas, float height, float width)
    {
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.about);
        float left = width / 8 * 7 - 13;
        float top = eachHeight * porinterID - (size /4 * 3);
        canvas.drawBitmap(b, left, top, paint);
    }

    /**
     * 画小人
     * @param canvas
     * @param paint
     * @param height
     * @param width
     */
    private void drawImageView(Canvas canvas, Paint paint, float height, float width)
    {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), persionId);
        canvas.drawBitmap(bitmap, width / 5, 32, paint);
    }


    /**
     * 画右侧竖线
     * @param paint
     * @param canvas
     * @param height
     * @param width
     */
    private void drawRight(Paint paint, Canvas canvas, float height, float width)
    {
        paint.setColor(getResources().getColor(R.color.grey_color_dark));
        paint.setStrokeWidth(8.0f);
        paint.setStrokeMiter(3.0f);
        float startX = width / 8 * 7;
        float startY = 8;
        float stopX = width / 8 * 7;
        float stopY = height - 8;
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }


    public void setImageViewPersion(int id)
    {
        persionId = id;
    }
    public void setProgressPointer(int pointers)
    {
        porinterID = pointers;
    }




}
