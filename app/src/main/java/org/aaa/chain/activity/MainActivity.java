package org.aaa.chain.activity;

import android.annotation.TargetApi;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;
import com.squareup.leakcanary.RefWatcher;
import java.util.ArrayList;
import java.util.List;
import org.aaa.chain.ChainApplication;
import org.aaa.chain.JSInteraction;
import org.aaa.chain.R;
import org.aaa.chain.adapter.TabItem;
import org.aaa.chain.permissions.PermissionsManager;
import org.aaa.chain.permissions.PermissionsResultAction;

/**
 * Created by benson on 2018/5/3.
 */

public class MainActivity extends BaseActivity {

    private FragmentTabHost tabHost;
    private List<TabItem> tabItemList;

    @Override public int initLayout() {
        return R.layout.activity_main;
    }

    @Override public void getViewById() {
        tabHost = $(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        initTab();
        requestPermissions();
    }

    private void initTab() {
        tabItemList = new ArrayList<>();
        tabItemList.add(
                new TabItem(getApplicationContext(), R.mipmap.tab_upload, R.mipmap.tab_upload, R.string.upload_home, UploadHomeFragment.class));
        tabItemList.add(new TabItem(getApplicationContext(), R.mipmap.tab_search_and_trans, R.mipmap.tab_search_and_trans, R.string.search_trans,
                SearchAndTransFragment.class));
        tabItemList.add(new TabItem(getApplicationContext(), R.mipmap.tab_my, R.mipmap.tab_my, R.string.my_home, MyHomeFragment.class));

        for (int i = 0; i < tabItemList.size(); i++) {
            TabItem tabItem = tabItemList.get(i);
            //实例化一个TabSpec,设置tab的名称和视图
            TabHost.TabSpec tabSpec = tabHost.newTabSpec(tabItem.getTitleString()).setIndicator(tabItem.getView());
            tabHost.addTab(tabSpec, tabItem.getFragmentClass(), null);

            //去掉分割线
            tabHost.getTabWidget().setDividerDrawable(null);

            //给Tab按钮设置背景
            tabHost.getTabWidget().getChildAt(i).setBackgroundColor(getResources().getColor(R.color.color_bg));

            //默认选中第一个tab
            if (i == 0) {
                tabItem.setChecked(true);
            }
        }

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override public void onTabChanged(String tabId) {
                //重置Tab样式
                for (int i = 0; i < tabItemList.size(); i++) {
                    TabItem tabitem = tabItemList.get(i);
                    if (tabId.equals(tabitem.getTitleString())) {
                        tabitem.setChecked(true);
                    } else {
                        tabitem.setChecked(false);
                    }
                }
            }
        });
    }

    @Override public void onClick(View v) {

    }

    @TargetApi(23) private void requestPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override public void onGranted() {
                Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
            }

            @Override public void onDenied(String permission) {
                Toast.makeText(MainActivity.this, "Permission " + permission + " has been denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = ChainApplication.getRefWatcher(this);
        refWatcher.watch(this);
    }
}