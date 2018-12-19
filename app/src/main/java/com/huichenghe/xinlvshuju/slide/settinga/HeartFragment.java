package com.huichenghe.xinlvshuju.slide.settinga;


import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huichenghe.bleControl.Ble.DataSendCallback;
import com.huichenghe.xinlvshuju.Adapter.frequencyAdapter;
import com.huichenghe.bleControl.Ble.BleDataForHRWarning;
import com.huichenghe.bleControl.Ble.BleDataForSettingArgs;
import com.huichenghe.xinlvshuju.CustomView.HrWarningWindow;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HeartFragment extends Fragment
{
    public final static String TAG = HeartFragment.class.getSimpleName();
    private RelativeLayout warningSetting;
    private RelativeLayout swichWarning, selectFrequency, heartSwitch;
    private LinearLayout warningLayout;
    private TextView maxTV, minTV;
    private CheckBox heartSwitchButton;
    private TextView frequecyContent;
    private int maxString, minString;
    private int switchState;
    private CheckBox switchButton;
    private Activity mActivity;
    private PopupWindow freguencyWindow;
    private final int HANDLER_MESSAGE = 0;
    private final int HEART_FREQENCY = 1;
    private final String switchs = "switchs";
    private final String maxHr = "max_hr";
    private final String minHr = "min_hr";
    private final String freguencys = "freguencys";
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case HANDLER_MESSAGE:
                    String maxT = mActivity.getString(R.string.max_warning_hr);
                    String minT = mActivity.getString(R.string.min_warning_hr);
                    maxTV.setText(maxT + "\u3000\u3000" + maxString);
                    minTV.setText(minT + "\u3000\u3000" + minString);
                    switchButton.setChecked(switchState == 0);
                    JSONObject json = new JSONObject();
                    try {
                        json.put(MyConfingInfo.HR_WARNING_OPEN_OR_NO, switchState);
                        json.put(MyConfingInfo.MAX_HR, maxString);
                        json.put(MyConfingInfo.MIN_HR, minString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.HRWARNING_SETTING_VALUE, json.toString());
                    switch (switchState)
                    {
                        case 0:
                            if(warningLayout.getVisibility() == View.GONE)
                            {
                                warningLayout.setVisibility(View.VISIBLE);
                            }
                            break;
                        case 1:

                            if(warningLayout.getVisibility() == View.VISIBLE)
                            {
                                warningLayout.setVisibility(View.GONE);
                            }
                            break;
                    }
                    break;
                case HEART_FREQENCY:
                    StringBuffer sb = new StringBuffer();
                    int hearData = msg.arg1;
                    int sw = msg.arg2;
                    if(hearData > 30)
                    {
                        hearData = 30;
                    }
                    if(hearData < 10 && hearData > 1)
                    {
                        hearData = 10;
                    }
                    if(hearData == 1)
                    {
                        if(getSupport())
                        {
                            sb.append(mActivity.getString(R.string.continue_moni));
                        }
                        else
                        {
                            hearData = 30;
                            sb.append(String.valueOf(hearData));
                            sb.append(mActivity.getString(R.string.freguency_unit));
                        }
                    }
                    else
                    {
                        sb.append(String.valueOf(hearData));
                        sb.append(mActivity.getString(R.string.freguency_unit));
                    }
                    frequecyContent.setText(sb.toString());
                    heartSwitchButton.setChecked(sw == 1);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(switchs, sw);
                        jsonObject.put(freguencys, hearData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.HR_FREQENCY, jsonObject.toString());
                    if(sw == 0)
                    {
                        if(selectFrequency.getVisibility() == View.VISIBLE)
                        {
                            selectFrequency.setVisibility(View.GONE);
                        }
                    }
                    else
                    {
                        if(selectFrequency.getVisibility() == View.GONE)
                        {
                            selectFrequency.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
            }
        }
    };

    public HeartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return initView(inflater, container);

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    private View initView(LayoutInflater inflater, ViewGroup container)
    {
        LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.fragment_heart, container, false);
        frequecyContent = (TextView)linearLayout.findViewById(R.id.heart_frequency_content);
        selectFrequency = (RelativeLayout)linearLayout.findViewById(R.id.select_frequency);
        heartSwitch = (RelativeLayout)linearLayout.findViewById(R.id.heart_monitor);
        heartSwitchButton = (CheckBox)linearLayout.findViewById(R.id.heart_switch);
        swichWarning = (RelativeLayout)linearLayout.findViewById(R.id.swich_hr_layout);
        warningLayout = (LinearLayout)linearLayout.findViewById(R.id.warning_layout);
        warningSetting = (RelativeLayout)linearLayout.findViewById(R.id.warning_range_setting);
        switchButton = (CheckBox)linearLayout.findViewById(R.id.swich_hr_warning);
        maxTV = (TextView)linearLayout.findViewById(R.id.max_hr_data);
        minTV = (TextView)linearLayout.findViewById(R.id.min_hr_data);
        readCatch();
        warningSetting.setOnClickListener(listener);
        swichWarning.setOnClickListener(listener);
        heartSwitch.setOnClickListener(listener);
        selectFrequency.setOnClickListener(listener);
//        readOpenOrClose();
        readWarningdata();
        return linearLayout;
    }

    private void readCatch()
    {
        String maxT = mActivity.getString(R.string.max_warning_hr);
        String minT = mActivity.getString(R.string.min_warning_hr);
        String freqency = LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).readSp(MyConfingInfo.HR_FREQENCY);
        String hrWarning = LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).readSp(MyConfingInfo.HRWARNING_SETTING_VALUE);
        if(freqency != null && !freqency.equals(""))
        {
            JSONObject json = null;
            int sw = 0;
            int fre = 10;
            try {
                json = new JSONObject(freqency);
                sw = json.getInt(switchs);
                fre = json.getInt(freguencys);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            if(fre > 30)
            {
                fre = 30;
            }
            if(fre < 10 && fre > 1)
            {
                fre = 10;
            }

            StringBuffer sb = new StringBuffer();
            if(fre == 1)
            {
                if(getSupport())
                {
                    sb.append(mActivity.getString(R.string.continue_moni));
                }
                else
                {
                    fre = 30;
                    sb.append(String.valueOf(fre));
                    sb.append(mActivity.getString(R.string.freguency_unit));
                }
            }
            else
            {
                sb.append(String.valueOf(fre));
                sb.append(mActivity.getString(R.string.freguency_unit));
            }
            frequecyContent.setText(sb.toString());
            switch (sw)
            {
                case 1:
                    if(!heartSwitchButton.isChecked())
                    {
                        heartSwitchButton.setChecked(true);
                    }
                    if(selectFrequency.getVisibility() == View.GONE)
                    {
                        selectFrequency.setVisibility(View.VISIBLE);
                    }
                    break;
                case 0:
                    if(heartSwitchButton.isChecked())
                    {
                        heartSwitchButton.setChecked(false);
                    }
                    if(selectFrequency.getVisibility() == View.VISIBLE)
                    {
                        selectFrequency.setVisibility(View.GONE);
                    }
                    break;
            }
        }
        if(hrWarning != null && !hrWarning.equals(""))
        {
            JSONObject js = null;
            int sw = 1;
            int ma = 180;
            int mi = 50;
            try {
                js = new JSONObject(hrWarning);
                sw = js.getInt(MyConfingInfo.HR_WARNING_OPEN_OR_NO);
                ma = js.getInt(MyConfingInfo.MAX_HR);
                mi = js.getInt(MyConfingInfo.MIN_HR);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            maxTV.setText(maxT + "\u3000\u3000" + ma);
            minTV.setText(minT + "\u3000\u3000" + mi);
            switch (sw)
            {
                case 0:
                    if(!switchButton.isChecked())
                    {
                        switchButton.setChecked(true);
                    }
                    if(warningLayout.getVisibility() == View.GONE)
                    {
                        warningLayout.setVisibility(View.VISIBLE);
                    }
                    break;
                case 1:
                    if(switchButton.isChecked())
                    {
                        switchButton.setChecked(false);
                    }
                    if(warningLayout.getVisibility() == View.VISIBLE)
                    {
                        warningLayout.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    }


    private void readWarningdata()
    {
        BleDataForSettingArgs.getInstance(mActivity.getApplicationContext()).setOnArgsBackListener(new DataSendCallback() {
            @Override
            public void sendSuccess(byte[] receveData)
            {
//                Log.i(TAG, "返回的数据xinlv：" + FormatUtils.bytesToHexString(butterData));
//                00020a04010016
                  if(receveData[1] == (byte)0x02 && receveData[3] == (byte)0x04)
                  {
                       Message msg = Message.obtain();
                       msg.what = HEART_FREQENCY;
                       msg.arg1 = receveData[2];
                       msg.arg2 = receveData[4];
                       mHandler.sendMessage(msg);
                   }
            }
            @Override
            public void sendFailed(){}
            @Override
            public void sendFinished()
            {
                BleDataForHRWarning.getInstance().setDataSendCallback(new WarningDataListener());
                BleDataForHRWarning.getInstance().requestWarningData();
            }
        });
        BleDataForSettingArgs.getInstance(mActivity.getApplicationContext()).readHeartAndFatigue();

//        BleDataForHRWarning.getInstance().setOnWarningDataback(new BleDataForHRWarning.OnWarningDataback()
//        {
//            @Override
//            public void onBack(int maxHR, int minHR, int state)
//            {
//                minString = minHR;
//                maxString = maxHR;
//                switchState = state;
//                mHandler.sendEmptyMessage(HANDLER_MESSAGE);
//            }
//        });
    }

    public boolean getSupport()
    {
        String continueM = LocalDataSaveTool.getInstance(mActivity).readSp(MyConfingInfo.CHECK_SUPPORT_HEAETS);
        if(continueM != null && continueM.equals(MyConfingInfo.SUPPORT_TRUE))
        {
            return true;
        }
        return false;
    }


    class WarningDataListener implements DataSendCallback
    {
        @Override
        public void sendSuccess(byte[] receveData)
        {
            // 返回最大和最小心率
            Log.i(TAG, "当前获取心率范围：" + FormatUtils.bytesToHexString(receveData));
//            02000000fe16
            maxString = receveData[2] & 0xff;
            minString = receveData[3] & 0xff;
            switchState = receveData[1] & 0xff;
            mHandler.sendEmptyMessage(HANDLER_MESSAGE);
//            maxTV.setText(new StringBuffer().append(getString(R.string.max_warning_hr)).append("\u3000").append(maxString));
//            minTV.setText(new StringBuffer().append(getString(R.string.min_warning_hr)).append("\u3000").append(minString));
        }
        @Override
        public void sendFailed(){}
        @Override
        public void sendFinished() { }
    }

    NoDoubleClickListener listener = new NoDoubleClickListener()
    {
        @Override
        public void onNoDoubleClick(View v)
        {
            if(v == warningSetting)
            {
                new HrWarningWindow(mActivity);
            }
            else if(v == swichWarning)
            {
                if(switchButton.isChecked())
                {
                    switchButton.setChecked(false);
                    BleDataForHRWarning.getInstance().closeOrOpenWarning(maxString, minString, (byte)0x01);
                    if(warningLayout.getVisibility() == View.VISIBLE)
                    {
                        warningLayout.setVisibility(View.GONE);
                    }
                }
                else
                {
                    switchButton.setChecked(true);
                    BleDataForHRWarning.getInstance().closeOrOpenWarning(maxString, minString, (byte)0x00);
                    if(warningLayout.getVisibility() == View.GONE)
                    {
                        warningLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
            else if(v == heartSwitch)   // 开关心率
            {
                if(heartSwitchButton.isChecked())
                {
                    heartSwitchButton.setChecked(false);
                    BleDataForSettingArgs.getInstance(mActivity.getApplicationContext()).setFatigueSwich((byte)0x00);
                    if(selectFrequency.getVisibility() == View.VISIBLE)
                    selectFrequency.setVisibility(View.GONE);
                }
                else
                {
                    heartSwitchButton.setChecked(true);
                    BleDataForSettingArgs.getInstance(mActivity.getApplicationContext()).setFatigueSwich((byte)0x01);
                    if(selectFrequency.getVisibility() == View.GONE)
                        selectFrequency.setVisibility(View.VISIBLE);
                }
            }
            else if (v == selectFrequency)    // 显示频率
            {

                //todo 心率
                if(getSupport())
                {
                    String fre = frequecyContent.getText().toString();
                    int content = 30;
                    if(fre != null && fre.equals(mActivity.getString(R.string.continue_moni)))
                    {
                        content = 1;
                    }
                    else
                    {
                        int indexLast = fre.indexOf(getString(R.string.freguency_unit));
                        content = Integer.valueOf(fre.substring(0, indexLast));
                    }
                    int[] locations = new int[2];
                    selectFrequency.getLocationInWindow(locations);
                    showSelectWindow(content, locations);
                }
                else
                {
                    int indexLast = frequecyContent.getText().toString().indexOf(getString(R.string.freguency_unit));
                    String content = frequecyContent.getText().toString().substring(0, indexLast);
//                Log.i(TAG, "后缀下标" + indexLast);
                    int[] location = new int[2];
                    selectFrequency.getLocationInWindow(location);
                    showSelectWindow(Integer.parseInt(content), location);
                }
            }
        }
    };

    private void showSelectWindow(int content, int[] location)
    {

        View view = getLayoutWindow(content);
        freguencyWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        freguencyWindow.setFocusable(true);
        freguencyWindow.setBackgroundDrawable(new BitmapDrawable());
        freguencyWindow.showAsDropDown(view, location[0] + 120, 50);
        setWindowBackgound(0.8f);
        FrameLayout.LayoutParams pa = (FrameLayout.LayoutParams) view.getLayoutParams();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        pa.width = metrics.widthPixels / 2;
        view.setLayoutParams(pa);
        freguencyWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                freguencyWindow = null;
                setWindowBackgound(1.0f);
            }
        });
    }

    private int getWindowWidth()
    {
        DisplayMetrics metrics = mActivity.getResources().getDisplayMetrics();
        return metrics.widthPixels/2;
    }

    public void setWindowBackgound(float i)
    {
        WindowManager.LayoutParams params = mActivity.getWindow().getAttributes();
        params.alpha = i;
        mActivity.getWindow().setAttributes(params);
    }

//todo 修改
    private View getLayoutWindow(int i)
    {
        final ArrayList<Integer>  dataList = new ArrayList<>();
        dataList.add(10);
        dataList.add(30);
        if(getSupport())
        {
            dataList.add(1);
        }
        View view = LayoutInflater.from(mActivity).inflate(R.layout.frequency_layout, null);
        frequencyAdapter adapter = new frequencyAdapter(dataList, getContext(), i);
        RecyclerView recycler = (RecyclerView)view.findViewById(R.id.recycler_view_frequecy);
        recycler.setHasFixedSize(true);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);
        adapter.setOnFrequencyClickListner(new frequencyAdapter.OnFrequencyItemClick() {
            @Override
            public void onItemClick(int position) {
                if(freguencyWindow != null && freguencyWindow.isShowing())
                {
                    freguencyWindow.dismiss();
                    if(dataList.get(position) == 1)
                    {
                        frequecyContent.setText(mActivity.getString(R.string.continue_moni));
                        BleDataForSettingArgs.getInstance(mActivity.getApplicationContext()).setHeartReatArgs((byte)0x01);
                    }
                    else
                    {
                        frequecyContent.setText(dataList.get(position) + mActivity.getString(R.string.freguency_unit));
                        BleDataForSettingArgs.getInstance(mActivity.getApplicationContext()).setHeartReatArgs((byte)(int)dataList.get(position));
                    }

                }
            }
        });
        return view;
    }
}
