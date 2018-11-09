package org.aaa.chain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import org.json.JSONObject;

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

        webView.addJavascriptInterface(new JsInteraction(), "aaaChain");
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

    public void getPublicKey(String privateKey, JSCallBack callback) {
        this.listener = callback;
        listener.onProgress();
        String content = "javascript:getPublicKey(" + "'" + privateKey + "'" + ")";
        webView.loadUrl(content);
    }

    public void encryptKey(String privateKey, String anotherPublicKey, String key, JSCallBack callback) {
        this.listener1 = callback;
        listener1.onProgress();
        String content = "javascript:encryptKey(" + "'" + privateKey + "'" + "," + "'" + anotherPublicKey + "'" + "," + "'" + key + "'" + ")";
        webView.loadUrl(content);
    }

    public void decryptKey(String privateKey, String anotherPublicKey, String enKey, JSCallBack callback) {
        this.listener = callback;
        listener.onProgress();
        String content = "javascript:decryptKey(" + "'" + privateKey + "'" + "," + "'" + anotherPublicKey + "'" + "," + "'" + enKey + "'" + ")";
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

    public void generationSecretKey(JSCallBack callBack) {
        this.listener = callBack;
        String content = "javascript:generationSecretKey()";
        webView.loadUrl(content);
    }

    public void authPermission(String account, JSCallBack callBack) {
        this.listener = callBack;
        String content = "javascript:authPermission(" + "'" + account + "'" + ")";
        webView.loadUrl(content);
    }

    public void transfer(String from, String to, String amount, String remark, JSCallBack callBack) {
        this.listener = callBack;
        String content = "javascript:transfer(" + "'" + from + "'" + "," + "'" + to + "'" + "," + "'" + amount + "'" + "," + "'" + remark + "'" + ")";
        webView.loadUrl(content);
    }

    public void getAccountInfo(String account, JSCallBack callBack) {
        this.listener = callBack;
        String content = "javascript:getAccountInfo(" + "'" + account + "'" + ")";
        webView.loadUrl(content);
    }

    public void mortgage(String account, String receiveAccount, String netq, String cpuq, JSCallBack callBack) {
        this.listener = callBack;
        String content = "javascript:mortgage("
                + "'"
                + account
                + "'"
                + ","
                + "'"
                + receiveAccount
                + "'"
                + ","
                + "'"
                + netq
                + "'"
                + ","
                + "'"
                + cpuq
                + "'"
                + ")";
        webView.loadUrl(content);
    }

    public void redemption(String account, String receiveAccount, String netq, String cpuq, JSCallBack callBack) {
        this.listener = callBack;
        String content = "javascript:redemption("
                + "'"
                + account
                + "'"
                + ","
                + "'"
                + receiveAccount
                + "'"
                + ","
                + "'"
                + netq
                + "'"
                + ","
                + "'"
                + cpuq
                + "'"
                + ")";
        webView.loadUrl(content);
    }

    public void sellram(String account, long bytesnum, JSCallBack callback) {
        this.listener = callback;
        listener.onProgress();
        String content = "javascript:sellram(" + "'" + account + "'" + "," + "'" + bytesnum + "'" + ")";
        webView.loadUrl(content);
    }

    public class JsInteraction {

        @JavascriptInterface public void getAAABalance(String error, String balance) {
            Log.i("info", "balance:" + balance);
            listener.onSuccess(balance);
        }

        @JavascriptInterface public void getSignature(String signature) {
            Log.i("info", "signature:" + signature);
            listener.onSuccess(signature);
        }

        @JavascriptInterface public void getEncryptKey(String enKey) {
            Log.i("info", "enKey:" + enKey);
            listener1.onSuccess(enKey);
        }

        @JavascriptInterface public void getDecryptKey(String deKey) {
            Log.i("info", "deKey:" + deKey);
            listener.onSuccess(deKey);
        }

        @JavascriptInterface public void prepay(String value) {
            Log.i("info", "prepay:" + value);
            listener.onSuccess(value);
        }

        @JavascriptInterface public void prepayError(String error) {
            Log.i("info", "prepay error:" + error);
            listener.onError(error);
        }

        @JavascriptInterface public void paySuccessStatus(String value) {
            Log.i("info", "paySuccessStatus:" + value);
            listener.onSuccess(value);
        }

        @JavascriptInterface public void payFailureStatus(String error) {
            Log.i("info", "payFailureStatus:" + error);
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

        @JavascriptInterface public void generationSecretKey(String privateKey, String publicKey) {
            Log.i("info", "privateKey:" + privateKey);
            Log.i("info", "publicKey:" + publicKey);
            listener.onSuccess(privateKey, publicKey);
        }

        @JavascriptInterface public void getPublicKey(String publicKey) {
            Log.i("info", "publicKey1:" + publicKey);
            listener.onSuccess(publicKey);
        }

        @JavascriptInterface public void authPermissionSuccess() {
            Log.i("info", "authPermissionSuccess:");
            listener.onSuccess("");
        }

        @JavascriptInterface public void authPermissionError(String error) {
            Log.i("info", "authPermissionError:" + error);
            listener.onError(error);
        }

        @JavascriptInterface public void transfer(String result, String error) {
            if (error == null) {
                listener.onSuccess(result);
                Log.i("info", "transfer successful:" + result);
            } else {
                listener.onError(error);
                Log.i("info", "transfer failure:" + error);
            }
        }

        @JavascriptInterface public void getAccountInfo(String result, String error) {
            if (error == null) {
                listener.onSuccess(result);
                Log.i("info", "getAccountInfo successful:" + result);
            } else {
                listener.onError(error);
                Log.i("info", "getAccountInfo failure:" + error);
            }
        }
    }

    private JSCallBack listener;
    private JSCallBack listener1;

    public interface JSCallBack {
        void onSuccess(String... stringArray);

        void onProgress();

        void onError(String error);
    }

    public void removeListener() {
        listener = null;
        listener1 = null;
    }
}
