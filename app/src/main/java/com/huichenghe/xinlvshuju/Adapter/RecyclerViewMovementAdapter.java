package com.huichenghe.xinlvshuju.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huichenghe.xinlvshuju.DataEntites.OutLineDataEntity;
import com.huichenghe.xinlvshuju.R;

import java.util.ArrayList;

/**
 * Created by lixiaoning on 15-12-23.
 */
public class RecyclerViewMovementAdapter extends RecyclerView.Adapter<RecyclerViewMovementAdapter.MyHolerThe>
{
    private Context mContext;
    private ArrayList<OutLineDataEntity> dataOutLine;




    public RecyclerViewMovementAdapter(Context mContext, ArrayList<OutLineDataEntity> dataOutline)
    {
        this.mContext = mContext;
        this.dataOutLine = dataOutline;
    }

    @Override
    public MyHolerThe onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(mContext).inflate(R.layout.mainpager_movement_list, parent);
        return new MyHolerThe(v);
    }

    @Override
    public void onBindViewHolder(MyHolerThe holder, int position)
    {
        OutLineDataEntity entity = dataOutLine.get(position);
        holder.time.setText(entity.getTime());
        holder.steps.setText(String.valueOf(entity.getStepCount()));
        holder.calories.setText(String.valueOf(entity.getCalorie()));
        holder.hrAverage.setText(String.valueOf(entity.getHeartReat()));
    }

    @Override
    public int getItemCount()
    {
        return dataOutLine.size();
    }

    class MyHolerThe extends RecyclerView.ViewHolder
    {
        TextView time, steps, calories, hrAverage;

        public MyHolerThe(View itemView)
        {
            super(itemView);
            time = (TextView)itemView.findViewById(R.id.movement_duratin_main);
            steps = (TextView)itemView.findViewById(R.id.movement_step_count_main);
            calories = (TextView)itemView.findViewById(R.id.movement_kll_main);
            hrAverage = (TextView)itemView.findViewById(R.id.movement_heart_reat_main);
        }
    }


}
