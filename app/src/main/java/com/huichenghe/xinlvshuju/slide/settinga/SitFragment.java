package com.huichenghe.xinlvshuju.slide.settinga;


import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huichenghe.bleControl.Ble.DataSendCallback;
import com.huichenghe.xinlvshuju.Adapter.SitRemindAdapter;
import com.huichenghe.bleControl.Ble.BleForSitRemind;
import com.huichenghe.xinlvshuju.CustomView.CustomChooseTimeSelector;
import com.huichenghe.xinlvshuju.CustomView.SelectTimeDurtion;
import com.huichenghe.bleControl.Entity.sitRemindEntity;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SitFragment extends Fragment
{
    private final String TAG = SitFragment.class.getSimpleName();
    private RelativeLayout sitSwitch;
    private PopupWindow addWindow;
    private RecyclerView recyclerView;
    private Activity mAcitivity;
    private SitRemindAdapter remindAdapter;
    private ArrayList<sitRemindEntity> dataList;
    private AlertDialog.Builder builder;
    private Handler myHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 0:
                    remindAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    Log.i(TAG, "没有久坐提醒");
                    if(builder == null)
                    {
                        builder = new AlertDialog.Builder(mAcitivity);
                        builder.setCancelable(false);
                        builder.setNegativeButton(R.string.be_true, null);
                        builder.setTitle(R.string.alter);
                        builder.setMessage(R.string.device_not_support);
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                builder = null;
                            }
                        });
                        builder.create();
                        builder.show();
                    }
                    break;
            }
        }
    };


    public SitFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mAcitivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return initView(inflater, container);
    }

    private View initView(LayoutInflater inflater, ViewGroup container)
    {
        View view = inflater.inflate(R.layout.sit_fragment, null);
        sitSwitch = (RelativeLayout)view.findViewById(R.id.sit_remind_switch);
        dataList = new ArrayList<>();
        remindAdapter = new SitRemindAdapter(mAcitivity, dataList);
        recyclerView = (RecyclerView)view.findViewById(R.id.sit_remind_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(mAcitivity));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(remindAdapter);
        sitSwitch.setOnClickListener(listener);
        readDeviceSitSetting();
        dealTheClick();
        return view;
    }

    private void dealTheClick()
    {
        remindAdapter.setOnSitItemClickListener(new SitRemindAdapter.OnSitItemClickListener() {
            @Override
            public void onSitClick(int position)
            {
               sitRemindEntity remindEntity =  dataList.get(position);
                showEditSitTimesWindow(remindEntity);
            }

            @Override
            public void onDelete(int position)
            {
                deleteThisItem(dataList.get(position));
                dataList.clear();
                remindAdapter.notifyDataSetChanged();
                BleForSitRemind.getInstance().sendToGetSitData();
            }
        });
    }

    NoDoubleClickListener listener = new NoDoubleClickListener()
    {
        @Override
        public void onNoDoubleClick(View v)
        {
            if(v == sitSwitch)
            {
                if(dataList.size() <= 3)
                {
                    showEditSitTimesWindow(null);
                }
                else
                {
                    MyToastUitls.showToast(mAcitivity, R.string.sit_remind_count, 1);
                }
            }
        }
    };

    private void showEditSitTimesWindow(sitRemindEntity en)
    {
        View view = getAddLayout(en);
        addWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addWindow.setFocusable(true);
        addWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        addWindow.setAnimationStyle(R.style.mypopupwindow_anim_style);
        addWindow.setBackgroundDrawable(new BitmapDrawable());
        addWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        addWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss()
            {
                addWindow = null;
            }
        });
    }

    private View getAddLayout(final sitRemindEntity en)
    {
        View view = LayoutInflater.from(mAcitivity).inflate(R.layout.add_sit_times, null);
        final TextView editBegin = (TextView)view.findViewById(R.id.edit_the_begin_time);
        final TextView editEnd = (TextView)view.findViewById(R.id.edit_the_end_time);
        final ImageView closeWindow = (ImageView)view.findViewById(R.id.close_add_times);
        final TextView beSave = (TextView)view.findViewById(R.id.save_the_time);
        final TextView select = (TextView)view.findViewById(R.id.select_time_durion);
        final String item;
        if(en != null)
        {
            editBegin.setText(en.getBeginTime());
            editEnd.setText(en.getEndTime());
            item = en.getDuration() + "\t" + getString(R.string.minute_up);
            boolean op = (en.getOpenOrno() == 0) ? false : true;
        }
        else
        {
            // 默认设置
            item = 30 + "\t" + getString(R.string.minute_has_prefix);
        }
        select.setText(item);

        NoDoubleClickListener addListener = new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v)
            {
                if(v == editBegin)
                {
                    String begin = editBegin.getText().toString();
                    showTimeWindow(editBegin, begin);
                }
                else if(v == editEnd)
                {
                    showTimeWindow(editEnd, editEnd.getText().toString());
                }
                else if(v == closeWindow)
                {
                    addWindow.dismiss();
                }
                else if(v == select)
                {
                    new SelectTimeDurtion(mAcitivity, item,  new SelectTimeDurtion.OnResultSelect() {
                        @Override
                        public void resultSelect(String result) {
                            select.setText(result);
                        }
                    });
                }
                else if(v == beSave)
                {
                    boolean isOk = checkInput(editBegin.getText().toString(),
                            editEnd.getText().toString(),
                            select.getText().toString(),
                            true);
                    if(isOk)
                    {
                        int num = 0;
                        String beginS = editBegin.getText().toString();
                        String endS = editEnd.getText().toString();
                        String[] duS = select.getText().toString().split("\\t");
                        int che = 1;
                        if(en != null)
                        {
                            num = en.getNumber();
                        }
                        else
                        {
                            num = getSitTiemNumber();
                        }
                        BleForSitRemind.getInstance().setSitData(new sitRemindEntity(num, che, beginS, endS, Integer.parseInt(duS[0])));
                        addWindow.dismiss();
                        dataList.clear();
                        remindAdapter.notifyDataSetChanged();
                        BleForSitRemind.getInstance().sendToGetSitData();
                    }
                }
            }
        };
        editBegin.setOnClickListener(addListener);
        editEnd.setOnClickListener(addListener);
        closeWindow.setOnClickListener(addListener);
        beSave.setOnClickListener(addListener);
        select.setOnClickListener(addListener);
        return view;
    }

    private void deleteThisItem(sitRemindEntity en)
    {
        if(en == null)return;
        BleForSitRemind.getInstance().deleteThisItem(en.getNumber());
    }

    private int getSitTiemNumber()
    {
        int result = 0;
        int[] allNumber = {0, 1, 2, 3};
        for (int i = 0; i < dataList.size(); i++)
        {
            sitRemindEntity en = dataList.get(i);
            int each = en.getNumber();
            if(allNumber[i] == each)
            {
                allNumber[i] = -1;
            }
        }
        for (int j = 0; j < allNumber.length; j ++)
        {
            Log.i(TAG, "编号是：" + allNumber[j]);
            if(allNumber[j] != -1)
            {
                result = allNumber[j];
                break;
            }
        }
        return result;
    }

    private boolean checkInput(String beginTime, String endTime, String s2, boolean checked)
    {
        if(beginTime != null && beginTime.isEmpty())
        {
            MyToastUitls.showToast(mAcitivity, R.string.start_time_not_null, 1);
            return false;
        }
        if(endTime != null && endTime.isEmpty())
        {
            MyToastUitls.showToast(mAcitivity, R.string.end_time_not_null, 1);
            return false;
        }
        String[] begin = beginTime.split(":");
        String[] end = endTime.split(":");
        int beginTimes = Integer.parseInt(begin[0]) * 60 + Integer.parseInt(begin[1]);
        int endTimes = Integer.parseInt(end[0]) * 60 + Integer.parseInt(end[1]);
        if(endTimes < beginTimes)
        {
            MyToastUitls.showToast(mAcitivity, R.string.end_can_not_befor_start_time, 1);
            return false;
        }
        else if(endTimes == beginTimes)
        {
            MyToastUitls.showToast(mAcitivity, R.string.end_dengyu_start, 1);
            return false;
        }
        return true;
    }


    private void showTimeWindow(final TextView text, String begin)
    {
        new CustomChooseTimeSelector(mAcitivity, begin, new CustomChooseTimeSelector.OnTimesChoose()
        {
            @Override
            public void onTimeChoose(String time)
            {
                text.setText(time);
            }
        });
    }

    /**
     * 读取设备久坐提醒开关状态
     */
    private void readDeviceSitSetting()
    {
        BleForSitRemind.getInstance().setOnSitDataRecever(new SitRemindCallback());
        BleForSitRemind.getInstance().sendToGetSitData();
    }
    class SitRemindCallback implements DataSendCallback
    {

        @Override
        public void sendSuccess(byte[] bufferTmp)
        {
            if(bufferTmp[0] == (byte)0x00)
            {
                if(bufferTmp.length < 4)
                {
                    return;
                }
                else
                {
//                00 00010009030e3c
//                   03011f0c1f133c ff16
                    int count = (bufferTmp.length - 3)/7;
                    for (int i = 0; i < count; i++)
                    {
                        int number = bufferTmp[1 + i * 7] & 0xff;
                        int open = bufferTmp[2 + i * 7] & 0xff;
                        int beginM = bufferTmp[3 + i * 7] & 0xff;
                        int beginH = bufferTmp[4 + i * 7] & 0xff;
                        int endM = bufferTmp[5 + i * 7] & 0xff;
                        int endH = bufferTmp[6 + i * 7] & 0xff;
                        int duration = bufferTmp[7 + i * 7] & 0xff;
                        String beginTime = beginH + ":" + ((beginM < 10) ? ("0" + beginM) : beginM);
                        String endTime = endH + ":" + ((endM < 10) ? ("0" + endM) :endM);
                        dataList.add(new sitRemindEntity(number, open, beginTime, endTime, duration));
                    }
                    myHandler.sendEmptyMessage(0);
                }
            }
            else if(bufferTmp[0] == (byte)0x01)
            {
            }
            else if(bufferTmp[0] == (byte)0x02)
            {
            }

            // 异常数据在此处理
//            myHandler.sendEmptyMessage(1);

        }

        @Override
        public void sendFailed() {

        }

        @Override
        public void sendFinished() {

        }
    }
}