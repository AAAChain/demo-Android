package org.aaa.chain.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.squareup.leakcanary.RefWatcher;
import java.util.ArrayList;
import java.util.List;
import org.aaa.chain.ChainApplication;
import org.aaa.chain.Constant;
import org.aaa.chain.JSInteraction;
import org.aaa.chain.R;
import org.aaa.chain.entities.ResumeRequestEntity;
import org.aaa.chain.views.CommonPopupWindow;

public class MyHomeDetailActivity extends BaseActivity {

    private List<ResumeRequestEntity> dataEntities = new ArrayList<>();
    private TextView tvBalance;
    ProgressDialog dialog = null;

    @Override public int initLayout() {
        return R.layout.activity_my_home_detail;
    }

    @Override public void getViewById() {

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        String title = bundle.getString("title");
        setTitleName(title);
        if (getResources().getString(R.string.myAccount).equals(title)) {
            $(R.id.cl_my_home_detail_account).setVisibility(View.VISIBLE);
            $(R.id.tv_change_phone_number).setOnClickListener(this);
            TextView account = $(R.id.tv_account_name);
            account.setText(ChainApplication.getInstance().getBaseInfo().getDocs().get(0).getAccount());
        } else if (getResources().getString(R.string.myWallet).equals(title)) {
            $(R.id.cl_my_home_detail_wallet).setVisibility(View.VISIBLE);
            tvBalance = $(R.id.tv_available_balance);
            ((TextView) $(R.id.tv_wallet_address)).setText(Constant.getPublicKey());
            JSInteraction.getInstance().getBalance(Constant.getAccount(), new JSInteraction.JSCallBack() {
                @Override public void onSuccess(String... stringArray) {
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            dialog.dismiss();
                            tvBalance.setText(String.valueOf(stringArray[0]));
                        }
                    });
                }

                @Override public void onProgress() {
                    dialog = ProgressDialog.show(MyHomeDetailActivity.this, "waiting...", "loading...");
                }

                @Override public void onError(String error) {

                }
            });

            TextView tvMyResumeTitle = $(R.id.tv_my_resume_title);
            tvMyResumeTitle.setText(String.format(getResources().getString(R.string.my_resume_title),
                    ChainApplication.getInstance().getBaseInfo().getDocs().get(0).getExtra().getJobType()));

            $(R.id.rl_transaction_history).setOnClickListener(this);
            $(R.id.rl_my_resume).setOnClickListener(this);
            $(R.id.tv_transfer_accounts).setOnClickListener(this);
        } else {
            $(R.id.cl_my_home_detail_setting).setVisibility(View.VISIBLE);
        }

        dataEntities.clear();
        for (int i = 0; i < 10; i++) {
            ResumeRequestEntity entity = new ResumeRequestEntity();
            entity.setAccount("test" + i);
            entity.set_id("hello" + i);
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
            case R.id.rl_my_resume:

                Bundle bundle = new Bundle();
                bundle.putInt("type", 3);
                startActivity(ResumeDetailsActivity.class, bundle);
                break;
            case R.id.tv_change_phone_number:
                new CommonPopupWindow(MyHomeDetailActivity.this).pupupWindowChangePhoneNumber();
                break;
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = ChainApplication.getRefWatcher(this);
        refWatcher.watch(this);
        JSInteraction.getInstance().removeListener();
    }
}
