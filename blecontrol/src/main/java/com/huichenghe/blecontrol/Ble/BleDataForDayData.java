package com.huichenghe.blecontrol.Ble;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.Utils.NetStatus;
import com.huichenghe.xinlvshuju.http.LoginOnBackground;
import com.huichenghe.xinlvshuju.http.OnAllLoginBack;
import com.huichenghe.xinlvshuju.http.UserAccountUtil;
import com.huichenghe.xinlvshuju.http.checkThirdPartyTask;
import com.huichenghe.xinlvshuju.mainpack.MyApplication;
import com.huichenghe.xinlvshuju.slide.AttionModle.UpdateAttionMovementAndSleepData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 此类处理全天数据
 * Created by lixiaoning on 15-11-17.
 */
public class BleDataForDayData extends BleBaseDataManage
{
    public static final String TAG = BleDataForDayData.class.getSimpleName();
    private Context context;
    public static byte fromDevice = (byte)0xa6;
    public static byte toDevice = (byte)0x26;

    public BleDataForDayData(Context context)
    {
        this.context = context;
    }


    /**
     * 请求全天数据
     *
     *
     */
    public int requestDayDate()
    {
//        byte[] data = new byte[info];
//        Calendar mCalendar = Calendar.getInstance();    // 获取日历对象
//        int year = mCalendar.get(Calendar.YEAR);
//        int month = mCalendar.get(Calendar.MONTH);
//        int date = mCalendar.get(Calendar.DATE);
//
//
//        data[0] = (byte)date;
//        data[clock] = (byte)month;
//        data[phone] = (byte)(year - 2000);
//
//
//        setMsgToByteDataAndSendToDevice(mRequest_cmd, data, data.length, true);
//
//        try {
//            Thread.sleep(200);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        setMsgToByteDataAndSendToDevice(mRequest_cmd, data, data.length, true);


        //6826 0400 12 07 0f 00 b416
        //6826 0400 1a 0b 0f 00 c616

        // 当天数据需请求，然后手环返回数据
        // 历史数据手环以广播形式主动上传，然后返回相应码
        byte[] reqData = new byte[4];
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        int year = cal.get(Calendar.YEAR);// 获取年份
        int month = cal.get(Calendar.MONTH) + 1;// 获取月份
        int day = cal.get(Calendar.DATE);// 获取日

        reqData[0] = (byte)day;
        reqData[1] = (byte)month;
        reqData[2] = (byte)(year - 2000);
        reqData[3] = (byte)0x00;

        return setMsgToByteDataAndSendToDevice(toDevice, reqData, reqData.length);


    }

    public void juestResponse(byte[] data)
    {
        byte[] responseData = new byte[4];
        System.arraycopy(data, 0, responseData, 0, responseData.length);
        setMsgToByteDataAndSendToDevice(toDevice, responseData, responseData.length);
    }




    //                     计步      热量	     里程	  活动时间  活动热量   静坐时间  静坐热量
//    68 a6 2000 010c0f 00 00000000 05000000 00000000 01000000 05000000 04000000 00000000 5916
    public void dealDayData(Context mContext, byte[] data)
    {
        Log.i(TAG, "接收到了全天数据---------：" + FormatUtils.bytesToHexString(data));
        //1a 0b 0f 00 59020000 5a020000 5b020000 5c020000 5d020000 2b16
        int day = data[0];         // 日
        int month = data[1];       // 月
        int year = data[2];        // 年

        String dataDate = formatTheDate(year + 2000, month, day);
        int stepAll = FormatUtils.byte2Int(data, 4);
        int calorie = FormatUtils.byte2Int(data, 8);
        int mileage = FormatUtils.byte2Int(data, 12);
        int movementTime = FormatUtils.byte2Int(data, 16);
        int moveCalorie = FormatUtils.byte2Int(data, 20);
        int sitTime = FormatUtils.byte2Int(data, 24);
        int sitCalorie = FormatUtils.byte2Int(data, 28);

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        Date dateNow = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateC = format.format(dateNow);
//        int today = calendar.get(Calendar.DATE);
//        int tomon = calendar.get(Calendar.MONTH) + clock;
//        int toyear = calendar.get(Calendar.YEAR);
//        String yearS = String.valueOf(toyear);
//        yearS = yearS.substring(phone);

        Log.i(TAG, "返回的天数据日期:" + dateC + "--" + dataDate);
        if(dataDate != null && dateC != null && !dataDate.equals(dateC))// 若不是当天，则回复响应数据
        {
            uploadingTheStepAndSleepData(getFormetDay(year, month, day));
            byte[] bytess = new byte[4];
            bytess[0] = (byte)day;
            bytess[1] = (byte)month;
            bytess[2] = (byte)year;
            bytess[3] = (byte)0x00;
            setMsgToByteDataAndSendToDevice(toDevice, bytess, bytess.length);

        }
        // 存入数据库
//        String date = "20" + year + "-" + month + "-" + day;
        String userAccount = UserAccountUtil.getAccount(mContext);
        MyDBHelperForDayData helper = MyDBHelperForDayData.getInstance(mContext);   // 拿到数据库帮助类


        // 先查询当天数据库是否有数据
        Cursor mCursor = helper.selecteDayData(mContext, userAccount, dataDate);
        if(mCursor.getCount() != 0)
        {   // 数据库有数据则更新
            helper.updateDayDataToday(mContext, userAccount, dataDate, stepAll,
                    calorie, mileage, String.valueOf(movementTime), moveCalorie, String.valueOf(sitTime), sitCalorie);
        }else
        {   // 无数据则直接添加
            helper.insert(mContext, userAccount, dataDate, stepAll, calorie, mileage,
                          String.valueOf(movementTime), moveCalorie, String.valueOf(sitTime), sitCalorie);
        }
        // 发送广播通知更新界面
        mContext.sendBroadcast(new Intent(MyConfingInfo.NOTIFY_MAINACTIVITY_TO_UPDATE_DAY_DATA));
    }

    private String getFormetDay(int year, int month, int day)
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.set(year, month - 1, day);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
    }

    private void uploadingTheStepAndSleepData(final String day)
    {
        String loginType = UserAccountUtil.getType(context);
        switch (loginType)
        {
            case MyConfingInfo.QQ_TYPE:
            case MyConfingInfo.WEICHART_TYPE:
            case MyConfingInfo.FACEBOOK_TYPE:
                if(NetStatus.isNetWorkConnected(context))
                {
                    checkThirdPartyTask tasks = new checkThirdPartyTask();
                    tasks.setOnLoginBackListener(new OnAllLoginBack()
                    {
                        @Override
                        public void onLoginBack()
                        {
                            new UpdateAttionMovementAndSleepData(context).executeOnExecutor(MyApplication.threadService, day);
                        }
                    });
                    tasks.executeOnExecutor(MyApplication.threadService, UserAccountUtil.getAccount(context), loginType, null, null, null);
                }
                break;
            case MyConfingInfo.NOMAL_TYPE:
                if(NetStatus.isNetWorkConnected(context))
                {
                    LoginOnBackground backLogin = new LoginOnBackground(context);
                    backLogin.setOnLoginBackListener(new OnAllLoginBack()
                    {
                        @Override
                        public void onLoginBack()
                        {
                            new UpdateAttionMovementAndSleepData(context).executeOnExecutor(MyApplication.threadService, day);
                        }
                    });
                    backLogin.executeOnExecutor(MyApplication.threadService);
                }
                break;
        }


    }


    private String formatTheDate(int year, int month, int day)
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.set(year, month - 1, day);
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(date);
    }


}
