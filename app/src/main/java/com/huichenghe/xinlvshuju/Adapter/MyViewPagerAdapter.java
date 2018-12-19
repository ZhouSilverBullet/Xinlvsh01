package com.huichenghe.xinlvshuju.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * 自定义viewpager适配器，继承FragmentPagerAdapter
 * Created by lixiaoning on 15-10-27.
 */
public class MyViewPagerAdapter extends FragmentPagerAdapter
{
    private ArrayList<Fragment> fragmentList;// fragment集合，即呈现在viewpager上的fragment
    public MyViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public MyViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }


    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
}
