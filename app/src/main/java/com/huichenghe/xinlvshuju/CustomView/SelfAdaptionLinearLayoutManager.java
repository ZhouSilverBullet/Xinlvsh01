package com.huichenghe.xinlvshuju.CustomView;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lixiaoning on 2016/4/29.
 */
public class SelfAdaptionLinearLayoutManager extends LinearLayoutManager
{
    private int[] measuredDimension = new int[2];

    public SelfAdaptionLinearLayoutManager(Context context) {
        super(context);
    }

    public SelfAdaptionLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state)
    {
        try {
            super.onLayoutChildren(recycler, state);
        }
        catch (IndexOutOfBoundsException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec)
    {
//        super.onMeasure(recycler, state, widthSpec, heightSpec);
        final int widthMode = View.MeasureSpec.getMode(widthSpec);
        final int heightMode = View.MeasureSpec.getMode(heightSpec);
        final int widthSize = View.MeasureSpec.getSize(widthSpec);
        final int heightSize = View.MeasureSpec.getSize(heightSpec);
        int width = 0;
        int height = 0;
        for (int i = 0; i < getItemCount(); i++)
        {
            try{
                measureScrapChild(recycler, i, widthSpec, heightSpec, measuredDimension);
            }catch (IndexOutOfBoundsException e)
            {
                e.printStackTrace();
            }

            if(getOrientation() == HORIZONTAL)
            {
                width = width + measuredDimension[0];
                if(i == 0)
                {
                    height = height + measuredDimension[1];
                }
            }
            else
            {
                height = height + measuredDimension[1];
                if(width == 0)
                {
                    width = width + measuredDimension[0];
                }
            }
        }
        switch (widthMode)
        {
        }
        switch (heightMode)
        {
            case View.MeasureSpec.EXACTLY:
                height = heightSize;
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:
        }
        setMeasuredDimension(widthSpec, height);
    }

    private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec, int heightSpec, int[] measuredDimension) {
        View view = recycler.getViewForPosition(position);

        // For adding Item Decor Insets to view
//        super.measureChildWithMargins(view, 0, 0);
        if (view != null) {
            RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
            int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,
                    getPaddingTop() + getPaddingBottom(), p.height);
            view.measure(widthSpec, childHeightSpec);
            measuredDimension[0] = view.getMeasuredWidth() + p.leftMargin + p.rightMargin;
            measuredDimension[1] = view.getMeasuredHeight() + p.bottomMargin + p.topMargin;
            recycler.recycleView(view);
        }
    }
}
