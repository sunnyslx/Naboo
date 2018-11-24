package com.idx.naboo.user.personal_center.order;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryan on 18-4-16.
 * Email: Ryan_chan01212@yeah.net
 */

public class OrderPagerAdapter extends FragmentPagerAdapter {

    private FragmentManager fragmentManager;
    private List<Fragment> fragmentList = new ArrayList<>();

    public OrderPagerAdapter(FragmentManager fm , List<Fragment> fragmentList) {
        super(fm);
        this.fragmentManager = fm;
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
}
