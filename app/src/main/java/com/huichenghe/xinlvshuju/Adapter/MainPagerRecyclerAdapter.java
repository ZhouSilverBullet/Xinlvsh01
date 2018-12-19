package com.huichenghe.xinlvshuju.Adapter;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huichenghe.xinlvshuju.DataEntites.OutLineDataEntity;
import com.huichenghe.xinlvshuju.DbEntities.Delete_item_entity;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.FormatUtils;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * 适配器，将离线数据分条目展现在RecyclerView
 * 此类继承自RecyclerView的内部类Adapter
 * Created by lixiaoning on 15-11-17.
 */
public class MainPagerRecyclerAdapter extends RecyclerView.Adapter<MainPagerRecyclerAdapter.MyRecyclerViewHolder>
{
    public static final String TAG = MainPagerRecyclerAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<OutLineDataEntity> dataList;
    // 引用点击借口
    private MyOnItemClickListener myOnItemClickListener;
    public static final int TYPE_HEADE = 0;
    public static final int TYPE_NORMAL = 1;
    private View mHeaderView;
//    private OnHeadClickListener onHeadClickListener;

    public void setHeardView(View heardView)
    {
        this.mHeaderView = heardView;
        notifyItemInserted(0);
    }

    @Override
    public int getItemViewType(int position)
    {
        if(mHeaderView == null)
        {
            return TYPE_NORMAL;
        }
        if(position == 0)
        {
            return TYPE_HEADE;
        }
        return TYPE_NORMAL;
    }

    public MainPagerRecyclerAdapter(Context mContext, ArrayList<OutLineDataEntity> list)
    {
        this.mContext = mContext;
        this.dataList = list;
    }


    public void addItem(OutLineDataEntity entity, int position)
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
        View vItem = LayoutInflater.from(mContext).inflate(R.layout.mainpager_movement_list, parent, false);
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
    public void onBindViewHolder(final MyRecyclerViewHolder holder, final int position)
    {
        if(getItemViewType(position) == TYPE_HEADE) return;
        int positions = holder.getLayoutPosition();
        final int pos = (mHeaderView == null) ? positions : positions - 1;

        if(holder instanceof MyRecyclerViewHolder)
        {
            int totalHR = 0;
            int count = 0;
            int aver = 0;
            String ourlineHr = dataList.get(pos).getHeartReat();
            if(ourlineHr != null && !ourlineHr.equals("null"))
            {
                if(ourlineHr != null && !ourlineHr.equals(""))
                {
                    byte[] outlineHrData = FormatUtils.hexString2ByteArray(ourlineHr);
                    for (int i = 0; i < outlineHrData.length; i++)
                    {
                        if(outlineHrData[i] == (byte)0xff || outlineHrData[i] == (byte)0x00)
                        {
                            continue;
                        }
                        totalHR += outlineHrData[i] & 0xff;
                        count ++;
                    }
                    if(count != 0)
                    {
                        float averageHR = (float)totalHR / (float)count;
                        aver = getHalfUpFromFloat(averageHR);
                    }
                }
            }
            String timess = dataList.get(pos).getTime();

            String[] time = timess.split("=");
            String time1 = time[0];
            String time2 = time[1];
            String ti = time1.substring(11) + "-" + time2.substring(11);
            String hrs = String.valueOf(aver);
            String calo = String.valueOf(dataList.get(pos).getCalorie());
            String stepa = String.valueOf(dataList.get(pos).getStepCount());
            Log.i(TAG, "离线数据时间:" + timess + "  计步数据：" + stepa + "  卡路里数据：" + calo);
            holder.duration.setText(ti);

            final ArrayList<Delete_item_entity> datas = new ArrayList<>();
            datas.add(new Delete_item_entity(R.mipmap.movement_step_icon_white, stepa, "steps"));
            datas.add(new Delete_item_entity(R.mipmap.movement_kcal_icon_white, calo, "kcal"));
            datas.add(new Delete_item_entity(R.mipmap.movement_heart_icon_white, hrs, "bpm"));
            DeleteRecyclerAdapter itemAd = new DeleteRecyclerAdapter(mContext, datas);
            LinearLayoutManager manager = new LinearLayoutManager(mContext);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            holder.deleteLayout.setLayoutManager(manager);
            holder.deleteLayout.setHasFixedSize(true);
            holder.deleteLayout.setItemAnimator(new DefaultItemAnimator());
            holder.deleteLayout.setAdapter(itemAd);
            View e = LayoutInflater.from(mContext).inflate(R.layout.delet_right_layout, null);
            itemAd.setFoodView(e);
            holder.deleteLayout.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState)
                {
                    super.onScrollStateChanged(recyclerView, newState);
                    Log.i(TAG, "recyclerView: newState" + newState);
                    myOnItemClickListener.onScrollStateChanged(newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy)
                {
                    super.onScrolled(recyclerView, dx, dy);
//                    if(dx > 0)
//                    {
//                        holder.deleteLayout.scrollToPosition(datas.size());
//                    }
//                    else
//                    {
//                        holder.deleteLayout.scrollToPosition(0);
//                    }
//                    Log.i(TAG, "recyclerView: dx:" + dx + "dy:" + dy);
                }
            });
//            holder.heartrate.setText(hrs);
//            holder.calorie.setText(calo);
//            holder.step.setText(stepa);
            int mType = dataList.get(pos).getType();
            switch (mType)
            {
                case OutLineDataEntity.TYPE_WALK:
                    mType = R.mipmap.walk;
                    break;
                case OutLineDataEntity.TYPE_RUNNINT:
                    mType = R.mipmap.running;
                    break;
                case OutLineDataEntity.TYPE_BALL_MOVEMENT:
                    mType = R.mipmap.ball;
                    break;
                case OutLineDataEntity.TYPE_CLIMING:
                    mType = R.mipmap.climing;
                    break;
                case OutLineDataEntity.TYPE_MUSCLE:
                    mType = R.mipmap.muscle;
                    break;
                case OutLineDataEntity.TYPE_AEROBIC:
                    mType = R.mipmap.aerobic;
                    break;
                case OutLineDataEntity.TYPE_UNKNOW:
                    mType = R.mipmap.unkonw_type;
                    break;
                case OutLineDataEntity.TYPE_CUSTOM:
                    mType = R.mipmap.custom_sport_type;
                    break;
            }
            Log.i("", "类型：" + mType);
            holder.icon.setImageResource(mType);

            itemAd.setMyOnItemClickListener(new DeleteRecyclerAdapter.DeleteLayoutClickListener()
            {
                @Override
                public void OnClick(int pos)
                {
                    myOnItemClickListener.OnClick(position);
                }
                @Override
                public void deleteClick(int pos)
                {
                    myOnItemClickListener.OnDelete(position);
                }

                @Override
                public void onScroll()
                {
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
        return (mHeaderView == null) ? dataList.size() : dataList.size() + 1;
    }

    class MyRecyclerViewHolder extends RecyclerView.ViewHolder
    {
        TextView duration;
        ImageView icon;
//        RelativeLayout mRelativeLayout;
        RecyclerView deleteLayout;
        public MyRecyclerViewHolder(View itemView)
        {
            super(itemView);
            if(itemView == mHeaderView)return;
            duration = (TextView)itemView.findViewById(R.id.movement_duratin_main);
            icon = (ImageView)itemView.findViewById(R.id.movement_type_main);
            deleteLayout = (RecyclerView)itemView.findViewById(R.id.delete_layout_recycler);
        }
    }

    public interface MyOnItemClickListener
    {
        void OnClick(int position);
        void OnDelete(int position);
        void onScrollStateChanged(int newState);
    }

    public void setMyOnItemClickListener(MyOnItemClickListener myOnItemClickListener)
    {
        this.myOnItemClickListener = myOnItemClickListener;
    }

}
