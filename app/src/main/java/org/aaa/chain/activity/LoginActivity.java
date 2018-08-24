package org.aaa.chain.activity;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.leakcanary.RefWatcher;
import org.aaa.chain.ChainApplication;
import org.aaa.chain.R;

public class LoginActivity extends BaseActivity {

    private TextView tvKey;
    private EditText etKey;
    private Button btnLogin;
    private ListPopupWindow listPopupWindow;

    /**
     * aaauser1
     *
     * Owner key:
     * Private key: 5Jtsaf2tyLGBTJER4bCBV5pvKiAE4v3v3G4agfQ91DkfLHRSLzX
     * Public key: EOS5odhW8Tw51UetwxnjVMRKv6Eq4SPn6UqidCt57HYN5KjymsA2v
     *
     * Active Key
     * Private key: 5JobQnxtEvshVRZW6berfYvzaUMZq2A8Ax5eZhuZqdTCqT19iLV
     * Public key: EOS5YQkZpsD8xzmguAGL88r2b4RBXxKbV3QGjS8Vg1maBzd8Df9zX
     *
     * ################################################################################
     * aaauser2
     *
     * Owner key:
     * Private key: 5KejiFM4Qq1kshgtShE5n9bVLf63AmVhkReMYjhJPChcoKAog1c
     * Public key: EOS7gqLCvRQFxLfbT2LMbzQQwAngToB3YApob74zgsZ7VN7vUBUtX
     *
     * Active Key
     * Private key: 5J4a77MxGSDnASAZHAV7gThSeoenvLB4nb8wFPkepXoiLyesuf5
     * Public key: EOS5SW9SkzUuTwhbJMsFDRge7cgKoTABiQ6ac3DrDux2UjjpKbm1h
     **/

    private String[] keysName = { "key1", "key2" };
    private String[] keys = { "5Jtsaf2tyLGBTJER4bCBV5pvKiAE4v3v3G4agfQ91DkfLHRSLzX", "5KejiFM4Qq1kshgtShE5n9bVLf63AmVhkReMYjhJPChcoKAog1c" };

    @Override public int initLayout() {
        return R.layout.activity_login;
    }

    @Override public void getViewById() {

        tvKey = $(R.id.tv_key);
        etKey = $(R.id.et_key);
        btnLogin = $(R.id.btn_login);

        tvKey.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        listPopupWindow = new ListPopupWindow(this);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.listpopupwindow_item, keysName);
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        listPopupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tvKey.setText(keysName[position]);
                etKey.setText(keys[position]);
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
                    startActivity(MainActivity.class, null);
                } else {
                    Toast.makeText(LoginActivity.this, "请输入秘钥", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = ChainApplication.getRefWatcher(this);
        refWatcher.watch(this);
    }
}
