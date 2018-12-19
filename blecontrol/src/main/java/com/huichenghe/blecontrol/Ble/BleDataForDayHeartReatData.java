package com.huichenghe.blecontrol.Ble;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.blecontrol.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.http.UserAccountUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 *
 * 获取全天心率
 * Created by lixiaoning on 15-11-27.
 */
public class BleDataForDayHeartReatData extends BleBaseDataManage
{

    private static final String TAG = BleDataForDayHeartReatData.class.getSimpleName();
    private Context mContext;

    public static final byte fromDevice = (byte)0xa6;
    public static final byte toDevice = (byte)0x26;
    private String stringDateFormat;

    public BleDataForDayHeartReatData(Context mContext)
    {
        this.mContext = mContext;
    }



    /**
     * 请求全天心率数据
     */
    public void requestHeartReatDataAll()
    {

        Log.i(TAG, "请求心率数据:");
        byte[] bytes = new byte[6];
        // 获取当天日期
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;   // 月份从0开始，必须加1
        int year = calendar.get(Calendar.YEAR) - 2000;
        byte packageData = (byte)0x08;
        int hour = calendar.get(Calendar.HOUR_OF_DAY);  // 当前是时间点
        int mi = calendar.get(Calendar.MINUTE);

        int count = hour/3;         // 获取的
        int counts = hour%3;
        if(counts != 0 || mi != 0)
        {
            count = count + 1;
        }

        bytes[0] = (byte)(day & 0xff);
        bytes[1] = (byte)(month & 0xff);
        bytes[2] = (byte)(year & 0xff);
        bytes[3] = (byte)0x03;
        bytes[4] = (byte)packageData;

        for (int i = count; i >= 1; i--)   // 循环获取当前时间点之前所有的心率数据包
        {
            bytes[5] =(byte)(i & 0xff);
            setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
            try {
                int random = (int)(1 + Math.random() * (24 - 1 + 1));
                Thread.sleep(400 + random);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "请求心率数据:" + FormatUtils.bytesToHexString(bytes));
        }
    }

    /**
     * 处理手环返回的心率数据
     * @param hr
     */
    public void dealTheHeartRateData(byte[] hr)
    {
        byte[] dataThree = new byte[180];
        for (int i = 0; i < dataThree.length; i ++)
        {
            dataThree[i] = hr[i + 6];
        }

        Log.i(TAG, "截取后的心率数据：" + FormatUtils.bytesToHexString(dataThree));



        String userAccount = UserAccountUtil.getAccount(mContext);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        String today = format.format(calendar.getTime());
        int dayCurrent = calendar.get(Calendar.DATE);
        int monthCurrent = calendar.get(Calendar.MONTH) + 1;
        int yearCurrent = calendar.get(Calendar.YEAR);

        // 插入一条只有账号和日期的数据
//        MyDBHelperForDayData.getInstance(mContext).insertHrAccount(mContext, userAccount, today);

        Log.i(TAG, "分时心率数据：" + FormatUtils.bytesToHexString(hr));
        int day = (hr[0] & 0xff);
        int month = (hr[1] & 0xff);
        int year = (hr[2] & 0xff) + 2000;
        Calendar calendarData = Calendar.getInstance();
        calendarData.set(year, (month - 1), day);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dataDay = format1.format(calendarData.getTime());

//        05 0c 0f 03 08 01
//        ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff
//        ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff
//        ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff
//        ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff4016

        Cursor mCursor = MyDBHelperForDayData.getInstance(mContext).selectHrAccount(mContext, userAccount, dataDay);
        if(mCursor.getCount() == 0)
        {
            MyDBHelperForDayData.getInstance(mContext).insertHrAccount(mContext, userAccount, dataDay);
        }
        mCursor.close();
        Log.i(TAG, "执行时间存入心率:" + System.currentTimeMillis());


        switch (hr[5])
        {
            case (byte)0x01:        // 第一包数据，一包数据有三个小时的数据，共180个字节


                if(dataThree.length < 180)
                {
                    return;
                }

                byte[] byteOne;

                for (int j = 0; j < dataThree.length/60; j ++)
                {
                    byteOne = new byte[60];
                    for (int i = j * 60; i < byteOne.length * j + 60; i ++)
                    {
                        byteOne[i - j * 60] = dataThree[i];
                    }
                    if(j == 0)
                    {
                        MyDBHelperForDayData.getInstance(mContext).updateHr(mContext, userAccount, dataDay, "one", FormatUtils.bytesToHexString(byteOne));
                    }
                    if(j == 1)
                    {
                        MyDBHelperForDayData.getInstance(mContext).updateHr(mContext, userAccount, dataDay, "two", FormatUtils.bytesToHexString(byteOne));
                    }
                    if(j == 2)
                    {
                        MyDBHelperForDayData.getInstance(mContext).updateHr(mContext, userAccount, dataDay, "three", FormatUtils.bytesToHexString(byteOne));
                    }

                }
                break;

            case (byte)0x02:
                if(dataThree.length < 180)
                {
                    return;
                }

                byte[] byteTwo;

                for (int j = 0; j < dataThree.length/60; j ++)
                {
                    byteTwo = new byte[60];
                    for (int i = j * 60; i < byteTwo.length * j + 60; i ++)
                    {
                        byteTwo[i - j * 60] = dataThree[i];
                    }
                    if(j == 0)
                    {
                        MyDBHelperForDayData.getInstance(mContext).updateHr(mContext, userAccount, dataDay, "four", FormatUtils.bytesToHexString(byteTwo));
                    }
                    if(j == 1)
                    {
                        MyDBHelperForDayData.getInstance(mContext).updateHr(mContext, userAccount, dataDay, "five", FormatUtils.bytesToHexString(byteTwo));
                    }
                    if(j == 2)
                    {
                        MyDBHelperForDayData.getInstance(mContext).updateHr(mContext, userAccount, dataDay, "six", FormatUtils.bytesToHexString(byteTwo));
                    }

                }




                break;
            case (byte)0x03:
                if(dataThree.length < 180)
                {
                    return;
                }

                byte[] byteThree;

                for (int j = 0; j < dataThree.length/60; j ++)
                {
                    byteThree = new byte[60];
                    for (int i = j * 60; i < byteThree.length * j + 60; i ++)
                    {
                        byteThree[i - j * 60] = dataThree[i];
                    }
                    if(j == 0)
                    {
                        MyDBHelperForDayData.getInstance(mContext).updateHr(mContext, userAccount, dataDay, "seven", FormatUtils.bytesToHexString(byteThree));
                    }
                    if(j == 1)
                    {
                        MyDBHelperForDayData.getInstance(mContext).updateHr(mContext, userAccount, dataDay, "eight", FormatUtils.bytesToHexString(byteThree));
                    }
                    if(j == 2)
                    {
                        MyDBHelperForDayData.getInstance(mContext).updateHr(mContext, userAccount, dataDay, "nine", FormatUtils.bytesToHexString(byteThree));
                    }

                }



                break;

            case (byte)0x04:
                if(dataThree.length < 180)
                {
                    return;
                }

                byte[] byteFour;

                for (int j = 0; j < dataThree.length/60; j ++)
                {
                    byteFour = new byte[60];
                    for (int i = j * 60; i < byteFour.length * j + 60; i ++)
                    {
                        byteFour[i - j * 60] = dataThree[i];
                    }
                    if(j == 0)
                    {
                        MyDBHelperForDayData.getInstance(mContext).updateHr(mContext, userAccount, dataDay, "ten", FormatUtils.bytesToHexString(byteFour));
                    }
                    if(j == 1)
                    {
                        MyDBHelperForDayData.getInstance(mContext).updateHr(mContext, userAccount, dataDay, "one1", FormatUtils.bytesToHexString(byteFour));
                    }
                    if(j == 2)
                    {
                        MyDBHelperForDayData.getInstance(mContext).updateHr(mContext, userAccount, dataDay, "two1", FormatUtils.bytesToHexString(byteFour));
                    }

                }



                break;
            case (byte)0x05:
                if(dataThree.length < 180)
                {
                    return;
                }

                byte[] byteFive;

                for (int j = 0; j < dataThree.length/60; j ++)
                {
                    byteFive = new byte[60];
                    for (int i = j * 60; i < byteFive.length * j + 60; i ++)
                    {
                        byteFive[i - j * 60] = dataThree[i];
                    }
                    if(j == 0)
                    {
                        MyDBHelperForDayData.getInstance(mContext).updateHr(mContext, userAccount, dataDay, "three1", FormatUtils.bytesToHexString(byteFive));
                    }
                    if(j == 1)
                    {
                        MyDBHelperForDayData.getInstance(mContext).updateHr(mContext, userAccount, dataDay, "four1", FormatUtils.bytesToHexString(byteFive));
                    }
                    if(j == 2)
                    {
                        MyDBHelperForDayData.getInstance(mContext).updateHr(mContext, userAccount, dataDay, "five1", FormatUtils.bytesToHexString(byteFive));
                    }

                }

                break;

            case (byte)0x06:
                if(dataThree.length < 180)
                {
                    return;
                }

                byte[] byteSix;

                for (int j = 0; j < dataThree.length/60; j ++)
                {
                    byteSix = new byte[60];
                    for (int i = j * 60; i < byteSix.length * j + 60; i ++)
                    {
                        byteSix[i - j * 60] = dataThree[i];
                    }
                    if(j == 0)
                    {
                        MyDBHelperForDayData.getInstance(mContext).updateHr(mContext, userAccount, dataDay, "six1", FormatUtils.bytesToHexString(byteSix));
                    }
                    if(j == 1)
                    {
                        MyDBHelperForDayData.getInstance(mContext).updateHr(mContext, userAccount, dataDay, "seven1", FormatUtils.bytesToHexString(byteSix));
                    }
                    if(j == 2)
                    {
                        MyDBHelperForDayData.getInstance(mContext).updateHr(mContext, userAccount, dataDay, "eight1", FormatUtils.bytesToHexString(byteSix));
                    }

                }
                break;
            case (byte)0x07:
                if(dataThree.length < 180)
                {
                    return;
                }

                byte[] byteSeven;

                for (int j = 0; j < dataThree.length/60; j ++)
                {
                    byteSeven = new byte[60];
                    for (int i = j * 60; i < byteSeven.length * j + 60; i ++)
                    {
                        byteSeven[i - j * 60] = dataThree[i];
                    }
                    if(j == 0)
                    {
                        MyDBHelperForDayData.getInstance(mContext).updateHr(mContext, userAccount, dataDay, "nine1", FormatUtils.bytesToHexString(byteSeven));
                    }
                    if(j == 1)
                    {
                        MyDBHelperForDayData.getInstance(mContext).updateHr(mContext, userAccount, dataDay, "ten1", FormatUtils.bytesToHexString(byteSeven));
                    }
                    if(j == 2)
                    {
                        MyDBHelperForDayData.getInstance(mContext).updateHr(mContext, userAccount, dataDay, "one2", FormatUtils.bytesToHexString(byteSeven));
                    }

                }

                break;

            case (byte)0x08:
                if(dataThree.length < 180)
                {
                    return;
                }

                byte[] byteEight;

                for (int j = 0; j < dataThree.length/60; j ++)
                {
                    byteEight = new byte[60];
                    for (int i = j * 60; i < byteEight.length * j + 60; i ++)
                    {
                        byteEight[i - j * 60] = dataThree[i];
                    }
                    if(j == 0)
                    {
                        MyDBHelperForDayData.getInstance(mContext).updateHr(mContext, userAccount, dataDay, "two2", FormatUtils.bytesToHexString(byteEight));
                    }
                    if(j == 1)
                    {
                        MyDBHelperForDayData.getInstance(mContext).updateHr(mContext, userAccount, dataDay, "three2", FormatUtils.bytesToHexString(byteEight));
                    }
                    if(j == 2)
                    {
                        MyDBHelperForDayData.getInstance(mContext).updateHr(mContext, userAccount, dataDay, "four2", FormatUtils.bytesToHexString(byteEight));
                    }
                }
                break;
        }

        if(day != dayCurrent || month != monthCurrent || year != yearCurrent)
        {
            Log.i(TAG, "心率日期对比：" + day + "--" + dayCurrent + "--" + month + "--" + monthCurrent + "--" + year + "--" + yearCurrent);

            byte[] responseData = new byte[6];
            for (int i = 0; i < responseData.length; i++)
            {
                responseData[i] = hr[i];
            }
            setMsgToByteDataAndSendToDevice(toDevice, responseData, responseData.length);

        }

    }

}
