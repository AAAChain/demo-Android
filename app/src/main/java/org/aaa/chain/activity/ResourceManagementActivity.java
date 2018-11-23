package org.aaa.chain.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.aaa.chain.Constant;
import org.aaa.chain.JSInteraction;
import org.aaa.chain.R;
import org.json.JSONException;
import org.json.JSONObject;

public class ResourceManagementActivity extends BaseActivity {

    private JSONObject jsonObject;

    String ramQuota;//持有内存
    String ramUsage;//占用内存

    String netRedemption;
    String cpuRedemption;
    String ramAvailable;
    String netUsed;
    String netAvailable;
    String netMax;
    String cpuUsed;
    String cpuAvailable;
    String cpuMax;
    String balance;

    String cpuAmount;
    String netAmount;

    TabLayout tabLayout;
    ViewPager viewPager;
    SearchTransAdapter adapter;
    InputMethodManager inputMethodManager;

    @Override public int initLayout() {
        return R.layout.activity_resource_management;
    }

    @Override public void getViewById() {
        setTitleName(getResources().getString(R.string.myResourceManagement));

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        tabLayout = $(R.id.tabLayout);
        viewPager = $(R.id.tab_viewpager);
        String[] titles = getResources().getStringArray(R.array.resource_management_tab);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new CPU_NETFragment());
        fragments.add(new MemoryFragment());
        adapter = new SearchTransAdapter(getSupportFragmentManager(), Arrays.asList(titles), fragments);

        dialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.loading));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override public void onPageSelected(int position) {

                if (position == 0) {
                    ((CPU_NETFragment) fragments.get(position)).updateView();
                } else {
                    ((MemoryFragment) fragments.get(position)).updateView();
                }
            }

            @Override public void onPageScrollStateChanged(int state) {

            }
        });
        getAccountInfo();
    }

    ProgressDialog dialog;

    public void getAccountInfo() {
        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        JSInteraction.getInstance().getAccountInfo(Constant.getCurrentAccount(), new JSInteraction.JSCallBack() {
            @Override public void onSuccess(String... stringArray) {

                if (listener != null) {
                    listener.getResource(stringArray[0]);
                    listener = null;
                } else if (listener1 != null) {
                    listener1.getResource(stringArray[0]);
                    listener1 = null;
                } else {
                    DecimalFormat decimalFormat = new DecimalFormat("##0.0#");
                    try {
                        jsonObject = new JSONObject(stringArray[0]);
                        JSONObject cpuLimit = new JSONObject(jsonObject.getString("cpu_limit"));
                        cpuAvailable = decimalFormat.format(cpuLimit.getDouble("available") / 1000);
                        cpuUsed = decimalFormat.format(cpuLimit.getLong("used") / 1000);
                        cpuMax = decimalFormat.format(cpuLimit.getLong("max") / 1000);

                        JSONObject netLimit = new JSONObject(jsonObject.getString("net_limit"));
                        netAvailable = decimalFormat.format(netLimit.getDouble("available") / 1024);
                        netUsed = decimalFormat.format(netLimit.getDouble("used") / 1024);
                        netMax = decimalFormat.format(netLimit.getDouble("max") / 1024);

                        double rq, ru;
                        rq = jsonObject.getDouble("ram_quota");
                        ru = jsonObject.getDouble("ram_usage");
                        ramAvailable = decimalFormat.format((rq - ru) / 1024);
                        ramQuota = decimalFormat.format(rq / 1024);
                        ramUsage = decimalFormat.format(ru / 1024);

                        if (jsonObject.getString("self_delegated_bandwidth") != null && !"null".equals(
                                jsonObject.getString("self_delegated_bandwidth"))) {
                            JSONObject redemption = new JSONObject(jsonObject.getString("self_delegated_bandwidth"));
                            netRedemption = redemption.getString("net_weight");
                            cpuRedemption = redemption.getString("cpu_weight");
                        }

                        balance = jsonObject.getString("core_liquid_balance");

                        String refundRequest = jsonObject.getString("refund_request");
                        if (refundRequest != null) {
                            JSONObject refund = new JSONObject(refundRequest);
                            cpuAmount = refund.getString("cpu_amount");
                            netAmount = refund.getString("net_amount");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            viewPager.setAdapter(adapter);
                            tabLayout.setupWithViewPager(viewPager);
                        }
                    });
                    Toast.makeText(ResourceManagementActivity.this, getResources().getString(R.string.get_account_success), Toast.LENGTH_SHORT)
                            .show();
                    dialog.dismiss();
                }
            }

            @Override public void onProgress() {

            }

            @Override public void onError(String error) {
                Toast.makeText(ResourceManagementActivity.this, getResources().getString(R.string.get_account_failure), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    @Override public void onClick(View v) {

    }

    ResourceListener listener;
    ResourceListener listener1;

    public void setResourceListener(ResourceListener listener) {
        this.listener = listener;
    }

    public void setResourceListener1(ResourceListener listener) {
        this.listener1 = listener;
    }

    public interface ResourceListener {
        void getResource(String resource);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        JSInteraction.getInstance().removeListener();
        listener = null;
        listener1 = null;
    }
}
