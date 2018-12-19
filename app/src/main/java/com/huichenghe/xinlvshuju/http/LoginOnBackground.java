package com.huichenghe.xinlvshuju.http;

import android.content.Context;
import android.os.AsyncTask;

import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.slide.Sub_login;

import java.io.IOException;

/**
 * Created by lixiaoning on 2016/6/3.
 */
public class LoginOnBackground extends AsyncTask<Void, Void, Boolean>
{
    private Context mContext;
    public LoginOnBackground(Context context)
    {
        this.mContext = context;
    }
    @Override
    protected Boolean doInBackground(Void... params)
    {
        boolean isOk = loginOnNomal();
        return isOk;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean)
    {

    }


    private OnAllLoginBack onAllLoginBack;
    public void setOnLoginBackListener(OnAllLoginBack onAllLoginBack)
    {
        this.onAllLoginBack = onAllLoginBack;
    }

    /**
     * 隐性登陆
     */
    private boolean loginOnNomal()
    {
        boolean isSuccess = false;
        String password = LocalDataSaveTool.getInstance(mContext.getApplicationContext()).readSp(MyConfingInfo.USER_PASSWORD);
        String account = UserAccountUtil.getAccount(mContext.getApplicationContext());
        Sub_login sub = new Sub_login(mContext);
        try {
            isSuccess = sub.requestLogin(account, password);
        } catch (IOException e){
            e.printStackTrace();
        }

        if (isSuccess) {
            if(onAllLoginBack != null)
                onAllLoginBack.onLoginBack("");
//				logOut.setVisibility(View.VISIBLE);
//				botton_right.setVisibility(View.GONE);
            // do nothing
        }
        return isSuccess;
    }
}
