package com.huichenghe.xinlvshuju.slide.settinga;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huichenghe.bleControl.Ble.DataSendCallback;
import com.huichenghe.bleControl.Ble.BleDataForRingDelay;
import com.huichenghe.bleControl.Ble.BleForPhoneAndSmsRemind;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class RingFragment extends Fragment
{
    public static final String TAG = RingFragment.class.getSimpleName();
    private RelativeLayout phoneSwitch;
    private CheckBox checkBox;
    private ArrayList<String> data;
    private Activity mActivity;
    private int DELAY_TIME = 0;
    private int delayTime = -1;
    private RelativeLayout chooseTime;
    private PopupWindow delayWindow;
    private TextView delayData;
    private Handler ringHandler = new Handler();

    public RingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return initView(inflater, container);
    }
    private View initView(LayoutInflater inflater, ViewGroup container)
    {
        View view = inflater.inflate(R.layout.fragment_ring, container, false);
        data = new ArrayList<>();
        checkBox = (CheckBox)view.findViewById(R.id.check_open);
        phoneSwitch = (RelativeLayout) view.findViewById(R.id.switch_the_ring_button_new);
        phoneSwitch.setOnClickListener(listener);
        chooseTime = (RelativeLayout)view.findViewById(R.id.choose_time_layout);
        chooseTime.setOnClickListener(listener);
        delayData = (TextView)view.findViewById(R.id.delay_data_show);
        intiList();
        StringBuffer buffer = new StringBuffer();
        buffer.append(getString(R.string.immediate_reminder));
        delayData.setText(buffer.toString());
        initSwitch();
        requestRingData();
        return view;
    }

    private void initSwitch()
    {
        final String phone = LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).readSp(MyConfingInfo.PHONE_REMIND_CHANGE);
            if(phone != null && phone.equals(MyConfingInfo.REMIND_OPEN))
            {
                openRing();
            }
            else
            {
                closeRing();
            }
    }

    private void intiList()
    {
        data.add(getString(R.string.immediate_reminder));
        data.add(String.valueOf(4));
        data.add(String.valueOf(10));
//        for (int i = 4; i <= 22; i+=2)
//        {
//            if(i == 22)
//            {
//                data.add(getString(R.string.immediate_reminder));
//            }
//            else
//            {
//                data.add(String.valueOf(i));
//            }
//        }
    }

    NoDoubleClickListener listener = new NoDoubleClickListener() {
    @Override
    public void onNoDoubleClick(View v)
    {
        if(v == phoneSwitch)
        {
            if(checkBox.isChecked())
            {
                closeRing();
                BleForPhoneAndSmsRemind.getInstance().openPhoneRemine((byte)0x03, (byte)0x00);
            }
            else
            {
                openRing();
                BleForPhoneAndSmsRemind.getInstance().openPhoneRemine((byte)0x03, (byte)0x01);
            }
        }
        else if(v == chooseTime)
        {
            int position = 0;
            String delay = delayData.getText().toString();
            if(delay != null && delay.equals(getString(R.string.immediate_reminder)))
            {
                position = 0;
            }
            else if(delay != null && delay.equals(getString(R.string.deley_ring_time) + "4" + getString(R.string.second)))
            {
                position = 1;
//                String con = delay.substring(0, delay.indexOf(getString(R.string.second)));
//                for (int i = 0; i < data.size(); i++)
//                {
//                    String content = data.get(i);
//                    if(con.equals(content))
//                    {
//                        position = i;
//                    }
//                }
            }
            else
            {
                position = 2;
            }
            showPopwindow(position);
        }
        }
    };

    private void openRing()
    {
        if(checkBox != null && !checkBox.isChecked())
        {
            checkBox.setChecked(true);
        }
        LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.PHONE_REMIND_CHANGE, MyConfingInfo.REMIND_OPEN);
        if(chooseTime.getVisibility() == View.GONE)
        {
            chooseTime.setVisibility(View.VISIBLE);
        }
    }

    private void closeRing()
    {
        if(checkBox != null && checkBox.isChecked())
        {
            checkBox.setChecked(false);
        }
        LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.PHONE_REMIND_CHANGE, MyConfingInfo.REMIND_CLOSE);
        if(chooseTime.getVisibility() == View.VISIBLE)
        {
            chooseTime.setVisibility(View.GONE);
        }
    }

    public void requestRingData()
    {
        BleDataForRingDelay delay = BleDataForRingDelay.getDelayInstance();
        delay.addListener(new DelayDataListener());
        delay.getDelayData();
    }


    class DelayDataListener implements DataSendCallback
    {
        @Override
        public void sendSuccess(byte[] receveData)
        {
            byte type = receveData[0];
            if(type == (byte)0x02)
            {
                final byte delayTime = receveData[1];
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "电话延时数据：" + delayTime);
                        if(delayTime == 0)
                        {
//                          delaySpinner.setSelection(data.size() - 1);
                            delayData.setText(R.string.immediate_reminder);
                        }
                        else
                        {
                            StringBuffer buffer = new StringBuffer();
                            buffer.append(getString(R.string.deley_ring_time));
                            buffer.append(String.valueOf(delayTime));
                            buffer.append(getString(R.string.second));
                            delayData.setText(buffer.toString());
                        }
                    }
                });
            }
            else if(type == (byte)0x01)
            {
//            sendToGetTheDealyData();
            }
        }

        @Override
        public void sendFailed()
        {

        }

        @Override
        public void sendFinished()
        {
            BleForPhoneAndSmsRemind.getInstance().setOnDataListener(new MyRemindListener());
            BleForPhoneAndSmsRemind.getInstance().readPhoneRemindStatus();
        }
    }


    class MyRemindListener implements DataSendCallback
    {
        @Override
        public void sendSuccess(byte[] receveData)
        {
            dealReadData(receveData);
        }
        @Override
        public void sendFailed() {

        }
        @Override
        public void sendFinished() {

        }
    }

    /**
     * 处理读取到的数据
     * @param data
     */
    private void dealReadData(final byte[] data)
    {
//        010301f516
//        Log.i(TAG, "处理读取到的数据：" + FormatUtils.bytesToHexString(data));
        ringHandler.post(new Runnable() {
            @Override
            public void run() {
                if(data[0] == (byte)0x01 && data[1] == (byte)0x03)
                {

                    if(data[2] == (byte)0x01)
                    {

                        openRing();
                    }
                    else
                    {
                        closeRing();
                    }
                }
            }
        });

    }


    private void showPopwindow(int position)
    {
        View view = getXMLLayout(position);
        delayWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        delayWindow.setFocusable(true);
//        delayWindow.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.dialogshadow));

        delayWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        setAlph(0.8f);
        delayWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss()
            {
                delayWindow = null;
                setAlph(1.0f);
            }
        });
    }


    private void setAlph(float alph)
    {
        WindowManager.LayoutParams params = mActivity.getWindow().getAttributes();
        params.alpha = alph;
        mActivity.getWindow().setAttributes(params);
    }

    private View getXMLLayout(int position)
    {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.ring_layout_for_delay, null);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.delay_layout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        DelayAdapter adapter = new DelayAdapter(mActivity, data, position);
        adapter.setOnDealItemClick(new OnDealItemClick() {
            @Override
            public void onClick(int position)
            {
                if(position == 0)
                {
                    delayData.setText(getString(R.string.immediate_reminder));
                    BleDataForRingDelay.getDelayInstance().settingDelayData(0);
                }
                else
                {
                    StringBuffer b = new StringBuffer();
                    b.append(getString(R.string.deley_ring_time));
                    b.append(data.get(position));
                    b.append(getString(R.string.second));
                    delayData.setText(b.toString());
                    int de = Integer.parseInt(data.get(position));
                    BleDataForRingDelay.getDelayInstance().settingDelayData(de);
                }
                delayWindow.dismiss();
            }

            @Override
            public void onLongClick(int position) {

            }
        });
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }
}
