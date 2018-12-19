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

import com.huichenghe.xinlvshuju.Adapter.HistogramWeekSleepAdapter;
import com.huichenghe.xinlvshuju.Adapter.HistogramWeekSportAdapter;
import com.huichenghe.xinlvshuju.CustomView.Custom_hisgram_view;
import com.huichenghe.xinlvshuju.CustomView.Custom_hisgram_view_sleep;
import com.huichenghe.xinlvshuju.DataEntites.stepAndCalorieEntity;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.Histogram_sleep_entity;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TrendWeekFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TrendWeekFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrendWeekFragment extends Fragment {
    public static final String TAG = TrendWeekFragment.class.getSimpleName();
    private HistogramWeekSportAdapter weekAdapter;
    private ArrayList<stepAndCalorieEntity> dataWeekList;
    private HistogramWeekSleepAdapter weekSleepAdapter;
    private ArrayList<Histogram_sleep_entity> dataWeekSleepList;
    private TrendActivity trendActivity;
    private Custom_hisgram_view trendStepView;
    private Custom_hisgram_view_sleep trendSleepView;
    private final int UPDATE_TEXT = 0;
    private int[] weekDataes;
    private Handler trendWeekHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_TEXT:
                    try {
                        showTheAverageOnTextView(weekDataes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private TextView completion, maxStep, averageStep, averageCalorie, averageSleepTime, averageDeepSleep;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter clock.
     * @param param2 Parameter phone.
     * @return A new instance of fragment TrendWeekFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrendWeekFragment newInstance(String param1, String param2) {
        TrendWeekFragment fragment = new TrendWeekFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    public TrendWeekFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Log.i(TAG, "先后顺序:TrendWeekFragment.onCreate");
    }


    @Override
    public void onResume() {
        super.onResume();
        trendWeekHandler.sendEmptyMessageDelayed(UPDATE_TEXT, 1000);
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "先后顺序:TrendWeekFragment.onCreateView");
//        weekSleepAdapter = new HistogramWeekSleepAdapter(getContext(), dataWeekSleepList);
        View view = inflater.inflate(R.layout.fragment_trend_week, container, false);
        trendStepView = (Custom_hisgram_view) view.findViewById(R.id.trend_week_step_each);
        trendSleepView = (Custom_hisgram_view_sleep) view.findViewById(R.id.trend_week_step_sleep);
        dataWeekSleepList = trendActivity.getWeekSleepDataForFragment();
        dataWeekList = trendActivity.getWeekStepData();
        initStepView(dataWeekList);
        initSleepView(dataWeekSleepList);
        weekAdapter = new HistogramWeekSportAdapter(getContext(), dataWeekList);
        completion = (TextView) view.findViewById(R.id.complete_already);
        maxStep = (TextView) view.findViewById(R.id.max_step);
        averageStep = (TextView) view.findViewById(R.id.average_step);
        averageCalorie = (TextView) view.findViewById(R.id.average_calorie_show);
        averageSleepTime = (TextView) view.findViewById(R.id.average_sleep_time_show);
        averageDeepSleep = (TextView) view.findViewById(R.id.average_deep_sleep_show);
        weekDataes = trendActivity.getWeekDataAverage();
        return view;
    }

    private void initSleepView(ArrayList<Histogram_sleep_entity> dataWeekSleepList) {
        int[][] sleepData = new int[7][];
        String[] sleepTime = new String[7];
        for (int i = 0; i < dataWeekSleepList.size(); i++) {
            Histogram_sleep_entity en = dataWeekSleepList.get(i);
            sleepTime[i] = en.getTime();
            int[] slee = new int[3];
            slee[0] = en.getDeepCount();
            slee[1] = en.getLightCount();
            slee[2] = en.getWakeCount();
            sleepData[i] = slee;
        }
        trendSleepView.setData(sleepData, sleepTime);
    }

    private void initStepView(ArrayList<stepAndCalorieEntity> dataList) {
        int dataKcal[] = new int[7];
        String dataTimeKcal[] = new String[7];
        for (int i = 0; i < dataList.size(); i++) {
            dataKcal[i] = dataList.get(i).getCurrentData();
            dataTimeKcal[i] = dataList.get(i).getTimes();
        }
        trendStepView.setData(dataKcal, dataTimeKcal);
    }


    private void showTheAverageOnTextView(int[] weekDataes) {
        if (getActivity() != null && getActivity().isDestroyed()) return;
        int sleepTime = weekDataes[4];
        int hour = sleepTime / 60;
        int minute = sleepTime % 60;
        int sleepTimes = weekDataes[5];
        int deepHour = sleepTimes / 60;
        int deepMinute = sleepTimes % 60;
        String comp = String.valueOf(weekDataes[0]) + getString(R.string.wancheng_ci);
        String maxS = String.valueOf(weekDataes[1]) + getString(R.string.step_simple_one);
        String aveS = String.valueOf(weekDataes[2]) + getString(R.string.step_simple_one);
        String aveC = String.valueOf(weekDataes[3]) + getString(R.string.calorie_text);
        String aveSS = hour + getString(R.string.hour_has_prefix) + minute + getString(R.string.minute_has_prefix);
        String aveDee = deepHour + getString(R.string.hour_has_prefix) + deepMinute + getString(R.string.minute_has_prefix);
        completion.setText(comp);
        maxStep.setText(maxS);
        averageStep.setText(aveS);
        averageCalorie.setText(aveC);
        averageSleepTime.setText(aveSS);
        averageDeepSleep.setText(aveDee);
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
        trendActivity = (TrendActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void updateWeekSteps(ArrayList<stepAndCalorieEntity> dataListHistogram) {
        dataWeekList = dataListHistogram;
        weekAdapter.notifyDataSetChanged();
    }

    public void refreshScreen() {
        dataWeekSleepList = trendActivity.getWeekSleepDataForFragment();
        dataWeekList = trendActivity.getWeekStepData();
        initStepView(dataWeekList);
        initSleepView(dataWeekSleepList);
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

}
