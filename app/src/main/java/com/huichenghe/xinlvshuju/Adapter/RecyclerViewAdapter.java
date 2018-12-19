package com.huichenghe.xinlvshuju.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huichenghe.bleControl.Ble.LocalDeviceEntity;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;

import java.util.ArrayList;

/**
 * RecyclerView的适配器
 * Created by lixiaoning on 15-11-phone.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewAdapterViewHolder>
{
    private Context context;

    // 引用监听借口
    private OnScanDeviceItemClickListener itemClickListener;
    // 数据集合
    private ArrayList<LocalDeviceEntity> scanDevice;
    // 将数据赋值到本地
    public RecyclerViewAdapter(Context context, ArrayList<LocalDeviceEntity> data)
    {
//        if(data != null)
//        {   // 写代码时卡在此处，原因是没有构建ArrayList对象，直接写this.data = data,本地为null所以报错
//
//            scanDevice = new ArrayList<LocalDeviceEntity>(data);
//        }
//        else
//        {
//            scanDevice = new ArrayList<LocalDeviceEntity>();
//        }

        this.scanDevice = data;

        this.context = context;
    }


    public void addItem(LocalDeviceEntity entity, int position)
    {
        scanDevice.add(position, entity);
        this.notifyItemInserted(position);
    }


    public void removeItem(int position)
    {
        scanDevice.remove(position);
        this.notifyItemRemoved(position);

    }

    @Override
    public RecyclerViewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // 加载item布局
        View view = LayoutInflater.from(context).inflate(R.layout.scan_device_item_now, parent, false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                 view.setLayoutParams(lp);
        // 构建ViewHoldler对象
        return new RecyclerViewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapterViewHolder holder, final int position)
    {
        holder.tvDeviceName.setText(scanDevice.get(position).getName());
        holder.tvDeviceName.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                itemClickListener.OnItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return scanDevice.size();
    }

    /**
     * 继承RecyclerView的内部类ViewHolder
     */
    public class RecyclerViewAdapterViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvDeviceName;
        public RecyclerViewAdapterViewHolder(View itemView)
        {
            super(itemView);
            tvDeviceName = (TextView)itemView.findViewById(R.id.device_name_show);

        }
    }


    public void setOnScanDeviceItemClickListener(OnScanDeviceItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }


    public interface OnScanDeviceItemClickListener
    {
        public void OnItemClick(int position);
    }


}
