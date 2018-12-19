package com.huichenghe.xinlvshuju.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huichenghe.xinlvshuju.DbEntities.Delete_item_entity;
import com.huichenghe.xinlvshuju.R;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * 适配器，将离线数据分条目展现在RecyclerView
 * 此类继承自RecyclerView的内部类Adapter
 * Created by lixiaoning on 15-11-17.
 */
public class DeleteRecyclerAdapter extends RecyclerView.Adapter<DeleteRecyclerAdapter.MyRecyclerViewHolder>
{
    public static final String TAG = DeleteRecyclerAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<Delete_item_entity> dataList;
    // 引用点击借口
    private DeleteLayoutClickListener myOnItemClickListener;
    public static final int TYPE_HEADE = 0;
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_FOOD = 2;

    private View mHeaderView;
    private View mFoodView;
//    private OnHeadClickListener onHeadClickListener;

    public void setHeardView(View heardView)
    {
        this.mHeaderView = heardView;
        notifyItemInserted(0);
    }

    public void setFoodView(View foodView)
    {
        this.mFoodView = foodView;
        notifyItemInserted(dataList.size());
    }

    @Override
    public int getItemViewType(int position)
    {
//        if(mHeaderView == null && mFoodView == null)
//        {
//            return TYPE_NORMAL;
//        }
        if(mHeaderView != null && position == 0)
        {
            return TYPE_HEADE;
        }
        if(mFoodView != null && position == dataList.size())
        {
            return TYPE_FOOD;
        }
        return TYPE_NORMAL;
    }

    public DeleteRecyclerAdapter(Context mContext, ArrayList<Delete_item_entity> list)
    {
        this.mContext = mContext;
        this.dataList = list;
    }


    public void addItem(Delete_item_entity entity, int position)
    {
        dataList.add(position, entity);
        notifyItemInserted(position);
    }


    /**
     * 此方法创建一个ViewHolder
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MyRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if(mHeaderView != null && viewType == TYPE_HEADE)
        {
            return new MyRecyclerViewHolder(mHeaderView);
        }
        if(mFoodView != null && viewType == TYPE_FOOD)
        {
            return new MyRecyclerViewHolder(mFoodView);
        }
        View vItem = LayoutInflater.from(mContext).inflate(R.layout.mainpager_movement_list_subview, parent, false);
        // 创建ViewHolder对象，并传入view
        MyRecyclerViewHolder h = new MyRecyclerViewHolder(vItem);
        return h;
    }




    /**
     * 绑定数据到holder上的各个控件
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(MyRecyclerViewHolder holder, final int position)
    {
        int itemType = getItemViewType(position);
        Log.i(TAG, "item的类型：" + itemType);
        if(itemType == TYPE_HEADE) return;
        if(itemType == TYPE_FOOD)
        {
            mFoodView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    myOnItemClickListener.deleteClick(dataList.size());
                }
            });
            return;
        }
        int positions = holder.getLayoutPosition();
        final int pos = (mHeaderView == null) ? positions : positions - 1;

        if(holder instanceof MyRecyclerViewHolder)
        {
            holder.mIcon.setImageResource(dataList.get(pos).getIcon());
            holder.moveData.setText(dataList.get(pos).getMoveData());
            holder.moveUnit.setText(dataList.get(pos).getMoveUnit());
            Log.i(TAG, "当前item的数据：头像" + dataList.get(pos).getIcon() + "  数据：" + dataList.get(pos).getMoveData() + "  单位：" + dataList.get(pos).getMoveUnit());
            holder.stepLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    myOnItemClickListener.OnClick(pos);
                }
            });
        }
//        else
//        {
//            holder.relativeLayout.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    switch (v.getId())
//                    {
//                        case R.id.center_data_layout:
//                            Toast.makeText(mContext, "点击了嘿嘿", Toast.LENGTH_SHORT).show();
//                            onHeadClickListener.OnHeadClick(0);
//
//                            break;
//                        case R.id.setting_the_target:
//
//                            onHeadClickListener.OnHeadClick(clock);
//
//                            break;
//                    }
//
//                }
//            });
//        }
    }

    private int getHalfUpFromFloat(float averageHR)
    {
        BigDecimal bigDecimal = new BigDecimal(averageHR);
        bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP);
        return bigDecimal.intValue();
    }


    @Override
    public int getItemCount()
    {
        int count = dataList.size();
        if(mHeaderView != null)
        {
            count ++;
        }
        if(mFoodView != null)
        {
            count++;
        }
        return count;
    }

    class MyRecyclerViewHolder extends RecyclerView.ViewHolder
    {
        ImageView mIcon;
        TextView moveData;
        TextView moveUnit;
        RelativeLayout stepLayout;
        public MyRecyclerViewHolder(View itemView)
        {
            super(itemView);
            if(itemView == mHeaderView)return;
            if(itemView == mFoodView)return;
            mIcon = (ImageView)itemView.findViewById(R.id.step_item_sub);
            moveData = (TextView)itemView.findViewById(R.id.movement_step_count_main);
            moveUnit = (TextView)itemView.findViewById(R.id.steps_unit);
            stepLayout = (RelativeLayout)itemView.findViewById(R.id.step_layout_sub);
            int widthPixels = mContext.getResources().getDisplayMetrics().widthPixels;
            RecyclerView.LayoutParams pa = (RecyclerView.LayoutParams) stepLayout.getLayoutParams();
            pa.width = widthPixels/64 * 19;
            stepLayout.setLayoutParams(pa);
        }
    }

    public interface DeleteLayoutClickListener
    {
        void OnClick(int position);
        void deleteClick(int position);
        void onScroll();
    }

    public void setMyOnItemClickListener(DeleteLayoutClickListener myOnItemClickListener)
    {
        this.myOnItemClickListener = myOnItemClickListener;
    }

}
