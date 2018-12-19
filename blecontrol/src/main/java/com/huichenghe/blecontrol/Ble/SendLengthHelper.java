package com.huichenghe.blecontrol.Ble;

/**
 * Created by lixiaoning on 2016/8/13.
 */
public class SendLengthHelper
{
    /**
     * 数据包请求命令发送后，需要等待一个超时时间来接收被请求设备的回复，
     * 若超时时间内没收到回复，则可重发请求。若重发至少3次以上均没能收到回复，才可放弃请求。
     超时重发时间：	假设数据包长为L,连接间隔为P，则超时时间为：
     [ (MAX(L(send),L(receive))/20) * P + 100 + random(P,100)] ms ;
     或者简单点的（不推荐用此公式）：
     [400 +　random(P,100)] ms

     说明： L(send)表示发送请求数据包的长度，L（receive)表示接收数据包的长度
     ，若接收的数据包长度为不定长，按预计的最大长度处理。MAX(L(send),L(receive))表示取发送和接收数据长度的最大
     */
    private static final int connectSpace = 24;

    public static int getSendLengthDelay(int sendLength, int receverLength)
    {
        int max = max(sendLength, receverLength);
        return  (max/20) * connectSpace + 100 + random(connectSpace, 100);
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
