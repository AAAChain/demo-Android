package org.aaa.chain.activity;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import org.aaa.chain.AAAWalletUtils;
import org.aaa.chain.R;
import org.web3j.crypto.CipherException;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

public class TransferActivity extends BaseActivity {

    private EditText beneficiaryAddress;
    private EditText coinNumber;
    private TextView balance;
    private TextView handlingFee;

    @Override public int initLayout() {
        return R.layout.activity_transfer;
    }

    @Override public void getViewById() {

        beneficiaryAddress = $(R.id.et_beneficiary_address);
        coinNumber = $(R.id.et_coin_number);
        handlingFee = $(R.id.tv_handling_fee);

        $(R.id.btn_transfer_accounts).setOnClickListener(this);
        AAAWalletUtils.getWallet();
        initWeb3j();
    }

    public static void main(String[] args) {
        try {
            BigInteger nonce = AAAWalletUtils.getNonce("0x5f26bd03d606bb433aa034cadee91fdc913ee391");
            System.out.println(nonce);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initWeb3j() {

        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    EthSendTransaction transaction =
                            AAAWalletUtils.transferBalance("0x5f26bd03d606bb433aa034cadee91fdc913ee391", "12345678", "1", AAAWalletUtils.getWallet(),
                                    AAAWalletUtils.getAddress());

                    Log.i("info", "transaction:" + transaction);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (CipherException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_transfer_accounts:

                break;
        }
    }
}
