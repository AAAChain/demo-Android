package org.aaa.chain.activity;

import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.aaa.chain.JSInteraction;
import org.aaa.chain.R;

public class MemoryFragment extends BaseFragment {

    private TextView tvMemoryUsed;
    private TextView tvMemoryAvailable;
    private TextView tvAllUsed;
    private TextView tvAllAvailable;
    private ContentLoadingProgressBar pbMemory;
    private ContentLoadingProgressBar pbAll;
    private Button btnBuy;
    private Button btnSell;
    private TextView tvBuyMemoryLabel;
    private TextView tvCurrentPrice;
    private EditText etBuyMemory;
    private EditText etRecAccount;

    private TextView tvBalance;
    private Button btnConfirmBuy;

    private int type = 0;

    @Override public int initLayout() {
        return R.layout.fragment_memory;
    }

    @Override public void getViewById() {

        tvMemoryUsed = $(R.id.tv_memory_used);
        tvMemoryAvailable = $(R.id.tv_memory_available);
        tvAllUsed = $(R.id.tv_all_used);
        tvAllAvailable = $(R.id.tv_all_available);
        pbMemory = $(R.id.pb_memory_resource);
        pbAll = $(R.id.pb_all_memory);
        btnBuy = $(R.id.btn_buy);
        btnSell = $(R.id.btn_sell);
        tvBuyMemoryLabel = $(R.id.tv_buy_memory_label);
        tvCurrentPrice = $(R.id.tv_current_price_num);
        etBuyMemory = $(R.id.et_buy_memory);
        etRecAccount = $(R.id.et_receive_account);
        btnConfirmBuy = $(R.id.btn_confirm_buy);

        tvBalance = $(R.id.tv_balance_show);
        tvBalance.setText(String.format(getResources().getString(R.string.balance), activity.balance));

        btnBuy.setOnClickListener(this);
        btnSell.setOnClickListener(this);
        btnConfirmBuy.setOnClickListener(this);

        tvMemoryUsed.setText(String.format(getActivity().getResources().getString(R.string.used), activity.ramUsage) + "kb");
        tvMemoryAvailable.setText(String.format(getActivity().getResources().getString(R.string.available), activity.ramAvailable) + "kb");
        pbMemory.setProgress(Double.valueOf(activity.ramUsage).intValue() / Double.valueOf(activity.ramQuota).intValue());
    }

    @Override public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_buy:
                btnBuy.setTextColor(getResources().getColor(R.color.white_color));
                btnBuy.setBackgroundResource(R.drawable.shape_blue_bg);
                btnSell.setTextColor(getResources().getColor(R.color.text_color));
                btnSell.setBackgroundResource(R.drawable.shape_white_bg);
                btnConfirmBuy.setText(getResources().getString(R.string.buy));
                tvBuyMemoryLabel.setText(getResources().getString(R.string.buy_memory));
                type = 0;
                break;
            case R.id.btn_sell:
                btnBuy.setTextColor(getResources().getColor(R.color.text_color));
                btnBuy.setBackgroundResource(R.drawable.shape_white_bg);
                btnSell.setTextColor(getResources().getColor(R.color.white_color));
                btnSell.setBackgroundResource(R.drawable.shape_blue_bg);
                btnConfirmBuy.setText(getResources().getString(R.string.sell));
                tvBuyMemoryLabel.setText(getResources().getString(R.string.sell_memory));
                type = 1;
                break;

            case R.id.btn_confirm_buy:

                String memory = etBuyMemory.getText().toString();
                String account = etRecAccount.getText().toString();

                if (TextUtils.isEmpty(memory) || TextUtils.isEmpty(account)) {
                    Toast.makeText(getActivity(), "not null", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (type == 0) {

                } else {
                    JSInteraction.getInstance().sellram(account, Integer.valueOf(memory), new JSInteraction.JSCallBack() {
                        @Override public void onSuccess(String... stringArray) {

                        }

                        @Override public void onProgress() {

                        }

                        @Override public void onError(String error) {

                        }
                    });
                }

                break;
        }
    }
}
