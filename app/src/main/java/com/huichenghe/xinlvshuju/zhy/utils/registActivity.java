package com.huichenghe.xinlvshuju.zhy.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.huichenghe.xinlvshuju.CustomView.CircleProgressDialog;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;
import com.huichenghe.xinlvshuju.Utils.NetStatus;
import com.huichenghe.xinlvshuju.mainpack.BaseActivity;
import com.huichenghe.xinlvshuju.mainpack.Regist_task;
import com.huichenghe.xinlvshuju.slide.EditPersionInfoActivity;

public class registActivity extends BaseActivity
{
    private static final String TAG = registActivity.class.getSimpleName();
    private ImageView back;
    private EditText editAccount, editPwd, confirmPWD, editEmail;
    private Button nextStep;
    private ImageView accountState, pwdState, emailState;
    private MYRecever recever;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist_layout);
        intiviews();
        registCloseBrocast();
    }


    private Handler registHandler = new Handler();
    private void registCloseBrocast()
    {
        recever = new MYRecever();
        registerReceiver(recever, new IntentFilter(MyConfingInfo.CLOSE_OTHER_PAGER));
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregistCloseBrocast();
    }

    private void unregistCloseBrocast()
    {
        unregisterReceiver(recever);
    }

    private void intiviews()
    {
        accountState = (ImageView)findViewById(R.id.account_enter_state);
        pwdState = (ImageView)findViewById(R.id.pwd_enter_state);
        emailState = (ImageView)findViewById(R.id.email_enter_state);
        back = (ImageView)findViewById(R.id.back_to_befor);
        editAccount = (EditText)findViewById(R.id.edit_account);
        editPwd = (EditText)findViewById(R.id.edit_pwd);
        confirmPWD = (EditText)findViewById(R.id.edit_pwd_confirme_regist);
        editEmail = (EditText)findViewById(R.id.edit_email);
        nextStep = (Button)findViewById(R.id.next_step);
        back.setOnClickListener(listerner);
        nextStep.setOnClickListener(listerner);
//        editAccount.addTextChangedListener(watchAccount);
        editPwd.addTextChangedListener(watchPwd);
//        confirmPWD.addTextChangedListener(confirePwd);
//        editEmail.addTextChangedListener(watchEmail);
        editEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == R.id.toNext || actionId == EditorInfo.IME_ACTION_DONE)
                {
                    checkTheEnterString();
                    return true;
                }
                return false;
            }
        });
    }


    TextWatcher watchAccount =  new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
        @Override
        public void afterTextChanged(Editable s)
        {
            String contentAccount = s.toString();
            if(contentAccount.length() >= 4 && contentAccount.length() <= 32)
            {
                accountState.setImageResource(R.mipmap.edit_ok);
            }
            else
            {
                accountState.setImageResource(R.mipmap.star_input);
            }

        }
    };

    TextWatcher watchPwd =  new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
        @Override
        public void afterTextChanged(Editable s)
        {
//            String contentPwd = s.toString();
//            if(contentPwd.length() >= 6 && contentPwd.length() <= 32)
//            {
//                pwdState.setImageResource(R.mipmap.edit_ok);
//            }
//            else
//            {
//                MyToastUitls.showToast(registActivity.this, R.string.error_invalid_password, 1);
//                pwdState.setImageResource(R.mipmap.star_input);
//            }
        }
    };
    TextWatcher confiredfPwd =  new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
        @Override
        public void afterTextChanged(Editable s)
        {
            String pwdString = editPwd.getText().toString();
            if(pwdString.length() >= 6 && pwdString.length() <= 32)
            {
                String firstPWD = editPwd.getText().toString();
                if(firstPWD != null && firstPWD.equals(s.toString()))
                {

                }
                else
                {
                    MyToastUitls.showToast(registActivity.this, R.string.double_pwd_not, 1);
                }

                pwdState.setImageResource(R.mipmap.edit_ok);
            }
            else
            {
                MyToastUitls.showToast(registActivity.this, R.string.error_invalid_password, 1);
                pwdState.setImageResource(R.mipmap.star_input);
            }
        }
    };

    TextWatcher watchEmail =  new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void afterTextChanged(Editable s)
        {
            String contentEmial = s.toString();
            if(contentEmial.length() >= 4 && contentEmial.length() <= 72)
            {
                if(isEmailValidate(contentEmial))
                {
                    emailState.setImageResource(R.mipmap.edit_ok);
                }
                else
                {
                    emailState.setImageResource(R.mipmap.star_input);
                }
            }
            else
            {
                emailState.setImageResource(R.mipmap.star_input);
            }

        }
    };
    NoDoubleClickListener listerner = new NoDoubleClickListener()
    {
        @Override
        public void onNoDoubleClick(View v)
        {
            if(v == back)
            {
                registActivity.this.onBackPressed();
            }
            else if(v == nextStep)
            {
                checkTheEnterString();
            }
        }
    };

    private boolean isEmailValidate(String e)
    {
        String patten = "^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-z0-9]{2,4})+$";
        return e.matches(patten);
    }


    /**
     * 校验输入的内容
     */
    private void checkTheEnterString()
    {
        // Reset errors.
        editAccount.setError(null);      // 复位错误提示
        editPwd.setError(null);
        confirmPWD.setError(null);
        editEmail.setError(null);

        // Store values at the time of the login attempt.
        // 获取用户输入内容
        final String accountt = editAccount.getText().toString();
        final String password = editPwd.getText().toString();
        String confirmPWDs = confirmPWD.getText().toString();
        final String email = editEmail.getText().toString();

        boolean cancel = false; // 代表没有通过验证
        View focusView = null;  // 没通过验证的view

        // 检查账号是否可用
        if (TextUtils.isEmpty(accountt)) {   // 非空验证
            editAccount.setError(getString(R.string.error_field_required));
            focusView = editAccount;
            cancel = true;
            if(!checkContinueOrStop(cancel, focusView))
            {
                return;
            }
        }  else if (!isAccountValid(accountt)) {
            editAccount.setError(getString(R.string.error_invalid_email));
            focusView = editAccount;
            cancel = true;
            if(!checkContinueOrStop(cancel, focusView))
            {
                return;
            }
        }

        // 检查密码是否可用，
        if (TextUtils.isEmpty(password) || !isPasswordValid(password))
        {
            editPwd.setError(getString(R.string.error_invalid_password));
            focusView = editPwd;
            cancel = true;
            if(!checkContinueOrStop(cancel, focusView))
            {
                return;
            }
        }

        if(TextUtils.isEmpty(confirmPWDs) || !isPasswordValid(confirmPWDs))
        {
            confirmPWD.setError(getString(R.string.error_invalid_password));
            focusView = confirmPWD;
            cancel = true;
            if(!checkContinueOrStop(cancel, focusView))
            {
                return;
            }
        }

        if(!password.equals(confirmPWDs))
        {
            confirmPWD.setError(getString(R.string.double_pwd_not));
            focusView = confirmPWD;
            cancel = true;
            if(!checkContinueOrStop(cancel, focusView))
            {
                return;
            }
        }

        if(TextUtils.isEmpty(email))
        {
            editEmail.setError(getString(R.string.error_email_not_null));
            focusView = editEmail;
            cancel = true;
            if(!checkContinueOrStop(cancel, focusView))
            {
                return;
            }
        }
        else if(!isEmailValidate(email))
        {
            editEmail.setError(getString(R.string.invaldate_email));
            focusView = editEmail;
            cancel = true;
            if(!checkContinueOrStop(cancel, focusView))
            {
                return;
            }
        }
        if(!NetStatus.isNetWorkConnected(getApplicationContext()))
        {
            MyToastUitls.showToast(getApplicationContext(), R.string.net_wrong, 1);
            return;
        }
        CircleProgressDialog.getInstance().showCircleProgressDialog(registActivity.this);
        registHandler.postDelayed(runProgressDialog, 15 * 1000);
        new Regist_task(getApplicationContext(), new Regist_task.registCallback() {
            @Override
            public void registBack(final boolean re)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        registHandler.removeCallbacks(runProgressDialog);
                        CircleProgressDialog.getInstance().closeCircleProgressDialog();
                        if(re)
                        {
                            doNextEnter(accountt, password, email);
                        }
                        else
                        {
                            MyToastUitls.showToast(getApplicationContext(), R.string.regist_faild_account_already_exsise, 1);
                        }
                    }
                });
            }
        }).execute(accountt, MyConfingInfo.WebRoot + "user_validation");

            // 进入完善资料页面
        /**
         * Intent mIntent = new Intent();
             mIntent.setClass(registActivity.this, EditPersionInfoActivity.class);
             mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             mIntent.putExtra(MyConfingInfo.WHERE_ARE_YOU_FORM, MyConfingInfo.FORM_REGIST);
             mIntent.putExtra(MyConfingInfo.USER_ACCOUNT, accountt);
             mIntent.putExtra(MyConfingInfo.USER_PASSWORD, password);
             mIntent.putExtra(MyConfingInfo.USER_EMAIL, email);
             mIntent.putExtra(MyConfingInfo.REGIST, true);
             startActivity(mIntent);
         */
    }

    Runnable runProgressDialog = new Runnable()
    {
        @Override
        public void run()
        {
            CircleProgressDialog.getInstance().closeCircleProgressDialog();
            MyToastUitls.showToast(getApplicationContext(), R.string.regist_outtime, 1);
        }
    };

    private void doNextEnter(String accountt, String password, String email)
    {
        Intent mIntent = new Intent();
        mIntent.setClass(registActivity.this, EditPersionInfoActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.putExtra(MyConfingInfo.WHERE_ARE_YOU_FORM, MyConfingInfo.FORM_REGIST);
        mIntent.putExtra(MyConfingInfo.USER_ACCOUNT, accountt);
        mIntent.putExtra(MyConfingInfo.USER_PASSWORD, password);
        mIntent.putExtra(MyConfingInfo.USER_EMAIL, email);
        mIntent.putExtra(MyConfingInfo.REGIST, true);
        startActivity(mIntent);
    }

    private boolean checkContinueOrStop(boolean cancel, View focusView)
    {
        if(cancel)
        {
            focusView.requestFocus();   // 没通过验证的获取焦点
            return false;
        }
        return true;
    }

    private boolean isAccountValid(String accountt)
    {
        return accountt.length() >= 4 && accountt.length() <= 32;
    }

    private boolean isPasswordValid(String password)
    {
        return password.length() >= 6 && password.length() <= 32;
    }

    class MYRecever extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent.getAction().equals(MyConfingInfo.CLOSE_OTHER_PAGER))
            {
                registActivity.this.finish();
            }

        }
    }

}
