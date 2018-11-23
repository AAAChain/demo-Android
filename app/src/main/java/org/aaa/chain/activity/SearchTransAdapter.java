package org.aaa.chain.activity;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.List;

public class SearchTransAdapter extends FragmentPagerAdapter {
    private List<String> titles;
    private List<Fragment> fragments;

    SearchTransAdapter(FragmentManager fm, List<String> titles, List<Fragment> fragments) {
        super(fm);
        this.titles = titles;
        this.fragments = fragments;
    }

    @Override public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override public int getCount() {
        return titles.size();
    }

    @Nullable @Override public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    
}
