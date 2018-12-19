package com.huichenghe.blecontrol.Ble;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.http.UserAccountUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 请求和处理每一个小时的数据
 * 当天数据主动请求，当天以前的数据手环主动上传
 * Created by lixiaoning on 15-12-clock.
 */
public class BleDataForEachHourData extends BleBaseDataManage
{
    private static final String TAG = BleDataForEachHourData.class.getSimpleName();
    private Context mContext;
    private int day;
    private int month;
    private int year;
    private String dateCurrent;



    public static final byte fromDevice = (byte)0xa6;
    public static final byte toDevice = (byte)0x26;
//    public final String patten = "yyyy-MM-dd";

    public BleDataForEachHourData(Context context)
    {
        this.mContext = mContext;
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        day = calendar.get(Calendar.DATE);
        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR) - 2000;
        Date dates = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateCurrent = format.format(dates);
    }

    /**
     * 获取当天每小时的数据
     */
    public void getTheEcchHourData()
    {
        byte[] bytes = new byte[4];
        bytes[0] = (byte)(day & 0xff);
        bytes[1] = (byte)(month & 0xff);
        bytes[2] = (byte)(year & 0xff);
        bytes[3] = (byte)0x01;
        setMsgToByteDataAndSendToDevice(toDevice, bytes, bytes.length);
    }


    /**
     * 处理手环上传的每小时计步和热量数据
     * @param eachData
     */
    public void dealTheEachData(byte[] eachData)
    {
        Log.i(TAG, "每小时数据：" + FormatUtils.bytesToHexString(eachData));
//        020c0f01
        int dayNow = eachData[0] & 0xff;
        int monthNow = eachData[1] & 0xff;
        int yearNow = (eachData[2] & 0xff) + 2000;
        String dayS = formatTheDataDate(yearNow, monthNow, dayNow);
//        计步数据
//        ffffffff ffffffff ffffffff ffffffff ffffffff ffffffff
//        ffffffff ffffffff ffffffff 00000000 cc000000 ffffffff
//        ffffffff 10000000 30000000 00000000 18000000 0d000000
//        00000000 33000000 ffffffff ffffffff ffffffff ffffffff

//        热量数据
//        ffffffff ffffffff ffffffff ffffffff ffffffff ffffffff
//        ffffffff ffffffff ffffffff 00000000 42000000 ffffffff
//        ffffffff 40000000 33000000 2a000000 37000000 36000000
//        37000000 35000000 ffffffff ffffffff ffffffff ffffffff

        int stepOne = FormatUtils.byte2Int(eachData, 4);
        int stepTwo = FormatUtils.byte2Int(eachData, 8);
        int stepThree = FormatUtils.byte2Int(eachData, 12);
        int stepFour = FormatUtils.byte2Int(eachData, 16);
        int stepFive = FormatUtils.byte2Int(eachData, 20);
        int stepSix = FormatUtils.byte2Int(eachData, 24);
        int stepSeven = FormatUtils.byte2Int(eachData, 28);
        int stepEight = FormatUtils.byte2Int(eachData, 32);
        int stepNine = FormatUtils.byte2Int(eachData, 36);
        int stepTen = FormatUtils.byte2Int(eachData, 40);
        int stepOne1 = FormatUtils.byte2Int(eachData, 44);
        int stepTwo1 = FormatUtils.byte2Int(eachData, 48);
        int stepThree1 = FormatUtils.byte2Int(eachData, 52);
        int stepFour1 = FormatUtils.byte2Int(eachData, 56);
        int stepFive1 = FormatUtils.byte2Int(eachData, 60);
        int stepSix1 = FormatUtils.byte2Int(eachData, 64);
        int stepSeven1 = FormatUtils.byte2Int(eachData, 68);
        int stepEight1 = FormatUtils.byte2Int(eachData, 72);
        int stepNine1 = FormatUtils.byte2Int(eachData, 76);
        int stepTen1 = FormatUtils.byte2Int(eachData, 80);
        int stepOne2 = FormatUtils.byte2Int(eachData, 84);
        int stepTwo2 = FormatUtils.byte2Int(eachData, 88);
        int stepThree2 = FormatUtils.byte2Int(eachData, 92);
        int stepFour2 = FormatUtils.byte2Int(eachData, 96);


        int calorieOne = FormatUtils.byte2Int(eachData, 100);
        int calorieTwo = FormatUtils.byte2Int(eachData, 104);
        int calorieThree = FormatUtils.byte2Int(eachData, 108);
        int calorieFour = FormatUtils.byte2Int(eachData, 112);
        int calorieFive = FormatUtils.byte2Int(eachData, 116);
        int calorieSix = FormatUtils.byte2Int(eachData, 120);
        int calorieSeven = FormatUtils.byte2Int(eachData, 124);
        int calorieEight = FormatUtils.byte2Int(eachData, 128);
        int calorieNine = FormatUtils.byte2Int(eachData, 132);
        int calorieTen = FormatUtils.byte2Int(eachData, 136);
        int calorieOne1 = FormatUtils.byte2Int(eachData, 140);
        int calorieTwo1 = FormatUtils.byte2Int(eachData, 144);
        int calorieThree1 = FormatUtils.byte2Int(eachData, 148);
        int calorieFour1 = FormatUtils.byte2Int(eachData, 152);
        int calorieFive1 = FormatUtils.byte2Int(eachData, 156);
        int calorieSix1 = FormatUtils.byte2Int(eachData, 160);
        int calorieSeven1 = FormatUtils.byte2Int(eachData, 164);
        int calorieEight1 = FormatUtils.byte2Int(eachData, 168);
        int calorieNine1 = FormatUtils.byte2Int(eachData, 172);
        int calorieTen1 = FormatUtils.byte2Int(eachData, 176);
        int calorieOne2 = FormatUtils.byte2Int(eachData, 180);
        int calorieTwo2 = FormatUtils.byte2Int(eachData, 184);
        int calorieThree2 = FormatUtils.byte2Int(eachData, 188);
        int calorieFour2 = FormatUtils.byte2Int(eachData, 192);

        // 先查询分时计步数据
        String userAccount = UserAccountUtil.getAccount(mContext);

        Cursor mCursor = MyDBHelperForDayData.getInstance(mContext).getEachHourData(mContext, userAccount, dayS);
        if(mCursor.getCount() == 0) // 代表没有数据
        {
            MyDBHelperForDayData.getInstance(mContext)
                    .insertEachHourData(mContext, userAccount, dayS, stepOne, stepTwo, stepThree, stepFour, stepFive, stepSix,
                    stepSeven, stepEight, stepNine, stepTen, stepOne1, stepTwo1, stepThree1, stepFour1, stepFive1,
                    stepSix1,stepSeven1, stepEight1, stepNine1, stepTen1, stepOne2, stepTwo2, stepThree2, stepFour2);
        }
        else                        // 有数据则更新就OK
        {
            MyDBHelperForDayData.getInstance(mContext).updateEachHourData(mContext, userAccount, dayS, stepOne, stepTwo, stepThree, stepFour, stepFive, stepSix,
                    stepSeven, stepEight, stepNine, stepTen, stepOne1, stepTwo1, stepThree1, stepFour1, stepFive1,
                    stepSix1,stepSeven1, stepEight1, stepNine1, stepTen1, stepOne2, stepTwo2, stepThree2, stepFour2);
        }


        // 先查询分时热量数据
        Cursor cursor = MyDBHelperForDayData.getInstance(mContext).getEachHourCalorieData(mContext, userAccount, dayS);
        if(cursor.getCount() == 0)  // 若没有数据，则直接插入数据
        {
            MyDBHelperForDayData.getInstance(mContext)
                    .insertEachHourCalorieData(mContext, userAccount, dayS, calorieOne, calorieTwo, calorieThree, calorieFour,
                            calorieFive, calorieSix, calorieSeven, calorieEight, calorieNine, calorieTen, calorieOne1, calorieTwo1,
                            calorieThree1, calorieFour1, calorieFive1, calorieSix1, calorieSeven1, calorieEight1, calorieNine1,
                            calorieTen1, calorieOne2, calorieTwo2, calorieThree2, calorieFour2);
        }
        else    // 有数据则更新数据
        {
            MyDBHelperForDayData.getInstance(mContext).updateEachHourCalorieData
                    (mContext, userAccount, dayS, calorieOne, calorieTwo, calorieThree, calorieFour,
                    calorieFive, calorieSix, calorieSeven, calorieEight, calorieNine, calorieTen, calorieOne1, calorieTwo1,
                    calorieThree1, calorieFour1, calorieFive1, calorieSix1, calorieSeven1, calorieEight1, calorieNine1,
                    calorieTen1, calorieOne2, calorieTwo2, calorieThree2, calorieFour2
            );
        }







        // 如果不是当天，则是由手环主动上传，所以需要回复相应数据
        Log.i(TAG, "分时数据时间比较：" + dateCurrent + "--" + dayS);
        if(dateCurrent != null && dayS != null && !dateCurrent.equals(dayS))
        {

            byte[] backData = new byte[4];
            for (int i = 0; i < backData.length; i ++)
            {
                backData[i] = eachData[i];
            }
            setMsgToByteDataAndSendToDevice(toDevice, backData, backData.length);
        }










    }

    private String formatTheDataDate(int yearNow, int monthNow, int dayNow)
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.set(yearNow, monthNow - 1, dayNow);
        Date dateF = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(dateF);
    }


}
