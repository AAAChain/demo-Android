package org.aaa.chain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("JavascriptInterface") public class JSInteraction {

    private WebView webView;
    private Context context;

    public JSInteraction(Context context) {
        this.context = context;
        loadWebKit(context);
    }

    @SuppressLint("SetJavaScriptEnabled") private void loadWebKit(Context context) {
        webView = new WebView(context);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                getBalance("0x5f26bd03d606bb433aa034cadee91fdc913ee391");
            }
        });

        webView.loadUrl("file:///android_asset/index.html");

        webView.addJavascriptInterface(new JsInteraction(), "aaa");

        //findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
        //    @Override public void onClick(View v) {
        //        webView.loadUrl("javascript:testok()");
        //    }
        //});
        //findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
        //    @Override public void onClick(View v) {
        //        webView.loadUrl("javascript:add(1)");
        //new Thread(new Runnable() {
        //    @Override public void run() {
        //
        //multihash = IpfsUtils.getInstance().addByte("test", "hello my world!".getBytes());
        //File file = new File(Environment.getExternalStorageDirectory().getPath() + "/mediacore_sdk.txt");
        //multihash = IpfsUtils.getInstance().addFile(file);

        //runOnUiThread(new Runnable() {
        //    @Override public void run() {
        //        textView.setText(multihash.toBase58());
        //    }
        //});
        //}
        //}).start();
        //}
        //});
        //findViewById(R.id.add2).setOnClickListener(new View.OnClickListener() {
        //    @Override public void onClick(View v) {
        //        new Thread(new Runnable() {
        //            @Override public void run() {
        //                if (multihash != null) {
        //                    String content = IpfsUtils.getInstance().getFile(multihash);
        //                    runOnUiThread(new Runnable() {
        //                        @Override public void run() {
        //                            textView.setText(content);
        //                        }
        //                    });
        //                }
        //            }
        //        }).start();
        //    }
        //});
    }

    public void createAccount(String password) {
        webView.loadUrl("javascript:createAccount(" + password + ")");
    }

    public void getCoinbase() {
        webView.loadUrl("javascript:getCoinbase()");
    }

    public void getAccounts() {
        webView.loadUrl("javascript:getAccounts()");
    }

    public void getBalance(String address) {
        webView.loadUrl("javascript:getBalance1(" + "\"" + address + "\"" + ")");
    }

    public static void main(String[] args) {
        String a = "0x5f26bd03d606bb433aa034cadee91fdc913ee391";
        String b = "javascript:getBalance1(" + "\"" + a + "\"" + ")";
        System.out.print(b);
    }

    //0x62cfe930f03b6ebe751d8531d087cda9df15888aa65e11eccff3fda93889de3e
    public void getTransaction(String txHash) {
        webView.loadUrl("javascript:getTransaction(" + txHash + ")");
    }

    private void sendBroadcast(Bundle bundle) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("org.aaa.chain.action").putExtras(bundle));
    }

    public class JsInteraction {
        //@JavascriptInterface public void testok(String content) {
        //    if (content == null) {
        //        textView.setText("null");
        //    } else {
        //        textView.setText(content);
        //    }
        //    Log.i("info", "content:" + content);
        //}
        //
        //@JavascriptInterface public void displayIpfs(String hash, String content) {
        //    Log.i("info", "hash:" + hash + " content:" + content);
        //}

        @JavascriptInterface public void getCoinbase(String error, String address) {
            Log.i("info", "coinbase:" + address);
            Bundle bundle = new Bundle();
            bundle.putString("index", "coin");
            bundle.putString("error", error);
            bundle.putString("result", address);
            sendBroadcast(bundle);
        }

        @JavascriptInterface public void getAccounts(String error, List<String> accounts) {
            Log.i("info", "account:" + accounts.size());
            Bundle bundle = new Bundle();
            bundle.putString("index", "account");
            bundle.putString("error", error);
            bundle.putStringArrayList("results", (ArrayList<String>) accounts);
            sendBroadcast(bundle);
        }

        @JavascriptInterface public void getBalance1(String error, String balance) {
            Log.i("info", "android balance:" + balance);
            Bundle bundle = new Bundle();
            bundle.putString("index", "balance");
            bundle.putString("error", error);
            bundle.putString("result", balance);
            sendBroadcast(bundle);
        }

        @JavascriptInterface public void getTransaction(String error, String transaction) {
            Log.i("info", "transaction:" + transaction);
            Bundle bundle = new Bundle();
            bundle.putString("index", "transaction");
            bundle.putString("error", error);
            bundle.putString("result", transaction);
            sendBroadcast(bundle);
        }
    }
}
