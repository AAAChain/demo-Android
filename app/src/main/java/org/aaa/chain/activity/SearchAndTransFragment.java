package org.aaa.chain.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;
import com.igexin.sdk.message.GTNotificationMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.aaa.chain.R;

public class SearchAndTransFragment extends BaseFragment implements ReceiveMsgListener {

    private SearchTransAdapter adapter;
    private TabLayout tabLayout;
    private String[] titles;

    @Override public int initLayout() {
        return R.layout.fragment_search_trans;
    }

    @Override public void getViewById() {
        ((BaseActivity) Objects.requireNonNull(getActivity())).back.setVisibility(View.GONE);
        ((BaseActivity) Objects.requireNonNull(getActivity())).setTitleName(getResources().getString(R.string.search_trans));

        tabLayout = $(R.id.tabLayout);
        ViewPager viewPager = $(R.id.tab_viewpager);

        //获取标签数据
        titles = getResources().getStringArray(R.array.search_trans_tab);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new SearchResourceFragment());
        fragments.add(new TransactionResourceFragment());
        adapter = new SearchTransAdapter(getChildFragmentManager(), Arrays.asList(titles), fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        if (((MainActivity) getActivity()).isShowNotifi()) {
            setNotification(true);
        } else {
            setNotification(false);
        }
    }

    public void setNotification(boolean isHave) {

        for (int i = 0; i < titles.length; i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);

            // 更新Badge前,先remove原来的customView,否则Badge无法更新
            View customView = tab.getCustomView();
            if (customView != null) {
                ViewParent parent = customView.getParent();
                if (parent != null) {
                    ((ViewGroup) parent).removeView(customView);
                }
            }

            View view = LayoutInflater.from(getActivity()).inflate(R.layout.tab_layout_item, null);
            TextView textView = (TextView) view.findViewById(R.id.tv_title);
            textView.setText(titles[i]);

            TextView badgeView = (TextView) view.findViewById(R.id.tv_badgeview);
            if (isHave && i == 1) {
                badgeView.setVisibility(View.VISIBLE);
            } else {
                badgeView.setVisibility(View.GONE);
            }
            tab.setCustomView(view);
        }
    }

    @Override public void onClick(View v) {

    }

    @Override public void onDestroy() {
        super.onDestroy();
    }

    @Override public void receiveMsg(GTNotificationMessage message) {
        setNotification(true);
    }
}
