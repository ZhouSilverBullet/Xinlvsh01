package com.huichenghe.xinlvshuju;

import android.content.Context;
import android.util.Log;

import com.huichenghe.xinlvshuju.UpdataService.SendDataCallback;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.http.LoginOnBackground;
import com.huichenghe.xinlvshuju.http.OnAllLoginBack;
import com.huichenghe.xinlvshuju.http.UserAccountUtil;
import com.huichenghe.xinlvshuju.http.checkThirdPartyTask;

/**
 * Created by lixiaoning on 2016/12/12.
 */

public class EachLoginHelper
{

    public EachLoginHelper(Context context, final SendDataCallback callback)
    {
        String loginType = UserAccountUtil.getType(context);
        switch (loginType) {
            case MyConfingInfo.QQ_TYPE:
            case MyConfingInfo.WEICHART_TYPE:
            case MyConfingInfo.FACEBOOK_TYPE:
                String account = UserAccountUtil.getAccount(context);
                checkThirdPartyTask task = new checkThirdPartyTask(context);
                task.setOnLoginBackListener(new OnAllLoginBack() {
                    @Override
                    public void onLoginBack(String res) {
                        Log.i("", "线程上传数据登录成功");
//                        dealTask();
                        callback.sendDataSuccess(res);
                    }
                });
                task.execute(account.split(";")[0], loginType, null, null, null);
                break;
            case MyConfingInfo.NOMAL_TYPE:
                LoginOnBackground onBack = new LoginOnBackground(context);
                onBack.setOnLoginBackListener(new OnAllLoginBack() {
                    @Override
                    public void onLoginBack(String res) {
                        Log.i("", "线程上传数据登录成功");
                        callback.sendDataSuccess(res);
                    }
                });
                onBack.execute();
                break;
        }
    }


}
