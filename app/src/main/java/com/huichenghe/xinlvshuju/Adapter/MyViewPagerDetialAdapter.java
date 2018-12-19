package com.huichenghe.xinlvshuju.Adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * 自定义viewpager适配器，继承FragmentPagerAdapter
 * Created by lixiaoning on 15-10-27.
 */
public class MyViewPagerDetialAdapter extends PagerAdapter
{
    private ArrayList<View> fragmentList;// fragment集合，即呈现在viewpager上的fragment
    public MyViewPagerDetialAdapter(ArrayList<View> dataList)
    {
        this.fragmentList = dataList;
    }


    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(fragmentList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        container.addView(fragmentList.get(position));
        return fragmentList.get(position);
    }
}
