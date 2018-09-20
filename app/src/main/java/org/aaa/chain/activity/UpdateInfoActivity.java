package org.aaa.chain.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.aaa.chain.R;

public class UpdateInfoActivity extends BaseActivity {

    private EditText updateInfo;
    private TextView done;
    private String title;
    private String name;

    @Override public int initLayout() {
        return R.layout.activity_update_info;
    }

    @Override public void getViewById() {
        title = getIntent().getStringExtra("title");
        name = getIntent().getStringExtra("name");
        setTitleName(title);
        updateInfo = $(R.id.et_update_info);
        updateInfo.setText(name);
        done = $(R.id.tv_title_bar_done);
        done.setVisibility(View.VISIBLE);
        done.setOnClickListener(this);
    }

    @Override public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_title_bar_done:
                if (!TextUtils.isEmpty(updateInfo.getText())) {
                    Intent intent = new Intent();
                    intent.putExtra("updateinfo", updateInfo.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(UpdateInfoActivity.this, "not null", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
