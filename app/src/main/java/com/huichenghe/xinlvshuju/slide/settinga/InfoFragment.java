package com.huichenghe.xinlvshuju.slide.settinga;


import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huichenghe.bleControl.Ble.BleForPhoneAndSmsRemind;
import com.huichenghe.bleControl.Ble.BleForQQWeiChartFacebook;
import com.huichenghe.bleControl.Ble.DataSendCallback;
import com.huichenghe.xinlvshuju.Adapter.AdapterForInfoRemind;
import com.huichenghe.xinlvshuju.DataEntites.Info_entry;
import com.huichenghe.xinlvshuju.NotifyService;
import com.huichenghe.xinlvshuju.PreferenceV.PreferencesService;
import com.huichenghe.xinlvshuju.PreferenceV.SharedPreferencesUtil;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment implements View.OnClickListener {
    private final static String TAG = InfoFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private ArrayList<Info_entry> enties;
    private AdapterForInfoRemind adapterForInfoRemind;
    private Activity mActivity;
    private String SP_SMS_REMIND = "sp sms remind";
    private String ENABLE_NOTIFICATION_LISTENER = "enabled_notification_listeners";
    private String ACCESS_INTENT_ACTION = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private RelativeLayout rl_rest_o;
    private CheckBox new_add;
    private RelativeLayout rl_each_info_for_remind;
    private boolean jin = true;
    private boolean kongzhi = true;
    private String i1;
    private String j1;
    private PreferencesService service;
    String s6;
    String s7;
    private LayoutInflater layoutInflater;
    String isSmsChecked;


    public InfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        service = new PreferencesService(getContext());
        layoutInflater = LayoutInflater.from(getActivity());
        return initView(inflater, container);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {

            if (new_add.isChecked() == true) {
                rl_each_info_for_remind.setVisibility(View.VISIBLE);
            } else {
                rl_each_info_for_remind.setVisibility(View.GONE);
            }


            String ss = SharedPreferencesUtil.getInstance(getActivity()).get("fundCode");
            if (ss.equals("12")) {
                new_add.setChecked(true);

                Log.e("----", "" + ss);
            } else if (ss.equals("34")) {
                new_add.setChecked(false);
                Log.e("----", "" + ss);
            }


        }
    }

    @Override
    public void onResume() {
        super.onResume();

//   if (service.getPreferences()!=null  ) {
//       baocun();
//   }


        if (new_add.isChecked()) {
            rl_each_info_for_remind.setVisibility(View.VISIBLE);
        } else {
            rl_each_info_for_remind.setVisibility(View.GONE);
        }


        String ss = SharedPreferencesUtil.getInstance(getActivity()).get("fundCode");
        if (ss.equals("12") || ss.equals(12)) {
            new_add.setChecked(true);
            rl_each_info_for_remind.setVisibility(View.VISIBLE);
        } else if (ss.equals("34") || ss.equals(34)) {
            new_add.setChecked(false);
            rl_each_info_for_remind.setVisibility(View.GONE);
        }


        if (jin == true) {
            service = new PreferencesService(getContext());
            Map<String, String> parms = service.getPreferences();
            i1 = parms.get("i1");
            j1 = parms.get("j1");


        } else {
            service = new PreferencesService(getContext());
            Map<String, String> parms = service.getPreferences();
            i1 = parms.get("i1") + 1;
            j1 = parms.get("j1") + 1;

        }

        if (kongzhi == true) {
            service = new PreferencesService(getContext());
            Map<String, String> parms = service.getPreferences();
            i1 = parms.get("i1");
            j1 = parms.get("j1");


        } else {
            service = new PreferencesService(getContext());
            Map<String, String> parms = service.getPreferences();
            i1 = parms.get("i1") + 1;
            j1 = parms.get("j1") + 1;

        }

        if (isEnable()) {

        } else {
            new_add.setChecked(false);
            allguan();
            rl_each_info_for_remind.setVisibility(View.GONE);
        }


        /**
         // 判断权限是否打开
         boolean isEnable = isEnable();
         Log.i(TAG, "InfoFragment -- onResume" + isEnable);
         if(!isEnable)
         {
         for (int i = 1; i < enties.size(); i++)
         {
         Info_entry infoEntity = enties.get(i);
         infoEntity.setChecked(false);
         adapterForInfoRemind.notifyItemChanged(i);
         }
         }**/

//        boolean isNotifyRunning = NotifyService.isServiceRunning(getActivity());
//        Log.i(TAG, "通知服务是否运行：" + isNotifyRunning);
//        toggleNotificationListenerService();

    }

    //    作者：Hugo
//    链接：http://www.zhihu.com/question/33540416/answer/113706620
//    来源：知乎
//    著作权归作者所有，转载请联系作者获得授权。

    private void toggleNotificationListenerService() {
        PackageManager pm = getActivity().getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(getActivity(), NotifyService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(new ComponentName(getActivity(), NotifyService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    private View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        rl_rest_o = (RelativeLayout) view.findViewById(R.id.rl_rest_s);
        rl_rest_o.setOnClickListener(this);
        new_add = (CheckBox) view.findViewById(R.id.open_remind_s_s);
        new_add.setOnClickListener(this);

        rl_each_info_for_remind = (RelativeLayout) view.findViewById(R.id.rl_each_info_for_remind);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.each_info_for_remind);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        enties = new ArrayList<>();
        String isSmsChecked = LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).readSp(MyConfingInfo.SMS_REMIND_CHANGE);
        String isQQChecked = LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).readSp(MyConfingInfo.SP_QQ_REMIND);
        String isWeichartChecked = LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).readSp(MyConfingInfo.SP_WEICHART_REMIND);
        String isFacebookChecked = LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).readSp(MyConfingInfo.SP_FACEBOOK_REMIND);
        String isWhatsappChecked = LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).readSp(MyConfingInfo.SP_WHATSAPP_REMIND);
        String isTwitterChecked = LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).readSp(MyConfingInfo.SP_TWITTER_REMIND);
        String isSkypeChecked = LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).readSp(MyConfingInfo.SP_SKYPE_REMIND);
        enties.add(new Info_entry(R.mipmap.sms_remind, mActivity.getString(R.string.contact_info),
                (isSmsChecked != null && isSmsChecked.equals(MyConfingInfo.REMIND_OPEN))));
        enties.add(new Info_entry(R.mipmap.qq_icon, mActivity.getString(R.string.qq_frend_info),
                (isQQChecked != null && isQQChecked.equals(MyConfingInfo.REMIND_OPEN))));
        enties.add(new Info_entry(R.mipmap.weichar_icon, mActivity.getString(R.string.weichart_info),
                (isWeichartChecked != null && isWeichartChecked.equals(MyConfingInfo.REMIND_OPEN))));
        enties.add(new Info_entry(R.mipmap.facebook_icon, mActivity.getString(R.string.facebook_info),
                (isFacebookChecked != null && isFacebookChecked.equals(MyConfingInfo.REMIND_OPEN))));
        enties.add(new Info_entry(R.mipmap.whats_app, mActivity.getString(R.string.whatsapp_info),
                (isWhatsappChecked != null && isWhatsappChecked.equals(MyConfingInfo.REMIND_OPEN))));
        enties.add(new Info_entry(R.mipmap.twitter, mActivity.getString(R.string.twitter_info),
                (isTwitterChecked != null && isTwitterChecked.equals(MyConfingInfo.REMIND_OPEN))));
        enties.add(new Info_entry(R.mipmap.skype, mActivity.getString(R.string.skype),
                (isSkypeChecked != null && isSkypeChecked.equals(MyConfingInfo.REMIND_OPEN))));
        view.findViewById(R.id.reset_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNoitficationAccess();
            }
        });

        adapterForInfoRemind = new AdapterForInfoRemind(mActivity, enties);
        adapterForInfoRemind.addOnSelectedListener(new AdapterForInfoRemind.OnitemChecked() {
            @Override
            public void onChecked(CompoundButton buttonView, boolean isChecked, int position) {
                switch (position) {
                    case 0:
                        if (isChecked) {

                            LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.SMS_REMIND_CHANGE, MyConfingInfo.REMIND_OPEN);
                            BleForPhoneAndSmsRemind.getInstance().openPhoneRemine((byte) 0x02, (byte) 0x01);
                        } else {
                            LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.SMS_REMIND_CHANGE, MyConfingInfo.REMIND_CLOSE);
                            BleForPhoneAndSmsRemind.getInstance().openPhoneRemine((byte) 0x02, (byte) 0x00);
                        }
                        break;
                    case 1:
                        if (isChecked) {
                            if (isEnable()) {
                                LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.SP_QQ_REMIND, MyConfingInfo.REMIND_OPEN);
                                BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0a, (byte) 0x01);

                            } else {
                                showConfimDialog(position);
                                enties.get(1).setChecked(false);
                                adapterForInfoRemind.notifyItemChanged(1);
                            }
                        } else {
                            LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.SP_QQ_REMIND, MyConfingInfo.REMIND_CLOSE);
                            BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0a, (byte) 0x00);
                        }
                        break;
                    case 2:
                        if (isChecked) {
                            if (isEnable()) {
                                LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.SP_WEICHART_REMIND, MyConfingInfo.REMIND_OPEN);
                                BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x09, (byte) 0x01);
                            } else {
                                showConfimDialog(position);
                                enties.get(2).setChecked(false);
                                adapterForInfoRemind.notifyItemChanged(2);
                            }
                        } else {
                            LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.SP_WEICHART_REMIND, MyConfingInfo.REMIND_CLOSE);
                            BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x09, (byte) 0x00);
                        }
                        break;
                    case 3:
                        if (isChecked) {
                            if (isEnable()) {
                                LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.SP_FACEBOOK_REMIND, MyConfingInfo.REMIND_OPEN);
                                BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0b, (byte) 0x01);
                            } else {
                                showConfimDialog(position);
                                enties.get(3).setChecked(false);
                                adapterForInfoRemind.notifyItemChanged(3);
                            }
                        } else {
                            LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.SP_FACEBOOK_REMIND, MyConfingInfo.REMIND_CLOSE);
                            BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0b, (byte) 0x00);
                        }
                        break;
                    case 4:     // whatsapp
                        if (isChecked) {
                            if (isEnable()) {
                                LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.SP_WHATSAPP_REMIND, MyConfingInfo.REMIND_OPEN);
                                BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0e, (byte) 0x01);
                            } else {
                                showConfimDialog(position);
                                enties.get(4).setChecked(false);
                                adapterForInfoRemind.notifyItemChanged(4);
                            }
                        } else {
                            LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.SP_WHATSAPP_REMIND, MyConfingInfo.REMIND_CLOSE);
                            BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0e, (byte) 0x00);
                        }
                        break;
                    case 5:     // twitter
                        if (isChecked) {
                            if (isEnable()) {
                                LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.SP_TWITTER_REMIND, MyConfingInfo.REMIND_OPEN);
                                BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0d, (byte) 0x01);
                            } else {
                                showConfimDialog(position);
                                enties.get(5).setChecked(false);
                                adapterForInfoRemind.notifyItemChanged(5);
                            }
                        } else {
                            LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.SP_TWITTER_REMIND, MyConfingInfo.REMIND_CLOSE);
                            BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0d, (byte) 0x00);
                        }
                        break;
                    case 6:     // skype
                        if (isChecked) {
                            if (isEnable()) {
                                LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.SP_SKYPE_REMIND, MyConfingInfo.REMIND_OPEN);
                                BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0c, (byte) 0x01);
                            } else {
                                showConfimDialog(position);
                                enties.get(6).setChecked(false);
                                adapterForInfoRemind.notifyItemChanged(6);
                            }
                        } else {
                            LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.SP_SKYPE_REMIND, MyConfingInfo.REMIND_CLOSE);
                            BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0c, (byte) 0x00);
                        }
                        break;
                }

            }
        });
        mRecyclerView.setAdapter(adapterForInfoRemind);
        readAllSwitch();
        return view;
    }

    /**
     * 读取qq，weix，facebook
     */
    private void readAllSwitch() {
        BleForQQWeiChartFacebook.getInstance().setOnDeviceDataBack(new InfoSwitchListener());
        BleForQQWeiChartFacebook.getInstance().readSwitch();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rl_rest_s:
                openNoitficationAccess();
                break;


            case R.id.open_remind_s_s:
                if (isEnable()) {
                    if (new_add.isChecked()) {
                        allkai();
                    } else {
                        allguan();
                    }
                } else {
                    new_add.setChecked(false);
                    View layout = layoutInflater.inflate(R.layout.dia_pic, null);
                    RelativeLayout rl_bt = (RelativeLayout) layout.findViewById(R.id.rl_bt);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    final AlertDialog dialog = builder.setView(layout).show();
//            Window dialogWindow = dialog.getWindow();
//            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//            dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
                    rl_bt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openNoitficationAccess();
                            dialog.dismiss();

                        }
                    });

                    //是否获取权限的
                }
                //是否点击的


                break;

        }

    }

    class InfoSwitchListener implements DataSendCallback {
        @Override
        public void sendSuccess(final byte[] receveData) {
            if (receveData[0] == (byte) 0x01 && receveData[1] == (byte) 0x02) {
                if (getActivity() != null && !getActivity().isFinishing() || getActivity() != null && !getActivity().isDestroyed()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parseDeviceDataAndShow(receveData);
                        }
                    });
                }
            }
        }

        @Override
        public void sendFailed() {
        }

        @Override
        public void sendFinished() {
        }
    }


    /**
     * 解析手环返回的一次性数据
     * 01 0901
     * 0a01
     * 0b01 1616
     * 01 0900 0a00 0b00 0c00 0d00 0e00 4016
     *
     * @param buffer
     */
    private void parseDeviceDataAndShow(byte[] buffer) {
//        01 0201 0900 0a00 0b01 0c00 0d00 0e00 46
        Log.i(TAG, "提醒数据：" + FormatUtils.bytesToHexString(buffer));
        if (buffer[1] == (byte) 0x02)     // 短信
        {
            dealEachStatus(0, (byte) 0x00, buffer[2], MyConfingInfo.SMS_REMIND_CHANGE);
        }
        if (buffer[3] == (byte) 0x09)     // 微信
        {
            dealEachStatus(2, (byte) 0x00, buffer[4], MyConfingInfo.SP_WEICHART_REMIND);
        }
        if (buffer[5] == (byte) 0x0a)     // QQ
        {
            dealEachStatus(1, (byte) 0x00, buffer[6], MyConfingInfo.SP_QQ_REMIND);
        }
        if (buffer[7] == (byte) 0x0b)     // facebook
        {
            dealEachStatus(3, (byte) 0x00, buffer[8], MyConfingInfo.SP_FACEBOOK_REMIND);
        }
        if (buffer[9] == (byte) 0x0c) {
            dealEachStatus(6, (byte) 0x00, buffer[10], MyConfingInfo.SP_SKYPE_REMIND);
        }
        if (buffer[11] == (byte) 0x0d) {
            dealEachStatus(5, (byte) 0x00, buffer[12], MyConfingInfo.SP_TWITTER_REMIND);
        }
        if (buffer[13] == (byte) 0x0e) {
            dealEachStatus(4, (byte) 0x00, buffer[14], MyConfingInfo.SP_WHATSAPP_REMIND);
        }
    }


    private void dealEachStatus(int position, byte status, byte comm, String eachInfo) {
        if (comm == status) {
            changeStatue(position, false);
            LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(eachInfo, MyConfingInfo.REMIND_CLOSE);
        } else {
            if (isEnable())  // 权限打开显示开
            {
                changeStatue(position, true);
                LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(eachInfo, MyConfingInfo.REMIND_OPEN);
            } else            // 反之一律显示关闭
            {
                changeStatue(position, false);
            }
        }
    }

    /**
     * 改变状态
     *
     * @param b
     */
    private void changeStatue(int position, boolean b) {
        if (b) {
            if (!enties.get(position).isChecked()) {
                enties.get(position).setChecked(true);
                adapterForInfoRemind.notifyItemChanged(position);
            }
        } else {
            if (enties.get(position).isChecked()) {
                enties.get(position).setChecked(false);
                adapterForInfoRemind.notifyItemChanged(position);
            }
        }


    }

    private void showConfimDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(R.string.enable_notification);
        builder.setMessage(R.string.enable_notification_message);
        builder.setPositiveButton(R.string.be_true, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openNoitficationAccess();
            }
        });
        builder.setNegativeButton(R.string.be_cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enties.get(position).setChecked(false);
                adapterForInfoRemind.notifyItemChanged(position);
            }
        });
        builder.create().show();
    }

    private void openNoitficationAccess() {
        startActivity(new Intent(ACCESS_INTENT_ACTION));
    }


    /**
     * 获取是否权限已打开
     *
     * @return
     */
    private boolean isEnable() {
        String pakName = mActivity.getApplicationContext().getPackageName();
        final String flat = Settings.Secure.getString(mActivity.getContentResolver(), ENABLE_NOTIFICATION_LISTENER);
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

//    private boolean isEnabled() {
//        String pkgName = getPackageName();
//        final String flat = Settings.Secure.getString(getContentResolver(),
//                ENABLED_NOTIFICATION_LISTENERS);
//        if (!TextUtils.isEmpty(flat)) {
//            final String[] names = flat.split(":");
//            for (int i = 0; i < names.length; i++) {
//                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
//                if (cn != null) {
//                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }

    private void allkai() {
        if (new_add.isChecked() == true) {
            rl_each_info_for_remind.setVisibility(View.VISIBLE);

            if (i1 == null && j1 == null) {

                if (jin == true) {
                    View layout = layoutInflater.inflate(R.layout.tuisong_tixing, null);
                    TextView textView = (TextView) layout.findViewById(R.id.tv_bt);
                    TextView tuzaitixing = (TextView) layout.findViewById(R.id.buzai_tixing);
                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                    final android.app.AlertDialog dialog = builder.setView(layout).show();
                    dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    tuzaitixing.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            jin = false;
                        }
                    });
                }
            }
            //===================================================================
//            changeStatue(0, true);
//            LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(isSmsChecked, MyConfingInfo.REMIND_OPEN);
//            BleForPhoneAndSmsRemind.getInstance().openPhoneRemine((byte) 0x02, (byte) 0x01);
//            enties.get(0).setChecked(true);
////                     BleForQQWeiChartFacebook.getInstance().openRemind((byte)0x0d, (byte)0x00);
//            adapterForInfoRemind.notifyItemChanged(0);
//
//            changeStatue(1, true);
//            LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.SP_QQ_REMIND, MyConfingInfo.REMIND_CLOSE);
//            BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0a, (byte) 0x01);
//            enties.get(1).setChecked(true);
//            adapterForInfoRemind.notifyItemChanged(1);
//
//            changeStatue(2, true);
//            LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.SP_WEICHART_REMIND, MyConfingInfo.REMIND_OPEN);
//            BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x09, (byte) 0x01);
//            enties.get(2).setChecked(true);
//            adapterForInfoRemind.notifyItemChanged(2);
//
//            changeStatue(3, true);
//            LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.SP_FACEBOOK_REMIND, MyConfingInfo.REMIND_OPEN);
//            BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0b, (byte) 0x01);
//            enties.get(3).setChecked(true);
//            adapterForInfoRemind.notifyItemChanged(3);
//
//            changeStatue(4, true);
//            LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.SP_WHATSAPP_REMIND, MyConfingInfo.REMIND_OPEN);
//            BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0e, (byte) 0x01);
//            enties.get(4).setChecked(true);
//            adapterForInfoRemind.notifyItemChanged(4);
//
//            changeStatue(5, true);
//            LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.SP_TWITTER_REMIND, MyConfingInfo.REMIND_OPEN);
//            BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0d, (byte) 0x01);
//            enties.get(5).setChecked(true);
//            adapterForInfoRemind.notifyItemChanged(5);
//
//            changeStatue(6, true);
//            LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.SP_SKYPE_REMIND, MyConfingInfo.REMIND_OPEN);
//            BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0c, (byte) 0x01);
//            enties.get(6).setChecked(true);
//            adapterForInfoRemind.notifyItemChanged(6);


            s6 = "12";
            SharedPreferencesUtil.getInstance(getActivity()).put("fundCode", s6);
        }
    }

    private void allguan() {
        if (new_add.isChecked() == false) {
            rl_each_info_for_remind.setVisibility(View.GONE);
            changeStatue(0, false);
            LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(isSmsChecked, MyConfingInfo.REMIND_CLOSE);
            BleForPhoneAndSmsRemind.getInstance().openPhoneRemine((byte) 0x02, (byte) 0x00);
            enties.get(0).setChecked(false);
            adapterForInfoRemind.notifyItemChanged(0);

            changeStatue(1, false);
            LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.SP_QQ_REMIND, MyConfingInfo.REMIND_CLOSE);
            BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0a, (byte) 0x00);
            enties.get(1).setChecked(false);
            adapterForInfoRemind.notifyItemChanged(1);

            changeStatue(2, false);
            LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.SP_WEICHART_REMIND, MyConfingInfo.REMIND_CLOSE);
            BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x09, (byte) 0x00);
            enties.get(2).setChecked(false);
            adapterForInfoRemind.notifyItemChanged(2);

            changeStatue(3, false);
            LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.SP_FACEBOOK_REMIND, MyConfingInfo.REMIND_CLOSE);
            BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0b, (byte) 0x00);
            enties.get(3).setChecked(false);
            adapterForInfoRemind.notifyItemChanged(3);

            changeStatue(4, false);
            LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.SP_WHATSAPP_REMIND, MyConfingInfo.REMIND_CLOSE);
            BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0e, (byte) 0x00);
            enties.get(4).setChecked(false);
            adapterForInfoRemind.notifyItemChanged(4);

            changeStatue(5, false);
            LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.SP_TWITTER_REMIND, MyConfingInfo.REMIND_CLOSE);
            BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0d, (byte) 0x00);
            enties.get(5).setChecked(false);
            adapterForInfoRemind.notifyItemChanged(5);

            changeStatue(6, false);
            LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.SP_SKYPE_REMIND, MyConfingInfo.REMIND_CLOSE);
            BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0c, (byte) 0x00);
            enties.get(6).setChecked(false);
            adapterForInfoRemind.notifyItemChanged(6);

        }

        s7 = "34";
        SharedPreferencesUtil.getInstance(getActivity()).put("fundCode", s7);

    }

}

