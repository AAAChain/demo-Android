package org.aaa.chain.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DecimalFormat;
import java.util.List;
import org.aaa.chain.Constant;
import org.aaa.chain.JSInteraction;
import org.aaa.chain.R;
import org.aaa.chain.adapter.BaseViewHolder;
import org.aaa.chain.adapter.BindViewHolderInterface;
import org.aaa.chain.adapter.RecyclerViewAdapter;
import org.aaa.chain.entities.ResumeRequestEntity;

public class CommonPopupWindow implements View.OnClickListener {

    private Context context;
    private View layoutView;
    private PopupWindow popupWindow;
    private Window window;
    private WindowManager.LayoutParams layoutParams;

    private View parent;

    private EditText etTransferAmount;
    private EditText etWalletAddress;
    private EditText etAccountPassword;
    private EditText etPhoneNumber;
    private EditText etVerificationCode;

    public CommonPopupWindow(Context context) {
        if (popupWindow == null) {
            popupWindow = new PopupWindow(context);
        }
        this.context = context;

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override public void onDismiss() {
                dimOrRecoverBehind(false);
                JSInteraction.getInstance().removeListener();
            }
        });
    }

    private void initPopupWindow(int layoutId, int height, int location) {
        parent = ((ViewGroup) ((AppCompatActivity) context).findViewById(android.R.id.content)).getChildAt(0);
        layoutView = LayoutInflater.from(context).inflate(layoutId, null);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setContentView(layoutView);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(height);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupWindow.setAnimationStyle(R.style.animTranslate);
        popupWindow.showAtLocation(parent, location, 0, 0);
    }

    public void pupupWindowTransactionHistory(List<ResumeRequestEntity> dataEntities) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight = metrics.heightPixels;

        initPopupWindow(R.layout.recyclerview, (int) (screenHeight * 0.7), Gravity.BOTTOM);

        RecyclerView recyclerView = layoutView.findViewById(R.id.recycleView);
        layoutView.findViewById(R.id.ll_transaction_history_label).setVisibility(View.VISIBLE);

        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        RecyclerViewAdapter adapter = new RecyclerViewAdapter<>(context, dataEntities, R.layout.transaction_history_item,
                new BindViewHolderInterface<ResumeRequestEntity>() {

                    @Override public void bindViewHolder(BaseViewHolder holder, ResumeRequestEntity dataEntity) {
                        ((TextView) holder.getView(R.id.tv_transaction_history_content)).setText(dataEntity.getAccount());
                        ((TextView) holder.getView(R.id.tv_transaction_history_type)).setText(dataEntity.get_id());
                        ((TextView) holder.getView(R.id.tv_transaction_history_date)).setText(dataEntity.getAccount());
                        ((TextView) holder.getView(R.id.tv_transaction_history_money)).setText(dataEntity.get_id());
                    }
                });

        recyclerView.setAdapter(adapter);

        dimOrRecoverBehind(true);
    }

    public void pupupWindowTransferAccounts(String balance) {
        initPopupWindow(R.layout.popup_transfer_accounts, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        etTransferAmount = layoutView.findViewById(R.id.et_transfer_amount);
        etWalletAddress = layoutView.findViewById(R.id.et_wallet_address);
        etAccountPassword = layoutView.findViewById(R.id.et_account_password);
        TextView tvBalance = layoutView.findViewById(R.id.tv_available_balance);
        tvBalance.setText(balance);
        layoutView.findViewById(R.id.btn_transfer_accounts).setOnClickListener(this);
        layoutView.findViewById(R.id.tv_close).setOnClickListener(this);

        dimOrRecoverBehind(true);
    }

    public void pupupWindowChangePhoneNumber() {
        initPopupWindow(R.layout.popup_change_phone_number, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        etPhoneNumber = layoutView.findViewById(R.id.et_phone_num);
        etVerificationCode = layoutView.findViewById(R.id.et_verification_code);
        layoutView.findViewById(R.id.btn_send_code).setOnClickListener(this);
        layoutView.findViewById(R.id.btn_commit).setOnClickListener(this);
        layoutView.findViewById(R.id.tv_close).setOnClickListener(this);

        dimOrRecoverBehind(true);
    }

    //背景变暗
    private void dimOrRecoverBehind(boolean isDim) {
        if (window == null) {
            window = ((AppCompatActivity) context).getWindow();
        }
        if (layoutParams == null) {
            layoutParams = window.getAttributes();
        }
        if (isDim) {
            layoutParams.alpha = 0.3f;
        } else {
            layoutParams.alpha = 1.0f;
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setAttributes(layoutParams);
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_transfer_accounts:
                if (TextUtils.isEmpty(etTransferAmount.getText().toString())) {
                    Toast.makeText(context, "please input amount", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(etWalletAddress.getText().toString())) {
                    Toast.makeText(context, "please input account", Toast.LENGTH_SHORT).show();
                    return;
                } else if ((TextUtils.isEmpty(etAccountPassword.getText().toString()))) {
                    Toast.makeText(context, "please input password", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!etAccountPassword.getText().toString().equals(Constant.getPassword())) {
                    Toast.makeText(context, "password is wrong", Toast.LENGTH_SHORT).show();
                    return;
                }

                ProgressDialog dialog = ProgressDialog.show(context, "waiting...", "transfer...");
                DecimalFormat decimalFormat = new DecimalFormat("##0.0000");
                String dd = decimalFormat.format(Float.valueOf(etTransferAmount.getText().toString()));
                JSInteraction.getInstance()
                        .transfer(Constant.getCurrentAccount(), etWalletAddress.getText().toString(), dd + " AAA", "",
                                new JSInteraction.JSCallBack() {
                                    @Override public void onSuccess(String... stringArray) {

                                        Toast.makeText(context, "transfer successful", Toast.LENGTH_SHORT).show();
                                        ((Activity) context).runOnUiThread(new Runnable() {
                                            @Override public void run() {
                                                dialog.dismiss();
                                                popupWindow.dismiss();
                                                listener.transferSuccess();
                                            }
                                        });
                                    }

                                    @Override public void onProgress() {

                                    }

                                    @Override public void onError(String error) {
                                        Toast.makeText(context, "transfer error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                break;
            case R.id.tv_close:
                popupWindow.dismiss();
                break;

            case R.id.btn_send_code:

                break;

            case R.id.btn_commit:

                break;
        }
    }

    private TransferListener listener;

    public void setTransferListener(TransferListener listener) {
        this.listener = listener;
    }

    public interface TransferListener {
        void transferSuccess();
    }
}
