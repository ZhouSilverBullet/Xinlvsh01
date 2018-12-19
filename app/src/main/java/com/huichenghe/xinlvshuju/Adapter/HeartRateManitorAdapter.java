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
 * 心率监测频率页面spinner适配器
 * Created by lixiaoning on 2016/5/19.
 */
public class HeartRateManitorAdapter extends BaseAdapter implements SpinnerAdapter
{
    private ArrayList<Integer> datalist;
    private Context context;
    private AppCompatSpinner spinner;
    public HeartRateManitorAdapter(ArrayList<Integer> data, Context context, AppCompatSpinner spinner)
    {
        this.datalist = data;
        this.context = context;
        this.spinner = spinner;
    }


    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int position) {
        return datalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return datalist.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent)
    {
        convertView = LayoutInflater.from(context).inflate(R.layout.spinner_for_delay, null);
        CheckedTextView time = (CheckedTextView)convertView.findViewById(R.id.check_select);
        ImageView image = (ImageView)convertView.findViewById(R.id.checked_icon);
        time.setText(datalist.get(position) + context.getString(R.string.minute_has_prefix));
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        convertView = LayoutInflater.from(context).inflate(R.layout.spinner_for_delay, null);
        CheckedTextView time = (CheckedTextView)convertView.findViewById(R.id.check_select);
        ImageView image = (ImageView)convertView.findViewById(R.id.checked_icon);
        time.setText(datalist.get(position) + context.getString(R.string.minute_has_prefix));
        if(spinner.getSelectedItemPosition() == position)
        {
           image.setImageResource(R.mipmap.select_button);
        }
        return convertView;
    }
}
