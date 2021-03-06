package com.huichenghe.xinlvshuju.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huichenghe.xinlvshuju.CustomView.HistogramImageViewForWeekTrend;
import com.huichenghe.xinlvshuju.DataEntites.stepAndCalorieEntity;
import com.huichenghe.xinlvshuju.R;

import java.util.ArrayList;

/**
 * Created by lixiaoning on 15-12-28.
 */
public class HistogramMonthSportAdapter extends RecyclerView.Adapter<HistogramMonthSportAdapter.MyHolders>
{
    private Context mContext;
    private ArrayList<stepAndCalorieEntity> dataTrendList;
    private int heightH;


    public HistogramMonthSportAdapter(Context mContext, ArrayList<stepAndCalorieEntity> dataList)
    {
        this.mContext = mContext;
        this.dataTrendList = dataList;
    }

    public void initHeight(int height)
    {
        this.heightH = height;
        notifyDataSetChanged();
    }





    @Override
    public MyHolders onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_for_month_sport_histogram, parent, false);
        return new MyHolders(v);
    }

    @Override
    public void onBindViewHolder(final MyHolders holder, int position)
    {


//        ViewTreeObserver observer = holder.imageViewHistogram.getViewTreeObserver();
//        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
//        {
//            @Override
//            public void onGlobalLayout()
//            {
//                holder.imageViewHistogram.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                initHeight(holder.imageViewHistogram.getHeight());
//            }
//        });


//        ViewGroup.LayoutParams params = holder.imageViewHistogram.getLayoutParams();
//        params.height = heightH;
//        holder.imageViewHistogram.setLayoutParams(params);
//        holder.imageViewHistogram.invalidate();

        holder.sportStep.setText(String.valueOf(dataTrendList.get(position).getCurrentData()));
        holder.sportDate.setText(String.valueOf(dataTrendList.get(position).getTimes()));

        holder.imageViewHistogram.addData(dataTrendList.get(position).getTimes(),
                dataTrendList.get(position).getCurrentData(),
                dataTrendList.get(position).getMaxData(), R.color.step_histogram);
        holder.imageViewHistogram.MyInvalidate();

    }

    @Override
    public int getItemCount()
    {
        return dataTrendList.size();
    }

    class MyHolders extends RecyclerView.ViewHolder
    {
        HistogramImageViewForWeekTrend imageViewHistogram;
        TextView sportStep, sportDate;

        public MyHolders(View itemView)
        {
            super(itemView);
            imageViewHistogram = (HistogramImageViewForWeekTrend)itemView.findViewById(R.id.week_histogram_item);
            sportStep = (TextView)itemView.findViewById(R.id.sport_step);
            sportDate = (TextView)itemView.findViewById(R.id.sport_date);
        }
    }

}
