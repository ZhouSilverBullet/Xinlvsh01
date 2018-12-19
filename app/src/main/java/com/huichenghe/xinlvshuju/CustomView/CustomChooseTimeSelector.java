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

import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.DataSecletor.NumericWheelAdapter;
import com.huichenghe.xinlvshuju.Utils.DataSecletor.WheelView;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by lixiaoning on 2016/5/16.
 */
public class CustomChooseTimeSelector
{
    private OnTimesChoose choose;
    private Context context;
    private String currentTime;
    private PopupWindow timeWindow;
    public CustomChooseTimeSelector(Context mContext, String time, OnTimesChoose onTimesChoose)
    {
        this.choose = onTimesChoose;
        this.context = mContext;
        this.currentTime = time;
        showPopWindow(getTimePick(time));
    }

    /**
     * 在popwindow里显示指定的view
     * @param timePick
     */
    private void showPopWindow(View timePick)
    {
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        timeWindow = new PopupWindow(
                timePick, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        timeWindow.setFocusable(true);
        timeWindow.setAnimationStyle(R.style.mypopupwindow_anim_style);
        timeWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        timeWindow.setBackgroundDrawable(new BitmapDrawable());
        timeWindow.showAtLocation(timePick, Gravity.CENTER_HORIZONTAL, 0, 0);
        timeWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss()
            {
                timeWindow = null;
            }
        });



    }

    /**
     * 时间选择器布局
     * @return
     */
    private View getTimePick(String time)
    {

        View view = LayoutInflater.from(context).inflate(R.layout.timepick, null);
        final WheelView hour = (WheelView)view.findViewById(R.id.hour);
        hour.setAdapter(new NumericWheelAdapter(0, 23, "%02d"));
        hour.setLabel(context.getString(R.string.hour_wheel));
        hour.setCyclic(true);
        final WheelView minute = (WheelView)view.findViewById(R.id.mins);
        minute.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        minute.setLabel(context.getString(R.string.minute_wheel));
        minute.setCyclic(true);

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int hourCurrent = 0;
        int moniCurrent = 0;
        if(time != null && time.equals(""))
        {
            hourCurrent = calendar.get(Calendar.HOUR_OF_DAY);
            moniCurrent = calendar.get(Calendar.MINUTE);
        }
        else
        {
            String[] times = time.split(":");
            hourCurrent = Integer.parseInt(times[0]);
            moniCurrent = Integer.parseInt(times[1]);
        }
        hour.setCurrentItem(hourCurrent);
        minute.setCurrentItem(moniCurrent);
        final LinearLayout background = (LinearLayout)view.findViewById(R.id.time_pick_backgournd);
        final ImageView bt = (ImageView)view.findViewById(R.id.set);
        NoDoubleClickListener innerListener = new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v)
            {
                if(v == bt)
                {
                    String h = String.valueOf(hour.getCurrentItem());
                    String m = String.valueOf(minute.getCurrentItem());
                    if (h.length() == 1) {
                        h = "0" + h;
                    }
                    if (m.length() == 1) {
                        m = "0" + m;
                    }
                    String str = h + ":" + m;
//                timeList.add(str);
                    choose.onTimeChoose(str);
                    timeWindow.dismiss();
                }
                else if(v == background)
                {
                    if(timeWindow.isShowing())
                    timeWindow.dismiss();
                }

            }
        };
        background.setOnClickListener(innerListener);
        bt.setOnClickListener(innerListener);
        return view;


    }


    public interface OnTimesChoose
    {
        void onTimeChoose(String time);
    }
}
