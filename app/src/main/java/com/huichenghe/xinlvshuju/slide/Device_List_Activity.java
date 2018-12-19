package com.huichenghe.xinlvshuju.slide;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.bleControl.Ble.DeviceConfig;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;
import com.huichenghe.xinlvshuju.mainpack.BaseActivity;

public class Device_List_Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device__list_);
        initView();
    }


    private void initView()
    {
        ImageView va = (ImageView) findViewById(R.id.back_button);
        va.setOnClickListener(listener);
        findViewById(R.id.hr_brachlet_layout).setOnClickListener(listener);
        findViewById(R.id.blood_brachlet_layout).setOnClickListener(listener);
        String action = getIntent().getAction();
        if((action != null && action.equals("")) || action == null)
        {
            if(va.getVisibility() == View.VISIBLE)
            {
                va.setVisibility(View.GONE);
            }
        }
        else
        {
            if(va.getVisibility() == View.GONE)
            {
                va.setVisibility(View.VISIBLE);
            }
        }

        String type = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.USER_DEVICE_TYPE);
        if(type != null && type.equals(MyConfingInfo.DEVICE_BLOOD))
        {
            showBloodImageView();
        }
        else if(type != null && type.equals(MyConfingInfo.DEVICE_HR))
        {
            showHRImageView();
        }
        else
        {

        }


    }

    private void showHRImageView()
    {
        ImageView hrView = (ImageView) findViewById(R.id.select_icon_hr);
        if(hrView.getVisibility() == View.GONE)
        {
            hrView.setVisibility(View.VISIBLE);
        }
        ImageView bloodView = (ImageView) findViewById(R.id.select_icon_blood);
        if(bloodView.getVisibility() == View.VISIBLE)
        {
            bloodView.setVisibility(View.GONE);
        }
    }

    private void showBloodImageView()
    {
        ImageView bloodView = (ImageView) findViewById(R.id.select_icon_blood);
        if(bloodView.getVisibility() == View.GONE)
        {
            bloodView.setVisibility(View.VISIBLE);
        }

        ImageView hrView = (ImageView) findViewById(R.id.select_icon_hr);
        if(hrView.getVisibility() == View.VISIBLE)
        {
            hrView.setVisibility(View.GONE);
        }

    }

    NoDoubleClickListener listener = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {

            switch (v.getId())
            {
                case R.id.back_button:
                    Device_List_Activity.this.onBackPressed();
                    break;
                case R.id.hr_brachlet_layout:
                    String type = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.USER_DEVICE_TYPE);
                    if(type != null && type.equals(MyConfingInfo.DEVICE_HR))
                    {
                        Device_List_Activity.this.onBackPressed();
                        MyToastUitls.showToast(getApplicationContext(), R.string.current_device_is_brancelet, 2);
                    }
                    else if(type != null && type.equals(MyConfingInfo.DEVICE_BLOOD) || type != null && type.equals(""))
                    {
                        String deviceName = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(DeviceConfig.DEVICE_NAME);
                        if(deviceName != null && deviceName.equals(""))
                        {
                            LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.USER_DEVICE_TYPE, MyConfingInfo.DEVICE_HR);
                            sendBroadcast(new Intent(MyConfingInfo.CHAGE_DEVICE_TYPE));
                            sendBroadcast(new Intent(MyConfingInfo.CLOSE_DEVICE_AMD_ACTIVITY));
                            Intent intent = new Intent();
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            String ac = getIntent().getAction();
                            if(ac != null)
                            {
//                                intent.putExtra("Device_amd_enter", "Device_amd_enter");
                            }
                            else
                            {
                                intent.setAction(MyConfingInfo.ACTION_SEARCH_DEVICE);
                            }
                            intent.setClass(getApplicationContext(), DeviceAmdActivity.class);
                            startActivity(intent);
                            MyToastUitls.showToast(getApplicationContext(), R.string.already_change_state_to_brancelet, 2);
                        }
                        else
                        {
                            MyToastUitls.showToast(getApplicationContext(), R.string.unbind_current_device, 2);
                        }
                        Device_List_Activity.this.onBackPressed();
//                        forgetDevice();
                    }
                    break;
                case R.id.blood_brachlet_layout:
                    String types = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.USER_DEVICE_TYPE);
                    if(types != null && types.equals(MyConfingInfo.DEVICE_BLOOD))
                    {
                        Device_List_Activity.this.onBackPressed();
                        MyToastUitls.showToast(getApplicationContext(), R.string.current_device_is_watch, 2);
                    }
                    else if(types != null && types.equals(MyConfingInfo.DEVICE_HR) || types != null && types.equals(""))
                    {
                        String deviceName = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(DeviceConfig.DEVICE_NAME);
                        if(deviceName != null && deviceName.equals(""))
                        {
                            sendBroadcast(new Intent(MyConfingInfo.CHAGE_DEVICE_TYPE));
                            sendBroadcast(new Intent(MyConfingInfo.CLOSE_DEVICE_AMD_ACTIVITY));
                            LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.USER_DEVICE_TYPE, MyConfingInfo.DEVICE_BLOOD);
                            Intent intents = new Intent();
                            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            String ac = getIntent().getAction();
                            if(ac != null)
                            {
//                                intents.putExtra("Device_amd_enter", "Device_amd_enter");
                            }
                            else
                            {
                                intents.setAction(MyConfingInfo.ACTION_SEARCH_DEVICE);
                            }
                            intents.setClass(getApplicationContext(), DeviceAmdActivity.class);
                            startActivity(intents);
                            MyToastUitls.showToast(getApplicationContext(), R.string.already_change_state_to_watch, 2);
                        }
                        else
                        {
                            MyToastUitls.showToast(getApplicationContext(), R.string.unbind_current_device, 2);
                        }
                        Device_List_Activity.this.onBackPressed();
//                        forgetDevice();

                    }
                    break;
            }
        }
    };

    private void forgetDevice()
    {
        if(BluetoothLeService.getInstance() != null)
        {
            BluetoothLeService.getInstance().close(false);
        }
        LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(DeviceConfig.DEVICE_ADDRESS, "");
        LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(DeviceConfig.DEVICE_NAME, "");
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        String action = getIntent().getAction();
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK)
        {
            if((action != null && action.equals("")) || action == null)
            {
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
