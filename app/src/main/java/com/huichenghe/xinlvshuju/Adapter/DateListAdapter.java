package com.huichenghe.xinlvshuju.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;

import java.util.ArrayList;

/**
 * Created by DIY on 2016/sitting/19.
 */
public class DateListAdapter extends RecyclerView.Adapter<DateListAdapter.MyViewHolder>
{
    private Context mContext;
    private ArrayList<String> dateList;
    private OnItemClickListener listener;

    public DateListAdapter(Context mContext, ArrayList<String> data, OnItemClickListener myListener)
    {
        this.mContext = mContext;
        this.dateList = data;
        this.listener = myListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View viewRoot = LayoutInflater.from(mContext).inflate(R.layout.item_for_histroy_data, parent, false);
        return new MyViewHolder(viewRoot);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
        holder.mTime.setText(dateList.get(position));


        // 设置点击监听
        holder.mTime.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                listener.ItemClick(position);
            }
        });
    }

//        <string name="sunday_all">周日</string>
//        <string name="tuesday_all">周一</string>
//        <string name="monday_all">周二</string>
//        <string name="wednesday_all">周三</string>
//        <string name="thursday_all">周四</string>
//        <string name="friday_all">周五</string>
//        <string name="saturday_all">周六</string>





    @Override
    public int getItemCount()
    {
        return dateList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView mTime;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            mTime = (TextView)itemView.findViewById(R.id.show_data_item);
        }
    }


    public interface OnItemClickListener
    {
        void ItemClick(int position);
    }







}