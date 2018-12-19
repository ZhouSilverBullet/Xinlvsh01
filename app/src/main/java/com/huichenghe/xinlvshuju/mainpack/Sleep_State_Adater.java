package com.huichenghe.xinlvshuju.mainpack;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huichenghe.xinlvshuju.R;

import java.util.ArrayList;

/**
 * Created by lixiaoning on 2016/9/27.
 */
public class Sleep_State_Adater extends RecyclerView.Adapter<Sleep_State_Adater.MySleepViewHolder>
{
    private ArrayList<sleepData_new> sleepData;
    private Context context;


    public Sleep_State_Adater(Context context, ArrayList<sleepData_new> data)
    {
        this.context = context;
        this.sleepData = data;
    }


    @Override
    public MySleepViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View root = LayoutInflater.from(context).inflate(R.layout.sleep_pager_grid_item, null);
        return new MySleepViewHolder(root);
    }

    @Override
    public void onBindViewHolder(MySleepViewHolder holder, int position)
    {
        sleepData_new detity = sleepData.get(position);
        holder.icon.setImageResource(detity.getSleepIcon());
        holder.upString.setText(detity.getUpString());
        holder.underString.setText(detity.getUnderString());
    }

    @Override
    public int getItemCount()
    {
        return sleepData.size();
    }

    class MySleepViewHolder extends RecyclerView.ViewHolder
    {
        ImageView icon;
        TextView upString, underString;
        public MySleepViewHolder(View itemView)
        {
            super(itemView);
            icon = (ImageView)itemView.findViewById(R.id.sleep_icon);
            upString = (TextView)itemView.findViewById(R.id.update_string);
            underString = (TextView)itemView.findViewById(R.id.under_string);
        }
    }
}
