package com.huichenghe.xinlvshuju.expand_activity.Treads;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.huichenghe.xinlvshuju.Adapter.MyViewPagerAdapter;
import com.huichenghe.xinlvshuju.BleDeal.BleSleepDataDeal;
import com.huichenghe.xinlvshuju.BleDeal.DayDataDealer;
import com.huichenghe.xinlvshuju.CustomView.CircleProgressDialog;
import com.huichenghe.xinlvshuju.DataEntites.stepAndCalorieEntity;
import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.UpdataService.DeviceTypeUtils;
import com.huichenghe.xinlvshuju.UpdataService.SendDataCallback;
import com.huichenghe.xinlvshuju.UpdataService.UpdateHistoryDataService;
import com.huichenghe.xinlvshuju.Utils.Histogram_sleep_entity;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;
import com.huichenghe.xinlvshuju.Utils.NetStatus;
import com.huichenghe.xinlvshuju.Utils.ShotScreenForShare;
import com.huichenghe.xinlvshuju.expand_activity.ShareActivity;
import com.huichenghe.xinlvshuju.http.UserAccountUtil;
import com.huichenghe.xinlvshuju.mainpack.BaseActivity;
import com.huichenghe.xinlvshuju.mainpack.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

/**
 */
public class TrendActivity extends BaseActivity
        implements TrendMonthFragment.OnFragmentInteractionListener,
                   TrendWeekFragment.OnFragmentInteractionListener
{
    public static final String TAG = TrendActivity.class.getSimpleName();
    private ArrayList<Fragment> fragmentList;   // fragment集合
    private TrendWeekFragment weekFragment;     // 周趋势fragment
    private TrendMonthFragment monthFragment;   // 月趋势fragment
    private MyViewPagerAdapter myFragmentAdapter;// 适配器
    private ViewPager mViewPager;
    private RadioButton buttonOne;
    private RadioButton buttonTwo;
    private ArrayList<stepAndCalorieEntity> dataListHistogram;
    private ArrayList<stepAndCalorieEntity> dataListMonth;
    private ArrayList<Histogram_sleep_entity> dataListSleepWeek;
    private ArrayList<Histogram_sleep_entity> dataListSleepMonth;
    private int[] weekData = new int[6];
    private int[] monthData = new int[6];
    private Handler treadHandler = new Handler(Looper.myLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trend);
//        int cpu = Runtime.getRuntime().availableProcessors() * info + phone;
//        Log.i(TAG, "手机信息:" + cpu);
//        settingTheStatebarAndNavigationbar();
        devideThread();
    }

    private void devideThread()
    {
//        MainActivity.threadService.execute(new initTask(0));
//        MainActivity.threadService.execute(new initTask(1));
//        MainActivity.threadService.execute(new initTask(2));
        initView();
        intiOther();
        initPager();
        getWeekData();
        getWeekSleepData();
//        MyApplication.threadService.execute(new initTask(3));
        MyApplication.threadService.execute(new initTask(4));
//        MyApplication.threadService.execute(new initTask(5));
        MyApplication.threadService.execute(new initTask(6));
    }

    public String getTodayDay()
    {
        Calendar calend = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(calend.getTime());
    }

    class initTask implements Runnable
    {
        int task;
        initTask(int i)
        {
            task = i;
        }
        @Override
        public void run()
        {
            SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
            String thName = Thread.currentThread().getName();
            Log.i(TAG, "当前线程名称" + thName + "开始执行：" + format.format(System.currentTimeMillis()));
            excute(task);
            Log.i(TAG, "当前线程名称" + thName + "执行结束：" + format.format(System.currentTimeMillis()));
        }
    }
    private void excute(int i)
    {
        switch (i)
        {
            case 0:
                initView();
                break;
            case 1:
                intiOther();
                break;
            case 2:
                initPager();
                break;
            case 3:
                getWeekData();
                break;
            case 4:
                getMonthSteps();
                break;
            case 5:
                getWeekSleepData();
                break;
            case 6:
                getMonthSleepData();
                break;
        }
    }

    private void intiOther()
    {
        dataListHistogram = new ArrayList<>(7);
        dataListMonth = new ArrayList<>(31);
        dataListSleepWeek = new ArrayList<>(7);
        dataListSleepMonth = new ArrayList<>(31);
    }

    private void initPager()
    {
        weekFragment = TrendWeekFragment.newInstance("","");
        monthFragment = TrendMonthFragment.newInstance();
        fragmentList = new ArrayList<>(2);
        fragmentList.add(weekFragment);
        fragmentList.add(monthFragment);
        myFragmentAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), fragmentList);
        // 获取viewpager并设置适配器
        mViewPager = (ViewPager)findViewById(R.id.trend_viewpager);
        mViewPager.setAdapter(myFragmentAdapter);
        // 设置viewpager页面切换监听
        mViewPager.addOnPageChangeListener(new myPagerListener());
    }


    private void initView()
    {
        // toolbar
                Toolbar toolbar = (Toolbar)findViewById(R.id.trend_toolbar);
                setSupportActionBar(toolbar);
                toolbar.setOnMenuItemClickListener(myMenuListener);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                toolbar.setNavigationIcon(R.mipmap.back_icon_new);
                toolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        TrendActivity.this.onBackPressed();
                    }
                });
        buttonOne = (RadioButton)findViewById(R.id.button_week);
        buttonOne.setChecked(true);
        buttonTwo = (RadioButton)findViewById(R.id.button_month);
        RadioGroup buttonGroup = (RadioGroup)findViewById(R.id.button_group);
        buttonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch (checkedId)
                {
                    case R.id.button_week:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.button_month:
                        mViewPager.setCurrentItem(1);
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        getRemoteTrendData();
    }


    private void getRemoteTrendData()
    {
        Log.i("趋势","从服务器上下载数据");
        String acc = UserAccountUtil.getAccount(getApplicationContext());
        if(acc == null || acc != null && acc.equals(""))return;
        if(!NetStatus.isNetWorkConnected(getApplicationContext()))return;
//        if(UpdateHistoryDataService.getInstance() != null)
//        {
//            UpdateHistoryDataService.getInstance().getStepTrendData(getTodayDay(), UserAccountUtil.getAccount(getApplicationContext()), stepCallback);
//            UpdateHistoryDataService.getInstance().getSleepTrendData(getTodayDay(), UserAccountUtil.getAccount(getApplicationContext()), sleepCallback);
//        }
    }


    private int count = 0;
    private int countInner = 0;
    private void getDatasFromDate(int day)
    {
        this.count = day;
        countInner = 0;
        CircleProgressDialog.getInstance().showCircleProgressDialog(TrendActivity.this);
        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 7; i++)
        {
            calendar.set(Calendar.DAY_OF_MONTH, day - i);
            String date = formate.format(calendar.getTime());
            UpdateHistoryDataService.getInstance().getDayData(date, UserAccountUtil.getAccount(getApplicationContext()), stepCallback);
            UpdateHistoryDataService.getInstance().getDateSleepData(date, UserAccountUtil.getAccount(getApplicationContext()), sleepCallback);
        }
        treadHandler.postDelayed(run, 10000);
    }

    private void getDatas(int day)
    {
        this.count = day;
        countInner = 0;
        CircleProgressDialog.getInstance().showCircleProgressDialog(TrendActivity.this);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        for (int i = 1; i <= day; i++)
        {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            String date = formate.format(calendar.getTime());
            UpdateHistoryDataService.getInstance().getDayData(date, UserAccountUtil.getAccount(getApplicationContext()), stepCallback);
            UpdateHistoryDataService.getInstance().getDateSleepData(date, UserAccountUtil.getAccount(getApplicationContext()), sleepCallback);
        }
        treadHandler.postDelayed(run, 10000);
    }

    Runnable run = new Runnable()
    {
        @Override
        public void run()
        {
            if(CircleProgressDialog.getInstance().getDialog() != null && CircleProgressDialog.getInstance().getDialog().isShowing())
            {
                reScreenWeekData();
                CircleProgressDialog.getInstance().closeCircleProgressDialog();
            }
        }
    };

    private void reScreenWeekData()
    {
        getWeekData();
        getWeekSleepData();
        getMonthSteps();
        getMonthSleepData();
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                weekFragment.refreshScreen();
                monthFragment.resetScreenData();
            }
        });
    }

    SendDataCallback stepCallback = new SendDataCallback()
    {
        @Override
        public void sendDataSuccess(String reslult)
        {
//            parseTrendJson(reslult);
        }
        @Override
        public void sendDataFailed(String result)
        {
            if(!canDoContinue())return;
            runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    MyToastUitls.showToast(TrendActivity.this, R.string.net_wrong, 1);
                    CircleProgressDialog.getInstance().closeCircleProgressDialog();
                }
            });
        }
        @Override
        public void sendDataTimeOut() {}
    };

    SendDataCallback sleepCallback = new SendDataCallback()
    {
        @Override
        public void sendDataSuccess(String reslult)
        {
            Log.i(TAG, "获取趋势数据：" + reslult);
            return;
//            try {
//                JSONObject json = new JSONObject(reslult);
//                String code = json.getString("code");
//                if(code != null && code.equals("9003"))
//                {
//                    String data = json.getString("data");
//                    JSONObject jsonInner = new JSONObject(data);
//                    String time = jsonInner.getString("time");
//                    String sleepData = jsonInner.getString("sleepData");
//                    new BleSleepDataDeal(getApplicationContext(), time, sleepData);
//                }
//                countInner ++;
//                if(count < 7)
//                {
//                    if(countInner == 7)
//                    {
//                        treadHandler.postDelayed(run, 1000);
//                    }
//                }
//                else
//                {
//                    Log.i(TAG, "获取趋势数据：" + countInner + "--" + count);
//                    if(countInner == count)
//                    {
//                        treadHandler.postDelayed(run, 1000);
//                    }
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
        @Override
        public void sendDataFailed(String result) {}
        @Override
        public void sendDataTimeOut() {}
    };

    private void parseTrendJson(String reslult)
    {
        Log.i(TAG, "获取趋势数据：" + reslult);
        try {
            JSONObject json = new JSONObject(reslult);
            String code = json.getString("code");
            if(code != null && code.equals("9003"))
            {
                String data = json.getString("data");
                JSONObject jsonEach = new JSONObject(data);
                String stepTrend = jsonEach.getString("stepTrend");
                JSONObject jsonTrend = new JSONObject(stepTrend);
                Iterator<String> it = jsonTrend.keys();
                ArrayList<stepAndCalorieEntity> entities = new ArrayList<>();
                int max = 0;
                while (it.hasNext())
                {
                    String key = it.next();
                    JSONObject eachData = new JSONObject(jsonTrend.getString(key));
                    String step = eachData.getString("step");
                    if(max > Integer.parseInt(step))
                    {
                        max = Integer.parseInt(step);
                    }
                }
                String[] dates = getStringArrayOnDate();
                for (int i = 0; i < dates.length; i++)
                {
                    String day = dates[i];
                    while (it.hasNext())
                    {
                        String key = it.next();
                        if(day != null && day.equals(key))
                        {
                            JSONObject eachData = new JSONObject(jsonTrend.getString(key));
                            String stepTarget = eachData.getString("stepTarget");
                            String calorie = eachData.getString("calorie");
                            String step = eachData.getString("step");
                            entities.add(new stepAndCalorieEntity(key.substring(5).replace("-", "/"), Integer.parseInt(step), max));
                        }
                    }
                }
                weekFragment.updateWeekSteps(entities);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateStepIntoDB(String key, String value)
    {
        new DayDataDealer(getApplicationContext(), key, value);
    }

    private void parseTrendSleepJson(String reslult)
    {
        try {
            JSONObject json = new JSONObject(reslult);
            String code = json.getString("code");
            if(code != null && code.equals("9003"))
            {
                String data = json.getString("data");
                JSONObject jsonInner = new JSONObject(data);
                JSONObject jsonData = jsonInner.getJSONObject("sleepTrend");
                Iterator<String> it = jsonData.keys();
                while (it.hasNext())
                {
                    String key = it.next();
                    String value = jsonData.getString(key);
                    if(value == null || value.equals(""))continue;
                    updateSleepIntoDB(key, value);
                }
            }
            else if(code != null && code.equals("9004")){}
            else if(code != null && code.equals("9001")){}

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateSleepIntoDB(String key, String value)
    {
        new BleSleepDataDeal(getApplicationContext(), key, value);
    }

    private String getCurrentDate()
    {
        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        return formate.format(calendar.getTime());
    }
    public void getMonthSleepData()
    {
        dataListSleepMonth.clear();
        int maxSleep = 0;
        String yes = null;
        String userAccount = UserAccountUtil.getAccount(TrendActivity.this);
        MyDBHelperForDayData db = MyDBHelperForDayData.getInstance(TrendActivity.this);
        Calendar calendar = Calendar.getInstance();
//        int maxDate = calendar.getActualMaximum(Calendar.DATE);
        int maxDate = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int[][] sleepAll = new int[maxDate][];
        for (int i = 0; i < maxDate; i ++)
        {
            String yestudayData = "";
            if(yestudayData.equals(""))
            {
                yestudayData = yes;
            }
            int a = i + 1;
            calendar.set(year, month, a);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String day = format.format(calendar.getTime());
            Cursor cursorYestuday = db.selectTheSleepData(TrendActivity.this, userAccount, day, DeviceTypeUtils.getDeviceType(getApplicationContext()));
            String todayData = parseTheSleepData(cursorYestuday);
            String todayResult = null;
            String yestudayResult = null;
            if(todayData != null && !todayData.equals(""))
            {
                todayResult = todayData.substring(0, 60);
            }
            if(yestudayData != null && !yestudayData.equals(""))
            {
                yestudayResult = yestudayData.replaceAll("\\d{12}$", "");
                yestudayResult = yestudayData.replaceAll(yestudayResult, "");
            }

            StringBuffer buffer = new StringBuffer();
            if(yestudayResult != null && !yestudayResult.equals(""))
            {
                buffer.append(yestudayResult);
            }
            if(todayResult != null && !todayResult.equals(""))
            {
                buffer.append(todayResult);
            }
            int[] sleeps = getTheTotalSleepAndDeepSleep(compareAllSleepData(buffer.toString()));
            sleepAll[i] = sleeps;
            yes = todayData;
            int totalSleep = sleeps[0] + sleeps[1] + sleeps[2];
            if(totalSleep > maxSleep)
            {
                maxSleep = totalSleep;
            }
        }
        int allSleepTimes = 0;
        int deepSleepTimes = 0;
        int sleepCount = 0;
        int deepCount = 0;
        for (int j = 0; j < maxDate; j ++)
        {
            String times = null;
            int time = j + 1;
            if(time < 10)
            {
                times = "0" + time;
            }
            else
            {
                times = String.valueOf(time);
            }
            int[] sleepData = sleepAll[j];
            if((sleepData[0] + sleepData[1]) > 0)
            {
                allSleepTimes += (sleepData[0] + sleepData[1]);
                sleepCount++;
            }
            if(sleepData[1] > 0)
            {
                deepSleepTimes += sleepData[1];
                deepCount ++;
            }
            dataListSleepMonth.add(new Histogram_sleep_entity(times, sleepData[1] * 10, sleepData[0] * 10, sleepData[2] * 10, maxSleep));
        }
        if(sleepCount > 0)
        {
            monthData[4] = (allSleepTimes * 10 / sleepCount);
        }
        else
        {
            monthData[4] = 0;
        }
        if(deepCount > 0)
        {
            monthData[5] = (deepSleepTimes * 10 / deepCount);
        }
        else
        {
            monthData[5] = 0;
        }
    }



    public int[] getMonthDatas()
    {
        return monthData;
    }

    public ArrayList<Histogram_sleep_entity> getSleepDataForMonth()
    {
        return dataListSleepMonth;
    }



    private void getWeekSleepData()
    {
        dataListSleepWeek.clear();
        int[][] datesDimen = new int[7][];
        int count = 0;
        int deepCount = 0;
        int maxSleep = 0;
        String yes = null;
        String userAccount = UserAccountUtil.getAccount(TrendActivity.this);
        MyDBHelperForDayData dbHelper = MyDBHelperForDayData.getInstance(TrendActivity.this);
        String[] days = getStringArrayOnDateForSleep();
        for (int i = 0; i < days.length - 1; i++)
        {
            Cursor cursorYestuday = null;
            String yestuday = null;
            if(yes == null || yes.equals(""))
            {
                cursorYestuday = dbHelper.selectTheSleepData(TrendActivity.this, userAccount, days[i], DeviceTypeUtils.getDeviceType(getApplicationContext()));
                yestuday = parseTheSleepData(cursorYestuday);
            }
            else
            {
                yestuday = yes;
            }
            Cursor cursorToday = dbHelper.selectTheSleepData(TrendActivity.this, userAccount, days[i + 1], DeviceTypeUtils.getDeviceType(getApplicationContext()));

            String today = parseTheSleepData(cursorToday);
            String yestudayResult = null;
            String todayResult = null;
            if(yestuday != null && !yestuday.equals(""))
            {
                String res = yestuday.replaceAll("\\d{12}$", "");
                yestudayResult = yestuday.replaceAll(res, "");
            }
            if(today != null && !today.equals(""))
            {
                todayResult = today.substring(0, 60);
            }
            StringBuffer buffer = new StringBuffer();
            if(yestudayResult != null && !yestudayResult.equals(""))
            {
                buffer.append(yestudayResult);
            }
            if(todayResult != null && !todayResult.equals(""))
            {
                buffer.append(todayResult);
            }
            int[] sleeps = getTheTotalSleepAndDeepSleep(compareAllSleepData(buffer.toString()));
            datesDimen[i] = sleeps;
            yes = today;
//            Log.i(TAG, "睡眠查询时间:" + days[i]);
            int totalSleep = sleeps[0] + sleeps[1] + sleeps[2];
            if(totalSleep > maxSleep)
            {
                maxSleep = totalSleep;
            }
        }
        int totalSleepTime = 0;
        int totalDeepSleepTime = 0;
        for (int j = 0; j < days.length - 1; j ++)
        {
            String time = days[j + 1].substring(5);
            time = time.replaceAll("-", "/");
            int[] sleep = datesDimen[j];
            // 浅睡 深睡 清醒
            dataListSleepWeek.add(new Histogram_sleep_entity(time, sleep[1] * 10, sleep[0] * 10,  sleep[2] * 10, maxSleep));
            if(sleep[0] + sleep[1] > 0)
            {
                totalSleepTime += sleep[0] + sleep[1];
                count++;
            }
            if(sleep[1] > 0)
            {
                totalDeepSleepTime += sleep[1];
                deepCount++;
            }
        }
        if(count <= 0)
        {
            weekData[4] = 0;
        }
        else
        {
            weekData[4] = (totalSleepTime * 10) / count;

        }
        if(deepCount <= 0)
        {
            weekData[5] = 0;
        }
        else
        {
            weekData[5] = (totalDeepSleepTime * 10) / deepCount;
        }
    }

    private String compareAllSleepData(String sleepData)
    {
        String first = sleepData.replaceAll("^[0, 3]+", "");
        return first.replaceAll("[0, 3]+$", "");
    }


    public int[] getWeekDataAverage()
    {
        return weekData;
    }


    public ArrayList<Histogram_sleep_entity> getWeekSleepDataForFragment()
    {
        return dataListSleepWeek;
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

    private int[] getTheTotalSleepAndDeepSleep(String s)
    {
        s = Replace(s, "2332", "2112");
        s = Replace(s, "2002", "2112");
        s = Replace(s, "232", "212");
        s = Replace(s, "202", "212");
        int[] sleepData = new int[3];   // 数组长度3代表深睡， 浅睡， 清醒
        int light = 0;
        int deep = 0;
        int wake = 0;
        Log.i(TAG, "截取后睡眠数据:" + s);
        if(s == null || !s.equals(""))
        {
            for (int i = 0; i < s.length(); i++)
            {
                int a = Integer.parseInt(s.substring(i, i+1));
                switch (a)
                {
                    case 1:
                        light++;
                        break;
                    case 2:
                        deep++;
                        break;
                    case 0:
                    case 3:
                        wake++;
                        break;
                }
            }
            sleepData[0] = light;
            sleepData[1] = deep;
            sleepData[2] = wake;
        }
        else
        {
            sleepData[0] = 0;
            sleepData[1] = 0;
            sleepData[2] = 0;
        }
        return sleepData;
    }

    private String parseTheSleepData(Cursor cursorYestuday)
    {
        if(cursorYestuday.getCount() != 0)
        {
            if(cursorYestuday.moveToFirst())
            {
                do
                {
                   return cursorYestuday.getString(cursorYestuday.getColumnIndex("sleepData"));
                }while (cursorYestuday.moveToNext());
            }
        }
        return null;
    }

    private String[] getStringArrayOnDateForSleep()
    {
        String[] dayArray = new String[8];
        for (int i = 0; i < dayArray.length; i++)
        {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            calendar.add(Calendar.DATE, -i);
            Date date = calendar.getTime();
            String day = format.format(date);

            dayArray[dayArray.length - i - 1] = day;
        }
        return dayArray;
    }

    public void getWeekData()
    {
        dataListHistogram.clear();
        String userAccount = UserAccountUtil.getAccount(TrendActivity.this);
        MyDBHelperForDayData dbHelper = MyDBHelperForDayData.getInstance(TrendActivity.this);
        String[] days = getStringArrayOnDate();
        String[] stepes = new String[days.length];
        int max = 0;
        for (int i = 0; i < days.length; i++)
        {
            Cursor cursor = dbHelper.selectTheStepAllFromDB(TrendActivity.this, userAccount, days[i], DeviceTypeUtils.getDeviceType(getApplicationContext()));
            String stepAll = parseTheCursor(cursor);
            if(stepAll == null || stepAll.equals(""))
            {
                stepAll = "0";
            }
            stepes[i] = stepAll;
            if(stepAll != null && !stepAll.equals(""))
            {
                int steps = Integer.parseInt(stepAll);
                if(steps > max)
                {
                    max = steps;
                }
            }
        }
        int calorieTotal = 0;
        int totalSteps = 0;
        int completionCount = 0;
        int stepCount = 0;
        int calorieCount = 0;
        for (int j = 0; j < days.length; j ++)
        {
            int eachSteps = Integer.parseInt(stepes[j]);
            String dayss = days[j].substring(5);
            dayss = dayss.replace('-', '/');
            dataListHistogram.add(new stepAndCalorieEntity(dayss, eachSteps, max));
            String tar = LocalDataSaveTool.getInstance(TrendActivity.this).readSp(MyConfingInfo.TARGET_SETTING_VALUE);
            int target = Integer.parseInt(tar);
            if(eachSteps >= target)
            {
                completionCount ++;
            }
            if(eachSteps > 0)
            {
                totalSteps += eachSteps;
                stepCount ++;
            }
            Cursor mCursor = MyDBHelperForDayData.getInstance(TrendActivity.this)
                    .selectTheCalorieFromDb(TrendActivity.this, userAccount, days[j], DeviceTypeUtils.getDeviceType(getApplicationContext()));
            int calories = parseCursorToGetCalorie(mCursor);
            if(calories > 0)
            {
                calorieTotal += calories;
                calorieCount ++;
            }
        }
        if(stepCount > 0)
        {
            weekData[2] = totalSteps / stepCount;
        }
        else
        {
            weekData[2] = 0;
        }
        if(calorieCount > 0)
        {
            weekData[3] = calorieTotal / calorieCount;
        }
        else
        {
            weekData[3] = 0;
        }
        weekData[0] = completionCount;
        weekData[1] = max;
    }

    private int parseCursorToGetCalorie(Cursor mCursor)
    {
        if(mCursor.getCount() != 0)
        {
            if(mCursor.moveToFirst())
            {
                do
                {
                    return mCursor.getInt(mCursor.getColumnIndex("calorie"));
                }while (mCursor.moveToNext());
            }
        }
        return 0;
    }

    public void getMonthSteps()
    {
        dataListMonth.clear();
        int stepCount = 0;
        int calorieCount = 0;
        int totalCalorie = 0;
        String userAccount = UserAccountUtil.getAccount(TrendActivity.this);
        MyDBHelperForDayData db = MyDBHelperForDayData.getInstance(TrendActivity.this);
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
//        int dataMax = calendar.getActualMaximum(Calendar.DATE);
        int dataMax = calendar.get(Calendar.DAY_OF_MONTH);
        Log.i(TAG, "当月天数:" + dataMax);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        String[] days = new String[dataMax];
        int[] stepsEach = new int[dataMax];
        int maxStpe = 0;
        for (int i = 0; i < dataMax; i++)
        {
            int a = i + 1;
            calendar.set(year, month, a);
            SimpleDateFormat farmat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String day = farmat.format(calendar.getTime());
            days[i] = (a < 10) ? ("0" + a) : String.valueOf(a);
            Cursor mCursor = db.selectTheStepAllFromDB(TrendActivity.this, userAccount, day, DeviceTypeUtils.getDeviceType(getApplicationContext()));
            int steps = parseTheMonthSteps(mCursor);
            Cursor mCursors = db.selectTheCalorieFromDb(TrendActivity.this, userAccount, day, DeviceTypeUtils.getDeviceType(getApplicationContext()));
            int caloriees = parseTheCursorToGetCalorie(mCursors);
            if(caloriees > 0)
            {
                totalCalorie += caloriees;
                calorieCount ++;
            }
            stepsEach[i] = steps;
            if(steps > maxStpe)
            {
                maxStpe = steps;
            }
        }
        int completion = 0;
        int averageSteps = 0;
        int totalSteps = 0;
        int averageCalorie = 0;
        String target = LocalDataSaveTool.getInstance(TrendActivity.this).readSp(MyConfingInfo.TARGET_SETTING_VALUE);
        int targetValue = Integer.parseInt(target);
        for (int j = 0; j < dataMax; j++)
        {
            String d = days[j];
//            d = d.substring(brocast);
            dataListMonth.add(new stepAndCalorieEntity(d, stepsEach[j], maxStpe));
            if(stepsEach[j] > 0)
            {
                totalSteps += stepsEach[j];
                stepCount ++;
            }
            if(stepsEach[j] >= targetValue)
            {
                completion ++;
            }
        }
        if(stepCount > 0)
        {
            monthData[2] = totalSteps / stepCount;
        }
        else
        {
            monthData[2] = 0;
        }
        if(calorieCount > 0)
        {
            monthData[3] = totalCalorie / calorieCount;
        }
        else
        {
            monthData[3] = 0;
        }
        monthData[0] = completion;
        monthData[1] = maxStpe;
    }

    private int parseTheCursorToGetCalorie(Cursor mCursors)
    {
        if(mCursors.getCount() != 0)
        {
            if(mCursors.moveToFirst())
            {
                do
                {
                    return mCursors.getInt(mCursors.getColumnIndex("calorie"));
                }while (mCursors.moveToNext());
            }
        }
        return 0;
    }


    public ArrayList<stepAndCalorieEntity> getMonthStepData()
    {
        return dataListMonth;
    }


    private int parseTheMonthSteps(Cursor mCursor)
    {
        int steps = 0;
        if(mCursor.getCount() != 0)
        {
            if(mCursor.moveToFirst())
            {
                do
                {
                    steps = mCursor.getInt(mCursor.getColumnIndex("stepAll"));
                }while (mCursor.moveToNext());
            }
        }
        return steps;
    }


    public ArrayList<stepAndCalorieEntity> getWeekStepData()
    {
        return dataListHistogram;
    }

    private String parseTheCursor(Cursor cursor)
    {
        String stepAll = "";
        if(cursor.getCount() != 0)
        {
            if(cursor.moveToFirst())
            {
                do
                {
                    stepAll = cursor.getString(cursor.getColumnIndex("stepAll"));
                }while (cursor.moveToNext());
            }
        }
        return stepAll;
    }

    private String[] getStringArrayOnDate()
    {
        String[] dayArray = new String[7];
        for (int i = 0; i < dayArray.length; i++)
        {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            calendar.add(Calendar.DATE, -i);
            Date date = calendar.getTime();
            String day = format.format(date);
            dayArray[dayArray.length - i - 1] = day;
        }
        return dayArray;
    }


    private String[] getStringArrayOnDateOnMonth()
    {
        String[] dayArray = new String[31];
        for (int i = 0; i < dayArray.length; i++)
        {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            calendar.add(Calendar.DATE, -i);
            Date date = calendar.getTime();
            String day = format.format(date);
            dayArray[dayArray.length - i - 1] = day;
        }
        return dayArray;
    }



    class myPagerListener implements ViewPager.OnPageChangeListener
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
        @Override
        public void onPageSelected(int position) {
            if(position == 0)
            {
                if(!buttonOne.isChecked())
                {
                    buttonOne.setChecked(true);
                }
            }
            else if(position == 1)
            {
                if(!buttonTwo.isChecked())
                {
                    buttonTwo.setChecked(true);
                }
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {}
    }


    // 菜单监听器
    private Toolbar.OnMenuItemClickListener myMenuListener = new Toolbar.OnMenuItemClickListener()
    {
        @Override
        public boolean onMenuItemClick(MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.trend_share_menu:
                    ShotScreenForShare.getInstance().takeshotScreen(TrendActivity.this);
                    Intent intent = new Intent();
                    intent.setClass(TrendActivity.this, ShareActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
            }
            return false;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share_simpel, menu);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void settingTheStatebarAndNavigationbar() {
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

}
