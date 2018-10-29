package org.aaa.chain.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.squareup.leakcanary.RefWatcher;
import java.io.IOException;
import java.util.Objects;
import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.aaa.chain.ChainApplication;
import org.aaa.chain.R;
import org.aaa.chain.entities.ExtraEntity;
import org.aaa.chain.entities.SearchResponseEntity;
import org.aaa.chain.utils.CommonUtils;
import org.aaa.chain.utils.HttpUtils;
import org.json.JSONException;
import org.json.JSONObject;

import static android.app.Activity.RESULT_OK;

public class UploadHomeFragment extends BaseFragment {

    private RadioButton rbFemale;
    private RadioButton rbMale;

    private TextView tvStartTime;
    private TextView tvBirthday;
    private TextView tvCompany;
    private EditText etJobContentInfo;
    private JSONObject object = new JSONObject();
    private ExtraEntity extraEntity;
    private SearchResponseEntity searchResponseEntity;
    private boolean isRefresh = true;

    @Override public int initLayout() {
        return R.layout.fragment_upload_home;
    }

    @Override public void getViewById() {

        ((BaseActivity) Objects.requireNonNull(getActivity())).setTitleName(getResources().getString(R.string.upload_home));
        RadioGroup rgSex = $(R.id.rg_sex);
        rbFemale = $(R.id.rb_female);
        rbMale = $(R.id.rb_male);
        tvStartTime = $(R.id.tv_start_time);
        tvBirthday = $(R.id.tv_birthday_show);
        tvCompany = $(R.id.tv_company);
        etJobContentInfo = $(R.id.et_job_content_info);
        if (searchResponseEntity == null) {
            getBaseInfo();
        }

        rbMale.setChecked(true);
        Drawable drawableMale = getResources().getDrawable(R.drawable.radio_button_bg);
        drawableMale.setBounds(0, 0, 70, 70);
        rbMale.setCompoundDrawables(drawableMale, null, null, null);

        Drawable drawableFemale = getResources().getDrawable(R.drawable.radio_button_bg);
        drawableFemale.setBounds(0, 0, 70, 70);
        rbFemale.setCompoundDrawables(drawableFemale, null, null, null);

        rgSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_female:
                        try {
                            object.put("sex", "female");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;

                    case R.id.rb_male:
                        try {
                            object.put("sex", "male");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;
                }
            }
        });

        RelativeLayout rlStartTime = $(R.id.rl_start_time);
        RelativeLayout rlBirthday = $(R.id.rl_birthday);
        RelativeLayout rlCompany = $(R.id.rl_company);
        rlStartTime.setOnClickListener(this);
        rlBirthday.setOnClickListener(this);
        rlCompany.setOnClickListener(this);
        Button btnNext = $(R.id.btn_next);
        btnNext.setOnClickListener(this);
    }

    @Override public void onResume() {
        super.onResume();
        if (searchResponseEntity != null && isRefresh) {
            extraEntity = ChainApplication.getInstance().getBaseInfo().getDocs().get(0).getExtra();
            String sex = extraEntity.getSex();
            if ("male".equals(sex)) {
                rbMale.setChecked(true);
            } else {
                rbFemale.setChecked(true);
            }
            String startTime = extraEntity.getStartTime();
            tvStartTime.setText(startTime);
            String birthday = extraEntity.getBirthday();
            tvBirthday.setText(birthday);
            String company = extraEntity.getCompany();
            tvCompany.setText(company);
            String jobContentInfo = extraEntity.getJobContentInfo();
            if (!TextUtils.isEmpty(jobContentInfo)) {
                etJobContentInfo.setText(jobContentInfo);
            } else {
                etJobContentInfo.setText(getResources().getString(R.string.last_job_details));
            }
        }
    }

    private void getBaseInfo() {
        ProgressDialog dialog = ProgressDialog.show(getActivity(), "waiting...", "loading...");
        new Thread(new Runnable() {
            @Override public void run() {
                HttpUtils.getInstance().getBaseInfo(new HttpUtils.ServerCallBack() {
                    @Override public void onFailure(Call call, IOException e) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override public void run() {
                                dialog.dismiss();
                                Toast.makeText(getActivity(), "failure", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override public void onResponse(Call call, Response response) {

                        if (response.code() == 200) {
                            ResponseBody body = response.body();
                            try {
                                searchResponseEntity = new Gson().fromJson(body.string(), SearchResponseEntity.class);
                                if (searchResponseEntity.getDocs().size() > 0) {
                                    ChainApplication.getInstance().setBaseInfo(searchResponseEntity);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override public void run() {
                                            dialog.dismiss();
                                            extraEntity = searchResponseEntity.getDocs().get(0).getExtra();
                                            String sex = extraEntity.getSex();
                                            if ("male".equals(sex)) {
                                                rbMale.setChecked(true);
                                            } else {
                                                rbFemale.setChecked(true);
                                            }
                                            String startTime = extraEntity.getStartTime();
                                            tvStartTime.setText(startTime);
                                            String birthday = extraEntity.getBirthday();
                                            tvBirthday.setText(birthday);
                                            String company = extraEntity.getCompany();
                                            tvCompany.setText(company);
                                            String jobContentInfo = extraEntity.getJobContentInfo();
                                            if (!TextUtils.isEmpty(jobContentInfo)) {
                                                etJobContentInfo.setText(jobContentInfo);
                                            } else {
                                                etJobContentInfo.setText(getResources().getString(R.string.last_job_details));
                                            }
                                        }
                                    });
                                }else {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override public void run() {
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override public void run() {
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(), "failure", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }

    @Override public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rl_start_time:
                CommonUtils.getInstance().initDate(getActivity(), tvStartTime, false);
                break;
            case R.id.rl_birthday:
                CommonUtils.getInstance().initDate(getActivity(), tvBirthday, false);
                break;
            case R.id.rl_company:
                startActivityForResult(
                        new Intent(getActivity(), UpdateInfoActivity.class).putExtra("title", getResources().getString(R.string.company_name))
                                .putExtra("name", tvCompany.getText().toString()), 1000);
                break;

            case R.id.btn_next:
                if (tvStartTime.getText().toString().equals("请选择")) {
                    tvStartTime.setText("2010-9");
                }
                try {
                    object.put("startTime", tvStartTime.getText().toString());
                    object.put("birthday", tvBirthday.getText().toString());
                    object.put("company", tvCompany.getText().toString());
                    object.put("jobContentInfo", etJobContentInfo.getText().toString());

                    object.put("lastCompany", extraEntity.getLastCompany());
                    object.put("lastWorkingHour", extraEntity.getLastWorkingHour());
                    object.put("jobType", extraEntity.getJobType());
                    object.put("lastJobContentInfo", extraEntity.getLastJobContentInfo());
                    object.put("name", extraEntity.getName());
                    object.put("hashId", searchResponseEntity.getDocs().get(0).getHashId());
                    object.put("price", extraEntity.getPrice());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(getActivity(), CreateSmallResumeActivity.class).putExtra("personalinfo", object.toString()));
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
            isRefresh = false;
        }
    }
}
