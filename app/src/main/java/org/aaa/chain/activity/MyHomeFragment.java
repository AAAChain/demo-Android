package org.aaa.chain.activity;

import android.os.Bundle;
import android.view.View;
import com.squareup.leakcanary.RefWatcher;
import java.util.Objects;
import org.aaa.chain.ChainApplication;
import org.aaa.chain.R;

public class MyHomeFragment extends BaseFragment {

    @Override public int initLayout() {
        return R.layout.fragment_my_home;
    }

    @Override public void getViewById() {
        ((BaseActivity) Objects.requireNonNull(getActivity())).back.setVisibility(View.GONE);
        ((BaseActivity) Objects.requireNonNull(getActivity())).setTitleName(getResources().getString(R.string.my_home));
        $(R.id.rl_my_account).setOnClickListener(this);
        $(R.id.rl_my_wallet).setOnClickListener(this);
        $(R.id.rl_my_setting).setOnClickListener(this);

    }

    @Override public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.rl_my_account:

                bundle.putString("title", Objects.requireNonNull(getActivity()).getResources().getString(R.string.myAccount));
                break;

            case R.id.rl_my_wallet:

                bundle.putString("title", Objects.requireNonNull(getActivity()).getResources().getString(R.string.myWallet));
                break;

            case R.id.rl_my_setting:
                bundle.putString("title", Objects.requireNonNull(getActivity()).getResources().getString(R.string.setting));

                break;
        }

        ((BaseActivity) Objects.requireNonNull(getActivity())).startActivity(MyHomeDetailActivity.class, bundle);
    }

    @Override public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = ChainApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
