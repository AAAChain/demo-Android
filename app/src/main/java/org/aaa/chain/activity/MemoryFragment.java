package org.aaa.chain.activity;

import android.app.ProgressDialog;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

public class MemoryFragment extends BaseFragment implements ResourceManagementActivity.ResourceListener {

    private TextView tvMemoryUsed;
    private TextView tvMemoryAvailable;
    private ContentLoadingProgressBar pbMemory;
    private Button btnBuy;
    private Button btnSell;
    private TextView tvBuyMemoryLabel;
    private TextView tvCurrentPrice;
    private EditText etBuyMemory;
    private EditText etRecAccount;

    private TextView tvBalance;
    private Button btnConfirmBuy;

    private int type = 0;

    private ProgressDialog dialog;

    @Override public int initLayout() {
        return R.layout.fragment_memory;
    }

    @Override public void getViewById() {

        tvMemoryUsed = $(R.id.tv_memory_used);
        tvMemoryAvailable = $(R.id.tv_memory_available);
        pbMemory = $(R.id.pb_memory_resource);
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

        activity.setResourceListener1(this);

        JSInteraction.getInstance().getRamPrice(new JSInteraction.JSCallBack() {
            @Override public void onSuccess(String... stringArray) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override public void run() {

                        tvCurrentPrice.setText(String.format(getResources().getString(R.string.current_price), stringArray[0]));
                    }
                });
            }

            @Override public void onProgress() {

            }

            @Override public void onError(String error) {

            }
        });

        tvMemoryUsed.setText(String.format(getActivity().getResources().getString(R.string.used), activity.ramUsage) + "kb");
        tvMemoryAvailable.setText(String.format(getActivity().getResources().getString(R.string.available), activity.ramAvailable) + "kb");
        pbMemory.setProgress(Double.valueOf(activity.ramUsage).intValue() / Double.valueOf(activity.ramQuota).intValue());

        etRecAccount.setText(Constant.getCurrentAccount());
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

                String currentPrice = tvCurrentPrice.getText().toString();

                if (TextUtils.isEmpty(memory) || TextUtils.isEmpty(account)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.not_null), Toast.LENGTH_SHORT).show();
                    return;
                }
                double bytenum =
                        Double.valueOf(memory) / Double.valueOf(currentPrice.substring(currentPrice.indexOf(" "), currentPrice.lastIndexOf(" ")));
                dialog = ProgressDialog.show(getActivity(), getResources().getString(R.string.waiting), getResources().getString(R.string.loading));
                if (type == 0) {

                    JSInteraction.getInstance().buyram(Constant.getCurrentAccount(), account, Math.round(bytenum), new JSInteraction.JSCallBack() {
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
                            Toast.makeText(getActivity(), getResources().getString(R.string.buy_failure), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                } else {
                    JSInteraction.getInstance().sellram(account, Math.round(bytenum), new JSInteraction.JSCallBack() {
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
                            Toast.makeText(getActivity(), getResources().getString(R.string.sell_failure), Toast.LENGTH_SHORT).show();
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

    @Override public void getResource(String resource) {
        DecimalFormat decimalFormat = new DecimalFormat("##0.0#");
        try {
            JSONObject jsonObject = new JSONObject(resource);
            double rq, ru;
            rq = jsonObject.getDouble("ram_quota");
            ru = jsonObject.getDouble("ram_usage");
            String ramAvailable = decimalFormat.format((rq - ru) / 1024);
            String ramQuota = decimalFormat.format(rq / 1024);
            String ramUsage = decimalFormat.format(ru / 1024);

            String balance = jsonObject.getString("core_liquid_balance");

            activity.balance = balance;

            activity.runOnUiThread(new Runnable() {
                @Override public void run() {
                    tvBalance.setText(String.format(getResources().getString(R.string.balance), balance));
                    tvMemoryUsed.setText(String.format(getActivity().getResources().getString(R.string.used), ramUsage) + "kb");
                    tvMemoryAvailable.setText(String.format(getActivity().getResources().getString(R.string.available), ramAvailable) + "kb");
                    pbMemory.setProgress(Double.valueOf(ramUsage).intValue() / Double.valueOf(ramQuota).intValue());

                    if (type == 0) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.buy_success), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.sell_success), Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
