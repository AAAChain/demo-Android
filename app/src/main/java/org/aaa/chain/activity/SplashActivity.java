package org.aaa.chain.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.squareup.leakcanary.RefWatcher;
import java.util.ArrayList;
import java.util.List;
import org.aaa.chain.ChainApplication;
import org.aaa.chain.JSInteraction;
import org.aaa.chain.R;
import org.aaa.chain.db.DBManager;
import org.aaa.chain.entities.KeyInfoEntity;
import org.aaa.chain.utils.PreferenceUtils;

public class SplashActivity extends BaseActivity {

    private EditText etPwd;
    private EditText etConfirmPwd;

    @Override public int initLayout() {
        return R.layout.activity_splash;
    }

    @Override public void getViewById() {

        JSInteraction.getInstance().initWebKit(this);

        Button btnGeneration = $(R.id.btn_generation);
        etPwd = $(R.id.et_pwd);
        etConfirmPwd = $(R.id.et_confirm_pwd);

        btnGeneration.setOnClickListener(this);
        if (!PreferenceUtils.getInstance().getIsFirst()) {
            startActivity(LoginActivity.class, null);
            finish();
        }
    }

    @Override public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_generation:

                if (TextUtils.isEmpty(etPwd.getText().toString()) || TextUtils.isEmpty(etConfirmPwd.getText().toString())) {
                    Toast.makeText(SplashActivity.this, getResources().getString(R.string.input_password), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!etPwd.getText().toString().equals(etConfirmPwd.getText().toString())) {
                    Toast.makeText(SplashActivity.this, getResources().getString(R.string.password_not_identical), Toast.LENGTH_SHORT).show();
                    return;
                }

                String content = etPwd.getText().toString();

                List<KeyInfoEntity> keyInfoEntityList = new ArrayList<>(DBManager.getInstance().getKeyInfo());
                if (keyInfoEntityList.size() == 0) {
                    KeyInfoEntity entity = new KeyInfoEntity();
                    entity.setPrivateKey("5JWYoMqLxGAmHi5BnhYRSdaTpNsF4jzcUCgKq57LMHqHqnCGJn4");
                    entity.setPublicKey("AAA7RccFsFi5NqgDQerEYRJ7odQ5EX135N1kD4v1hxKAfrCadKxp3");
                    entity.setAccount("aaauser1");
                    entity.setPassword(content);
                    DBManager.getInstance().saveKeyInfo(entity);
                    KeyInfoEntity entity1 = new KeyInfoEntity();
                    entity1.setPrivateKey("5KZyaoA9W2N6CP7EDoYBXXVMySVm1ZswVte2beByL2SD1C1cnEk");
                    entity1.setPublicKey("AAA5HZLkL5tat8aEa8egckNNeuFHWvLSCvC5v2v7N8WPSpQT2FzPM");
                    entity1.setAccount("aaauser2");
                    entity1.setPassword(content);
                    DBManager.getInstance().saveKeyInfo(entity1);
                    keyInfoEntityList.add(entity);
                    keyInfoEntityList.add(entity1);
                    ChainApplication.getInstance().setKeyInfoEntity(keyInfoEntityList);
                } else {
                    ChainApplication.getInstance().setKeyInfoEntity(keyInfoEntityList);
                }
                PreferenceUtils.getInstance().setIsFirst(false);

                Bundle bundle = new Bundle();
                bundle.putString("pwd", content);
                startActivity(LoginActivity.class, bundle);
                finish();

                break;
        }
    }

    @Override protected void onStop() {
        super.onStop();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = ChainApplication.getRefWatcher(this);
        refWatcher.watch(this);
    }
}
