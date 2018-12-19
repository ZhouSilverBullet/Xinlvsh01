package com.huichenghe.xinlvshuju.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;

import java.util.ArrayList;

/**
 * Created by lixiaoning on 2016/5/26.
 */
public class frequencyAdapter extends RecyclerView.Adapter<frequencyAdapter.MyHolder>
{
    private ArrayList<Integer> data;
    private Context mContext;
    private int content;
    public frequencyAdapter(ArrayList<Integer> dataList, Context context, int i)
    {
        this.data = dataList;
        this.mContext = context;
        this.content = i;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View veiw = LayoutInflater.from(mContext).inflate(R.layout.heart_frequecy_item, null);
        return new MyHolder(veiw);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position)
    {
        StringBuffer con = new StringBuffer();
        if(data.get(position) == 1)
        {
            con.append(mContext.getString(R.string.continue_moni));
        }
        else
        {
            con.append(String.valueOf(data.get(position)));
            con.append(mContext.getString(R.string.minute_has_prefix));
        }
        holder.freguency.setText(con.toString());
        if(data.get(position) == content)
        {
            holder.selectButton.setImageResource(R.mipmap.select_button);
        }
        holder.item.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                onFrequencyItemClick.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder
    {
        TextView freguency;
        ImageView selectButton;
        RelativeLayout item;

        public MyHolder(View itemView)
        {
            super(itemView);
            item = (RelativeLayout)itemView.findViewById(R.id.item_frequency);
            freguency = (TextView)itemView.findViewById(R.id.item_name);
            selectButton = (ImageView)itemView.findViewById(R.id.choose_item);
        }
    }
    public interface OnFrequencyItemClick
    {
        void onItemClick(int position);
    }

    public void setOnFrequencyClickListner(OnFrequencyItemClick onclick)
    {
        this.onFrequencyItemClick = onclick;
    }
    private OnFrequencyItemClick onFrequencyItemClick;

}
