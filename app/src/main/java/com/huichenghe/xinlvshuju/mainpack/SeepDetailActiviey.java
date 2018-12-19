package com.huichenghe.xinlvshuju.mainpack;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huichenghe.bleControl.Ble.BleDataForSleepData;
import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.bleControl.Ble.DataSendCallback;
import com.huichenghe.xinlvshuju.BleDeal.BleSleepDataDeal;
import com.huichenghe.xinlvshuju.BleDeal.HRDataDealer;
import com.huichenghe.xinlvshuju.CustomView.CircleProgressDialog;
import com.huichenghe.xinlvshuju.CustomView.CustomDateSelector;
import com.huichenghe.xinlvshuju.CustomView.HeartReatDetailsViewForSleep;
import com.huichenghe.xinlvshuju.CustomView.SleepDetaillView;
import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.EachLoginHelper;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.UpdataService.DeviceTypeUtils;
import com.huichenghe.xinlvshuju.UpdataService.SendDataCallback;
import com.huichenghe.xinlvshuju.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;
import com.huichenghe.xinlvshuju.Utils.NetStatus;
import com.huichenghe.xinlvshuju.Utils.ShotScreenForShare;
import com.huichenghe.xinlvshuju.expand_activity.MovementDetail.SleepDataHelper;
import com.huichenghe.xinlvshuju.expand_activity.ShareActivity;
import com.huichenghe.xinlvshuju.expand_activity.Treads.TrendActivity;
import com.huichenghe.xinlvshuju.http.UserAccountUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class SeepDetailActiviey extends BaseActivity
{
    public final String TAG = SeepDetailActiviey.class.getSimpleName();
    private ImageView privoisDate, nextDate;
    private TextView sleepDataDate;
    private String currentDate;
    private String HR_DATA = "hr data";
    private String HR_TIME = "hr time";
    private SleepDetaillView sleepDetaillView;
    private TextView deepSleepHour, lightSleepHour, aweakSleepHour, maxHR, averHR;
    private HeartReatDetailsViewForSleep sleepHRView;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sleeping);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        initView();
    }

    private void initView()
    {
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_the_sleep_data);
        int screeHe = getResources().getDisplayMetrics().heightPixels;
        refreshLayout.setProgressViewOffset(false, -100, screeHe/10);
        refreshLayout.setOnRefreshListener(refreshListener);
        currentDate = getIntent().getStringExtra("currentDate");
        sleepDetaillView = (SleepDetaillView)findViewById(R.id.zhuzhuangtu_sleep);
        findViewById(R.id.trend_button).setOnClickListener(listener);
        findViewById(R.id.share_button).setOnClickListener(listener);
        findViewById(R.id.close_the_detail).setOnClickListener(listener);
        privoisDate = (ImageView)findViewById(R.id.provios_data);
        nextDate = (ImageView)findViewById(R.id.next_data);
        sleepDataDate = (TextView)findViewById(R.id.day_select);
        privoisDate.setOnClickListener(listener);
        nextDate.setOnClickListener(listener);
        sleepDataDate.setOnClickListener(listener);
        sleepDataDate.setText(compareTheDate(currentDate));
        deepSleepHour = (TextView)findViewById(R.id.deep_hour);
        lightSleepHour = (TextView)findViewById(R.id.light_sleep__hour);
        aweakSleepHour = (TextView)findViewById(R.id.wake_sleep__hour);
        sleepHRView = (HeartReatDetailsViewForSleep) findViewById(R.id.sleep_hr);
        maxHR = (TextView) findViewById(R.id.max_hr_sleep);
        averHR = (TextView)findViewById(R.id.average_hr_sleep);
        maxHR.setText(getString(R.string.height_hr) + "--");
        maxHR.setText(getString(R.string.lower_hr) + "--");
    }

    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener()
    {
        @Override
        public void onRefresh()
        {
            String today = getTodayDate();
            if(today.equals(currentDate))
            {
                if(BluetoothLeService.getInstance() != null && !BluetoothLeService.getInstance().isConnectedDevice())
                {
                  //  MyToastUitls.showToast(getApplicationContext(), R.string.not_connecte, 1);
                    refreshLayout.setRefreshing(false);
                    return;
                }
                BleDataForSleepData.getInstance(getApplicationContext()).setOnSleepDataRecever(new DataSendCallback() {
                    @Override
                    public void sendSuccess(byte[] receveData)
                    {
                        new BleSleepDataDeal(receveData, getApplicationContext());
                        LoadSleepDataAndHR(currentDate);
                    }
                    @Override
                    public void sendFailed() {}
                    @Override
                    public void sendFinished()
                    {
                        refreshLayout.setRefreshing(false);
                    }
                });
                BleDataForSleepData.getInstance(getApplicationContext()).getSleepingData();
            }
            else
            {
//                getDateSleepData(currentDate);
                refreshLayout.setRefreshing(false);
            }
        }
    };

    @Override
    protected void onResume()
    {
        super.onResume();
        loadCount = 0;
        LoadSleepDataAndHR(currentDate);
//        if(BluetoothLeService.getInstance() != null && !BluetoothLeService.getInstance().isConnectedDevice())
//        {
//            getDateSleepData(currentDate);
//        }
    }

    private void getDateSleepData(String currentDateInner)
    {
        Log.i("three_detail","从服务器上下载数据");
        if(!NetStatus.isNetWorkConnected(getApplicationContext()))
        {
            MyToastUitls.showToast(SeepDetailActiviey.this, R.string.net_wrong, 1);
            return;
        }
//        if(UpdateHistoryDataService.getInstance() != null)
//        {
//            CircleProgressDialog.getInstance().showCircleProgressDialog(SeepDetailActiviey.this);
//            UpdateHistoryDataService.getInstance().getDateSleepData(currentDateInner, UserAccountUtil.getAccount(getApplicationContext()), sleepCallback);
//           UpdateHistoryDataService.getInstance().getDateHRData(currentDateInner, UserAccountUtil.getAccount(getApplicationContext()), callback);
//        }
    }

    SendDataCallback sleepCallback = new SendDataCallback() {
        @Override
        public void sendDataSuccess(String reslult)
        {
            Log.i("updateData", reslult);
            try {
                JSONObject json = new JSONObject(reslult);
                String code = json.getString("code");
                if(code != null && code.equals("9003"))
                {
                    JSONObject subJson = json.getJSONObject("data");
                    String time = subJson.getString("time");
                    String data = subJson.getString("sleepData");
                    if(data == null || data.equals("") || data.length() <= 0)return;
                    new BleSleepDataDeal(getApplicationContext(), time, data);
                }
                else if(code != null && code.equals("9001"))
                {
                    new EachLoginHelper(getApplicationContext(), new SendDataCallback() {
                        @Override
                        public void sendDataSuccess(String reslult)
                        {
//                            getDateSleepData(currentDate);
                        }
                        @Override
                        public void sendDataFailed(String result){}
                        @Override
                        public void sendDataTimeOut(){}
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void sendDataFailed(String result)
        {
            if(!canDoContinue())return;
            MyToastUitls.showToast(SeepDetailActiviey.this, R.string.net_wrong, 1);
        }
        @Override
        public void sendDataTimeOut() {}
    };

    /**
     * 获取心率回调
     */
    SendDataCallback callback = new SendDataCallback() {
        @Override
        public void sendDataSuccess(String reslult)
        {
            Log.i(TAG, "SleepDetail收到的心率数据：" + reslult);
            try
            {
                JSONObject jsonObject = new JSONObject(reslult);
                String code = jsonObject.getString("code");
                if(code != null && code.equals("9003"))
                {
                    String reJs = jsonObject.getString("data");
                    new HRDataDealer(getApplicationContext(), reJs);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            LoadSleepDataAndHR(currentDate);
                            CircleProgressDialog.getInstance().closeCircleProgressDialog();
                        }
                    });
                }
                else
                {
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
                    MyToastUitls.showToast(SeepDetailActiviey.this, R.string.net_wrong, 1);
                    CircleProgressDialog.getInstance().closeCircleProgressDialog();
                }
            });
        }
        @Override
        public void sendDataTimeOut() {}
    };

    View.OnClickListener listener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.trend_button:
                    startActivity(new Intent(SeepDetailActiviey.this, TrendActivity.class));
                    break;
                case R.id.share_button:
                    ShotScreenForShare.getInstance().takeshotScreen(SeepDetailActiviey.this);
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(SeepDetailActiviey.this.getApplicationContext(), ShareActivity.class);
                    startActivity(intent);
                    break;
                case R.id.close_the_detail:
                    SeepDetailActiviey.this.onBackPressed();
                    break;
                case R.id.provios_data:
                    showPriviosData();
                    break;
                case R.id.next_data:
                    showNextData();

                    break;
                case R.id.day_select:
                    showSeleterData();
                    break;
            }
        }
    };

    private void showSeleterData()
    {
        new CustomDateSelector(SeepDetailActiviey.this, currentDate, new CustomDateSelector.OnDateChoose() {
            @Override
            public void choose(String dates)
            {
                currentDate = dates;
//						dataList.clear();
//						resetTheScreen();
//						readDbAndshow(currenDate);
//						updateThisScreen(currenDate);
//						stepFragmentLikes.updateHeadView(currenDate);
//						stepFragmentLikes.updateOutlineData(currenDate);
                sleepDataDate.setText(compareTheDate(currentDate));
                loadCount = 0;
                LoadSleepDataAndHR(currentDate);
//                getDateSleepData(currentDate);
            }
        });
    }

    private void showNextData()
    {
        String bufferP = getNextDate(currentDate);
        if(bufferP != null && !bufferP.equals(""))
        {
            currentDate = bufferP;
            sleepDataDate.setText(compareTheDate(currentDate));
            loadCount = 0;
            LoadSleepDataAndHR(currentDate);
//            getDateSleepData(currentDate);
        }
    }

    private String getNextDate(String currenDate)
    {
        String da = getTodayDate();
        if(da.equals(currenDate))
        {
            return da;
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

    private String getTodayDate()
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(calendar.getTime());
    }
    private void showPriviosData()
    {
        String bufferP = getPriviosDay(currentDate);
        if(bufferP != null && !bufferP.equals(""))
        {
            currentDate = bufferP;
            sleepDataDate.setText(compareTheDate(currentDate));
            loadCount = 0;
            LoadSleepDataAndHR(currentDate);
//            getDateSleepData(currentDate);
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

    private void LoadSleepDataAndHR(String currentDate)
    {
        String userAccount = UserAccountUtil.getAccount(getApplicationContext());
        String beforDay = getBeforDay(currentDate);
        new GetSleepData().execute(beforDay, currentDate, userAccount);
    }

    private String getBeforDay(String currentDate)
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        String[] eaDate = currentDate.split("-");
        calendar.set(Integer.parseInt(eaDate[0]), Integer.parseInt(eaDate[1]) - 1, Integer.parseInt(eaDate[2]) - 1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(calendar.getTime());
    }

    class GetSleepData extends AsyncTask<String, Void, Boolean>
    {
        String[] sleepData;
        String hrData;
        ArrayList<String> hrTime;
        @Override
        protected Boolean doInBackground(String... params)
        {
            sleepData = new SleepDataHelper(getApplicationContext())
                    .loadSleepData(params[0], params[1], params[2]);
            if(sleepData == null)return false;
            if(sleepData[2] != null && sleepData[2].equals(""))
                return false;
//            Log.i(TAG, "睡眠数据GetSleepData：" + sleepData[phone]);
            Map<String, Object> mapData = selectAndShowHr(params[0], params[1], params[2], sleepData[0], sleepData[1]);
            hrData = (String)mapData.get(HR_DATA);
            hrTime = (ArrayList<String>)mapData.get(HR_TIME);
            return true;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            super.onPostExecute(aBoolean);
            if(aBoolean)
            {
                if(sleepData != null && !sleepData.equals(""))
                {
                    sleepDetaillView.setSleepData(sleepData[0], sleepData[1], sleepData[2]);
                    sleepDetaillView.invalidate();
                    updateTheTextAboutDeepAndLightAndCompletion(sleepData[2]);
                }
//             更新显示, 获取深睡和浅睡时间
                if (hrData != null && !hrData.equals(""))
                {
                    String[] hrTimes = new String[hrTime.size()];
                    for (int i = 0; i < hrTimes.length; i++)
                    {
                        hrTimes[i] = hrTime.get(i);
                    }
                    sleepHRView.setData(hrTimes, hrData, 220);
                    sleepHRView.postInvalidate();
                    Log.i(TAG, "睡眠心率数据：" + hrData);
                    getTheMaxHRAndAverageHR(hrData);
                }
            }
            else
            {
                if(currentDate != null && !currentDate.equals(getTodayDate()))
                {
                    if(loadCount == 0)
                    {
                        loadCount++;
                        getDateSleepData(currentDate);
                    }
                }
                else
                {
                    if(BluetoothLeService.getInstance() != null && !BluetoothLeService.getInstance().isConnectedDevice())
                    {
                        if(loadCount == 0)
                        {
                            loadCount ++;
                            getDateSleepData(currentDate);
                        }
                    }
                }
                sleepHRView.setData(null, "", 220);
                sleepHRView.invalidate();
                sleepDetaillView.setSleepData("", "", null);
                sleepDetaillView.invalidate();
                updateTheTextAboutDeepAndLightAndCompletion("");
                getTheMaxHRAndAverageHR("");
            }
        }
    }

    private int loadCount = 0;

    private void getTheMaxHRAndAverageHR(String hex)
    {
        if(hex != null && hex.equals(""))
        {
            String max = getString(R.string.height_heartreat) + "--";
            String aver = getString(R.string.average_heartreat) + "--";
            maxHR.setText(max);
            averHR.setText(aver);
            return;
        }
        int maxH = 0;
        int numberAnd = 0;
        int count = 1;
        int average = 0;
        byte[] bytes = FormatUtils.hexString2ByteArray(hex);
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
        String max = getString(R.string.height_heartreat) + String.valueOf(maxH);
        String aver = getString(R.string.average_heartreat) + String.valueOf(average);
        maxHR.setText(max);
        averHR.setText(aver);
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

    private void updateTheTextAboutDeepAndLightAndCompletion(String ridEnd)
    {
        if(ridEnd != null && ridEnd.equals(""))
        {
            String ho = "0h";
            String mi = "0min";
            lightSleepHour.setText(ho + mi);
            String dh = "0h";
            String dm = "0min";
            deepSleepHour.setText(dh + dm);
            aweakSleepHour.setText("0h0min");
//            hourAll.setText("0");
//            minuteAll.setText("00");
//            setTheSleepState(0, sleepState);
//            targetCompletion.setText("00%");
            return;
        }
        int light = 0;  // 浅睡
        int deep = 0;   // 深睡
        int aweak = 0;
        char[] chars = ridEnd.toCharArray();
        String strs= new String(chars);
        strs = Replace(strs, "2332", "2112");
        strs = Replace(strs, "2002", "2112");
        strs = Replace(strs, "232", "212");
        strs = Replace(strs, "202", "212");
        chars=strs.toCharArray();
        for(int i = 0; i < chars.length; i++)
        {
            char d = chars[i];
            if(d == '1')
            {
                light ++;
            }
            else if(d == '2')
            {
                deep ++;
            }
            else if(d == '0' || d == '3')
            {
                aweak ++;
            }
        }
        if(light != 0)
        {
            light = light * 10;
        }
        if(deep != 0)
        {
            deep = deep * 10;
        }
        if(aweak != 0)
        {
            aweak = aweak * 10;
        }
        // 将数据转化为00h00'格式
        String[] lightSleep = tranlateToHourMinute(light);
        String ho = lightSleep[0] + "h";
        String mi = lightSleep[1] + "min";
        lightSleepHour.setText(ho + mi);
        String[] deepSleep = tranlateToHourMinute(deep);
        String dh = deepSleep[0] + "h";
        String dm = deepSleep[1] + "min";
        deepSleepHour.setText(dh + dm);
        String[] aweakSleep = tranlateToHourMinute(aweak);
        aweakSleepHour.setText(aweakSleep[0] + "h" + aweakSleep[1] + "min");
        String[] sleepAll = tranlateToHourMinute(light + deep);
//        hourAll.setText(sleepAll[0]);
//        minuteAll.setText(sleepAll[1]);
//        calculationTheTargetAndCompletion(light + deep);
//        setTheSleepState(light + deep, sleepState);
    }

    private String[] tranlateToHourMinute(int light)
    {
        String[] strArray = new String[2];
        String format = null;
        int hour = light / 60;
        int minute = light % 60;
        if (minute < 10) {
            format = "0" + minute;
        }
        else
        {
            format = String.valueOf(minute);
        }
        strArray[0] = String.valueOf(hour);
        strArray[1] = format;
        return strArray;
    }

    private Map<String, Object> selectAndShowHr(String beforDay, String today, String userAccount, String startTime, String endTime)
    {
        return getTheHrDataCurrentDay(beforDay, today, userAccount, startTime, endTime);
    }

    /**
     * 查询数据库当天心率数据
     */
    private Map<String, Object> getTheHrDataCurrentDay(String beforDay, String nowDay, String userAccount, String startT, String endT)
    {
        Map<String, Object> map = new HashMap<>();
        ArrayList<String> timeeS = new ArrayList<>();
        String dataS = "";
        String[] Culums = new String[]{"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
                "one1", "two1", "three1", "four1", "five1", "six1", "seven1", "eight1", "nine1", "ten1",
                "one2", "two2", "three2", "four2"};
        String[] s = startT.split(":");
        String[] e = endT.split(":");
        int startHour = Integer.parseInt(s[0]);
        int endHour = Integer.parseInt(e[0]);
        if(startHour >= 22 && endHour >= 22)
        {
            for (int i = startHour; i <= endHour; i++)
            {
                timeeS.add(i + ":00");
                Cursor cursor = MyDBHelperForDayData.getInstance(getApplicationContext()).selectHRFromCulums(getApplicationContext(), userAccount, beforDay, Culums[i], DeviceTypeUtils.getDeviceType(getApplicationContext()));
                if(cursor.getCount() != 0)
                {
                    if(cursor.moveToFirst())
                    {
                        do
                        {
                            String HRData = cursor.getString(cursor.getColumnIndex(Culums[i]));
                            if(HRData == null)
                            {
                                HRData = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
                            }
                            dataS += HRData;
                        }while (cursor.moveToNext());
                    }
                }
                cursor.close();
            }
        }
        else if(startHour >= 22 && endHour < 22)
        {
            Log.i("", "跨天");
            for (int i = startHour; i < 24; i++)
            {
                timeeS.add(i + ":00");
                Cursor cursor = MyDBHelperForDayData.getInstance(getApplicationContext())
                        .selectHRFromCulums(getApplicationContext(), userAccount, beforDay, Culums[i], DeviceTypeUtils.getDeviceType(getApplicationContext()));
                if(cursor.getCount() != 0)
                {
                    if(cursor.moveToFirst())
                    {
                        do
                        {
                            String HRData = cursor.getString(cursor.getColumnIndex(Culums[i]));
                            if(HRData == null)
                            {
                                HRData = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
                            }
                            dataS += HRData;
                        }while (cursor.moveToNext());
                    }
                }
                cursor.close();
            }
            for (int j = 0; j <= endHour; j ++)
            {
                timeeS.add(j + ":00");
                Cursor cursor = MyDBHelperForDayData.getInstance(getApplicationContext())
                        .selectHRFromCulums(getApplicationContext(), userAccount, nowDay, Culums[j], DeviceTypeUtils.getDeviceType(getApplicationContext()));
                if(cursor.getCount() != 0)
                {
                    if(cursor.moveToFirst())
                    {
                        do
                        {
                            String HRData = cursor.getString(cursor.getColumnIndex(Culums[j]));
                            if(HRData == null)
                            {
                                HRData = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
                            }
                            dataS += HRData;
                        }while (cursor.moveToNext());
                    }
                }
                cursor.close();
            }
        }
        else if(startHour < 22 && endHour < 22)
        {
            Log.i(TAG, "当天");
            for (int j = startHour; j <= endHour; j ++)
            {
                timeeS.add(j + ":00");
                Cursor cursor = MyDBHelperForDayData.getInstance(getApplicationContext()).selectHRFromCulums(getApplicationContext(), userAccount, nowDay, Culums[j], DeviceTypeUtils.getDeviceType(getApplicationContext()));
                if(cursor.getCount() != 0)
                {
                    if(cursor.moveToFirst())
                    {
                        do
                        {
                            String HRData = cursor.getString(cursor.getColumnIndex(Culums[j]));
                            if(HRData == null)
                            {
                                HRData = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
                            }
                            dataS += HRData;
                        }while (cursor.moveToNext());
                    }
                }
                cursor.close();
            }
        }
        map.put(HR_DATA, dataS);
        map.put(HR_TIME, timeeS);
        return map;
    }
}
