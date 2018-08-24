package org.aaa.chain.activity;

import android.view.View;
import android.widget.Button;
import org.aaa.chain.R;

public class ResumeDetailsActivity extends BaseActivity {

    Button buy;
    int type = 0;
    Button authorization;

    @Override public int initLayout() {
        type = getIntent().getIntExtra("type", 0);
        int id;
        if (type == 0) {
            id = R.layout.activity_resume_details;
        } else {
            id = R.layout.activity_postion_info;
        }
        return id;
    }

    @Override public void getViewById() {

        if (type == 0){
            buy = $(R.id.btn_resume_buy);
            buy.setOnClickListener(this);

        }else {
            authorization = $(R.id.btn_authorization);
            authorization.setOnClickListener(this);
        }
    }

    @Override public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_resume_buy:

                break;

            case R.id.btn_authorization:

                break;
        }
    }
}
