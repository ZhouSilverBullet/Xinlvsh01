package com.huichenghe.bleControl.Ble;

/**
 * Created by lixiaoning on 2016/6/4.
 */
public interface DataSendCallback
{
    void sendSuccess(byte[] receveData);
    void sendFailed();
    void sendFinished();
}
