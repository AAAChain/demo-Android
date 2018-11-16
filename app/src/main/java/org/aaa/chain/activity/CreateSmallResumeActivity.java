package org.aaa.chain.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.leakcanary.RefWatcher;
import org.aaa.chain.ChainApplication;
import org.aaa.chain.R;
import org.aaa.chain.utils.CommonUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class CreateSmallResumeActivity extends BaseActivity {

    private TextView tvLastCompany;
    private TextView tvLastWorkingHour;
    private TextView tvJobType;
    private EditText etLastJobContentInfo;
    private JSONObject object;

    @Override public int initLayout() {
        return R.layout.activity_create_small_resume;
    }

    @Override public void getViewById() {

        String personalinfo = getIntent().getStringExtra("personalinfo");

        RelativeLayout rlLastCompany = $(R.id.rl_last_company);
        RelativeLayout rlLastWorkingHour = $(R.id.rl_last_working_hour);
        RelativeLayout rlJobType = $(R.id.rl_job_type);
        RelativeLayout rlSkillLabel = $(R.id.rl_skill_label);
        Button btnGet = $(R.id.btn_next);
        btnGet.setOnClickListener(this);
        rlLastCompany.setOnClickListener(this);
        rlLastWorkingHour.setOnClickListener(this);
        rlJobType.setOnClickListener(this);
        rlSkillLabel.setOnClickListener(this);

        tvLastCompany = $(R.id.tv_last_company);
        tvLastWorkingHour = $(R.id.tv_last_working_hour);
        tvJobType = $(R.id.tv_job_type);
        TextView tvSkillLabel = $(R.id.tv_skill_label);
        etLastJobContentInfo = $(R.id.et_last_job_content_info);

        try {
            object = new JSONObject(personalinfo);
            String jobType = object.getString("jobType");
            tvJobType.setText(jobType);
            String lastCompany = object.getString("lastCompany");
            String lastWorkingHour = object.getString("lastWorkingHour");
            String lastJobContentInfo = object.getString("lastJobContentInfo");
            tvLastCompany.setText(lastCompany);
            tvLastWorkingHour.setText(lastWorkingHour);
            if (!TextUtils.isEmpty(lastJobContentInfo)) {
                etLastJobContentInfo.setText(lastJobContentInfo);
            } else {
                etLastJobContentInfo.setText(getResources().getString(R.string.last_job_details));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setTitleName(getResources().getString(R.string.create_a_resume));
    }

    @Override public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rl_last_company:
                startActivityForResult(new Intent(CreateSmallResumeActivity.this, UpdateInfoActivity.class).putExtra("title",
                        getResources().getString(R.string.company_name)).putExtra("name", tvLastCompany.getText().toString()), 1000);
                break;
            case R.id.rl_last_working_hour:

                CommonUtils.getInstance().initDate(CreateSmallResumeActivity.this.getApplicationContext(), tvLastWorkingHour, true);
                break;
            case R.id.rl_job_type:
                startActivityForResult(new Intent(CreateSmallResumeActivity.this, UpdateInfoActivity.class).putExtra("title",
                        getResources().getString(R.string.position_type)).putExtra("name", tvJobType.getText().toString()), 2000);
                break;
            case R.id.rl_skill_label:

                break;
            case R.id.btn_next:
                try {
                    object.put("lastWorkingHour", tvLastWorkingHour.getText().toString());
                    object.put("lastJobContentInfo", etLastJobContentInfo.getText().toString());
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
        if (RESULT_OK == resultCode) {
            String content = data.getExtras().getString("updateinfo");
            if (1000 == requestCode) {
                tvLastCompany.setText(content);
                try {
                    object.put("lastCompany", content);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (2000 == requestCode) {
                tvJobType.setText(content);
                try {
                    object.put("jobType", content);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = ChainApplication.getRefWatcher(this);
        refWatcher.watch(this);
    }
}
