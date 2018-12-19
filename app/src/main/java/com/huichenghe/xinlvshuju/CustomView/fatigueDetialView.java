package com.huichenghe.xinlvshuju.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.huichenghe.xinlvshuju.CommonUtilss.CommonUtils;
import com.huichenghe.xinlvshuju.DensityUtils;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.expand_activity.MovementDetail.fatigueHelper;

/**
 * Created by lixiaoning on 2016/5/11.
 */
public class fatigueDetialView extends ImageView
{
    private final String sdnn = "SDNN";
    private int largeSizeInDB = 24;
    private int textSizeInDb = 14;
    private int smallTextSizeInDb = 12;
    private float smallTextSizeInPx = 0;
    private float textSizeInPx = 0;
    private float largeSizeInPx = 0;
    private byte[] fatigueData = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
//    private byte[] fatigueData;
    private int maxH = 100;
    private Context mContext;
    int[] drawColors = {getResources().getColor(R.color.fatigue_one),
                          getResources().getColor(R.color.fatigue_two)};

    private String[] buttomX = {"0:00", "3:00", "6:00", "9:00", "12:00", "15:00", "18:00", "21:00", "23:00"};
    public fatigueDetialView(Context context) {
        super(context);
        inti(context);
    }
    public fatigueDetialView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inti(context);
    }
    public fatigueDetialView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inti(context);
    }

    private void inti(Context mContext)
    {
        this.mContext = mContext;
        textSizeInPx = CommonUtils.getTextViewWidth(mContext, "A", textSizeInDb);
        smallTextSizeInPx = CommonUtils.getTextViewWidth(mContext, "A", smallTextSizeInDb);
        largeSizeInPx = CommonUtils.getTextViewWidth(mContext, "A", largeSizeInDB);
    }

    public void setFatigueData(byte[] data)
    {
        fatigueData = data;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        subOnDraw(canvas);
    }

    private void subOnDraw(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        float width = getMeasuredWidth();
        float height = getMeasuredHeight();

        drawYAxis(paint, canvas, width, height);
        drawXAxis(paint, canvas, width, height);
        drawFatigueColor(paint, canvas, width, height);
//        drawSDNN(paint, canvas, width, height);
        drawFatigueLine(paint, canvas, width, height);
    }

    private void drawSDNN(Paint paint, Canvas canvas, float width, float height)
    {
        paint.setColor(Color.BLACK);
        paint.setTextSize(largeSizeInPx);
        float textWidth = CommonUtils.getTextViewWidth(mContext, sdnn, largeSizeInDB);
        canvas.drawText("SDNN", width - textWidth,  40, paint);
    }

    /**
     * 画X轴坐标
     * @param paint
     * @param canvas
     * @param width
     * @param height
     */
    private void drawXAxis(Paint paint, Canvas canvas, float width, float height)
    {
        paint.setColor(Color.GRAY);
        paint.setTextSize(smallTextSizeInPx);
        float eachX = width / (buttomX.length - 1);
        float Y = height - 6;
        for (int i = 0; i < buttomX.length; i ++)
        {
            if(i > 0 && i <= 7)
            {
                float textWidth = CommonUtils.getTextViewWidth(mContext, buttomX[i], smallTextSizeInDb);
                canvas.drawText(buttomX[i], (eachX * i) - (textWidth/2), Y, paint);
            }
            else if(i == 0)
            {
                canvas.drawText(buttomX[i], eachX * i, Y, paint);
            }
            else
            {
                float textWidth = CommonUtils.getTextViewWidth(mContext, buttomX[i], smallTextSizeInDb);
                canvas.drawText(buttomX[i], (eachX * i - textWidth + 10), Y, paint);
            }
        }
    }


    /**
     * 画疲劳曲线
     * @param paint
     * @param canvas
     * @param width
     * @param height
     */
    private void drawFatigueLine(Paint paint, Canvas canvas, float width, float height)
    {
        height = height - textSizeInPx * 2 - 6;
        PathEffect effect;
        if(fatigueData != null && fatigueData.length > 0)
        {
            float eachWidth = width / 23;
            float eachHeight = height / (maxH - 1);
            paint.setColor(Color.RED);
            paint.setStrokeWidth(DensityUtils.getLineWidth(getContext()));
            paint.setStyle(Paint.Style.STROKE);
            Path path = new Path();
            float startY;
            if(fatigueData[0] <= 0 || fatigueData[0] > 100)
            {
                startY = height - 50 * eachHeight;
            }
            else
            {
                startY = height - eachHeight * fatigueData[0];
            }
            path.moveTo(0, startY);
            for (int i = 0; i < fatigueData.length; i++)
            {
//                Log.i("", "疲劳值" + fatigueData[i]);
                if(fatigueData[i] > 100)
                    fatigueData[i] = 100;
                if(fatigueData[i] > 0)
                {
                    path.lineTo(eachWidth * i,  height -(fatigueData[i] * eachHeight));
                }
            }
            effect = new CornerPathEffect(10);
            canvas.translate(0, textSizeInPx);     // 以0，0点为原点
            paint.setPathEffect(effect);
            canvas.drawPath(path, paint);
//            canvas.translate(0, 70);
        }
        else
        {
            return;
        }
    }

    // 画y轴标示
    private void drawYAxis(Paint paint, Canvas canvas, float width, float height)
    {
        paint.setTextSize(textSizeInPx);
        paint.setColor(Color.GRAY);
        height = height - textSizeInPx * 2 - 6;
        for (int i = 100; i > 0; i-= 10)
        {
            canvas.drawText(String.valueOf(i), 10, (height / 100 * (100 - i)) + textSizeInPx + textSizeInPx / 3, paint);
        }
    }

    // 画色块
    private void drawFatigueColor(Paint paint, Canvas canvas, float width, float height)
    {
        fatigueHelper fatigueGetter = new fatigueHelper();
        int[] threeLineData = fatigueGetter.getFatigueClass(fatigueGetter.getUserAger(getContext()));
        PathEffect pathEffectDash = new DashPathEffect(new float[]{10f, 3f}, 0);
        Path path2 = new Path();
        drawDashline(paint, canvas, width, height, Color.GREEN, threeLineData[0], pathEffectDash, path2);
        drawDashline(paint, canvas, width, height, Color.BLUE, threeLineData[1], pathEffectDash, path2);
        drawDashline(paint, canvas, width, height, Color.RED, threeLineData[2], pathEffectDash, path2);
        String text = getContext().getResources().getString(R.string.health_note);
        String textSecond = getResources().getString(R.string.zhengchang);
        String textThree = getResources().getString(R.string.danger_note);
        String textFour = getResources().getString(R.string.danger_more);
        drawNoteText(canvas, paint, width, height, text, threeLineData[0], text);
        drawNoteText(canvas, paint, width, height, textSecond, threeLineData[1], text);
        drawNoteText(canvas, paint, width, height, textThree, threeLineData[2], text);
        drawNoteText(canvas, paint, width, height, textFour, threeLineData[2] - (int)smallTextSizeInPx/2, text);


//        path2.moveTo(widthLeft, subHeight - (eachHeight * threeLineData[1]));
//        path2.lineTo(widthLeft, subHeight - (eachHeight * threeLineData[1]));
//        path2.lineTo(width, subHeight - (eachHeight * threeLineData[1]));
//        canvas.drawPath(path2, paint);
//        Bitmap dotted = BitmapFactory.decodeResource(getResources(), R.mipmap.xuxian);
////        paint.setColor(drawColors[0]);
////        canvas.drawRect(0, smallTextSizeInPx, width, height/2, paint);
//        RectF rectFOne = new RectF(widthLeft, smallTextSizeInPx, width, smallTextSizeInPx + 2);
//        canvas.drawBitmap(dotted, null, rectFOne, paint);
//        RectF rectFTwo = new RectF(widthLeft, (height - smallTextSizeInPx) / 2, width, (height - smallTextSizeInPx) / 2 + 2);
//        canvas.drawBitmap(dotted, null, rectFTwo, paint);
////        paint.setColor(drawColors[1]);
////        canvas.drawRect(0, height/2, width, height, paint);
//        paint.setColor(getResources().getColor(R.color.black_color_transparent));
//        paint.setTextSize(textSizeInPx);
//        canvas.drawText(getResources().getString(R.string.Active_zone), 120, height/4, paint);
//        paint.setColor(getResources().getColor(R.color.black_color_transparent));
//        canvas.drawText(getResources().getString(R.string.fatigue_zone), 120, height/4 * 3, paint);
    }

    private void drawNoteText(Canvas canvas, Paint paint, float width, float height, String text, int textHeight, String texts)
    {
        paint.setTextSize(textSizeInPx);
        paint.setStrokeWidth(0);
        float widthRight = CommonUtils.getTextViewWidth(getContext(), texts, smallTextSizeInPx);
        float subHeight = height - smallTextSizeInPx;
        float eachHeight = subHeight / 100;
        paint.setColor(Color.BLACK);
        float y = subHeight - (eachHeight * textHeight) - smallTextSizeInPx/2;
        canvas.drawText(text, width - (widthRight + 20), (y > height) ? height + textHeight/3 : y, paint);
    }

    private void drawDashline(Paint paint, Canvas canvas, float width, float height, float color, int line, PathEffect pathEffectDash, Path path2)
    {
        int widthLeft = (int)CommonUtils.getTextViewWidth(mContext, "100", smallTextSizeInDb);
        float subHeight = height - smallTextSizeInPx;
        float eachHeight = subHeight / 100;
        path2.moveTo(widthLeft, subHeight - (eachHeight * line));
        path2.lineTo(widthLeft, subHeight - (eachHeight * line));
        path2.lineTo(width, subHeight - (eachHeight * line));
        paint.setColor((int)color);
        paint.setPathEffect(pathEffectDash);
        paint.setStrokeWidth(DensityUtils.getLineWidth(getContext()));
        canvas.translate(0, 0);
        canvas.drawPath(path2, paint);
        path2.reset();
    }
}
