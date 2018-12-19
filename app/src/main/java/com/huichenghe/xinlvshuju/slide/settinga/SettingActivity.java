package com.huichenghe.xinlvshuju.slide.settinga;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.PreferenceV.PreferencesService;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.appRemind.AppMessegeFragment;
import com.huichenghe.xinlvshuju.mainpack.BaseActivity;

import java.util.Map;

public class SettingActivity extends BaseActivity {
    private ImageView back;
    private RadioButton clockRemind, phoneRemind, smsRemind,
            sitRemind, heartWarning, lostRemind, sosRemind,
            brocastRemind, phoneSos;
    private LinearLayout settingPager;
    private Fragment clockFragment, infoFragment,
            heartFragment, lostFragment, ringFragment,
            sitFragment, sosFragment, dangerFragment, changeDeviceUIFragment;
    private View bra_div_line, remind_div_lost;

    private FragmentManager fragmentManager;
    private String ACCESS_INTENT_ACTION = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    public   static int  start;
    public   static int  falses;

    private PreferencesService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        intitViews();
        intitFragment();

        service =new PreferencesService(getApplicationContext());
        Map<String, String> params = service.getPreferences();
        //short_ed.setText(params.get("short_ed"));
        //height_ed.setText(params.get("height_ed"));
        String s = params.get("s1");
        String h = params.get("s2");
        Log.e("------s=", "--------------" + s);
        Log.e("------h=", "--------------" + h);



    }
    @Override
    protected void onResume() {
        super.onResume();
      //  intitFragment();
        String newType = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.DEVICE_INFO_TYPE);
        if (newType != null && newType.equals(MyConfingInfo.DEVICE_INFO_NEW)) {
            infoFragment = new AppMessegeFragment();
        } else {
            infoFragment = new InfoFragment();
        }
    }

    private void intitFragment() {
        fragmentManager = SettingActivity.this.getSupportFragmentManager();
        clockFragment = new ClickFragment();
        String newType = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.DEVICE_INFO_TYPE);
        //判断是不是新的设备
        if (newType != null && newType.equals(MyConfingInfo.DEVICE_INFO_NEW)) {
            infoFragment = new AppMessegeFragment();
        } else {
            infoFragment = new InfoFragment();
        }
        heartFragment = new HeartFragment();
        ringFragment = new RingFragment();
        sitFragment = new SitFragment();
        lostFragment = new LostFragment();
        dangerFragment = new DangerPhoneFragment();
        changeDeviceUIFragment = new DeviceUICtrolFragment();
//        sosFragment = new SosFragment();
        fragmentManager.beginTransaction().replace(R.id.viewPager_setting, clockFragment).commit();
    }

    private void intitViews() {
        back = (ImageView) findViewById(R.id.care_back);
        back.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                SettingActivity.this.onBackPressed();
            }
        });
        clockRemind = (RadioButton) findViewById(R.id.clock_for_remind);
        clockRemind.setChecked(true);
        phoneRemind = (RadioButton) findViewById(R.id.phone_for_remind);
        smsRemind = (RadioButton) findViewById(R.id.sms_for_remind);
        sitRemind = (RadioButton) findViewById(R.id.sit_for_remind);
        heartWarning = (RadioButton) findViewById(R.id.heart_for_warning);
        lostRemind = (RadioButton) findViewById(R.id.remind_for_lost);
        sosRemind = (RadioButton) findViewById(R.id.remind_for_sos);
        brocastRemind = (RadioButton) findViewById(R.id.broact_for_remind);
        phoneSos = (RadioButton) findViewById(R.id.phone_for_sos);
        clockRemind.setOnCheckedChangeListener(checkListener);
        phoneRemind.setOnCheckedChangeListener(checkListener);
        smsRemind.setOnCheckedChangeListener(checkListener);
        sitRemind.setOnCheckedChangeListener(checkListener);
        heartWarning.setOnCheckedChangeListener(checkListener);
        lostRemind.setOnCheckedChangeListener(checkListener);
        sosRemind.setOnCheckedChangeListener(checkListener);
        brocastRemind.setOnCheckedChangeListener(checkListener);
        phoneSos.setOnCheckedChangeListener(checkListener);
        settingPager = (LinearLayout) findViewById(R.id.viewPager_setting);
        bra_div_line = findViewById(R.id.broact_line);
        remind_div_lost = findViewById(R.id.remind_div);
//        addListener();
        checkBlood();
        checkDeviceUI();
    }
    private void checkDeviceUI() {
        String changeUI = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.DEVICE_CHANGE_UI);
        if (changeUI != null && changeUI.equals(MyConfingInfo.DEVICE_SHOW_UI)) {
            if (brocastRemind.getVisibility() == View.INVISIBLE) {
                brocastRemind.setVisibility(View.VISIBLE);
                bra_div_line.setVisibility(View.VISIBLE);
            }
        } else {
            if (brocastRemind.getVisibility() == View.VISIBLE) {
                brocastRemind.setVisibility(View.INVISIBLE);
                bra_div_line.setVisibility(View.INVISIBLE);
            }
        }

    }

    private void checkBlood() {
        String userDeviceType = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.USER_DEVICE_TYPE);
        if (userDeviceType != null && userDeviceType.equals(MyConfingInfo.DEVICE_HR)) {
            if (sosRemind.getVisibility() == View.INVISIBLE) {
                sosRemind.setVisibility(View.VISIBLE);
                remind_div_lost.setVisibility(View.VISIBLE);
            }
        } else if (userDeviceType != null && userDeviceType.equals("") || userDeviceType != null && userDeviceType.equals(MyConfingInfo.DEVICE_BLOOD)) {
            if (sosRemind.getVisibility() == View.VISIBLE) {
                sosRemind.setVisibility(View.INVISIBLE);
                remind_div_lost.setVisibility(View.INVISIBLE);
            }
        }
    }


    CompoundButton.OnCheckedChangeListener checkListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (clockRemind == buttonView && isChecked) {
                fragmentManager.beginTransaction().replace(R.id.viewPager_setting, clockFragment).commit();
            } else if (phoneRemind == buttonView && isChecked) {
                fragmentManager.beginTransaction().replace(R.id.viewPager_setting, ringFragment).commit();
            } else if (smsRemind == buttonView && isChecked) {
                fragmentManager.beginTransaction().replace(R.id.viewPager_setting, infoFragment).commit();
                //  暂时先屏蔽，可能会启动
                if(smsRemind.isChecked())
                {
                    if(isEnable())
                    {
                        // LocalDataSaveTool.getInstance(SettingActivity.this.getApplicationContext()).writeSp(MyConfingInfo.PUSH_MESSAGE, MyConfingInfo.PUSH_MESSAGE_TRUE);
                        falses=14;
                    }
                    else
                    {
                        openorclose(true);
                        start=13;
                    }
                }
                else
                {
                    //   LocalDataSaveTool.getInstance(SettingActivity.this.getApplicationContext()).writeSp(MyConfingInfo.PUSH_MESSAGE, MyConfingInfo.PUSH_MESSAGE_FALSE);

                }


            } else if (sitRemind == buttonView && isChecked) {
                fragmentManager.beginTransaction().replace(R.id.viewPager_setting, sitFragment).commit();

            } else if (heartWarning == buttonView && isChecked) {
                fragmentManager.beginTransaction().replace(R.id.viewPager_setting, heartFragment).commit();
            } else if (lostRemind == buttonView && isChecked) {
                fragmentManager.beginTransaction().replace(R.id.viewPager_setting, lostFragment).commit();
            } else if (sosRemind == buttonView && isChecked) {
                fragmentManager.beginTransaction().replace(R.id.viewPager_setting, dangerFragment).commit();
            } else if (brocastRemind == buttonView && isChecked) {
                fragmentManager.beginTransaction().replace(R.id.viewPager_setting, changeDeviceUIFragment).commit();
            } else if (phoneSos == buttonView && isChecked) {
                fragmentManager.beginTransaction().replace(R.id.viewPager_setting, sosFragment).commit();
            }
        }
    };

    private void openNoitficationAccess() {
        startActivity(new Intent(ACCESS_INTENT_ACTION));
    }

    private void initViewState(boolean checked) {
        if (checked) {
            //   暂时
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate( R.layout.dia_pic, null);
            RelativeLayout rl_bt= (RelativeLayout) layout.findViewById(R.id.rl_bt);
            final AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
            final AlertDialog dialog = builder.setView(layout).show();
//            Window dialogWindow = dialog.getWindow();
//            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//            dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
            rl_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openNoitficationAccess();
                    dialog.dismiss();
                    start=1;
                    Log.e("------------","------------"+start+1);

                }
            });


            return;
        } else {
//            showConfimDialog("授权通知", "确定取消授权吗？");
            falses=2;
            return;
        }
    }
    /**
     * 获取是否权限已打开
     *
     * @return
     */
    private String ENABLE_NOTIFICATION_LISTENER = "enabled_notification_listeners";

    private boolean isEnable() {
        String pakName = getApplicationContext().getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), ENABLE_NOTIFICATION_LISTENER);
        if (flat != null && !flat.isEmpty()) {
            String[] flatNames = flat.split(":");
            for (int i = 0; i < flatNames.length; i++) {
                ComponentName na = ComponentName.unflattenFromString(flatNames[i]);
                if (na != null) {
                    if (TextUtils.equals(pakName, na.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    //执行通知监听操作F
    private void openorclose(boolean isChecked) {
        initViewState(isChecked);
    }

    public   static  int  getStart(){
        return  start;
    }

}