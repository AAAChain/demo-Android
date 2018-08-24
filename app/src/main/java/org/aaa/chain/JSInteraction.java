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
    private static JSInteraction instance;

    public static JSInteraction getInstance() {
        if (instance == null) {
            instance = new JSInteraction();
        }
        return instance;
    }

    @SuppressLint("SetJavaScriptEnabled") public void initWebKit(Context context) {
        webView = new WebView(context);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //getBalance("0x5f26bd03d606bb433aa034cadee91fdc913ee391");
            }
        });

        webView.loadUrl("file:///android_asset/index.html");

        webView.addJavascriptInterface(new JsInteraction(), "aaa");
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

    public void getBalance() {
        //webView.loadUrl("javascript:getBalance(" + "\"" + address + "\"" + ")");
        webView.loadUrl("javascript:getBalance()");
    }

    //0x62cfe930f03b6ebe751d8531d087cda9df15888aa65e11eccff3fda93889de3e
    public void getTransaction(String txHash) {
        webView.loadUrl("javascript:getTransaction(" + txHash + ")");
    }

    public class JsInteraction {

        @JavascriptInterface public void getCoinbase(String error, String address) {
            Log.i("info", "coinbase:" + address);
            Bundle bundle = new Bundle();
            bundle.putString("index", "coin");
            bundle.putString("error", error);
            bundle.putString("result", address);
        }

        @JavascriptInterface public void getAccounts(String error, List<String> accounts) {
            Log.i("info", "account:" + accounts.size());
            Bundle bundle = new Bundle();
            bundle.putString("index", "account");
            bundle.putString("error", error);
            bundle.putStringArrayList("results", (ArrayList<String>) accounts);
        }

        @JavascriptInterface public void getBalance1(String error, String balance) {
            Log.i("info", "android balance:" + balance);
            Bundle bundle = new Bundle();
            bundle.putString("index", "balance");
            bundle.putString("error", error);
            bundle.putString("result", balance);
        }

        @JavascriptInterface public void getTransaction(String error, String transaction) {
            Log.i("info", "transaction:" + transaction);
            Bundle bundle = new Bundle();
            bundle.putString("index", "transaction");
            bundle.putString("error", error);
            bundle.putString("result", transaction);
        }

        @JavascriptInterface public void getEosBalance(String[] balance) {
            Log.i("info", "balance:" + balance[0]);
            ChainApplication.getInstance().setBalance(balance[0]);
        }
    }
}
