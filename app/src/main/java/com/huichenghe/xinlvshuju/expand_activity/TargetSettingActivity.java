package com.huichenghe.xinlvshuju.expand_activity;


import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.huichenghe.bleControl.Ble.BleDataForTarget;
import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.xinlvshuju.CommonUtilss.CommonUtils;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.expand_activity.MovementDetail.SleepDataHelper;
import com.huichenghe.xinlvshuju.http.UserAccountUtil;
import com.huichenghe.xinlvshuju.mainpack.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class TargetSettingActivity extends BaseActivity
{
    public static final String TAG = TargetSettingActivity.class.getSimpleName();
    private AppCompatSeekBar appCompatSeekBar;
    private AppCompatSeekBar appCompatSeekBarSleep;
    private ArrayList<TextView> aList;
    private ArrayList<LinearLayout> bList;
    private ArrayList<TextView> tvListSleep;
    private ArrayList<LinearLayout> tvListSleepB;
    private TextView a2000, a4000, a6000, a8000
            , a10000, a12000, a14000, a16000, a18000, a20000
            , sleep1, sleep11, sleep2, sleep22, sleep3, sleep33, sleep4, sleep44, sleep5;

    private TextView b2000, b4000, b6000, b8000
            , b10000, b12000, b14000, b16000, b18000, b20000
            , sleepb1, sleepb2, sleepb3, sleepb4, sleepb5;
    private TranslateAnimation mTranslateAnimation;
    private ScaleAnimation mScaleAnimation;
    private AnimationSet animSet = new AnimationSet(true);
    private AnimationSet animSetSleep = new AnimationSet(true);
    private TranslateAnimation mSleepTrAnimation;
    private ScaleAnimation mSleepScalAnimation;
    private LinearLayout layout_2000, layout_4000, layout_6000, layout_8000, layout_10000,
            layout_12000, layout_14000, layout_16000, layout_18000, layout_20000;
    private LinearLayout layout_4h, layout_5h, layout_6h, layout_7h, layout_8h, layout_9h, layout_10h, layout_11h,  layout_12h;
    boolean isA2000 = false;
    boolean isA4000 = false;
    boolean isA6000 = false;
    boolean isA8000 = false;
    boolean isA10000 = false;
    boolean isA12000 = false;
    boolean isA14000 = false;
    boolean isA16000 = false;
    boolean isA18000 = false;
    boolean isA20000 = false;
//    private TextView sportState, sleepState;
    private int sportTarget;
    private int[] dataArray = new int[]{2000, 4000, 6000, 8000, 10000, 12000, 14000, 16000, 18000, 20000};
    private int[] sleepArray = new int[]{4, 5, 6, 7, 8, 9, 10, 11, 12};
    private TextView[] aTV;
    private TextView[] bTV;
    private int maxProgress = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_setting);

//        if(BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice())
//        {
//            BluetoothLeService.getInstance().setBLENotify(null, true, false);
//        }

        init();
        initText();
//        initAnimationStart();
//        initAnimationStartSleep();
//        initAnimationStop();
        settingTheStatebarAndNavigationbar();

    }

    private void compareTheTargetAndShow(int sportTarget)
    {
        for (int i = 0; i < dataArray.length; i++)
        {
            Log.i(TAG, "目标是：" + dataArray[i] + "--" +  sportTarget);
            if(dataArray[i] == sportTarget)
            {
                int status = R.string.zhengchang;
                showAndHideTheTextView(aList.get(i), bList.get(i), i, aList, bList);
                if(sportTarget < 4000)
                {
                    status = R.string.pianshao;
                }
                else if(sportTarget < 8000 && sportTarget >= 4000)
                {
                    status = R.string.zhengchang;
                }
                else if(sportTarget < 12000 && sportTarget >= 8000)
                {
                    status = R.string.huoyue;
                }
                else if(sportTarget <= 20000 && sportTarget > 12000)
                {
                    status = R.string.daren;
                }
//                sportState.setText(status);

                appCompatSeekBar.setProgress((maxProgress / 9) * i);

            }
        }




    }


    /**
     * 初始化恢复动画
     */
    private void initAnimationStop() { }


    /**
     * 初始化动画对象
     */
    private void initAnimationStartSleep()
    {   // 缩放动画
        mSleepScalAnimation = new ScaleAnimation(1.0f, 2.0f, 1.0f, 2.0f);
        mSleepScalAnimation.setDuration(200);
        // 移动动画
        mSleepTrAnimation = new TranslateAnimation(0, 0, 0, -72);
        mSleepTrAnimation.setDuration(200);

//        mTranslateAnimation.setRepeatCount(clock);
        animSetSleep.addAnimation(mSleepScalAnimation);
        animSetSleep.addAnimation(mSleepTrAnimation);
        animSetSleep.setFillAfter(true);
        animSetSleep.setRepeatCount(1);
        animSetSleep.setInterpolator(this, android.R.anim.accelerate_decelerate_interpolator);

    }

    /**
     * 初始化动画对象
     */
    private void initAnimationStart()
    {   // 缩放动画
        mScaleAnimation = new ScaleAnimation(1.0f, 2.0f, 1.0f, 2.0f);
        mScaleAnimation.setDuration(200);
        // 移动动画
        mTranslateAnimation = new TranslateAnimation(0, -22, 0, -72);
        mTranslateAnimation.setDuration(200);

//        mTranslateAnimation.setRepeatCount(clock);
        animSet.addAnimation(mScaleAnimation);
        animSet.addAnimation(mTranslateAnimation);
        animSet.setFillAfter(true);
        animSet.setInterpolator(this, android.R.anim.accelerate_decelerate_interpolator);

    }


    /**
     * 初始化目标TextView
     */
    private void initText()
    {
        layout_2000 = (LinearLayout)findViewById(R.id.layout_2000);
        layout_4000 = (LinearLayout)findViewById(R.id.layout_4000);
        layout_6000 = (LinearLayout)findViewById(R.id.layout_6000);
        layout_8000 = (LinearLayout)findViewById(R.id.layout_8000);
        layout_10000 = (LinearLayout)findViewById(R.id.layout_10000);
        layout_12000 = (LinearLayout)findViewById(R.id.layout_12000);
        layout_14000 = (LinearLayout)findViewById(R.id.layout_14000);
        layout_16000 = (LinearLayout)findViewById(R.id.layout_16000);
        layout_18000 = (LinearLayout)findViewById(R.id.layout_18000);
        layout_20000 = (LinearLayout)findViewById(R.id.layout_20000);


        layout_4h = (LinearLayout)findViewById(R.id.layout_4h);
        layout_5h = (LinearLayout)findViewById(R.id.layout_5h);
        layout_6h = (LinearLayout)findViewById(R.id.layout_6h);
        layout_7h = (LinearLayout)findViewById(R.id.layout_7h);
        layout_8h = (LinearLayout)findViewById(R.id.layout_8h);
        layout_9h = (LinearLayout)findViewById(R.id.layout_9h);
        layout_10h = (LinearLayout)findViewById(R.id.layout_10h);
        layout_11h = (LinearLayout)findViewById(R.id.layout_11h);
        layout_12h = (LinearLayout)findViewById(R.id.layout_12h);
        // 运动Text
        a2000 = (TextView)findViewById(R.id.a2000);
        a4000 = (TextView)findViewById(R.id.a4000);
        a6000 = (TextView)findViewById(R.id.a6000);
        a8000 = (TextView)findViewById(R.id.a8000);
        a10000 = (TextView)findViewById(R.id.a10000);
        a12000 = (TextView)findViewById(R.id.a12000);
        a14000 = (TextView)findViewById(R.id.a14000);
        a16000 = (TextView)findViewById(R.id.a16000);
        a18000 = (TextView)findViewById(R.id.a180000);
        a20000 = (TextView)findViewById(R.id.a20000);

        b2000 = (TextView)findViewById(R.id.b2000);
        b4000 = (TextView)findViewById(R.id.b4000);
        b6000 = (TextView)findViewById(R.id.b6000);
        b8000 = (TextView)findViewById(R.id.b8000);
        b10000 = (TextView)findViewById(R.id.b10000);
        b12000 = (TextView)findViewById(R.id.b12000);
        b14000 = (TextView)findViewById(R.id.b14000);
        b16000 = (TextView)findViewById(R.id.b16000);
        b18000 = (TextView)findViewById(R.id.b180000);
        b20000 = (TextView)findViewById(R.id.b20000);

        WindowManager manager = getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        String oneString = b4000.getText().toString();
        String twoString = b20000.getText().toString();
        int size1 = (int)CommonUtils.getTextViewWidth(TargetSettingActivity.this, oneString, 18);
        int size2 = (int)CommonUtils.getTextViewWidth(TargetSettingActivity.this, twoString, 18);



        layout_4000.setPadding(metrics.widthPixels/10 * 1 - size1/4, 0, 0, 0);
        layout_6000.setPadding(metrics.widthPixels/10 * 2 - size1/4, 0, 0, 0);
        layout_8000.setPadding(metrics.widthPixels/10 * 3 - size2/2, 0, 0, 0);
        layout_10000.setPadding(metrics.widthPixels/10 * 4 - size2/2, 0, 0, 0);
        layout_12000.setPadding(metrics.widthPixels/10 * 5 - size2/2, 0, 0, 0);
        layout_14000.setPadding(metrics.widthPixels/10 * 6 - size2/2, 0, 0, 0);
        layout_16000.setPadding(metrics.widthPixels/10 * 7 - size2/2, 0, 0, 0);
        layout_18000.setPadding(metrics.widthPixels/10 * 7 - size2/2, 0, 0, 0);
        layout_20000.setPadding(metrics.widthPixels/10 * 7 - size2/2, 0, 0, 0);

//        Log.i(TAG, "字体宽度:" + b20000.getWidth());


        aList.add(a2000);
        aList.add(a4000);
        aList.add(a6000);
        aList.add(a8000);
        aList.add(a10000);
        aList.add(a12000);
        aList.add(a14000);
        aList.add(a16000);
        aList.add(a18000);
        aList.add(a20000);

        bList.add(layout_2000);
        bList.add(layout_4000);
        bList.add(layout_6000);
        bList.add(layout_8000);
        bList.add(layout_10000);
        bList.add(layout_12000);
        bList.add(layout_14000);
        bList.add(layout_16000);
        bList.add(layout_18000);
        bList.add(layout_20000);
        // 睡眠Text
        sleep1 = (TextView)findViewById(R.id.below_4h);
        sleep11 = (TextView)findViewById(R.id.below_5h);
        sleep2 = (TextView)findViewById(R.id.below_6h);
        sleep22 = (TextView)findViewById(R.id.below_7h);
        sleep3 = (TextView)findViewById(R.id.below_8h);
        sleep33 = (TextView)findViewById(R.id.below_9h);
        sleep4 = (TextView)findViewById(R.id.below_10h);
        sleep44 = (TextView)findViewById(R.id.below_11h);
        sleep5 = (TextView)findViewById(R.id.below_12h);
        tvListSleep.add(sleep1);
        tvListSleep.add(sleep11);
        tvListSleep.add(sleep2);
        tvListSleep.add(sleep22);
        tvListSleep.add(sleep3);
        tvListSleep.add(sleep33);
        tvListSleep.add(sleep4);
        tvListSleep.add(sleep44);
        tvListSleep.add(sleep5);

        sleepb1 = (TextView)findViewById(R.id.four_hour_b);
        sleepb2 = (TextView)findViewById(R.id.six_hour_b);
        sleepb3 = (TextView)findViewById(R.id.eight_hour_b);
        sleepb4 = (TextView)findViewById(R.id.ten_hour_b);
        sleepb5 = (TextView)findViewById(R.id.telv_hour_b);
        layout_5h.setPadding(metrics.widthPixels/ 12 * 1, 0, 0, 0);
        layout_6h.setPadding(metrics.widthPixels / 12 * 2, 0, 0, 0);
        layout_7h.setPadding(metrics.widthPixels / 12 * 3, 0, 0, 0);
        layout_8h.setPadding(metrics.widthPixels / 12 * 4, 0, 0, 0);
        layout_9h.setPadding(metrics.widthPixels / 12 * 5, 0, 0, 0);
        layout_10h.setPadding(metrics.widthPixels / 12 * 6, 0, 0, 0);
        layout_11h.setPadding(metrics.widthPixels / 12 * 7, 0, 0, 0);
        layout_12h.setPadding(metrics.widthPixels / 12 * 8, 0, 0, 0);
        tvListSleepB.add(layout_4h);
        tvListSleepB.add(layout_5h);
        tvListSleepB.add(layout_6h);
        tvListSleepB.add(layout_7h);
        tvListSleepB.add(layout_8h);
        tvListSleepB.add(layout_9h);
        tvListSleepB.add(layout_10h);
        tvListSleepB.add(layout_11h);
        tvListSleepB.add(layout_12h);

        aTV = new TextView[]{a2000, a4000, a6000, a8000, a10000, a12000, a14000, a16000, a18000, a20000};
        bTV = new TextView[]{b2000, b4000, b6000, b8000, b10000, b12000, b14000, b16000, b18000, b20000};
        getSPAndShow();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    private void getSPAndShow()
    {
//        sleepState = (TextView)findViewById(R.id.sleep_status);
//        sportState = (TextView)findViewById(R.id.sport_status);
        String sportTar = LocalDataSaveTool.getInstance(TargetSettingActivity.this).readSp(MyConfingInfo.TARGET_SETTING_VALUE);
        sportTarget = Integer.valueOf(sportTar);
        compareTheTargetAndShow(sportTarget);
        String sleepTar = LocalDataSaveTool.getInstance(TargetSettingActivity.this).readSp(MyConfingInfo.TARGET_SETTING_VALUE_SLEEP);
        compareTheSleepTargetAndShow(sleepTar);
    }

    private void compareTheSleepTargetAndShow(String sleepTar)
    {
        if(sleepTar != null && !sleepTar.equals(""))
        {
            int state = 0;
            for (int i = 0; i < sleepArray.length; i++)
            {
                if(sleepArray[i] == Integer.valueOf(sleepTar)) {
                    showAndHideTheTextView(tvListSleep.get(i), tvListSleepB.get(i), i, tvListSleep, tvListSleepB);
                    if(Integer.valueOf(sleepTar) <= 6)
                    {
                        state = R.string.pianshao;
                    }
                    else if(Integer.valueOf(sleepTar) < 10 && Integer.valueOf(sleepTar) >= 8)
                    {
                        state = R.string.zhengchang;
                    }
                    else if(Integer.valueOf(sleepTar) <= 12 && Integer.valueOf(sleepTar) > 8)
                    {
                        state = R.string.chongzu;
                    }
//                    sleepState.setText(state);
                    appCompatSeekBarSleep.setProgress((maxProgress / 8) * i);
                }
            }

        }
    }


    private void clearOtherAnimSleep(TextView tv)
    {
        for(int j = 0; j < tvListSleep.size(); j ++)
        {
            if(tvListSleep.get(j).getId() != tv.getId())
            {
                TextView v = tvListSleep.get(j);
                v.clearAnimation();
                v.setTextColor(getResources().getColor(R.color.grey_color_dark));

            }
        }
    }


    /**
     * 设置沉浸式状态栏和底部虚拟键全屏
     */
    private void settingTheStatebarAndNavigationbar() {
        // 透明状态栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //底部虚拟键透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }


    // 获取界面对象
    private void init()
    {
        aList = new ArrayList<>();
        bList = new ArrayList<>();
        tvListSleep = new ArrayList<>();
        tvListSleepB = new ArrayList<>();
        Toolbar toolbar = (Toolbar)findViewById(R.id.target_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.back_icon_new);
        toolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                TargetSettingActivity.this.onBackPressed();
            }
        });

        toolbar.setOnMenuItemClickListener(menuListener);

        appCompatSeekBarSleep = (AppCompatSeekBar)findViewById(R.id.sleep_seekBar);
        appCompatSeekBarSleep.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(0 <= progress && progress < 11)
                {
                    showAndHideTheTextView(sleep1, layout_4h, 0, tvListSleep, tvListSleepB);
                }
                if(11 <= progress && progress < 22)
                {
                    showAndHideTheTextView(sleep11, layout_5h, 1, tvListSleep, tvListSleepB);
                }
                if(22 <= progress && progress < 33)
                {
                    showAndHideTheTextView(sleep2, layout_6h, 2, tvListSleep, tvListSleepB);
                }
                if(33 <= progress && progress < 44)
                {
                    showAndHideTheTextView(sleep22, layout_7h, 3, tvListSleep, tvListSleepB);
                }
                if(44 <= progress && progress < 55)
                {
                    showAndHideTheTextView(sleep3, layout_8h, 4, tvListSleep, tvListSleepB);
                }
                if(55 <= progress && progress < 66)
                {
                    showAndHideTheTextView(sleep33, layout_9h, 5, tvListSleep, tvListSleepB);
                }
                if(66 <= progress && progress < 77)
                {
                    showAndHideTheTextView(sleep4, layout_10h, 6, tvListSleep, tvListSleepB);
                }
                if(77 < progress && progress < 88)
                {
                    showAndHideTheTextView(sleep44, layout_11h, 7, tvListSleep, tvListSleepB);
                }
                if(88 < progress && progress < 100)
                {
                    showAndHideTheTextView(sleep5, layout_12h, 8, tvListSleep, tvListSleepB);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if(0 <= progress &&  progress < 11)
                {
                    seekBar.setProgress(maxProgress /8 * 0);
                }
                else if(11 <= progress && progress < 22)
                {
                    seekBar.setProgress(maxProgress / 8 * 1);
                }
                else if(22 <= progress && progress < 33)
                {
                    seekBar.setProgress(maxProgress / 8 * 2);
                }
                else if(33 <= progress && progress < 44)
                {
                    seekBar.setProgress(maxProgress / 8 * 3);
                }
                else if(44 <= progress && progress < 55)
                {
                    seekBar.setProgress(maxProgress / 8 * 4);
                }
                else if(55 <= progress && progress < 66)
                {
                    seekBar.setProgress(maxProgress / 8 * 5);
                }
                else if(66 <= progress && progress < 77)
                {
                    seekBar.setProgress(maxProgress / 8 * 6);
                }
                else if(77 <= progress && progress < 88)
                {
                    seekBar.setProgress(maxProgress / 8 * 7);
                }
                else if(88 <= progress && progress < 100)
                {
                    seekBar.setProgress(maxProgress);
                }


            }
        });

        appCompatSeekBar = (AppCompatSeekBar)findViewById(R.id.sport_seekBar);
        appCompatSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                Log.i("", "当前进度：" + progress);

                if( 0 <= progress && progress < 10)
                {
                    showAndHideTheTextView(a2000, layout_2000, 0, aList, bList);
//                    sportState.setText(R.string.pianshao);
                }

                if( 10 <= progress && progress < 20)
                {
                    showAndHideTheTextView(a4000, layout_4000, 1, aList, bList);
//                    sportState.setText(R.string.pianshao);

                }

                if( 20 <= progress && progress < 30)
                {
                    showAndHideTheTextView(a6000, layout_6000, 2, aList, bList);
//                    sportState.setText(R.string.zhengchang);

                }

                if( 30 <= progress && progress < 40)
                {
                    showAndHideTheTextView(a8000, layout_8000, 3, aList, bList);
//                    sportState.setText(R.string.zhengchang);

                }

                if( 40 <= progress && progress < 50)
                {
                    showAndHideTheTextView(a10000, layout_10000, 4, aList, bList);
//                    sportState.setText(R.string.huoyue);

                }
                if( 50 <= progress && progress < 60)
                {
                    showAndHideTheTextView(a12000, layout_12000, 5, aList, bList);
//                    sportState.setText(R.string.huoyue);


                }
                if(60 <= progress && progress < 70)
                {
                    showAndHideTheTextView(a14000, layout_14000, 6, aList, bList);
//                    sportState.setText(R.string.daren);

                }
                if(70 <= progress && progress < 80)
                {
                    showAndHideTheTextView(a16000, layout_16000, 7, aList, bList);
//                    sportState.setText(R.string.daren);

                }

                if(80 <= progress && progress < 90)
                {
                    showAndHideTheTextView(a18000, layout_18000, 8, aList, bList);
//                    sportState.setText(R.string.daren);

                }

                if(90 <= progress && progress < 100)
                {
                    showAndHideTheTextView(a20000, layout_20000, 9, aList, bList);
//                    sportState.setText(R.string.daren);

                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                int progress = seekBar.getProgress();
                if( 0 <= progress && progress < 10)
                {
                    // 在手指松开后定位button的位置
                    seekBar.setProgress(maxProgress - maxProgress);
                }

                if( 10 <= progress && progress < 20)
                {
                    seekBar.setProgress(maxProgress / 9);
                }

                if( 20 <= progress && progress < 30)
                {
                    seekBar.setProgress(maxProgress / 9 * 2);

                }

                if( 30 <= progress && progress < 40)
                {
                    seekBar.setProgress(maxProgress / 9 * 3);

                }

                if( 40 <= progress && progress < 50)
                {
                    seekBar.setProgress(maxProgress / 9 * 4);

                }
                if( 50 <= progress && progress < 60)
                {
                    seekBar.setProgress(maxProgress / 9 * 5);


                }
                if(60 <= progress && progress < 70)
                {
                    seekBar.setProgress(maxProgress / 9 * 6);

                }
                if(70 <= progress && progress < 80)
                {
                    seekBar.setProgress(maxProgress / 9 * 7);

                }

                if(80 <= progress && progress < 90)
                {
                    seekBar.setProgress(maxProgress / 9 * 8);

                }

                if(90 <= progress && progress < 100)
                {
                    seekBar.setProgress(100);

                }
            }
        });






//        appCompatSeekBar.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_bg));
//        appCompatSeekBar.setThumb(getResources().getDrawable(R.drawable.sport_button));
//        appCompatSeekBar.setPadding(10, 30, 10, 30);


    }



    Toolbar.OnMenuItemClickListener menuListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId())
            {
                case R.id.target_set_ook:
//                    Toast.makeText(TargetSettingActivity.this, "李晓宁", Toast.LENGTH_SHORT).show();
                    saveTheTargetsToSP();
                    break;
            }
            return true;
        }
    };

    private void saveTheTargetsToSP()
    {
        int sportProgress = appCompatSeekBar.getProgress();
        int sleepProgress = appCompatSeekBarSleep.getProgress();

            if( 0 <= sportProgress && sportProgress < 10)
            {
                sportProgress = 2000;
            }
            if( 10 <= sportProgress && sportProgress < 20)
            {
                sportProgress = 4000;
            }
            if( 20 <= sportProgress && sportProgress < 30)
            {
                sportProgress = 6000;
            }
            if( 30 <= sportProgress && sportProgress < 40)
            {
                sportProgress = 8000;
            }
            if( 40 <= sportProgress && sportProgress < 50)
            {
                sportProgress = 10000;
            }
            if( 50 <= sportProgress && sportProgress < 60)
            {
                sportProgress = 12000;
            }
            if(60 <= sportProgress && sportProgress < 70)
            {
                sportProgress = 14000;
            }
            if(70 <= sportProgress && sportProgress < 80)
            {
                sportProgress = 16000;
            }
            if(80 <= sportProgress && sportProgress < 90)
            {
                sportProgress = 18000;
            }
            if(90 <= sportProgress && sportProgress <= 100)
            {
                sportProgress = 20000;
            }

            if(0 <= sleepProgress &&  sleepProgress < 11)
            {
                sleepProgress = 4;
            }
            if(11 <= sleepProgress && sleepProgress < 22)
            {
                sleepProgress = 5;
            }
            if(22 <= sleepProgress && sleepProgress < 33)
            {
                sleepProgress = 6;
            }
            if(33 <= sleepProgress && sleepProgress < 44)
            {
                sleepProgress = 7;
            }
            if(44 <= sleepProgress && sleepProgress <= 55)
            {
                sleepProgress = 8;
            }
            if(55 <= sleepProgress && sleepProgress <= 66)
            {
                sleepProgress = 9;
            }
            if(66 <= sleepProgress && sleepProgress <= 77)
            {
                sleepProgress = 10;
            }
            if(77 <= sleepProgress && sleepProgress <= 88)
            {
                sleepProgress = 11;
            }
            if(88 <= sleepProgress && sleepProgress <= 100)
            {
                sleepProgress = 12;
            }

        LocalDataSaveTool.getInstance(TargetSettingActivity.this)
                .writeSp(MyConfingInfo.TARGET_SETTING_VALUE, String.valueOf(sportProgress));
        LocalDataSaveTool.getInstance(TargetSettingActivity.this)
                .writeSp(MyConfingInfo.TARGET_SETTING_VALUE_SLEEP, String.valueOf(sleepProgress));
        if(BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice())
        {
            asyncTarget(sportProgress, sleepProgress * 60);
        }
        TargetSettingActivity.this.onBackPressed();


    }

    private void asyncTarget(int sportTarget, int sleepTarget)
    {
        String today = getCurrentDate();
        String yes = getBeforDay(today);
        String[] sleepData = new SleepDataHelper(getApplicationContext()).loadSleepData(yes, today, UserAccountUtil.getAccount(getApplicationContext()));
        int minu = sleepData[2].length() * 10;
        int hour = minu/60;
        int minute = minu%60;
        BleDataForTarget.getInstance().sendTargetToDevice(sportTarget, sleepTarget, hour, minute);
    }

    private String getBeforDay(String currentDate)
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        String[] eaDate = currentDate.split("-");
        calendar.set(Integer.parseInt(eaDate[0]), Integer.parseInt(eaDate[1]) - 1, Integer.parseInt(eaDate[2]) - 1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(calendar.getTime());
    }

    private String getCurrentDate()
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(calendar.getTime());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_target_ok, menu);
        return true;
    }


    private void showAndHideTheTextView(TextView a2000, LinearLayout b2000, int index, ArrayList<TextView> listA, ArrayList<LinearLayout> listB)
    {
        if(b2000.getVisibility() == View.INVISIBLE)
        {
            b2000.setVisibility(View.VISIBLE);
            a2000.setVisibility(View.INVISIBLE);
            for (int i = 0; i < listB.size(); i ++)
            {
                if(i != index)
                {
                    LinearLayout b = listB.get(i);
                    if(b.getVisibility() == View.VISIBLE && i != index)
                    {
                        b.setVisibility(View.INVISIBLE);
                    }
                }
            }
            for (int j = 0; j < listA.size(); j ++)
            {
                if(j != index)
                {
                    TextView a = listA.get(j);
                    if(a.getVisibility() == View.INVISIBLE)
                    {
                        a.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }
}
