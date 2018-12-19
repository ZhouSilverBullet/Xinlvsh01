package com.huichenghe.xinlvshuju.slide.AttionModle;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.huichenghe.xinlvshuju.Adapter.AttionAdapter;
import com.huichenghe.xinlvshuju.Adapter.RecyclerItemClick;
import com.huichenghe.xinlvshuju.Adapter.WrapContentLinearLayoutManager;
import com.huichenghe.xinlvshuju.CustomView.CustomDateSelector;
import com.huichenghe.xinlvshuju.DataEntites.AttionPersionInfoEntity;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;
import com.huichenghe.xinlvshuju.Utils.NetStatus;
import com.huichenghe.xinlvshuju.http.LoginOnBackground;
import com.huichenghe.xinlvshuju.http.OnAllLoginBack;
import com.huichenghe.xinlvshuju.http.checkThirdPartyTask;
import com.huichenghe.xinlvshuju.mainpack.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class LovingCareActivity extends BaseActivity
{
    private static final String TAG = LovingCareActivity.class.getSimpleName();
    private ImageView careBack, careAdd;
    private SwipeRefreshLayout refreshLoving;
    private RecyclerView attion_fimally;
    private AttionAdapter attionAdapter;
    private PopupWindow addWindow;
    private ArrayList<AttionPersionInfoEntity> dataList;
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loving_care);
        intiTheView();
        attionData();
    }



    private void attionData()
    {
        if(NetStatus.isNetWorkConnected(getApplicationContext()))
        {
            getTheAttionData();
        }
        else
        {
            MyToastUitls.showToast(LovingCareActivity.this, R.string.net_wrong, 1);
            refreshLoving.setRefreshing(false);
        }
    }

    class requestAttionData extends AsyncTask<Void, Void, Boolean>
    {
        String resultData = null;

        @Override
        protected Boolean doInBackground(Void... params) {
            resultData = RequestTheData();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            closeRefresh();
            Log.i(TAG, "关注的数据:" + resultData);
            if (aBoolean)
            {
                parseTheJson(resultData);
//                requestAttionData.cancel(true);
            }
        }

    }  ;

        private void closeRefresh()
        {
            if (refreshLoving != null && refreshLoving.isRefreshing())
            {
                refreshLoving.setRefreshing(false);
            }
        }

        /**
         * 因为cookie有效时间很短，所以每次请求数据，先登录
         */
        private void checkLoginAndLogin() {
            String countData = LocalDataSaveTool.getInstance(LovingCareActivity.this).readSp(MyConfingInfo.USER_ACCOUNT);
            if (countData != null) {
                JSONObject json;
                String type = null;
                String account = null;
                try {
                    json = new JSONObject(countData);
                    type = json.getString(MyConfingInfo.TYPE);
                    account = json.getString(MyConfingInfo.ACCOUNT);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                switch (type)
                {
                    case MyConfingInfo.QQ_TYPE:
                    case MyConfingInfo.WEICHART_TYPE:
                    case MyConfingInfo.FACEBOOK_TYPE:
                        checkThirdPartyTask task = new checkThirdPartyTask(LovingCareActivity.this);
                        task.setOnLoginBackListener(new OnAllLoginBack() {
                            @Override
                            public void onLoginBack(String re) {
                                new requestAttionData().execute();
                            }
                        });
                        task.execute(account.split(";")[0], type, null, null, null);

                        break;
                    case MyConfingInfo.NOMAL_TYPE:
                        LoginOnBackground onBack = new LoginOnBackground(LovingCareActivity.this);
                        onBack.setOnLoginBackListener(new OnAllLoginBack() {
                            @Override
                            public void onLoginBack(String re) {
                                new requestAttionData().execute();
                            }
                        });
                        onBack.execute();

                        break;
                }

            }
        }

        private void parseTheJson(String resultData) {
            dataList.clear();
            attionAdapter.notifyDataSetChanged();
            JSONObject json;
            try {
                json = new JSONObject(resultData);
                String jsonData = json.getString("data");
                JSONArray array = new JSONArray(jsonData);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = (JSONObject) array.get(i);
                    String attionId = obj.getString("id");
                    String nick = obj.getString("mark");
                    String lastDate = obj.getString("lastDate");
                    String sleepState = obj.getString("sleepStatus");
                    String finishTimes = obj.getString("finishTimes");
                    String userHead = obj.getString("header");
                    String fatigue = obj.getString("indexFatigue");
                    AttionPersionInfoEntity ent = new AttionPersionInfoEntity(attionId, finishTimes, sleepState, lastDate, fatigue, userHead, nick);
                    if (!dataList.contains(ent))
                    {
                        dataList.add(ent);
                        attionAdapter.notifyItemInserted(dataList.size());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 获取好友关注列表
         */
        private void getTheAttionData() {
//       requestAttionData.execute();
            checkLoginAndLogin();
        }

        private String RequestTheData() {
            URL url;
            HttpURLConnection conn = null;
            String resultString = "";
            String responseData = "";
            String cookie = LocalDataSaveTool.getInstance(LovingCareActivity.this).readSp(MyConfingInfo.COOKIE_FOR_ME);
            try {
                url = new URL(MyConfingInfo.WebRoot + "attention_myAttention");
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(6 * 1000);
                conn.setReadTimeout(6 * 1000);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(true);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Cookie", cookie);
                conn.setInstanceFollowRedirects(true);
                conn.connect();
                InputStreamReader reader = new InputStreamReader(conn.getInputStream(), "UTf-8");
                BufferedReader read = new BufferedReader(reader);
                while ((resultString = read.readLine()) != null) {
                    responseData += resultString;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
//        Log.i(TAG, "关注的数据:" + responseData);
            return responseData;
        }

        private void intiTheView() {
            careBack = (ImageView) findViewById(R.id.care_back);
            careAdd = (ImageView) findViewById(R.id.care_add);
            refreshLoving = (SwipeRefreshLayout) findViewById(R.id.refresh_the_fimally);
            attion_fimally = (RecyclerView) findViewById(R.id.recycle_for_loving_care);
            setAdaper(attion_fimally);
            refreshLoving.setProgressViewOffset(false, 0, 100);
            refreshLoving.setRefreshing(true);
            setAllListener();
        }

        private void setAdaper(final RecyclerView attion_fimally) {
            dataList = new ArrayList<>();
            attion_fimally.setHasFixedSize(true);
            attion_fimally.setItemAnimator(new DefaultItemAnimator());
            attion_fimally.setLayoutManager(new WrapContentLinearLayoutManager(LovingCareActivity.this));
            attionAdapter = new AttionAdapter(LovingCareActivity.this, dataList);
            attionAdapter.setOnItemClick(new RecyclerItemClick()
            {
                @Override
                public void onItmeClick(int position)
                {
                    String id = dataList.get(position).getId();
                    Intent intent = new Intent(LovingCareActivity.this, AttionInfoActivity.class);
                    intent.putExtra("dataId", id);
                    intent.putExtra("headIcon", dataList.get(position).getHeader());
                    intent.putExtra("remark", dataList.get(position).getMark());
                    intent.putExtra("fatigue", dataList.get(position).getIndexfatigue());
                    intent.putExtra("days", dataList.get(position).getLastdate());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                @Override
                public void onItemLongClick(int position) { }
            });
            attionAdapter.setOnDeleteListener(new AttionAdapter.DeleteListener()
            {
                @Override
                public void delete(final int position)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LovingCareActivity.this);
                    builder.setTitle(R.string.cancle_attion);
                    builder.setMessage(R.string.cancle_attion_obj);
                    builder.setNegativeButton(R.string.be_cancle, null);
                    builder.setPositiveButton(R.string.be_true, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            AttionPersionInfoEntity attionEntity = dataList.get(position);
                            dataList.remove(position);
                            attionAdapter.notifyDataSetChanged();
                            String[] laginData = getLoginType();
                            LoginAndDelete(laginData, position, attionEntity.getId());
                        }
                    });
                    builder.create().show();
                }
            });
            attion_fimally.setAdapter(attionAdapter);
        }

    private void LoginAndDelete(String[] laginData, final int position, final String id)
    {
        switch (laginData[0])
        {
            case MyConfingInfo.QQ_TYPE:
            case MyConfingInfo.WEICHART_TYPE:
            case MyConfingInfo.FACEBOOK_TYPE:
                checkThirdPartyTask thirdTask = new checkThirdPartyTask(LovingCareActivity.this);
                thirdTask.setOnLoginBackListener(new OnAllLoginBack()
                {
                    @Override
                    public void onLoginBack(String re)
                    {
                        String cookie = LocalDataSaveTool.getInstance(LovingCareActivity.this).readSp(MyConfingInfo.COOKIE_FOR_ME);
                        new DeleteAttionObj().execute(id, cookie);
                    }
                });
                thirdTask.execute(laginData[1].split(";")[0], laginData[0], null, null, null);
                break;
            case MyConfingInfo.NOMAL_TYPE:
                LoginOnBackground nomalTask = new LoginOnBackground(LovingCareActivity.this);
                nomalTask.setOnLoginBackListener(new OnAllLoginBack()
                {
                    @Override
                    public void onLoginBack(String re)
                    {
                        String cookie = LocalDataSaveTool.getInstance(LovingCareActivity.this).readSp(MyConfingInfo.COOKIE_FOR_ME);
                        new DeleteAttionObj().execute(id, cookie);
                    }
                });
                nomalTask.execute();
                break;
        }
    }

    private String[] getLoginType()
    {
        String[] allData = new String[2];
        String account = LocalDataSaveTool.getInstance(LovingCareActivity.this).readSp(MyConfingInfo.USER_ACCOUNT);
        try {
            JSONObject json = new JSONObject(account);
            allData[0] = json.getString(MyConfingInfo.TYPE);
            allData[1] = json.getString(MyConfingInfo.ACCOUNT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return allData;
    }

    private void setAllListener() {
            careBack.setOnClickListener(listener);
            careAdd.setOnClickListener(listener);
            refreshLoving.setOnRefreshListener(refreshListener);
        }


        NoDoubleClickListener listener = new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(v == careBack)
                {
                    LovingCareActivity.this.onBackPressed();
                }
                else if(v == careAdd)
                {
                    checkAndShow();
                }
            }
        };

    private void checkAndShow()
    {
        if(NetStatus.isNetWorkConnected(getApplicationContext()))
        {
            showAddAttionWindow();
        }
        else
        {
            MyToastUitls.showToast(LovingCareActivity.this, R.string.net_wrong, 1);
        }
    }

    private void showAddAttionWindow()
    {
        View view = getXMLLayout();
        addWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        addWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.dialogshadow));
        addWindow.setFocusable(true);
        addWindow.setOutsideTouchable(true);
        addWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) view.getLayoutParams();
        param.width = (int)(getResources().getDisplayMetrics().widthPixels * 0.8f);
        view.setLayoutParams(param);
//        setWindowAs(0.6f);
        addWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss()
            {
                addWindow = null;
//                setWindowAs(1.0f);
            }
        });
    }

    private View getXMLLayout()
    {
        View view = LayoutInflater.from(LovingCareActivity.this).inflate(R.layout.add_attion_layout, null);
        final EditText attionAccunt = (EditText) view.findViewById(R.id.attion_account);
        final TextView attionBirthday = (TextView)view.findViewById(R.id.attion_birthday);
        final EditText attionRemark = (EditText)view.findViewById(R.id.attion_remark);
        final Button add = (Button)view.findViewById(R.id.be_true_to_add);
        NoDoubleClickListener listener = new NoDoubleClickListener()
        {
            @Override
            public void onNoDoubleClick(View v)
            {
                if(v == add)
                {
                    String accou = attionAccunt.getText().toString();
                    String bir = attionBirthday.getText().toString();
                    String remark = attionRemark.getText().toString();
                    if(dealEnterContent(accou, bir, remark))
                    {
                        // 发送数据添加关注
                        if(NetStatus.isNetWorkConnected(getApplicationContext()))
                        {
                            sendToAddAttion(accou, bir, remark);
                        }
                        else
                        {
                            MyToastUitls.showToast(LovingCareActivity.this, R.string.net_wrong, 1);
                        }
                    }
                }
                else if(v == attionBirthday)
                {
                    new CustomDateSelector(LovingCareActivity.this,
                            attionBirthday.getText().toString(), new CustomDateSelector.OnDateChoose()
                    {
                        @Override
                        public void choose(String dates)
                        {
                            attionBirthday.setText(dates);
                        }
                    });
                }
            }
        };
        add.setOnClickListener(listener);
        attionBirthday.setOnClickListener(listener);
        return view;
    }

    private void sendToAddAttion(final String accou, final String bir, final String remark)
    {
        String userAccount = LocalDataSaveTool.getInstance(LovingCareActivity.this)
                .readSp(MyConfingInfo.USER_ACCOUNT);
        String type = null;
        String accounst = null;
        try {
            JSONObject json = new JSONObject(userAccount);
            type = json.getString(MyConfingInfo.TYPE);
            accounst = json.getString(MyConfingInfo.ACCOUNT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (type)
        {
            case MyConfingInfo.QQ_TYPE:
            case MyConfingInfo.WEICHART_TYPE:
            case MyConfingInfo.FACEBOOK_TYPE:
                checkThirdPartyTask party = new checkThirdPartyTask(LovingCareActivity.this);
                party.setOnLoginBackListener(new OnAllLoginBack() {
                    @Override
                    public void onLoginBack(String re)
                    {
                        lovingCareAddHelper helper = new lovingCareAddHelper(LovingCareActivity.this);
                        helper.setOnAddAcctionCallback(new AddAcctionCallback() {
                            @Override
                            public void onFailed()
                            {

                            }
                            @Override
                            public void onSuccess(String code)
                            {
                                checkTheCode(code);
                            }
                        });
                        helper.execute(accou, bir, remark);
                    }
                });
                party.execute(accounst.split(";")[0], type, null, null, null);
                break;
            case MyConfingInfo.NOMAL_TYPE:
                LoginOnBackground onbackgound = new LoginOnBackground(LovingCareActivity.this);
                onbackgound.setOnLoginBackListener(new OnAllLoginBack() {
                    @Override
                    public void onLoginBack(String re) {
                        lovingCareAddHelper helper = new lovingCareAddHelper(LovingCareActivity.this);
                        helper.setOnAddAcctionCallback(new AddAcctionCallback() {
                            @Override
                            public void onFailed()
                            {

                            }
                            @Override
                            public void onSuccess(String code) {
                                checkTheCode(code);
                            }
                        });
                        helper.execute(accou, bir, remark);
                    }
                });
                onbackgound.execute();
                break;
        }




    }

    private void checkTheCode(String code)
    {
        switch (code)
        {
            case "9003":
                MyToastUitls.showToast(LovingCareActivity.this, R.string.add_success, 1);
                getTheAttionData();
                refreshLoving.setProgressViewOffset(false, 0, 100);
                refreshLoving.setRefreshing(true);
                break;
            case "400":
                MyToastUitls.showToast(LovingCareActivity.this, R.string.add_not_5, 1);
                break;
            case "401":
                MyToastUitls.showToast(LovingCareActivity.this, R.string.already_add, 1);
                break;
            case "402":
                MyToastUitls.showToast(LovingCareActivity.this, R.string.birthday_wrong, 1);
                break;
            case "403":
                MyToastUitls.showToast(LovingCareActivity.this, R.string.not_add_obj, 1);
                break;
            case "404":
                MyToastUitls.showToast(LovingCareActivity.this, R.string.can_not_add_self, 1);
                break;
            case "9002":
                MyToastUitls.showToast(LovingCareActivity.this, R.string.error_invalid_email, 1);
                break;
        }
        if(addWindow != null && addWindow.isShowing())
        addWindow.dismiss();
    }

    private boolean dealEnterContent(String accou, String bir, String remark)
    {

        if(accou != null && accou.equals(""))
        {
            MyToastUitls.showToast(LovingCareActivity.this, R.string.attion_accunt_not_null, 1);
            return false;
        }
        if(bir != null && bir.equals(""))
        {
            MyToastUitls.showToast(LovingCareActivity.this, R.string.attion_birtheay_not_null, 1);
            return false;
        }
        if(remark != null && remark.equals(""))
        {
            MyToastUitls.showToast(LovingCareActivity.this, R.string.remark_not_null, 1);
            return false;
        }
        if(remark.length() > 32)
        {
            MyToastUitls.showToast(LovingCareActivity.this, R.string.remark_to_long, 1);
            return false;
        }
        return true;
    }

    private void setWindowAs(float alpht)
    {
        WindowManager.LayoutParams paramses =  LovingCareActivity.this.getWindow().getAttributes();
        paramses.alpha = alpht;
        LovingCareActivity.this.getWindow().setAttributes(paramses);
    }






    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.removeCallbacks(runnable);
                getTheAttionData();
                handler.postDelayed(runnable, 2000);
            }
        };


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                closeRefresh();
            }
        };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}