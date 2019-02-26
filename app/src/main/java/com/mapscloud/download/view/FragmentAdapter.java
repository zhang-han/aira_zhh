package com.mapscloud.download.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments = new ArrayList<Fragment>();

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int location) {
        return fragments.get(location);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void setDatas(List<Fragment> fragments) {
        this.fragments = fragments;
    }
    
}
