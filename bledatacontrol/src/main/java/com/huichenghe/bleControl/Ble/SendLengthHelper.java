package com.huichenghe.bleControl.Ble;

/**
 * Created by lixiaoning on 2016/8/13.
 */
public class SendLengthHelper
{
    private static final int connectSpace = 60;

    public static int getSendLengthDelay(int sendLength, int receverLength)
    {
        int max = max(sendLength, receverLength);
        return  (max/20) * connectSpace + 100 + random(connectSpace, 100);
    }

    public static int getSendLengthDelayForPay(int sendLength, int receverLength)
    {
        int max = max(sendLength, receverLength);
        return  (max/20) * connectSpace + 500 + random(connectSpace, 100);
    }

    public int getSendLengthDelayTime(int sendLength)
    {
        return sendLength * connectSpace + random(connectSpace, 100);
    }

    private static int random(int connectSpace, int i)
    {
        return (int)(connectSpace + Math.random() * (i - connectSpace));
    }


    private static int max(int sendLength, int receverLength)
    {
        return (sendLength > receverLength) ? sendLength : receverLength;
    }


}
