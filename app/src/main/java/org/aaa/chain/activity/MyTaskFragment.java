package org.aaa.chain.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import com.squareup.leakcanary.RefWatcher;
import java.util.Arrays;
import java.util.Objects;
import org.aaa.chain.ChainApplication;
import org.aaa.chain.R;

public class MyTaskFragment extends BaseFragment {

    MyTaskPagerAdapter adapter;

    @Override public int initLayout() {
        return R.layout.fragment_my_task;
    }

    @Override public void getViewById() {
        ((BaseActivity) Objects.requireNonNull(getActivity())).back.setVisibility(View.GONE);
        ((BaseActivity) Objects.requireNonNull(getActivity())).setTitleName(getResources().getString(R.string.my_task));

        TabLayout tabLayout = $(R.id.tabLayout);
        ViewPager viewPager = $(R.id.tab_viewpager);

        //获取标签数据
        String[] titles = getResources().getStringArray(R.array.my_task_tab);
        adapter = new MyTaskPagerAdapter(getChildFragmentManager(), Arrays.asList(titles));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override public void onClick(View v) {

    }

    @Override public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = ChainApplication.getRefWatcher(Objects.requireNonNull(getActivity()));
        refWatcher.watch(this);
    }
}
