package org.aaa.chain.common;

public interface ListenerCallback {

    void onCallBackError(String msg,int code);

    void onCallBackSuccess();

    void showProgress();

    void hideProgress();
}
