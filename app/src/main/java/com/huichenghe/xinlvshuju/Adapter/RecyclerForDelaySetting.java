package com.huichenghe.xinlvshuju.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;

/**
 * Created by lixiaoning on 15-12-31.
 */
public class RecyclerForDelaySetting extends RecyclerView.Adapter<RecyclerForDelaySetting.MyHoler>
{
    private Context mContext;
    private String[] dataList;
    public RecyclerForDelaySetting(Context mContext)
    {
        this.mContext = mContext;
        String second = mContext.getString(R.string.second);
        dataList = new String[]{4 + second, 6 + second ,8 + second,
                10 + second, 12 + second, 14 + second,
                16 + second, 18 + second, 20 + second, mContext.getString(R.string.immediate_reminder)};
    }

    @Override
    public MyHoler onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_for_delay_settng, parent, false);
        return new MyHoler(view);
    }

    @Override
    public void onBindViewHolder(final MyHoler holder, final int position)
    {
        holder.showTime.setText(dataList[position]);
        holder.showTime.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                itemClickListener.onClickItem(dataList[position]);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return dataList.length;
    }

    class MyHoler extends RecyclerView.ViewHolder
    {
        TextView showTime;
        public MyHoler(View itemView)
        {
            super(itemView);
            showTime = (TextView)itemView.findViewById(R.id.show_the_time);
        }


    }



    private OnDelaySettingItemClickListener itemClickListener;

    public interface OnDelaySettingItemClickListener
    {
        void onClickItem(String position);
    }

    public void setTheItemOnClickListener(OnDelaySettingItemClickListener itemListener)
    {
        this.itemClickListener = itemListener;
    }

}
