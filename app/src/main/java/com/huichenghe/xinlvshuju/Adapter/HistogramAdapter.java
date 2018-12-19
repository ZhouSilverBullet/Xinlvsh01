package com.huichenghe.xinlvshuju.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huichenghe.xinlvshuju.CustomView.HistogramImageView;
import com.huichenghe.xinlvshuju.DataEntites.stepAndCalorieEntity;
import com.huichenghe.xinlvshuju.R;

import java.util.ArrayList;

/**
 * 柱状图适配器, 呈现计步和卡路里
 * Created by lixiaoning on 15-11-13.
 */
public class HistogramAdapter extends RecyclerView.Adapter<HistogramAdapter.HistogramViewholder>
{
    public static final String TAG = HistogramAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<stepAndCalorieEntity> dataList;
    private int color ;

    private int mHeight;
    /**
     * 赋值并刷新界面
     * @param h
     */
    public void initWH(int h)
    {
        mHeight = h;
        notifyDataSetChanged(); // 刷新显示
    }

    public HistogramAdapter(Context c, ArrayList<stepAndCalorieEntity> data, int color)
    {
        this.color = color;
        this.context = c;
        this.dataList = data;
    }

    public void addItem(stepAndCalorieEntity entity, int position)
    {
        dataList.add(position, entity);
        notifyItemInserted(position);
    }


    @Override
    public HistogramViewholder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view =  LayoutInflater.from(context).inflate(R.layout.ce_attention_persion_bt_item, parent, false);
        return new HistogramViewholder(view);
    }

    @Override
    public void onBindViewHolder(HistogramViewholder holder, int position)
    {
        stepAndCalorieEntity entity = dataList.get(position);
        holder.histogram.addData(entity.getTimes(), entity.getCurrentData(), entity.getMaxData());
//        holder.histogram.invalidate();
        holder.histogram.MyInvalidate();
    }

    @Override
    public int getItemCount()
    {
        return dataList.size();
    }


    class HistogramViewholder extends RecyclerView.ViewHolder
    {
        HistogramImageView histogram;
        public HistogramViewholder(View itemView)
        {
            super(itemView);
            histogram = (HistogramImageView)itemView.findViewById(R.id.baitian_image);
            histogram.setHistogramColor(color);
            histogram.setHeadColor(Color.WHITE);
            histogram.setFootColor(context.getResources().getColor(R.color.black_color_transparent));
        }
    }




























//    // ViewHolder 里边有一个自定义的柱状图控件对象
//    class LocalViewHolder extends ViewHolder
//    {
//       HistogramImageView mHistogramImageView;
//    }
//
//
//    class LocaleItemData extends ItemData
//    {
//        String time;
//        int stepCount;
//        int maxStep;
//    }




//    public void addStepItem(String time, int setp, int maxStep)
//    {
//        LocaleItemData data = new LocaleItemData();
//        data.time = time;
//        data.stepCount = setp;
//        data.maxStep = maxStep;
//        addItem(data);
//    }





//    @Override
//    protected ViewHolder newViewHolder(View paramView, int paramInt)
//    {
//        LocalViewHolder holder = new LocalViewHolder();
//        holder.mHistogramImageView = (HistogramImageView)paramView.findViewById(R.id.baitian_image);
//        return holder;
//    }

//    @Override
//    public synchronized View getView(int position, View convertView, ViewGroup parent)
//    {
//        if(getCount() == 0)
//        {
//            return null;
//        }
//        convertView = newListItemView(parent);
//        ViewHolder holder = newViewHolder(convertView, position);
//        setListItemData(holder, position);
//        return convertView;
//    }

//    @Override
//    protected View newListItemView(ViewGroup paramViewGroup)
//    {
//        return LayoutInflater.from(mContext).inflate(R.layout.ce_attention_persion_bt_item, paramViewGroup, false);
//    }


//    @Override
//    protected void setListItemData(ViewHolder paramViewHolder, int paramInt)
//    {
//        LocalViewHolder hol = (LocalViewHolder)paramViewHolder;
//        LocaleItemData item = (LocaleItemData)getItem(paramInt);
//
//        ViewGroup.LayoutParams params = hol.mHistogramImageView.getLayoutParams();
//
//        params.height = mHeight;
//        hol.mHistogramImageView.setLayoutParams(params);
//        hol.mHistogramImageView.addData(item.time, item.stepCount, item.maxStep);
//
//        hol.mHistogramImageView.invalidate();
//    }


}
