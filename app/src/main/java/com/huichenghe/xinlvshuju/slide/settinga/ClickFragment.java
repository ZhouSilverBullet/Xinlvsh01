package com.huichenghe.xinlvshuju.slide.settinga;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.huichenghe.bleControl.Ble.DataSendCallback;
import com.huichenghe.xinlvshuju.Adapter.RemindCustomRecyclerAdapter;
import com.huichenghe.xinlvshuju.Adapter.WrapContentLinearLayoutManager;
import com.huichenghe.bleControl.Ble.BleDataForCustomRemind;
import com.huichenghe.xinlvshuju.DataEntites.CustomRemindEntity;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClickFragment extends Fragment
{
    private static String TAG = ClickFragment.class.getSimpleName();
    private RemindCustomRecyclerAdapter mRemindCustomRecyclerAdapter;
    private ArrayList<CustomRemindEntity> dataList;
    private SwipeRefreshLayout refreshTheRemind;
    private int MESSAGE_NAME = 0;
    private int remindNumbler = 0;
    private Activity mActivity;
    private int number = 0;
    private int count = 0;
    private Handler remindHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 0:
                    if(refreshTheRemind.isRefreshing())
                    {
                        refreshTheRemind.setRefreshing(false);
                    }
                    break;
            }
        }
    };
    public ClickFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return initView(inflater, container);
    }

    private View initView(LayoutInflater inflater, ViewGroup container)
    {
        dataList = new ArrayList<>();
        View view = inflater.inflate(R.layout.custom_layout_for_clock_remind, container, false);
        addMyClock(view);
        mRemindCustomRecyclerAdapter = new RemindCustomRecyclerAdapter(mActivity, dataList);
        RecyclerView mRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_layout_show_remind);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(RemindActivity.this));
        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(mActivity));
        mRecyclerView.setAdapter(mRemindCustomRecyclerAdapter);
        mRemindCustomRecyclerAdapter.setOnEachButtonClickListener(new RemindCustomRecyclerAdapter.OnEachButtonClickListener()
        {
            @Override
            public void onDeleteButtonClick(int position) {
                deleteThisItem(position, dataList, mRemindCustomRecyclerAdapter);
            }
            @Override
            public void onItemClick(int position) {
                byte[] bytess = new byte[dataList.size()];

                for (int i = 0; i < dataList.size(); i++) {
                    CustomRemindEntity en = dataList.get(i);
                    byte aa = en.getNumber();     // 获取提醒编号
                    bytess[i] = aa;
                }
                Intent intent = new Intent();
                intent.setClass(mActivity.getApplicationContext(), SubCustonRemindActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(MyConfingInfo.SEND_TO_THE_ACTIVITY, bytess);
                intent.putExtra(MyConfingInfo.DETAIL_REMIND, (CustomRemindEntity) dataList.get(position));
                startActivity(intent);
            }
        });
        refreshTheRemind = (SwipeRefreshLayout)view.findViewById(R.id.refresh_costum_remind);
        refreshTheRemind.setProgressViewOffset(false, 1, 100);
        refreshTheRemind.setRefreshing(true);
        remindHandler.sendEmptyMessageDelayed(0, 1500);
        refreshTheRemind.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                getAllRemindData();
            }
        });
        return view;
    }

    private void addMyClock(View view)
    {
        RelativeLayout relaAdd = (RelativeLayout)view.findViewById(R.id.add_my_clock);
        relaAdd.setOnClickListener(new NoDoubleClickListener()
        {
            @Override
            public void onNoDoubleClick(View v)
            {
                openCusotmView();
            }
        });
    }

    private void openCusotmView()
    {
        byte[] bytess = new byte[dataList.size()];
        if(dataList.size() != 0)
        {
            for (int i = 0; i < dataList.size(); i ++)
            {
                CustomRemindEntity en = dataList.get(i);
                byte aa = en.getNumber();     // 获取提醒编号
                bytess[i] = aa;
            }
        }
        if(dataList.size() >= 8)
        {
            Toast.makeText(mActivity, R.string.remind_number, Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent intent = new Intent();
            intent.setClass(mActivity.getApplicationContext(), SubCustonRemindActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(MyConfingInfo.SEND_TO_THE_ACTIVITY, bytess);
            startActivity(intent);
        }
    }

    private void deleteThisItem(final int position, final ArrayList<CustomRemindEntity> datas, final RemindCustomRecyclerAdapter adap)
    {
        CustomRemindEntity en = datas.get(position);
        byte number = en.getNumber();
        BleDataForCustomRemind cu = BleDataForCustomRemind.getCustomRemindDataInstance();
        cu.deletePx(number);
        datas.remove(position);
        adap.notifyDataSetChanged();
//        mRemindCustomRecyclerAdapter.removeItem(position);
//        cu.setOnDeleteListener(new BleDataForCustomRemind.DeleteCallback()
//        {
//            @Override
//            public void deleteCallback(byte a)
//            {
//            }
//        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BleDataForCustomRemind.getCustomRemindDataInstance().closeSendData();
    }

    // 获取自定义提醒内容
    class MyAsycnTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Void... params)
        {
//            new BleDataForCustomRemind().getCustomRemind();
            return null;
        }
    }


    @Override
    public void onResume()
    {
        super.onResume();
        getAllRemindData();
    }
    private void getAllRemindData()
    {
        dataList.clear();
        count = 0;
        remindNumbler = 0;
        mRemindCustomRecyclerAdapter.notifyDataSetChanged();
//        mHandler.sendEmptyMessageDelayed(MESSAGE_NAME, 80);
        BleDataForCustomRemind.getCustomRemindDataInstance().setOnRequesCallback(new DataSendCallback() {
            @Override
            public void sendSuccess(byte[] dataValid)
            {
//                new BleDataForCustomRemind.RequesCallback()
//                {
//                    @Override
//                    public void request(CustomRemindEntity entity)
//                    {
//                        if(entity == null)
//                        {
//                            // doNothing
//                        }
//                        else if(!dataList.contains(entity))
//                        {
//                            mRemindCustomRecyclerAdapter.addItem(entity, dataList.size());
//                        }
//                    }
//                    @Override
//                    public void requestOver() {
//                        if(refreshTheRemind.isRefreshing())
//                            refreshTheRemind.setRefreshing(false);
//                    }
//                }

                if(dataValid.length > 5)    // 长度大于4是有效数据
                {
                    if(dataValid[0] == (byte)0x00)  // 第一个字节00代表是读取自定义提醒返回的数据
                    {
                        boolean isCustom = false;
                        CustomRemindEntity entity = new CustomRemindEntity();
                        entity.setNumber(dataValid[1]);    // 设置此对象的编号
                        byte type = dataValid[2];          // 提醒类型，决定图片的选择
                        entity.setType(type);
                        if(type == (byte)0x06)
                        {
                            isCustom = true;
                        }
                        int count = dataValid[3] & 0xff;
                        entity.setCount(count);   // 提醒时间的个数
                        String timeS = "";
                        for (int i = 4; i < 4 + count * 2; i += 2)
                        {
                            int hour = dataValid[i] & 0xff;
                            int minute = dataValid[i + 1] & 0xff;
                            String a = ((hour < 10) ? ("0" + hour) : String.valueOf(hour)) + ":" + ((minute < 10) ? ("0" + minute) : String.valueOf(minute));
                            timeS = timeS + ((timeS.length() == 0) ? "" : "/") + a;
                        }
                        entity.setTimes(timeS);
                        int ind = 3 + 2 * count + 1;
                        byte week = dataValid[ind];
                        String weekS = FormatWeeks(mActivity.getApplicationContext(), week);          // 构建星期显示格式
                        if(weekS != null && weekS.equals(""))
                        {
                            if(getContext() == null)
                            {
                                return;
                            }
                            weekS = getContext().getApplicationContext().getString(R.string.only_once);
                        }
                        entity.setRepeats(weekS);
                        entity.setWeekMetadata(week);
                        if(isCustom)
                        {
                            String cusName = null;
                            int start = ind + 1;
                            int end = dataValid.length - 2;
                            byte[] customByte = new byte[end - start];
//                  314e d638 3153 314e 00
//                    Log.i(TAG, "自定义开始:" + start + "自定义结束:" + end + "数组长度:" + customByte.length);
                            for (int i = start; i < dataValid.length - 2; i += 2)
                            {
                                customByte[i - start] = dataValid[i + 1];
                                customByte[i - start + 1]
                                        = dataValid[i];
                            }
                            try {
                                cusName = new String(customByte, "Unicode");
                                cusName = cusName.trim();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
//                    Log.i(TAG, "自定义名称:" + cusName + "自定义byte:" + FormatUtils.bytesToHexString(customByte) + "数组长度" + customByte.length);
                            entity.setCustomName(cusName);
                        }
                        if(!dataList.contains(entity))
                        {
                            mRemindCustomRecyclerAdapter.addItem(entity, dataList.size());
                        }
                    }
                }
                else
                {
                }
            }

            @Override
            public void sendFailed() {

            }

            @Override
            public void sendFinished()
            {
                if(refreshTheRemind.isRefreshing())
                refreshTheRemind.setRefreshing(false);
            }
        });
        BleDataForCustomRemind.getCustomRemindDataInstance().getTheCustomRemind(remindNumbler);
    }


    private String FormatWeeks(Context mContext, byte week)
    {
        String ret = "";
        if(week == (byte)0x7f)
        {
            ret = mContext.getString(R.string.everyday);
        }
        else
        {
            String[] weekName = new String[]
                    {
//                    <string name="sunday">日</string>
//                    <string name="monday">一</string>
//                    <string name="tuesday">二</string>
//                    <string name="wednesday">三</string>
//                    <string name="thursday">四</string>
//                    <string name="friday">五</string>
//                    <string name="saturday">六</string>
                            mContext.getString(R.string.sunday),
                            mContext.getString(R.string.monday),
                            mContext.getString(R.string.tuesday),
                            mContext.getString(R.string.wednesday),
                            mContext.getString(R.string.thursday),
                            mContext.getString(R.string.friday),
                            mContext.getString(R.string.saturday),
                    };

            for (int i = 0; i < 7; i ++)
            {
                if((week >> i & (byte)0x01) == (byte)0x01)  // week依次右移，并做and运算，若结果为0x01,则代表有对应的天
                {
                    if(!ret.equals(""))
                    {
                        ret += "/";
                    }
                    ret += weekName[i];
                    Log.i(TAG, "重复周:" + weekName[i]);
                }

            }
        }
        return ret;
    }




}
