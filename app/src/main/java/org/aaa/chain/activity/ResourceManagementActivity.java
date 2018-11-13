package org.aaa.chain.activity;

import android.app.ProgressDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;
import java.math.BigDecimal;
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

    @Override public int initLayout() {
        return R.layout.activity_resource_management;
    }

    @Override public void getViewById() {
        setTitleName(getResources().getString(R.string.myResourceManagement));

        TabLayout tabLayout = $(R.id.tabLayout);
        ViewPager viewPager = $(R.id.tab_viewpager);

        String[] titles = getResources().getStringArray(R.array.resource_management_tab);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new CPU_NETFragment());
        fragments.add(new MemoryFragment());
        SearchTransAdapter adapter = new SearchTransAdapter(getSupportFragmentManager(), Arrays.asList(titles), fragments);

        DecimalFormat decimalFormat = new DecimalFormat("##0.0#");

        //try {
        //    String json = "{\n"
        //            + "  \"core_liquid_balance\" : \"1902.0000 AAA\",\n"
        //            + "  \"ram_quota\" : 98197241,\n"
        //            + "  \"ram_usage\" : 3718,\n"
        //            + "  \"self_delegated_bandwidth\" : {\n"
        //            + "    \"net_weight\" : \"2000.0000 AAA\",\n"
        //            + "    \"to\" : \"aaauser1\",\n"
        //            + "    \"cpu_weight\" : \"2000.0000 AAA\",\n"
        //            + "    \"from\" : \"aaauser1\"\n"
        //            + "  },\n"
        //            + "  \"cpu_weight\" : 20000000,\n"
        //            + "  \"cpu_limit\" : {\n"
        //            + "    \"used\" : 2700,\n"
        //            + "    \"available\" : 82277117,\n"
        //            + "    \"max\" : 82279817\n"
        //            + "  },\n"
        //            + "  \"permissions\" : [\n"
        //            + "    {\n"
        //            + "      \"required_auth\" : {\n"
        //            + "        \"keys\" : [\n"
        //            + "          {\n"
        //            + "            \"key\" : \"AAA7RccFsFi5NqgDQerEYRJ7odQ5EX135N1kD4v1hxKAfrCadKxp3\",\n"
        //            + "            \"weight\" : 1\n"
        //            + "          }\n"
        //            + "        ],\n"
        //            + "        \"accounts\" : [\n"
        //            + "          {\n"
        //            + "            \"weight\" : 1,\n"
        //            + "            \"permission\" : {\n"
        //            + "              \"actor\" : \"aaatrust1111\",\n"
        //            + "              \"permission\" : \"eosio.code\"\n"
        //            + "            }\n"
        //            + "          }\n"
        //            + "        ],\n"
        //            + "        \"waits\" : [\n"
        //            + "\n"
        //            + "        ],\n"
        //            + "        \"threshold\" : 1\n"
        //            + "      },\n"
        //            + "      \"parent\" : \"owner\",\n"
        //            + "      \"perm_name\" : \"active\"\n"
        //            + "    },\n"
        //            + "    {\n"
        //            + "      \"required_auth\" : {\n"
        //            + "        \"keys\" : [\n"
        //            + "          {\n"
        //            + "            \"key\" : \"AAA7RccFsFi5NqgDQerEYRJ7odQ5EX135N1kD4v1hxKAfrCadKxp3\",\n"
        //            + "            \"weight\" : 1\n"
        //            + "          }\n"
        //            + "        ],\n"
        //            + "        \"accounts\" : [\n"
        //            + "\n"
        //            + "        ],\n"
        //            + "        \"waits\" : [\n"
        //            + "\n"
        //            + "        ],\n"
        //            + "        \"threshold\" : 1\n"
        //            + "      },\n"
        //            + "      \"parent\" : \"\",\n"
        //            + "      \"perm_name\" : \"owner\"\n"
        //            + "    }\n"
        //            + "  ],\n"
        //            + "  \"last_code_update\" : \"1970-01-01T00:00:00.000\",\n"
        //            + "  \"account_name\" : \"aaauser1\",\n"
        //            + "  \"voter_info\" : {\n"
        //            + "    \"owner\" : \"aaauser1\",\n"
        //            + "    \"producers\" : [\n"
        //            + "\n"
        //            + "    ],\n"
        //            + "    \"staked\" : 40000000,\n"
        //            + "    \"proxy\" : \"\",\n"
        //            + "    \"proxied_vote_weight\" : \"0.00000000000000000\",\n"
        //            + "    \"is_proxy\" : 0,\n"
        //            + "    \"last_vote_weight\" : \"0.00000000000000000\"\n"
        //            + "  },\n"
        //            + "  \"head_block_time\" : \"2018-11-08T08:34:47.500\",\n"
        //            + "  \"privileged\" : false,\n"
        //            + "  \"net_limit\" : {\n"
        //            + "    \"used\" : 129,\n"
        //            + "    \"available\" : 862766290,\n"
        //            + "    \"max\" : 862766419\n"
        //            + "  },\n"
        //            + "  \"refund_request\" : null,\n"
        //            + "  \"created\" : \"2018-10-10T06:34:55.500\",\n"
        //            + "  \"net_weight\" : 20000000,\n"
        //            + "  \"total_resources\" : {\n"
        //            + "    \"net_weight\" : \"2000.0000 AAA\",\n"
        //            + "    \"ram_bytes\" : 98197241,\n"
        //            + "    \"owner\" : \"aaauser1\",\n"
        //            + "    \"cpu_weight\" : \"2000.0000 AAA\"\n"
        //            + "  },\n"
        //            + "  \"head_block_num\" : 10353900\n"
        //            + "}";
        //    jsonObject = new JSONObject(json);
        //    JSONObject cpuLimit = new JSONObject(jsonObject.getString("cpu_limit"));
        //    cpuAvailable = decimalFormat.format(cpuLimit.getDouble("available") / 1024);
        //    cpuUsed = decimalFormat.format(cpuLimit.getLong("used") / 1024);
        //    cpuMax = decimalFormat.format(cpuLimit.getLong("max") / 1024);
        //
        //    JSONObject netLimit = new JSONObject(jsonObject.getString("net_limit"));
        //    netAvailable = decimalFormat.format(netLimit.getDouble("available") / 1024);
        //    netUsed = decimalFormat.format(netLimit.getDouble("used") / 1024);
        //    netMax = decimalFormat.format(netLimit.getDouble("max") / 1024);
        //
        //    double rq, ru;
        //    rq = jsonObject.getDouble("ram_quota");
        //    ru = jsonObject.getDouble("ram_usage");
        //    ramAvailable = decimalFormat.format((rq - ru) / 1024);
        //    ramQuota = decimalFormat.format(rq / 1024);
        //    ramUsage = decimalFormat.format(ru / 1024);
        //
        //    balance = jsonObject.getString("core_liquid_balance");
        //
        //    String refundRequest = jsonObject.getString("refund_request");
        //    if (refundRequest != null) {
        //        JSONObject refund = new JSONObject(refundRequest);
        //        cpuAmount = refund.getString("cpu_amount");
        //        netAmount = refund.getString("net_amount");
        //    }
        //} catch (JSONException e) {
        //    e.printStackTrace();
        //}
        //
        //viewPager.setAdapter(adapter);
        //tabLayout.setupWithViewPager(viewPager);

        ProgressDialog dialog = ProgressDialog.show(this, "waiting...", "loading...");
        JSInteraction.getInstance().getAccountInfo(Constant.getCurrentAccount(), new JSInteraction.JSCallBack() {
            @Override public void onSuccess(String... stringArray) {

                try {
                    jsonObject = new JSONObject(stringArray[0]);
                    JSONObject cpuLimit = new JSONObject(jsonObject.getString("cpu_limit"));
                    cpuAvailable = decimalFormat.format(cpuLimit.getDouble("available") / 1024);
                    cpuUsed = decimalFormat.format(cpuLimit.getLong("used") / 1024);
                    cpuMax = decimalFormat.format(cpuLimit.getLong("max") / 1024);

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
                Toast.makeText(ResourceManagementActivity.this, "get account successful", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override public void onProgress() {

            }

            @Override public void onError(String error) {
                Toast.makeText(ResourceManagementActivity.this, "get account failure", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    @Override public void onClick(View v) {

    }

    @Override protected void onDestroy() {
        super.onDestroy();
        JSInteraction.getInstance().removeListener();
    }
}
