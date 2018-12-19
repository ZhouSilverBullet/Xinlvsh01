package com.huichenghe.xinlvshuju.slide.settinga;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huichenghe.bleControl.Ble.DataSendCallback;
import com.huichenghe.xinlvshuju.Adapter.MYRecyclerAdapter;
import com.huichenghe.bleControl.Ble.BleDataForCustomRemind;
import com.huichenghe.xinlvshuju.CustomView.CustomChooseTimeSelector;
import com.huichenghe.xinlvshuju.CustomView.SelfAdaptionLinearLayoutManager;
import com.huichenghe.xinlvshuju.DataEntites.CustomRemindEntity;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;
import com.huichenghe.xinlvshuju.mainpack.BaseActivity;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class SubCustonRemindActivity extends BaseActivity
{
    public static final String TAG = SubCustonRemindActivity.class.getSimpleName();
    private LinearLayout chooseType, chooseTime, chooseRepeat;
    private RelativeLayout mMove, mMedicine, mDrink, mSleep, mEat, mCustom, mShowTime, cancleShow;
    private LinearLayout mLinearLayout, mWeekSelect;
    private TextView showType, showTime, showRepeat, addTime;
    private TranslateAnimation translateAnimation;
    private Button okToSend;
    private EditText editCustomName;
    private boolean isShowingChooseDialog = false;
    private boolean isShowingTime = false;
    private PopupWindow mPopupWindow;
    private ArrayList<String> timeList;
    private RecyclerView recyclerView;
    private MYRecyclerAdapter myRecyclerAdapter;
    private CheckBox sunday, tuesday, monday, wednesday, thursday, saturday, friday;
    private ImageView deleteTime;
    byte[] dataNO;
    private int currentNumber = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_custon_remind);
        initToobar();
        intiView();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        dataNO = getIntent().getByteArrayExtra(MyConfingInfo.SEND_TO_THE_ACTIVITY);
        Log.i(TAG, "提醒编号：" + FormatUtils.bytesToHexString(dataNO));
        CustomRemindEntity en = (CustomRemindEntity)getIntent().getSerializableExtra(MyConfingInfo.DETAIL_REMIND);
        if(en != null)
        {
            currentNumber = en.getNumber();
//            byte remindType = en.getType();
            String repeateTime = en.getRepeats();
            String remindTimes = en.getTimes();
            typeInInt = en.getType();

            Log.i(TAG, "提醒内容::" + typeInInt + "--" + repeateTime + "--" + remindTimes + "--" + en.getNumber());
            if(typeInInt != 0)
            {
                String remindName = null;
                switch (typeInInt)
                {
                    case 1:
                        remindName = getString(R.string.movement_text);
                        break;
                    case 2:
                        remindName = getString(R.string.eat_foot);
                        break;
                    case 3:
                        remindName = getString(R.string.drink);
                        break;
                    case 4:
                        remindName = getString(R.string.take_medicine);
                        break;
                    case 5:
                        remindName = getString(R.string.sleep_text_choose);
                        break;
                    case 6:
//                        holder.mIcon.setImageResource(R.mipmap.custom_remind_icon);
                        remindName = en.getCustomName();
                        break;
                }
                showType.setText(remindName);
            }
            if(repeateTime != null && repeateTime.length() != 0)
            {
                showRepeat.setText(repeateTime);
                if(!repeateTime.equals(getString(R.string.only_once)) && !repeateTime.equals(getString(R.string.everyday)))
                {
                    String[] arr = null;
                    if(repeateTime.contains("/"))
                    {
                        arr = repeateTime.split("/");
                    }
                    else
                    {
                        arr = new String[1];
                        arr[0] = repeateTime;
                    }
                    for (int i = 0; i < arr.length; i++)
                    {
                        if(arr[i].equals(getString(R.string.sunday)))
                        {
                            sunday.setChecked(true);
                        }
                        else if(arr[i].equals(getString(R.string.monday)))
                        {
                            tuesday.setChecked(true);
                        }
                        else if(arr[i].equals(getString(R.string.tuesday)))
                        {
                            monday.setChecked(true);
                        }
                        else if(arr[i].equals(getString(R.string.wednesday)))
                        {
                            wednesday.setChecked(true);
                        }
                        else if(arr[i].equals(getString(R.string.thursday)))
                        {
                            thursday.setChecked(true);
                        }
                        else if(arr[i].equals(getString(R.string.friday)))
                        {
                            saturday.setChecked(true);
                        }
                        else if(arr[i].equals(getString(R.string.saturday)))
                        {
                            friday.setChecked(true);
                        }
                    }
                }
                else if(repeateTime != null && repeateTime.equals(getString(R.string.everyday)))
                {
                    sunday.setChecked(true);
                    monday.setChecked(true);
                    tuesday.setChecked(true);
                    wednesday.setChecked(true);
                    thursday.setChecked(true);
                    friday.setChecked(true);
                    saturday.setChecked(true);
                }
            }
            if(remindTimes != null && remindTimes.length() != 0)
            {
                showTime.setText(remindTimes);
                try
                {
                    String[] timess =  remindTimes.split("/");
                    if(timess != null && timess.length != 0)
                    {
                        for(int i = 0; i < timess.length; i++)
                        {
                            if(!timeList.contains(timess[i]))
                            {
                                timeList.add(timess[i]);
                                myRecyclerAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private void intiView()
    {
        okToSend = (Button)findViewById(R.id.ok_to_send_custom_remind);
        okToSend.setOnClickListener(listenterForChoose);
        timeList = new ArrayList<String>();
        myRecyclerAdapter = new MYRecyclerAdapter(SubCustonRemindActivity.this, timeList);
        chooseType = (LinearLayout)findViewById(R.id.choose_type_img);
        chooseTime = (LinearLayout)findViewById(R.id.choose_time_img);
        chooseRepeat = (LinearLayout)findViewById(R.id.choose_repeat_img);
        chooseType.setOnClickListener(listenterForChoose);
        chooseTime.setOnClickListener(listenterForChoose);
        chooseRepeat.setOnClickListener(listenterForChoose);
        mLinearLayout = (LinearLayout)findViewById(R.id.choose_layout_movement);
        mShowTime = (RelativeLayout)findViewById(R.id.show_time_layout);
        mMove = (RelativeLayout)findViewById(R.id.item_movement);
        cancleShow = (RelativeLayout)findViewById(R.id.cancle_show_layout);
        mMedicine = (RelativeLayout)findViewById(R.id.item_take_medicine);
        mDrink = (RelativeLayout)findViewById(R.id.item_drink);
        mSleep = (RelativeLayout)findViewById(R.id.item_sleep);
        mEat = (RelativeLayout)findViewById(R.id.item_eat);
        mCustom = (RelativeLayout)findViewById(R.id.item_custom);
        mMove.setOnClickListener(listenterForChoose);
        mMedicine.setOnClickListener(listenterForChoose);
        mDrink.setOnClickListener(listenterForChoose);
        mSleep.setOnClickListener(listenterForChoose);
        mEat.setOnClickListener(listenterForChoose);
        mCustom.setOnClickListener(listenterForChoose);
        showType = (TextView)findViewById(R.id.show_the_type);
        showTime = (TextView)findViewById(R.id.show_the_time);
        showRepeat = (TextView)findViewById(R.id.show_the_repeat);
        addTime = (TextView)findViewById(R.id.add_custom_time);
        addTime.setOnClickListener(listenterForChoose);
        cancleShow.setOnClickListener(listenterForChoose);
        recyclerView = (RecyclerView)findViewById(R.id.rv_showTime);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new SelfAdaptionLinearLayoutManager(SubCustonRemindActivity.this));
        recyclerView.setAdapter(myRecyclerAdapter);
        myRecyclerAdapter.setOnDeleteListener(new MYRecyclerAdapter.OnDeleteListener()
        {
            @Override
            public void onDelete(int position)
            {
                timeList.remove(position);
                myRecyclerAdapter.notifyDataSetChanged();
//                myRecyclerAdapter.notifyItemRemoved(position);
            }
            @Override
            public void onItemClic(final int position)
            {
                String times = timeList.get(position);
                String ho = times.substring(0, 2);
                String mi = times.substring(3);
                new CustomChooseTimeSelector(SubCustonRemindActivity.this, ho + ":" + mi, new CustomChooseTimeSelector.OnTimesChoose() {
                    @Override
                    public void onTimeChoose(String time) {
                        timeList.set(position, time);
                        myRecyclerAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        mWeekSelect = (LinearLayout)findViewById(R.id.week_select);
        sunday = (CheckBox)findViewById(R.id.sunday);
        tuesday = (CheckBox)findViewById(R.id.tuesday);
        monday = (CheckBox)findViewById(R.id.monday);
        wednesday = (CheckBox)findViewById(R.id.wednesday);
        thursday = (CheckBox)findViewById(R.id.thurday);
        saturday = (CheckBox)findViewById(R.id.saturday);
        friday = (CheckBox)findViewById(R.id.friday);
        sunday.setOnCheckedChangeListener(new checkListener());
        tuesday.setOnCheckedChangeListener(new checkListener());
        monday.setOnCheckedChangeListener(new checkListener());
        wednesday.setOnCheckedChangeListener(new checkListener());
        thursday.setOnCheckedChangeListener(new checkListener());
        saturday.setOnCheckedChangeListener(new checkListener());
        friday.setOnCheckedChangeListener(new checkListener());
    }
    StringBuffer bufferS = new StringBuffer();
//    ArrayList<String> listWeek = new ArrayList<String>(sos);
    String[] listWeek = new String[]{"无", "无", "无", "无", "无", "无", "无"};

    class checkListener implements CompoundButton.OnCheckedChangeListener
    {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            if(bufferS.length() != 0)
            {
                bufferS.delete(0, bufferS.length());
            }

            switch (buttonView.getId())
            {
                case R.id.sunday:
                    if(isChecked)
                    {
                        listWeek[0] = getString(R.string.sunday);
                    }
                    else
                    {
                        listWeek[0] = "无";
                    }
                    break;
                case R.id.tuesday:
                    if(isChecked)
                    {
                        listWeek[1] = getString(R.string.monday);
                    }
                    else
                    {
                        listWeek[1] = "无";
                    }
                    break;
                case R.id.monday:
                    if(isChecked)
                    {
                        listWeek[2] = getString(R.string.tuesday);
                    }
                    else
                    {
                        listWeek[2] = "无";
                    }
                    break;
                case R.id.wednesday:
                    if(isChecked)
                    {
                        listWeek[3] = getString(R.string.wednesday);
                    }
                    else
                    {
                        listWeek[3] = "无";
                    }
                    break;
                case R.id.thurday:
                    if(isChecked)
                    {
                        listWeek[4] = getString(R.string.thursday);
                    }
                    else
                    {
                        listWeek[4] = "无";
                    }
                    break;
                case R.id.saturday:
                    if(isChecked)
                    {
                        listWeek[5] = getString(R.string.friday);
                    }
                    else
                    {
                        listWeek[5] = "无";
                    }
                    break;
                case R.id.friday:
                    if(isChecked)
                    {
                        listWeek[6] = getString(R.string.saturday);
                    }
                    else
                    {
                        listWeek[6] = "无";
                    }
                    break;
            }
            int count = 0;
            for (int i = 0; i < listWeek.length; i ++)
            {
                String s = listWeek[i];
                Log.i(TAG, "当前周：" + s);
                if(!s.equals("无"))
                {
                    count ++;
                    bufferS.append(s);
                    bufferS.append("/");
                }
            }
            String aa;
            if(count == 7)
            {
                aa = getString(R.string.everyday);
            }
            else
            {
                aa = bufferS.toString().trim();
                if(aa != null)
                {
                    if(aa.length() > 2)
                    {
                        aa = aa.substring(0, bufferS.length() - 1);
                    }
                }
                if(aa == null || aa.equals(""))
                {
                    aa = getString(R.string.only_once);
                }
            }
            Log.i(TAG, "当前字符串：" + aa);
            showRepeat.setText(aa);
        }
    }

    private byte typeInInt;
    NoDoubleClickListener listenterForChoose = new NoDoubleClickListener()
    {
        @Override
        public void onNoDoubleClick(View v)
        {
            switch (v.getId())
            {
                case R.id.cancle_show_layout:
                    // 隐藏布局
                    if(translateAnimation != null)
                    translateAnimation.cancel();
                    mLinearLayout.setVisibility(View.GONE);
                    break;
                case R.id.choose_type_img:
                    // 显示选择提醒类型
                    // 执行动画
                    mLinearLayout.setVisibility(View.VISIBLE);
                    translateAnimation = new TranslateAnimation(0, 0, mLinearLayout.getHeight(), 0);
                    translateAnimation.setDuration(500);
                    mLinearLayout.setAnimation(translateAnimation);
                    translateAnimation.startNow();
                    isShowingChooseDialog = true;
                    break;
                case R.id.choose_time_img:
                    mShowTime.setVisibility(View.VISIBLE);
                    translateAnimation = new TranslateAnimation(0, 0, mShowTime.getHeight(), 0);
                    translateAnimation.setDuration(500);
                    mShowTime.setAnimation(translateAnimation);
                    translateAnimation.start();
                    isShowingTime = true;
                    break;
                case R.id.choose_repeat_img:
                    int isShow = mWeekSelect.getVisibility();// 获取控件是否隐藏或显示
                    if(isShow == View.VISIBLE)
                    {
                        mWeekSelect.setVisibility(View.GONE);
                    }
                    else
                    {
                        mWeekSelect.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.item_movement:
                    //  点击运动选择
                    // 隐藏布局
                    translateAnimation.cancel();
                    mLinearLayout.setVisibility(View.GONE);
                    showType.setText(getString(R.string.movement_text));
                    typeInInt = (byte)0x01;
                    isShowingChooseDialog = false;
                    break;
                case R.id.item_take_medicine:
                    translateAnimation.cancel();
                    mLinearLayout.setVisibility(View.GONE);
                    showType.setText(getString(R.string.take_medicine));
                    typeInInt = (byte)0x04;
                    isShowingChooseDialog = false;

                    break;
                case R.id.item_drink:
                    translateAnimation.cancel();
                    mLinearLayout.setVisibility(View.GONE);
                    showType.setText(getString(R.string.drink));
                    typeInInt = (byte)0x03;
                    isShowingChooseDialog = false;

                    break;
                case R.id.item_sleep:
                    translateAnimation.cancel();
                    mLinearLayout.setVisibility(View.GONE);
                    showType.setText(getString(R.string.sleep_text_choose));
                    typeInInt = (byte)0x05;
                    isShowingChooseDialog = false;
                    break;
                case R.id.item_eat:
                    translateAnimation.cancel();
                    mLinearLayout.setVisibility(View.GONE);
                    showType.setText(getString(R.string.eat_foot));
                    typeInInt = (byte)0x02;
                    isShowingChooseDialog = false;
                    break;
                case R.id.item_custom:
                    isShowingChooseDialog = false;
                    // 点击编辑
                    translateAnimation.cancel();
                    mLinearLayout.setVisibility(View.GONE);
                    final AlertDialog.Builder dialogs = new AlertDialog.Builder(SubCustonRemindActivity.this);
                    View view = LayoutInflater.from(SubCustonRemindActivity.this).inflate(R.layout.edit_custom_layout, null);
                    dialogs.setView(view);
                    dialogs.setCancelable(false);
                    editCustomName = (EditText)view.findViewById(R.id.edit_custom_name);
                    dialogs.setPositiveButton(getString(R.string.be_true), new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            String name = editCustomName.getText().toString().trim();
                            if(name != null && name.isEmpty())
                            {
                                Undismiss(dialog);
                                MyToastUitls.showToast(SubCustonRemindActivity.this, R.string.type_not_null, 1);
                                return;
                            }
                            int nameLength = 0;
                            try {

                                byte[] nameB = name.getBytes("Unicode");
                                if(nameB[0] == (byte)0xff && nameB[1] == (byte)0xfe)
                                {
                                    nameLength = nameB.length - 2;
                                }
                                else
                                {
                                    nameLength = nameB.length;
                                }

                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            int maxBytes = 44;
                            String cusLeng = LocalDataSaveTool.getInstance(getApplicationContext()).readSp(MyConfingInfo.CHECK_SUPPORT_CUSTOM_REMIND_MAX_LENGTH);
                            if(cusLeng != null && !cusLeng.equals(""))
                            {
                                maxBytes = Integer.valueOf(cusLeng);
                            }
                            if (nameLength > maxBytes)
                            {
                                Undismiss(dialog);
                                editCustomName.setError(getString(R.string.remind_name_to_long));
                                return;
                            }
                            dismiss(dialog);
                            showType.setText(name);
                        }
                    });
                    dialogs.setNegativeButton(getString(R.string.be_cancle), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss(dialog);
                        }
                    });
                    dialogs.create().show();
                    typeInInt = (byte)0x06;
                    break;

                case R.id.add_custom_time:
                    // 显示时间选择器
                    if(timeList.size() < 6)
                    {
                        new CustomChooseTimeSelector(SubCustonRemindActivity.this, "", new CustomChooseTimeSelector.OnTimesChoose() {
                            @Override
                            public void onTimeChoose(String time) {
                                if(!timeList.contains(time))
                                {
                                    timeList.add(time);
                                    compareAndSortAndAdd(timeList, time);
                                    myRecyclerAdapter.notifyDataSetChanged();
                                }
                                else
                                {
                                    MyToastUitls.showToast(SubCustonRemindActivity.this, R.string.time_already_has, 1);
                                }
                            }
                        });
                    }
                    else
                    {
                        MyToastUitls.showToast(SubCustonRemindActivity.this, R.string.time_count, 1);
                    }
                    break;
                case R.id.ok_to_send_custom_remind:
                    // 确定编辑完成，并发送数据到手环
                    String typeName = showType.getText().toString().trim();
                    String typeTime = showTime.getText().toString().trim();
                    String typeRepeat = showRepeat.getText().toString().trim();
                    // 先进行非空验证
                    boolean isOk = juageTheInput(typeName, typeTime, SubCustonRemindActivity.this);
//                    String timeS = timeList;
//                    String repeatS = listWeek;
                    Log.i(TAG, "提醒名字--:" + typeName);
                    if(isOk)
                    {
                        if(typeInInt == 6)
                        {
                            try
                            {
                                int len = 0;
                                byte[] bytess = typeName.getBytes("Unicode");
                                if(bytess[0] == (byte)0xff && bytess[1] == (byte)0xfe)
                                {
//                                    byte[] end = new byte[bytess.length - phone];
//                                    System.arraycopy(bytess, 0, end, phone, bytess.length - phone);
//                                    bytess = end;
                                    len = bytess.length - 2;
                                }
                                else
                                {
                                    len = bytess.length;
                                }

                                byteToSend = new byte[2 + len + 3 + (2 * timeList.size())];
//                                Log.i(TAG, "自定义提醒名称长度:" + bytess.length + "数组长度:" + byteToSend.length);
                            } catch (UnsupportedEncodingException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            byteToSend = new byte[2 + (timeList.size()) * 2 + 3];
                        }
                        // 星期的一个字节
                        byte b = 0;
                        int a = 0;
                        for (int i = 0; i < 7; i ++)
                        {
                            String s = listWeek[i];
                            if(!s.equals("无"))
                            {
                                if(i >= 0)
                                {
                                    if(i == 7)
                                    {
                                        b |= (byte)0x01;
                                    }
                                    else
                                    {
                                        b |= ((byte)0x01 << i);
                                    }
                                }
                            }
                        }
                        // 运动类型
                        // 提醒时间点总数
                        int tim = timeList.size();
                        byte times = (byte)(tim & 0xff);
                        // 提醒时间
                        int s = 0;
                        byte[] bbb = new byte[tim * 2];
                        for (int i = 0; i < tim; i++)
                        {
                            String sub = timeList.get(i);
                            String[] timess = sub.split(":");
                            String hour = timess[0];
                            String minute = timess[1];
                            int hourI = Integer.parseInt(hour);
                            int minuteI = Integer.parseInt(minute);
                            byte hourB = (byte)(hourI & 0xff);
                            byte minuteB = (byte)(minuteI & 0xff);

                            bbb[i + s] = hourB;
                            bbb[i + 1 + s] = minuteB;
                            s++;
                        }
                        // 提醒编号
                        int length = 0;
                        int[] arr = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
                        if(currentNumber != -1)
                        {
                            length = currentNumber;
                        }
                        else
                        {
                            if(dataNO != null && dataNO.length != 0)
                            {

//                            length = dataNO.length;


                                for (int i = 0; i < dataNO.length; i++)
                                {
                                    Log.i(TAG, "提醒编号:" + dataNO[i]);
                                    int in = dataNO[i] & 0xff;
                                    for (int j = 0; j < arr.length; j ++)
                                    {
                                        if(in == arr[j])
                                        {
                                            arr[j] = -1;
                                        }
                                    }
                                }
                                for (int en = 0; en < arr.length; en++)
                                {
                                    if(arr[en] != -1)
                                    {
                                        length = arr[en];
                                        Log.i(TAG, "提醒发出编号:" + arr[en]);
                                        break;
                                    }
                                }
                            }
                            else
                            {
                                length = 0;
                            }
                        }
//                        dataNO[length] = (byte)(length * 0xff);
                        /**
                         * 整合数据
                         */
                        int index = 0;          // 标志位
                        byteToSend[index] = 0x01;   index ++;                    // 整合设置
                        byteToSend[index] = (byte)(length & 0xff);   index ++;   // 整合此条提醒的编号
                        // 整合提醒类型
                        byteToSend[index] = (byte)(typeInInt & 0xff);   index ++;
                        // 整合提醒时间总数
                        byteToSend[index] = times;  index ++;
                        // 整合提醒时间
                        for (int i = 0; i < bbb.length; i ++)
                        {
                            Log.i(TAG, "提醒时间在循环中：" + FormatUtils.bytesToHexString(bbb) + byteToSend.length);
                            byteToSend[i + 4] = bbb[i];

                            index ++;
                        }
                        // 整合重复天
                        byteToSend[index] = (byte)b; index ++;
                        // 整合提醒名称，此部分数据只在自定义提醒名称时发送
                        if(typeInInt == 6)
                        {
                            try {                                       // 整合运动类型
                                byte[] bateName = typeName.getBytes("Unicode");

                                Log.i(TAG,  "自定义提醒转换后:" + FormatUtils.bytesToHexString(bateName));
                                if(bateName[0] == (byte)0xff && bateName[1] == (byte)0xfe)
                                {
//                                    byte[] end = new byte[bytess.length - phone];
//                                    System.arraycopy(bytess, 0, end, phone, bytess.length - phone);
//                                    bytess = end;
//                                    len = bytess.length - phone;

                                    for (int j = 2; j < bateName.length; j ++)
                                    {
                                        byteToSend[(j - 2) + index] = bateName[j];
//                                    index ++;
                                    }
                                }
                                else
                                {

                                    for (int j = 0; j < bateName.length; j ++)
                                    {
                                        byteToSend[j + index] = bateName[j];
//                                    index ++;
                                    }
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        // 数据整合完成，向手环发送
                        BleDataForCustomRemind.getCustomRemindDataInstance().setOnRequesCallback(new DataSendCallback() {
                            @Override
                            public void sendSuccess(byte[] receveData) {
                            }

                            @Override
                            public void sendFailed() {

                            }

                            @Override
                            public void sendFinished()
                            {
                                SubCustonRemindActivity.this.onBackPressed();   // 发送完成返回上一个界面
                            }
                        });
                        byte[] dd=byteToSend;
                        BleDataForCustomRemind.getCustomRemindDataInstance().setCustomRingSettings(byteToSend);
//                                .setMsgToByteDataAndSendToDevice(
//                                        BleDataForCustomRemind.toDevice,
//                                        byteToSend, byteToSend.length);
                    }
                    break;
            }
        }
    };


    /**
     * 完成选择时间
     */
    private void completeSelectTime()
    {
        // 点击完成时间选择,将用户选择内容显示到showTime上
        StringBuffer buffer = new StringBuffer();
        if(timeList.size() != 0)
        {
            for (int i = 0; i < timeList.size(); i++)
            {
                // 遍历集合
                String s = timeList.get(i);
                if(s != null && !s.equals(""))
                {
                    buffer.append(s);
                    buffer.append("/");
                }
            }
            showTime.setText(buffer.toString().trim().substring(0, buffer.length() - 1));
        }
        mShowTime.setVisibility(View.GONE);
    }

    private void Undismiss(DialogInterface dialog)
    {
        Field field = null;
        try {
            field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dismiss(DialogInterface dialog)
    {
        Field fields = null;
        try {
            fields = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            fields.setAccessible(true);
            fields.set(dialog, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void UndissmissDialog(AlertDialog.Builder dialogs)
    {
        try {








        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean juageTheInput(String typeName, String typeTime, Context context)
    {
        if(typeName != null && typeName.equals(""))
        {
            MyToastUitls.showToast(context, R.string.type_not_null, 1);
            return false;
        }else if(typeTime != null && typeTime.equals(""))
        {
            MyToastUitls.showToast(context, R.string.time_not_null, 1);
            return false;
        }
        else
        {
            return true;
        }
    }

    byte[] byteToSend;
    private void compareAndSortAndAdd(ArrayList<String> timeList, String str)
    {
        if(timeList.size() == 0)
        {
            timeList.add(str);
        }
        else
        {
            boolean isCanInsert = true;
            for (int i = 0; i < timeList.size(); i++)
            {
                String time = timeList.get(i);
                if(time != null && time.equals(str))
                {
                    isCanInsert = false;
                }
            }

            if(isCanInsert)
            {
                timeList.add(str);
            }
            for (int j = 0; j < timeList.size(); j++)
            {
                for (int s = j + 1; s < timeList.size(); s++)
                {
                    String time1 = timeList.get(j);
                    String time2 = timeList.get(s);
                    String[] timeInList = time1.split(":");
                    String[] timeNew = time2.split(":");

                    if(Integer.parseInt(timeInList[0]) != Integer.parseInt(timeNew[0]))
                    {
                        if(Integer.parseInt(timeInList[0]) > Integer.parseInt(timeNew[0]))
                        {
//                            timeList.remove(j);
//                            timeList.remove(s);
//                            timeList.set()
                            timeList.set(j, time2);
                            timeList.set(s, time1);
                        }
                    }
                    else if(Integer.parseInt(timeInList[0]) == Integer.parseInt(timeNew[0]))
                    {
                        if(Integer.parseInt(timeInList[1]) > Integer.parseInt(timeNew[1]))
                        {
                            timeList.set(j, time2);
                            timeList.set(s, time1);
                        }
                    }
                }
            }



        }





    }


    private void initToobar()
    {
        TextView toolbar = (TextView) findViewById(R.id.title_bar);
        ImageView back = (ImageView)findViewById(R.id.title_back);
        back.setOnClickListener(new NoDoubleClickListener()
        {
            @Override
            public void onNoDoubleClick(View v)
            {
                if(isShowingChooseDialog)
                {
                    translateAnimation.cancel();
                    mLinearLayout.setVisibility(View.GONE);
                    isShowingChooseDialog = false;
                }
                else if(isShowingTime)
                {
                    completeSelectTime();
                    translateAnimation.cancel();
//                    mShowTime.setVisibility(View.GONE);
                    isShowingTime = false;
                }
                else if(!isShowingChooseDialog && !isShowingTime)
                {
                    SubCustonRemindActivity.this.onBackPressed();
                }

            }
        });

    }

    // 对虚拟键操作
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            if(isShowingChooseDialog)
            {
                translateAnimation.cancel();
                mLinearLayout.setVisibility(View.GONE);
                isShowingChooseDialog = false;
                return true;
            }
            if(isShowingTime)
            {
                completeSelectTime();
                translateAnimation.cancel();
//                mShowTime.setVisibility(View.GONE);
                isShowingTime = false;
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
