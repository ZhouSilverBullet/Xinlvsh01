package com.huichenghe.xinlvshuju.slide.AttionModle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huichenghe.xinlvshuju.Adapter.WrapContentLinearLayoutManager;
import com.huichenghe.xinlvshuju.CustomView.CircleProgressDialog;
import com.huichenghe.xinlvshuju.CustomView.Custom_attion_sleep_view;
import com.huichenghe.xinlvshuju.CustomView.Custom_attion_step_chart;
import com.huichenghe.xinlvshuju.CustomView.Custom_circle_loving_care;
import com.huichenghe.xinlvshuju.DataEntites.Attion_sleep_data_entity;
import com.huichenghe.xinlvshuju.DataEntites.Attion_step_Entity;
import com.huichenghe.xinlvshuju.DataEntites.stepAndCalorieEntity;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.SDPathUtils;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;
import com.huichenghe.xinlvshuju.Utils.NetStatus;
import com.huichenghe.xinlvshuju.Utils.ShotScreenForShare;
import com.huichenghe.xinlvshuju.expand_activity.ShareActivity;
import com.huichenghe.xinlvshuju.http.IconTaskForNomal;
import com.huichenghe.xinlvshuju.http.LoginOnBackground;
import com.huichenghe.xinlvshuju.http.OnAllLoginBack;
import com.huichenghe.xinlvshuju.http.ReadPhotoFromSD;
import com.huichenghe.xinlvshuju.http.checkThirdPartyTask;
import com.huichenghe.xinlvshuju.http.onBitmapBack;
import com.huichenghe.xinlvshuju.mainpack.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AttionInfoActivity extends BaseActivity
{
    public static final String TAG = AttionInfoActivity.class.getSimpleName();
    private TextView reMark;
//    private ImageView fatigueProgress, fatiguePointer,
    private ImageView shareI;
//    private RelativeLayout stepHisgramLayout, sleepHisgramLayout;
//    private RecyclerView sleepLayout;
    private ImageView back;
//    private TextView dataYear;
    private AttionStepAdapter stepAdapter;
    private AttionSleepAdapter sleepAdapter;
    private ArrayList<stepAndCalorieEntity> dataList;
    private ArrayList<AttionSleepDetialEntity> sleepList;
    private ArrayList<AttionDetialEntity> allDataList;
    private RelativeLayout sleepTrendLayout, stepTrendLayout;
    private Custom_attion_step_chart attionSteps;
    private Custom_attion_sleep_view attionSleep;
    private ArrayList<Attion_step_Entity> attionStepData;
    private ArrayList<Attion_sleep_data_entity> attionSleepData;
    private Handler attionHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attion_info);
        initView();
        if(!NetStatus.isNetWorkConnected(getApplicationContext()))
        {
            MyToastUitls.showToast(AttionInfoActivity.this, R.string.net_wrong, 1);
            return;
        }
        showDialogAndSetHandler();
        String userId = getIntent().getStringExtra("dataId");
        String headIcon = getIntent().getStringExtra("headIcon");
        String remark = getIntent().getStringExtra("remark");
        String fatigue = getIntent().getStringExtra("fatigue");
        String days = getIntent().getStringExtra("days");
        getDetialData(userId);
        requesHeadIcon(headIcon);
        reMark.setText(remark);
        setFatigue(fatigue);
        setYear(days);

    }
    Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            CircleProgressDialog.getInstance().showCircleProgressDialog(AttionInfoActivity.this);
        }
    };

    Runnable closeRunnable = new Runnable() {
        @Override
        public void run() {
            CircleProgressDialog.getInstance().closeCircleProgressDialog();
        }
    };
    private void showDialogAndSetHandler()
    {
        attionHandler.postDelayed(runnable, 1000);
        attionHandler.postDelayed(closeRunnable, 3000);
    }

    private void setYear(String days)
    {
//        dataYear.setText(days.substring(0, 4));
    }

    private void setFatigue(String fatigue)
    {
        if(fatigue != null && !fatigue.equals("") && !fatigue.equals("null") && !fatigue.equals("0"))
        {
            TextView tvFagigue = (TextView) findViewById(R.id.fatigue_value_tv);
            StringBuffer buff = new StringBuffer();
            buff.append(getString(R.string.Active_zone));
            buff.append(fatigue);
            buff.append("%");
            tvFagigue.setText(buff.toString());
            Custom_circle_loving_care circle_loving_care = (Custom_circle_loving_care) findViewById(R.id.head_view_layout);
            circle_loving_care.runAnima(Integer.valueOf(fatigue) * 100 / 360);
        }
//        int poster = fatiguePointer.getWidth();
//        Log.i(TAG, "疲劳值显示：" + fatigue + "--" + poster);
//        if(fatigue != null && !fatigue.equals("") && !fatigue.equals("null") && !fatigue.equals("0"))
//        {
//            int fa = Integer.parseInt(fatigue);
//            fa = 100 - fa;
//            fatigueProgress.measure(0, 0);
//            int eachWidth = fatigueProgress.getMeasuredWidth()/ 100;
//            int offset = eachWidth * fa;
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fatiguePointer.getLayoutParams();
//            params.leftMargin = offset;
//            fatiguePointer.setLayoutParams(params);
//        }
    }

    private void requesHeadIcon(String icon)
    {
        String[] heads = icon.split("\\.");
        File file = new File(SDPathUtils.getSdcardPath() + MyConfingInfo.SDCARD_DATA_ROOT_DIR + File.separator + heads[0] + ".jpg");
        if(file.exists())
        {
            ReadPhotoFromSD readPhotoFromSD = new ReadPhotoFromSD();
            readPhotoFromSD.setOnBitmapBack(new onBitmapBack() {
                @Override
                public void onBitmapBack(Bitmap bitmap)
                {
                    Custom_circle_loving_care circle_loving_care = (Custom_circle_loving_care) findViewById(R.id.head_view_layout);
                    circle_loving_care.setInnerIconBigmap(bitmap);
                }
                @Override
                public void onBitmapError() {
                }
            });
            readPhotoFromSD.execute(file);
        }
        else
        {
            IconTaskForNomal taskForNomal = new IconTaskForNomal(AttionInfoActivity.this);
            taskForNomal.setOnBitmapBack(new onBitmapBack() {
                @Override
                public void onBitmapBack(Bitmap bitmap)
                {
                    if(bitmap != null)
                    {
                        Custom_circle_loving_care circle_loving_care = (Custom_circle_loving_care) findViewById(R.id.head_view_layout);
                        circle_loving_care.setInnerIconBigmap(bitmap);
                    }
                }
                @Override
                public void onBitmapError() {
                }
            });
            taskForNomal.execute(MyConfingInfo.WebRoot + "download_userHeader" + "?filename=" + icon, icon);
        }
    }



    private void initView()
    {
        attionStepData = new ArrayList<>();
        attionStepData.add(new Attion_step_Entity(1000, "9/4"));
        attionStepData.add(new Attion_step_Entity(900, "9/4"));
        attionStepData.add(new Attion_step_Entity(800, "9/4"));
        attionStepData.add(new Attion_step_Entity(1200, "9/4"));
        attionStepData.add(new Attion_step_Entity(1000, "9/4"));
        attionStepData.add(new Attion_step_Entity(300, "9/4"));
        attionStepData.add(new Attion_step_Entity(500, "9/4"));
        attionSteps = (Custom_attion_step_chart) findViewById(R.id.attion_step_chart);
        attionSteps.setStepData(attionStepData);

        attionSleepData = new ArrayList<>();
        attionSleepData.add(new Attion_sleep_data_entity(100, 200, 300, "9/4"));
        attionSleepData.add(new Attion_sleep_data_entity(300, 200, 100, "9/4"));
        attionSleepData.add(new Attion_sleep_data_entity(40, 20, 300, "9/4"));
        attionSleepData.add(new Attion_sleep_data_entity(100, 180, 290, "9/4"));
        attionSleepData.add(new Attion_sleep_data_entity(100, 200, 300, "9/4"));
        attionSleepData.add(new Attion_sleep_data_entity(100, 10, 370, "9/4"));
        attionSleepData.add(new Attion_sleep_data_entity(160, 230, 300, "9/4"));
        attionSleep = (Custom_attion_sleep_view) findViewById(R.id.attion_sleep_chart);
        attionSleep.setSleepData(attionSleepData);



        RadioGroup groupSelect;
        allDataList = new ArrayList<>();
//        head = (CircleImageView)findViewById(R.id.attion_head_icon);
        reMark = (TextView)findViewById(R.id.attion_remark_name);
//        fatigueProgress = (ImageView)findViewById(R.id.progress_fatigue);
//        fatiguePointer = (ImageView)findViewById(R.id.point_fatigue);
        groupSelect = (RadioGroup)findViewById(R.id.radio_group_attion_data);
        stepTrendLayout = (RelativeLayout)findViewById(R.id.layout_step_attion_data);
        sleepTrendLayout = (RelativeLayout)findViewById(R.id.layout_sleep_attion_data);
//        stepHisgramLayout = (RelativeLayout)findViewById(R.id.step_hisgram_layout);
//        sleepHisgramLayout = (RelativeLayout)findViewById(R.id.hisgram_sleep_layout);
//        stepLayout = (RecyclerView)findViewById(R.id.step_hisgram);
        setStepRecyclerView();
//        sleepLayout = (RecyclerView)findViewById(R.id.sleep_hisgram);
        setSleepRecyclerView();
        RadioButton daySelect, nightSelect;
        daySelect = (RadioButton)findViewById(R.id.day_data_select);
        daySelect.setChecked(true);
        nightSelect = (RadioButton)findViewById(R.id.night_data_select);
        groupSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch (checkedId)
                {
                    case R.id.day_data_select:
                        if(stepTrendLayout.getVisibility() == View.GONE)
                        {
                            stepTrendLayout.setVisibility(View.VISIBLE);
                        }
                        if(sleepTrendLayout.getVisibility() == View.VISIBLE)
                        {
                            sleepTrendLayout.setVisibility(View.GONE);
                        }
                        break;
                    case R.id.night_data_select:
                        if(sleepTrendLayout.getVisibility() == View.GONE)
                        {
                            sleepTrendLayout.setVisibility(View.VISIBLE);
                        }
                        if(stepTrendLayout.getVisibility() == View.VISIBLE)
                        {
                            stepTrendLayout.setVisibility(View.GONE);
                        }
                        break;
                }
            }
        });
//        dataYear = (TextView)findViewById(R.id.data_year);
        shareI = (ImageView)findViewById(R.id.share_attion);
        back = (ImageView)findViewById(R.id.back_to_attion_list);
        back.setOnClickListener(listernt);
        shareI.setOnClickListener(listernt);
    }

    NoDoubleClickListener listernt = new NoDoubleClickListener()
    {
        @Override
        public void onNoDoubleClick(View v)
        {
            if(v == back)
            {
                AttionInfoActivity.this.onBackPressed();
            }
            else if(v == shareI)
            {
                ShotScreenForShare.getInstance().takeshotScreen(AttionInfoActivity.this);
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(AttionInfoActivity.this, ShareActivity.class);
                startActivity(intent);
            }
        }
    };

    private void setSleepRecyclerView()
    {
        sleepList = new ArrayList<>();
//        sleepList.add(new AttionSleepDetialEntity("23/45", 100, 800, 400, 1000));
//        sleepList.add(new AttionSleepDetialEntity("23/45", 900, 100, 200, 1000));
        sleepAdapter = new AttionSleepAdapter(AttionInfoActivity.this, sleepList);
        LinearLayoutManager ma = new WrapContentLinearLayoutManager(AttionInfoActivity.this);
        ma.setOrientation(LinearLayoutManager.HORIZONTAL);
//        sleepLayout.setLayoutManager(ma);
//        sleepLayout.setItemAnimator(new DefaultItemAnimator());
//        sleepLayout.setHasFixedSize(true);
//        sleepLayout.setAdapter(sleepAdapter);
    }

    private void setStepRecyclerView()
    {
        int colores = AttionInfoActivity.this.getResources().getColor(R.color.step_histogram);
        dataList = new ArrayList<>();
        stepAdapter = new AttionStepAdapter(AttionInfoActivity.this, dataList, colores);
//        stepLayout.setItemAnimator(new DefaultItemAnimator());
//        stepLayout.setHasFixedSize(true);
        LinearLayoutManager manager = new WrapContentLinearLayoutManager(AttionInfoActivity.this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        stepLayout.setLayoutManager(manager);
//        stepLayout.setAdapter(stepAdapter);
    }

    /**
     * 获取详细数据
     * @param userId
     */
    private void getDetialData(String userId)
    {
        getAccountAndParse(userId);
    }


    public void requestDetail(String userId)
    {
        RequsetDetialData detial = new RequsetDetialData(AttionInfoActivity.this);
        detial.setAddAcctionCallback(new AddAcctionCallback()
        {
            @Override
            public void onFailed() {

            }
            @Override
            public void onSuccess(String code)
            {
                attionHandler.removeCallbacks(runnable);
                attionHandler.removeCallbacks(closeRunnable);
                CircleProgressDialog.getInstance().closeCircleProgressDialog();
//                Log.i(TAG, "详细情亲关注数据" + code);
                parseTheResponse(code);
            }
        });
        detial.execute(userId);
    }

    private void getAccountAndParse(final String userId)
    {
        String userType = null;
        String account = null;
        String userAcc = LocalDataSaveTool.getInstance(AttionInfoActivity.this).readSp(MyConfingInfo.USER_ACCOUNT);
        try {
            JSONObject json = new JSONObject(userAcc);
            userType = json.getString(MyConfingInfo.TYPE);
            account = json.getString(MyConfingInfo.ACCOUNT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (userType)
        {
            case MyConfingInfo.QQ_TYPE:
            case MyConfingInfo.WEICHART_TYPE:
            case MyConfingInfo.FACEBOOK_TYPE:
                checkThirdPartyTask thirdTask = new checkThirdPartyTask();
                thirdTask.setOnLoginBackListener(new OnAllLoginBack()
                {
                    @Override
                    public void onLoginBack(String re)
                    {
                        requestDetail(userId);
                    }
                });
                thirdTask.execute(account.split(";")[0], userType, null, null, null);
                break;
            case MyConfingInfo.NOMAL_TYPE:
                LoginOnBackground loginTask = new LoginOnBackground(AttionInfoActivity.this);
                loginTask.setOnLoginBackListener(new OnAllLoginBack()
                {
                    @Override
                    public void onLoginBack(String re)
                    {
                        requestDetail(userId);
                    }
                });
                loginTask.execute();
                break;
        }
    }

    private void parseTheResponse(String code)
    {
        String coder = null;
        String data = null;
        try {
            JSONObject json = new JSONObject(code);
            coder = json.getString("code");
            data = json.getString("data");
            if(coder.equals("9003"))
            {
                if(data != null && !data.equals(""))
                {
                    int maxStep = 0;
                    int maxSleep = 0;
                    JSONArray jsonArray = new JSONArray(data);
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject obj = (JSONObject)jsonArray.get(i);
                        String day = obj.getString("day");
                        int step = obj.getInt("step");
                        int sleepDept = obj.getInt("sleepDept");
                        int sleepLight = obj.getInt("sleepLight");
                        int sleepAweek = obj.getInt("sleepAweek");
                        if(step > maxStep)
                        {
                            maxStep = step;
                        }
                        if(sleepDept > maxSleep)
                        {
                            maxSleep = sleepDept;
                        }
                        if(sleepLight > maxSleep)
                        {
                            maxSleep = sleepLight;
                        }
//                        if(sleepAweek > maxSleep)
//                        {
//                            maxSleep = sleepAweek;
//                        }
                        allDataList.add(new AttionDetialEntity(day, step, sleepDept, sleepLight, sleepAweek));
                    }
                    setToAdapter(allDataList, maxStep, maxSleep);
                }
                else
                {
                    return;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private void setToAdapter(ArrayList<AttionDetialEntity> allDataList, int maxStep, int maxSleep)
    {
        int dataLength = 6;
        boolean isCreate = false;
        for (int i = dataLength; i >= 0; i--)
        {
            String theDay = getTodayFormatDate(i);
            for (int j = allDataList.size() - 1; j >= 0; j--)
            {
                AttionDetialEntity entityAttion = allDataList.get(j);
                String day = entityAttion.getDay();
                String patten = "yyyy-MM-dd";
                SimpleDateFormat format = new SimpleDateFormat(patten, Locale.getDefault());
                Date date = null;
                try {
                    date = format.parse(day);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat formates = new SimpleDateFormat("MM/dd", Locale.getDefault());
                String days = formates.format(date);
                Log.i(TAG, "关注时间对比：" + days + "--" + theDay);
                if(days != null && days.equals(theDay))
                {
                    String sleepDay = getTodayString(theDay, true);
                    theDay = getTodayString(theDay, false);
                    stepAdapter.addItem(new stepAndCalorieEntity(theDay, entityAttion.getStep(), maxStep), dataList.size());
                    sleepAdapter.setData(new AttionSleepDetialEntity(sleepDay,
                            entityAttion.getSleepDeep(),
                            entityAttion.getSleepLight(),
                            entityAttion.getSleepAweek(),
                            maxSleep), sleepList.size());
                    isCreate = true;
                    break;
                }
            }
            if(!isCreate)
            {
                String sleepDay = getTodayString(theDay, true);
                theDay = getTodayString(theDay, false);
                stepAdapter.addItem(new stepAndCalorieEntity(theDay, 0, maxStep), dataList.size());
                sleepAdapter.setData(new AttionSleepDetialEntity(sleepDay, 0, 0, 0, maxSleep), sleepList.size());
            }
            isCreate = false;
        }
    }

    private String getTodayString(String theDay, boolean isNight)
    {
        if(theDay.equals(getTodayFormatDate(0)))
        {
            if(isNight)
            {
                return getString(R.string.yestoday);
            }
            else
            {
                return getString(R.string.today);
            }
        }
        else
        {
            return theDay;
        }
    }

    private String getTodayFormatDate(int i)
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - i);
        SimpleDateFormat format = new SimpleDateFormat("MM/dd", Locale.getDefault());
        return format.format(calendar.getTime());
    }

}
