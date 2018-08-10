package org.aaa.chain.common;

import android.text.TextUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginManager {

    public void doLogin(String phoneNum, String verificationCode, ListenerCallback listenerCallback) {
        listenerCallback.showProgress();
        if (!TextUtils.isEmpty(phoneNum)) {
            if (phoneNum.trim().length() == 11) {
                if (isPhoneNum(phoneNum)) {
                    if (!TextUtils.isEmpty(verificationCode)) {
                        listenerCallback.onCallBackSuccess();
                    } else {
                        listenerCallback.onCallBackError("verification code is not null", -1);
                    }
                } else {
                    listenerCallback.onCallBackError("phone number is not legal", -1);
                }
            } else {
                listenerCallback.onCallBackError("phone number is wrong", -1);
            }
        } else {
            listenerCallback.onCallBackError("phone number is not null", -1);
        }
        listenerCallback.hideProgress();
    }

    private boolean isPhoneNum(String phoneNum) {
        String REGEX_MOBILE = "^[1][3578][0-9]{9}$";
        Pattern p = Pattern.compile(REGEX_MOBILE);
        Matcher m = p.matcher(phoneNum);
        return m.matches();
    }
}
