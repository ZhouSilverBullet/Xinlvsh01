package com.huichenghe.xinlvshuju.BleDeal;

import android.content.Context;
import android.util.Log;

import com.huichenghe.bleControl.Ble.BleDataForQQAndOtherRemine;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

import java.io.UnsupportedEncodingException;

/**
 * Created by lixiaoning on 2016/8/11.
 */
public class NotificationSendHelper
{
    public final String TAG = "NotificationSendHelper";

    private Context context;
    private byte[] dataContent;
    public NotificationSendHelper(Context context)
    {
        this.context = context;
    }
    public synchronized void comparePackageNameAndSend(String packName, String title, String text)
    {
        String weiChart = "com.tencent.mm";
        String QQ = "com.tencent.mobileqq";
        String Facebook = "com.facebook.katana";
        String facebookMesg = "com.facebook.orca";
        String WhatsApp = "com.whatsapp";
        String Twitter = "com.twitter.android";
        String Skype = "com.skype.rover";
        String sk = "com.skype.raider";
        String weiChartTwo = "com.tencent.vt05";
        Log.i(TAG, "当前应用：" + title + "--内容：" + text);
        if(packName != null && packName.equals(weiChart) || packName != null && packName.equals(weiChartTwo))
        {
            checkAndSendMessage(MyConfingInfo.SP_WEICHART_REMIND, BleDataForQQAndOtherRemine.weichart, title, text);
        }
        else if(packName != null && packName.equals(QQ))
        {
            checkAndSendMessage(MyConfingInfo.SP_QQ_REMIND, BleDataForQQAndOtherRemine.qq, title, text);
        }
        else if(packName != null && packName.equals(Facebook) || (packName != null && packName.equals(facebookMesg)))
        {
            checkAndSendMessage(MyConfingInfo.SP_FACEBOOK_REMIND, BleDataForQQAndOtherRemine.facebook, title, text);
        }
        else if(packName != null && packName.equals(Skype) || packName != null && packName.equals(sk))
        {
            checkAndSendMessage(MyConfingInfo.SP_SKYPE_REMIND, BleDataForQQAndOtherRemine.skype, title, text);
        }
        else if(packName != null && packName.equals(Twitter))
        {
            checkAndSendMessage(MyConfingInfo.SP_TWITTER_REMIND, BleDataForQQAndOtherRemine.twitter, title, text);
        }
        else if(packName != null && packName.equals(WhatsApp))
        {
            checkAndSendMessage(MyConfingInfo.SP_WHATSAPP_REMIND, BleDataForQQAndOtherRemine.whatsapp, title, text);
        }
        else
        {
            checkAndSendMessage(title, text);
        }
    }

    private void checkAndSendMessage(String title, String text)
    {
            getContentFromWeiChartOther(title, text);
            byte other = (byte) 0xfe;//其他应用的消息
            BleDataForQQAndOtherRemine.getIntance().sendMessageToDevice(other, dataContent);
    }

    private void checkAndSendMessage(String witches, byte who, String title, String text)
    {
        String or = LocalDataSaveTool.getInstance(context.getApplicationContext()).readSp(witches);
        if(or != null && or.equals(MyConfingInfo.REMIND_OPEN))
        {
            getContentFromWeiChartOther(title, text);
            BleDataForQQAndOtherRemine.getIntance().sendMessageToDevice(who, dataContent);
        }
        else
        {
//                do nothing
        }
    }

    private void getContentFromWeiChartOther(String title, String text)
    {
        String texts = title + ":" + text;
        if(texts != null && texts.equals(""))
        {
            return;
        }
        getCanSendByte(texts);
    }

    private void getCanSendByte(String text)
    {
        int max = 32;
        String maxLen = LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.CHECK_SUPPORT_MSG_MAX_LENGTH);
        if(maxLen != null && !maxLen.equals(""))
        {
            max = Integer.valueOf(maxLen);
        }
        try {
            dataContent = text.getBytes("utf-8");
            if(dataContent.length > max)
            {
                text = text.substring(0, max/3);
//                getCanSendByte(text);
                dataContent = text.getBytes("utf-8");
            }
            else
            {
                dataContent = text.getBytes("utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
