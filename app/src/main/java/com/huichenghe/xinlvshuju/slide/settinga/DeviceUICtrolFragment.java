package com.huichenghe.xinlvshuju.slide.settinga;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.huichenghe.bleControl.Ble.BleReadDeviceMenuState;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.FormatUtils;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.appRemind.ListViewDecoration;
import com.huichenghe.xinlvshuju.appRemind.listener.OnItemClickListener;
import com.huichenghe.xinlvshuju.views.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceUICtrolFragment extends Fragment {

    private DeviceInfoInfoAdapter mDeviceInfoInfoAdapter;
    private List<UiMenuInfo> uiMenuInfos;
    private int newDatas = -1;
    private ArrayList<Integer> witchs = new ArrayList<>();

    public DeviceUICtrolFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_deviceuicontrol, container, false);
        initView(inflate);
        return inflate;
    }

    private void initView(View inflate) {
        SwipeMenuRecyclerView menuRecyclerView = (SwipeMenuRecyclerView) inflate.findViewById(R.id.devimenu_ui);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        menuRecyclerView.setItemAnimator(new DefaultItemAnimator());
        menuRecyclerView.setHasFixedSize(true);
        menuRecyclerView.addItemDecoration(new ListViewDecoration());
        getUiMenuInfos();
        mDeviceInfoInfoAdapter = new DeviceInfoInfoAdapter(uiMenuInfos);
        menuRecyclerView.setAdapter(mDeviceInfoInfoAdapter);
    }

    public void getUIData()
    {
        String data = LocalDataSaveTool.getInstance(getContext().getApplicationContext()).readSp(MyConfingInfo.DEVICE_SHOW_UI);
        if(data == null || data.equals("") || data.length() <= 2)
        {
            addListener();
        }
        else
        {
            addNewListener();
        }
    }



    private void getUiMenuInfos()
    {
        if (uiMenuInfos == null)
            uiMenuInfos = new ArrayList<>();
        if (uiMenuInfos != null && uiMenuInfos.size() > 0)
            uiMenuInfos.clear();
        String data = LocalDataSaveTool.getInstance(getContext().getApplicationContext()).readSp(MyConfingInfo.DEVICE_SHOW_UI);
        if(data == null || data.equals("") || data.length() <= 2)
        {
            UiMenuInfo uiMenuSetting = new UiMenuInfo(getActivity().getResources().getDrawable(R.mipmap.shezhi), getString(R.string.setting), false);
            UiMenuInfo uiMenuKcal = new UiMenuInfo(getActivity().getResources().getDrawable(R.mipmap.kcal_device), getString(R.string.calorie_show), false);
            UiMenuInfo uiMenuKM = new UiMenuInfo(getActivity().getResources().getDrawable(R.mipmap.km_device), getString(R.string.km_show), false);
            UiMenuInfo uiMenuOutLine = new UiMenuInfo(getActivity().getResources().getDrawable(R.mipmap.active_device), getString(R.string.outLine_show), false);
            UiMenuInfo uiMenuHR = new UiMenuInfo(getActivity().getResources().getDrawable(R.mipmap.heat_device), getString(R.string.hr_show), false);
            UiMenuInfo uiMenuSteps = new UiMenuInfo(getActivity().getResources().getDrawable(R.mipmap.step_device), getString(R.string.step_show), false);
            uiMenuInfos.add(uiMenuSetting);
            uiMenuInfos.add(uiMenuKcal);
            uiMenuInfos.add(uiMenuKM);
            uiMenuInfos.add(uiMenuOutLine);
            uiMenuInfos.add(uiMenuHR);
            uiMenuInfos.add(uiMenuSteps);
        }
        else
        {
            witchs.clear();
            byte[] hasData = FormatUtils.hexString2ByteArray(data);
            int hasDatas = FormatUtils.byte2Int(hasData, 0);
            Log.i("pj", "转换后的数据：" + FormatUtils.bytesToHexString(hasData) + "--" + hasDatas);
            for (int i = 0; i < hasData.length * 8; i ++)
            {
                int res = getEachResult(i, hasDatas);
                if(res == 0)
                {
                    switch (i)
                    {
                        case 0:
                            witchs.add(i);
                            uiMenuInfos.add(new UiMenuInfo(getResources().getDrawable(R.mipmap.step_device), getString(R.string.step_show), false));
                            break;
                        case 1:
                            witchs.add(i);
                            uiMenuInfos.add(new UiMenuInfo(getResources().getDrawable(R.mipmap.heat_device), getString(R.string.hr_show), false));
                            break;
                        case 2:
                            witchs.add(i);
                            uiMenuInfos.add(new UiMenuInfo(getResources().getDrawable(R.mipmap.active_device), getString(R.string.outLine_show), false));
                            break;
                        case 3:
                            witchs.add(i);
                            uiMenuInfos.add(new UiMenuInfo(getResources().getDrawable(R.mipmap.km_device), getString(R.string.km_show), false));
                            break;
                        case 4:
                            witchs.add(i);
                            uiMenuInfos.add(new UiMenuInfo(getResources().getDrawable(R.mipmap.kcal_device), getString(R.string.calorie_show), false));
                            break;
                        case 5:
                            witchs.add(i);
                            uiMenuInfos.add(new UiMenuInfo(getResources().getDrawable(R.mipmap.sleep_device), getString(R.string.sleep_show), false));
                            break;
                        case 6:
                            witchs.add(i);
                            uiMenuInfos.add(new UiMenuInfo(getResources().getDrawable(R.mipmap.shezhi), getString(R.string.setting), false));
                            break;
                        case 7:
                            witchs.add(i);
                            uiMenuInfos.add(new UiMenuInfo(getResources().getDrawable(R.mipmap.blood_device), getString(R.string.blood_show), false));
                            break;
                        case 8:
                            witchs.add(i);
                            uiMenuInfos.add(new UiMenuInfo(getResources().getDrawable(R.mipmap.weather_device), getString(R.string.weather_show), false));
                            break;
                        case 9:
                            witchs.add(i);
                            uiMenuInfos.add(new UiMenuInfo(getResources().getDrawable(R.mipmap.unread_device), getString(R.string.unread_msg_show), false));
                            break;
                    }
                }
            }
        }
    }

    private byte[] changeOrder(byte[] hasData)
    {
        byte[] newBytes = new byte[hasData.length];
        for (int i = 0; i < hasData.length; i++)
        {
            newBytes[i] = hasData[hasData.length - i - 1];
        }
        return newBytes;
    }

    private int getEachResult(int i, int hasDatas)
    {
        return  ((hasDatas & 0xffffffff) >> i) & 0x01;
    }

    private void addNewListener()
    {
        BleReadDeviceMenuState.getInstance().setResultlistener(new BleReadDeviceMenuState.DevicemenuCallback() {
            @Override
            public void onGEtCharArray(byte[] receveData)
            {
//                receveData = FormatUtils.hexString2ByteArray("02000000005516");
                final byte[] data = new byte[receveData.length - 3];
                System.arraycopy(receveData, 1, data, 0, data.length);
                newDatas = FormatUtils.byte2Int(data, 0);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        for (int i = 0; i < data.length * 8; i ++)
                        {
                                    int res = getEachResult(i, newDatas);
                                    Log.i("Pj", "转换后的判断结果：" + res);
                                    if(res == 0)
                                    {
                                        doChangeState(i, true);
                                    }
                                    else
                                    {
                                        doChangeState(i, false);
                                    }
                        }
                        mDeviceInfoInfoAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        BleReadDeviceMenuState.getInstance().geistate((byte) 0x2c, (byte) 0x02);
    }

    private void doChangeState(int i, boolean b)
    {
        int re = compareHasThisFeather(i);
        if(re == -3)
        {
            return;
        }
        else
        {
            Log.i("pj", "设置位置：" + re + "结果：" + b);
            uiMenuInfos.get(re).setOpen(b);
        }
    }

    private int compareHasThisFeather(int i)
    {
        for (int j = 0; j < witchs.size(); j++)
        {
            Log.i("PJ", "转换后list里的数据：" + witchs.get(j));
            if(witchs.get(j) == i)
            {
                return j;
            }
        }
        return -3;
    }

    char[] data;
    private void addListener() {
        BleReadDeviceMenuState.getInstance().setResultlistener(new BleReadDeviceMenuState.DevicemenuCallback() {
            @Override
            public void onGEtCharArray(byte[] receveData)
            {
                String s = com.huichenghe.bleControl.Utils.FormatUtils.bytesToHexString(receveData).substring(2, 4);
                Log.e("PJ", "sendSuccess:2进制" + s);
                String sFoot = com.huichenghe.bleControl.Utils.FormatUtils.byteToHexString(receveData[receveData.length - 2]);
                if (sFoot.equals("0x16"))
                    return;
                Log.e("PJ", "sendSuccess:" + sFoot);
                String s1 = HToB(s);
                int th = Integer.parseInt(s1) + 100000000;
                String s2 = String.valueOf(th).substring(1);
                char[] chars = s2.toCharArray();
                Log.e("PJ", "sendSuccess16:" + s2);
                onGEtCharArrayFr(chars);
                DeviceUICtrolFragment.this.data = chars;
            }
        });
        BleReadDeviceMenuState.getInstance().geistate((byte) 0x2c, (byte) 0x02);
    }

    //16转2
    public String HToB(String a) {

        String b = Integer.toBinaryString(Integer.parseInt(a, 16));
        Log.e("PJ", "sendSuccessFormat:" + b);
        return b;
    }

    // 二进制转十六进制
    public String BToH(String a) {
        // 将二进制转为十进制再从十进制转为十六进制
        String b = Integer.toHexString(Integer.valueOf(toD(a, 2)));
        return b;
    }

    // 任意进制数转为十进制数
    public String toD(String a, int b) {
        int r = 0;
        for (int i = 0; i < a.length(); i++) {
            r = (int) (r + formatting(a.substring(i, i + 1))
                    * Math.pow(b, a.length() - i - 1));
        }
        return String.valueOf(r);
    }

    // 将十六进制中的字母转为对应的数字
    public int formatting(String a) {
        int i = 0;
        for (int u = 0; u < 10; u++) {
            if (a.equals(String.valueOf(u))) {
                i = u;
            }
        }
        if (a.equals("a")) {
            i = 10;
        }
        if (a.equals("b")) {
            i = 11;
        }
        if (a.equals("c")) {
            i = 12;
        }
        if (a.equals("d")) {
            i = 13;
        }
        if (a.equals("e")) {
            i = 14;
        }
        if (a.equals("f")) {
            i = 15;
        }
        return i;
    }

    @Override
    public void onResume() {
        super.onResume();
        getUIData();
    }


    void showLog(String s)
    {
        Log.e("PJ", s);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    //从蓝牙请求数据后的回调
    public void onGEtCharArrayFr(char[] chars)
    {
        showLog("Pj_chars的长度" + chars.length);
        for (int i = 0; i < chars.length; i++) {
            if (i == 0 || i == 2)
               continue;   // 1 3 4 5 6 7  -- 0 1 2 3 4 5
               if (chars[i] == '0') {
                    uiMenuInfos.get((i == 1) ? (i - 1) : (i - 2)).setOpen(true);
               } else {
                    uiMenuInfos.get((i == 1) ? (i - 1) : (i - 2)).setOpen(false);
               }
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDeviceInfoInfoAdapter.notifyDataSetChanged();

            }
        });
    }




//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        BleByteDataSendTool.getInstance().judgeTheDataLengthAndAddToSendByteArray();
//    }

    class DeviceInfoInfoAdapter extends RecyclerView.Adapter<DeviceInfoInfoAdapter.ViewHolder> {

        public DeviceInfoInfoAdapter(List<UiMenuInfo> uiMenuInfos) {
            this.uiMenuInfos = uiMenuInfos;
        }

        OnItemClickListener itemClickListener;
        List<UiMenuInfo> uiMenuInfos;

//        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
//            this.itemClickListener = onItemClickListener;
//        }

        @Override
        public DeviceInfoInfoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DeviceInfoInfoAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_devicemenu, parent, false));
        }

        @Override
        public void onBindViewHolder(DeviceInfoInfoAdapter.ViewHolder holder, int position) {
            holder.setData(uiMenuInfos, position);
            holder.setOnItemClickListener(itemClickListener);
        }

        @Override
        public int getItemCount() {
            return uiMenuInfos == null ? 0 : uiMenuInfos.size();
        }

        public void setOnItemClicklistener(OnItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView mTextView;
            CheckBox mCheckBox;

            public ViewHolder(View itemView)
            {
                super(itemView);
                mTextView = (TextView) itemView.findViewById(R.id.tv_device_mene_name);
                mCheckBox = (CheckBox) itemView.findViewById(R.id.open_close);
            }


            public void setData(final List<UiMenuInfo> appInfos, final int position)
            {
                UiMenuInfo uiMenuInfo = appInfos.get(position);
                mTextView.setText(uiMenuInfo.getName());
                Drawable drawable = uiMenuInfo.getIcon();
                /// 这一步必须要做,否则不会显示.
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                mTextView.setCompoundDrawables(drawable, null, null, null);
                mCheckBox.setChecked(uiMenuInfo.isOpen());
                mCheckBox.setOnClickListener(new View.OnClickListener()
                                            {
                                                 @Override
                                                 public void onClick(View v)
                                                 {
                                                     String data = LocalDataSaveTool.getInstance(getContext().getApplicationContext()).readSp(MyConfingInfo.DEVICE_SHOW_UI);
                                                     if (mCheckBox.isChecked())
                                                     {
                                                         mCheckBox.setChecked(true);
                                                         if(data == null || data.equals("") || data.length() <= 2)
                                                         {
                                                             sendsetopenOrclose(true, position);
                                                         }
                                                         else
                                                         {
                                                             sendNewDataToDevice(true, position);
                                                         }
                                                     }
                                                     else
                                                     {
                                                         mCheckBox.setChecked(false);
                                                         if(data == null || data.equals("") || data.length() <= 2)
                                                         {
                                                             sendsetopenOrclose(false, position);
                                                         }
                                                         else
                                                         {
                                                             sendNewDataToDevice(false, position);
                                                         }
                                                     }
                                                 }
                                             }
                );
            }

            private void sendNewDataToDevice(boolean isOpen, int position)
            {
                int number = witchs.get(position);
                int allData = newDatas;
                String all = getDataBinString(allData, number, isOpen);
                int dataInt = Integer.parseInt(all, 2);
                BleReadDeviceMenuState.getInstance().sendUpdateSwitchData32(dataInt);
                newDatas = dataInt;
                Log.i("", "页面配置所有数据：" + dataInt);
            }


            private String getDataBinString(int allData, int index, boolean isOpen)
            {
                StringBuffer sb = new StringBuffer();
                for (int i = 31; i >= 0; i--)
                {
                    if(i == index)
                    {
                        String a = "0";
                        if(!isOpen)
                        {
                            a = "1";
                        }
                        sb.append(a);
                    }
                    else
                    {
                        sb.append(String.valueOf(getEachResult(i, allData)));
                    }
                }
                return sb.toString();
            }

            OnItemClickListener onItemClickListener;
            public void setOnItemClickListener(OnItemClickListener itemClickListener)
            {
                this.onItemClickListener = itemClickListener;
            }
            @Override
            public void onClick(View v)
            {
                onItemClickListener.onItemClick(getAdapterPosition());
            }
        }

        private void sendsetopenOrclose(boolean isChecked, int position)
        {
            int pos = (position == 0) ? (position + 1) : (position + 2);
            if (isChecked) {
                data[pos] = '0';
            } else {
                data[pos] = '1';
            }
            String senddata = new String(data);
            String bToH = BToH(senddata);
            showLog(bToH + "---0000011112到这里");
            if(bToH.length() == 1)
            {
                bToH = "0" + bToH;
            }
            byte[] bytes = null;
            try
            {
                bytes = FormatUtils.hexStringToByteArray(bToH);
            }
            catch (Exception e)
            {
                bytes = new byte[1];
                bytes[0] = 0x00;
                e.printStackTrace();
            }
            BleReadDeviceMenuState.getInstance().sendUpdateSwitchData(bytes[0]);
            showLog(FormatUtils.byteToHexString(bytes[0]) + "-----0000011112到这里");
        }
    }




}
