package org.aaa.chain.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.leakcanary.RefWatcher;
import org.aaa.chain.ChainApplication;
import org.aaa.chain.R;
import org.aaa.chain.utils.CommonUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class CreateSmallResumeActivity extends BaseActivity {

    private RelativeLayout rlLastCompany;
    private RelativeLayout rlLastWorkingHour;
    private RelativeLayout rlJobType;
    private RelativeLayout rlSkillLabel;

    private TextView tvLastCompany;
    private TextView tvLastWorkingHour;
    private TextView tvJobType;
    private TextView tvSkillLabel;
    private String personalinfo;
    private JSONObject object;

    @Override public int initLayout() {
        return R.layout.activity_create_small_resume;
    }

    @Override public void getViewById() {

        personalinfo = getIntent().getStringExtra("personalinfo");
        try {
            object = new JSONObject(personalinfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        rlLastCompany = $(R.id.rl_last_company);
        rlLastWorkingHour = $(R.id.rl_last_working_hour);
        rlJobType = $(R.id.rl_job_type);
        rlSkillLabel = $(R.id.rl_skill_label);
        Button btnGet = $(R.id.btn_next);
        btnGet.setOnClickListener(this);
        rlLastCompany.setOnClickListener(this);
        rlLastWorkingHour.setOnClickListener(this);
        rlJobType.setOnClickListener(this);
        rlSkillLabel.setOnClickListener(this);

        tvLastCompany = $(R.id.tv_last_company);
        tvLastWorkingHour = $(R.id.tv_last_working_hour);
        tvJobType = $(R.id.tv_job_type);
        tvSkillLabel = $(R.id.tv_skill_label);

        setTitleName(getResources().getString(R.string.create_a_resume));
    }

    @Override public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rl_last_company:
                startActivityForResult(new Intent(CreateSmallResumeActivity.this, UpdateInfoActivity.class).putExtra("title", "公司名称"), 1000);
                break;
            case R.id.rl_last_working_hour:

                CommonUtils.getInstance().initDate(CreateSmallResumeActivity.this, tvLastWorkingHour);
                break;
            case R.id.rl_job_type:

                break;
            case R.id.rl_skill_label:

                break;
            case R.id.btn_next:
                try {
                    object.put("latWorkingHour", tvLastWorkingHour.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Bundle bundle = new Bundle();
                bundle.putString("metadata", object.toString());
                startActivity(UploadResumeActivity.class, bundle);
                finish();
                break;
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode && 1000 == requestCode) {
            tvLastCompany.setText(data.getExtras().getString("updateinfo"));
            try {
                object.put("latCompany", data.getExtras().getString("updateinfo"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = ChainApplication.getRefWatcher(this);
        refWatcher.watch(this);
    }
}
