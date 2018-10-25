package org.aaa.chain.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.squareup.leakcanary.RefWatcher;
import java.util.List;
import org.aaa.chain.ChainApplication;
import org.aaa.chain.Constant;
import org.aaa.chain.JSInteraction;
import org.aaa.chain.R;
import org.aaa.chain.db.DBManager;
import org.aaa.chain.entities.KeyInfoEntity;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends BaseActivity {

    private EditText etPwd;

    @Override public int initLayout() {
        return R.layout.activity_splash;
    }

    @Override public void getViewById() {

        JSInteraction.getInstance().initWebKit(this);

        Button btnDemo = $(R.id.btn_demo);
        Button btnImport = $(R.id.btn_import);
        Button btnCreate = $(R.id.btn_create);
        etPwd = $(R.id.et_pwd);

        btnDemo.setOnClickListener(this);
        btnImport.setOnClickListener(this);
        btnCreate.setOnClickListener(this);
    }

    @Override public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_demo:
                startActivity(LoginActivity.class, null);
                break;

            case R.id.btn_import:
                Bundle bundle = new Bundle();
                bundle.putBoolean("import", true);
                startActivity(LoginActivity.class, bundle);

                break;

            case R.id.btn_create:

                if (TextUtils.isEmpty(etPwd.getText().toString())) {
                    Toast.makeText(SplashActivity.this, "please set your password", Toast.LENGTH_SHORT).show();
                    return;
                }
                ProgressDialog dialog = ProgressDialog.show(SplashActivity.this, "waiting...", "creating...");
                JSInteraction.getInstance().generationSecretKey(new JSInteraction.JSCallBack() {
                    @Override public void onSuccess(String... stringArray) {
                        KeyInfoEntity entity = new KeyInfoEntity();
                        entity.setAccount("aaauser");
                        entity.setPassword(etPwd.getText().toString());
                        entity.setPrivateKey(stringArray[0]);
                        entity.setPublicKey(stringArray[1]);

                        DBManager.getInstance().saveKeyInfo(entity);
                        dialog.dismiss();
                        startActivity(LoginActivity.class, null);
                    }

                    @Override public void onProgress() {

                    }

                    @Override public void onError(String error) {

                    }
                });
                break;
        }
    }

    @Override protected void onStop() {
        super.onStop();
        JSInteraction.getInstance().removeListener();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = ChainApplication.getRefWatcher(this);
        refWatcher.watch(this);
    }
}
