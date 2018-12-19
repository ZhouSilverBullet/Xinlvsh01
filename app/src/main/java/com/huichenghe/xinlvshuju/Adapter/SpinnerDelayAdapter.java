package com.huichenghe.xinlvshuju.Adapter;


import android.content.Context;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;

import com.huichenghe.xinlvshuju.R;

import java.util.ArrayList;

/**
 * Created by lixiaoning on 2016/4/29.
 */
public class SpinnerDelayAdapter extends BaseAdapter implements SpinnerAdapter
{
    private ArrayList<String> dataList;
    private Context mContext;
    private AppCompatSpinner mSpinner;
    public SpinnerDelayAdapter(ArrayList<String> data, Context mContext, AppCompatSpinner spinner)
    {
        this.dataList = data;
        this.mContext = mContext;
        this.mSpinner = spinner;
    }

//    @Override
//    public void registerDataSetObserver(DataSetObserver observer) {
//    }
//    @Override
//    public void unregisterDataSetObserver(DataSetObserver observer) {
//    }

    @Override
    public int getCount()
    {
        return dataList.size();
    }
    @Override
    public Object getItem(int position)
    {
        return dataList.get(position);
    }
    @Override
    public long getItemId(int position)
    {
        return position;
    }

//    @Override
//    public boolean hasStableIds() {
//        return false;
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return initView(position, convertView, parent);
    }

//    @Override
//    public int getItemViewType(int position) {
//        return 0;
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return 0;
//    }

//    @Override
//    public boolean isEmpty() {
//        return false;
//    }

    private View initView(int position, View convertView, ViewGroup parent)
    {
        View delayView = LayoutInflater.from(mContext).inflate(R.layout.spinner_for_delay, null);
        CheckedTextView te = (CheckedTextView)delayView.findViewById(R.id.check_select);
//        te.setCheckMarkDrawable(mContext.getResources().getDrawable(R.mipmap.select_button));
        te.toggle();
        if(position == 9)
        {
            te.setText(mContext.getString(R.string.immediate_reminder));
        }
        else
        {
            te.setText(dataList.get(position) + mContext.getString(R.string.second));
        }
        return delayView;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View delayView = LayoutInflater.from(mContext).inflate(R.layout.spinner_for_delay, null);
        CheckedTextView te = (CheckedTextView)delayView.findViewById(R.id.check_select);
        ImageView icon = (ImageView)delayView.findViewById(R.id.checked_icon);
        if(position == 9)
        {
            te.setText(mContext.getString(R.string.immediate_reminder));
        }
        else
        {
            te.setText(dataList.get(position) + mContext.getString(R.string.second));
        }
        if(mSpinner.getSelectedItemPosition() == position)
        {
//            te.setCheckMarkDrawable();
//            icon.setImageDrawable();
            icon.setImageResource(R.mipmap.select_button);
        }
        return delayView;
    }
}
