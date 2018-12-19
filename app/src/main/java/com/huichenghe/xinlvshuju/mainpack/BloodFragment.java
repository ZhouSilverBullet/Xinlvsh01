package com.huichenghe.xinlvshuju.mainpack;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huichenghe.bleControl.Ble.BleBaseDataForBlood;
import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.xinlvshuju.BleDeal.BloodCallback;
import com.huichenghe.xinlvshuju.BleDeal.BloodDataDealer;
import com.huichenghe.xinlvshuju.BleDeal.BloodDataEntity;
import com.huichenghe.xinlvshuju.BleDeal.LoadingBloodData;
import com.huichenghe.xinlvshuju.CommonUtilss.CommonUtils;
import com.huichenghe.xinlvshuju.PreferenceV.PreferencesService;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.UpdataService.SendDataCallback;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;
import com.huichenghe.xinlvshuju.expand_activity.MovementDetail.fatigueHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BloodFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BloodFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BloodFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public final String TAG = BloodFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final int STEP_PAGER = 0;
    private final int BLOOD_PRESSURE = 1;
    private MainActivity activity;
    private BloodPressureView bloodView;
    //    private SwipeRefreshLayout refreshLayout;
    private String currentDate;
    private TextView highBlood, lowBlood, underStandand, upBloodStandan, hrBlood, upBloodNomal, upBloodBottom, underBloodNomail, underBloodBottom, hrStandand, hrNomail, hrBottom;
    private TextView hrvTv, spo2Tv;
    // TODO: Rename and change types of parameters
    private int mParam1;
    private String mParam2;
    private HorizontalScrollView bloodScrollView;

    private OnFragmentInteractionListener mListener;
    private PreferencesService service;
    private int int_s, int_h;

    public BloodFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StepFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BloodFragment newInstance(int param1, String param2) {
        BloodFragment fragment = new BloodFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return intitEachView(inflater, container,
                savedInstanceState);
    }

    private View intitEachView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.blood_pressure_layout, container, false);
        intitView(view);
        return view;
    }

    private void intitView(View view) {
        bloodScrollView = (HorizontalScrollView) view.findViewById(R.id.scroll_blood);
        LinearLayout bloodHeadView = (LinearLayout) view.findViewById(R.id.blood_head_view);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) bloodHeadView.getLayoutParams();
        params.height = getActivity().getWindowManager().getDefaultDisplay().getHeight() * 1 / 3;
        bloodHeadView.setLayoutParams(params);
//        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_blood);
//        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
//        {
//            @Override
//            public void onRefresh()
//            {
//                refreshLayout.setRefreshing(false);
//            }
//        });
        AssetManager am = getContext().getAssets();
        Typeface tp = Typeface.createFromAsset(am, "fonts/DS-DIGIB.TTF");
        highBlood = (TextView) view.findViewById(R.id.high_blood_value);
        upBloodStandan = (TextView) view.findViewById(R.id.biao_zhun_zhi);
        upBloodNomal = (TextView) view.findViewById(R.id.up_blood_small);
        upBloodBottom = (TextView) view.findViewById(R.id.up_blood_big);
        lowBlood = (TextView) view.findViewById(R.id.low_blood_value);
        underStandand = (TextView) view.findViewById(R.id.under_blood_standan);
        underBloodNomail = (TextView) view.findViewById(R.id.under_blood_smail);
        underBloodBottom = (TextView) view.findViewById(R.id.under_blood_big);
        hrBlood = (TextView) view.findViewById(R.id.xin_lv);
        hrStandand = (TextView) view.findViewById(R.id.hr_standand);
        hrNomail = (TextView) view.findViewById(R.id.hr_blood_small);
        hrBottom = (TextView) view.findViewById(R.id.hr_blood_big);
        spo2Tv = (TextView) view.findViewById(R.id.spo2_value);
        hrvTv = (TextView) view.findViewById(R.id.hrv_value);
        highBlood.setTypeface(tp);
        upBloodStandan.setTypeface(tp);
        upBloodNomal.setTypeface(tp);
        upBloodBottom.setTypeface(tp);
        lowBlood.setTypeface(tp);
        underStandand.setTypeface(tp);
        underBloodNomail.setTypeface(tp);
        underBloodBottom.setTypeface(tp);
        hrStandand.setTypeface(tp);
        hrNomail.setTypeface(tp);
        hrBottom.setTypeface(tp);
        hrBlood.setTypeface(tp);
        bloodView = (BloodPressureView) view.findViewById(R.id.blood_value_view);
//        bloodView.setData(valueFirst, valueSecond, valueTimes);
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        RelativeLayout.LayoutParams paramss = (RelativeLayout.LayoutParams) bloodView.getLayoutParams();
        paramss.width = widthPixels - (int) CommonUtils.Dp2Px(getContext(), 16);
        bloodView.setLayoutParams(paramss);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser)
//    {
//        super.setUserVisibleHint(isVisibleToUser);
//        if(getUserVisibleHint())
//        {
//            Log.i(TAG, "BloodFragment已显示");
//        }
//        else
//        {
//
//        }
//    }

    @Override
    public void onResume() {
        LoadBloodData(currentDate);
        super.onResume();
        if (!BloodFragment.this.isHidden()) {
            if (BluetoothLeService.getInstance() != null && !BluetoothLeService.getInstance().isConnectedDevice()) {
//                getRemoteBloodData(currentDate);
            }
        }
    }

    //todo hidd
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            Log.i(TAG, "BloodFragment已显示");
            service = new PreferencesService(getContext());
            if (service.getPreferences() != null) ;
            Map<String, String> params = service.getPreferences();
            //short_ed.setText(params.get("short_ed"));
            //height_ed.setText(params.get("height_ed"));
            String s = params.get("short_ed");
            String h = params.get("height_ed");
            Log.e("------s=", "--------------" + s);
            Log.e("------h=", "--------------" + h);
            if (s.length() > 0 && h.length() > 0) {
                int_s = Integer.parseInt(s);
                int_h = Integer.parseInt(h);
            }
            if (BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice()) {

                if (int_s != 0 && int_h != 0) {
                    BleBaseDataForBlood.getBloodInstance().sendStandardBloodData(int_h, int_s);
                }
            } else {
                //     Toast.makeText(getActivity(), R.string.toask_mark_bl, Toast.LENGTH_LONG).show();
            }
//
            if (s.equals("") && h.equals("") && s != null && h != null || s.equals(0) && h.equals(0)) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View layout = inflater.inflate(R.layout.blood_view, null);
                final EditText short_blood_q = (EditText) layout.findViewById(R.id.short_blood_q);
                final EditText height_blood_q = (EditText) layout.findViewById(R.id.height_ed_q);
                final Button true_bt = (Button) layout.findViewById(R.id.true_bt);
                final Button false_bt = (Button) layout.findViewById(R.id.false_bt);

                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                final android.app.AlertDialog dialog = builder.setView(layout).show();
                dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失

                false_bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });

                true_bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String short_blood = short_blood_q.getText().toString();
                        String height_blood = height_blood_q.getText().toString();
                        if (short_blood != null && height_blood != null) {
                            if (short_blood.length() > 0 && height_blood.length() > 0) {
                                int int_short_bood = Integer.parseInt(short_blood);
                                int int_height_bood = Integer.parseInt(height_blood);
                                if (int_short_bood >= 40 && int_short_bood <= 100 && int_height_bood >= 90 && int_height_bood <= 180) {
                                    // 增加到本地
                                    service = new PreferencesService(getContext());
                                    service.save(short_blood, height_blood);
                                    Log.e("----------", "-----------保存成功");
                                    //读取本地值
                                    if (service.getPreferences() != null) {
                                        Map<String, String> params = service.getPreferences();
                                        //  short_ed.setText(params.get("short_ed"));
                                        //height_ed.setText(params.get("height_ed"));
                                        String s = params.get("short_ed");
                                        String h = params.get("height_ed");
                                        Log.e("------s=", "--------------" + s);
                                        Log.e("------h=", "--------------" + h);
                                        int int_s = Integer.parseInt(s);
                                        int int_h = Integer.parseInt(h);

                                        // 写发送的代码
                                        if (BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice()) {
                                            if (s.length() > 1 && h.length() > 1) {
                                                BleBaseDataForBlood.getBloodInstance().sendStandardBloodData(int_h, int_s);
                                            }
                                        }

                                        dialog.dismiss();
                                    }
                                } else {
//                                            Toast.makeText(getActivity(), "值不能为空并且低压范围[40-100] 高压范围[90-180]", Toast.LENGTH_LONG).show();
//                                            Toast toast = Toast.makeText(getActivity(), "请输入正确范围，舒张压40-100，收缩压90-180", Toast.LENGTH_SHORT);
//                                            toast.setGravity(Gravity.CENTER, 0, 0);
//                                            toast.show();
                                    Toast toast = Toast.makeText(getActivity(), R.string.toask_mark, Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            } else {
//                                        Toast.makeText(getActivity(), "值不能为空并且低压范围[40-100] 高压范围[90-180]", Toast.LENGTH_LONG).show();
//                                            Toast toast = Toast.makeText(getActivity(), "请输入正确范围，舒张压40-100，收缩压90-180", Toast.LENGTH_SHORT);
//                                            toast.setGravity(Gravity.CENTER, 0, 0);
//                                            toast.show();
                                Toast toast = Toast.makeText(getActivity(), R.string.toask_mark, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        } else {
//                                          Toast.makeText(getActivity(), "值不能为空并且低压范围[40-100] 高压范围[90-180]", Toast.LENGTH_LONG).show();
//                                            Toast toast = Toast.makeText(getActivity(), "请输入正确范围，舒张压40-100，收缩压90-180", Toast.LENGTH_SHORT);
//                                            toast.setGravity(Gravity.CENTER, 0, 0);
//                                            toast.show();
                            Toast toast = Toast.makeText(getActivity(), R.string.toask_mark, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }

                    }
                });

            }
        } else {
            Log.i(TAG, "BloodFragment已pause");
        }

    }

    public void getRemoteBloodData(String currentDate) {
        Log.i("four", "从服务器上下载数据");
        if (currentDate != null && currentDate.equals("") || currentDate == null)
            return;
//        if(UpdateHistoryDataService.getInstance() != null && !UserAccountUtil.getAccount(getContext()).equals(""))
//        {
//            UpdateHistoryDataService.getInstance()
//                    .getBloodData(currentDate, UserAccountUtil.getAccount(getContext().getApplicationContext()), new ServiceCallback());
//        }
    }

    class ServiceCallback implements SendDataCallback {
        @Override
        public void sendDataSuccess(String reslult) {
            Log.i("updateData", "血压数据：" + reslult);
            try {
                JSONObject json = new JSONObject(reslult);
                String code = json.getString("code");
                if (code.equals("9003")) {
                    JSONObject jsonObj = json.getJSONObject("data");
                    String jsonArray = jsonObj.getString("bloodPressure");
                    if (jsonArray != null && jsonArray.equals("")) return;
                    new BloodDataDealer(getContext().getApplicationContext(), jsonArray);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LoadBloodData(currentDate);
                        }
                    });

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void sendDataFailed(String result) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MyToastUitls.showToast(getActivity(), R.string.net_wrong, 1);
                }
            });
        }

        @Override
        public void sendDataTimeOut() {
        }
    }


    public void LoadBloodData(final String currentDate) {
        new LoadingBloodData(getContext(), new BloodCallback() {
            @Override
            public void onDataCallback(ArrayList<BloodDataEntity> en) {
                if (en.size() > 0) {
                    BloodDataEntity entity = en.get(en.size() - 1);
                    setViewShow(entity);
                    System.out.println("huang1234:" + entity.getDate() + ","
                            + entity.getTime() + "," + entity.getHeartRate() + "," + entity.getHighPre() + "," + entity.getHrv() + "," + entity.getLowPre() + "," + entity.getSpo2());
                    updateChat(en);
                } else {
                    if (currentDate != null && !currentDate.equals(getTodayDate())) {
                        if (loadCount == 0) {
                            loadCount++;
                            getRemoteBloodData(currentDate);
                        }
                    } else {
                        if (BluetoothLeService.getInstance() != null && !BluetoothLeService.getInstance().isConnectedDevice()) {
                            if (loadCount == 0) {
                                loadCount++;
                                getRemoteBloodData(currentDate);
                            }
                        }
                    }
                    resetViewShow();
                    resetChat();
                }
            }
        }).execute(currentDate);
    }

    public int loadCount = 0;

    private String getTodayDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(calendar.getTime());
    }

    public void setDate(String currentDates) {
        this.currentDate = currentDates;
    }

    private void resetChat() {
        bloodView.setData(null, null, null, null);
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        RelativeLayout.LayoutParams paramss = (RelativeLayout.LayoutParams) bloodView.getLayoutParams();
        paramss.width = widthPixels - (int) CommonUtils.Dp2Px(getContext(), 16);
        bloodView.setLayoutParams(paramss);
    }


    private void updateChat(ArrayList<BloodDataEntity> en) {
        int s = en.size();
//        if(s > 8)
//        {
//            s = 8;
//        }
        int[] valueHigh = new int[s];
        int[] valueLow = new int[s];
        int[] valueTimess = new int[s];
        String[] valueTimes = new String[s];
        for (int i = 0; i < s; i++) {
            valueHigh[i] = en.get(i).getHighPre();
            valueLow[i] = en.get(i).getLowPre();
            valueTimes[i] = en.get(i).getTime();
            valueTimess[i] = i;
        }
        bloodView.setData(valueHigh, valueLow, valueTimes, valueTimess);
        int dataWidth = (int) (bloodView.getSpaceWidth() * valueHigh.length);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        RelativeLayout.LayoutParams relLayout = (RelativeLayout.LayoutParams) bloodView.getLayoutParams();
        if (dataWidth > screenWidth - (int) CommonUtils.Dp2Px(getContext(), 16)) {
            relLayout.width = dataWidth;
            bloodView.setLayoutParams(relLayout);
            bloodScrollView.setOnTouchListener(onTouchListener);
        } else {
            relLayout.width = screenWidth - (int) CommonUtils.Dp2Px(getContext(), 16);
            bloodView.setLayoutParams(relLayout);
        }
    }

    float elementX, movementX;
    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    elementX = event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (bloodScrollView.getScrollX() == 0) {
                        ((MainActivity) getActivity()).mResidemenu.menuUnLock();
                        break;
                    }
                    movementX = event.getRawX();
                    int scrollX = bloodScrollView.getScrollX();
                    Log.i(TAG, "触摸的x值：" + elementX + "--移动的x值：" + movementX + "--scrollX:" + scrollX);
                    if ((movementX - elementX) > 0 && (movementX - elementX) > 30) {
                        ((MainActivity) getActivity()).mResidemenu.menuLock();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    ((MainActivity) getActivity()).mResidemenu.menuUnLock();
                    break;
            }
            return false;
        }
    };


    private void setViewShow(BloodDataEntity entity) {
        highBlood.setText(String.valueOf(entity.getHighPre()).trim());
        lowBlood.setText(String.valueOf(entity.getLowPre()));
        hrBlood.setText(String.valueOf(entity.getHeartRate()));
        spo2Tv.setText(String.valueOf(entity.getSpo2()));
        fatigueHelper helper = new fatigueHelper();
        int re = helper.getFagigue(helper.getUserAger(getContext()), entity.getHrv());
        String hrv = getResultString(re);
        hrvTv.setText(hrv);
    }

    private String getResultString(int re) {
        String hrv = "";
        switch (re) {
            case 0:
                hrv = getString(R.string.health_note);
                break;
            case 1:
                hrv = getString(R.string.zhengchang);
                break;
            case 2:
                hrv = getString(R.string.danger_note);
                break;
            case 3:
                hrv = getString(R.string.danger_more);
                break;
        }
        return hrv;
    }

    private void resetViewShow() {
        highBlood.setText(String.valueOf(0).trim());
        lowBlood.setText(String.valueOf(0));
        hrBlood.setText(String.valueOf(0));
        spo2Tv.setText(String.valueOf(0));
        hrvTv.setText(getString(R.string.zhengchang));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (MainActivity) context;
        currentDate = activity.currenDate;
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
