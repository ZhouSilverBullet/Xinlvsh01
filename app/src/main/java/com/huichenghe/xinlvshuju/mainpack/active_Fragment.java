package com.huichenghe.xinlvshuju.mainpack;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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

import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.xinlvshuju.BleDeal.DayDataDealer;
import com.huichenghe.xinlvshuju.CustomView.StepProgressView;
import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.UpdataService.DeviceTypeUtils;
import com.huichenghe.xinlvshuju.UpdataService.SendDataCallback;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MetricAndBritish;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.expand_activity.TargetSettingActivity;
import com.huichenghe.xinlvshuju.http.UserAccountUtil;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 */
public class active_Fragment extends Fragment {
    public final static String TAG = active_Fragment.class.getSimpleName();
    private RecyclerView gridView;
    private TextView target;
    private ArrayList<active_content> activeList;
    private GridAdapter gridAdapter;
    private StepProgressView stepProgress;
    private String currentDate;
    private MainActivity mainActivity;
    private Handler active_handler = new Handler();

    public active_Fragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return intiView(inflater, container);
    }

    //如果界面不可见就把心率关闭；
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.i(TAG, "active_fragment onHiddenChanged--" + hidden);
        if (hidden) {
            if (BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice()) {
                BluetoothLeService.getInstance().setHrNotify(null, false);
            }
        } else {//每次可见的时候都从数据库中取得数据然后刷新ui的显示
            if (mainActivity != null && !mainActivity.isFirstOpen) {
                updateThisScreen(currentDate);
            }
            if (BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice()) {
                BluetoothLeService.getInstance().setHrNotify(null, true);
            } else if (BluetoothLeService.getInstance() != null && !BluetoothLeService.getInstance().isConnectedDevice()) {
//                getAlldataFromService();
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    private View intiView(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.fragment_active, container, false);
        findView(v);
        return v;
    }

    private void findView(View v) {
        target = (TextView) v.findViewById(R.id.target_show_and_setting);
        target.setOnClickListener(clickListener);
        setTarget(target);
        intiGrideView(v);
        stepProgress = (StepProgressView) v.findViewById(R.id.active_progress);
        stepProgress.setOnClickListener(clickListener);
        currentDate = ((MainActivity) getActivity()).currenDate;
        initRefresh(v);
        updateThisScreen(currentDate);
    }

    /**
     * 刷新
     * 如果连接了蓝牙，刷新的时候不是今天，就会跳转到今天然后把当天的数据上传到服务器上去
     *
     * @param v
     */
    private void initRefresh(View v) {
        final SwipeRefreshLayout refsh = (SwipeRefreshLayout) v.findViewById(R.id.refresh_data_detail);
        int screeHe = getContext().getResources().getDisplayMetrics().heightPixels;
        refsh.setProgressViewOffset(false, -100, screeHe / 10);
        refsh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String today = getTodayDate();
                if (!today.equals(currentDate)) {
                    ((MainActivity) getActivity()).setActivieContent();
                    currentDate = ((MainActivity) getActivity()).currenDate;
                }
                if (BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice()) {
                    mainActivity.showConnectState(7);
                    mainActivity.getothercurrenrdata();
                    active_handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (refsh != null && refsh.isRefreshing()) {
                                refsh.setRefreshing(false);
                                ((MainActivity) getActivity()).upLoadTodayAllData(currentDate);
                            }
                        }
                    }, 4000);
                } else {
                    getCurrentDateData(currentDate);
                   // MyToastUitls.showToast(getContext(), R.string.not_connecte, 1);
                    refsh.setRefreshing(false);
                }
            }
        });
    }

    /**
     * 从服务器上下载数据，在下载数据之前先要隐性的登录账号
     *
     * @param currentDate
     */
    public void getCurrentDateData(final String currentDate) {
        Log.i("one", "从服务器上下载数据");
//        if(UserAccountUtil.getAccount(getContext()).equals(""))return;
//        new EachLoginHelper(getContext().getApplicationContext(), new SendDataCallback() {
//            @Override
//            public void sendDataSuccess(String reslult)
//            {
//                if(UpdateHistoryDataService.getInstance() != null)
//                {
//                     UpdateHistoryDataService.getInstance().getDayData(currentDate, UserAccountUtil.getAccount(getContext()), downCallback);
//                }
//            }
//            @Override
//            public void sendDataFailed(String result){}
//            @Override
//            public void sendDataTimeOut() {}
//        });

    }

    /**
     * 获取数据的回调
     */
    SendDataCallback downCallback = new SendDataCallback() {
        @Override
        public void sendDataSuccess(String reslult) {
            Log.i("updateData", "获取全天数据成功：" + reslult);
            new DayDataDealer(getContext().getApplicationContext(), reslult);
        }

        @Override
        public void sendDataFailed(String result) {
            Log.i(TAG, "获取全天数据失败：" + result);
        }

        @Override
        public void sendDataTimeOut() {
        }
    };

    /**
     * 得到当天日期
     *
     * @return
     */
    private String getTodayDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(calendar.getTime());
    }

    /**
     * 刷新大转盘
     *
     * @param steps  步数
     * @param comp   完成进度百分之多少
     * @param degree 旋转角度
     */
    private void updateCircleProgress(final int steps, final int comp, final float degree) {
        stepProgress.cancleAnimate();
        StringBuffer bu = new StringBuffer();
        bu.append(mainActivity.getString(R.string.degre_completion));
        bu.append("\t");
        bu.append(String.valueOf(comp));
        bu.append("%");
        stepProgress.updateTextValue(steps, bu.toString());
        stepProgress.runAnimate(degree);
    }

    /**
     * 设置目标数
     *
     * @param target
     */
    private void setTarget(TextView target) {
        String stepTarget = LocalDataSaveTool.getInstance(getContext()).readSp(MyConfingInfo.TARGET_SETTING_VALUE);
        if (stepTarget != null && !stepTarget.equals("")) {
            target.setText("2000");
        } else {
            target.setText(stepTarget);
        }
    }

    /**
     * 目标数，运动详情的点击事件
     */
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.target_show_and_setting:
                    if (currentDate != null && currentDate.equals(getTodayDate())) {
                        startActivity(new Intent(getActivity(), TargetSettingActivity.class));
                    }
                    break;
                case R.id.active_progress:
                    Intent intent = new Intent(getActivity(), Active_detial_Activity.class);
                    intent.putExtra("currentDate", currentDate);
                    startActivity(intent);
                    break;
            }

        }
    };

    /**
     * 初始化recyclerView组件，该组件listview GridView用法类似
     *
     * @param v
     */
    private void intiGrideView(View v) {
        gridView = (RecyclerView) v.findViewById(R.id.active_grid_view);
        activeList = new ArrayList<>();
        gridAdapter = new GridAdapter(getContext(), activeList);
        if (gridView != null) {
            gridView.setHasFixedSize(true);
            gridView.setItemAnimator(new DefaultItemAnimator());
            gridView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            gridView.setAdapter(gridAdapter);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "active_Fragment_onResume");
        registeBro();
        String va = LocalDataSaveTool.getInstance(getContext().getApplicationContext()).readSp(MyConfingInfo.TARGET_SETTING_VALUE);
        target.setText(va);
        if (!active_Fragment.this.isHidden()) {
            Log.i(TAG, "active_Fragment_isNOtHidden");
            if (BluetoothLeService.getInstance() != null && !BluetoothLeService.getInstance().isConnectedDevice()) {
                Log.i(TAG, "active_Fragment_isConnectedDevice");
//                getAlldataFromService();
            } else if (BluetoothLeService.getInstance() != null) {
                if (BluetoothLeService.getInstance() != null) {
                    BluetoothLeService.getInstance().setHrNotify(null, true);
                }
            } else {
//                getAlldataFromService();
            }
        }
    }

    private int flag = 1;

    public void getAlldataFromService() {
        if (UserAccountUtil.getAccount(getContext()).equals("")) return;
        if (flag == 1) {
            flag = 2;
            getCurrentDateData(currentDate = ((MainActivity) getActivity()).currenDate);
        } else {
            getCurrentDateData(currentDate = ((MainActivity) getActivity()).currenDate);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "active_Fragment_onPause");
        unregisteBraca();
        if (BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice()) {
            if (!active_Fragment.this.isHidden())
                BluetoothLeService.getInstance().setHrNotify(null, false);
        }
    }

    private MyBracaost myBraca;

    /**
     * 注册广播
     */
    private void registeBro() {
        myBraca = new MyBracaost();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyConfingInfo.NOTIFY_MAINACTIVITY_TO_UPDATE_DAY_DATA);
        filter.addAction(MyConfingInfo.BROADCAST_CHAGE_UNIT);
        filter.addAction(MyConfingInfo.DELETE_ALL_DATABASE);
        getContext().registerReceiver(myBraca, filter);
    }

    private void unregisteBraca() {
        getContext().unregisterReceiver(myBraca);
    }

    /**
     * 刷新ui上的心率数据
     * @param showResult
     */
    public void updateHR(final String showResult) {
        if (activeList.size() <= 0) return;
        activeList.remove(0);
        activeList.add(0, new active_content(1, R.mipmap.active_heart, showResult, "bpm"));
        gridAdapter.notifyItemChanged(0);
    }


    class MyBracaost extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MyConfingInfo.NOTIFY_MAINACTIVITY_TO_UPDATE_DAY_DATA)) {
                updateThisScreen(currentDate);
            } else if (intent.getAction().equals(MyConfingInfo.BROADCAST_CHAGE_UNIT)) {
                updateThisScreen(currentDate);
            } else if (intent.getAction().equals(MyConfingInfo.DELETE_ALL_DATABASE)) {
                resetTheScreen();
            }
        }
    }

    public void setDate(String currentDates) {
        this.currentDate = currentDates;
    }

    /**
     * 从本地数据库取得数据然后刷新ui
     *
     * @param todayDate
     */
    public void updateThisScreen(String todayDate) {
        String account = UserAccountUtil.getAccount(getContext().getApplicationContext());
        MyDBHelperForDayData helper = MyDBHelperForDayData.getInstance(getContext().getApplicationContext());
        Cursor mCursor = helper.selecteDayData(getContext().getApplicationContext(), account, todayDate, DeviceTypeUtils.getDeviceType(getContext().getApplicationContext()));
        Log.i(TAG, "全天数据查询出：" + mCursor.getCount());
        // 遍历Cursor
        if (mCursor.getCount() != 0)        // 有数据
        {
            if (mCursor.moveToFirst())      // 游标移动到开头
            {
                int stepFromDB;
                int calorieFromDB;
                int mileageFromDB;
                String movementTimeFromDB;
                String setTimeFromDB;
                do {
                    stepFromDB = mCursor.getInt(mCursor.getColumnIndex("stepAll"));
                    calorieFromDB = mCursor.getInt(mCursor.getColumnIndex("calorie"));
                    mileageFromDB = mCursor.getInt(mCursor.getColumnIndex("mileage"));
                    movementTimeFromDB = mCursor.getString(mCursor.getColumnIndex("movementTime"));
                    setTimeFromDB = mCursor.getString(mCursor.getColumnIndex("sitTime"));
                } while (mCursor.moveToNext());
                mCursor.close();
                int moveTime = Integer.parseInt(movementTimeFromDB);
                int h = moveTime / 60;
                int m = moveTime % 60;
                float mileage = ((float) mileageFromDB) / 1000;
                BigDecimal bd = new BigDecimal(mileage);
                bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                String valueOfTarget = LocalDataSaveTool.getInstance(getContext().getApplicationContext()).readSp(MyConfingInfo.TARGET_SETTING_VALUE);
                float targetF = (float) stepFromDB / (float) Integer.valueOf(valueOfTarget);
                if (activeList.size() == 0) {
                    activeList.add(new active_content(1, R.mipmap.active_heart, "--", "bpm"));
                    activeList.add(new active_content(1, R.mipmap.active_kcal, String.valueOf(calorieFromDB), "kcal"));
                    String s = LocalDataSaveTool.getInstance(getContext().getApplicationContext()).readSp(MyConfingInfo.METRICORINCH);
                    MetricAndBritish mb = new MetricAndBritish();
                    if (s != null && s.equals(MyConfingInfo.INCH)) {
                        float mi = mb.kilometreToMile(bd.floatValue());
                        activeList.add(new active_content(1, R.mipmap.active_km, String.valueOf(mi), mainActivity.getString(R.string.mile)));
                    } else if (s != null) {
                        activeList.add(new active_content(1, R.mipmap.active_km, bd.toString(), "km"));
                    }
                    active_content con = new active_content(4, R.mipmap.active_time);
                    String[] contentAc = new String[]{String.valueOf(h), "h", String.valueOf(m), "min"};
                    con.setContentArray(contentAc);
                    activeList.add(con);
                    gridAdapter.notifyDataSetChanged();
                } else {
                    activeList.remove(1);
                    activeList.add(1, new active_content(1, R.mipmap.active_kcal, String.valueOf(calorieFromDB), "kcal"));
                    String s = LocalDataSaveTool.getInstance(getContext().getApplicationContext()).readSp(MyConfingInfo.METRICORINCH);
                    MetricAndBritish mb = new MetricAndBritish();
                    if (s != null && s.equals(MyConfingInfo.INCH)) {
                        float mi = mb.kilometreToMile(bd.floatValue());
                        activeList.remove(2);
                        activeList.add(2, new active_content(1, R.mipmap.active_km, String.valueOf(mi), mainActivity.getString(R.string.mile)));
                    } else if (s != null) {
                        activeList.remove(2);
                        activeList.add(2, new active_content(1, R.mipmap.active_km, bd.toString(), mainActivity.getString(R.string.km_total)));
                    }
                    active_content con = new active_content(4, R.mipmap.active_time);
                    String[] contentAc = new String[]{String.valueOf(h), "h", String.valueOf(m), "min"};
                    con.setContentArray(contentAc);
                    activeList.remove(3);
                    activeList.add(3, con);
                    gridAdapter.notifyDataSetChanged();
                }
                updateCircleProgress(stepFromDB, (int) (targetF * 100), (targetF * 360) > 360 ? 360 : (targetF * 360));
            }
        } else {
            if (currentDate != null && !currentDate.equals(getTodayDate())) {
                if (loadCount == 0) {
                    loadCount++;
                    getCurrentDateData(currentDate);
                }
            } else {
                if (BluetoothLeService.getInstance() != null && !BluetoothLeService.getInstance().isConnectedDevice()) {
                    if (loadCount == 0) {
                        loadCount++;
                        getCurrentDateData(currentDate);
                    }
                }
            }
            resetTheScreen();
        }
    }

    public int loadCount = 0;

    private void resetTheScreen() {
        activeList.clear();
        activeList.add(new active_content(1, R.mipmap.active_heart, "--", "bpm"));
        activeList.add(new active_content(1, R.mipmap.active_kcal, String.valueOf(0), "kcal"));
        String s = LocalDataSaveTool.getInstance(getContext().getApplicationContext()).readSp(MyConfingInfo.METRICORINCH);
        MetricAndBritish mb = new MetricAndBritish();
        if (s != null && s.equals(MyConfingInfo.INCH)) {
            activeList.add(new active_content(1, R.mipmap.active_km, String.valueOf(0), mainActivity.getString(R.string.mile)));
        } else if (s != null) {
            activeList.add(new active_content(1, R.mipmap.active_km, String.valueOf(0), mainActivity.getString(R.string.km_total)));
        }
        active_content con = new active_content(4, R.mipmap.active_time);
        String[] contentAc = new String[]{String.valueOf(0), "h", String.valueOf(0), "min"};
        con.setContentArray(contentAc);
        activeList.add(con);
        gridAdapter.notifyDataSetChanged();
        updateCircleProgress(0, 0, 0);
    }
}
