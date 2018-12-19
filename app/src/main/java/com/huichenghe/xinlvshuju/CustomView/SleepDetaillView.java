package com.huichenghe.xinlvshuju.CustomView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.huichenghe.xinlvshuju.CommonUtilss.CommonUtils;
import com.huichenghe.xinlvshuju.R;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * 展示睡眠数据的自定义ImageView
 * Created by lixiaoning on 15-12-sos.
 */
public class SleepDetaillView extends ImageView {
    public static final String TAG = SleepDetaillView.class.getSimpleName();

    private static final int textSizeInDp = 16;
    private float textHeightInPx;

    private ArrayList<StData> mAllData = new ArrayList<StData>();
    int mMinTime, mMaxTime;
    int indexCount = 0;
    private String beginTime = "";
    private String endTime = "";
    private String twoTime = "";
    private String threeTime = "";
    private Context mContext;


    private class StData {
        // 一个int数据代表十分钟的睡眠状态
        String sleepTimeBegin;
        private int[] sleepStatus;
    }

    public void setSleepData(String beginTimes, String endTimes, String sleepStatus) {
        mAllData.clear();
        this.beginTime = beginTimes;
        this.endTime = endTimes;
        if (beginTime != null && beginTime.equals("")) {
            return;
        }
        indexCount = 0;
        StData stData = new StData();
        stData.sleepTimeBegin = beginTime;
        sleepStatus = Replace(sleepStatus, "2332", "2112");
        sleepStatus = Replace(sleepStatus, "2002", "2112");
        sleepStatus = Replace(sleepStatus, "232", "212");
        sleepStatus = Replace(sleepStatus, "202", "212");
        stData.sleepStatus = new int[sleepStatus.length()];
        int length = sleepStatus.length();
        // 遍历String并将每一个String转化为int，同时赋值给int数组
        for (int i = 0; i < length; i++) {
            String sub = sleepStatus.substring(i, i + 1);
            stData.sleepStatus[i] = Integer.parseInt(sub);
//            Log.i(TAG, "睡眠数据：：：" + sub);
            indexCount++;
        }
        mAllData.add(stData);
        getTwoAndThreeTimes(beginTimes, endTimes);
    }

    private String Replace(String strReplaced, String oldStr, String newStr) {
        int pos = 0;
        int findPos;
        while ((findPos = strReplaced.indexOf(oldStr, pos)) != -1) {
            strReplaced = strReplaced.substring(0, findPos) + newStr + strReplaced.substring(findPos + oldStr.length());
            findPos += newStr.length();
        }
        return strReplaced;
    }

    private void getTwoAndThreeTimes(String beginTimes, String endTimes) {
        int beginT = getIntFromString(beginTimes);
        int endT = getIntFromString(endTimes);
        if (beginT >= 1320) {
            String[] tim = getTheTimes(endT, beginT);
            twoTime = tim[0];
            threeTime = tim[1];
        } else {
            if (endT - beginT >= 3) {
                String[] tim = getTheTimes(endT, beginT);
                twoTime = tim[0];
                threeTime = tim[1];
            } else {
                twoTime = "";
                threeTime = "";
            }
        }

    }

    private String[] getTheTimes(int endT, int beginT) {
        String[] centerTime = new String[2];
        if (beginT < 1320)       // 只有当天数据
        {
            int count = endT - beginT;
            float bus = count / 3.0f;
            float twoTimeF = beginT + bus;
            float threeTimeF = beginT + (bus * 2);
            twoTimeF = getHlftUp(twoTimeF);
            threeTimeF = getHlftUp(threeTimeF);
            centerTime[0] = formatTime(twoTimeF);
            centerTime[1] = formatTime(threeTimeF);
        } else                    // 跨天数据
        {
            float twoTimeF;
            float threetimeF;
            if (endT > beginT) {
                int count = endT - beginT;
                float bus = count / 3.0f;
                twoTimeF = beginT + bus;
                threetimeF = beginT + (bus * 2);
            } else {
                int count = endT + (1440 - beginT);
                float bus = count / 3.0f;
                twoTimeF = beginT + bus;
                threetimeF = beginT + (bus * 2);
                if (twoTimeF > 1440) {
                    twoTimeF = twoTimeF - 1440;
                }
                if (threetimeF > 1440) {
                    threetimeF = threetimeF - 1440;
                }
            }

            centerTime[0] = formatTime(getHlftUp(twoTimeF));
            centerTime[1] = formatTime(getHlftUp(threetimeF));
        }
        return centerTime;
    }

    private String formatTime(float twoTime) {
        int ho = (int) twoTime / 60;
        int mi = (int) twoTime % 60;
        String miString = null;
        if (mi == 0) {
            miString = "00";
        } else if (mi < 10) {
            miString = "0" + mi;
        } else {
            miString = String.valueOf(mi);
        }
        return ho + ":" + miString;
    }

    private float getHlftUp(float twoTime) {
        BigDecimal big = new BigDecimal(twoTime);
        big = big.setScale(0, BigDecimal.ROUND_HALF_UP);
        return big.floatValue();
    }

    private int getIntFromString(String beginTimes) {
        String[] times = beginTimes.split(":");
        return Integer.parseInt(times[0]) * 60 + Integer.parseInt(times[1]);
    }


    public SleepDetaillView(Context context) {
        super(context);
        this.mContext = context;
        textHeightInPx = CommonUtils.getTextViewWidth(context, "A", textSizeInDp);
    }

    public SleepDetaillView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        textHeightInPx = CommonUtils.getTextViewWidth(context, "A", textSizeInDp);
    }

    public SleepDetaillView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        textHeightInPx = CommonUtils.getTextViewWidth(context, "A", textSizeInDp);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        subDraw(canvas);
        super.onDraw(canvas);

    }

    private void subDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);   // 消除锯齿
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);
        paint.setTextSize(textHeightInPx);
        float width = getMeasuredWidth();       // 获取当前控件的宽度px
        float height = getMeasuredHeight();
        Bitmap sleep_buttom = BitmapFactory.decodeResource(getResources(), R.mipmap.sleep_buttom_icon);
        float iconWidth = sleep_buttom.getWidth();
        float iconHeight = sleep_buttom.getHeight();
        drawSleepRect(canvas, paint, width, height - textHeightInPx, iconHeight); // 画出睡眠状态
        drawBottomIcon(canvas, paint, width, height, sleep_buttom, iconHeight);
        if (beginTime != null && !beginTime.equals("")) {
            drawText(canvas, paint, height, width, iconHeight);             // 画出开始和结束时间
        }
    }

    private void drawBottomIcon(Canvas canvas, Paint paint, float width, float height, Bitmap sleep_bottom, float iconHeight) {
        RectF rec = new RectF();
        rec.left = 0;
        rec.top = height - iconHeight;
        rec.right = width;
        rec.bottom = height;
        canvas.drawBitmap(sleep_bottom, null, rec, paint);
    }


    /**
     * @param canvas
     * @param paint
     */
    private void drawText(Canvas canvas, Paint paint, float height, float width, float iconHeight) {
//        Log.i(TAG, "第二个时间：" + twoTime);
//        Log.i(TAG, "第三个时间：" + threeTime);
        paint.setTextSize(textHeightInPx);
        paint.setColor(Color.WHITE);
        canvas.drawText(beginTime, 1, height - iconHeight / 11 * 2, paint);
        float aa = CommonUtils.getTextViewWidth(mContext, endTime, textSizeInDp);
        canvas.drawText(endTime, width - aa, height - iconHeight / 11 * 2, paint);
        if (twoTime != null && !twoTime.equals(""))
            canvas.drawText(twoTime, width / 3, height - iconHeight / 11 * 2, paint);
        if (threeTime != null && !threeTime.equals(""))
            canvas.drawText(threeTime, width / 3 * 2, height - iconHeight / 11 * 2, paint);
    }


    // 此方法用于画出睡眠状态
    private void drawSleepRect(Canvas canvas, Paint paint, float width, float height, float iconHeight) {
        int allTime = indexCount;
//        allTime /= 60;

        float eachDistanceW = (width - (width / 105 * 8)) / allTime;  // 计算每个像素显示多少数据
        float deepSleepH = height;

        float ws = 0;
        float step_x;
        float step_y = height - iconHeight / 11 * 4;
        float x = 0;
        int drawType = 8;
        boolean continueD = false;
        for (int i = 0; i < mAllData.size(); i++) {
            for (int j = 0; j < mAllData.get(i).sleepStatus.length; j++) {
                step_x = eachDistanceW * ws;
                step_x += width / 105 * 4;
                ws++;
                paint.setColor(getResources().getColor(R.color.start_sleep_color_background));
                // 一个数字代表十分钟的睡眠状态
                if (mAllData.get(i).sleepStatus[j] == 0 || mAllData.get(i).sleepStatus[j] == 3) // 代表活动
                {
                    if (drawType == 3) {
                        continueD = true;
                    }
                    drawType = 3;
                    x = height / 4 * 3;
                    canvas.drawRect(step_x, x, step_x + eachDistanceW, step_y, paint);
                    paint.setColor(getResources().getColor(R.color.sober));
                } else if (mAllData.get(i).sleepStatus[j] == 1)    // 代表浅睡
                {
                    if (drawType == 1) {
                        continueD = true;
                    }
                    drawType = 1;
                    x = height / 4 * 2;
                    canvas.drawRect(step_x, x, step_x + eachDistanceW, step_y, paint);
                    paint.setColor(getResources().getColor(R.color.light_sleep));
                } else if (mAllData.get(i).sleepStatus[j] == 2)    // 代表深睡
                {
                    if (drawType == 2) {
                        continueD = true;
                    }
                    drawType = 2;
                    x = height / 4 * 1;
                    canvas.drawRect(step_x, x, step_x + eachDistanceW, step_y, paint);
                    paint.setColor(getResources().getColor(R.color.deep_sleep));
                }
                if (continueD) {
                    canvas.drawRect(step_x - 2, x + 1, step_x + eachDistanceW - 1, step_y, paint);
                    continueD = false;
                } else {
                    canvas.drawRect(step_x + 1, x + 1, step_x + eachDistanceW - 1, step_y, paint);
                }

            }
        }
    }
}
