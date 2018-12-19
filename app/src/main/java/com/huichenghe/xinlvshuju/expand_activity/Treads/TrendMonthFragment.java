package com.huichenghe.xinlvshuju.expand_activity.Treads;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huichenghe.xinlvshuju.CustomView.Custom_hisgram_view_sleep_trend;
import com.huichenghe.xinlvshuju.CustomView.Custom_hisgram_view_trend;
import com.huichenghe.xinlvshuju.DataEntites.stepAndCalorieEntity;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.Histogram_sleep_entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TrendMonthFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TrendMonthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrendMonthFragment extends Fragment
{
    public static final String TAG = TrendWeekFragment.class.getSimpleName();
    private ArrayList<stepAndCalorieEntity> monthData;
    private Custom_hisgram_view_trend cus;
    private Custom_hisgram_view_sleep_trend sleepViews;


    private TrendActivity trendActivity;
    private TextView complation, maxStpes, averageSteps, averageCalorie, averageSleepTimes, averageDeepSleep;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private final int UPDATE_TEXT = 0;
    private TextView showMon;
    private int[] monthDatas;
    private Handler trendMonthHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case UPDATE_TEXT:
                    updateTheMonthTextView(monthDatas);
                    break;
            }
        }
    };

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TrendMonthFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrendMonthFragment newInstance()
    {
        TrendMonthFragment fragment = new TrendMonthFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, "");
        args.putString(ARG_PARAM2, "");
        fragment.setArguments(args);
        return fragment;
    }

    public TrendMonthFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        Log.i(TAG, "setUserVisibleHint:" + isVisibleToUser);
        if(getUserVisibleHint())
        {
            monthData = trendActivity.getMonthStepData();
            ArrayList<Histogram_sleep_entity> monthSleepData = trendActivity.getSleepDataForMonth();
            initTrendStep(monthData);
            initTrendMonthSleep(monthSleepData);
        }
        else
        {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_trend_month, container, false);
        cus = (Custom_hisgram_view_trend) view.findViewById(R.id.stpe_show_each_hour);
        sleepViews = (Custom_hisgram_view_sleep_trend) view.findViewById(R.id.trend_month_step_sleep);

//        HistogramMonthSleepAdapter monthSleepAdapter = new HistogramMonthSleepAdapter(getContext(), monthSleepData);
//        HistogramMonthSportAdapter monthAdapter = new HistogramMonthSportAdapter(getContext(), monthData);

//        Log.i(TAG, "时间数据月:" + monthSleepData.get(0).getTime() + "--" + monthSleepData.get(0).getTotalCount());
//        Log.i(TAG, "时间数据周:" + monthData.get(0).getTimes() + "--" + monthData.get(0).getMaxData());
//        RecyclerView recyclerViewMonth = (RecyclerView)view.findViewById(R.id.week_sport_histogram_month);
//        recyclerViewMonth.setHasFixedSize(true);
//        recyclerViewMonth.setItemAnimator(new DefaultItemAnimator());
//        LinearLayoutManager manager = new LinearLayoutManager(getContext());
//        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        manager.setStackFromEnd(true);
//        recyclerViewMonth.setLayoutManager(manager);
//        recyclerViewMonth.setAdapter(monthAdapter);
//        RecyclerView recyclerViewMonthSleep = (RecyclerView)view.findViewById(R.id.week_sleep_histogram_aa);
//        recyclerViewMonthSleep.setHasFixedSize(true);
//        recyclerViewMonthSleep.setItemAnimator(new DefaultItemAnimator());
//        LinearLayoutManager manager1 = new LinearLayoutManager(getContext());
//        manager1.setOrientation(LinearLayoutManager.HORIZONTAL);
//        manager1.setStackFromEnd(true);
//        recyclerViewMonthSleep.setLayoutManager(manager1);
//        recyclerViewMonthSleep.setAdapter(monthSleepAdapter);

        complation = (TextView)view.findViewById(R.id.complation_month);
        maxStpes = (TextView)view.findViewById(R.id.steps_month);
        averageSteps = (TextView)view.findViewById(R.id.average_steps_month);
        averageCalorie = (TextView)view.findViewById(R.id.average_calorie_month);
        averageSleepTimes = (TextView)view.findViewById(R.id.sleep_time_month);
        averageDeepSleep = (TextView)view.findViewById(R.id.deep_time_month);
        showMon = (TextView)view.findViewById(R.id.month_of_show);

        monthDatas = trendActivity.getMonthDatas();
        return view;
    }

    private void initTrendMonthSleep(ArrayList<Histogram_sleep_entity> monthSleepData)
    {
        int[][] sleepData = new int[monthSleepData.size()][];
        String[] sleepTime = new String[monthSleepData.size()];
        int dataSize = monthSleepData.size();
        for (int i = 0; i < dataSize; i++)
        {
            Histogram_sleep_entity en = monthSleepData.get(i);
            String t = en.getTime();
            sleepTime[i] = t;
            int[] slee = new int[3];
            slee[0] = en.getDeepCount();
            slee[1] = en.getLightCount();
            slee[2] = en.getWakeCount();
            sleepData[i] = slee;
        }
        sleepViews.setData(sleepData, sleepTime);
    }

    private void initTrendStep(ArrayList<stepAndCalorieEntity> monthData)
    {
        int[] steps = new int[monthData.size()];
        String[] stepTimes = new String[monthData.size()];
        for (int i = 0; i < monthData.size(); i ++)
        {
            steps[i] = monthData.get(i).getCurrentData();
            stepTimes[i] = monthData.get(i).getTimes();
        }
        cus.setData(steps, stepTimes);
    }

    @Override
    public void onResume() {
        super.onResume();
        trendMonthHandler.sendEmptyMessageDelayed(UPDATE_TEXT, 1000);
    }



    private void updateTheMonthTextView(int[] monthDatas)
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int mon = calendar.get(Calendar.MONTH) + 1;
        String showM = mon + getString(R.string.trend_month_text);
        showMon.setText(showM);
        Log.i(TAG, "完成次数:" + monthDatas[0] + "---" + monthDatas[5]);
        int sleepTime = monthDatas[4];
        int hour = sleepTime / 60;
        int minute = sleepTime % 60;

        int sleepTimes = monthDatas[5];
        int deepHour = sleepTimes / 60;
        int deepMinute = sleepTimes % 60;
        String comp = String.valueOf(monthDatas[0]) + getString(R.string.wancheng_ci);
        String maxS = String.valueOf(monthDatas[1]) + getString(R.string.step_simple_one);
        String aveS = String.valueOf(monthDatas[2]) + getString(R.string.step_simple_one);
        String aveC = String.valueOf(monthDatas[3]) + getString(R.string.calorie_text);
        String aveT = hour + getString(R.string.hour_has_prefix) + minute + getString(R.string.minute_has_prefix);
        String aveDe = deepHour + getString(R.string.hour_has_prefix) + deepMinute + getString(R.string.minute_has_prefix);
        complation.setText(comp);
        maxStpes.setText(maxS);
        averageSteps.setText(aveS);
        averageCalorie.setText(aveC);
        averageSleepTimes.setText(aveT);
        averageDeepSleep.setText(aveDe);





    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        trendActivity = (TrendActivity)activity;
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
        public void onFragmentInteraction(Uri uri);
    }


    public void resetScreenData()
    {
        initTrendStep(trendActivity.getMonthStepData());
        initTrendMonthSleep(trendActivity.getSleepDataForMonth());
    }

}
