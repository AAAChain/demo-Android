package org.aaa.chain.activity;

import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
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
import com.squareup.leakcanary.RefWatcher;
import java.util.Locale;
import org.aaa.chain.ChainApplication;
import org.aaa.chain.Constant;
import org.aaa.chain.R;

public class LoginActivity extends BaseActivity {

    private TextView tvKey;
    private EditText etKey;
    private ListPopupWindow listPopupWindow;

    private String[] keysName = { Constant.getAccount(), Constant.getAnotherAccount() };
    private String[] keys = { Constant.getPrivateKey(), Constant.getAnotherPrivateKey() };

    @Override public int initLayout() {
        return R.layout.activity_login;
    }

    @Override public void getViewById() {
        tvKey = $(R.id.tv_key);
        etKey = $(R.id.et_key);
        Button btnLogin = $(R.id.btn_login);

        boolean isImport = getIntent().getBooleanExtra("import", false);
        if (isImport) {
            tvKey.setVisibility(View.GONE);
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
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.listpopupwindow_item, keysName);
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        listPopupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tvKey.setText(keysName[position]);
                etKey.setText(keys[position]);
                if (position == 0) {
                    ChainApplication.getInstance().isAccount1 = true;
                } else {
                    ChainApplication.getInstance().isAccount1 = false;
                }
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
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.input_secret_key), Toast.LENGTH_SHORT).show();
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
