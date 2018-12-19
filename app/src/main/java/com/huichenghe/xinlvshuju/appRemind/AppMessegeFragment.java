package com.huichenghe.xinlvshuju.appRemind;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huichenghe.bleControl.Ble.BleForQQWeiChartFacebook;
import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.PreferenceV.PreferencesService;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;
import com.huichenghe.xinlvshuju.appRemind.Utils.RunningAppInfoParam;
import com.huichenghe.xinlvshuju.appRemind.listener.OnItemClickListener;
import com.huichenghe.xinlvshuju.slide.settinga.SettingActivity;
import com.huichenghe.xinlvshuju.views.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class AppMessegeFragment extends Fragment {
    public final String TAG = "AppMessegeFragment";
    static List<AppInfo> appInfosChecked = new LinkedList<>();
    private AppInfoAdapter mAppInfoAdapter;
    private RunningAppInfoParam runningAppInfoParam;
    private SwipeMenuRecyclerView rl_appChecked;
    //    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SettingActivity activity;
    private CheckBox cb_remind;
    private ImageButton cb_remind_one;
    private TextView tv_add, tv_reste_info;
    private AlertDialog.Builder builder;
    LayoutInflater layoutInflater;
    private String i1;
    private String j1;
    private PreferencesService service;
    private RelativeLayout rl_rest;

    private boolean jin = true;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:

                    break;
            }
        }
    };

    public AppMessegeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onResume() {
        super.onResume();
        initViewState();//初始化控件状态
        initInfoData();
        if (jin == true) {
            service = new PreferencesService(getContext());
            Map<String, String> parms = service.getPreferences();
            i1 = parms.get("i1");
            j1 = parms.get("j1");
            Log.e("----------onre", "----" + i1 + j1);

        } else {
            service = new PreferencesService(getContext());
            Map<String, String> parms = service.getPreferences();
            i1 = parms.get("i1") + 1;
            j1 = parms.get("j1") + 1;
            Log.e("----------onre", "----" + i1 + j1);
        }


//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == -1) {
            initInfoData();
        }
//            initdATA();
    }

    private void initViewState(boolean checked) {
        if (checked) {
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

            return;
        } else {
//            showConfimDialog("授权通知", "确定取消授权吗？");
            return;
        }
//        cb_remind.setChecked(isEnable());//拿到checkbox保存的状态
//        mSwipeRefreshLayout.setEnabled(checked);
//        tv_add.setEnabled(checked);
//        if (checked) {
//            rl_appChecked.setVisibility(View.VISIBLE);
//            tv_add.setBackgroundResource(R.drawable.bg_hudu);
//        } else {
//            rl_appChecked.setVisibility(View.INVISIBLE);
//            tv_add.setBackgroundResource(R.drawable.bg_hudu_checked);
//        }


    }

    /**
     * 获取是否权限已打开
     *
     * @return
     */
    private String ENABLE_NOTIFICATION_LISTENER = "enabled_notification_listeners";

    private boolean isEnable() {
        String pakName = activity.getApplicationContext().getPackageName();
        final String flat = Settings.Secure.getString(activity.getContentResolver(), ENABLE_NOTIFICATION_LISTENER);
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

    private void initStateFromBle() {
        for (int i = 0; i < appInfosChecked.size(); i++) {
            String label = appInfosChecked.get(i).getLabel();
            if (label.equalsIgnoreCase("qq")) {
                showLog("qq开关打开");
                LocalDataSaveTool.getInstance(getContext().getApplicationContext()).writeSp(MyConfingInfo.SP_QQ_REMIND, MyConfingInfo.REMIND_OPEN);
                BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0a, (byte) 0x01);
            } else if (label.equalsIgnoreCase("微信")) {
                showLog("微信");
                LocalDataSaveTool.getInstance(getContext().getApplicationContext()).writeSp(MyConfingInfo.SP_WEICHART_REMIND, MyConfingInfo.REMIND_OPEN);
                BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x09, (byte) 0x01);
            } else if (label.equalsIgnoreCase("FaceBook")) {
                showLog("FaceBook");
                LocalDataSaveTool.getInstance(getContext().getApplicationContext()).writeSp(MyConfingInfo.SP_FACEBOOK_REMIND, MyConfingInfo.REMIND_OPEN);
                BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0b, (byte) 0x01);

            } else if (label.equalsIgnoreCase("skype")) {
                showLog("skype");
                LocalDataSaveTool.getInstance(getContext().getApplicationContext()).writeSp(MyConfingInfo.SP_SKYPE_REMIND, MyConfingInfo.REMIND_OPEN);
                BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0c, (byte) 0x01);

            } else if (label.equalsIgnoreCase("twitter")) {
                showLog("twitter");
                LocalDataSaveTool.getInstance(getContext().getApplicationContext()).writeSp(MyConfingInfo.SP_TWITTER_REMIND, MyConfingInfo.REMIND_OPEN);
                BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0d, (byte) 0x01);

            } else if (label.equalsIgnoreCase("whatsAPP")) {
                showLog("whatsAPP");
                LocalDataSaveTool.getInstance(getContext().getApplicationContext()).writeSp(MyConfingInfo.SP_WHATSAPP_REMIND, MyConfingInfo.REMIND_OPEN);
                BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0e, (byte) 0x01);
            } else {
            }
        }
    }

    private String ACCESS_INTENT_ACTION = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    private void openNoitficationAccess() {
        startActivity(new Intent(ACCESS_INTENT_ACTION));
    }

    private void initViewState() {
//        Boolean checked = Utils.loadData(getContext(), REMIND);

        String push = LocalDataSaveTool.getInstance(getContext().getApplicationContext()).readSp(MyConfingInfo.PUSH_MESSAGE);
        boolean state = false;
        if (isEnable()) {
            if (push != null && push.equals(MyConfingInfo.PUSH_MESSAGE_TRUE)) {
                state = true;
                //         cb_remind_one.setChecked(true);

            } else {
                state = false;
                //    cb_remind_one.setChecked(false);
            }
        } else {
            LocalDataSaveTool.getInstance(getContext().getApplicationContext()).writeSp(MyConfingInfo.PUSH_MESSAGE, MyConfingInfo.PUSH_MESSAGE_FALSE);
            state = false;
        }
        changeState(state);
    }

    private void changeState(boolean state) {
        cb_remind.setChecked(state);//拿到checkbox保存的状态
//        mSwipeRefreshLayout.setEnabled(state);
        tv_add.setEnabled(state);
        if (state) {
            if (rl_appChecked.getVisibility() == View.GONE) {
                rl_appChecked.setVisibility(View.VISIBLE);
                tv_add.setVisibility(View.VISIBLE);
                tv_reste_info.setVisibility(View.VISIBLE);
            }


        } else {
            if (rl_appChecked.getVisibility() == View.VISIBLE) {
                rl_appChecked.setVisibility(View.GONE);
                tv_add.setVisibility(View.GONE);
                tv_reste_info.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (SettingActivity) getActivity();
    }

    private void initInfoData() {
        appInfosChecked.clear();
        Cursor cursor = MyDBHelperForDayData.getInstance(getContext().getApplicationContext()).selectInfoData(getActivity().getApplicationContext());
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    String appPackage = cursor.getString(cursor.getColumnIndex("appName"));
                    if (appPackage == null || (appPackage.equals("")))
                        continue;
                    PackageManager pm = getContext().getPackageManager();
                    try {
                        ApplicationInfo appInfo = pm.getApplicationInfo(appPackage, PackageManager.GET_UNINSTALLED_PACKAGES);
                        appInfosChecked.add(new AppInfo(appInfo.loadIcon(pm), true, appPackage, appInfo.loadLabel(pm).toString()));
                        mAppInfoAdapter.notifyDataSetChanged();
                        initStateFromBle();     //读取设备开关状态
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
        }
    }

    private void initdATA() {
//        if (!mSwipeRefreshLayout.isRefreshing())
//            mSwipeRefreshLayout.setRefreshing(true);
        runningAppInfoParam = new RunningAppInfoParam(getActivity());
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<AppInfo> thirdAppInfo = runningAppInfoParam.getThirdAppInfo();
                appInfosChecked.clear();

                for (Iterator<AppInfo> it = thirdAppInfo.iterator(); it.hasNext(); ) {
                    AppInfo s = it.next();
                    if (s.isCheck()) {
                        appInfosChecked.add(s);
                        showLog("add");
                    }
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAppInfoAdapter.notifyDataSetChanged();
//                        if (mSwipeRefreshLayout.isRefreshing())
//                            mSwipeRefreshLayout.setRefreshing(false);
//                        mSwipeRefreshLayout.setEnabled(false);
                        initStateFromBle();//读取设备开关状态
                    }
                });
            }
        }).start();
    }

    void showLog(String s) {
        Log.e("TAG", s);
    }

    public void showToast(final String msg) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_app_messege, container, false);
        initView(inflate);

        layoutInflater = LayoutInflater.from(getActivity());

        service = new PreferencesService(getContext());
        Map<String, String> parms = service.getPreferences();
        i1 = parms.get("i1");
        j1 = parms.get("j1");
        Log.e("----------onre", "----" + i1 + j1);

        return inflate;
    }

    private void addListener() {

        rl_rest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNoitficationAccess();
            }
        });

        cb_remind_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_remind_one.isClickable()) {
                    openNoitficationAccess();

                } else {
                    openorclose(false);
                    openNoitficationAccess();
                }

                if (isEnable()) {
                    LocalDataSaveTool.getInstance(getContext().getApplicationContext()).writeSp(MyConfingInfo.PUSH_MESSAGE, MyConfingInfo.PUSH_MESSAGE_TRUE);

                } else {
                    LocalDataSaveTool.getInstance(getContext().getApplicationContext()).writeSp(MyConfingInfo.PUSH_MESSAGE, MyConfingInfo.PUSH_MESSAGE_FALSE);

                }

            }
        });

        //打开关闭提醒的监听
        cb_remind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_remind.isChecked()) {
                    ist_f();
                    if (isEnable()) {
                        if (i1 == null && j1 == null) {

                            if (jin == true) {
                                LocalDataSaveTool.getInstance(getContext().getApplicationContext()).writeSp(MyConfingInfo.PUSH_MESSAGE, MyConfingInfo.PUSH_MESSAGE_TRUE);
                                View layout = layoutInflater.inflate(R.layout.tuisong_tixing, null);
                                TextView tv_bt = (TextView) layout.findViewById(R.id.tv_bt);
                                TextView tuzaitixing = (TextView) layout.findViewById(R.id.buzai_tixing);
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                                final android.app.AlertDialog dialog = builder.setView(layout).show();
                                tv_bt.setOnClickListener(new View.OnClickListener() {
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

                        changeState(true);
                    } else {
                    //    openorclose(true);
                    }
                } else {
                    LocalDataSaveTool.getInstance(getContext().getApplicationContext()).writeSp(MyConfingInfo.PUSH_MESSAGE, MyConfingInfo.PUSH_MESSAGE_FALSE);
                    changeState(false);
                }
            }
        });
        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appInfosChecked.size() > 10) {
                    MyToastUitls.showToast(getContext(), R.string.add_app_upper_limit, 1);
                } else {
                    startActivityForResult(new Intent(getActivity(), AllAppActivity.class), 1);
                }
            }
        });
        //recycerview条目点击监听
        mAppInfoAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                Toast.makeText(getActivity(), position + "", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initView(View inflate) {
//        mSwipeRefreshLayout = (SwipeRefreshLayout) inflate.findViewById(R.id.sr_fresh);
//        mSwipeRefreshLayout.setProgressViewOffset(false, 1, 100);
        cb_remind = (CheckBox) inflate.findViewById(R.id.open_remind_s);
        cb_remind_one = (ImageButton) inflate.findViewById(R.id.open_remind_one);

        rl_rest = (RelativeLayout) inflate.findViewById(R.id.rl_rest);
        tv_add = (TextView) inflate.findViewById(R.id.tv_add);
        rl_appChecked = (SwipeMenuRecyclerView) inflate.findViewById(R.id.appChecked);
        rl_appChecked.setLayoutManager(new LinearLayoutManager(getActivity()));
        rl_appChecked.setItemAnimator(new DefaultItemAnimator());
        rl_appChecked.setHasFixedSize(true);
        rl_appChecked.addItemDecoration(new ListViewDecoration());
        mAppInfoAdapter = new AppInfoAdapter(appInfosChecked);
        rl_appChecked.setAdapter(mAppInfoAdapter);
        addListener();
        initInfoData();
        setNoteInfo(inflate);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            service = new PreferencesService(getContext());
            Map<String, String> parms = service.getPreferences();
            i1 = parms.get("i1");
            j1 = parms.get("j1");
            Log.e("----------onre", "----" + i1 + j1);


            if (jin == true) {
                service = new PreferencesService(getContext());
                Map<String, String> parms1 = service.getPreferences();
                i1 = parms1.get("i1");
                j1 = parms1.get("j1");
                Log.e("----------onre", "----" + i1 + j1);

            } else {
                service = new PreferencesService(getContext());
                Map<String, String> parms1 = service.getPreferences();
                i1 = parms1.get("i1") + 1;
                j1 = parms1.get("j1") + 1;
                Log.e("----------onre", "----" + i1 + j1);
            }
        }
    }

    private void setNoteInfo(View inflate) {
        tv_reste_info = (TextView) inflate.findViewById(R.id.reset_info);
        tv_reste_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNoitficationAccess();
            }
        });
    }


    //执行通知监听操作F
    private void openorclose(boolean isChecked) {
        initViewState(isChecked);
    }


    class AppInfoAdapter extends RecyclerView.Adapter<AppInfoAdapter.ViewHolder> {

        public AppInfoAdapter(List<AppInfo> appInfos) {
            this.appInfos = appInfos;
        }

        OnItemClickListener itemClickListener;
        List<AppInfo> appInfos;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.itemClickListener = onItemClickListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.setData(appInfos, position);
//            holder.setOnItemClickListener(itemClickListener);
        }

        @Override
        public int getItemCount() {
            return appInfos == null ? 0 : appInfos.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            ImageView iv_icon;
            TextView tv_label;
            TextView tv_dele;
            OnItemClickListener mOnItemClickListener;

            public ViewHolder(View itemView) {
                super(itemView);
                itemView.setClickable(true);
                iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
                tv_label = (TextView) itemView.findViewById(R.id.tv_label);
                tv_dele = (TextView) itemView.findViewById(R.id.tv_dele);
            }

            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(getAdapterPosition());
                }
            }

            public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
                this.mOnItemClickListener = onItemClickListener;
            }

            public void setData(final List<AppInfo> appInfos, final int position) {
                tv_dele.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                       showLog("当前位置：" + position + "--appInfos.size():" + appInfos.size());
                        rl_appChecked.smoothCloseMenu();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                Utils.saveData(getActivity(), false, appInfos.get(position).getPackageName());
                                MyDBHelperForDayData.getInstance(getContext().getApplicationContext()).deleteInfoData(appInfos.get(position).getPackageName());
                                deleteThisApp(appInfos.get(position).getPackageName());
                                appInfos.remove(position);
                                mAppInfoAdapter.notifyDataSetChanged();
                            }
                        }, 150);
                    }
                });
                iv_icon.setImageDrawable(appInfos.get(position).getDrawable());
                tv_label.setText(appInfos.get(position).getLabel());
            }
        }
    }

    private void deleteThisApp(String packageName) {
        Cursor cursor = MyDBHelperForDayData.getInstance(getContext()).selectInfoData(getContext().getApplicationContext());
        if (cursor == null) {
//                            MyDBHelperForDayData.getInstance(context).insertInfoData(inserName);
        } else {
            if (parseCursorAndCompare(cursor, packageName)) {
                MyDBHelperForDayData.getInstance(getContext()).deleteInfoData(packageName);
            }
        }
    }

    private boolean parseCursorAndCompare(Cursor cursor, String com) {
        if (com != null && com.equals("")) {
            return false;
        }
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    String appName = cursor.getString(cursor.getColumnIndex("appName"));
                    if (com.equals(appName)) {
                        return false;
                    }
                } while (cursor.moveToNext());
            }
        }
        return true;
    }

    private void finish() {
        getActivity().onBackPressed();
    }

    private void ist_f() {
        if (cb_remind.isChecked()) {
            if (!cb_remind_one.isClickable()) {
//            if (cb_remind_one.isChecked() == true) {
//                cb_remind_one.setChecked(true);
                cb_remind.setChecked(true);

            } else {
//                cb_remind_one.setChecked(false);
//                cb_remind_one.setChecked(false);

                cb_remind.setChecked(false);

            }
            if (isEnable()) {
//

            } else {

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
            }

        }

    }
}
