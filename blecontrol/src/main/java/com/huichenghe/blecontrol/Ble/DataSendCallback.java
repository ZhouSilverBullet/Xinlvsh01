package com.huichenghe.blecontrol.Ble;

/**
 * Created by lixiaoning on 2016/6/4.
 */
public interface DataSendCallback
{
    void sendSuccess(byte[] receveData);
    void sendFinish();
}
