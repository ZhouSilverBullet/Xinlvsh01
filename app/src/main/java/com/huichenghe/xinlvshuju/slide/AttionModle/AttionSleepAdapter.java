package com.huichenghe.xinlvshuju.slide.AttionModle;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.huichenghe.xinlvshuju.R;

import java.util.ArrayList;

/**
 * Created by lixiaoning on 2016/6/7.
 */
public class AttionSleepAdapter extends RecyclerView.Adapter<AttionSleepAdapter.myViewHolder>
{
    private Context context;
    private ArrayList<AttionSleepDetialEntity> sleepList;
    public AttionSleepAdapter(Context context, ArrayList<AttionSleepDetialEntity> sleepList)
    {
        this.context = context;
        this.sleepList = sleepList;
    }
    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.item_attion_sleep_data, parent, false);
        return new myViewHolder(view);
    }

    public void setData(AttionSleepDetialEntity entity, int position)
    {
        sleepList.add(position, entity);
        notifyItemChanged(position);
    }
    @Override
    public void onBindViewHolder(myViewHolder holder, int position)
    {
        AttionSleepDetialEntity ent = sleepList.get(position);
        holder.sleepHisgram.addData(ent.getDay(), ent.getDeepSleep(), ent.getLightSleep(), ent.getAweekSleep(), ent.getTotalData());
        holder.sleepHisgram.MyInvalidate();
    }

    @Override
    public int getItemCount() {
        return sleepList.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder
    {
        HistogramImageViewAttion sleepHisgram;
        public myViewHolder(View itemView)
        {
            super(itemView);
            sleepHisgram = (HistogramImageViewAttion)itemView.findViewById(R.id.sleep_attion_hisgram);
            sleepHisgram.setFootColor(Color.BLACK);
            sleepHisgram.setHeadColor(context.getResources().getColor(R.color.deep_sleep));
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)sleepHisgram.getLayoutParams();
            params.width = context.getResources().getDisplayMetrics().widthPixels / 7;
            sleepHisgram.setLayoutParams(params);
            sleepHisgram.setPadding(16f);
        }
    }








}
