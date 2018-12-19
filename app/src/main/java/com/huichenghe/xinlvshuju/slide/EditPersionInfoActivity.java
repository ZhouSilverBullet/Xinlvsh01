package com.huichenghe.xinlvshuju.slide;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
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
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.huichenghe.bleControl.Ble.BleBaseDataForBlood;
import com.huichenghe.bleControl.Ble.BleDataForSettingArgs;
import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.xinlvshuju.CustomView.CircleImageView;
import com.huichenghe.xinlvshuju.CustomView.CircleProgressDialog;
import com.huichenghe.xinlvshuju.CustomView.Custom_BMI;
import com.huichenghe.xinlvshuju.DataEntites.ThirdPartyLoginInfoEntity;
import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.PreferenceV.PreferencesService;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.SDPathUtils;
import com.huichenghe.xinlvshuju.Utils.DataSecletor.NumericWheelAdapter;
import com.huichenghe.xinlvshuju.Utils.DataSecletor.OnWheelScrollListener;
import com.huichenghe.xinlvshuju.Utils.DataSecletor.WheelView;
import com.huichenghe.xinlvshuju.Utils.EditorPhotoUtils;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MetricAndBritish;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;
import com.huichenghe.xinlvshuju.Utils.NetStatus;
import com.huichenghe.xinlvshuju.Utils.ProgressUtils;
import com.huichenghe.xinlvshuju.Utils.SaveImageUtil;
import com.huichenghe.xinlvshuju.Utils.WebGlobalConfig;
import com.huichenghe.xinlvshuju.http.HttpUtil;
import com.huichenghe.xinlvshuju.http.IconTask;
import com.huichenghe.xinlvshuju.http.LoginOnBackground;
import com.huichenghe.xinlvshuju.http.OnAllLoginBack;
import com.huichenghe.xinlvshuju.http.UserAccountUtil;
import com.huichenghe.xinlvshuju.http.checkThirdPartyTask;
import com.huichenghe.xinlvshuju.http.onBitmapBack;
import com.huichenghe.xinlvshuju.mainpack.BaseActivity;
import com.huichenghe.xinlvshuju.mainpack.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class EditPersionInfoActivity extends BaseActivity  implements View.OnClickListener{
    private static final String TAG = EditPersionInfoActivity.class.getSimpleName();
    private CoordinatorLayout mCoordinatorLayout;
    private RelativeLayout editPersionInfoLayout;
    private RelativeLayout showPersionInfoLayout;
    private TextView fabY;
    private ImageView fabN;
    private ImageView takePortrait; // 编辑布局，圆形头像控件
    private EditText nickName, height, weight;
    private EditText birthday;
    private RadioGroup editMaleGroup;
    private RadioButton editMale, editFemale, showMale, showFemale;
    private int gender;
    private String ACCOUNT = "user.account";
    private String EMAIL = "user.email";
    private String PASSWORD = "user.password";
    private String NICK = "user.nick";
    private String BIRTHDAY = "user.birthdate";
    private String GENDER = "user.gender";
    private String HEIGHT = "user.height";
    private String WEIGHT = "user.weight";
    private String HEADER = "header";
    private CircleImageView showPortran;        // 头像
    private TextView birthdayS, heightS, weightS, nickS;
    private boolean regist;     // 注册时编辑资料
    private boolean editPersionInfo;// 修改时编辑资料
    private boolean editPersionFirst;// 直接进入时编辑资料
    private boolean editFirst;
    private TextView textCM, textKG, editCM, editKG;
    private String accounta = null;         // 用户输入的用户名
    private String pwdd = null;             // 用户输入密码
    private String emailes = null;            // 用户邮箱
    private Toolbar toolbar;
    private PopupWindow changePWD;
    private RadioGroup unitSelect;
    private PopupWindow pop;
    private RadioButton metricButton, inlnButton;
    private String whereFrom;
    private String nickN;
    private String emailN;
    private String birthdayN;
    private String heightN;
    private String weightN;
    private RadioButton radioButtonMetric, radioButtonInln;
    private PopupWindow bmiPopwindow;
    private ThirdPartyLoginInfoEntity thirdPartyLoginInfoEntity;
    private String meOrLn;
    private TextView userAccount;
    private String modifyPhotoPath;

    String or;      // 代表意图；
    private Toast toast;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    //个人资料
    private  TextView short_ed;
    private  TextView height_ed;
    //编辑资料
    private  TextView short_blood_ed_w;
    private  TextView height_blood_ed_w;

    private PreferencesService service;

    private  int flag;

    private  int  int_s;
    private  int  int_h;


    private  RelativeLayout    to_vis_ss;
    private  RelativeLayout to_vis_rl_layout;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    private GoogleApiClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_persion_info);

        to_vis_ss= (RelativeLayout) findViewById(R.id.to_vis);
        to_vis_rl_layout= (RelativeLayout) findViewById(R.id.to_vis_rl_layout);
        to_vis_rl_layout.setVisibility(View.GONE);
        to_vis_ss.setVisibility(View.GONE);



        //血压初始化
        short_ed= (TextView) findViewById(R.id.short_ed);
        height_ed= (TextView) findViewById(R.id.height_ed);
        short_blood_ed_w= (TextView) findViewById(R.id.short_blood_ed_w);
        height_blood_ed_w= (TextView) findViewById(R.id.height_blood_ed_w);
        short_blood_ed_w.setOnClickListener(this);
        height_blood_ed_w.setOnClickListener(this);

        service=new PreferencesService(EditPersionInfoActivity.this);
        if (service.getPreferences()!=null) {
            //读取本地值
            Map<String, String> params = service.getPreferences();
            //  short_ed.setText(params.get("short_ed"));
            //height_ed.setText(params.get("height_ed"));
            String s = params.get("short_ed");
            String h = params.get("height_ed");
            Log.e("------s=", "--------------" + s);
            Log.e("------h=", "--------------" + h);
            if (s.length() > 0 && h.length() > 0) {

                short_ed.setText(s);
                height_ed.setText(h);
                short_blood_ed_w.setText(s);
                height_blood_ed_w.setText(h);
            }
        }





        meOrLn = LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(MyConfingInfo.METRICORINCH);
//        regist = getIntent().getBooleanExtra(MyConfingInfo.WHERE_ARE_YOU_FROM, false);
        editPersionInfo = getIntent().getBooleanExtra(MyConfingInfo.EDIT_PERSION, false);
//        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
//        int statusBar = getStatusBarHeight();
//        mCoordinatorLayout.setPadding(0, statusBar, 0, 0);
//        settingTheStatebarAndNavigationbar();
        init();
        initMetricAndInch();
        whereFrom = getIntent().getStringExtra(MyConfingInfo.WHERE_ARE_YOU_FORM);
        switch (whereFrom) {
            case MyConfingInfo.FORM_REGIST:     // 注册
                LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).writeSp(MyConfingInfo.METRICORINCH, MyConfingInfo.METRIC);
                File fileImg = new File(SDPathUtils.getSdcardPath() + MyConfingInfo.IMAGEVIEW_SAVE_PATH);
                if (fileImg.exists()) {
                    fileImg.delete();
                }
                resetAccountAndPwdAndEmail();
                editPersionInfoLayout.setVisibility(View.VISIBLE);
                break;
            case MyConfingInfo.FORM_DIRECTLY_USE:
                LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).writeSp(MyConfingInfo.METRICORINCH, MyConfingInfo.METRIC);
                File fileImgs = new File(SDPathUtils.getSdcardPath() + MyConfingInfo.IMAGEVIEW_SAVE_PATH);
                if (fileImgs.exists()) {
                    fileImgs.delete();
                }
                resetAccountAndPwdAndEmail();
                editPersionInfoLayout.setVisibility(View.VISIBLE);
                break;
            case MyConfingInfo.FORM_VIEW_DATA:
                String unit = LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(MyConfingInfo.METRICORINCH);
                if (unit != null && unit.equals(MyConfingInfo.INCH)) {
                    int hei = Integer.parseInt(getContent(MyConfingInfo.USER_HEIGHT));
                    int wei = Integer.parseInt(getContent(MyConfingInfo.USER_WEIGHT));
                    MetricAndBritish units = new MetricAndBritish();
                    int heig = units.BritishToMetricInInch(hei);
                    int weig = units.BritishToMetricInlb(wei);
                    heightN = String.valueOf(heig);
                    weightN = String.valueOf(weig);
                    textKG.setText(getString(R.string.lb_Imperial));
                    textCM.setText(getString(R.string.inch_Imperial));
                    editKG.setText(getString(R.string.lb_Imperial));
                    editCM.setText(getString(R.string.inch_Imperial));
                }
                showTheUserInfo();
                break;
            case MyConfingInfo.FORM_THIRD_LOGIN:
                LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).writeSp(MyConfingInfo.METRICORINCH, MyConfingInfo.METRIC);
                thirdPartyLoginInfoEntity = (ThirdPartyLoginInfoEntity) getIntent().getSerializableExtra(MyConfingInfo.THIRD_LOGIN);
                File fileImsg = new File(SDPathUtils.getSdcardPath() + MyConfingInfo.IMAGEVIEW_SAVE_PATH);
                if (fileImsg.exists()) {
                    fileImsg.delete();
                }
                resetAccountAndPwdAndEmail();
                editPersionInfoLayout.setVisibility(View.VISIBLE);
                IconTask iconTask = new IconTask();
                iconTask.setOnBitmapBack(new onBitmapBack() {
                    @Override
                    public void onBitmapBack(Bitmap bitmap) {
                        takePortrait.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapError() {

                    }
                });
                iconTask.execute(thirdPartyLoginInfoEntity.getIcon());
                if (thirdPartyLoginInfoEntity.getNickName() != null && !thirdPartyLoginInfoEntity.getNickName().isEmpty()) {
                    nickName.setText(thirdPartyLoginInfoEntity.getNickName());
                }
                if (thirdPartyLoginInfoEntity.getGender() != null && thirdPartyLoginInfoEntity.getGender().equals("m")) {
                    editMale.setChecked(true);
                } else {
                    editFemale.setChecked(true);
                }
                break;
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        if (nickName.getText().toString().equals("") && birthday.getText().toString().equals("") ){
            Log.e("------------","------------123");

            short_blood_ed_w.setText("");
            Log.e("------------","------------");
            height_blood_ed_w.setText("");


        }






    }


    private void resetAccountAndPwdAndEmail() {
        LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).writeSp(MyConfingInfo.USER_EMAIL, "");
        LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).writeSp(MyConfingInfo.USER_ACCOUNT, "");
        LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).writeSp(MyConfingInfo.USER_PASSWORD, "");
    }

    private void showTheUserInfo() {
        // 读取缓存的个人资料并显示
        Bitmap mBitmap = SaveImageUtil.readImageFormSD(SDPathUtils.getSdcardPath() + MyConfingInfo.IMAGEVIEW_SAVE_PATH);
        if (mBitmap != null) {
            showPortran.setImageBitmap(mBitmap);
            takePortrait.setImageBitmap(mBitmap);
        }
        String nic = getContent(MyConfingInfo.USER_NICK);
        String bir = getContent(MyConfingInfo.USER_BIRTHDAY);
        String hei = getContent(MyConfingInfo.USER_HEIGHT);
        String wei = getContent(MyConfingInfo.USER_WEIGHT);
        Log.i(TAG, "onResume" + wei);
        Log.i(TAG, "onResume" + hei);
        String unit = LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(MyConfingInfo.METRICORINCH);
        if (unit != null && unit.equals(MyConfingInfo.INCH)) {
            if (hei != null & !hei.equals("") && wei != null && !wei.equals("")) {
                MetricAndBritish units = new MetricAndBritish();
                int heig = units.MetricToBritishInCm(Integer.parseInt(hei));
                int weig = units.MetricToBritishInKg(Integer.parseInt(wei));
                hei = String.valueOf(heig);
                wei = String.valueOf(weig);
//                textKG.setText("lb");
//                textCM.setText("inch");
//                editKG.setText("lb");
//                editCM.setText("inch");
                textKG.setText(getString(R.string.lb_Imperial));
                textCM.setText(getString(R.string.inch_Imperial));
                editKG.setText(getString(R.string.lb_Imperial));
                editCM.setText(getString(R.string.inch_Imperial));
                inlnButton.setChecked(true);
                radioButtonInln.setChecked(true);
                Log.i(TAG, "onResume转换后:" + wei);
            }
        }
        showAccount();
        showText(nickS, nic);
        showText(birthdayS, bir);
        showText(heightS, hei);
        showText(weightS, wei);
        String genders = LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(MyConfingInfo.USER_GENDER);
        if (genders.equals("1")) {
            showMale.setChecked(true);
            if (!regist) {
                editMale.setChecked(true);
            }
        } else {
            showFemale.setChecked(true);
            if (!regist) {
                editFemale.setChecked(true);
            }
        }
        showEditText(nickName, nic);
        showText(birthday, bir);
        showEditText(height, hei);
        showEditText(weight, wei);
    }

    private void showAccount()
    {
        String userAcc = LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(MyConfingInfo.USER_ACCOUNT);
        if(userAcc != null && userAcc.equals(""))
        {
            userAccount.setVisibility(View.INVISIBLE);
        }
        else
        {
            try {
                JSONObject json = new JSONObject(userAcc);
                String acc = json.getString(MyConfingInfo.ACCOUNT);
                if(acc != null && acc.contains(";"))
                {
                    userAccount.setText(acc.split(";")[1]);
                }
                else
                {
                    userAccount.setText(acc);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initMetricAndInch() {
        textCM = (TextView) findViewById(R.id.text_cm);
        textKG = (TextView) findViewById(R.id.text_kg);
        editCM = (TextView) findViewById(R.id.edit_cm);
        editKG = (TextView) findViewById(R.id.edit_kg);
    }


//    private DialogInterface.OnClickListener liss = new DialogInterface.OnClickListener()
//    {
//        @Override
//        public void onClick(DialogInterface dialog, int which)
//        {
//            switch (which)
//            {
//                case 0:
//                    changeToMetric();
//                    break;
//                case 1:
//                    changeToInch();
//                    break;
//                case 2:
//                    dialog.dismiss();
//                    break;
//            }
//        }
//    };

    // 英制
    private void changeToInch() {
        String unit = LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(MyConfingInfo.METRICORINCH);
        if (unit != null && !unit.equals(MyConfingInfo.INCH)) {
            LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).writeSp(MyConfingInfo.METRICORINCH, MyConfingInfo.INCH);
            textKG.setText(getString(R.string.lb_Imperial));
            textCM.setText(getString(R.string.inch_Imperial));
            editKG.setText(getString(R.string.lb_Imperial));
            editCM.setText(getString(R.string.inch_Imperial));
            Intent intent = new Intent(MyConfingInfo.BROADCAST_CHAGE_UNIT);
            intent.putExtra(MyConfingInfo.METRICORINCH, MyConfingInfo.INCH);
            sendBroadcast(intent);
//        String whereFrom = getIntent().getStringExtra(MyConfingInfo.WHERE_ARE_YOU_FORM);
//        if(whereFrom != null && whereFrom.equals(MyConfingInfo.FORM_VIEW_DATA) && !isChangeInfo)
//        {
//            String heiCache = LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(MyConfingInfo.USER_HEIGHT);
//            String weiCache = LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(MyConfingInfo.USER_WEIGHT);
//            if(heiCache != null && !heiCache.equals(""))
//            {
//                int heiC = Integer.parseInt(heiCache);
//                heiC = new MetricAndBritish().MetricToBritishInCm(heiC);
//                heightS.setText(String.valueOf(heiC));
//                height.setText(String.valueOf(heiC));
//            }
//            if(weiCache != null && !weiCache.equals(""))
//            {
//                int weiC = Integer.parseInt(weiCache);
//                weiC = new MetricAndBritish().MetricToBritishInKg(weiC);
//                weightS.setText(String.valueOf(weiC));
//                weight.setText(String.valueOf(weiC));
//            }
//        }
//        else
//        {
            String cmEdit = height.getText().toString();
            String kgEdit = weight.getText().toString();
            if (cmEdit != null && !cmEdit.isEmpty()) {
                int heiC = Integer.parseInt(cmEdit);
                heiC = new MetricAndBritish().MetricToBritishInCm(heiC);
                heightS.setText(String.valueOf(heiC));
                height.setText(String.valueOf(heiC));
            }
            if (kgEdit != null && !kgEdit.isEmpty()) {
                int weiC = Integer.parseInt(kgEdit);
                weiC = new MetricAndBritish().MetricToBritishInKg(weiC);
                weightS.setText(String.valueOf(weiC));
                weight.setText(String.valueOf(weiC));
            }

            inlnButton.setChecked(true);
            radioButtonInln.setChecked(true);
        }

//        }
    }

    // 公制
    private void changeToMetric() {
        String unit = LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(MyConfingInfo.METRICORINCH);
        if (unit != null && !unit.equals(MyConfingInfo.METRIC)) {
            LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).writeSp(MyConfingInfo.METRICORINCH, MyConfingInfo.METRIC);
            textKG.setText(getString(R.string.kg_metric));
            textCM.setText(getString(R.string.cm_metric));
            editKG.setText(getString(R.string.kg_metric));
            editCM.setText(getString(R.string.cm_metric));
            Intent intent = new Intent(MyConfingInfo.BROADCAST_CHAGE_UNIT);
            intent.putExtra(MyConfingInfo.METRICORINCH, MyConfingInfo.METRIC);
            sendBroadcast(intent);
//        String whereFrom = getIntent().getStringExtra(MyConfingInfo.WHERE_ARE_YOU_FORM);
//        if(whereFrom != null && whereFrom.equals(MyConfingInfo.FORM_VIEW_DATA) && !isChangeInfo)
//        {
//            String heiCache = LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(MyConfingInfo.USER_HEIGHT);
//            String weiCache = LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(MyConfingInfo.USER_WEIGHT);
//            if(heiCache != null && !heiCache.equals(""))
//            {
//                heightS.setText(heiCache);
//                height.setText(heiCache);
//            }
//            if(weiCache != null && !weiCache.equals(""))
//            {
//                weightS.setText(weiCache);
//                weight.setText(weiCache);
//                Log.i(TAG, "缓存中的体重:" + weiCache);
//            }
//        }
            String cmEdit = height.getText().toString();
            String kgEdit = weight.getText().toString();
            if (cmEdit != null && !cmEdit.isEmpty()) {
                int heiC = Integer.parseInt(cmEdit);
                heiC = new MetricAndBritish().BritishToMetricInInch(heiC);
                heightS.setText(String.valueOf(heiC));
                height.setText(String.valueOf(heiC));
            }
            if (kgEdit != null && !kgEdit.isEmpty()) {
                int weiC = Integer.parseInt(kgEdit);
                weiC = new MetricAndBritish().BritishToMetricInlb(weiC);
                weightS.setText(String.valueOf(weiC));
                weight.setText(String.valueOf(weiC));
            }

            metricButton.setChecked(true);
            radioButtonMetric.setChecked(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //判断设备类型
        String type = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.USER_DEVICE_TYPE);
        if(type != null && type.equals(MyConfingInfo.DEVICE_BLOOD))
        {
            to_vis_rl_layout.setVisibility(View.VISIBLE);
            to_vis_ss.setVisibility(View.VISIBLE);

            service = new PreferencesService(EditPersionInfoActivity.this);
            Map<String, String> params = service.getPreferences();
            //short_ed.setText(params.get("short_ed"));
            //height_ed.setText(params.get("height_ed"));
            String s = params.get("short_ed");
            String h = params.get("height_ed");
            Log.e("------s=", "--------------" + s);
            Log.e("------h=", "--------------" + h);

            if (s.length()>0 && h.length()>0){
                int_s = Integer.parseInt(s);
                int_h = Integer.parseInt(h);
            }
            if (BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice()) {

                if (s.length()>1 && h.length()>1) {
                    BleBaseDataForBlood.getBloodInstance().sendStandardBloodData(int_h, int_s);
                }

            } else {
                //   Toast.makeText(EditPersionInfoActivity.this,  R.string.toask_mark_bl, Toast.LENGTH_LONG).show();

            }

        }
        else if(type != null && type.equals(MyConfingInfo.DEVICE_HR))
        {
            to_vis_rl_layout.setVisibility(View.GONE);
            to_vis_ss.setVisibility(View.GONE);

        }






    }

    private String getContent(String s) {
        return LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(s);
    }


    /**
     * 显示指定控件，指定内容
     *
     * @param tv
     * @param content
     */
    private void showText(TextView tv, String content) {
        if (tv != null && content != null) {
            tv.setText(content);
        }

    }


    private void showEditText(EditText et, String content) {
        if (et != null && content != null) {
            et.setText(content);
        }
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resurcedId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resurcedId > 0) {
            result = getResources().getDimensionPixelSize(resurcedId);
        }

        return result;
    }

    private void settingTheStatebarAndNavigationbar() {
        // 透明状态栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //底部虚拟键透明
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }


    /**
     * 初始化界面对象并监听处理
     */
    private void init() {
        userAccount = (TextView)findViewById(R.id.user_info_account);
        radioButtonInln = (RadioButton) findViewById(R.id.select_lnch);
        radioButtonMetric = (RadioButton) findViewById(R.id.select_metric);
//        radioButtonMetric.setChecked(true);
        metricButton = (RadioButton) findViewById(R.id.select_metric_show);
        inlnButton = (RadioButton) findViewById(R.id.select_lnch_show);
        metricButton.setChecked(true);
        unitSelect = (RadioGroup) findViewById(R.id.radio_grop_selects);
        unitSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.select_metric:
                        changeToMetric();
                        break;
                    case R.id.select_lnch:
                        changeToInch();
                        break;
                }
            }
        });
        showPortran = (CircleImageView) findViewById(R.id.show_portrait);

        nickS = (TextView) findViewById(R.id.nick_show_persioninfo);
        birthdayS = (TextView) findViewById(R.id.show_birthday_persioninfo);
        heightS = (TextView) findViewById(R.id.show_height_persioninfo);
        weightS = (TextView) findViewById(R.id.show_weight_persioninfo);
        editMale = (RadioButton) findViewById(R.id.male_edit);
        editFemale = (RadioButton) findViewById(R.id.female_edit);
        showMale = (RadioButton) findViewById(R.id.show_male);
        showFemale = (RadioButton) findViewById(R.id.show_female);
        editMaleGroup = (RadioGroup) findViewById(R.id.group_layout_edit);
        // 设置选项监听
        editMaleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.male_edit:
                        gender = 1;
                        break;
                    case R.id.female_edit:
                        gender = 2;
                        break;
                }
            }
        });
        nickName = (EditText) findViewById(R.id.nick_name);
        birthday = (EditText) findViewById(R.id.input_birthday);
        birthday.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                String bir = birthday.getText().toString();
//                if(bir != null && !bir.equals(""))
//                {
//                    Log.i(TAG, "出生日期:" + bir);
//                }
                // 显示悬浮窗，窗内显示日期选择
                showPopwindow(getDataPick(bir));
            }
        });
        height = (EditText) findViewById(R.id.edit_height);
        weight = (EditText) findViewById(R.id.edit_weight);
        /**
         String whereFrom = getIntent().getStringExtra(MyConfingInfo.WHERE_ARE_YOU_FORM);
         if(whereFrom.equals(MyConfingInfo.FORM_REGIST) || whereFrom.equals(MyConfingInfo.FORM_DIRECTLY_USE))
         {
         weight.addTextChangedListener(new TextWatcher()
         {
         @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
         String hei = height.getText().toString();
         if(hei != null && hei.isEmpty())
         {
         MyToastUitls.showToast(EditPersionInfoActivity.this, R.string.enter_height_befor, 1);
         height.requestFocus();
         }
         }
         @Override public void onTextChanged(CharSequence s, int start, int before, int count)
         {

         }
         @Override public void afterTextChanged(Editable s)
         {
         if(checkInputWeight())
         {
         //                    hideKanband(weight);
         //                    Log.i(TAG, "调用Edittext监听");
         showBMIWindow(getBMILayout());
         }
         }
         });
         }*
         */

        toolbar = (Toolbar) findViewById(R.id.persioniffo_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // toolbar监听返回键
        toolbar.setNavigationIcon(R.mipmap.back_icon_new);
        toolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {   // 关闭页面
                EditPersionInfoActivity.this.onBackPressed();
            }
        });
        // 菜单监听处理
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_edit_persion_info:
                        // 显示popwindow
                        showBottomPopwindow();
//                        // 判断sdk版本
//                        if(Build.VERSION.SDK_INT < 21)
//                        {
//                            editPersionInfoLayout.setVisibility(View.VISIBLE);
//                        }
//                        else
//                        {
//                            // 执行圆形动画
//                            int cx = editPersionInfoLayout.getWidth();  // 圆心x轴
//                            int cy = 0;                                 // 圆心y轴
//
//                            // get the final radius for the clipping circle
//                            int finalRadius = Math.max(editPersionInfoLayout.getWidth(), editPersionInfoLayout.getHeight());
//
//                            // create the animator for this view (the start radius is zero)
//                            Animator anim =
//                                    ViewAnimationUtils.createCircularReveal(editPersionInfoLayout, cx, cy / 2, 0, finalRadius);
//
//                            // make the view visible and start the animation
//                            editPersionInfoLayout.setVisibility(View.VISIBLE);
//                            anim.start();
//                        showPersionInfoLayout.setVisibility(View.INVISIBLE);
//                        fabN.hide();
//                        }
                        break;
//                    case R.id.menu_edit_persion_info_change_pwd:
//                        if(NetStatus.isNetWorkConnected(EditPersionInfoActivity.this))
//                        {
//                            String account = LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(MyConfingInfo.USER_ACCOUNT);
//                            if(account == null || account.equals(""))
//                            {
//                                MyToastUitls.showToast(EditPersionInfoActivity.this, R.string.not_login, 1);
//                            }
//                            else
//                            {
//                                chandeThePassword();
//                            }
//                        }
//                        else
//                        {
//                            MyToastUitls.showToast(EditPersionInfoActivity.this, R.string.network_disconnect, 1);
//                        }
//                        break;
//                    case R.id.menu_edit_persion_info_setting_gongyingzhi:
//                        builder = new AlertDialog.Builder(EditPersionInfoActivity.this);
//                        builder.setTitle(R.string.choose_danwei);
//                        builder.setCancelable(false);
//                        builder.setItems(new String[]{getString(R.string.gongzhi), getString(R.string.yingzhi), getString(R.string.be_cancle)}, liss);
//                        builder.create().show();
//                        break;
                }
                return true;
            }
        });
        // 悬浮按钮
        fabY = (TextView) findViewById(R.id.fab_ok);
        fabY.setOnClickListener(myListener);
        fabN = (ImageView) findViewById(R.id.fab_cancle);
        fabN.setOnClickListener(myListener);
        // 头像
        takePortrait = (ImageView) findViewById(R.id.take_portrait);
        takePortrait.setOnClickListener(myListener);
        showPortran.setOnClickListener(myListener);
        // 编辑个人信息布局
        editPersionInfoLayout = (RelativeLayout) findViewById(R.id.edit_persion_info_layout);
        // 显示个人信息布局
        showPersionInfoLayout = (RelativeLayout) findViewById(R.id.show_persioninfo_layout);
        initGender();
    }


    /**
     * 隐藏软键盘
     *
     * @param weight
     */
    private void hideKanband(EditText weight) {
        InputMethodManager ma = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        ma.hideSoftInputFromInputMethod(weight.getWindowToken(), 0);
    }

    private void showBMIWindow(View view) {
        if (!isDestroyed() && !isFinishing())
        {
            bmiPopwindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            bmiPopwindow.setBackgroundDrawable(new BitmapDrawable());
            bmiPopwindow.setFocusable(true);
            bmiPopwindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            bmiPopwindow.setAnimationStyle(R.style.mypopupwindow_anim_style);
            bmiPopwindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            bmiPopwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    bmiPopwindow = null;
                }
            });
        }
    }

    private View getBMILayout() {
        int wei = Integer.parseInt(weight.getText().toString());
        int hei = Integer.parseInt(height.getText().toString());
        Log.i(TAG, "转换前：" + hei + "--" + wei);
        String unit = LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(MyConfingInfo.METRICORINCH);
        if (unit != null && unit.equals(MyConfingInfo.INCH)) {
            MetricAndBritish mab = new MetricAndBritish();
            hei = mab.BritishToMetricInInch(hei);
            wei = mab.BritishToMetricInlb(wei);
            Log.i(TAG, "转换后：" + hei + "--" + wei);
        }
        float bmis = getBMIValue(wei, hei);
        int bmi = getHelfUp(bmis);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.bmi_layout, null);
        final TextView registNowAndCloseBMI = (TextView) view.findViewById(R.id.regist_now_and_close);
        final ImageView backToEdit = (ImageView) view.findViewById(R.id.back_to_edit);
        Custom_BMI ruler = (Custom_BMI) view.findViewById(R.id.bmi_imageView);
        TextView userBmi = (TextView) view.findViewById(R.id.user_bmi);
//        TextView standardWeight = (TextView)view.findViewById(R.id.standard_weight);
        TextView weightRange = (TextView) view.findViewById(R.id.weight_range);
        setBMIScreen(ruler, bmi, userBmi, weightRange, wei, hei);
        NoDoubleClickListener innerListener = new NoDoubleClickListener()
        {
            @Override
            public void onNoDoubleClick(View v)
            {
                if (v == backToEdit)
                {
                    if (bmiPopwindow != null && bmiPopwindow.isShowing())
                        bmiPopwindow.dismiss();
                } else if (v == registNowAndCloseBMI)
                {
                    bmiPopwindow.dismiss();
                    dealTheInput();
                }
            }
        };
        backToEdit.setOnClickListener(innerListener);
        registNowAndCloseBMI.setOnClickListener(innerListener);
        return view;
    }

    private int getHelfUp(float bmi) {
        BigDecimal big = new BigDecimal(bmi);
        BigDecimal b = big.setScale(0, BigDecimal.ROUND_HALF_UP);
        return b.intValue();
    }

    private void setBMIScreen(Custom_BMI ruler, int bmi,
                              TextView userBmi, TextView weightRange, int wei, int hei) {
        float maxBmi = 0;
        float minBmi = 0;
        String weiRange = "";
        if (bmi <= 18.5) {
            ruler.setTextValue("BMI", String.valueOf(bmi), getString(R.string.thin), R.mipmap.persion_one, R.color.less_color);
//            ruler.setImageViewPersion(R.mipmap.persion_one);
//            ruler.setProgressPointer(1);
//            maxBmi = (float)((18.5 * hei * hei)/10000);
//            maxBmi = getHelfUp(maxBmi);
//            weiRange = getString(R.string.health_range) + getString(R.string.less_than) + maxBmi + "kg";
        } else if (bmi > 18.5 && bmi <= 25) {
//            ruler.setImageViewPersion(R.mipmap.persion_two);
//            ruler.setProgressPointer(2);
            ruler.setTextValue("BMI", String.valueOf(bmi), getString(R.string.zhengchang), R.mipmap.persion_two, R.color.nomale_color);
        } else if (bmi > 25 && bmi <= 30) {
            ruler.setTextValue("BMI", String.valueOf(bmi), getString(R.string.over_weight), R.mipmap.persion_three, R.color.much_color);
//            ruler.setImageViewPersion(R.mipmap.persion_three);
//            ruler.setProgressPointer(3);
//            maxBmi = (30 * hei * hei)/10000;
//            minBmi = (float)((25 * hei * hei) /10000);
//            maxBmi = getHelfUp(maxBmi);
//            minBmi = getHelfUp(minBmi);
//            weiRange = getString(R.string.health_range) + minBmi + "-" + maxBmi + "kg";
        } else if (bmi > 30) {
            ruler.setTextValue("BMI", String.valueOf(bmi), getString(R.string.Obesity), R.mipmap.persion_four, R.color.less_color);
//            ruler.setImageViewPersion(R.mipmap.persion_four);
//            ruler.setProgressPointer(4);
//            maxBmi = (25 * hei * hei)/10000;
//            minBmi = (float)(30 * hei * hei)/10000;
//            minBmi = getHelfUp(minBmi);
//            weiRange = getString(R.string.health_range) + getString(R.string.greater_than) + minBmi + "kg";
        }
//        ruler.invalidate();

        userBmi.setText("18-25");
//        standardWeight.setText();
        weightRange.setText(getWeightRange(wei, hei));
    }

    private String getStandarWeight(int hei) {
        float standard = (22 * hei * hei) / 10000;
        return getString(R.string.standard_height) + getHelfUp(standard) + "kg";
    }

    private String getWeightRange(int wei, int hei) {
        float maxBmis = (25 * hei * hei) / 10000;
        float minBmis = (float) (18.5 * hei * hei) / 10000;
        int maxBmi = getHelfUp(maxBmis);
        int minBmi = getHelfUp(minBmis);
        return minBmi + "-" + maxBmi + "kg";
    }

    private float getBMIValue(int wei, int hei) {
        float we = (float) wei;
        float he = ((float) hei) / 100;
//        Log.i(TAG, "身高体重：" + we + "--" + he);
        return we / he / he;
    }

    private boolean checkInputWeight() {
        String wei = weight.getText().toString();
        if (wei != null && !wei.isEmpty()) {
            int weig = Integer.parseInt(wei);
            String unit = LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(MyConfingInfo.METRICORINCH);
            if (unit != null && unit.equals(MyConfingInfo.INCH)) {
                if (weig >= 66 && weig <= 375) {
                    return true;
                } else {
                    return false;
                }
            } else {
                if (weig >= 30 && weig <= 170) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    private void initGender() {
        String useGender = LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(MyConfingInfo.USER_GENDER);

        if (useGender != null && !useGender.isEmpty()) {
            gender = Integer.parseInt(useGender);
            if (gender == 1) editMale.setChecked(true);
            if (gender == 2) editFemale.setChecked(true);
        } else {
            gender = 1;
            editMale.setChecked(true);
        }
    }

    private void showBottomPopwindow() {
        View view = getXMLLayout();
        pop = new PopupWindow(view, LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        pop.setAnimationStyle(R.style.mypopupwindow_anim_style);
        pop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.showAtLocation(view, Gravity.CENTER, 0, 0);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                pop = null;
            }
        });

    }

    private View getXMLLayout() {
        View vi = LayoutInflater.from(EditPersionInfoActivity.this).inflate(R.layout.bottom_layout_at_editpersioninfo, null);
        final TextView changePersionInfo = (TextView) vi.findViewById(R.id.change_persion_info);
        final TextView changePersionPwd = (TextView) vi.findViewById(R.id.change_persion_pwd);
        final TextView cancle = (TextView)vi.findViewById(R.id.cancle_window);
        if(!getAndCheckPWD())
        {
            changePersionPwd.setVisibility(View.GONE);
        }
        final LinearLayout modifyLayout = (LinearLayout) vi.findViewById(R.id.layout_modify);
        NoDoubleClickListener liss = new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (v == changePersionInfo) {
                    pop.dismiss();
                    String type = UserAccountUtil.getType(EditPersionInfoActivity.this);
                    if(type != null && type.equals("5"))
                    {
                        showModifyLayout();

                    }
                    else
                    {
                        if(NetStatus.isNetWorkConnected(EditPersionInfoActivity.this))
                        {
                            showModifyLayout();
                        }
                        else
                        {
                            MyToastUitls.showToast(EditPersionInfoActivity.this, R.string.net_wrong, 1);
                        }
                    }
                } else if (v == changePersionPwd) {
                    pop.dismiss();
                    if (NetStatus.isNetWorkConnected(EditPersionInfoActivity.this)) {
                        String account = UserAccountUtil.getAccount(EditPersionInfoActivity.this);
                        if (account == null || account.equals("")) {
                            MyToastUitls.showToast(EditPersionInfoActivity.this, R.string.not_login, 1);
                        } else {
                            chandeThePassword(account);
                        }
                    } else {
                        MyToastUitls.showToast(EditPersionInfoActivity.this, R.string.network_disconnect, 1);
                    }
                } else if (v == modifyLayout) {
                    if (pop != null && pop.isShowing())
                        pop.dismiss();
                }
                else if(v == cancle)
                {
                    if(pop != null && pop.isShowing())
                        pop.dismiss();
                }
            }
        };
        changePersionInfo.setOnClickListener(liss);
        changePersionPwd.setOnClickListener(liss);
        cancle.setOnClickListener(liss);
        modifyLayout.setOnClickListener(liss);
        return vi;
    }

    private void showModifyLayout()
    {
        Bitmap bitmaps = SaveImageUtil.readImageFormSD(SDPathUtils.getSdcardPath() + MyConfingInfo.IMAGEVIEW_SAVE_PATH);
        takePortrait.setImageBitmap(bitmaps);
        mBitmap = null;
        modifyPhotoPath = null;
        modifyPersionInfo();
    }

    private boolean getAndCheckPWD()
    {
        String pwd = LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(MyConfingInfo.USER_PASSWORD);
        if(pwd != null && pwd.isEmpty())
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    private void modifyPersionInfo() {
        // 判断sdk版本
        if (Build.VERSION.SDK_INT < 21) {
            editPersionInfoLayout.setVisibility(View.VISIBLE);
        } else {
            // 执行圆形动画
            int cx = editPersionInfoLayout.getWidth();  // 圆心x轴
            int cy = 0;                                 // 圆心y轴

            // get the final radius for the clipping circle
            int finalRadius = Math.max(editPersionInfoLayout.getWidth(), editPersionInfoLayout.getHeight());

            // create the animator for this view (the start radius is zero)
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(editPersionInfoLayout, cx, cy / 2, 0, finalRadius);

            // make the view visible and start the animation
            editPersionInfoLayout.setVisibility(View.VISIBLE);
            anim.start();
//             showPersionInfoLayout.setVisibility(View.INVISIBLE);
//                        fabN.hide();
        }
    }

    private void chandeThePassword(String userAccount) {
        View view = getTheChangeLayout(userAccount);
        changePWD = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        changePWD.setBackgroundDrawable(new BitmapDrawable());
        changePWD.setAnimationStyle(R.style.mypopupwindow_anim_style_left_right);
        changePWD.setFocusable(true);
//        changePWD.setClippingEnabled(false);
        changePWD.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        changePWD.showAtLocation(view, Gravity.CENTER, 0, 0);
        changePWD.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                changePWD = null;
            }
        });
    }

    private View getTheChangeLayout(String userAccount) {
        LayoutInflater ss = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View viewR = ss.inflate(R.layout.change_pwd_layout, null);
        TextView tvUserAccount = (TextView) viewR.findViewById(R.id.user_acount);
        tvUserAccount.setText(getString(R.string.account_text) + ":" + userAccount);
        ImageView back = (ImageView) viewR.findViewById(R.id.back_icon_changePwd);
        back.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (changePWD != null && changePWD.isShowing()) {
                    changePWD.dismiss();
                }
            }
        });
        Button button = (Button) viewR.findViewById(R.id.be_true_to_change);
        final EditText oldPWD = (EditText) viewR.findViewById(R.id.old_pwd_input);
        final EditText newPWD = (EditText) viewR.findViewById(R.id.new_pwd_input);
        final EditText confirm = (EditText) viewR.findViewById(R.id.new_pwd_confirm);

        button.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
//                MyToastUitls.showToast(EditPersionInfoActivity.this, R.string.please_wait, 1);
                boolean checked = checkTheInput(EditPersionInfoActivity.this, oldPWD, newPWD, confirm);
                if (checked)
                {
                    if(!NetStatus.isNetWorkConnected(EditPersionInfoActivity.this))
                    {
                        MyToastUitls.showToast(EditPersionInfoActivity.this, R.string.net_wrong, 1);
                        return;
                    }
                    CircleProgressDialog.getInstance().showCircleProgressDialog(EditPersionInfoActivity.this);
                    Editable a = oldPWD.getText();
                    Editable b = newPWD.getText();
                    final String oldP = a.toString();
                    final String newP = b.toString();
                    LoginOnBackground onBackground = new LoginOnBackground(EditPersionInfoActivity.this);
                    onBackground.setOnLoginBackListener(new OnAllLoginBack() {
                        @Override
                        public void onLoginBack(String result) {
                            new changPWDTask().execute(oldP, newP);
                        }
                    });
                    onBackground.execute();
                }
            }
        });
        return viewR;
    }

    private String connectNET(String oldP, String newP) throws IOException {
        String oldPP = WebGlobalConfig.get32MD5Str(oldP);
        String newPP = WebGlobalConfig.get32MD5Str(newP);
        Log.i(TAG, "新旧密码:" + oldPP + "--" + newPP);
        URL url = null;
        HttpURLConnection connection = null;

        String path = MyConfingInfo.WebRoot + "user_updatePwd?oldPwd=" + oldPP + "&newPwd=" + newPP + "&newPwd2=" + newPP + "";
        String cookie = LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(MyConfingInfo.COOKIE_FOR_ME);
        try {
            url = new URL(path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", cookie);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        connection.setDoOutput(true);
        connection.setDoInput(true);
        // 设置连接超时
        connection.setConnectTimeout(6 * 1000);
        connection.setReadTimeout(6 * 1000);
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.connect();

        InputStreamReader reader = new InputStreamReader(connection.getInputStream(), "UTF-8");
        BufferedReader read = new BufferedReader(reader);
        String result = "";
        String c = "";
        while ((c = read.readLine()) != null) {
            result += c;
        }
        Log.i(TAG, "修改结果:" + result);

        read.close();
        reader.close();
        connection.disconnect();
        return result;

    }
    //todo onclick
    @Override
    public void onClick(View v) {

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate( R.layout.blood_view, null);
        final EditText short_blood_q= (EditText) layout. findViewById(R.id.short_blood_q);
        final EditText height_blood_q = (EditText) layout. findViewById(R.id.height_ed_q);
        final  Button   true_bt= (Button)  layout.findViewById(R.id.true_bt);
        final  Button   false_bt= (Button)  layout.findViewById(R.id.false_bt);
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        final android.app.AlertDialog dialog = builder.setView(layout).show();
        switch (v.getId()){
            case R.id.short_blood_ed_w:
                false_bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                true_bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String short_blood = short_blood_q.getText().toString();
                        String height_blood = height_blood_q.getText().toString();
                        if (short_blood !=null && height_blood !=null){
                            if (short_blood.length()>0 && height_blood.length()>0){
                                int int_short_bood = Integer.parseInt(short_blood);
                                int int_height_bood = Integer.parseInt(height_blood);
                                if (int_short_bood>=40 && int_short_bood<=100 && int_height_bood>=90 && int_height_bood <=180){
                                    short_blood_ed_w.setText(short_blood);
                                    height_blood_ed_w.setText(height_blood);
                                    // 增加到本地
                                    service = new PreferencesService(getApplicationContext());
                                    service.save(short_blood, height_blood);
                                    Log.e("----------", "-----------保存成功");
                                    //读取本地值
                                    Map<String, String> params = service.getPreferences();
                                    //  short_ed.setText(params.get("short_ed"));
                                    //height_ed.setText(params.get("height_ed"));
                                    String s = params.get("short_ed");
                                    String h = params.get("height_ed");
                                    Log.e("------s=", "--------------" + s);
                                    Log.e("------h=", "--------------" + h);
                                    short_blood_ed_w.setText(s);
                                    height_blood_ed_w.setText(h);
                                    short_ed.setText(s);
                                    height_ed.setText(h);
                                    int int_s =Integer.parseInt(s);
                                    int int_h =Integer.parseInt(h);
                                    BleBaseDataForBlood.getBloodInstance().sendStandardBloodData(int_h, int_s);
                                    dialog.dismiss();

                                }else {
//                                    Toast.makeText(EditPersionInfoActivity.this, "请输入正确范围，舒张压40-100，收缩压90-180", Toast.LENGTH_LONG).show();
                                    Toast toast=Toast.makeText(EditPersionInfoActivity.this,R.string.toask_mark , Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();


                                }
                            }else {
//
                                Toast toast=Toast.makeText(EditPersionInfoActivity.this,R.string.toask_mark , Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        }else {
//
                            Toast toast=Toast.makeText(EditPersionInfoActivity.this,R.string.toask_mark , Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                });

                break;

            case  R.id.height_blood_ed_w:
                false_bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                true_bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String short_blood = short_blood_q.getText().toString();
                        String height_blood = height_blood_q.getText().toString();
                        if (short_blood !=null && height_blood !=null){

                            if (short_blood.length()>0 && height_blood.length()>0){
                                int int_short_bood = Integer.parseInt(short_blood);
                                int int_height_bood = Integer.parseInt(height_blood);
                                if (int_short_bood>=40 && int_short_bood<=100 && int_height_bood>=90 && int_height_bood <=180){
                                    short_blood_ed_w.setText(short_blood);
                                    height_blood_ed_w.setText(height_blood);
                                    // 增加到本地
                                    service = new PreferencesService(getApplicationContext());
                                    service.save(short_blood, height_blood);
                                    Log.e("----------", "-----------保存成功");
                                    //读取本地值
                                    Map<String, String> params = service.getPreferences();
                                    //  short_ed.setText(params.get("short_ed"));
                                    //height_ed.setText(params.get("height_ed"));
                                    String s = params.get("short_ed");
                                    String h = params.get("height_ed");
                                    Log.e("------s=", "--------------" + s);
                                    Log.e("------h=", "--------------" + h);
                                    short_ed.setText(s);
                                    height_ed.setText(h);
                                    short_blood_ed_w.setText(s);
                                    height_blood_ed_w.setText(h);
                                    int int_s =Integer.parseInt(s);
                                    int int_h =Integer.parseInt(h);
                                    BleBaseDataForBlood.getBloodInstance().sendStandardBloodData(int_h, int_s);
                                    dialog.dismiss();

                                }else {
//                                   Toast.makeText(EditPersionInfoActivity.this, "请输入正确范围，舒张压40-100，收缩压90-180", Toast.LENGTH_LONG).show();
//                                    Toast toast=Toast.makeText(EditPersionInfoActivity.this, "请输入正确范围，舒张压40-100，收缩压90-180", Toast.LENGTH_SHORT);
//                                    toast.setGravity(Gravity.CENTER, 0, 0);
//                                    toast.show();
                                    Toast toast=Toast.makeText(EditPersionInfoActivity.this,R.string.toask_mark , Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();

                                }
                            }else {
//                               Toast.makeText(EditPersionInfoActivity.this, "请输入正确范围，舒张压40-100，收缩压90-180", Toast.LENGTH_LONG).show();
//                                    Toast toast=Toast.makeText(EditPersionInfoActivity.this, "请输入正确范围，舒张压40-100，收缩压90-180", Toast.LENGTH_SHORT);
//                                    toast.setGravity(Gravity.CENTER, 0, 0);
//                                    toast.show();
                                Toast toast=Toast.makeText(EditPersionInfoActivity.this,R.string.toask_mark , Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        }else {
//                           Toast.makeText(EditPersionInfoActivity.this, "请输入正确范围，舒张压40-100，收缩压90-180", Toast.LENGTH_LONG).show();
//                                    Toast toast=Toast.makeText(EditPersionInfoActivity.this, "请输入正确范围，舒张压40-100，收缩压90-180", Toast.LENGTH_SHORT);
//                                    toast.setGravity(Gravity.CENTER, 0, 0);
//                                    toast.show();
                            Toast toast=Toast.makeText(EditPersionInfoActivity.this,R.string.toask_mark , Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                });

                break;
        }








    }






    class changPWDTask extends AsyncTask<String, Void, Boolean> {
        JSONObject json;
        @Override
        protected Boolean doInBackground(String... params) {

            try {
                String re = connectNET(params[0], params[1]);
                json = new JSONObject(re);
                String code = json.getString("code");
                if (code != null && code.equals("9003"))
                {
                    LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).writeSp(MyConfingInfo.USER_PASSWORD, params[1]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            CircleProgressDialog.getInstance().closeCircleProgressDialog();
            if (json != null) {
                String code = null;
                try {
                    code = json.getString("code");
                    String msg = json.getString("msg");
                    if (code != null && code.equals("9003")) {
                        MyToastUitls.showToast(EditPersionInfoActivity.this, R.string.pwd_change_success, 1);
                        if (changePWD != null && changePWD.isShowing()) {
                            changePWD.dismiss();
                        }
                    } else if (code != null && code.equals("300")) {
                        MyToastUitls.showToast(EditPersionInfoActivity.this, R.string.pwd_failed_the_same, 1);
                    } else if (code != null && code.equals("301")) {
                        MyToastUitls.showToast(EditPersionInfoActivity.this, R.string.pwd_failed_diff, 1);
                    } else if(code != null && code.equals("9004"))
                    {
                        MyToastUitls.showToast(EditPersionInfoActivity.this, R.string.old_pwd_not_null, 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean checkTheInput(EditPersionInfoActivity editPersionInfoActivity, EditText oldPWD, EditText newPWD, EditText confirm) {
        Editable a = oldPWD.getText();
        Editable b = newPWD.getText();
        String oldP = a.toString();
        String newP = b.toString();
        Editable c = confirm.getText();
        String confirmP = c.toString();

        if (oldP == null || oldP.equals("") || oldP.length() < 6 || oldP.length() > 32) {
            oldPWD.requestFocus();
            MyToastUitls.showToast(editPersionInfoActivity, R.string.old_pwd_not_null, 1);
            return false;
        }
        if (newP == null || newP.equals("") || newP.length() < 6 || newP.length() > 32) {
            newPWD.requestFocus();
            String msg = getResources().getString(R.string.new_pwd_enter) + getResources().getString(R.string.error_invalid_password);
            MyToastUitls.showToastInString(editPersionInfoActivity, msg, 1);
            return false;
        }
        if (confirmP == null || confirmP.equals("") || confirmP.length() < 6 || confirmP.length() > 32) {
            confirm.requestFocus();
            String msg = getResources().getString(R.string.confirm_the_pwd) + getResources().getString(R.string.error_invalid_password);
            MyToastUitls.showToastInString(editPersionInfoActivity, msg, 1);
            return false;
        }
        if (!newP.equals(confirmP)) {
            MyToastUitls.showToast(editPersionInfoActivity, R.string.double_pwd_not, 1);
            confirm.requestFocus();
            return false;
        }
        return true;
    }

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
        mPopupWindow = new PopupWindow(dataPick, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setAnimationStyle(R.style.mypopupwindow_anim_style);
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // 设置焦点
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.showAtLocation(dataPick, Gravity.CENTER_HORIZONTAL, 0, 0);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mPopupWindow = null;
            }
        });
    }

    private EditorPhotoUtils editorPhotoUtil;

    /**
     * 监听dialog的item
     */
    private DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            editorPhotoUtil = new EditorPhotoUtils(EditPersionInfoActivity.this, takePortrait);
            switch (which) {
                case 0:         // 拍照
                    editorPhotoUtil.onTakePhotoChosen();
                    break;
                case 1:         // 从相册选择
                    editorPhotoUtil.onPickFromGalleryChosen();
                    break;
                case 2:
                    dialog.dismiss();
                    break;
            }

        }
    };
    private Bitmap mBitmap = null;

    /**
     * startActivityforResult,通过调用此方法回传数据
     *
     * @param requestCode // 响应码
     * @param resultCode  // 结果码
     * @param data        // 数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
//        mBitmap = data.getParcelableExtra("data");
//        Log.i(TAG, "拍照结果:" + requestCode + "--" + mBitmap);
        editorPhotoUtil.setTakePhotoListener(new EditorPhotoUtils.takePhotoCallback()
        {
            @Override
            public void getBitmap(Bitmap bitmap, String headName)
            {
                mBitmap = bitmap;
                modifyPhotoPath = headName;
            }
        });
        editorPhotoUtil.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 处理监听事件
     */
    private NoDoubleClickListener myListener = new NoDoubleClickListener() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onNoDoubleClick(View v) {
            switch (v.getId()) {   // 点击头像,弹出对话框
                case R.id.take_portrait:
                    AlertDialog.Builder dialog = new AlertDialog.Builder(EditPersionInfoActivity.this);
                    dialog.setItems(new String[]{getString(R.string.take_photo_action),
                            getString(R.string.selecte_from_local),
                            getString(R.string.cancle)}, dialogListener);
                    dialog.setCancelable(false);
                    dialog.create().show();
                    break;
                case R.id.fab_ok:
//                    String unit = LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(MyConfingInfo.METRICORINCH);
//                    if(unit != null && unit.equals(MyConfingInfo.INCH))
//                    {
//                        int hei = Integer.parseInt(heightN);
//                        int wei = Integer.parseInt(weightN);
//                        MetricAndBritish units = new MetricAndBritish();
//                        int heig = units.BritishToMetricInInch(hei);
//                        int weig = units.BritishToMetricInlb(wei);
//                        heightN = String.valueOf(heig);
//                        weightN = String.valueOf(weig);
//                        textKG.setText("lb");
//                        textCM.setText("inch");
//                        editKG.setText("lb");
//                        editCM.setText("inch");
//                    }

                    if (checkTheInputContent()) {
                        showBMIWindow(getBMILayout());
                    }

                    break;

                case R.id.fab_cancle:
                    if (whereFrom != null && whereFrom.equals(MyConfingInfo.FORM_VIEW_DATA)) {
                        if (Build.VERSION.SDK_INT < 21) {
                            editPersionInfoLayout.setVisibility(View.INVISIBLE);
                        } else {
                            // previously visible view  控件隐藏动画
                            //                    final View myView = findViewById(R.id.my_view);
                            // get the center for the clipping circle
                            // 获取园的中心
                            int cx = editPersionInfoLayout.getWidth();
                            int cy = 0;
                            // get the initial radius for the clipping circle
                            // 获取园的半径
                            int initialRadius = editPersionInfoLayout.getWidth();
                            // create the animation (the final radius is zero)
                            Animator anim =
                                    ViewAnimationUtils.createCircularReveal(editPersionInfoLayout, cx, cy, initialRadius, 0);

                            // make the view invisible when the animation is done
                            anim.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    editPersionInfoLayout.setVisibility(View.INVISIBLE);
                                }
                            });

                            // start the animation
                            anim.start();

                            //                    showPersionInfoLayout.setVisibility(View.VISIBLE);
                        }
                        checkMetric();
                    } else {
                        EditPersionInfoActivity.this.onBackPressed();
                    }
                    break;
                case R.id.show_portrait:
                    showCirclePhoto();
                    break;
            }
        }
    };

    private void showCirclePhoto()
    {
        View view = getPhotoLayout();
        final PopupWindow photoWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        photoWindow.setBackgroundDrawable(new BitmapDrawable());
        photoWindow.setFocusable(true);
        photoWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        photoWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }

    private View getPhotoLayout()
    {
        View view = LayoutInflater.from(EditPersionInfoActivity.this).inflate(R.layout.photo_show_layout, null, false);
        ImageView v = (ImageView)view.findViewById(R.id.photo_location);
        Bitmap bitmap = SaveImageUtil.readImageFormSD(SDPathUtils.getSdcardPath() + MyConfingInfo.IMAGEVIEW_SAVE_PATH);
        if(bitmap == null)
        {
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.head_portrait);
        }
        DisplayMetrics metrics = EditPersionInfoActivity.this.getResources().getDisplayMetrics();
        LinearLayout.LayoutParams pa = (LinearLayout.LayoutParams) v.getLayoutParams();
        float h = bitmap.getHeight();
        float w = bitmap.getWidth();
        float scale = h / w;
        pa.width = metrics.widthPixels;
        pa.height = (int)(metrics.widthPixels * scale);
        v.setLayoutParams(pa);
        v.setBackground(showPortran.getDrawable());
        return view;
    }


    private void doLoginAction()
    {
        Intent mIntent = new Intent();
        String deviceType = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.USER_DEVICE_TYPE);
        if(deviceType != null && deviceType.equals(""))
        {
            mIntent.setClass(getApplicationContext(), Device_List_Activity.class);
        }
        else if(deviceType != null && deviceType.equals(MyConfingInfo.DEVICE_HR))
        {
            mIntent.setClass(getApplicationContext(), MainActivity.class);
        }
        else if(deviceType != null && deviceType.equals(MyConfingInfo.DEVICE_BLOOD))
        {
            mIntent.setClass(getApplicationContext(), MainActivity.class);
        }
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mIntent);
        EditPersionInfoActivity.this.finish();
        sendBroadcast(new Intent(MyConfingInfo.CLOSE_OTHER_PAGER));
    }

    private void dealTheInput() {
        String whereFrom = getIntent().getStringExtra(MyConfingInfo.WHERE_ARE_YOU_FORM);
        switch (whereFrom) {
            case MyConfingInfo.FORM_REGIST:
                if (!NetStatus.isNetWorkConnected(EditPersionInfoActivity.this)) {
                    MyToastUitls.showToast(EditPersionInfoActivity.this, R.string.network_disconnect, 1);
                    return;
                }
                if (mBitmap != null) {
                    saveImageFromBm(mBitmap, SDPathUtils.getSdcardPath() + MyConfingInfo.IMAGEVIEW_SAVE_PATH);
                }
                CircleProgressDialog.getInstance().showCircleProgressDialog(EditPersionInfoActivity.this);
                registNow(birthdayN, nickN, heightN, weightN);
                break;
            case MyConfingInfo.FORM_DIRECTLY_USE:
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(MyConfingInfo.TYPE, MyConfingInfo.DIRCET_TYPE);
                    jsonObject.put(MyConfingInfo.ACCOUNT, "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).writeSp(MyConfingInfo.USER_ACCOUNT, jsonObject.toString());
                savePersionToSP(nickN, birthdayN, heightN, weightN, gender);
                if (mBitmap != null) {
                    saveImageFromBm(mBitmap, SDPathUtils.getSdcardPath() + MyConfingInfo.IMAGEVIEW_SAVE_PATH);
                }
                doLoginAction();
//                Intent intent = new Intent();
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setClass(EditPersionInfoActivity.this, MainActivity.class);
//                startActivity(intent);
//                EditPersionInfoActivity.this.finish();
//                sendBroadcast(new Intent(MyConfingInfo.CLOSE_OTHER_PAGER));
                break;
            case MyConfingInfo.FORM_VIEW_DATA:

                ProgressUtils.getInstance().showProgressDialog(EditPersionInfoActivity.this, getString(R.string.modify_persion_info));
//                boolean hasLogin = getIntent().getBooleanExtra(MyConfingInfo.HASLOGIN, false);
                String useAcc = UserAccountUtil.getAccount(EditPersionInfoActivity.this);
                String usePwd = LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(MyConfingInfo.USER_PASSWORD);
                String useEmail = LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(MyConfingInfo.USER_EMAIL);
                Log.i(TAG, "当前用户名和密码:" + useAcc + "--" + usePwd);
                if (checkTheAccountAndPWD(useAcc, usePwd)) // 用户是登录状态
                {
                    if (NetStatus.isNetWorkConnected(EditPersionInfoActivity.this))
                    {
                        String loginType = UserAccountUtil.getType(EditPersionInfoActivity.this);
                        switch (loginType)
                        {
                            case MyConfingInfo.QQ_TYPE:
                            case MyConfingInfo.WEICHART_TYPE:
                            case MyConfingInfo.FACEBOOK_TYPE:
                                checkThirdPartyTask chekcThird = new checkThirdPartyTask(EditPersionInfoActivity.this);
                                chekcThird.setOnLoginBackListener(new OnAllLoginBack() {
                                    @Override
                                    public void onLoginBack(String result)
                                    {
                                        midifyNow(birthdayN, nickN, heightN, weightN);
                                    }
                                });
                                chekcThird.execute(useAcc, loginType, null, null, null);
                                break;
                            case MyConfingInfo.NOMAL_TYPE:
                                new MyModifyTask().execute(new String[]{useAcc, usePwd, useEmail});
                                break;
                        }

                    } else {
                        if (ProgressUtils.getInstance().isShowing())
                        {
                            ProgressUtils.getInstance().closeProgressDialog();
                        }
                        MyToastUitls.showToast(EditPersionInfoActivity.this, R.string.network_disconnect, 1);
                    }
                }
                else    // 用户是直接使用，本地存储即可
                {
                    Log.i(TAG, "转换前后:" + heightN);
                    updatePersionInfoaa(useEmail, birthdayN, nickN, heightN, weightN, gender + "");
                }
                break;
            case MyConfingInfo.FORM_THIRD_LOGIN:
                checkThirdPartyTask thirdTask = new checkThirdPartyTask();
                thirdTask.setOnCheckLoginBack(new checkThirdPartyTask.OnCheckLoginBack()
                {
                    @Override
                    public void onThirdLoginFirstBack(String code, String autoAccount)
                    {
                        midifyNow(birthdayN, nickN, heightN, weightN);
                    }
                    @Override
                    public void onThirdLoginNoFirstBack(String data)
                    {
                        // 不会进到这里
                    }
                });
                thirdTask.execute(thirdPartyLoginInfoEntity.getUserId(), thirdPartyLoginInfoEntity.getType(), null, null, null);
                break;
        }
    }

    private boolean checkTheAccountAndPWD(String acc, String pwd)
    {
        return (acc != null && !acc.equals(""));
    }


    class MyModifyTask extends AsyncTask<String, Void, Boolean> {
        String emaill = null;

        @Override
        protected Boolean doInBackground(String... params) {
            emaill = params[2];
            boolean isLogOk = false;
            try {
                isLogOk = new Sub_login(EditPersionInfoActivity.this).requestLogin(params[0], params[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ;
            Log.i(TAG, "登陆:" + isLogOk);
            return isLogOk;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                Log.i(TAG, "修改用户资料++");
                midifyNow(birthdayN, nickN, heightN, weightN);
            } else {
                MyToastUitls.showToast(EditPersionInfoActivity.this, R.string.network_disconnect, 1);
                if (ProgressUtils.getInstance().isShowing()) {
                    ProgressUtils.getInstance().closeProgressDialog();
                }
            }
        }
    }


    private void updatePersionInfoaa(String emailN, String birthdayN, String nickN, String heightN, String weightN, String gender) {
        LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).writeSp(MyConfingInfo.USER_EMAIL, emailN);
        LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).writeSp(MyConfingInfo.USER_BIRTHDAY, birthdayN);
        LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).writeSp(MyConfingInfo.USER_NICK, nickN);
        LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).writeSp(MyConfingInfo.USER_HEIGHT, heightN);
        LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).writeSp(MyConfingInfo.USER_WEIGHT, weightN);
        LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).writeSp(MyConfingInfo.USER_GENDER, gender);
        updatePersionInfo(nickN, birthdayN, heightN, weightN, Integer.parseInt(gender));
        if (mBitmap != null) {
            saveImageFromBm(mBitmap, SDPathUtils.getSdcardPath() + MyConfingInfo.IMAGEVIEW_SAVE_PATH);
        }
        if (mBitmap != null) {
            showPortran.setImageBitmap(mBitmap);
        }
        MyToastUitls.showToast(EditPersionInfoActivity.this, R.string.modify_persion_ok, 1);
        // 隐藏编辑个人信息布局
        if (Build.VERSION.SDK_INT < 21 && Build.VERSION.SDK_INT >= 18) {
            editPersionInfoLayout.setVisibility(View.INVISIBLE);
            showPersionInfoLayout.setVisibility(View.VISIBLE);
        } else {
            inVisibaleCurrentLayout();
        }

        if (ProgressUtils.getInstance().isShowing()) {
            ProgressUtils.getInstance().closeProgressDialog();
        }
    }

    /**
     * 保存照片到sd卡
     *
     * @param mPhoto
     * @param imageviewSavePath
     */
    private void saveImageFromBm(Bitmap mPhoto, String imageviewSavePath) {
        Log.e(TAG, "头像的存储路径：" + imageviewSavePath);
        try {
            // 缓冲输出流，需传一个文件输出流
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imageviewSavePath, false));
            // 压缩图片
            // 参数一：存储照片的格式，参数二：照片是否压缩，参数三：输出流
            mPhoto.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 修改用户资料
     *
     * @param birthdayN
     * @param nickN
     * @param heightN
     * @param weightN
     */
    private void midifyNow(String birthdayN, String nickN, String heightN, String weightN) {
        boolean hasImage = false;
        String path = null;
        File file = null;
        // 判断是否有图片
        if(modifyPhotoPath != null)
        {
            path = modifyPhotoPath;
            file = new File(modifyPhotoPath);
            hasImage = true;
        }
        else
        {
            file = new File(SDPathUtils.getSdcardPath() + MyConfingInfo.IMAGEVIEW_SAVE_PATH);
            if (file.exists()) {
                hasImage = true;
                path = SDPathUtils.getSdcardPath() + MyConfingInfo.IMAGEVIEW_SAVE_PATH;
            }
        }
        modifyAsync asy = new modifyAsync(birthdayN, nickN, heightN, weightN, path, file, hasImage);
        asy.execute((Void) null);
    }


    /**
     * 修改资料异步任务
     */
    public class modifyAsync extends AsyncTask<Void, Void, Boolean> {
        private String birthdayM, nickM, heightM, weightM, pathM;
        private boolean hasImage;
        private File mFile;
        private HashMap<String, String> dataMap;    // 字符形内容map
        private HashMap<String[], File> fileMap;    // 文件型内容的map
        private String[] mm = new String[3];

        public modifyAsync(String birthdayM, String nickM,
                           String heightM, String weightM, String path,
                           File mFile, boolean hasImage) {
            this.birthdayM = birthdayM;
            this.nickM = nickM;
            this.heightM = heightM;
            this.weightM = weightM;
            this.pathM = path;
            this.hasImage = hasImage;
            this.mFile = mFile;
            dataMap = new HashMap<String, String>();
            dataMap.put(NICK, nickM);
            dataMap.put(BIRTHDAY, birthdayM);
            dataMap.put(GENDER, gender + "");
            dataMap.put(HEIGHT, heightM);
            dataMap.put(WEIGHT, weightM);
            mm[0] = path;
            mm[1] = "image/jpeg";
            mm[2] = HEADER;
            fileMap = new HashMap<String[], File>();
            fileMap.put(mm, mFile);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String u = MyConfingInfo.WebRequeModify;
            try {
                String cookie = LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(MyConfingInfo.COOKIE_FOR_ME);
                Log.i(TAG, "保存的cookie:" + cookie);
                HttpUtil.postDataAndImageHaveCookie(hasImage, u, dataMap, fileMap, cookie, new HttpUtil.DealResponse() {
                    @Override
                    public boolean dealResponse(int responseCode, InputStream input) throws IOException {
                        String result = "";
                        InputStreamReader re = new InputStreamReader(input);
                        BufferedReader reader = new BufferedReader(re);
                        String c = null;
                        while ((c = reader.readLine()) != null)
                        {
                            result = result + c;
                        }
                        Log.i(TAG, "修改资料的结果：" + result);
                        parseTheRequest(result, nickM, birthdayM, heightM, weightM);
                        return false;
                    }

                    @Override
                    public void setHeader(String url, Object obj) {
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (mBitmap != null) {
                saveImageFromBm(mBitmap, SDPathUtils.getSdcardPath() + MyConfingInfo.IMAGEVIEW_SAVE_PATH);
                showPortran.setImageBitmap(mBitmap);
            }
            if (ProgressUtils.getInstance().isShowing()) {
                ProgressUtils.getInstance().closeProgressDialog();
            }
        }
    }

    private void parseTheRequest(String result, final String nickM, final String birthdayM,
                                 String heightM, String weightM)
    {
        JSONObject json = null;
        try {
            json = new JSONObject(result);
            String code = json.getString("code");
            if (code.equals("9003"))
            {

                if (BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice())
                {
                    String unit = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.METRICORINCH);
                    boolean is24 = DateFormat.is24HourFormat(getApplicationContext());
                    BleDataForSettingArgs.getInstance(EditPersionInfoActivity.this.getApplicationContext()).setArgs(unit, is24);
                }
                LocalDataSaveTool.getInstance(EditPersionInfoActivity.this.getApplicationContext()).writeSp(MyConfingInfo.PERSION_HAS_CHANGER, "1");
                // 存储个人信息
                savePersionToSP(nickM, birthdayM, heightM, weightM, gender);
                // 更新个人信息布局
                doNext(nickM, birthdayM, heightM, weightM);
            }
            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LocalDataSaveTool.getInstance(EditPersionInfoActivity.this.getApplicationContext()).writeSp(MyConfingInfo.PERSION_HAS_CHANGER, "0");
                        MyToastUitls.showToast(EditPersionInfoActivity.this.getApplicationContext(), R.string.modify_persion_failed, 2);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改资料或者第三方登录编辑完成执行此方法
     * @param nickM
     * @param birthdayM
     * @param heightM
     * @param weightM
     */
    private void doNext(final String nickM, final String birthdayM, final String heightM, final String weightM)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {

                String hei = heightM;
                String wei = weightM;
                String where = getIntent().getStringExtra(MyConfingInfo.WHERE_ARE_YOU_FORM);
//                Log.i(TAG, "第三方登录判定是否是第三方登录" + where);
                if(where != null && where.equals(MyConfingInfo.FORM_VIEW_DATA))
                {
                    String unit = LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(MyConfingInfo.METRICORINCH);
                    if (unit != null && unit.equals(MyConfingInfo.INCH)) {
//                        MetricAndBritish units = new MetricAndBritish();
//                        int heig = units.MetricToBritishInCm(Integer.parseInt(hei));
//                        int weig = units.MetricToBritishInKg(Integer.parseInt(wei));
//                        hei = String.valueOf(heig);
//                        wei = String.valueOf(weig);
                        textKG.setText(getString(R.string.lb_Imperial));
                        textCM.setText(getString(R.string.inch_Imperial));
                        editKG.setText(getString(R.string.lb_Imperial));
                        editCM.setText(getString(R.string.inch_Imperial));
                    }
                    updatePersionInfo(nickM, birthdayM, hei, wei, gender);
                    MyToastUitls.showToast(EditPersionInfoActivity.this, R.string.modify_persion_ok, 2);
                    // 隐藏编辑个人信息布局
                    if (Build.VERSION.SDK_INT < 21 && Build.VERSION.SDK_INT >= 18) {
                        editPersionInfoLayout.setVisibility(View.INVISIBLE);
                        showPersionInfoLayout.setVisibility(View.VISIBLE);
                    } else {
                        inVisibaleCurrentLayout();
                    }
                }
                else if(where != null && where.equals(MyConfingInfo.FORM_THIRD_LOGIN))
                {
//                    Log.i(TAG, "第三方登录是第三方登录跳转到主页");
                    JSONObject json = new JSONObject();
                    try {
                        json.put(MyConfingInfo.TYPE, thirdPartyLoginInfoEntity.getType());
                        json.put(MyConfingInfo.ACCOUNT, thirdPartyLoginInfoEntity.getUserId() + ";" + thirdPartyLoginInfoEntity.getAccount());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "保存的账号信息：" + json.toString());
                    // 保存userid
                    LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).writeSp(MyConfingInfo.USER_ACCOUNT, json.toString());
                    MyToastUitls.showToast(EditPersionInfoActivity.this, R.string.login_success, 2);
                    doLoginAction();
//                    Intent intent = new Intent();
//                    intent.setClass(EditPersionInfoActivity.this, MainActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    sendBroadcast(new Intent(MyConfingInfo.CLOSE_OTHER_PAGER));
                }
            }
        });
    }


    /**
     * 隐藏编辑个人信息布局
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void inVisibaleCurrentLayout() {
        int cxx = editPersionInfoLayout.getWidth();
        int cyy = 0;

        // get the initial radius for the clipping circle
        // 获取园的半径
        int initialRadiuss = editPersionInfoLayout.getHeight();

        // create the animation (the final radius is zero)
        Animator anims =
                ViewAnimationUtils.createCircularReveal(editPersionInfoLayout, cxx, cyy, initialRadiuss, 0);

        // make the view invisible when the animation is done
        anims.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                editPersionInfoLayout.setVisibility(View.INVISIBLE);


            }

        });

        // start the animation
        anims.start();

        showPersionInfoLayout.setVisibility(View.VISIBLE);

    }

    /**
     * 修改资料后更新个人信息
     *
     * @param nickM
     * @param birthdayM
     * @param heightM
     * @param weightM
     * @param gender
     */
    private void updatePersionInfo(String nickM, String birthdayM,
                                   String heightM, String weightM, int gender) {
        // 更新头像
        Bitmap bitmap = SaveImageUtil.readImageFormSD(SDPathUtils.getSdcardPath() + MyConfingInfo.IMAGEVIEW_SAVE_PATH);
        showPortran.setImageBitmap(bitmap);

        // 更新其他资料

        String unit = LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(MyConfingInfo.METRICORINCH);
        if (unit != null && unit.equals(MyConfingInfo.INCH)) {
            int hei = Integer.parseInt(heightM);
            int wei = Integer.parseInt(weightM);
            MetricAndBritish units = new MetricAndBritish();
            int heig = units.MetricToBritishInCm(hei);
            int weig = units.MetricToBritishInKg(wei);
            heightM = String.valueOf(heig);
            weightM = String.valueOf(weig);
            radioButtonInln.setChecked(true);
            inlnButton.setChecked(true);
        } else {
            radioButtonMetric.setChecked(true);
            metricButton.setChecked(true);
        }

        showText(nickS, nickM);
        showText(birthdayS, birthdayM);
        showText(heightS, heightM);
        showText(weightS, weightM);
        String genderM = LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(MyConfingInfo.USER_GENDER);
        if (genderM.equals("1")) {
            showMale.setChecked(true);
        } else {
            showFemale.setChecked(true);
        }
    }


    /**
     * 获取并校验输入内容
     */
    private boolean checkTheInputContent() {
        nickN = nickName.getText().toString();       // 昵称
        birthdayN = birthday.getText().toString();   // birthday
        heightN = height.getText().toString();       // 身高
        weightN = weight.getText().toString();       // 体重

//        int checkedid = group.getCheckedRadioButtonId();
//        Log.i(TAG, "点击的id" + checkedid);
        boolean empty = false;
        View thisView = null;
//        ArrayList<EditText> list = new ArrayList<EditText>();
//        list.add(nickName);
//        list.add(height);
//        list.add(weight);
//        for (int i = 0; i < list.size(); i++) {
//            EditText editText = list.get(i);
//            String info = editText.getText().toString();
//            // 非空验证
//            if (info.isEmpty()) {
//                editText.setError(getString(R.string.not_null));
//                empty = true;
//                thisView = editText;
//            }
//        }
        if(!chechNeckName(nickN))
        {
            nickName.setError(EditPersionInfoActivity.this.getString(R.string.nick_name_short_or_long));
            empty = true;
            thisView = nickName;
            if(!checkContinueOrClose(empty, thisView))
            {
                return false;
            }
        }

        if (birthdayN != null && birthdayN.isEmpty())
        {
            birthday.setError(getString(R.string.not_null));
            empty = true;
            thisView = birthday;
            if(!checkContinueOrClose(empty, thisView))
            {
                return false;
            }
        }

        // 判断身高
        String unit = LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(MyConfingInfo.METRICORINCH);
        if (unit != null && unit.equals(MyConfingInfo.INCH)) {
            if ((heightN != null && heightN.isEmpty()) || Integer.parseInt(heightN) < 16 || Integer.parseInt(heightN) > 98) {
                height.setError(getString(R.string.height_wrong));
                empty = true;
                thisView = height;
                if(!checkContinueOrClose(empty, thisView))
                {
                    return false;
                }
            } else {
                heightN = String.valueOf(new MetricAndBritish().BritishToMetricInInch(Integer.parseInt(heightN)));
            }
            if ((weightN != null && weightN.isEmpty()) || Integer.parseInt(weightN) < 66 || Integer.parseInt(weightN) > 375) {
                weight.setError(getString(R.string.weight_wrong));
                empty = true;
                thisView = weight;
                if(!checkContinueOrClose(empty, thisView))
                {
                    return false;
                }
            } else {
                weightN = String.valueOf(new MetricAndBritish().BritishToMetricInlb(Integer.parseInt(weightN)));
            }
        } else {
            if ((heightN != null && heightN.isEmpty()) || Integer.parseInt(heightN) < 40 || Integer.parseInt(heightN) > 250) {
                height.setError(getString(R.string.height_wrong));
                empty = true;
                thisView = height;
                if(!checkContinueOrClose(empty, thisView))
                {
                    return false;
                }
            }
            if ((weightN != null && weightN.isEmpty()) || Integer.parseInt(weightN) < 30 || Integer.parseInt(weightN) > 170) {
                weight.setError(getString(R.string.weight_wrong));
                empty = true;
                thisView = weight;
                if(!checkContinueOrClose(empty, thisView))
                {
                    return false;
                }
            }
        }
        Log.i(TAG, "--------检查完毕-------");
        // 存入缓存
//        savePersionToSP(nickN, emailN, birthdayN, heightN, weightN, gender, accounta, pwdd);
        return true;
    }

    private boolean checkContinueOrClose(boolean empty, View thisView)
    {
        if(empty)
        {
            thisView.requestFocus();
            return false;
        }
        return true;
    }

    private boolean chechNeckName(String nickN)
    {
        return nickN.length() <= 32 && nickN.length() > 0;
    }

    private boolean registNow(String birthdayN, String nickN, String heightN, String weightN) {
        boolean haveImage = false;      // 标识是否有图片
        accounta = getIntent().getStringExtra(MyConfingInfo.USER_ACCOUNT);  // intent传递过来的账号和密码
        pwdd = getIntent().getStringExtra(MyConfingInfo.USER_PASSWORD);
        emailes = getIntent().getStringExtra(MyConfingInfo.USER_EMAIL);

//            从内存卡读取图片
        String path = null;
        File file = new File(SDPathUtils.getSdcardPath() + MyConfingInfo.IMAGEVIEW_SAVE_PATH);
        if (file.exists())   // 判断有无图片
        {
            path = SDPathUtils.getSdcardPath() + MyConfingInfo.IMAGEVIEW_SAVE_PATH;
            haveImage = true;
        }
        // 执行异步注册
        Log.i(TAG, "--------执行异步任务-------");

        String pwd = WebGlobalConfig.get32MD5Str(pwdd); // MD5加密
        RegistTask task = new RegistTask(accounta, pwd, emailes, birthdayN, nickN, heightN, weightN, gender + "", path, file, haveImage);
        task.execute();
        return false;
    }


    /**
     * 读取图片
     *
     * @param file
     */
    private Bitmap readBitmapFromSd(File file) {
        Bitmap mBitmap = BitmapFactory.decodeFile(String.valueOf(file));
        return mBitmap;
    }




    private void login_Now(String accounta, String pwdd) {
        boolean isLoginSuccess = false;
        Sub_login toLogin = new Sub_login(EditPersionInfoActivity.this);
        try {
            isLoginSuccess = toLogin.requestLogin(accounta, pwdd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isLoginSuccess) {
            saveTheAccount(accounta);
            Bitmap mBitmapL = toLogin.getImageIcon();
            if (mBitmapL != null)
                SaveImageUtil.saveImageToSD(mBitmapL, SDPathUtils.getSdcardPath() + MyConfingInfo.SDCARD_DATA_ROOT_DIR, File.separator + "tmp.jpeg");
            JSONObject json = null;
            try {
                json = new JSONObject();
                json.put(MyConfingInfo.TYPE, MyConfingInfo.NOMAL_TYPE);
                json.put(MyConfingInfo.ACCOUNT, accounta);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).writeSp(MyConfingInfo.USER_ACCOUNT, json.toString());
            LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).writeSp(MyConfingInfo.USER_PASSWORD, pwdd);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    doLoginAction();
//                    Intent intent = new Intent();
//                    intent.setClass(EditPersionInfoActivity.this, MainActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    EditPersionInfoActivity.this.onBackPressed();
//                    sendBroadcast(new Intent(MyConfingInfo.CLOSE_OTHER_PAGER));
                }
            });
        }
    }

    // 先查询数据库有无此账号，没有则添加
    private void saveTheAccount(String mAccount) {
        Cursor accountCurson = MyDBHelperForDayData.getInstance(EditPersionInfoActivity.this).selectAccount(EditPersionInfoActivity.this);
        ArrayList<String> acc = parserTheAccount(accountCurson);
        if (!acc.contains(mAccount)) {
            MyDBHelperForDayData.getInstance(EditPersionInfoActivity.this).insertAccount(EditPersionInfoActivity.this, mAccount);
        }
    }

    private ArrayList<String> parserTheAccount(Cursor accountCurson) {
        ArrayList<String> account = new ArrayList<>();
        if (accountCurson.getCount() > 0) {
            if (accountCurson.moveToFirst()) {
                do {
                    String accountS = accountCurson.getString(accountCurson.getColumnIndex("account"));
                    if (!account.contains(accountS)) {
                        account.add(accountS);
                    }
                } while (accountCurson.moveToNext());
            }
        }
        return account;
    }

    /**
     * 异步发送注册数据
     */
    class RegistTask extends AsyncTask<Void, Void, Boolean> {
        public final String TAG = EditPersionInfoActivity.class.getSimpleName();
        private HashMap<String, String> hashMap;
        private HashMap<String[], File> fileMap;
        String[] hh = new String[3];


        String content;
        boolean haveImage = false;


        public RegistTask(String account, String password,
                          String email, String birthday,
                          String nickName, String height,
                          String weight, String gender,
                          String path, File mFile, boolean haveImage) {
            this.haveImage = haveImage;
            hashMap = new HashMap<String, String>();
            hashMap.put(ACCOUNT, account);
            hashMap.put(EMAIL, email);
            hashMap.put(PASSWORD, password);
            hashMap.put(NICK, nickName);
            hashMap.put(BIRTHDAY, birthday);
            hashMap.put(GENDER, gender);
            hashMap.put(HEIGHT, height);
            hashMap.put(WEIGHT, weight);
            hh[0] = path;
            hh[1] = "image/jpeg";
            hh[2] = HEADER;
            fileMap = new HashMap<String[], File>();
            fileMap.put(hh, mFile);
            Log.i(TAG, "--------创建异步任务赋值-------- " + path + "是否:" + haveImage);
        }

        /**
         * 将Bitmap对象转换为byte数组
         *
         * @param icon
         */
        private String BitmapTobytesToBase64ToString(Bitmap icon) {
            if (icon != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                icon.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] bytes = bos.toByteArray();
                return Base64.encodeToString(bytes, Base64.DEFAULT);
            }
            return null;
        }


        HttpUtil.DealResponse deal = new HttpUtil.DealResponse() {
            @Override
            public boolean dealResponse(int responseCode, InputStream input) throws IOException {
                Log.i(TAG, "响应码：" + responseCode);
                InputStreamReader reader;
                if (input != null) {
                    String line = null;
                    String result = "";
                    reader = new InputStreamReader(input);
                    BufferedReader br = new BufferedReader(reader);
                    while (((line = br.readLine()) != null)) {
                        result = result + line;
                    }
                    Log.i(TAG, "注册返回结果" + result);
                    if (result != null && !result.equals("")) {
                        boolean isSuccess = false;
                        try {
                            isSuccess = parserJSON(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (isSuccess)// 注册成功
                        {
                            login_Now(accounta, pwdd);
                        }
                    } else {
                        MyToastUitls.showToast(EditPersionInfoActivity.this, R.string.regist_outtime, 1);
                    }
                }
                return false;
            }

            @Override
            public void setHeader(String url, Object obj) {
            }
        };

        /**
         * 解析服务器返回的ＪＳＯＮ数据
         *
         * @param result
         */
        private boolean parserJSON(String result) throws JSONException {
            CircleProgressDialog.getInstance().closeCircleProgressDialog();
            JSONObject json = new JSONObject(result);
            String code = json.getString("code");
            String msg = json.getString("msg");
            if (code.equals("9003")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyToastUitls.showToast(EditPersionInfoActivity.this, R.string.regist_ok, 1);
                    }
                });

                return true;
            } else if (code.equals("100")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyToastUitls.showToast(EditPersionInfoActivity.this, R.string.regist_already_have, 1);
                    }
                });

            } else if (code.equals("9002")) {
                Log.i(TAG, "返回吗:" + code);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        MyToastUitls.showToast(EditPersionInfoActivity.this, R.string.param_wrong, 1);
                    }
                });

            }
            return false;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.i(TAG, "--------执行doInBackground-------");
//            HttpUtil httpUtil = new HttpUtil();
            try {
                HttpUtil.postDataAndImage(EditPersionInfoActivity.this, haveImage, MyConfingInfo.WebRegist, hashMap, fileMap, deal);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }


    private void writeToSP(String key, String value) {
        LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).writeSp(key, value);
    }


    private void savePersionToSP(String nickN,
                                 String birthdayN, String heightN,
                                 String weightN, int gender) {
        writeToSP(MyConfingInfo.USER_NICK, nickN);
        writeToSP(MyConfingInfo.USER_BIRTHDAY, birthdayN);
        writeToSP(MyConfingInfo.USER_HEIGHT, heightN);
        writeToSP(MyConfingInfo.USER_WEIGHT, weightN);
        writeToSP(MyConfingInfo.USER_GENDER, gender + "");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_persion, menu);
        return true;
    }


    //---------------------------------------------------------------------------

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

    private WheelView year;
    private WheelView month;
    private WheelView day;
    private PopupWindow mPopupWindow;

    /**
     * @return
     */

    private View getDataPick(String bir) {
        int curYears = 0;
        int curMons = 0;
        int curDays = 0;
        if (bir != null && !bir.equals("")) {
            curYears = Integer.parseInt(bir.substring(0, 4));
            curMons = Integer.parseInt(bir.substring(5, 7));
            curDays = Integer.parseInt(bir.substring(8, 10));
        }
        Calendar c = Calendar.getInstance(TimeZone.getDefault());     // 日期对象
        int curYear = c.get(Calendar.YEAR);
        int curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
        int curDate = c.get(Calendar.DATE);
        final View view = LayoutInflater.from(EditPersionInfoActivity.this).inflate(R.layout.datapick, null);
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
                String str = ((year.getCurrentItem() + 1900) + "-" + mm + "-" + dd);
//                Toast.makeText(EditPersionInfoActivity.this, str, Toast.LENGTH_LONG).show();
                str = checkTheDate(str);
                birthday.setText(str);
                birthday.setError(null);
                mPopupWindow.dismiss();
            }
        });
        RelativeLayout cancel = (RelativeLayout) view.findViewById(R.id.cancle_select);
        cancel.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        return view;
    }

    private String checkTheDate(String str)
    {
        if(str == null)return str;
        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
        Date selectDate = null;
        try {
            selectDate = formate.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long selectMi = selectDate.getTime();
        Calendar calend = Calendar.getInstance();
        long currentMi = calend.getTimeInMillis();
        if(selectMi > currentMi)
        {
            str = formate.format(calend.getTime());
        }
        return str;
    }

    private void initMonth(int curYear, int curMonth) {
        month.setAdapter(new NumericWheelAdapter(1, getMonthData(curYear, curMonth), "%02d"));

    }

    private int getMonthData(int curYear, int curMonth) {
        int mo = 12;
        Calendar curr = Calendar.getInstance();
        int yearNow = curr.get(Calendar.YEAR);
        if (yearNow == curYear) {
            mo = curr.get(Calendar.MONTH) + 1;
        }
        Log.i(TAG, "时间: 参数年" + curYear + "获取年:" + yearNow + "mo" + mo);
        return mo;
    }

    /**
     */
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            String whereFrom = getIntent().getStringExtra(MyConfingInfo.WHERE_ARE_YOU_FORM);
            if (whereFrom.equals(MyConfingInfo.FORM_VIEW_DATA) && editPersionInfoLayout.getVisibility() == View.VISIBLE) {
                if (Build.VERSION.SDK_INT < 21) {
                    editPersionInfoLayout.setVisibility(View.INVISIBLE);
                } else {
                    // previously visible view  控件隐藏动画
//                    final View myView = findViewById(R.id.my_view);
                    // get the center for the clipping circle
                    // 获取园的中心
                    int cx = editPersionInfoLayout.getWidth();
                    int cy = 0;
                    // get the initial radius for the clipping circle
                    // 获取园的半径
                    int initialRadius = editPersionInfoLayout.getWidth();
                    // create the animation (the final radius is zero)
                    Animator anim =
                            ViewAnimationUtils.createCircularReveal(editPersionInfoLayout, cx, cy, initialRadius, 0);

                    // make the view invisible when the animation is done
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            editPersionInfoLayout.setVisibility(View.INVISIBLE);
                        }
                    });
                    // start the animation
                    anim.start();
//                    showPersionInfoLayout.setVisibility(View.VISIBLE);
                }
                // 检查用户是否改变公英制
                checkMetric();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void checkMetric() {
        String meOrLnNow = LocalDataSaveTool.getInstance(EditPersionInfoActivity.this).readSp(MyConfingInfo.METRICORINCH);
        if (meOrLn != null && meOrLn.equals(meOrLnNow))  // 没有改变
        {
//            do nothing
        } else {
            if (meOrLn != null && meOrLn.equals(MyConfingInfo.METRIC)) {
                changeToMetric();
            } else {
                changeToInch();
            }
        }
    }
}
