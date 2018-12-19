package com.huichenghe.xinlvshuju.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huichenghe.xinlvshuju.CustomView.HistogramImageView;
import com.huichenghe.xinlvshuju.DataEntites.stepAndCalorieEntity;
import com.huichenghe.xinlvshuju.R;

import java.util.ArrayList;

/**
 * 活动详情柱状图适配器
 * 继承RecyclerView内部类，泛型的意思是必须包含一个此类对象
 * Created by lixiaoning on 15-11-14.
 */
public class RecyclerViewAdapterForMovementDetail
        extends RecyclerView.Adapter<RecyclerViewAdapterForMovementDetail.MovementViewHolder>
{
    private Context mContext;       // 对应的Activity
    private ArrayList<stepAndCalorieEntity> zhuzhuangtuEntities;   // 数据实体类
    private int mHeight;            // 自定义柱状图的高度


    // 构造方法，通过构造方法传递参数，并为成员赋值给成员变量
    public RecyclerViewAdapterForMovementDetail(Context mContext, ArrayList<stepAndCalorieEntity> mList, int layoutHeight)
    {
        this.mContext = mContext;
        this.zhuzhuangtuEntities = mList;
        this.mHeight = layoutHeight;
    }


    /**
     * 继承RecyclerView的内部类ViewHolder，
     * 此类在onCreateViewHolder中创建
     */
    public class MovementViewHolder extends RecyclerView.ViewHolder
    {
        HistogramImageView mHistogramImageView;     // 柱状图控件
        public MovementViewHolder(View itemView)
        {
            super(itemView);
            // 通过item的view获取柱状图控件
            mHistogramImageView = (HistogramImageView)itemView.findViewById(R.id.historgram_image);
        }
    }


    /**
     * 此类先调用此方法，创建ViewHolder对象，此对象继承RecyclerView里的ViewHolder，并返回一个viewHolder给api
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MovementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.zhuzhangtu_item, parent, false); // 加载item布局
        return new MovementViewHolder(v);
    }


    /**
     * 此方法在onCreateViewHolder后调用并，相当于getView()
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(MovementViewHolder holder, int position) {

        ViewGroup.LayoutParams params = holder.mHistogramImageView.getLayoutParams();
        params.height = mHeight;
        holder.mHistogramImageView.setLayoutParams(params);     // 设置柱状图控件的高度



        int stepCount = zhuzhuangtuEntities.get(position).getCurrentData();
        int maxCount = zhuzhuangtuEntities.get(position).getMaxData();
        String time = zhuzhuangtuEntities.get(position).getTimes();

        Log.i("", "数据：" + stepCount + maxCount + time);
            holder.mHistogramImageView.addData(time, stepCount, maxCount);
            holder.mHistogramImageView.MyInvalidate();


    }


    /**
     * 获取条目个数
     * @return
     */
    @Override
    public int getItemCount() {
        Log.i("", "数据的个数：" + zhuzhuangtuEntities.size());
        return zhuzhuangtuEntities.size();
    }






}
