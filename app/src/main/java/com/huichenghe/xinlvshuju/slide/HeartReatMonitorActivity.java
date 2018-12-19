package com.huichenghe.xinlvshuju.slide;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.huichenghe.bleControl.Ble.DataSendCallback;
import com.huichenghe.xinlvshuju.Adapter.HeartRateManitorAdapter;
import com.huichenghe.bleControl.Ble.BleDataForSettingArgs;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import java.util.ArrayList;

public class HeartReatMonitorActivity extends AppCompatActivity
{
    private static final String TAG = HeartReatMonitorActivity.class.getSimpleName();
    private RelativeLayout fatigueModle;
    private AppCompatSpinner heartReatMonnitor;
    private HeartRateManitorAdapter adapter;
    private ArrayList<Integer> dataList;
    private CheckBox checked;
    private ImageView backToMain;
    private final int UPDATEHEARTREAT = 0;
    private final int UPDATEFATIGUE = 1;

    private Handler myHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case UPDATEHEARTREAT:
                    for (int i = 0; i < dataList.size(); i++)
                    {
                        if(dataList.get(i) == msg.arg1)
                        {
                            heartReatMonnitor.setSelection(i);
                        }
                    }
                    break;
                case UPDATEFATIGUE:
                    if(msg.arg1 == 0)
                    {
                        checked.setChecked(false);
                    }
                    else
                    {
                        checked.setChecked(true);
                    }
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_reat_monitor);
        initView();
    }

    private void initView()
    {
        backToMain = (ImageView)findViewById(R.id.back_to_main);
        backToMain.setOnClickListener(listener);
        checked = (CheckBox)findViewById(R.id.swich_button);
        fatigueModle = (RelativeLayout)findViewById(R.id.fatigue_layout);
        heartReatMonnitor = (AppCompatSpinner)findViewById(R.id.spinner_layout);
        dataList = new ArrayList<>();
        dataList.add(10);
        dataList.add(20);
        dataList.add(30);
        dataList.add(60);
        adapter = new HeartRateManitorAdapter(dataList, HeartReatMonitorActivity.this, heartReatMonnitor);
        heartReatMonnitor.setAdapter(adapter);
        heartReatMonnitor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Log.i(TAG, "走了这里lllhearreatMon" );
                int times = dataList.get(position);
                BleDataForSettingArgs.getInstance(HeartReatMonitorActivity.this.getApplicationContext()).setHeartReatArgs((byte)times);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });
        fatigueModle.setOnClickListener(listener);
        readHeartReatAndFatigue();
    }

    NoDoubleClickListener listener = new NoDoubleClickListener()
    {
        @Override
        public void onNoDoubleClick(View v)
        {
            if(v == backToMain)
            {
                HeartReatMonitorActivity.this.onBackPressed();
            }
            else if(v == fatigueModle)
            {
                if(checked.isChecked())
                {
                    checked.setChecked(false);
                    BleDataForSettingArgs.getInstance(HeartReatMonitorActivity.this).setFatigueSwich((byte)0x00);
                }
                else if(!checked.isChecked())
                {
                    checked.setChecked(true);
                    BleDataForSettingArgs.getInstance(HeartReatMonitorActivity.this).setFatigueSwich((byte)0x01);
                }
            }
        }
    };

    /**
     * 读取监测频率和开关状态
     */
    private void readHeartReatAndFatigue()
    {
        BleDataForSettingArgs.getInstance(HeartReatMonitorActivity.this).readHeartAndFatigue();
        BleDataForSettingArgs.getInstance(HeartReatMonitorActivity.this)
                .setOnArgsBackListener(new DataSendCallback() {
                    @Override
                    public void sendSuccess(byte[] receveData)
                    {
//                0002050301fa16
                                if(receveData[1] == (byte)0x02)
                                {
                                    Message msg = Message.obtain();
                                    msg.what = UPDATEHEARTREAT;
                                    msg.arg1 = receveData[2];
                                    myHandler.sendMessage(msg);
                                }
                                if(receveData[3] == (byte)0x03)
                                {
//                    Log.i(TAG, "返回的开关状态：" + butterData[4]);
                                    Message msg = Message.obtain();
                                    msg.what = UPDATEFATIGUE;
                                    msg.arg1 = receveData[4];
                                    myHandler.sendMessage(msg);
                                }
                    }

                    @Override
                    public void sendFailed() {

                    }

                    @Override
                    public void sendFinished() {

                    }
                });
    }
}
