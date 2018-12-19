package com.huichenghe.xinlvshuju.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huichenghe.xinlvshuju.CustomView.RemoveView;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;

import java.util.ArrayList;


    /**
     * 显示时间的RecyclerView的适配器
     */
public class MYRecyclerAdapter extends RecyclerView.Adapter<MYRecyclerAdapter.MyHolder>
    {
        private Context mContext;
        private ArrayList<String> timeList;
        private OnDeleteListener onDeleteListener;

        public MYRecyclerAdapter(Context mContext, ArrayList<String> data)
        {
            this.mContext = mContext;
            this.timeList = data;
        }


        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {// 在oncreate中加载布局，返回holder
            View v = LayoutInflater.from(mContext).inflate(R.layout.item_for_show_remind_tiem, parent, false);
            return new MyHolder(v);
        }

        @Override
        public void onBindViewHolder(final MyHolder holder, final int position)
        {
            NoDoubleClickListener listener = new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    switch (v.getId())
                    {
                        case R.id.delete_this_time:
                            onDeleteListener.onDelete(position);
                            holder.removeView.scrollTo(-10, 0);
                            break;
                        case R.id.time_in_item:
                            onDeleteListener.onItemClic(position);
                            break;
                    }
                }
            };

            // 在控件上设置数据
            holder.mTime.setText(timeList.get(position));
            holder.deleteTime.setOnClickListener(listener);
            holder.mTime.setOnClickListener(listener);
        }

        @Override
        public int getItemCount()
        {
            return timeList.size();
        }

        class MyHolder extends RecyclerView.ViewHolder
        {
            TextView mTime;
            LinearLayout deleteTime;
            RemoveView removeView;
            public MyHolder(View itemView)
            {
                super(itemView);
                mTime = (TextView)itemView.findViewById(R.id.time_in_item);// 里边只有一个控件
                deleteTime = (LinearLayout)itemView.findViewById(R.id.delete_this_time);
                removeView = (RemoveView)itemView.findViewById(R.id.scroll_layout);
                removeView.setWidth(mContext.getResources().getDisplayMetrics().widthPixels);
            }
        }




        public void setOnDeleteListener(OnDeleteListener onDeleteListener)
        {
            this.onDeleteListener = onDeleteListener;
        }

        public interface OnDeleteListener
        {
            void onDelete(int position);

            void onItemClic(int position);

        }

    }


