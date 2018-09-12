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

    private RelativeLayout rlCompany;
    private RelativeLayout rlTimeRange;
    private RelativeLayout rlJobType;
    private RelativeLayout rlSkillLabel;

    private TextView tvCompany;
    private TextView tvTimeRange;
    private TextView tvJobType;
    private TextView tvSkillLabel;
    private String personalinfo;
    private JSONObject object;

    @Override public int initLayout() {
        return R.layout.activity_create_small_resume;
    }

    @Override public void getViewById() {

        initSlideBackLayout(this);

        personalinfo = getIntent().getStringExtra("personalinfo");
        try {
            object = new JSONObject(personalinfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        rlCompany = $(R.id.rl_company);
        rlTimeRange = $(R.id.rl_time_range);
        rlJobType = $(R.id.rl_job_type);
        rlSkillLabel = $(R.id.rl_skill_label);
        Button btnGet = $(R.id.btn_next);
        btnGet.setOnClickListener(this);
        rlCompany.setOnClickListener(this);
        rlTimeRange.setOnClickListener(this);
        rlJobType.setOnClickListener(this);
        rlSkillLabel.setOnClickListener(this);

        tvCompany = $(R.id.tv_company);
        tvTimeRange = $(R.id.tv_time_range);
        tvJobType = $(R.id.tv_job_type);
        tvSkillLabel = $(R.id.tv_skill_label);

        setTitleName(getResources().getString(R.string.create_a_resume));
    }

    @Override public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rl_company:
                startActivityForResult(new Intent(CreateSmallResumeActivity.this, UpdateInfoActivity.class).putExtra("title", "公司名称"), 1000);
                break;
            case R.id.rl_time_range:

                CommonUtils.getInstance().initDate(CreateSmallResumeActivity.this, tvTimeRange);
                break;
            case R.id.rl_job_type:

                break;
            case R.id.rl_skill_label:

                break;
            case R.id.btn_next:
                try {
                    object.put("latestWorkHours", tvTimeRange.getText().toString());
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
            tvCompany.setText(data.getExtras().getString("updateinfo"));
            try {
                object.put("latestCompany", data.getExtras().getString("updateinfo"));
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
