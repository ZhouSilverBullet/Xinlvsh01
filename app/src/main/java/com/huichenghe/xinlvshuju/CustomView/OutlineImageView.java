package com.huichenghe.xinlvshuju.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.DpUitls;

/**
 * 自定义离线数据柱状图
 * Created by lixiaoning on 15-11-13.
 */
public class OutlineImageView extends ImageView
{

    private Context mContext;
    private static final String TAG = OutlineImageView.class.getSimpleName();

    public static final float MAX_BK_ZHU_TU = 1.05f;

    // 柱状图的颜色
    final int ZhuColor = 0x50FEE3;

    // 柱状图上显示的字体
    private static final int smallDp = 10;

    private int DEF_ZHU = 13;
    private float maxData = 1;


    private float mTextPx;  // dp转化为px后储存在此变量里


        String mTime = "";    // 数据的时间
        int dataCount = 0;  // 步数
        int colors = R.color.White_forme;



    public OutlineImageView(Context context)
    {
        super(context);
        this.mContext = context;
        mTextPx = DpUitls.DpToPx(context, smallDp);     // DP转化为px
    }
    public OutlineImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.mContext = context;
        mTextPx = DpUitls.DpToPx(context, smallDp);     // DP转化为px
    }
    public OutlineImageView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        this.mContext =context;
        mTextPx = DpUitls.DpToPx(context, smallDp);     // DP转化为px
    }

    /**
     * 添加数据
     * @param time  // 时间
     * @param count // 数据
     */
    public void addData(String time, int count, int maxS, int colorData)
    {
//        item = new MovementData();  // 创建数据结构对象
        dataCount = count;
        mTime = time;
        colors = colorData;

        maxData = maxS;
        maxData = maxS * MAX_BK_ZHU_TU;
        Log.i("", "数据2：" + dataCount + maxData + time);
    }

    /**
     * 判断是否有数据
     * @return
     */
    private boolean isHaveData()
    {
        if(dataCount != 0)
        {
            return true;
        }
        return false;
    }


    /**
     * 更新数据显示
     */
    public void MyInvalidate()
    {
        Log.i("", "柱状图更新invalidate ");
        if(isHaveData())            // 若有数据
        {
            Log.i("", "柱状图更新有数据");
            super.postInvalidate(); // 调用父类函数更新显示
        }
    }


    @Override
    protected void onDraw(Canvas canvas)
    {

        if(isHaveData())    // 判断是否有数据
        {
            subOnDrow(canvas); // 有则画图形
        }
        super.onDraw(canvas);
    }

    /**
     * 画出图形
     * @param mCanvas
     */
    private void subOnDrow(Canvas mCanvas)
    {
        Paint mPaint = new Paint(); // 画笔对象
        mPaint.setAntiAlias(true);  // 消除锯齿
        mPaint.setStyle(Paint.Style.FILL);// 设置为实体
        mPaint.setStrokeWidth(1);         // 设置边的宽度
        mPaint.setColor(getResources().getColor(colors));        // 设置柱体的颜色

        float width = getMeasuredWidth();    // 获取此view的原始宽度，单位px
        float height = getMeasuredHeight();  // 获取此view的原始高度，单位px

        float eachCompany = (width - mTextPx) / maxData; // 每个数据单位所占的像素值
        float top = 0;
        // 参数相当于padding
        RectF mRectF = new RectF(10, top, dataCount * eachCompany, height - 10);
        Log.i("", "更新的步数：" + dataCount);

        mCanvas.drawRect(mRectF, mPaint);      // 画柱状图


        /**
         * 画柱状图上的字体,内容为步数
         */
//        String showSetp = "" + dataCount;
//        Log.i("", "控件的宽度：" + width + "控件的高度：" + height + "步数：" + showSetp);
//        float showStepLength = getTextViewWidth(mContext, showSetp, smallDp);
//        float a = showStepLength / phone;
//        float b = width / phone;
//        float c = 0;
//        if(b > a)
//        {
//            c = b - a;
//        }
//
////        float step_x = width - showStepLength;
//        float step_x = c;
//        float step_y = height - (dataCount * eachCompany) - mTextPx/phone;
//        mPaint.setColor(getResources().getColor(R.color.histogram_color));
//        mPaint.setTextSize(mTextPx);
//        mCanvas.drawText(showSetp, step_x, step_y, mPaint);         // 画柱状图上的步数
//        Log.i("", "控件的宽度：" + width + "控件的高度：" + height);
//
//
//        /**
//         * 画出柱状图下的字体，内容是时间
//         */
//        String showTime = mTime;
//        float showTimeLength = getTextViewWidth(mContext, showTime, smallDp);
//
//        mPaint.setColor(Color.WHITE);
//        mCanvas.drawText(showTime, 0, height, mPaint);






//        Paint mPaint = new Paint();
//        int centre = getWidth() / phone; // 圆心的x坐标
//        int radius = (centre - 16) - brocast;     // 设置半径
//
//        mPaint.setStrokeWidth(26);
//        mPaint.setColor(Color.BLUE);
//        // 矩形对象，用来定义圆弧的边界
//        RectF mRectF = new RectF(centre - radius, centre - radius, centre + radius, centre + radius);
//        mPaint.setStyle(Paint.Style.FILL);    // 设置为空心
//        // 参数一：边界，参数二：起点角度，参数三：重点角度，参数四：代表圆弧的起点和终点闭合，参数五：画笔
////        canvas.drawArc(mRectF, 0, mProgress, true, mPaint);
//        mCanvas.drawRect(mRectF, mPaint);





    }


    /**
     * 获取指定字符串的宽度
     * @param mContext
     * @param textString
     * @param textDp
     * @return
     */
    private float getTextViewWidth(Context mContext, String textString, float textDp)
    {
        if((textString == null ) || textString.equals(""))  // 如果没有内容，返回0.0f
        {
            return 0.0f;
        }
        TextView text = new TextView(mContext);
        TextPaint mTextPaint = text.getPaint();
        mTextPaint.setTextSize(DpUitls.DpToPx(mContext, textDp));
        float textLength = mTextPaint.measureText(textString);
        return  textLength;

    }






}
