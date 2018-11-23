package org.aaa.chain.activity;

import android.app.ProgressDialog;
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
import org.json.JSONException;
import org.json.JSONObject;

public class CPU_NETFragment extends BaseFragment implements ResourceManagementActivity.ResourceListener {

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

    private ProgressDialog dialog;

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

        activity.setResourceListener(this);

        tvNetResourceAvailable.setText(String.format(getActivity().getResources().getString(R.string.available), activity.netAvailable) + "kb");
        tvNetResourceUsed.setText(String.format(getActivity().getResources().getString(R.string.used), activity.netUsed) + "kb");
        pbNetResource.setProgress(Double.valueOf(activity.netUsed).intValue() / Double.valueOf(activity.netMax).intValue());

        tvCalResourceAvailable.setText(String.format(getResources().getString(R.string.available), activity.cpuAvailable) + "ms");
        tvCalResourceUsed.setText(String.format(getResources().getString(R.string.used), activity.cpuUsed) + "ms");
        pbCalResource.setProgress(Double.valueOf(activity.cpuUsed).intValue() / Double.valueOf(activity.cpuMax).intValue());

        if (activity.cpuAmount != null || activity.netAmount != null) {
            tvRefundAmount.setText(getAmount(activity.cpuAmount, activity.netAmount) + " AAA");
        } else {
            tvRefundAmount.setText("0 AAA");
        }

        etRecAccount.setText(Constant.getCurrentAccount());
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

                if (activity.cpuRedemption != null){
                    tvNetRedemption.setText(String.format(getResources().getString(R.string.available_redemption), activity.netRedemption));
                }else {
                    tvNetRedemption.setText(String.format(getResources().getString(R.string.available_redemption), "0.0000 AAA"));

                }
                if (activity.netRedemption != null){
                    tvCalRedemption.setText(String.format(getResources().getString(R.string.available_redemption), activity.cpuRedemption));
                }else {
                    tvCalRedemption.setText(String.format(getResources().getString(R.string.available_redemption), "0.0000 AAA"));
                }

                tvCalMortgage.setText(getResources().getString(R.string.calculation_redemption));
                tvNetMortgage.setText(getResources().getString(R.string.network_redemption));
                type = 1;
                break;

            case R.id.btn_confirm_mortgage:

                String account = etRecAccount.getText().toString();
                String calMortgage = etCalMortgage.getText().toString();
                String netMortgage = etNetMortgage.getText().toString();

                if (TextUtils.isEmpty(account) || TextUtils.isEmpty(calMortgage) || TextUtils.isEmpty(netMortgage)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.not_null), Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog = ProgressDialog.show(getActivity(), getResources().getString(R.string.waiting), getResources().getString(R.string.loading));
                if (type == 0) {
                    JSInteraction.getInstance()
                            .mortgage(Constant.getCurrentAccount(), account, getPrice(netMortgage), getPrice(calMortgage),
                                    new JSInteraction.JSCallBack() {
                                        @Override public void onSuccess(String... stringArray) {

                                            activity.runOnUiThread(new Runnable() {
                                                @Override public void run() {
                                                    activity.getAccountInfo();
                                                }
                                            });
                                        }

                                        @Override public void onProgress() {

                                        }

                                        @Override public void onError(String error) {
                                            Toast.makeText(getActivity(), getResources().getString(R.string.mortgage_failure), Toast.LENGTH_SHORT)
                                                    .show();
                                            dialog.dismiss();
                                        }
                                    });
                } else {
                    JSInteraction.getInstance()
                            .redemption(Constant.getCurrentAccount(), account, getPrice(netMortgage), getPrice(calMortgage),
                                    new JSInteraction.JSCallBack() {
                                        @Override public void onSuccess(String... stringArray) {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override public void run() {
                                                    activity.getAccountInfo();
                                                }
                                            });
                                        }

                                        @Override public void onProgress() {

                                        }

                                        @Override public void onError(String error) {
                                            Toast.makeText(getActivity(), getResources().getString(R.string.redemption_failure), Toast.LENGTH_SHORT)
                                                    .show();
                                            dialog.dismiss();
                                        }
                                    });
                }
                break;
        }
    }

    public void updateView() {
        tvBalance.setText(String.format(getResources().getString(R.string.balance), activity.balance));
    }

    String cpuAmount;
    String netAmount;

    @Override public void getResource(String resource) {
        DecimalFormat decimalFormat = new DecimalFormat("##0.0#");
        try {
            JSONObject jsonObject = new JSONObject(resource);
            JSONObject cpuLimit = new JSONObject(jsonObject.getString("cpu_limit"));
            String cpuAvailable = decimalFormat.format(cpuLimit.getDouble("available") / 1000);
            String cpuUsed = decimalFormat.format(cpuLimit.getLong("used") / 1000);
            String cpuMax = decimalFormat.format(cpuLimit.getLong("max") / 1000);

            JSONObject netLimit = new JSONObject(jsonObject.getString("net_limit"));
            String netAvailable = decimalFormat.format(netLimit.getDouble("available") / 1024);
            String netUsed = decimalFormat.format(netLimit.getDouble("used") / 1024);
            String netMax = decimalFormat.format(netLimit.getDouble("max") / 1024);

            JSONObject redemption = new JSONObject(jsonObject.getString("self_delegated_bandwidth"));
            String netRedemption = redemption.getString("net_weight");
            String cpuRedemption = redemption.getString("cpu_weight");

            String balance = jsonObject.getString("core_liquid_balance");

            String refundRequest = jsonObject.getString("refund_request");
            if (refundRequest != null && !refundRequest.equals("null")) {
                JSONObject refund = new JSONObject(refundRequest);
                cpuAmount = refund.getString("cpu_amount");
                netAmount = refund.getString("net_amount");
            }

            activity.balance = balance;

            activity.runOnUiThread(new Runnable() {
                @Override public void run() {
                    tvNetResourceAvailable.setText(String.format(getActivity().getResources().getString(R.string.available), netAvailable) + "kb");
                    tvNetResourceUsed.setText(String.format(getActivity().getResources().getString(R.string.used), netUsed) + "kb");
                    pbNetResource.setProgress(Double.valueOf(netUsed).intValue() / Double.valueOf(netMax).intValue());

                    tvCalResourceAvailable.setText(String.format(getResources().getString(R.string.available), cpuAvailable) + "ms");
                    tvCalResourceUsed.setText(String.format(getResources().getString(R.string.used), cpuUsed) + "ms");
                    pbCalResource.setProgress(Double.valueOf(cpuUsed).intValue() / Double.valueOf(cpuMax).intValue());

                    if (cpuAmount != null || netAmount != null) {
                        tvRefundAmount.setText(getAmount(cpuAmount, netAmount) + " AAA");
                    } else {
                        tvRefundAmount.setText("0 AAA");
                    }

                    tvNetRedemption.setText(netRedemption);
                    tvCalRedemption.setText(cpuRedemption);
                    tvBalance.setText(String.format(getResources().getString(R.string.balance), balance));

                    if (type == 0) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.mortgage_success), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.redemption_success), Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
