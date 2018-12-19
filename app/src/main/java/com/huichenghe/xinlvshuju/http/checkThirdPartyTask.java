package com.huichenghe.xinlvshuju.http;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.slide.Sub_login;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

/**
 * 检查是否第三方登录过
 */
public class checkThirdPartyTask extends AsyncTask<String, Void, Boolean>
{
    private final String USERACCOUNT = "user.account";
    private final String USERTYPE = "user.type";
    private final String USERPWD = "user.password";
    private Context mContext;
    public checkThirdPartyTask(Context context)
    {
        this.mContext = context;
    }
    public checkThirdPartyTask(){}
    private Handler checkHandler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }
    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
    @Override
    protected Boolean doInBackground(final String... params)
    {
        HashMap<String, String> data = new HashMap<>();
        data.put(USERTYPE, params[1]);
        data.put(USERPWD, params[0]);
        String actionUrl = MyConfingInfo.WebRoot + "user_thirdPartyLogin";
        Sub_login login = new Sub_login(mContext);
        login.setOnThirdLoginBack(new Sub_login.OnThirdLoginBack() {
        @Override
        public void onThirdLoginBack(String requesCode) {
            if(onAllLoginBack != null)
            onAllLoginBack.onLoginBack(requesCode);
            parserTheThirdJSON(requesCode, params[0], params[1], params[2], params[3], params[4]);
            }
        });
        login.ThirdLogin(actionUrl, data);
        return null;
    }


    private OnAllLoginBack onAllLoginBack;
    public void setOnLoginBackListener(OnAllLoginBack onAllLoginBack)
    {
        this.onAllLoginBack = onAllLoginBack;
    }


    /**
     * 解析第三方登录回传的信息
     * @param requesCode
     * @param useIc
     * @param userGende
     * @param usNick
     */
    private void parserTheThirdJSON(String requesCode, String userId, String type, String useIc, String userGende, String usNick)
    {
//        {"code":"9005","msg":"资料不全！","data":""}
        try {
            JSONObject obj = new JSONObject(requesCode);
            String code = obj.getString("code");
            String data = obj.getString("data");
            JSONObject jsonObj = obj.getJSONObject("data");
            String accountAuto = jsonObj.getString("account");
            if(code != null && code.equals("9005"))
            {
                if(onCheckLoginBack != null)
                onCheckLoginBack.onThirdLoginFirstBack(code, accountAuto);

            }
            else if(code != null && code.equals("9003"))
            {
                if(onCheckLoginBack != null)
                onCheckLoginBack.onThirdLoginNoFirstBack(data);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }







    private OnCheckLoginBack onCheckLoginBack;
    public void setOnCheckLoginBack(OnCheckLoginBack onCheckLoginBack)
    {
        this.onCheckLoginBack = onCheckLoginBack;
    }
    public interface OnCheckLoginBack
    {
        void onThirdLoginFirstBack(String code, String autoAccount);
        void onThirdLoginNoFirstBack(String data);
    }


}