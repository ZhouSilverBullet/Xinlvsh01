package com.huichenghe.xinlvshuju.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.huichenghe.xinlvshuju.DataEntites.Info_entry;
import com.huichenghe.xinlvshuju.R;

import java.util.ArrayList;

/**
 * Created by lixiaoning on 2016/5/3.
 */
public class AdapterForInfoRemind extends RecyclerView.Adapter<AdapterForInfoRemind.MyViewHolder>
{
    private Info_entry info_entry;
    private ArrayList<Info_entry> dataList;
    private Context mContext;
    private OnitemChecked checked;
    public AdapterForInfoRemind(Context mContext, ArrayList<Info_entry> en)
    {
        this.dataList = en;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.info_remind_layout, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
        Info_entry entry  = dataList.get(position);
        holder.remindHead.setText(entry.getDataName());
        holder.icon.setImageResource(entry.getDataId());
        holder.mCheckBox.setChecked(entry.isChecked());
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                checked.onChecked(buttonView, isChecked, position);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return dataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        private TextView remindHead;
        private CheckBox mCheckBox;
        private ImageView icon;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            remindHead = (TextView) itemView.findViewById(R.id.info_rmind_name);
            mCheckBox = (CheckBox)itemView.findViewById(R.id.swich_button_for_info_remind);
            icon = (ImageView)itemView.findViewById(R.id.icon_head_info_rmind);
        }
    }


    public interface OnitemChecked
    {
        void onChecked(CompoundButton buttonView, boolean isChecked, int position);
    }

    public void addOnSelectedListener(OnitemChecked checkeListener)
    {
        this.checked = checkeListener;
    }


}
