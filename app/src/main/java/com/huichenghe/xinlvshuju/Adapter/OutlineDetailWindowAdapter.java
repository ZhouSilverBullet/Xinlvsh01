package com.huichenghe.xinlvshuju.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huichenghe.xinlvshuju.DataEntites.EntityForOutline;
import com.huichenghe.xinlvshuju.R;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by lixiaoning on 15-12-24.
 */
public class OutlineDetailWindowAdapter extends
        RecyclerView.Adapter<OutlineDetailWindowAdapter.MyOutlineViewholder>
{
    public static final String TAG = OutlineDetailWindowAdapter.class.getSimpleName();
    private Context mContext;
    private View headView;
    public final int TYPE_HEAD = 0;
    public final int TYPE_BODY = 1;
    private ArrayList<EntityForOutline> dataList;
//    private int[] colorArray = new int[]{R.color.danger_color, R.color.not_o2, R.color.has_o2, R.color.fat_burning, R.color.warm_up};

    public OutlineDetailWindowAdapter(Context mContext, ArrayList<EntityForOutline> dataList, View headView)
    {
        this.mContext = mContext;
        this.dataList = dataList;
        this.headView = headView;
//        for (int i = 0; i < dataList.size(); i++)
//        {
//            int a = dataList.get(i).getProgress();
//            Log.i(TAG, "最大值：" + a + "--" + max);
//            if(a > max)
//            {
//                max = a;
//            }
//        }
    }


    public void setHeadView(View head)
    {
        this.headView = head;
        notifyItemInserted(0);
    }
    public void addItem(EntityForOutline entityForOutline, int position)
    {
        dataList.add(position, entityForOutline);
        notifyItemInserted(position);
    }

    public void removeItem(int position)
    {
        dataList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemViewType(int position)
    {
        if(headView == null)
        {
            return TYPE_BODY;
        }
        if(position == 0)
        {
            return TYPE_HEAD;
        }
        return TYPE_BODY;
    }

    @Override
    public MyOutlineViewholder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if(headView != null && viewType == TYPE_HEAD)
        {
            return new MyOutlineViewholder(headView);
        }
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_for_outline_detail, null);
        return new MyOutlineViewholder(v);
    }


    int max = 0;
    private int widthI;

    public void addWidth(int widthO)
    {
        widthI = widthO;
        notifyDataSetChanged();
    }



    @Override
    public void onBindViewHolder(final MyOutlineViewholder holder, int positiones)
    {
        if(getItemViewType(positiones) == TYPE_HEAD)return;
        int posi = holder.getLayoutPosition();
        int position = (headView == null) ? posi : posi - 1;

        EntityForOutline entityForOutline = dataList.get(position);
        holder.sportName.setText(entityForOutline.getMovementName());
        holder.movementHeartRound.setText(entityForOutline.getMovementHRRound());
        holder.sportTime.setText(entityForOutline.getMovementTime());
        holder.movementComp.setText(entityForOutline.getMovementComp());
        int iconId = R.mipmap.movement_warm;
        switch (entityForOutline.getMovementType())
        {
            case 0:
                iconId = R.mipmap.movement_warm;
                break;
            case 1:
                iconId = R.mipmap.movement_aer;
                break;
            case 2:
                iconId = R.mipmap.movement_ana;
                break;
            case 3:
                iconId = R.mipmap.movement_burn;
                break;
            case 4:
                iconId = R.mipmap.movement_limit;
                break;
        }
        holder.movementIcon.setImageResource(iconId);


//        final int[] width = new int[1];
//        int pg = dataList.get(position).getProgress();
//        if(pg != 0)
//        {
//            float percentage = (float)pg/(float)max;
//            Log.i(TAG, "数据是:----" + percentage + dataList.size());
//            int result = getHalfUp(percentage * 100);
//        }
//        holder.sportName.setText(dataList.get(position).getStateName());
//        int times = dataList.get(position).getProgress();
//        String st = (times / 60) + "h" + times % 60 + "min";
//        holder.sportTime.setText(st);
//        final ViewTreeObserver vto = holder.sportColor.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
//        {
//            @Override
//            public void onGlobalLayout()
//            {
//                holder.sportColor.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                addWidth(holder.sportColor.getWidth());
//            }
//        });
//        ViewGroup.LayoutParams params = holder.sportColor.getLayoutParams();
//        params.width = widthI;
//        holder.sportColor.setLayoutParams(params);
//        holder.sportColor.invalidate();
//        holder.sportColor.addData("222", dataList.get(position).getProgress(), max, colorArray[position]);
//        Log.i(TAG, "进度：" + result);
//        Log.i(TAG, "进度布局宽度：::" + width[0]);

    }



    private int getHalfUp(float v)
    {
        if(v != 0)
        {
            BigDecimal bigDecimal = new BigDecimal(v);
            bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP);
            return bigDecimal.intValue();
        }
        return 0;
    }

    @Override
    public int getItemCount()
    {
        return (headView == null) ? dataList.size() : dataList.size() + 1;
    }

    class MyOutlineViewholder extends RecyclerView.ViewHolder
    {
        ImageView movementIcon;
        TextView sportName;
        TextView sportTime;
        TextView movementHeartRound;
        TextView movementComp;
        public MyOutlineViewholder(View itemView)
        {
            super(itemView);
            if(itemView == headView)return;
            movementIcon = (ImageView)itemView.findViewById(R.id.movement_icon);
            sportName = (TextView)itemView.findViewById(R.id.movement_name);
            sportTime = (TextView)itemView.findViewById(R.id.movement_time);
            movementHeartRound = (TextView)itemView.findViewById(R.id.movement_heart_round);
            movementComp = (TextView)itemView.findViewById(R.id.movement_comp);
        }
    }
}
