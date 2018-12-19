package com.huichenghe.xinlvshuju.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huichenghe.bleControl.Entity.sitRemindEntity;
import com.huichenghe.xinlvshuju.CustomView.RemoveView;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;

import java.util.ArrayList;

/**
 * Created by lixiaoning on 2016/5/17.
 */
public class SitRemindAdapter extends RecyclerView.Adapter<SitRemindAdapter.MyViewHolder>
{
    private Context context;
    private OnSitItemClickListener onClickListener;
    private ArrayList<sitRemindEntity> dataEntityList;
    public SitRemindAdapter(Context context, ArrayList<sitRemindEntity> dataList)
    {
        this.context = context;
        this.dataEntityList = dataList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.itme_for_sit_entity, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
        sitRemindEntity entity = dataEntityList.get(position);
        holder.sitTime.setText(entity.getBeginTime() + "-" + entity.getEndTime());
        holder.durationTime.setText(entity.getDuration() + context.getString(R.string.minute_up));
        holder.relativeLayout.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                onClickListener.onSitClick(position);
            }
        });
        holder.deleteLayout.setOnClickListener(new NoDoubleClickListener()
        {
            @Override
            public void onNoDoubleClick(View v)
            {
                if(onClickListener != null)
                {
                    onClickListener.onDelete(position);
                }
            }
        });
        holder.removeView.scrollTo(-10, 0);
    }

    @Override
    public int getItemCount() {
        return dataEntityList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView sitTime, durationTime;
        RelativeLayout relativeLayout;
        LinearLayout deleteLayout;
        RemoveView removeView;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            sitTime = (TextView)itemView.findViewById(R.id.sit_time);
            durationTime = (TextView)itemView.findViewById(R.id.duration_time);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.sit_remind_data);
            deleteLayout = (LinearLayout)itemView.findViewById(R.id.delete_sit_layout);
            removeView = (RemoveView) itemView.findViewById(R.id.remove_view_sit);
        }
    }


    public void setOnSitItemClickListener(OnSitItemClickListener sitItemClickListener)
    {
        this.onClickListener = sitItemClickListener;
    }
    public interface OnSitItemClickListener
    {
        void onSitClick(int position);
        void onDelete(int position);
    }




}
