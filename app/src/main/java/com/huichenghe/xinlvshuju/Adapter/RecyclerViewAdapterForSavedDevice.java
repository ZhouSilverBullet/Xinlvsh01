package com.huichenghe.xinlvshuju.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.bleControl.Ble.LocalDeviceEntity;
import com.huichenghe.xinlvshuju.DbEntities.DeviceSaveData;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 显示保存设备的RecyclerView的adapter
 *
 * Created by lixiaoning on 15-11-sitting.
 */
public class RecyclerViewAdapterForSavedDevice
        extends RecyclerView.Adapter<RecyclerViewAdapterForSavedDevice.MyRecyclerViewHoldler>
{
    private Context mContext;
    private List<DeviceSaveData> datas;
    private OnItemInnerButtonClickListener myOnItemInnerButtonClickListener;


    public RecyclerViewAdapterForSavedDevice(Context mContext, List<DeviceSaveData> datas)
    {
        this.mContext = mContext;
        this.datas = datas;
    }


    @Override
    public MyRecyclerViewHoldler onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // items
        View view = LayoutInflater.from(mContext).inflate(R.layout.scan_device_item_save, parent, false);
        return new MyRecyclerViewHoldler(view);
    }

    private Animation getAnimation()
    {
//        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 0.0f, 0.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        animation.setDuration(460);
        return AnimationUtils.loadAnimation(mContext, R.anim.scale_for_recyclerview);
    }



    public void addItem(DeviceSaveData dataItem)
    {
        datas.add(dataItem);
        notifyItemInserted(datas.size() - 1);
    }

    @Override
    public void onBindViewHolder(final MyRecyclerViewHoldler holder, final int position)
    {
//        holder.cardView.setAnimation(getAnimation());
        DeviceSaveData device = datas.get(position);
        holder.deviceName.setText(device.getDeviceName());
        // 连接时的时间
        Date connecteDate = device.getDate();
        // 当前时间
        long time = System.currentTimeMillis();
        Date currentDate = new Date();
        currentDate.setTime(time);
        // 比较两个时间
        String dateResult = compareTheBothDate(connecteDate, currentDate);
        holder.connecteTime.setText(dateResult);

        if(position == 0)
        {
            LocalDeviceEntity deviceEntity = new LocalDeviceEntity(
                    device.getDeviceName(), device.getDeviceAddress(), -50, new byte[]{}
            );
            boolean hasConnected = (BluetoothLeService.getInstance() != null) && BluetoothLeService.getInstance().isDeviceConnected(deviceEntity);
            Log.i("", "走了这里。。？？" + hasConnected );
            if(hasConnected)
            {
                holder.btConnecte.setVisibility(View.GONE);             // 隐藏连接按钮
                holder.imConnecte.setVisibility(View.VISIBLE);          // 显示对号
            }
            else
            {
                holder.btConnecte.setVisibility(View.VISIBLE);
                holder.imConnecte.setVisibility(View.GONE);
            }
        }
//        if(device.getConnecteState() == MyConfingInfo.CONNECTED)    // 若此设备是练级状态，
//        {
//            holder.btConnecte.setVisibility(View.GONE);             // 隐藏连接按钮
//            holder.imConnecte.setVisibility(View.VISIBLE);          // 显示对号
//        }else if(device.getConnecteState() == MyConfingInfo.DISCONNECTE)
//        {
//            holder.btConnecte.setVisibility(View.VISIBLE);
//            holder.imConnecte.setVisibility(View.GONE);
//        }






//        holder.connecteTime.setText(device.getDate().toString());
        // 为控件设置监听
        holder.btConnecte.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                myOnItemInnerButtonClickListener.OnClickViewOne(position, v, holder.imConnecte);
            }
        });
        holder.btDisconnecte.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                myOnItemInnerButtonClickListener.OnClickViewTwo(position, holder.btConnecte, holder.imConnecte);
            }
        });
    }



    /**
     * 对两个Date对象进行比较，
     * @param connecteDate
     * @param currentDate
     */
    private String compareTheBothDate(Date connecteDate, Date currentDate) {
        String rtString = null;
        SimpleDateFormat dateFormatOne = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String oneDate = dateFormatOne.format(connecteDate);// 连接时间转化为固定格式字符串
        String twoDate = dateFormatOne.format(currentDate); // 当前时间转化为固定格式字符串

        int oneYear = Integer.parseInt(oneDate.substring(0, 4));
        int oneMonth = Integer.parseInt(oneDate.substring(5, 7));
        int oneDay = Integer.parseInt(oneDate.substring(8));
        int twoYear = Integer.parseInt(twoDate.substring(0, 4));
        int twoMonth = Integer.parseInt(twoDate.substring(5, 7));
        int twoDay = Integer.parseInt(twoDate.substring(8));

        int difYear = twoYear - oneYear;
        int difMonth = twoMonth - oneMonth;
        int difDay = twoDay - oneDay;


        if(difYear != 0)    // 不等于0代表不是同一年，
        {
            rtString = difYear + mContext.getString(R.string.year_ago);

        }
        if(difMonth != 0 && difYear == 0)   // 不等于0代表不是同一个月，
        {
            rtString = difMonth + mContext.getString(R.string.month_ago);
        }
        if(difDay != 0 && difYear == 0 && difMonth == 0)                     // 不等于0代表不是同一天
        {
            rtString = difDay + mContext.getString(R.string.day_age);
        }

        if(difDay == 0 && difYear == 0 && difMonth == 0)
        {
            rtString = mContext.getString(R.string.today);
        }



        return rtString;

    }


    public void removeItem(int position)
    {
        notifyItemRemoved(position);
    }

    /**
     * 设置监听器
     * @param myOnItemInnerButtonClickListener
     */
    public void setOnItemInnerButtonClickListener(OnItemInnerButtonClickListener myOnItemInnerButtonClickListener)
    {
        this.myOnItemInnerButtonClickListener = myOnItemInnerButtonClickListener;
    }

    /**
     * 监听器，监听item上按钮的点击事件
     */
    public interface OnItemInnerButtonClickListener
    {
        void OnClickViewOne(int position, View vBt, View vIm);
        void OnClickViewTwo(int position, View vBt, View vIm);
    }

//    /**
//     * 监听器
//     */
//    View.OnClickListener clickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            switch (v.getId())
//            {
//                case R.id.button_connecte:
//                    myOnItemInnerButtonClickListener.OnClickViewOne();
//                    break;
//                case R.id.button_disconnect:
//                    myOnItemInnerButtonClickListener.OnClickViewTwo();
//                    break;
//            }
//        }
//    };

    @Override
    public int getItemCount()
    {
        return datas.size();
    }




    /**
     * viewHoldler
     */
    class MyRecyclerViewHoldler extends RecyclerView.ViewHolder
   {
       TextView deviceName;
       TextView connecteTime;
       TextView btConnecte;
       ImageView btDisconnecte;
       ImageView imConnecte;
//       CardView cardView;
       public MyRecyclerViewHoldler(View itemView)
       {
           super(itemView);
           deviceName = (TextView)itemView.findViewById(R.id.device_name);
           connecteTime = (TextView)itemView.findViewById(R.id.connecte_time);
           btConnecte = (TextView)itemView.findViewById(R.id.button_connecte);
           btDisconnecte = (ImageView)itemView.findViewById(R.id.button_disconnect);
           imConnecte = (ImageView)itemView.findViewById(R.id.image_connect);
//           cardView = (CardView)itemView.findViewById(R.id.card_view_layout);

       }
   }



}
