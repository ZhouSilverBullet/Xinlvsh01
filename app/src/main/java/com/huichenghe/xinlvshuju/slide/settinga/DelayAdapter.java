package com.huichenghe.xinlvshuju.slide.settinga;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import java.util.ArrayList;

/**
 * Created by lixiaoning on 2016/6/8.
 */
public class DelayAdapter extends RecyclerView.Adapter<DelayAdapter.MyviewHolder>
{
    private Context context;
    private ArrayList<String> dataList;
    private int selectPosition;
    private OnDealItemClick onDealItemClick;

    public void setOnDealItemClick(OnDealItemClick onDealItemClick)
    {
        this.onDealItemClick = onDealItemClick;
    }

    public DelayAdapter(Context context, ArrayList<String> data, int position)
    {
        this.context = context;
        this.dataList = data;
        this.selectPosition = position;
    }
    @Override
    public MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.spinner_for_delay, null);
        return new MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyviewHolder holder, final int position)
    {
        if(position == 0)
        {
            holder.textView.setText(context.getString(R.string.immediate_reminder));
        }
        else
        {
            StringBuffer buffer = new StringBuffer();
            buffer.append(dataList.get(position));
            buffer.append(context.getString(R.string.second));
            holder.textView.setText(buffer.toString());
        }
        if(selectPosition == position)
        {
            holder.choose.setImageResource(R.mipmap.select_button);
            holder.textView.setTextColor(context.getResources().getColor(R.color.direct_text_color));
        }
        holder.mItem.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(onDealItemClick != null)
                {
                    onDealItemClick.onClick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount()
    {
        return dataList.size();
    }

    class MyviewHolder extends RecyclerView.ViewHolder
    {
        ImageView choose;
        CheckedTextView textView;
        RelativeLayout mItem;

        public MyviewHolder(View itemView) {
            super(itemView);
            choose = (ImageView)itemView.findViewById(R.id.checked_icon);
            textView = (CheckedTextView)itemView.findViewById(R.id.check_select);
            mItem = (RelativeLayout)itemView.findViewById(R.id.item_delay);
        }
    }
}
