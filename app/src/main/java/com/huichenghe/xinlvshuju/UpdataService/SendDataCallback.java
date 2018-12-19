package com.huichenghe.xinlvshuju.UpdataService;

/**
 * Created by lixiaoning on 2016/12/7.
 */

public interface SendDataCallback
{
    void sendDataSuccess(String reslult);
    void sendDataFailed(String result);
    void sendDataTimeOut();
}
