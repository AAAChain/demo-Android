package org.aaa.chain.activity;

import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.leakcanary.RefWatcher;
import java.util.ArrayList;
import java.util.List;
import org.aaa.chain.ChainApplication;
import org.aaa.chain.Constant;
import org.aaa.chain.JSInteraction;
import org.aaa.chain.R;
import org.aaa.chain.entities.ResumeRequestEntity;
import org.aaa.chain.views.CommonPopupWindow;

public class MyHomeDetailActivity extends BaseActivity implements CommonPopupWindow.TransferListener {

    private List<ResumeRequestEntity> dataEntities = new ArrayList<>();
    private TextView tvBalance;
    ProgressDialog dialog = null;
    private CommonPopupWindow commonPopupWindow;

    @Override public int initLayout() {
        return R.layout.activity_my_home_detail;
    }

    @Override public void getViewById() {

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        String title = bundle.getString("title");
        setTitleName(title);

        commonPopupWindow = new CommonPopupWindow(this);
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (getResources().getString(R.string.myAccount).equals(title)) {
            $(R.id.cl_my_home_detail_account).setVisibility(View.VISIBLE);
            $(R.id.tv_change_phone_number).setOnClickListener(this);
            TextView account = $(R.id.tv_account_name);
            account.setText(Constant.getCurrentAccount());
            $(R.id.tv_account_copy).setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    cm.setText(Constant.getCurrentAccount());
                    Toast.makeText(MyHomeDetailActivity.this, getResources().getString(R.string.copy_success), Toast.LENGTH_LONG).show();
                }
            });
        } else if (getResources().getString(R.string.myWallet).equals(title)) {
            $(R.id.cl_my_home_detail_wallet).setVisibility(View.VISIBLE);
            tvBalance = $(R.id.tv_available_balance);
            ((TextView) $(R.id.tv_wallet_address)).setText(Constant.getCurrentAccount());
            JSInteraction.getInstance().getBalance(Constant.getCurrentAccount(), new JSInteraction.JSCallBack() {
                @Override public void onSuccess(String... stringArray) {
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            dialog.dismiss();
                            tvBalance.setText(String.valueOf(stringArray[0]));
                        }
                    });
                }

                @Override public void onProgress() {
                    dialog = ProgressDialog.show(MyHomeDetailActivity.this, getResources().getString(R.string.waiting),
                            getResources().getString(R.string.loading));
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

            TextView privateKey = $(R.id.tv_private_key);
            privateKey.setText(Constant.getCurrentPrivateKey());
            TextView tvCopyPublicKey = $(R.id.tv_copy1);
            TextView tvCopyPrivateKey = $(R.id.tv_copy2);
            tvCopyPublicKey.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    cm.setText(Constant.getCurrentPublicKey());
                    Toast.makeText(MyHomeDetailActivity.this, getResources().getString(R.string.copy_success), Toast.LENGTH_LONG).show();
                }
            });

            tvCopyPrivateKey.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    cm.setText(Constant.getCurrentPrivateKey());
                    Toast.makeText(MyHomeDetailActivity.this, getResources().getString(R.string.copy_success), Toast.LENGTH_LONG).show();
                }
            });
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
                commonPopupWindow.pupupWindowTransactionHistory(dataEntities);
                break;

            case R.id.tv_transfer_accounts:
                commonPopupWindow.pupupWindowTransferAccounts(tvBalance.getText().toString());
                commonPopupWindow.setTransferListener(this);
                break;
            case R.id.rl_my_resume:

                Bundle bundle = new Bundle();
                bundle.putInt("type", 3);
                startActivity(ResumeDetailsActivity.class, bundle);
                break;
            case R.id.tv_change_phone_number:
                commonPopupWindow.pupupWindowChangePhoneNumber();
                break;
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = ChainApplication.getRefWatcher(this);
        refWatcher.watch(this);
        JSInteraction.getInstance().removeListener();
    }

    @Override public void transferSuccess() {
        JSInteraction.getInstance().getBalance(Constant.getCurrentAccount(), new JSInteraction.JSCallBack() {
            @Override public void onSuccess(String... stringArray) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        dialog.dismiss();
                        tvBalance.setText(String.valueOf(stringArray[0]));
                    }
                });
            }

            @Override public void onProgress() {
                dialog = ProgressDialog.show(MyHomeDetailActivity.this, getResources().getString(R.string.waiting),
                        getResources().getString(R.string.loading));
            }

            @Override public void onError(String error) {

            }
        });
    }
}
