package com.huichenghe.xinlvshuju.UpdataService;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.huichenghe.xinlvshuju.DataEntites.OutLineDataEntity;
import com.huichenghe.xinlvshuju.EachLoginHelper;
import com.huichenghe.xinlvshuju.http.UserAccountUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateHistoryDataService extends Service {
    public static final String TAG = "updateData";
    private DayDataHelper mDayDataHelper;
    private SleepDataHelper mSleepDataHelper;
    private EachStepDataHelper mEachStepDataHelper;
    private HrDataHelper mHrDataHelper;
    private HRVDataHelper mHRVDataHelper;
    private OutLineDataHelper mOutLineDataHelper;
    private BloodDataHelper mBloodDataHelper;
    private static UpdateHistoryDataService mUpdateService;
    private final int UPLOAD_TASK = 0;
    private Timer timer = null;
    private Handler handler = new Handler();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        Log.i(TAG, "上传服务已绑定。。。");
        upLoadData();
        return new UpdateHistoryBind();
    }


    @Override
    public boolean onUnbind(Intent intent) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        return super.onUnbind(intent);
    }

    public static UpdateHistoryDataService getInstance() {
        return mUpdateService;
    }

    public class UpdateHistoryBind extends Binder {
        public UpdateHistoryDataService getService() {
            return UpdateHistoryDataService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        mUpdateService = this;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /**
     * 上传历史数据线程
     */
    class checkThread extends Thread {
        @Override
        public void run() {
            final ArrayList<String> needDates = getNeedDays();
            new EachLoginHelper(getApplicationContext(), new SendDataCallback() {
                @Override
                public void sendDataSuccess(String reslult) {
                    Log.i(TAG, "线程上传数据登录成功");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            dealTask(needDates);
                        }
                    });
                }

                @Override
                public void sendDataFailed(String result) {
                }

                @Override
                public void sendDataTimeOut() {
                }
            });
        }
    }

    public void upLoadData() {
        startTimer();
    }

    private ArrayList<String> needDates;

    /**
     * 定时上传数据
     */
    private void startTimer() {
        if (timer == null) timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (UserAccountUtil.getAccount(getApplicationContext()).equals("")) {
                    return;
                }
                needDates = getNeedDays();
                if (needDates.size() < 0) return;
                new EachLoginHelper(getApplicationContext(), new SendDataCallback() {
                    @Override
                    public void sendDataSuccess(String reslult) {
                        Log.i(TAG, "线程上传数据登录成功");
                        dealTask(needDates);
                    }

                    @Override
                    public void sendDataFailed(String result) {
                    }

                    @Override
                    public void sendDataTimeOut() {
                    }
                });
            }
        }, 3 * 60 * 1000, 30 * 60 * 1000);
    }

    /**
     * 得到目前为止前7天的时间list
     * @return
     */
    public ArrayList<String> getNeedDays() {
        ArrayList<String> al = new ArrayList<>(7);
        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        for (int i = 0; i < 7; i++) {
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.add(Calendar.DATE, -i);
            Date d = calendar.getTime();
            String needChecoDay = formate.format(d);
            Log.i(TAG, "线程上传开启需要上传的日期添加到list：" + needChecoDay);
            al.add(needChecoDay);
        }
        return al;
    }


    private boolean isSendding = false;

    public void dealTask(ArrayList<String> needDayss) {
        if (needDayss.size() <= 0) return;
        if (needDayss.size() > 0) {
            String day = needDayss.get(0);
            needDayss.remove(0);
            String account = UserAccountUtil.getAccount(getApplicationContext());
            checkAndUploadCurrentDayData(account, day);
        }
    }

    public void upTodayData() {
      //  new UploadTodayTask().start();
        String account = UserAccountUtil.getAccount(getApplicationContext());
        checkAndUploadCurrentDayData(account, getCurrentDate());
    }

    class UploadTodayTask extends Thread {
        @Override
        public void run() {
            super.run();
            new EachLoginHelper(getApplicationContext(), new SendDataCallback() {
                @Override
                public void sendDataSuccess(String reslult) {
                    Log.i(TAG, "线程上传数据登录成功");
                    String account = UserAccountUtil.getAccount(getApplicationContext());
                    checkAndUploadCurrentDayData(account, getCurrentDate());
                }

                @Override
                public void sendDataFailed(String result) {
                }

                @Override
                public void sendDataTimeOut() {
                }
            });

        }
    }

    private void checkAndUploadCurrentDayData(String account, String needChecoDay) {
        Log.i(TAG, "上传的日期：" + needChecoDay);
        uploadDayData(account, needChecoDay);
    }

    /**
     * 上传
     *
     * @param account
     * @param needChecoDay
     */
    private int count = 0;
    private void uploadDayData(final String account, final String needChecoDay) {
        Log.i(TAG, "上传" + needChecoDay + "全天数据");
        if (mDayDataHelper == null) {
            mDayDataHelper = new DayDataHelper(getApplicationContext());
        }
        mDayDataHelper.setSendCallback(new SendDataCallback() {
            @Override
            public void sendDataSuccess(String reslult) {
                if (reslult != null && reslult.equals("")) {
                    upLoadEachData(account, needChecoDay);
                    return;
                }
                try {
                    JSONObject json = new JSONObject(reslult);
                    String code = json.getString("code");
                    if (code != null && code.equals("9001")) //未登录
                    { count = count + 1;
                        if (count >= 3) {
                            count = 0;
                            return;
                        } else {
                            new EachLoginHelper(getApplicationContext(), new SendDataCallback() {
                                @Override
                                public void sendDataSuccess(String reslult) {
                                    Log.i(TAG, "线程上传数据登录成功");
                                    uploadDayData(account, needChecoDay);
                                }

                                @Override
                                public void sendDataFailed(String result) {
                                }

                                @Override
                                public void sendDataTimeOut() {
                                }
                            });
                        }
                    } else if (code != null && code.equals("9003"))    // 上传成功
                    {
                        upLoadEachData(account, needChecoDay);
                    } else if (code != null && code.equals("9004"))    // 上传失败
                    {
//                           uploadDayData(account, needChecoDay);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                    Log.i(TAG, "上传返回的结果：" + reslult);
            }

            @Override
            public void sendDataFailed(String result) {
            }

            @Override
            public void sendDataTimeOut() {
            }
        });

        mDayDataHelper.checkAndUpDayData(account, needChecoDay);
    }


    private void upLoadEachData(final String account, final String needChecoDay) {
        Log.i(TAG, "上传" + needChecoDay + "分时计步数据");
        // 分时计步， 卡路里
        if (mEachStepDataHelper == null) {
            mEachStepDataHelper = new EachStepDataHelper(getApplicationContext());
        }
        mEachStepDataHelper.setSendCallback(new SendDataCallback() {
            @Override
            public void sendDataSuccess(String reslult) {
                if (reslult != null && reslult.equals("")) {
                    upLoadEachCalorieData(account, needChecoDay);
                    return;
                }
                try {
                    JSONObject json = new JSONObject(reslult);
                    String code = json.getString("code");
                    if (code != null && code.equals("9001")) //未登录
                    {
                        new EachLoginHelper(getApplicationContext(), new SendDataCallback() {
                            @Override
                            public void sendDataSuccess(String reslult) {
                                Log.i(TAG, "线程上传数据登录成功");
                                upLoadEachData(account, needChecoDay);
                            }

                            @Override
                            public void sendDataFailed(String result) {
                            }

                            @Override
                            public void sendDataTimeOut() {
                            }
                        });
                    } else if (code != null && code.equals("9003"))    // 上传成功
                    {
                        upLoadEachCalorieData(account, needChecoDay);
                    } else if (code != null && code.equals("9004"))    // 上传失败
                    {
//                            uploadDayData(account, needChecoDay);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void sendDataFailed(String result) {
            }

            @Override
            public void sendDataTimeOut() {
            }
        });
        mEachStepDataHelper.checkAndUpEachStepData(account, needChecoDay);
    }

    /**
     * 上传分时卡路里数据
     *
     * @param account
     * @param needCheckDay
     */
    private void upLoadEachCalorieData(final String account, final String needCheckDay) {
        Log.i(TAG, "上传" + needCheckDay + "分时卡路里数据");
        // 分时计步， 卡路里
        if (mEachStepDataHelper == null) {
            mEachStepDataHelper = new EachStepDataHelper(getApplicationContext());
        }
        mEachStepDataHelper.setSendCallback(new SendDataCallback() {
            @Override
            public void sendDataSuccess(String reslult) {
                if (reslult != null && reslult.equals("")) {
                    upLoadSleepData(account, needCheckDay);
                    return;
                }
                try {
                    JSONObject json = new JSONObject(reslult);
                    String code = json.getString("code");
                    if (code != null && code.equals("9001")) //未登录
                    {
                        new EachLoginHelper(getApplicationContext(), new SendDataCallback() {
                            @Override
                            public void sendDataSuccess(String reslult) {
                                Log.i(TAG, "线程上传数据登录成功");
                                upLoadEachCalorieData(account, needCheckDay);
                            }

                            @Override
                            public void sendDataFailed(String result) {
                            }

                            @Override
                            public void sendDataTimeOut() {
                            }
                        });
                    } else if (code != null && code.equals("9003"))    // 上传成功
                    {
                        upLoadSleepData(account, needCheckDay);
                    } else if (code != null && code.equals("9004"))    // 上传失败
                    {
//                            uploadDayData(account, needChecoDay);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void sendDataFailed(String result) {
            }

            @Override
            public void sendDataTimeOut() {
            }
        });
        mEachStepDataHelper.checkAndUpEachCalorieData(account, needCheckDay);
    }


    private void upLoadSleepData(final String account, final String needChecoDay) {
        Log.i(TAG, "上传" + needChecoDay + "睡眠数据");
        // 睡眠数据
        if (mSleepDataHelper == null) {
            mSleepDataHelper = new SleepDataHelper(getApplicationContext());
        }
        mSleepDataHelper.setSendCallback(new SendDataCallback() {
            @Override
            public void sendDataSuccess(String reslult) {
                if (reslult != null && reslult.equals("")) {
                    upLoadHRData(account, needChecoDay);
                    return;
                }
                try {
                    JSONObject json = new JSONObject(reslult);
                    String code = json.getString("code");
                    if (code != null && code.equals("9001")) //未登录
                    {
                        new EachLoginHelper(getApplicationContext(), new SendDataCallback() {
                            @Override
                            public void sendDataSuccess(String reslult) {
                                Log.i(TAG, "线程上传数据登录成功");
                                upLoadSleepData(account, needChecoDay);
                            }

                            @Override
                            public void sendDataFailed(String result) {
                            }

                            @Override
                            public void sendDataTimeOut() {
                            }
                        });
                    } else if (code != null && code.equals("9003"))    // 上传成功
                    {
                        upLoadHRData(account, needChecoDay);
                    } else if (code != null && code.equals("9004"))    // 上传失败
                    {
//                            uploadDayData(account, needChecoDay);
//                        upLoadHRData(account, needChecoDay);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void sendDataFailed(String result) {
            }

            @Override
            public void sendDataTimeOut() {
            }
        });
        mSleepDataHelper.checkAndUpSleepData(account, needChecoDay);
    }


    private void upLoadHRData(final String account, final String needCheckDay) {
        Log.i(TAG, "上传" + needCheckDay + "心率数据");
        // 心率数据
        if (mHrDataHelper == null) {
            mHrDataHelper = new HrDataHelper(getApplicationContext());
        }
        mHrDataHelper.setSendCallback(new SendDataCallback() {
            @Override
            public void sendDataSuccess(String reslult) {
                if (reslult != null && reslult.equals("")) {
                    upLoadHRVData(account, needCheckDay);
                    return;
                }
                try {
                    JSONObject json = new JSONObject(reslult);
                    String code = json.getString("code");
                    if (code != null && code.equals("9001")) //未登录
                    {
                        new EachLoginHelper(getApplicationContext(), new SendDataCallback() {
                            @Override
                            public void sendDataSuccess(String reslult) {
                                Log.i(TAG, "线程上传数据登录成功");
                                upLoadHRData(account, needCheckDay);
                            }

                            @Override
                            public void sendDataFailed(String result) {
                            }

                            @Override
                            public void sendDataTimeOut() {
                            }
                        });
                    } else if (code != null && code.equals("9003"))    // 上传成功
                    {
                        upLoadHRVData(account, needCheckDay);
                    } else if (code != null && code.equals("9004"))    // 上传失败
                    {
//                            uploadDayData(account, needChecoDay);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void sendDataFailed(String result) {
            }

            @Override
            public void sendDataTimeOut() {
            }
        });
        mHrDataHelper.checkAndUpHrData(account, needCheckDay);
    }

    private void upLoadHRVData(final String account, final String needCheckDay) {
        Log.i(TAG, "上传" + needCheckDay + "HRV数据");
        // hrv数据
        if (mHRVDataHelper == null) {
            mHRVDataHelper = new HRVDataHelper(getApplicationContext());
        }
        mHRVDataHelper.setSendCallback(new SendDataCallback() {
            @Override
            public void sendDataSuccess(String reslult) {
                if (reslult != null && reslult.equals("")) {
                    upLoadOutlineData(account, needCheckDay);
                    return;
                }
                try {
                    JSONObject json = new JSONObject(reslult);
                    String code = json.getString("code");
                    if (code != null && code.equals("9001")) //未登录
                    {
                        new EachLoginHelper(getApplicationContext(), new SendDataCallback() {
                            @Override
                            public void sendDataSuccess(String reslult) {
                                Log.i(TAG, "线程上传数据登录成功");
                                upLoadHRVData(account, needCheckDay);
                            }

                            @Override
                            public void sendDataFailed(String result) {
                            }

                            @Override
                            public void sendDataTimeOut() {
                            }
                        });
                    } else if (code != null && code.equals("9003"))    // 上传成功
                    {
                        upLoadOutlineData(account, needCheckDay);
                    } else if (code != null && code.equals("9004"))    // 上传失败
                    {
//                            uploadDayData(account, needChecoDay);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void sendDataFailed(String result) {
            }

            @Override
            public void sendDataTimeOut() {
            }
        });
        mHRVDataHelper.checkAndUpHrvData(account, needCheckDay);
    }

    /**
     * 上传离线数据
     *
     * @param account
     * @param needCheckDay
     */
    private void upLoadOutlineData(final String account, final String needCheckDay) {
        Log.i(TAG, "上传" + needCheckDay + "离线数据");
        // 离线数据
        if (mOutLineDataHelper == null) {
            mOutLineDataHelper = new OutLineDataHelper(getApplicationContext());
        }
        mOutLineDataHelper.setSendCallback(new SendDataCallback() {
            @Override
            public void sendDataSuccess(String reslult) {
                if (reslult != null && reslult.equals("")) {
                    upLoadBloodData(account, needCheckDay);
                    return;
                }
                try {
                    JSONObject json = new JSONObject(reslult);
                    String code = json.getString("code");
                    if (code != null && code.equals("9001")) //未登录
                    {
                        new EachLoginHelper(getApplicationContext(), new SendDataCallback() {
                            @Override
                            public void sendDataSuccess(String reslult) {
                                Log.i(TAG, "线程上传数据登录成功");
                                upLoadOutlineData(account, needCheckDay);
                            }

                            @Override
                            public void sendDataFailed(String result) {
                            }

                            @Override
                            public void sendDataTimeOut() {
                            }
                        });
                    } else if (code != null && code.equals("9003"))    // 上传成功
                    {
                        upLoadBloodData(account, needCheckDay);
                    } else if (code != null && code.equals("9004"))    // 上传失败
                    {
//                            uploadDayData(account, needChecoDay);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void sendDataFailed(String result) {
            }

            @Override
            public void sendDataTimeOut() {
            }
        });
        mOutLineDataHelper.checkOutLineDat(account, needCheckDay);
    }

    private void upLoadBloodData(final String account, final String needCheckDay) {
        Log.i(TAG, "上传" + needCheckDay + "血压数据");
        // 血压数据
        if (mBloodDataHelper == null) {
            mBloodDataHelper = new BloodDataHelper(getApplicationContext());
        }
        mBloodDataHelper.setSendCallback(new SendDataCallback() {
            @Override
            public void sendDataSuccess(String reslult) {
                if (reslult != null && reslult.equals("")) {
                    upLoadNext();
                    return;
                }
                try {
                    JSONObject json = new JSONObject(reslult);
                    String code = json.getString("code");
                    if (code != null && code.equals("9001")) //未登录
                    {
                        new EachLoginHelper(getApplicationContext(), new SendDataCallback() {
                            @Override
                            public void sendDataSuccess(String reslult) {
                                Log.i(TAG, "线程上传数据登录成功");
                                upLoadBloodData(account, needCheckDay);
                            }

                            @Override
                            public void sendDataFailed(String result) {
                            }

                            @Override
                            public void sendDataTimeOut() {
                            }
                        });
                    } else if (code != null && code.equals("9003")) {
                        upLoadNext();
                        isSendding = false;
                    } else if (code != null && code.equals("9004")) {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void sendDataFailed(String result) {
            }

            @Override
            public void sendDataTimeOut() {
            }
        });
        mBloodDataHelper.checkBloodData(account, needCheckDay);
    }

    private void upLoadNext() {
        if (needDates == null || needDates.size() <= 0) return;
        if (needDates.size() > 0) {
            String day = needDates.get(0);
            needDates.remove(0);
            String account = UserAccountUtil.getAccount(getApplicationContext());
            checkAndUploadCurrentDayData(account, day);
        }
    }


    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formats.format(calendar.getTime());
    }

    public void getDayData(String date, String account, SendDataCallback callback) {
        new DayDataHelper(getApplicationContext()).getDayData(date, account, "allData", callback);
    }

    public void getDateStepsData(String date, String account, SendDataCallback callback) {
        new EachStepDataHelper(getApplicationContext()).getStepsData(date, account, "stepDay", callback);
    }

    public void getDateCaloriesData(String date, String account, SendDataCallback callback) {
        new EachStepDataHelper(getApplicationContext()).getCalorieData(date, account, "calorieDay", callback);
    }

    public void getDateSleepData(String date, String account, SendDataCallback callback) {
        new SleepDataHelper(getApplicationContext()).getdateSleepData(date, account, "sleepData", callback);
    }

    public void getDateHRData(String date, String account, SendDataCallback callback) {
        new HrDataHelper(getApplicationContext()).getdateHRData(date, account, "heartRate", callback);
    }

    public void getDateHRVData(String date, String account, SendDataCallback callback) {
        new HRVDataHelper(getApplicationContext()).getdateHRVData(date, account, "hrv", callback);
    }

    public void getOutLineData(String date, String account, SendDataCallback callback) {
        new OutLineDataHelper(getApplicationContext()).getdateHRVData(date, account, "offlineData", callback);
    }

    public void getBloodData(String date, String account, SendDataCallback callback) {
        new BloodDataHelper(getApplicationContext()).getDayBloodData(date, account, "bloodPressure", callback);
    }

    public void getStepTrendData(String date, String account, SendDataCallback callback) {
        new TrendDataHelper(getApplicationContext()).getDateTrend(date, account, "stepTrend", callback);
    }

    public void getSleepTrendData(String date, String account, SendDataCallback callback) {
        new TrendDataHelper(getApplicationContext()).getDateTrend(date, account, "sleepTrend", callback);
    }

    public void deleteOutlineData(OutLineDataEntity en, SendDataCallback callback) {
        new OutLineDataHelper(getApplicationContext()).deleteData("deleteOffline", en, callback);
    }

}