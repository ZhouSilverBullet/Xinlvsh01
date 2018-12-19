package com.huichenghe.xinlvshuju.mainpack;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huichenghe.bleControl.Ble.BleDataForHardVersion;
import com.huichenghe.bleControl.Ble.BleDataForOnLineMovement;
import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.bleControl.Ble.DataSendCallback;
import com.huichenghe.xinlvshuju.Adapter.MainPagerRecyclerAdapter;
import com.huichenghe.xinlvshuju.BleDeal.OutlineDataDealer;
import com.huichenghe.xinlvshuju.CustomView.CircleProgressDialog;
import com.huichenghe.xinlvshuju.CustomView.CustomChooseTimeSelector;
import com.huichenghe.xinlvshuju.CustomView.CustomChooseTypeDialog;
import com.huichenghe.xinlvshuju.CustomView.Custom_movement_progress;
import com.huichenghe.xinlvshuju.CustomView.Movement_deatail_window;
import com.huichenghe.xinlvshuju.DataEntites.OutLineDataEntity;
import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.EachLoginHelper;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.UpdataService.DeviceTypeUtils;
import com.huichenghe.xinlvshuju.UpdataService.SendDataCallback;
import com.huichenghe.xinlvshuju.UpdataService.UpdateHistoryDataService;
import com.huichenghe.xinlvshuju.Utils.DateUtils;
import com.huichenghe.xinlvshuju.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;
import com.huichenghe.xinlvshuju.Utils.NetStatus;
import com.huichenghe.xinlvshuju.http.UserAccountUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.blueware.agent.android.BlueWare.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class Movement_Fragment extends Fragment
{
    public final String TAG = "Movement_Fragment";
    private final int GET_LIVE_DATA_FROM_BRANCH = 0;
    private boolean isFirstData = false;
    private final String MOVEMENT_STEP = "movement_step";
    private final String MOVEMENT_KCAL = "movement_kcal";
    private final String MOVEMENT_HR = "movement_hr";
    private TextView targetTime;
    private long beginTime, endTime, pauseBeginTime, pauseEndTime;
    private long keepPauseTime, keepMovementTime;
    private TextView move_step, move_kcal, move_hr, move_keep_time;
    private CheckBox pauseOrRestart;
    private String currentDate;
    private ArrayList<OutLineDataEntity> outlineData;
    private Custom_movement_progress pro;
    private OutlineDataHelper outlineDataHelper;
    private MainPagerRecyclerAdapter itemAdapter;
    private LinearLayout endLayout, startLayout;
    private OutLineDataEntity movementEntity;
    private String movementBegginTime;
    private String movementEndTime;
    private int eachMinuteCount;
    private int timesCount = 60;
    private boolean isMovementing = false;
    private boolean isPauseing = false;
    private ImageView pauseButton, restartButton;



    private Handler movementHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if(msg.what == GET_LIVE_DATA_FROM_BRANCH)
            {
                if(msg.arg1 == 6)
                {
                    isFirstData = true;
                }
                else
                {
                    endTime = DateUtils.currentTimeSeconds();
                    movementEndTime = formateTime(endTime);
                    keepMovementTime = endTime - beginTime - keepPauseTime;
                    Log.i(TAG, "运动数据：beginTime:" + beginTime + "\t\tendTime:" + endTime + "\t\tkeepPauseTime:" + keepMovementTime);
                    Log.i(TAG, "运动数据：pauseBeginTime:" + pauseBeginTime + "\t\tpauseEndTime:" + pauseEndTime);
                    String keepTime = formatKeepTime(keepMovementTime);
                    move_keep_time.setText(keepTime);
                }
                BleDataForOnLineMovement.getBleDataForOutlineInstance().sendHRDataToDevice((byte)0x00);
                this.removeMessages(GET_LIVE_DATA_FROM_BRANCH);
                Message mzg = this.obtainMessage();
                mzg.what = GET_LIVE_DATA_FROM_BRANCH;
                mzg.arg1 = 1;
                if(!isHide)
                {
                    this.sendMessageDelayed(mzg, 1000);
                }
                else
                {
                    this.sendMessageDelayed(mzg, 20000);
                }
            }
        }
    };
    private boolean isHide = false;

    @Override
    public void onHiddenChanged(boolean hidden)
    {
        super.onHiddenChanged(hidden);
        isHide = hidden;
        if(hidden)
        {
            if(isMovementing)
            {
                timesCount = 4;
            }
        }
        else
        {
            timesCount = 60;
            if(isMovementing && !isPauseing)
            {
                movementHandler.removeMessages(GET_LIVE_DATA_FROM_BRANCH);
                Message mzg = movementHandler.obtainMessage();
                mzg.what = GET_LIVE_DATA_FROM_BRANCH;
                mzg.arg1 = 1;
                movementHandler.sendMessageDelayed(mzg, 1000);
            }
            getRemoteOutlineData(currentDate);
        }
    }

    public Movement_Fragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        updateHardVersion();
        return intitView(inflater, container);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        currentDate = ((MainActivity)activity).currenDate;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        updateOutlineData(currentDate);
        if(!Movement_Fragment.this.isHidden())
        {
            getRemoteOutlineData(currentDate);
        }
    }

    public void setDate(String currentDates)
    {
        this.currentDate = currentDates;
    }

    /**
     * 从服务器上下载数据1
     * @param currentDates
     */
    public void getRemoteOutlineData(final String currentDates)
    {
        System.out.print("hhp");
        Log.i("two","从服务器下载数据");
//        if(UpdateHistoryDataService.getInstance() != null && !UserAccountUtil.getAccount(getContext()).equals(""))
//        {
//            UpdateHistoryDataService.getInstance().getOutLineData(currentDate, UserAccountUtil.getAccount(getContext()), callback);
//        }
    }

    private void updateHardVersion()
    {
        if(BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice())
        {
            String v = LocalDataSaveTool.getInstance(getActivity().getApplicationContext()).readSp(MyConfingInfo.HARD_VERSION);
            if(v != null && v.equals(""))
            {
                BleDataForHardVersion.getInstance().setDataSendCallback(new DataSendCallback()
                {
                    @Override
                    public void sendSuccess(byte[] receveData)
                    {
                        Log.i(TAG, "收到的数据：" + FormatUtils.bytesToHexString(receveData));
                        byte hard = receveData[0];
                        byte bluetooth;
                        byte soft;
                        byte[] hardVerson = new byte[receveData.length - 4];
                        System.arraycopy(receveData, 0, hardVerson, 0, receveData.length - 4);
                        hardVerson = revresionBytes(hardVerson);
                        bluetooth = receveData[receveData.length - 4];
                        soft = receveData[receveData.length - 3];
                        final String versionString = FormatUtils.bytesToHexString(hardVerson) + "/"
                                + parseTheHexString(bluetooth) + "/"
                                + parseTheHexString(soft);
                        LocalDataSaveTool.getInstance(getApplicationContext()).writeSp(MyConfingInfo.HARD_VERSION, versionString);

                        Log.i(TAG, "设备版本号:" + versionString);
                    }
                    @Override
                    public void sendFailed(){}
                    @Override
                    public void sendFinished(){

                    }
                });
                BleDataForHardVersion.getInstance().requestHardVersion();
            }
            else
            {

            }
        }
        else
        {
        }
    }
    private byte[] revresionBytes(byte[] hardVerson)
    {
        byte[] reV = new byte[hardVerson.length];
        for (int i = 0; i < hardVerson.length; i++)
        {
            reV[i] = hardVerson[hardVerson.length - i - 1];
        }
        return reV;
    }

    private static String parseTheHexString(byte hard)
    {
        byte a = (byte)(hard >> 4);
        byte b = (byte)(hard & 0x0f);
        String a1 = Integer.toHexString(a);
        String b1 = Integer.toHexString(b);
        return a1 + "" + b1;
    }
    SendDataCallback callback = new SendDataCallback() {
        @Override
        public void sendDataSuccess(String reslult)
        {
            JSONObject json = null;
            try {
                json = new JSONObject(reslult);
                String code = json.getString("code");
                if(code != null && code.equals("9001"))
                {
                    new EachLoginHelper(getContext(), new SendDataCallback() {
                        @Override
                        public void sendDataSuccess(String reslult)
                        {
                            getRemoteOutlineData(currentDate);
                        }
                        @Override
                        public void sendDataFailed(String result){}
                        @Override
                        public void sendDataTimeOut(){}
                    });
                }
                else if(code != null && code.equals("9003"))
                {
                    Log.i(TAG, "远程离线数据：" + reslult);
                    new OutlineDataDealer(getContext().getApplicationContext(), reslult);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateOutlineData(currentDate);
                        }
                    });
                }
                else if(code != null && code.equals("9004"))    // 上传失败
                {
//                            uploadDayData(account, needChecoDay);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void sendDataFailed(String result) {}
        @Override
        public void sendDataTimeOut() {}
    };

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        movementHandler.removeMessages(GET_LIVE_DATA_FROM_BRANCH);
    }

    public void updateOutlineData(String date)
    {
        Log.i(TAG, "movement_fragment当前日期：" + currentDate);
        Cursor cursor = outlineDataHelper.readDbAndshow(date);
        outlineData.clear();
        if(cursor.getCount() > 0)
        {
            outlineData = outlineDataHelper.getTheDataFromCursor(cursor, outlineData);
        }
        else
        {
            if(currentDate != null && !currentDate.equals(getTodayDate()))
            {
                if(loadCount == 0)
                {
                    loadCount++;
                    getRemoteOutlineData(currentDate);
                }
            }
            else
            {
                if(BluetoothLeService.getInstance() != null && !BluetoothLeService.getInstance().isConnectedDevice())
                {
                    if(loadCount == 0)
                    {
                        loadCount++;
                        getRemoteOutlineData(currentDate);
                    }
                }
            }
            outlineData.add(new OutLineDataEntity(1, "2016-03-05 00:00=2016-03-05 00:00", 0, 0, "", "2016-03-05", getString(R.string.running)));
            outlineData.add(new OutLineDataEntity(2, "2016-03-05 00:00=2016-03-05 00:00", 0, 0, "", "2016-03-05", getString(R.string.running)));
        }
        itemAdapter.notifyDataSetChanged();
    }

    public int loadCount = 0;

    private String getTodayDate()
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(calendar.getTime());
    }

    private View intitView(LayoutInflater inflater, ViewGroup container)
    {
        View root = inflater.inflate(R.layout.fragment_movement_01, container, false);
        initFind(root);
        initRecycler(root);
        outlineDataHelper = new OutlineDataHelper(getContext());
        return root;
    }

    private void initRecycler(View root)
    {
        outlineData = new ArrayList<>();
        final RecyclerView outLineData = (RecyclerView)root.findViewById(R.id.recycle_listview_for_movement);
        outLineData.setHasFixedSize(true);
        outLineData.setLayoutManager(new LinearLayoutManager(getContext()));
        outLineData.setItemAnimator(new DefaultItemAnimator());
        itemAdapter = new MainPagerRecyclerAdapter(getContext(), outlineData);
        itemAdapter.setMyOnItemClickListener(itemClickListener);
        outLineData.setAdapter(itemAdapter);
    }

    MainPagerRecyclerAdapter.MyOnItemClickListener itemClickListener = new MainPagerRecyclerAdapter.MyOnItemClickListener()
    {
        @Override
        public void OnClick(int position)
        {
            final OutLineDataEntity en = outlineData.get(position);
            if(en.getType() == OutLineDataEntity.TYPE_UNKNOW)
            {
                new CustomChooseTypeDialog(getContext(), new CustomChooseTypeDialog.ChooseDialogCallback()
                {
                    @Override
                    public void onChoose(int Type, String sportName)
                    {
                        if(Type == OutLineDataEntity.TYPE_CUSTOM)
                        {
                            en.setSportName(sportName);
                        }
                        en.setType(Type);
                        updateTheOutLineDataType(en, Type, sportName);
                        itemAdapter.notifyDataSetChanged();
                        Movement_deatail_window.getPopWindowInstance().showTheOutlinDetialWindow(en, getActivity());
                    }
                    @Override
                    public void onDissmiss(){}
                    @Override
                    public void onCancle(){}
                });
            }
            else
            {
                Movement_deatail_window.getPopWindowInstance().showTheOutlinDetialWindow(en, getActivity());
            }
        }

        @Override
        public void OnDelete(int position)
        {
            final OutLineDataEntity entity = outlineData.get(position);
            final String[] times = entity.getTime().split("=");
            if(times[0].equals("2016-03-05 00:00") && times[1].equals("2016-03-05 00:00"))
            {
                return;
            }
            if(NetStatus.isNetWorkConnected(getContext().getApplicationContext()))
            {
                deleteThisItem(entity, times);
            }
            else
            {
                MyToastUitls.showToast(getActivity(), R.string.net_wrong, 1);
            }
        }

        @Override
        public void onScrollStateChanged(int newState)
        {
            if(newState == 0)
            {
                ((MainActivity)getActivity()).mResidemenu.menuUnLock();
            }
            else if(newState == 1)
            {
                ((MainActivity)getActivity()).mResidemenu.menuLock();
            }
        }
    };

    private void deleteThisItem(final OutLineDataEntity entity, final String[] times)
    {
        if(UpdateHistoryDataService.getInstance() != null)
        {
            //原先的删除需要请求服务器现在修改为离线数据只保存在本地
            CircleProgressDialog.getInstance().showCircleProgressDialog(getActivity());
            String usAccount = UserAccountUtil.getAccount(getContext());
            MyDBHelperForDayData.getInstance(getContext())
                    .deleteOutLineData(getContext(), usAccount, entity.getDay(), times[0], times[1], DeviceTypeUtils.getDeviceType(getContext().getApplicationContext()));
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateOutlineData(entity.getDay());
                    CircleProgressDialog.getInstance().closeCircleProgressDialog();
                }
            });
//            UpdateHistoryDataService.getInstance().deleteOutlineData(entity, new SendDataCallback() {
//                @Override
//                public void sendDataSuccess(String reslult)
//                {
//                    Log.i(TAG, "删除离线数据：" + reslult);
//
//
//                    try {
//                        JSONObject jsonObject = new JSONObject(reslult);
//                        String code = jsonObject.getString("code");
//                        if(code != null && code.equals("9003"))
//                        {
//                            String usAccount = UserAccountUtil.getAccount(getContext());
//                            MyDBHelperForDayData.getInstance(getContext())
//                                    .deleteOutLineData(getContext(), usAccount, entity.getDay(), times[0], times[1], DeviceTypeUtils.getDeviceType(getContext().getApplicationContext()));
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    updateOutlineData(entity.getDay());
//                                    CircleProgressDialog.getInstance().closeCircleProgressDialog();
//                                }
//                            });
//                        }
//                        else if(code != null && code.equals("9001"))
//                        {
//                            new EachLoginHelper(getActivity().getApplicationContext(), new SendDataCallback() {
//                                @Override
//                                public void sendDataSuccess(String reslult)
//                                {
//                                    deleteThisItem(entity, times);
//                                }
//                                @Override
//                                public void sendDataFailed(String result){}
//                                @Override
//                                public void sendDataTimeOut(){}
//                            });
//                        }
//                        else
//                        {
//                            CircleProgressDialog.getInstance().closeCircleProgressDialog();
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                @Override
//                public void sendDataFailed(String result){}
//                @Override
//                public void sendDataTimeOut() {}
//            });
        }
    }


    Runnable run = new Runnable()
    {
        @Override
        public void run()
        {
            ((MainActivity)getActivity()).mResidemenu.menuUnLock();
        }
    };

    private void updateTheOutLineDataType(OutLineDataEntity entity, int type, String sportName)
    {
        String times = entity.getTime();
        int index = times.indexOf("=");
        String startTime = times.substring(0, index);
        String endTime = times.substring(index + 1);
        String userAccount = UserAccountUtil.getAccount(getContext());
        MyDBHelperForDayData.getInstance(getContext())
                .updateOutLineData(getContext(), userAccount, entity.getDay(), startTime, endTime, "type", type, "sportName",
                sportName, DeviceTypeUtils.getDeviceType(getContext().getApplicationContext()), "0");
    }

    DataSendCallback sendCallback = new DataSendCallback() {
        @Override
        public void sendSuccess(final byte[] receveData)
        {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Log.i(TAG, "在线运动返回的数据：" + com.huichenghe.bleControl.Utils.FormatUtils.bytesToHexString(receveData));
                    if(receveData[0] == 0)
                    {
//                      00 5e 61000000 49000000 3b000000 00 4016
//                      00 48 17090000 34070000 b3000000 00 5316
                        int hrValue = receveData[1] & 0xff;
                        int stepValue = FormatUtils.byte2Int(receveData, 2);
                        int mileValue = FormatUtils.byte2Int(receveData, 6);
                        int kcalValue = FormatUtils.byte2Int(receveData, 10);
                        int paceValue = receveData[14] & 0xff;
                        if(isFirstData)
                        {
                            LocalDataSaveTool.getInstance(getContext()).writeSp(MOVEMENT_STEP, String.valueOf(stepValue));
                            LocalDataSaveTool.getInstance(getContext()).writeSp(MOVEMENT_KCAL, String.valueOf(kcalValue));
                            LocalDataSaveTool.getInstance(getContext()).writeSp(MOVEMENT_HR, String.valueOf(hrValue));
                            beginTime = DateUtils.currentTimeSeconds();
                            movementBegginTime = formateTime(beginTime);
                            isFirstData = false;
                            isMovementing = true;
                            isPauseing = false;
                            if(endLayout.getVisibility() == View.GONE)
                            {
                                endLayout.setVisibility(View.VISIBLE);
                                if(pauseButton.getVisibility() == View.GONE)
                                {
                                    pauseButton.setVisibility(View.VISIBLE);
                                }
                                if(restartButton.getVisibility() == View.VISIBLE)
                                {
                                    restartButton.setVisibility(View.GONE);
                                }
                            }
                            if(startLayout.getVisibility() == View.VISIBLE)
                            {
                                startLayout.setVisibility(View.GONE);
                            }
                        }
                        else
                        {
                            if(eachMinuteCount >= timesCount)eachMinuteCount = 0;
                            eachMinuteCount ++;
                            int steps = Integer.valueOf(LocalDataSaveTool.getInstance(getContext()).readSp(MOVEMENT_STEP));
                            int kcals = Integer.valueOf(LocalDataSaveTool.getInstance(getContext()).readSp(MOVEMENT_KCAL));
                            int hrs = Integer.valueOf(LocalDataSaveTool.getInstance(getContext()).readSp(MOVEMENT_HR));
                            Log.i(TAG, "movement::steps原始" + steps + "新" + stepValue + "--原始kcals;" + kcals + "新" + kcalValue);
                            final String moveSteps = String.valueOf(stepValue - steps);
                            final String moveKcal = String.valueOf(kcalValue - kcals);
                            final String moveHr = String.valueOf(hrValue);
                            movementEntity.setStepCount(Integer.valueOf(moveSteps));
                            movementEntity.setCalorie(Integer.valueOf(moveKcal));
                            if(eachMinuteCount == 1)
                            {
                                String hr;
                                if(moveHr != null && !moveHr.equals("") && !moveHr.equals("null"))
                                {
                                    hr = FormatUtils.byteToHexStringUn0X(receveData[1]);
                                }
                                else
                                {
                                    hr = FormatUtils.byteToHexStringUn0X((byte)0x00);
                                }
                                movementEntity.setHeartReat(movementEntity.getHeartReat() + hr);
                                Log.i(TAG, "movement::hr:" + movementEntity.getHeartReat() + hr);
                            }

                                    move_step.setText(moveSteps);
                                    move_kcal.setText(moveKcal);
                                    move_hr.setText(moveHr);
                                    pro.setProgress((int)getProgress(keepMovementTime));
                        }
                    }
                    else if(receveData[0] == 1)
                    {
                        Message mzg = movementHandler.obtainMessage();
                        mzg.what = GET_LIVE_DATA_FROM_BRANCH;
                        mzg.arg1 = 6;
                        movementHandler.sendMessage(mzg);
                    }
                }
            });
        }
        @Override
        public void sendFailed() {}
        @Override
        public void sendFinished() {}
    };

    private String formateTime(long beginTime)
    {
        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return formate.format(new Date(beginTime * 1000));
    }

    private float getProgress(long keepMovementTime)
    {
        String time = getViewTargetTime();
        String[] spTime = time.split(":");
        int TargetTimes = Integer.parseInt(spTime[0]) * 60 * 60 + Integer.parseInt(spTime[1]) * 60;
        int keepTimes = (int) keepMovementTime;
        Log.i(TAG, "运动数据：目标分钟：" + time);
        float pro = 0;
        if(keepTimes >= TargetTimes)
        {
            pro = 270;
        }
        else
        {
            if(keepTimes == 0)
            {
                pro = 0;
            }
            else
            {
                pro = ((float)keepTimes/(float)TargetTimes) * 270;
            }
        }
        Log.i(TAG, "运动数据：目标分钟完成度：" + pro + "  keepTimes: " + keepTimes + "  TargetTimes:" + TargetTimes);
        return pro;
    }

    private String formatKeepTime(long keepMovementTime)
    {
        long h = keepMovementTime / 3600;
        long m = keepMovementTime / 60 % 60;
        long s = keepMovementTime % 60;
        StringBuffer buffer = new StringBuffer();
        buffer.append((h < 10) ? ("0" + String.valueOf(h)) : String.valueOf(h));
        buffer.append(":");
        buffer.append((m < 10) ? ("0" + String.valueOf(m)) : String.valueOf(m));
        buffer.append(":");
        buffer.append((s < 10) ? ("0" + String.valueOf(s)) : String.valueOf(s));
        return buffer.toString();
    }

    /**
     * 计时的点击事件
     * @param root
     */
    private void initFind(View root)
    {
        startLayout = (LinearLayout) root.findViewById(R.id.star_movement_layout);
        endLayout = (LinearLayout)root.findViewById(R.id.end_movement_layout);
        final TextView start = (TextView)root.findViewById(R.id.start_icon);
        TextView end_movement = (TextView)root.findViewById(R.id.end_movement);
        NoDoubleClickListener movementClickListener = new NoDoubleClickListener()
        {
            @Override
            public void onNoDoubleClick(View v)
            {
                switch (v.getId())
                {
                    case R.id.start_icon:
                        if(!BluetoothAdapter.getDefaultAdapter().isEnabled())
                        {
                            MyToastUitls.showToast(getContext(), R.string.not_connecte, 1);
                            return;
                        }
                        if(BluetoothLeService.getInstance() != null &&  !BluetoothLeService.getInstance().isConnectedDevice())
                        {
                            MyToastUitls.showToast(getContext(), R.string.not_connecte, 1);
                            return;
                        }
                        if(checkVersion())
                        {
                            return;
                        }
                        start.setClickable(false);
                        movementHandler.postDelayed(new Runnable(){
                            @Override
                            public void run(){
                                start.setClickable(true);
                            }
                        }, 5000);
                        // 显示选择运动类型
                        new CustomChooseTypeDialog(getContext(), new CustomChooseTypeDialog.ChooseDialogCallback()
                        {
                            //选择类型之后的回调
                            @Override
                            public void onChoose(int Type, String sportName)
                            {
                                movementEntity = new OutLineDataEntity();
                                movementEntity.setType(Type);
                                movementEntity.setSportName(sportName);

                                BleDataForOnLineMovement.getBleDataForOutlineInstance().setOnSendRecever(sendCallback);
                                BleDataForOnLineMovement.getBleDataForOutlineInstance().sendHRDataToDevice((byte)0x01);
                            }
                            @Override
                            public void onDissmiss(){}
                            @Override
                            public void onCancle()
                            {
                                start.setClickable(true);
                            }
                        });
                        break;
                    case R.id.end_movement:
                        start.setClickable(true);
                        isMovementing = false;
                        movementHandler.removeMessages(GET_LIVE_DATA_FROM_BRANCH);
                        if(endLayout.getVisibility() == View.VISIBLE)
                        {
                            endLayout.setVisibility(View.GONE);
                        }
                        if(startLayout.getVisibility() == View.GONE)
                        {
                            startLayout.setVisibility(View.VISIBLE);
                        }
                        if(movementBegginTime == null || movementEndTime == null)
                        {
                            return;
                        }
                        if(movementBegginTime != null && movementBegginTime.equals("")
                                || movementEndTime != null && movementEndTime.equals(""))return;
                        // 2016-08-09
                        movementEntity.setTime(movementBegginTime + "=" + movementEndTime);
                        movementEntity.setDay(movementEndTime.substring(0, 10));
                        BleDataForOnLineMovement.getBleDataForOutlineInstance().sendHRDataToDevice((byte)0x02);
                        showAskDialog();
                        break;
                    case R.id.select_movement_target:
                        String time = getViewTargetTime();
                        new CustomChooseTimeSelector(getActivity(), time, new CustomChooseTimeSelector.OnTimesChoose()
                        {
                            @Override
                            public void onTimeChoose(String time)
                            {
//                                Log.i(TAG, "选择的时间" + time);
                                String[] chooseTime = time.split(":");
                                StringBuffer chooseT = new StringBuffer();
                                int h = Integer.parseInt(chooseTime[0]);
                                int min = Integer.parseInt(chooseTime[1]);
                                if(h != 0)
                                {
                                    chooseT.append(String.valueOf(h));
                                    chooseT.append("h");
                                }
                                if(min != 0)
                                {
                                    chooseT.append(String.valueOf(min));
                                    chooseT.append("min");
                                }
                                if(chooseT.toString().equals(""))chooseT.append("1min");
                                targetTime.setText(chooseT.toString());
                            }
                        });
                        break;
                    case R.id.pause_button:
                        if(restartButton.getVisibility() == View.GONE)
                        {
                            pauseButton.setVisibility(View.GONE);
                            restartButton.setVisibility(View.VISIBLE);
                            movementHandler.removeMessages(GET_LIVE_DATA_FROM_BRANCH);
                            pauseBeginTime = DateUtils.currentTimeSeconds();
                            isPauseing = true;
                        }
                        break;
                    case R.id.restart_button:
                        if(pauseButton.getVisibility() == View.GONE)
                        {
                            isPauseing = false;
                            restartButton.setVisibility(View.GONE);
                            pauseButton.setVisibility(View.VISIBLE);
                            pauseEndTime = DateUtils.currentTimeSeconds();
                            keepPauseTime += pauseEndTime - pauseBeginTime;
                            Message msg = movementHandler.obtainMessage();
                            msg.what = GET_LIVE_DATA_FROM_BRANCH;
                            msg.arg1 = 2;
                            movementHandler.sendMessage(msg);
                        }
                        break;
//                    pauseOrRestart = (CheckBox)root.findViewById(R.id.pause_or_restart);
//                    pauseOrRestart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//                    {
//                        @Override
//                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//                        {
//                            if(isChecked)
//                            {
//
//                            }
//                            else
//                            {
//
//                            }
//                        }
//                    });
                }
            }

        };
        end_movement.setOnClickListener(movementClickListener);
        start.setOnClickListener(movementClickListener);
        targetTime = (TextView) root.findViewById(R.id.select_movement_target);
        targetTime.setText("30min");
        targetTime.setOnClickListener(movementClickListener);
        pro = (Custom_movement_progress) root.findViewById(R.id.progress_movement);
//        pro.runAnimate(270);
        move_step = (TextView)root.findViewById(R.id.steps_show);
        move_kcal = (TextView)root.findViewById(R.id.kcal_show);
        move_hr = (TextView)root.findViewById(R.id.hear_show);
        move_keep_time = (TextView)root.findViewById(R.id.keep_move_time_01);
        pauseButton = (ImageView) root.findViewById(R.id.pause_button);
        restartButton = (ImageView)root.findViewById(R.id.restart_button);
        pauseButton.setOnClickListener(movementClickListener);
        restartButton.setOnClickListener(movementClickListener);
    }
//todo bug
    private boolean checkVersion()
    {
        boolean isNotSupport = false;
        String hardVersion = LocalDataSaveTool.getInstance(getContext()).readSp(MyConfingInfo.HARD_VERSION);
        String[] versions = hardVersion.split("/");
        int hardV = Integer.parseInt(versions[0], 16);
        int blueV = Integer.parseInt(versions[1], 16);
        int softV = Integer.valueOf(versions[2], 16);
        Log.i(TAG, "固件版本转后：" + hardV);
        if(hardV == 15 && softV <= 6)
        {
            isNotSupport = true;
        }
        if(hardV == 17 && softV <= 26)
        {
            isNotSupport = true;
        }
        if(hardV == 18 && softV <= 28)
        {
            isNotSupport = true;
        }
        if(hardV == 19 && softV <= 10)
        {
            isNotSupport = true;
        }
        if(hardV == 21 && softV <= 7)
        {
            isNotSupport = true;
        }
        if(hardV == 23 && blueV < 1)
        {
            isNotSupport = true;
        }
        if(isNotSupport)
        {
            showNotSupportDialog();
        }
        return isNotSupport;
    }

    private void showNotSupportDialog()
    {
        AlertDialog.Builder notSupport = new AlertDialog.Builder(getActivity());
        notSupport.setCancelable(false);
        notSupport.setTitle(R.string.alter);
        notSupport.setMessage(R.string.not_support_this_device);
        notSupport.setNegativeButton(R.string.be_true, null);
        notSupport.create().show();
    }

    private void showAskDialog()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage(R.string.save_movement_data);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(R.string.be_true, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Movement_deatail_window.getPopWindowInstance().showTheOutlinDetialWindow(movementEntity, getActivity());
                OutlineDataDealer outHelper = new OutlineDataDealer();
                outHelper.saveOutlineData(movementEntity, getContext());
                updateOutlineData(currentDate);
                resetAllTextView();
            }
        });
        alertDialog.setNegativeButton(R.string.be_cancle, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                resetAllTextView();
                movementEntity = null;
            }
        });
        alertDialog.create().show();
    }

    private void resetAllTextView()
    {
        LocalDataSaveTool.getInstance(getContext()).writeSp(MOVEMENT_STEP, String.valueOf(-1));
        LocalDataSaveTool.getInstance(getContext()).writeSp(MOVEMENT_KCAL, String.valueOf(-1));
        LocalDataSaveTool.getInstance(getContext()).writeSp(MOVEMENT_HR, String.valueOf(-1));
        beginTime = 0;
        endTime = 0;
        pauseBeginTime = 0;
        pauseEndTime = 0;
        keepPauseTime = 0;
        keepMovementTime = 0;
        eachMinuteCount = 0;
        move_step.setText("0");
        move_kcal.setText("0");
        move_hr.setText("0");
        move_keep_time.setText("00:00:00");
        pro.setProgress(0);
    }

    private String getViewTargetTime()
    {
        String tvTime = targetTime.getText().toString();
        if(!tvTime.contains("h"))
        {
            tvTime = "0h" + tvTime;
        }
        if(!tvTime.contains("min"))
        {
            tvTime = tvTime + "0min";
        }
        return tvTime.substring(0, tvTime.indexOf("h")) + ":" + tvTime.substring(tvTime.indexOf("h") + 1, tvTime.indexOf("m"));
    }
}
