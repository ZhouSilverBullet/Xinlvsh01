package com.huichenghe.xinlvshuju;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.huichenghe.bleControl.Ble.BleForPhoneAndSmsRemind;
import com.huichenghe.xinlvshuju.Utils.BreakPhoneUtils;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;


/**
 * 广播接收器，接收系统发出的短信广播
 *
 * 注意：此广播要在minifes.xml中注册
 * Created by lixiaoning on 15-12-phone.
 */
public class SmsReciver extends BroadcastReceiver
{
    public static final String TAG = SmsReciver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent)
    {
//        Log.i(TAG, "收到短消息");
        Bundle bundle = intent.getExtras();
        SmsMessage msg = null;
        if(null != bundle)
        {
            Object[] smsObj = (Object[])bundle.get("pdus");
            for (Object object : smsObj)
            {
                msg = SmsMessage.createFromPdu((byte[]) object);

                Log.i("TAG", "info, number:" + msg.getOriginatingAddress()
                        + "   body:" + msg.getDisplayMessageBody() + "   time:"
                        + msg.getTimestampMillis());
                boolean isNeedRemind = false;
                String type =  LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.DEVICE_INFO_TYPE);
                if(type != null && type.equals(MyConfingInfo.DEVICE_INFO_NORMAL) || type == null)
                {
                    String smsR = LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.SMS_REMIND_CHANGE);
                    if(smsR != null && smsR.equals(MyConfingInfo.REMIND_OPEN))
                    {
                        isNeedRemind = true;
                    }
                }

                if(isNeedRemind)   // 用户已打开短信提醒
                {
//                    Log.i(TAG, "短信息:" + smsR + "--" + isNeedRemind);
                    // 查询contentProvider，若有联系人姓名，则发送提醒信息
                    String number = msg.getOriginatingAddress();        // 获取电话号码
                    String contactName = BreakPhoneUtils.getContactNameFromPhoneBook(context, number);
                    if(contactName != null && !contactName.equals(""))  // 有联系人名字
                    {
                        // 打开短信提醒
//                        Log.i(TAG, "短信息名字:" + contactName + "--" + msg.getDisplayMessageBody() + "--" + msg.getMessageBody());
                        BleForPhoneAndSmsRemind remind = BleForPhoneAndSmsRemind.getInstance();
                        remind.startSMSRemine(contactName, (byte)0x05);
                        break;
                    }
                    else
                    {
                        BleForPhoneAndSmsRemind remind = BleForPhoneAndSmsRemind.getInstance();
                        remind.startSMSRemine(number, (byte)0x05);
                        break;
                    }
                }
            }
        }
    }
}
