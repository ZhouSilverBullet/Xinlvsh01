package com.huichenghe.xinlvshuju.slide;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huichenghe.xinlvshuju.Adapter.RecyclerForDelaySetting;
import com.huichenghe.xinlvshuju.Adapter.RemindCustomRecyclerAdapter;
import com.huichenghe.xinlvshuju.Adapter.WrapContentLinearLayoutManager;
import com.huichenghe.xinlvshuju.DataEntites.CustomRemindEntity;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.Utils.ProgressUtils;
import com.huichenghe.xinlvshuju.mainpack.BaseActivity;
import com.huichenghe.xinlvshuju.slide.settinga.SubCustonRemindActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class RemindActivity extends BaseActivity
{
    private CheckBox phoneButton, smsButton, hrWarning;
    private TextView hrSetting;
    private RemindCustomRecyclerAdapter mRemindCustomRecyclerAdapter;
    private ArrayList<CustomRemindEntity> dataList;
//    private ContentLoadingProgressBar myProgressBar;
    private myRecever recever;
    private static final String TAG = RemindActivity.class.getSimpleName();
    private EditText maxEdit, minEdit;
    private SwipeRefreshLayout refreshLayout;
    private TextView settingDelay;
    private PopupWindow delayWindow;
    private int delayTime = 6;
//    private ImageView plusIcon;
    private boolean isExcute = false;

//    Handler myHandler = new Handler()
//    {
//        @Override
//        public void handleMessage(Message msg)
//        {
//            super.handleMessage(msg);
//            switch (msg.what)
//            {
//                case 0:
//                    getHRWarningData();
//                    break;
//                case clock:
//                    getDelayData();
//                    break;
//
//
//
//            }
//        }
//    };

    private static class MYHandler extends Handler
    {
        private WeakReference<RemindActivity> weakR = null;

        public MYHandler(RemindActivity activity)
        {
            weakR = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg)
        {
            RemindActivity ac = weakR.get();
            if(ac != null)
            {

                switch (msg.what)
                {
                    case 0:
                        getHRWarningData();
                        break;
                    case 1:
                        getDelayData();
                        break;
                }
            }
        }
    }

    private MYHandler myHandler = new MYHandler(this);






    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind);
        //设置状态栏和底部功能键
//        settingTheStatebarAndNavigationbar();
//        if(BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice())
//        {
//            BluetoothLeService.getInstance().setBLENotify(null, true, false);
//        }

        registerRece();
        init();
        checkSwitch();

        myHandler.sendEmptyMessageDelayed(0, 50);
        myHandler.sendEmptyMessageDelayed(1, 100);
        if(!isExcute)
        {
            myHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    new MyAsycnTask().execute((Void) null);
//            refreshLayout.setRefreshing(true);
                }
            }, 500);

        }



    }



    // 注册广播
    private void registerRece()
    {
       recever = new myRecever();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyConfingInfo.CUSTOM_REMIND_VALID);
        filter.addAction(MyConfingInfo.ACTION_FOR_HR_WARNING);
        filter.addAction(MyConfingInfo.BROADCAST_DELAY_DATA);
       registerReceiver(recever, filter);

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        myHandler.removeMessages(0);
        myHandler.removeMessages(1);
        unregisterReceiver(recever);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
//        myProgressBar.show();
//        plusIcon.setVisibility(View.INVISIBLE);

        refreshLayout.setProgressViewOffset(false, 0, 100);
        refreshLayout.setRefreshing(true);
        if (isExcute)
        {
            new MyAsycnTask().execute();

        }
        isExcute = true;
        if(refreshLayout.isRefreshing())
        {
            myHandler.postDelayed(runn, 2000);
        }

    }

    Runnable runn = new Runnable()
    {
        @Override
        public void run()
        {
            if(refreshLayout.isRefreshing())
            {
                refreshLayout.setRefreshing(false);
            }
        }
    };

    private static void getHRWarningData()
    {
//        BleDataForHRWarning.getInstance().requestTheHRWarningData();
    }

    private static void getDelayData()
    {
//        BleDataForRingDelay.getDelayInstance().sendToGetTheDealyData();
    }


    class MyAsycnTask extends AsyncTask<Void, Void, Boolean>
    {

        @Override
        protected Boolean doInBackground(Void... params)
        {
            dataList.clear();
//            BleDataForCustomRemind.getCustomRemindDataInstance().getCustomRemind();
            return null;
        }
    }

    /**
     * 检查开关显示的状态
     */
    private void checkSwitch()
    {
        String phone = LocalDataSaveTool.getInstance(RemindActivity.this).readSp(MyConfingInfo.PHONE_REMIND_CHANGE);
        String sms = LocalDataSaveTool.getInstance(RemindActivity.this).readSp(MyConfingInfo.SMS_REMIND_CHANGE);

        if(!phone.isEmpty())
        {
            if(phone.equals(MyConfingInfo.REMIND_OPEN))
            {
                phoneButton.setChecked(true);
            }
            else
            {
                phoneButton.setChecked(false);
            }
        }
        if(!sms.isEmpty())
        {
            if(sms.equals(MyConfingInfo.REMIND_OPEN))
            {
                smsButton.setChecked(true);
            }
            else
            {
                smsButton.setChecked(false);
            }
        }


    }

    private void init()
    {
//        plusIcon = (ImageView)findViewById(R.id.custom_icon);
        settingDelay = (TextView)findViewById(R.id.setting_delay);
        settingDelay.setOnClickListener(listener);
        hrWarning = (CheckBox)findViewById(R.id.hr_remind_switch);
//        myProgressBar = (ContentLoadingProgressBar)findViewById(R.id.myProgressBar);

        dataList = new ArrayList<CustomRemindEntity>();
        mRemindCustomRecyclerAdapter = new RemindCustomRecyclerAdapter(RemindActivity.this, dataList);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_layout_show_remind);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(RemindActivity.this));
        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(RemindActivity.this));
        mRecyclerView.setAdapter(mRemindCustomRecyclerAdapter);
        mRemindCustomRecyclerAdapter.setOnEachButtonClickListener(new RemindCustomRecyclerAdapter.OnEachButtonClickListener() {
            @Override
            public void onDeleteButtonClick(int position) {
//                Toast.makeText(RemindActivity.this, "点击了" + position, Toast.LENGTH_SHORT).show();

                deleteThisItem(position, dataList, mRemindCustomRecyclerAdapter);
            }

            @Override
            public void onItemClick(int position) {
                byte[] bytess = new byte[dataList.size()];

                for (int i = 0; i < dataList.size(); i++) {
                    CustomRemindEntity en = dataList.get(i);
                    byte aa = en.getNumber();     // 获取提醒编号
                    bytess[i] = aa;

                }


                Intent intent = new Intent();
                intent.setClass(RemindActivity.this, SubCustonRemindActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(MyConfingInfo.SEND_TO_THE_ACTIVITY, bytess);
                intent.putExtra(MyConfingInfo.DETAIL_REMIND, (CustomRemindEntity) dataList.get(position));
                startActivity(intent);
            }
        });


        RelativeLayout customLayout = (RelativeLayout) findViewById(R.id.custom_layout);
        customLayout.setOnClickListener(listener);
        hrSetting = (TextView)findViewById(R.id.hr_remind_setting);
        hrSetting.setOnClickListener(listener);

        phoneButton = (CheckBox)findViewById(R.id.phone_remind_switch);
        smsButton = (CheckBox)findViewById(R.id.sms_remind_switch);
        phoneButton.setOnCheckedChangeListener(checkListener);
        smsButton.setOnCheckedChangeListener(checkListener);
        hrWarning.setOnCheckedChangeListener(checkListener);




        Toolbar toolbar = (Toolbar)findViewById(R.id.Remind_toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(getResources().getColor(R.color.White_forme));
        setSupportActionBar(toolbar);

        // 显示返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 修改返回键图标
        toolbar.setNavigationIcon(R.mipmap.back_icon_new);

        // 返回键监听
        toolbar.setNavigationOnClickListener(new NoDoubleClickListener()
        {
            @Override
            public void onNoDoubleClick(View v) {
                RemindActivity.this.onBackPressed();
            }
        });

        Log.i("", "Navigation" + toolbar.getNavigationContentDescription());

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_costum_remind);
//        refreshLayout.setProgressViewEndTarget(false, 200);
        refreshLayout.setProgressBackgroundColorSchemeResource(R.color.main_background);
        refreshLayout.setColorSchemeResources(R.color.blue, R.color.danger_color);
//        refreshLayout.setRefreshing(true);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {

                new MyAsycnTask().execute();
                myHandler.postDelayed(runn, 2000);
            }
        });





    }

    private void deleteThisItem(final int position, final ArrayList<CustomRemindEntity> datas, final RemindCustomRecyclerAdapter adap)
    {
//        CustomRemindEntity en = datas.get(position);
//        byte number = en.getNumber();
//        BleDataForCustomRemind cu = new BleDataForCustomRemind();
//        cu.deleteCustomRemind(number);
//        datas.remove(position);
//        adap.notifyDataSetChanged();
//        mRemindCustomRecyclerAdapter.removeItem(position);
//        cu.setOnDeleteListener(new BleDataForCustomRemind.DeleteCallback()
//        {
//            @Override
//            public void deleteCallback(byte a)
//            {
//
//            }
//        });
    }

    private void settingTheStatebarAndNavigationbar()
    {
        // 透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //底部虚拟键透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }
    PopupWindow popupWindow;

    NoDoubleClickListener listener = new NoDoubleClickListener()
    {
        @Override
        public void onNoDoubleClick(View v)
        {
            switch (v.getId())
            {
                case R.id.hr_remind_setting:
                    showHRWarningWindow();

                    break;
                case R.id.custom_layout:

                        byte[] bytess = new byte[dataList.size()];
                        if(dataList.size() != 0)
                        {
                            for (int i = 0; i < dataList.size(); i ++)
                            {
                                CustomRemindEntity en = dataList.get(i);
                                byte aa = en.getNumber();     // 获取提醒编号
                                bytess[i] = aa;
                            }
                        }
                        if(dataList.size() >= 8)
                        {
                            Toast.makeText(RemindActivity.this, R.string.remind_number, Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Intent intent = new Intent();
                            intent.setClass(RemindActivity.this, SubCustonRemindActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(MyConfingInfo.SEND_TO_THE_ACTIVITY, bytess);
                            startActivity(intent);
                        }
                    break;

                case R.id.back_button_setting:
                    if(popupWindow != null && popupWindow.isShowing())
                    {
                        popupWindow.dismiss();
                    }
                    break;

                case R.id.setting_the_warning_hr:
                    String maxHR = maxEdit.getText().toString().trim();
                    String minHR = minEdit.getText().toString().trim();

                    boolean isOk = checkTheMaxHRAndMinHR(maxHR, minHR);
                    if(isOk)
                    {
//                        BleDataForHRWarning.getInstance().setAndOpenTheHRWarningData(Integer.parseInt(maxHR), Integer.parseInt(minHR));
                    }

                    break;

                case R.id.setting_delay:
                    View viewLayout = getDelayWindow();
                    delayWindow = new PopupWindow(viewLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    delayWindow.setFocusable(true);
                    delayWindow.setAnimationStyle(R.style.mypopupwindow_anim_style_left_right);
                    delayWindow.setBackgroundDrawable(new BitmapDrawable());
                    delayWindow.showAtLocation(viewLayout, Gravity.CENTER, 0, 0);
                    delayWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
                    {
                        @Override
                        public void onDismiss()
                        {
                            delayWindow = null;
                        }
                    });
                    break;
            }
        }
    };

    private View getDelayWindow()
    {
        RecyclerForDelaySetting adapter = new RecyclerForDelaySetting(RemindActivity.this);
        View viewRoot = LayoutInflater.from(RemindActivity.this).inflate(R.layout.delay_setting_layout, null);
        ImageView tvBack = (ImageView)viewRoot.findViewById(R.id.delay_back_to_window);
        tvBack.setOnClickListener(new NoDoubleClickListener()
        {
            @Override
            public void onNoDoubleClick(View v)
            {
                if(delayWindow != null && delayWindow.isShowing())
                {
                    delayWindow.dismiss();
                }
            }
        });
        final TextView tvTimes = (TextView)viewRoot.findViewById(R.id.delay_time);
        if(tvTimes.getText() != null && tvTimes.getText().equals(""))
        {
            String remindS = "";
            SpannableString span = null;

            if(delayTime == 0)
            {
                remindS = getString(R.string.immediate_reminder);
                span = new SpannableString(remindS);
            }
            else
            {
                remindS = delayTime + getString(R.string.second) + getString(R.string.choose_result);
                span = new SpannableString(remindS);
                span.setSpan(new AbsoluteSizeSpan(26, true), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            tvTimes.setText(span);
        }
        TextView tvCompletion = (TextView)viewRoot.findViewById(R.id.choose_complete);
        tvCompletion.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                openTheSwitch();

                int sendData;
                String setting = tvTimes.getText().toString();
                if (setting != null && setting.equals(getString(R.string.immediate_reminder))) {
                    sendData = 0;
                } else {
                    String first = setting.substring(0, 1);
                    if (first != null && first.equals("clock") || first != null && first.equals("phone")) {
                        sendData = Integer.parseInt(setting.substring(0, 2));
                    } else {
                        sendData = Integer.parseInt(setting.substring(0, 1));
                    }
                }

//                BleDataForRingDelay.getDelayInstance().settingTheRingDelay(sendData);
                ProgressUtils.getInstance().showProgressDialog(RemindActivity.this, getString(R.string.delay_setting));
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (ProgressUtils.getInstance().isShowing()) {
                            ProgressUtils.getInstance().closeProgressDialog();
                            Toast.makeText(RemindActivity.this, getString(R.string.setting_failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 5000);

            }
        });

        final RecyclerView recyclerView = (RecyclerView)viewRoot.findViewById(R.id.recycle_for_show_delay_time);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(RemindActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.setTheItemOnClickListener(new RecyclerForDelaySetting.OnDelaySettingItemClickListener()
        {
            @Override
            public void onClickItem(String result)
            {
//                Toast.makeText(RemindActivity.this, "点击了" + result, Toast.LENGTH_SHORT).show();
                SpannableString span = null;
                if(result != null && result.equals(getString(R.string.immediate_reminder)))
                {
                    tvTimes.setText(result);
                }
                else
                {
                    String de = result + getString(R.string.choose_result);
                    span = new SpannableString(de);
                    span.setSpan(new AbsoluteSizeSpan(26, true), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvTimes.setText(span);
                }
            }
        });

        return viewRoot;
    }

    private void openTheSwitch()
    {
        phoneButton.setChecked(true);
        LocalDataSaveTool.getInstance(RemindActivity.this).writeSp(MyConfingInfo.PHONE_REMIND_CHANGE, MyConfingInfo.REMIND_OPEN);
    }

    private boolean checkTheMaxHRAndMinHR(String maxHR, String minHR)
    {
        if(maxHR == null || maxHR.equals(""))
        {
            Toast.makeText(RemindActivity.this, getString(R.string.setting_failed_not_null), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(minHR == null || minHR.equals(""))
        {
            Toast.makeText(RemindActivity.this, getString(R.string.setting_failed_not_null), Toast.LENGTH_SHORT).show();
            return false;
        }
        int maxHr = Integer.parseInt(maxHR);
        int minHr = Integer.parseInt(minHR);
        if(maxHr > 260)
        {
            Toast.makeText(RemindActivity.this, getString(R.string.setting_hr_max_falied), Toast.LENGTH_SHORT).show();
            return false;
        }

        if(minHr < 0)
        {
            Toast.makeText(RemindActivity.this, getString(R.string.setting_hr_min_falied), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(maxHr == minHr)
        {
            Toast.makeText(RemindActivity.this, getString(R.string.setting_hr_min_falied_not), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(maxHr < minHr)
        {
            Toast.makeText(RemindActivity.this, getString(R.string.setting_hr_min_falied_not_reverse), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;




    }

    private void showHRWarningWindow()
    {
        View view = getThePopwindowView();
        popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setAnimationStyle(R.style.mypopupwindow_anim_style_left_right);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popupWindow = null;
            }
        });
    }

    private int getStatusBarHeight()
    {
        int result = 0;
        int resurcedId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resurcedId > 0)
        {
            result = getResources().getDimensionPixelSize(resurcedId);
        }
        return result;
    }

    private View getThePopwindowView()
    {
        View v = LayoutInflater.from(RemindActivity.this).inflate(R.layout.popwindow_setting_hr_remind, null);
//        NestedScrollView scrollView = (NestedScrollView)v.findViewById(R.id.nestedScrollView);
//        scrollView.setPadding(0, getStatusBarHeight(), 0, 0);
        ImageView mBack = (ImageView) v.findViewById(R.id.back_button_setting);
        mBack.setOnClickListener(listener);

        maxEdit = (EditText)v.findViewById(R.id.max_hr_edit);
        minEdit = (EditText)v.findViewById(R.id.min_hr_edit);
        String result = LocalDataSaveTool.getInstance(RemindActivity.this).readSp(MyConfingInfo.HRWARNING_SETTING_VALUE);
        try
        {
            JSONObject json = new JSONObject(result);
            int max = json.getInt(MyConfingInfo.MAX_HR);
            int min = json.getInt(MyConfingInfo.MIN_HR);
            if(max != 0 && min != 0)
            {
                maxEdit.setText(String.valueOf(max));
                minEdit.setText(String.valueOf(min));
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        TextView setButton = (TextView)v.findViewById(R.id.setting_the_warning_hr);
        setButton.setOnClickListener(listener);
        return v;
    }


    // 监听checkbox，
    CheckBox.OnCheckedChangeListener checkListener = new CheckBox.OnCheckedChangeListener()
    {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            switch (buttonView.getId()) {
                case R.id.phone_remind_switch:   // 电话提醒开关状态改变
                    if (isChecked)                  // 选择了
                    {
                        LocalDataSaveTool.getInstance(RemindActivity.this).writeSp(MyConfingInfo.PHONE_REMIND_CHANGE, MyConfingInfo.REMIND_OPEN);
                    } else {
                        LocalDataSaveTool.getInstance(RemindActivity.this).writeSp(MyConfingInfo.PHONE_REMIND_CHANGE, MyConfingInfo.REMIND_CLOSE);
                    }
                    break;
                case R.id.sms_remind_switch:
                    if (isChecked)       // 选择了
                    {
                        LocalDataSaveTool.getInstance(RemindActivity.this).writeSp(MyConfingInfo.SMS_REMIND_CHANGE, MyConfingInfo.REMIND_OPEN);
                    } else {
                        LocalDataSaveTool.getInstance(RemindActivity.this).writeSp(MyConfingInfo.SMS_REMIND_CHANGE, MyConfingInfo.REMIND_CLOSE);
                    }
                    break;
                case R.id.hr_remind_switch:
                    if (isChecked)
                    {
                        String value = LocalDataSaveTool.getInstance(RemindActivity.this).readSp(MyConfingInfo.HRWARNING_SETTING_VALUE);
                        if (value != null && value.equals("")) {
                            Toast.makeText(RemindActivity.this, getString(R.string.hr_warning_range), Toast.LENGTH_SHORT).show();
                        } else {
//                            String values = LocalDataSaveTool.getInstance(RemindActivity.this).readSp(MyConfingInfo.HRWARNING_SETTING_VALUE);
                            try {
                                JSONObject json = new JSONObject(value);
                                int maxHRS = json.getInt(MyConfingInfo.MAX_HR);
                                int minHRS = json.getInt(MyConfingInfo.MIN_HR);
//                                BleDataForHRWarning.getInstance().setAndOpenTheHRWarningData(maxHRS, minHRS);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                    else
                    {
                        String value = LocalDataSaveTool.getInstance(RemindActivity.this).readSp(MyConfingInfo.HRWARNING_SETTING_VALUE);
                        try
                        {
                            JSONObject json = new JSONObject(value);
                            int maxHRS = json.getInt(MyConfingInfo.MAX_HR);
                            int minHRS = json.getInt(MyConfingInfo.MIN_HR);
//                            BleDataForHRWarning.getInstance().closeTheHRWarning(maxHRS, minHRS);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    break;
            }
        }
    };


    /**
     * 接收自定义提醒数据的广播
     */
    class  myRecever extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent.getAction().equals(MyConfingInfo.CUSTOM_REMIND_VALID))
            {
//                byte[] baseData = intent.getByteArrayExtra(MyConfingInfo.CUSTOM_REMIND_VALID_DATA);
//                Log.i(TAG, "自定义提醒数据：" + FormatUtils.bytesToHexString(baseData));
//                // 解析数据
//                final CustomRemindEntity entity = BleDataForCustomRemind.getCustomRemindDataInstance().dealTheValidData(RemindActivity.this, baseData);
//                if(entity != null )
//                {
////                    dataList.add(entity);   // 将解析后得到的实体类，添加到实体集合中去
////                    mRemindCustomRecyclerAdapter.notifyDataSetChanged();
//                      mRemindCustomRecyclerAdapter.addItem(entity, dataList.size());
//
//                    Log.i(TAG, "自定义提醒数据个数：" + dataList.size());
//                }
            }
            else if(intent.getAction().equals(MyConfingInfo.ACTION_FOR_HR_WARNING))
            {
                String data = LocalDataSaveTool.getInstance(RemindActivity.this).readSp(MyConfingInfo.HRWARNING_SETTING_VALUE);
                try {
                    JSONObject json = new JSONObject(data);
                    int state = json.getInt(MyConfingInfo.HR_WARNING_OPEN_OR_NO);
                    int maxHR = json.getInt(MyConfingInfo.MAX_HR);
                    int minHr = json.getInt(MyConfingInfo.MIN_HR);

                    hrSetting.setText(String.valueOf(getString(R.string.max) + maxHR + "/" + getString(R.string.min) + minHr));
                    Log.i(TAG, "预警设置结果:" + state);
                    if(state == 0)
                    {
                        if(!hrWarning.isChecked())
                        {
                            hrWarning.setChecked(true);
                        }
                    }
                    else if(state == 1)
                    {
                        if(hrWarning.isChecked())
                        {
                            hrWarning.setChecked(false);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(popupWindow != null && popupWindow.isShowing())
                {
                    popupWindow.dismiss();
                }
                if(ProgressUtils.getInstance().isShowing())
                {
                    ProgressUtils.getInstance().closeProgressDialog();
                }
            }
            else if(intent.getAction().equals(MyConfingInfo.BROADCAST_DELAY_DATA))
            {
                delayTime = intent.getIntExtra(MyConfingInfo.DELAY_DATA, 4);
                String remind = delayTime + getString(R.string.second) + getString(R.string.choose_result);
                if(delayTime == 0)
                {
                    remind = getString(R.string.immediate_reminder);
                }
                Log.i(TAG, "获取到的延时数据----" + delayTime);
                settingDelay.setText(remind);
                if(delayWindow != null && delayWindow.isShowing())
                {
                    delayWindow.dismiss();

                }
                if(ProgressUtils.getInstance().isShowing())
                {
                    ProgressUtils.getInstance().closeProgressDialog();
                }
            }

        }
    }





    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        myHandler.removeCallbacks(runn);
        return super.onKeyDown(keyCode, event);
    }
}
