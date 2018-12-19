package com.huichenghe.xinlvshuju.mainpack;


import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.huichenghe.bleControl.Ble.BleBaseDataForBlood;
import com.huichenghe.bleControl.Ble.BleBaseDataForOutlineMovement;
import com.huichenghe.bleControl.Ble.BleDataForBattery;
import com.huichenghe.bleControl.Ble.BleDataForDayData;
import com.huichenghe.bleControl.Ble.BleDataForDayHeartReatData;
import com.huichenghe.bleControl.Ble.BleDataForEachHourData;
import com.huichenghe.bleControl.Ble.BleDataForFrame;
import com.huichenghe.bleControl.Ble.BleDataForHRWarning;
import com.huichenghe.bleControl.Ble.BleDataForHardVersion;
import com.huichenghe.bleControl.Ble.BleDataForLangue;
import com.huichenghe.bleControl.Ble.BleDataForPhoneComm;
import com.huichenghe.bleControl.Ble.BleDataForSettingArgs;
import com.huichenghe.bleControl.Ble.BleDataForSettingParams;
import com.huichenghe.bleControl.Ble.BleDataForSleepData;
import com.huichenghe.bleControl.Ble.BleDataForTarget;
import com.huichenghe.bleControl.Ble.BleDataForWeather;
import com.huichenghe.bleControl.Ble.BleDataforSyn;
import com.huichenghe.bleControl.Ble.BleForGetFatigueData;
import com.huichenghe.bleControl.Ble.BleForPhoneAndSmsRemind;
import com.huichenghe.bleControl.Ble.BleForQQWeiChartFacebook;
import com.huichenghe.bleControl.Ble.BleReadDeviceMenuState;
import com.huichenghe.bleControl.Ble.BleScanUtils;
import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.bleControl.Ble.DataSendCallback;
import com.huichenghe.bleControl.Ble.DeviceConfig;
import com.huichenghe.bleControl.Ble.DeviceExceptionDeal;
import com.huichenghe.bleControl.Ble.LocalDeviceEntity;
import com.huichenghe.bleControl.WeatherEntity;
import com.huichenghe.xinlvshuju.BleDeal.BleEachDataDealer;
import com.huichenghe.xinlvshuju.BleDeal.BleExceptionAndTestDataDealer;
import com.huichenghe.xinlvshuju.BleDeal.BleFatigueDataDealer;
import com.huichenghe.xinlvshuju.BleDeal.BleSleepDataDeal;
import com.huichenghe.xinlvshuju.BleDeal.BleVersionDealer;
import com.huichenghe.xinlvshuju.BleDeal.BleWarningDataHelper;
import com.huichenghe.xinlvshuju.BleDeal.BloodDataDealer;
import com.huichenghe.xinlvshuju.BleDeal.DayDataDealer;
import com.huichenghe.xinlvshuju.BleDeal.HRDataDealer;
import com.huichenghe.xinlvshuju.BleDeal.OutlineDataDealer;
import com.huichenghe.xinlvshuju.CommonUtilss.CommonUtils;
import com.huichenghe.xinlvshuju.CustomView.CircleImageView;
import com.huichenghe.xinlvshuju.CustomView.CustomDateSelector;
import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.LocationS.LocationBack;
import com.huichenghe.xinlvshuju.LocationS.Locations;
import com.huichenghe.xinlvshuju.LocationS.WeatherUtils;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.PreferenceV.PreferencesService;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.ResideMenu.ResideMenu;
import com.huichenghe.xinlvshuju.ResideMenu.ResideMenuInfo;
import com.huichenghe.xinlvshuju.ResideMenu.ResideMenuItem;
import com.huichenghe.xinlvshuju.SDPathUtils;
import com.huichenghe.xinlvshuju.UpdataService.DeviceTypeUtils;
import com.huichenghe.xinlvshuju.UpdataService.UpdateHistoryDataService;
import com.huichenghe.xinlvshuju.Utils.BreakPhoneUtils;
import com.huichenghe.xinlvshuju.Utils.DataSecletor.NumericWheelAdapter;
import com.huichenghe.xinlvshuju.Utils.DataSecletor.OnWheelScrollListener;
import com.huichenghe.xinlvshuju.Utils.DataSecletor.WheelView;
import com.huichenghe.xinlvshuju.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;
import com.huichenghe.xinlvshuju.Utils.SaveImageUtil;
import com.huichenghe.xinlvshuju.Utils.ShotScreenForShare;
import com.huichenghe.xinlvshuju.expand_activity.MovementDetail.SleepDataHelper;
import com.huichenghe.xinlvshuju.expand_activity.ShareActivity;
import com.huichenghe.xinlvshuju.expand_activity.Treads.TrendActivity;
import com.huichenghe.xinlvshuju.http.UserAccountUtil;
import com.huichenghe.xinlvshuju.http.getHardVersionHelper;
import com.huichenghe.xinlvshuju.slide.AboutActivity;
import com.huichenghe.xinlvshuju.slide.AttionModle.LovingCareActivity;
import com.huichenghe.xinlvshuju.slide.DeviceAmdActivity;
import com.huichenghe.xinlvshuju.slide.EditPersionInfoActivity;
import com.huichenghe.xinlvshuju.slide.HelpActivity;
import com.huichenghe.xinlvshuju.slide.settinga.SettingActivity;
import com.huichenghe.xinlvshuju.slide.takePhotoPackage.TakePhotoActivity;
import com.huichenghe.xinlvshuju.zhy.utils.nextToLoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

//import static com.baidu.location.h.j.R;


public class MainActivity extends MainBaseActivity {
    private static final String TAG = "MainActivity";
    private final int MSG_SYNC_TIME = 0;                // 同步时间
    private final int MSG_GET_DEVICE_VERSION = 1;    // 版本号
    public final int MSG_GET_DEVICE_BATTERU = 2;    // 电量
    public final int MSG_GET_DAY_DATA = 3;            // 全天数据
    private final int MST_GET_ALL_HEARTREAT_DATA = 4;// 全天心率
    private final int MSG_GET_EACH_HOUR_DATA = 5;    // 分时运动数据
    private final int MSG_GET_SLEEP_DATA = 6;        // 睡眠数据
    private final int MSG_LIVE_HR_DATA = 7;
    private final int MSG_REMIND_PHONE = 8;
    private final int MSG_GET_HR_WARNING = 9;
    private final int MSG_GET_FATIGUE = 10;
    private final int MSG_SETTING_12_OR_24 = 11;
    private final int SHOWPRREVIEWCAMERA = 20;
    private final int CONNECT_AGAIN = 12;
    public final String REFRESH_DAY_DATA = "refresh data";
    public final String FIRST_GET_DAY_DATA = "first_get_day_data";
    private int countDeviceVersion = 0;
    public ResideMenu mResidemenu;
    private ResideMenuItem mResideDevice;
    private ResideMenuItem mRessideRemind;
    private ResideMenuItem mRessideTakePhoto;
    private ResideMenuItem mRessideAbout;
    private ResideMenuItem mRessideService;
    private ResideMenuItem mRessideFamlayAttion;
    private ResideMenuInfo mResideMenuInfo;
    private TextView todayActive;
    private ImageView priviosDate, nextDate;
    private boolean isLoginTrue = false;
    private TextView exitButton, loginButton;
    private boolean closeTheRequest = false;
    public String currenDate = null;
    private PopupWindow dateSelector;
    private WheelView year, month, day;
    private ArrayList<View> fragmentList;
    private active_Fragment activFragment;
    private Movement_Fragment movementFragment;
    private SleepFragment sleepFragment;
    private BloodFragment bloodFragment;
    private Locations location;
    private ArrayList<LocalDeviceEntity> myData;    // 扫描的设备集合
    // 线程通信对象
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            GetEachHourTask task = new GetEachHourTask();
            switch (msg.what) {
                case MSG_SYNC_TIME: // 同步时间
                    synchronizeTime();
                    listenerOfDeviceUI();
                    break;
                case MSG_GET_DEVICE_BATTERU:
                    getAndShowBattary(msg);
                    break;
                case MSG_GET_SET_LANGUE:
                    setDeviceLangue();
                    break;
                case MSG_GET_DEVICE_VERSION:
                    getHardVerwion();
                    break;
                case MSG_GET_DAY_DATA:  // 全天数据
                    final int arg = msg.arg1;
                    if (!closeTheRequest) {
                        if (BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice()) {
                            BleDataForDayData.getDayDataInstance(MainActivity.this.getApplicationContext()).setOnDayDataListener(new DataSendCallback() {
                                @Override
                                public void sendSuccess(byte[] receveData) {
                                    new DayDataDealer(MainActivity.this, receveData);
                                    if (conectState != null && conectState.getText().toString().equals(getString(R.string.datasynchronize)) && conectState.getVisibility() == View.VISIBLE)
                                        showConnectState(8);
                                }

                                @Override
                                public void sendFailed() {
                                    if (conectState != null && conectState.getText().toString().equals(getString(R.string.datasynchronize)) && conectState.getVisibility() == View.VISIBLE)
                                        showConnectState(9);
                                }

                                @Override
                                public void sendFinished() {
                                    if (state == 8 || state == 9)
                                        showConnectState(10);
                                    if (arg == 0) {
                                        Message msg = mHandler.obtainMessage();
                                        msg.what = MST_GET_ALL_HEARTREAT_DATA;
                                        msg.arg1 = 0;
                                        mHandler.sendMessageDelayed(msg, 0);
                                    }
                                }
                            });
                            BleDataForDayData.getDayDataInstance(MainActivity.this.getApplicationContext()).getDayData();
                        }
                    }
                    break;
                case MST_GET_ALL_HEARTREAT_DATA:
                    task.execute(2, msg.arg1);
                    break;
                case MSG_GET_EACH_HOUR_DATA:
                    task.execute(1, msg.arg1);
                    break;
                case MSG_GET_SLEEP_DATA:
                    task.execute(4, 0);
                    break;
                case MSG_LIVE_HR_DATA:
                    break;
                case MSG_GET_HR_WARNING:
                    requestHrWarningData();
                    getCheckFrameData();
                    break;
                case MSG_GET_FATIGUE:
                    getFatigueData();
                    break;
                case MSG_SETTING_12_OR_24:
                    settingArges();
                    checkInfo();
                    break;
                case SHOWPRREVIEWCAMERA:
//					takePhotoActivity = TakePhotoActivity.getTakePhotoInstance(MainActivity.this);
//					takePhotoActivity.showCameraPopWindow();
                    break;
                case CONNECT_AGAIN:
                    connectTheSaveDevice(true);
                    break;
            }
        }
    };

    private void getCheckFrameData() {
        BleDataForFrame.getInstance().setCheckFrameListener3(new DataSendCallback() {
            @Override
            public void sendSuccess(byte[] receveData) {
//				01 30 02 64 b616
                if (receveData.length >= 4) {
                    for (int i = 0; i < receveData.length - 2; i += 2) {
                        if (i == 0) {
                            LocalDataSaveTool.getInstance(getApplicationContext())
                                    .writeSp(MyConfingInfo.CHECK_SUPPORT_MSG_MAX_LENGTH, String.valueOf(receveData[i + 1] & 0xff));
                            Log.i(TAG, "消息提醒最大长度：" + String.valueOf(receveData[i + 1] & 0xff));
                        } else if (i == 2) {
                            LocalDataSaveTool.getInstance(getApplicationContext())
                                    .writeSp(MyConfingInfo.CHECK_SUPPORT_CUSTOM_REMIND_MAX_LENGTH, String.valueOf(receveData[i + 1] & 0xff));
                            Log.i(TAG, "消息提醒最大长度2：" + String.valueOf(receveData[i + 1] & 0xff));
                        }
                    }
                }
            }

            @Override
            public void sendFailed() {
            }

            @Override
            public void sendFinished() {
            }
        });
//01 03 01 04 01 05 00 30 16
        BleDataForFrame.getInstance().setCheckFrameListener(new DataSendCallback() {
            @Override
            public void sendSuccess(byte[] receveData) {
                if (receveData[0] == 1) {
                    byte[] checkData = new byte[receveData.length - 3];
                    System.arraycopy(receveData, 1, checkData, 0, checkData.length);
                    Log.i(TAG, "收到的检测帧数据：" + FormatUtils.bytesToHexString(checkData));
                    for (int i = 0; i < checkData.length; i += 2) {
                        if (i == 0) {
                            if (checkData[i + 1] == 1) {
                                LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.CHECK_SUPPORT_WEATHER_INTERFACE, MyConfingInfo.SUPPORT_TRUE);
                            } else {
                                LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.CHECK_SUPPORT_WEATHER_INTERFACE, MyConfingInfo.SUPPORT_FALSE);
                            }
                        } else if (i == 2) {
                            if (checkData[i + 1] == 1) {
                                LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.CHECK_SUPPORT_HEAETS, MyConfingInfo.SUPPORT_TRUE);
                            } else {
                                LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.CHECK_SUPPORT_HEAETS, MyConfingInfo.SUPPORT_FALSE);
                            }
                        } else if (i == 4) {
                            if (checkData[i + 1] == 1) {
                                LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.CHECK_SUPPORT_NEW_CLOCK, MyConfingInfo.SUPPORT_TRUE);
                            } else {
                                LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.CHECK_SUPPORT_NEW_CLOCK, MyConfingInfo.SUPPORT_FALSE);
                            }
                        }
                    }
                } else if (receveData[0] == 2) {
                    Log.i(TAG, "收到的检测帧数据：" + FormatUtils.bytesToHexString(receveData));
                    if (receveData[1] == 1) {
                        LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.CHECK_SUPPORT_MSG_MAX_LENGTH, String.valueOf(receveData[2] & 0xff));
                    } else if (receveData[1] == 2) {
                        LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.CHECK_SUPPORT_CUSTOM_REMIND_MAX_LENGTH, String.valueOf(receveData[2] & 0xff));
                    }
                }
            }

            @Override
            public void sendFailed() {
            }

            @Override
            public void sendFinished() {
            }
        });
        LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.CHECK_SUPPORT_NEW_CLOCK, MyConfingInfo.SUPPORT_FALSE);
        LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.CHECK_SUPPORT_HEAETS, MyConfingInfo.SUPPORT_FALSE);
        LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.CHECK_SUPPORT_WEATHER_INTERFACE, MyConfingInfo.SUPPORT_FALSE);
        LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.CHECK_SUPPORT_MSG_MAX_LENGTH, "");
        LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.CHECK_SUPPORT_CUSTOM_REMIND_MAX_LENGTH, "");
        BleDataForFrame.getInstance().getCheckFrame();
        BleDataForFrame.getInstance().getSupportParam();
        // BleDataForFrame.getInstance().getWeatherParam();
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

    private void getFatigueData() {
        BleForGetFatigueData fati = BleForGetFatigueData.getInstance(getApplicationContext());
        fati.setDataSendCallback(new DataSendCallback() {
            @Override
            public void sendSuccess(byte[] receveData) {
                new BleFatigueDataDealer(receveData, MainActivity.this.getApplicationContext());
            }

            @Override
            public void sendFailed() {
            }

            @Override
            public void sendFinished() {
                initOtherData();
            }
        });
        fati.getFatigueDayData();
    }

    private void requestHrWarningData() {
        BleDataForHRWarning hrWarning = BleDataForHRWarning.getInstance();
        hrWarning.setDataSendCallback(new MyHRWarningCallback());
        hrWarning.requestWarningData();
    }


    class MyHRWarningCallback implements DataSendCallback {
        @Override
        public void sendSuccess(byte[] receveData) {
            new BleWarningDataHelper(receveData, MainActivity.this.getApplicationContext());
        }

        @Override
        public void sendFailed() {
        }

        @Override
        public void sendFinished() {
            mHandler.sendEmptyMessageDelayed(MSG_GET_FATIGUE, 0);
        }
    }

    /**
     * 获取固件版本
     */
    private void getHardVerwion() {
        LocalDataSaveTool.getInstance(MainActivity.this.getApplicationContext()).writeSp(MyConfingInfo.HARD_VERSION, "");
        BleDataForHardVersion.getInstance().setDataSendCallback(new DataSendCallback() {
            @Override
            public void sendSuccess(byte[] bufferTmp) {
                new BleVersionDealer(bufferTmp, MainActivity.this);
            }

            @Override
            public void sendFailed() {
            }

            @Override
            public void sendFinished() {
//				Log.i(TAG, "======获取版本号版本，继续获取电量等");
                Message msgb = Message.obtain();
                msgb.what = MSG_GET_DEVICE_BATTERU;
                msgb.arg1 = 0;
                mHandler.sendMessageDelayed(msgb, 0);
            }
        });
        BleDataForHardVersion.getInstance().requestHardVersion();
    }

    /**
     * 获取设备语言
     */
    private String strlangue;//当前设备的语言

    private void getPhoneLangue() {
        Locale.getDefault().getLanguage();
        strlangue = Locale.getDefault().toString();
        Log.i(TAG, "MainActivity" + strlangue);

    }

    /**
     * 设置设备的语言
     */
    private void setDeviceLangue() {
        if (BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice()) {
            BleDataForLangue getflangue = BleDataForLangue.getInstance();
            if (strlangue.equals("th_TH")) {
                //泰语
                getflangue.getBatteryPx(2);
            } else if (strlangue.contains("zh_CN")) {
                //中文，国产手机是zh_CN但是国外手机确实zh_CH后面还有字符
                getflangue.getBatteryPx(0);
            } else {

                getflangue.getBatteryPx(1);
            }
        }
    }

    /**
     * 获取电量
     *
     * @param msg
     */
    private void getAndShowBattary(Message msg) {
        if (!closeTheRequest) {
            if (BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice()) {
                final int ar = msg.arg1;
                Log.i(TAG, "message.arg1:" + ar);
                BleDataForBattery getB = BleDataForBattery.getInstance();
                getB.setBatteryListener(new DataSendCallback() {
                    @Override
                    public void sendSuccess(byte[] receveData) {

                        updateBattery(receveData[0] & 0xff);
                    }

                    @Override
                    public void sendFailed() {
                        //    BluetoothLeService.getInstance().disconnect();
                        // connectTheSaveDevice(true);
                    }

                    @Override
                    public void sendFinished() {
                        if (ar == 0) {
                            mHandler.sendEmptyMessageDelayed(MSG_GET_HR_WARNING, 0);
                        }
                    }
                });
                getB.getBatteryPx();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void settingArges() {
        String unit = LocalDataSaveTool.getInstance(MainActivity.this.getApplicationContext()).readSp(MyConfingInfo.METRICORINCH);
        boolean is24 = DateFormat.is24HourFormat(MainActivity.this.getApplicationContext());
        BleDataForSettingArgs setArgs = BleDataForSettingArgs.getInstance(getApplicationContext());
        setArgs.setDataSendCallback(new MyArgesCallback());
        setArgs.setArgs(unit, is24);
    }

    class MyArgesCallback implements DataSendCallback {
        @Override
        public void sendSuccess(byte[] receveData) {
        }

        @Override
        public void sendFailed() {
        }

        @Override
        public void sendFinished() {
            mHandler.sendEmptyMessageDelayed(MSG_GET_DEVICE_VERSION, 0);
        }
    }

    private void listenerOfDeviceUI() {
        BleReadDeviceMenuState.getInstance().setResultlistener(new BleReadDeviceMenuState.DevicemenuCallback() {
            @Override
            public void onGEtCharArray(byte[] data) {
                Log.i(TAG, "当前收到显示UI" + FormatUtils.bytesToHexString(data));

//				03000000009816
                if (data[0] == 3) {
                    //    getBinaryStrFromByte()
                    String dataString = FormatUtils.bytesToHexString(data);
                    dataString = dataString.substring(2, dataString.length() - 4);
                    char b = (char) dataString.charAt(0);
//                    //char 56表示8,高位只要不是1就支持血压页面
//                    if (b < 56) {
//                        LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.SUPPORT_BLOOD, "1");
//                        RadioButton rb = (RadioButton) findViewById(R.id.blood_button);
//                        if (rb.getVisibility() == View.GONE) {
//                            rb.setVisibility(View.VISIBLE);
//
//                        }
////                        initFragment011();
//                    } else {
//                        LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.SUPPORT_BLOOD, "0");
//                        RadioButton rb = (RadioButton) findViewById(R.id.blood_button);
//                        if (rb.getVisibility() == View.VISIBLE) {
//                            rb.setVisibility(View.GONE);
//                        }
//                        // initFragment011();
//                    }
                    LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.DEVICE_SHOW_UI, dataString);
                } else {
                    LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.DEVICE_CHANGE_UI, MyConfingInfo.DEVICE_SHOW_UI);
                }
            }
        });
        LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.DEVICE_SHOW_UI, "");//20FCFFFF
        LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.DEVICE_CHANGE_UI, "");
        BleReadDeviceMenuState.getInstance().geistate((byte) 0x2c, (byte) 0x02);
        BleReadDeviceMenuState.getInstance().geistate((byte) 0x2c, (byte) 0x03);
    }

    /**
     * 同步时间并发下一条指令
     */
    private void synchronizeTime() {
        Log.i(TAG, "执行synTime");
        BleDataforSyn syn = BleDataforSyn.getSynInstance();
        syn.setDataSendCallback(new DataSendCallback() {
            @Override
            public void sendSuccess(byte[] receveData) {
            }

            @Override
            public void sendFailed() {
            }

            @Override
            public void sendFinished() {
                mHandler.sendEmptyMessageDelayed(MSG_SETTING_12_OR_24, 0);
            }
        });
        syn.syncCurrentTime();
    }


    private getHardVersionHelper helper;
    /**
     * 检查更新固件
     * @param hardV
     */
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//	private GoogleApiClient client;

    /**
     * 更新电量显示
     *
     * @param battery
     */
    private void updateBattery(final int battery) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (battery >= 0 && mResideMenuInfo != null) {
                    String ba = battery + "%";
                    mResideMenuInfo.setBattery(ba);
                    if (battery < 30) {
                        mResideMenuInfo.seteleicon(R.mipmap.ele20);
                    } else if (battery < 50) {
                        mResideMenuInfo.seteleicon(R.mipmap.ele40);
                    } else if (battery < 70) {
                        mResideMenuInfo.seteleicon(R.mipmap.ele60);
                    } else if (battery < 90) {
                        mResideMenuInfo.seteleicon(R.mipmap.ele80);
                    } else {
                        mResideMenuInfo.seteleicon(R.mipmap.ele100);
                    }
                    // mResideMenuInfo.setIcon(R.mipmap.20);
                } else {
                    if (mResideMenuInfo != null)
                        mResideMenuInfo.setBattery("X");
                }
            }
        });

    }

    private void getKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.huichenghe.xinlvsh01", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                Log.i("TAG", "KeyHash:" + info.signatures.length);
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String key = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                System.out.println("key========" + key);
                Log.i("TAG", "KeyHash:" + key);
                Log.i("TAG", "KeyHash:" + key);
                Log.i("TAG", "KeyHash:" + key);
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题
        setContentView(R.layout.layout_first_pager);
        Log.i(TAG, "MainActivity---onCreate" + savedInstanceState);
        initResidemenu();
        init();
        registerreceiverphone();
        registBroatcast();
        //registCallingListener();
        startNotifyService();
        createDayDB();
        initFourPagerFragment();
        setOutlineListener();
        bindUpdateService();
        //第一次安装APP的时候无法获取版本号，造成点击运动事件获取版本号出错
        //
    }

    /**
     * 绑定上传服务
     */
    private void bindUpdateService() {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), UpdateHistoryDataService.class);
        bindService(intent, loadDataServiceConnect, Context.BIND_AUTO_CREATE);
    }

    /**
     * 解绑上传服务
     */
    private void unbindUpdataService() {
        unbindService(loadDataServiceConnect);
    }

    ServiceConnection loadDataServiceConnect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "绑定上传service成功");
            UpdateHistoryDataService mService = ((UpdateHistoryDataService.UpdateHistoryBind) service).getService();
//			mService.upLoadData();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private void setLocationConfigue(final int wat, final String date, final int dateCount, final byte byt) {
        location = new Locations(getApplicationContext(), new LocationBack() {
            @Override
            public void result(String re) {
                //通过城市名来判断是否是在国外
                String is_china = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.IS_CHINA);
                if (is_china.equals("china") || is_china == null) {
                    String s = re;
                    parseWeatherResult(re, wat, date, dateCount, byt);
                } else {
                    String forecastfor3 = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.FORCAST);
                    parsehefengWeatherResult(forecastfor3, wat, date, dateCount, byt);
                }
            }
        });
        location.registListener();
    }

    /**
     * 和风天气解析
     *
     * @param s
     * @param wat
     * @param date
     * @param dateCount
     * @param byt
     */
    private void parsehefengWeatherResult(String s, int wat, String date, int dateCount, byte byt) {
        Log.i(TAG, "当前日期：" + date + "   天数：" + dateCount + "   wat:" + wat);
        try {

            JSONArray jsonforcast = new JSONArray(s);
            JSONObject jsontodayweather = jsonforcast.getJSONObject(0);//今天天气
            String weatherDate = jsontodayweather.getString("date") + " " + "9:00";//日期
            JSONObject jsonwea = jsontodayweather.getJSONObject("cond");
            String weather = jsonwea.getString("txt_d");//天气
            String Code = jsonwea.getString("code_d");//天气code
            String zwx = jsontodayweather.getString("uv");//紫外线
            JSONObject jsontmp = jsontodayweather.getJSONObject("tmp");
            String tempMax = jsontmp.getString("max");
            String tempMin = jsontmp.getString("min");
            String tmp = tempMax + "℃~" + tempMin + "℃";
            // String tmp = "-7℃~" + "-2℃";
            JSONObject jsonwind = jsontodayweather.getJSONObject("wind");
            String strwind = jsonwind.getString("sc");//风力
            String winddir = jsonwind.getString("dir");//风向
            int we = WeatherUtils.comparecode(Code);
            int intzyx = Integer.parseInt(zwx);
            if (strwind.contains("-")) {
                String[] ss = strwind.split("-");
                strwind = ss[1];
            }
            int intfl = WeatherUtils.getWindli(strwind);
            int intfx = WeatherUtils.getDriect(winddir);
            String aqi = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.AQI);
            String currentTemp = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.NOWTEMP);
            String city = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.USERCITY);

            switch (wat) {
                case 0:
                    BleDataForWeather.getIntance().sendWeather(byt, city, weatherDate, we, weather, tmp, intzyx, intfl, intfx, aqi, currentTemp);
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    // 请求3天数据
                    List<WeatherEntity> datas = new ArrayList<>();
                    datas.add(new WeatherEntity(date, we, tmp));
                    String[] days = date.split("-");
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Log.i("weathers", "第一天date：" + date);
                    for (int i = 1; i < dateCount; i++) {
                        JSONObject jsonObject = jsonforcast.getJSONObject(i);//今天天气
                        JSONObject jsoncond = jsonObject.getJSONObject("cond");
                        String code = jsoncond.getString("code_d");//天气code
                        int wea = WeatherUtils.comparecode(code);
                        int yearS = Integer.valueOf(days[0]);
                        int monS = Integer.valueOf(days[1]) - 1;
                        int dayS = Integer.valueOf(days[2]) + i;
                        calendar.set(yearS, monS, dayS);
                        String thisDate = form.format(calendar.getTime());
                        JSONObject jsontmpe = jsonObject.getJSONObject("tmp");
                        String tmpe = jsontmpe.getString("max") + "℃~" + jsontmpe.getString("min") + "℃";
                        datas.add(new WeatherEntity(thisDate, wea, tmpe));
                    }
                    BleDataForWeather.getIntance().sendDatesWeather(byt, city, datas);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 小米天气解析
     *
     * @param s
     * @param wat
     * @param date
     * @param dateCount
     * @param byt
     */
    private void parseWeatherResult(String s, int wat, String date, int dateCount, byte byt) {
        Log.i(TAG, "当前日期：" + date + "   天数：" + dateCount + "   wat:" + wat);
        try {
            JSONObject dataJson = new JSONObject(s);
            String today = dataJson.getString("today");
            String forecast = dataJson.getString("forecast");
            String aqiA = dataJson.getString("aqi");
            String realtime = dataJson.getString("realtime");
            JSONObject todayData = new JSONObject(today);
            JSONObject forecastData = new JSONObject(forecast);
            JSONObject aqiData = new JSONObject(aqiA);
            JSONObject real = new JSONObject(realtime);
            String weather = null, currentTemp = null, fl1 = null, fx1 = null,
                    aqi = null, city = null, index_uv = null, weatherDate = null, temp = null;
            Log.i(TAG, "解析天气数据的时间：：" + weatherDate);
            switch (wat) {
                case 0:
                    String dates = todayData.getString("date");
                    String tempMax = todayData.getString("tempMax");
                    String tempMin = todayData.getString("tempMin");
                    temp = tempMax + "℃~" + tempMin + "℃";
                    weather = real.getString("weather");
                    currentTemp = real.getString("temp");
                    fl1 = real.getString("WS");
                    fx1 = real.getString("WD");
                    String time = real.getString("time");
                    aqi = aqiData.getString("aqi");
                    city = aqiData.getString("city");
                    index_uv = forecastData.getString("index_uv");
                    weatherDate = dates + " " + time;
                    int we = WeatherUtils.compare(weather);
                    int zyx = WeatherUtils.getXYX(index_uv);
                    int fl = WeatherUtils.getWind(fl1);
                    int fx = WeatherUtils.getDriect(fx1);
                    BleDataForWeather.getIntance().sendWeather(byt, city, weatherDate, we, weather, temp, zyx, fl, fx, aqi, currentTemp);
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    temp = forecastData.getString("temp" + wat);
                    weather = forecastData.getString("weather" + wat);
                    fl1 = forecastData.getString("fl" + wat);
                    fx1 = forecastData.getString("wind" + wat);
                    city = aqiData.getString("city");
                    weatherDate = date;
                    int wee = WeatherUtils.compare(weather);
                    int zyxx = WeatherUtils.getXYX(index_uv);
                    int fll = WeatherUtils.getWind(fl1);
                    int fxx = WeatherUtils.getDriect(fx1);
                    BleDataForWeather.getIntance().sendWeather(byt, city, weatherDate, wee, weather, temp, zyxx, fll, fxx, aqi, currentTemp);
                    break;
                case 7:
                    // 请求6天数据
                    List<WeatherEntity> datas = new ArrayList<>();
                    String tempMax7 = todayData.getString("tempMax");
                    String tempMin7 = todayData.getString("tempMin");
                    temp = tempMax7 + "℃~" + tempMin7 + "℃";
                    weather = real.getString("weather");
                    datas.add(new WeatherEntity(date, WeatherUtils.compare(weather), temp));
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String[] days = date.split("-");
                    Log.i("weathers", "第一天date：" + date);
                    for (int i = 1; i < dateCount; i++) {
                        weather = forecastData.getString("weather" + i);
                        temp = forecastData.getString("temp" + i);
                        int weathers = WeatherUtils.compare(weather);
                        int yearS = Integer.valueOf(days[0]);
                        int monS = Integer.valueOf(days[1]) - 1;
                        int dayS = Integer.valueOf(days[2]) + i;
                        calendar.set(yearS, monS, dayS);
                        String thisDate = form.format(calendar.getTime());
                        Log.i("weathers", "剩下的date：" + thisDate);
                        datas.add(new WeatherEntity(thisDate, weathers, temp));
                    }
                    BleDataForWeather.getIntance().sendDatesWeather(byt, city, datas);
                    break;
            }
            Log.i(TAG, "解析天气数据的天气：：" + weather);
            Log.i(TAG, "解析天气数据的温度：：" + temp);
            Log.i(TAG, "解析天气数据的紫外线：：" + index_uv);
            Log.i(TAG, "解析天气数据的风力：：" + fl1);
            Log.i(TAG, "解析天气数据的风向：：" + fx1);
            Log.i(TAG, "解析天气数据的空气质量：：" + aqi);
            Log.i(TAG, "解析天气数据的地点：：" + city);
            Log.i(TAG, "解析天气数据的当前温度：：" + currentTemp);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private final String FRAGMENT_ACTIVE = "fragment_active";
    private final String FRAGMENT_MOVEMENT = "fragment_movement";
    private final String FRAGMENT_SLEEP = "fragment_sleep";
    private final String FRAGMENT_BLOOD = "fragment_blood";

    private void initFourPagerFragment() {
        FragmentManager frManager = getSupportFragmentManager();
        initFragment01();
        initCheckButton(frManager, activFragment, movementFragment, sleepFragment, bloodFragment);
    }


    private void initFragment01() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (activFragment == null) {
            activFragment = new active_Fragment();
            fragmentTransaction.add(R.id.main_pager, activFragment, FRAGMENT_ACTIVE);
        }
        hideOther(fragmentTransaction);
        fragmentTransaction.show(activFragment);
        fragmentTransaction.commit();
    }

    private void initFragment02() {
        FragmentTransaction fragmentTr = getSupportFragmentManager().beginTransaction();
        if (movementFragment == null) {
            movementFragment = new Movement_Fragment();
            fragmentTr.add(R.id.main_pager, movementFragment, FRAGMENT_MOVEMENT);
        }
        hideOther(fragmentTr);
        fragmentTr.show(movementFragment);
        fragmentTr.commit();
    }

    private void initFragment03() {
        FragmentTransaction frTransaction = getSupportFragmentManager().beginTransaction();
        if (sleepFragment == null) {
            sleepFragment = new SleepFragment();
            frTransaction.add(R.id.main_pager, sleepFragment, FRAGMENT_SLEEP);
        }
        hideOther(frTransaction);
        frTransaction.show(sleepFragment);
        frTransaction.commit();
    }

    private void initFragment04() {
        FragmentTransaction task = getSupportFragmentManager().beginTransaction();
        if (bloodFragment == null) {
            bloodFragment = new BloodFragment();
            task.add(R.id.main_pager, bloodFragment, FRAGMENT_BLOOD);
        }
        hideOther(task);
        task.show(bloodFragment);
        task.commit();
    }

    // 隐藏fragment
    private void hideOther(FragmentTransaction fragmentTransaction) {
        if (activFragment != null) {
            fragmentTransaction.hide(activFragment);
        }
        if (movementFragment != null) {
            fragmentTransaction.hide(movementFragment);
        }
        if (sleepFragment != null) {
            fragmentTransaction.hide(sleepFragment);
        }
        if (bloodFragment != null) {
            fragmentTransaction.hide(bloodFragment);
        }
    }

    private void initCheckButton(final FragmentManager frManager, final Fragment activeFragment,
                                 final Fragment movementFragment, final Fragment sleepFragment, final Fragment bloodFragment) {
        checkPagerShow();
        RadioGroup.OnCheckedChangeListener checkChange = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.active_button:
                        try {
                            initFragment01();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
//						changeFragment(R.id.active_button, frManager, R.id.main_pager, activeFragment, FRAGMENT_ACTIVE);
                        break;
                    case R.id.movement_button:
                        initFragment02();
//						changeFragment(R.id.movement_button, frManager, R.id.main_pager, movementFragment, FRAGMENT_MOVEMENT);
                        break;
                    case R.id.sleep_button:
                        initFragment03();
//						changeFragment(R.id.sleep_button, frManager, R.id.main_pager, sleepFragment, FRAGMENT_SLEEP);
                        break;
                    case R.id.blood_button:
                        initFragment04();
//						changeFragment(R.id.blood_button, frManager, R.id.main_pager, bloodFragment, FRAGMENT_BLOOD);
                        break;
                }

            }
        };
        RadioGroup buttonGroup = (RadioGroup) findViewById(R.id.bottom_radio_group);
        buttonGroup.setOnCheckedChangeListener(checkChange);
    }

    private void checkPagerShow() {
        RadioButton rb = (RadioButton) findViewById(R.id.blood_button);
        RadioButton ac_rb = (RadioButton) findViewById(R.id.active_button);
        ac_rb.toggle();
        String userDeviceType = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.USER_DEVICE_TYPE);
        String is_support = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.SUPPORT_BLOOD);
        if ((userDeviceType != null && userDeviceType.equals(MyConfingInfo.DEVICE_HR))) {
            if(is_support.equals("0")){
                if (rb.getVisibility() == View.VISIBLE) {
                    rb.setVisibility(View.GONE);
                }
            }else if(is_support.equals("1")){
                if (rb.getVisibility() == View.GONE) {
                    rb.setVisibility(View.VISIBLE);
                }
            }

        } else if (userDeviceType != null && userDeviceType.equals("") || userDeviceType != null && userDeviceType.equals(MyConfingInfo.DEVICE_BLOOD)) {
            if (rb.getVisibility() == View.GONE) {
                rb.setVisibility(View.VISIBLE);
            }
        }

    }

    private void changeFragment(int buttonId, FragmentManager fr, int connerId, Fragment targetFragment, String flag) {
        if (((RadioButton) MainActivity.this.findViewById(buttonId)).isChecked()) {
            fr.beginTransaction().replace(connerId, targetFragment, flag).commit();
        }
    }


    BleExceptionAndTestDataDealer bleExceptionAndTestDataDealer = null;

    /**
     * 监听离线事件
     * niubi: 01e003100090fff003000080ffe003100090fff003100090ffe003100080ff828cba16
     * niubi: 01e003000080fff0030000a0ffe003000090ffd003100090ffe003000090ff838c8b16
     * niubi: 01e003100090ffc003000090fff003f0ffa0ffe003100090ffe003100090ff848c9b16
     * 05-22 11:12:38.054: I/MainActivity(14858): niubi: 01e003000090ffd003000080ffd003100080ffe0030000a0ffe0031000a0ff858c7d16
     * 05-22 11:12:38.275: I/MainActivity(14858): niubi: 01f003200090ffe003100080ffe003100080ffd003100090ffe003100090ff868cbe16
     * 05-22 11:12:38.477: I/MainActivity(14858): niubi: 01f003100080ffe003100080ffd003000080ffe003100080ffe0030000a0ff878c7f16
     * 05-22 11:12:38.643: I/MainActivity(14858): niubi: 01d003000080ffe0030000a0ffe0031000a0ffd003100090ffd003100090ff888c9016
     * 05-22 11:12:38.843: I/MainActivity(14858): niubi: 01e003000090ffd003000080fff003100080ffd0031000a0ffe003100090ff898c9116
     * 05-22 11:12:39.043: I/MainActivity(14858): niubi: 01d003000080ffe003000080ffe0030000a0ffe003000080ffc003100090ff8a8c4216
     */
    private void setOutlineListener() {
        if (bleExceptionAndTestDataDealer == null) {
            bleExceptionAndTestDataDealer = new BleExceptionAndTestDataDealer(MainActivity.this.getApplicationContext());
        }
        DeviceExceptionDeal.getExceptionInstance(MainActivity.this.getApplicationContext()).setOnExceptionData(new DataSendCallback() {
            @Override
            public void sendSuccess(byte[] receveData) {

                Log.i(TAG, "niubi: " + com.huichenghe.bleControl.Utils.FormatUtils.bytesToHexString(receveData));
                byte[] temp = new byte[receveData.length - 1];
                System.arraycopy(receveData, 1, temp, 0, temp.length);
                if (receveData[0] == (byte) 0x00) {
                    bleExceptionAndTestDataDealer.saveExceptionData(receveData);
                } else {
                    bleExceptionAndTestDataDealer.dealTextData(receveData);
                }
            }

            @Override
            public void sendFailed() {
            }

            @Override
            public void sendFinished() {
            }
        });

        BleBaseDataForOutlineMovement.getOutlineInstance().setOnOutLineDataListener(new DataSendCallback() {
            @Override
            public void sendSuccess(byte[] receveData) {
                new OutlineDataDealer(MainActivity.this.getApplicationContext(), receveData);
            }

            @Override
            public void sendFailed() {
            }

            @Override
            public void sendFinished() {
            }
        });

//		String acc = UserAccountUtil.getAccount(getApplicationContext());
//		MyDBHelperForDayData.getInstance(getApplicationContext()).selectBloodData(getApplicationContext(), acc, "2016-10-16 12:12");
//		MyDBHelperForDayData.getInstance(getApplicationContext()).insertBloodData(acc, "2016-10-16 12:12", 120, 23, 44, 44, 55);
        //04-11 17:10:19.501: I/MainActivity(8603): 血压数据：00 f8 0d ed 58 60 48 43 5f 64 14 16
//04-12 09:50:25.961: I/MainActivity(19831): 血压数据：00 46 f8 ed 58  64(收缩压) 3c（舒张压） 4c（心率）61 （spo2）645016

        BleBaseDataForBlood.getBloodInstance().setOnBloodDataListener(new DataSendCallback() {
            @Override
            public void sendSuccess(byte[] receveData) {
                Log.i(TAG, "血压数据：" + FormatUtils.bytesToHexString(receveData));
                new BloodDataDealer(MainActivity.this, receveData);
                if (bloodFragment != null) {
                    bloodFragment.LoadBloodData(currenDate);
                }
            }

            @Override
            public void sendFailed() {
            }

            @Override
            public void sendFinished() {
            }
        });
        BleDataForDayData.getDayDataInstance(MainActivity.this.getApplicationContext()).setOnDayDataListener(new DataSendCallback() {
            @Override
            public void sendSuccess(byte[] receveData) {
                new DayDataDealer(MainActivity.this, receveData);
            }

            @Override
            public void sendFailed() {
            }

            @Override
            public void sendFinished() {
            }
        });
        BleDataForEachHourData.getEachHourDataInstance().setOnBleDataReceListener(new DataSendCallback() {
            @Override
            public void sendSuccess(byte[] receveData) {
                new BleEachDataDealer(MainActivity.this, receveData);
            }

            @Override
            public void sendFailed() {
            }

            @Override
            public void sendFinished() {
            }
        });

        BleDataForDayHeartReatData.getHRDataInstance(getApplicationContext()).setOnHrDataRecever(new DataSendCallback() {
            @Override
            public void sendSuccess(byte[] receveData) {
                new HRDataDealer(receveData, MainActivity.this.getApplicationContext());
            }

            @Override
            public void sendFailed() {
            }

            @Override
            public void sendFinished() {
            }
        });

        BleDataForSleepData.getInstance(getApplicationContext()).setOnSleepDataRecever(new DataSendCallback() {
            @Override
            public void sendSuccess(byte[] receveData) {
                new BleSleepDataDeal(receveData, MainActivity.this);
            }

            @Override
            public void sendFailed() {
            }

            @Override
            public void sendFinished() {
            }
        });

        BleDataForTarget.getInstance().setSendCallback(new DataSendCallback() {
            @Override
            public void sendSuccess(byte[] receveData) {
                String stepTarget = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.TARGET_SETTING_VALUE);
                String sleepTarget = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.TARGET_SETTING_VALUE_SLEEP);
                if (stepTarget != null && stepTarget.equals("")) {
                    stepTarget = "8000";
                }
                if (sleepTarget != null && sleepTarget.equals("")) {
                    sleepTarget = "8";
                }
                asyncTarget(Integer.parseInt(stepTarget), Integer.parseInt(sleepTarget) * 60);
            }

            @Override
            public void sendFailed() {
            }

            @Override
            public void sendFinished() {
            }
        });

        BleDataForWeather.getIntance().setWeatherCallbackListener(new DataSendCallback() {
            @Override
            public void sendSuccess(byte[] receveData) {
                Log.i(TAG, "收到的天气请求：" + FormatUtils.bytesToHexString(receveData));
//				010d0801114616
//				02080111064016
                switch (receveData[0]) {
                    case 1:
                        int day = receveData[2] & 0xff;
                        int mon = receveData[3] & 0xff;
                        int yea = (receveData[4] & 0xff) + 2000;
//						String date = yea + "-" + mon + "-" + day;
                        Calendar calenda = Calendar.getInstance(Locale.getDefault());
                        calenda.set(yea, mon - 1, day);
                        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String date = formate.format(calenda.getTime());
                        Calendar calendar = Calendar.getInstance();
                        int dayy = calendar.get(Calendar.DAY_OF_MONTH);
                        int monn = calendar.get(Calendar.MONTH) + 1;
                        if (mon == monn) {
                            int we = day - dayy;
                            if (we < 0 || we > 6) return;
                            dealSpecityDay(date, 1, we, BleDataForWeather.toDeviceNew);
                        }
                        break;
                    case 2:
                        int day2 = receveData[1] & 0xff;
                        int mon2 = receveData[2] & 0xff;
                        int yea2 = (receveData[3] & 0xff) + 2000;
                        int dayCount = receveData[4] & 0xff;
                        Calendar calendass = Calendar.getInstance(Locale.getDefault());
                        calendass.set(yea2, mon2 - 1, day2);
                        SimpleDateFormat formatess = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String date2 = formatess.format(calendass.getTime());
                        Calendar calendar2 = Calendar.getInstance();
                        int dayy2 = calendar2.get(Calendar.DAY_OF_MONTH);
                        int monn2 = calendar2.get(Calendar.MONTH) + 1;
                        if (mon2 == monn2) {
                            int we = day2 - dayy2;
                            if (we < 0 || we > 6) return;
                            dealSpecityDay(date2, dayCount, 7, BleDataForWeather.toDeviceNew);
                        }
                        break;
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

    private void dealSpecityDay(String date, int count, int i, byte byt) {
        setLocationConfigue(i, date, count, byt);
        location.startGps();
    }

    private void asyncTarget(int sportTarget, int sleepTarget) {
        String today = getTodayDate();
        String yes = getBeforDay(today);
        String[] sleepData = new SleepDataHelper(getApplicationContext()).loadSleepData(yes, today, UserAccountUtil.getAccount(getApplicationContext()));
        int minu = sleepData[2].length() * 10;
        int hour = minu / 60;
        int minute = minu % 60;
        BleDataForTarget.getInstance().sendTargetTo(sportTarget, sleepTarget, hour, minute);
    }

    private String getBeforDay(String currentDate) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        String[] eaDate = currentDate.split("-");
        calendar.set(Integer.parseInt(eaDate[0]), Integer.parseInt(eaDate[1]) - 1, Integer.parseInt(eaDate[2]) - 1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(calendar.getTime());
    }


    private void readDayDataAndBattery() {
        if (BluetoothLeService.getInstance() == null) {
            return;
        }
        if (BluetoothLeService.getInstance().isConnectedDevice()) {
//			int delay = new BleDataForDayData(getApplicationContext()).requestDayDate();
//			try {
//				Thread.sleep(new SendLengthHelper().getSendLengthDelayTime(delay));
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			BleDataForBattery getB = BleDataForBattery.getInstance();
//			getB.setBatteryListener(batteryListener);
//			getB.getBatteryFromBr();
        }
    }

    private void getBigestMerory() {
        Log.i(TAG, "mistep的最大内存：" + Runtime.getRuntime().maxMemory());
        Log.i(TAG, "mistep的totle内存：" + Runtime.getRuntime().totalMemory());
        Log.i(TAG, "mistep的free内存：" + Runtime.getRuntime().freeMemory());
    }


    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.i(TAG, "MainActivity---onPostCreate");
        // 初始化toolbarlayout
//		RelativeLayout toolbarLayout = (RelativeLayout) findViewById(R.id.toolbar_layout_for_main);
//		toolbarLayout.setPadding(0, getStatusBarHeight(), 0, 0);
//		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) toolbarLayout.getLayoutParams();
//		params.height = (int)(getStatusBarHeight() + DpUitls.DpToPx(MainActivity.this.getApplicationContext(), 56));
//		toolbarLayout.setLayoutParams(params);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "MainActivity----onRestart");
    }


    private void intiFragment() {
        /**
         * //		bloodFragment = new BloodFragment();
         View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.main_head_layout, null, false);
         fragmentList = new ArrayList<>(2);
         fragmentList.add(view);
         //		fragmentList.add(bloodFragment);
         MyViewPagerDetialAdapter viewAdapter = new MyViewPagerDetialAdapter(fragmentList);
         ViewPager viewpager = (ViewPager)findViewById(R.id.main_pager);
         viewpager.setAdapter(viewAdapter);
         Log.i(TAG, "stepFragmentLikes = " + stepFragmentLikes);
         //		 stepFragmentLikes = new StepFragmentLikes(view, MainActivity.this);
         viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
         {
         @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
         {
         }
         @Override public void onPageSelected(int position)
         {
         if(position == 0)
         {
         mResidemenu.menuUnLock();
         }
         else
         {
         mResidemenu.menuLock();
         }
         }
         @Override public void onPageScrollStateChanged(int state)
         {

         }
         });
         */
    }


    private void startNotifyService() {
//		Intent intent = new Intent();
//		intent.setClass(MainActivity.this, NotifyService.class);
//		startService(intent);
    }

    private void createDayDB() {
        Calendar calendar = Calendar.getInstance();
        Date d = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String day = format.format(d);
        String userAccount = UserAccountUtil.getAccount(MainActivity.this.getApplicationContext());
        Cursor mCursor = MyDBHelperForDayData.getInstance(MainActivity.this.getApplicationContext())
                .selectTheSleepData(MainActivity.this.getApplicationContext(), userAccount, day, DeviceTypeUtils.getDeviceType(getApplicationContext()));
        if (mCursor.getCount() <= 0) {
            MyDBHelperForDayData.getInstance(MainActivity.this.getApplicationContext())
                    .insertTheSleepData(MainActivity.this.getApplicationContext(),
                            userAccount, day, "",
                            DeviceTypeUtils.getDeviceType(getApplicationContext()), "1");
        }
    }

    //获取是否存在NavigationBar
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
        }
        return hasNavigationBar;
    }


    private int getNavigationBarHeight() {
        Resources resources = MainActivity.this.getApplicationContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        Log.v("dbw", "Navi height:" + height);
        return height;
    }

    boolean has = false;

    private void initResidemenu() {
        mResidemenu = new ResideMenu(MainActivity.this.getApplicationContext());
        mResideDevice = new ResideMenuItem(MainActivity.this.getApplicationContext(), R.mipmap.device_amd, R.string.device_adm);
        mRessideRemind = new ResideMenuItem(MainActivity.this.getApplicationContext(), R.mipmap.settings, R.string.setting);
        mRessideTakePhoto = new ResideMenuItem(MainActivity.this.getApplicationContext(), R.mipmap.camera, R.string.take_photo);
        mRessideAbout = new ResideMenuItem(MainActivity.this.getApplicationContext(), R.mipmap.about, R.string.about);
//		mRessideService = new ResideMenuItem(MainActivity.this.getApplicationContext(), R.mipmap.app_service, R.string.app_service);
//		mRessideFamlayAttion = new ResideMenuItem(MainActivity.this.getApplicationContext(), R.mipmap.loving_attion, R.string.loving_care);
        mResidemenu.setBackground(R.mipmap.solid_bg);
        mResidemenu.attachToActivity(this);
        mResidemenu.setScaleValue(0.46f);
        mResidemenu.setScaleValueY(1.0f);
        mResidemenu.setDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        mResidemenu.addMenuItem(mResideDevice);
//		mResidemenu.addMenuItem(mRessideFamlayAttion);
        mResidemenu.addMenuItem(mRessideTakePhoto);
        mResidemenu.addMenuItem(mRessideRemind);
//		mResidemenu.addMenuItem(mRessideService);
        mResidemenu.addMenuItem(mRessideAbout);
        mResideMenuInfo = new ResideMenuInfo(MainActivity.this.getApplicationContext());
        mResidemenu.addMenuInfo(mResideMenuInfo);

        exitButton = mResidemenu.getExitButton();
        loginButton = mResidemenu.getLoginButton();
        showNickName();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            boolean has = checkDeviceHasNavigationBar(MainActivity.this.getApplicationContext());
            Log.i(TAG, "是否有虚拟按键：" + has);
            if (has) {
                mResidemenu.setPadding(0, 0, 0, getNavigationBarHeight());
            }
        }
    }

    private void showNickName() {
        String nick = LocalDataSaveTool.getInstance(MainActivity.this.getApplicationContext()).readSp(MyConfingInfo.USER_NICK);
        if (nick != null && !nick.isEmpty()) {
            mResideMenuInfo.setNickName(nick);
        }
    }


    private void loadAnimail(RelativeLayout center) {
        Animation animation = AnimationUtils.loadAnimation(MainActivity.this.getApplicationContext(), R.anim.scale_for_recyclerview);
        center.setAnimation(animation);
        center.startLayoutAnimation();
    }

    /**
     * 注册监听来电广播
     */
    private void registerreceiverphone() {
        mBroadcastReceiver = new BroadcastReceiverMgr();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(B_PHONE_STATE);
        intentFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    /**
     * 注销来电广播
     */
    private void unregisterreceiverphone() {
        if (mBroadcastReceiver != null) {

            try {

                unregisterReceiver(mBroadcastReceiver);

            } catch (IllegalArgumentException e) {

                e.printStackTrace();
            }

        }
    }

    //todo dianhua
    public final static String B_PHONE_STATE = TelephonyManager.ACTION_PHONE_STATE_CHANGED;

    BroadcastReceiverMgr mBroadcastReceiver;

    //内部类，用来监听来电广播
    class BroadcastReceiverMgr extends BroadcastReceiver {
        private final String TAG = MainActivity.TAG;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "[Broadcast]" + action);
            //呼入电话
            if (action.equals(MainActivity.B_PHONE_STATE)) {
                Log.i(TAG, "[Broadcast]PHONE_STATE");
                doReceivePhone(context, intent);
            }
        }

        /**
         * 处理电话广播.
         *
         * @param context
         * @param intent
         */
        public void doReceivePhone(Context context, Intent intent) {
            String phoneNumber = intent.getStringExtra(
                    TelephonyManager.EXTRA_INCOMING_NUMBER);
            TelephonyManager telephony =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int state = telephony.getCallState();
            boolean isNeedRemind = false;
            String phoneR = LocalDataSaveTool.getInstance(MainActivity.this.getApplicationContext()).readSp(MyConfingInfo.PHONE_REMIND_CHANGE);
            if (phoneR.equals(MyConfingInfo.REMIND_OPEN))        // 判断缓存中的提醒是否打开
            {
                isNeedRemind = true;
            }
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i(TAG, "[Broadcast]等待接电话=" + phoneNumber);
                    if (isNeedRemind)    // 提醒是打开的
                    {
                        final boolean[] hasRemind = {false};
                        // 先打开手环的来电提醒功能
                        final BleForPhoneAndSmsRemind bfp = BleForPhoneAndSmsRemind.getInstance();
//							 读取联系人
                        final String name = BreakPhoneUtils.getContactNameFromPhoneBook(MainActivity.this.getApplicationContext(), phoneNumber);
//							 发送数据
                        bfp.beginPhoneRemind(phoneNumber, name, BleForPhoneAndSmsRemind.startPhoneRemindToDevice);
                    }

                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.i(TAG, "[Broadcast]电话挂断=" + phoneNumber);
                    BleForPhoneAndSmsRemind.getInstance().closeTheRemind();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i(TAG, "[Broadcast]通话中=" + phoneNumber);
                    BleForPhoneAndSmsRemind.getInstance().closeTheRemind();
                    break;
            }
        }
    }

    /**
     * 监听来电
     */
    private void registCallingListener() {
        // 电话管理类
        TelephonyManager phoneManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        // 电话状态监听类
        PhoneStateListener listenerM = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, final String incomingNumber) {
//				String inNumber = incomingNumber;
//				inNumber = "+4912345689";
                boolean isNeedRemind = false;
                String phoneR = LocalDataSaveTool.getInstance(MainActivity.this.getApplicationContext()).readSp(MyConfingInfo.PHONE_REMIND_CHANGE);
                if (phoneR.equals(MyConfingInfo.REMIND_OPEN))        // 判断缓存中的提醒是否打开
                {
                    isNeedRemind = true;
                }
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:// 空闲状态
                        BleForPhoneAndSmsRemind.getInstance().closeTheRemind();
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:// 来电,正在响铃
//						Log.i(TAG, "来电中。。。。");
                        if (isNeedRemind)    // 提醒是打开的
                        {
                            final boolean[] hasRemind = {false};
                            // 先打开手环的来电提醒功能
                            final BleForPhoneAndSmsRemind bfp = BleForPhoneAndSmsRemind.getInstance();
//							bfp.openPhoneRemine((byte) 0x03);
//							try {
//								Thread.sleep(100);
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//							 读取联系人
                            final String name = BreakPhoneUtils.getContactNameFromPhoneBook(MainActivity.this.getApplicationContext(), incomingNumber);
//							 发送数据
                            bfp.beginPhoneRemind(incomingNumber, name, BleForPhoneAndSmsRemind.startPhoneRemindToDevice);
//							bfp.addCallback(new BleForPhoneAndSmsRemind.onPhoneHasRecever() {
//								@Override
//								public void onReceverLisener() {
//									hasRemind[0] = true;
//								}
//							});
//							mHandler.postDelayed(new Runnable() {
//								@Override
//								public void run() {
//									if (!hasRemind[0]) {
//										bfp.startPhoneRemind(incomingNumber, name, BleForPhoneAndSmsRemind.startPhoneRemindToDevice);
////										Message msg = Message.obtain(mHandler);
////										msg.what = MSG_REMIND_PHONE;
////										Bundle bundle = new Bundle();
////										bundle.putString("phone_number", incomingNumber);
////										bundle.putString("phone_name", name);
////										msg.setData(bundle);
//////										mHandler.sendMessageDelayed(msg, 300);
////										mHandler.sendMessage(msg);
//									}
//								}
//							}, 300);
                        }
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:// 接起电话
                        BleForPhoneAndSmsRemind.getInstance().closeTheRemind();
                        break;
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        phoneManager.listen(listenerM, PhoneStateListener.LISTEN_CALL_STATE);
    }


    public void endPhoneCall() {
        try {
            Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
            try {
                IBinder iBinder = (IBinder) method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
                ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
                iTelephony.endCall();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean isFirstOpen = true;    // 首次打开app


    @Override
    protected void onResume() {
        super.onResume();
        getPhoneLangue();
        Log.i(TAG, "MainActivity---onResume");
        setPhoneListener();
        Bitmap mBitmap = SaveImageUtil.readImageFormSD(SDPathUtils.getSdcardPath() + MyConfingInfo.IMAGEVIEW_SAVE_PATH);
        if (mBitmap != null) {
//			persionInfo.setImageBitmap(mBitmap);
            mResideMenuInfo.setIcon(mBitmap);
        }
        setFootButton();
        if (isFirstOpen) {
            openBle();
            isFirstOpen = false;
        }
        Log.i(TAG, "<< BLE Service onDestory >>绑定BluetoothLeService?" + BluetoothLeService.getInstance());
        // 绑定service
        //	reBindBluetoothLeService();
        if (BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice()) {
            mResideMenuInfo.setConnectState(getString(R.string.already_con));
        } else {
            mResideMenuInfo.setBattery("X");
            mResideMenuInfo.setConnectState(getString(R.string.disconnected));
        }
        showNickName();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // getCurrentData();
            }
        }, 600);
        //给手环发送身高年龄资料
        if (BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice()) {
            setParamsToDevice();
        }
        String strsipport=LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.SUPPORT_BLOOD);
        Log.i("cececece",strsipport);
//        if(strsipport.equals("1")){
//            RadioButton rb = (RadioButton) findViewById(R.id.blood_button);
//            if (rb.getVisibility() == View.GONE) {
//                rb.setVisibility(View.VISIBLE);
//                initFragment011();
//
//            }
//        }else{
//            RadioButton rb = (RadioButton) findViewById(R.id.blood_button);
//            if (rb.getVisibility() == View.VISIBLE) {
//                rb.setVisibility(View.INVISIBLE);
//                initFragment011();
//            }
//        }

    }

    private void setPhoneListener() {
        BleDataForPhoneComm.getInstance().setDeviceCommListener(new DataSendCallback() {
            @Override
            public void sendSuccess(byte[] receveData) {
                int flag = receveData[0];
                if (flag == 1) {

                } else if (flag == 2) {
                    BleDataForPhoneComm.getInstance().respondFlag2();
                    BreakPhoneUtils.doBreakPhone();

                } else if (flag == 3) {

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

    //当Activity的当前Window获得或失去焦点时会被回调此方法
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            //如果焦点获得，进行操作
            reBindBluetoothLeService();
        }
    }

    /**
     * 从下位机获取全天数据等
     */
    public final int MSG_GET_SET_LANGUE = 13;//设置语言

    public void getothercurrenrdata() {
        Message msg = mHandler.obtainMessage();
        msg.what = MSG_GET_DAY_DATA;
        msg.arg1 = 1;
        mHandler.sendMessageDelayed(msg, 0);
    }

    public void getCurrentData() {
        Message msg = mHandler.obtainMessage();
        msg.what = MSG_GET_DAY_DATA;
        msg.arg1 = 1;
        mHandler.sendMessageDelayed(msg, 0);
        Message mg = mHandler.obtainMessage();
        mg.what = MSG_GET_SET_LANGUE;
        mg.arg1 = 1;
        mHandler.sendMessageDelayed(mg, 200);
        Message mzg = mHandler.obtainMessage();
        mzg.what = MSG_GET_DEVICE_BATTERU;
        mzg.arg1 = 1;
        mHandler.sendMessageDelayed(mzg, 0);
//        Message msgH = mHandler.obtainMessage();
//        msgH.what = MST_GET_ALL_HEARTREAT_DATA;
//        msgH.arg1 = 1;
//        mHandler.sendMessage(msgH);
//        Message msgE = mHandler.obtainMessage();
//        msgE.what = MSG_GET_EACH_HOUR_DATA;
//        msgE.arg1 = 1;
//        mHandler.sendMessage(msgE);
    }

    private void reBindBluetoothLeService() {
        Log.i(TAG, "绑定BluetoothLeService1");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                connectService();
            }
        }, 600);
    }


    /**
     * 设置注销/登录字样
     */
    private void setFootButton() {
        String userA = LocalDataSaveTool.getInstance(MainActivity.this.getApplicationContext()).readSp(MyConfingInfo.USER_ACCOUNT);
        if (userA != null && userA.isEmpty()) {
            loginButton.setText(R.string.login);
        } else {
            loginButton.setText(R.string.login_out_login_in);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "MainActivity---onPause");

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "MainActivity---onStart");
        setLocationConfigue(0, getTodayDate(), 1, BleDataForWeather.toDevice);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "MainActivity---onStop");
        location.unregisterListener();
        location.stopGps();
    }

    //todo des
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegistBroatcast();
        unregisterreceiverphone();
        removeCallback();
        unbindAllService();
        unbindUpdataService();
        Log.i(TAG, "MainActivity---onDestroy");
        mHandler.removeMessages(MSG_GET_DAY_DATA);
        mHandler.removeMessages(MSG_GET_DEVICE_BATTERU);
        mHandler.removeMessages(MSG_GET_SET_LANGUE);
    }

    private void removeCallback() {
        if (BluetoothLeService.getInstance() != null) {
            BluetoothLeService.getInstance().removeAllCallback();
        }
        if (activFragment != null) {
            activFragment = null;
        }
    }

    /**
     * 解绑service
     */
    private void unbindAllService() {
        if (haveBinded) {
            Log.i(TAG, "绑定BluetoothLeService尝试解绑");
            unbindService(mBleServiceConnected);
        }
//		stopService(new Intent(MainActivity.this, NotifyService.class));
    }

    //------------------------------------------------------------------------------------

    /**
     * 打开蓝牙
     */
    private void openBle() {
        if (BluetoothAdapter.getDefaultAdapter() != null && !BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            //Boolean isOpenBle = BluetoothAdapter.getDefaultAdapter().enable();
            //Log.i("", "蓝牙是否打开" + isOpenBle);
            // showToast(R.string.make_sure, 1);
        }
    }


    @Override
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        super.overridePendingTransition(enterAnim, exitAnim);
    }

    //接收蓝牙适配器状态改变的系统广播
    private BroadcastReceiver mBluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "接收到的action:" + action);
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                dealBleState(intent);
            } else if (MyConfingInfo.NOTIFY_MAINACTIVITY_TO_UPDATE_OUTLINE_DATA.equals(action)) {
                if (movementFragment != null && movementFragment.isResumed())
                    movementFragment.updateOutlineData(currenDate);
            } else if (action.equals(MyConfingInfo.BROADCAST_FOR_UPDATA_HARDWARE)) {
                if (BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice()) {
                    BluetoothLeService.getInstance().setHrNotify(null, false);
                }
            } else if (action.equals(MyConfingInfo.CLOSE_THE_REQUEST_FORM_DEVICE)) {
                closeTheRequest = intent.getBooleanExtra("ok_or_no", false);
                Log.i(TAG, "是否关闭获取电量和天数据:" + closeTheRequest);
            } else if (action.equals(MyConfingInfo.UPDATE_THE_MAIN_OUTLINE_TYPE)) {
//				readDbAndshow(getTodayDate());
            } else if (intent.getAction().equals(MyConfingInfo.CLOSE_OTHER_PAGER)) {
                MainActivity.this.finish();
            } else if (action.equals(MyConfingInfo.CLOSE_APP)) {
                MainActivity.this.finish();
                System.exit(0);
            } else if (action.equals(DeviceConfig.DEVICE_CONNECTE_AND_NOTIFY_SUCESSFUL)) {
                showConnectState(2);
                mHandler.removeCallbacks(run);
                closeDialog();
                countDeviceVersion = 0;
                initTimeAndVersionAndBattery();
                mResideMenuInfo.setConnectState(getString(R.string.already_con));
//				Log.i(TAG, "devce info:" + device.hashCode());
//				tvConState.setText(R.string.already_con);
                // 发送广播，设备已链接并使能
            } else if (action.equals(DeviceConfig.DEVICE_CONNECTING_AUTO)) {
                if (BluetoothAdapter.getDefaultAdapter() != null
                        && BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                    myData = new ArrayList<>();
                    String dds = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(DeviceConfig.DEVICE_ADDRESS);
                    if (dds != null && !dds.equals("")) {
                        startScan();
                        String text = conectState.getText().toString().trim();
                        //如果提示是连接失败，或者未找到设备
                        if (text.equals(getString(R.string.confail)) || text.equals(getString(R.string.unfinddevice))) {

                        } else {
                            showConnectState(1);
                        }

                        if (showAlter) {
                            mHandler.postDelayed(run, 40000);
                        }
                    } else {
                        showConnectState(6);
                        mHandler.removeCallbacks(run);
                    }
                }


            } else if (action.equals(MyConfingInfo.CHAGE_DEVICE_TYPE)) {
                Log.i(TAG, "接收到切换设备广播:");
//				initFourPagerFragment();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkPagerShow();
                        initFragment011();
                    }
                });
            }
        }
    };

    /**
     * 搜索设备
     */
    private void startScan() {
        MyApplication.threadService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    BleScanUtils.getBleScanUtilsInstance(getApplicationContext()).setmOnDeviceScanFoundListener(myDeviceListnenr);
                    BleScanUtils.getBleScanUtilsInstance(getApplicationContext()).scanDevice(null);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("111111", "");
                }

            }
        });
    }

    /**
     * 实例化设备监听器，并对扫描到的设备进行监听
     */

    private BleScanUtils.OnDeviceScanFoundListener myDeviceListnenr = new BleScanUtils.OnDeviceScanFoundListener() {
        @Override
        public void OnDeviceFound(final LocalDeviceEntity mLocalDeviceEntity) {
            boolean isEquals = false;
//            Log.i(TAG, "扫描到的设备" + mLocalDeviceEntity.getName() + "  " + Thread.currentThread().getName());
            String deviceType = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.USER_DEVICE_TYPE);
            if (deviceType.equals(MyConfingInfo.DEVICE_HR)) {
                String deviceName = mLocalDeviceEntity.getName();
                if (deviceName != null && deviceName.contains("_")) {
                    String[] d = deviceName.split("_");
                    if (!d[0].equals("B7")) {
                        if (!myData.contains(mLocalDeviceEntity)) {
                            myData.add(mLocalDeviceEntity);
                        }
                    }
                } else {
                    if (!myData.contains(mLocalDeviceEntity)) {
                        myData.add(mLocalDeviceEntity);
                    }
                }
            } else if (deviceType.equals(MyConfingInfo.DEVICE_BLOOD)) {
                String deviceName = mLocalDeviceEntity.getName();
                if (deviceName.contains("_")) {
                    String[] d = deviceName.split("_");
                    if (d[0].equals("B7")) {
                        if (!myData.contains(mLocalDeviceEntity)) {
                            myData.add(mLocalDeviceEntity);
                        }
                    }
                } else {
                    return;
                }
            }
            Collections.sort(myData, new Comparator<LocalDeviceEntity>() {
                @Override
                public int compare(LocalDeviceEntity lhs, LocalDeviceEntity rhs) {
                    return new Integer(rhs.getRssi()).compareTo(lhs.getRssi());
                }
            });

        }

        @Override
        public void onScanStateChange(boolean isChange) {
            //搜索结束
            //   changeTheScanLayout(isChange);
        }
    };

    //处理蓝牙开关业务
    private void dealBleState(Intent intent) {
        int btState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
        Log.i(TAG, "btState:" + btState);
        if (btState == BluetoothAdapter.STATE_ON)        // 手机蓝牙模块处于打开状态
        {
            onBluetoothModeOpen();
        } else if (btState == BluetoothAdapter.STATE_OFF)    // 模块处于关闭状态
        {
            onBluetoothModeClose();
        } else if (btState == BluetoothAdapter.STATE_TURNING_ON) {
        } else if (btState == BluetoothAdapter.STATE_TURNING_OFF) {
        }
    }

    Runnable run = new Runnable() {
        @Override
        public void run() {
            boolean bol = false;
            String deviceaddress = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(DeviceConfig.DEVICE_ADDRESS);
            for (LocalDeviceEntity deviceentity : myData) {
                if (deviceentity.getAddress().equals(deviceaddress)) {
                    bol = true;
                    break;

                }
            }
            //todo lanya
            if (BluetoothAdapter.getDefaultAdapter() != null
                    && BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                if (bol) {
                    showConnectState(4);
                    mHandler.removeCallbacks(run);


                } else {
                    mHandler.removeCallbacks(run);
                    showConnectState(3);

                }
            } else {

            }
        }
    };


    private void initFragment011() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (activFragment == null) {
            activFragment = new active_Fragment();
            fragmentTransaction.add(R.id.main_pager, activFragment, FRAGMENT_ACTIVE);
        }
        hideOther(fragmentTransaction);
        fragmentTransaction.show(activFragment);
        fragmentTransaction.commitAllowingStateLoss();
        activFragment.updateThisScreen(currenDate);
    }

    /**
     * 遍历cursor取出数据
     *
     * @param mCursor startTime, endTime, calorie, stepTotle
     */
    private void ergodicCursor(Cursor mCursor) {
        if (mCursor.moveToFirst()) {
//			dataList.clear();// 先清空数据
            do {
                String startTime = mCursor.getString(mCursor.getColumnIndex("startTime"));
                String endTime = mCursor.getString(mCursor.getColumnIndex("endTime"));
                int calorie = mCursor.getInt(mCursor.getColumnIndex("calorie"));
                int stepTotal = mCursor.getInt(mCursor.getColumnIndex("stepTotle"));
                String time = startTime + "-" + endTime;
            } while (mCursor.moveToNext());
            mCursor.close();
        }
        Log.i(TAG, "更新离线数据界面");
//		itemAdapter.notifyDataSetChanged();
    }


    /**
     * 注册广播
     */
    private void registBroatcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
//		filter.addAction(MyConfingInfo.NOTIFY_MAINACTIVITY_TO_UPDATE_DAY_DATA);
        filter.addAction(MyConfingInfo.NOTIFY_MAINACTIVITY_TO_UPDATE_OUTLINE_DATA);
//		filter.addAction(MyConfingInfo.BROADCAST_CHAGE_UNIT);
        filter.addAction(MyConfingInfo.BROADCAST_FOR_UPDATA_HARDWARE);
        filter.addAction(MyConfingInfo.CLOSE_THE_REQUEST_FORM_DEVICE);
        filter.addAction(MyConfingInfo.UPDATE_THE_MAIN_OUTLINE_TYPE);
        filter.addAction(MyConfingInfo.CLOSE_OTHER_PAGER);
//		filter.addAction(MyConfingInfo.DELETE_ALL_DATABASE);
        filter.addAction(MyConfingInfo.CLOSE_APP);
        filter.addAction(DeviceConfig.DEVICE_CONNECTE_AND_NOTIFY_SUCESSFUL);
        filter.addAction(DeviceConfig.DEVICE_CONNECTING_AUTO);
        filter.addAction(MyConfingInfo.CHAGE_DEVICE_TYPE);
        registerReceiver(mBluetoothStateReceiver, filter);
    }
    /*
     *注销广播
	 */

    private void unRegistBroatcast() {
        unregisterReceiver(mBluetoothStateReceiver);
    }

    // 手机蓝牙打开
    private void onBluetoothModeOpen() {
        // 连接绑定服务对象
        connectService();
    }

    // 手机蓝牙关闭onBluetoothClose
    private void onBluetoothModeClose() {
//		tvBattery.setText("X");
//		tvConState.setText(R.string.disconnected);
        mResideMenuInfo.setBattery("X");
        mResideMenuInfo.setConnectState(getString(R.string.disconnected));
        LocalDataSaveTool.getInstance(MainActivity.this.getApplicationContext()).writeSp(MyConfingInfo.HARD_VERSION, "");
        // MyToastUitls.showToast(MainActivity.this.getApplicationContext(), R.string.bluetooth_has_stop, 2);
        // showConnectState(5);
        //BluetoothAdapter.getDefaultAdapter().enable();
    }

    /**
     * 打开蓝牙并绑定Service
     */
//	private BleServiceConnected mBleServiceConnected;	// 绑定服务回调借口实现类
    private boolean haveBinded = false;                            // 标识绑定状态

    private void connectService() {
        if (BluetoothAdapter.getDefaultAdapter() != null
                && BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            String deviceName = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(DeviceConfig.DEVICE_NAME);
            if (deviceName.equals("")) {
                showConnectState(6);
                //mHandler.removeCallbacks(run);
                //bolstate = true;
            }
            if (!haveBinded && mBleServiceConnected != null) {
//				mBleServiceConnected = new BleServiceConnected();
                Intent intent = new Intent(MainActivity.this.getApplicationContext(), BluetoothLeService.class);
                if (BluetoothLeService.getInstance() == null) {
                    startService(intent);
                    Log.i(TAG, "BluetoothLeService已启动");
                }
                haveBinded = MainActivity.this.bindService(intent, mBleServiceConnected, Context.BIND_AUTO_CREATE);
                Log.i(TAG, "BluetoothLeService已绑定" + haveBinded);
            } else {
                Log.i(TAG, "BluetoothLeService已启动并绑定,无需重复绑定");
            }
        } else {
            String deviceName = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(DeviceConfig.DEVICE_NAME);
            if (deviceName.equals("")) {
                showConnectState(6);
            } else {
                //先判断设备是否已经打开
                showConnectState(5);
            }

        }
    }

    private void showList(BluetoothGatt gatt) {
        List<BluetoothGattService> slist = gatt.getServices();
        for (BluetoothGattService service : slist) {
            UUID uuid = service.getUuid();
            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic : characteristics) {
                UUID chUU = characteristic.getUuid();
                Log.i(TAG, "当前设备的服务：" + uuid + "服务特征值：" + chUU);
            }
        }
    }

    private void initTimeAndVersionAndBattery() {
        mHandler.sendEmptyMessageDelayed(MSG_SYNC_TIME, 0);
    }

    private void initOtherData() {
        Message msg = mHandler.obtainMessage();
        msg.what = MSG_GET_DAY_DATA;
        msg.arg1 = 0;
//		Log.i(TAG, "第=============第一次获取全天数据");
        mHandler.sendMessageDelayed(msg, 0);
    }

    /**
     * 获取睡眠数据
     */
//	private class GetSleepDataTask extends AsyncTask<Void, Void, Boolean>
//	{
//		@Override
//		protected Boolean doInBackground(Void... params)
//		{
//
//			return null;
//		}
//	}


    private class GetEachHourTask extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... params) {
            int a = params[0];
            final int watchs = params[1];
            switch (a) {
                case 1:
                    BleDataForEachHourData.getEachHourDataInstance().setOnBleDataReceListener(new DataSendCallback() {
                        @Override
                        public void sendSuccess(byte[] receveData) {
                            new BleEachDataDealer(MainActivity.this, receveData);
                        }

                        @Override
                        public void sendFailed() {
                        }

                        @Override
                        public void sendFinished() {
                            if (watchs == 0) {
                                mHandler.sendEmptyMessageDelayed(MSG_GET_SLEEP_DATA, 0);
                            }
                        }
                    });
                    BleDataForEachHourData.getEachHourDataInstance().getEachData();
                    break;
                case 2:
                    BleDataForDayHeartReatData
                            .getHRDataInstance(MainActivity.this.getApplicationContext())
                            .setOnHrDataRecever(new DataSendCallback() {
                                @Override
                                public void sendSuccess(byte[] receveData) {
                                    new HRDataDealer(receveData, MainActivity.this.getApplicationContext());
                                }

                                @Override
                                public void sendFailed() {
                                }

                                @Override
                                public void sendFinished() {
                                    if (watchs == 0) {
                                        Message msg = mHandler.obtainMessage();
                                        msg.what = MSG_GET_EACH_HOUR_DATA;
                                        msg.arg1 = 0;
                                        mHandler.sendMessageDelayed(msg, 0);
                                    }
                                }
                            });
                    BleDataForDayHeartReatData
                            .getHRDataInstance(MainActivity.this.getApplicationContext())
                            .requestHeartReatDataAll();
                    break;
                case 3:
                    break;
                case 4:
                    BleDataForSleepData.getInstance(MainActivity.this.getApplicationContext())
                            .setOnSleepDataRecever(new com.huichenghe.bleControl.Ble.DataSendCallback() {
                                @Override
                                public void sendSuccess(byte[] receveData) {
                                    new BleSleepDataDeal(receveData, MainActivity.this);
                                }

                                @Override
                                public void sendFailed() {
                                }

                                @Override
                                public void sendFinished() {
                                    setParamsToDevice();
                                }
                            });
                    BleDataForSleepData.getInstance(MainActivity.this.getApplicationContext()).getSleepingData();
                    break;
            }
            return true;
        }
    }

    private void setParamsToDevice() {
        String hei = getPersionInfo(MyConfingInfo.USER_HEIGHT);     // 身高
        String wei = getPersionInfo(MyConfingInfo.USER_WEIGHT);     // 体重
        String gen = String.valueOf((Integer.parseInt(getPersionInfo(MyConfingInfo.USER_GENDER)) - 1)); // 性别
        String bir = getPersionInfo(MyConfingInfo.USER_BIRTHDAY);
        new BleDataForSettingParams(MainActivity.this.getApplicationContext())
                .settingTheStepParamsToBracelet(hei, wei, gen, bir);
        String deviceType = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.USER_DEVICE_TYPE);
        String deviceSupport = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.CHECK_SUPPORT_WEATHER_INTERFACE);
        Log.i(TAG, "定位条件：" + deviceType);
        if (deviceType != null && deviceType.equals(MyConfingInfo.DEVICE_BLOOD)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "定位开始");
                    location.startGps();
                }
            });
            // 定位
        }
    }

    /**
     * 读取基本信息帮助方法
     *
     * @param key
     * @return
     */
    private String getPersionInfo(String key) {
        return LocalDataSaveTool.getInstance(MainActivity.this.getApplicationContext()).readSp(key);
    }


    private boolean mBLENotifyOpened = false;

    private void setBLENotify(BluetoothGatt gatt, boolean isOpen, boolean force) {
        Log.e(TAG, "setBLENotify  -bdoChecked; isOpen= " + isOpen);
        Log.e(TAG, "setBLENotify  -bdoChecked; mBLENotifyOpened= " + mBLENotifyOpened);
        if (mBLENotifyOpened == isOpen && !force) {
            return;
        }
        if (gatt == null) {
            BluetoothLeService serviceMain = BluetoothLeService.getInstance();
            if (serviceMain == null) {
                Log.e(TAG, "writeDelayValue  e1");
                return;
            }
            gatt = serviceMain.getBluetoothGatt();
            if (gatt == null) {
                Log.e(TAG, "writeDelayValue  e2");
                return;
            }
        }
        Log.e(TAG, "setBLENotify  -bdoChecked; BluetoothAdapter.getDefaultAdapter().isEnabled()= "
                + BluetoothAdapter.getDefaultAdapter().isEnabled());
        mBLENotifyOpened = isOpen;

        if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            BluetoothGattService main = gatt
                    .getService(DeviceConfig.MAIN_SERVICE_UUID);

            Log.e(TAG, "onBLEServiceFound doChecked main=" + main);
            if (main != null) {
                try {
                    BluetoothGattCharacteristic characteristic = main
                            .getCharacteristic(DeviceConfig.UUID_CHARACTERISTIC_NOTIFY);
                    BluetoothLeService.printCharacteristicProperty(characteristic);
                    boolean isNotifyOk = BluetoothLeService.getInstance().setCharacteristicNotification(characteristic, true);
                    Log.i(TAG, "使能uuid:" + characteristic.getUuid().toString()
                            + "使能状态：" + isNotifyOk);
//					List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
//					for (BluetoothGattDescriptor descriptor : descriptors)
//					{
//						Log.i(TAG, "特征值里的描述数据：" + FormatUtils.bytesToHexString(descriptor.getValue()));
//					}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            BluetoothGattService hrate = gatt
                    .getService(DeviceConfig.HEARTRATE_SERVICE_UUID);
            Log.e(TAG, "onBLEServiceFound doChecked hrate=" + hrate);
            if (null != hrate) {
                BluetoothGattCharacteristic characteristics =
                        hrate.getCharacteristic(DeviceConfig.HEARTRATE_FOR_TIRED_NOTIFY);
                if (characteristics != null) {
                    boolean isNotify = BluetoothLeService.getInstance().setCharacteristicNotification(characteristics, isOpen);
//						Log.e(TAG, "onBLEServiceFound characteristic=" + characteristics.getUuid().toString());
                    Log.i(TAG, "使能uuid:" + characteristics.getUuid().toString()
                            + "使能心率状态：" + isNotify);
                }
            }
        } else {
            Log.e(TAG, "have  found service, but bt have disabled  doChecked");
        }
    }

    private String showResult = "--";

    //todo 111
    class gattHelperListener implements BleGattHelperListener {
        @Override
        public void onDeviceStateChangeUI(LocalDeviceEntity device,
                                          BluetoothGatt gatt,
                                          final String uuid, final byte[] value) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "上传的心率数据:" + FormatUtils.bytesToHexString(value) + "--" + Thread.currentThread().getName());
                    int liveHR = value[1] & 0xff;
                    if (liveHR != 0 && currenDate.equals(getTodayDate())) {
                        showResult = String.valueOf(liveHR);
                    } else {
                        showResult = "--";
                    }
                    activFragment.updateHR(showResult);
                }
            });
        }

        @Override
        public void onDeviceConnectedChangeUI(final LocalDeviceEntity device,
                                              boolean showToast,
                                              final boolean fromServer) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mResideMenuInfo.setBattery("X");
                    mResideMenuInfo.setConnectState(getString(R.string.disconnected));
//					stepFragmentLikes.setHR("--");
                    LocalDataSaveTool.getInstance(MainActivity.this.getApplicationContext()).writeSp(MyConfingInfo.HARD_VERSION, "");
                    Intent intent = new Intent();
                    intent.setAction(MyConfingInfo.ON_DEVICE_STATE_CHANGE);
                    intent.putExtra("DEVICE_OBJ", device);
                    intent.putExtra(MyConfingInfo.DISCONNECT_STATE, fromServer);
                    sendBroadcast(intent);
                }
            });
        }
    }

    ;

    private void updateHR() {
        if (activFragment != null)
            activFragment.updateHR(showResult);
    }


    /**
     * 绑定Service的回调借口的实现类
     */
    private ServiceConnection mBleServiceConnected = new ServiceConnection() {
        @Override // 开启Service后调用此抽象方法
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (!BluetoothLeService.getInstance().initialize()) {
                showToast(R.string.ble_not_support, 1);
            } else {
                Log.i(TAG, "绑定BluetoothLeService--成功回调");
                // 回传接口实现类
                BluetoothLeService.getInstance().addCallback(
                        BleGattHelper.getInstance(getApplicationContext(), new gattHelperListener()));
                readDayDataAndBattery();
            }
        }

        @Override // 与service断开后调用此抽象方法
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "绑定BluetoothLeService_serviceDisconnecte");
        }
    };

    private void connectTheSaveDevice(boolean isConnectWhere) {
        Log.i(TAG, "自动重连");
        if (BluetoothLeService.getInstance() != null && !BluetoothLeService.getInstance().isConnectedDevice()) {
            String deviceName = LocalDataSaveTool.getInstance(MainActivity.this.getApplicationContext()).readSp(DeviceConfig.DEVICE_NAME);
            String deviceAddress = LocalDataSaveTool.getInstance(MainActivity.this.getApplicationContext()).readSp(DeviceConfig.DEVICE_ADDRESS);
            Log.i(TAG, "自动重连对象" + deviceAddress);
            if (deviceName != null && !deviceName.equals("") && deviceAddress != null && !deviceAddress.equals("")) {
                BluetoothLeService.getInstance().connect(new LocalDeviceEntity(deviceName, deviceAddress, -50, new byte[0]));
            }
        }
    }


//-------------------------------------------------------------------------------------

    private int touchState = 0;
    private float moveStartX, moveStartY;
    private float moveOffsetX = 0;


    private String compareTheDate(String date) {
        String resultFormat = "";
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String compareDate = format.format(calendar.getTime());
        if (date != null && !date.equals("") && date.equals(compareDate)) {
            String[] dates = date.split("-");
            calendar.set(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]) - 1, Integer.parseInt(dates[2]));
            SimpleDateFormat formates = new SimpleDateFormat("MM.dd", Locale.getDefault());
            String mAndD = formates.format(calendar.getTime());
            resultFormat = getResources().getString(R.string.today);
        } else {
            String[] dates = date.split("-");
//			Calendar calendars = Calendar.getInstance(TimeZone.getDefault());
            calendar.set(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]) - 1, Integer.parseInt(dates[2]));
            int week = calendar.get(Calendar.DAY_OF_WEEK);
            SimpleDateFormat formates = new SimpleDateFormat("MM.dd", Locale.CHINA);
            String mAndD = formates.format(calendar.getTime());
            String weekS = "";
            if (week == Calendar.SUNDAY) {
                weekS = getString(R.string.sunday_all);
            } else if (week == Calendar.MONDAY) {
                weekS = getString(R.string.tuesday_all);
            } else if (week == Calendar.TUESDAY) {
                weekS = getString(R.string.monday_all);
            } else if (week == Calendar.WEDNESDAY) {
                weekS = getString(R.string.wednesday_all);
            } else if (week == Calendar.THURSDAY) {
                weekS = getString(R.string.thursday_all);
            } else if (week == Calendar.FRIDAY) {
                weekS = getString(R.string.friday_all);
            } else if (week == Calendar.SATURDAY) {
                weekS = getString(R.string.saturday_all);
            }
            resultFormat = mAndD + "\t" + weekS;
        }
        return resultFormat;
    }

    private String getTodayDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(calendar.getTime());
    }

    /**
     * 上传到服务器上去
     */
    public void upLoadTodayAllData(String currenDay) {
        if (UpdateHistoryDataService.getInstance() != null && !UserAccountUtil.getAccount(getApplicationContext()).equals("")) {
            UpdateHistoryDataService.getInstance().upTodayData();
        }
    }

    public void setActivieContent() {
        todayActive.setText(compareTheDate(getTodayDate()));
        currenDate = getTodayDate();
        LoadCurrentDateData(currenDate);
    }

    RelativeLayout mainPager;
    TextView conectState;
    private int state = 0;
    private PreferencesService service;
    private int int_s;
    private int int_h;

    // 1 正在连接，2 连接成功， 3,请唤醒设备 4,连接失败请重试 5，蓝牙未打开 ，6未绑定 ，7数据同步
    //8数据同步成功，9数据同步失败，10，数据同步完成
    public void showConnectState(int comm) {
        state = comm;
        mainPager = (RelativeLayout) findViewById(R.id.main_pager);
        conectState.setTextColor(Color.BLACK);
        conectState.setOnClickListener(onclicklis);
        if (comm == 1) {
            if (conectState.getVisibility() == View.GONE) {
                conectState.setText(getString(R.string.connecting_now));
                conectState.setVisibility(View.VISIBLE);
                mainPager.setPadding(0, (int) CommonUtils.Dp2Px(getApplicationContext(), 48), 0, 0);
            } else {
                conectState.setText(getString(R.string.connecting_now));
            }
        } else if (comm == 2) {

            if (BluetoothAdapter.getDefaultAdapter() != null
                    && BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                conectState.setText(getString(R.string.device_connecte_successful));
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (conectState.getVisibility() == View.VISIBLE) {
                            conectState.setVisibility(View.GONE);
                            mainPager.setPadding(0, 0, 0, 0);
                            Message mg = mHandler.obtainMessage();
                            mg.what = MSG_GET_SET_LANGUE;
                            mg.arg1 = 1;
                            mHandler.sendMessageDelayed(mg, 200);
                        }
                    }
                }, 3000);
            } else {

            }
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


        } else if (comm == 3) {
            if (showAlter) {
                showAlter = false;
                if (!this.isDestroyed() || !this.isDestroyed())
                    if (conectState.getVisibility() == View.GONE) {
                        conectState.setText(getString(R.string.unfinddevice));
                        conectState.setVisibility(View.VISIBLE);
                        mainPager.setPadding(0, (int) CommonUtils.Dp2Px(getApplicationContext(), 48), 0, 0);

                    } else {
                        conectState.setText(getString(R.string.unfinddevice));
                    }
                //	showAlterDialog();
            }
        } else if (comm == 4) {
            if (showAlter) {
                showAlter = false;
                if (!this.isDestroyed() || !this.isDestroyed())
                    if (conectState.getVisibility() == View.GONE) {
                        conectState.setText(getString(R.string.confail));
                        conectState.setVisibility(View.VISIBLE);
                        mainPager.setPadding(0, (int) CommonUtils.Dp2Px(getApplicationContext(), 48), 0, 0);

                    } else {
                        conectState.setText(getString(R.string.confail));
                    }
                //	showAlterDialog();
            }

        } else if (comm == 5) {

            if (conectState.getVisibility() == View.GONE) {
                conectState.setText(getString(R.string.buleunopen));
                conectState.setVisibility(View.VISIBLE);
                mainPager.setPadding(0, (int) CommonUtils.Dp2Px(getApplicationContext(), 48), 0, 0);

            } else {
                conectState.setText(getString(R.string.buleunopen));
            }

        } else if (comm == 6) {
            conectState.setTextColor(getResources().getColor(R.color.colorPrimary));
            if (conectState.getVisibility() == View.GONE) {
                conectState.setText(getString(R.string.not_bind));
                conectState.setVisibility(View.VISIBLE);
                mainPager.setPadding(0, (int) CommonUtils.Dp2Px(getApplicationContext(), 48), 0, 0);

            } else {
                conectState.setText(getString(R.string.not_bind));
            }

        } else if (comm == 7) {
            if (conectState.getVisibility() == View.GONE) {
                conectState.setText(getString(R.string.datasynchronize));
                conectState.setVisibility(View.VISIBLE);
                mainPager.setPadding(0, (int) CommonUtils.Dp2Px(getApplicationContext(), 48), 0, 0);

            } else {
                conectState.setText(getString(R.string.datasynchronize));
            }
        } else if (comm == 8) {
            conectState.setText(getString(R.string.datasynchronizesuce));

        } else if (comm == 9) {
            conectState.setText(getString(R.string.datasynchronizefail));
        } else if (comm == 10) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (conectState.getVisibility() == View.VISIBLE) {
                        conectState.setVisibility(View.GONE);
                        mainPager.setPadding(0, 0, 0, 0);
                    }
                }
            }, 3000);
        } else {
            conectState.setText(getString(R.string.not_bind));
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (conectState.getVisibility() == View.VISIBLE) {
                        conectState.setVisibility(View.GONE);
                        mainPager.setPadding(0, 0, 0, 0);
                    }
                }
            }, 1500);
        }
    }

    private View.OnClickListener onclicklis = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //如果设备未绑定
            if (state == 6) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, DeviceAmdActivity.class);
                startActivity(intent);
            } else {

            }
        }
    };
    private boolean showAlter = true;
    private AlertDialog.Builder connectDialog;
    private AlertDialog alerDialog;

    private void showAlterDialog() {
        connectDialog = new AlertDialog.Builder(MainActivity.this);
        connectDialog.setCancelable(false);
        connectDialog.setTitle(R.string.note_connect_timeout);
        connectDialog.setMessage(R.string.connect_falied_please_open_blue);
        connectDialog.setPositiveButton(R.string.be_true, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
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

    private void init() {
//		showConnectState();
        conectState = (TextView) findViewById(R.id.connect_state_show);
        priviosDate = (ImageView) findViewById(R.id.day_data_privios);
        nextDate = (ImageView) findViewById(R.id.day_data_next);
        todayActive = (TextView) findViewById(R.id.today_acitve);
        mResideMenuInfo.setOnClickListener(NodoubleListener);
        mRessideTakePhoto.setOnClickListener(NodoubleListener);
        mResideDevice.setOnClickListener(NodoubleListener);
        mRessideRemind.setOnClickListener(NodoubleListener);
        mRessideAbout.setOnClickListener(NodoubleListener);
//		mRessideService.setOnClickListener(NodoubleListener);
//		mRessideFamlayAttion.setOnClickListener(NodoubleListener);
        exitButton.setOnClickListener(NodoubleListener);
        loginButton.setOnClickListener(NodoubleListener);
        priviosDate.setOnClickListener(NodoubleListener);
        nextDate.setOnClickListener(NodoubleListener);
        todayActive.setOnClickListener(NodoubleListener);
        currenDate = getTodayDate();
        String res = compareTheDate(currenDate);
        todayActive.setText(res);
        setnevgition();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitle("");            // 设置默认标题为空
        setSupportActionBar(toolbar);    // 设置导航条
        toolbar.setOnMenuItemClickListener(menuListener);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// 显示toolbar左侧按钮
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.three_line);      // 监听左侧按钮
        toolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
//				mMenu.toggle();
                mResidemenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });

        Bitmap mBitmap = SaveImageUtil.readImageFormSD(SDPathUtils.getSdcardPath() + MyConfingInfo.IMAGEVIEW_SAVE_PATH);
        if (mBitmap != null) {
//			persionInfo.setImageBitmap(mBitmap);
            mResideMenuInfo.setIcon(mBitmap);
        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setnevgition() {
        // 透明状态栏
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // 透明导航
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    public String getCurrentDate() {
        return currenDate;
    }

    private Toolbar.OnMenuItemClickListener menuListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId())    // 通过菜单id区分
            {
                case R.id.share_main:
                    ShotScreenForShare.getInstance().takeshotScreen(MainActivity.this);
//					startActivity(ShareActivity.class);
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(MainActivity.this.getApplicationContext(), ShareActivity.class);
                    startActivity(intent);
                    break;
                case R.id.trend_main:
//					startActivity(TrendActivity.class);
                    Intent intents = new Intent();
                    intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intents.setClass(MainActivity.this.getApplicationContext(), TrendActivity.class);
                    startActivity(intents);
                    break;
            }
            return true;
        }

//		@Override
//		public boolean onMenuItemClick(MenuItem item) {
//
//			return true;
//		}
    };

    private void showPriviosDate() {
        String bufferP = getPriviosDay(currenDate);
        if (bufferP != null && !bufferP.equals("")) {
            currenDate = bufferP;
            todayActive.setText(compareTheDate(currenDate));
            LoadCurrentDateData(currenDate);
        }
    }

    private void LoadCurrentDateData(String date) {
        if (activFragment != null && activFragment.isResumed()) {
            activFragment.setDate(currenDate);
            if (!activFragment.isHidden()) {
                activFragment.loadCount = 0;
                activFragment.updateThisScreen(date);
//				activFragment.getCurrentDateData(date);
            }
        }
        if (movementFragment != null && movementFragment.isResumed()) {
            movementFragment.setDate(currenDate);
            //if (!movementFragment.isHidden()) {
            movementFragment.loadCount = 0;
            movementFragment.updateOutlineData(date);
//				movementFragment.getRemoteOutlineData(date);
            //  }
        }
        if (sleepFragment != null && sleepFragment.isResumed()) {
            sleepFragment.setDate(currenDate);
            if (!sleepFragment.isHidden()) {
                sleepFragment.loadCount = 0;
                sleepFragment.loadSleepData(date);
//				sleepFragment.getSleepDataFromRemote(date);
            }
        }
        if (bloodFragment != null && bloodFragment.isResumed()) {
            bloodFragment.setDate(currenDate);
            if (!bloodFragment.isHidden()) {
                bloodFragment.LoadBloodData(date);
//				bloodFragment.getRemoteBloodData(date);
            }
        }
    }


    private String getPriviosDay(String currenDate) {
        if (currenDate.equals("1900-01-01")) {
            return null;
        } else {
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            Log.i(TAG, "当前日期格式：" + currenDate);
            String[] eaDate = currenDate.split("-");
            calendar.set(Integer.parseInt(eaDate[0]), Integer.parseInt(eaDate[1]) - 1, Integer.parseInt(eaDate[2]) - 1);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return format.format(calendar.getTime());
        }
    }

    private void showNextDate() {
        String buffDate = getNextDate(currenDate);
        if (buffDate != null && !buffDate.equals("")) {
            currenDate = buffDate;
            todayActive.setText(compareTheDate(currenDate));
            LoadCurrentDateData(currenDate);
        }

    }

    private String getNextDate(String currenDate) {
        String da = getTodayDate();
        if (da.equals(currenDate)) {
            return null;
        } else {
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            Log.i(TAG, "当前日期格式：" + currenDate);
            String[] eaDate = currenDate.split("-");
            calendar.set(Integer.parseInt(eaDate[0]), Integer.parseInt(eaDate[1]) - 1, Integer.parseInt(eaDate[2]) + 1);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return format.format(calendar.getTime());
        }
    }


    View.OnClickListener NodoubleListener = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            if (v == mResideDevice) {
//				startActivity(DeviceAmdActivity.class);
//				launchActivity(mResideDevice, DeviceAmdActivity.class);
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(MainActivity.this.getApplicationContext(), DeviceAmdActivity.class);
                startActivity(intent);
            } else if (v == mRessideFamlayAttion) {
                if (checkLogin()) {
//					launchActivity(mRessideFamlayAttion, LovingCareActivity.class);
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(MainActivity.this.getApplicationContext(), LovingCareActivity.class);
                    startActivity(intent);
                } else {
                    MyToastUitls.showToast(MainActivity.this.getApplicationContext(), R.string.not_login, 1);
                }
            } else if (v == mRessideTakePhoto) {
                if (BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice()) {
//					launchActivity(mRessideTakePhoto, TakePhotoActivity.class);
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(MainActivity.this.getApplicationContext(), TakePhotoActivity.class);
                    startActivity(intent);
                } else {
                    //todo tixing
                    String deviceName = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(DeviceConfig.DEVICE_NAME);
                    if (deviceName.equals("")) {
                        MyToastUitls.showToast(MainActivity.this.getApplicationContext(), R.string.not_bind, 2);
                    } else
                        MyToastUitls.showToast(MainActivity.this.getApplicationContext(), R.string.not_connecte, 2);
                }
            } else if (v == mRessideRemind) {
                if (BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice()) {
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(MainActivity.this.getApplicationContext(), SettingActivity.class);
                    startActivity(intent);
                } else {
                    String deviceName = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(DeviceConfig.DEVICE_NAME);
                    if (deviceName.equals("")) {
                        MyToastUitls.showToast(MainActivity.this.getApplicationContext(), R.string.not_bind, 2);
                    } else
                        MyToastUitls.showToast(MainActivity.this.getApplicationContext(), R.string.not_connecte, 2);
                }
            } else if (v == mRessideAbout) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(MainActivity.this.getApplicationContext(), AboutActivity.class);
                startActivity(intent);
            } else if (v == mRessideService) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(MainActivity.this.getApplicationContext(), HelpActivity.class);
                startActivity(intent);
            } else if (v == mResideMenuInfo) {
                Intent mIntent = new Intent();
                mIntent.setClass(MainActivity.this.getApplicationContext(), EditPersionInfoActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mIntent.putExtra(MyConfingInfo.WHERE_ARE_YOU_FORM, MyConfingInfo.FORM_VIEW_DATA);
                mIntent.putExtra(MyConfingInfo.HASLOGIN, isLoginTrue);
                launchEditPersionInfoActivity(mResideMenuInfo.getHeadPottrin(), mIntent);
            } else if (v == exitButton) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.exit));
                builder.setCancelable(false);
                builder.setMessage(getString(R.string.entrue_to_eixt));
                builder.setPositiveButton(getString(R.string.be_true), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });
                builder.setNegativeButton(getString(R.string.be_cancle), null);
                builder.create().show();
            } else if (v == loginButton) {
                String account = LocalDataSaveTool.getInstance(MainActivity.this.getApplicationContext()).readSp(MyConfingInfo.USER_ACCOUNT);
                if (account != null && !account.isEmpty()) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setTitle(R.string.login_out_login_in);
                    builder1.setMessage(R.string.are_you_true);
                    builder1.setCancelable(false);
                    builder1.setPositiveButton(R.string.be_true, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.USER_DEVICE_TYPE, "");
                            LogOffAndOther();
                        }
                    });
                    builder1.setNegativeButton(R.string.be_cancle, null);
                    builder1.create().show();
                } else {
//					launchActivitys(loginButton, LoginActivity.class);
                    Intent intent1 = new Intent();
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent1.setClass(MainActivity.this.getApplicationContext(), nextToLoginActivity.class);
                    intent1.putExtra(MyConfingInfo.HOT_START, MyConfingInfo.HOT_START_FROM_MAIN);
                    startActivity(intent1);
                }
            } else if (v == priviosDate) {
                showPriviosDate();
            } else if (v == nextDate) {
                showNextDate();
            } else if (v == todayActive) {
                new CustomDateSelector(MainActivity.this, currenDate, new CustomDateSelector.OnDateChoose() {
                    @Override
                    public void choose(String dates) {
                        currenDate = dates;
                        todayActive.setText(compareTheDate(currenDate));
                        LoadCurrentDateData(currenDate);
                    }
                });
            }
        }
    };

    private boolean checkLogin() {
        String cou = UserAccountUtil.getAccount(MainActivity.this.getApplicationContext());
        if (cou != null && cou.equals("")) {
            return false;
        } else {
            return true;
        }
    }
    /**
     private void showTheDateSelect()
     {
     View v = getXmlLayout();
     popWindowForHistoryData = new PopupWindow(v, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
     popWindowForHistoryData.setAnimationStyle(R.style.mypopupwindow_anim_style);
     popWindowForHistoryData.setFocusable(true);
     popWindowForHistoryData.setBackgroundDrawable(new BitmapDrawable());
     popWindowForHistoryData.showAtLocation(v, Gravity.CENTER, 0, getWindowManager().getDefaultDisplay().getHeight());
     popWindowForHistoryData.setOnDismissListener(new PopupWindow.OnDismissListener() {
    @Override public void onDismiss()
    {
    popWindowForHistoryData = null;
    }
    });
     RelativeLayout relat = (RelativeLayout)v.findViewById(R.id.other_layout);
     relat.setOnClickListener(new View.OnClickListener()
     {
     @Override public void onClick(View v)
     {
     popWindowForHistoryData.dismiss();
     }
     });
     }
     private View getXmlLayout()
     {
     View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.show_histroy_data_layout, null);
     RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.show_the_data_in_popwindow);
     recyclerView.setHasFixedSize(true);
     recyclerView.setItemAnimator(new DefaultItemAnimator());
     recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
     recyclerView.setAdapter(new DateListAdapter(MainActivity.this, hasDataDate, new DateListAdapter.OnItemClickListener()
     {
     @Override public void ItemClick(int position)
     {
     // 用户点击某一天
     currenDate = hasDataDate.get(position);
     updateThisScreen(currenDate);	// 更新全天数据
     readDbAndshow(currenDate);
     if(popWindowForHistoryData.isShowing())
     {
     popWindowForHistoryData.dismiss();
     }

     todayActive.setText(compareTheDate(currenDate));
     }
     }));

     return view;
     }
     **/
    /**
     * 显示日期选择器
     *
     * @param dataPick
     */
    private void showPopwindow(View dataPick) {
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        // 构建悬浮窗
        dateSelector = new PopupWindow(dataPick, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dateSelector.setOutsideTouchable(false);
        dateSelector.setAnimationStyle(R.style.mypopupwindow_anim_style);
        // 设置焦点
        dateSelector.setFocusable(true);
        dateSelector.setBackgroundDrawable(new BitmapDrawable());
        dateSelector.showAtLocation(dataPick, Gravity.CENTER_HORIZONTAL, 0, 0);
        dateSelector.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dateSelector = null;
            }
        });
    }

    private View getDataPick(String bir) {
        int curYears = 0;
        int curMons = 0;
        int curDays = 0;
        Calendar c = Calendar.getInstance(TimeZone.getDefault());     // 日期对象
        int curYear = c.get(Calendar.YEAR);
        int curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
        int curDate = c.get(Calendar.DATE);
        final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.datapick, null);

        year = (WheelView) view.findViewById(R.id.year);
        year.setAdapter(new NumericWheelAdapter(1900, curYear));// 参数一：最小年限。参数二：最大年限
        year.setLabel(getString(R.string.year));
        year.setCyclic(true);
        year.addScrollingListener(scrollListenerYear);
        month = (WheelView) view.findViewById(R.id.month);
//        initMonth(curYear, curMonth);
        month.setAdapter(new NumericWheelAdapter(1, 12, "%02d"));
        month.setLabel(getString(R.string.month));
        month.setCyclic(true);
        month.addScrollingListener(scrollListenerMonth);
        day = (WheelView) view.findViewById(R.id.day);
        initDay(curYear, curMonth);
        day.setLabel(getString(R.string.day_day));
        day.setCyclic(true);
        day.addScrollingListener(scrollListenerDay);

        if (bir != null && bir.equals("")) {
            year.setCurrentItem(curYear - 1900);
            month.setCurrentItem(curMonth - 1);
            day.setCurrentItem(curDate - 1);
        } else {
            curYears = Integer.parseInt(bir.substring(0, 4));
            curMons = Integer.parseInt(bir.substring(5, 7));
            curDays = Integer.parseInt(bir.substring(8, 10));
            year.setCurrentItem(curYears - 1900);
            month.setCurrentItem(curMons - 1);
            day.setCurrentItem(curDays - 1);
        }
        ImageView bt = (ImageView) view.findViewById(R.id.set);
        bt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                String mm = null;
                int months = month.getCurrentItem() + 1;
                if (months < 10) {
                    mm = "0" + months;
                } else {
                    mm = String.valueOf(months);
                }
                String dd = String.valueOf((day.getCurrentItem() + 1));
                if (Integer.parseInt(dd) < 10) {
                    dd = "0" + dd;
                }
                currenDate = ((year.getCurrentItem() + 1900) + "-" + mm + "-" + dd);
//                Toast.makeText(EditPersionInfoActivity.this, str, Toast.LENGTH_LONG).show();
//				dataList.clear();
//				readDbAndshow(currenDate);		// 读全天数据
//				resetTheScreen();
//				updateThisScreen(currenDate);
                todayActive.setText(compareTheDate(currenDate));
                dateSelector.dismiss();
            }
        });
        RelativeLayout cancel = (RelativeLayout) view.findViewById(R.id.cancle_select);
        cancel.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                dateSelector.dismiss();
            }
        });
        return view;
    }

    private OnWheelScrollListener scrollListenerDay = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {
        }

        @Override
        public void onScrollingFinished(WheelView wheel) {
            int n_year = year.getCurrentItem() + 1900;// 楠烇拷
            int n_month = month.getCurrentItem() + 1; // 閺堬拷
            int n_day = day.getCurrentItem() + 1;
//            initMonth(n_year, n_month);
//            month.setCurrentItem(0);
            initDay(n_year, n_month);
            compleTheDay(n_year, n_month, n_day);
        }
    };

    private void compleTheDay(int n_year, int n_month, int n_day) {
        Calendar ca = Calendar.getInstance(TimeZone.getDefault());
        int yearNow = ca.get(Calendar.YEAR);
        int monthNow = ca.get(Calendar.MONTH) + 1;
        int dayNow = ca.get(Calendar.DATE);
        if (n_year == yearNow) {
            if (n_month > monthNow) {
                month.setCurrentItem(monthNow - 1);
            }
            if (n_month == monthNow && n_day > dayNow) {
                day.setCurrentItem(dayNow - 1);
            }
        }
    }

    private OnWheelScrollListener scrollListenerMonth = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {
        }

        @Override
        public void onScrollingFinished(WheelView wheel) {
            int n_year = year.getCurrentItem() + 1900;// 楠烇拷
            int n_month = month.getCurrentItem() + 1; // 閺堬拷
            int n_day = day.getCurrentItem() + 1;
            initDay(n_year, n_month);
            compleTheMonth(n_year, n_month, n_day);
        }
    };

    private void compleTheMonth(int n_year, int n_month, int n_day) {
        Calendar ca = Calendar.getInstance(TimeZone.getDefault());
        int yearNow = ca.get(Calendar.YEAR);
        int monthNow = ca.get(Calendar.MONTH) + 1;
        int dayNow = ca.get(Calendar.DATE);
        if (n_year == yearNow) {
            if (n_month > monthNow) {
                month.setCurrentItem(monthNow - 1);
            }
            if (n_month == monthNow && n_day > dayNow) {
                day.setCurrentItem(dayNow - 1);
            }
        }
    }

    private OnWheelScrollListener scrollListenerYear = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {
        }

        @Override
        public void onScrollingFinished(WheelView wheel) {
            int n_year = year.getCurrentItem() + 1900;// 楠烇拷
            int n_month = month.getCurrentItem() + 1; // 閺堬拷
            int n_day = day.getCurrentItem() + 1;
            initDay(n_year, n_month);
            conmpareTheItem(n_year, n_month, n_day);
        }
    };

    private void initDay(int arg1, int arg2) {
        day.setAdapter(new NumericWheelAdapter(1, getDay(arg1, arg2), "%02d"));
    }

    /**
     * @param year
     * @param month
     * @return
     */
    private int getDay(int year, int month) {
        int day = 30;
        boolean flag = false;
        switch (year % 4) {
            case 0:
                flag = true;
                break;
            default:
                flag = false;
                break;
        }
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 2:
                day = flag ? 29 : 28;
                break;
            default:
                day = 30;
                break;
        }
        return day;
    }

    private void conmpareTheItem(int n_year, int n_month, int n_day) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int yearNow = calendar.get(Calendar.YEAR);
        int monthNow = calendar.get(Calendar.MONTH) + 1;
        int dayNow = calendar.get(Calendar.DATE);
        if (yearNow == n_year) {
            if (n_month > monthNow) {
                month.setCurrentItem(monthNow - 1);
            }
            if (n_month == monthNow && n_day > dayNow) {
                day.setCurrentItem(dayNow - 1);
            }
        }
    }


    /**
     * 注销登陆
     */
    private void LogOffAndOther() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    logOutLogin();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        clearThePWDAndAccount();
        launchActivitys(loginButton, nextToLoginActivity.class);
        LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.HARD_VERSION, "");
        clearDevice();
        unregisterreceiverphone();
        MainActivity.this.finish();
    }

    private void clearDevice() {
        if (BluetoothLeService.getInstance() != null) {
            BluetoothLeService.getInstance().close(false);
        }
        LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(DeviceConfig.DEVICE_ADDRESS, "");
        LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(DeviceConfig.DEVICE_NAME, "");
    }

    private void clearThePWDAndAccount() {
        LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.USER_ACCOUNT, "");
        LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.USER_PASSWORD, "");
        LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.USER_NICK, "");
        LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.USER_BIRTHDAY, "");
        LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.USER_HEIGHT, "");
        LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.USER_WEIGHT, "");
        LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.USER_EMAIL, "");
    }


    /**
     * 注销登陆
     */
    private void logOutLogin() throws IOException {
        URL url = null;
        try {
            url = new URL(MyConfingInfo.WebRoot + "user_loginOut");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(true);
        conn.setReadTimeout(6 * 1000);
        conn.setUseCaches(false);
        conn.setConnectTimeout(6 * 1000);
        conn.connect();
    }

    /**
     * 让新的Activity从小范围扩大到全屏
     *
     * @param aboutA
     * @param cl
     */
    private void launchActivity(ViewGroup aboutA, Class<?> cl) {

        ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(
                aboutA,    // 点击的控件
                aboutA.getWidth() / 4,            // 起始坐标x
                aboutA.getHeight() / 2,    // 起始坐标y
                0,            // 拉伸开始的区域的大小
                0);        // 拉伸开始的区域的大小
        startNewActivityOptions(options, cl);
    }

    /**
     * 让新的Activity从小范围扩大到全屏
     *
     * @param aboutA
     * @param cl
     */
    private void launchActivitys(TextView aboutA, Class<?> cl) {

        ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(
                aboutA,    // 点击的控件
                0,            // 起始坐标x
                aboutA.getHeight() / 2,    // 起始坐标y
                aboutA.getWidth(),            // 拉伸开始的区域的大小
                aboutA.getHeight());        // 拉伸开始的区域的大小
        startNewActivityOptions(options, cl);
    }

    /**
     * 设置关联动画启动Acitvity
     *
     * @param persionInfo
     */
    private void launchEditPersionInfoActivity(CircleImageView persionInfo, Intent mIntent) {
        ActivityOptionsCompat compat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this,
                        persionInfo,
                        getString(R.string.image));
        ActivityCompat.startActivity(this, mIntent,
                compat.toBundle());
    }


    /**
     * 获取状态栏的高度
     *
     * @return
     */
    private int getStatusBarHeight() {
        int sesult = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            sesult = getResources().getDimensionPixelSize(resourceId);
        }
        return sesult;
    }


    /**
     * 设置关联动画启动Acitvity
     *
     * @param
     */
    private void launchDetailActivity(RelativeLayout view, Intent mIntent) {
//		ActivityOptionsCompat compat =
//				ActivityOptionsCompat.makeSceneTransitionAnimation(
//						this,
//						view,
//						getString(R.string.movement_image));
//		ActivityCompat.startActivity(this, mIntent,
//				compat.toBundle());
        int x = (int) (view.getX() + view.getWidth() / 2);
        int y = (int) (view.getY() + view.getHeight() / 2);
//		Log.i(TAG, "启动的x:" + x + "--" + "y:" + y);
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeScaleUpAnimation(view, x, y, 0, 0);
        ActivityCompat.startActivity(this, mIntent, compat.toBundle());
    }

    private void launchRemindActivty(ViewGroup remindA) {
        // 让新的Activity从小范围扩大到全屏
        ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(
                remindA,    // 点击的控件
                0,            // 起始坐标x
                remindA.getHeight() / 2,    // 起始坐标y
                0,            // 拉伸开始的区域的大小
                0);        // 拉伸开始的区域的大小
        startNewActivityOptions(options, SettingActivity.class);

//		ActivityOptionsCompat compat =
//				ActivityOptionsCompat.makeSceneTransitionAnimation(
//						this,
//						remindA,
//						getString(R.string.text));
//
//		ActivityCompat.startActivity(this, new Intent(this,
//						RemindActivity.class),
//				compat.toBundle());
    }

    private void startNewActivityOptions(ActivityOptionsCompat options, Class<?> cl) {
        Intent intent = new Intent(this, cl);
        intent.putExtra(MyConfingInfo.WHERE_ARE_YOU_FROM, MyConfingInfo.FROM_THE_MAINACTIVITY);
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }


    /**
     * 启动指定的class对象
     *
     * @param cla
     */
    private void startActivity(Class<?> cla) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this.getApplicationContext(), cla);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 创建新任务,此任务奔溃,不会影响整体
        startActivity(intent);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mResidemenu.isOpened()) {
            mResidemenu.closeMenu();
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
        }
        return true;
    }

    /**
     * 在xml中定义onClick属性,等同于监听
     *
     * @param view
     */
    public void toggleMenu(View view) {

    }

    /**
     * 创建选项菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);// 解析菜单布局
        return true;
    }

    private void showToast(int StringId, int timeState) {
        Toast.makeText(MainActivity.this.getApplicationContext(), getString(StringId), timeState == 1 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }
}
