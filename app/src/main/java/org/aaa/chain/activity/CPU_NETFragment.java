package org.aaa.chain.activity;

import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DecimalFormat;
import org.aaa.chain.Constant;
import org.aaa.chain.JSInteraction;
import org.aaa.chain.R;

public class CPU_NETFragment extends BaseFragment {

    private TextView tvCalResourceUsed;
    private TextView tvCalResourceAvailable;
    private TextView tvNetResourceUsed;
    private TextView tvNetResourceAvailable;
    private ContentLoadingProgressBar pbCalResource;
    private ContentLoadingProgressBar pbNetResource;
    private TextView tvRefundAmount;
    private Button btnMortgage;
    private Button btnRedemption;
    private TextView tvCalMortgage;
    private TextView tvNetMortgage;
    private EditText etCalMortgage;
    private EditText etNetMortgage;
    private EditText etRecAccount;
    private Button btnConfirm;

    private TextView tvCalRedemption;
    private TextView tvNetRedemption;

    private TextView tvBalance;

    private int type = 0;

    @Override public int initLayout() {
        return R.layout.fragment_cpu_net;
    }

    @Override public void getViewById() {
        tvCalResourceUsed = $(R.id.tv_calculation_resource_used);
        tvCalResourceAvailable = $(R.id.tv_calculation_resource_available);
        tvNetResourceUsed = $(R.id.tv_network_resource_used);
        tvNetResourceAvailable = $(R.id.tv_network_resource_available);
        pbCalResource = $(R.id.pb_calculation_resource);
        pbNetResource = $(R.id.pb_network_resource);
        tvRefundAmount = $(R.id.tv_refund_amount);
        btnMortgage = $(R.id.btn_mortgage);
        btnRedemption = $(R.id.btn_redemption);
        tvCalMortgage = $(R.id.tv_calculation_mortgage_label);
        tvNetMortgage = $(R.id.tv_network_mortgage_label);
        etCalMortgage = $(R.id.et_calculation_mortgage);
        etNetMortgage = $(R.id.et_network_mortgage);
        etRecAccount = $(R.id.et_receive_account);
        btnConfirm = $(R.id.btn_confirm_mortgage);

        tvCalRedemption = $(R.id.tv_calculation_redemption_num);
        tvNetRedemption = $(R.id.tv_network_redemption_num);

        tvBalance = $(R.id.tv_balance_show);
        tvBalance.setText(String.format(getResources().getString(R.string.balance), activity.balance));

        btnMortgage.setOnClickListener(this);
        btnRedemption.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);

        tvNetResourceAvailable.setText(String.format(getActivity().getResources().getString(R.string.available), activity.netAvailable) + "kb");
        tvNetResourceUsed.setText(String.format(getActivity().getResources().getString(R.string.used), activity.netUsed) + "kb");
        pbNetResource.setProgress(Double.valueOf(activity.netUsed).intValue() / Double.valueOf(activity.netMax).intValue());

        tvCalResourceAvailable.setText(String.format(getResources().getString(R.string.available), activity.cpuAvailable) + "kb");
        tvCalResourceUsed.setText(String.format(getResources().getString(R.string.used), activity.cpuUsed) + "kb");
        pbCalResource.setProgress(Double.valueOf(activity.cpuUsed).intValue() / Double.valueOf(activity.cpuMax).intValue());

        if (activity.cpuAmount != null || activity.netAmount != null) {
            tvRefundAmount.setText(getAmount(activity.cpuAmount, activity.netAmount));
        } else {
            tvRefundAmount.setText("0 AAA");
        }
    }

    private String getPrice(String price) {
        DecimalFormat decimalFormat = new DecimalFormat("##0.0000");
        String dd = decimalFormat.format(Float.valueOf(price));
        return dd + " AAA";
    }

    private String getAmount(String... amount) {
        double a1 = Double.valueOf(amount[0].substring(0, amount[0].lastIndexOf(" ")));
        double a2 = Double.valueOf(amount[1].substring(0, amount[1].lastIndexOf(" ")));
        return String.valueOf(a1 + a2);
    }

    @Override public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_mortgage:
                btnConfirm.setText(getResources().getString(R.string.mortgage));
                btnMortgage.setTextColor(getResources().getColor(R.color.white_color));
                btnMortgage.setBackgroundResource(R.drawable.shape_blue_bg);
                btnRedemption.setTextColor(getResources().getColor(R.color.text_color));
                btnRedemption.setBackgroundResource(R.drawable.shape_white_bg);
                tvCalRedemption.setVisibility(View.GONE);
                tvNetRedemption.setVisibility(View.GONE);

                tvCalMortgage.setText(getResources().getString(R.string.calculation_mortgage));
                tvNetMortgage.setText(getResources().getString(R.string.network_mortgage));
                type = 0;
                break;

            case R.id.btn_redemption:
                btnConfirm.setText(getResources().getString(R.string.redemption));
                btnMortgage.setTextColor(getResources().getColor(R.color.text_color));
                btnMortgage.setBackgroundResource(R.drawable.shape_white_bg);
                btnRedemption.setTextColor(getResources().getColor(R.color.white_color));
                btnRedemption.setBackgroundResource(R.drawable.shape_blue_bg);
                tvCalRedemption.setVisibility(View.VISIBLE);
                tvNetRedemption.setVisibility(View.VISIBLE);

                tvCalMortgage.setText(getResources().getString(R.string.calculation_redemption));
                tvNetMortgage.setText(getResources().getString(R.string.network_redemption));
                type = 1;
                break;

            case R.id.btn_confirm_mortgage:

                String account = etRecAccount.getText().toString();
                String calMortgage = etCalMortgage.getText().toString();
                String netMortgage = etNetMortgage.getText().toString();

                if (TextUtils.isEmpty(account) || TextUtils.isEmpty(calMortgage) || TextUtils.isEmpty(netMortgage)) {
                    Toast.makeText(getActivity(), "not null", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (type == 0) {
                    JSInteraction.getInstance()
                            .mortgage(Constant.getAccount(), account, getPrice(netMortgage), getPrice(calMortgage), new JSInteraction.JSCallBack() {
                                @Override public void onSuccess(String... stringArray) {

                                }

                                @Override public void onProgress() {

                                }

                                @Override public void onError(String error) {

                                }
                            });
                } else {
                    JSInteraction.getInstance()
                            .redemption(Constant.getAccount(), account, getPrice(netMortgage), getPrice(calMortgage), new JSInteraction.JSCallBack() {
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
