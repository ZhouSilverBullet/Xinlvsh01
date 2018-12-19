package com.huichenghe.xinlvshuju.mainpack;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huichenghe.bleControl.Ble.BleDataForSleepData;
import com.huichenghe.bleControl.Ble.BleDataForTarget;
import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.bleControl.Ble.DataSendCallback;
import com.huichenghe.xinlvshuju.BleDeal.BleSleepDataDeal;
import com.huichenghe.xinlvshuju.CustomView.CircleProgressDialog;
import com.huichenghe.xinlvshuju.CustomView.Custom_sleep_circle_progress;
import com.huichenghe.xinlvshuju.EachLoginHelper;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.UpdataService.SendDataCallback;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.expand_activity.MovementDetail.SleepDataHelper;
import com.huichenghe.xinlvshuju.expand_activity.TargetSettingActivity;
import com.huichenghe.xinlvshuju.http.UserAccountUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 */
public class SleepFragment extends Fragment {
    public final String TAG = "SleepFragment";
    private ArrayList<sleepData_new> sleepArray;
    private String currentDate;
    private MainActivity mainActivity;
    private TextView sleepTargetSetting;
    private Sleep_State_Adater sleepAdapter;
    private Custom_sleep_circle_progress cirPress;
    private SwipeRefreshLayout refreshLayout;
    private Handler handler = new Handler(Looper.myLooper());

    public SleepFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return initView(inflater, container);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
        currentDate = mainActivity.currenDate;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivity = (MainActivity) getActivity();
        currentDate = mainActivity.currenDate;
        initTarget();
        if (BluetoothLeService.getInstance() != null && !BluetoothLeService.getInstance().isConnectedDevice()) {
            if (!SleepFragment.this.isHidden()) {
                loadSleepData(currentDate);
//                getSleepDataFromRemote(currentDate);
            }
        } else {
            requestDeviceSleepData();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
        } else {
            if (currentDate == null) {
                currentDate= getTodayDate();
            } else {
                loadSleepData(currentDate);
            }
            if (BluetoothLeService.getInstance() != null && !BluetoothLeService.getInstance().isConnectedDevice()) {
//                getSleepDataFromRemote(currentDate);
            }
        }
    }

    private View initView(LayoutInflater inflater, ViewGroup container) {
        View root = inflater.inflate(R.layout.fragment_sleep, container, false);
        findView(root);
        return root;
    }

    private String getTodayDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(calendar.getTime());
    }

    private void findView(View root) {
        refreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.refresh_sleep_fragment);
        int reHei = getContext().getResources().getDisplayMetrics().heightPixels;
        refreshLayout.setProgressViewOffset(false, -100, reHei / 10);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String today = getTodayDate();
                if (!today.equals(currentDate)) {
                    ((MainActivity) getActivity()).setActivieContent();
                    currentDate = ((MainActivity) getActivity()).currenDate;
                }
                if (BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice()) {
                    requestDeviceSleepData();
                } else {
//                    getSleepDataFromRemote(currentDate);
                    // MyToastUitls.showToast(getContext(), R.string.not_connecte, 1);
                    refreshLayout.setRefreshing(false);
                }

            }
        });
        sleepTargetSetting = (TextView) root.findViewById(R.id.target_show_and_setting);
        sleepTargetSetting.setOnClickListener(listener);
        initTarget();
        cirPress = (Custom_sleep_circle_progress) root.findViewById(R.id.sleep_progress_cir);
        cirPress.setOnClickListener(listener);
        sleepArray = new ArrayList<>();
        sleepArray.add(new sleepData_new(R.mipmap.deep_sleep_icon, "0h0min", mainActivity.getString(R.string.deep_sleep)));
        sleepArray.add(new sleepData_new(R.mipmap.light_sleep_icon, "0h0min", mainActivity.getString(R.string.light_sleep)));
        sleepArray.add(new sleepData_new(R.mipmap.week_sleep_icon, "0h0min", mainActivity.getString(R.string.wake_sleep)));
        sleepArray.add(new sleepData_new(R.mipmap.sleep_state_icon, mainActivity.getString(R.string.pianshao), mainActivity.getString(R.string.sleep_state)));
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycle_for_sleep_grid);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        sleepAdapter = new Sleep_State_Adater(getContext(), sleepArray);
        recyclerView.setAdapter(sleepAdapter);
    }

    /**
     * 获取设备睡眠数据
     */
    private void requestDeviceSleepData() {
        BleDataForSleepData.getInstance(getContext()).setOnSleepDataRecever(new DataSendCallback() {
            @Override
            public void sendSuccess(byte[] receveData) {
                new BleSleepDataDeal(receveData, getContext());
                loadSleepData(currentDate);
                int[] target = getTargetData();
                asyncTarget(target[0], target[1]);
            }

            @Override
            public void sendFailed() {
            }

            @Override
            public void sendFinished() {
                if (refreshLayout != null && refreshLayout.isRefreshing())
                    refreshLayout.setRefreshing(false);
            }
        });
        BleDataForSleepData.getInstance(getContext()).getSleepingData();
    }

    private void asyncTarget(int sportTarget, int sleepTarget) {
        String today = getTodayDate();
        String yes = getBeforDay(today);
        String[] sleepData = new SleepDataHelper(getContext().getApplicationContext())
                .loadSleepData(yes, today, UserAccountUtil.getAccount(getContext().getApplicationContext()));
        int minu = sleepData[2].length() * 10;
        int hour = minu / 60;
        int minute = minu % 60;
        BleDataForTarget.getInstance().sendTargetToDevice(sportTarget, sleepTarget, hour, minute);
    }


    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(calendar.getTime());
    }

    public void getSleepDataFromRemote(final String currentDate) {
        Log.i("three", "从服务器上下载数据");
//        if(UpdateHistoryDataService.getInstance() != null && !UserAccountUtil.getAccount(getContext()).equals(""))
//        {
//            CircleProgressDialog.getInstance().showCircleProgressDialog(getContext());
//            UpdateHistoryDataService.getInstance().getDateSleepData(currentDate, UserAccountUtil.getAccount(getContext()), ServiceDataCalllbackToy);
//            UpdateHistoryDataService.getInstance().getDateSleepData(getBeforDay(currentDate), UserAccountUtil.getAccount(getContext()), ServiceDataCalllbackYes);
//        }
    }

    public int[] getTargetData() {
        String stepTarget = LocalDataSaveTool.getInstance(getContext().getApplicationContext()).readSp(MyConfingInfo.TARGET_SETTING_VALUE);
        String sleepTarget = LocalDataSaveTool.getInstance(getContext().getApplicationContext()).readSp(MyConfingInfo.TARGET_SETTING_VALUE_SLEEP);
        if (stepTarget != null && stepTarget.equals("")) {
            stepTarget = "8000";
        }
        if (sleepTarget != null && sleepTarget.equals("")) {
            sleepTarget = "8";
        }
        return new int[]{Integer.parseInt(stepTarget), Integer.parseInt(sleepTarget) * 60};
    }

    SendDataCallback ServiceDataCalllbackToy = new SendDataCallback() {
        @Override
        public void sendDataSuccess(String reslult) {
            Log.i("updateData", "服务器睡眠数据01: " + reslult);
            JSONObject json = null;
            try {
                json = new JSONObject(reslult);
                String code = json.getString("code");
                if (code != null && code.equals("9001")) {
                    new EachLoginHelper(getContext(), new SendDataCallback() {
                        @Override
                        public void sendDataSuccess(String reslult) {
//                            getSleepDataFromRemote(currentDate);
                        }

                        @Override
                        public void sendDataFailed(String result) {
                        }

                        @Override
                        public void sendDataTimeOut() {
                        }
                    });
                } else if (code != null && code.equals("9003")) {
                    JSONObject subJson = json.getJSONObject("data");
                    String time = subJson.getString("time");
                    String data = subJson.getString("sleepData");
                    new BleSleepDataDeal(getContext().getApplicationContext(), time, data);
                } else if (code != null && code.equals("9004")) {
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void sendDataFailed(String result) {
        }

        @Override
        public void sendDataTimeOut() {
        }
    };

    SendDataCallback ServiceDataCalllbackYes = new SendDataCallback() {
        @Override
        public void sendDataSuccess(String reslult) {
            Log.i("updateData", "服务器睡眠数据02：" + reslult);
            JSONObject json = null;
            try {
                json = new JSONObject(reslult);
                String code = json.getString("code");
                if (code != null && code.equals("9001")) {
                    CircleProgressDialog.getInstance().closeCircleProgressDialog();
                } else if (code != null && code.equals("9003")) {
                    JSONObject subJson = json.getJSONObject("data");
                    String time = subJson.getString("time");
                    String data = subJson.getString("sleepData");
                    new BleSleepDataDeal(getContext().getApplicationContext(), time, data);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadSleepData(currentDate);
                            CircleProgressDialog.getInstance().closeCircleProgressDialog();
                        }
                    });
                } else if (code != null && code.equals("9004")) {
                    CircleProgressDialog.getInstance().closeCircleProgressDialog();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void sendDataFailed(String result) {
            CircleProgressDialog.getInstance().closeCircleProgressDialog();
        }

        @Override
        public void sendDataTimeOut() {
        }
    };

    private void initTarget() {
        String sleepTarget = LocalDataSaveTool.getInstance(getContext()).readSp(MyConfingInfo.TARGET_SETTING_VALUE_SLEEP);
        sleepTargetSetting.setText(sleepTarget + "h");
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sleep_progress_cir:
                    currentDate = mainActivity.currenDate;
                    Intent intent = new Intent();
                    intent.putExtra("currentDate", currentDate);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(getContext(), SeepDetailActiviey.class);
                    startActivity(intent);
                    break;
                case R.id.target_show_and_setting:
                    if (currentDate != null && currentDate.equals(getTodayDate())) {
                        startActivity(new Intent(getContext(), TargetSettingActivity.class));
                    }
                    break;
            }
        }
    };

    public void loadSleepData(String currentDate) {
        Log.i(TAG, "加载睡眠数据：" + currentDate);
//        CircleProgressDialog.getInstance().showCircleProgressDialog(getActivity());
        String userAccount = UserAccountUtil.getAccount(getActivity());
        String beforDay = getBeforDay(currentDate);
        new GetSleepData().execute(beforDay, currentDate, userAccount);
    }

    public void setDate(String currentDates) {
        this.currentDate = currentDates;
    }

    private String getBeforDay(String currentDate) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        String[] eaDate = currentDate.split("-");
        calendar.set(Integer.parseInt(eaDate[0]), Integer.parseInt(eaDate[1]) - 1, Integer.parseInt(eaDate[2]) - 1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(calendar.getTime());
    }

    class GetSleepData extends AsyncTask<String, Void, Boolean> {
        String[] sleepData;
        String hrData;
        ArrayList<String> hrTime;

        @Override
        protected Boolean doInBackground(String... params) {
            sleepData = new SleepDataHelper(mainActivity.getApplicationContext())
                    .loadSleepData(params[0], params[1], params[2]);
            if (sleepData == null) return false;
            if (sleepData[2] != null && sleepData[2].equals(""))
                return false;
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
//            CircleProgressDialog.getInstance().closeCircleProgressDialog();
            if (aBoolean) {
                if (sleepData != null && !sleepData.equals("")) {
                    updateTheTextAboutDeepAndLightAndCompletion(sleepData[2]);
                }
            } else {
                if (currentDate != null && !currentDate.equals(getTodayDate())) {
                    if (loadCount == 0) {
                        loadCount++;
                        getSleepDataFromRemote(currentDate);
                    }
                } else {
                    if (BluetoothLeService.getInstance() != null && !BluetoothLeService.getInstance().isConnectedDevice()) {
                        if (loadCount == 0) {
                            loadCount++;
                            getSleepDataFromRemote(currentDate);
                        }
                    }
                }
                sleepArray.clear();
                sleepArray.add(new sleepData_new(R.mipmap.deep_sleep_icon, "0h0min", mainActivity.getString(R.string.deep_sleep)));
                sleepArray.add(new sleepData_new(R.mipmap.light_sleep_icon, "0h0min", mainActivity.getString(R.string.light_sleep)));
                sleepArray.add(new sleepData_new(R.mipmap.week_sleep_icon, "0h0min", mainActivity.getString(R.string.wake_sleep)));
                sleepArray.add(new sleepData_new(R.mipmap.sleep_state_icon, mainActivity.getString(R.string.pianshao), mainActivity.getString(R.string.sleep_state)));
                sleepAdapter.notifyDataSetChanged();
                StringBuffer stringB = new StringBuffer();
                stringB.append(mainActivity.getString(R.string.degre_completion));
                stringB.append("\t\t");
                stringB.append("0");
                stringB.append("%");
                cirPress.setSleepValue(stringB.toString(), String.valueOf(0), String.valueOf(0), 0);
            }
        }
    }

    public int loadCount = 0;
    private String  Replace(String strReplaced, String oldStr, String newStr) {
        int pos = 0;
        int findPos;
        while ((findPos = strReplaced.indexOf(oldStr, pos)) != -1) {
            strReplaced = strReplaced.substring(0, findPos) + newStr + strReplaced.substring(findPos + oldStr.length());
            findPos += newStr.length();
        }
        return strReplaced;
    }
    private void updateTheTextAboutDeepAndLightAndCompletion(String ridEnd) {
        if (ridEnd != null && ridEnd.equals("")) {
            sleepArray.clear();
            sleepArray.add(new sleepData_new(R.mipmap.deep_sleep_icon, "0h0min", mainActivity.getString(R.string.deep_sleep)));
            sleepArray.add(new sleepData_new(R.mipmap.light_sleep_icon, "0h0min", mainActivity.getString(R.string.light_sleep)));
            sleepArray.add(new sleepData_new(R.mipmap.week_sleep_icon, "0h0min", mainActivity.getString(R.string.wake_sleep)));
            sleepArray.add(new sleepData_new(R.mipmap.sleep_state_icon, mainActivity.getString(R.string.pianshao), mainActivity.getString(R.string.sleep_state)));
            sleepAdapter.notifyDataSetChanged();
            StringBuffer stringB = new StringBuffer();
            stringB.append(mainActivity.getString(R.string.degre_completion));
            stringB.append("\t\t");
            stringB.append("0");
            stringB.append("%");
            cirPress.setSleepValue(stringB.toString(), String.valueOf(0), String.valueOf(0), 0);
            return;
        }
        int aweak = 0;
        int light = 0;  // 浅睡
        int deep = 0;   // 深睡
        char[] chars = ridEnd.toCharArray();
        String strs= new String(chars);
        strs = Replace(strs, "2332", "2112");
        strs = Replace(strs, "2002", "2112");
        strs = Replace(strs, "232", "212");
        strs = Replace(strs, "202", "212");
        chars=strs.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char d = chars[i];
            if (d == '1') {
                light++;
            } else if (d == '2') {
                deep++;
            } else if (d == '0' || d == '3') {
                aweak++;
            }
        }
        if (light != 0) {
            light = light * 10;
        }
        if (deep != 0) {
            deep = deep * 10;
        }
        if (aweak != 0) {
            aweak = aweak * 10;
        }

        // 将数据转化为00h00'格式
        String[] lightSleep = tranlateToHourMinute(light);
        String ho = lightSleep[0] + "h";
        String mi = lightSleep[1] + "min";

        String[] deepSleep = tranlateToHourMinute(deep);
        String dh = deepSleep[0] + "h";
        String dm = deepSleep[1] + "min";
        String[] aweakSleep = tranlateToHourMinute(aweak);
        String ah = aweakSleep[0] + "h";
        String am = aweakSleep[1] + "min";

        String[] sleepAll = tranlateToHourMinute(light + deep + aweak);
        int comp = calculationTheTargetAndCompletion(light + deep + aweak);
        StringBuffer stringB = new StringBuffer();
        stringB.append(mainActivity.getString(R.string.degre_completion));
        stringB.append("\t\t");
        stringB.append(comp);
        stringB.append("%");
        int de = (int) (((float) comp) / 100 * 360);
        if (de > 360) {
            de = 360;
        }
        cirPress.runAnimate(stringB.toString(), sleepAll[0], sleepAll[1], de);
        int stateId = setTheSleepState(light + deep + aweak);
        sleepArray.clear();
        sleepArray.add(new sleepData_new(R.mipmap.deep_sleep_icon, dh + dm, mainActivity.getString(R.string.deep_sleep)));
        sleepArray.add(new sleepData_new(R.mipmap.light_sleep_icon, ho + mi, mainActivity.getString(R.string.light_sleep)));
        sleepArray.add(new sleepData_new(R.mipmap.week_sleep_icon, ah + am, mainActivity.getString(R.string.wake_sleep)));
        sleepArray.add(new sleepData_new(R.mipmap.sleep_state_icon, mainActivity.getString(stateId), mainActivity.getString(R.string.sleep_state)));
        sleepAdapter.notifyDataSetChanged();
    }

    // 计算完成度
    private int calculationTheTargetAndCompletion(int allLong) {
        String tar = LocalDataSaveTool.getInstance(mainActivity.getApplicationContext()).readSp(MyConfingInfo.TARGET_SETTING_VALUE_SLEEP);
        if (tar != null && !tar.equals("")) {
            int times = Integer.parseInt(tar);
            times = times * 60;
            float result = (float) allLong / (float) times;
            result *= 100;
            int r = roundHalfUp(result);
            return r;
        }
        return 0;
    }

    // 四舍五入
    private int roundHalfUp(float result) {
        BigDecimal bigDecimal = new BigDecimal(result);
        return bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
    }

    // 显示睡眠状态
    private int setTheSleepState(int timesTatal) {
        int s = R.string.zhengchang;
        if (timesTatal >= 0 && timesTatal < 6 * 60) {
            s = R.string.pianshao;
        } else if (timesTatal >= 6 * 60 && timesTatal < 9 * 60) {
            s = R.string.zhengchang;
        } else if (timesTatal >= 9 * 60 && timesTatal <= 12 * 60) {
            s = R.string.chongzu;
        } else if (timesTatal > 12 * 60) {
            s = R.string.henchongzu;
        }
        return s;
    }

    // 时间格式转换
    private String[] tranlateToHourMinute(int light) {
        String[] strArray = new String[2];
        String format = null;
        int hour = light / 60;
        int minute = light % 60;
        if (minute < 10) {
            format = "0" + minute;
        } else {
            format = String.valueOf(minute);
        }
        strArray[0] = String.valueOf(hour);
        strArray[1] = format;
        return strArray;
    }

}
