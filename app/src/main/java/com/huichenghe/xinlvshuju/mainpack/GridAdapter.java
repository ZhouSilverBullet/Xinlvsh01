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
 * 活动主页上的网格adapter
 * Created by lixiaoning on 2016/9/27.
 */
public class GridAdapter extends RecyclerView.Adapter<GridAdapter.MyGridViewHolder>
{
    private Context context;
    private ArrayList<active_content> activeData;

    public GridAdapter(Context context, ArrayList<active_content> data)
    {
        this.context = context;
        this.activeData = data;
    }
    @Override
    public MyGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View item = LayoutInflater.from(context).inflate(R.layout.grid_layout_item, null);
        return new MyGridViewHolder(item);
    }

    @Override
    public void onBindViewHolder(MyGridViewHolder holder, int position)
    {
        active_content content = activeData.get(position);
        if(content.getType() == 1)
        {
            holder.imageView.setImageResource(content.getIconId());
            holder.tv.setText(content.getContent());
            holder.tvUnit.setText(content.getUnit());
            holder.tvM.setVisibility(View.GONE);
            holder.tvMin.setVisibility(View.GONE);
        }
        else
        {
            holder.imageView.setImageResource(content.getIconId());
            String[] arr = content.getContentArray();
            holder.tv.setText(arr[0]);
            holder.tvUnit.setText(arr[1]);
            holder.tvM.setText(arr[2]);
            holder.tvMin.setText(arr[3]);
            holder.tvM.setVisibility(View.VISIBLE);
            holder.tvMin.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount()
    {
        return activeData.size();
    }

    class MyGridViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;
        TextView tv, tvUnit, tvM, tvMin;

        public MyGridViewHolder(View itemView)
        {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.icon_item);
            tv = (TextView) itemView.findViewById(R.id.acitve_item_tv);
            tvUnit = (TextView)itemView.findViewById(R.id.unit_item_nomale);
            tvM = (TextView)itemView.findViewById(R.id.unit_item_m);
            tvMin = (TextView)itemView.findViewById(R.id.unit_item_m_unit);
        }
    }
}
