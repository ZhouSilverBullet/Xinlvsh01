package com.huichenghe.xinlvshuju.mainpack;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huichenghe.xinlvshuju.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BloodPressFragment extends Fragment {


    public BloodPressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.blood_pressure_layout, container, false);
    }

}
