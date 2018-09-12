package org.aaa.chain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
        webView = new WebView(context.getApplicationContext());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        webView.loadUrl("file:///android_asset/index.html");

        webView.addJavascriptInterface(new JsInteraction(), "aaa");
    }

    public void getBalance(String account, JSCallBack callBack) {
        this.listener = callBack;
        listener.onProgress();
        String content = "javascript:getBalance(" + "'" + account + "'" + ")";
        webView.loadUrl(content);
    }

    public void getSignature(String json, String privateKey, JSCallBack callback) {
        this.listener = callback;
        listener.onProgress();
        String content = "javascript:getSignature(" + "'" + json + "'" + "," + "'" + privateKey + "'" + ")";
        webView.loadUrl(content);
    }

    public void prepay(long id, String buyer, String seller, String price, JSCallBack callback) {
        this.listener = callback;
        listener.onProgress();
        String content = "javascript:prepay(" + "'" + id + "'" + "," + "'" + buyer + "'" + "," + "'" + seller + "'" + "," + "'" + price + "'" + ")";
        webView.loadUrl(content);
    }

    public void confirmOrder(String account, long id, JSCallBack callback) {
        this.listener = callback;
        listener.onProgress();
        String content = "javascript:confirmOrder(" + "'" + account + "'" + "," + "'" + id + "'" + ")";
        webView.loadUrl(content);
    }

    public void checkPayStatus(int id, JSCallBack callBack) {
        this.listener = callBack;
        String content = "javascript:getPrepayStatus(" + id + ")";
        webView.loadUrl(content);
    }

    public class JsInteraction {

        @JavascriptInterface public void getEosBalance(String error, String[] balance) {
            Log.i("info", "balance:" + balance[0]);
            listener.onSuccess(balance[0]);
        }

        @JavascriptInterface public void getSignature(String signature) {
            Log.i("info", "signature:" + signature);
            listener.onSuccess(signature);
        }

        @JavascriptInterface public void prepay(String value) {
            Log.i("info", "prepay:" + value);
            listener.onSuccess(value);
        }

        @JavascriptInterface public void prepayError(String error) {
            Log.i("info", "prepay error:" + error);
            listener.onError(error);
        }

        @JavascriptInterface public void paySuccess(String value) {
            Log.i("info", "paySuccess:" + value);
            listener.onSuccess(value);
        }

        @JavascriptInterface public void payFailure(String error) {
            Log.i("info", "payFailure:" + error);
            listener.onError(error);
        }
    }

    private JSCallBack listener;

    public interface JSCallBack {
        void onSuccess(String content);

        void onProgress();

        void onError(String error);
    }

    public void removeListener() {
        listener = null;
    }
}
