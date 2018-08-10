package org.aaa.chain.activity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import io.ipfs.multihash.Multihash;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import org.aaa.chain.AAAWalletUtils;
import org.aaa.chain.JSInteraction;
import org.aaa.chain.R;
import org.web3j.crypto.CipherException;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.utils.Numeric;

public class JSActivity extends BaseActivity {

    private Multihash multihash;
    private JSInteraction jsInteraction;
    private TextView tvHash;
    private TextView tvTime;
    private TextView tvBalance;
    private TextView tvEthNum;
    private TextView tvBlockNum;
    private TextView tvGasPrice;
    private TextView tvBlockTotalNum;
    private TextView tvFrom;
    private TextView tvTo;
    private Button transfer;

    @Override public int initLayout() {
        return R.layout.activity_js;
    }

    @Override public void getViewById() {
        tvHash = $(R.id.tv_hash);
        tvTime = $(R.id.tv_time);
        tvBalance = $(R.id.tv_balance);
        tvEthNum = $(R.id.ethNum);
        tvGasPrice = $(R.id.tv_gasPrice);
        tvBlockNum = $(R.id.tv_number);
        tvBlockTotalNum = $(R.id.tv_total_number);
        tvFrom = $(R.id.tv_from);
        tvTo = $(R.id.tv_to);
        transfer = $(R.id.btn_transfer_accounts);
        transfer.setOnClickListener(this);
        initWeb3j();

        try {
            AAAWalletUtils.createWallet("123456");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //java调用
    private void initWeb3j() {

        new Thread(new Runnable() {
            @Override public void run() {
                EthTransaction transaction;
                try {
                    //transaction = AAAWalletUtils.getTransactionByHash("0xa71243e362b5e5213f8544fc3188d06808c1e30b6cfc9f77325b25f87ac40c92");
                    transaction = AAAWalletUtils.getTransactionByHash("0x62cfe930f03b6ebe751d8531d087cda9df15888aa65e11eccff3fda93889de3e");
                    EthBlock block = AAAWalletUtils.getBlockByHash(transaction.getTransaction().getBlockHash(), true);
                    BigDecimal balance = AAAWalletUtils.getAccountBalance(transaction.getTransaction().getFrom());
                    BigInteger totalNum = AAAWalletUtils.getBlockTotalNum().getBlockNumber();

                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            tvBalance.setText(balance.toString() + " ETHER");
                            tvBlockNum.setText(transaction.getTransaction().getBlockNumber().toString());

                            tvBlockTotalNum.setText(totalNum.toString());

                            tvFrom.setText(transaction.getTransaction().getFrom());
                            tvTo.setText(transaction.getTransaction().getTo());

                            BigInteger total = transaction.getTransaction().getGas().multiply(transaction.getTransaction().getGasPrice());

                            tvGasPrice.setText(AAAWalletUtils.fromWei(total.toString()) + " ETHER");
                            tvEthNum.setText(AAAWalletUtils.fromWei(transaction.getTransaction().getValue().toString()).toString() + " ETHER");
                            tvHash.setText(transaction.getTransaction().getHash());
                            tvTime.setText(AAAWalletUtils.timestampToDate(block.getBlock().getTimestamp().longValue()));
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //js调用
    private void initWeb3js() {
        //jsInteraction = new JSInteraction(this);
        //jsInteraction.getBalance("0x5f26bd03d606bb433aa034cadee91fdc913ee391");
        //jsInteraction.getTransaction("0x62cfe930f03b6ebe751d8531d087cda9df15888aa65e11eccff3fda93889de3e");
        //LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("org.aaa.chain.action"));
    }

    @Override public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_transfer_accounts:
                startActivity(TransferActivity.class, null);
                break;
        }
    }

    @Override protected void onPause() {
        super.onPause();
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    //BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    //    @Override public void onReceive(Context context, Intent intent) {
    //
    //        Bundle bundle = intent.getExtras();
    //        String index = intent.getStringExtra("index");
    //        String error = bundle.getString("error");
    //        String result = bundle.getString("result");
    //        List<String> results = bundle.getStringArrayList("results");
    //        if ("balance".equals(index)) {
    //            tvBalance.setText(result);
    //        } else if ("transaction".equals(index)) {
    //
    //        }
    //    }
    //};
}
