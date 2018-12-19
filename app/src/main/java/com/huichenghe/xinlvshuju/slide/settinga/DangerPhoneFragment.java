package com.huichenghe.xinlvshuju.slide.settinga;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.huichenghe.bleControl.Ble.BleForLIftUpRemind;
import com.huichenghe.bleControl.Ble.DataSendCallback;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;

/**
 * A simple {@link Fragment} subclass.
 */
public class DangerPhoneFragment extends Fragment
{
    private CheckBox check;
    private final String LIFT_UP_THE_WRIST = "lift_up_the_wrist";
    public DangerPhoneFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return intit(container, inflater);
    }

    private View intit(ViewGroup container, LayoutInflater inflater)
    {
        View v = inflater.inflate(R.layout.fragment_danger_phone, container, false);
        intitArgs(v);
        return v;
    }

    private void intitArgs(View v)
    {
        check = (CheckBox) v.findViewById(R.id.swich_hang_up);
        v.findViewById(R.id.hang_up_switch).setOnClickListener(new NoDoubleClickListener()
        {
            @Override
            public void onNoDoubleClick(View view)
            {
                if(check.isChecked())
                {
                    check.setChecked(false);
                    LocalDataSaveTool.getInstance(getContext()).writeSp(LIFT_UP_THE_WRIST, String.valueOf(0));
                    BleForLIftUpRemind.getInstance().openLiftUp(false);
                }
                else
                {
                    check.setChecked(true);
                    LocalDataSaveTool.getInstance(getContext()).writeSp(LIFT_UP_THE_WRIST, String.valueOf(1));
                    BleForLIftUpRemind.getInstance().openLiftUp(true);
                }
            }
        });
        readLiftUpData();
    }

    DataSendCallback callback = new DataSendCallback() {
        @Override
        public void sendSuccess(byte[] receveData)
        {
            parseByteData(receveData);
        }

        @Override
        public void sendFailed() {

        }

        @Override
        public void sendFinished() {

        }
    };

    private void parseByteData(final byte[] receveData)
    {
        if(receveData[0] == 1)
        {
            if(receveData[1] == 6)
            {
                LocalDataSaveTool.getInstance(getContext()).writeSp(LIFT_UP_THE_WRIST, String.valueOf(receveData[2] & 0xff));
                if(getActivity() == null)return;
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        switch (receveData[2] & 0xff)
                        {
                            case 0:
                                if(check.isChecked())
                                {
                                    check.setChecked(false);
                                }
                                break;
                            case 1:
                                if(!check.isChecked())
                                {
                                    check.setChecked(true);
                                }
                                break;
                        }
                    }
                });
            }
        }
        else if(receveData[0] == 0)
        {
        }
    }


    private void readLiftUpData()
    {
        String state = LocalDataSaveTool.getInstance(getContext()).readSp(LIFT_UP_THE_WRIST);
        if(state != null && state.equals(""))
        {
            if(check.isChecked())
            {
                check.setChecked(false);
            }
        }
        else
        {
            switch (state)
            {
                case "0":
                    if(check.isChecked())
                    {
                        check.setChecked(false);
                    }
                    break;
                case "1":
                    if(!check.isChecked())
                    {
                        check.setChecked(true);
                    }
                    break;
            }
        }
        BleForLIftUpRemind.getInstance().setLiftUpListener(callback);
        BleForLIftUpRemind.getInstance().requestLiftUpData();
    }

}
