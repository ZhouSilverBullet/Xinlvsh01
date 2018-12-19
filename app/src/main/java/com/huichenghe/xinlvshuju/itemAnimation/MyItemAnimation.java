package com.huichenghe.xinlvshuju.itemAnimation;

import android.support.v7.widget.RecyclerView;

/**
 * Created by ${lixiaoning} on 16-info-brocast.
 */
public class MyItemAnimation extends RecyclerView.ItemAnimator
{
    @Override
    public boolean animateDisappearance(RecyclerView.ViewHolder viewHolder,
                                        ItemHolderInfo preLayoutInfo,
                                        ItemHolderInfo postLayoutInfo)
    {



        return false;
    }

    @Override
    public boolean animateAppearance(RecyclerView.ViewHolder viewHolder,
                                     ItemHolderInfo preLayoutInfo,
                                     ItemHolderInfo postLayoutInfo)
    {



        return false;
    }

    @Override
    public boolean animatePersistence(RecyclerView.ViewHolder viewHolder,
                                      ItemHolderInfo preLayoutInfo,
                                      ItemHolderInfo postLayoutInfo)
    {


        return false;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder,
                                 RecyclerView.ViewHolder newHolder,
                                 ItemHolderInfo preLayoutInfo,
                                 ItemHolderInfo postLayoutInfo)
    {



        return false;
    }

    @Override
    public void runPendingAnimations()
    {



    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item)
    {



    }

    @Override
    public void endAnimations()
    {


    }

    @Override
    public boolean isRunning()
    {


        return false;
    }
}
