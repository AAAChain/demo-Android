package org.aaa.chain.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.leakcanary.RefWatcher;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import org.aaa.chain.AAAWalletUtils;
import org.aaa.chain.IpfsApplication;
import org.aaa.chain.R;
import org.aaa.chain.common.ListenerCallback;
import org.aaa.chain.common.LoginManager;
import org.aaa.chain.permissions.PermissionsManager;
import org.aaa.chain.permissions.PermissionsResultAction;
import org.aaa.chain.utils.ImageUtils;
import org.aaa.chain.views.CommonPopupWindow;
import org.web3j.crypto.CipherException;

public class LoginActivity extends BaseActivity implements ListenerCallback {

    private EditText etPhoneNum;
    private EditText etVerificationCode;

    @Override public int initLayout() {
        return R.layout.activity_login;
    }

    @Override public void getViewById() {

        etPhoneNum = $(R.id.et_phone_num);
        etVerificationCode = $(R.id.et_verification_code);
        Button btnSendCode = $(R.id.btn_send_code);
        Button btnLogin = $(R.id.btn_login);


        btnSendCode.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

    }

    @Override public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_send_code:
                //new CommonPopupWindow(LoginActivity.this).pupupWindowOnBottom();
                break;

            case R.id.btn_login:
                new LoginManager().doLogin(etPhoneNum.getText().toString(), etVerificationCode.getText().toString(), this);
                break;
        }
    }

    @Override public void onCallBackError(String msg, int code) {

        showToast(this, msg);
        startActivity(MainActivity.class, null);
        //startActivity(JSActivity.class, null);
        //finish();
    }

    @Override public void onCallBackSuccess() {
        showToast(this, "successful");
        //startActivity(MainActivity.class, null);
        startActivity(JSActivity.class, null);
    }

    @Override public void showProgress() {

    }

    @Override public void hideProgress() {

    }

    //@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //    super.onActivityResult(requestCode, resultCode, data);
    //    if (ImageUtils.ALBUM_REQUEST_CODE == requestCode && data != null) {
    //        ImageUtils.getInstance().handleImage(LoginActivity.this, data);
    //    } else if (ImageUtils.TAKE_PHOTO_REQUEST_CODE == requestCode) {
    //        ImageUtils.getInstance().handleImage(LoginActivity.this, null);
    //    } else if (ImageUtils.CROP_IMAGE_REQUEST_CODE == requestCode && data != null) {
    //        Bitmap bit;
    //        try {
    //            bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
    //        } catch (FileNotFoundException e) {
    //            e.printStackTrace();
    //        }
    //    }
    //}

    @Override protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = IpfsApplication.getRefWatcher(this);
        refWatcher.watch(this);
    }
}
