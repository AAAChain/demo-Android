package org.aaa.chain.activity;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.List;

public class MyTaskPagerAdapter extends FragmentPagerAdapter {
    private List<String> titles;

    public MyTaskPagerAdapter(FragmentManager fm, List<String> titles) {
        super(fm);
        this.titles = titles;
    }

    @Override public Fragment getItem(int position) {
        MyTaskContentFragment fragment = new MyTaskContentFragment();
        return fragment;
    }

    @Override public int getCount() {
        return titles.size();
    }

    @Nullable @Override public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
