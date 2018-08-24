package org.aaa.chain.activity;

import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.aaa.chain.R;

public class UploadResumeActivity extends BaseActivity {

    ConstraintLayout layout1;
    ConstraintLayout layout2;
    ProgressBar progressBar;
    TextView tvPercent;

    private int[] data = new int[100];
    int hasData = 0;
    //记录ProgressBar的完成进度
    int status = 0;

    @Override public int initLayout() {
        return R.layout.activity_upload_resume;
    }

    @Override public void getViewById() {

        layout1 = $(R.id.cl_layout1);
        layout2 = $(R.id.cl_layout2);
        progressBar = $(R.id.progress);
        tvPercent = $(R.id.tv_percent);
        $(R.id.btn_resume_upload).setOnClickListener(this);

        //启动线程来执行任务
        new Thread() {
            public void run() {
                while (status < 100) {
                    // 获取耗时操作的完成百分比
                    status = doWork();
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            progressBar.setProgress(status);
                            tvPercent.setText(status + "%");
                        }
                    });
                }
            }
        }.start();
    }

    //模拟一个耗时的操作
    private int doWork() {
        data[hasData++] = (int) (Math.random() * 100);
        for (int i : data) {
            Log.i("info", "data:" + data[i]);
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return hasData;
    }

    @Override public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_resume_upload:

                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
                break;
        }
    }
}
