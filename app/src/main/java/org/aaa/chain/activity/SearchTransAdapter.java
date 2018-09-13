package org.aaa.chain.activity;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;
import java.util.List;

public class SearchTransAdapter extends FragmentPagerAdapter {
    private List<String> titles;
    private List<Fragment> fragments = new ArrayList<>();

    public SearchTransAdapter(FragmentManager fm, List<String> titles) {
        super(fm);
        this.titles = titles;
        fragments.add(new SearchResourceFragment());
        fragments.add(new TransactionResourceFragment());
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
