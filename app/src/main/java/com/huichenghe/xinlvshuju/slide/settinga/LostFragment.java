package com.huichenghe.xinlvshuju.slide.settinga;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import com.huichenghe.bleControl.Ble.BleForLostRemind;
import com.huichenghe.bleControl.Ble.DataSendCallback;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

/**
 * A simple {@link Fragment} subclass.
 */
public class LostFragment extends Fragment
{
    private RelativeLayout switchLose;
    private CheckBox checkLost;
    private Activity mActivity;
    private final int CHENGE_LSOT_STATE = 0;
    private String OPENORNO = "open or no";

    private Handler lostHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case CHENGE_LSOT_STATE:
                    Bundle bundle = msg.getData();
                    boolean isOpen = bundle.getBoolean(OPENORNO);
                    if(isOpen)
                    {
                        if(!checkLost.isChecked())
                        checkLost.setChecked(true);
                        LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.LOST_WARNING, MyConfingInfo.REMIND_OPEN);
                    }
                    else
                    {
                        if(checkLost.isChecked())
                        checkLost.setChecked(false);
                        LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.LOST_WARNING, MyConfingInfo.REMIND_CLOSE);
                    }
                    break;
            }
        }
    };
    public LostFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return intiView(inflater, container);
    }

    private View intiView(LayoutInflater inflater, ViewGroup container)
    {
        View view = inflater.inflate(R.layout.fragment_lost, container, false);
        checkLost = (CheckBox)view.findViewById(R.id.check_lost);
        switchLose = (RelativeLayout)view.findViewById(R.id.switch_the_lose_remind);
        readCache();
        switchLose.setOnClickListener(new NoDoubleClickListener()
        {
            @Override
            public void onNoDoubleClick(View v)
            {
                if(checkLost.isChecked())
                {
                    checkLost.setChecked(false);
                    BleForLostRemind.getInstance().openAndHandler(false);
                    LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.LOST_WARNING, MyConfingInfo.REMIND_CLOSE);
                }
                else
                {
                    checkLost.setChecked(true);
                    BleForLostRemind.getInstance().openAndHandler(true);
                    LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).writeSp(MyConfingInfo.LOST_WARNING, MyConfingInfo.REMIND_OPEN);
                }
            }
        });

        getLostSwitchFromDevice();

        return view;
    }

    private void readCache()
    {
        String lost = LocalDataSaveTool.getInstance(mActivity.getApplicationContext()).readSp(MyConfingInfo.LOST_WARNING);
        if(lost != null && lost.equals(MyConfingInfo.REMIND_OPEN))
        {
            if(!checkLost.isChecked())
                checkLost.setChecked(true);
        }
        else
        {
            if(checkLost.isChecked())
                checkLost.setChecked(false);
        }
    }

    private void getLostSwitchFromDevice()
    {
        BleForLostRemind.getInstance().setLostListener(new LostDataListener());
        BleForLostRemind.getInstance().requestAndHandler();
    }


    class LostDataListener implements DataSendCallback
    {
        @Override
        public void sendSuccess(byte[] receveData)
        {
            if(receveData[0] == (byte)0x01)  // 读取
            {
                Message msg = Message.obtain();
                msg.what = CHENGE_LSOT_STATE;
                Bundle bundle = new Bundle();
                if(receveData[2] == (byte)0x00)
                {
                    bundle.putBoolean(OPENORNO, false);
                }
                else
                {
                    bundle.putBoolean(OPENORNO, true);
                }
                msg.setData(bundle);
                lostHandler.sendMessage(msg);
            }
        }
        @Override
        public void sendFailed()
        {

        }
        @Override
        public void sendFinished()
        {

        }
    }
}
