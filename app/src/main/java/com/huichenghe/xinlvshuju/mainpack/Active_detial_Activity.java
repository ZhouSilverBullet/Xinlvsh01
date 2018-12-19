package com.huichenghe.xinlvshuju.mainpack;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huichenghe.bleControl.Ble.BleDataForDayData;
import com.huichenghe.bleControl.Ble.BleDataForDayHeartReatData;
import com.huichenghe.bleControl.Ble.BleDataForEachHourData;
import com.huichenghe.bleControl.Ble.BleForGetFatigueData;
import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.bleControl.Ble.DataSendCallback;
import com.huichenghe.xinlvshuju.BleDeal.BleEachDataDealer;
import com.huichenghe.xinlvshuju.BleDeal.BleFatigueDataDealer;
import com.huichenghe.xinlvshuju.BleDeal.DayDataDealer;
import com.huichenghe.xinlvshuju.BleDeal.HRDataDealer;
import com.huichenghe.xinlvshuju.CustomView.CircleProgressDialog;
import com.huichenghe.xinlvshuju.CustomView.CustomDateSelector;
import com.huichenghe.xinlvshuju.CustomView.Custom_hisgram_view;
import com.huichenghe.xinlvshuju.CustomView.HeartReatDetailsView;
import com.huichenghe.xinlvshuju.CustomView.fatigueDetialView;
import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.EachLoginHelper;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.UpdataService.DeviceTypeUtils;
import com.huichenghe.xinlvshuju.UpdataService.SendDataCallback;
import com.huichenghe.xinlvshuju.UpdataService.UpdateHistoryDataService;
import com.huichenghe.xinlvshuju.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;
import com.huichenghe.xinlvshuju.Utils.NetStatus;
import com.huichenghe.xinlvshuju.Utils.ShotScreenForShare;
import com.huichenghe.xinlvshuju.expand_activity.ShareActivity;
import com.huichenghe.xinlvshuju.expand_activity.Treads.TrendActivity;
import com.huichenghe.xinlvshuju.http.LoginOnBackground;
import com.huichenghe.xinlvshuju.http.OnAllLoginBack;
import com.huichenghe.xinlvshuju.http.UserAccountUtil;
import com.huichenghe.xinlvshuju.http.checkThirdPartyTask;
import com.huichenghe.xinlvshuju.slide.AttionModle.UpdateFatigueTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 第一个界面的详情页
 */
public class Active_detial_Activity extends BaseActivity
{
    public static final String TAG = "Active_detial_Activity";
    private String currentDate;
    private Custom_hisgram_view stepView, kcalView;
    private int data[] = null;
    private TextView maxHR, averageHR, activeState, movementState, active_h, active_m, active_kcal, sit_h, sit_m, sit_kcal;
    private HeartReatDetailsView HRView;
    private fatigueDetialView fatigueView;
    private RelativeLayout stepEachLayout;
    private RelativeLayout kcalEachLayout;
    private HorizontalScrollView hr_view;
    private TextView titleDate;
    private SwipeRefreshLayout refreshLayout;
    private final int UPLOAD_FATIGUE_VALUE = 5;

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(final Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case UPLOAD_FATIGUE_VALUE:
                    String[] accountInfo = getLoginType();
                    switch (accountInfo[0])
                    {
                        case MyConfingInfo.QQ_TYPE:
                        case MyConfingInfo.WEICHART_TYPE:
                        case MyConfingInfo.FACEBOOK_TYPE:
                            if(NetStatus.isNetWorkConnected(getApplicationContext()))
                            {
                                checkThirdPartyTask tasks = new checkThirdPartyTask(getApplicationContext());
                                tasks.setOnLoginBackListener(new OnAllLoginBack() {
                                    @Override
                                    public void onLoginBack(String re)
                                    {
                                        new UpdateFatigueTask(getApplicationContext()).execute(msg.arg1);
                                    }
                                });
                                tasks.executeOnExecutor(MyApplication.threadService, accountInfo[1].split(";")[0], accountInfo[0], null, null, null);
                            }
                            break;
                        case MyConfingInfo.NOMAL_TYPE:
                            if(NetStatus.isNetWorkConnected(getApplicationContext()))
                            {
                                LoginOnBackground loginTask = new LoginOnBackground(Active_detial_Activity.this);
                                loginTask.setOnLoginBackListener(new OnAllLoginBack() {
                                    @Override
                                    public void onLoginBack(String re)
                                    {
                                        new UpdateFatigueTask(getApplicationContext()).execute(msg.arg1);
                                    }
                                });
                                loginTask.executeOnExecutor(MyApplication.threadService);
                            }
                            break;
                    }
                break;
                case 0:
                getDayData();
                break;
                case 1:
                    getEachStepAndColires();
                    break;
                case 2:
                    getFatigueData();
                    break;
                case 3:
                    new hrTask().start();
                    break;
                case 4:
                    LoadingTheAllData(currentDate);
                    this.sendEmptyMessageDelayed(6, 0);
                    break;
                case 6:
                    refreshLayout.setRefreshing(false);
                    break;
            }
        }
    };

    private void getFatigueData()
    {
        BleForGetFatigueData.getInstance(getApplicationContext()).setDataSendCallback(new DataSendCallback() {
            @Override
            public void sendSuccess(byte[] receveData)
            {
                new BleFatigueDataDealer(receveData, Active_detial_Activity.this.getApplicationContext());
            }
            @Override
            public void sendFailed() {}
            @Override
            public void sendFinished() {
//                handler.sendEmptyMessageDelayed(3, 600);
            }
        });
        BleForGetFatigueData.getInstance(getApplicationContext()).getFatigueDayData();
    }

    private void getEachStepAndColires()
    {
        BleDataForEachHourData.getEachHourDataInstance().setOnBleDataReceListener(new DataSendCallback() {
            @Override
            public void sendSuccess(byte[] receveData)
            {
                new BleEachDataDealer(Active_detial_Activity.this, receveData);
            }
            @Override
            public void sendFailed() {}
            @Override
            public void sendFinished() {
//                handler.sendEmptyMessageDelayed(2, 600);
            }
        });
        BleDataForEachHourData.getEachHourDataInstance().getEachData();
    }

    private void getDayData()
    {
        BleDataForDayData.getDayDataInstance(getApplicationContext()).setOnDayDataListener(new DataSendCallback() {
            @Override
            public void sendSuccess(byte[] receveData)
            {
                new DayDataDealer(Active_detial_Activity.this, receveData);
            }
            @Override
            public void sendFailed() {}
            @Override
            public void sendFinished()
            {
//                handler.sendEmptyMessageDelayed(1, 600);
            }
        });
        BleDataForDayData.getDayDataInstance(getApplicationContext()).getDayData();
    }

    class hrTask extends Thread
    {
        @Override
        public void run()
        {
            BleDataForDayHeartReatData.getHRDataInstance(getApplicationContext()).setOnHrDataRecever(new DataSendCallback() {
                @Override
                public void sendSuccess(byte[] receveData)
                {
                    new HRDataDealer(receveData, Active_detial_Activity.this.getApplicationContext());
                }
                @Override
                public void sendFailed(){}
                @Override
                public void sendFinished() {
                    handler.sendEmptyMessageDelayed(4, 0);
                }
            });
            BleDataForDayHeartReatData.getHRDataInstance(getApplicationContext()).requestHeartReatDataAll();
        }
    }

    private String[] getLoginType()
    {
        String[] acc = new String[2];
        String account = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.USER_ACCOUNT);
        try {
            JSONObject json = new JSONObject(account);
            acc[0] = json.getString(MyConfingInfo.TYPE);
            acc[1] = json.getString(MyConfingInfo.ACCOUNT);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return acc;
    }
//todo oncreate
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_movement);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        currentDate = getIntent().getStringExtra("currentDate");
        intitView();
        initTitleBar();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        loadCount = 0;
        LoadingTheAllData(currentDate);
//        if(BluetoothLeService.getInstance() != null && !BluetoothLeService.getInstance().isConnectedDevice())
//        {
//            getCurrentRemoteDate(currentDate);
//        }
    }

    private void getCurrentRemoteDate(String currentDatee)
    {
        if(UpdateHistoryDataService.getInstance() != null)
        {
            if(!NetStatus.isNetWorkConnected(getApplicationContext()))
            {
                MyToastUitls.showToast(Active_detial_Activity.this, R.string.net_wrong, 1);
                CircleProgressDialog.getInstance().closeCircleProgressDialog();
                return;
            }
            Log.i("one_detail","从服务器上下载数据");
//            if(!canDoContinue())return;
//                CircleProgressDialog.getInstance().showCircleProgressDialog(Active_detial_Activity.this);
//                String account = UserAccountUtil.getAccount(getApplicationContext());
//                UpdateHistoryDataService.getInstance().getDateStepsData(currentDatee, account, RequestCallback);
//                UpdateHistoryDataService.getInstance().getDateHRData(currentDatee, account, RequestCallback);
//                UpdateHistoryDataService.getInstance().getDateCaloriesData(currentDatee, account, RequestCallback);
//                UpdateHistoryDataService.getInstance().getDateHRVData(currentDatee, account, RequestCallback);
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            LoadingTheAllData(currentDate);
            CircleProgressDialog.getInstance().closeCircleProgressDialog();
        }
    };


    SendDataCallback RequestCallback = new SendDataCallback()
    {
        @Override
        public void sendDataSuccess(String reslult)
        {
            if(!canDoContinue())return;
            Log.i("updateData", "请求的数据" + reslult);
            try {
                JSONObject json = new JSONObject(reslult);
                String code = json.getString("code");
                String data = json.getString("data");
                if(data != null && data.equals(""))
                {
                    CircleProgressDialog.getInstance().closeCircleProgressDialog();
                    return;
                }
                if(code != null && code.equals("9003"))
                {
                    if(!canDoContinue())return;
                    String msg = json.getString("msg");
                    if(msg != null && msg.contains("stepDay"))
                    {
                        new BleEachDataDealer(getApplicationContext(), data, 0);
                    }
                    else if(msg != null && msg.contains("heartRate"))
                    {
                        new HRDataDealer(getApplicationContext(), data);
                    }
                    else if(msg != null && msg.contains("calorieDay"))
                    {
                        new BleEachDataDealer(getApplicationContext(), data, 1);
                    }
                    else if(msg != null && msg.contains("hrv"))
                    {
                        new BleFatigueDataDealer(getApplicationContext(), data);
                        handler.postDelayed(runnable, 1000);
                    }
                }
                else if(code != null && code.equals("9001"))
                {
                    if(!canDoContinue())return;
                    String msg = json.getString("msg");
                    if(msg.contains("未登录"))
                    {
                        new EachLoginHelper(getApplicationContext(), new SendDataCallback()
                        {
                            @Override
                            public void sendDataSuccess(String reslult){}
                            @Override
                            public void sendDataFailed(String result){}
                            @Override
                            public void sendDataTimeOut(){}
                        });
                    }
                }
                else if(code != null && code.equals("9004"))
                {
                    if(!canDoContinue())return;
                    CircleProgressDialog.getInstance().closeCircleProgressDialog();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void sendDataFailed(String result)
        {
            if(!canDoContinue())return;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MyToastUitls.showToast(Active_detial_Activity.this, R.string.net_wrong, 1);
                    CircleProgressDialog.getInstance().closeCircleProgressDialog();
                }
            });
        }
        @Override
        public void sendDataTimeOut() {}
    };

    private void initTitleBar()
    {
        findViewById(R.id.close_the_detail).setOnClickListener(clickListener);
        findViewById(R.id.trend_button).setOnClickListener(clickListener);
        findViewById(R.id.share_button).setOnClickListener(clickListener);
        findViewById(R.id.provios_data).setOnClickListener(clickListener);
        findViewById(R.id.next_data).setOnClickListener(clickListener);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        int screeHe = getApplicationContext().getResources().getDisplayMetrics().heightPixels;
        refreshLayout.setProgressViewOffset(false, -100, screeHe/10);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                String dayes = getTodayDate();
                Log.i(TAG, "时间对比:" + dayes + "--" + currentDate);
                if(currentDate != null && !currentDate.equals(""))
                {
                    if(currentDate.equals(dayes))
                    {
                        if(BluetoothLeService.getInstance() != null && !BluetoothLeService.getInstance().isConnectedDevice())
                        {
//                            MyToastUitls.showToast(Active_detial_Activity.this
//                                                   ,R.string.not_connecte, 1);
                            handler.sendEmptyMessageDelayed(6, 0);
                            return;
                        }
                        handler.sendEmptyMessage(0);
                        handler.sendEmptyMessage(1);
                        handler.sendEmptyMessage(2);
                        handler.sendEmptyMessage(3);
                    }
                    else
                    {
//                        getCurrentRemoteDate(currentDate);
                        handler.sendEmptyMessageDelayed(6, 0);
                    }
                }
            }});
        titleDate = (TextView) findViewById(R.id.day_select);
        titleDate.setText(compareTheDate(currentDate));
        titleDate.setOnClickListener(clickListener);
        maxHR = (TextView)findViewById(R.id.max_hr);
        averageHR = (TextView)findViewById(R.id.average_hr);
        HRView = (HeartReatDetailsView) findViewById(R.id.daytime_hr);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) HRView.getLayoutParams();
        params.width = this.getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        HRView.setLayoutParams(params);
        fatigueView = (fatigueDetialView) findViewById(R.id.fatigue_layout);
        hr_view = (HorizontalScrollView)findViewById(R.id.hr_layout);
        activeState = (TextView)findViewById(R.id.active_state_ment);
        movementState = (TextView)findViewById(R.id.kcal_movement_state);
        active_h = (TextView)findViewById(R.id.moveTime_h);
        active_m = (TextView)findViewById(R.id.moveTime_m);
        active_kcal = (TextView)findViewById(R.id.moveCalorie);
        sit_h = (TextView)findViewById(R.id.sitTime_h);
        sit_m = (TextView)findViewById(R.id.sitTime_m);
        sit_kcal = (TextView)findViewById(R.id.sitCalorie);
    }



    private void intitView()
    {
        stepView = (Custom_hisgram_view) findViewById(R.id.stpe_show_each_hour);
        kcalView = (Custom_hisgram_view) findViewById(R.id.kcal_show_each_hour);
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.content_select);
        RadioButton stepRadio = (RadioButton)findViewById(R.id.step_select_button);
        RadioButton kcalRadio = (RadioButton)findViewById(R.id.kcal_select_button);
        RadioButton heartRadio = (RadioButton)findViewById(R.id.radio_hr);
        RadioGroup hrvGroup = (RadioGroup)findViewById(R.id.radio_select_group);
        heartRadio.setChecked(true);
        stepEachLayout = (RelativeLayout)findViewById(R.id.step_each_layout);
        kcalEachLayout = (RelativeLayout)findViewById(R.id.kcal_each_layout);
        stepRadio.setChecked(true);
        radioGroup.setOnCheckedChangeListener(checkListener);
        hrvGroup.setOnCheckedChangeListener(checkListener);
    }


    RadioGroup.OnCheckedChangeListener checkListener = new RadioGroup.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId)
        {
            switch (checkedId)
            {
                case R.id.step_select_button:
                    if(stepEachLayout.getVisibility() == View.GONE)
                    {
                        stepEachLayout.setVisibility(View.VISIBLE);
                    }
                    if(kcalEachLayout.getVisibility() == View.VISIBLE)
                    {
                        kcalEachLayout.setVisibility(View.GONE);
                    }

                    break;
                case R.id.kcal_select_button:
                    if(kcalEachLayout.getVisibility() == View.GONE)
                    {
                        kcalEachLayout.setVisibility(View.VISIBLE);
                    }
                    if(stepEachLayout.getVisibility() == View.VISIBLE)
                    {
                        stepEachLayout.setVisibility(View.GONE);
                    }
                    break;
                case R.id.radio_hr:
                    if(hr_view.getVisibility() == View.GONE)
                    {
                        hr_view.setVisibility(View.VISIBLE);
                    }
                    if(fatigueView.getVisibility() == View.VISIBLE)
                    {
                        fatigueView.setVisibility(View.GONE);
                    }
                    break;
                case R.id.radio_fatigue:
                    if(hr_view.getVisibility() == View.VISIBLE)
                    {
                        hr_view.setVisibility(View.GONE);
                    }
                    if(fatigueView.getVisibility() == View.GONE)
                    {
                        fatigueView.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };

    /**
     * 从本地数据库中取得数据然后显示在ui 上；
     * @param date
     */
    private void LoadingTheAllData(String date){
        this.currentDate = date;
        String userAccount = UserAccountUtil.getAccount(getApplicationContext());
        GetDataTask stepTask = new GetDataTask();
        stepTask.execute(date, userAccount);
        new GetCalorieTask().execute(date, userAccount);
        Cursor cursor1 = MyDBHelperForDayData.getInstance(getApplicationContext()).selecteDayData(getApplicationContext(), userAccount, date, DeviceTypeUtils.getDeviceType(getApplicationContext()));
        parserTheCursor(cursor1);
    }

    private void parserTheCursor(Cursor cursor1)
    {
        if(cursor1.getCount() > 0)
        {
            if(cursor1.moveToFirst())
            {
                do {
                    String movementTime = cursor1.getString(cursor1.getColumnIndex("movementTime"));
                    String sitTime = cursor1.getString(cursor1.getColumnIndex("sitTime"));
                    final int moveCalorie = cursor1.getInt(cursor1.getColumnIndex("moveCalorie"));
                    final int sitCalorie = cursor1.getInt(cursor1.getColumnIndex("sitCalorie"));
                    int moveTi = Integer.parseInt(movementTime);
                    int sitTi = Integer.parseInt(sitTime);
                    final int hourM = moveTi / 60;
                    final int minuteM = moveTi % 60;
                    final int hourS = sitTi / 60;
                    final int minuteS = sitTi % 60;
                    movementTime = hourM + "h" + minuteM + "'";
                    sitTime = hourS + "h" + minuteS + "'";
                    final String finalSitTime = sitTime;
                    final String finalMovementTime = movementTime;
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            active_h.setText(String.valueOf(hourM));
                            active_m.setText(String.valueOf(minuteM));
                            active_kcal.setText(String.valueOf(moveCalorie));
                            sit_h.setText(String.valueOf(hourS));
                            sit_m.setText(String.valueOf(minuteS));
                            sit_kcal.setText(String.valueOf(sitCalorie));
                        }
                    });
                }while (cursor1.moveToNext());
                cursor1.close();
            }
        }
        else
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    active_h.setText(String.valueOf(0));
                    active_m.setText(String.valueOf(0));
                    active_kcal.setText(String.valueOf(0));
                    sit_h.setText(String.valueOf(0));
                    sit_m.setText(String.valueOf(0));
                    sit_kcal.setText(String.valueOf(0));
                }
            });
        }
    }


    class GetCalorieTask extends AsyncTask<String, Void, Boolean>
    {
        protected Cursor cursor;
        private int[] eachCalored;
        private byte[] fatigue;
        int fatigueOne = 0;
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(String... params)
        {
            String dates = params[0];
            Cursor mmCursor1 = MyDBHelperForDayData.getInstance(getApplicationContext()).getEachHourCalorieData(getApplicationContext(), params[1], dates, DeviceTypeUtils.getDeviceType(getApplicationContext()));
            if(mmCursor1.getCount() >= 0)
            {
                eachCalored = parserTheCursorEachDate(mmCursor1);
            }

            Cursor mFatigueCursor = MyDBHelperForDayData.getInstance(getApplicationContext()).selectFatigueData(getApplicationContext(), params[1], dates, DeviceTypeUtils.getDeviceType(getApplicationContext()));
            if(mFatigueCursor.getCount() > 0)
            {
                fatigue = parserTheFatigue(mFatigueCursor);
                fatigueOne = getOneFatigueValue(fatigue);
                Log.i(TAG, "遍历疲劳值：" + fatigueOne);
                if(dates.equals(getTodayDate()))
                {
                    Message msg = Message.obtain();
                    msg.what = UPLOAD_FATIGUE_VALUE;
                    msg.arg1 = fatigueOne;
                    handler.sendMessage(msg);
                }
            }
            return true;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            if(aBoolean)
            {
                if(eachCalored != null && eachCalored.length > 0)
                {
                    updateTheEachStep(eachCalored, kcalView, false);
                }
                else
                {
                    kcalView.setData(null, null);
                }
                Log.i(TAG, "显示的疲劳数据：" + FormatUtils.bytesToHexString(fatigue));
//                updateFatigueShow(fatigue);
                fatigueView.setFatigueData(fatigue);
            }
        }
    }

    private String getTodayDate()
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(calendar.getTime());
    }

    private byte[] parserTheFatigue(Cursor mFatigueCursor)
    {
        String fatigue = "";
        if(mFatigueCursor.getCount() > 0)
        {
            if(mFatigueCursor.moveToFirst())
            {
                do {
                    fatigue = mFatigueCursor.getString(mFatigueCursor.getColumnIndex("fatigue"));
                }
                while (mFatigueCursor.moveToNext());
            }
        }
        mFatigueCursor.close();
        return FormatUtils.hexString2ByteArray(fatigue);
    }


    private int getOneFatigueValue(byte[] fatigue)
    {
        int onlyValue = 0;
        for (int i = fatigue.length - 1; i >= 0; i--)
        {
            int res = fatigue[i] & 0xff;
//            Log.i(TAG, "遍历疲劳值：" + res);
            if(res == 0 || res == 255)
            {
            }
            else
            {
                onlyValue = res;
                break;
            }

        }
        return onlyValue;
    }

    /**
     * 获取分时计步数据
     */
    class GetDataTask extends AsyncTask<String, Void, Boolean>
    {
        protected Cursor cursor;
        private ArrayList<Integer> timees;
        private String dates, userAccount;
        String hex = null;
        private int[] HRData;
        private int[] eachHourStep;
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
//            stepEntityList.clear();
//            mHistogramAdapter.notifyDataSetChanged();
        }
        @Override
        protected Boolean doInBackground(String... params)
        {
            dates = params[0];
            userAccount = params[1];
//            String userAccount = LocalDataSaveTool.getInstance(mContext).readSp(MyConfingInfo.USER_ACCOUNT);
            // 查询分时计步数据
            cursor = MyDBHelperForDayData.getInstance(getApplicationContext())
                    .getEachHourData(getApplicationContext(), userAccount, dates, DeviceTypeUtils.getDeviceType(getApplicationContext()));
            if(cursor.getCount() <= 0)
                return false;
            eachHourStep = parserTheCursorEachDate(cursor);
            return true;
        }


        /**
         * 通过分时计步的开始时间，确定心率曲线的开始时间
         * @param aBoolean
         */
        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            if(aBoolean)
            {
                ArrayList<Integer> timees = updateTheEachStep(eachHourStep, stepView, true);
                hex = getTheHrDataCurrentDay(dates, userAccount, timees);
//                Log.i(TAG, "eachHourStep:" + eachHourStep.length);
                HRData = getTheMaxHRAndAverageHR(hex);
                StringBuffer bu = new StringBuffer();
                StringBuffer buAverage = new StringBuffer();
                bu.append(getString(R.string.height_heartreat));
                bu.append(((HRData[0] == 0) ? "--" : HRData[0]));
                buAverage.append(getString(R.string.average_heartreat));
                buAverage.append(((HRData[1] == 0) ? "--" : HRData[1]));
                maxHR.setText(bu.toString());
                averageHR.setText(buAverage.toString());
                String[] nameX = new String[timees.size()];
                for (int i = 0; i < timees.size(); i ++)
                {
                    nameX[i] = timees.get(i) + ":00";
                }
                float density = HRView.getContext().getResources().getDisplayMetrics().density;
                float dens = 1.0f;
                if( density > 1.5 && density <= 2.0)
                {
                    dens = 1.0f;
                }
                else if(density <= 1.5)
                {
                    dens = 0.85f;
                }
                else if(density > 2.0 && density <= 3.0)
                {
                    dens = 1.4f;
                }
                else
                {
                    dens = 1.7f;
                }

                int hrWidth = (int)((hex.length()/2) * dens);
                int displayPx = getResources().getDisplayMetrics().widthPixels;
                Log.i(TAG, "自定义控件宽度：" + hrWidth + "----" + "屏幕宽度：" + displayPx);
                if(hrWidth > displayPx)
                {
                    RelativeLayout.LayoutParams paramss = (RelativeLayout.LayoutParams) HRView.getLayoutParams();
                    paramss.width = hrWidth;
                    HRView.setLayoutParams(paramss);
                }
                else
                {
                    RelativeLayout.LayoutParams pa = (RelativeLayout.LayoutParams) HRView.getLayoutParams();
                    pa.width = displayPx;
                    HRView.setLayoutParams(pa);
                }
                HRView.setData(nameX, hex, 220);
                HRView.invalidate();
            }
            else
            {
                if(currentDate != null && !currentDate.equals(getTodayDate()))
                {
                    if(loadCount == 0)
                    {
                        loadCount++;
                        getCurrentRemoteDate(currentDate);
                    }
                }
                else
                {
                    if(BluetoothLeService.getInstance() != null && !BluetoothLeService.getInstance().isConnectedDevice())
                    {
                        if(loadCount == 0)
                        {
                            loadCount++;
                            getCurrentRemoteDate(currentDate);
                        }
                    }
                }
                stepView.setData(null, null);
                HRView.setData(null, "", 220);
                HRView.invalidate();
                maxHR.setText(getString(R.string.height_heartreat) +  "--");
                averageHR.setText(getString(R.string.average_heartreat) + "--");
            }
        }
    }

    private int loadCount = 0;

    private int[] getTheMaxHRAndAverageHR(String hex)
    {
        Log.i(TAG, "心率数据:" + hex);
        int maxH = 0;
        int numberAnd = 0;
        int count = 1;
        int average = 0;
        byte[] bytes =  FormatUtils.hexString2ByteArray(hex);
        for (int i = 0; i < bytes.length; i++)
        {
            if(bytes[i] == (byte)0xff || bytes[i] == (byte)0x00)
            {
                continue;
            }
            count ++;
            int eachData = bytes[i] & 0xff;
            numberAnd += eachData;
            if(eachData > maxH)
            {
                maxH = eachData;
            }
        }
        average = numberAnd/count;
//        final int finalMaxH = maxH;
//        final int finalAverage = average;
//        ((Detail_activity)mContext).runOnUiThread(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                maxHR.setText(getString(R.string.height_heartreat) + String.valueOf(finalMaxH));
//                averageHR.setText(getString(R.string.average_heartreat) + String.valueOf(finalAverage));
//            }
//        });
        return new int[]{maxH, average};
    }

    /**
     * 查询数据库当天心率数据
     */
    private String getTheHrDataCurrentDay(String date, String userAccount, ArrayList<Integer> timesEach)
    {
        Log.i(TAG, "查询心率getTheHrDataCurrentDay-" + timesEach.size());
        String dataS = "";
        String[] Culums = new String[]{"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
                "one1", "two1", "three1", "four1", "five1", "six1", "seven1", "eight1", "nine1", "ten1",
                "one2", "two2", "three2", "four2"};
        for (Integer cus : timesEach)
        {
            Log.i(TAG, "当天心率数据：" + cus);
            String culum = Culums[cus];
            Cursor mCursor = MyDBHelperForDayData.getInstance(getApplicationContext()).selectHRFromCulums(getApplicationContext(), userAccount, date, culum, DeviceTypeUtils.getDeviceType(getApplicationContext()));
            if(mCursor.getCount() != 0)
            {
                if (mCursor.moveToFirst())
                {
                    do
                    {
                        String oneHr =  mCursor.getString(mCursor.getColumnIndex(culum));
                        if(oneHr == null)
                        {
                            oneHr = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
                        }
                        dataS += oneHr;

                    }while (mCursor.moveToNext());
                }
                mCursor.close();
            }
        }
        return dataS;
    }


    private ArrayList<Integer> updateTheEachStep(int[] eachHourStep, Custom_hisgram_view conView, boolean isStepCount)
    {
        ArrayList<Integer> times = new ArrayList<>();
        ArrayList<Integer> showSteps = new ArrayList<>();
        ArrayList<String> showStepTimes = new ArrayList<>();

        int[] stepAllCount = {0};
//        int bigMax = getTheMaxInt(eachHourStep);
//                    dataLists.clear();
//                    adapter.notifyDataSetChanged();
        for (int i = 0; i < eachHourStep.length; i++)
        {
            if (eachHourStep[i] == -1)
            {
                continue;
            }
            stepAllCount[0] += eachHourStep[i];
            if (eachHourStep[i] > 0)
            {
//                Log.i(TAG, "分时计步数据:" + i + ":00" + "---" + steps[i] + "--" + bigMax);
//                dataLists.add(new stepAndCalorieEntity(i + ":00", steps[i], bigMax));
//                mHistogramAdapter.addItem(new stepAndCalorieEntity(i + ":00", eachHourStep[i], bigMax), stepEntityList.size());
                showSteps.add(eachHourStep[i]);
                showStepTimes.add(i + ":00");
                times.add(i);
            }
            else if (eachHourStep[i] == 0)
            {
                if (stepAllCount[0] != 0)
                {
//                    Log.i(TAG, "分时计步数据0步数:" + i + ":00" + "---" + steps[i] + "--" + bigMax);
//                    dataLists.add(new stepAndCalorieEntity(i + ":00", steps[i], bigMax));
//                    mHistogramAdapter.addItem(new stepAndCalorieEntity(i + ":00", eachHourStep[i], bigMax), stepEntityList.size());
                    showSteps.add(eachHourStep[i]);
                    showStepTimes.add(i + ":00");
                    times.add(i);
                }
            }
        }
        int[] steps = new int[showSteps.size()];
        String[] stepTimes = new String[showSteps.size()];
        for (int i = 0; i < showSteps.size(); i++)
        {
            steps[i] = showSteps.get(i);
            stepTimes[i] = showStepTimes.get(i);
//            Log.i(TAG, "分时计步：" + stepTimes[i] + "--时间" + steps[i]);
        }
        conView.setData(steps, stepTimes);
        if(isStepCount)
        {
            setTheSportStateByTheWay(stepAllCount[0]);
        }
        return times;
    }


    private void setTheSportStateByTheWay(int stepAllCount)
    {
        stepAllCount = Math.abs(stepAllCount);

        int id = R.string.pianshao;
        if(stepAllCount < 4000 && stepAllCount >= 0)
        {
            id = R.string.pianshao;
        }
        else if(stepAllCount < 8000 && stepAllCount >= 4000)
        {
            id = R.string.zhengchang;
        }
        else if(stepAllCount < 12000 && stepAllCount >= 8000)
        {
            id = R.string.huoyue;
        }
        else if(stepAllCount >= 12000)
        {
            id = R.string.daren;
        }
        Log.i(TAG, "运动状态--:" + getString(id) + "步数" + stepAllCount);
        activeState.setText(getString(id));
        movementState.setText(getString(id));
    }

    /**
     * 取数组的最大值
     * @param steps
     */
    private int getTheMaxInt(int[] steps)
    {
        int max = 0;
        for (int i = 0; i < steps.length; i ++)
        {
            if(steps[i] > steps[max])
            {
                max = i;
            }
        }
        return steps[max];
    }


    /**
     * 解析查询出的数据游标对象
     * @param cursor
     */
    private int[] parserTheCursorEachDate(Cursor cursor)
    {
        int[] steps = new int[0];
        final ArrayList<Integer> times = new ArrayList<>();
//        final int[] stepAllCount = {0};
        if(cursor.getCount() != 0)
        {
            if(cursor.moveToFirst())
            {
                do{
                    steps = new int[24];
                    steps[0] = cursor.getInt(cursor.getColumnIndex("one"));
                    steps[1] = cursor.getInt(cursor.getColumnIndex("two"));
                    steps[2] = cursor.getInt(cursor.getColumnIndex("three"));
                    steps[3] = cursor.getInt(cursor.getColumnIndex("four"));
                    steps[4] = cursor.getInt(cursor.getColumnIndex("five"));
                    steps[5] = cursor.getInt(cursor.getColumnIndex("six"));
                    steps[6] = cursor.getInt(cursor.getColumnIndex("seven"));
                    steps[7] = cursor.getInt(cursor.getColumnIndex("eight"));
                    steps[8] = cursor.getInt(cursor.getColumnIndex("nine"));
                    steps[9] = cursor.getInt(cursor.getColumnIndex("ten"));
                    steps[10] = cursor.getInt(cursor.getColumnIndex("one1"));
                    steps[11] = cursor.getInt(cursor.getColumnIndex("two1"));
                    steps[12] = cursor.getInt(cursor.getColumnIndex("three1"));
                    steps[13] = cursor.getInt(cursor.getColumnIndex("four1"));
                    steps[14] = cursor.getInt(cursor.getColumnIndex("five1"));
                    steps[15] = cursor.getInt(cursor.getColumnIndex("six1"));
                    steps[16] = cursor.getInt(cursor.getColumnIndex("seven1"));
                    steps[17] = cursor.getInt(cursor.getColumnIndex("eight1"));
                    steps[18] = cursor.getInt(cursor.getColumnIndex("nine1"));
                    steps[19] = cursor.getInt(cursor.getColumnIndex("ten1"));
                    steps[20] = cursor.getInt(cursor.getColumnIndex("one2"));
                    steps[21] = cursor.getInt(cursor.getColumnIndex("two2"));
                    steps[22] = cursor.getInt(cursor.getColumnIndex("three2"));
                    steps[23] = cursor.getInt(cursor.getColumnIndex("four2"));
                }while (cursor.moveToNext());
            }

        }
        cursor.close();
        return steps;
    }



    View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.close_the_detail:
                    Active_detial_Activity.this.onBackPressed();
                    break;
                case R.id.trend_button:
                    startActivity(new Intent(Active_detial_Activity.this, TrendActivity.class));
                    break;
                case R.id.share_button:
                    ShotScreenForShare.getInstance().takeshotScreen(Active_detial_Activity.this);
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(Active_detial_Activity.this.getApplicationContext(), ShareActivity.class);
                    startActivity(intent);
                    break;
                case R.id.provios_data:
                    String bufferP = getPriviosDay(currentDate);
                    if(bufferP != null && !bufferP.equals(""))
                    {
                        currentDate = bufferP;
                        titleDate.setText(compareTheDate(currentDate));
                        loadCount = 0;
                        LoadingTheAllData(currentDate);
//                        getCurrentRemoteDate(currentDate);
                    }
                    break;
                case R.id.next_data:
                    String buffDate = getNextDate(currentDate);
                    if(buffDate != null && !buffDate.equals(""))
                    {
                        currentDate = buffDate;
                        titleDate.setText(compareTheDate(currentDate));
                        loadCount = 0;
                        LoadingTheAllData(currentDate);
//                        getCurrentRemoteDate(currentDate);
                    }
                    break;
                case R.id.day_select:
                    new CustomDateSelector(Active_detial_Activity.this, currentDate, new CustomDateSelector.OnDateChoose() {
                        @Override
                        public void choose(String dates)
                        {
                            currentDate = dates;
                            titleDate.setText(compareTheDate(currentDate));
                            loadCount = 0;
                            LoadingTheAllData(currentDate);
//                            getCurrentRemoteDate(currentDate);
                        }
                    });
                    break;
            }
        }
    };


    private String getNextDate(String currenDate)
    {
        String da = getTodayDate();
        if(da.equals(currenDate))
        {
            return null;
        }
        else
        {
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            Log.i(TAG, "当前日期格式：" + currenDate);
            String[] eaDate = currenDate.split("-");
            calendar.set(Integer.parseInt(eaDate[0]), Integer.parseInt(eaDate[1]) - 1, Integer.parseInt(eaDate[2]) + 1);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return format.format(calendar.getTime());
        }
    }
    private String getPriviosDay(String currenDate)
    {
        if(currenDate.equals("1900-01-01"))
        {
            return null;
        }
        else
        {
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            Log.i(TAG, "当前日期格式：" + currenDate);
            String[] eaDate = currenDate.split("-");
            calendar.set(Integer.parseInt(eaDate[0]), Integer.parseInt(eaDate[1]) - 1, Integer.parseInt(eaDate[2]) - 1);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return format.format(calendar.getTime());
        }
    }

    private String compareTheDate(String date)
    {
        String resultFormat = "";
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String compareDate = format.format(calendar.getTime());
        if(date != null && !date.equals("") && date.equals(compareDate))
        {
            String[] dates = date.split("-");
            calendar.set(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]) - 1, Integer.parseInt(dates[2]));
            SimpleDateFormat formates = new SimpleDateFormat("MM.dd", Locale.getDefault());
            String mAndD = formates.format(calendar.getTime());
            resultFormat = getResources().getString(R.string.today);
        }
        else
        {
            String[] dates = date.split("-");
//			Calendar calendars = Calendar.getInstance(TimeZone.getDefault());
            calendar.set(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]) - 1, Integer.parseInt(dates[2]));
            int week = calendar.get(Calendar.DAY_OF_WEEK);
            SimpleDateFormat formates = new SimpleDateFormat("MM.dd", Locale.getDefault());
            String mAndD = formates.format(calendar.getTime());
            String weekS = "";
            if(week == Calendar.SUNDAY)
            {
                weekS = getString(R.string.sunday_all);
            }
            else if(week == Calendar.MONDAY)
            {
                weekS = getString(R.string.tuesday_all);
            }
            else if(week == Calendar.TUESDAY)
            {
                weekS = getString(R.string.monday_all);
            }
            else if(week == Calendar.WEDNESDAY)
            {
                weekS = getString(R.string.wednesday_all);
            }
            else if(week == Calendar.THURSDAY)
            {
                weekS = getString(R.string.thursday_all);
            }
            else if(week == Calendar.FRIDAY)
            {
                weekS = getString(R.string.friday_all);
            }
            else if(week == Calendar.SATURDAY)
            {
                weekS = getString(R.string.saturday_all);
            }
            resultFormat = mAndD + "\t\t" + weekS;
        }
        return resultFormat;
    }

}
