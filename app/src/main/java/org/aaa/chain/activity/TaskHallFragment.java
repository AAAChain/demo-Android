package org.aaa.chain.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.leakcanary.RefWatcher;
import java.util.Objects;
import org.aaa.chain.ChainApplication;
import org.aaa.chain.R;
import org.aaa.chain.entities.PersonalEntity;
import org.aaa.chain.utils.CommonUtils;

import static android.app.Activity.RESULT_OK;

public class TaskHallFragment extends BaseFragment {

    private RelativeLayout rlBirthday;
    private RelativeLayout rlCompany;

    private RadioGroup rgSex;
    private RadioButton rbFemale;
    private RadioButton rbMale;
    private RelativeLayout rlWorkTime;
    private Button btnNext;

    private TextView tvDate;
    private TextView tvBirthday;
    private TextView tvCompany;
    private PersonalEntity personalEntity;

    @Override public int initLayout() {
        return R.layout.fragment_task_hall;
    }

    @Override public void getViewById() {

        ((BaseActivity) Objects.requireNonNull(getActivity())).setTitleName("个人信息");

        rgSex = $(R.id.rg_sex);
        rbFemale = $(R.id.rb_female);
        rbMale = $(R.id.rb_male);
        tvDate = $(R.id.tv_date_show);
        tvBirthday = $(R.id.tv_birthday_show);
        tvCompany = $(R.id.tv_company);

        personalEntity = new PersonalEntity();

        rbMale.setChecked(true);

        rgSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_female:
                        personalEntity.setSex(1);
                        break;

                    case R.id.rb_male:
                        personalEntity.setSex(0);

                        break;
                }
            }
        });

        rlWorkTime = $(R.id.rl_work_time);
        rlBirthday = $(R.id.rl_birthday);
        rlCompany = $(R.id.rl_company);
        rlWorkTime.setOnClickListener(this);
        rlBirthday.setOnClickListener(this);
        rlCompany.setOnClickListener(this);
        btnNext = $(R.id.btn_next);
        btnNext.setOnClickListener(this);
    }

    @Override public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rl_work_time:
                CommonUtils.getInstance().initDate(getActivity(), tvDate);
                break;
            case R.id.rl_birthday:
                CommonUtils.getInstance().initDate(getActivity(), tvBirthday);
                break;
            case R.id.rl_company:
                startActivityForResult(new Intent(getActivity(), UpdateInfoActivity.class).putExtra("title", "公司名称"), 1000);
                break;

            case R.id.btn_next:
                //if (TextUtils.isEmpty(tvDate.getText())) {
                //    Toast.makeText(getActivity(), "请选择参加工作时间", Toast.LENGTH_SHORT).show();
                //    return;
                //}
                //if (TextUtils.isEmpty(tvBirthday.getText())) {
                //    Toast.makeText(getActivity(), "请选择出生年月", Toast.LENGTH_SHORT).show();
                //    return;
                //}
                //if (TextUtils.isEmpty(tvCompany.getText())) {
                //    Toast.makeText(getActivity(), "请设置公司名称", Toast.LENGTH_SHORT).show();
                //    return;
                //}
                personalEntity.setWorkHours(tvDate.getText().toString());
                personalEntity.setBirthday(tvBirthday.getText().toString());
                personalEntity.setCompany(tvCompany.getText().toString());
                startActivity(new Intent(getActivity(), CreateSmallResumeActivity.class).putExtra("personal", personalEntity));
                break;
        }
    }

    @Override public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = ChainApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode && requestCode == 1000) {
            String info = data.getExtras().getString("updateinfo");
            tvCompany.setText(info);
        }
    }
}
