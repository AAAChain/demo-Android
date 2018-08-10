package org.aaa.chain.activity;

import android.view.View;
import android.widget.Button;
import com.squareup.leakcanary.RefWatcher;
import org.aaa.chain.IpfsApplication;
import org.aaa.chain.R;
import org.aaa.chain.views.SlideBackLayout;

public class ProjectDetailActivity extends BaseActivity {

    @Override public int initLayout() {
        return R.layout.activity_project_detail;
    }

    @Override public void getViewById() {

        initSlideBackLayout(this);

        Button btnGet = $(R.id.btn_project_detail_get);
        btnGet.setOnClickListener(this);

        setTitleName("项目详情");
    }

    @Override public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_project_detail_get:

                break;
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = IpfsApplication.getRefWatcher(this);
        refWatcher.watch(this);
    }
}
