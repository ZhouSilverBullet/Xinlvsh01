package com.huichenghe.xinlvshuju.CustomView;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.huichenghe.xinlvshuju.Adapter.TimeSelectAdapter;
import com.huichenghe.xinlvshuju.R;

/**
 * Created by lixiaoning on 2016/5/17.
 */
public class SelectTimeDurtion
{
    private final static String TAG = SelectTimeDurtion.class.getSimpleName();
    private Context mContext;
    private Dialog dialog;
    private OnResultSelect onResultSelect;
    private TimeSelectAdapter adapter;
    public interface OnResultSelect
    {
        void resultSelect(String result);
    }




    public SelectTimeDurtion(Context mContext, String item, final OnResultSelect onResultSelect)
    {
        this.mContext = mContext;
        this.onResultSelect = onResultSelect;
        String[] it = item.split("\\t");
//        Log.i(TAG, "当前的事件:" + it[0]);
        dialog = new Dialog(mContext, R.style.mySizeDialog);
        View view = LayoutInflater.from(mContext).inflate(R.layout.time_durion_select, null);
        dialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.time_list);
        adapter = new TimeSelectAdapter(mContext, it[0]);
        adapter.setOnTimeItemClick(new TimeSelectAdapter.OnTimeItemClick() {
            @Override
            public void onTimeClick(String time) {
                dialog.dismiss();
                onResultSelect.resultSelect(time);
            }
        });
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(adapter);


        dialog.show();
    }




}
