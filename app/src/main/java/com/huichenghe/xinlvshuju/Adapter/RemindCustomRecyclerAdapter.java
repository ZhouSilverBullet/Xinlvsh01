package com.huichenghe.xinlvshuju.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huichenghe.xinlvshuju.CustomView.RemoveView;
import com.huichenghe.xinlvshuju.DataEntites.CustomRemindEntity;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;

import java.util.ArrayList;

/**
 * Created by lixiaoning on 15-12-info.
 */
public class RemindCustomRecyclerAdapter extends RecyclerView.Adapter<RemindCustomRecyclerAdapter.MyViewHolder>
{
    public static final String TAG = RemindCustomRecyclerAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<CustomRemindEntity> dataList;
    private OnEachButtonClickListener onEachButtonClickListener;



    public RemindCustomRecyclerAdapter(Context mContext, ArrayList<CustomRemindEntity> dataLists)
    {
        this.dataList = dataLists;
        this.mContext = mContext;
    }

    public void addItem(CustomRemindEntity entity, int position)
    {
            dataList.add(position, entity);
            this.notifyItemInserted(position);

//        notifyItemRangeChanged(position, this.getItemCount());
    }

    public void removeItem(int position) {
        dataList.remove(position);
        notifyItemRemoved(position);
    }

    public int getDataPosition()
    {
        return dataList.size();
    }

    // 加载布局文件，返回一个viewHolder
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_for_custom_remind, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        CustomRemindEntity customRemindEntity = dataList.get(position);
//        Log.i(TAG, "图片资源：" + customRemindEntity.getIcon() + "--" +
//                                 customRemindEntity.getTimes() + "--" +
//                                 customRemindEntity.getRepeats() + "--" +
//                                 customRemindEntity.getType());
//        Log.i(TAG, "提醒编号--:" + customRemindEntity.getNumber());
        byte type = customRemindEntity.getType();

                switch (type)
                {
                    case (byte)0x01:
                        holder.mIcon.setImageResource(R.mipmap.movement);
                        holder.mMotionType.setText(mContext.getString(R.string.movement_text));
                        break;
                    case (byte)0x02:
                        holder.mIcon.setImageResource(R.mipmap.appo);
                        holder.mMotionType.setText(mContext.getString(R.string.eat_foot));
                        break;
                    case (byte)0x03:
                        holder.mIcon.setImageResource(R.mipmap.dringk);
                        holder.mMotionType.setText(mContext.getString(R.string.drink));
                        break;
                    case (byte)0x04:
                        holder.mIcon.setImageResource(R.mipmap.medicine);
                        holder.mMotionType.setText(mContext.getString(R.string.take_medicine));
                        break;
                    case (byte)0x05:
                        holder.mIcon.setImageResource(R.mipmap.sleep);
                        holder.mMotionType.setText(mContext.getString(R.string.sleep_text_choose));
                        break;
                    case (byte)0x06:
                        holder.mIcon.setImageResource(R.mipmap.custom_remind_icon);
                        holder.mMotionType.setText(customRemindEntity.getCustomName());
                        break;
                }
        holder.remindTime.setText(customRemindEntity.getTimes());
        holder.remindRepeat.setText(customRemindEntity.getRepeats());
        holder.deleteLayout.setOnClickListener(new Listeners(position));
        holder.detilLayout.setOnClickListener(new Listeners(position));
        holder.removeView.scrollTo(-10, 0);
    }

    @Override
    public int getItemCount()
    {
        return dataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView mIcon;
        TextView mMotionType, remindTime, remindRepeat;
        LinearLayout deleteLayout, detilLayout;
        RemoveView removeView;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            mIcon = (ImageView)itemView.findViewById(R.id.icon_type_l);
            mMotionType = (TextView)itemView.findViewById(R.id.motion_type);
            remindTime = (TextView)itemView.findViewById(R.id.remind_time);
            setLayoutPamarmers(mMotionType);
            setLayoutPamarmersLi(remindTime);
            remindRepeat = (TextView)itemView.findViewById(R.id.remind_repeat);
            deleteLayout = (LinearLayout)itemView.findViewById(R.id.delete_layout);
            detilLayout = (LinearLayout)itemView.findViewById(R.id.remind_detail_layout);
            removeView = (RemoveView) itemView.findViewById(R.id.rimove_layout_for_show);
        }
    }

    private void setLayoutPamarmersLi(TextView mMotionType)
    {
        LinearLayout.LayoutParams pa = (LinearLayout.LayoutParams) mMotionType.getLayoutParams();
        pa.width = mContext.getResources().getDisplayMetrics().widthPixels / 3;
        mMotionType.setLayoutParams(pa);
    }

    private void setLayoutPamarmers(TextView mMotionType)
    {
        RelativeLayout.LayoutParams pa = (RelativeLayout.LayoutParams) mMotionType.getLayoutParams();
        pa.width = mContext.getResources().getDisplayMetrics().widthPixels / 3;
        mMotionType.setLayoutParams(pa);
    }


    class Listeners extends NoDoubleClickListener
    {
        int position;
        public Listeners(int position)
        {
            this.position = position;
        }
        @Override
        public void onNoDoubleClick(View v)
        {
            switch (v.getId())
            {
                case R.id.delete_layout:
                    onEachButtonClickListener.onDeleteButtonClick(position);
                    break;

                case R.id.remind_detail_layout:
                    onEachButtonClickListener.onItemClick(position);
                    break;
            }
        }
    }

//    View.OnClickListener listener = new View.OnClickListener()
//    {
//        @Override
//        public void onClick(View v)
//        {
//
//        }
//    };


    public void setOnEachButtonClickListener(OnEachButtonClickListener onEachButtonClickListener) {
        this.onEachButtonClickListener = onEachButtonClickListener;
    }

    public interface OnEachButtonClickListener
    {
        void onDeleteButtonClick(int position);
        void onItemClick(int position);
    }

}
