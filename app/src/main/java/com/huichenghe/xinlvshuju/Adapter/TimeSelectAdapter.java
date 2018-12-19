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

/**
 * 静坐提醒时间选择
 * Created by lixiaoning on 2016/5/17.
 */
public class TimeSelectAdapter extends RecyclerView.Adapter<TimeSelectAdapter.MyHolder>
{
    private Context context;
    private int[] data = {30, 60, 90, 120};
    private int positiones = 0;
    private OnTimeItemClick onTimeItemClick;
    public TimeSelectAdapter(Context context, String s)
    {
        this.context = context;
        for (int i = 0; i < data.length; i++)
        {
            if(data[i] == Integer.parseInt(s))
            {
                positiones = i;
            }
        }
    }

    public void setOnTimeItemClick(OnTimeItemClick click)
    {
        this.onTimeItemClick = click;
    }
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.item_for_time_select, null);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position)
    {
        if(position == positiones)
        {
            holder.imSelect.setImageResource(R.mipmap.select_button);
        }
        holder.tvTime.setText(data[position] + "\t" +context.getString(R.string.minute_has_prefix));
        holder.relativeLayout.setOnClickListener(new NoDoubleClickListener()
        {
            @Override
            public void onNoDoubleClick(View v)
            {
                onTimeItemClick.onTimeClick(holder.tvTime.getText().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    class MyHolder extends RecyclerView.ViewHolder
    {
        TextView tvTime;
        ImageView imSelect;
        RelativeLayout relativeLayout;
        public MyHolder(View itemView)
        {
            super(itemView);
            tvTime = (TextView)itemView.findViewById(R.id.time_left);
            imSelect = (ImageView)itemView.findViewById(R.id.already_select);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.layout_time);
        }
    }


    public interface OnTimeItemClick
    {
        void onTimeClick(String time);
    }



}
