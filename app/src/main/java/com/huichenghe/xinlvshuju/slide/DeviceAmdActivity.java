package com.huichenghe.xinlvshuju.slide;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huichenghe.bleControl.Ble.BleBaseDataForBlood;
import com.huichenghe.bleControl.Ble.BleDataForFactoryReset;
import com.huichenghe.bleControl.Ble.BleDataforSyn;
import com.huichenghe.bleControl.Ble.BleForFindDevice;
import com.huichenghe.bleControl.Ble.BleForQQWeiChartFacebook;
import com.huichenghe.bleControl.Ble.BleReadDeviceMenuState;
import com.huichenghe.bleControl.Ble.BleScanUtils;
import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.bleControl.Ble.DataSendCallback;
import com.huichenghe.bleControl.Ble.DeviceConfig;
import com.huichenghe.bleControl.Ble.LocalDeviceEntity;
import com.huichenghe.xinlvshuju.Adapter.RecyclerViewAdapter;
import com.huichenghe.xinlvshuju.Adapter.WrapContentLinearLayoutManager;
import com.huichenghe.xinlvshuju.CustomView.CircleProgressDialog;
import com.huichenghe.xinlvshuju.CustomView.MyProgressDialog;
import com.huichenghe.xinlvshuju.DbEntities.DeviceSaveData;
import com.huichenghe.xinlvshuju.DbEntities.LocalDataBaseUtils;
import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.PreferenceV.PreferencesService;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;
import com.huichenghe.xinlvshuju.expand_activity.help_bind_device;
import com.huichenghe.xinlvshuju.mainpack.BaseActivity;
import com.huichenghe.xinlvshuju.mainpack.MainActivity;
import com.huichenghe.xinlvshuju.mainpack.MyApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

import static com.huichenghe.xinlvshuju.R.string.not_bind;

/**
 * 设备管理界面
 */
public class DeviceAmdActivity extends BaseActivity {
    private static final String TAG = DeviceAmdActivity.class.getSimpleName();
    private RelativeLayout mRelativeLayout;     // 扫描列表布局
    private Rect myRect;
    private AppBarLayout mAppBarLayout;
    private TextView searchButton;
    private RecyclerView mRecyclerView;
    private int SHOW_FINST_PAGER = 1;         // 代表第一布局
    private int SHOW_SECOND_PAGER = 2;        // 代表第二布局
    private int showState = SHOW_FINST_PAGER; // 标识变量,标识当前显示的布局
    private ArrayList<LocalDeviceEntity> myData;    // 扫描的设备集合
    //    private List<DeviceSaveData> drivaceDate;
    private RecyclerViewAdapter deviceAdapter;  // 扫描结果显示的adapter
    //    private RecyclerViewAdapterForSavedDevice saveDeviceAdapter;// 保存的设备的adapter
//    private ContentLoadingProgressBar scanProgress;
    private MyProgressDialog connectProgress;
    private final int findDeviceMessage = 0;
    private final int closeDeviceMessage = 1;
    private Dialog dialogClose = null;
    private final int CHAGE_SCAN_LAYOUT = 5;
    private final int CLOSE_NOTE_NOT_CONNECT = 4;
    private final int reCode = 67893;
    private SwipeRefreshLayout refreshLayout;
    private PreferencesService service;
    private int int_s, int_h;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case findDeviceMessage:
                    if (!DeviceAmdActivity.this.isDestroyed() && !DeviceAmdActivity.this.isFinishing()) {
                        showShockDialog(true);
                    }
                    break;
                case closeDeviceMessage:
                    if (!DeviceAmdActivity.this.isDestroyed() && !DeviceAmdActivity.this.isFinishing()) {
                        showShockDialog(false);
                    }
                    break;
                case CHAGE_SCAN_LAYOUT:
                    Log.i(TAG, "scanProgress:" + scanProgress.hashCode());
                    if (msg.arg1 == 0) {
                        if (scanProgress.getVisibility() == View.GONE) {
                            scanDevice.setText(R.string.cancle_scan);
                            scanProgress.setVisibility(View.VISIBLE);
                            animationDrawable = (AnimationDrawable) scanProgress.getDrawable();
                            animationDrawable.setOneShot(false);
                            animationDrawable.start();
//                            Log.i(TAG, "改变界面布局：显示");
//                            Toast.makeText(DeviceAmdActivity.this, "显示搜索进度", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (scanProgress.getVisibility() == View.VISIBLE) {
                            scanDevice.setText(R.string.bind_device);
                            scanProgress.setVisibility(View.GONE);
                            animationDrawable.stop();
//                            Log.i(TAG, "改变界面布局：隐藏");
//                            Toast.makeText(DeviceAmdActivity.this, "隐藏搜索进度", Toast.LENGTH_SHORT).show();
                            //判断设备类型
                            String type = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.USER_DEVICE_TYPE);
                            if (type != null && type.equals(MyConfingInfo.DEVICE_BLOOD)) {
                                service = new PreferencesService(getApplicationContext());
                                if (service.getPreferences() != null) ;
                                Map<String, String> params = service.getPreferences();
                                //short_ed.setText(params.get("short_ed"));
                                //height_ed.setText(params.get("height_ed"));
                                String s = params.get("short_ed");
                                String h = params.get("height_ed");
                                Log.e("------s=", "--------------" + s);
                                Log.e("------h=", "--------------" + h);
                                if (s.length() > 0 && h.length() > 0) {
                                    int_s = Integer.parseInt(s);
                                    int_h = Integer.parseInt(h);
                                }


                                if (BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice()) {
                                    if (int_s != 0 && int_h != 0) {
                                        BleBaseDataForBlood.getBloodInstance().sendStandardBloodData(int_h, int_s);
                                    }

                                } else {
                                    // Toast.makeText(MainActivity.this, R.string.toask_mark_bl, Toast.LENGTH_LONG).show();
                                }
                            } else if (type != null && type.equals(MyConfingInfo.DEVICE_HR)) {

                            }

                        }
                    }
                    break;
                case CLOSE_NOTE_NOT_CONNECT:
                    if (!BluetoothLeService.getInstance().isDeviceConnected((LocalDeviceEntity) msg.obj)) {
                        closeProgressDialog();
                        Toast.makeText(DeviceAmdActivity.this, getString(R.string.connect_outoftime), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
    private boolean hasUpdate;
    //    private TextView buttonC;
//    private ImageView imageV;
    private float cxx;
    private float cyy;
    private ImageView scanProgress;
    private AnimationDrawable animationDrawable;
    private TextView scanDevice;
    private ImageView connectState, helpBindDevice;
    private TextView connectedName, connectedStateTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_amd);
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Log.i(TAG, "DeviceAmdActivity--onCreate");
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        init();
        initstate();
        registerReceiverForAllEvent();
//        selecterFormDBAndUpdateUI();    // 查询数据库，并显示
//        if(Build.VERSION.SDK_INT >= 23)
//        {
//            RequestUtils.requestPermission(DeviceAmdActivity.this);
//            RequestUtils.requestPermissionSdcard(DeviceAmdActivity.this);
//        }
        initConnectState();
        checkAction();
    }

    private void initstate() {
        String deviceName = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(DeviceConfig.DEVICE_NAME);
        if (deviceName.equals("")) {
            searchButton.setText(R.string.bind_device);
            connectedName.setText(not_bind);

        } else {
            searchButton.setText(R.string.unbind_device);
            connectedName.setText(deviceName);

        }
    }

//    @Override
//    public void onAttachedToWindow()
//    {
//        super.onAttachedToWindow();
//        checkAction();
//    }

    private void initConnectState() {
//        Log.i(TAG, "BluetoothLeService.getInstance()：是否连接：" + BluetoothLeService.getInstance().isConnectedDevice());
        if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            if (BluetoothLeService.getInstance() != null) {
                if (BluetoothLeService.getInstance().isConnectedDevice()) {
                    connectState.setImageResource(R.mipmap.device_connect);
                    String deName = LocalDataSaveTool.getInstance(DeviceAmdActivity.this).readSp(DeviceConfig.DEVICE_NAME);
                    if (deName != null && deName.equals("")) {
                        if (!BluetoothLeService.getInstance().isConnectedDevice()) {
                            updateUnbind();
                        }
                    }
                    connectedName.setText(deName);
                    searchButton.setText(R.string.unbind_device);
                    //  connectedStateTv.setText(R.string.already_con);
                } else if (BluetoothLeService.getInstance().isConnectingDevice()) {
                    connectState.setImageResource(R.mipmap.device_disconnect);
                    String deName = LocalDataSaveTool.getInstance(DeviceAmdActivity.this).readSp(DeviceConfig.DEVICE_NAME);
                    connectedName.setText(deName);
                    searchButton.setText(R.string.unbind_device);
                    // connectedStateTv.setText(R.string.connecting_now);
                    mHandler.postDelayed(runnable, 40 * 1000);
                } else {
                    connectState.setImageResource(R.mipmap.device_disconnect);
                    String deName = LocalDataSaveTool.getInstance(DeviceAmdActivity.this).readSp(DeviceConfig.DEVICE_NAME);
                    if (deName != null && deName.equals("")) {
                        connectedName.setText(not_bind);
                        searchButton.setText(R.string.bind_device);
                    } else {
                        connectedName.setText(deName);
                    }
                    connectedStateTv.setText("");
                }
            }
        } else {
            changeConnectStateWhenBleClose(true);
        }
    }

    /**
     * 蓝牙打开更新界面显示
     */
    private void changeConnectStateWhenBleClose(boolean isClose) {
        if (isClose) {
            connectState.setImageResource(R.mipmap.device_disconnect);
            String deName = LocalDataSaveTool.getInstance(DeviceAmdActivity.this).readSp(DeviceConfig.DEVICE_NAME);
            if (deName != null && deName.equals("")) {
                searchButton.setText(R.string.bind_device);
                connectedName.setText(R.string.not_bind);
            } else {
                connectedName.setText(deName);
                searchButton.setText(R.string.unbind_device);
            }
            //  connectedStateTv.setText(R.string.bluetooth_has_stop_now);
        } else {
            String deName = LocalDataSaveTool.getInstance(DeviceAmdActivity.this).readSp(DeviceConfig.DEVICE_NAME);
            if (deName != null && deName.equals("")) {
                searchButton.setText(R.string.bind_device);
                connectedName.setText(not_bind);
                connectedStateTv.setText("");
            } else {
//                connectedName.setText(deName);
//                searchButton.setText(R.string.unbind_device);
            }
        }
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // showAlterDialog();
        }
    };

    private AlertDialog.Builder connectDialog;
    private AlertDialog alerDialog;

    private void showAlterDialog() {
        if (connectDialog == null) {
            connectDialog = new AlertDialog.Builder(DeviceAmdActivity.this);
        }
        connectDialog.setCancelable(false);
        connectDialog.setTitle(R.string.note_connect_timeout);
        connectDialog.setMessage(R.string.connect_falied_please_open_blue);
        connectDialog.setPositiveButton(R.string.help, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), help_bind_device.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        connectDialog.setNegativeButton(R.string.be_true, null);
        alerDialog = connectDialog.create();
        alerDialog.show();
    }

    private void closeDialog() {
        if (connectDialog != null && alerDialog != null && alerDialog.isShowing()) {
            alerDialog.dismiss();
            connectDialog = null;
            alerDialog = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "DeviceAmdActivity--onResume");
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                showTheInvisibleView(mAppBarLayout);
//            }
//        }, 300);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "DeviceAmdActivity--onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "DeviceAmdActivity--onStart");
    }

    private void checkAction() {
        String action = getIntent().getAction();
        Log.i(TAG, "DeviceAmdActivity--action:" + action);
        if (action != null && action.equals(MyConfingInfo.ACTION_SEARCH_DEVICE)) {
            if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                //  BluetoothAdapter.getDefaultAdapter().enable();
                MyToastUitls.showToast(DeviceAmdActivity.this.getApplicationContext(), R.string.make_sure, 1);
                return;
            } else {
                if (BluetoothLeService.getInstance() == null) {
                    Intent in = new Intent();
                    in.setClass(getApplicationContext(), BluetoothLeService.class);
                    startService(in);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice()) {
//                        Toast.makeText().show();
//                        MyToastUitls.showToast(DeviceAmdActivity.this, R.string.already_connect, 1);
//                        AlertDialog.Builder connectDialog = new AlertDialog.Builder(DeviceAmdActivity.this);
//                    connectDialog.setCancelable(false);
//                    connectDialog.setTitle(R.string.alter);
//                    connectDialog.setMessage(R.string.seach_alter);
//                    connectDialog.setNegativeButton(R.string.be_cancle, null);
//                        connectDialog.setPositiveButton(R.string.be_true, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
                    BluetoothLeService.getInstance().close(false);
                    clearTheDevice();
                    startScan();
                    showTheScanLayoutAfterUserBeTrue(false);
                    initConnectState();
//                        }
//                    });
//                    connectDialog.create().show();
                } else {
                    showTheScanLayoutAfterUserBeTrue(false);
                    startScan();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);
        unregisterReceiver(myReceiver);
        BleScanUtils.getBleScanUtilsInstance(getApplicationContext()).stopScan();
        Log.i(TAG, "DeviceAmdActivity--onDestroy");
    }

    /**
     * 查询数据库并更新UI
     */
//    private void selecterFormDBAndUpdateUI()
//    {
//        drivaceDate.clear();
//        List<DeviceSaveData> dataForDevice = LocalDataBaseUtils.getInstance(DeviceAmdActivity.this).getDeviceListFromDB();
//
//        if(dataForDevice.size() == 0)
//        {
//            return;
//        }
//        for (int i = dataForDevice.size() - 1; i >= 0; i --)
//        {
//            drivaceDate.add(dataForDevice.get(i));
////            saveDeviceAdapter.addItem(dataForDevice.get(i));
//        }
//        saveDeviceAdapter.notifyDataSetChanged();
//    }

    /**
     * 注册广播
     */
    private void registerReceiverForAllEvent() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(DeviceConfig.DEVICE_CONNECTE_AND_NOTIFY_SUCESSFUL);
        filter.addAction(MyConfingInfo.ON_DEVICE_STATE_CHANGE);
        filter.addAction(MyConfingInfo.FACTORY_RESET_SUCCESS);
        filter.addAction(MyConfingInfo.CLOSE_DEVICE_AMD_ACTIVITY);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(DeviceConfig.DEVICE_CONNECTING_AUTO);
        registerReceiver(myReceiver, filter);
    }

    private LayoutAnimationController getAnimailController() {
        LayoutAnimationController con = null;
//            ScaleAnimation animation = new ScaleAnimation(1f, 1f, 0.0f, 0.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//            animation.setDuration(500);
//            animation.setInterpolator(new AccelerateInterpolator());
        Animation animation = AnimationUtils.loadAnimation(DeviceAmdActivity.this, R.anim.scale_for_recyclerview);
        con = new LayoutAnimationController(animation, 0.5f);
        con.setOrder(LayoutAnimationController.ORDER_NORMAL);
        return con;
    }

    private Animation getAnimation() {
//        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 0.0f, 0.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        animation.setDuration(460);
        return AnimationUtils.loadAnimation(DeviceAmdActivity.this, R.anim.scale_for_recyclerview);
    }

    private SwipeRefreshLayout.OnRefreshListener refeshlis = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (refreshLayout != null && refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(false);
                BleScanUtils.getBleScanUtilsInstance(getApplicationContext()).stopScan();
                clearTheListDevice();
                startScan();
            }
        }
    };

    private void init() {
        showTitleBarTitle();
        findViewById(R.id.delete_local_db_data).setOnClickListener(myListener);
        findViewById(R.id.find_device).setOnClickListener(myListener);
        findViewById(R.id.title_of_scan_device).setOnClickListener(myListener);
        findViewById(R.id.device_list).setOnClickListener(myListener);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_device);
        refreshLayout.setOnRefreshListener(refeshlis);
        int screeHe = DeviceAmdActivity.this.getResources().getDisplayMetrics().heightPixels;
        refreshLayout.setProgressViewOffset(false, -100, screeHe / 10);
        connectedName = (TextView) findViewById(R.id.connected_device_name);
        connectedStateTv = (TextView) findViewById(R.id.connected_device_state);
        helpBindDevice = (ImageView) findViewById(R.id.help_bind_device);
        connectState = (ImageView) findViewById(R.id.connect_state_imageview);
        scanProgress = (ImageView) findViewById(R.id.circle_progress_bar);
        myData = new ArrayList<>();
        deviceAdapter = new RecyclerViewAdapter(DeviceAmdActivity.this, myData);
        deviceAdapter.setOnScanDeviceItemClickListener(myItemListener);
        // 导航条透明
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //底部虚拟键透明
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        shadow = (TextView)findViewById(R.id.dialog_show_shadow);
        scanDevice = (TextView) findViewById(R.id.scan_device);
        scanDevice.setOnClickListener(myListener);
        helpBindDevice.setOnClickListener(myListener);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout_big);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.scan_device_show);

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.device_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 返回键监听
        toolbar.setNavigationIcon(R.mipmap.back_icon_new);
        toolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
//                if(getAction() != null && getAction().equals(MyConfingInfo.ACTION_SEARCH_DEVICE))
//                {
                doEnterMainActivity();
                //}
                DeviceAmdActivity.this.finish();      // 结束页面
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(DeviceAmdActivity.this.getApplicationContext(), "打开设备列表", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // FloatingActionButton,搜索按钮
        searchButton = (TextView) findViewById(R.id.seach_button);
        searchButton.setOnClickListener(myListener);

        // 显示扫描结果的 RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.Recycler_view);
//        mRecyclerView.setHasFixedSize(true);    // 方法用来使RecyclerView保持固定的大小
        // 添加分割线
//        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
//                super.onDraw(c, parent, state);
//            }
//        });

        // 设置嵌套滑动为false，即RevyclerView滑动是，同布局内的CollapsingToolbarLayout不同步滑动
        // 此设置的原因是RecyclerView内部默认设置为true，会联动滑动，所以特此设置
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置item添加移除动画
        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this));
        mRecyclerView.setAdapter(deviceAdapter);
        findViewById(R.id.factory_reset).setOnClickListener(myListener);
        scanProgress.setImageResource(R.drawable.loading_animation);
        Log.i(TAG, "scanProgress:" + scanProgress.hashCode());
    }

    private void showTitleBarTitle() {
        String type = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.USER_DEVICE_TYPE);
        if (type.equals(MyConfingInfo.DEVICE_HR)) {
            ((TextView) findViewById(R.id.device_current)).setText(R.string.brachlet);
        } else {
            ((TextView) findViewById(R.id.device_current)).setText(R.string.watch);
        }
    }

    private void showDialogAndDoContinue() {
        if (BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice()) {
            final AlertDialog.Builder dialogs = new AlertDialog.Builder(DeviceAmdActivity.this);
            dialogs.setCancelable(false);
            DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(DeviceAmdActivity.this, "点击的是:" + which, Toast.LENGTH_SHORT).show();
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            BleDataForFactoryReset.getBleDataInstance().settingFactoryReset();
//                        try {
//                            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
//                            field.setAccessible(true);
//                            field.set(dialog, false);
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        dialog.dismiss();
//                        View v = LayoutInflater.from(DeviceAmdActivity.this).inflate(R.layout.layout_for_dialog, null);
//                        dialog.setView(v);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
//                        dialog.dismiss();
                            // do nothing
                            break;
                    }
                }
            };
            dialogs.setTitle(R.string.factory_settings_title);
            dialogs.setMessage(R.string.factory_settings_remind);
            dialogs.setNegativeButton(R.string.be_cancle, dialogListener);
            dialogs.setPositiveButton(R.string.be_true, dialogListener);
            dialogs.create().show();
        } else {
            MyToastUitls.showToast(DeviceAmdActivity.this, R.string.not_connecte, 1);
        }
    }

    private NoDoubleClickListener myListener = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View view) {
            switch (view.getId()) {
                case R.id.delete_local_db_data:
                    AlertDialog.Builder deleteDialog = new AlertDialog.Builder(DeviceAmdActivity.this);
                    deleteDialog.setTitle(R.string.delete_local_data);
                    deleteDialog.setMessage(R.string.delete_all_data);
                    deleteDialog.setNegativeButton(R.string.be_cancle, null);
                    deleteDialog.setPositiveButton(R.string.be_true, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteAllData();
                        }
                    });
                    deleteDialog.show();
                    break;
                case R.id.find_device:
                    if (BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice()) {
                        AlertDialog.Builder findDeviceDialog = new AlertDialog.Builder(DeviceAmdActivity.this);
                        findDeviceDialog.setTitle(R.string.find_device);
                        findDeviceDialog.setMessage(R.string.device_shock);
                        findDeviceDialog.setNegativeButton(R.string.be_cancle, null);
                        findDeviceDialog.setPositiveButton(R.string.be_true, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BleForFindDevice.getBleForFindDeviceInstance().setListener(new DataSendCallback() {
                                    @Override
                                    public void sendSuccess(byte[] receveData) {
                                        Log.i(TAG, "找回设备。。。");
                                        mHandler.sendEmptyMessageDelayed(findDeviceMessage, 0);
                                    }

                                    @Override
                                    public void sendFailed() {
                                    }

                                    @Override
                                    public void sendFinished() {
                                    }
                                });
                                BleForFindDevice.getBleForFindDeviceInstance().findConnectedDevice((byte) 0x00);
                            }
                        });
                        findDeviceDialog.create().show();
                    } else {
                        MyToastUitls.showToast(DeviceAmdActivity.this.getApplicationContext(), R.string.not_connecte, 1);
                    }
                    break;
                case R.id.title_of_scan_device:
                    if (getAction() != null && getAction().equals(MyConfingInfo.ACTION_SEARCH_DEVICE)) {
                        String s = getIntent().getStringExtra("Device_amd_enter");
                        if (s != null) {
                            invisivableTheSecondLayout(true);
                            return;
                        }
                        doEnterMainActivity();
                        return;
                    }
                    invisivableTheSecondLayout(true);
//                    clearTheListDevice();
//                    hideTheSecondLayout();      // 隐藏扫描结果布局
//                    if(Build.VERSION.SDK_INT < 21)
//                    {
//                        mRelativeLayout.setVisibility(View.INVISIBLE);
//                        showState = SHOW_FINST_PAGER;
//                    }
//                    else
//                    {
//                        hideTheSecondLayout();
//                    }
                    break;
                case R.id.scan_device:
                    if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                        MyToastUitls.showToast(getApplicationContext(), R.string.make_sure, 1);
                        return;
                    }
                    if (BleScanUtils.getBleScanUtilsInstance(getApplicationContext()) != null
                            && BleScanUtils.getBleScanUtilsInstance(getApplicationContext()).isScanning) {
                        BleScanUtils.getBleScanUtilsInstance(getApplicationContext()).stopScan();
                    } else {
                        startScan();
                    }
                    break;
                case R.id.seach_button:
                    if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
//                        BluetoothAdapter.getDefaultAdapter().enable();
                        MyToastUitls.showToast(DeviceAmdActivity.this.getApplicationContext(), R.string.make_sure, 1);
                        return;
                    } else {
                        String deviceName = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(DeviceConfig.DEVICE_NAME);
                        if (!deviceName.equals("")) {
                            AlertDialog.Builder connectDialog = new AlertDialog.Builder(DeviceAmdActivity.this);
                            connectDialog.setCancelable(false);
                            connectDialog.setTitle(R.string.alter);
                            connectDialog.setMessage(R.string.seach_alter);
                            connectDialog.setNegativeButton(R.string.be_cancle, null);
                            connectDialog.setPositiveButton(R.string.be_true, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice() ||
                                            (BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectingDevice())) {
                                        BluetoothLeService.getInstance().disconnect();
                                        BluetoothLeService.getInstance().close(false);
                                    }
                                    clearTheDevice();
                                    updateUnbind();
                                    mHandler.removeCallbacks(runnable);
                                    closeDialog();
                                    MyToastUitls.showToast(getApplicationContext(), R.string.unbind_device_success, 1);
//                                    startScan();
//                                    showTheScanLayoutAfterUserBeTrue(true);
                                }
                            });
                            connectDialog.create().show();
                        } else {
                            showTheScanLayoutAfterUserBeTrue(true);
                            startScan();
                        }
                    }
                    break;
                case R.id.factory_reset:
                    showDialogAndDoContinue();
                    break;
                case R.id.device_list:
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(getApplicationContext(), Device_List_Activity.class);
                    intent.setAction("start_has_back_buttom");
                    startActivity(intent);
                    break;
                case R.id.help_bind_device:
                    Intent intentes = new Intent();
                    intentes.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentes.setClass(getApplicationContext(), help_bind_device.class);
                    startActivity(intentes);
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * 清除缓存设备
     */
    private void clearTheDevice() {
        LocalDataSaveTool.getInstance(DeviceAmdActivity.this.getApplicationContext()).writeSp(DeviceConfig.DEVICE_NAME, "");
        LocalDataSaveTool.getInstance(DeviceAmdActivity.this.getApplicationContext()).writeSp(DeviceConfig.DEVICE_ADDRESS, "");
    }

    private void deleteAllData() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                CircleProgressDialog.getInstance().showCircleProgressDialog(DeviceAmdActivity.this);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CircleProgressDialog.getInstance().closeCircleProgressDialog();
                    }
                }, 2000);
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                return MyDBHelperForDayData.getInstance(DeviceAmdActivity.this.getApplicationContext()).deleteAllDeviceData(DeviceAmdActivity.this.getApplicationContext());
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                sendBroadcast(new Intent(MyConfingInfo.DELETE_ALL_DATABASE));
                CircleProgressDialog.getInstance().closeCircleProgressDialog();
                MyToastUitls.showToast(DeviceAmdActivity.this.getApplicationContext(), R.string.delete_success, 1);
            }
        }.execute();
    }

    private void startScan() {
        if (BluetoothAdapter.getDefaultAdapter() != null
                && BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            MyApplication.threadService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        BleScanUtils.getBleScanUtilsInstance(getApplicationContext()).setmOnDeviceScanFoundListener(myDeviceListnenr);
                        BleScanUtils.getBleScanUtilsInstance(getApplicationContext()).scanDevice(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            MyToastUitls.showToast(DeviceAmdActivity.this.getApplicationContext(), R.string.buleunopen, 2);
        }
    }

    /**
     * 显示扫描布局并开始扫描
     */
    private void showTheScanLayoutAfterUserBeTrue(boolean userAni) {
//        if(BluetoothAdapter.getDefaultAdapter().disable())
//                        {
//                            BluetoothAdapter.getDefaultAdapter().enable();
//                        }
        // 点击按钮搜索设备
        if (Build.VERSION.SDK_INT < 21) {
            mRelativeLayout.setVisibility(View.VISIBLE);
        } else {
            if (userAni) {
                showTheScanLayout();       // 显示扫描结果布局
            } else {
                mRelativeLayout.setVisibility(View.VISIBLE);
            }
        }
        showState = SHOW_SECOND_PAGER;
//        new searchAsycnTask().execute();
//        BleScanUtils.getBleScanUtilsInstance(DeviceAmdActivity.this).scanLeDeviceLoopBegin(true, false, refreshLayout);
//        BleScanUtils.getBleScanUtilsInstance(DeviceAmdActivity.this).scanDevice(null);
//        BleScanUtils.getBleScanUtilsInstance(DeviceAmdActivity.this).setmOnDeviceScanFoundListener(myDeviceListnenr);
//        if(scanProgress.getVisibility() == View.GONE)
//        {
//            scanProgress.setVisibility(View.VISIBLE);
//            animationDrawable = (AnimationDrawable)scanProgress.getDrawable();
//            animationDrawable.setOneShot(false);
//            animationDrawable.start();
//            scanDevice.setText(R.string.cancle_scan);
//         }
    }

    private void showShockDialog(boolean showOrClose) {
        if (showOrClose) {
            if (dialogClose == null) {
                dialogClose = new Dialog(DeviceAmdActivity.this, R.style.mySizeDialog);
            }
            View view = getXmlForShock();
            dialogClose.setCanceledOnTouchOutside(false);
            dialogClose.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            dialogClose.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (animationDrawable.isRunning()) {
                        animationDrawable.stop();
                    }
                    mHandler.sendEmptyMessageDelayed(closeDeviceMessage, 80);
                    BleForFindDevice.getBleForFindDeviceInstance().findConnectedDevice((byte) 0x01);
                    if (dialogClose != null) {
                        dialogClose.cancel();
                        dialogClose = null;
                    }
                }
            });
            setDialogWidth(view);
            dialogClose.show();
        } else {
            if (dialogClose != null && dialogClose.isShowing())
                dialogClose.dismiss();
            dialogClose = null;
        }
    }

    private void setDialogWidth(View view) {
        DisplayMetrics me = getApplicationContext().getResources().getDisplayMetrics();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.width = me.widthPixels;
        view.setLayoutParams(params);
    }

    private View getXmlForShock() {
        View view = LayoutInflater.from(DeviceAmdActivity.this).inflate(R.layout.shock_dialog, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.circle_progress_shork_bar);
        imageView.setImageResource(R.drawable.loading_animation);
        animationDrawable = (AnimationDrawable) imageView.getDrawable();
        animationDrawable.setOneShot(false);
        animationDrawable.start();
        // 关闭震动
        TextView close = (TextView) view.findViewById(R.id.clock_the_shock);
        close.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                BleForFindDevice.getBleForFindDeviceInstance().findConnectedDevice((byte) 0x01);
                mHandler.sendEmptyMessageDelayed(closeDeviceMessage, 60);

            }
        });
        return view;
    }


    private void changeTheScanLayout(boolean isChange) {
        Message msg = Message.obtain();
        msg.what = CHAGE_SCAN_LAYOUT;
        if (isChange) {
            msg.arg1 = 0;
        } else {
            msg.arg1 = 1;
        }
        mHandler.sendMessage(msg);
    }

//    private void closeRefreshLayout()
//    {
//        mHandler.postDelayed(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                if(refreshLayout != null && refreshLayout.isRefreshing())
//                {
////                    refreshLayout.setProgressViewOffset(false, 0, 0);
//                    refreshLayout.setRefreshing(false);
//                }
//
//            }
//        }, 6000);
//    }


    /**
     * 实例化设备监听器，并对扫描到的设备进行监听
     */
    private BleScanUtils.OnDeviceScanFoundListener myDeviceListnenr = new BleScanUtils.OnDeviceScanFoundListener() {
        @Override
        public void OnDeviceFound(final LocalDeviceEntity mLocalDeviceEntity) {


            String scanrecord = FormatUtils.bytesToHexString(mLocalDeviceEntity.getmScanRecord());
            boolean isEquals = false;
//            Log.i(TAG, "扫描到的设备" + mLocalDeviceEntity.getName() + "  " + Thread.currentThread().getName());
            String deviceType = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.USER_DEVICE_TYPE);
            if (deviceType.equals(MyConfingInfo.DEVICE_HR)) {
                String deviceName = mLocalDeviceEntity.getName();
                if (scanrecord.contains("ffb6")) {
                    int q = scanrecord.indexOf("ffb6");
                    char ch = scanrecord.charAt(q + 5);
                    System.out.println("huangtianba" + ch);
                    if (ch == 52) {
                        if (!myData.contains(mLocalDeviceEntity)) {
                            myData.add(mLocalDeviceEntity);
                        }
                    }

                }
//                if (deviceName != null && deviceName.contains("_")) {
//                    String[] d = deviceName.split("_");
//                    if (!d[0].equals("B7")) {
//                        if (!myData.contains(mLocalDeviceEntity)) {
//                            myData.add(mLocalDeviceEntity);
//                        }
//                    }
//                } else {
//                    if (!myData.contains(mLocalDeviceEntity)) {
//                        myData.add(mLocalDeviceEntity);
//                    }
//                }
            } else if (deviceType.equals(MyConfingInfo.DEVICE_BLOOD)) {
                String deviceName = mLocalDeviceEntity.getName();
                String add = mLocalDeviceEntity.getAddress();
                String ww = mLocalDeviceEntity.getRssi() + "";
                if (scanrecord.contains("ffb6")) {
                    int q = scanrecord.indexOf("ffb6");
                    char ch = scanrecord.charAt(q + 5);
                    if (ch == 56) {
                        if (!myData.contains(mLocalDeviceEntity)) {
                            myData.add(mLocalDeviceEntity);
                        }

                    }
                }
//                if (deviceName.contains("_")) {
//                    String[] d = deviceName.split("_");
//                    if (d[0].equals("B7")) {
//                        if (!myData.contains(mLocalDeviceEntity)) {
//                            myData.add(mLocalDeviceEntity);
//                        }
//                    }
//                } else {
//                    return;
//                }
            }
            Collections.sort(myData, new Comparator<LocalDeviceEntity>() {
                @Override
                public int compare(LocalDeviceEntity lhs, LocalDeviceEntity rhs) {
                    return new Integer(rhs.getRssi()).compareTo(lhs.getRssi());
                }
            });
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    deviceAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onScanStateChange(boolean isChange) {
            changeTheScanLayout(isChange);
        }
    };


//    class searchAsycnTask extends AsyncTask<Void, Void, Boolean>
//    {
//        @Override
//        protected Boolean doInBackground(Void... params) {
//
//            // 扫描设备
//
//            return null;
//        }
//    }

    /**
     * 显示第二布局，并执行动画
     */

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showTheScanLayout() {
//                  searchButton.setOnTouchListener(new View.OnTouchListener() {
//                      @Override
//                      public boolean onTouch(View v, MotionEvent event) {
//                         cxx = event.getRawX();
//                         cyy = event.getRawY() * phone;
//                          return false;
//                      }
//                  });
//                int[] location = new int[phone];
//                searchButton.getLocationInWindow(location);
//                int x = location[0];
//                int y = location[clock];
//                    shadow.setVisibility(View.VISIBLE);
        // FloatingButton隐藏
        cxx = (int) searchButton.getX() + (searchButton.getWidth() / 2);
        cyy = mAppBarLayout.getMeasuredHeightAndState() * 2;
        // int cyyy = searchButton.getHeight();
        // get the final radius for the clipping circle 最大的作为半径
        int finalRadius = Math.max(mRelativeLayout.getWidth(), mRelativeLayout.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anims =
                ViewAnimationUtils.createCircularReveal(mRelativeLayout, (int) cxx, (int) cyy / 2, 0, finalRadius);

//        anims.addListener(new AnimatorListenerAdapter()
//        {
//            @Override
//            public void onAnimationEnd(Animator animation)
//            {
//                super.onAnimationEnd(animation);
        mRelativeLayout.setVisibility(View.VISIBLE);
        showState = SHOW_SECOND_PAGER;
//            }
//        });
        anims.start();
//        searchButton.setVisibility(View.GONE);
//        searchButton.hide();

    }

    /**
     * 显示隐藏的布局，并执行圆形动画
     *
     * @param v
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showTheInvisibleView(View v) {
        float x = v.getWidth() / 2;
        float y = v.getHeight() * 2;
        int theRadius = Math.max(v.getWidth(), v.getHeight());

        ViewAnimationUtils.createCircularReveal(v, (int) x, (int) y, 0, theRadius);
        v.setVisibility(View.VISIBLE);

    }

    /**
     * 隐藏第二布局,并执行动画
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void hideTheSecondLayout() {
        // get the center for the clipping circle
        // 获取园的中心
        cxx = (int) searchButton.getX() + (searchButton.getWidth() / 2);
        cyy = mAppBarLayout.getMeasuredHeightAndState();
//                    cyy = searchButton.getHeight() * phone;

        // get the initial radius for the clipping circle
        // 获取园的半径
        int initialRadius = mRelativeLayout.getWidth();

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(mRelativeLayout, (int) cxx, (int) cyy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mRelativeLayout.setVisibility(View.INVISIBLE);
                showState = SHOW_FINST_PAGER;
            }
        });
        // start the animation
        anim.start();

    }

    /**
     * 保存连接成功的设备
     *
     * @param deviceEntity
     */
    private void saveTheTargetDevice(LocalDeviceEntity deviceEntity) {

        // 当前时间

        long d = System.currentTimeMillis();
        Date connecteDate = new Date();
        connecteDate.setTime(d);
        // 当前的毫秒值是：1446621538047
        Log.i(TAG, "当前的毫秒值是：" + d + "设备名：" + deviceEntity.getName());

        // 先删除此条数据，再存入新数据，为的是添加到数据库最后一条

        DeviceSaveData de = LocalDataBaseUtils
                .getInstance(DeviceAmdActivity.this.getApplicationContext())
                .selectDevice(deviceEntity.getName());
        if (de != null) {   // 设备存在，则删除，再添加，
            Log.i(TAG, "存储的设备名：" + de.getDeviceName());
            LocalDataBaseUtils.getInstance(DeviceAmdActivity.this.getApplicationContext()).delete(DeviceSaveData.class, "deviceName", deviceEntity.getName());
            String change = LocalDataSaveTool.getInstance(DeviceAmdActivity.this.getApplicationContext()).readSp(MyConfingInfo.PERSION_HAS_CHANGER);
            if (change.equals("clock"))// 资料修改了
            {
//                    new BleDataForSettingParams(DeviceAmdActivity.this.getApplicationContext()).settingTheStepParamsToBracelet();
            }
        } else {
//                new BleDataForSettingParams(DeviceAmdActivity.this.getApplicationContext()).settingTheStepParamsToBracelet();
        }
        // 连接成功，将此设备存入数据库
        LocalDataBaseUtils.getInstance(DeviceAmdActivity.this.getApplicationContext()).insertDeviceList
                (connecteDate, deviceEntity.getName(), deviceEntity.getAddress());
        // 更新ui RecyclerVew显示

//             udateSaveDeviceList(deviceEntity.getName());
//            selecterFormDBAndUpdateUI();

    }

    /**
     * adapter内部对OnClick的监听，即对item的监听
     */
    RecyclerViewAdapter.OnScanDeviceItemClickListener myItemListener =
            new RecyclerViewAdapter.OnScanDeviceItemClickListener() {
                @Override
                public void OnItemClick(final int position) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                    if(refreshLayout != null && refreshLayout.isRefreshing())
//                        refreshLayout.setRefreshing(false);
                            BleScanUtils.getBleScanUtilsInstance(getApplicationContext()).stopScan();
//                    BleScanUtils.getBleScanUtilsInstance(DeviceAmdActivity.this).scanLeDeviceLoopBegin(false, false, refreshLayout);
//                    Toast.makeText(DeviceAmdActivity.this, "点击的是：第" + position + "个", Toast.LENGTH_SHORT).show();
                            final LocalDeviceEntity deviceEntity = myData.get(position);// 获取设备实体类
                            showProgressDialog("" + deviceEntity.getName());
                            Log.i(TAG, "点击的设备：" + deviceEntity.getName() + BluetoothLeService.getInstance());
                            if (BluetoothLeService.getInstance() != null) {
                                BluetoothLeService.getInstance().connect(deviceEntity);
                                Message msg = mHandler.obtainMessage();
                                msg.what = CLOSE_NOTE_NOT_CONNECT;
                                msg.obj = deviceEntity;
                                mHandler.sendMessageDelayed(msg, 12000);
                            }
                        }
                    });
                }
            };


    /**
     * 更新设备界面
     * 此position为扫描列表的position，非保存列表的position
     */
//    private void udateSaveDeviceList(String deviceName) {
//        // 通过连接的设备名查询数据库
//        DeviceSaveData deviceData = LocalDataBaseUtils.getInstance(DeviceAmdActivity.this).selectDevice(deviceName);
//        Log.i("", "查询的设备数据：" + deviceData.getDeviceName());
//
////        int index = drivaceDate.indexOf(deviceData);    // 返回此对象在集合中的下标
//
//        if(drivaceDate.size() != 0)
//        {
//            for (int i = 0; i < drivaceDate.size(); i ++ )
//            {
//                if(drivaceDate.get(i).getDeviceName().equals(deviceData.getDeviceName()))
//                {
//                    drivaceDate.remove(i);
//                }
//            }
//        }
//            drivaceDate.add(0, deviceData);               // 将该条数据添加到显示列表的第一个
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                saveDeviceAdapter.notifyDataSetChanged();
//            }
//        });
//
//    }


    /**
     * 显示dialog
     *
     * @param msg
     */
    private void showProgressDialog(String msg) {
        if (connectProgress == null) {
            connectProgress = new MyProgressDialog(DeviceAmdActivity.this);
        }
        connectProgress.build(msg);
        if (!connectProgress.isShowing()) {
            connectProgress.show();
        }
    }


    /**
     * 关闭dialog
     */
    private void closeProgressDialog() {
        if (connectProgress != null && connectProgress.isShowing()) {
            connectProgress.dismiss();
            connectProgress = null;
        }
    }

    /**
     * 广播接收器
     */
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            switch (intent.getAction()) {   // 设备已连接的广播
                case DeviceConfig.DEVICE_CONNECTE_AND_NOTIFY_SUCESSFUL:
                    mHandler.removeMessages(CLOSE_NOTE_NOT_CONNECT);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            getNewFunction();
//                            LocalDeviceEntity device = (LocalDeviceEntity)intent.getSerializableExtra("DEVICE_OK_OBJ");
                            String conName = LocalDataSaveTool.getInstance(DeviceAmdActivity.this.getApplicationContext()).readSp(DeviceConfig.DEVICE_NAME);
                            Log.i(TAG, "连接的设备名：BroadcastReceiver" + conName);
                            updateConnectState(conName);
                            if (getAction() != null && getAction().equals(MyConfingInfo.ACTION_SEARCH_DEVICE)) {
                                String s = getIntent().getStringExtra("Device_amd_enter");
                                if (s != null && !s.equals("")) {
                                    invisivableTheSecondLayout(true);
                                    return;
                                }

                                doEnterMainActivity();
                                return;
                            }
                            invisivableTheSecondLayout(true);
//                            saveTheTargetDevice(device);
//                            selecterFormDBAndUpdateUI();
                        }
                    }, 1000);
                    // 改变进度条的图片为对号
                    // 改变进度条的msg为连接成功
                    if (connectProgress != null) {
                        connectProgress.changeTheImageView(R.mipmap.target_set_ok);
                        connectProgress.changeTheText(R.string.device_connecte_successful);
                    }
                    break;
                case MyConfingInfo.ON_DEVICE_STATE_CHANGE:
                    boolean active = intent.getBooleanExtra(MyConfingInfo.DISCONNECT_STATE, false);
                    connectState.setImageResource(R.mipmap.device_disconnect);
                    String deviceName = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(DeviceConfig.DEVICE_NAME);
                    if (deviceName != null && deviceName.equals("")) {
                        updateUnbind();
                    } else {
                        connectedName.setText(deviceName);
                        //          connectedStateTv.setText(R.string.connecting_now);
                        searchButton.setText(R.string.unbind_device);
//                        mHandler.postDelayed(runnable, 40 * 1000);
                    }
                    if (dialogClose != null && dialogClose.isShowing()) {
                        dialogClose.dismiss();
                        dialogClose = null;
                    }
                    break;
                case MyConfingInfo.FACTORY_RESET_SUCCESS:
                    MyToastUitls.showToast(DeviceAmdActivity.this.getApplicationContext(), R.string.factory_reset_seccess, 1);
                    break;
                case MyConfingInfo.CLOSE_DEVICE_AMD_ACTIVITY:
                    DeviceAmdActivity.this.finish();
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                    if (state == BluetoothAdapter.STATE_ON) {
                        changeConnectStateWhenBleClose(false);
                    } else if (state == BluetoothAdapter.STATE_OFF) {
                        mHandler.removeCallbacks(runnable);
                        changeConnectStateWhenBleClose(true);
                    } else if (state == BluetoothAdapter.STATE_TURNING_ON) {

                    }
                    break;
                case DeviceConfig.DEVICE_CONNECTING_AUTO:
                    connectState.setImageResource(R.mipmap.device_disconnect);
                    String deName = LocalDataSaveTool.getInstance(DeviceAmdActivity.this).readSp(DeviceConfig.DEVICE_NAME);
                    connectedName.setText(deName);
                    searchButton.setText(R.string.unbind_device);
                    //   connectedStateTv.setText(R.string.connecting_now);
//                    mHandler.postDelayed(runnable, 40 * 1000);
                    break;
            }
        }
    };


    /**
     * 检查是否支持修改设备界面
     */
    private void getNewFunction() {
        listenerOfDeviceUI();
    }

    private void listenerOfDeviceUI() {
        BleReadDeviceMenuState.getInstance().setResultlistener(new BleReadDeviceMenuState.DevicemenuCallback() {
            @Override
            public void onGEtCharArray(byte[] data) {
//				Log.i(TAG, "当前收到显示UI");
                String dataString = FormatUtils.bytesToHexString(data);
                dataString = dataString.substring(0, dataString.length() - 4);
                String Otherdata=dataString.substring(2, dataString.length() - 4);
                char b = (char) Otherdata.charAt(0);
                //char 56表示8,高位只要不是1就支持血压页面
                if (b < 56) {
                    LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.SUPPORT_BLOOD, "1");
                   // LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.USER_DEVICE_TYPE, MyConfingInfo.DEVICE_BLOOD);
                    sendBroadcast(new Intent(MyConfingInfo.CHAGE_DEVICE_TYPE));
                } else {
                    LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.SUPPORT_BLOOD, "0");
                //    LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.USER_DEVICE_TYPE, MyConfingInfo.DEVICE_HR);
                    sendBroadcast(new Intent(MyConfingInfo.CHAGE_DEVICE_TYPE));

                }
                LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.DEVICE_CHANGE_UI, MyConfingInfo.DEVICE_SHOW_UI);
                LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.DEVICE_SHOW_UI, dataString);
                checkInfo();
            }
        });
        LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.DEVICE_CHANGE_UI, MyConfingInfo.DEVICE_HIDE_UI);
        BleDataforSyn.getSynInstance().syncCurrentTime();
        BleReadDeviceMenuState.getInstance().geistate((byte) 0x2c, (byte) 0x03);
    }

    private void checkInfo() {
        BleForQQWeiChartFacebook.getInstance().setOnDeviceCheckBack(new DataSendCallback() {
            @Override
            public void sendSuccess(byte[] receveData) {
                if (receveData[2] == (byte) 0x89) {
                    LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.DEVICE_INFO_TYPE, MyConfingInfo.DEVICE_INFO_NEW);
                } else {
                    LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.DEVICE_INFO_TYPE, MyConfingInfo.DEVICE_INFO_NORMAL);
                }
            }

            @Override
            public void sendFailed() {
            }

            @Override
            public void sendFinished() {
            }
        });
        LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.DEVICE_INFO_TYPE, MyConfingInfo.DEVICE_INFO_NORMAL);
        BleForQQWeiChartFacebook.getInstance().checkInfoType();
    }


    // 连接成功后，更新界面显示状态
    private void updateConnectState(String name) {
        if (name != null && name.equals("")) {
            if (!BluetoothLeService.getInstance().isConnectedDevice()) {
                updateUnbind();
                mHandler.removeCallbacks(runnable);
                closeDialog();
                return;
            }
        }
        connectState.setImageResource(R.mipmap.device_connect);
        connectedName.setText(name);
        //   connectedStateTv.setText(R.string.already_con);
        searchButton.setText(R.string.unbind_device);
        mHandler.removeCallbacks(runnable);
        closeDialog();
    }

    private void updateUnbind() {
        connectState.setImageResource(R.mipmap.device_disconnect);
        connectedName.setText(not_bind);
        connectedStateTv.setText("");
        searchButton.setText(R.string.bind_device);
    }

    private void invisivableTheSecondLayout(boolean useAnim) {
        if (BleScanUtils.getBleScanUtilsInstance(DeviceAmdActivity.this.getApplicationContext()).isScanning) {
//            BleScanUtils.getBleScanUtilsInstance(DeviceAmdActivity.this).scanLeDeviceLoopBegin(false, false, refreshLayout);
            BleScanUtils.getBleScanUtilsInstance(getApplicationContext()).stopScan();
        }
        if (mRelativeLayout.getVisibility() == View.VISIBLE) {
            if (Build.VERSION.SDK_INT < 21) {
                mRelativeLayout.setVisibility(View.INVISIBLE);
            } else {
                if (useAnim) {
                    hideTheSecondLayout();
                } else {
                    mRelativeLayout.setVisibility(View.INVISIBLE);
                }
            }
            showState = SHOW_FINST_PAGER;
        }


    }

    private String getAction() {
        return getIntent().getAction();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)        // 点击的是返回键
        {
            if (getAction() != null && getAction().equals(MyConfingInfo.ACTION_SEARCH_DEVICE)) {
                String s = getIntent().getStringExtra("Device_amd_enter");
                if (s != null && showState == SHOW_SECOND_PAGER) {
                    invisivableTheSecondLayout(true);
                    return false;
                } else {
                    doEnterMainActivity();
                }
            }

//           if(getAction() != null && getAction().equals(MyConfingInfo.ACTION_SEARCH_DEVICE))
//           {
//               doEnterMainActivity();
//           }
            if (showState == SHOW_SECOND_PAGER)    // 第二页
            {
                invisivableTheSecondLayout(true);
//               clearTheListDevice();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void doEnterMainActivity() {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        DeviceAmdActivity.this.onBackPressed();
    }

    private void clearTheListDevice() {
        myData.clear();
        deviceAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_device, menu);
        return true;
    }


    //    /**
//     * 当设备断开是执行此回调方法
//     */
//    @Override
//    public void OnDisconnected(LocalDeviceEntity device)
//    {
//        long time = System.currentTimeMillis();
//        Date date = new Date();
//        date.setTime(time);
//        LocalDataBaseUtils.getInstance(DeviceAmdActivity.this)
//                .insertDeviceList(date,
//                        device.getName(),
//                        device.getAddress(),
//                        MyConfingInfo.DISCONNECTE);
//        drivaceDate.clear();
//
//        selecterFormDBAndUpdateUI();
//    }
}
