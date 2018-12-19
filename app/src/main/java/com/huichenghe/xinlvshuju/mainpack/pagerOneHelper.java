package com.huichenghe.xinlvshuju.mainpack;

import android.view.View;

/**
 * Created by lixiaoning on 2016/7/11.
 */
public class pagerOneHelper
{
    public static final String TAG = pagerOneHelper.class.getSimpleName();
    private MainActivity context;









    public pagerOneHelper(View view, MainActivity context)
    {
        this.context = context;
//        initHeadView(view);
//        updateOutLineDataFromSd(context.currenDate);
////        intitOutLineData();
//        updateThisScreen(context.getCurrentDate());     // 读取全天数据，并显示

    }





















//    private void intitOutLineData()
//    {
//        Cursor mCursor = outlineDataHelper.readDbAndshow(context.getCurrentDate());        // 读取数据库离线运动，并显示
//        if (mCursor.getCount() != 0)        // 如果有数据
//        {
//            outlineDataList.clear();
//            outlineDataList = outlineDataHelper.getTheDataFromCursor(mCursor, outlineDataList);
//            itemAdapter.notifyDataSetChanged();
//            if(showOrHide.getVisibility() == View.VISIBLE)
//            {
//                showOrHide.setVisibility(View.GONE);
//            }
//            if(outLineList.getVisibility() == View.GONE)
//            {
//                outLineList.setVisibility(View.VISIBLE);
//            }
//        }
//        else
//        {
//            if(showOrHide.getVisibility() == View.GONE)
//            {
//                showOrHide.setVisibility(View.VISIBLE);
//            }
//            if(outLineList.getVisibility() == View.VISIBLE)
//            {
//                outLineList.setVisibility(View.GONE);
//            }
//        }
//    }























}
