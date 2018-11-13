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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Response;
import org.aaa.chain.ChainApplication;
import org.aaa.chain.Constant;
import org.aaa.chain.JSInteraction;
import org.aaa.chain.R;
import org.aaa.chain.db.DBManager;
import org.aaa.chain.entities.KeyInfoEntity;
import org.aaa.chain.utils.HttpUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends BaseActivity {

    private EditText etPwd;
    private EditText etNewAccount;

    private List<KeyInfoEntity> keyInfoEntityList = new ArrayList<>();

    @Override public int initLayout() {
        return R.layout.activity_splash;
    }

    @Override public void getViewById() {

        JSInteraction.getInstance().initWebKit(this);

        Button btnDemo = $(R.id.btn_demo);
        Button btnImport = $(R.id.btn_import);
        Button btnCreate = $(R.id.btn_create);
        etPwd = $(R.id.et_pwd);
        etNewAccount = $(R.id.et_new_account);

        btnDemo.setOnClickListener(this);
        btnImport.setOnClickListener(this);
        btnCreate.setOnClickListener(this);

        keyInfoEntityList.addAll(DBManager.getInstance().getKeyInfo());
        if (keyInfoEntityList.size() == 0) {
            KeyInfoEntity entity = new KeyInfoEntity();
            entity.setPrivateKey("5JWYoMqLxGAmHi5BnhYRSdaTpNsF4jzcUCgKq57LMHqHqnCGJn4");
            entity.setPublicKey("AAA7RccFsFi5NqgDQerEYRJ7odQ5EX135N1kD4v1hxKAfrCadKxp3");
            entity.setAccount("aaauser1");
            entity.setPassword("123456");
            DBManager.getInstance().saveKeyInfo(entity);
            KeyInfoEntity entity1 = new KeyInfoEntity();
            entity1.setPrivateKey("5KZyaoA9W2N6CP7EDoYBXXVMySVm1ZswVte2beByL2SD1C1cnEk");
            entity1.setPublicKey("AAA5HZLkL5tat8aEa8egckNNeuFHWvLSCvC5v2v7N8WPSpQT2FzPM");
            entity1.setAccount("aaauser2");
            entity1.setPassword("123456");
            DBManager.getInstance().saveKeyInfo(entity1);
            keyInfoEntityList.add(entity);
            keyInfoEntityList.add(entity1);
            ChainApplication.getInstance().setKeyInfoEntity(keyInfoEntityList);
        } else {
            ChainApplication.getInstance().setKeyInfoEntity(keyInfoEntityList);
        }
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
                if (TextUtils.isEmpty(etNewAccount.getText().toString())) {
                    Toast.makeText(SplashActivity.this, "please new account", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (etNewAccount.getText().toString().length() != 12) {
                    Toast.makeText(SplashActivity.this, "account name should be exactly 12 characters long", Toast.LENGTH_SHORT).show();
                    return;
                }
                ProgressDialog dialog = ProgressDialog.show(SplashActivity.this, "waiting...", "creating...");
                JSInteraction.getInstance().generationSecretKey(new JSInteraction.JSCallBack() {
                    @Override public void onSuccess(String... stringArray) {
                        HttpUtils.getInstance().newAccount(etNewAccount.getText().toString(), stringArray[1], new HttpUtils.ServerCallBack() {
                            @Override public void onFailure(Call call, IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override public void run() {
                                        Toast.makeText(SplashActivity.this, "new account failure", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });
                            }

                            @Override public void onResponse(Call call, Response response) {

                                try {
                                    String msg = response.body().string();
                                    Log.i("info", "new account:" + msg);
                                    if (response.code() == 200) {
                                        KeyInfoEntity entity = new KeyInfoEntity();
                                        entity.setAccount(etNewAccount.getText().toString());
                                        entity.setPassword(etPwd.getText().toString());
                                        entity.setPrivateKey(stringArray[0]);
                                        entity.setPublicKey(stringArray[1]);

                                        DBManager.getInstance().saveKeyInfo(entity);
                                        ChainApplication.getInstance().addKeyInfoEntity(entity);
                                        runOnUiThread(new Runnable() {
                                            @Override public void run() {
                                                dialog.dismiss();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("account", etNewAccount.getText().toString());
                                                bundle.putString("privatekey", stringArray[0]);
                                                startActivity(LoginActivity.class, bundle);
                                                Toast.makeText(SplashActivity.this, "new account successful", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override public void run() {
                                                dialog.dismiss();
                                                Toast.makeText(SplashActivity.this, "new account failure" + response.code(), Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        });
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
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
