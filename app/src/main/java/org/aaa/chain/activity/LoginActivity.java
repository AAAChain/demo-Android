package org.aaa.chain.activity;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.igexin.sdk.PushManager;
import com.squareup.leakcanary.RefWatcher;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import okhttp3.Call;
import okhttp3.Response;
import org.aaa.chain.ChainApplication;
import org.aaa.chain.Constant;
import org.aaa.chain.JSInteraction;
import org.aaa.chain.R;
import org.aaa.chain.db.DBManager;
import org.aaa.chain.entities.KeyInfoEntity;
import org.aaa.chain.utils.GeTuiIntentService;
import org.aaa.chain.utils.GeTuiPushService;
import org.aaa.chain.utils.HttpUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends BaseActivity {

    private TextView tvKey;
    private EditText etKey;
    private ListPopupWindow listPopupWindow;
    private EditText etNewAccount;

    private List<String> keyAccounts = new ArrayList<>();
    private List<String> keys = new ArrayList<>();
    private String password;

    @Override public int initLayout() {
        return R.layout.activity_login;
    }

    @Override public void getViewById() {
        tvKey = $(R.id.tv_key);
        etKey = $(R.id.et_key);
        Button btnLogin = $(R.id.btn_login);
        Button btnCreate = $(R.id.btn_create);
        etNewAccount = $(R.id.et_new_account);

        btnCreate.setOnClickListener(this);
        PushManager.getInstance().initialize(this.getApplicationContext(), GeTuiPushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), GeTuiIntentService.class);

        ChainApplication.getInstance().setKeyInfoEntity(DBManager.getInstance().getKeyInfo());
        Bundle bundle = getIntent().getExtras();

        if (bundle == null) {
            password = ChainApplication.getInstance().getKeyInfoEntity().get(0).getPassword();
        } else {
            password = bundle.getString("pwd");
        }

        for (KeyInfoEntity entity : ChainApplication.getInstance().getKeyInfoEntity()) {
            keyAccounts.add(entity.getAccount());
            keys.add(entity.getPrivateKey());
        }

        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }

        ImageView imageView = $(R.id.iv_app_title);
        if (locale.getLanguage().equals("zh")) {
            imageView.setImageResource(R.mipmap.app_title_zh_icon);
        } else {
            imageView.setImageResource(R.mipmap.app_title_en_icon);
        }

        tvKey.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        listPopupWindow = new ListPopupWindow(this);
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.listpopupwindow_item, keyAccounts.toArray(new String[keyAccounts.size()]));
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        listPopupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tvKey.setText(keyAccounts.get(position));
                etKey.setText(keys.get(position));
                listPopupWindow.dismiss();
            }
        });
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_key:
                listPopupWindow.setAnchorView(tvKey);
                listPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                listPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                listPopupWindow.setModal(true);
                listPopupWindow.setDropDownGravity(Gravity.START | Gravity.BOTTOM);
                listPopupWindow.show();
                break;

            case R.id.btn_login:
                if (!TextUtils.isEmpty(etKey.getText())) {
                    if (!keys.contains(etKey.getText().toString())) {
                        JSInteraction.getInstance().getPublicKey(etKey.getText().toString(), new JSInteraction.JSCallBack() {
                            @Override public void onSuccess(String... stringArray) {

                                runOnUiThread(new Runnable() {
                                    @Override public void run() {
                                        JSInteraction.getInstance().getKeyAccounts(stringArray[0], new JSInteraction.JSCallBack() {
                                            @Override public void onSuccess(String... stringArray) {
                                                KeyInfoEntity entity = new KeyInfoEntity();
                                                entity.setPublicKey(stringArray[0]);
                                                entity.setPrivateKey(etKey.getText().toString());
                                                try {
                                                    JSONObject jsonObject = new JSONObject(stringArray[0]);
                                                    JSONArray jsonArray = new JSONArray(jsonObject.getString("account_names"));
                                                    entity.setAccount(jsonArray.getString(0));
                                                    entity.setPassword(password);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                DBManager.getInstance().saveKeyInfo(entity);
                                                ChainApplication.getInstance().addKeyInfoEntity(entity);
                                                keyAccounts.add(entity.getAccount());
                                                keys.add(entity.getPrivateKey());

                                                Constant.setCurrentPrivateKey(entity.getPrivateKey());
                                                Constant.setCurrentPublicKey(entity.getPublicKey());
                                                Constant.setCurrentAccount(entity.getAccount());
                                                Constant.setPassword(entity.getPassword());
                                                startActivity(MainActivity.class, null);
                                            }

                                            @Override public void onProgress() {

                                            }

                                            @Override public void onError(String error) {

                                            }
                                        });
                                    }
                                });
                            }

                            @Override public void onProgress() {

                            }

                            @Override public void onError(String error) {

                            }
                        });
                    } else {
                        for (KeyInfoEntity entity : ChainApplication.getInstance().getKeyInfoEntity()) {
                            if (entity.getPrivateKey().equals(etKey.getText().toString())) {
                                Constant.setCurrentPublicKey(entity.getPublicKey());
                                Constant.setCurrentPrivateKey(entity.getPrivateKey());
                                Constant.setCurrentAccount(entity.getAccount());
                                Constant.setPassword(entity.getPassword());
                            }
                        }
                        startActivity(MainActivity.class, null);
                    }
                } else {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.input_secret_key), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_create:
                if (TextUtils.isEmpty(etNewAccount.getText().toString())) {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.input_new_account), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (etNewAccount.getText().toString().length() != 12) {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.account_name_rule), Toast.LENGTH_SHORT).show();
                    return;
                }
                ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, getResources().getString(R.string.waiting),
                        getResources().getString(R.string.creating));
                JSInteraction.getInstance().generationSecretKey(new JSInteraction.JSCallBack() {
                    @Override public void onSuccess(String... stringArray) {
                        HttpUtils.getInstance().newAccount(etNewAccount.getText().toString(), stringArray[1], new HttpUtils.ServerCallBack() {
                            @Override public void onFailure(Call call, IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override public void run() {
                                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.create_account_failure),
                                                Toast.LENGTH_SHORT).show();
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
                                        entity.setPassword(password);
                                        entity.setPrivateKey(stringArray[0]);
                                        entity.setPublicKey(stringArray[1]);

                                        DBManager.getInstance().saveKeyInfo(entity);
                                        keyAccounts.add(entity.getAccount());
                                        keys.add(entity.getPrivateKey());
                                        Constant.setCurrentAccount(entity.getAccount());
                                        Constant.setCurrentPrivateKey(entity.getPrivateKey());
                                        Constant.setCurrentPublicKey(entity.getPublicKey());
                                        Constant.setPassword(entity.getPassword());
                                        ChainApplication.getInstance().addKeyInfoEntity(entity);
                                        runOnUiThread(new Runnable() {
                                            @Override public void run() {
                                                dialog.dismiss();
                                                startActivity(MainActivity.class, null);
                                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.create_account_success),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override public void run() {
                                                dialog.dismiss();
                                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.create_account_failure),
                                                        Toast.LENGTH_SHORT).show();
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

    @Override protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = ChainApplication.getRefWatcher(this);
        refWatcher.watch(this);
    }
}
