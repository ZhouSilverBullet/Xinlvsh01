package com.huichenghe.xinlvshuju.slide;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.huichenghe.xinlvshuju.CustomView.CircleProgressDialog;
import com.huichenghe.xinlvshuju.DataEntites.ThirdPartyLoginInfoEntity;
import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.SDPathUtils;
import com.huichenghe.xinlvshuju.mainpack.BaseActivity;
import com.huichenghe.xinlvshuju.mainpack.MainActivity;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;
import com.huichenghe.xinlvshuju.Utils.NetStatus;
import com.huichenghe.xinlvshuju.Utils.SaveImageUtil;
import com.huichenghe.xinlvshuju.http.IconTask;
import com.huichenghe.xinlvshuju.http.checkThirdPartyTask;
import com.huichenghe.xinlvshuju.zhy.utils.registActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
/**
 * 实现LoaderCallbacks 是为了异步查询联系人数据库，做自动补全操作
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements LoaderCallbacks<Cursor>
{
    public static final String TAG = LoginActivity.class.getSimpleName();
    private final int LOGIN_CANCEL = 0;
    private final int LOGIN_ERROR = 1;
    private final int LOGIN_COMPLETE = 2;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]
    {
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private ProgressBar mProgressView;
    private ScrollView mLoginFormView;
    private TextView forget_pwd;
    private RelativeLayout mLinearLayout;
    private ImageView back;
    private boolean isFirstLayout = true;
    private int whereAreYouFrom;
    private Button findPwd, mEmailSignInButton;
    private AutoCompleteTextView content;
    private ProgressDialog dialog;
    private ImageView seePwd;
    private boolean isHideFlag = false;
    private ArrayList<String> savedAccount;
    private TextView newUserRegist;
    private ImageView backTo;
    private ImageView qqLogin, weiChartLogin, facebookLogin;
    private checkThirdPartyTask checkTask;
    private closeRecever recever;

    // 缓存中的个人信息包括账号密码
    private String info;
    private Handler loginHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case LOGIN_CANCEL:
                    MyToastUitls.showToast(LoginActivity.this, R.string.grant_failed, 1);
                    CircleProgressDialog.getInstance().showCircleProgressDialog(LoginActivity.this);
                    break;
                case LOGIN_ERROR:
                    MyToastUitls.showToast(LoginActivity.this, R.string.grant_failed, 1);
                    CircleProgressDialog.getInstance().showCircleProgressDialog(LoginActivity.this);
                    break;
                case LOGIN_COMPLETE:
//                    MyToastUitls.showToast(LoginActivity.this, R.string.grant_success, 1);
                    Object[] objs = (Object[]) msg.obj;
                    String platformName = (String)objs[0];
                    HashMap<String, Object> res = (HashMap<String, Object>) objs[1];
                    showLoginInfo(platformName, res);
                    break;
            }
            super.handleMessage(msg);
        }
    };



    /**
     * 授权成功后的用户信息
     * @param res
     */
    private void showLoginInfo(String platformName, HashMap<String, Object> res)
    {
        if (platformName != null)
        {
            Platform platform = ShareSDK.getPlatform(platformName);
            PlatformDb dbInfo  = platform.getDb();
            final String useIcon = dbInfo.getUserIcon();
            final String userGender = dbInfo.getUserGender();
            final String userName = dbInfo.getUserName();
            final String userId = dbInfo.getUserId();
            String token = dbInfo.getToken();
            String ptName = dbInfo.getPlatformNname();
            final String[] type = new String[1];
            Log.i(TAG, "获取的授权信息：" + useIcon);
            Log.i(TAG, "获取的授权信息：" + userGender);
            Log.i(TAG, "获取的授权信息：" + userName);
            Log.i(TAG, "获取的授权信息：" + userId);
            Log.i(TAG, "获取的授权信息：" + token);
            Log.i(TAG, "获取的授权信息：" + ptName);

            MyToastUitls.showToast(LoginActivity.this, R.string.grant_success, 1);
            if(ptName.equals("QQ"))
            {
                type[0] = "2";
            }
            else if(ptName.equals("Wechat"))
            {
                type[0] = "1";
            }
            else if(ptName.equals("Facebook"))
            {
                type[0] = "3";
            }
            checkTask = new checkThirdPartyTask(LoginActivity.this);
            checkTask.setOnCheckLoginBack(new checkThirdPartyTask.OnCheckLoginBack() {
                @Override
                public void onThirdLoginFirstBack(String code, String autoAccount)
                {
                    CircleProgressDialog.getInstance().closeCircleProgressDialog();
                    dealTheThirdBackData(code, autoAccount, userId, type[0], useIcon, userName, userGender);
                }
                @Override
                public void onThirdLoginNoFirstBack(String data)
                {
                    CircleProgressDialog.getInstance().closeCircleProgressDialog();
                    dealNotFirstThirdLoginBack(data, useIcon, userId, type[0]);
                }
            });
            checkTask.execute(userId, type[0], useIcon, userGender, userName);
            loginHandler.postDelayed(runnable, 15000);
        }
    }
    Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            MyToastUitls.showToast(LoginActivity.this, R.string.login_outtime, 1);
            CircleProgressDialog.getInstance().closeCircleProgressDialog();
        }
    };

    private void dealNotFirstThirdLoginBack(String data, String useIcon, String userId, String type)
    {
        parseAllDataAndSave(data, useIcon, userId, type);
        // 进入主页
        doLoginAction();
//        Intent intent = new Intent();
//        intent.setClass(LoginActivity.this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        LoginActivity.this.finish();
//        sendBroadcast(new Intent(MyConfingInfo.CLOSE_OTHER_PAGER));
    }

    private void dealTheThirdBackData(String code, String autoAccount, String userId, String type,
                                      String useIcon, String userName, String userGender)
    {
        // 进入编辑资料页面
        showEditPersionPager(new ThirdPartyLoginInfoEntity(autoAccount, userId, type, useIcon, userName, userGender));
    }

    class closeRecever extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            switch (intent.getAction())
            {
                case MyConfingInfo.CLOSE_OTHER_PAGER:
                    LoginActivity.this.finish();
                    break;
            }

        }
    }



    /**
     * 用户注销后点击第三方登录，解析并保存
     * @param data
     */
    private void parseAllDataAndSave(String data, String usIcon, String userId, String type)
    {
        // 获取qq头像，这是从qq服务器获取，
        new IconTask().execute(usIcon);
        try {
            JSONObject obj = new JSONObject(data);
            String nick = obj.getString("nick");
            String birthdate = obj.getString("birthdate");
            String gender = obj.getString("gender");
            String height = obj.getString("height");
            String weight = obj.getString("weight");
            String account = obj.getString("account");
            saveSharePresce(MyConfingInfo.USER_NICK, nick);
            saveSharePresce(MyConfingInfo.USER_BIRTHDAY, birthdate);
            saveSharePresce(MyConfingInfo.USER_GENDER, gender);
            saveSharePresce(MyConfingInfo.USER_HEIGHT, height);
            saveSharePresce(MyConfingInfo.USER_WEIGHT, weight);
            JSONObject json = new JSONObject();
            json.put(MyConfingInfo.TYPE, type);
            json.put(MyConfingInfo.ACCOUNT, userId + ";" + account);
            saveSharePresce(MyConfingInfo.USER_ACCOUNT, json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveSharePresce(String key, String value)
    {
        LocalDataSaveTool.getInstance(LoginActivity.this).writeSp(key, value);
    }

    /**
     * 跳转到编辑资料页面
     */
    private void showEditPersionPager(ThirdPartyLoginInfoEntity info)
    {
        Intent intent = new Intent();
        intent.putExtra(MyConfingInfo.THIRD_LOGIN, info);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(LoginActivity.this, EditPersionInfoActivity.class);
        intent.putExtra(MyConfingInfo.WHERE_ARE_YOU_FORM, MyConfingInfo.FORM_THIRD_LOGIN);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        ShareSDK.initSDK(getApplicationContext());
        recever = new closeRecever();
        IntentFilter intentFilter = new IntentFilter(MyConfingInfo.CLOSE_OTHER_PAGER);
        registerReceiver(recever, intentFilter);
    }
    @Override
    protected void onResume()
    {
        super.onResume();
    }
    // 初始化，拿到控件对象
    private void init()
    {
        qqLogin = (ImageView)findViewById(R.id.qq_login);
        weiChartLogin = (ImageView)findViewById(R.id.weichart_login);
        facebookLogin = (ImageView)findViewById(R.id.facebook_login);
        weiChartLogin.setOnClickListener(listenerForClick);
        facebookLogin.setOnClickListener(listenerForClick);
        backTo = (ImageView)findViewById(R.id.back_to_next);
        newUserRegist = (TextView)findViewById(R.id.new_user_start_regist);
        savedAccount = new ArrayList<>();
        seePwd = (ImageView)findViewById(R.id.see_users_pwd);
        content = (AutoCompleteTextView)findViewById(R.id.enter_content);
        findPwd = (Button) findViewById(R.id.entrue_to_find_pwd);
        back = (ImageView)findViewById(R.id.back_to_login);
        mLinearLayout = (RelativeLayout)findViewById(R.id.get_password_layout);
        forget_pwd = (TextView)findViewById(R.id.forget_the_pwd);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mLoginFormView = (ScrollView)findViewById(R.id.login_form_scroll);
        mProgressView = (ProgressBar)findViewById(R.id.login_progress);
        mPasswordView = (EditText) findViewById(R.id.password);
        qqLogin.setOnClickListener(listenerForClick);
        backTo.setOnClickListener(listenerForClick);
        forget_pwd.setOnClickListener(listenerForClick);
        back.setOnClickListener(listenerForClick);
        findPwd.setOnClickListener(listenerForClick);
        seePwd.setOnClickListener(listenerForClick);
        newUserRegist.setOnClickListener(listenerForClick);
        mEmailSignInButton.setOnClickListener(listenerForClick);
        // Set up the login form.
        MyDBHelperForDayData dbHelper = MyDBHelperForDayData.getInstance(getApplicationContext());
        Cursor mCursor = dbHelper.selectAccount(LoginActivity.this);
        parserTheCursorAboutAccount(mCursor, mEmailView);
        // 设置有编辑动作时监听
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

    }

    private void parserTheCursorAboutAccount(Cursor mCursor, AutoCompleteTextView mEmailView)
    {
        if(mCursor.getCount() > 0)
        {
            if(mCursor.moveToFirst())
            {
                do {
                    String accountSaved = mCursor.getString(mCursor.getColumnIndex("account"));
                    if(!savedAccount.contains(accountSaved))
                    {
                        savedAccount.add(accountSaved);
                    }
                    mEmailView.setAdapter(new ArrayAdapter<>(this, R.layout.layout_for_complete_account, savedAccount));

                }while (mCursor.moveToNext());
            }
        }

    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    NoDoubleClickListener listenerForClick = new NoDoubleClickListener()
    {
        @Override
        public void onNoDoubleClick(View v)
        {
            switch (v.getId())
            {
                case R.id.email_sign_in_button:
                    if(NetStatus.isNetWorkConnected(LoginActivity.this))
                    {
                        // 检查输入格式并登陆
                            attemptLogin();
                    }
                    else
                    {
                        MyToastUitls.showToast(LoginActivity.this, R.string.net_wrong, 1);
                    }
                    break;
                case R.id.forget_the_pwd:
                    // 点击 ？号， 显示找回密码布局
                    if(Build.VERSION.SDK_INT < 21 && Build.VERSION.SDK_INT >= 18)
                    {
//                        Toast.makeText(LoginActivity.this, "点击了lll", Toast.LENGTH_SHORT).show();
                        mLinearLayout.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        showTheGetPwdLayoutWithAnim(forget_pwd, mLinearLayout);
                    }
                    isFirstLayout = false;
//                    mLinearLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.back_to_login:    // 返回按钮
//                    mLinearLayout.setVisibility(View.GONE);
                    if(Build.VERSION.SDK_INT < 21)
                    {
                        mLinearLayout.setVisibility(View.GONE);
                    }
                    else
                    {
                        hideTheGetPwdLayoutWithAnim(forget_pwd, mLinearLayout);
                    }
                    isFirstLayout = true;
                    break;
                case R.id.entrue_to_find_pwd:
                    sendEmailToFindPwd();
                    break;
                case R.id.see_users_pwd:
                    if(!isHideFlag)
                    {
                        mPasswordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }
                    else
                    {
                        mPasswordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                    isHideFlag = !isHideFlag;
                    mPasswordView.postInvalidate();
                    break;
                case R.id.new_user_start_regist:
                    Intent mIntents = new Intent();
                    mIntents.setClass(LoginActivity.this, registActivity.class);
                    mIntents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mIntents);
                    break;
                case R.id.back_to_next:
                    LoginActivity.this.onBackPressed();
                    break;
                case R.id.qq_login:
//                    Toast.makeText(LoginActivity.this, "qq登录", Toast.LENGTH_SHORT).show();
                    if(NetStatus.isNetWorkConnected(LoginActivity.this))
                    {
                        loginThrowQQ();
                    }
                    else
                    {
                        MyToastUitls.showToast(LoginActivity.this, R.string.net_wrong, 1);
                    }
                    break;
                case R.id.weichart_login:
                    if(NetStatus.isNetWorkConnected(LoginActivity.this))
                    {
                        if(isPkgInstalled("com.tencent.mm"))
                        {
                            loginThrowWeiChart();
                        }
                        else
                        {
                            MyToastUitls.showToast(LoginActivity.this, R.string.install_weichat, 1);
                        }
                    }
                    else
                    {
                        MyToastUitls.showToast(LoginActivity.this, R.string.net_wrong, 1);
                    }
                    break;
                case R.id.facebook_login:
                    if(NetStatus.isNetWorkConnected(LoginActivity.this))
                    {
                        loginThrowFacebook();
                    }
                    else
                    {
                        MyToastUitls.showToast(LoginActivity.this, R.string.net_wrong, 1);
                    }
                    break;
            }
        }
    };


    /**
     * 判定是否安装相应程序
     * @param pkgName 包名
     * @return
     */
    private boolean isPkgInstalled(String pkgName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getApplicationContext().getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * facebook第三方登录
     */
    private void loginThrowFacebook()
    {
//        Platform platform = ShareSDK.getPlatform(Twitt.Name);
//        platform.SSOSetting(false);
//        platform.setPlatformActionListener(platformListener);
//        platform.authorize();
        throwGrant(Facebook.NAME);
    }

    /**
     * 微信第三方登录
     */
    private void loginThrowWeiChart()
    {
//        Platform platform = ShareSDK.getPlatform(Wechat.NAME);
//        platform.SSOSetting(false);
//        platform.setPlatformActionListener(platformListener);
//        platform.authorize();
        throwGrant(Wechat.NAME);
    }


    /**
     * qq第三方登录
     */
    private void loginThrowQQ()
    {
        // 判断是否有登录过
//        String userId = LocalDataSaveTool.getInstance(LoginActivity.this).readSp(MyConfingInfo.QQ_THIRD_LOGIN);
//        String type = "2";
//        String pwd = "Cositea001";
//        if(userId != null && !userId.isEmpty())
//        {
//            new checkThirdPartyTask().execute(userId, type, null, null, null);
//        }
//        else
//        {
            throwGrant(QQ.NAME);
//        }
//        platform.setPlatformActionListener(platformListener);
//        platform.SSOSetting(false);
//        platform.authorize(new String[]{"name", "description"});
//        platform.showUser(null);
    }

    private void throwGrant(String name)
    {
        Platform platform = ShareSDK.getPlatform(name);
        authorize(platform);
    }

    private void authorize(Platform platform)
    {
//        客服方法
//        fb.SSOSetting(false);  //设置false表示使用SSO授权方式
//        fb.setPlatformActionListener(platformListener); // 设置分享事件回调
//
//        if (fb.isValid ()){
//            fb.removeAccount(true);
//        }
//        fb.showUser(null);//获取到用户信息



        if(platform == null)
        {
            return;
        }
        if(platform.isAuthValid())
        {
            platform.removeAccount(true);
        }
        Toast.makeText(LoginActivity.this, R.string.authorization, Toast.LENGTH_SHORT).show();
        platform.SSOSetting(false);
        platform.setPlatformActionListener(platformListener);
        platform.showUser(null);
        CircleProgressDialog.getInstance().showCircleProgressDialog(LoginActivity.this);
    }

    PlatformActionListener platformListener = new PlatformActionListener()
    {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap)
        {
            if(i == Platform.ACTION_USER_INFOR)
            {
                Message msg = Message.obtain();
                msg.what = LOGIN_COMPLETE;
                msg.obj = new Object[]{platform.getName(), hashMap};
                loginHandler.sendMessage(msg);
            }
        }
        @Override
        public void onError(Platform platform, int i, Throwable throwable)
        {
            if(i == Platform.ACTION_USER_INFOR)
            {
                loginHandler.sendEmptyMessage(LOGIN_ERROR);
                platform.removeAccount();
            }
            throwable.printStackTrace();
//            String name = platform.getName();
//            int id = platform.getId();
//            throwable.printStackTrace();
//            Log.i(TAG, "第三方登录：" + name + "--" + id);
        }
        @Override
        public void onCancel(Platform platform, int i)
        {
            if(i == Platform.ACTION_USER_INFOR)
            {
                loginHandler.sendEmptyMessage(LOGIN_CANCEL);
                platform.removeAccount();
            }
        }
    };


    private void sendEmailToFindPwd()
    {
        String enter = content.getText().toString();
        if(enter != null && enter.equals(""))
        {
            content.setError(getString(R.string.error_field_required));
            content.requestFocus();
            return;
        }
        else if(enter != null && enter.length() < 4 || enter != null && enter.length() > 32)
        {
            content.setError(getString(R.string.error_invalid_email));
            content.requestFocus();
            return;
        }
        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage(getString(R.string.get_the_password));
        dialog.show();
        new FandPWDTask().execute(enter);
    }


    class FandPWDTask extends AsyncTask<String, Void, Boolean>
    {
        String results;
        @Override
        protected Boolean doInBackground(String... params)
        {
            try
            {
                results = connectNET(params[0]);
                Log.i(TAG, "找回密码返回结果:" + results);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            super.onPostExecute(aBoolean);
            if(aBoolean)
            {
                if(results != null && !results.equals(""))
                {
                    try {
                        JSONObject json = new JSONObject(results);
                        String code = json.getString("code");
                        if(code != null && !code.equals(""))
                        {
                            if(code.equals("9003"))
                            {
                                Toast.makeText(LoginActivity.this, R.string.send_to_email, Toast.LENGTH_SHORT).show();
                            }
                            else if(code.equals("200"))
                            {
                                Toast.makeText(LoginActivity.this, R.string.send_to_email_failed, Toast.LENGTH_SHORT).show();
                            }
                            else if(code.equals("201"))
                            {
                                Toast.makeText(LoginActivity.this, R.string.send_to_email_nothave, Toast.LENGTH_SHORT).show();
                            }
                            else if(code.equals("9004"))
                            {
                                Toast.makeText(LoginActivity.this, R.string.find_pwd_wrong, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            dialog.dismiss();
        }
    }

    private String connectNET(String enter) throws IOException
    {
        String path = MyConfingInfo.WebRoot + File.separator + "user_findPwd?account=" + enter;
        HttpURLConnection connection;
        String result = "";
        try {
            URL url = new URL(path);
            connection = (HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(6 * 1000);
            connection.setReadTimeout(6 * 1000);
            connection.setUseCaches(false);
            connection.connect();
            InputStreamReader r = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(r);
            String a = "";
            while ((a = reader.readLine()) != null)
            {
                result += a;
            }
            r.close();
            reader.close();
            connection.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }finally {
        }
       return result;
    }


    /**
     * 判断此页面是由哪里启动的并做出相应操作，最终进入MainActivity
     */
    private void judgeAndEnterMainAcitivity()
    {
        int from = getIntent().getIntExtra(MyConfingInfo.WHERE_ARE_YOU_FROM, -1);

        if(from == MyConfingInfo.FROM_THE_MAINACTIVITY)
        {
            LoginActivity.this.onBackPressed();
        }else
        {
            Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mIntent);
            LoginActivity.this.finish();
        }

    }


    /**
     * 显示动画
     * @param tv
     * @param ml
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showTheGetPwdLayoutWithAnim(TextView tv, RelativeLayout ml)
    {
        // 执行圆形动画
        int[] location = new int[2];            // 两个元素的数组
        tv.getLocationOnScreen(location);
        int x = location[0] + (tv.getWidth()/2);                    // 圆心x轴
        int y = location[1];                    // 圆心y轴

        // get the final radius for the clipping circle
        int finalRadius = Math.max(ml.getWidth(), ml.getHeight());  // 最大半径
        int minRadius = tv.getWidth()/2;        // 最小半径

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(ml, x, y, minRadius, finalRadius);
        // make the view visible and start the animation
        ml.setVisibility(View.VISIBLE);
        anim.start();
    }


    /**
     * 隐藏动画
     * @param vg
     * @param layout
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void hideTheGetPwdLayoutWithAnim(TextView vg, final RelativeLayout layout)
    {
        int[] location = new int[2];
        vg.getLocationOnScreen(location);
        int cxx = location[0] + (vg.getWidth()/2);
        int cyy = location[1]* 7;
//        int cyy = vg.getMeasuredHeightAndState();

//        int cxx = (int)vg.getX();
//        int cyy = (int)vg.getY();

        // get the initial radius for the clipping circle
        // 获取园的半径
        int initialRadiuss = layout.getHeight();    // 最大半径
        int minRadiuss = vg.getWidth()/2;             // 最小半径

        // create the animation (the final radius is zero)
        Animator anims =
                ViewAnimationUtils.createCircularReveal(layout, cxx, cyy, initialRadiuss, minRadiuss);

        // make the view invisible when the animation is done
        anims.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                layout.setVisibility(View.GONE);
            }
        });

        // start the animation
        anims.start();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            if(!isFirstLayout)
            {
                if(Build.VERSION.SDK_INT < 21)
                {
                    mLinearLayout.setVisibility(View.GONE);
                }
                else
                {
                    hideTheGetPwdLayoutWithAnim(forget_pwd, mLinearLayout);
                }
                isFirstLayout = true;
                return false;
            }
        }

        if(CircleProgressDialog.getInstance().getDialog() != null && CircleProgressDialog.getInstance().getDialog().isShowing())
        {
            CircleProgressDialog.getInstance().closeCircleProgressDialog();
            if(checkTask != null)
            {
                checkTask.cancel(true);
            }
            return true;
        }


        return super.onKeyDown(keyCode, event);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin()
    {
        if (mAuthTask != null) {        // 判断异步任务对象
            return;
        }
        // Reset errors.
        mEmailView.setError(null);      // 复位错误提示
        mPasswordView.setError(null);
        // Store values at the time of the login attempt.
        // 获取用户输入内容
        String account = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        Log.i(TAG, "账号：" + account + "密码：" + password);
        boolean cancel = false; // 代表没有通过验证
        View focusView = null;  // 没通过验证的view

        // 检查账号是否可用
        if (TextUtils.isEmpty(account)) {   // 非空验证
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
            if(!checkContinueOrClose(cancel, focusView))
            {
                return;
            }
        }  else if (!isEmailValid(account)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
            if(!checkContinueOrClose(cancel, focusView))
            {
                return;
            }
        }

        // Check for a valid password, if the user entered one.
        // 检查密码是否可用，
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
            if(!checkContinueOrClose(cancel, focusView))
            {
                return;
            }
        }

//        if (cancel) {
//            // There was an error; don't attempt login and focus the first
//            // form field with an error.
//            focusView.requestFocus();   // 没通过验证的获取焦点
//        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(account, password);
            mAuthTask.execute((Void) null);
//        }
    }

    private boolean checkContinueOrClose(boolean cancel, View focusView)
    {
        if(cancel)
        {
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.length() >= 4 && email.length() <= 32;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 6 && password.length() <= 32;
    }


    private Boolean isConnectivity()
    {
        ConnectivityManager manager = (ConnectivityManager)LoginActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    /**
     * 显示进度条，并隐藏login布局
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.
                    setVisibility(
                            show ?
                                    View.GONE
                                    : View.VISIBLE);
            mLoginFormView
                    .animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView
                    .animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                                     ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
                                     ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract
                        .Contacts
                        .Data
                        .MIMETYPE +
                        " = ?",
                new String[]{
                         ContactsContract
                        .CommonDataKinds
                        .Email
                        .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(
            Loader<Cursor> cursorLoader,
            Cursor cursor)
    {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        loginHandler.removeCallbacks(runnable);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(recever);

    }

    private interface ProfileQuery
    {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection)
    {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    /**
     * 异步登录/注册
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private String mAccount;    // 账号
        private String mPassword;   // 密码

        UserLoginTask(String account, String password) {
            mAccount = account;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            boolean isSuccess = false;
            Sub_login login = null;

            try {

                login = new Sub_login(LoginActivity.this);// 登陆帮助类

                isSuccess = login.requestLogin(mAccount, mPassword);    // 发送账号密码
                Log.i("", "登陆返回结果：-----" + isSuccess);

            } catch (IOException e) {
                e.printStackTrace();
            }
            if(isSuccess)       // 登陆成功
            {
                JSONObject json = new JSONObject();
                try {
                    json.put(MyConfingInfo.TYPE, MyConfingInfo.NOMAL_TYPE);
                    json.put(MyConfingInfo.ACCOUNT, mAccount);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LocalDataSaveTool.getInstance(LoginActivity.this).writeSp(MyConfingInfo.USER_ACCOUNT, json.toString());
                LocalDataSaveTool.getInstance(LoginActivity.this).writeSp(MyConfingInfo.USER_PASSWORD, mPassword);
                if(login != null)   // 对象不为空
                {
                    Bitmap mB = login.getImageIcon();
                    if(mB != null)
                    {
                        // 将Bitmap存入缓存
                        SaveImageUtil.saveImageToSD(mB, SDPathUtils.getSdcardPath() + MyConfingInfo.SDCARD_DATA_ROOT_DIR, File.separator + "tmp.jpeg");
                                // 获取图片成功，将图片转为byte数组
//                        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
//                        boolean isOK = mB.compress(Bitmap.CompressFormat.JPEG, 100, byteOut);
//                        bytesImage = byteOut.toByteArray();
//                        LocalDataSaveTool.getInstance(LoginActivity.this).writeSp(MyConfingInfo.USER_ACCOUNT, mAccount);
//                        LocalDataSaveTool.getInstance(LoginActivity.this).writeSp(MyConfingInfo.USER_PASSWORD, mPassword);
                    }
                    else
                    {
                        File file = new File(SDPathUtils.getSdcardPath() + MyConfingInfo.IMAGEVIEW_SAVE_PATH);
                        if(file.exists())
                        {
                            file.delete();
                        }
                    }
                }
                return true;

            }
//            else
//            {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(LoginActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
//                    }
//                });
//                return false;
//            }
//            for (String credential : DUMMY_CREDENTIALS) {
//                String[] pieces = credential.split(":");
//                if (pieces[0].equals(mEmail)) {
//                    // Account exists, return true if the password matches.
//                    return pieces[clock].equals(mPassword);
//                }
//            }

            // TODO: register the new account here.
            return false;
        }



        @Override
        protected void onPostExecute(final Boolean success)
        {
            mAuthTask = null;
            showProgress(false);

            if (success)
            {
                saveTheAccount(mAccount);
                MyToastUitls.showToast(LoginActivity.this, R.string.login_success, 1);
                doLoginAction();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
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
            mIntent.setClass(LoginActivity.this, MainActivity.class);
        }
        else if(deviceType != null && deviceType.equals(MyConfingInfo.DEVICE_BLOOD))
        {
            mIntent.setClass(LoginActivity.this, MainActivity.class);
        }
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mIntent);
        LoginActivity.this.finish();
        sendBroadcast(new Intent(MyConfingInfo.CLOSE_OTHER_PAGER));
    }

    // 先查询数据库有无此账号，没有则添加
    private void saveTheAccount(String mAccount)
    {
        Cursor accountCurson = MyDBHelperForDayData.getInstance(LoginActivity.this).selectAccount(LoginActivity.this);
        ArrayList<String> acc = parserTheAccount(accountCurson);
        if(!acc.contains(mAccount))
        {
            MyDBHelperForDayData.getInstance(LoginActivity.this).insertAccount(LoginActivity.this, mAccount);
        }
    }

    private ArrayList<String> parserTheAccount(Cursor accountCurson)
    {
        ArrayList<String> account = new ArrayList<>();
        if(accountCurson.getCount() > 0)
        {
            if(accountCurson.moveToFirst())
            {
                do {
                    String accountS = accountCurson.getString(accountCurson.getColumnIndex("account"));
                    if(!account.contains(accountS))
                    {
                        account.add(accountS);
                    }
                }while (accountCurson.moveToNext());
            }
        }
        return account;
    }
}

