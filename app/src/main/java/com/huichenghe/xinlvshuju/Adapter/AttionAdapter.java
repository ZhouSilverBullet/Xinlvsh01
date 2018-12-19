package com.huichenghe.xinlvshuju.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huichenghe.xinlvshuju.CustomView.CircleImageView;
import com.huichenghe.xinlvshuju.CustomView.RemoveView;
import com.huichenghe.xinlvshuju.DataEntites.AttionPersionInfoEntity;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.SDPathUtils;
import com.huichenghe.xinlvshuju.Utils.DpUitls;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.http.IconTaskForNomal;
import com.huichenghe.xinlvshuju.http.ReadPhotoFromSD;
import com.huichenghe.xinlvshuju.http.onBitmapBack;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by DIY on 2016/sitting/18.
 */
public class AttionAdapter extends RecyclerView.Adapter<AttionAdapter.MyHold>
{
    public static final String TAG = AttionAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<AttionPersionInfoEntity> attionData;
    private RecyclerItemClick onItemClick;
    public void setOnItemClick(RecyclerItemClick recyclerItemClick)
    {
        this.onItemClick = recyclerItemClick;
    }
    public AttionAdapter(Context mContext, ArrayList<AttionPersionInfoEntity> attion)
    {
        this.mContext = mContext;
        this.attionData = attion;
    }
    @Override
    public MyHold onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_for_attion_data, null);
        return new MyHold(view);
    }

    @Override
    public void onBindViewHolder(MyHold holder, final int position)
    {
        requesHeadIcon(holder.head, position);
        AttionPersionInfoEntity en = attionData.get(position);
        holder.attionMark.setText(en.getMark());
        holder.dataTime.setText(en.getLastdate());
        SpannableStringBuilder comp = formateString(R.string.movement_count,
                en.getFinishtimes() + mContext.getString(R.string.wancheng_ci), 16, Color.BLACK);
        holder.complete.setText(comp);
        String sleepss = null;


        Log.i(TAG, "亲情关注数据：" + en.getMark());
        Log.i(TAG, "亲情关注数据：" + en.getHeader());
        switch (en.getSleepstatus())
        {
            case "A":
                sleepss = mContext.getString(R.string.chongzu);
                break;
            case "B":
                sleepss = mContext.getString(R.string.liang_hao);
                break;
            case "C":
            case "null":
                sleepss = mContext.getString(R.string.pianshao);
                break;
        }
        String showFatigue = mContext.getString(R.string.Active_zone);
        if(en.getIndexfatigue() != null && en.getIndexfatigue() != "null" && !en.getIndexfatigue().equals(""))
        {
            int fa = Integer.parseInt(en.getIndexfatigue());
            if(fa < 50 && fa > 0)
            {
                showFatigue = mContext.getString(R.string.fatigue);
            }
            else if(fa <= 100 && fa >= 50)
            {
                showFatigue = mContext.getString(R.string.Active_zone);
            }
            else
            {
                showFatigue = mContext.getString(R.string.Active_zone);
            }
        }
        SpannableStringBuilder sleepS = formateString(R.string.sleep_count, sleepss, 16, Color.BLACK);
        SpannableStringBuilder moveS = formateString(R.string.fatigue_count, showFatigue, 16, Color.BLACK);
        holder.sleepState.setText(sleepS);
        holder.moveState.setText(moveS);
        holder.itemAttion.setOnClickListener(new NoDoubleClickListener()
        {
            @Override
            public void onNoDoubleClick(View view) {
                if(onItemClick != null)
                    onItemClick.onItmeClick(position);
            }
        });

        holder.deleteLayout.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                if(deleteListener != null)
                {
                    deleteListener.delete(position);
                }
            }
        });
        holder.removeView.scrollTo(-10, 0);
    }

    private SpannableStringBuilder formateString(int id, String content, int size, int color)
    {
        SpannableString ss = new SpannableString(content);
        ss.setSpan(new AbsoluteSizeSpan((int)DpUitls.SpToPx(mContext, size)), 0, content.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(color), 0, content.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(mContext.getString(id));
        ssb.append("\t");
        ssb.append(ss);
        return ssb;
    }

    private void requesHeadIcon(final CircleImageView head, int position)
    {
        String headName = attionData.get(position).getHeader();
        String[] heads = headName.split("\\.");
        File file = new File(SDPathUtils.getSdcardPath() + MyConfingInfo.SDCARD_DATA_ROOT_DIR + File.separator + heads[0] + ".jpg");
        Log.i(TAG, "关注头像" + file.getPath());
        if(file.exists())
        {
            Log.i(TAG, "走了这里sd卡");
           ReadPhotoFromSD readPhotoFromSD = new ReadPhotoFromSD();
            readPhotoFromSD.setOnBitmapBack(new onBitmapBack() {
                @Override
                public void onBitmapBack(Bitmap bitmap)
                {
                    head.setImageBitmap(bitmap);
                }
                @Override
                public void onBitmapError() {
                    head.setImageResource(R.mipmap.attion_defalt_head);
                }
            });
            readPhotoFromSD.execute(file);
        }
        else
        {
            Log.i(TAG, "走了这里net");
            IconTaskForNomal taskForNomal = new IconTaskForNomal(mContext);
            taskForNomal.setOnBitmapBack(new onBitmapBack() {
                @Override
                public void onBitmapBack(Bitmap bitmap)
                {
                    if(bitmap != null)
                    {
                        Log.i(TAG, "走了这里net" + bitmap);
                        head.setImageBitmap(bitmap);
                    }
                }
                @Override
                public void onBitmapError()
                {
                    Log.i(TAG, "走了这里net_error");
                    head.setImageResource(R.mipmap.attion_defalt_head);
                }
            });
            taskForNomal.execute(MyConfingInfo.WebRoot + "download_userHeader" + "?filename=" +
                            attionData.get(position).getHeader(),
                    attionData.get(position).getHeader());
        }
    }

    @Override
    public int getItemCount()
    {
        return attionData.size();
    }

    class MyHold extends RecyclerView.ViewHolder
    {
        private CircleImageView head;
        private TextView attionMark, dataTime;
        private TextView complete, sleepState, moveState;
        private RelativeLayout itemAttion;
        private RemoveView removeView;
        private LinearLayout deleteLayout;
        public MyHold(View itemView)
        {
            super(itemView);
            head = (CircleImageView)itemView.findViewById(R.id.loving_head_icon);
            attionMark = (TextView)itemView.findViewById(R.id.loving_nick);
            dataTime = (TextView)itemView.findViewById(R.id.loving_time);
            complete = (TextView)itemView.findViewById(R.id.moving_complete);
            sleepState = (TextView)itemView.findViewById(R.id.sleeping_statess);
            moveState = (TextView)itemView.findViewById(R.id.movement_state);
            itemAttion = (RelativeLayout)itemView.findViewById(R.id.item_attion_obj);
            removeView = (RemoveView)itemView.findViewById(R.id.remove_layout);
            removeView.setWidth(mContext.getResources().getDisplayMetrics().widthPixels);
            deleteLayout = (LinearLayout)itemView.findViewById(R.id.delete_attion_layout);
        }
    }

    public interface DeleteListener
    {
        void delete(int position);
    }

    private DeleteListener deleteListener;
    public void setOnDeleteListener(DeleteListener deleteListener)
    {
        this.deleteListener = deleteListener;
    }



}
