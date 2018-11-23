package org.aaa.chain.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import java.util.Objects;
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
        $(R.id.rl_my_resource_management).setOnClickListener(this);
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

            case R.id.rl_my_resource_management:
                startActivity(new Intent(getActivity(), ResourceManagementActivity.class));
                break;

            case R.id.rl_my_setting:
                bundle.putString("title", Objects.requireNonNull(getActivity()).getResources().getString(R.string.setting));
                break;
        }

        if (v.getId() != R.id.rl_my_resource_management) {
            ((BaseActivity) Objects.requireNonNull(getActivity())).startActivity(MyHomeDetailActivity.class, bundle);
        }
    }

    @Override public void onDestroy() {
        super.onDestroy();
    }
}
