package org.aaa.chain.activity;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import org.aaa.chain.R;

public class FileExploreActivity extends BaseActivity {

    private RecyclerView recyclerView;

    @Override public int initLayout() {
        return R.layout.activity_file_explore;
    }

    @Override public void getViewById() {

        recyclerView = $(R.id.recycleView);
    }

    @Override public void onClick(View v) {

    }
}
