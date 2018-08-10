package org.aaa.chain.activity;

import android.os.Bundle;
import android.view.View;
import com.squareup.leakcanary.RefWatcher;
import java.util.ArrayList;
import java.util.List;
import org.aaa.chain.IpfsApplication;
import org.aaa.chain.R;
import org.aaa.chain.entities.DataEntity;
import org.aaa.chain.views.CommonPopupWindow;

public class MyHomeDetailActivity extends BaseActivity {

    private List<DataEntity> dataEntities = new ArrayList<>();

    @Override public int initLayout() {
        return R.layout.activity_my_home_detail;
    }

    @Override public void getViewById() {

        initSlideBackLayout(this);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        String title = bundle.getString("title");
        setTitleName(title);
        if (getResources().getString(R.string.myAccount).equals(title)) {
            $(R.id.cl_my_home_detail_account).setVisibility(View.VISIBLE);
            $(R.id.tv_change_phone_number).setOnClickListener(this);
        } else if (getResources().getString(R.string.myWallet).equals(title)) {
            $(R.id.cl_my_home_detail_wallet).setVisibility(View.VISIBLE);
            $(R.id.rl_transaction_history).setOnClickListener(this);
            $(R.id.tv_transfer_accounts).setOnClickListener(this);
        } else {
            $(R.id.cl_my_home_detail_setting).setVisibility(View.VISIBLE);
        }

        dataEntities.clear();
        for (int i = 0; i < 10; i++) {
            DataEntity entity = new DataEntity();
            entity.setDescription("test" + i);
            entity.setTitle("hello" + i);
            dataEntities.add(entity);
        }
    }

    @Override public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rl_transaction_history:
                new CommonPopupWindow(MyHomeDetailActivity.this).pupupWindowTransactionHistory(dataEntities);
                break;

            case R.id.tv_transfer_accounts:
                new CommonPopupWindow(MyHomeDetailActivity.this).pupupWindowTransferAccounts();
                break;
            case R.id.tv_change_phone_number:
                new CommonPopupWindow(MyHomeDetailActivity.this).pupupWindowChangePhoneNumber();
                break;
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = IpfsApplication.getRefWatcher(this);
        refWatcher.watch(this);
    }
}
