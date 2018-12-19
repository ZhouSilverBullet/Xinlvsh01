package com.huichenghe.xinlvshuju.slide.AttionModle;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.huichenghe.xinlvshuju.CustomView.HistogramImageView;
import com.huichenghe.xinlvshuju.DataEntites.stepAndCalorieEntity;
import com.huichenghe.xinlvshuju.R;

import java.util.ArrayList;

/**
 * Created by lixiaoning on 2016/6/7.
 */
public class AttionStepAdapter extends RecyclerView.Adapter<AttionStepAdapter.Myholder>
{
    private Context context;
    private ArrayList<stepAndCalorieEntity> dataList;
    private int color;

    public AttionStepAdapter(Context context, ArrayList<stepAndCalorieEntity> data, int colores)
    {
        this.context = context;
        this.dataList = data;
        this.color = colores;
    }

    public void addItem(stepAndCalorieEntity en, int position)
    {
        dataList.add(position, en);
        notifyItemInserted(position);
    }

    @Override
    public Myholder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.item_for_attion_info_stepcount, parent, false);
        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(Myholder holder, int position)
    {
        stepAndCalorieEntity entity = dataList.get(position);
        holder.histogramImageView.addData(entity.getTimes(), entity.getCurrentData(), entity.getMaxData());
        holder.histogramImageView.MyInvalidate();
    }

    @Override
    public int getItemCount()
    {
        return dataList.size();
    }

    class Myholder extends RecyclerView.ViewHolder
    {
        HistogramImageView histogramImageView;
        public Myholder(View itemView)
        {
            super(itemView);
            histogramImageView = (HistogramImageView)itemView.findViewById(R.id.item_step);
            histogramImageView.setHistogramColor(color);
            histogramImageView.setHeadColor(color);
            histogramImageView.setFootColor(Color.BLACK);
            histogramImageView.setPadding(16f);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)histogramImageView.getLayoutParams();
            params.width = context.getResources().getDisplayMetrics().widthPixels / 7;
            histogramImageView.setLayoutParams(params);
        }
    }
}
