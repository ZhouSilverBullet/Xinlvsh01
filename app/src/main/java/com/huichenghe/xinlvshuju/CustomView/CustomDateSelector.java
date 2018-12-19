package com.huichenghe.xinlvshuju.CustomView;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.DataSecletor.NumericWheelAdapter;
import com.huichenghe.xinlvshuju.Utils.DataSecletor.OnWheelScrollListener;
import com.huichenghe.xinlvshuju.Utils.DataSecletor.WheelView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by lixiaoning on 2016/5/10.
 */
public class CustomDateSelector
{
    private WheelView year, month, day;
    private Context mContext;
    private PopupWindow dateSelector;
    private OnDateChoose onDateChoose;

    public interface OnDateChoose
    {
        void choose(String dates);
    }

    public CustomDateSelector(Context context, String date, OnDateChoose dateChoose)
    {
        this.mContext = context;
        this.onDateChoose = dateChoose;
        showPopwindow(getDataPick(date));
    }


    /**
    /**
     * 显示日期选择器
     *
     * @param dataPick
     */
    private void showPopwindow(View dataPick)
    {
        WindowManager windowManager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        // 构建悬浮窗
        dateSelector = new PopupWindow(dataPick, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dateSelector.setOutsideTouchable(false);
        dateSelector.setAnimationStyle(R.style.mypopupwindow_anim_style);
        // 设置焦点
        dateSelector.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dateSelector.setFocusable(true);
        dateSelector.setBackgroundDrawable(new BitmapDrawable());
        dateSelector.showAtLocation(dataPick, Gravity.CENTER_HORIZONTAL, 0, 0);
        dateSelector.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss()
            {
                dateSelector = null;
            }
        });
    }

    private View getDataPick(String bir)
    {
        int curYears = 0;
        int curMons = 0;
        int curDays = 0;
        Calendar c = Calendar.getInstance(TimeZone.getDefault());     // 日期对象
        int curYear = c.get(Calendar.YEAR);
        int curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
        int curDate = c.get(Calendar.DATE);
        final View view = LayoutInflater.from(mContext).inflate(R.layout.datapick, null);

        year = (WheelView) view.findViewById(R.id.year);
        year.setAdapter(new NumericWheelAdapter(1900, curYear));// 参数一：最小年限。参数二：最大年限
        year.setLabel(mContext.getString(R.string.year));
        year.setCyclic(true);
        year.addScrollingListener(scrollListenerYear);
        month = (WheelView) view.findViewById(R.id.month);
//        initMonth(curYear, curMonth);
        month.setAdapter(new NumericWheelAdapter(1, 12, "%02d"));
        month.setLabel(mContext.getString(R.string.month));
        month.setCyclic(true);
        month.addScrollingListener(scrollListenerMonth);
        day = (WheelView) view.findViewById(R.id.day);
        initDay(curYear, curMonth);
        day.setLabel(mContext.getString(R.string.day_day));
        day.setCyclic(true);
        day.addScrollingListener(scrollListenerDay);

        if(bir != null && bir.equals(""))
        {
            year.setCurrentItem(curYear - 1900);
            month.setCurrentItem(curMonth - 1);
            day.setCurrentItem(curDate - 1);
        }
        else
        {
            curYears = Integer.parseInt(bir.substring(0, 4));
            curMons = Integer.parseInt(bir.substring(5, 7));
            curDays = Integer.parseInt(bir.substring(8, 10));
            year.setCurrentItem(curYears - 1900);
            month.setCurrentItem(curMons - 1);
            day.setCurrentItem(curDays - 1);
        }
        ImageView bt = (ImageView) view.findViewById(R.id.set);
        bt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                String mm = null;
                int months = month.getCurrentItem() + 1;
                if(months < 10)
                {
                    mm = "0" + months;
                }
                else
                {
                    mm = String.valueOf(months);
                }
                String dd = String.valueOf((day.getCurrentItem() + 1));
                if(Integer.parseInt(dd) < 10)
                {
                    dd = "0" + dd;
                }
                String chooseDate = ((year.getCurrentItem() + 1900) + "-" + mm + "-" + dd);
                chooseDate = checkTheDate(chooseDate);
                onDateChoose.choose(chooseDate);
//                Toast.makeText(EditPersionInfoActivity.this, str, Toast.LENGTH_LONG).show();
//                dataList.clear();
//                readDbAndshow(currenDate);		// 读全天数据
//                resetTheScreen();
//                updateThisScreen(currenDate);
//                todayActive.setText(compareTheDate(currenDate));
                dateSelector.dismiss();
            }
        });
        RelativeLayout cancel = (RelativeLayout) view.findViewById(R.id.cancle_select);
        cancel.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                dateSelector.dismiss();
            }
        });
        return view;
    }

    private String checkTheDate(String str)
    {
        if(str == null)return str;
        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
        Date selectDate = null;
        try {
            selectDate = formate.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long selectMi = selectDate.getTime();
        Calendar calend = Calendar.getInstance();
        long currentMi = calend.getTimeInMillis();
        if(selectMi > currentMi)
        {
            str = formate.format(calend.getTime());
        }
        return str;
    }
    private OnWheelScrollListener scrollListenerDay = new OnWheelScrollListener()
    {
        @Override
        public void onScrollingStarted(WheelView wheel){}
        @Override
        public void onScrollingFinished(WheelView wheel)
        {
            int n_year = year.getCurrentItem() + 1900;// 楠烇拷
            int n_month = month.getCurrentItem() + 1; // 閺堬拷
            int n_day = day.getCurrentItem() + 1;
//            initMonth(n_year, n_month);
//            month.setCurrentItem(0);
            initDay(n_year, n_month);
            compleTheDay(n_year, n_month, n_day);
        }
    };
    private void compleTheDay(int n_year, int n_month, int n_day)
    {
        Calendar ca = Calendar.getInstance(TimeZone.getDefault());
        int yearNow = ca.get(Calendar.YEAR);
        int monthNow = ca.get(Calendar.MONTH) + 1;
        int dayNow = ca.get(Calendar.DATE);
        if(n_year == yearNow)
        {
            if(n_month > monthNow)
            {
                month.setCurrentItem(monthNow - 1);
            }
            if(n_month == monthNow && n_day > dayNow)
            {
                day.setCurrentItem(dayNow - 1);
            }
        }
    }

    private OnWheelScrollListener scrollListenerMonth = new OnWheelScrollListener()
    {
        @Override
        public void onScrollingStarted(WheelView wheel){}
        @Override
        public void onScrollingFinished(WheelView wheel)
        {
            int n_year = year.getCurrentItem() + 1900;// 楠烇拷
            int n_month = month.getCurrentItem() + 1; // 閺堬拷
            int n_day = day.getCurrentItem() + 1;
            initDay(n_year, n_month);
            compleTheMonth(n_year, n_month, n_day);
        }
    };

    private void compleTheMonth(int n_year, int n_month, int n_day)
    {
        Calendar ca = Calendar.getInstance(TimeZone.getDefault());
        int yearNow = ca.get(Calendar.YEAR);
        int monthNow = ca.get(Calendar.MONTH) + 1;
        int dayNow = ca.get(Calendar.DATE);
        if(n_year == yearNow)
        {
            if(n_month > monthNow)
            {
                month.setCurrentItem(monthNow - 1);
            }
            if(n_month == monthNow && n_day > dayNow)
            {
                day.setCurrentItem(dayNow - 1);
            }
        }
    }

    private OnWheelScrollListener scrollListenerYear = new OnWheelScrollListener()
    {
        @Override
        public void onScrollingStarted(WheelView wheel){}
        @Override
        public void onScrollingFinished(WheelView wheel)
        {
            int n_year = year.getCurrentItem() + 1900;// 楠烇拷
            int n_month = month.getCurrentItem() + 1; // 閺堬拷
            int n_day = day.getCurrentItem() + 1;
            initDay(n_year, n_month);
            conmpareTheItem(n_year, n_month, n_day);
        }
    };
    private void initDay(int arg1, int arg2) {
        day.setAdapter(new NumericWheelAdapter(1, getDay(arg1, arg2), "%02d"));
    }
    /**
     * @param year
     * @param month
     * @return
     */
    private int getDay(int year, int month) {
        int day = 30;
        boolean flag = false;
        switch (year % 4) {
            case 0:
                flag = true;
                break;
            default:
                flag = false;
                break;
        }
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 2:
                day = flag ? 29 : 28;
                break;
            default:
                day = 30;
                break;
        }
        return day;
    }

    private void conmpareTheItem(int n_year, int n_month, int n_day)
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int yearNow = calendar.get(Calendar.YEAR);
        int monthNow = calendar.get(Calendar.MONTH) + 1;
        int dayNow = calendar.get(Calendar.DATE);
        if(yearNow == n_year)
        {
            if(n_month > monthNow)
            {
                month.setCurrentItem(monthNow - 1);
            }
            if(n_month == monthNow && n_day > dayNow)
            {
                day.setCurrentItem(dayNow - 1);
            }
        }
    }





}
