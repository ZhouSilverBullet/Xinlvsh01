package com.huichenghe.xinlvshuju.CustomView;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huichenghe.xinlvshuju.DataEntites.OutLineDataEntity;
import com.huichenghe.xinlvshuju.DataEntites.chooseTypeEntity;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;

import java.util.ArrayList;

/**
 * 离线运动选择Dialog
 * Created by lixiaoning on 15-12-clock.
 */
public class CustomChooseTypeDialog
{
    public static final String TAG = CustomChooseTypeDialog.class.getSimpleName();
    private MYAdapter myAdapter;
    Dialog dialog;
    private ArrayList<chooseTypeEntity> dataList;
    private Context mContext;
    private ChooseDialogCallback mChooseDialogCallback;
    private ImageView back;
    private Button beSure;
    private int positionclick = 0;
    private MYAdapter adapter;
    private TextView editOk;
    private TextView editCancle;
    private Dialog builder;
    private AppCompatEditText editName;


    public interface ChooseDialogCallback
    {
        void onChoose(int Type, String sportName);
        void onDissmiss();
        void onCancle();
    }

    public CustomChooseTypeDialog(Context mContext, ChooseDialogCallback callback)
    {
        this.mContext = mContext;
        this.mChooseDialogCallback = callback;
        // 数据
        dataList = new ArrayList<>();
        dataList.add(new chooseTypeEntity(R.mipmap.walk, R.string.walk, OutLineDataEntity.TYPE_WALK));
        dataList.add(new chooseTypeEntity(R.mipmap.running, R.string.running, OutLineDataEntity.TYPE_RUNNINT));
        dataList.add(new chooseTypeEntity(R.mipmap.climing, R.string.climing, OutLineDataEntity.TYPE_CLIMING));
        dataList.add(new chooseTypeEntity(R.mipmap.ball, R.string.ball, OutLineDataEntity.TYPE_BALL_MOVEMENT));
        dataList.add(new chooseTypeEntity(R.mipmap.muscle, R.string.muscle, OutLineDataEntity.TYPE_MUSCLE));
        dataList.add(new chooseTypeEntity(R.mipmap.aerobic, R.string.aerobic, OutLineDataEntity.TYPE_AEROBIC));
        dataList.add(new chooseTypeEntity(R.mipmap.custom_sport_type, R.string.custom_remind, OutLineDataEntity.TYPE_CUSTOM));
        dialog = new Dialog(mContext, R.style.mySizeDialog);
//        dialog.setTitle(mContext.getString(R.string.choose_movement_type_a));
        LayoutInflater inflater =
                (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.choose_type_layout, null);
        dialog.setContentView(linearLayout, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        adapter = new MYAdapter();
        RecyclerView recyclerView = (RecyclerView)linearLayout.findViewById(R.id.layout_choose);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(adapter);
        settingTheDialogWidthAndHeight(linearLayout);
        back = (ImageView)linearLayout.findViewById(R.id.back_to_back);
        back.setOnClickListener(listener);
        beSure = (Button)linearLayout.findViewById(R.id.select_the_modle);
        beSure.setOnClickListener(listener);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                mChooseDialogCallback.onDissmiss();
            }
        });
        dialog.show();
    }

    NoDoubleClickListener listener = new NoDoubleClickListener()
    {
        @Override
        public void onNoDoubleClick(View v) {
            if(v == back)
            {
                if(dialog.isShowing())
                dialog.dismiss();
                mChooseDialogCallback.onCancle();
            }
            else if(v == beSure)
            {
                if(positionclick == 6)
                {
                    MyToastUitls.showToast(mContext, R.string.sport_name_not_null, 1);
                    return;
                }
                mChooseDialogCallback.onChoose(
                        dataList.get(positionclick).getType(), mContext.getString(dataList.get(positionclick).getTypeName()));
                if(dialog.isShowing())
                dialog.dismiss();
            }
            else if(v == editOk)
            {
               Editable editable = editName.getText();
                String sportName = editable.toString();
                if(sportName != null && sportName.isEmpty())
                {
                    MyToastUitls.showToast(mContext, R.string.sport_name_not_null, 1);
                }
                else
                {
                    mChooseDialogCallback.onChoose(6, sportName);
                    if(dialog.isShowing())dialog.dismiss();
                    if(builder.isShowing())builder.dismiss();
                }
            }
            else if(v == editCancle)
            {
                builder.dismiss();
            }
        }
    };

    /**
     * 根据屏幕，设置dialog的宽高
     */
    private void settingTheDialogWidthAndHeight(LinearLayout view)
    {
//        BitmapFactory.Options options = new BitmapFactory.Options();    // 对图片进行处理
//        options.inJustDecodeBounds = true;                              // 只获取图片的边界属性
//        BitmapFactory.decodeResource(mContext.getResources(),  R.mipmap.walk, options );// 解析图片
        FrameLayout.LayoutParams listLp =   (FrameLayout.LayoutParams) view.getLayoutParams();
        listLp.width = getSceenWidthPixels(mContext);
        listLp.height = getScreeHeightPixesl(mContext);
        view.setLayoutParams(listLp);
    }
    private int getSceenWidthPixels(Context mContext)
    {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }
    private int getScreeHeightPixesl(Context mContext)
    {
        DisplayMetrics dms = mContext.getResources().getDisplayMetrics();
        return dms.heightPixels;
    }
    private void settingTheCustomDialogScale(View v)
    {
        FrameLayout.LayoutParams pa = (FrameLayout.LayoutParams)v.getLayoutParams();
        pa.height = (int)(getScreeHeightPixesl(mContext) * 0.3);
        pa.width = (int)(getSceenWidthPixels(mContext) * 0.9);
        v.setLayoutParams(pa);
    }

    /**
     * dialog的Adapter
     */
    class MYAdapter extends RecyclerView.Adapter<MYAdapter.MyHolder>
    {
        ArrayList<MyHolder> holders = new ArrayList<>(7);
        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View v = LayoutInflater.from(mContext).inflate(R.layout.movement_type_choose, parent, false);
            MyHolder holder = new MyHolder(v);
            holders.add(holder);
            return holder;
        }
        @Override
        public void onBindViewHolder(final MyHolder holder, final int position)
        {
            if(position == 0)
            {
                holder.selectView.setVisibility(View.VISIBLE);
            }
            holder.mTextView.setText(dataList.get(position).getTypeName());
            Drawable drawable = mContext.getResources().getDrawable(dataList.get(position).getIconId());
            assert drawable != null;
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.mTextView.setCompoundDrawables(drawable, null, null, null);
            holder.mRelativeLayout.setOnClickListener(new NoDoubleClickListener()
            {
                @Override
                public void onNoDoubleClick(View v)
                {
                    Log.i(TAG, "点击的position;" + position);
                    positionclick = position;
                    if(positionclick == 6)
                    {
                        builder = new Dialog(mContext, R.style.customSportNameDialog);
                        RelativeLayout relativeLayout = getCustomView();
                        builder.setContentView(relativeLayout, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                        builder.setCanceledOnTouchOutside(false);
                        settingTheCustomDialogScale(relativeLayout);
                        builder.show();
                    }
                        holder.selectView.setVisibility(View.VISIBLE);
                        hideOthersView(position);
                }
            });
        }

        private void hideOthersView(int position)
        {
            for (int i = 0; i < holders.size(); i++)
            {
                if(i != position)
                {
                    MyHolder holder = holders.get(i);
                    holder.selectView.setVisibility(View.GONE);
                }
            }
        }

        private RelativeLayout getCustomView()
        {
            RelativeLayout v = (RelativeLayout)LayoutInflater.from(mContext).inflate(R.layout.edit_custom_sport_type, null);
            editName = (AppCompatEditText)v.findViewById(R.id.edit_custom);
            editOk = (TextView)v.findViewById(R.id.edit_ok);
            editCancle = (TextView)v.findViewById(R.id.edit_cancle);
            editOk.setOnClickListener(listener);
            editCancle.setOnClickListener(listener);
            return v;
        }


        @Override
        public int getItemCount()
        {
            return dataList.size();
        }

        /**
         * viewholder
         */
        class MyHolder extends RecyclerView.ViewHolder
        {
//            ImageView mImageView;
            TextView mTextView;
            RelativeLayout mRelativeLayout;
            private ImageView selectView;

            public MyHolder(View itemView)
            {
                super(itemView);
//                mImageView = (ImageView)itemView.findViewById(R.id.type_icon);
                mTextView = (TextView)itemView.findViewById(R.id.type_name);
                mRelativeLayout = (RelativeLayout)itemView.findViewById(R.id.content_head);
                selectView = (ImageView)itemView.findViewById(R.id.select);
            }
        }
    }
}
